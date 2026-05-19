# core/croquet state management deep dive — coverage reference

> **Issue:** [#778](https://github.com/rysweet/RabbitHole/issues/778)
> **Scope:** 25 new JUnit 4 test files covering state classes, codecs, edits, triggers, meta-state, and preferences in `core/croquet`
> **Line count:** 5,006 lines of test code
> **Tests:** 530 tests, all passing
> **Build verification:** `mvn test -pl core/croquet -Dtest="*CoverageTest" -DfailIfNoTests=false -q`

---

## Overview

This coverage sprint provides deep characterization tests for the `core/croquet`
state management framework — the reactive data layer underlying all Alice IDE
UI components. The tests cover six functional areas across six packages:

| Area | Package(s) | Test Files | Tests | Lines |
|---|---|---|---:|---:|
| State classes | `o.l.croquet` | 12 | 224 | 2,333 |
| Codecs | `o.l.croquet.codecs` | 6 | 119 | 1,027 |
| Edits | `o.l.croquet.edits` | 2 | 59 | 500 |
| Triggers | `o.l.croquet.triggers` | 2 | 87 | 709 |
| Meta-state | `o.l.croquet.meta` | 1 | 12 | 138 |
| Preferences | `o.l.croquet.preferences` | 2 | 29 | 299 |
| **Total** | | **25** | **530** | **5,006** |

All tests are JUnit 4 (`@Test`, `@Before`, `static org.junit.Assert.*`) and run
headlessly — no display, no `Application` boot, no AWT event dispatch thread.

---

## Architecture of the test suite

### Headless testing strategy

The croquet framework is tightly coupled to Swing and the croquet `Application`
singleton. The coverage tests bypass these dependencies using three techniques:

1. **`CroquetTestUtils` listener removal** — After constructing a state object,
   the setup method calls `CroquetTestUtils.removeItemListeners()`,
   `removeSpinnerChangeListeners()`, `removeDocumentListeners()`, or
   `removeListSelectionListeners()` to strip Swing listeners that would
   call `Application.getActiveInstance()` on state change.

2. **Concrete test subclasses** — Abstract state classes (`BooleanState`,
   `BoundedIntegerState`, `StringState`, etc.) are instantiated via minimal
   concrete inner classes that implement only the required abstract methods
   (typically `getValueClass()` and presentation stubs).

3. **Reflection-based structure tests** — Classes that cannot be instantiated
   headlessly (`ColorState`, `TabState`, `CustomItemState`, all
   `Application`-dependent trigger classes) are tested via `Class.forName()`
   and reflection assertions on hierarchy, method signatures, and modifiers.

### Test naming convention

All new files follow the `*CoverageTest.java` naming pattern to avoid collision
with existing `*Test.java` and `*ExtendedTest.java` files. The Maven Surefire
pattern `*CoverageTest` selects exactly this suite.

### Shared test infrastructure

| Utility | Location | Purpose |
|---|---|---|
| `CroquetTestUtils` | `o.l.croquet` | Listener removal, deterministic UUID generation, `StringCodec` for test use |
| `TestBooleanState` | Inner class in `BooleanStateCoverageTest` | Minimal concrete `BooleanState` subclass |
| `TestBoundedIntegerState` | Inner class in `BoundedIntegerStateCoverageTest` | Minimal concrete `BoundedIntegerState` subclass |
| `TestBoundedDoubleState` | Inner class in `BoundedDoubleStateCoverageTest` | Minimal concrete `BoundedDoubleState` subclass |
| `TestStringState` | Inner class in `StringStateCoverageTest` | Minimal concrete `StringState` subclass |
| `TestDirection` enum | Inner enum in `EnumConstantStateCoverageTest` | Three-value enum for state tests |

---

## Test file reference

### State coverage tests

#### 1. `BooleanStateCoverageTest` (288 lines, 20 tests)

**Location:** `core/croquet/src/test/java/org/lgna/croquet/BooleanStateCoverageTest.java`

Covers `BooleanState` edge cases, binary encode/decode round-trip, toggle
sequences, listener interaction, and `ButtonModel` synchronization.

| Method | What It Tests |
|---|---|
| `encodeAndDecode_false_roundTrips` | Binary encode `false`, decode via `ByteArrayBinaryEncoder`, assert equality |
| `encodeAndDecode_true_roundTrips` | Binary encode `true`, decode, assert equality |
| `encodeAndDecode_multipleValues_sequential` | Encode several values sequentially, decode in order |
| `rapidToggle_tenTimes_endsCorrectly` | Toggle 10 times, verify final value matches expected parity |
| `setValueTransactionlessly_sameValue_doesNotFireListener` | Setting same value suppresses listener callback |
| `setValueTransactionlessly_differentValue_firesListenerOnce` | Distinct value change fires exactly one callback |
| `buttonModel_initiallyNotSelected` | `ButtonModel.isSelected()` matches initial `false` |
| `buttonModel_selectedAfterSetTrue` | `ButtonModel.isSelected()` updates after `setValue(true)` |
| `buttonModel_deselectedAfterToggleBack` | Toggle true → false syncs back to button model |
| `changeValueFromEdit_multipleSequential` | Multiple `changeValueFromEdit` calls apply sequentially |
| `multipleListeners_allFire` | Two old-school listeners both receive callbacks |
| `multipleNewSchoolListeners_allFire` | Two new-school listeners both receive callbacks |
| `textForTrue_afterValueChange_staysConsistent` | `getTextFor(true)` is stable across state changes |
| `getTextFor_togglesWithBoolArg` | `getTextFor(true)` ≠ `getTextFor(false)` |
| `setIconForBothTrueAndFalse_setsSameIcon` | Icon setter works for both values |
| `appendRepresentation_toNonEmptyStringBuilder` | Appends to pre-existing StringBuilder content |
| `setValueWhileDisabled_stillUpdatesValue` | Disabled state does not block programmatic value change |
| `changingCallback_receivesCorrectPrevAndNext` | `changing()` callback receives correct previous/next values |
| `initialTrue_encodeDecode_roundTrips` | State initialized to `true` round-trips correctly |

#### 2. `BoundedIntegerStateCoverageTest` (244 lines, 22 tests)

**Location:** `core/croquet/src/test/java/org/lgna/croquet/BoundedIntegerStateCoverageTest.java`

Covers boundary values, clamping, atomic change, spinner model sync, and
encode/decode round-trip for `BoundedIntegerState`.

| Method | What It Tests |
|---|---|
| `encodeAndDecode_midRange_roundTrips` | Mid-range integer round-trips through binary encoding |
| `encodeAndDecode_zero_roundTrips` | Zero round-trips correctly |
| `encodeAndDecode_negative_roundTrips` | Negative integer round-trips |
| `encodeAndDecode_maxInt_roundTrips` | `Integer.MAX_VALUE` round-trips |
| `encodeAndDecode_minInt_roundTrips` | `Integer.MIN_VALUE` round-trips |
| `setValue_atMinimum` | Value at minimum boundary is accepted |
| `setValue_atMaximum` | Value at maximum boundary is accepted |
| `setValue_oneAboveMinimum` | `min + 1` is accepted |
| `setValue_oneBelowMaximum` | `max - 1` is accepted |
| `atomicChange_valueOnly` | Atomic change of value alone |
| `atomicChange_minimumOnly` | Atomic change of minimum alone |
| `atomicChange_maximumOnly` | Atomic change of maximum alone |
| `atomicChange_extentSetting` | Atomic change includes extent |
| `atomicChange_isAdjusting` | Atomic change with adjusting flag |
| `spinnerModel_minimumMatchesAfterSetMinimum` | Spinner model reflects minimum change |
| `spinnerModel_maximumMatchesAfterSetMaximum` | Spinner model reflects maximum change |
| `noListenerFire_whenValueUnchanged` | Same-value set suppresses listener |
| `listenerFires_forEachDistinctChange` | Each distinct value fires listener once |
| `details_negativeMinimum` | Negative minimum in details string |
| `appendRepresentation_negative` | Negative value in representation |
| `appendRepresentation_zero` | Zero in representation |
| `multipleSetAll_lastWins` | Multiple `setAll()` calls — last value wins |

#### 3. `BoundedDoubleStateCoverageTest` (244 lines, 22 tests)

**Location:** `core/croquet/src/test/java/org/lgna/croquet/BoundedDoubleStateCoverageTest.java`

Covers boundary values, very small/very large doubles, atomic change, spinner
model sync, and encode/decode for `BoundedDoubleState`.

| Method | What It Tests |
|---|---|
| `encodeAndDecode_normal_roundTrips` | Normal double round-trips |
| `encodeAndDecode_zero_roundTrips` | Zero round-trips |
| `encodeAndDecode_negative_roundTrips` | Negative double round-trips |
| `encodeAndDecode_verySmall_roundTrips` | Very small (near-epsilon) value round-trips |
| `encodeAndDecode_veryLarge_roundTrips` | Very large value round-trips |
| `setValue_atMinimum` | Minimum boundary accepted |
| `setValue_atMaximum` | Maximum boundary accepted |
| `setValue_veryCloseToMinimum` | Near-minimum boundary accepted |
| `setValue_veryCloseToMaximum` | Near-maximum boundary accepted |
| `setMinimum_negative` | Negative minimum set correctly |
| `setMaximum_veryLarge` | Very large maximum set correctly |
| `atomicChange_allFields` | Atomic change of all fields simultaneously |
| `atomicChange_valueOnly` | Atomic change of value alone |
| `atomicChange_isAdjusting` | Atomic change with adjusting flag |
| `noListenerFire_whenValueUnchanged` | Same-value suppresses listener |
| `listenerFires_forEachDistinctChange` | Distinct values fire listener |
| `details_negativeRange` | Negative range in details string |
| `appendRepresentation_negative` | Negative value representation |
| `appendRepresentation_zero` | Zero representation |
| `spinnerModel_valueAfterSetValue` | Spinner model reflects value |
| `spinnerModel_stepSize` | Step size configurable on spinner model |
| `multipleSetAll_lastWins` | Multiple `setAll()` — last value wins |

#### 4. `StringStateCoverageTest` (218 lines, 20 tests)

**Location:** `core/croquet/src/test/java/org/lgna/croquet/StringStateCoverageTest.java`

Covers empty/null/unicode strings, Swing Document sync, encode/decode
round-trip, and listener behavior for `StringState`.

| Method | What It Tests |
|---|---|
| `encodeAndDecode_normalString_roundTrips` | Normal string round-trips |
| `encodeAndDecode_emptyString_roundTrips` | Empty string round-trips |
| `encodeAndDecode_unicode_roundTrips` | Unicode characters round-trip |
| `encodeAndDecode_multipleStrings` | Sequential encode/decode |
| `setValue_unicode_updatesValue` | Unicode value set correctly |
| `setValue_unicodeEmoji_syncsDocument` | Emoji text syncs to Swing Document |
| `setValue_whitespaceOnly` | Whitespace-only strings accepted |
| `setValue_newlines` | Newlines in value |
| `setValue_tabs` | Tab characters in value |
| `setValue_longString_1000chars` | 1000-character string handled |
| `appendRepresentation_emptyString` | Empty string representation |
| `appendRepresentation_nullValue` | Null value representation |
| `multipleDistinctValues_eachFiresListener` | Each distinct change fires listener |
| `sameValue_doesNotFireListener` | Same-value suppresses listener |
| `documentSync_afterMultipleChanges` | Document stays in sync across multiple changes |
| `changeValueFromEdit_specialChars` | Special characters via edit path |
| `textForBlankCondition_setAndReset` | Blank-condition text set and reset |
| `textForBlankCondition_setToNull` | Null blank-condition text |
| `setValueWhileDisabled_stillWorks` | Disabled does not block programmatic set |
| `enabledToggle_doesNotAffectValue` | Enable/disable does not change value |

#### 5. `EnumConstantStateCoverageTest` (237 lines, 22 tests)

**Location:** `core/croquet/src/test/java/org/lgna/croquet/EnumConstantStateCoverageTest.java`

Covers constructor variants, item enumeration, listener behavior, codec
delegation, and Swing model sync for `EnumConstantState<E>`.

| Method | What It Tests |
|---|---|
| `constructor_setsInitialValueByIndex` | First enum constant selected by default |
| `constructor_index1_selectsSecondEnum` | Index 1 selects second enum |
| `constructor_lastIndex` | Last index selects last enum |
| `getItemCount_matchesEnumConstants` | Item count matches enum constant count |
| `getItemCount_singleValueEnum` | Single-value enum has count 1 |
| `getItemAt_returnsCorrectEnums` | `getItemAt(i)` returns correct enum constant |
| `indexOf_existingEnum` | `indexOf()` finds existing enum |
| `indexOf_firstEnum` | `indexOf()` returns 0 for first constant |
| `containsItem_allEnumsPresent` | All enum constants are contained |
| `setValueTransactionlessly_updatesSelection` | `setValue` updates selection |
| `setValueTransactionlessly_updatesIndex` | `setValue` updates selected index |
| `clearSelection_setsNull` | Clear selection sets value to null |
| `oldSchoolListener_firesOnChange` | Old-school listener receives callbacks |
| `newSchoolListener_firesOnChange` | New-school listener receives callbacks |
| `listener_doesNotFire_whenSameValue` | Same-value suppresses listener |
| `appendRepresentation_delegatesToCodec` | Representation delegates to codec |
| `appendRepresentation_null` | Null value representation |
| `iterator_coversAllEnums` | Iterator covers all enum constants |
| `toArray_matchesEnumValues` | `toArray()` matches `values()` |
| `getData_returnsImmutableListData` | Data is immutable |
| `getSwingModel_nonNull` | Swing model is non-null |
| `comboBoxModel_sizeMatchesEnumCount` | ComboBox model size matches enum count |

#### 6. `ImmutableDataSingleSelectListStateCoverageTest` (191 lines, 22 tests)

**Location:** `core/croquet/src/test/java/org/lgna/croquet/ImmutableDataSingleSelectListStateCoverageTest.java`

Covers the immutability contract, item access, selection operations, and
listener behavior for `ImmutableDataSingleSelectListState`.

| Method | What It Tests |
|---|---|
| `constructor_setsInitialValue` | Initial value set by constructor index |
| `constructor_middleIndex` | Middle index selects correct item |
| `constructor_lastIndex` | Last index selects last item |
| `constructor_noSelection` | Negative index means no initial selection |
| `getData_returnsImmutableListData` | Data is immutable list data |
| `getData_itemCountMatches` | Data item count matches constructor items |
| `getItemAt_allPositions` | All positions return correct items |
| `getItemCount` | Item count correct |
| `indexOf` | `indexOf()` finds items |
| `indexOf_missing` | `indexOf()` returns -1 for missing items |
| `containsItem_present` | Present items found |
| `containsItem_absent` | Absent items not found |
| `setSelectedIndex_updatesValue` | Selected index updates value |
| `setValueTransactionlessly_updatesIndex` | Value change updates index |
| `clearSelection` | Clear selection nulls value |
| `listener_firesOnSelectionChange` | Listener fires on selection change |
| `iterator_coversAllItems` | Iterator covers all items |
| `toArray` | `toArray()` matches items |
| `appendRepresentation_delegatesToCodec` | Representation delegates to codec |
| `singleElement_getValue` | Single-element list works |
| `getSwingModel_nonNull` | Swing model non-null |
| `comboBoxModel_sizeMatches` | ComboBox model size correct |

#### 7. `MutableDataSingleSelectListStateCoverageTest` (222 lines, 21 tests)

**Location:** `core/croquet/src/test/java/org/lgna/croquet/MutableDataSingleSelectListStateCoverageTest.java`

Covers add/remove items, selection tracking, listener behavior, and data
replacement for `MutableDataSingleSelectListState`.

| Method | What It Tests |
|---|---|
| `constructor_withItems_setsInitialValue` | Initial value from constructor items |
| `constructor_emptyCodecOnly` | Empty construction with codec only |
| `addItem_appends` | `addItem()` appends to end |
| `addItem_atIndex` | `addItem(index)` inserts at position |
| `removeItem_decreasesCount` | Removal decreases count |
| `removeItem_preservesSelection` | Removal of non-selected item preserves selection |
| `setItems_preservesSelectionWhenPresent` | `setItems()` preserves selection when item still present |
| `setItems_clearsSelectionWhenAbsent` | `setItems()` clears selection when item absent |
| `clear_removesAll` | `clear()` removes all items |
| `removeItemAndSelectAppropriateReplacement_selectedItem` | Removing selected item picks replacement |
| `listener_firesOnValueChange` | Listener fires on value change |
| `listener_firesAfterAddAndSelect` | Listener fires when added item selected |
| `listener_sameValue_doesNotFire` | Same-value suppresses listener |
| `appendRepresentation_delegatesToCodec` | Representation delegates to codec |
| `iterator_afterMutations` | Iterator reflects mutations |
| `setListData_replacesAndSetsIndex` | `setListData()` replaces and sets index |
| `setRandomSelectedValue_selectsValid` | Random selection selects valid item |
| `setRandomSelectedValue_emptyList` | Random selection on empty list is safe |
| `getData_returnsMutableListData` | Data is mutable |
| `getSwingModel_nonNull` | Swing model non-null |
| `comboBoxModel_sizeMatchesAfterAdd` | ComboBox model size matches after add |

#### 8. `RefreshableDataSingleSelectListStateCoverageTest` (181 lines, 17 tests)

**Location:** `core/croquet/src/test/java/org/lgna/croquet/RefreshableDataSingleSelectListStateCoverageTest.java`

Covers data refresh listener forwarding and basic list operations for
`RefreshableDataSingleSelectListState`.

| Method | What It Tests |
|---|---|
| `constructor_setsInitialValue` | Initial value set correctly |
| `constructor_itemCountMatches` | Item count from refreshable data |
| `getData_returnsRefreshableListData` | Data type is refreshable |
| `getData_isSameInstance` | Same data instance returned |
| `getItemAt_returnsCorrectItems` | All positions correct |
| `indexOf_findsItems` | `indexOf()` finds items |
| `containsItem_present` | Present items found |
| `containsItem_absent` | Absent items not found |
| `setSelectedIndex_updatesValue` | Selection by index works |
| `setValueTransactionlessly_updatesIndex` | Value change updates index |
| `clearSelection_clearsValue` | Clear selection nulls value |
| `listener_firesOnSelectionChange` | Listener fires correctly |
| `iterator_coversAllItems` | Iterator complete |
| `toArray_matchesData` | `toArray()` matches data |
| `appendRepresentation_delegatesToCodec` | Representation delegates |
| `getSwingModel_nonNull` | Swing model non-null |
| `comboBoxModel_sizeMatches` | ComboBox model size correct |

#### 9. `ItemStateCoverageTest` (224 lines, 19 tests)

**Location:** `core/croquet/src/test/java/org/lgna/croquet/ItemStateCoverageTest.java`

Covers codec delegation, getValue/setValue, listener interaction, and
encode/decode round-trip for `ItemState<T>` via a concrete `SimpleItemState`
subclass.

| Method | What It Tests |
|---|---|
| `constructor_setsInitialValue` | Initial value set |
| `constructor_nullInitialValue` | Null initial value accepted |
| `getItemCodec_returnsNonNull` | Codec non-null |
| `getItemCodec_valueClass` | Codec value class correct |
| `decodeValue_delegatesToCodec` | Decode delegates to codec |
| `encodeValue_delegatesToCodec` | Encode delegates to codec |
| `appendRepresentation_delegatesToCodec` | Representation delegates |
| `appendRepresentation_nullValue` | Null value representation |
| `appendUserRepr_appendsCurrentValue` | User representation uses current value |
| `appendUserRepr_afterValueChange` | User representation reflects changes |
| `setValueTransactionlessly_updatesValue` | Value update works |
| `setValueTransactionlessly_toNull` | Null value accepted |
| `changeValueFromEdit_updatesValue` | Edit path updates value |
| `valueListener_firesOnChange` | Old-school listener fires |
| `newSchoolListener_firesOnChange` | New-school listener fires |
| `addAndInvokeValueListener_firesImmediately` | Immediate invocation on add |
| `addAndInvokeNewSchoolListener_firesImmediately` | Immediate invocation for new-school |
| `encodeDecodeRoundTrip_multipleValues` | Multiple values round-trip |
| `getMigrationId_returnsNonNull` | Migration ID non-null |

#### 10. `ColorStateCoverageTest` (103 lines, 11 tests)

**Location:** `core/croquet/src/test/java/org/lgna/croquet/ColorStateCoverageTest.java`

**Reflection-only.** `ColorState` cannot be instantiated headlessly because its
constructor creates `ColorChooserDialogCoreComposite` which accesses
`Application.INHERIT_GROUP`.

| Method | What It Tests |
|---|---|
| `colorState_extendsItemState` | Class hierarchy assertion |
| `colorState_extendsState` | Transitive hierarchy assertion |
| `colorState_extendsAbstractCompletionModel` | Transitive hierarchy assertion |
| `colorState_isAbstract` | Abstract modifier check |
| `colorState_isPublic` | Public modifier check |
| `colorState_hasGetValue` | Method presence via reflection |
| `colorState_hasSetValueTransactionlessly` | Method presence |
| `colorState_hasDecodeValue` | Method presence |
| `colorState_hasEncodeValue` | Method presence (may be inherited) |
| `colorState_hasAppendRepresentation` | Method presence |
| `colorState_isInColorPackage` | Package assertion |

#### 11. `TabStateCoverageTest` (103 lines, 15 tests)

**Location:** `core/croquet/src/test/java/org/lgna/croquet/TabStateCoverageTest.java`

**Reflection-only.** `TabState` requires `TabComposite<?>` items that cannot be
constructed headlessly.

| Method | What It Tests |
|---|---|
| `tabState_extendsSingleSelectListState` | Direct parent assertion |
| `tabState_extendsItemState` | Transitive hierarchy assertion |
| `tabState_extendsState` | Transitive hierarchy assertion |
| `tabState_isAbstract` | Abstract modifier check |
| `tabState_isPublic` | Public modifier check |
| `tabState_hasGetValue` | Method presence |
| `tabState_hasSetValueTransactionlessly` | Method presence |
| `tabState_hasGetItemCount` | Method presence |
| `tabState_hasGetItemAt` | Method presence |
| `tabState_hasGetSelectedIndex` | Method presence |
| `tabState_hasSetSelectedIndex` | Method presence |
| `tabState_hasConstructor` | Constructor presence |
| `tabState_isInCroquetPackage` | Package assertion |
| `simpleTabState_extendsTabState` | Subclass hierarchy |
| `simpleTabState_isAbstract` | Subclass modifier |

#### 12. `CustomItemStateCoverageTest` (98 lines, 13 tests)

**Location:** `core/croquet/src/test/java/org/lgna/croquet/CustomItemStateCoverageTest.java`

**Reflection-only.** `CustomItemState` requires cascade infrastructure.

| Method | What It Tests |
|---|---|
| `customItemState_extendsItemState` | Direct parent |
| `customItemState_extendsState` | Transitive hierarchy |
| `customItemState_extendsAbstractCompletionModel` | Transitive hierarchy |
| `customItemState_isAbstract` | Abstract modifier |
| `customItemState_isPublic` | Public modifier |
| `defaultCustomItemState_extendsCustomItemState` | Subclass hierarchy |
| `defaultCustomItemState_isAbstract` | Subclass modifier |
| `customItemStateWithInternalBlank_extendsCustomItemState` | InternalBlank variant |
| `customItemState_hasConstructor` | Constructor presence |
| `customItemState_constructorIsProtected` | Constructor visibility |
| `customItemState_hasGetValue` | Method presence |
| `customItemState_hasGetItemCodec` | Method presence |
| `customItemState_isInCroquetPackage` | Package assertion |

---

### Codec coverage tests

#### 13. `EnumCodecCoverageTest` (197 lines, 22 tests)

**Location:** `core/croquet/src/test/java/org/lgna/croquet/codecs/EnumCodecCoverageTest.java`

Covers singleton cache, binary encode/decode round-trip across multiple enum
types, representation, and class structure for `EnumCodec<E>`.

| Method | What It Tests |
|---|---|
| `getInstance_sameClass_returnsSameInstance` | Singleton cache identity |
| `getInstance_differentClass_returnsDifferentInstance` | Different enums get different codecs |
| `getInstance_emptyEnum` | Empty enum handled |
| `getInstance_singleValueEnum` | Single-value enum handled |
| `createInstance_returnsNewInstance` | Non-cached instance creation |
| `createInstance_withCustomizer_storesIt` | Customizer stored on instance |
| `createInstance_nullCustomizer` | Null customizer accepted |
| `getValueClass_returnsCorrectClass` | Value class matches enum class |
| `roundTrip_allColorValues` | All values of a test enum round-trip |
| `roundTrip_allSizeValues` | All values of second test enum round-trip |
| `roundTrip_singleValue` | Single-value enum round-trip |
| `appendRepresentation_nonNull_appendsName` | Non-null appends enum name |
| `appendRepresentation_green` | Specific enum constant representation |
| `appendRepresentation_null_appendsNull` | Null value representation |
| `appendRepresentation_cachedAcrossCalls` | Representation stable |
| `toString_containsClassName` | toString includes class name |
| `toString_containsEnumName` | toString includes enum name |
| `toString_differentEnums` | toString differs per enum type |
| `enumCodec_implementsItemCodec` | Implements `ItemCodec` |
| `enumCodec_isPublic` | Public modifier |
| `enumCodec_hasPrivateConstructor` | Private constructor for singleton pattern |
| `localizationCustomizerInterface_exists` | Nested interface exists |

#### 14. `AbstractItemCodecCoverageTest` (157 lines, 13 tests)

**Location:** `core/croquet/src/test/java/org/lgna/croquet/codecs/AbstractItemCodecCoverageTest.java`

Covers `getValueClass()`, `appendRepresentation()`, encode/decode round-trip,
and class structure via concrete test subclasses of `AbstractItemCodec<T>`.

| Method | What It Tests |
|---|---|
| `getValueClass_returnsConstructorArg` | Value class for String codec |
| `getValueClass_integerCodec` | Value class for Integer codec |
| `appendRepresentation_defaultUsesToString` | Default representation uses `toString()` |
| `appendRepresentation_integer` | Integer representation |
| `appendRepresentation_null` | Null value representation |
| `appendRepresentation_appendsToExisting` | Appends to existing content |
| `encodeAndDecode_string_roundTrips` | String round-trip |
| `encodeAndDecode_integer_roundTrips` | Integer round-trip |
| `encodeAndDecode_emptyString` | Empty string round-trip |
| `encodeAndDecode_multipleValues` | Multiple values sequentially |
| `abstractItemCodec_implementsItemCodec` | Implements `ItemCodec` |
| `abstractItemCodec_isAbstract` | Abstract modifier |
| `abstractItemCodec_isPublic` | Public modifier |

#### 15. `SimpleTabCompositeCodecCoverageTest` (156 lines, 18 tests)

**Location:** `core/croquet/src/test/java/org/lgna/croquet/codecs/SimpleTabCompositeCodecCoverageTest.java`

Covers `SimpleTabCompositeCodec` structure tests, plus `DefaultItemCodec` and
`FileCodec` companion classes in the codecs package.

| Method | What It Tests |
|---|---|
| `simpleTabCompositeCodec_implementsItemCodec` | Implements `ItemCodec` |
| `simpleTabCompositeCodec_isPublic` | Public modifier |
| `simpleTabCompositeCodec_isNotAbstract` | Concrete class |
| `hasGetValueClass` | Method presence |
| `hasDecodeValue` | Method presence |
| `hasEncodeValue` | Method presence |
| `hasAppendRepresentation` | Method presence |
| `hasConstructor` | Constructor presence |
| `isInCodecsPackage` | Package assertion |
| `defaultItemCodec_getValueClass` | Default codec value class |
| `defaultItemCodec_isPublic` | Default codec modifier |
| `defaultItemCodec_extendsAbstractItemCodec` | Default codec hierarchy |
| `defaultItemCodec_appendRepresentation_string` | Default codec representation |
| `defaultItemCodec_appendRepresentation_null` | Default codec null representation |
| `fileCodec_implementsItemCodec` | FileCodec implements ItemCodec |
| `fileCodec_getValueClass` | FileCodec value class |
| `fileCodec_singleton_notNull` | FileCodec singleton not null |
| `fileCodec_appendRepresentation_file` | FileCodec representation |

---

### Edit coverage tests

#### 16. `AbstractEditCoverageTest` (262 lines, 29 tests)

**Location:** `core/croquet/src/test/java/org/lgna/croquet/edits/AbstractEditCoverageTest.java`

Covers `DescriptionStyle` enum, `isPersistent`, `canUndo`/`canRedo`, encoding,
and description formatting for `AbstractEdit`.

| Method | What It Tests |
|---|---|
| `descriptionStyle_terseNotDetailed` | Terse style is not detailed |
| `descriptionStyle_detailedIsDetailed` | Detailed style is detailed |
| `descriptionStyle_logIsDetailedAndLog` | Log style is both detailed and log |
| `descriptionStyle_valuesContainsAll` | `values()` contains all styles |
| `descriptionStyle_valueOf_terse` | `valueOf("TERSE")` returns TERSE |
| `descriptionStyle_valueOf_detailed` | `valueOf("DETAILED")` returns DETAILED |
| `descriptionStyle_valueOf_log` | `valueOf("LOG")` returns LOG |
| `defaultCanUndo_returnsTrue` | Default `canUndo` is true |
| `defaultCanRedo_returnsTrue` | Default `canRedo` is true |
| `getModel_nullActivity_returnsNull` | Null activity → null model |
| `getGroup_nullModel_returnsNull` | Null model → null group |
| `getTerseDescription_returnsNonEmpty` | Terse description non-empty |
| `getDetailedDescription_containsClassName` | Detailed description contains class name |
| `getLogDescription_containsClassName` | Log description contains class name |
| `getRedoPresentation_startsWithRedo` | Redo presentation starts with "Redo" |
| `getUndoPresentation_startsWithUndo` | Undo presentation starts with "Undo" |
| `toString_equalsDetailedDescription` | `toString()` matches detailed description |
| `doOrRedo_isDo_callsInternal` | `doOrRedo(true)` calls internal do |
| `doOrRedo_isRedo_callsInternal` | `doOrRedo(false)` calls internal redo |
| `undo_callsInternal` | `undo()` calls internal undo |
| `encode_doesNotThrow` | Encoding does not throw |
| `abstractEdit_implementsEdit` | Implements `Edit` interface |
| `abstractEdit_isAbstract` | Abstract modifier |
| `abstractEdit_isPublic` | Public modifier |
| `abstractEdit_implementsBinaryEncodableAndDecodable` | Binary encoding interface |
| `createCopy_methodExists` | `createCopy()` method present |
| `customDescription_appearsInTerse` | Custom description in terse output |
| `customDescription_appearsInDetailed` | Custom description in detailed output |
| `customDescription_appearsInLog` | Custom description in log output |

#### 17. `StateEditCoverageTest` (240 lines, 30 tests)

**Location:** `core/croquet/src/test/java/org/lgna/croquet/edits/StateEditCoverageTest.java`

Covers encode/decode round-trip, `doOrRedo`/`undo` semantics, description
formatting, and type-specific state edits for `StateEdit<T>`.

| Method | What It Tests |
|---|---|
| `constructor_storesPrevAndNext` | Previous/next values stored |
| `constructor_nullValues` | Null values accepted |
| `constructor_sameValues` | Same prev/next accepted |
| `canUndo_nullModel_false` | Null model → cannot undo |
| `canRedo_nullModel_false` | Null model → cannot redo |
| `getModel_null` | Null model returned |
| `getGroup_null` | Null group returned |
| `doOrRedo_isDo_doesNotThrow` | Do operation completes |
| `doOrRedo_isRedo_cannotRedoThrows` | Redo without model throws |
| `undo_cannotUndo_throws` | Undo without model throws |
| `getTerseDescription_containsSelect` | Terse has "select" |
| `getTerseDescription_containsArrow` | Terse has arrow separator |
| `getTerseDescription_containsPrevValue` | Terse has previous value |
| `getTerseDescription_containsNextValue` | Terse has next value |
| `getDetailedDescription_containsClassName` | Detailed has class name |
| `getLogDescription_containsClassName` | Log has class name |
| `undoPresentation_startsWithUndo` | Undo starts with "Undo" |
| `redoPresentation_startsWithRedo` | Redo starts with "Redo" |
| `undoPresentation_containsValues` | Undo contains values |
| `redoPresentation_containsValues` | Redo contains values |
| `toString_matchesDetailedDescription` | `toString()` matches detailed |
| `integerEdit_storesValues` | Integer state edit stores values |
| `integerEdit_descriptionContainsValues` | Integer description has values |
| `booleanEdit_storesValues` | Boolean state edit stores values |
| `booleanEdit_descriptionContainsValues` | Boolean description has values |
| `doubleEdit_storesValues` | Double state edit stores values |
| `stateEdit_extendsAbstractEdit` | Hierarchy assertion |
| `stateEdit_isFinal` | Final modifier |
| `stateEdit_isPublic` | Public modifier |
| `encode_nullModel_throwsNPE` | Null model encoding throws |

---

### Trigger coverage tests

#### 18. `TriggerBehaviorCoverageTest` (250 lines, 25 tests)

**Location:** `core/croquet/src/test/java/org/lgna/croquet/triggers/TriggerBehaviorCoverageTest.java`

Covers construction, `UserActivity` attachment, and encoding for the three
headless-constructable trigger types: `IterationTrigger`,
`ChangeEventTrigger`, and `CascadeAutomaticDeterminationTrigger`.

| Method | What It Tests |
|---|---|
| `iterationTrigger_constructsWithUserActivity` | Construction succeeds |
| `iterationTrigger_getUserActivity_returnsSameActivity` | Activity stored correctly |
| `iterationTrigger_getViewController_returnsNull` | No view controller |
| `iterationTrigger_encode_doesNotThrow` | Encoding succeeds |
| `iterationTrigger_appendRepr_containsClassName` | Representation has class name |
| `iterationTrigger_setsTriggerOnActivity` | Trigger set on activity |
| `iterationTrigger_multipleInstances_distinct` | Distinct instances |
| `changeEventTrigger_constructs` | Construction succeeds |
| `changeEventTrigger_getUserActivity` | Activity stored correctly |
| `changeEventTrigger_getEvent_returnsEvent` | ChangeEvent stored |
| `changeEventTrigger_setsTriggerOnActivity` | Trigger set on activity |
| `changeEventTrigger_encode_doesNotThrow` | Encoding succeeds |
| `changeEventTrigger_appendRepr` | Representation correct |
| `changeEventTrigger_getViewController_returnsNull` | No view controller |
| `changeEventTrigger_nullEvent` | Null event handled |
| `cascadeTrigger_createsChildActivity` | Child activity created |
| `cascadeTrigger_childActivityHasTrigger` | Child activity has trigger |
| `cascadeTrigger_triggerIsCascadeType` | Trigger is cascade type |
| `cascadeTrigger_getViewController_delegatesToPrevious` | View controller delegation |
| `cascadeTrigger_encode_doesNotThrow` | Encoding succeeds |
| `cascadeTrigger_appendRepr` | Representation correct |
| `userActivity_newChildActivity_createsChild` | Child creation works |
| `userActivity_multipleChildren` | Multiple children supported |
| `userActivity_defaultTrigger_isNull` | Default trigger is null |
| `userActivity_afterTrigger_triggerSet` | Trigger set after construction |

#### 19. `TriggerHierarchyCoverageTest` (554 lines, 63 tests)

**Location:** `core/croquet/src/test/java/org/lgna/croquet/triggers/TriggerHierarchyCoverageTest.java`

**The largest test file.** Tests ALL 21 trigger classes via reflection for class
hierarchy, method signatures, constructor presence, abstract/concrete status,
and `BinaryEncodableAndDecodable` interface implementation.

Trigger classes covered:

| Trigger Class | Tests |
|---|---|
| `Trigger` (base) | Abstract, public, implements `BinaryEncodableAndDecodable`, has `showPopupMenu`, `encode`, `getUserActivity`, `getViewController`, `appendRepr` |
| `EventObjectTrigger` | Extends `Trigger`, abstract, has `getEvent` |
| `ComponentEventTrigger` | Extends `EventObjectTrigger`, abstract |
| `InputEventTrigger` | Extends `ComponentEventTrigger`, concrete |
| `AbstractMouseEventTrigger` | Extends `ComponentEventTrigger`, abstract |
| `MouseEventTrigger` | Extends `AbstractMouseEventTrigger`, concrete |
| `ActionEventTrigger` | Extends `EventObjectTrigger`, concrete, public |
| `ItemEventTrigger` | Extends `EventObjectTrigger`, concrete, public |
| `DocumentEventTrigger` | Extends `Trigger`, concrete, public |
| `KeyEventTrigger` | Extends `ComponentEventTrigger`, concrete, public |
| `PopupMenuEventTrigger` | Extends `ComponentEventTrigger`, concrete, public |
| `PropertyChangeEventTrigger` | Extends `EventObjectTrigger`, concrete, public |
| `WindowEventTrigger` | Extends `ComponentEventTrigger`, concrete, public |
| `TreeSelectionEventTrigger` | Extends `Trigger`, concrete, public |
| `ChangeEventTrigger` | Extends `EventObjectTrigger`, concrete |
| `IterationTrigger` | Extends `Trigger`, concrete |
| `NullTrigger` | Extends `Trigger`, concrete |
| `SimulationTrigger` | Extends `Trigger`, concrete |
| `DragTrigger` | Extends `Trigger`, concrete |
| `DropTrigger` | Extends `Trigger`, concrete |
| `CascadeAutomaticDeterminationTrigger` | Extends `Trigger`, concrete |

---

### Meta-state coverage tests

#### 20. `StateTrackingMetaStateCoverageTest` (138 lines, 12 tests)

**Location:** `core/croquet/src/test/java/org/lgna/croquet/meta/StateTrackingMetaStateCoverageTest.java`

Covers delegation, listener forwarding, and a documented framework bug in
`StateTrackingMetaState`.

| Method | What It Tests |
|---|---|
| `getValue_delegatesToUnderlyingState` | Value delegates to underlying state |
| `getValue_reflectsUnderlyingStateChange` | Meta-state reflects underlying changes |
| `getValue_afterMultipleChanges` | Multiple changes tracked |
| `metaListener_firesOnUnderlyingStateChange` | Meta listener fires on underlying change |
| `metaListener_firesCorrectPrevValue` | Previous value correct in callback |
| `metaListener_doesNotFire_whenSameValue` | Same-value suppresses listener |
| `metaListener_removeValueListener_hasBug_addsInsteadOfRemoves` | **Documents existing framework bug:** `removeValueListener` calls `addNewSchoolValueListener` instead of removing |
| `addAndInvokeListener_firesImmediately` | Immediate invocation on add |
| `multipleListeners_allFire` | Multiple listeners all fire |
| `metaState_isAbstract` | Abstract modifier |
| `stateTrackingMetaState_extendsMetaState` | Hierarchy assertion |
| `stateTrackingMetaState_isAbstract` | Abstract modifier |

---

### Preference coverage tests

#### 21. `PreferenceStringStateCoverageTest` (185 lines, 18 tests)

**Location:** `core/croquet/src/test/java/org/lgna/croquet/preferences/PreferenceStringStateCoverageTest.java`

Covers construction, default value behavior, preference key generation,
encryption key support, and value operations for `PreferenceStringState`.

| Method | What It Tests |
|---|---|
| `constructor_setsDefaultValue` | Default value set |
| `constructor_emptyDefault` | Empty default accepted |
| `constructor_nullDefault_resolvedFromPreferences` | Null default resolved from preferences |
| `getPreferenceKey_returnsNonNull` | Preference key non-null |
| `getPreferenceKey_matchesMigrationId` | Key matches migration ID |
| `setValueTransactionlessly_updatesValue` | Value update works |
| `setValueTransactionlessly_toEmpty` | Empty string accepted |
| `changeValueFromEdit_updatesValue` | Edit path works |
| `isEnabled_defaultTrue` | Enabled by default |
| `setEnabled_false` | Can disable |
| `appendRepresentation` | Representation correct |
| `listener_firesOnChange` | Listener fires |
| `getSwingModel_nonNull` | Swing model non-null |
| `getSwingModel_documentNonNull` | Document non-null |
| `isStoringPreferenceDesired_defaultTrue` | Storing desired by default |
| `getMigrationId_nonNull` | Migration ID non-null |
| `getEncryptionKey_null_returnsNull` | Null encryption key for default |
| `getEncryptionKey_nonNull_returnsBytes` | Custom encryption key returns bytes |

#### 22. `PreferenceMutableDataSingleSelectListStateCoverageTest` (114 lines, 11 tests)

**Location:** `core/croquet/src/test/java/org/lgna/croquet/preferences/PreferenceMutableDataSingleSelectListStateCoverageTest.java`

Covers construction, data management, and hierarchy verification for
`PreferenceMutableDataSingleSelectListState`.

| Method | What It Tests |
|---|---|
| `constructor_setsInitialValue` | Initial value set |
| `constructor_itemCount` | Item count correct |
| `constructor_selectedIndex` | Selected index correct |
| `setSelectedIndex_updatesValue` | Selection by index |
| `setValueTransactionlessly_updatesIndex` | Value updates index |
| `addItem_increases_count` | Add increases count |
| `removeItem_decreases_count` | Remove decreases count |
| `getData_nonNull` | Data non-null |
| `iterator_coversAll` | Iterator complete |
| `hierarchy_extendsMutableDataSingleSelectListState` | Direct parent check |
| `hierarchy_extendsSingleSelectListState` | Transitive hierarchy |

---

## Coverage analysis

### What is covered

- **State classes:** Full behavioral coverage for `BooleanState`,
  `BoundedIntegerState`, `BoundedDoubleState`, `StringState`,
  `EnumConstantState`, `ImmutableDataSingleSelectListState`,
  `MutableDataSingleSelectListState`, `RefreshableDataSingleSelectListState`,
  `ItemState`, `PreferenceStringState`,
  `PreferenceMutableDataSingleSelectListState`, and
  `StateTrackingMetaState`. Reflection-only coverage for `ColorState`,
  `TabState`, and `CustomItemState`.

- **Codecs:** Full behavioral coverage for `EnumCodec` and
  `AbstractItemCodec`. Reflection + companion class coverage for
  `SimpleTabCompositeCodec`, `DefaultItemCodec`, and `FileCodec`.

- **Edits:** Full behavioral coverage for `AbstractEdit` and `StateEdit`,
  including `DescriptionStyle` enum, undo/redo semantics, and multi-type
  state edits (String, Integer, Boolean, Double).

- **Triggers:** Full behavioral coverage for `IterationTrigger`,
  `ChangeEventTrigger`, and `CascadeAutomaticDeterminationTrigger`.
  Reflection-based hierarchy coverage for all 21 trigger classes.

- **UserActivity:** Construction, child activity creation, trigger
  attachment tested as part of trigger behavioral tests.

### What is explicitly excluded

| Class | Reason |
|---|---|
| `ColorState` behavioral tests | Constructor accesses `Application.INHERIT_GROUP` |
| `TabState` behavioral tests | Requires `TabComposite<?>` items with Swing infrastructure |
| `CustomItemState` behavioral tests | Requires cascade `CascadeBlank` infrastructure |
| Application-dependent triggers (behavioral) | Private constructors call `Application.getActiveInstance()` |
| `DragTrigger`/`DropTrigger` behavioral tests | Require `ViewController` with Swing components |
| `PreferenceStringState` encryption paths | `PreferenceManager.getUserPreferences()` returns null without Application |

### Known framework bugs documented

1. **`StateTrackingMetaState.removeValueListener()` bug** — The
   `removeValueListener()` method calls `addNewSchoolValueListener()` instead of
   removing the listener. This is a pre-existing framework bug documented by
   `metaListener_removeValueListener_hasBug_addsInsteadOfRemoves` in
   `StateTrackingMetaStateCoverageTest`. No production code change made.

---

## Production classes exercised

| Package | Classes Exercised |
|---|---|
| `org.lgna.croquet` | `BooleanState`, `BoundedIntegerState`, `BoundedDoubleState`, `StringState`, `EnumConstantState`, `ImmutableDataSingleSelectListState`, `MutableDataSingleSelectListState`, `RefreshableDataSingleSelectListState`, `ItemState`, `SingleSelectListState`, `State`, `Group`, `AbstractCompletionModel`, `ColorState`*, `TabState`*, `CustomItemState`* |
| `org.lgna.croquet.codecs` | `EnumCodec`, `AbstractItemCodec`, `SimpleTabCompositeCodec`, `DefaultItemCodec`, `FileCodec` |
| `org.lgna.croquet.edits` | `AbstractEdit`, `StateEdit` |
| `org.lgna.croquet.triggers` | All 21 trigger classes (see hierarchy table above) |
| `org.lgna.croquet.meta` | `MetaState`, `StateTrackingMetaState` |
| `org.lgna.croquet.preferences` | `PreferenceStringState`, `PreferenceMutableDataSingleSelectListState` |
| `org.lgna.croquet.history` | `UserActivity` |
| `edu.cmu.cs.dennisc.codec` | `ByteArrayBinaryEncoder`, `BinaryDecoder`, `BinaryEncoder` |

\* Reflection-only coverage
