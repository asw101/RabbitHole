# Core Croquet Test Coverage — 70%+ Push

Comprehensive unit test coverage for `org.lgna.croquet` — the Swing-based
application framework underlying Alice 3. Covers all State subclasses, all
Operation subclasses, all Codec implementations, all EditFactory
implementations, ListData variants, and MenuModel hierarchy.

**Module:** `core/croquet`
**Framework:** JUnit 4
**Total test lines:** ~27,000+ across ~146 test files
**New lines added:** ~3,790 (26 new files, 13 deepened files)
**Headless:** All tests run without a display (CI-safe via `-Djava.awt.headless=true`)

---

## Table of Contents

1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Test Strategies](#test-strategies)
4. [Work Package Index](#work-package-index)
   - [WP1: Operations](#wp1-operations)
   - [WP2: MenuModels](#wp2-menumodels)
   - [WP3: State Deepening](#wp3-state-deepening)
   - [WP4: Codecs & Data](#wp4-codecs--data)
   - [WP5: Miscellaneous New Classes](#wp5-miscellaneous-new-classes)
   - [WP6: Thin Test Deepening](#wp6-thin-test-deepening)
5. [New Test File Index](#new-test-file-index)
6. [Deepened Existing Files](#deepened-existing-files)
7. [Configuration](#configuration)
8. [Running Tests](#running-tests)
9. [Test Patterns Reference](#test-patterns-reference)
10. [Troubleshooting](#troubleshooting)

---

## Overview

The `core/croquet` module provides a Swing-based application framework with:

- **State management** — `BooleanState`, `StringState`, `BoundedIntegerState`,
  `BoundedDoubleState`, `SingleSelectListState`, `ItemState`, `ColorState`,
  `EnumConstantState`, `TabState`, and preference-backed variants.
- **Operations** — `Operation`, `ActionOperation`, `EditOperation`,
  `IteratingOperation`, `OwnedByCompositeOperation`, and their abstract bases.
- **Codecs** — `EnumCodec`, `DefaultItemCodec`, `ColorCodec`, `FileCodec`,
  `SimpleTabCompositeCodec`, and the abstract `ItemCodec<T>` contract.
- **Menu models** — `AbstractMenuModel`, `StaticMenuModel`,
  `PredeterminedMenuModel`, `BooleanStateMenuModel`, `LabelMenuSeparatorModel`,
  `PopupPrepModel`, and `CascadeMenuModel`.
- **List data** — `ImmutableListData`, `MutableListData`, `RefreshableListData`,
  and `AbstractMutableListData`.
- **History/undo** — `UserActivity`, `UndoHistory`, edit steps.
- **Edits** — `StateEdit`, `AbstractEdit`, edit factories.

### Baseline

Prior coverage push (30.4%→50%) documented in
[core-croquet-coverage-push.md](../reference/core-croquet-coverage-push.md).

| Metric | Before | After |
|--------|--------|-------|
| Test files | 120 | ~146 |
| Test lines | 23,288 | ~27,078+ |
| Test count | 2,006 | ~2,250+ |
| Failures | 0 | 0 |

### What Is Covered

| Area | Classes Tested | Strategy | Lines |
|------|---------------|----------|-------|
| ValueHolder | `ValueHolder<T>` | Direct (behavioral) | ~100 |
| EditOperation | `EditOperation` | Direct (2-arg ctor) | ~120 |
| OwnedByComposite operations | `AbstractOwnedByCompositeOperation`, `OwnedByCompositeOperation` | Reflection | ~180 |
| OwnedByCompositeOperationSubKey | `OwnedByCompositeOperationSubKey` | Direct (value class) | ~140 |
| OperationImp | `OperationImp` (name/icon/accelerator) | Reflection + behavioral | ~120 |
| StaticMenuModel | `StaticMenuModel` | Concrete stub | ~140 |
| PredeterminedMenuModel | `PredeterminedMenuModel` | Concrete stub | ~120 |
| LabelMenuSeparatorModel | `LabelMenuSeparatorModel` | Direct instantiation | ~100 |
| PopupPrepModel | `PopupPrepModel.SwingModel` | Direct + event | ~100 |
| EmptyConditionText | `EmptyConditionText` | Concrete stub | ~80 |
| HtmlStringValue | `HtmlStringValue` | Concrete stub | ~80 |
| UnsupportedGenerationException | `UnsupportedGenerationException` | Direct (all ctors) | ~60 |
| EmptyIconFactory | `EmptyIconFactory` | Singleton + createIcon | ~60 |
| ImageIconFactory | `ImageIconFactory` | Ctors + scaling | ~80 |
| ActivityNode | `ActivityNode` | Listeners + fireChanged | ~80 |
| CascadeLabelSeparator | `CascadeLabelSeparator` | Localize + isValid | ~60 |
| BoundedNumberState.AtomicChange | `AtomicChange` fluent builder | Direct | ~80 |
| State deepening (8 files) | `BoundedInteger`, `BoundedDouble`, `SingleSelect`, `String`, `Boolean` | Behavioral | ~800 |
| Codec deepening (3 files) | `EnumCodec`, list data codecs | Behavioral | ~500 |
| Thin test deepening (9 files) | Various <80-line test files | Additional assertions | ~500 |

### What Is NOT Covered

- **Swing rendering** — `paintComponent` / `paintBorder` calls requiring a live
  `Graphics2D` context. Structural assertions cover constructors and properties
  but not pixel-level rendering.
- **Application singleton** — Classes whose constructors transitively call
  `Application.getActiveInstance()` at construction time are tested via
  reflection only. The dependency chain is documented per-class.
- **OperationOwningComposite instantiation** — `OwnedByCompositeOperation`
  requires a `Composite<V>` owner at construction. Since composites pull in the
  full UI stack, these are tested via reflection hierarchy and method-signature
  verification.

---

## Architecture

### Test Utilities

All croquet tests share `CroquetTestUtils`, which provides:

```java
// Deterministic UUIDs for test isolation
UUID id = CroquetTestUtils.nextTestUUID();

// Listener removal helpers — prevent Application.getActiveInstance() NPE
CroquetTestUtils.removeItemListeners(booleanState);
CroquetTestUtils.removeSpinnerChangeListeners(boundedState);
CroquetTestUtils.removeDocumentListeners(stringState);
CroquetTestUtils.removeListSelectionListeners(listState);

// Shared string codec for list/state tests
ItemCodec<String> codec = CroquetTestUtils.STRING_CODEC;
```

### Test Naming Conventions

| Suffix | Purpose | Example |
|--------|---------|---------|
| `*Test.java` | Primary behavioral tests | `BooleanStateTest.java` |
| `*DeepTest.java` | Deep coverage of complex classes | `StringStateDeepTest.java` |
| `*ExtendedTest.java` | Extended behavioral edge cases | `StringStateExtendedTest.java` |
| `*CoverageTest.java` | Reflection/structural coverage | `ColorStateCoverageTest.java` |

### Headless Testing Pattern

Every test file in `core/croquet` runs headlessly. The pattern for State subclasses:

```java
@Before
public void setUp() {
    state = new TestBooleanState(TEST_GROUP, false);
    CroquetTestUtils.removeItemListeners(state);
}
```

For classes that cannot be instantiated headlessly, reflection-based testing is used:

```java
@Test
public void hierarchy_extendsAbstractOperation() {
    assertTrue(AbstractOwnedByCompositeOperation.class
        .isAssignableFrom(OwnedByCompositeOperation.class));
}

@Test
public void method_fireExists() throws Exception {
    Method m = OwnedByCompositeOperation.class.getDeclaredMethod(
        "fire", Trigger.class);
    assertNotNull(m);
}
```

---

## Test Strategies

### Strategy 1: Direct Instantiation

Used for classes with simple constructors that don't trigger the Application
singleton chain.

**Applicable to:** `EditOperation`, `OwnedByCompositeOperationSubKey`,
`UnsupportedGenerationException`, `LabelMenuSeparatorModel`, `EmptyIconFactory`,
`ImageIconFactory`, `BoundedNumberState.AtomicChange`, `HtmlStringValue`.

```java
@Test
public void constructor_setsGroup() {
    EditOperation op = new EditOperation(TEST_GROUP, testEdit);
    assertEquals(TEST_GROUP, op.getGroup());
}
```

### Strategy 2: Concrete Test Stubs

Used for abstract classes that need a minimal concrete implementation for testing.

**Applicable to:** `StaticMenuModel`, `PredeterminedMenuModel`,
`EmptyConditionText`.

```java
// Minimal concrete stub extending the abstract class
private static class TestStaticMenuModel extends StaticMenuModel {
    TestStaticMenuModel(Group group) {
        super(group, UUID.fromString("..."));
    }

    @Override
    protected void localize() { }

    @Override
    protected String findDefaultLocalizedText() {
        return "Test Menu";
    }
}
```

### Strategy 3: Reflection / Structural Coverage

Used for classes deep in the UI stack that cannot be instantiated without
`Application.getActiveInstance()` or a live Composite owner.

**Applicable to:** `OwnedByCompositeOperation`,
`AbstractOwnedByCompositeOperation`, `ColorState`.

```java
@Test
public void class_isPublic() {
    assertTrue(Modifier.isPublic(OwnedByCompositeOperation.class.getModifiers()));
}

@Test
public void class_hasExpectedTypeParameter() {
    TypeVariable<?>[] params = OwnedByCompositeOperation.class.getTypeParameters();
    assertEquals(1, params.length);
    assertEquals("C", params[0].getName());
}
```

### Strategy 4: Behavioral Deepening

Used to add thorough behavioral tests to existing test files that have thin
coverage (<80 lines or <8 test methods).

**Applicable to:** `AbstractElementTest`, `DataIndexPairTest`,
`AbstractCompletionModelTest`, `ValueEventTest`, `EditInterfaceTest`,
`InitializerTest`, `ItemCodecArraysTest`, `CascadeItemTest`, `GroupTest`.

---

## Work Package Index

### WP1: Operations

**~700 lines across 6 files.**

Coverage for `ValueHolder<T>`, `EditOperation`, `OwnedByCompositeOperation`,
`AbstractOwnedByCompositeOperation`, `OwnedByCompositeOperationSubKey`,
and `OperationImp` internals.

#### ValueHolderTest.java

| Test | Assertion |
|------|-----------|
| `constructor_setsInitialValue` | Value stored at construction |
| `getValue_returnsStored` | Getter round-trip |
| `setValue_updatesValue` | Mutation works |
| `setValue_null_accepted` | Null-safe storage |
| `toString_includesValue` | Debug representation |
| `genericType_preserved` | Type parameter consistency |

#### EditOperationTest.java

| Test | Assertion |
|------|-----------|
| `constructor_setsGroup` | Group accessible via getter |
| `constructor_setsEdit` | Edit stored correctly |
| `getAction_returnsNonNull` | Swing Action is initialized |
| `setEnabled_true` | Enable/disable inherited from AbstractModel |
| `setEnabled_false` | Disabled state propagates to Action |
| `getName_returnsNonNull` | Operation has a name |
| `getMigrationId_matchesElement` | Migration identity correct |
| `getLocalizationKey_nonNull` | Localization infrastructure present |

#### OwnedByCompositeOperationCoverageTest.java

| Test | Assertion |
|------|-----------|
| `hierarchy_extendsAbstract` | Extends `AbstractOwnedByCompositeOperation` |
| `class_isPublic` | Public visibility |
| `typeParameter_isC` | Generic `<C>` parameter |
| `method_fire_exists` | `fire(Trigger)` method present |
| `method_getComposite_exists` | `getComposite()` accessor present |
| `constructor_parameterTypes` | Constructor signature correct |
| `interface_OperationOwningComposite_exists` | Required interface exists |
| `abstract_localize_inherited` | `localize()` from AbstractModel |

#### OwnedByCompositeOperationSubKeyTest.java

| Test | Assertion |
|------|-----------|
| `constructor_setsFields` | All constructor args stored |
| `equals_sameValues_true` | Value equality |
| `equals_differentValues_false` | Inequality |
| `equals_null_false` | Null-safe |
| `equals_differentType_false` | Type-safe |
| `hashCode_consistent` | Same values → same hash |
| `hashCode_differs` | Different values → different hash |
| `getter_returnsComposite` | Getter works |
| `toString_nonNull` | String representation exists |

#### AbstractOwnedByCompositeOperationCoverageTest.java

| Test | Assertion |
|------|-----------|
| `class_isAbstract` | Abstract modifier |
| `hierarchy_extendsOperation` | Extends `Operation` |
| `method_getComposite_abstract` | Abstract method declared |
| `typeParameters_count` | Expected generic parameters |
| `constructor_isProtected` | Visibility check |

#### OperationImpDeepTest.java

| Test | Assertion |
|------|-----------|
| `name_setAndGet` | Name round-trip |
| `icon_setAndGet` | Icon round-trip |
| `accelerator_setAndGet` | KeyStroke round-trip |
| `menuItem_createReturnsNonNull` | Menu item factory works |
| `button_createReturnsNonNull` | Button factory works |
| `enabled_propagatesToSwing` | Enable state syncs |

### WP2: MenuModels

**~500 lines across 4 files.**

Coverage for menu model variants that were untested.

#### StaticMenuModelTest.java

| Test | Assertion |
|------|-----------|
| `constructor_setsGroup` | Group stored |
| `getMenuItemPrepModels_empty` | No items initially |
| `addMenuItemPrepModel_adds` | Item appears after add |
| `addSeparator_incrementsCount` | Separator counted |
| `localize_calledOnInit` | Localization hook fires |
| `findDefaultLocalizedText_used` | Default text returned |
| `getId_matchesMigrationId` | Identity consistent |
| `hierarchy_extendsAbstractMenuModel` | Correct parent class |

#### PredeterminedMenuModelTest.java

| Test | Assertion |
|------|-----------|
| `constructor_withEmptyItems` | Empty list accepted |
| `constructor_withItems` | Items stored |
| `getItems_returnsUnmodifiable` | Defensive copy |
| `hierarchy_extendsStaticMenuModel` | Correct parent |
| `localize_noop` | No crash on localize |

#### LabelMenuSeparatorModelTest.java

| Test | Assertion |
|------|-----------|
| `constructor_setsLabel` | Label stored |
| `getLabel_returnsExpected` | Round-trip |
| `isSeparator_true` | Always a separator |
| `hierarchy_extendsMenuModel` | Correct parent |

#### PopupPrepModelSwingModelTest.java

| Test | Assertion |
|------|-----------|
| `swingModel_constructable` | Inner class instantiable |
| `popup_setAndGet` | Popup round-trip |
| `preparePopup_firesListeners` | Event dispatch works |
| `hierarchy_nestedInPopupPrepModel` | Correct enclosing class |

### WP3: State Deepening

**~800 lines across 8 files (deepening existing + new Deep files).**

Adds thorough behavioral tests to State subclasses that had thin coverage.

#### BoundedIntegerStateDeepTest.java

| Test | Assertion |
|------|-----------|
| `atomicChange_setMin` | Min via fluent builder |
| `atomicChange_setMax` | Max via fluent builder |
| `atomicChange_setValue` | Value via fluent builder |
| `atomicChange_setStep` | Step via fluent builder |
| `atomicChange_apply` | AtomicChange commits all fields |
| `getValue_clampsToMin` | Value ≥ min enforced |
| `getValue_clampsToMax` | Value ≤ max enforced |
| `setValueTransactionlessly_firesEvent` | Listener called |
| `setRange_updatesMinMax` | Range update |
| `getSwingModel_spinnerModelSynced` | Swing model matches state |
| `scrollStep_default` | Default scroll increment |
| `scrollStep_custom` | Custom scroll increment |

#### BoundedDoubleStateDeepTest.java

Similar to integer variant with double-precision edge cases:
`NaN` handling, `Infinity` boundaries, epsilon comparisons, step-size
fractional values.

#### SingleSelectListStateDeepTest.java

| Test | Assertion |
|------|-----------|
| `setListData_updatesItems` | Data replacement |
| `setSelectedIndex_negativeOne_clearsSelection` | Deselect |
| `setSelectedItem_notInList_ignored` | Invalid selection no-op |
| `getSelectedItem_afterRemove_nulls` | Removal clears |
| `codec_roundTrip_preservesSelection` | Encode/decode selection |
| `scrollModel_syncsWithList` | Scroll pane model sync |

#### StringStateDeepTest.java (deepened)

Additional tests for empty-string edge cases, max-length validation,
whitespace handling, and document listener removal patterns.

#### BooleanStateDeepTest.java

Additional tests for `isSelected()` ↔ `getValue()` consistency, toggle
idempotency, and `SwingModel.getButtonModel()` synchronization.

#### ItemStateDeepTest.java

Additional tests for null-value handling, codec interaction, and
value-changed event dispatch.

#### ColorStateCoverageTest.java (deepened)

Additional reflection assertions for `createComponent()`, `getColorChooser()`,
and the `ColorState.Dialog` inner class.

#### MultipleSelectionListStateDeepTest.java

Additional tests for add/remove selection, select-all, clear-all, and
listener notification patterns.

### WP4: Codecs & Data

**~500 lines across 3 files.**

#### EnumCodecDeepTest.java

| Test | Assertion |
|------|-----------|
| `encode_allValues` | Every enum constant encodes |
| `decode_allValues` | Every enum constant decodes |
| `roundTrip_allValues` | Encode → decode identity |
| `decode_invalidOrdinal_throws` | Error handling |
| `getValueClass_matchesEnum` | Type check |
| `appendRepresentation_usesName` | Human-readable output |

#### ImmutableListDataDeepTest.java (deepened)

Additional tests for index-out-of-bounds, empty-list behavior,
`indexOf` for missing elements, `toArray` consistency, and iterator
exhaustion.

#### MutableListDataDeepTest.java (deepened)

Additional tests for concurrent modification, listener ordering,
add-at-index boundary conditions, remove-last-element, and
`setAllItems` with empty list.

### WP5: Miscellaneous New Classes

**~700 lines across 9 files.**

#### EmptyConditionTextTest.java

| Test | Assertion |
|------|-----------|
| `getText_returnsEmpty` | Empty string for no condition |
| `hierarchy_implementsConditionText` | Correct interface |

#### HtmlStringValueTest.java

| Test | Assertion |
|------|-----------|
| `constructor_storesHtml` | Value stored |
| `getHtml_returnsValue` | Getter works |
| `toString_containsHtml` | Debug output useful |

#### UnsupportedGenerationExceptionTest.java

| Test | Assertion |
|------|-----------|
| `constructor_noArg` | Default ctor |
| `constructor_message` | Message ctor |
| `constructor_messageCause` | Message + cause ctor |
| `constructor_cause` | Cause-only ctor |
| `hierarchy_extendsRuntimeException` | Correct parent |
| `getMessage_returnsProvided` | Message preserved |
| `getCause_returnsProvided` | Cause preserved |

#### EmptyIconFactoryTest.java

| Test | Assertion |
|------|-----------|
| `getInstance_returnsSingleton` | Same instance |
| `createIcon_returnsNonNull` | Icon created |
| `createIcon_hasZeroDimensions` | Empty icon is 0×0 |
| `getIconSize_zero` | Consistent dimensions |

#### ImageIconFactoryDeepTest.java

| Test | Assertion |
|------|-----------|
| `constructor_fromResource` | Resource-based ctor |
| `constructor_fromImage` | Image-based ctor |
| `getIconWidth_matchesImage` | Width correct |
| `getIconHeight_matchesImage` | Height correct |
| `trim_reducesSize` | Trimming works |
| `scale_changesSize` | Scaling works |

#### ActivityNodeTest.java

| Test | Assertion |
|------|-----------|
| `addListener_registers` | Listener stored |
| `removeListener_unregisters` | Listener removed |
| `fireChanged_notifiesAll` | All listeners called |
| `fireChanged_noListeners_noCrash` | Empty case safe |
| `getChildren_emptyInitially` | No children at start |

#### CascadeLabelSeparatorTest.java

| Test | Assertion |
|------|-----------|
| `getText_returnsLabel` | Label accessible |
| `isValid_returnsTrue` | Always valid |
| `localize_doesNotThrow` | Safe localization |

#### BoundedNumberStateAtomicChangeTest.java

| Test | Assertion |
|------|-----------|
| `builder_setMin` | Fluent min |
| `builder_setMax` | Fluent max |
| `builder_setValue` | Fluent value |
| `builder_setStep` | Fluent step |
| `builder_chain` | All methods chainable |
| `apply_commitsAllValues` | Atomic commit |
| `apply_partialUpdate_onlyChangesSet` | Partial update |
| `toString_includesAllFields` | Debug output |

#### ListSelectionListenerAdapterTest.java

| Test | Assertion |
|------|-----------|
| `valueChanged_nonAdjusting_fires` | Event dispatch |
| `valueChanged_adjusting_skipped` | Filtering works |

### WP6: Thin Test Deepening

**~500 lines across 9 existing files.**

Files with <80 lines or <8 tests receive 8–20 additional test methods.

| File | Tests Added | Focus |
|------|-------------|-------|
| `AbstractElementTest.java` | 10 | getId, getMigrationId, localization |
| `DataIndexPairTest.java` | 8 | Boundary indices, equals/hashCode |
| `AbstractCompletionModelTest.java` | 10 | Hierarchy, completion protocol |
| `ValueEventTest.java` | 8 | Source, oldValue, newValue, toString |
| `EditInterfaceTest.java` | 8 | Hierarchy, method signatures |
| `InitializerTest.java` | 10 | Lifecycle callbacks, ordering |
| `ItemCodecArraysTest.java` | 8 | Empty arrays, null elements |
| `CascadeItemTest.java` | 8 | Hierarchy, getBlankChildren |
| `GroupTest.java` | 8 | getInstance caching, equals, UUID |

---

## New Test File Index

All new test files created by this coverage push:

```
core/croquet/src/test/java/org/lgna/croquet/
├── ValueHolderTest.java                                (~100 lines)
├── EditOperationTest.java                              (~120 lines)
├── OwnedByCompositeOperationCoverageTest.java          (~120 lines)
├── AbstractOwnedByCompositeOperationCoverageTest.java  (~100 lines)
├── OwnedByCompositeOperationSubKeyTest.java            (~140 lines)
├── OperationImpDeepTest.java                           (~120 lines)
├── StaticMenuModelTest.java                            (~140 lines)
├── PredeterminedMenuModelTest.java                     (~120 lines)
├── LabelMenuSeparatorModelTest.java                    (~100 lines)
├── PopupPrepModelSwingModelTest.java                   (~100 lines)
├── BoundedIntegerStateDeepTest.java                    (~140 lines)
├── BoundedDoubleStateDeepTest.java                     (~140 lines)
├── SingleSelectListStateDeepTest.java                  (~120 lines)
├── BooleanStateDeepTest.java                           (~100 lines)
├── ItemStateDeepTest.java                              (~100 lines)
├── MultipleSelectionListStateDeepTest.java             (~100 lines)
├── EmptyConditionTextTest.java                         (~80 lines)
├── EmptyIconFactoryTest.java                           (~60 lines)
├── HtmlStringValueTest.java                            (~80 lines)
├── UnsupportedGenerationExceptionTest.java             (~60 lines)
├── ActivityNodeTest.java                               (~80 lines)
├── ListSelectionListenerAdapterTest.java               (~60 lines)
├── CascadeLabelSeparatorTest.java                      (~60 lines)
├── BoundedNumberStateAtomicChangeTest.java             (~80 lines)
├── codecs/
│   └── EnumCodecDeepTest.java                          (~120 lines)
└── icon/
    └── ImageIconFactoryDeepTest.java                   (~80 lines)
```

---

## Deepened Existing Files

| File | Lines Before | Lines After | Tests Added |
|------|-------------|-------------|-------------|
| `AbstractElementTest.java` | ~60 | ~160 | 10 |
| `DataIndexPairTest.java` | ~50 | ~130 | 8 |
| `AbstractCompletionModelTest.java` | ~70 | ~170 | 10 |
| `ValueEventTest.java` | ~55 | ~135 | 8 |
| `EditInterfaceTest.java` | ~50 | ~130 | 8 |
| `InitializerTest.java` | ~45 | ~135 | 10 |
| `ItemCodecArraysTest.java` | ~55 | ~135 | 8 |
| `CascadeItemTest.java` | ~50 | ~130 | 8 |
| `GroupTest.java` | ~60 | ~140 | 8 |
| `StringStateDeepTest.java` | ~137 | ~220 | 8 |
| `ColorStateCoverageTest.java` | ~80 | ~150 | 8 |
| `ImmutableListDataExtendedTest.java` | ~120 | ~220 | 10 |
| `MutableListDataDeepTest.java` | ~150 | ~250 | 10 |

---

## Configuration

### Maven

Tests are run via the standard Maven Surefire plugin configuration in
`core/croquet/pom.xml`:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-surefire-plugin</artifactId>
  <configuration>
    <argLine>-Djava.awt.headless=true</argLine>
  </configuration>
</plugin>
```

### Prerequisites

1. **Tweedle grammar submodule** must be initialized:
   ```bash
   git submodule update --init tweedle-lang
   ```

2. **JDK 17+** (same as project baseline).

3. **No display required** — all tests run headlessly.

### Environment Variables

None required. All test isolation is handled via `CroquetTestUtils` listener
removal and `Group.getInstance()` with deterministic UUIDs.

---

## Running Tests

### All core/croquet tests

```bash
cd /path/to/alice
mvn test -pl core/croquet -Djava.awt.headless=true
```

### Single test class

```bash
mvn test -pl core/croquet -Dtest=EditOperationTest
```

### Single test method

```bash
mvn test -pl core/croquet -Dtest=EditOperationTest#constructor_setsGroup
```

### With coverage report (JaCoCo)

```bash
mvn test jacoco:report -pl core/croquet
# Report at core/croquet/target/site/jacoco/index.html
```

### Verify test count

```bash
mvn test -pl core/croquet 2>&1 | grep "Tests run:"
# Expected: Tests run: 2250+, Failures: 0, Errors: 0, Skipped: 0
```

### Count test lines

```bash
find core/croquet/src/test/java -name "*.java" -exec wc -l {} + | tail -1
# Expected: 27000+
```

---

## Test Patterns Reference

### Pattern: Deterministic Group

Every test that needs a `Group` uses a fixed UUID to avoid collisions:

```java
private static final Group TEST_GROUP =
    Group.getInstance(
        UUID.fromString("00000000-0000-0000-0099-000000000001"),
        "editOpTest");
```

The UUID scheme uses `0099-00000000NNNN` where NNNN is unique per test class.

### Pattern: Listener Removal in @Before

State subclass tests always remove Swing listeners before exercising behavior:

```java
@Before
public void setUp() {
    state = new TestBooleanState(TEST_GROUP, false);
    CroquetTestUtils.removeItemListeners(state);
}
```

This prevents `NullPointerException` from `Application.getActiveInstance()`
which is `null` in headless test environments.

### Pattern: Reflection for Untestable Classes

Classes requiring `OperationOwningComposite` or `Application` at construction
are tested structurally:

```java
@Test
public void class_isPublicAndNotFinal() {
    int mods = OwnedByCompositeOperation.class.getModifiers();
    assertTrue(Modifier.isPublic(mods));
    assertFalse(Modifier.isFinal(mods));
}

@Test
public void method_fireExists_withCorrectSignature() throws Exception {
    Method m = OwnedByCompositeOperation.class.getDeclaredMethod(
        "fire", Trigger.class);
    assertEquals(void.class, m.getReturnType());
}
```

### Pattern: Concrete Stub for Abstract Class

Abstract menu models and condition texts use minimal concrete stubs:

```java
private static class TestStaticMenuModel extends StaticMenuModel {
    TestStaticMenuModel(Group group) {
        super(group, CroquetTestUtils.nextTestUUID());
    }

    @Override
    protected void localize() { /* no-op for test */ }

    @Override
    protected String findDefaultLocalizedText() {
        return "Test";
    }
}
```

### Pattern: Value Class Testing

For value classes like `OwnedByCompositeOperationSubKey`, test the full
equals/hashCode contract:

```java
@Test
public void equals_reflexive() {
    assertEquals(key, key);
}

@Test
public void equals_symmetric() {
    OwnedByCompositeOperationSubKey key2 = /* same values */;
    assertEquals(key, key2);
    assertEquals(key2, key);
}

@Test
public void hashCode_consistent() {
    OwnedByCompositeOperationSubKey key2 = /* same values */;
    assertEquals(key.hashCode(), key2.hashCode());
}
```

### Pattern: AtomicChange Fluent Builder

`BoundedNumberState.AtomicChange` uses a fluent builder that is tested
by verifying each setter returns `this` and that `apply()` commits atomically:

```java
@Test
public void atomicChange_fluentChain() {
    AtomicChange change = state.createAtomicChange()
        .setMin(0).setMax(100).setValue(50).setStep(5);
    assertSame(change, change.setMin(0)); // fluent returns this
}

@Test
public void atomicChange_apply_commitsAll() {
    state.createAtomicChange()
        .setMin(10).setMax(90).setValue(50).setStep(2)
        .apply();
    assertEquals(10, state.getMinimum().intValue());
    assertEquals(90, state.getMaximum().intValue());
    assertEquals(50, state.getValue().intValue());
}
```

---

## Troubleshooting

### Tests fail with `NullPointerException` at `Application.getActiveInstance()`

**Cause:** A Swing listener fired during test setup before listener removal.

**Fix:** Ensure `CroquetTestUtils.remove*Listeners(state)` is called
immediately after state construction in `@Before`, before any value
manipulation.

### `HeadlessException` during test run

**Cause:** Test is running without `-Djava.awt.headless=true`.

**Fix:** Pass the system property via Maven:
```bash
mvn test -pl core/croquet -Djava.awt.headless=true
```

Or add to the Surefire configuration in `pom.xml`.

### `NoSuchMethodException` in reflection tests

**Cause:** A refactoring renamed or changed the method signature of the
class under test.

**Fix:** Update the reflection test to match the new method name/signature.
Reflection tests are intentionally brittle — they serve as change detectors
for public API surface.

### Test UUID collision

**Cause:** Two test classes using the same UUID for `Group.getInstance()`.

**Fix:** Each test class must use a unique UUID. Follow the convention:
`00000000-0000-0000-00NN-000000000001` where `NN` is unique per class.

### `ClassCastException` in listener removal

**Cause:** The Swing model type changed (e.g., `DefaultButtonModel` replaced
with a custom implementation).

**Fix:** Update `CroquetTestUtils` to handle the new model type, or use
`instanceof` guards before casting.
