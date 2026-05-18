# Core/IDE Deep Test Coverage — StageIDE Package

> **Issue:** [#764](https://github.com/rysweet/RabbitHole/issues/764)
> **Scope:** 8 new JUnit 4 test files covering `org.alice.stageide` — cascade filler-inners,
> custom expression creators, program composition, and StoryApiConfigurationManager behavioral gaps
> **Line target:** 2100+ additional lines of test code
> **Build verification:** `mvn test -pl core/ide -am -Dtest='*ContractTest,*BehavioralTest,*RegistrationTest' -DfailIfNoTests=false -q`

---

## Overview

This round of test coverage completes the `org.alice.stageide` package — the
StoryIDE layer that wires Alice 3's cascade menus, custom expression creators,
and program lifecycle into a coherent editing experience. All 8 test files are
JUnit 4, headless-safe, and use reflection or source-analysis patterns that
require no display server, no croquet Application boot, and no IDE singleton.

| Package | Classes Under Test | New Test File(s) | Approx. Lines |
|---|---|---|---|
| `o.a.stageide.cascade.fillerinners` | 16 event-listener filler-inners | `FillerInnerEventListenerContractTest` | ~350 |
| `o.a.stageide.cascade.fillerinners` | `SourceFillerInner`, `AudioSourceFillerInner`, `ImageSourceFillerInner` | `FillerInnerResourceContractTest` | ~250 |
| `o.a.stageide.cascade.fillerinners` | `ColorFillerInner`, `KeyFillerInner`, `ImagePaintFillerInner`, `ModelResourceFillerInner` | `FillerInnerExpressionContractTest` | ~300 |
| `o.a.stageide.cascade` | `ExpressionCascadeManager` (21 registrations + orphan detection) | `ExpressionCascadeManagerRegistrationTest` | ~300 |
| `o.a.stageide.custom` | `KeyCustomExpressionCreatorComposite`, `KeyState` | `KeyCustomExpressionCreatorContractTest` | ~200 |
| `o.a.stageide.custom` | `ColorCustomExpressionCreatorComposite` | `ColorCustomExpressionCreatorContractTest` | ~200 |
| `o.a.stageide.program` | `ProgramContext`, `RunProgramContext` | `ProgramContextContractTest` | ~300 |
| `o.a.stageide` | `StoryApiConfigurationManager`, sceneeditor helpers | `StoryApiConfigManagerBehavioralTest` | ~300 |

All tests are read-only characterization — they verify structural contracts and
registration completeness without mutating production state.

---

## Running the Tests

```bash
# Run all 8 new test files
mvn test -pl core/ide -am \
  -Dtest='*ContractTest,*BehavioralTest,*RegistrationTest' \
  -DfailIfNoTests=false -q

# Run a single test class
mvn test -pl core/ide -am -Dtest=FillerInnerEventListenerContractTest -q

# Run only cascade filler-inner tests
mvn test -pl core/ide -am \
  -Dtest='FillerInnerEventListenerContractTest,FillerInnerResourceContractTest,FillerInnerExpressionContractTest' -q

# Run with JaCoCo coverage report
mvn verify -pl core/ide -am -Pcoverage
```

The JaCoCo HTML report is generated at:
```
core/ide/target/site/jacoco/index.html
```

### Prerequisites

| Requirement | Detail |
|---|---|
| JDK | 17+ (project standard) |
| Maven | 3.9+ |
| Display | Not required — all tests are headless |
| Submodule | `git submodule update --init tweedle-lang` before first build |
| AWT headless | Automatic — tests use `java.awt.headless=true` via surefire config |

---

## Test Architecture

### Testing Patterns Used

All 8 files use one or more of these headless-safe patterns:

1. **Reflection Contract Testing** — `Class.forName()` on hardcoded `org.alice.stageide.*`
   class names to verify hierarchy, method signatures, and field presence without
   instantiation. Safe in headless CI because no constructors are called.

2. **Source-Analysis Testing** — Reads `.java` source files from `src/main/java/`
   using the standard Maven layout. Parses class structure via substring/regex
   matching. Resilient to formatting changes; would break only on class renames.

3. **Data-Driven Parameterization** — Filler-inner families share structural
   contracts. Test methods iterate over `String[]` arrays of class names,
   verifying each class satisfies the same contract (extends correct base,
   declares required methods, handles expected value types).

4. **Singleton Verification** — `getInstance()` return-type and accessibility
   checks via reflection, without calling the method (which would trigger
   GUI initialization).

### Class Hierarchy Under Test

```
ExpressionFillerInner (abstract)
 ├── ArrowKeyListenerFillerInner          ← event-listener family
 ├── NumberKeyListenerFillerInner
 ├── MouseClickedOnScreenFillerInner
 ├── MouseClickOnObjectFillerInner
 ├── KeyListenerFillerInner
 ├── TransformationListenerFillerInner
 ├── TimerEventListenerFillerInner
 ├── SceneActivationEventFillerInner
 ├── ComesIntoViewEventListenerFillerInner
 ├── LeavesViewEventListenerFillerInner
 ├── StartCollisionListenerFillerInner
 ├── EndCollisionListenerFillerInner
 ├── EnterProximityEventListenerFillerInner
 ├── ExitProximityEventListenerFillerInner
 ├── StartOcclusionEventListenerFillerInner
 ├── EndOcclusionEventListenerFillerInner
 ├── ColorFillerInner                     ← expression family
 ├── KeyFillerInner
 ├── ImagePaintFillerInner
 ├── ModelResourceFillerInner
 └── SourceFillerInner<R>                 ← resource family (abstract)
      ├── AudioSourceFillerInner
      └── ImageSourceFillerInner

CustomExpressionCreatorComposite<V>
 ├── KeyCustomExpressionCreatorComposite
 └── ColorCustomExpressionCreatorComposite

ProgramContext (abstract)
 └── RunProgramContext
```

---

## Test File Reference

### 1. `FillerInnerEventListenerContractTest`

**Location:** `core/ide/src/test/java/org/alice/stageide/cascade/fillerinners/FillerInnerEventListenerContractTest.java`

Structural contract tests for all 16 event-listener filler-inner classes. Uses
data-driven iteration over class names to verify each class satisfies the
`ExpressionFillerInner` contract without instantiation.

#### Test Cases

| Method | What It Tests |
|---|---|
| `allEventListenerFillerInners_extendExpressionFillerInner` | Every event-listener filler-inner class extends `ExpressionFillerInner`. Iterates over all 16 class names via `Class.forName()`. |
| `allEventListenerFillerInners_declareAppendItemsMethod` | Each class declares or inherits `appendItems(List, ValueDetails, boolean, Expression)`. |
| `allEventListenerFillerInners_arePublicClasses` | Each class has `public` visibility (required for cascade manager registration). |
| `allEventListenerFillerInners_havePublicConstructor` | Each class has at least one public constructor. |
| `arrowKeyListener_handlesCorrectEventType` | `ArrowKeyListenerFillerInner` source references `ArrowKeyEvent` or equivalent arrow-key type. |
| `numberKeyListener_handlesCorrectEventType` | `NumberKeyListenerFillerInner` source references number-key event type. |
| `collisionListenerPair_haveSymmetricStructure` | `StartCollisionListenerFillerInner` and `EndCollisionListenerFillerInner` have the same method count and parameter signatures. |
| `proximityListenerPair_haveSymmetricStructure` | `EnterProximityEventListenerFillerInner` and `ExitProximityEventListenerFillerInner` are structurally symmetric. |
| `occlusionListenerPair_haveSymmetricStructure` | `StartOcclusionEventListenerFillerInner` and `EndOcclusionEventListenerFillerInner` are structurally symmetric. |
| `viewEventListenerPair_haveSymmetricStructure` | `ComesIntoViewEventListenerFillerInner` and `LeavesViewEventListenerFillerInner` are structurally symmetric. |
| `mouseClickFillerInners_areSeparateClasses` | `MouseClickedOnScreenFillerInner` and `MouseClickOnObjectFillerInner` are distinct classes (not aliases). |
| `eventListenerFillerInnerCount_isExactly16` | Guards against silent addition/removal — the test data array must contain exactly 16 entries. |
| `allEventListenerFillerInners_areInCorrectPackage` | Every class resolves within `org.alice.stageide.cascade.fillerinners`. |
| `sceneActivationEvent_isStandalone` | `SceneActivationEventFillerInner` is not part of a start/end pair — it has no symmetric counterpart. |
| `timerEventListener_isStandalone` | `TimerEventListenerFillerInner` has no symmetric counterpart. |
| `keyListener_isDistinctFromArrowAndNumber` | `KeyListenerFillerInner` is a separate class from both `ArrowKeyListenerFillerInner` and `NumberKeyListenerFillerInner`. |

#### Usage

```bash
mvn test -pl core/ide -am -Dtest=FillerInnerEventListenerContractTest -q
```

---

### 2. `FillerInnerResourceContractTest`

**Location:** `core/ide/src/test/java/org/alice/stageide/cascade/fillerinners/FillerInnerResourceContractTest.java`

Contract tests for the `SourceFillerInner<R>` abstract hierarchy and its two
concrete implementations: `AudioSourceFillerInner` and `ImageSourceFillerInner`.

#### Test Cases

| Method | What It Tests |
|---|---|
| `sourceFillerInner_isAbstractOrGeneric` | `SourceFillerInner` is parameterized with `<R extends Resource>`. |
| `sourceFillerInner_extendsExpressionFillerInner` | Hierarchy check: `SourceFillerInner` → `ExpressionFillerInner`. |
| `audioSourceFillerInner_extendsSourceFillerInner` | `AudioSourceFillerInner` → `SourceFillerInner`. |
| `imageSourceFillerInner_extendsSourceFillerInner` | `ImageSourceFillerInner` → `SourceFillerInner`. |
| `audioSourceFillerInner_handlesAudioResource` | Source or generic-type analysis confirms `AudioResource` parameterization. |
| `imageSourceFillerInner_handlesImageResource` | Source or generic-type analysis confirms `ImageResource` parameterization. |
| `sourceFillerInner_constructorAcceptsTwoClassParams` | The `SourceFillerInner(Class<?>, Class<R>)` constructor signature is present. |
| `bothConcreteImplementations_arePublic` | Both `AudioSourceFillerInner` and `ImageSourceFillerInner` are public. |
| `bothConcreteImplementations_havePublicConstructor` | Both have at least one public constructor. |
| `sourceFillerInnerSubclassCount_isExactly2` | Guards against undetected additions to the `SourceFillerInner` family. |

#### Usage

```bash
mvn test -pl core/ide -am -Dtest=FillerInnerResourceContractTest -q
```

---

### 3. `FillerInnerExpressionContractTest`

**Location:** `core/ide/src/test/java/org/alice/stageide/cascade/fillerinners/FillerInnerExpressionContractTest.java`

Contract tests for the four standalone expression filler-inners that handle
non-event, non-resource value types: `ColorFillerInner`, `KeyFillerInner`,
`ImagePaintFillerInner`, and `ModelResourceFillerInner`.

> **Note:** `ModelResourceFillerInner` exists in the package but is **not registered**
> in `ExpressionCascadeManager`. It is tested here for structural contracts but
> flagged as a known orphan in the registration tests.

#### Test Cases

| Method | What It Tests |
|---|---|
| `allExpressionFillerInners_extendExpressionFillerInner` | All four extend `ExpressionFillerInner`. |
| `allExpressionFillerInners_declareAppendItems` | Each declares or inherits `appendItems`. |
| `colorFillerInner_handlesColorType` | Source references `Color` or `org.lgna.story.Color`. |
| `keyFillerInner_handlesKeyType` | Source references `Key` or `org.lgna.story.Key`. |
| `imagePaintFillerInner_handlesPaintType` | Source references `ImagePaint` or paint-related type. |
| `modelResourceFillerInner_handlesModelResourceType` | Source references model resource types. |
| `colorFillerInner_isNotSingleton` | No `getInstance()` method — fillers are registered by instance, not static lookup. |
| `expressionFillerInnerCount_isExactly4` | Guard against silent additions to this family. |
| `allExpressionFillerInners_areInCorrectPackage` | Package membership verification. |
| `allExpressionFillerInners_arePublicClasses` | Public visibility required for registration. |

#### Usage

```bash
mvn test -pl core/ide -am -Dtest=FillerInnerExpressionContractTest -q
```

---

### 4. `ExpressionCascadeManagerRegistrationTest`

**Location:** `core/ide/src/test/java/org/alice/stageide/cascade/ExpressionCascadeManagerRegistrationTest.java`

Cross-reference verification: ensures every filler-inner class in the
`fillerinners` package is actually registered by `ExpressionCascadeManager`'s
constructor, and vice versa — every registration target exists as a class.

#### Test Cases

| Method | What It Tests |
|---|---|
| `constructor_registersAllKnownFillerInners` | Source-analysis of `ExpressionCascadeManager.java` constructor confirms references to all 21 registered filler-inner class names (16 event-listener + 2 resource + 3 expression). Note: `ModelResourceFillerInner` exists in the package but is intentionally not registered — it is an orphan class. |
| `noOrphanedFillerInnerClasses` | Every `.java` file in `cascade/fillerinners/` (excluding `SourceFillerInner` abstract base and `ModelResourceFillerInner` known-orphan) is referenced in `ExpressionCascadeManager.java`. Catches new additions that lack registration. |
| `noPhantomRegistrations` | Every class name referenced in the constructor's filler-inner setup resolves via `Class.forName()`. Catches stale registrations after renames. |
| `registeredFillerInnerCount_isExactly21` | Exactly 21 `addExpressionFillerInner` calls exist in the constructor. Package has 23 files (21 registered + 1 abstract base + 1 known orphan). |
| `expressionCascadeManager_extendsBaseManager` | `o.a.stageide.cascade.ExpressionCascadeManager` extends `o.a.ide.cascade.ExpressionCascadeManager`. |
| `expressionCascadeManager_hasPublicConstructor` | Required for `StoryApiConfigurationManager` to instantiate it. |
| `relationalTypeRegistrations_includeExpectedTypes` | Source confirms relational-to-boolean filler registrations for `SThing`, `MoveDirection`, `TurnDirection`, `RollDirection`, `Key`, `Color`, `Paint`. |
| `addSimsExpressionFillerInners_isProtected` | The `addSimsExpressionFillerInners()` hook is `protected` (extensibility contract). |
| `createPartMenuModel_isProtected` | The `createPartMenuModel(...)` override is `protected`. |

#### Usage

```bash
mvn test -pl core/ide -am -Dtest=ExpressionCascadeManagerRegistrationTest -q
```

---

### 5. `KeyCustomExpressionCreatorContractTest`

**Location:** `core/ide/src/test/java/org/alice/stageide/custom/KeyCustomExpressionCreatorContractTest.java`

Contract tests for `KeyCustomExpressionCreatorComposite` and the `KeyState`
singleton that backs it.

#### Test Cases

| Method | What It Tests |
|---|---|
| `keyComposite_extendsCECC` | `KeyCustomExpressionCreatorComposite` extends `CustomExpressionCreatorComposite`. |
| `keyComposite_hasSingletonHolder` | Source contains `SingletonHolder` inner class pattern. |
| `keyComposite_getInstanceReturnsSameType` | `getInstance()` return type is `KeyCustomExpressionCreatorComposite`. |
| `keyState_extendsSimpleItemState` | `KeyState` extends `SimpleItemState<Key>`. |
| `keyState_hasSingletonHolder` | `KeyState` uses the lazy-holder singleton pattern. |
| `keyState_getInstanceReturnsSameType` | `getInstance()` return type is `KeyState`. |
| `keyState_isUsedByKeyComposite` | Source-analysis confirms `KeyCustomExpressionCreatorComposite` references `KeyState`. |
| `keyComposite_isInCorrectPackage` | Package is `org.alice.stageide.custom`. |
| `keyViewController_existsInComponents` | `KeyViewController` class exists in `custom.components` subpackage. |
| `keyCustomExpressionCreatorView_existsInComponents` | `KeyCustomExpressionCreatorView` class exists in `custom.components` subpackage. |

#### Usage

```bash
mvn test -pl core/ide -am -Dtest=KeyCustomExpressionCreatorContractTest -q
```

---

### 6. `ColorCustomExpressionCreatorContractTest`

**Location:** `core/ide/src/test/java/org/alice/stageide/custom/ColorCustomExpressionCreatorContractTest.java`

Contract tests for `ColorCustomExpressionCreatorComposite` — the singleton
composite that provides a `JColorChooser`-backed color picker in cascade menus.

#### Test Cases

| Method | What It Tests |
|---|---|
| `colorComposite_extendsCECC` | `ColorCustomExpressionCreatorComposite` extends `CustomExpressionCreatorComposite`. |
| `colorComposite_hasSingletonHolder` | Source contains lazy-holder singleton pattern. |
| `colorComposite_getInstanceReturnsSameType` | `getInstance()` return type matches. |
| `colorComposite_isPublicClass` | Public visibility for registration. |
| `colorComposite_sourceMentionsJColorChooser` | Source references `JColorChooser` or color-chooser wiring. |
| `colorComposite_isInCorrectPackage` | Package is `org.alice.stageide.custom`. |
| `colorComposite_isNotSameAsKeyComposite` | Color and Key composites are distinct classes. |
| `colorComposite_hasNoKeyStateDependency` | Source does not reference `KeyState` (separation of concerns). |

#### Usage

```bash
mvn test -pl core/ide -am -Dtest=ColorCustomExpressionCreatorContractTest -q
```

---

### 7. `ProgramContextContractTest`

**Location:** `core/ide/src/test/java/org/alice/stageide/program/ProgramContextContractTest.java`

Contract tests for `ProgramContext` (abstract base) and `RunProgramContext`
(concrete runtime). Verifies adapter-registration contracts, lifecycle method
signatures, and constructor parameter requirements without instantiation.

#### Test Cases

| Method | What It Tests |
|---|---|
| `programContext_isAbstract` | `ProgramContext` has the `abstract` modifier. |
| `programContext_constructorAcceptsNamedUserType` | Constructor parameter type is `NamedUserType`. |
| `programContext_declaresGetProgramInstance` | `getProgramInstance()` method exists. |
| `programContext_declaresGetProgram` | `getProgram()` method exists. |
| `programContext_declaresGetProgramImp` | `getProgramImp()` method exists. |
| `programContext_declaresGetVirtualMachine` | `getVirtualMachine()` method exists. |
| `programContext_declaresGetOnscreenRenderTarget` | `getOnscreenRenderTarget()` method exists. |
| `programContext_declaresSetActiveScene` | `setActiveScene()` method exists. |
| `programContext_declaresCleanUpProgram` | `cleanUpProgram()` method exists. |
| `runProgramContext_extendsProgramContext` | `RunProgramContext` → `ProgramContext`. |
| `runProgramContext_constructorAcceptsNamedUserType` | Same constructor contract as parent. |
| `runProgramContext_declaresInitializeInContainer` | `initializeInContainer(ProgramImp.AwtContainerInitializer)` exists. |
| `runProgramContext_isConcreteClass` | `RunProgramContext` is NOT abstract. |
| `programContext_lifecycleMethodCount` | Expected number of lifecycle methods matches source analysis. |
| `programContext_isInCorrectPackage` | Package is `org.alice.stageide.program`. |
| `programContext_adapterRegistrationCount` | Source-analysis counts adapter registrations in constructor body (expected: 17). |

#### Usage

```bash
mvn test -pl core/ide -am -Dtest=ProgramContextContractTest -q
```

---

### 8. `StoryApiConfigManagerBehavioralTest`

**Location:** `core/ide/src/test/java/org/alice/stageide/StoryApiConfigManagerBehavioralTest.java`

Behavioral characterization of `StoryApiConfigurationManager` methods not
covered by the existing `StoryApiConfigurationManagerTest`, plus sceneeditor
gap-fill tests for `ShowJointedModelJointAxesState` and `ThumbnailGenerator`.

#### Test Cases

| Method | What It Tests |
|---|---|
| `getExpressionCreator_methodExists` | `getExpressionCreator()` method is declared and public. |
| `getExpressionCreator_returnTypeIsCorrect` | Return type is an expression-creator type (not void). |
| `getCategoryProcedureSubComposites_methodExists` | Accessor for category procedure sub-composites is present. |
| `getCategoryFunctionSubComposites_methodExists` | Accessor for category function sub-composites is present. |
| `getCategoryOrAlphabeticalProcedureSubComposites_methodExists` | Alphabetical variant accessor is present. |
| `getCategoryOrAlphabeticalFunctionSubComposites_methodExists` | Alphabetical variant accessor is present. |
| `isSignatureLocked_methodExists` | Signature-locking check method is present. |
| `isTabClosable_methodExists` | Tab-closability check method is present. |
| `augmentTypeIfNecessary_methodExists` | Type-augmentation hook is present. |
| `createReplacementForFieldAccessIfAppropriate_methodExists` | Field-access replacement factory is present. |
| `constructor_registersCustomExpressionCreators` | Source-analysis confirms constructor wiring of custom expression creators (Key, Color, AudioSource, VolumeLevel). |
| `singleton_usesLazyHolderPattern` | Source contains `SingletonHolder` inner class and `getInstance()`. |
| `showJointedModelJointAxesState_existsInSceneeditor` | `ShowJointedModelJointAxesState` class resolves in `org.alice.stageide.sceneeditor`. |
| `showJointedModelJointAxesState_isPublic` | Public visibility required for sceneeditor panel wiring. |
| `thumbnailGenerator_existsInSceneeditor` | `ThumbnailGenerator` class resolves in `org.alice.stageide.sceneeditor`. |
| `thumbnailGenerator_isPublic` | Public visibility. |
| `thumbnailGenerator_declaresTakeThumbnail` | Source or reflection confirms a thumbnail-generation method exists. |
| `cameraOption_existsInSceneeditor` | `CameraOption` class resolves in `org.alice.stageide.sceneeditor`. |

#### Usage

```bash
mvn test -pl core/ide -am -Dtest=StoryApiConfigManagerBehavioralTest -q
```

---

## Configuration

### Headless Environment

All tests run headless. The `core/ide` module's surefire configuration sets:

```xml
<argLine>-Djava.awt.headless=true</argLine>
```

No additional configuration is needed. If running outside Maven (e.g., IntelliJ),
add `-Djava.awt.headless=true` to the JVM arguments.

### Source Path Resolution

Source-analysis tests locate production `.java` files relative to the standard
Maven layout:

```
core/ide/src/main/java/org/alice/stageide/...
```

This path is resolved at test time via:
```java
Path projectRoot = Paths.get(System.getProperty("user.dir"));
// or via test resource classpath resolution
```

The tests assume the working directory is the repository root (standard Maven
behavior). No additional configuration is needed.

---

## Guard Tests

Three tests act as **guards** that break when the production code changes
without corresponding test updates:

| Guard Test | Breaks When |
|---|---|
| `eventListenerFillerInnerCount_isExactly16` | A new event-listener filler-inner is added or one is removed |
| `noOrphanedFillerInnerClasses` | A new filler-inner is added to `cascade/fillerinners/` but not registered in `ExpressionCascadeManager` (excludes known orphan `ModelResourceFillerInner`) |
| `programContext_adapterRegistrationCount` | An adapter registration is added or removed from `ProgramContext`'s constructor |

When a guard test fails, update both the production code and the corresponding
test data array/count constant.

---

## Relationship to Existing Tests

These 8 files complement, but do not overlap with, existing test coverage:

| Existing File | What It Covers | Gap Filled By New Tests |
|---|---|---|
| `StoryApiConfigurationManagerTest` | Static constants, type checking, method configuration | Behavioral methods (`getExpressionCreator`, category composites, singleton pattern) |
| `BooleanFillerInnerDeepTest` | `BooleanFillerInner` deep behavior | Event-listener, resource, and expression filler-inner families |
| `ConstantsOwningFillerInnerTest` | `ConstantsOwningFillerInner` | Same — non-overlapping filler-inner families |
| `NumberFillerInnerDeepTest` | `IntegerFillerInner`, `DoubleFillerInner` | Same — non-overlapping filler-inner families |
| `FillerInnerTypeResolutionTest` | Type resolution across all fillers | Registration completeness and cross-reference verification |
| `AudioSourceCustomExpressionCreatorCompositeTest` | AudioSource composite | Key and Color composites |
| `VolumeLevelCustomExpressionCreatorCompositeTest` | VolumeLevel composite | Key and Color composites |
| Sceneeditor tests (36 files, 6000+ lines) | SetUpMethodGenerator, SceneFieldCodeGenerator, StorytellingSceneEditor, etc. | ShowJointedModelJointAxesState, ThumbnailGenerator, CameraOption |

No existing test files are modified.

---

## Security Considerations

- All `Class.forName()` targets are hardcoded `org.alice.stageide.*` string literals — no dynamic class name derivation
- No credentials, PII, or sensitive data in any test file
- No network access — all tests are offline reflection and source-analysis
- Tests are read-only characterization — no mutation of production state

---

## Risks and Mitigations

| Risk | Mitigation |
|---|---|
| Source-analysis tests use substring matching — could break on class renames | Guard tests detect renames immediately; fix is updating string literals |
| `ExpressionCascadeManager` instantiation might gain side effects | Tests use source-analysis instead of instantiation; constructor is never called |
| AWT headless prevents custom composite instantiation | Reflection-only approach — no `getInstance()` calls |
| Source path resolution depends on Maven layout | Standard Maven convention; would only break on project restructure |
| New filler-inners added without registration | `noOrphanedFillerInnerClasses` guard test catches this automatically |
| `ModelResourceFillerInner` is an orphan (not registered) | Documented as known orphan; excluded from orphan guard. Could indicate dead code or future feature |

---

## Examples

### Adding a New Event-Listener Filler-Inner

When adding a new filler-inner (e.g., `DoubleClickListenerFillerInner`):

1. Create the class in `cascade/fillerinners/`
2. Register it in `ExpressionCascadeManager`'s constructor
3. Add the class name to `FillerInnerEventListenerContractTest.EVENT_LISTENER_CLASSES`
4. Update `eventListenerFillerInnerCount_isExactly16` → `isExactly17`
5. Run: `mvn test -pl core/ide -am -Dtest='*ContractTest,*RegistrationTest' -q`

### Adding a New Custom Expression Creator

When adding a new custom expression creator composite:

1. Create the composite class in `custom/`
2. Wire it in `StoryApiConfigurationManager`'s constructor
3. Add contract tests following the `KeyCustomExpressionCreatorContractTest` pattern
4. Update `StoryApiConfigManagerBehavioralTest.constructor_registersCustomExpressionCreators`
5. Run: `mvn test -pl core/ide -am -Dtest='*ContractTest,*BehavioralTest' -q`

### Verifying Registration Completeness

To verify all filler-inners are properly registered after any cascade changes:

```bash
mvn test -pl core/ide -am -Dtest=ExpressionCascadeManagerRegistrationTest -q
```

A failure in `noOrphanedFillerInnerClasses` means a `.java` file exists in
`cascade/fillerinners/` that isn't referenced in `ExpressionCascadeManager`.
A failure in `noPhantomRegistrations` means the manager references a class
that no longer exists.
