# Joint Hierarchy Manager Decomposition

> **Module:** `core/story-api`
> **Package:** `org.lgna.story.implementation`
> **Issue:** #641 — Reduce JointHierarchyManager.java (650 → ≤500 lines)

## Overview

`JointHierarchyManager` owns the joint hierarchy for every jointed model in
Alice 3 — wrapping inner `JointImp` instances for resource swapping, building
parent/child relationships, resolving joint lookups, walking the hierarchy
tree, computing IK chains, and collecting straighten/pose data.

At 650 lines, the class mixed three distinct concerns:

1. **Joint wrapping** — a 178-line inner class (`JointImpWrapper`) that
   delegates every `JointImp` method to an internal joint and handles
   resource swapping.
2. **IK chain computation** — an `AddOp` enum plus three recursive methods
   (118 lines) that build joint chains for inverse kinematics.
3. **Hierarchy management** — construction, update, lookup, tree walk, caches,
   and straighten/pose support.

The decomposition extracts concerns #1 and #2 into focused helper classes
while leaving the hierarchy management core in `JointHierarchyManager`.

## Architecture

```
┌──────────────────────────────────────────────┐
│         JointHierarchyManager                │  ~354 lines
│  (hierarchy construction, lookup,            │
│   tree walk, caches, JointData,              │
│   StraightenTreeWalkObserver)                │
│                                              │
│  Map<JointId, JointImpWrapper>  mapIdToJoint │
│  Map<JointArrayId, JointId[]>  arrays        │
├────────────────────┬─────────────────────────┤
│                    │                         │
│   creates          │         delegates       │
│                    ▼                         │
│  ┌──────────────────────┐  ┌───────────────┐ │
│  │   JointImpWrapper    │  │ IkChainHelper │ │
│  │                      │  │               │ │
│  │ Wraps JointImp for   │  │ Static IK     │ │
│  │ resource swapping.   │  │ chain methods │ │
│  │ Delegates all JointImp│  │ + AddOp enum  │ │
│  │ methods to internal  │  │               │ │
│  │ joint. Handles Sims  │  │ Parameterized │ │
│  │ adapter creation on  │  │ by Function   │ │
│  │ copyOnto().          │  │ <JointId,     │ │
│  │                      │  │  JointImp>    │ │
│  └──────────────────────┘  └───────────────┘ │
└──────────────────────────────────────────────┘
```

## Class Reference

### JointHierarchyManager

**Role:** Coordinator. Owns the `mapIdToJoint` and `mapArrayIdToJointIdArray`
maps, hierarchy construction/update logic, joint lookup, tree walk, caches,
and straighten/pose support. Delegates IK chain computation to
`IkChainHelper` via `this::getJointImplementation` method reference.

| Visibility | Member | Purpose |
|---|---|---|
| `(pkg)` | `JointHierarchyManager(JointedModelResourceBinder<R>)` | Constructor; stores resource binder |
| `(pkg)` | `buildJointHierarchy(JointedModelImp<?,R>)` | Creates joint wrappers, links parent/child, fills arrays |
| `(pkg)` | `updateSkeleton(JointedModelImp<?,R>)` | Replaces internal joints on resource change; removes stale joints |
| `(pkg)` | `getJointImplementation(JointId)` | O(1) lookup from `mapIdToJoint` |
| `(pkg)` | `getJointImplementation(String)` | O(1) lookup for `DynamicJointId` via lazily-built name index |
| `(pkg)` | `getJointIdArray(JointArrayId)` | Returns cached joint ID array |
| `(pkg)` | `isEmpty()` | True when no joints are mapped |
| `(pkg)` | `getJointWrappers()` | Returns all wrapper values |
| `(pkg)` | `getRootJointImps()` | Returns cached list of root joints (no parent) |
| `(pkg)` | `treeWalk(TreeWalkObserver)` | DFS walk from all roots |
| `(pkg)` | `getJoints()` | Returns cached DFS-ordered joint list |
| `(pkg)` | `setAllJointPivotsVisible(boolean)` | Sets pivot visibility on all wrappers |
| `(pkg)` | `setScaleOnJoints(Dimension3)` | Applies scale to all joints |
| `(pkg)` | `getInclusiveListOfJointsBetween(JointImp, JointImp, List<Direction>, EntityImp)` | Delegates to `IkChainHelper` |
| `(pkg)` | `collectStraightenData()` | Tree-walks to gather `JointData` for straighten animation |
| `(pkg)` | `straightenOutJoints()` | Snaps all joints to their original orientation |

**Inner types retained:**

| Type | Lines | Purpose |
|---|---|---|
| `JointData` | ~23 | Captures joint orientation for interpolation; referenced externally as `JointHierarchyManager.JointData` by `JointedModelImp` |
| `StraightenTreeWalkObserver` | ~16 | Collects `JointData` during tree walk |

**Private helpers:**
`invalidateCaches`, `createJointImps`, `resolveAllJointIds`, `fillInJointArrays`,
`findJointsMatching`, `matchNewDataToExistingJoints`, `treeWalk(JointImp, TreeWalkObserver)`.

### JointImpWrapper

**Role:** Wraps an inner `JointImp` for transparent resource swapping. Every
public `JointImp` method delegates to the wrapped `internalJointImp`. The
wrapper maintains its own parent/child links (`jointParentWrapper`,
`jointChildrenWrapper`) so the hierarchy persists across resource changes.

**Visibility:** Package-private top-level class (no `public` keyword).

**Key design decisions:**

- **Constructor injection** of `JointedModelResourceBinder<?>` replaces the
  former `JointHierarchyManager.this.resourceBinder.isSims()` outer-class
  access in `copyOnto()`. The wildcard `<?>` suffices because `isSims()`
  does not use the resource type parameter.
- No generics needed on `JointImpWrapper` itself.
- Both `buildJointHierarchy` and `updateSkeleton` are construction sites —
  both must pass `resourceBinder` to the new 3-arg constructor.

| Visibility | Member | Purpose |
|---|---|---|
| `(pkg)` | `JointImpWrapper(JointedModelImp<?,?>, JointImp, JointedModelResourceBinder<?>)` | Constructor; stores model, internal joint, and binder |
| `@Override` | `setAbstraction(SJoint)` | Forwards to both super and internal joint |
| `@Override` | `getJointParent()` | Returns wrapper parent link |
| `@Override` | `getJointChildren()` | Returns wrapper children list |
| `@Override` | `setJointParent(JointImp)` | Updates wrapper parent/child links |
| `@Override` | `copyOnto(JointImp)` | Copies internal joint; calls `AdapterFactory.getAdapterFor()` for Sims models |
| `(pkg)` | `replaceWithJoint(JointImp)` | Swaps internal joint; reparents scene-graph components |
| `@Override` | `setScale`, `isReoriented`, `isRelocated`, `getName`, `getScene`, `getJointId`, `isFreeInX/Y/Z`, `getAxisAlignedMinimumBoundingBox`, `getSgComposite`, `updateCumulativeBound`, `getOriginalOrientation`, `getScaledOriginalTransformation`, `getLocalTransformation`, `setLocalTransformation`, `postCheckSetVehicle`, `isFacing`, `applyTranslation`, `applyRotationInRadians`, `isPivotVisible`, `setPivotVisible` | Delegate to `internalJointImp` |

**Fields:**

| Field | Type | Purpose |
|---|---|---|
| `internalJointImp` | `JointImp` | The real joint implementation (mutable; swapped on resource change) |
| `jointParentWrapper` | `JointImp` | Wrapper-level parent link |
| `jointChildrenWrapper` | `List<JointImp>` | Wrapper-level children (final, mutable list) |

### IkChainHelper

**Role:** Static utility class for IK (inverse kinematics) chain computation.
Finds the ordered list of joints between two arbitrary joints in the
hierarchy, including correct `Bone.Direction` annotations for each segment.

**Visibility:** Package-private top-level class (no `public` keyword).

**Key design decision:** All three methods accept a
`Function<JointId, JointImp>` parameter for joint lookup, replacing the
original `this.getJointImplementation()` instance calls. The caller passes
`this::getJointImplementation` from `JointHierarchyManager`.

| Visibility | Member | Purpose |
|---|---|---|
| `(pkg) static` | `getInclusiveListOfJointsBetween(JointImp, JointImp, List<Direction>, EntityImp, Function<JointId, JointImp>)` | Entry point: returns ordered joint chain between two joints |
| `private static` | `updateJointsBetween(List<JointImp>, List<Direction>, JointImp, EntityImp, AddOp, Function<JointId, JointImp>)` | Recursive helper: walks toward ancestor, adding joints via `AddOp` |
| `private static` | `updateJointsUpToAndExcludingCommonAncestor(List<JointImp>, List<Direction>, JointImp, JointImp, EntityImp, Function<JointId, JointImp>)` | Handles case where neither joint is an ancestor of the other |

**Required imports:**

`IkChainHelper` needs `edu.cmu.cs.dennisc.java.util.Lists` for
`Lists.newLinkedList()`, used 3× (once in `getInclusiveListOfJointsBetween`
for the result list, twice in `updateJointsUpToAndExcludingCommonAncestor`
for the two path accumulators). The `LinkedList` is required because
`AddOp.PREPEND` calls `addFirst()`.

**Inner types:**

| Type | Visibility | Purpose |
|---|---|---|
| `AddOp` | `private enum` | `PREPEND` or `APPEND` — controls whether joints are added to front or back of the chain list |

**Algorithm overview:**

1. If `jointA == jointB`, returns a single-element list with `DOWNSTREAM`.
2. If `jointA` is a descendant of `jointB`, walks from A up to B using `PREPEND`.
3. If `jointB` is a descendant of `jointA`, walks from B up to A using `APPEND`.
4. Otherwise, walks both joints up to the scene owner, finds the common ancestor,
   trims both paths to exclude the common ancestor, and concatenates them.

## Existing Related Classes

These classes predate this decomposition and remain unchanged:

| Class | Purpose |
|---|---|
| `JointedModelImp` | The jointed model implementation; owns a `JointHierarchyManager`; references `JointHierarchyManager.JointData` for straighten animations |
| `JointedModelResourceBinder` | Resource binding abstraction; provides `isSims()`, `createJointImplementation()`, `getAllJointIds()`, `getJointArrayIds()` |
| `JointImp` | Base joint implementation; extended by `JointImpWrapper` |
| `Bone` / `Bone.Direction` | IK bone direction enum (`UPSTREAM`, `DOWNSTREAM`) used by IK chain |
| `JointedModelImp.TreeWalkObserver` | Callback interface for hierarchy tree walks |
| `JointId` / `JointArrayId` | Immutable joint identifiers from the resource layer |

## Line Counts

| File | Lines | Role |
|---|---|---|
| `JointHierarchyManager.java` | 350 | Coordinator: hierarchy construction, lookup, tree walk, caches, straighten/pose |
| `JointImpWrapper.java` | 244 | Joint wrapping with resource-swap support |
| `IkChainHelper.java` | 193 | Static IK chain computation |
| **Total** | **787** | Original code (~650) plus copyright headers and Javadoc on each new file |

The manager itself drops from 650 to 350 lines — well under the 500-line target.

## Constants

| Constant | Location | Value | Purpose |
|---|---|---|---|
| (none) | — | — | No constants in the extracted classes. `FLIP_COORDINATE_SPACE`-style constants are not involved. |

## Thread Safety

None of the three classes are thread-safe. `JointHierarchyManager` is always
accessed from the Alice animation thread via `JointedModelImp`. No
synchronization is added or removed by this decomposition.
