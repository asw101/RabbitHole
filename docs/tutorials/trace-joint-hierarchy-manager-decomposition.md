# Tutorial: Trace the Joint Hierarchy Manager Decomposition

A guided walkthrough showing how jointed model operations flow through
`JointHierarchyManager` and its two extracted helpers. Use this to understand
the delegation pattern before modifying joint hierarchy behavior.

## Prerequisites

- Familiarity with the Alice 3 scene graph (`edu.cmu.cs.dennisc.scenegraph`)
- Access to the `core/story-api` source in `org.lgna.story.implementation`
- Optional: read the [Reference](../reference/joint-hierarchy-manager-decomposition.md)
  for the full API surface

## Overview

When a `JointedModelImp` needs to work with its joints, the request passes
through these classes:

```text
JointedModelImp
  └── JointHierarchyManager         ← coordinator (hierarchy, lookup, walk)
      ├── JointImpWrapper            ← wraps each JointImp for resource swaps
      └── IkChainHelper              ← static IK chain computation
```

## Step 1: Model Initialization — buildJointHierarchy

Open `JointHierarchyManager.java`. Find `buildJointHierarchy`:

```java
void buildJointHierarchy(JointedModelImp<?, R> owner) {
  Map<JointId, JointImp> jointMap = createJointImps(owner);
  for (Map.Entry<JointId, JointImp> entry : jointMap.entrySet()) {
    JointImpWrapper wrapper = new JointImpWrapper(owner, entry.getValue(),
                                                   resourceBinder);
    mapIdToJoint.put(entry.getKey(), wrapper);
  }
  fillInJointArrays();
  for (Map.Entry<JointId, JointImpWrapper> entry : mapIdToJoint.entrySet()) {
    entry.getValue().setJointParent(mapIdToJoint.get(entry.getKey().getParent()));
  }
  invalidateCaches();
}
```

This is the primary entry point. It:
1. Creates raw `JointImp` instances via the resource binder
2. Wraps each in a `JointImpWrapper` (passing `resourceBinder` for Sims detection)
3. Links parent/child relationships through the wrapper layer

**Key insight:** The wrapper layer exists so that when a model's resource
changes (e.g., switching from one Sims outfit to another), the internal
`JointImp` can be swapped without breaking external references. Code that
holds a `JointImp` reference actually holds a `JointImpWrapper`.

Note that `updateSkeleton()` is a second construction site — it also
creates `JointImpWrapper` instances (for joints added by the new resource)
and must pass `resourceBinder` to the constructor.

## Step 2: Joint Wrapping — JointImpWrapper

Open `JointImpWrapper.java`. Every method delegates to `internalJointImp`:

```java
@Override
public AffineMatrix4x4 getLocalTransformation() {
  return internalJointImp.getLocalTransformation();
}
```

The interesting behavior is in `copyOnto()`:

```java
@Override
protected void copyOnto(JointImp newJoint) {
  internalJointImp.copyOnto(newJoint);
  if (resourceBinder.isSims()) {
    AdapterFactory.getAdapterFor(newJoint.getSgComposite());
  }
  if (getAbstraction() != null) {
    newJoint.setAbstraction(getAbstraction());
  }
}
```

**Key insight:** `resourceBinder` is injected via the constructor rather
than accessed through an outer-class reference. This is the only reason the
wrapper needs the binder — to detect Sims models that require adapter
recreation when joints are regenerated.

## Step 3: Resource Swapping — replaceWithJoint

Still in `JointImpWrapper.java`, find `replaceWithJoint`:

```java
void replaceWithJoint(JointImp newJoint) {
  copyOnto(newJoint);
  AbstractTransformable oldSgComposite = internalJointImp.getSgComposite();
  AbstractTransformable newSgComposite = newJoint.getSgComposite();
  // Reparent scene-graph children from old to new joint
  for (Component child : oldSgComposite.getComponents()) {
    if (!(child instanceof ModelJoint)) {
      child.setParent(newJoint.getSgComposite());
    }
  }
  // Reconnect root for Sims models
  if (newSgComposite.getParent() == null) {
    newSgComposite.setParent(oldSgComposite.getParent());
  }
  internalJointImp = newJoint;
}
```

This is called by `JointHierarchyManager.matchNewDataToExistingJoints()`
during `updateSkeleton()`. The wrapper swaps its internal joint while
external code continues using the same wrapper reference.

## Step 4: Joint Lookup — getJointImplementation

Back in `JointHierarchyManager.java`:

```java
JointImp getJointImplementation(JointId jointId) {
  return this.mapIdToJoint.get(jointId);
}
```

This is the O(1) lookup used by `JointedModelImp` for all joint-by-ID
access. The returned `JointImp` is actually a `JointImpWrapper`, but
callers don't need to know that.

**Key insight:** This method is also passed as a method reference
(`this::getJointImplementation`) to `IkChainHelper` for IK chain
resolution.

## Step 5: IK Chain Computation — IkChainHelper

Open `IkChainHelper.java`. The public entry point:

```java
static List<JointImp> getInclusiveListOfJointsBetween(
    JointImp jointA, JointImp jointB,
    List<Bone.Direction> directions, EntityImp owner,
    Function<JointId, JointImp> jointLookup) {
```

The `Function<JointId, JointImp>` parameter decouples this utility from
`JointHierarchyManager`. The manager calls it as:

```java
// In JointHierarchyManager:
List<JointImp> getInclusiveListOfJointsBetween(
    JointImp jointA, JointImp jointB,
    List<Bone.Direction> directions, EntityImp owner) {
  return IkChainHelper.getInclusiveListOfJointsBetween(
      jointA, jointB, directions, owner,
      this::getJointImplementation);
}
```

**Key insight:** The `Function` parameter is the only connection between
`IkChainHelper` and `JointHierarchyManager`. This makes `IkChainHelper`
independently testable — you can pass a mock lookup function.

**Implementation note:** `IkChainHelper` uses `Lists.newLinkedList()` from
`edu.cmu.cs.dennisc.java.util.Lists` (3×) because `AddOp.PREPEND` calls
`addFirst()` on the result list, which requires a `LinkedList`.

## Step 6: IK Chain Algorithm

The algorithm handles three cases:

1. **Same joint** (`jointA == jointB`): Returns `[jointA]` with `DOWNSTREAM`.

2. **Ancestor relationship**: If A is a descendant of B, walks from A up to
   B using `AddOp.PREPEND` (building the list from tail to head). If B is a
   descendant of A, walks from B up to A using `AddOp.APPEND`.

3. **Siblings / cousins**: Walks both joints up to the scene owner, finds
   their common ancestor, trims both paths to exclude the common ancestor,
   and concatenates path-A (UPSTREAM directions) with path-B (DOWNSTREAM
   directions).

The `AddOp` enum encapsulates the prepend/append behavior:

```java
private enum AddOp {
  PREPEND {
    public List<JointImp> add(List<JointImp> rv, JointImp joint,
                              List<Bone.Direction> directions,
                              Bone.Direction direction) {
      rv.addFirst(joint);
      if (directions != null) { directions.addFirst(direction); }
      return rv;
    }
  },
  APPEND { /* mirror of PREPEND using add() instead of addFirst() */ };
}
```

## Step 7: Straighten and Pose — JointData

Back in `JointHierarchyManager.java`, the `JointData` inner class and
`StraightenTreeWalkObserver` remain in the manager because they are
tightly coupled to tree walk and externally referenced as
`JointHierarchyManager.JointData` by `JointedModelImp`:

```java
static class JointData {
  private final JointImp jointImp;
  private final UnitQuaternion q0;  // current orientation
  private final UnitQuaternion q1;  // original orientation (target)

  public void setPortion(double portion) {
    // Interpolates between current and original orientation
    this.jointImp.setLocalOrientationOnly(
        this.q0.interpolate(this.q1, portion).asMatrix3x3());
  }
}
```

`JointedModelImp` uses `collectStraightenData()` to gather `JointData`
instances, then animates them by calling `setPortion()` at each frame.

## Step 8: Verify with a Test

Run the decomposition-specific test to confirm everything works:

```bash
mvn -pl core/story-api -am -DfailIfNoTests=false \
  -Dtest=JointedModelImpDecompositionTest test -q
```

The test exercises `JointHierarchyManager` through `JointedModelImp` — it
does not reference `JointImpWrapper` or `IkChainHelper` directly, proving
the public API is unchanged.

## Summary

| Concern | Class | Pattern |
|---|---|---|
| Joint wrapping / resource swap | `JointImpWrapper` | Constructor-injected `JointedModelResourceBinder<?>` for `isSims()` |
| IK chain computation | `IkChainHelper` | Static methods parameterized by `Function<JointId, JointImp>` |
| Hierarchy construction / update | `JointHierarchyManager` | Creates `JointImpWrapper` instances; delegates IK to `IkChainHelper` |
| Joint lookup | `JointHierarchyManager` | O(1) map lookup; passed as method reference to `IkChainHelper` |
| Tree walk | `JointHierarchyManager` | DFS traversal with `TreeWalkObserver` callback |
| Straighten / pose | `JointHierarchyManager` | `JointData` + `StraightenTreeWalkObserver` stay coupled to tree walk |

## What to Do When Extending Joint Behavior

1. **New JointImp delegation**: Add the `@Override` to `JointImpWrapper`,
   delegating to `internalJointImp`.
2. **New IK algorithm**: Add a static method to `IkChainHelper` with a
   `Function<JointId, JointImp>` parameter.
3. **New hierarchy query**: Add to `JointHierarchyManager`, using the
   existing `mapIdToJoint` map.
4. **New tree walk consumer**: Implement `TreeWalkObserver` and call
   `treeWalk(observer)`.

Always run the full test suite after changes:

```bash
mvn -pl core/story-api -am -DfailIfNoTests=false test -q
```
