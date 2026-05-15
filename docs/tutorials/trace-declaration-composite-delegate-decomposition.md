# Tracing the Declaration Composite Delegate Decomposition

This tutorial walks through the delegate extraction of
`DeclarationLikeSubstanceComposite.java`, explaining why each piece was
extracted, what stays in the coordinator, and how the delegates collaborate
with the composite and its subclasses.

## Starting point

`DeclarationLikeSubstanceComposite` is an abstract base class for all
declaration-like dialog composites in the Alice IDE. It provides the UI state
for declaring variables, parameters, fields, and local variables — including
type selection, name entry, initializer expression, and "is final" toggle.

At 617 lines, the class mixed three concerns: core state management (fields,
constructor, accessors), validation logic (explanation text assembly), and
dialog lifecycle management (listener wiring, initializer caching). The
decomposition separates the latter two into focused delegates.

## The three concerns

### Concern 1: Core state (stays in coordinator)

The heart of the composite is its five state fields:

```java
private final BooleanState isFinalState;
private final CustomItemState<AbstractType> valueComponentTypeState;
private final BooleanState valueIsArrayTypeState;
private final StringState nameState;
private final CustomItemState<Expression> initializerState;
```

These are created conditionally in the constructor based on the `Details`
builder. The `ApplicabilityStatus` enum controls whether each field is
applicable, displayed, or editable. This constructor logic, the `Details`
builder, and all public accessors stay in the coordinator — they define the
composite's identity.

### Concern 2: Validation (extracted to DeclarationValidationDelegate)

Three explanation methods compute human-readable error text:

- `getValueTypeExplanation()` — returns `null` if a type is selected, or a
  localized "must be set" message if not.
- `getNameExplanation()` — validates identifier syntax and availability,
  returning `null` on success or a localized error on failure.
- `getInitializerExplanation()` — checks whether a null initializer is
  acceptable based on type rules and the `isNullAllowedForInitializer()`
  override hook.

The `getStatusPreRejectorCheck()` method orchestrates all three, feeding
their results into `errorStatus.setText()`. This orchestration moves to the
delegate as `computeStatus()`, while the coordinator retains a thin shell:

```java
// In DeclarationLikeSubstanceComposite (after extraction)
@Override
protected Status getStatusPreRejectorCheck() {
    if (validationDelegate.computeStatus(this.errorStatus)) {
        return this.errorStatus;
    } else {
        return IS_GOOD_TO_GO_STATUS;
    }
}
```

The `isNullAllowedForInitializerUnderAnyCircumstances()` method — a private
helper that checks array/primitive/wrapper constraints — also moves to the
delegate. It has no subclass overrides and only depends on `getValueType()`.

### Concern 3: Dialog lifecycle (extracted to DeclarationDialogLifecycleDelegate)

The composite manages a type-to-initializer cache that preserves user-entered
initializer expressions when the user changes the value type:

```java
private final Map<AbstractType<?,?,?>, Expression> mapTypeToInitializer = Maps.newHashMap();
```

When the value type is about to change, `handleValueTypeChanging()` saves the
current initializer keyed by the current type. When the type finishes
changing, `handleValueTypeChanged()` restores any previously saved initializer
for the new type.

Three listener instances drive this:

1. `isArrayValueTypeListener` — reacts to array-type toggle changes
2. `valueComponentTypeListener` — reacts to component type selection changes
3. `initializerListener` — notifies the view when the initializer changes

These listeners, the cache, and the wiring/unwiring logic in
`handlePreShowDialog()` / `handlePostHideDialog()` move to the lifecycle
delegate. The coordinator calls `lifecycleDelegate.wireListeners()` and
`lifecycleDelegate.unwireListeners()` at the same points in the method flow.

## Tracing `handlePreShowDialog` after extraction

Here is the execution flow when a declaration dialog opens, showing what the
coordinator does versus what the delegate does:

```
1. Subclass.handlePreShowDialog(dialog)       // e.g., AddPredeterminedValueTypeManagedFieldComposite
2.   └── super.handlePreShowDialog(dialog)     // DeclarationLikeSubstanceComposite
3.       ├── Reset isFinalState                 // coordinator
4.       ├── Reset initializerState             // coordinator
5.       ├── Reset valueComponentTypeState      // coordinator
6.       ├── Reset valueIsArrayTypeState        // coordinator
7.       ├── Reset nameState                    // coordinator
8.       ├── lifecycleDelegate.wireListeners()  // DELEGATE
9.       │   ├── Add isArrayValueTypeListener   // delegate (if editable)
10.      │   ├── Add valueComponentTypeListener // delegate (if editable)
11.      │   └── Add initializerListener        // delegate (if editable)
12.      ├── lifecycleDelegate.clearTypeToInitializerCache()  // coordinator calls delegate
13.      ├── View.handleInitializerChanged()     // coordinator
14.      └── super.handlePreShowDialog(dialog)   // grandparent
```

The critical invariants are: (a) state reset (steps 3–7) happens *before*
listener wiring (step 8), so listeners don't fire on the programmatic reset;
(b) the cache clear (step 12) happens after all listeners are wired rather
than between the type and initializer listeners as in the original code. This
reordering is safe because no listener fires during the synchronous setup —
user interaction cannot occur until the dialog is shown.

## Tracing `handlePostHideDialog` after extraction

```
1. Subclass.handlePostHideDialog()
2.   └── super.handlePostHideDialog()          // DeclarationLikeSubstanceComposite
3.       ├── super.handlePostHideDialog()       // grandparent (first)
4.       └── lifecycleDelegate.unwireListeners() // DELEGATE
5.           ├── Remove initializerListener      // delegate
6.           ├── Remove isArrayValueTypeListener  // delegate (if editable)
7.           └── Remove valueComponentTypeListener // delegate (if editable)
```

Listener removal follows the same structural grouping as addition:
initializer listener first, then type listeners — matching the original code.
(Within the type listener group, the sub-order is preserved, not reversed.)

## Tracing validation after extraction

When the user changes any input in the dialog, the croquet framework calls
`getStatusPreRejectorCheck()`:

```
1. Framework calls getStatusPreRejectorCheck()
2.   └── DeclarationLikeSubstanceComposite.getStatusPreRejectorCheck()
3.       ├── validationDelegate.computeStatus(errorStatus)  // returns boolean
4.       │   ├── getValueTypeExplanation(composite.getValueType())
5.       │   │   └── composite.findLocalizedText("mustBeSet")           // callback
6.       │   ├── getNameExplanation(composite.getNameState().getValue())
7.       │   │   ├── composite.isNameValid(name)                        // callback
8.       │   │   ├── composite.isNameAvailable(name)                    // callback
9.       │   │   └── composite.findLocalizedText("isNotAValidName")     // callback
10.      │   └── getInitializerExplanation(composite.getInitializerState().getValue())
11.      │       ├── isNullAllowedForInitializerUnderAnyCircumstances()  // delegate-local
12.      │       └── composite.isNullAllowedForInitializer()             // polymorphic callback
13.      └── return errorStatus (if true) or IS_GOOD_TO_GO_STATUS (if false)
```

When `AddParameterComposite` overrides `getStatusPreRejectorCheck()`, it calls
`super.getStatusPreRejectorCheck()` first (step 2), which flows through the
delegate, then performs its own additional parameter name collision check.

## The package-private widening

Four helper methods that were `private` are widened to package-private:

| Method | Why it's needed by delegate |
| --- | --- |
| `isValueComponentTypeEditable()` | Lifecycle delegate checks before wiring type listener |
| `isValueIsArrayTypeEditable()` | Lifecycle delegate checks before wiring array listener |
| `isNameEditable()` | Validation delegate checks before computing name explanation |
| `isInitializerEditable()` | Lifecycle delegate checks before wiring initializer listener |

These are simple null-safe enablement checks. Widening them has no behavioral
effect and is invisible outside the package.

## Test strategy

Three test files verify the extraction:

### DeclarationCompositeStructureTest

A reflection-based characterization test written *before* the extraction. It
verifies structural properties that must hold before and after:

- The composite class declares its five state fields.
- `getStatusPreRejectorCheck()` is a protected method.
- `handlePreShowDialog()` and `handlePostHideDialog()` are protected methods.
- After extraction: the two delegate fields exist and are non-public.

### DeclarationValidationDelegateTest

Unit tests for the validation delegate's explanation methods:

- `getValueTypeExplanation()` returns `null` when a type is present.
- `getValueTypeExplanation()` returns localized text when type is null.
- `getNameExplanation()` returns `null` for valid, available names.
- `getNameExplanation()` returns error text for empty, invalid, or unavailable names.
- `getInitializerExplanation()` respects the null-allowed rules.
- `computeStatus()` returns `false` when all explanations are null (composite returns `IS_GOOD_TO_GO_STATUS`).
- `computeStatus()` returns `true` when any explanation is non-null (composite returns `errorStatus`).

### DeclarationDialogLifecycleDelegateTest

Unit tests for the lifecycle delegate's cache and wiring logic:

- `handleValueTypeChanging()` preserves the current initializer in the cache.
- `handleValueTypeChanged()` restores a cached initializer for the new type.
- `wireListeners()` and `unwireListeners()` are symmetric.
- Cache is cleared at the start of `wireListeners()`.

## Key risks and mitigations

| Risk | Mitigation |
| --- | --- |
| `super` call chain breaks | Coordinator retains `getStatusPreRejectorCheck()`, `handlePreShowDialog()`, `handlePostHideDialog()` as overrides; delegates are called within, not replacing, the methods |
| Listener wiring order changes | `wireListeners()` and `unwireListeners()` preserve the same listener groups; cache clear moved after all wiring but still within the synchronous `handlePreShowDialog()` sequence |
| Cache clear-on-show contract violated | `clearTypeToInitializerCache()` is called by the coordinator immediately after `wireListeners()`. The original code cleared between type and initializer listener additions; the reordering is safe because no listener fires during synchronous dialog setup |
| `isNullAllowedForInitializer()` override missed | Delegate calls `composite.isNullAllowedForInitializer()` which dispatches polymorphically |
| Localized text key drift | Characterization tests assert exact localized text keys |
