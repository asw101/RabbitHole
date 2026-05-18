# How to run the Issue #775 coverage push tests

This guide explains how to run, verify, and troubleshoot the 84 test files
added by Issue #775 across `core/croquet`, `core/util`, and `core/story-api`.

## Prerequisites

- JDK 17+ (project default)
- Maven 3.9+
- Tweedle grammar submodule initialized:
  ```sh
  git submodule update --init tweedle-lang
  ```

## Quick start — run all three modules

```sh
mvn test -pl core/croquet,core/util,core/story-api -am -Djava.awt.headless=true
```

Expected: all tests pass in under 3 minutes. No tests require a display,
GPU, or network.

## Run a single module

```sh
mvn test -pl core/croquet -Djava.awt.headless=true
mvn test -pl core/util
mvn test -pl core/story-api -am
```

Note: `core/story-api` requires `-am` (also-make) to build its parent
dependencies. `core/util` is self-contained. `core/croquet` should be run
with `-Djava.awt.headless=true` to prevent accidental AWT peer creation.

## Run a specific test class

```sh
# Croquet trigger tests
mvn test -pl core/croquet -Dtest=TriggerTest -Djava.awt.headless=true

# Util animation tests
mvn test -pl core/util -Dtest=DurationBasedAnimationTest

# Story-API IK solver tests
mvn test -pl core/story-api -am -Dtest=BoneTest

# Run all tests in a package
mvn test -pl core/story-api -am \
  -Dtest="org.lgna.ik.core.solver.*Test"
```

## Run tests by tier

### core/croquet tiers

```sh
# Tier 1: Triggers
mvn test -pl core/croquet -Djava.awt.headless=true \
  -Dtest="org.lgna.croquet.triggers.*Test"

# Tier 2: Cascade runtime
mvn test -pl core/croquet -Djava.awt.headless=true \
  -Dtest="org.lgna.croquet.imp.cascade.*Test"

# Tier 3: History steps
mvn test -pl core/croquet -Djava.awt.headless=true \
  -Dtest="org.lgna.croquet.history.*Test"

# Tier 4: Preferences
mvn test -pl core/croquet -Djava.awt.headless=true \
  -Dtest="org.lgna.croquet.preferences.*Test"
```

### core/util tiers

```sh
# Tier 1: AWT utilities
mvn test -pl core/util \
  -Dtest="edu.cmu.cs.dennisc.java.awt.*Test"

# Tier 2: Animation deepening
mvn test -pl core/util \
  -Dtest="edu.cmu.cs.dennisc.animation.*Test"

# Tier 3: Codec edge cases
mvn test -pl core/util \
  -Dtest="edu.cmu.cs.dennisc.codec.*Test"

# Tier 4: Image/texture utilities
mvn test -pl core/util \
  -Dtest="edu.cmu.cs.dennisc.image.*Test,edu.cmu.cs.dennisc.texture.*Test"
```

### core/story-api tiers

```sh
# Tier 1: IK solver
mvn test -pl core/story-api -am \
  -Dtest="org.lgna.ik.core.solver.*Test,org.lgna.ik.core.*Test"

# Tier 2: Interact conditions
mvn test -pl core/story-api -am \
  -Dtest="org.alice.interact.condition.*Test"

# Tier 3: Handle/input state
mvn test -pl core/story-api -am \
  -Dtest="org.alice.interact.HandleSetTest,org.alice.interact.HandleManagerTest,org.alice.interact.InputStateTest,org.alice.interact.PickHintDeepTest"

# Tier 4: Manipulator math
mvn test -pl core/story-api -am \
  -Dtest="org.alice.interact.manipulator.*Test"
```

## Measure coverage with JaCoCo

### Single-module coverage

```sh
# Quick per-module coverage check
mvn test jacoco:report -pl core/croquet -Djava.awt.headless=true
# Open core/croquet/target/site/jacoco/index.html

mvn test jacoco:report -pl core/util
# Open core/util/target/site/jacoco/index.html

mvn test jacoco:report -pl core/story-api -am
# Open core/story-api/target/site/jacoco/index.html
```

### Full aggregate coverage

Run the same command used by CI:

```sh
mvn -DincludeSims=false -Dinstall4j.skip -Pcoverage verify
python3 scripts/summarize-jacoco-coverage.py \
  --output coverage-summary.md \
  --evidence-manifest coverage-evidence-manifest.json \
  --target-aggregate-line-percent 70.0 \
  --min-aggregate-line-percent 8.0 \
  --min-module-line-percent core/croquet=45.0 \
  --min-module-line-percent core/util=65.0 \
  --min-module-line-percent core/story-api=65.0
```

### View coverage reports

| Module | Report path |
| --- | --- |
| `core/croquet` | `core/croquet/target/site/jacoco/index.html` |
| `core/util` | `core/util/target/site/jacoco/index.html` |
| `core/story-api` | `core/story-api/target/site/jacoco/index.html` |
| Aggregate | `coverage-report/target/site/jacoco-aggregate/index.html` |
| CSV (scripting) | `coverage-report/target/site/jacoco-aggregate/jacoco.csv` |

## Coverage targets

| Module | Baseline | Target | Ratchet floor |
| --- | ---: | ---: | ---: |
| `core/croquet` | 30.4% | 50%+ | 45.0% |
| `core/util` | 51.6% | 70%+ | 65.0% |
| `core/story-api` | 52.3% | 70%+ | 65.0% |

The ratchet floors are deliberately set below the targets to provide margin.
See [Coverage reporting and ratchets](../reference/coverage-reporting.md)
for the ratchet policy.

## Test file inventory

### core/croquet — 27 files

| Package | Test files | Count |
| --- | --- | ---: |
| `o.l.croquet.triggers` | `TriggerTest`, `EventObjectTriggerTest`, `InputEventTriggerTest`, `ComponentEventTriggerTest`, `DocumentEventTriggerTest`, `MiscTriggerTest` | 6 |
| `o.l.croquet.imp.cascade` | `RtNodeTest`, `RtItemTest`, `RtBlankTest`, `RtRootTest`, `RtSeparatorTest`, `BlankNodeTest` | 6 |
| `o.l.croquet.history` | `DragStepTest`, `MenuSelectionTest`, `PrepStepTest`, `UserActivityDeepTest`, `MenuItemSelectStepTest` | 5 |
| `o.l.croquet.preferences` | `PreferenceManagerTest`, `PreferenceStringStateTest`, `PreferencesManagerTest` | 3 |
| `o.l.croquet.codecs` | `SimpleTabCompositeCodecTest` | 1 |
| `o.l.croquet.icon` | `IconFactoryTest` | 1 |
| `o.l.croquet.data` | `MutableListDataDeepTest` | 1 |
| `o.l.croquet.meta` | `StateTrackingMetaStateTest` | 1 |
| `o.l.croquet` | `StringStateDeepTest`, `ItemStateTest`, `AbstractElementDeepTest`, `CascadeItemTest`, `AbstractMenuModelTest`, `ImporterTest` | 6 |
| `o.l.croquet.imp.*` | `WizardDialogLogicTest`, `BooleanStateMenuModelTest`, `SingleSelectListStateMenuTest`, `FrameIsShowingStateTest` | 4 |

### core/util — 27 files

| Package | Test files | Count |
| --- | --- | ---: |
| `e.c.c.d.java.awt` | `RectangleUtilitiesTest`, `DimensionUtilitiesTest`, `FontUtilitiesTest`, `BeveledShapeTest`, `MultilineTextTest`, `MouseEventUtilitiesTest`, `AreaUtilitiesTest` | 7 |
| `e.c.c.d.animation` | `AbstractAnimatorTest`, `AbstractAnimationTest`, `WaitingAnimationTest`, `TraditionalStyleTest`, `DurationBasedAnimationTest`, `InterpolationAnimationTest`, `AnimationThreadTest` | 7 |
| `e.c.c.d.codec` | `BinaryCodecEdgeCasesTest`, `InputStreamBinaryDecoderTest`, `OutputStreamBinaryEncoderTest` | 3 |
| `e.c.c.d.image` | `ImageUtilitiesTest`, `TgaUtilitiesTest`, `PngUtilitiesTest` | 3 |
| `e.c.c.d.texture` | `BufferedImageTextureTest` | 1 |
| `e.c.c.d.glyph` | `GlyphVectorTest` | 1 |
| `e.c.c.d.app` | `ApplicationRootTest` | 1 |
| `e.c.c.d.worker.process` | `ProcessWorkerTest` | 1 |
| `e.c.c.d.ui` | `UndoRedoManagerTest` | 1 |
| `e.c.c.d.media` | `AudioResourceTest` | 1 |
| `e.c.c.d.java.lang` | `SystemUtilitiesDeepTest` | 1 |

### core/story-api — 30 files

| Package | Test files | Count |
| --- | --- | ---: |
| `o.l.ik.core.solver` | `BoneTest`, `ChainTest`, `SolverTest`, `ConstraintTest`, `WeightsTest` | 5 |
| `o.l.ik.core` | `IKCoreTest` | 1 |
| `o.l.ik.core.enforcer` | `JointedModelIkEnforcerTest`, `TightPositionalIkEnforcerTest` | 2 |
| `o.a.interact.condition` | `MouseDragConditionTest`, `ClickConditionsTest`, `KeyPressConditionTest`, `MouseConditionsTest`, `DragAndDropConditionTest`, `ModifierConditionsTest`, `ManipulatorConditionSetDeepTest` | 7 |
| `o.a.interact.handle` | `HandleSetTest`, `HandleManagerTest` | 2 |
| `o.a.interact` | `InputStateTest`, `PickHintDeepTest` | 2 |
| `o.a.interact.manipulator` | `AbstractManipulatorTest`, `SnapUtilitiesTest`, `LinearDragManipulatorTest`, `ObjectRotateManipulatorTest`, `CameraDragManipulatorTest` | 5 |
| `o.l.story.implementation` | `PerspectiveCameraMarkerImpTest`, `UserDialogDelegateTest` | 2 |
| `o.l.story.implementation.alice` | `DynamicResourceTest`, `AliceResourceUtilitiesTest` | 2 |
| `o.l.story.resourceutilities` | `ModelResourceLoaderTest`, `StorytellingResourcesTest` | 2 |
| `o.a.interact.event` | `ManipulationEventTest` | 1 |
| `o.a.interact.animation` | `TargetBasedFrameObserverTest` | 1 |
| `o.l.story` | `SBipedTest`, `SQuadrupedTest` | 2 |

## Troubleshooting

### Tests fail with `HeadlessException`

All Issue #775 tests are headless-safe. If a test fails with
`HeadlessException`, it is either:
1. Missing the `-Djava.awt.headless=true` system property (for croquet tests)
2. Accidentally creating a Swing component — fix by removing the AWT call

### `NullPointerException` in `Application.getActiveInstance()`

A croquet test forgot to remove Swing listeners in `@Before`. See the
listener removal pattern in [core/croquet TESTING.md](../../core/croquet/TESTING.md).

### `Group already registered`

Two tests are using the same UUID for `Group.getInstance()`. Use
`UUID.randomUUID()` for every Group construction.

### Tweedle parser classes missing

```sh
git submodule update --init tweedle-lang
```

### JaCoCo CSV is missing

Ensure you are using the `-Pcoverage` profile:
```sh
mvn -Pcoverage verify -pl core/croquet
```

### IK solver tests fail with `ClassNotFoundException`

Ensure `core/story-api` is built with dependencies (`-am` flag):
```sh
mvn test -pl core/story-api -am -Dtest=BoneTest
```

### Coverage is below the ratchet

1. Check if new source lines were added without corresponding tests.
2. Run the per-module HTML report to identify uncovered lines.
3. Add targeted tests for the uncovered paths.
4. See [Expand coverage ratchets](./expand-coverage-ratchets.md) for the
   ratchet adjustment workflow.

## Cross-references

- [core/croquet coverage push](../reference/core-croquet-coverage-push.md) — Full test inventory and patterns
- [core/util coverage push phase 3](../reference/core-util-coverage-push-phase3.md) — Full test inventory and patterns
- [core/story-api coverage push phase 2](../reference/core-story-api-coverage-push-phase2.md) — Full test inventory and patterns
- [core/croquet TESTING.md](../../core/croquet/TESTING.md) — Headless Swing isolation patterns
- [core/story-api TESTING.md](../../core/story-api/TESTING.md) — Test double strategies
- [Coverage reporting and ratchets](../reference/coverage-reporting.md) — Ratchet policy and CI workflow
- [Run the coverage sprint tests](./run-coverage-sprint-tests.md) — Earlier coverage sprint (Issue #751)
