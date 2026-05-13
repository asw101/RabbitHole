# Virtual Machine Delegate Decomposition

This reference describes the internal decomposition of the 1193-line
`VirtualMachine` into a thin coordinator (~470 lines) plus two package-private
delegate classes: `VmExpressionEvaluator` (~400 lines) and
`VmStatementExecutor` (~340 lines).

The decomposition is a pure internal refactor. The public API surface —
`VirtualMachine`'s 20 public methods and 16 abstract methods — is unchanged.
All existing execution behavior, listener dispatch, error messages, and
exception semantics are preserved identically. A reflection-based contract test
(`VmContractTest`) guards the API surface before and after extraction.

## Contents

- [Motivation](#motivation)
- [Architecture](#architecture)
- [Class responsibilities](#class-responsibilities)
  - [VirtualMachine (coordinator)](#virtualmachine-coordinator)
  - [VmExpressionEvaluator](#vmexpressionevaluator)
  - [VmStatementExecutor](#vmstatementexecutor)
  - [VmContractTest](#vmcontracttest)
- [Public API](#public-api)
- [Package-private collaboration](#package-private-collaboration)
- [Cross-delegate calls](#cross-delegate-calls)
- [Listener dispatch](#listener-dispatch)
- [Thread safety](#thread-safety)
- [Subclass compatibility](#subclass-compatibility)
- [Security boundary](#security-boundary)
- [Error handling contract](#error-handling-contract)
- [Configuration](#configuration)
- [Validation](#validation)
- [Acceptance criteria](#acceptance-criteria)
- [Claim boundaries](#claim-boundaries)

## Motivation

The original `VirtualMachine.java` contained 1193 lines mixing five distinct
concerns: expression evaluation dispatch (28 `evaluate*` methods + switch),
statement execution dispatch (17 `execute*` methods + switch), public entry
points (`ENTRY_POINT_*`), field access/invocation operations, and listener
management. This made the class difficult to navigate, review, and extend
safely.

RabbitHole issue #551 decomposes the VM into focused delegates, mirroring the
[Encoder delegate decomposition](./encoder-delegate-decomposition.md) pattern
(issue #483). Each delegate is small enough to understand in isolation and
extend independently.

## Architecture

```text
VirtualMachine (abstract coordinator, ~470 lines)
├── VmExpressionEvaluator (package-private final, ~400 lines)
│   └── evaluate() dispatch switch (27 expression types),
│       26 evaluateXxx dispatch-target methods,
│       evaluateArgument, EPIC_HACK_evaluateLambdaExpression,
│       evaluateBoolean, evaluateInt, evaluate(Expression, Class<E>),
│       evaluateArguments (delegated from VM's public wrapper)
└── VmStatementExecutor (package-private final, ~340 lines)
    └── execute() dispatch switch (14 statement types),
        14 executeXxx dispatch-target methods,
        excecuteForEachLoop, excecuteEachInTogether shared helpers
```

All three classes live in `org.lgna.project.virtualmachine`. The delegates are
package-private with no public constructors. They are instantiated only by
`VirtualMachine`'s constructor and receive a back-reference to it for shared
services.

## Class responsibilities

### VirtualMachine (coordinator)

| Responsibility | Methods |
| --- | --- |
| Abstract frame management | `getThis()`, `pushBogusFrame()`, `pushConstructorFrame()`, `setConstructorFrameUserInstance()`, `pushMethodFrame()`, `pushLambdaFrame()`, `popFrame()`, `getFrameForThread()`, `pushCurrentThread()`, `popCurrentThread()` |
| Abstract variable lookup | `lookup()`, `pushLocal()`, `getLocal()`, `setLocal()`, `popLocal()` |
| Abstract stack trace | `getStackTrace()` |
| Public entry points | `ENTRY_POINT_evaluate()`, `ENTRY_POINT_invoke()`, `ENTRY_POINT_createInstance()` |
| Field access | `get()`, `set()`, `getUserField()`, `setUserField()`, `getFieldDeclaredInJavaWithField()`, `setFieldDeclaredInJavaWithField()` |
| Array operations | `createArrayInstance()`, `getArrayLength()`, `getItemAtIndex()`, `setItemAtIndex()` |
| Method invocation | `invoke()`, `invokeUserMethod()`, `invokeMethodDeclaredInJava()` |
| Instance creation | `createInstance()`, `getConstructor()` |
| Scene editor hacks | `ACCEPTABLE_HACK_FOR_SCENE_EDITOR_initializeField()`, `ACCEPTABLE_HACK_FOR_SCENE_EDITOR_executeStatement()` |
| Field initialization | `createAndSetFieldInstance()` |
| Adapter registration | `registerAbstractClassAdapter()` |
| Lifecycle | `stopExecution()`, `setForSceneEditor()` |
| Listener management | `addVirtualMachineListener()`, `removeVirtualMachineListener()`, `getVirtualMachineListeners()` |
| Delegation wrappers | `evaluate(Expression)` → `expressionEvaluator.evaluate()`, `execute(Statement)` → `statementExecutor.execute()`, `evaluateArguments()` → `expressionEvaluator.evaluateArguments()` |

The coordinator owns entry points, field/array/invocation operations, abstract
method declarations, and delegation wrappers. Method bodies for evaluate/execute
delegate to the appropriate delegate class.

### VmExpressionEvaluator

| Responsibility | Methods |
| --- | --- |
| Dispatch | `evaluate(Expression)` — pattern-matching switch over 27 expression types |
| Assignment | `evaluateAssignmentExpression(AssignmentExpression)` |
| Literals | `evaluateBooleanLiteral`, `evaluateNullLiteral`, `evaluateDoubleLiteral`, `evaluateFloatLiteral`, `evaluateIntegerLiteral`, `evaluateStringLiteral` |
| Array expressions | `evaluateArrayInstanceCreation`, `evaluateArrayAccess`, `evaluateArrayLength` |
| Field/local access | `evaluateFieldAccess`, `evaluateLocalAccess`, `evaluateParameterAccess` |
| Infix expressions | `evaluateArithmeticInfixExpression`, `evaluateBitwiseInfixExpression`, `evaluateConditionalInfixExpression`, `evaluateRelationalInfixExpression`, `evaluateShiftInfixExpression` |
| Unary/string | `evaluateLogicalComplement`, `evaluateStringConcatenation` |
| Invocation | `evaluateMethodInvocation` |
| This/type | `evaluateThisExpression`, `evaluateTypeExpression`, `evaluateTypeLiteral` |
| Resource | `evaluateResourceExpression` |
| Lambda | `evaluateLambdaExpression`, `EPIC_HACK_evaluateLambdaExpression` |
| Typed evaluation | `evaluate(Expression, Class<E>)` — cast wrapper with `UserArrayInstance` handling |
| Type coercion helpers | `evaluateBoolean(Expression, String)`, `evaluateInt(Expression, String)` |
| Argument evaluation | `evaluateArgument(AbstractArgument)`, `evaluateArguments(AbstractCode, ...)` |
| Listener dispatch | Fires `ExpressionEvaluationEvent` after each evaluation |

`VmExpressionEvaluator` calls back to `VirtualMachine` for `get()`, `set()`,
`getItemAtIndex()`, `setItemAtIndex()`, `setLocal()`, `getLocal()`, `lookup()`,
`getThis()`, `invoke()`, `createArrayInstance()`, `getArrayLength()`,
`pushLambdaFrame()`, `popFrame()`, `isStopped`, `getListenerList()`,
`checkNotNull()`, and `mapAbstractClsToAdapterCls`. It has no mutable state
beyond the `VirtualMachine` reference.

### VmStatementExecutor

| Responsibility | Methods |
| --- | --- |
| Dispatch | `execute(Statement)` — pattern-matching switch over 14 statement types |
| Block | `executeBlockStatement(BlockStatement, VirtualMachineListener[])` |
| Conditional | `executeConditionalStatement(ConditionalStatement, VirtualMachineListener[])` |
| Comment | `executeComment(Comment, VirtualMachineListener[])` |
| Loops | `executeCountLoop`, `executeWhileLoop`, `executeForEachInArrayLoop`, `executeForEachInIterableLoop` |
| Concurrent | `executeDoInOrder`, `executeDoTogether`, `executeEachInArrayTogether`, `executeEachInIterableTogether` |
| Expression | `executeExpressionStatement(ExpressionStatement, VirtualMachineListener[])` |
| Return | `executeReturnStatement(ReturnStatement, VirtualMachineListener[])` |
| Local | `executeLocalDeclarationStatement(LocalDeclarationStatement, VirtualMachineListener[])` |
| Shared helpers | `excecuteForEachLoop(AbstractForEachLoop, Object[], VirtualMachineListener[])`, `excecuteEachInTogether(AbstractEachInTogether, Object[], VirtualMachineListener[])` |
| Listener dispatch | Fires `StatementExecutionEvent` (executing/executed) and per-iteration events |

`VmStatementExecutor` calls back to `VirtualMachine` for `pushLocal()`,
`setLocal()`, `popLocal()`, `getFrameForThread()`, `pushCurrentThread()`,
`popCurrentThread()`, `isStopped`, and `getListenerList()`. It accesses the
expression evaluator through `vm.expressionEvaluator` for `evaluate()`,
`evaluateBoolean()`, `evaluateInt()`, and `evaluate(Expression, Class<E>)`.

### VmContractTest

Reflection-based contract test verifying the public API surface of
`VirtualMachine.java` remains stable across refactoring.

| Category | Count | Examples |
| --- | --- | --- |
| Public methods | 19 | `ENTRY_POINT_evaluate`, `ENTRY_POINT_invoke`, `ENTRY_POINT_createInstance`, `get`, `set`, `invokeUserMethod`, `invokeMethodDeclaredInJava`, `evaluateArguments`, `getItemAtIndex`, `setItemAtIndex`, `stopExecution`, `addVirtualMachineListener`, `removeVirtualMachineListener`, `getVirtualMachineListeners`, `registerAbstractClassAdapter`, `createAndSetFieldInstance`, `ACCEPTABLE_HACK_FOR_SCENE_EDITOR_initializeField`, `ACCEPTABLE_HACK_FOR_SCENE_EDITOR_executeStatement`, `setForSceneEditor` |
| Abstract methods | 16 | `getStackTrace`, `getThis`, `pushBogusFrame`, `pushConstructorFrame`, `setConstructorFrameUserInstance`, `pushMethodFrame`, `pushLambdaFrame`, `popFrame`, `lookup`, `pushLocal`, `getLocal`, `setLocal`, `popLocal`, `getFrameForThread`, `pushCurrentThread`, `popCurrentThread` |

The test uses `java.lang.reflect` to verify method existence, parameter types,
and return types. It does not instantiate the VM or execute any code. It must
pass identically before and after delegate extraction.

## Public API

The public API is exclusively through `VirtualMachine`'s public and abstract
methods. No API changes are made by this decomposition.

```java
public abstract class VirtualMachine {
  // Entry points (unchanged)
  public Object[] ENTRY_POINT_evaluate(UserInstance instance, Expression[] expressions);
  public Object ENTRY_POINT_invoke(UserInstance target, AbstractMethod method, Object... arguments);
  public UserInstance ENTRY_POINT_createInstance(NamedUserType entryPointType, Object... arguments);

  // Field access (unchanged)
  public Object get(AbstractField field, Object instance);
  public void set(AbstractField field, Object instance, Object value);

  // Array access (unchanged)
  public Object getItemAtIndex(AbstractType<?,?,?> arrayType, Object array, Integer index);
  public void setItemAtIndex(AbstractType<?,?,?> arrayType, Object array, Integer index, Object value);

  // Method invocation (unchanged)
  public Object invokeUserMethod(Object instance, UserMethod method, Object... arguments);
  public Object invokeMethodDeclaredInJava(Object instance, JavaMethod method, Object... arguments);

  // Argument evaluation (unchanged — called from AST's InstanceCreation)
  public Object[] evaluateArguments(AbstractCode code, NodeListProperty<SimpleArgument> arguments,
      NodeListProperty<SimpleArgument> variableArguments, NodeListProperty<JavaKeyedArgument> keyedArguments);

  // Lifecycle (unchanged)
  public void stopExecution();
  public void setForSceneEditor();

  // Listeners (unchanged)
  public void addVirtualMachineListener(VirtualMachineListener listener);
  public void removeVirtualMachineListener(VirtualMachineListener listener);
  public List<VirtualMachineListener> getVirtualMachineListeners();

  // Registration (unchanged)
  public void registerAbstractClassAdapter(Class<?> abstractCls, Class<?> adapterCls);

  // Instance management (unchanged)
  public void createAndSetFieldInstance(UserInstance userInstance, UserField field);
  public void ACCEPTABLE_HACK_FOR_SCENE_EDITOR_initializeField(UserInstance instance, UserField field);
  public void ACCEPTABLE_HACK_FOR_SCENE_EDITOR_executeStatement(UserInstance instance, Statement statement);
}
```

External callers (AST nodes, `ReleaseVirtualMachine`, IDE code) continue to use
these methods exactly as before. The `evaluateArguments` method is particularly
important because it is called from `InstanceCreation.evaluate(VirtualMachine)`
in the AST package — it must remain a public method on `VirtualMachine` as a
thin delegation wrapper.

## Package-private collaboration

Both delegates are package-private final classes with a single constructor that
accepts a `VirtualMachine` reference:

```java
final class VmExpressionEvaluator {
  private final VirtualMachine vm;
  VmExpressionEvaluator(VirtualMachine vm) { this.vm = vm; }
}

final class VmStatementExecutor {
  private final VirtualMachine vm;
  VmStatementExecutor(VirtualMachine vm) { this.vm = vm; }
}
```

`VirtualMachine` initializes them in the constructor and stores them as
package-private fields:

```java
public abstract class VirtualMachine {
  final VmExpressionEvaluator expressionEvaluator = new VmExpressionEvaluator(this);
  final VmStatementExecutor statementExecutor = new VmStatementExecutor(this);
  // ...
}
```

Four fields on `VirtualMachine` are widened from `private` to package-private to
allow delegate access without accessor methods:

| Field | Original visibility | New visibility | Used by |
| --- | --- | --- | --- |
| `virtualMachineListeners` | `private` | package-private | Both delegates (listener dispatch) |
| `isStopped` | `private` | package-private | Both delegates (stop check) |
| `mapAbstractClsToAdapterCls` | `private` | package-private | Evaluator (lambda adapter lookup) |
| `checkNotNull` | `private` method | package-private method | Both delegates (null guard) |

No fields are made `public` or `protected`. All visibility changes are within
the same Java package.

## Cross-delegate calls

The statement executor needs expression evaluation (e.g., `evaluateBoolean` for
loop conditions, `evaluate` for expression statements). The expression evaluator
needs statement execution (e.g., `execute` within lambda bodies).

Both delegates resolve this through the `VirtualMachine` reference:

```text
VmStatementExecutor → vm.expressionEvaluator.evaluate(expr)
VmStatementExecutor → vm.expressionEvaluator.evaluateBoolean(expr, msg)
VmStatementExecutor → vm.expressionEvaluator.evaluateInt(expr, msg)
VmStatementExecutor → vm.expressionEvaluator.evaluate(expr, cls)

VmExpressionEvaluator → vm.statementExecutor.execute(statement)
  (only in EPIC_HACK_evaluateLambdaExpression lambda body)
```

There is no circular instantiation — both delegates are created on the same
`VirtualMachine` instance and reference each other through the parent.

## Listener dispatch

Listener events are dispatched by the delegates directly:

- **Expression evaluation events**: `VmExpressionEvaluator.evaluate()` fires
  `ExpressionEvaluationEvent` after each evaluation, using
  `vm.virtualMachineListeners` with the existing `synchronized` block.

- **Statement execution events**: `VmStatementExecutor.execute()` fires
  `StatementExecutionEvent` (executing/executed pair) and per-iteration events
  (`CountLoopIterationEvent`, `ForEachLoopIterationEvent`,
  `WhileLoopIterationEvent`, `EachInTogetherItemEvent`), using
  `vm.virtualMachineListeners` with the existing `synchronized` block for
  snapshot.

The two delegates use different dispatch patterns, both preserved from the
original code:

- **Expression evaluator**: Iterates `vm.virtualMachineListeners` directly
  inside the `synchronized` block (no snapshot array).
- **Statement executor**: Captures a `VirtualMachineListener[]` snapshot array
  under the lock, then iterates outside the lock.

## Thread safety

Thread safety is unchanged:

- `virtualMachineListeners` uses `synchronized` blocks for add/remove/snapshot,
  exactly as before.
- `isStopped` is written only from `stopExecution()` and read from delegates.
  The existing single-writer pattern is preserved.
- `DoTogether` and `EachInTogether` thread spawning calls
  `vm.pushCurrentThread()`/`vm.popCurrentThread()` through the same delegation
  path as before.

No new synchronization is added. No existing synchronization is removed.

## Subclass compatibility

`ReleaseVirtualMachine` is the only concrete subclass of `VirtualMachine`. It
overrides:

- All 16 abstract methods (frame management, variable lookup, stack trace)
- Zero `evaluate*` methods
- Zero `execute*` methods

Because `ReleaseVirtualMachine` overrides none of the methods being extracted,
the delegate extraction has zero subclass breakage risk. The abstract methods
remain on `VirtualMachine` and continue to be overridden by
`ReleaseVirtualMachine` as before.

## Security boundary

No security impact — pure internal refactoring within the same Java package.
Delegates are package-private (not public). No new attack surface, no credential
handling, no serialization changes, no reflection changes beyond
`VmContractTest`.

## Error handling contract

All exception semantics are preserved:

| Exception | Origin | Preserved by |
| --- | --- | --- |
| `LgnaVmNullPointerException` | `checkNotNull()`, `evaluateRelationalInfixExpression` | Evaluator delegates to `vm.checkNotNull()` |
| `LgnaVmNoReturnException` | `invokeUserMethod` | Stays in VM coordinator |
| `LgnaVmClassCastException` | `evaluateBoolean`, `evaluateInt` | Evaluator owns these methods |
| `LgnaVmArrayIndexOutOfBoundsException` | `checkIndex` | Stays in VM coordinator |
| `ReturnException` | `executeReturnStatement` | Executor throws; VM coordinator catches |
| `NullPointerException` | `evaluate(null)` | Evaluator null-checks expression |
| `RuntimeException` | default switch cases | Both delegates preserve default cases |

## Configuration

No configuration changes. The decomposition is invisible to callers.

## Validation

Run the full `core/ast` module test suite:

```bash
git submodule update --init tweedle-lang
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/ast -am -DfailIfNoTests=false -Dcheckstyle.skip test
```

This runs:
- `VmContractTest` — API surface verification (reflection-based)
- `VmExpressionEvaluationCharacterizationTest` — expression evaluation behavior
- `VmStatementExecutionCharacterizationTest` — statement execution behavior
- `VmFieldAccessCharacterizationTest` — field access round-trips
- `VmErrorHandlingCharacterizationTest` — error conditions and exceptions
- `VmStoryApiDispatchCharacterizationTest` — adapter dispatch and instance creation
- `SilverThreadVirtualMachineExecutionTest` — silver thread execution paths
- `VirtualMachineHeadlessRuntimeEventTest` — headless runtime event dispatch

All tests pass without a display server or network access.

### Line count verification

After extraction, `VirtualMachine.java` is under 500 lines (target ~470):

```bash
wc -l core/ast/src/main/java/org/lgna/project/virtualmachine/VirtualMachine.java
# Expected: ~470
```

## Acceptance criteria

1. **VmContractTest passes** — all 19 public methods and 16 abstract methods
   verified via reflection with correct parameter types and return types.

2. **All 8 existing test classes pass** — zero behavioral regressions.

3. **VirtualMachine.java < 500 lines** — reduced from 1193 lines.

4. **VmExpressionEvaluator.java exists** — package-private final class, ~400
   lines, owns all `evaluate*` methods.

5. **VmStatementExecutor.java exists** — package-private final class, ~340
   lines, owns all `execute*` methods.

6. **No public API changes** — `evaluateArguments` remains public on
   `VirtualMachine` as a delegation wrapper (called from AST's
   `InstanceCreation`).

7. **mvn -pl core/ast -am -DfailIfNoTests=false -Dcheckstyle.skip test** passes.

## Claim boundaries

This decomposition does **not** claim:

| Non-claim | Reason |
| --- | --- |
| Behavioral changes | Pure structural refactor. |
| Performance improvement | Method delegation adds negligible call overhead. |
| Thread safety improvement | Existing synchronization is preserved as-is. |
| New expression/statement support | No new AST node types are handled. |
| Lambda evaluation coverage | `evaluateLambdaExpression` (without EPIC_HACK) still throws `RuntimeException("todo")`. |
| DoTogether correctness for ≥2 statements | Multi-threaded DoTogether behavior is unchanged and untested. |
| Cross-module behavior | Only `core/ast` is modified. |
| Desktop runtime correctness | No GUI, no scene rendering, no display server. |
