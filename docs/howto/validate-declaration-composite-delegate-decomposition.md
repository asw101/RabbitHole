# How to Validate the Declaration Composite Delegate Decomposition

This guide walks through verifying that the `DeclarationLikeSubstanceComposite`
delegate extraction is correct: the coordinator is under 500 lines, delegates
exist and are package-private, all tests pass, and subclass override chains
are preserved.

## Prerequisites

- A checkout or worktree of the branch containing the decomposition.
- The Tweedle grammar submodule initialized:

  ```bash
  git submodule update --init tweedle-lang
  test -d tweedle-lang/Grammar
  ```

- The Node memory preference:

  ```bash
  export NODE_OPTIONS=--max-old-space-size=32768
  ```

## Step 1: Verify line count

```bash
wc -l core/ide/src/main/java/org/alice/ide/ast/declaration/DeclarationLikeSubstanceComposite.java
```

Expected: ≤ 500 lines. The original was 617 lines.

## Step 2: Verify delegates exist

```bash
ls -la core/ide/src/main/java/org/alice/ide/ast/declaration/DeclarationValidationDelegate.java
ls -la core/ide/src/main/java/org/alice/ide/ast/declaration/DeclarationDialogLifecycleDelegate.java
```

Both files should exist.

## Step 3: Verify delegates are package-private

```bash
grep -n '^public class' \
  core/ide/src/main/java/org/alice/ide/ast/declaration/DeclarationValidationDelegate.java \
  core/ide/src/main/java/org/alice/ide/ast/declaration/DeclarationDialogLifecycleDelegate.java
```

Expected: no matches. The delegates should use `class` without `public`.

## Step 4: Verify no public API changes

Check that all existing public and protected method signatures in the
coordinator are unchanged:

```bash
grep -n 'public\|protected' \
  core/ide/src/main/java/org/alice/ide/ast/declaration/DeclarationLikeSubstanceComposite.java \
  | head -60
```

Compare with the original 617-line version. No public or protected method
should have been removed or had its signature changed.

## Step 5: Verify private-to-package-private widening

The four helper methods should no longer have `private`:

```bash
grep -n 'isValueComponentTypeEditable\|isValueIsArrayTypeEditable\|isNameEditable\|isInitializerEditable' \
  core/ide/src/main/java/org/alice/ide/ast/declaration/DeclarationLikeSubstanceComposite.java
```

Expected: each method should appear without `private` keyword (package-private
is the default when no access modifier is present).

## Step 6: Run characterization tests

```bash
NODE_OPTIONS=--max-old-space-size=32768 mvn -pl core/ide -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest="DeclarationCompositeStructureTest" \
  test
```

Expected: all structural assertions pass (delegate field existence, method
presence, visibility checks).

## Step 7: Run delegate unit tests

```bash
NODE_OPTIONS=--max-old-space-size=32768 mvn -pl core/ide -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest="DeclarationValidationDelegateTest,DeclarationDialogLifecycleDelegateTest" \
  test
```

Expected: all delegate logic tests pass.

## Step 8: Run full module test suite

```bash
NODE_OPTIONS=--max-old-space-size=32768 mvn -pl core/ide -am \
  -DfailIfNoTests=false \
  test
```

Expected: zero test failures. The decomposition should not break any existing
test in the `core/ide` module.

## Step 9: Verify subclass compilation

The following subclasses must compile and their override patterns must be
preserved:

```bash
grep -rn 'super\.getStatusPreRejectorCheck\|super\.handlePreShowDialog\|super\.handlePostHideDialog' \
  core/ide/src/main/java/org/alice/ide/ast/declaration/
```

Expected: existing `super` calls in `AddParameterComposite`,
`AddPredeterminedValueTypeManagedFieldComposite`,
and `InsertLocalDeclarationStatementComposite` are unchanged. Note that
`AddUnmanagedFieldComposite` overrides `isNullAllowedForInitializer()`, not
the dialog lifecycle methods.

## Troubleshooting

**Missing Tweedle parser classes**: Check the submodule:

```bash
git submodule status tweedle-lang
test -d tweedle-lang/Grammar
```

If the submodule is missing, run `git submodule update --init tweedle-lang`.

**Test fails with "No tests were executed"**: Ensure `-DfailIfNoTests=false`
is passed. The test class names must match exactly.

**Compilation error in subclass**: If a subclass fails to compile after the
extraction, verify that the widened helper methods (`isNameEditable()`, etc.)
are visible to the delegate. They should be package-private (no access
modifier), not `private`.
