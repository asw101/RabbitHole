# Tutorial: Trace the VM Delegate Decomposition

This tutorial walks through the extraction of `VmExpressionEvaluator` and
`VmStatementExecutor` from `VirtualMachine.java`. You will trace how expression
evaluation and statement execution were moved into focused delegates while
preserving the public API and all existing test behavior.

For the full contract, see the [VM Delegate Decomposition
reference](../reference/vm-delegate-decomposition.md).

## Contents

- [Goal](#goal)
- [1. Understand the before state](#1-understand-the-before-state)
- [2. Trace the contract test](#2-trace-the-contract-test)
- [3. Trace expression evaluator extraction](#3-trace-expression-evaluator-extraction)
- [4. Trace statement executor extraction](#4-trace-statement-executor-extraction)
- [5. Trace cross-delegate calls](#5-trace-cross-delegate-calls)
- [6. Trace field visibility widening](#6-trace-field-visibility-widening)
- [7. Trace the evaluateArguments wrapper](#7-trace-the-evaluatearguments-wrapper)
- [8. Run the validation](#8-run-the-validation)
- [9. Compare before and after](#9-compare-before-and-after)

## Goal

Understand how the 1193-line `VirtualMachine.java` was decomposed into three
focused files while preserving the identical public API and all existing test
behavior. After this tutorial you will be able to explain:

- Why a reflection-based contract test was written first
- How `evaluate()` delegation works through `VmExpressionEvaluator`
- How `execute()` delegation works through `VmStatementExecutor`
- Why cross-delegate calls go through the `VirtualMachine` reference
- Why four fields were widened to package-private
- Why `evaluateArguments` remains a public wrapper on `VirtualMachine`

Open these source files alongside this guide:

```text
core/ast/src/main/java/org/lgna/project/virtualmachine/VirtualMachine.java
core/ast/src/main/java/org/lgna/project/virtualmachine/VmExpressionEvaluator.java
core/ast/src/main/java/org/lgna/project/virtualmachine/VmStatementExecutor.java
core/ast/src/test/java/org/lgna/project/virtualmachine/VmContractTest.java
```

## 1. Understand the before state

Before extraction, `VirtualMachine.java` was 1193 lines with five mixed
concerns:

```text
Lines   1–100:   License, imports, 16 abstract method declarations
Lines 103–178:   Public entry points (ENTRY_POINT_*)
Lines 180–412:   Field/array/invocation operations
Lines 413–510:   invokeUserMethod, invokeMethodDeclaredInJava, invoke
Lines 513–808:   26 evaluate* dispatch methods + evaluate() dispatch switch
Lines 810–845:   evaluateBoolean, evaluateInt type coercion helpers
Lines 847–1157:  14 execute* dispatch methods + execute() dispatch switch
Lines 1160–1193: stopExecution, listeners, isStopped, isForRunning fields
```

The expression evaluation methods (lines 513–845) and statement execution
methods (lines 847–1157) accounted for ~645 lines — over half the class. These
two groups had clear boundaries: evaluators return values, executors produce side
effects and fire listener events.

**Key observation:** `ReleaseVirtualMachine` overrides zero `evaluate*` and zero
`execute*` methods. This means extracting these methods cannot break any
subclass.

## 2. Trace the contract test

Open `VmContractTest.java`. The contract test was written **before** any
extraction to establish the API baseline.

**Public method verification:**

```java
@Test
public void virtualMachineHasEntryPointEvaluate() throws Exception {
  Method m = VirtualMachine.class.getDeclaredMethod("ENTRY_POINT_evaluate",
      UserInstance.class, Expression[].class);
  assertTrue(Modifier.isPublic(m.getModifiers()));
  assertEquals(Object[].class, m.getReturnType());
}
```

This pattern repeats for all 19 public methods. Each test:

1. Calls `getDeclaredMethod` with exact parameter types
2. Asserts the method is `public`
3. Asserts the return type

**Abstract method verification:**

```java
@Test
public void virtualMachineHasAbstractGetThis() throws Exception {
  Method m = VirtualMachine.class.getDeclaredMethod("getThis");
  assertTrue(Modifier.isAbstract(m.getModifiers()));
  assertTrue(Modifier.isProtected(m.getModifiers()));
  assertEquals(UserInstance.class, m.getReturnType());
}
```

This pattern repeats for all 16 abstract methods. Each test:

1. Calls `getDeclaredMethod` with exact parameter types
2. Asserts the method is `abstract`
3. Asserts the method is `protected` (frame management) or `public` (getStackTrace)
4. Asserts the return type

**Class structure verification:**

```java
@Test
public void virtualMachineIsAbstract() {
  assertTrue(Modifier.isAbstract(VirtualMachine.class.getModifiers()));
}

@Test
public void releaseVirtualMachineExtendsVirtualMachine() {
  assertEquals(VirtualMachine.class, ReleaseVirtualMachine.class.getSuperclass());
}
```

The contract test is pure reflection — no VM instantiation, no AST construction,
no execution. It runs in milliseconds and fails immediately if any method
signature changes.

**Why reflection?** The test verifies the public contract independent of
implementation. Whether `evaluate()` is implemented directly on
`VirtualMachine` or delegated to `VmExpressionEvaluator`, the method signature
on `VirtualMachine` is the same. The contract test passes identically before and
after extraction.

## 3. Trace expression evaluator extraction

Open `VmExpressionEvaluator.java`. Find the `evaluate()` method:

```java
Object evaluate(Expression expression) {
  if (expression == null) {
    throw new NullPointerException();
  }
  Object rv = switch (expression) {
    case AssignmentExpression e -> evaluateAssignmentExpression(e);
    case BooleanLiteral e -> evaluateBooleanLiteral(e);
    case InstanceCreation e -> e.evaluate(vm);
    // ... 24 more cases ...
    default -> throw new RuntimeException(expression.getClass().getName());
  };
  synchronized (vm.virtualMachineListeners) {
    if (!vm.virtualMachineListeners.isEmpty()) {
      ExpressionEvaluationEvent event = new ExpressionEvaluationEvent(vm, expression, rv);
      for (VirtualMachineListener listener : vm.virtualMachineListeners) {
        listener.expressionEvaluated(event);
      }
    }
  }
  return rv;
}
```

**What changed:** The method body is identical to the original
`VirtualMachine.evaluate()`. The only changes are:

1. `this.evaluate(...)` → `evaluate(...)` (same class now)
2. `this.set(...)` → `vm.set(...)` (VM coordinator method)
3. `this.getLocal(...)` → `vm.getLocal(...)` (abstract method on VM)
4. `this.virtualMachineListeners` → `vm.virtualMachineListeners` (widened field)
5. Access modifier changed from `protected` to package-private (default)

**On VirtualMachine, the delegation wrapper:**

```java
protected Object evaluate(Expression expression) {
  return expressionEvaluator.evaluate(expression);
}
```

This single-line wrapper preserves the `protected` visibility for any
hypothetical subclass override (though none exist today).

## 4. Trace statement executor extraction

Open `VmStatementExecutor.java`. Find the `execute()` method:

```java
void execute(Statement statement) throws ReturnException {
  if (vm.isStopped) {
    return;
  }
  assert statement != null : vm;
  if (statement.isEnabled.getValue()) {
    StatementExecutionEvent statementEvent;
    VirtualMachineListener[] listeners;
    synchronized (vm.virtualMachineListeners) {
      // ... snapshot listeners ...
    }
    // ... fire executing, dispatch, fire executed ...
  }
}
```

**What changed:** Same pattern as the evaluator:

1. `this.isStopped` → `vm.isStopped`
2. `this.virtualMachineListeners` → `vm.virtualMachineListeners`
3. `this.evaluateBoolean(...)` → `vm.expressionEvaluator.evaluateBoolean(...)`
4. `this.evaluate(...)` → `vm.expressionEvaluator.evaluate(...)`
5. `this.pushLocal(...)` → `vm.pushLocal(...)`

The statement dispatch switch is identical:

```java
switch (statement) {
  case BlockStatement s -> executeBlockStatement(s, listeners);
  case ConditionalStatement s -> executeConditionalStatement(s, listeners);
  // ... 12 more cases ...
  default -> throw new RuntimeException();
}
```

**On VirtualMachine, the delegation wrapper:**

```java
protected void execute(Statement statement) throws ReturnException {
  statementExecutor.execute(statement);
}
```

## 5. Trace cross-delegate calls

The most interesting design decision is how the two delegates call each other.

**Executor needs evaluator** (common case):

```java
// In VmStatementExecutor.executeCountLoop:
final int n = vm.expressionEvaluator.evaluateInt(
    countLoop.count.getValue(), "count expression is null");
```

The executor evaluates loop conditions, array expressions, and return values
through the evaluator. It accesses the evaluator via `vm.expressionEvaluator`
— a package-private field on the shared `VirtualMachine` instance.

**Evaluator needs executor** (rare case — lambda bodies only):

```java
// In VmExpressionEvaluator.EPIC_HACK_evaluateLambdaExpression:
//   Inside the anonymous LambdaContext:
vm.statementExecutor.execute(userLambda.body.getValue());
```

This only occurs in `EPIC_HACK_evaluateLambdaExpression`, where a lambda body
(statements) is executed during expression evaluation. The evaluator accesses
the executor via `vm.statementExecutor`.

**Why not inject directly?** The delegates could hold direct references to each
other, but this creates a chicken-and-egg initialization problem. By routing
through `VirtualMachine`, both delegates can be initialized independently:

```java
final VmExpressionEvaluator expressionEvaluator = new VmExpressionEvaluator(this);
final VmStatementExecutor statementExecutor = new VmStatementExecutor(this);
```

Both fields are `final` and initialized at field-declaration time. By the time
any method is called, both are non-null.

## 6. Trace field visibility widening

Four members changed from `private` to package-private:

### `virtualMachineListeners`

```java
// Before: private final List<VirtualMachineListener> virtualMachineListeners = ...
// After:  final List<VirtualMachineListener> virtualMachineListeners = ...
```

Both delegates read this list to fire events. The existing `synchronized` blocks
are preserved in the delegates.

### `isStopped`

```java
// Before: private boolean isStopped = false;
// After:  boolean isStopped = false;
```

Both delegates check this flag to short-circuit execution. It is only written by
`stopExecution()`, which remains on `VirtualMachine`.

### `mapAbstractClsToAdapterCls`

```java
// Before: private final Map<Class<?>, Class<?>> mapAbstractClsToAdapterCls = ...
// After:  final Map<Class<?>, Class<?>> mapAbstractClsToAdapterCls = ...
```

The evaluator reads this map in `EPIC_HACK_evaluateLambdaExpression` to look up
adapter classes for lambda contexts.

### `checkNotNull`

```java
// Before: private void checkNotNull(Object value, String message) { ... }
// After:  void checkNotNull(Object value, String message) { ... }
```

Both delegates call this method to throw `LgnaVmNullPointerException` with
descriptive messages.

**Why not use accessor methods?** Package-private field access is simpler,
faster, and consistent with the existing codebase style. Adding `getListenerList()`,
`isStopped()` accessors would add boilerplate without benefit in the same
package.

## 7. Trace the evaluateArguments wrapper

`evaluateArguments` is called from outside the `virtualmachine` package:

```java
// In org.lgna.project.ast.InstanceCreation:
public Object evaluate(VirtualMachine vm, Object instance, Object[] arguments) {
  Object[] all = vm.evaluateArguments(this, this.requiredArguments, ...);
  // ...
}
```

Because `InstanceCreation` is in `org.lgna.project.ast`, it cannot access
package-private methods on `VirtualMachine`. So `evaluateArguments` must remain
a **public** method on `VirtualMachine`:

```java
// On VirtualMachine:
public Object[] evaluateArguments(AbstractCode code,
    NodeListProperty<SimpleArgument> arguments,
    NodeListProperty<SimpleArgument> variableArguments,
    NodeListProperty<JavaKeyedArgument> keyedArguments) {
  return expressionEvaluator.evaluateArguments(code, arguments,
      variableArguments, keyedArguments);
}
```

The actual implementation lives in `VmExpressionEvaluator`. The wrapper is a
thin one-line delegation.

## 8. Run the validation

From the repository root:

```bash
git submodule update --init tweedle-lang
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/ast -am -DfailIfNoTests=false -Dcheckstyle.skip test
```

All tests pass — `VmContractTest` verifies the API surface, and the five
characterization suites plus two silver thread tests verify behavioral
preservation.

## 9. Compare before and after

### Line counts

| File | Before | After |
| --- | --- | --- |
| `VirtualMachine.java` | 1193 | ~470 |
| `VmExpressionEvaluator.java` | — | ~400 |
| `VmStatementExecutor.java` | — | ~340 |
| **Total** | 1193 | ~1210 |

Total line count increases slightly due to imports, class declarations, and the
`VirtualMachine` reference field in each delegate. This is expected — the goal
is file-level comprehensibility, not total line reduction.

### Method ownership

| Method category | Before (owner) | After (owner) |
| --- | --- | --- |
| 16 abstract methods | VirtualMachine | VirtualMachine (unchanged) |
| 20 public methods | VirtualMachine | VirtualMachine (unchanged) |
| 26 evaluate* dispatch methods | VirtualMachine | VmExpressionEvaluator |
| evaluate() dispatch | VirtualMachine | VmExpressionEvaluator |
| evaluateBoolean/Int | VirtualMachine | VmExpressionEvaluator |
| evaluateArgument | VirtualMachine | VmExpressionEvaluator |
| EPIC_HACK_evaluateLambdaExpression | VirtualMachine | VmExpressionEvaluator |
| 14 execute* dispatch methods | VirtualMachine | VmStatementExecutor |
| execute() dispatch | VirtualMachine | VmStatementExecutor |
| excecuteForEachLoop | VirtualMachine | VmStatementExecutor |
| excecuteEachInTogether | VirtualMachine | VmStatementExecutor |

### What stayed on VirtualMachine

- All abstract methods (subclass contract)
- All public entry points (external API)
- Field/array/invocation operations (shared services)
- Instance creation and constructor resolution
- Listener management (add/remove/get)
- Lifecycle methods (stopExecution, setForSceneEditor)
- Adapter registration
- One-line delegation wrappers for `evaluate()`, `execute()`, `evaluateArguments()`

The coordinator is now a clean API surface with clear delegation boundaries.
Each file has a single responsibility and fits on a screen.
