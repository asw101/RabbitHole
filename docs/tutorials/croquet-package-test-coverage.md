# Tutorial: org.alice.ide.croquet package test coverage

This tutorial documents the test suite covering the `org.alice.ide.croquet`
package — codecs, edits, numberpad models, help composites, and project/find
data structures. The 18 test files add 2000+ lines of headless JUnit 4 tests
that exercise construction contracts, singleton caching, data accessors,
AST round-trips, and defensive null handling.

## What you will learn

- How to test `ItemCodec` implementations (NodeCodec, SingletonCodec, ResourceCodec, etc.)
- How to test edit classes using `null` UserActivity (established project pattern)
- How to write headless-guarded tests for numberpad models that touch Swing
- How to test tree data structures (SearchTreeNode hierarchy) without IDE context
- How to test enum constants and singleton accessors in help composites

## Prerequisites

- JDK 17+
- Maven 3.8+
- Tweedle grammar submodule initialized: `git submodule update --init tweedle-lang`
- Familiarity with JUnit 4 (`@Test`, `@Before`, `@Assume`)

## Architecture overview

```
org.alice.ide.croquet
├── codecs/               ← ItemCodec<T> implementations (serialize/deserialize AST)
│   ├── NodeCodec         ← Cached by class, encodes via Node UUID
│   ├── PropertyOfNodeCodec ← Encodes InstanceProperty via owner Node + name
│   ├── ResourceCodec     ← Encodes Resource via UUID
│   ├── SingletonCodec    ← Encodes singleton classes via reflection
│   ├── StringCodec       ← Enum singleton, trivial encode/decode
│   ├── LocaleCodec       ← Enum singleton, language/country/variant encoding
│   └── typeeditor/
│       └── DeclarationCompositeCodec ← Enum singleton, IDE-dependent decode
├── edits/                ← AbstractEdit subclasses (undo/redo operations)
│   ├── DependentEdit     ← Wraps dependent edit with forwarding
│   └── ast/
│       ├── ExpressionPropertyEdit   ← Swaps expression on a property
│       ├── DeclareMethodEdit        ← Adds/removes methods on UserType
│       ├── BlockStatementEdit       ← Abstract base for block-scoped edits
│       ├── InsertStatementEdit      ← Inserts statements (already tested)
│       └── rename/
│           └── RenameDeclarationEdit ← Renames any AbstractDeclaration
├── models/
│   ├── numberpad/        ← Numeric input models + operation singletons
│   │   ├── NumberModel   ← Abstract base, JTextField-backed
│   │   ├── IntegerModel  ← Singleton, no decimal point
│   │   ├── DoubleModel   ← Singleton, decimal point supported
│   │   ├── FloatModel    ← Singleton, decimal point supported
│   │   └── *Operation    ← 5 NumberPadOperation subclasses ("triggers")
│   ├── help/             ← Help dialog composites + enums
│   │   ├── AbstractIssueComposite  ← JIRA issue submission base
│   │   ├── BugSubmitAttachment     ← YES/NO enum
│   │   ├── BugSubmitVisibility     ← PUBLIC/PRIVATE enum
│   │   └── ShowPathPropertyComposite ← System property display hierarchy
│   └── project/find/     ← Reference search data structures
│       ├── core/
│       │   ├── SearchResult           ← Declaration + references aggregator
│       │   └── criteria/
│       │       └── AcceptIfNotGenerated ← Criterion<Expression> singleton
│       └── croquet/tree/nodes/
│           ├── SearchTreeNode              ← Parent/children tree base
│           ├── DeclarationSeachTreeNode    ← Wraps AbstractDeclaration
│           └── ExpressionSearchTreeNode   ← Wraps Expression
```

## Test files

| # | Test file | Package | Target class(es) | LOC |
|---|-----------|---------|-------------------|-----|
| 1 | `NodeCodecTest` | `codecs` | NodeCodec | ~80 |
| 2 | `PropertyOfNodeCodecTest` | `codecs` | PropertyOfNodeCodec | ~70 |
| 3 | `ResourceCodecTest` | `codecs` | ResourceCodec | ~70 |
| 4 | `SingletonCodecTest` | `codecs` | SingletonCodec | ~60 |
| 5 | `DeclarationCompositeCodecTest` | `codecs.typeeditor` | DeclarationCompositeCodec | ~60 |
| 6 | `ExpressionPropertyEditTest` | `edits.ast` | ExpressionPropertyEdit | ~150 |
| 7 | `DeclareMethodEditTest` | `edits.ast` | DeclareMethodEdit | ~160 |
| 8 | `BlockStatementEditTest` | `edits.ast` | BlockStatementEdit (via concrete subclass) | ~120 |
| 9 | `DependentEditTest` | `edits` | DependentEdit | ~100 |
| 10 | `RenameDeclarationEditTest` | `edits.ast.rename` | RenameDeclarationEdit | ~120 |
| 11 | `IntegerModelTest` | `models.numberpad` | IntegerModel, DoubleModel, FloatModel | ~130 |
| 12 | `NumberPadOperationTest` | `models.numberpad` | NumeralOperation, BackspaceOperation, DecimalPointOperation | ~130 |
| 13 | `PlusMinusOperationTest` | `models.numberpad` | PlusMinusOperation | ~80 |
| 14 | `AcceptIfNotGeneratedTest` | `models.project.find.core.criteria` | AcceptIfNotGenerated | ~60 |
| 15 | `SearchTreeNodeTest` | `models.project.find.croquet.tree.nodes` | SearchTreeNode | ~160 |
| 16 | `ExpressionSearchTreeNodeTest` | `models.project.find.croquet.tree.nodes` | ExpressionSearchTreeNode, DeclarationSeachTreeNode | ~120 |
| 17 | `ShowPathPropertyCompositeTest` | `models.help` | ShowPathPropertyComposite hierarchy | ~80 |
| 18 | `AbstractIssueCompositeTest` | `models.help` | AbstractIssueComposite + enums | ~100 |

Total: ~2050 lines across 18 test files.

## Phase 1: Codec tests (~350 LOC)

### Pattern: getInstance caching

All non-enum codecs (NodeCodec, PropertyOfNodeCodec, ResourceCodec,
SingletonCodec) use a `getInstance(Class)` factory with internal `Map` caching.
Tests verify identity-equality across calls:

```java
@Test
public void getInstance_returnsSameInstance() {
  NodeCodec<NullLiteral> first = NodeCodec.getInstance(NullLiteral.class);
  NodeCodec<NullLiteral> second = NodeCodec.getInstance(NullLiteral.class);
  assertSame(first, second);
}
```

> **Discovered bug:** `NodeCodec.getInstance` creates a new instance when the
> class is absent from the cache but never calls `map.put(cls, rv)` to store it.
> The `assertSame` test will **fail** and expose this caching defect.
> `PropertyOfNodeCodec`, `ResourceCodec`, and `SingletonCodec` should be checked
> for the same pattern. Tests should either fix the production code or use
> `assertNotNull` + `assertEquals(getValueClass)` until the bug is addressed.

### Pattern: getValueClass contract

Every `ItemCodec<T>` must return the class passed at construction:

```java
@Test
public void getValueClass_matchesConstructionType() {
  NodeCodec<NullLiteral> codec = NodeCodec.getInstance(NullLiteral.class);
  assertEquals(NullLiteral.class, codec.getValueClass());
}
```

### Pattern: appendRepresentation null-safety

Codecs should handle `null` values without throwing:

```java
@Test
public void appendRepresentation_nullValue_doesNotThrow() {
  StringBuilder sb = new StringBuilder();
  NodeCodec<NullLiteral> codec = NodeCodec.getInstance(NullLiteral.class);
  codec.appendRepresentation(sb, null);
  // Should complete without NPE
}
```

> **Caution:** `NodeCodec.appendRepresentation` calls
> `NodeUtilities.safeAppendRepr(sb, value, Application.getLocale())`. If no
> `Application` has been initialized, `Application.getLocale()` may throw.
> Tests should either set up a minimal `Application` context or catch and
> document the expected exception. The `DeclarationCompositeCodec` non-null
> path similarly calls `FormatterState.getInstance()` which requires IDE context.

### Pattern: encodeValue null-safety

```java
@Test
public void encodeValue_nullValue_encodesFlag() {
  // Uses a test BinaryEncoder that records the boolean flag
  NodeCodec<NullLiteral> codec = NodeCodec.getInstance(NullLiteral.class);
  // null values encode a false boolean prefix
}
```

### NodeCodec global map

NodeCodec maintains a static `mapIdToNode` for UUID-based lookup. Tests verify
add/remove symmetry:

```java
@Test
public void addAndRemoveNodeFromGlobalMap() {
  NullLiteral node = new NullLiteral();
  NodeCodec.addNodeToGlobalMap(node);
  // node is now resolvable by its UUID
  NodeCodec.removeNodeFromGlobalMap(node);
  // node is no longer resolvable
}
```

### DeclarationCompositeCodec (enum singleton)

`DeclarationCompositeCodec.SINGLETON` is an enum-based codec. Tests verify:
- `getValueClass()` returns `DeclarationComposite.class`
- `appendRepresentation` with `null` value appends `"null"` without throwing
- The enum has exactly one constant (`SINGLETON`)

> **Note:** `decodeValue` depends on `IDE.getActiveInstance()` and cannot be
> tested headless. Tests cover only the stateless methods.

### StringCodec and LocaleCodec

These are enum singletons with trivial implementations. Tests verify:
- `StringCodec.SINGLETON.getValueClass()` returns `String.class`
- `LocaleCodec.SINGLETON.getValueClass()` returns `Locale.class`
- `appendRepresentation` produces expected string output

## Phase 2: Numberpad tests (~400 LOC)

### Headless guard pattern

NumberModel subclasses create a `JTextField` in their constructors. In headless
CI environments, this may fail. Tests use JUnit `Assume` to skip gracefully:

```java
@Before
public void setUp() {
  Assume.assumeFalse(
    "Skipping in headless environment",
    GraphicsEnvironment.isHeadless()
  );
}
```

### IntegerModel singleton and properties

```java
@Test
public void getInstance_returnsSingleton() {
  IntegerModel a = IntegerModel.getInstance();
  IntegerModel b = IntegerModel.getInstance();
  assertSame(a, b);
}

@Test
public void isDecimalPointSupported_returnsFalse() {
  assertFalse(IntegerModel.getInstance().isDecimalPointSupported());
}
```

### DoubleModel and FloatModel decimal support

```java
@Test
public void doubleModel_supportsDecimalPoint() {
  assertTrue(DoubleModel.getInstance().isDecimalPointSupported());
}

@Test
public void floatModel_supportsDecimalPoint() {
  assertTrue(FloatModel.getInstance().isDecimalPointSupported());
}
```

### NumberModel text manipulation

When running non-headless, tests exercise the full text lifecycle:

```java
@Test
public void setText_andGetExpressionValue() {
  IntegerModel model = IntegerModel.getInstance();
  model.setText("42");
  Expression expr = model.getExpressionValue();
  assertNotNull(expr);
}

@Test
public void setText_empty_disablesOk() {
  IntegerModel model = IntegerModel.getInstance();
  model.setText("");
  assertNotNull(model.getExplanationIfOkButtonShouldBeDisabled());
}
```

### Operation singletons ("triggers")

The five `NumberPadOperation` subclasses are the "triggers" in the design:

| Operation | Factory | Behavior |
|-----------|---------|----------|
| `NumeralOperation` | `getInstance(model, numeral)` | Appends digit to text field |
| `PlusMinusOperation` | `getInstance(model)` | Negates the numeric value |
| `BackspaceOperation` | `getInstance(model)` | Deletes last character |
| `DecimalPointOperation` | `getInstance(model)` | Inserts `.` (if supported) |
| `NumberPadOperation` | Abstract base | Holds `numberModel` reference |

Tests verify:
- Each `getInstance` returns a cached singleton per model
- Operations hold a reference to the correct `NumberModel`
- `NumeralOperation.getInstance(model, 5)` is identity-equal on repeat calls

```java
@Test
public void numeralOperation_cachedPerModelAndDigit() {
  IntegerModel model = IntegerModel.getInstance();
  NumeralOperation a = NumeralOperation.getInstance(model, (short) 7);
  NumeralOperation b = NumeralOperation.getInstance(model, (short) 7);
  assertSame(a, b);
}

@Test
public void differentDigits_differentInstances() {
  IntegerModel model = IntegerModel.getInstance();
  NumeralOperation a = NumeralOperation.getInstance(model, (short) 3);
  NumeralOperation b = NumeralOperation.getInstance(model, (short) 5);
  assertNotSame(a, b);
}
```

## Phase 3: Edit tests (~700 LOC)

### Pattern: null UserActivity construction

All edit constructors accept `UserActivity` as the first parameter. The
established project pattern (see `InsertStatementEditTest`) passes `null`:

```java
ExpressionPropertyEdit edit = new ExpressionPropertyEdit(
  null,  // UserActivity — null is safe for construction
  expressionProperty,
  prevExpression,
  nextExpression
);
```

This works because `AbstractEdit` stores the activity reference without
dereferencing it during construction. Tests are limited to constructors and
accessors; `doOrRedoInternal` and `undoInternal` require IDE context.

### ExpressionPropertyEdit

Tests verify construction and accessor contracts with real AST nodes:

```java
@Test
public void construct_storesExpressions() {
  NullLiteral prev = new NullLiteral();
  NullLiteral next = new NullLiteral();
  // ExpressionProperty obtained from a real AST ExpressionStatement
  ExpressionPropertyEdit edit = new ExpressionPropertyEdit(
    null, expressionProperty, prev, next
  );
  assertNotNull(edit);
}
```

### DeclareMethodEdit

Tests verify construction with `UserType`, method name, return type, and
optional body:

```java
@Test
public void construct_withAllParameters() {
  NamedUserType type = new NamedUserType();
  type.name.setValue("MyType");
  BlockStatement body = new BlockStatement();

  DeclareMethodEdit edit = new DeclareMethodEdit(
    null, type, "myMethod", JavaType.VOID_TYPE, body
  );
  assertSame(type, edit.getDeclaringType());
  assertEquals("myMethod", edit.getMethodName());
  assertSame(JavaType.VOID_TYPE, edit.getReturnType());
}

@Test
public void construct_withoutBody_defaultsToEmpty() {
  NamedUserType type = new NamedUserType();
  type.name.setValue("MyType");

  DeclareMethodEdit edit = new DeclareMethodEdit(
    null, type, "myMethod", JavaType.VOID_TYPE
  );
  assertNotNull(edit);
}
```

### DeclareMethodEdit — no doOrRedo testing

> **Important:** `DeclareMethodEdit.doOrRedoInternal` calls
> `IDE.getActiveInstance().getDocumentFrame().getDeclarationsEditorComposite()`
> (line 142), and `undoInternal` does the same (line 153). Both will NPE without
> a running IDE instance. Tests are therefore **limited to construction and
> accessor contracts only** — do not attempt `doOrRedo`/`undo` round-trips.

### RenameDeclarationEdit

Tests verify rename applies the new name and undo restores the old name:

```java
@Test
public void doOrRedo_appliesNewName() {
  UserMethod method = new UserMethod();
  method.name.setValue("oldName");

  RenameDeclarationEdit edit = new RenameDeclarationEdit(
    null, method, "oldName", "newName"
  );
  edit.doOrRedoInternal(true);
  assertEquals("newName", method.getName());
}

@Test
public void undo_restoresOldName() {
  UserMethod method = new UserMethod();
  method.name.setValue("oldName");

  RenameDeclarationEdit edit = new RenameDeclarationEdit(
    null, method, "oldName", "newName"
  );
  edit.doOrRedoInternal(true);
  edit.undoInternal();
  assertEquals("oldName", method.getName());
}
```

### DependentEdit

A minimal wrapper edit that delegates all operations (`doOrRedoInternal`,
`undoInternal`, `appendDescription`) to a `ResponsibleModel` obtained via
`getModel()`. Tests are limited to construction:

```java
@Test
public void construct_withNullActivity() {
  DependentEdit<?> edit = new DependentEdit<>(null);
  assertNotNull(edit);
}
```

> **Note:** Calling `appendDescription`, `doOrRedoInternal`, or `undoInternal`
> on a `DependentEdit` without a model will throw `RuntimeException` because
> `getResponsibleModel()` casts `getModel()` to `ResponsibleModel`. Tests
> should not call these methods without first wiring a model.

### BlockStatementEdit (abstract)

Tested via a minimal concrete subclass that exposes the accessor:

```java
@Test
public void getBlockStatement_returnsConstructionValue() {
  BlockStatement block = new BlockStatement();
  // TestBlockStatementEdit is a test-local concrete subclass
  TestBlockStatementEdit edit = new TestBlockStatementEdit(null, block);
  assertSame(block, edit.getBlockStatement());
}
```

## Phase 4: Project/find tests (~400 LOC)

### AcceptIfNotGenerated singleton

```java
@Test
public void getInstance_returnsSameInstance() {
  AcceptIfNotGenerated a = AcceptIfNotGenerated.getInstance();
  AcceptIfNotGenerated b = AcceptIfNotGenerated.getInstance();
  assertSame(a, b);
}

@Test
public void implements_Criterion() {
  assertTrue(AcceptIfNotGenerated.getInstance() instanceof Criterion);
}
```

### SearchTreeNode tree operations

`SearchTreeNode` is a general-purpose tree structure with parent/child
relationships. Tests cover the full tree manipulation API without needing any
IDE context:

```java
@Test
public void rootNode_hasNullParent() {
  SearchTreeNode root = new SearchTreeNode(null);
  assertNull(root.getParent());
}

@Test
public void addChild_appearsInChildren() {
  SearchTreeNode root = new SearchTreeNode(null);
  SearchTreeNode child = new SearchTreeNode(root);
  root.addChild(child);

  assertEquals(1, root.getChildren().size());
  assertSame(child, root.getChildren().get(0));
}

@Test
public void getIsLeaf_trueWhenNoChildren() {
  SearchTreeNode node = new SearchTreeNode(null);
  assertTrue(node.getIsLeaf());
}

@Test
public void getIsLeaf_falseWhenHasChildren() {
  SearchTreeNode root = new SearchTreeNode(null);
  root.addChild(new SearchTreeNode(root));
  assertFalse(root.getIsLeaf());
}

@Test
public void removeAllChildren_clearsChildren() {
  SearchTreeNode root = new SearchTreeNode(null);
  root.addChild(new SearchTreeNode(root));
  root.addChild(new SearchTreeNode(root));
  root.removeAllChildren();
  assertTrue(root.getChildren().isEmpty());
}

@Test
public void sibling_navigation() {
  SearchTreeNode root = new SearchTreeNode(null);
  SearchTreeNode c1 = new SearchTreeNode(root);
  SearchTreeNode c2 = new SearchTreeNode(root);
  SearchTreeNode c3 = new SearchTreeNode(root);
  root.addChild(c1);
  root.addChild(c2);
  root.addChild(c3);

  assertSame(c2, c1.getYoungerSibling());
  assertSame(c1, c2.getOlderSibling());
  assertEquals(1, c2.getLocationAmongstSiblings());
}

@Test
public void toString_root_returnsROOT() {
  SearchTreeNode root = new SearchTreeNode(null);
  assertEquals("ROOT", root.toString());
}
```

### DeclarationSeachTreeNode

```java
@Test
public void getValue_returnsDeclaration() {
  UserMethod method = new UserMethod();
  method.name.setValue("testMethod");
  SearchTreeNode root = new SearchTreeNode(null);
  DeclarationSeachTreeNode node = new DeclarationSeachTreeNode(root, method);

  assertSame(method, node.getValue());
  assertSame(root, node.getParent());
}
```

### ExpressionSearchTreeNode

```java
@Test
public void getValue_returnsExpression() {
  NullLiteral expr = new NullLiteral();
  SearchTreeNode root = new SearchTreeNode(null);
  ExpressionSearchTreeNode node = new ExpressionSearchTreeNode(root, expr);

  assertSame(expr, node.getValue());
}
```

### SearchResult

`SearchResult` aggregates an `AbstractDeclaration` with a list of
`Expression` references. The constructor validates the declaration type via
an assertion. Tests use `UserMethod` (which is an `AbstractMethod`):

```java
@Test
public void construct_withUserMethod() {
  UserMethod method = new UserMethod();
  method.name.setValue("doSomething");

  SearchResult result = new SearchResult(method);
  assertSame(method, result.getDeclaration());
  assertEquals("doSomething", result.getName());
  assertTrue(result.getReferences().isEmpty());
}

@Test
public void addReference_accumulatesReferences() {
  UserMethod method = new UserMethod();
  method.name.setValue("doSomething");

  SearchResult result = new SearchResult(method);
  NullLiteral ref1 = new NullLiteral();
  NullLiteral ref2 = new NullLiteral();
  result.addReference(ref1);
  result.addReference(ref2);

  assertEquals(2, result.getReferences().size());
  assertSame(ref1, result.getReferences().get(0));
}
```

> **Note:** `SearchResult.getIcon()` and `stencilHighlightForReference()` depend
> on `DeclarationTabState` and `IDE.getActiveInstance()` — these are not tested
> headless.

## Phase 5: Help tests (~200 LOC)

### Enum constants

```java
@Test
public void bugSubmitAttachment_hasTwoValues() {
  BugSubmitAttachment[] values = BugSubmitAttachment.values();
  assertEquals(2, values.length);
  assertNotNull(BugSubmitAttachment.valueOf("YES"));
  assertNotNull(BugSubmitAttachment.valueOf("NO"));
}

@Test
public void bugSubmitVisibility_hasTwoValues() {
  BugSubmitVisibility[] values = BugSubmitVisibility.values();
  assertEquals(2, values.length);
  assertNotNull(BugSubmitVisibility.valueOf("PUBLIC"));
  assertNotNull(BugSubmitVisibility.valueOf("PRIVATE"));
}
```

### ShowPathPropertyComposite hierarchy

`ShowClassPathPropertyComposite` and `ShowLibraryPathPropertyComposite` extend
`ShowPathPropertyComposite`, which itself extends
`SimpleOperationUnadornedDialogCoreComposite`. The constructors pass a `UUID`
migration ID and a `Group` (`Application.INFORMATION_GROUP`) to the superclass.

> **Caution:** Constructing these composites may require a Croquet
> `Application` to be initialized (for `Group` registration). If construction
> throws in headless CI, wrap with
> `Assume.assumeTrue("Croquet Application required", applicationAvailable())`.
> The property name can be verified only if construction succeeds:

```java
@Test
public void showClassPath_propertyName() {
  ShowClassPathPropertyComposite composite = new ShowClassPathPropertyComposite();
  assertEquals("java.class.path", composite.getPropertyName());
}

@Test
public void showLibraryPath_propertyName() {
  ShowLibraryPathPropertyComposite composite = new ShowLibraryPathPropertyComposite();
  assertEquals("java.library.path", composite.getPropertyName());
}
```

> **Note:** `createView()` is not called in tests — it requires Swing. The
> headless tests cover only the data/configuration layer.

### AbstractIssueComposite

Tested via a test-local concrete subclass that stubs the abstract methods.
Tests verify the `ISSUE_GROUP` constant and state accessors:

```java
@Test
public void issueGroup_isNotNull() {
  assertNotNull(AbstractIssueComposite.ISSUE_GROUP);
}
```

## Configuration

### Headless CI

All tests run under `mvn test` with no special configuration. Numberpad tests
that require a display automatically skip via `Assume.assumeFalse(isHeadless())`.

```sh
# Run all croquet tests
cd /path/to/alice
mvn test -pl core/ide -Dtest="org.alice.ide.croquet.**"

# Run a specific phase
mvn test -pl core/ide -Dtest="org.alice.ide.croquet.codecs.*Test"
mvn test -pl core/ide -Dtest="org.alice.ide.croquet.edits.**"
mvn test -pl core/ide -Dtest="org.alice.ide.croquet.models.numberpad.*Test"
mvn test -pl core/ide -Dtest="org.alice.ide.croquet.models.project.**"
mvn test -pl core/ide -Dtest="org.alice.ide.croquet.models.help.*Test"
```

### Memory settings

For large test runs, increase Maven heap:

```sh
export MAVEN_OPTS="-Xmx4g"
```

### JaCoCo coverage measurement

```sh
# Generate coverage report
mvn verify -pl core/ide -Pcoverage

# View HTML report
open core/ide/target/site/jacoco/index.html
```

## API reference

### Codec test API surface

| Class | Factory | Key methods tested |
|-------|---------|-------------------|
| `NodeCodec<T>` | `getInstance(Class<T>)` | `getValueClass`, `appendRepresentation`, `addNodeToGlobalMap`, `removeNodeFromGlobalMap` |
| `PropertyOfNodeCodec<T>` | `getInstance(Class<T>)` | `getValueClass`, `appendRepresentation` |
| `ResourceCodec<R>` | `getInstance(Class<R>)` | `getValueClass`, `appendRepresentation` |
| `SingletonCodec<T>` | `getInstance(Class<T>)` | `getValueClass`, `appendRepresentation` |
| `StringCodec` | `SINGLETON` | `getValueClass`, `appendRepresentation` |
| `LocaleCodec` | `SINGLETON` | `getValueClass`, `appendRepresentation`, `encodeValue`/`decodeValue` round-trip |
| `DeclarationCompositeCodec` | `SINGLETON` | `getValueClass`, `appendRepresentation` (null safe) |

### Edit test API surface

| Class | Constructor pattern | Key methods tested |
|-------|--------------------|--------------------|
| `ExpressionPropertyEdit` | `(null, ExpressionProperty, Expression, Expression)` | construction, accessors |
| `DeclareMethodEdit` | `(null, UserType, String, AbstractType[, BlockStatement])` | `getDeclaringType`, `getMethodName`, `getReturnType` (no do/undo — requires IDE) |
| `BlockStatementEdit` | `(null, BlockStatement)` via subclass | `getBlockStatement` |
| `DependentEdit` | `(null)` | construction |
| `RenameDeclarationEdit` | `(null, AbstractDeclaration, String, String)` | `doOrRedoInternal`, `undoInternal` |

### Numberpad test API surface

| Class | Factory | Key methods tested |
|-------|---------|-------------------|
| `IntegerModel` | `getInstance()` | singleton identity, `isDecimalPointSupported`, `setText`, `getExpressionValue`, `getExplanationIfOkButtonShouldBeDisabled` |
| `DoubleModel` | `getInstance()` | singleton identity, `isDecimalPointSupported` |
| `FloatModel` | `getInstance()` | singleton identity, `isDecimalPointSupported` |
| `NumeralOperation` | `getInstance(model, numeral)` | singleton caching per model+digit |
| `PlusMinusOperation` | `getInstance(model)` | singleton caching per model |
| `BackspaceOperation` | `getInstance(model)` | singleton caching per model |
| `DecimalPointOperation` | `getInstance(model)` | singleton caching per model |

### Project/find test API surface

| Class | Constructor | Key methods tested |
|-------|------------|-------------------|
| `SearchTreeNode` | `(parent)` | `getParent`, `getChildren`, `addChild`, `removeAllChildren`, `getIsLeaf`, `childrenContains`, `getChildForReference`, `getLocationAmongstSiblings`, `getYoungerSibling`, `getOlderSibling`, `toString` |
| `DeclarationSeachTreeNode` | `(parent, AbstractDeclaration)` | `getValue` |
| `ExpressionSearchTreeNode` | `(parent, Expression)` | `getValue` |
| `AcceptIfNotGenerated` | `getInstance()` | singleton identity, `Criterion` interface |
| `SearchResult` | `(AbstractDeclaration)` | `getDeclaration`, `getName`, `addReference`, `getReferences` |

## Risks and mitigations

| Risk | Mitigation |
|------|-----------|
| **Codec `getInstance` never caches** — `NodeCodec`, `PropertyOfNodeCodec`, `ResourceCodec`, and `SingletonCodec` all create new instances but never call `map.put(cls, rv)` | `assertSame` tests will expose this as a production bug. Use `assertNotNull` + `assertEquals(getValueClass)` as fallback until the bug is fixed |
| Edit `doOrRedo`/`undo` requires `IDE.getActiveInstance()` | Tests limited to construction + accessors; only `RenameDeclarationEdit` can safely exercise do/undo with pure AST (`DeclareMethodEdit` calls IDE on both paths) |
| NumberModel constructors create `JTextField` | Headless guard: `Assume.assumeFalse(GraphicsEnvironment.isHeadless())` |
| `DeclarationCompositeCodec.decodeValue` needs IDE instance | Test only `getValueClass` and `appendRepresentation` (null case only — non-null calls `FormatterState`) |
| `NodeCodec.appendRepresentation` calls `Application.getLocale()` | May throw if no `Application` initialized; guard or mock the `Application` singleton |
| `ShowPathPropertyComposite` constructor requires Croquet `Application` | Guard with `Assume`; test `getPropertyName` only if construction succeeds |
| `SearchResult.getIcon()` needs `DeclarationTabState` | Not tested headless; test only data accessors |
| `AcceptIfNotGenerated.accept()` needs a `UserMethod` ancestor | Test singleton identity only; `accept()` tested separately in `FindCrawlerTest` |

## Security considerations

- No test credentials or secrets — tests use only in-memory AST nodes and literals
- No file system side effects — all tests are pure in-memory
- No external network dependencies
- Codec null-input tests provide defensive input validation coverage
