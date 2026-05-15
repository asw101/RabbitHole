# CompositeResourceManager Inner Class and Localization Extraction

This reference documents the extraction of 13 inner state classes and 2
localization methods from `CompositeResourceManager` into two new
package-private files in `org.lgna.croquet` (issue #631).

Before extraction: 649 lines. After extraction: ~225 lines.

## Contents

- [Motivation](#motivation)
- [Extracted components](#extracted-components)
- [File inventory](#file-inventory)
- [Visibility changes](#visibility-changes)
- [Localization delegate design](#localization-delegate-design)
- [Factory methods retained in CompositeResourceManager](#factory-methods-retained-in-compositeresourcemanager)
- [Characterization tests](#characterization-tests)
- [Validation commands](#validation-commands)
- [Compatibility rules](#compatibility-rules)
- [Security considerations](#security-considerations)
- [Examples](#examples)

## Motivation

`CompositeResourceManager.java` contained 649 lines, of which ~381 were 13
`private static final` inner classes that implement framework state types
(`InternalStringValue`, `InternalBooleanState`, etc.). These classes are only
instantiated by factory methods and never referenced by code outside the
`org.lgna.croquet` package.

Additionally, the `localize()` and `localizeSidekicks()` methods (~55 lines)
are pure functions that read the state maps but do not mutate them, making
them natural candidates for a static utility delegate.

Extracting these two concerns reduces `CompositeResourceManager` to its
essential responsibility: owning the state maps, factory methods, registration
methods, and the `contains()` lookup index.

## Extracted components

### InternalStateTypes.java — 13 state classes

| Class | Supertype | Static? | Key fields |
| --- | --- | --- | --- |
| `InternalStringValue` | `AbstractComposite.AbstractInternalStringValue` | Yes | `key` (inherited) |
| `InternalStringState` | `StringState` | Yes | `key` |
| `InternalPreferenceStringState` | `PreferenceStringState` | Yes | `key`, `isStoringPreferenceDesiredState` |
| `InternalBooleanState` | `BooleanState` | Yes | `key` |
| `InternalPreferenceBooleanState` | `PreferenceBooleanState` | Yes | `key` |
| `InternalSingleSelectListState<T>` | `SingleSelectListState<T, ListData<T>>` | Yes | `key` |
| `InternalImmutableDataSingleSelectListState<T>` | `ImmutableDataSingleSelectListState<T>` | Yes | `key` |
| `InternalRefreshableDataSingleSelectListState<T>` | `RefreshableDataSingleSelectListState<T>` | Yes | `key` |
| `InternalMutableDataSingleSelectListState<T>` | `MutableDataSingleSelectListState<T>` | Yes | `key` |
| `InternalTabState<T>` | `SimpleTabState<T>` | Yes | `key` |
| `InternalBoundedIntegerState` | `BoundedIntegerState` | Yes | `key` |
| `InternalBoundedDoubleState` | `BoundedDoubleState` | Yes | `key` |
| `InternalCascadeWithInternalBlank<T>` | `CascadeWithInternalBlank<T>` | Yes | `key`, `customizer` |

All 13 classes share a common pattern:
- Store an `AbstractComposite.Key key` field
- Override `getClassUsedForLocalization()` → `key.getComposite().getClass()`
- Override `getSubKeyForLocalization()` → `key.getLocalizationKey()`
- Override `appendRepr(StringBuilder)` to append `;key=`

`InternalCascadeWithInternalBlank` additionally overrides `createEdit()` and
`updateBlankChildren()` to delegate to its `CascadeCustomizer`.

`InternalPreferenceStringState` calls the inherited static
`getEncryptionKey()` from `PreferenceStringState` — this call chain is
unchanged by the extraction.

### CompositeLocalizationDelegate.java — static localization utility

| Method | Purpose |
| --- | --- |
| `localize(AbstractComposite<?>, maps...)` | Iterates `mapKeyToStringValue` to set localized text on each string value |
| `localizeSidekicks(AbstractComposite<?>, maps...)` | Iterates completion-model maps to set sidekick labels from localization bundles |

The `SIDEKICK_LABEL_EPILOGUE` constant (`.sidekickLabel`) moves with the
localization delegate as it is only referenced by `localizeSidekicks`.

## File inventory

| File | Role | Approx lines |
| --- | --- | --- |
| `CompositeResourceManager.java` | State maps, factory methods, `contains()`, registration. Delegates `localize()` to `CompositeLocalizationDelegate`. | ~225 |
| `InternalStateTypes.java` | 13 package-private `static final` state classes + imports | ~395 |
| `CompositeLocalizationDelegate.java` | Static `localize()` and `localizeSidekicks()` methods | ~55 |
| `CompositeResourceManagerTest.java` | 24 existing + 3 new characterization tests | ~350 |

## Visibility changes

| Before | After | Rationale |
| --- | --- | --- |
| `private static final class InternalStringValue` | `static final class InternalStringValue` (package-private) | Constructors called by `CompositeResourceManager` factory methods |
| `private static final class InternalBooleanState` | `static final class InternalBooleanState` (package-private) | Same |
| (all 13 inner classes) | package-private in `InternalStateTypes.java` | Same package, no new public API surface |

**Risk: LOW.** All 13 classes were only referenced by factory methods in
`CompositeResourceManager`. Widening from `private` to package-private
exposes them only to other classes in `org.lgna.croquet` — a framework-internal
package. No external consumer can reference them.

## Localization delegate design

`CompositeLocalizationDelegate` receives maps as method parameters rather
than storing them as constructor arguments:

```java
final class CompositeLocalizationDelegate {

    private static final String SIDEKICK_LABEL_EPILOGUE = ".sidekickLabel";

    static void localize(AbstractComposite<?> composite,
            Map<AbstractComposite.Key, AbstractComposite.AbstractInternalStringValue> stringValues,
            Map<AbstractComposite.Key, ? extends CompletionModel>... sidekickMaps) {
        for (Map.Entry<...> entry : stringValues.entrySet()) {
            AbstractComposite.AbstractInternalStringValue sv = entry.getValue();
            sv.setText(composite.modifyLocalizedText(sv,
                composite.findLocalizedText(entry.getKey().getLocalizationKey())));
        }
        localizeSidekicks(composite, sidekickMaps);
    }

    @SuppressWarnings("unchecked")
    private static void localizeSidekicks(AbstractComposite<?> composite,
            Map<AbstractComposite.Key, ? extends CompletionModel>... maps) {
        // ... iterates maps, sets sidekick labels from localization bundles
    }
}
```

This design avoids storing duplicate map references and keeps the delegate
stateless. `CompositeResourceManager.localize()` becomes a one-line
forwarding call.

## Factory methods retained in CompositeResourceManager

All `create*` factory methods stay in `CompositeResourceManager` because they:
1. Instantiate types from `InternalStateTypes`
2. Register them in the state maps
3. Add them to the `containsIndex` identity set

Moving factory methods out would require exposing the maps, defeating the
purpose of the extraction.

Full list of retained factory and registration methods (15 total):

| Method | Returns |
| --- | --- |
| `createStringValue(key)` | `PlainStringValue` |
| `createStringState(key, initialValue)` | `StringState` |
| `createPreferenceStringState(key, initialValue, booleanState, encryptionId)` | `PreferenceStringState` |
| `createBooleanState(key, initialValue)` | `BooleanState` |
| `createPreferenceBooleanState(key, initialValue)` | `PreferenceBooleanState` |
| `createBoundedIntegerState(key, details)` | `BoundedIntegerState` |
| `createBoundedDoubleState(key, details)` | `BoundedDoubleState` |
| `createCascadeWithInternalBlank(key, cls, customizer)` | `Cascade<T>` |
| `createGenericListState(key, data, selectionIndex)` | `SingleSelectListState<T, ListData<T>>` |
| `createImmutableListState(key, selectionIndex, codec, values)` | `ImmutableDataSingleSelectListState<T>` |
| `createImmutableListStateForEnum(key, valueCls, customizer, initialValue)` | `ImmutableDataSingleSelectListState<T>` |
| `createRefreshableListState(key, data, selectionIndex)` | `RefreshableDataSingleSelectListState<T>` |
| `createMutableListState(key, codec, selectionIndex, values)` | `MutableDataSingleSelectListState<T>` |
| `createImmutableTabState(key, selectionIndex, cls, tabComposites)` | `ImmutableDataTabState<C>` |
| `registerStringValue(stringValue)` | `void` |

Additionally retained: `registerActionOperation()`, `registerCustomItemState()`,
`getMapKeyToTabState()`, and `contains()`.

## Characterization tests

Three new tests are added to `CompositeResourceManagerTest.java` to
characterize the extraction boundaries:

### `localize_delegatesToCompositeLocalizationDelegate`

Verifies that calling `localize()` on a `CompositeResourceManager` with
registered string values and boolean states still produces localized text
through the new `CompositeLocalizationDelegate` boundary. The test creates
string values with known localization keys, calls `localize()`, and verifies
the text was set by checking the `getText()` result.

### `factoryMethods_createTypesFromInternalStateTypes`

Verifies that each factory method returns an instance of the expected
supertype (`BooleanState`, `StringState`, `PreferenceBooleanState`,
`BoundedIntegerState`, `BoundedDoubleState`). This proves the types in
`InternalStateTypes.java` are correctly instantiated across the file boundary.

### `contains_worksAfterExtraction`

Round-trip test that creates one instance via each factory method, calls
`contains()` for each, and verifies all return `true`. Also verifies an
unknown `StubModel` returns `false`. This proves the `containsIndex`
identity set still works correctly when types live in a separate file.

## Validation commands

### Compile the croquet module

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/croquet -am compile
```

### Run existing + new tests

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/croquet -am \
  -Dtest=CompositeResourceManagerTest \
  test
```

Expected: 27 tests pass (24 existing + 3 new), 0 failures, 0 errors.

### Verify line count

```bash
wc -l core/croquet/src/main/java/org/lgna/croquet/CompositeResourceManager.java
```

Target: under 500 lines (actual ~225).

### Verify no inner class declarations remain

```bash
grep -c 'private static final class Internal' \
  core/croquet/src/main/java/org/lgna/croquet/CompositeResourceManager.java
```

Expected: 0.

### Verify new files exist

```bash
ls -la core/croquet/src/main/java/org/lgna/croquet/InternalStateTypes.java \
       core/croquet/src/main/java/org/lgna/croquet/CompositeLocalizationDelegate.java
```

Both files must exist with package-private (default) class visibility.

## Compatibility rules

1. **Zero changes to AbstractComposite.java call sites.** All factory method
   calls from `AbstractComposite` go through `this.resourceManager.create*()`
   unchanged.

2. **No new public API.** Both new files contain only package-private classes.
   No external consumer can import or reference them.

3. **Same package, same classloader.** `InternalStateTypes` classes are loaded
   by the same classloader that previously loaded `CompositeResourceManager`
   inner classes. No serialization or reflection boundaries are crossed.

4. **All 24 existing tests pass unchanged.** The extraction is purely
   structural — no behavioral change.

5. **Localization produces identical results.** The delegate receives the
   same maps and calls the same `findLocalizedText` / `modifyLocalizedText`
   methods on the same composite instance.

## Security considerations

- **`getEncryptionKey()` call chain is unchanged.**
  `InternalPreferenceStringState` calls the inherited static
  `PreferenceStringState.getEncryptionKey()`. Moving the class to a separate
  file does not alter the method resolution — it remains a static call
  inherited from the superclass.

- **No new public API surface.** Extracted types are package-private. No
  encryption keys, preference values, or internal state are exposed beyond
  the existing package boundary.

- **No serialization boundaries crossed.** All types remain in the same
  package, loaded by the same classloader, within the same JVM process.

## Examples

### Before: CompositeResourceManager.java (649 lines)

```
CompositeResourceManager.java
├── 13 private static final inner classes      (lines 73–453, ~381 lines)
├── 15 state maps                              (lines 457–474)
├── map accessor + registration methods        (lines 478–496)
├── 15 factory methods                         (lines 500–597)
├── contains()                                 (lines 601–603)
├── SIDEKICK_LABEL_EPILOGUE constant           (line 607)
├── localizeSidekicks()                        (lines 610–639)
└── localize()                                 (lines 642–648)
```

### After: 3 files

```
CompositeResourceManager.java (~225 lines)
├── 15 state maps
├── map accessor + registration methods
├── 15 factory methods (instantiate InternalStateTypes classes)
├── contains()
└── localize() → one-line delegation to CompositeLocalizationDelegate

InternalStateTypes.java (~395 lines)
├── InternalStringValue
├── InternalStringState
├── InternalPreferenceStringState
├── InternalBooleanState
├── InternalPreferenceBooleanState
├── InternalSingleSelectListState<T>
├── InternalImmutableDataSingleSelectListState<T>
├── InternalRefreshableDataSingleSelectListState<T>
├── InternalMutableDataSingleSelectListState<T>
├── InternalTabState<T>
├── InternalBoundedIntegerState
├── InternalBoundedDoubleState
└── InternalCascadeWithInternalBlank<T>

CompositeLocalizationDelegate.java (~55 lines)
├── SIDEKICK_LABEL_EPILOGUE constant
├── localize() — iterates string value map, sets text
└── localizeSidekicks() — iterates completion model maps, sets sidekick labels
```

### Calling pattern (unchanged)

```java
// In AbstractComposite — no changes required
BooleanState state = this.resourceManager.createBooleanState(key, false);
// ...
this.resourceManager.localize(this);  // delegates to CompositeLocalizationDelegate
```
