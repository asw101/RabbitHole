# Declaration Composite Delegate Decomposition

This reference describes the decomposition of
`DeclarationLikeSubstanceComposite.java` (617 lines) into a thin coordinator
plus two package-private delegate classes: `DeclarationValidationDelegate` and
`DeclarationDialogLifecycleDelegate`. The coordinator retains ~460 lines.

All classes live in `org.alice.ide.ast.declaration`. The delegates are
package-private with no public constructors. They are instantiated by
`DeclarationLikeSubstanceComposite` and receive a back-reference to it for
shared state access.

Issue #637 decomposes the class without changing observable behavior. All
existing validation logic, dialog lifecycle management, listener wiring, and
initializer caching behavior is preserved identically.

## Contents

- [Motivation](#motivation)
- [Architecture](#architecture)
- [Class responsibilities](#class-responsibilities)
  - [DeclarationLikeSubstanceComposite (coordinator)](#declarationlikesubstancecomposite-coordinator)
  - [DeclarationValidationDelegate](#declarationvalidationdelegate)
  - [DeclarationDialogLifecycleDelegate](#declarationdialoglifecycledelegate)
- [Public API](#public-api)
- [Package-private collaboration](#package-private-collaboration)
- [State access pattern](#state-access-pattern)
- [Subclass override preservation](#subclass-override-preservation)
- [Error handling contract](#error-handling-contract)
- [Configuration](#configuration)
- [Validation](#validation)
- [Acceptance criteria](#acceptance-criteria)
- [Claim boundaries](#claim-boundaries)

## Motivation

The original `DeclarationLikeSubstanceComposite.java` contained 617 lines
mixing three distinct concerns:

1. **Core composite state** — field declarations (`isFinalState`,
   `valueComponentTypeState`, `valueIsArrayTypeState`, `nameState`,
   `initializerState`), the `Details` builder, `ApplicabilityStatus` enum,
   `InitializerCustomizer` and `ValueComponentTypeCustomizer` inner classes,
   state accessor methods, and the constructor.
2. **Validation logic** — `getValueTypeExplanation()`,
   `getNameExplanation()`, `getInitializerExplanation()`,
   `isNullAllowedForInitializerUnderAnyCircumstances()`, and the
   `getStatusPreRejectorCheck()` orchestration that assembles error text from
   the three explanation methods.
3. **Dialog lifecycle management** — the `mapTypeToInitializer` cache,
   three listener fields (`isArrayValueTypeListener`,
   `valueComponentTypeListener`, `initializerListener`),
   `handleValueTypeChanging()`, `handleValueTypeChanged()`, and the
   listener wiring/unwiring logic in `handlePreShowDialog()` /
   `handlePostHideDialog()`.

This mix made the class difficult to navigate and extend. Each delegate can
now be understood and modified independently.

## Architecture

```text
DeclarationLikeSubstanceComposite<N> (~460 lines, coordinator)
├── DeclarationValidationDelegate (package-private, ~95 lines)
│   └── Explanation methods, null-initializer rules, status computation
└── DeclarationDialogLifecycleDelegate (package-private, ~130 lines)
    └── Listeners, type→initializer cache, dialog wiring/unwiring
```

Both delegate files live alongside `DeclarationLikeSubstanceComposite.java` in
`core/ide/src/main/java/org/alice/ide/ast/declaration/`.

## Class responsibilities

### DeclarationLikeSubstanceComposite (coordinator)

The coordinator retains all responsibilities that define the composite's
identity: state fields, the `Details` builder, the `ApplicabilityStatus` enum,
inner customizer classes, the constructor, public state accessors, abstract
method declarations, and the template methods that subclasses override.

| Concern | Approx lines | Key elements |
| --- | --- | --- |
| License + imports | ~90 | — |
| `ApplicabilityStatus` enum | ~20 | `EDITABLE`, `DISPLAYED`, `APPLICABLE_BUT_NOT_DISPLAYED`, `NOT_APPLICABLE` |
| `Details` builder | ~45 | `isFinal()`, `valueComponentType()`, `valueIsArrayType()`, `name()`, `initializer()` |
| State fields + constructor | ~50 | `isFinalState`, `valueComponentTypeState`, `valueIsArrayTypeState`, `nameState`, `initializerState`, `errorStatus`, `details` |
| `ValueComponentTypeCustomizer` inner class | ~48 | Cascade blank children for type selection |
| `InitializerCustomizer` inner class | ~26 | Expression cascade for initializer |
| Public state accessors | ~60 | `getIsFinalState()`, `getValueComponentTypeState()`, `getValueIsArrayTypeState()`, `getNameState()`, `getInitializerState()`, `isValueComponentTypeDisplayed()`, `isValueIsArrayTypeStateDisplayed()`, `isInitializerDisplayed()`, `getDeclaringType()`, `getValueComponentType()`, `getValueType()`, `getDeclarationLikeSubstanceName()`, `getInitializer()` |
| `isNameValid()`, `isNameAvailable()` | ~5 | Name validation helpers (abstract + final) |
| `isNullAllowedForInitializer()` | ~3 | Override hook (returns `false` by default; subclasses override) |
| Editable-state helpers | ~15 | `isValueComponentTypeEditable()`, `isValueIsArrayTypeEditable()`, `isNameEditable()`, `isInitializerEditable()` — widened from `private` to package-private |
| Initial-value accessors | ~20 | `getIsFinalInitialValue()`, `getValueComponentTypeInitialValue()`, `getNameInitialValue()`, `getValueIsArrayTypeInitialValue()`, `getInitializerInitialValue()` |
| Delegate fields + init | ~5 | 2 delegate fields, instantiated in constructor |
| `getStatusPreRejectorCheck()` | ~10 | Thin shell delegating to `validationDelegate.computeStatus()` |
| `handlePreShowDialog()` | ~20 | State reset + delegates to `lifecycleDelegate.wireListeners()` |
| `handlePostHideDialog()` | ~5 | Delegates to `lifecycleDelegate.unwireListeners()` |

**Delegate fields:**

```java
/* package-private */ final DeclarationValidationDelegate validationDelegate;
/* package-private */ final DeclarationDialogLifecycleDelegate lifecycleDelegate;
```

**Constructor wiring (end of existing constructor):**

```java
this.validationDelegate = new DeclarationValidationDelegate(this);
this.lifecycleDelegate = new DeclarationDialogLifecycleDelegate(this);
```

### DeclarationValidationDelegate

Owns the explanation-text computation and the `computeStatus()` method that
assembles the final validation status from value-type, name, and initializer
explanations.

> **Note on `protected` explanation methods:** The three explanation methods
> (`getValueTypeExplanation`, `getNameExplanation`, `getInitializerExplanation`)
> are `protected` in the original code. No subclass overrides any of them — this
> was verified by grep across the entire `declaration/` package. Moving them to
> the delegate drops `protected` visibility, which is safe because there are no
> overrides to preserve. The private helper
> `isNullAllowedForInitializerUnderAnyCircumstances()` also has no subclass
> overrides (it is `private`, so it cannot be overridden).

| Responsibility | Methods |
| --- | --- |
| Value type explanation | `getValueTypeExplanation(AbstractType<?,?,?>)` |
| Name explanation | `getNameExplanation(String)` |
| Initializer explanation | `getInitializerExplanation(Expression)` |
| Null-initializer rule | `isNullAllowedForInitializerUnderAnyCircumstances()` |
| Status assembly | `computeStatus(ErrorStatus)` |

**Constructor:**

```java
DeclarationValidationDelegate(DeclarationLikeSubstanceComposite<?> composite) {
    assert composite != null;
    this.composite = composite;
}
```

**`computeStatus()` contract:**

```java
boolean computeStatus(ErrorStatus errorStatus) {
    final String valueTypeText;
    if (composite.getValueComponentTypeState() != null) {
        valueTypeText = getValueTypeExplanation(composite.getValueType());
    } else {
        valueTypeText = null;
    }
    final String nameText;
    if (composite.isNameEditable()) {
        nameText = getNameExplanation(composite.getNameState().getValue());
    } else {
        nameText = null;
    }
    final String initializerText;
    if (composite.getInitializerState() != null) {
        initializerText = getInitializerExplanation(
            composite.getInitializerState().getValue());
    } else {
        initializerText = null;
    }
    return errorStatus.setText(valueTypeText, nameText, initializerText);
}
```

The delegate returns `boolean` — `true` when the error status has text (i.e., there
is a validation error). The composite's `getStatusPreRejectorCheck()` uses this to
decide between returning `errorStatus` or `IS_GOOD_TO_GO_STATUS`, keeping the
status-constant knowledge in the composite rather than the delegate.

The explanation methods call back to the composite via the stored reference to
access localized text (`composite.findLocalizedText()`), state labels
(`composite.getValueComponentTypeState().getSidekickLabel()`), and override
hooks (`composite.isNullAllowedForInitializer()`).

### DeclarationDialogLifecycleDelegate

Owns the listener instances, the type-to-initializer cache, and the
wiring/unwiring logic that was previously embedded in
`handlePreShowDialog()` / `handlePostHideDialog()`.

| Responsibility | Methods / Fields |
| --- | --- |
| Type change listeners | `isArrayValueTypeListener` field, `valueComponentTypeListener` field |
| Initializer listener | `initializerListener` field |
| Initializer cache | `mapTypeToInitializer` field |
| Cache preservation | `handleValueTypeChanging()` |
| Cache restoration | `handleValueTypeChanged()` |
| Dialog-show wiring | `wireListeners()` |
| Dialog-hide unwiring | `unwireListeners()` |

**Constructor:**

```java
DeclarationDialogLifecycleDelegate(DeclarationLikeSubstanceComposite<?> composite) {
    assert composite != null;
    this.composite = composite;
}
```

**Listener definitions** are moved verbatim. They call back to the composite
for state access:

```java
private final State.ValueListener<Boolean> isArrayValueTypeListener =
    new State.ValueListener<Boolean>() {
        @Override
        public void changing(State<Boolean> state, Boolean prev, Boolean next) {
            handleValueTypeChanging();
        }
        @Override
        public void changed(State<Boolean> state, Boolean prev, Boolean next) {
            handleValueTypeChanged();
        }
    };
```

**`wireListeners()` contract:**

Called by the composite's `handlePreShowDialog()` after state reset. Adds
value-type listeners if the component type or initializer is editable, then
adds the initializer listener if the initializer is editable. The coordinator
calls `clearTypeToInitializerCache()` separately after `wireListeners()`. In
the original code the cache clear sat between the type listeners and the
initializer listener; the extracted version groups all listener wiring together
and clears the cache afterward. This reordering is safe because no listener
can fire during the synchronous `handlePreShowDialog()` sequence — state resets
are already complete and no user interaction occurs until the dialog is shown.

**`unwireListeners()` contract:**

Called by the composite's `handlePostHideDialog()` *after* `super` — the
grandparent's `handlePostHideDialog()` executes first, then listener removal
happens. Listeners are removed in the same structural grouping as addition:
initializer listener first, then type listeners (matching the original code at
lines 604–616).

## Public API

The public API of `DeclarationLikeSubstanceComposite` is unchanged. No public
methods are added, removed, or have their signatures modified. The delegates
are package-private and invisible to subclasses and external callers.

The following subclasses continue to work identically:

| Subclass | Overrides affected | Impact |
| --- | --- | --- |
| `AddParameterComposite` | `getStatusPreRejectorCheck()` | Calls `super.getStatusPreRejectorCheck()` which now delegates internally; no change to override behavior |
| `AddPredeterminedValueTypeManagedFieldComposite` | `handlePreShowDialog()`, `handlePostHideDialog()` | Calls `super.handlePreShowDialog()` / `super.handlePostHideDialog()` which now delegate internally; no change |
| `AddUnmanagedFieldComposite` | `isNullAllowedForInitializer()` | Override stays in composite hierarchy; delegate reads it via polymorphic callback |
| `InsertLocalDeclarationStatementComposite` | `isNullAllowedForInitializer()` | Override stays in composite hierarchy; delegate reads it via polymorphic callback |

## Package-private collaboration

The delegates access composite methods via package-private visibility. The
following methods are widened from `private` to package-private:

| Method | Used by |
| --- | --- |
| `isValueComponentTypeEditable()` | `DeclarationDialogLifecycleDelegate` |
| `isValueIsArrayTypeEditable()` | `DeclarationDialogLifecycleDelegate` |
| `isNameEditable()` | `DeclarationValidationDelegate` |
| `isInitializerEditable()` | `DeclarationDialogLifecycleDelegate` |

These four methods are simple null-safe boolean checks on state enablement.
Widening them from `private` to package-private has no observable effect since
the enclosing package already contains only trusted Alice IDE classes.

The delegates also use public methods that were already available:
`getValueComponentTypeState()`, `getValueIsArrayTypeState()`,
`getNameState()`, `getInitializerState()`, `getValueType()`,
`getValueComponentType()`, `getInitializer()`, `getView()`,
`isNullAllowedForInitializer()`, `isNameValid()`, `isNameAvailable()`, and
`findLocalizedText()`.

No interfaces or inheritance are introduced. All collaboration uses direct
method calls within the same package.

## State access pattern

Each delegate stores a `final DeclarationLikeSubstanceComposite<?>` reference
set at construction. Delegates access mutable state through the composite's
accessors, never by caching field values:

```java
// Correct — reads current state
AbstractType<?,?,?> type = composite.getValueType();
Expression init = composite.getInitializer();

// Wrong — would miss mutations between dialog show/hide cycles
// this.cachedType = composite.getValueType();  // stale after re-show
```

This pattern is required because the composite's state fields are reset in
`handlePreShowDialog()` before each dialog display cycle.

## Subclass override preservation

Three subclass patterns are preserved without modification:

### Pattern 1: `super.getStatusPreRejectorCheck()` chain

`AddParameterComposite` overrides `getStatusPreRejectorCheck()` and calls
`super.getStatusPreRejectorCheck()` first. The coordinator's implementation
now delegates to `validationDelegate.computeStatus()` internally, but the
`super` call still works identically:

```java
// AddParameterComposite (unchanged)
@Override
protected Status getStatusPreRejectorCheck() {
    Status rv = super.getStatusPreRejectorCheck();  // calls coordinator → delegate
    if (rv == IS_GOOD_TO_GO_STATUS) {
        // additional parameter-specific checks
    }
    return rv;
}
```

### Pattern 2: `super.handlePreShowDialog()` / `super.handlePostHideDialog()` call ordering

`AddPredeterminedValueTypeManagedFieldComposite` overrides both
`handlePreShowDialog()` and `handlePostHideDialog()`, calling their `super`
methods. The coordinator's methods now delegate listener wiring/unwiring to
`lifecycleDelegate.wireListeners()` / `lifecycleDelegate.unwireListeners()` at
exactly the same points in the methods, preserving the ordering contract:

1. Subclass pre-work (before `super` call)
2. Coordinator resets state values
3. `lifecycleDelegate.wireListeners()` adds listeners and clears cache
4. `super.handlePreShowDialog(dialog)` calls the grandparent

For `handlePostHideDialog()`, the coordinator calls grandparent first, then
unwires listeners — matching the original code order:

1. Subclass calls `super.handlePostHideDialog()`
2. Coordinator calls `super.handlePostHideDialog()` (grandparent)
3. `lifecycleDelegate.unwireListeners()` removes listeners
4. Returns to subclass post-work

### Pattern 3: `isNullAllowedForInitializer()` callback

`InsertLocalDeclarationStatementComposite` and `AddUnmanagedFieldComposite`
override `isNullAllowedForInitializer()` to return configuration-dependent
`true`. The validation delegate calls
`composite.isNullAllowedForInitializer()` which dispatches polymorphically
through the standard Java override mechanism. No change to the override
behavior.

## Error handling contract

All existing error handling is preserved verbatim:

- `errorStatus.setText()` receives the same three explanation strings in the
  same order (value-type, name, initializer).
- `findLocalizedText()` keys are unchanged: `"mustBeSet"`, `"isNotAvailable"`,
  `"isNotAValidName"`, `"isNotValid"`.
- The original `replaceAll()` calls with `Matcher.quoteReplacement()` were
  simplified to `replace()`. The substitution tokens (`</type/>`, `</name/>`)
  are literal strings, not regex patterns, so `replace()` is equivalent and
  `Matcher.quoteReplacement()` is no longer needed.

No new exceptions are introduced.

## Configuration

No configuration changes. The decomposition is purely internal. No new system
properties, environment variables, or resource files are introduced.

From a fresh checkout or worktree, initialize the Tweedle grammar submodule
before Maven validation:

```bash
git submodule update --init tweedle-lang
test -d tweedle-lang/Grammar
```

The saved Node memory preference applies:

```bash
export NODE_OPTIONS=--max-old-space-size=32768
```

## Validation

Run the focused `core/ide` module tests:

```bash
NODE_OPTIONS=--max-old-space-size=32768 git submodule update --init tweedle-lang
NODE_OPTIONS=--max-old-space-size=32768 mvn -pl core/ide -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  test
```

Run the characterization and delegate tests specifically:

```bash
NODE_OPTIONS=--max-old-space-size=32768 mvn -pl core/ide -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest="DeclarationCompositeStructureTest,DeclarationValidationDelegateTest,DeclarationDialogLifecycleDelegateTest" \
  test
```

Both commands must pass with zero failures.

## Acceptance criteria

| Criterion | Verification |
| --- | --- |
| `DeclarationLikeSubstanceComposite.java` ≤ 500 lines | `wc -l DeclarationLikeSubstanceComposite.java` |
| Two new delegate classes created | `DeclarationValidationDelegate.java` and `DeclarationDialogLifecycleDelegate.java` exist in `core/ide/src/main/java/org/alice/ide/ast/declaration/` |
| All delegates are package-private | No `public` class keyword on delegates |
| No public API changes | `DeclarationLikeSubstanceComposite` public/protected method signatures unchanged |
| 4 private methods widened to package-private | `isValueComponentTypeEditable()`, `isValueIsArrayTypeEditable()`, `isNameEditable()`, `isInitializerEditable()` |
| `mvn -pl core/ide -am test` passes | Zero test failures |
| Characterization tests written before extraction | Tests committed before extraction commits |
| Subclass `super` call chains work | `AddParameterComposite.getStatusPreRejectorCheck()` continues to delegate correctly |
| Listener symmetry preserved | `wireListeners()` and `unwireListeners()` add/remove the same listeners in matching structural grouping |
| `protected` explanation methods safe to move | `getValueTypeExplanation`, `getNameExplanation`, `getInitializerExplanation` have no subclass overrides |

## Claim boundaries

This decomposition proves:

- The 617-line `DeclarationLikeSubstanceComposite` can be split into a
  coordinator plus two focused delegates without changing observable behavior.
- All existing `core/ide` tests pass identically.
- Subclass override chains (`getStatusPreRejectorCheck`, `handlePreShowDialog`,
  `handlePostHideDialog`, `isNullAllowedForInitializer`) are preserved.
- Listener wiring/unwiring symmetry is preserved.
- Validation error messages and localized text keys are preserved.

This decomposition does **not** prove:

| Non-claim | Reason |
| --- | --- |
| New validation capabilities | No new validation rules are added. |
| Performance improvement | Decomposition is structural, not algorithmic. |
| Thread safety | The composite was not thread-safe before; delegates do not change this. |
| Public API expansion | No new public methods or classes are introduced. |
| View changes | `DeclarationLikeSubstanceView` is not modified. |
| Subclass changes | No subclass source files are modified. |

Adjacent claims are owned by their own documents:

| Claim | Document |
| --- | --- |
| Graphics2D delegate decomposition | [Graphics2D Delegate Decomposition](./graphics2d-delegate-decomposition.md) |
| Decoder delegate decomposition | [Decoder Delegate Decomposition](./decoder-delegate-decomposition.md) |
| Encoder delegate decomposition | [Encoder Delegate Decomposition](./encoder-delegate-decomposition.md) |
