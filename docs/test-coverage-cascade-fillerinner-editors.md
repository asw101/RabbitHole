# Core/IDE Deep Test Coverage — Cascade, FillerInner, CodeEditor, DeclarationsEditor

> **Issue:** [#747](https://github.com/rysweet/RabbitHole/issues/747)  
> **Scope:** 9 new JUnit 4 test files covering logic-heavy headless paths in `core/ide`  
> **Line target:** 500+ additional lines of test code  
> **Build verification:** `mvn test -pl core/ide -am -q`

---

## Overview

This round of test coverage targets four packages in `core/ide` that contain
significant logic exercisable without a GUI:

| Package | Classes Under Test | New Test File(s) |
|---|---|---|
| `o.a.ide.cascade` | `ExpressionCascadeManager`, `ExpressionPropertyContext`, `BlockStatementIndexPairContext` | `ExpressionCascadeManagerDeepTest`, `ExpressionPropertyContextTest`, `BlockStatementIndexPairContextTest` |
| `o.a.ide.cascade.fillerinners` | `BooleanFillerInner`, `ConstantsOwningFillerInner`, `IntegerFillerInner`, `DoubleFillerInner` | `BooleanFillerInnerDeepTest`, `ConstantsOwningFillerInnerTest`, `NumberFillerInnerDeepTest` |
| `o.a.ide.codeeditor` | `StatementListPropertyPaneInfo`, `CodeEditor` | `StatementListPropertyPaneInfoTest`, `CodeEditorDeepTest` |
| `o.a.ide.declarationseditor` | `DeclarationComposite`, `CodeComposite` | `DeclarationCompositeRoutingTest` |

All tests are JUnit 4 (`@Test`, `@Before`, `static org.junit.Assert.*`) and run
headlessly — no display, no croquet Application boot, no IDE singleton required.

---

## Test File Reference

### 1. `ExpressionCascadeManagerDeepTest`

**Location:** `core/ide/src/test/java/org/alice/ide/cascade/ExpressionCascadeManagerDeepTest.java`

Extends coverage of the abstract `ExpressionCascadeManager` beyond the existing
`ExpressionCascadeManagerTest`. Uses the same `TestableExpressionCascadeManager`
subclass pattern.

#### Test Cases

| Method | What It Tests |
|---|---|
| `countLoopLocals_notAccessible_inDefaultMode` | In default (Alice) mode, `FormatterState.isJava()` returns `false`, so `CountLoop` variables should NOT appear in `getAccessibleLocals()`. Constructs a `CountLoop` inside a `BlockStatement`, pushes a `BlockStatementIndexPair` context, and asserts the locals list is empty. |
| `forEachLoop_localVariable_isAccessible` | Constructs a `ForEachInArrayLoop` with a `UserLocal` (isFinal=true), places it in a `BlockStatement`, pushes context, and verifies the loop variable appears in accessible locals. |
| `forEachLoop_localVariable_hasCorrectType` | Same setup as above; asserts the `UserLocal`'s value type matches the array element type. |
| `pushAndPopContext_multipleNested` | Pushes three contexts, pops them in reverse order, verifies stack integrity at each step via `popContext()`. |
| `getTypeFor_nullInput_returnsNull` | `getTypeFor(null)` returns null without throwing. |
| `getTypeFor_numberType_returnsDouble` | `getTypeFor(JavaType.getInstance(Number.class))` returns `DOUBLE_OBJECT_TYPE` (the only non-identity mapping). |
| `getTypeFor_stringType_returnsString` | `getTypeFor(JavaType.STRING_TYPE)` returns `STRING_TYPE` unchanged (identity mapping). |
| `appendItems_withPreviousExpression_addsCurrentValueFillIn` | Pushes a context with a non-null previous expression, calls `appendItems` with matching type — verifies `PreviousExpressionItselfFillIn` is added to items list. |

#### Usage

```bash
mvn test -pl core/ide -am -Dtest=ExpressionCascadeManagerDeepTest -q
```

---

### 2. `ExpressionPropertyContextTest`

**Location:** `core/ide/src/test/java/org/alice/ide/cascade/ExpressionPropertyContextTest.java`

Tests the `ExpressionPropertyContext` which wraps an `ExpressionProperty` from the
AST and provides block-statement-aware context for the cascade menu system.

#### Test Cases

| Method | What It Tests |
|---|---|
| `constructor_capturesExpressionProperty` | Constructs with an `ExpressionStatement.expression` property, verifies the stored property reference. |
| `getBlockStatementIndexPair_expressionInBlock` | Constructs `ExpressionStatement` → adds to `BlockStatement.statements` → creates context → verifies `getBlockStatementIndexPair()` returns a pair whose block matches and index is 0. |
| `getBlockStatementIndexPair_secondStatement` | Two `ExpressionStatement` instances in a block — the second statement's context should have index 1. |
| `getBlockStatementIndexPair_orphanExpression` | An `ExpressionProperty` not inside any `BlockStatement` returns null from `getBlockStatementIndexPair()`. |
| `getPreviousExpression_returnsExpressionValue` | Sets the `ExpressionProperty` value to a `DoubleLiteral(3.14)`, verifies `getPreviousExpression()` returns it. |
| `getPreviousExpression_nullProperty_returnsNull` | When the expression property has no value set, returns null. |
| `getBlockStatementIndexPair_constructorInvocation_returnsNull` | When the owning statement is a `ConstructorInvocationStatement`, `getBlockStatementIndexPair()` returns null (explicit null-return branch in the source). |

#### Usage

```bash
mvn test -pl core/ide -am -Dtest=ExpressionPropertyContextTest -q
```

---

### 3. `BlockStatementIndexPairContextTest`

**Location:** `core/ide/src/test/java/org/alice/ide/cascade/BlockStatementIndexPairContextTest.java`

Tests the `BlockStatementIndexPairContext` — a lightweight context wrapper that
delegates to a `BlockStatementIndexPair`.

#### Test Cases

| Method | What It Tests |
|---|---|
| `constructor_storesPair` | Constructs with a real `BlockStatementIndexPair`, verifies getter returns it. |
| `getBlockStatementIndexPair_returnsConstructorArg` | Round-trip: the pair returned equals the pair passed in. |
| `getBlockStatementIndexPair_withNullBlock` | Constructing with a pair that has a null block — verifies safe access. |
| `getPreviousExpression_alwaysNull` | This context type always returns null for `getPreviousExpression()` since it represents a statement slot, not an expression slot. |
| `multipleInstances_independent` | Two contexts with different pairs are independent; changing one doesn't affect the other. |

#### Usage

```bash
mvn test -pl core/ide -am -Dtest=BlockStatementIndexPairContextTest -q
```

---

### 4. `BooleanFillerInnerDeepTest`

**Location:** `core/ide/src/test/java/org/alice/ide/cascade/fillerinners/BooleanFillerInnerDeepTest.java`

Deep tests for `BooleanFillerInner.appendItems()` branches — the method has three
code paths depending on whether a `ConditionalInfixExpression`,
`LogicalComplement`, or general boolean expression is the previous value.

#### Test Cases

| Method | What It Tests |
|---|---|
| `appendItems_withNullPreviousExpression` | Default path — no previous expression, verifies items are appended without exception. |
| `appendItems_withConditionalInfixPrevious` | Sets previous expression to a `ConditionalInfixExpression`, invokes `appendItems`, verifies the conditional-specific branch is taken (items list is non-empty). |
| `appendItems_withLogicalComplementPrevious` | Previous is `LogicalComplement(BooleanLiteral(true))` — verifies complement-specific items are appended. |
| `appendItems_generalBooleanPrevious` | Previous is a plain `BooleanLiteral` — exercises the general/fallback path. |
| `isAssignableTo_booleanPrimitive` | `isAssignableTo(JavaType.BOOLEAN_PRIMITIVE_TYPE)` returns true. |
| `isAssignableTo_booleanObject` | `isAssignableTo(JavaType.BOOLEAN_OBJECT_TYPE)` returns true. |
| `isAssignableTo_unrelatedType_false` | `isAssignableTo(JavaType.getInstance(String.class))` returns false. |
| `addRelationalType_addsRelationalMenu` | After `addRelationalType(DoubleType)`, calling `appendItems` with `isTop=true` and non-null `prevExpression` adds a `RelationalObjectCascadeMenu` for that type to the items list. |
| `addRelationalType_duplicate_addsTwice` | Adding the same relational type twice produces two `RelationalObjectCascadeMenu` entries (list, not set). |

#### Usage

```bash
mvn test -pl core/ide -am -Dtest=BooleanFillerInnerDeepTest -q
```

---

### 5. `ConstantsOwningFillerInnerTest`

**Location:** `core/ide/src/test/java/org/alice/ide/cascade/fillerinners/ConstantsOwningFillerInnerTest.java`

Tests the `ConstantsOwningFillerInner` factory/cache pattern and its
`appendItems` behavior for types that expose named constants (e.g., enum-like
types with public static final fields).

#### Test Cases

| Method | What It Tests |
|---|---|
| `getInstance_returnsSameInstanceForSameType` | `getInstance(type)` called twice with the same `JavaType` returns `==` same instance (cache hit). |
| `getInstance_differentTypes_differentInstances` | Two different types produce different filler instances. |
| `getInstance_nullType_doesNotThrow` | Null input either throws `NullPointerException` or returns null — documents the contract. |
| `isAssignableTo_ownType_true` | The filler reports assignable for its own type (inherited from `ExpressionFillerInner`). |
| `isAssignableTo_otherType_false` | Not assignable to an unrelated type. |
| `appendItems_producesNonEmptyList_forEnumType` | For an actual enum type (e.g., `ArithmeticInfixExpression.Operator`), `appendItems` produces at least one menu item. |

#### Usage

```bash
mvn test -pl core/ide -am -Dtest=ConstantsOwningFillerInnerTest -q
```

---

### 6. `NumberFillerInnerDeepTest`

**Location:** `core/ide/src/test/java/org/alice/ide/cascade/fillerinners/NumberFillerInnerDeepTest.java`

Deep tests for `IntegerFillerInner` and `DoubleFillerInner` (which extend
`AbstractNumberFillerInner`) — arithmetic expression handling via `super.appendItems()`,
literal arrays, and `ValueDetails`-driven customization.

#### Test Cases

| Method | What It Tests |
|---|---|
| `integerLiterals_withCustomDetails` | Mock `IntegerValueDetails` whose `getLiterals()` returns `{-5,-4,...,5}` — verifies `IntegerFillerInner.getLiterals(details)` delegates to the interface. |
| `integerLiterals_withNull_returnsDefaults` | Null details → defaults `[0, 1, 2, 3]`. |
| `doubleLiterals_withNull_returnsDefaults` | Null details → defaults `[0.0, 0.25, 0.5, 1.0, 2.0, 10.0]`. |
| `doubleLiterals_customDetails` | Mock `NumberValueDetails` whose `getLiterals()` returns a custom array — verifies `DoubleFillerInner.getLiterals(details)` delegates to the interface. |
| `integerFillerInner_appendItems_withPrevExpression` | Previous expression is non-null and `isTop=true` — exercises `AbstractNumberFillerInner.super.appendItems` (ArithmeticInfixExpression operator-replace/reduce branch) plus `IntegerFillerInner`'s own items: `RandomCascadeMenu`, `RealToIntegerCascadeMenu`, and `MathCascadeMenu`. |
| `doubleFillerInner_appendItems_withPrevExpression` | Same pattern for `DoubleFillerInner` — verifies `RandomCascadeMenu`, `IntegerToRealCascadeMenu`, and `MathCascadeMenu` items appear when `isTop && prevExpression != null`. Also exercises the `ArithmeticInfixExpression` branch via `super.appendItems`. |
| `integerFillerInner_isAssignableTo_double_false` | Integer filler is NOT assignable to Double type. |
| `doubleFillerInner_isAssignableTo_number_true` | Double filler IS assignable to `Number` if the type hierarchy holds. |
| `getLiterals_emptyDetails_returnsDefaultArray` | Mock `IntegerValueDetails` whose `getLiterals()` returns an empty array — verifies no items are added for literals. |
| `abstractNumberFillerInner_getType_matchesConstructor` | The type returned by the filler matches what was passed to the constructor. |

#### Usage

```bash
mvn test -pl core/ide -am -Dtest=NumberFillerInnerDeepTest -q
```

---

### 7. `StatementListPropertyPaneInfoTest`

**Location:** `core/ide/src/test/java/org/alice/ide/codeeditor/StatementListPropertyPaneInfoTest.java`

Tests the data-object `StatementListPropertyPaneInfo` which tracks the geometric
bounds and `StatementListPropertyView` reference for a statement list pane in the code editor.

#### Test Cases

| Method | What It Tests |
|---|---|
| `constructor_storesViewAndBounds` | Constructs with a mock/null `StatementListPropertyView` and a `Rectangle`, verifies both are stored via getters. |
| `setBounds_thenGetBounds` | Sets bounds via setter, verifies round-trip via getter. |
| `constructor_nullBounds_storedAsNull` | Passing null bounds to constructor — `getBounds()` returns null. |
| `setBounds_overridesConstructorBounds` | Constructs with one Rectangle, calls `setBounds` with another, verifies the new one is returned. |
| `getStatementListPropertyPane_matchesConstructor` | The `StatementListPropertyView` returned matches the one provided at construction. |
| `setStatementListPropertyPane_overrides` | After `setStatementListPropertyPane(newView)`, getter returns the new view. |
| `multipleInstances_independentBounds` | Two instances with different bounds don't interfere. |

#### Usage

```bash
mvn test -pl core/ide -am -Dtest=StatementListPropertyPaneInfoTest -q
```

---

### 8. `DeclarationCompositeRoutingTest`

**Location:** `core/ide/src/test/java/org/alice/ide/declarationseditor/DeclarationCompositeRoutingTest.java`

Tests the static routing logic in `DeclarationComposite.getInstance()` and
`CodeComposite.getInstance()` — specifically the null-safe paths and the
`instanceof`-based dispatch (procedures → `CodeComposite`, types →
`TypeComposite`, etc.).

#### Test Cases

| Method | What It Tests |
|---|---|
| `getInstance_null_returnsNull` | `DeclarationComposite.getInstance(null)` returns null without throwing. |
| `codeComposite_getInstance_null_returnsNull` | `CodeComposite.getInstance(null)` returns null without throwing. |
| `codeComposite_cacheField_exists` | Reflective check: `CodeComposite` has a static `map` field that is a `Map`. |
| `declarationComposite_getInstance_withProcedure_returnsCodeComposite` | Passing a `UserMethod` (procedure) to `DeclarationComposite.getInstance()` returns an instance of `CodeComposite`. |
| `declarationComposite_getInstance_sameMethod_sameInstance` | Calling twice with the same `UserMethod` returns the same cached instance (cache lives in `CodeComposite.map`). |
| `codeComposite_getDeclaration_roundTrip` | After obtaining a `CodeComposite` for a method, `getDeclaration()` returns that method. |

#### Usage

```bash
mvn test -pl core/ide -am -Dtest=DeclarationCompositeRoutingTest -q
```

---

### 9. `CodeEditorDeepTest`

**Location:** `core/ide/src/test/java/org/alice/ide/codeeditor/CodeEditorDeepTest.java`

Extended structural and reflective tests for the `CodeEditor` class, verifying
field presence and constructor contract without instantiating a GUI.

#### Test Cases

| Method | What It Tests |
|---|---|
| `codeEditor_hasCodeProperty` | Reflective check that `CodeEditor` declares or inherits a field/method for the code composite. |
| `codeEditor_isNotAbstract` | The class is concrete and instantiable (given correct parameters). |
| `codeEditor_implementsExpectedInterface` | Verifies the class implements `Transferable`-related or drag-drop interfaces used by the IDE. |
| `codeEditor_constructorParameterTypes` | The primary constructor accepts `(AbstractProjectEditorAstI18nFactory, AbstractCode)` (verified via reflection). |
| `codeEditor_superclass_isCroquetComponent` | The superclass chain includes `CodePanelWithDropReceptor` (a croquet component base class). |
| `statementListPropertyPaneInfo_usedInCodeEditor` | Reflective check that `CodeEditor` references `StatementListPropertyPaneInfo` (field type or method return type). |

#### Usage

```bash
mvn test -pl core/ide -am -Dtest=CodeEditorDeepTest -q
```

---

## Running All New Tests

```bash
# Individual test file
mvn test -pl core/ide -am -Dtest=ExpressionCascadeManagerDeepTest -q

# All new tests at once
mvn test -pl core/ide -am \
  -Dtest="ExpressionCascadeManagerDeepTest,ExpressionPropertyContextTest,BlockStatementIndexPairContextTest,BooleanFillerInnerDeepTest,ConstantsOwningFillerInnerTest,NumberFillerInnerDeepTest,StatementListPropertyPaneInfoTest,DeclarationCompositeRoutingTest,CodeEditorDeepTest" \
  -q

# Full core/ide test suite (regression check)
mvn test -pl core/ide -am -q
```

---

## AST Construction Patterns (Reference)

All tests construct AST nodes headlessly. Here are the key patterns:

### BlockStatement with ExpressionStatement

```java
BlockStatement block = new BlockStatement();
ExpressionStatement exprStmt = new ExpressionStatement(
    new DoubleLiteral(3.14));
block.statements.add(exprStmt);
// exprStmt is now at index 0 in the block
```

### ForEachInArrayLoop with UserLocal

```java
UserLocal item = new UserLocal("item", JavaType.getInstance(String.class), true);
Expression arrayExpr = new ArrayInstanceCreation(
    JavaType.getInstance(String[].class), new IntegerLiteral(0));
BlockStatement body = new BlockStatement();
ForEachInArrayLoop loop = new ForEachInArrayLoop(item, arrayExpr, body);
```

### CountLoop

```java
BlockStatement body = new BlockStatement();
CountLoop countLoop = new CountLoop(
    new UserLocal("count", JavaType.INTEGER_OBJECT_TYPE, false),
    new UserLocal("index", JavaType.INTEGER_OBJECT_TYPE, false),
    new IntegerLiteral(5),
    body);
```

### ArithmeticInfixExpression

```java
ArithmeticInfixExpression arith = new ArithmeticInfixExpression(
    new IntegerLiteral(1),
    ArithmeticInfixExpression.Operator.PLUS,
    new IntegerLiteral(2),
    JavaType.getInstance(Integer.class));
```

### BlockStatementIndexPair

```java
BlockStatement block = new BlockStatement();
BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 0);
```

---

## Known Constraints

| Constraint | Mitigation |
|---|---|
| `FormatterState.isJava()` defaults to `false` (Alice mode) | Tests verify Alice-mode behavior; no mutation of the singleton |
| `ConstantsOwningFillerInner.getInstance()` uses a static cache | Tests use distinct types to avoid cross-test interference |
| `CodeComposite` constructor triggers `handleAstChangeThatCouldBeOfInterest()` | Tests route through `DeclarationComposite.getInstance()` which handles this safely for `UserMethod` |
| `DeclarationComposite` constructor registers a name listener calling `IDE.getActiveInstance()` | When no IDE is active, `getActiveInstance()` returns null and the listener is a no-op |
| `CodeEditor` is a Swing component | Tests use reflection only — no instantiation; extends `CodePanelWithDropReceptor` |
| `StatementListPropertyPaneInfo.contains()` takes `MouseEvent` | Tests for bounds use `setBounds`/`getBounds` directly; `contains()` needs a `Component` for `MouseEvent` construction — skip or mock |
| `BooleanFillerInner.appendItems()` creates `CascadeFillIn` instances | Tests verify list size and non-null, not singleton identity |
| `IntegerValueDetails` and `NumberValueDetails` are interfaces, not annotations | Tests use anonymous inner classes or mocks to provide custom `getLiterals()` return values |

---

## File Inventory

```
core/ide/src/test/java/org/alice/ide/
├── cascade/
│   ├── ExpressionCascadeManagerTest.java        (existing, 280 lines)
│   ├── ExpressionCascadeManagerDeepTest.java     ← NEW
│   ├── ExpressionPropertyContextTest.java        ← NEW
│   ├── BlockStatementIndexPairContextTest.java   ← NEW
│   └── fillerinners/
│       ├── CascadeFillerInnersTest.java          (existing, 149 lines)
│       ├── BooleanFillerInnerDeepTest.java       ← NEW
│       ├── ConstantsOwningFillerInnerTest.java   ← NEW
│       └── NumberFillerInnerDeepTest.java        ← NEW
├── codeeditor/
│   ├── CodeEditorTest.java                       (existing, 43 lines)
│   ├── CodeEditorDeepTest.java                   ← NEW
│   └── StatementListPropertyPaneInfoTest.java    ← NEW
└── declarationseditor/
    ├── ProcedureTabSelectionTest.java            (existing, 155 lines)
    └── DeclarationCompositeRoutingTest.java      ← NEW
```

**Total new test files:** 9  
**Estimated new test lines:** 550–650  
**Estimated new test methods:** ~70
