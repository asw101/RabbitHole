# SingleSelectListState decomposition into focused helper classes

The `org.lgna.croquet.SingleSelectListState` class has been decomposed from a 500-line monolith into a focused orchestration class plus three extracted helpers. Three inner/anonymous constructs — `DataIndexPair`, `EmptyConditionText`, and the `ListSelectionListener` anonymous class — now live as package-private top-level classes in `org.lgna.croquet`. Two deprecated `setListData` overloads have been consolidated into a single delegation method.

After decomposition, `SingleSelectListState.java` is under 400 lines and contains only its core responsibilities: selection index management, atomic change guarding, list mutation, and view factory methods. The three new classes each hold a single coherent responsibility and live in the same package (`org.lgna.croquet`), requiring no module or POM changes.

## Finished behavior

### DataIndexPair

`DataIndexPair<T, D extends ListData<T>>` is a package-private class that implements `javax.swing.ComboBoxModel`. It adapts a `ListData<T>` instance and a mutable selection index into the Swing `ComboBoxModel` contract, bridging the croquet data layer to Swing's `JComboBox` and `JList` components.

| Member | Visibility | Purpose |
| --- | --- | --- |
| `DataIndexPair(D data, int index, IntConsumer selectionIndexSetter)` | package | Constructor. Accepts the data source, initial selection index, and a callback to propagate `setSelectedItem` back to the owning `SingleSelectListState`'s swing model without a direct class reference. |
| `addListDataListener(ListDataListener)` | public | Delegates to `data.addListener()`. |
| `removeListDataListener(ListDataListener)` | public | Delegates to `data.removeListener()`. |
| `getSize()` | public | Returns `data.getItemCount()`. |
| `getElementAt(int)` | public | Returns `data.getItemAt(index)`, or `null` for index `‑1`. |
| `getSelectedItem()` | public | Returns the element at the current selection index, or `null` when no selection. |
| `setSelectedItem(Object)` | public | Looks up the item's index via `data.indexOf()` and invokes the `selectionIndexSetter` callback. |
| `data` | package | The backing `ListData<T>` instance. Accessed directly by `SingleSelectListState` (14 access sites). |
| `index` | package | The current selection index. Accessed directly by `SingleSelectListState` (9 access sites). |

**Design decision — `IntConsumer` callback:** The original inner class called `SingleSelectListState.this.swingModel.setSelectionIndex(index)` directly. Because `DataIndexPair` is now a separate class, the chicken-and-egg init order (both `DataIndexPair` and `swingModel` are `final` fields) is resolved by passing an `IntConsumer` at construction time. The consumer must be a lambda `idx -> swingModel.setSelectionIndex(idx)`, not a method reference — a method reference (`swingModel::setSelectionIndex`) would eagerly evaluate `swingModel`, which is `null` at the point of `DataIndexPair` construction. The lambda defers field access to invocation time, which is safe because `setSelectedItem` is only called after construction completes.

**Pre-existing TODO preserved:** The original `setSelectedItem` contains the comment `//todo: update this.index???`. This TODO is preserved in the extraction. The current behavior is intentional: `setSelectedItem` updates the swing model's selection index via the callback but does not directly update `DataIndexPair.index`. The index is updated indirectly through the Swing event chain (`setSelectionIndex` → `ListSelectionEvent` → listener → `changingValueFromSwing` → `setCurrentTruthAndBeautyValue` → `dataIndexPair.index = ...`). This refactoring does not change that behavior.

**Design decision — package-private fields:** The `data` and `index` fields remain package-private (no getters). There are 23 direct field accesses from `SingleSelectListState` (14 for `data`, 9 for `index`, across 21 source lines) and same-package access is idiomatic in the croquet codebase. Introducing getters would add boilerplate without safety benefit since both classes are in the same package and neither is subclassed externally.

### EmptyConditionText

`EmptyConditionText` is a package-private class extending `PlainStringValue`. It provides the localized "empty list" label text displayed by combo box and list views when no items are available.

| Member | Visibility | Purpose |
| --- | --- | --- |
| `EmptyConditionText(Supplier<Class<? extends Element>> localizationClassSupplier, Supplier<String> subKeySupplier)` | package | Constructor. Accepts two suppliers that resolve the localization class and sub-key from the owning `SingleSelectListState`, avoiding a direct back-reference. |
| `getClassUsedForLocalization()` | protected | Returns the result of `localizationClassSupplier.get()`. |
| `getSubKeyForLocalization()` | protected | Appends `".emptyConditionText"` to the result of `subKeySupplier.get()` (or uses `"emptyConditionText"` alone if the sub-key is null). |

**Design decision — `Supplier` parameters:** The original inner class accessed `SingleSelectListState.this.getClassUsedForLocalization()` and `SingleSelectListState.this.getSubKeyForLocalization()` directly. Two `Supplier` parameters decouple the localization without reflection and without exposing `SingleSelectListState` internals as public API.

**Fixed UUID:** The hardcoded UUID `c71e2755-d05a-4676-87db-99b3baec044d` is preserved unchanged. It is the stable identity for this localization key and must not be modified.

### ListSelectionListenerAdapter

`ListSelectionListenerAdapter<T>` is a package-private class implementing `javax.swing.event.ListSelectionListener`. It translates Swing `ListSelectionEvent` notifications into croquet `changingValueFromSwing` calls on the owning `SingleSelectListState`.

| Member | Visibility | Purpose |
| --- | --- | --- |
| `ListSelectionListenerAdapter(SingleSelectListState<T, ?> owner)` | package | Constructor. Stores a reference to the owning state for `changingValueFromSwing` dispatch. |
| `valueChanged(ListSelectionEvent)` | public | Guards against re-entrant calls using `owner.isSettingSwingValue()`. When not re-entrant, reads the current selection index from `owner.getSwingModel()` (public), resolves the selected item via the combo box model, creates a `NullTrigger` user activity, and calls `owner.changingValueFromSwing()`. Finishes pending activities. |

**Design decision — `isSettingSwingValue()` accessor:** The original anonymous class read the private field `isInTheMidstOfSettingSwingValue` directly. A new package-private accessor method `isSettingSwingValue()` is added to `SingleSelectListState` to allow `ListSelectionListenerAdapter` the same guard check without exposing the field as public API. This is the minimal surface area increase required.

**Visibility constraint — `changingValueFromSwing`:** This method is declared `final void` (package-private) in `State.java`. Same-package access from `ListSelectionListenerAdapter` works correctly. If the adapter were ever moved to a different package, this call would fail to compile — an intentional constraint keeping the adapter tightly coupled to the croquet package.

## Changes to SingleSelectListState

### Removed constructs

| Construct | Lines removed | Replaced by |
| --- | --- | --- |
| Inner class `DataIndexPair` | L74–123 (50 lines) | Top-level `DataIndexPair.java` |
| Inner class `EmptyConditionText` | L440–461 (22 lines) | Top-level `EmptyConditionText.java` |
| Anonymous `ListSelectionListener` | L472–491 (20 lines) | Top-level `ListSelectionListenerAdapter.java` |
| Duplicate `setListData(int, T...)` body | L403–411 (9 lines) | Delegates to `setListData(int, Collection)` |

### Added members

| Member | Visibility | Purpose |
| --- | --- | --- |
| `isSettingSwingValue()` | package | Returns the value of `isInTheMidstOfSettingSwingValue`. Used by `ListSelectionListenerAdapter`. |

### Consolidated deprecated methods

The two `@Deprecated setListData` overloads previously had identical bodies (push atomic, setItems, setSelectedIndex, pop atomic). After consolidation, the varargs overload delegates to the `Collection` overload:

```java
@Deprecated
public void setListData(int selectedIndex, T... items) {
    this.setListData(selectedIndex, Arrays.asList(items));
}

@Deprecated
public void setListData(int selectedIndex, Collection<T> items) {
    this.pushIsInTheMidstOfAtomicChange();
    try {
        this.setItems(items);
        this.setSelectedIndex(selectedIndex);
    } finally {
        this.popIsInTheMidstOfAtomicChange();
    }
}
```

### Constructor change

The constructor now passes a lambda `idx -> getSwingModel().setSelectionIndex(idx)` as the `IntConsumer` to `DataIndexPair`, and instantiates `EmptyConditionText` with supplier lambdas and `ListSelectionListenerAdapter` with `this`:

```java
public SingleSelectListState(Group group, UUID id, int selectionIndex, D data) {
    super(group, id, getItemAt(data, selectionIndex), data.getItemCodec());
    this.dataIndexPair = new DataIndexPair<>(data, selectionIndex, idx -> getSwingModel().setSelectionIndex(idx));
    this.swingModel = new SingleSelectListStateSwingModel(this.dataIndexPair);
    swingModel.getListSelectionModel().addListSelectionListener(
        new ListSelectionListenerAdapter<>(this));
}
```

Note: The `IntConsumer` lambda captures `this` and calls `getSwingModel()` at invocation time. The `swingModel` field is assigned on the next line, which is safe because `setSelectedItem` (which invokes the consumer) is never called during construction — it is only invoked later by Swing event dispatch.

### Field declarations (after extraction)

```java
private final DataIndexPair<T, D> dataIndexPair;
private final SingleSelectListStateSwingModel swingModel;
private final Lazy<MenuModel> menuModelLazy = ...;         // unchanged
private final PlainStringValue emptyConditionText = new EmptyConditionText(
    this::getClassUsedForLocalization,
    this::getSubKeyForLocalization
);
private final Lazy<SingleSelectListStateComboBoxPrepModel<T, D>> comboBoxPrepModelLazy = ...; // unchanged
private boolean isInTheMidstOfSettingSwingValue;
```

### Line count

| Version | Lines |
| --- | --- |
| Before | 500 |
| After | ~394 |

The reduction comes from: 50 lines (`DataIndexPair`), 22 lines (`EmptyConditionText`), 20 lines (anonymous listener), 9 lines (duplicate `setListData` body), offset by +1 line (`isSettingSwingValue` accessor) and +1 line (lambda consumer parameter), minus ~4 blank lines trimmed.

## New files

All three files are in `core/croquet/src/main/java/org/lgna/croquet/`:

| File | Lines | Visibility |
| --- | --- | --- |
| `DataIndexPair.java` | ~70 | Package-private class |
| `EmptyConditionText.java` | ~40 | Package-private class |
| `ListSelectionListenerAdapter.java` | ~45 | Package-private class |

None of the new classes are part of croquet's public API. They are implementation details consumed only by `SingleSelectListState` and its immediate neighbors in the `org.lgna.croquet` package.

## Public API impact

**None.** All public and protected methods on `SingleSelectListState` retain their exact signatures and behavior. The extraction is purely structural — no method was added, removed, renamed, or had its access modifier changed on the public surface. Subclasses such as `MutableDataSingleSelectListState` are unaffected because they interact only through the public/protected API of `SingleSelectListState` and `ItemState`.

## Test coverage

Characterization tests in `core/croquet/src/test/java/org/lgna/croquet/SingleSelectListStateTest.java` pin the following behaviors before any extraction begins:

| Test | Behavior pinned |
| --- | --- |
| `testInitialSelectionIndex` | Constructor correctly sets the initial selection index and `getValue()` returns the corresponding item. |
| `testSetSelectedIndex` | `setSelectedIndex(i)` updates both `getSelectedIndex()` and `getValue()` consistently. |
| `testClearSelection` | `clearSelection()` sets index to -1 and value to null. |
| `testSetItems` | `setItems(Collection)` replaces the backing data, preserves selection when the previously-selected item is in the new set, and resets to -1 when it is not. |
| `testAddAndRemoveItem` | `addItem(T)` increases count by 1, `removeItem(T)` decreases it. Selection index integrity is maintained across mutations. |
| `testAtomicChangeGuard` | Nested `pushIsInTheMidstOfAtomicChange`/`popIsInTheMidstOfAtomicChange` coalesces intermediate changes. The `changeValue` callback fires exactly once on the outermost pop, not on inner pops. |
| `testSetListDataDeprecated` | The `@Deprecated setListData(int, Collection)` method atomically replaces items and sets the selection index in one operation. |
| `testDataIndexPairComboBoxModelContract` | The extracted `DataIndexPair` satisfies the `ComboBoxModel` contract: `getSize()`, `getElementAt()`, `getSelectedItem()`, and `setSelectedItem()` behave correctly. |
| `testEmptyConditionTextLocalization` | `EmptyConditionText` returns the correct sub-key suffix `".emptyConditionText"` and delegates localization class resolution to the owner. |

Tests use a minimal `ListData` stub (backed by `ArrayList`) and do not require a running Swing event loop or Alice application context.

## Risks and mitigations

| Risk | Mitigation |
| --- | --- |
| Init-order: `DataIndexPair` consumer captures `swingModel` before assignment | Consumer is a lambda (not a method reference), so `swingModel` is read at invocation time, not at capture time. `setSelectedItem` is never invoked during construction. Verified by characterization test. |
| Downstream subclass breakage | All extracted code was `private` inner classes or anonymous. No subclass could have accessed them. Public API surface is unchanged. |
| `ComboBoxModel` contract violation after extraction | `DataIndexPair` preserves the exact same implementation. `SingleSelectListStateSwingModel` continues to receive the same `ComboBoxModel` instance. |
| Serialization | `SingleSelectListState` is not `Serializable`. No serialization concerns. |

## Module and build impact

No POM, module-info, or build configuration changes are required. All three new classes are in the same package (`org.lgna.croquet`) and the same Maven module (`core/croquet`). The existing `maven-compiler-plugin` configuration compiles them automatically.
