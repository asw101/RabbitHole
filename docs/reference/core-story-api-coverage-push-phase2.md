# core/story-api coverage push phase 2 — 52.3% → 70%

Issue #775 raises `core/story-api` line coverage from 52.3% to 70%+ by
adding 30 test files across the IK solver math layer, interact conditions,
handle/input state management, manipulator logic, implementation helpers,
and event data classes. All new tests are JUnit 4, headless-safe, and use
hand-rolled stubs (no Mockito).

This push builds on Phase 1 (#751, 21.75% → ~52.3%). Phase 2 targets the
IK (inverse kinematics) solver package, the interact condition/handle/input
subsystem, and manipulator snap math — all pure-logic domains with zero
rendering dependency.

## Coverage model

```
Baseline covered:  ~8,264 lines  (52.3% of ~15,800 total)
New direct:       +2,000 lines
New transitive:   +1,200 lines   (math types, property chains, event dispatch)
────────────────────────────────
Projected covered: ~11,464 lines (~72.6%)
```

## Test inventory

### Tier 1 — IK solver math (8 files, ~782 lines)

The `org.lgna.ik.core.solver` package implements a Jacobian-based inverse
kinematics solver. `Bone.Axis` is a nested static class with axis vector
storage and contribution methods. `Constraint`, `Weights`, and chain
construction are data classes. The `Solver` itself can be tested with
synthetic bone chains.

| Test file | Source classes covered | Est. lines |
| --- | --- | ---: |
| `BoneTest` | `Bone`, `Bone.Axis` (axis vectors, linear/angular contributions, direction) | 200 |
| `ChainTest` | `Chain` (bone chain construction, length, joint enumeration) | 100 |
| `SolverTest` | `Solver` (Jacobian computation, single-step solve, convergence) | 150 |
| `ConstraintTest` | `Constraint` (position target, orientation target, combined) | 80 |
| `WeightsTest` | `Weights` (per-bone weight assignment, normalization) | 60 |
| `IKCoreTest` | `IKCore` (top-level solve orchestration with synthetic chain) | 80 |
| `JointedModelIkEnforcerTest` | `JointedModelIkEnforcer` (enforcer contract, data flow) | 60 |
| `TightPositionalIkEnforcerTest` | `TightPositionalIkEnforcer` (positional accuracy thresholds) | 52 |

**Test strategy:** `Bone.Axis` tests validate axis vector operations:
`getCurrentValue()`, `setCurrentValue()`, `invertDirection()`,
`updateLinearContributions()`, and `updateAngularContributions()` against
known cross-product and negation results. `Chain` tests build synthetic
bone chains and verify length and traversal. `Solver` tests use a 2-bone
planar chain with a reachable target and assert convergence within tolerance.

**Headless safety:** The IK solver operates entirely on `AffineMatrix4x4`,
`Point3`, and `OrthogonalMatrix3x3` — no scenegraph or display dependency.
The `JointedModelIkEnforcer` and `TightPositionalIkEnforcer` tests use
reflection to bypass the `JointImp` constructor dependency, testing only
the enforcer's data-flow and threshold logic.

### Tier 2 — Interact conditions (7 files, ~500 lines)

The `org.alice.interact.condition` package defines input conditions that
determine when a manipulator activates. Each condition checks mouse buttons,
modifier keys, and drag state against an `InputState` snapshot.

| Test file | Source classes covered | Est. lines |
| --- | --- | ---: |
| `MouseDragConditionTest` | `MouseDragCondition` (button match, modifier match, stateChanged/justStarted/justEnded) | 100 |
| `ClickConditionsTest` | `ClickedObjectCondition`, `DoubleClickCondition` | 60 |
| `KeyPressConditionTest` | `KeyPressCondition` (key code match, modifier mask) | 60 |
| `MouseConditionsTest` | `MouseWheelCondition`, `MouseMoveCondition` | 60 |
| `DragAndDropConditionTest` | `DragAndDropCondition` (drag source, drop target matching) | 80 |
| `ModifierConditionsTest` | `ManipulatorConditionSet` modifier sub-conditions | 60 |
| `ManipulatorConditionSetDeepTest` | `ManipulatorConditionSet` (condition composition, priority ordering) | 80 |

**Test strategy:** Conditions are tested by constructing two `InputState`
instances with known button/key/modifier values and asserting
`stateChanged()`, `justStarted()`, `justEnded()`, and `isRunning()`.
`InputState` is a value object that accepts `setMouseState()`,
`setKeyState()`, and `setIsDragEvent()` — confirmed headless-safe.

```java
@Test
public void mouseDragCondition_stateChanged_detectsNewDrag() {
  InputState current = new InputState();
  current.setMouseState(MouseEvent.BUTTON1, true);
  current.setIsDragEvent(true);
  InputState previous = new InputState();
  MouseDragCondition condition = new MouseDragCondition(
      MouseEvent.BUTTON1, new PickCondition(PickHint.getAnything()));
  assertTrue(condition.stateChanged(current, previous));
}
```

### Tier 3 — Handle and input state (4 files, ~300 lines)

| Test file | Source classes covered | Est. lines |
| --- | --- | ---: |
| `HandleSetTest` | `HandleSet` (handle registration, lookup by type, iteration) | 80 |
| `HandleManagerTest` | `HandleManager` (handle lifecycle, visibility toggling) | 80 |
| `InputStateTest` | `InputState` (mouse/key state tracking, event application) | 80 |
| `PickHintDeepTest` | `PickHint` (pick type flags, intersection, union, contains) | 60 |

**Test strategy:** `InputState` tests apply sequences using `setMouseState()`,
`setKeyState()`, and `setIsDragEvent()` to build known state snapshots,
then verify via `isKeyDown()`, `isAnyMouseButtonDown()`, and
`getIsDragEvent()`. `HandleSet` tests use `PickHint` flag combinations
to verify handle filtering.

### Tier 4 — Manipulator snap math (5 files, ~400 lines)

The manipulator classes contain matrix math for snapping objects to grid
positions, rotation increments, and camera orbits. Tests target the static
utility methods and the snap/grid math, not the interactive drag pipeline.

| Test file | Source classes covered | Est. lines |
| --- | --- | ---: |
| `AbstractManipulatorTest` | `AbstractManipulator` base (state machine, cursor tracking) | 80 |
| `SnapUtilitiesTest` | `SnapUtilities` (snap to grid, snap angle, snap to plane) | 100 |
| `LinearDragManipulatorTest` | `LinearDragManipulator` (axis-constrained drag math) | 80 |
| `ObjectRotateManipulatorTest` | `ObjectRotateManipulator` (rotation axis, angle snapping) | 80 |
| `CameraDragManipulatorTest` | `CameraDragManipulator` (orbit math, zoom factor) | 60 |

**Test strategy:** Manipulator tests call the static math methods directly
(e.g., `SnapUtilities.snapToGrid(value, gridSize)`). The interactive drag
pipeline (which requires `AbstractTransformable`) is not tested. For
manipulator classes with instance methods, tests create minimal subclasses
that stub out the scenegraph dependency.

### Tier 5 — Implementation helpers (5 files, ~400 lines)

| Test file | Source classes covered | Est. lines |
| --- | --- | ---: |
| `PerspectiveCameraMarkerImpTest` | `PerspectiveCameraMarkerImp`, `CameraMarkerImp` (marker transform, FOV) | 80 |
| `DynamicResourceTest` | `DynamicResource` (construction, property access, serialization) | 80 |
| `AliceResourceUtilitiesTest` | `AliceResourceUtilities` (resource path resolution, model lookup) | 80 |
| `ModelResourceLoaderTest` | `ModelResourceLoader` (resource loading, caching, error paths) | 80 |
| `UserDialogDelegateTest` | `UserDialogDelegate` (dialog result recording, null safety) | 80 |

**Test strategy:** `DynamicResource` and `AliceResourceUtilities` tests
use `@Rule TemporaryFolder` for file-system operations. `ModelResourceLoader`
tests verify cache behavior with repeated lookups.

### Tier 6 — Event data classes and animation (5 files, ~300 lines)

| Test file | Source classes covered | Est. lines |
| --- | --- | ---: |
| `ManipulationEventTest` | `ManipulationEvent` (construction, type, source entity) | 60 |
| `TargetBasedFrameObserverTest` | `TargetBasedAnimationFrameObserver` (frame callback, target tracking) | 60 |
| `StorytellingResourcesTest` | `StorytellingResources` (resource registration, lookup, iteration) | 80 |
| `SBipedTest` | `SBiped` facade (joint accessors, pose, walk, turn) | 60 |
| `SQuadrupedTest` | `SQuadruped` facade (joint accessors, specific quadruped joints) | 40 |

**Test strategy:** Event classes are constructed with `null` source and
synthetic data, then accessors are verified. Facade tests use the
established `TestScene` + `StubFactory` pattern from Phase 1. Resource
tests use `@Rule TemporaryFolder`.

## Exclusions

| Class/Package | Reason |
| --- | --- |
| `org.alice.interact.manipulator.Camera*` (full drag) | Requires `OnscreenRenderTarget` for pick ray |
| `org.lgna.story.implementation.eventhandling.*` | Event dispatch requires `ProgramImp` with render target |
| `org.lgna.story.implementation.overlay.*` | Overlay rendering requires GL context |
| `org.lgna.story.implementation.alice.modelresource.*` | Model resource loading requires classpath resources |
| Manipulator interactive drag pipeline | Requires `AbstractTransformable` scenegraph connection |

## Verification

```bash
# Run all core/story-api tests
mvn test -pl core/story-api -am

# Run only IK solver tests
mvn test -pl core/story-api \
  -Dtest="org.lgna.ik.core.solver.*Test,org.lgna.ik.core.*Test"

# Run only interact condition tests
mvn test -pl core/story-api \
  -Dtest="org.alice.interact.condition.*Test"

# Run only manipulator math tests
mvn test -pl core/story-api \
  -Dtest="org.alice.interact.manipulator.*Test"

# Generate coverage report
mvn verify -pl core/story-api -am
# Open core/story-api/target/site/jacoco/index.html

# Full aggregate verification
mvn -DincludeSims=false -Dinstall4j.skip -Pcoverage verify
python3 scripts/summarize-jacoco-coverage.py \
  --output coverage-summary.md \
  --evidence-manifest coverage-evidence-manifest.json \
  --min-module-line-percent core/story-api=65.0
```

## Risks and mitigations

| Risk | Severity | Mitigation |
| --- | --- | --- |
| IK `Chain` constructor needs `JointImp` objects | HIGH | Test `Bone.Axis` pure math only; use reflection for `Chain` init |
| Manipulators need scenegraph `AbstractTransformable` | HIGH | Test `InputState`, conditions, `SnapUtilities` math only |
| `StorytellingResources` needs filesystem | MEDIUM | Use `@Rule TemporaryFolder` |
| `SBiped`/`SQuadruped` need `JointedModelImp` setup | MEDIUM | Reuse `StubFactory` from Phase 1 |
| Event classes with `null` source | LOW | Confirmed safe — `ManipulationEvent` accepts null |
