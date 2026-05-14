# Validate JointedModelImp Decomposition

Use this guide to verify the extraction of joint hierarchy management, resource
binding, and visual/skin operations from `JointedModelImp` into three
package-private delegate classes.

For the full contract, see the [JointedModelImp Decomposition
reference](../reference/jointed-model-imp-decomposition.md).

## When to use this guide

Use this guide when:

- Reviewing changes that extract delegate classes from `JointedModelImp`
- Modifying any of the three managers (`JointedModelResourceBinder`,
  `JointedModelVisualManager`, `JointHierarchyManager`)
- Changing the `setNewResource()` orchestration logic
- Updating line-count thresholds in `JointedModelImpDecompositionTest`
- Modifying `JointImpWrapper` behavior

## Before you start

Run commands from the repository root:

```bash
git submodule update --init tweedle-lang
test -d tweedle-lang/Grammar
export NODE_OPTIONS=--max-old-space-size=32768
```

## Step 1: Verify compilation

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/story-api -am -DfailIfNoTests=false -Dcheckstyle.skip compile
```

All four files must compile without errors:

- `JointedModelImp.java`
- `JointedModelResourceBinder.java`
- `JointedModelVisualManager.java`
- `JointHierarchyManager.java`

## Step 2: Verify new files exist

```bash
for f in JointedModelResourceBinder JointedModelVisualManager JointHierarchyManager; do
  test -f core/story-api/src/main/java/org/lgna/story/implementation/${f}.java \
    && echo "OK: ${f}" || echo "MISSING: ${f}"
done
```

All three files must report `OK`.

## Step 3: Verify line counts

```bash
wc -l core/story-api/src/main/java/org/lgna/story/implementation/JointedModelImp.java
wc -l core/story-api/src/main/java/org/lgna/story/implementation/JointedModelResourceBinder.java
wc -l core/story-api/src/main/java/org/lgna/story/implementation/JointedModelVisualManager.java
wc -l core/story-api/src/main/java/org/lgna/story/implementation/JointHierarchyManager.java
```

Expected:

- `JointedModelImp.java` — under 500 lines (target ~370).
- `JointedModelResourceBinder.java` — approximately 160 lines.
- `JointedModelVisualManager.java` — approximately 200 lines.
- `JointHierarchyManager.java` — approximately 380 lines.

## Step 4: Verify package-private visibility (no new public surface)

```bash
grep -rn 'public class\|public interface' \
  core/story-api/src/main/java/org/lgna/story/implementation/JointedModelResourceBinder.java \
  core/story-api/src/main/java/org/lgna/story/implementation/JointedModelVisualManager.java \
  core/story-api/src/main/java/org/lgna/story/implementation/JointHierarchyManager.java
```

Expected: no output. All three managers are package-private with no public
classes or interfaces.

## Step 5: Verify public interfaces are retained

```bash
grep 'public interface' \
  core/story-api/src/main/java/org/lgna/story/implementation/JointedModelImp.java
```

Expected output (three interfaces):

```
  public interface VisualData<R extends JointedModelResource> {
  public interface JointImplementationAndVisualDataFactory<R extends JointedModelResource> {
  public interface TreeWalkObserver {
```

## Step 6: Run the characterization tests

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/story-api -am \
  -DfailIfNoTests=false \
  -Dcheckstyle.skip \
  -Dtest=JointedModelImpDecompositionTest \
  test
```

All tests must pass. The characterization tests verify:

- `JointedModelImp` delegates to each manager
- Manager creation order (ResourceBinder → VisualManager → HierarchyManager)
- `setNewResource()` orchestration sequence (including `hasJoints()` guard)
- `TreeWalkObserver` is accessible as `JointedModelImp.TreeWalkObserver`
- Joint map lookups return the same wrappers before and after extraction
- Scale operations coordinate visual manager and hierarchy manager

## Step 7: Run the full module tests

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/story-api -am -DfailIfNoTests=false -Dcheckstyle.skip test
```

All existing tests must pass with no regressions.

## Troubleshooting

### Compilation error: cannot find symbol in JointImpWrapper

`JointImpWrapper` moved to `JointHierarchyManager`. If external code
references `JointedModelImp.JointImpWrapper`, that is a bug — `JointImpWrapper`
was always private and not part of the public API. The only known external
reference is the `TreeWalkObserver` interface, which remains on
`JointedModelImp`.

### Scale not applied after setNewResource()

Verify that `setScale()` on `JointedModelImp` delegates to both managers:

```java
// In JointedModelImp.setScale()
visualManager.setScaleOnVisuals(scale, this.sgScalable);
hierarchyManager.setScaleOnJoints(scale);
```

Both calls are required because visuals and joints track scale independently.

### Tree walk returns empty list

Verify `hierarchyManager.parentOrphanedJoints(getSgComposite())` is called
in the constructor. Without this, root joints have no scene graph parent and
`getRootJointImps()` may return joints that are not connected.
