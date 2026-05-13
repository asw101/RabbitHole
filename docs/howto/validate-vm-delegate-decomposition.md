# Validate the VM Delegate Decomposition

Use this guide to verify the extraction of `VmExpressionEvaluator` and
`VmStatementExecutor` from `VirtualMachine.java`. This guide covers compilation,
API contract tests, line count verification, delegate wiring, and the full
`core/ast` test suite.

For the full contract, see the [VM Delegate Decomposition
reference](../reference/vm-delegate-decomposition.md).

## When to use this guide

Use this guide for changes near:

```text
core/ast/src/main/java/org/lgna/project/virtualmachine/VirtualMachine.java
core/ast/src/main/java/org/lgna/project/virtualmachine/VmExpressionEvaluator.java
core/ast/src/main/java/org/lgna/project/virtualmachine/VmStatementExecutor.java
core/ast/src/main/java/org/lgna/project/virtualmachine/ReleaseVirtualMachine.java
```

Also run these checks when changing:

- Any `evaluate*` or `execute*` method in either delegate;
- The `evaluate(Expression)` or `execute(Statement)` dispatch switches;
- Field visibility on `VirtualMachine` (`virtualMachineListeners`, `isStopped`,
  `mapAbstractClsToAdapterCls`, `checkNotNull`);
- `evaluateArguments` or its delegation wrapper on `VirtualMachine`;
- Listener dispatch in either delegate;
- Cross-delegate calls (executor calling evaluator or vice versa).

Do not use this guide for desktop UI automation, visible rendering, Save
workflows, grading, or broad Tweedle decode coverage. Those claims require their
own evidence lanes.

## Before you start

Run commands from the repository root:

```bash
git submodule update --init tweedle-lang
test -d tweedle-lang/Grammar
export NODE_OPTIONS=--max-old-space-size=32768
```

## Step 1: Verify file existence

```bash
ls -la core/ast/src/main/java/org/lgna/project/virtualmachine/VmExpressionEvaluator.java
ls -la core/ast/src/main/java/org/lgna/project/virtualmachine/VmStatementExecutor.java
ls -la core/ast/src/test/java/org/lgna/project/virtualmachine/VmContractTest.java
```

Expected outcome: all three files exist.

## Step 2: Verify line counts

```bash
wc -l core/ast/src/main/java/org/lgna/project/virtualmachine/VirtualMachine.java
wc -l core/ast/src/main/java/org/lgna/project/virtualmachine/VmExpressionEvaluator.java
wc -l core/ast/src/main/java/org/lgna/project/virtualmachine/VmStatementExecutor.java
```

Expected outcome:

| File | Expected lines |
| --- | --- |
| `VirtualMachine.java` | < 500 (~470) |
| `VmExpressionEvaluator.java` | ~400 |
| `VmStatementExecutor.java` | ~340 |

## Step 3: Verify delegate visibility

```bash
grep -n 'class VmExpressionEvaluator' \
  core/ast/src/main/java/org/lgna/project/virtualmachine/VmExpressionEvaluator.java
grep -n 'class VmStatementExecutor' \
  core/ast/src/main/java/org/lgna/project/virtualmachine/VmStatementExecutor.java
```

Expected output:

```text
final class VmExpressionEvaluator {
final class VmStatementExecutor {
```

Both must be package-private (no `public` modifier) and `final`.

## Step 4: Verify delegation wiring

```bash
grep -n 'expressionEvaluator\|statementExecutor' \
  core/ast/src/main/java/org/lgna/project/virtualmachine/VirtualMachine.java
```

Expected output includes:

```text
final VmExpressionEvaluator expressionEvaluator = new VmExpressionEvaluator(this);
final VmStatementExecutor statementExecutor = new VmStatementExecutor(this);
```

And delegation wrappers:

```text
return expressionEvaluator.evaluate(expression);
statementExecutor.execute(statement);
return expressionEvaluator.evaluateArguments(...);
```

## Step 5: Verify field visibility widening

```bash
grep -n 'virtualMachineListeners\|boolean isStopped\|mapAbstractClsToAdapterCls\|void checkNotNull' \
  core/ast/src/main/java/org/lgna/project/virtualmachine/VirtualMachine.java
```

Expected: none of these lines contain `private`. They should be package-private
(no explicit modifier or have no `private`/`protected`/`public` keyword).

## Step 6: Run the contract test

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/ast -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest=VmContractTest \
  test
```

Expected outcome: all test methods pass. The contract test verifies:

- 19 public methods exist with correct parameter types and return types
- 16 abstract methods exist with correct parameter types and return types
- `VirtualMachine` is abstract
- `ReleaseVirtualMachine` extends `VirtualMachine`

## Step 7: Run all characterization tests

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/ast -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest='VmContractTest,VmExpressionEvaluationCharacterizationTest,VmStatementExecutionCharacterizationTest,VmFieldAccessCharacterizationTest,VmErrorHandlingCharacterizationTest,VmStoryApiDispatchCharacterizationTest' \
  test
```

Expected outcome: all test methods pass.

## Step 8: Run the full module test suite

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/ast -am -DfailIfNoTests=false -Dcheckstyle.skip test
```

Expected outcome: all tests in `core/ast` pass, including:

- `VmContractTest`
- `VmExpressionEvaluationCharacterizationTest`
- `VmStatementExecutionCharacterizationTest`
- `VmFieldAccessCharacterizationTest`
- `VmErrorHandlingCharacterizationTest`
- `VmStoryApiDispatchCharacterizationTest`
- `SilverThreadVirtualMachineExecutionTest`
- `VirtualMachineHeadlessRuntimeEventTest`

## Step 9: Verify evaluateArguments wrapper

```bash
grep -n 'evaluateArguments' \
  core/ast/src/main/java/org/lgna/project/virtualmachine/VirtualMachine.java
```

Expected: `evaluateArguments` exists as a `public` method on `VirtualMachine`
that delegates to `expressionEvaluator.evaluateArguments(...)`. This method must
remain public because it is called from `InstanceCreation.evaluate(VirtualMachine)`
in the AST package.

## Review the results

### Contract test assertions

| Assertion | Meaning |
| --- | --- |
| Public method exists | Method with exact name, parameter types, and return type is declared on `VirtualMachine` |
| Abstract method exists | Method is declared abstract with correct signature |
| Class is abstract | `VirtualMachine` cannot be directly instantiated |
| Subclass relationship | `ReleaseVirtualMachine.class.getSuperclass() == VirtualMachine.class` |

### Behavioral preservation

| Assertion category | Meaning |
| --- | --- |
| Expression evaluation tests pass | `evaluate()` dispatch produces identical results through delegate |
| Statement execution tests pass | `execute()` dispatch produces identical event sequences through delegate |
| Field access tests pass | Field read/write round-trips are unaffected |
| Error handling tests pass | Exception types and messages are preserved |
| Story API dispatch tests pass | Adapter registration and instance creation work through delegate |

## Troubleshooting

### Missing Tweedle grammar classes

```bash
git submodule status tweedle-lang
test -d tweedle-lang/Grammar
```

If the submodule is uninitialized, Maven fails with missing parser classes.
Always initialize before running characterization.

### Compilation error in delegate: cannot access private field

If a delegate class cannot access a `VirtualMachine` field, verify the field
visibility was widened to package-private. The four fields that must be
package-private are:

1. `virtualMachineListeners` (List)
2. `isStopped` (boolean)
3. `mapAbstractClsToAdapterCls` (Map)
4. `checkNotNull` (method)

### VmContractTest fails after adding a new public method

If you add a new public method to `VirtualMachine`, update `VmContractTest` to
include the new method signature. The contract test is the source of truth for
the API surface.

### Cross-delegate call fails: NPE on vm.expressionEvaluator

Both delegates are initialized as final fields on `VirtualMachine`. If either is
null, verify the initialization order — both must be initialized before any
method call (e.g., in a field initializer, not in a constructor that calls
`super` first).

## What this guide does NOT cover

- Desktop runtime execution or GUI toolkit startup
- Full world playback or visible scene rendering
- Save, Save As, or project recovery workflows
- Grading, creative assessment, or lesson completion
- Lambda expression evaluation (non-EPIC_HACK path)
- DoTogether with ≥2 concurrent statements
- Cross-module behavior outside `core/ast`
- Performance benchmarking of delegate overhead

These behaviors require separate evidence lanes with their own fixtures and
review language.
