# Validate CompositeResourceManager Extraction

Use this guide to verify the extraction of 13 inner state classes and 2
localization methods from `CompositeResourceManager` into `InternalStateTypes`
and `CompositeLocalizationDelegate`.

For the full contract, see the [CompositeResourceManager Extraction
reference](../reference/composite-resource-manager-extraction.md).

## When to use this guide

Use this guide when:

- Reviewing changes that extract inner classes from `CompositeResourceManager`
- Modifying any of the 13 extracted state types in `InternalStateTypes.java`
- Changing the localization delegate or the `localize()` forwarding call
- Adding new state types to `CompositeResourceManager`
- Updating characterization tests after structural changes

## Before you start

Run commands from the repository root:

```bash
git submodule update --init tweedle-lang
test -d tweedle-lang/Grammar
export NODE_OPTIONS=--max-old-space-size=32768
```

## Step 1: Verify compilation

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/croquet -am compile
```

All three files (`CompositeResourceManager.java`, `InternalStateTypes.java`,
`CompositeLocalizationDelegate.java`) must compile without errors.

## Step 2: Verify line count

```bash
wc -l core/croquet/src/main/java/org/lgna/croquet/CompositeResourceManager.java
```

Target: under 500 lines (expected ~225). Before extraction: 649.

## Step 3: Run the characterization and contract tests

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/croquet -am \
  -Dtest=CompositeResourceManagerTest \
  test
```

Expected: 27 tests pass (24 existing + 3 new), 0 failures, 0 errors.

The 3 new characterization tests verify:
- `localize_delegatesToCompositeLocalizationDelegate` — localization still
  works through the delegate boundary
- `factoryMethods_createTypesFromInternalStateTypes` — factory methods return
  correct supertypes from the extracted file
- `contains_worksAfterExtraction` — round-trip `contains()` works across
  file boundaries

## Step 4: Run the full croquet module test suite

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/croquet -am clean test
```

This catches any compilation, linking, or behavioral errors introduced by
the extraction.

## Step 5: Verify new file structure

```bash
ls -la core/croquet/src/main/java/org/lgna/croquet/{CompositeResourceManager,InternalStateTypes,CompositeLocalizationDelegate}.java
```

Expected:

| File | Class visibility |
| --- | --- |
| `CompositeResourceManager.java` | package-private (`class CompositeResourceManager`) |
| `InternalStateTypes.java` | package-private (`final class InternalStateTypes`) |
| `CompositeLocalizationDelegate.java` | package-private (`final class CompositeLocalizationDelegate`) |

## Step 6: Verify no inner class declarations remain in CompositeResourceManager

```bash
grep -c 'private static final class Internal' \
  core/croquet/src/main/java/org/lgna/croquet/CompositeResourceManager.java
```

Expected: 0 matches. All 13 `private static final class Internal*`
declarations must be absent.

## Step 7: Verify all 13 classes exist in InternalStateTypes

```bash
grep 'static final class Internal' \
  core/croquet/src/main/java/org/lgna/croquet/InternalStateTypes.java | wc -l
```

Expected: 13. Each class should be `static final class` with package-private
visibility (no `private` modifier).

## Step 8: Verify localization delegate contains expected methods

```bash
grep -n 'static void localize\|private static void localizeSidekicks\|SIDEKICK_LABEL_EPILOGUE' \
  core/croquet/src/main/java/org/lgna/croquet/CompositeLocalizationDelegate.java
```

Expected: 3 matches — `localize`, `localizeSidekicks`, and the constant.

## Step 9: Verify CompositeResourceManager.localize() delegates

```bash
grep -A 3 'void localize' \
  core/croquet/src/main/java/org/lgna/croquet/CompositeResourceManager.java
```

The method body should be a single call to
`CompositeLocalizationDelegate.localize(composite, this.mapKeyToStringValue, ...)`.
The `SIDEKICK_LABEL_EPILOGUE` constant and `localizeSidekicks` must NOT appear
in `CompositeResourceManager.java`.

## Step 10: Verify no public API changes

```bash
grep 'public class' \
  core/croquet/src/main/java/org/lgna/croquet/{InternalStateTypes,CompositeLocalizationDelegate}.java
```

Expected: 0 matches. Neither new file should contain a `public class`
declaration.

## Troubleshooting

### Compilation fails with "cannot find symbol" for Internal* types

Factory methods in `CompositeResourceManager` reference the 13 types by
simple name. Ensure `InternalStateTypes.java` is in the same package
(`org.lgna.croquet`) and the class names are unchanged.

### Compilation fails with "cannot access private constructor"

Inner class constructors were `private` when they were inner classes. After
extraction to a separate file, they must be package-private (no access
modifier). Verify each constructor in `InternalStateTypes.java` has no
`private` keyword.

### localize() produces different results

Verify that `CompositeLocalizationDelegate.localize()` receives the
`mapKeyToStringValue` map as a named parameter plus all 13 sidekick maps
via varargs in the same order as the original `localizeSidekicks` call.
The original call in `CompositeResourceManager.localize()` passed maps in
this order: `mapKeyToActionOperation`, `mapKeyToBooleanState`,
`mapKeyToPreferenceBooleanState`, `mapKeyToBoundedDoubleState`,
`mapKeyToBoundedIntegerState`, `mapKeyToCascade`, `mapKeyToItemState`,
`mapKeyToImmutableSingleSelectListState`,
`mapKeyToRefreshableSingleSelectListState`,
`mapKeyToMutableSingleSelectListState`, `mapKeyToTabState`,
`mapKeyToPreferenceStringState`, `mapKeyToStringState`.

### Test fails: "contains must return true"

The `containsIndex` identity set in `CompositeResourceManager` must still
receive `add()` calls in each factory method. Verify the factory methods
were not accidentally modified during extraction.

### InternalPreferenceStringState fails to compile

This class calls the inherited static `getEncryptionKey()` from
`PreferenceStringState`. After extraction, the class still extends
`PreferenceStringState` and the call resolves identically. If it fails,
check that the `extends PreferenceStringState` clause is present.

## What this guide does NOT cover

- Behavioral changes to localization output (none expected)
- Changes to `AbstractComposite.java` (none required)
- Upstream Croquet framework compatibility beyond this package
- Checkstyle enforcement (existing rules unchanged)
