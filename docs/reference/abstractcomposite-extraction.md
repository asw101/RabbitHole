# AbstractComposite Extraction — Architecture Reference

> **Issue:** #578 — Reduce AbstractComposite.java from 1113 lines to under 500
>
> **Module:** `core/croquet` (`org.lgna.croquet`)

## Overview

`AbstractComposite<V>` was a 1113-line god class responsible for view lifecycle,
tab/card management, 11 inner state classes, 15 keyed maps, 14+ factory methods,
localization, and model containment checks. Three cohesive helper classes were
extracted to bring `AbstractComposite` down to ~390 lines of focused coordination
logic.

### After Extraction

| Class | Responsibility | Approx. Lines |
|---|---|---|
| `AbstractComposite<V>` | Key, externally-referenced inner types, `synchronized` monitors, thin delegation wrappers | ~390 |
| `CompositeResourceManager` | 11 Internal\* state classes + 3 operation/cascade/item classes, 15 keyed maps, `create*` factories, `contains()`, `localize()` | ~700 |
| `CompositeTabManager` | `InternalTabState`, `InternalSplitComposite`, `InternalCardOwnerComposite`, tab activation loops | ~170 |
| `CompositeViewLifecycle<V>` | View field, `ScrollPane`, `cardId`, lazy init (view/cardId), eager init (scrollPane) | ~90 |

All three helper classes are **package-private** (no `public` modifier). They are
internal implementation details of the croquet composite framework and are not
part of the public API.

---

## Class Catalog

### AbstractComposite\<V extends CompositeView\<?, ?\>\>

**Location:** `core/croquet/src/main/java/org/lgna/croquet/AbstractComposite.java`

The central composite abstraction. After extraction, it retains:

- **Key** — inner class used by all resource factories (referenced externally by
  `AbstractSeverityStatusComposite` and others)
- **AbstractInternalStringValue** — protected abstract inner class extended by
  `AbstractSeverityStatusComposite.InternalSideButton`
- **BoundedIntegerDetails / BoundedDoubleDetails** — public inner classes
  referenced by at least 4 external files
- **Action / ItemStateCustomizer / CascadeCustomizer** — protected interfaces
  implemented by subclasses
- `synchronized getView()` and `synchronized getCardId()` — thread-safe monitors
  that stay on the `AbstractComposite` instance (not delegated)
- All `protected create*()` factory wrappers — thin one-liners that forward to
  `CompositeResourceManager` or `CompositeTabManager`
- `registerStringValue()` — delegation wrapper, called by
  `AbstractSeverityStatusComposite` subclass
- `handlePreActivation()` / `handlePostDeactivation()` — orchestration methods
  that delegate tab iteration to `CompositeTabManager`
- `subComposites` list and `registerSubComposite()`/`unregisterSubComposite()`

**Why these stay:** 14+ subclasses call `super.handlePreActivation()`. The
synchronized monitors must use `this` (the composite instance) as the lock object
for correctness. The inner types and interfaces are referenced from outside the
package.

### CompositeResourceManager

**Location:** `core/croquet/src/main/java/org/lgna/croquet/CompositeResourceManager.java`

Owns the keyed registries of internal state objects and their factory methods.

**Inner classes moved here:**
- `InternalStringValue`
- `InternalStringState`
- `InternalPreferenceStringState`
- `InternalBooleanState`
- `InternalPreferenceBooleanState`
- `InternalSingleSelectListState<T>`
- `InternalImmutableDataSingleSelectListState<T>`
- `InternalRefreshableDataSingleSelectListState<T>`
- `InternalMutableDataSingleSelectListState<T>`
- `InternalBoundedIntegerState`
- `InternalBoundedDoubleState`
- `InternalActionOperation` — constructor becomes package-private (was `private`);
  the **class definition** stays in `AbstractComposite` because `Action.perform()`
  references it in its signature. `CompositeResourceManager` instantiates via
  same-package access.
- `InternalCascadeWithInternalBlank<T>` — same pattern; class stays in AC,
  constructor becomes package-private.
- `InternalCustomItemState<T>` — same pattern; class stays in AC,
  constructor becomes package-private.

**Maps moved here (15 total):**
- `mapKeyToStringValue`
- `mapKeyToStringState`
- `mapKeyToPreferenceStringState`
- `mapKeyToBooleanState`
- `mapKeyToPreferenceBooleanState`
- `mapKeyToSingleSelectListState`
- `mapKeyToImmutableSingleSelectListState`
- `mapKeyToRefreshableSingleSelectListState`
- `mapKeyToMutableSingleSelectListState`
- `mapKeyToBoundedIntegerState`
- `mapKeyToBoundedDoubleState`
- `mapKeyToActionOperation`
- `mapKeyToCascade`
- `mapKeyToItemState`
- `mapKeyToTabState` (shared with `CompositeTabManager` via accessor)

**Key methods:**

```java
// Factory methods — each creates the Internal* instance and registers it
PlainStringValue createStringValue(AbstractComposite.Key key)
StringState createStringState(AbstractComposite.Key key, String initialValue)
PreferenceStringState createPreferenceStringState(AbstractComposite.Key key,
    String initialValue, BooleanState isStoringPreferenceDesiredState,
    UUID encryptionId)
BooleanState createBooleanState(AbstractComposite.Key key, boolean initialValue)
PreferenceBooleanState createPreferenceBooleanState(AbstractComposite.Key key,
    boolean initialValue)
BoundedIntegerState createBoundedIntegerState(AbstractComposite.Key key,
    BoundedIntegerState.Details details)
BoundedDoubleState createBoundedDoubleState(AbstractComposite.Key key,
    BoundedDoubleState.Details details)
ActionOperation createActionOperation(AbstractComposite.Key key,
    AbstractComposite.Action action)
<T> Cascade<T> createCascadeWithInternalBlank(AbstractComposite.Key key,
    Class<T> cls, AbstractComposite.CascadeCustomizer<T> customizer)
<T> CustomItemState<T> createCustomItemState(AbstractComposite.Key key,
    ItemCodec<T> itemCodec, T initialValue,
    AbstractComposite.ItemStateCustomizer<T> customizer)
<T> SingleSelectListState<T, ListData<T>> createGenericListState(
    AbstractComposite.Key key, ListData<T> data, int selectionIndex)
// ... and enum/refreshable/mutable/tab variants

// Containment check — iterates 13 of 15 maps (excludes mapKeyToStringValue
// since PlainStringValue is not a Model, and mapKeyToSingleSelectListState
// which was omitted in the original code)
boolean contains(Model model)

// Localization — localizes string values and sidekick labels
void localize(AbstractComposite<?> composite)
```

**Construction:** Receives and stores a reference to the owning
`AbstractComposite<?>`. Accesses the composite's `findLocalizedText()`,
`modifyLocalizedText()`, and `getClassUsedForLocalization()` via same-package
visibility.

### CompositeTabManager

**Location:** `core/croquet/src/main/java/org/lgna/croquet/CompositeTabManager.java`

Owns tab-related inner classes and the tab activation/deactivation iteration
loops.

**Inner classes moved here:**
- `InternalTabState<T extends SimpleTabComposite<?>>`
- `InternalSplitComposite`
- `InternalCardOwnerComposite`

**State moved here:**
- `registeredTabStates` (`Set<TabState>`)
- Read access to `mapKeyToTabState` (owned by `CompositeResourceManager`,
  exposed via accessor)

**Key methods:**

```java
// Tab state registration (delegated from AbstractComposite)
void registerTabState(TabState<?, ?> tabState)
void unregisterTabState(TabState<?, ?> tabState)

// Tab activation iteration — called by AbstractComposite.handlePreActivation()
void activateAllTabs()

// Tab deactivation iteration — called by AbstractComposite.handlePostDeactivation()
void deactivateAllTabs()

// Tab state factory — creates InternalTabState instances
<C extends SimpleTabComposite<?>> ImmutableDataTabState<C> createImmutableTabState(
    AbstractComposite.Key key, int selectionIndex, Class<C> cls, C... tabComposites)

// Split/Card factories — return instances, NOT registered here.
// AC wrappers call registerSubComposite() when needed.
SplitComposite createSplitComposite(Composite<?> leading, Composite<?> trailing,
    boolean isHorizontal, double resizeWeight)
CardOwnerComposite createCardOwnerComposite(Composite<?>... cards)
```

**Note:** The original has two card owner creation methods:
`createAndRegisterCardOwnerComposite` (registers with subComposites) and
`createCardOwnerCompositeButDoNotRegister` (does not). Both stay as AC wrappers;
both delegate construction to `TabManager.createCardOwnerComposite()`, but only
the first calls `this.registerSubComposite()`. Same pattern for the
`createHorizontalSplitComposite`/`createVerticalSplitComposite` wrappers.

### CompositeViewLifecycle\<V extends CompositeView\<?, ?\>\>

**Location:** `core/croquet/src/main/java/org/lgna/croquet/CompositeViewLifecycle.java`

Manages the view instance, scroll pane wrapper, and card identity.

**Fields moved here:**
- `cardId` (`UUID`) — lazy, created on first `getOrCreateCardId()` call
- `view` (`V`) — lazy, created on first `getOrCreateView()` call
- `scrollPane` (`ScrollPane`) — **eager**, passed at construction time
  (created via `AC.createScrollPaneIfDesired()` in the AC constructor)

**Key methods:**

```java
// Lazy view initialization — NOT synchronized here; the calling
// AbstractComposite.getView() holds the synchronized(this) lock
V getOrCreateView(Supplier<V> viewFactory)

// Card ID lazy init — NOT synchronized here; caller holds lock
UUID getOrCreateCardId()

// View access without creation
V peekView()

// Scroll pane access
ScrollPane getScrollPaneIfItExists()

// Root component resolution (scrollPane ?? view)
SwingComponentView<?> getRootComponent()

// View release
void releaseView()
```

**Important:** This class does **not** hold any synchronization monitors. The
`synchronized` keyword remains on `AbstractComposite.getView()` and
`AbstractComposite.getCardId()`, using `this` (the composite instance) as the
monitor. `CompositeViewLifecycle` methods are only called from within those
synchronized blocks.

---

## Delegation Patterns

### Factory Method Delegation

Subclasses continue calling `protected` factory methods on `AbstractComposite`.
Each wrapper is a one-liner:

```java
// In AbstractComposite — before extraction (representative)
protected BooleanState createBooleanState(String keyText, boolean initialValue) {
    Key key = this.createKey(keyText);
    InternalBooleanState rv = new InternalBooleanState(initialValue, key);
    this.mapKeyToBooleanState.put(key, rv);
    return rv;
}

// In AbstractComposite — after extraction
protected BooleanState createBooleanState(String keyText, boolean initialValue) {
    return this.resourceManager.createBooleanState(this.createKey(keyText), initialValue);
}
```

### Activation Delegation

```java
// In AbstractComposite
@Override
public void handlePreActivation() {
    this.initializeIfNecessary();
    this.getView().handleCompositePreActivation();
    for (Composite<?> subComposite : this.subComposites) {
        subComposite.handlePreActivation();
    }
    this.tabManager.activateAllTabs();
}

@Override
public void handlePostDeactivation() {
    this.getView().handleCompositePostDeactivation();
    this.tabManager.deactivateAllTabs();
    for (Composite<?> subComposite : this.subComposites) {
        subComposite.handlePostDeactivation();
    }
}
```

### View Lifecycle Delegation

```java
// In AbstractComposite — synchronized monitor stays here
@Override
public final synchronized V getView() {
    return this.viewLifecycle.getOrCreateView(this::createView);
}

@Override
public final synchronized UUID getCardId() {
    return this.viewLifecycle.getOrCreateCardId();
}
```

---

## Subclass Impact

### Zero Breaking Changes

All 14 direct subclasses compile and run without modification:

| Subclass | Notes |
|---|---|
| `AbstractTabComposite<V>` | Calls `super.handlePreActivation()` — unchanged |
| `SimpleComposite<V>` | Uses `createBooleanState()`, `createStringState()` — wrapper signatures unchanged |
| `AbstractSeverityStatusComposite<V>` | Extends `AbstractInternalStringValue` — inner type stays in AC |
| `CardOwnerComposite` | Extends AC directly — no factory usage affected |
| `MenuBarComposite` | Minimal AC usage — unaffected |
| `FocusWindowComposite` | Minimal AC usage — unaffected |
| `PopupCoreComposite<V>` | Uses `createActionOperation()` — wrapper unchanged |
| `ToolPaletteCoreComposite<V>` | Contains `OuterComposite extends AbstractComposite` — unaffected |
| `ToolBarComposite` | Minimal AC usage — unaffected |
| `AbstractSplitComposite<SP>` | Minimal AC usage — unaffected |
| `ListDataComposite<T,V>` | Minimal AC usage — unaffected |
| `MessageDialogComposite<V>` | Uses `createBooleanState()` — wrapper unchanged |
| `SplitComposite` | Via `CardOwnerComposite` chain — unaffected |
| `ColorChooserTabComposite<V>` | Minimal AC usage — unaffected |

### External Inner Type References

These inner types remain on `AbstractComposite` because they are referenced from
outside the class:

- `AbstractComposite.Key` — used in `AbstractSeverityStatusComposite` and
  resource manager constructors
- `AbstractComposite.AbstractInternalStringValue` — extended by
  `AbstractSeverityStatusComposite.InternalSideButton`
- `AbstractComposite.BoundedIntegerDetails` — used by 4+ external files
- `AbstractComposite.BoundedDoubleDetails` — used by 4+ external files
- `AbstractComposite.Action` — implemented by subclass anonymous classes
- `AbstractComposite.ItemStateCustomizer<T>` — implemented by subclass anonymous
  classes
- `AbstractComposite.CascadeCustomizer<T>` — implemented by subclass anonymous
  classes
- `AbstractComposite.InternalActionOperation` — referenced by `Action.perform()`
  signature; class definition stays in AC, constructor widened to package-private
- `AbstractComposite.InternalCustomItemState<T>` — exposed via
  `ItemStateCustomizer` pattern; same treatment
- `AbstractComposite.InternalCascadeWithInternalBlank<T>` — exposed via
  `CascadeCustomizer` pattern; same treatment

> **Visibility change:** The above three inner classes have their constructors
> widened from `private` to package-private so that `CompositeResourceManager`
> (same package) can instantiate them. The classes themselves remain nested inside
> `AbstractComposite` to preserve external type references.

---

## Thread Safety

The original synchronization contract is preserved exactly:

| Method | Lock | Location |
|---|---|---|
| `getView()` | `synchronized(this)` on `AbstractComposite` instance | `AbstractComposite` |
| `getCardId()` | `synchronized(this)` on `AbstractComposite` instance | `AbstractComposite` |
| All other methods | No synchronization | Same as before |

The helper classes (`CompositeViewLifecycle`, `CompositeResourceManager`,
`CompositeTabManager`) contain **no** `synchronized` blocks. They are only
accessed from the owning `AbstractComposite` instance, which holds the lock when
thread safety is required.

---

## Security Considerations

- **InternalPreferenceStringState** moves to `CompositeResourceManager` with its
  `getEncryptionKey()` call unchanged. The encryption key derivation uses
  `PreferenceStringState.getEncryptionKey(String)` with the same UUID-based key
  string.
- All three new classes are **package-private** — no visibility escalation from
  the original `private` inner classes.
- Three inner class constructors (`InternalActionOperation`,
  `InternalCascadeWithInternalBlank`, `InternalCustomItemState`) are widened from
  `private` to package-private. This is strictly within the same package and does
  not affect the public API surface.
- No new network surface, file I/O, or external process spawning is introduced.
- This is a desktop GUI framework; there is no authentication or authorization
  surface affected.

---

## Configuration

No new configuration is introduced. The extraction is purely structural — a
refactoring of existing code into separate files within the same package.

---

## Build & Test

```bash
# Compile and run tests for core/croquet and all upstream dependencies
mvn -pl core/croquet -am -DfailIfNoTests=false -Dcheckstyle.skip test
```

### Characterization Tests

Three new test files validate the extraction preserves behavior:

| Test Class | What It Validates |
|---|---|
| `CompositeViewLifecycleTest` | Lazy view creation, cardId generation, releaseView nulls field, scrollPane wiring |
| `CompositeTabManagerTest` | Tab registration/unregistration, activation iteration order, split/card factory creation |
| `CompositeResourceManagerTest` | Factory methods produce correct types, `contains()` finds all registered models, `localize()` wires text |

These tests use package-private access (same `org.lgna.croquet` package in
`src/test/java`) to construct and exercise the managers in isolation.

---

## Migration Guide

**There is no migration required.** The refactoring is internal to
`AbstractComposite`. All existing subclasses and callers continue to use the same
`protected` and `public` API on `AbstractComposite<V>`.

If you are writing a **new** subclass of `AbstractComposite`:
- Continue calling `createBooleanState()`, `createStringState()`, etc. on `this`
- Continue calling `registerSubComposite()` on `this`
- Continue overriding `handlePreActivation()` and calling `super.handlePreActivation()`
- Do **not** interact with `CompositeResourceManager`, `CompositeTabManager`, or
  `CompositeViewLifecycle` directly — they are package-private implementation
  details

---

## File Inventory

```
core/croquet/src/main/java/org/lgna/croquet/
├── AbstractComposite.java           (~390 lines, down from 1113)
├── CompositeResourceManager.java    (~650 lines, NEW)
├── CompositeTabManager.java         (~170 lines, NEW)
├── CompositeViewLifecycle.java      (~90 lines, NEW)
└── ... (all other files unchanged)

core/croquet/src/test/java/org/lgna/croquet/
├── CompositeResourceManagerTest.java  (NEW)
├── CompositeTabManagerTest.java       (NEW)
└── CompositeViewLifecycleTest.java    (NEW)
```
