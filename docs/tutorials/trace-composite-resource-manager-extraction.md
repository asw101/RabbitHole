# Tutorial: Trace the CompositeResourceManager Extraction

This tutorial walks through the extraction of 13 inner state classes and 2
localization methods from `CompositeResourceManager`, explaining each design
decision and the resulting file boundaries.

For the full reference, see the [CompositeResourceManager Extraction
reference](../reference/composite-resource-manager-extraction.md). For a
validation checklist, see the
[how-to guide](../howto/validate-composite-resource-manager-extraction.md).

## Prerequisites

- Familiarity with the Croquet framework's `AbstractComposite` and its
  `Key`-based state registration pattern
- Repository checked out with `git submodule update --init tweedle-lang`

## 1. Understand the before state

Before extraction, `CompositeResourceManager.java` was 649 lines organized
as:

```
Lines   1–42:   Copyright header
Lines  43–64:   Package declaration and imports
Lines  65–68:   Class Javadoc and declaration
Lines  73–453:  13 private static final inner classes (~381 lines)
Lines 455–474:  15 state maps + containsIndex
Lines 476–496:  Map accessor and registration methods
Lines 498–597:  15 factory methods
Lines 599–603:  contains() method
Lines 605–648:  localize() and localizeSidekicks() with SIDEKICK_LABEL_EPILOGUE
```

The 13 inner classes account for **59%** of the file. They share a nearly
identical structure — each stores an `AbstractComposite.Key`, overrides
three methods for localization routing, and overrides `appendRepr` for
debugging. The only outlier is `InternalCascadeWithInternalBlank`, which
also overrides `createEdit()` and `updateBlankChildren()` to delegate to a
`CascadeCustomizer`.

## 2. Trace the inner class extraction

Open `InternalStateTypes.java`. Observe the structure:

```java
package org.lgna.croquet;

// imports...

final class InternalStateTypes {
    private InternalStateTypes() {} // marker class, no instances
}

// ── Extracted internal state types ──────────────────────────────────

final class InternalStringValue extends AbstractComposite.AbstractInternalStringValue {
    InternalStringValue(AbstractComposite.Key key) {           // was: private
        super(UUID.fromString("142b66a2-..."), key);
    }
}

final class InternalBooleanState extends BooleanState {
    private final AbstractComposite.Key key;

    InternalBooleanState(boolean initialValue, AbstractComposite.Key key) { // was: private
        super(Application.INHERIT_GROUP, UUID.fromString("5053e40f-..."), initialValue);
        this.key = key;
    }

    // getKey(), getClassUsedForLocalization(), getSubKeyForLocalization(), appendRepr()
}

// ... 11 more classes following the same pattern
```

Key observations:

- **Marker class**: `InternalStateTypes` is a `final class` with a private
  constructor — a file anchor documenting the extraction origin. The 13
  extracted types are top-level classes in the same file, not nested inside it.
- **Visibility widening**: Constructor access changed from `private` to
  package-private. This is the only visibility change and it is safe because
  the constructors are only called by `CompositeResourceManager` factory
  methods in the same package.
- **No behavioral changes**: Method bodies are byte-for-byte identical to
  the original inner classes.
- **UUIDs preserved**: Each class retains its original hardcoded UUID,
  ensuring serialization compatibility.

## 3. Trace the InternalCascadeWithInternalBlank outlier

This class has extra overrides that the other 12 classes lack:

```java
final class InternalCascadeWithInternalBlank<T> extends CascadeWithInternalBlank<T> {
    private final AbstractComposite.CascadeCustomizer<T> customizer;
    private final AbstractComposite.Key key;

    // constructor...

    @Override
    protected Edit createEdit(UserActivity userActivity, T[] values) {
        return this.customizer.createEdit(values);
    }

    @Override
    protected List<CascadeBlankChild> updateBlankChildren(
            List<CascadeBlankChild> rv, BlankNode<T> blankNode) {
        this.customizer.appendBlankChildren(rv, blankNode);
        return rv;
    }

    // standard key/localization/appendRepr overrides...
}
```

The `customizer` field delegation pattern is preserved exactly. Neither
`createEdit` nor `updateBlankChildren` accesses any state from
`CompositeResourceManager`, so extraction is straightforward.

## 4. Trace the localization delegate

Open `CompositeLocalizationDelegate.java`:

```java
package org.lgna.croquet;

final class CompositeLocalizationDelegate {
    private CompositeLocalizationDelegate() {}

    static final String SIDEKICK_LABEL_EPILOGUE = ".sidekickLabel";

    @SafeVarargs
    @SuppressWarnings("unchecked")
    static void localize(AbstractComposite<?> composite,
            Map<AbstractComposite.Key, AbstractComposite.AbstractInternalStringValue> stringValues,
            Map<AbstractComposite.Key, ? extends CompletionModel>... sidekickMaps) {
        // localize string values
        for (Map.Entry<...> entry : stringValues.entrySet()) {
            AbstractComposite.AbstractInternalStringValue sv = entry.getValue();
            sv.setText(composite.modifyLocalizedText(sv,
                composite.findLocalizedText(entry.getKey().getLocalizationKey())));
        }
        // localize sidekick labels
        localizeSidekicks(composite, sidekickMaps);
    }

    @SafeVarargs
    static void localizeSidekicks(AbstractComposite<?> composite,
            Map<AbstractComposite.Key, ? extends CompletionModel>... maps) {
        // ... identical to original, using composite.findLocalizedText()
    }
}
```

Design decisions:

- **Static methods, not instance methods**: The delegate is stateless. It
  receives maps as parameters at call time rather than storing them. This
  avoids duplicate references and makes the dependency explicit.
- **`SIDEKICK_LABEL_EPILOGUE` constant moved here**: It is only used by
  `localizeSidekicks`. Keeping it with its sole consumer improves locality.
- **`localizeSidekicks` is package-private**: This enables direct testing
  from `CompositeLocalizationDelegateTest` while remaining invisible outside
  the `org.lgna.croquet` package.

## 5. Trace the forwarding call in CompositeResourceManager

Open `CompositeResourceManager.java`. Find the `localize()` method:

```java
void localize(AbstractComposite<?> composite) {
    CompositeLocalizationDelegate.localize(composite,
        this.mapKeyToStringValue, this.getSidekickMaps());
}
```

The sidekick maps are cached in a lazily-initialized array by
`getSidekickMaps()`, avoiding allocation on every `localize()` call.

This is the only change to `CompositeResourceManager`'s method signatures.
The method remains `void localize(AbstractComposite<?>)` — callers in
`AbstractComposite` see no difference.

## 6. Trace a factory method

Factory methods in `CompositeResourceManager` reference the extracted
top-level types by simple name (same package, no qualifier needed):

```java
// Before (inner class):
BooleanState createBooleanState(AbstractComposite.Key key, boolean initialValue) {
    InternalBooleanState rv = new InternalBooleanState(initialValue, key);
    this.mapKeyToBooleanState.put(key, rv);
    this.containsIndex.add(rv);
    return rv;
}

// After (extracted top-level type — identical code, different class file):
BooleanState createBooleanState(AbstractComposite.Key key, boolean initialValue) {
    InternalBooleanState rv = new InternalBooleanState(initialValue, key);
    this.mapKeyToBooleanState.put(key, rv);
    this.containsIndex.add(rv);
    return rv;
}
```

The return type stays `BooleanState` (the public supertype). Callers never
see the internal type — only the factory method body references it.

## 7. Trace the characterization tests

Three new tests in `CompositeResourceManagerTest.java` pin the extraction
boundaries:

### `localize_delegatesToCompositeLocalizationDelegate`

```java
@Test
public void localize_delegatesToCompositeLocalizationDelegate() {
    PlainStringValue sv = composite.doCreateStringValue("greeting");
    // Set up a localization source that returns "Hello"
    // ...
    resourceManager.localize(composite);
    // Assert the text was set through the delegate chain
}
```

This test would fail if:
- The delegate does not receive the `mapKeyToStringValue` map
- The delegate's iteration logic differs from the original

### `factoryMethods_createTypesFromInternalStateTypes`

```java
@Test
public void factoryMethods_createTypesFromInternalStateTypes() {
    assertTrue(composite.doCreateBooleanState("b", true) instanceof BooleanState);
    assertTrue(composite.doCreateStringState("s", "v") instanceof StringState);
    // ... one assertion per factory method
}
```

This test would fail if `InternalStateTypes` classes do not properly
extend their expected supertypes.

### `contains_worksAfterExtraction`

```java
@Test
public void contains_worksAfterExtraction() {
    BooleanState bool = composite.doCreateBooleanState("b", false);
    StringState str = composite.doCreateStringState("s", "v");
    BoundedIntegerState intSt = composite.doCreateBoundedIntegerState("i");
    BoundedDoubleState dblSt = composite.doCreateBoundedDoubleState("d");

    assertTrue(resourceManager.contains(bool));
    assertTrue(resourceManager.contains(str));
    assertTrue(resourceManager.contains(intSt));
    assertTrue(resourceManager.contains(dblSt));
    assertFalse(resourceManager.contains(new StubModel()));
}
```

This test would fail if `containsIndex.add()` calls were accidentally
removed from factory methods during the extraction.

## 8. Verify the InternalPreferenceStringState encryption chain

This is the highest-risk class due to its call to `getEncryptionKey()`:

```java
final class InternalPreferenceStringState extends PreferenceStringState {
    InternalPreferenceStringState(String initialValue, AbstractComposite.Key key,
            BooleanState isStoringPreferenceDesiredState, UUID encryptionId) {
        super(Application.INHERIT_GROUP,
              UUID.fromString("ad98acf0-..."),
              initialValue,
              key.getPreferenceKey(),
              getEncryptionKey(encryptionId != null ? encryptionId.toString() : null));
        // ...
    }
}
```

`getEncryptionKey()` is a `protected static` method inherited from
`PreferenceStringState`. After extraction, `InternalPreferenceStringState`
still extends `PreferenceStringState`, so the inherited method resolves
identically. The call chain is:

```
InternalPreferenceStringState constructor
  → PreferenceStringState.getEncryptionKey(String)  [inherited static]
  → PreferenceStringState super constructor
```

No change in resolution. No change in security boundary.

## 9. Summary of design decisions

| Decision | Rationale |
| --- | --- |
| Marker class `InternalStateTypes` with 13 top-level classes in one file | Reduces file count; classes are tiny and share the same pattern |
| Stateless delegate rather than delegate object | Maps are owned by `CompositeResourceManager`; passing them avoids dual ownership |
| Factory methods stay in `CompositeResourceManager` | They mutate maps and `containsIndex`; moving them would require exposing internal state |
| Package-private visibility for all extracted types | Matches framework-internal convention; no new public API |
| 3 characterization tests, not 13 | Tests verify boundaries (type hierarchy, localization chain, `contains()` identity), not individual class behavior |

## What this tutorial does NOT cover

- How to add new state types (add to `InternalStateTypes`, add factory method
  and map in `CompositeResourceManager`, add map parameter to `localize()`)
- Localization bundle format or property file conventions
- `AbstractComposite` lifecycle beyond the `localize()` call
