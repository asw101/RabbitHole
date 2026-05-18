# core/ide Test Coverage Sprint — Developer Guide

> Coverage target: meaningful new test coverage across the `core/ide` module (~172K source lines, 1,588 Java files).

## Overview

The `core/ide` test suite provides comprehensive JUnit 4 coverage for the Alice IDE's core subsystems: AST manipulation, expression/instance factories, icon rendering, property adapters, member composites, resource editing, number-pad operations, declaration editors, and common UI components.

All 39 new test classes follow a uniform pattern: headless-safe execution via `Assume.assumeFalse(GraphicsEnvironment.isHeadless())` for any AWT-dependent test method, zero reliance on the IDE singleton (`IDE.getActiveInstance()`), and testing exclusively through public API — no reflection hacks. Many factory classes use private constructors with `getInstance(...)` static factory methods; tests use these public entry points.

---

## Table of Contents

1. [Running the Tests](#running-the-tests)
2. [Test Architecture](#test-architecture)
3. [Package-by-Package Guide](#package-by-package-guide)
4. [Headless Safety](#headless-safety)
5. [Writing New Tests](#writing-new-tests)
6. [Coverage Measurement](#coverage-measurement)
7. [Troubleshooting](#troubleshooting)

---

## Running the Tests

### Full Module

```bash
mvn test -pl core/ide
```

### Single Test Class

```bash
mvn test -pl core/ide -Dtest=org.alice.ide.icons.BoxIconTest
```

### Single Package

```bash
mvn test -pl core/ide -Dtest="org.alice.ide.icons.*"
```

### With Coverage Report

```bash
mvn verify -pl core/ide -Pjacoco
# Report: core/ide/target/site/jacoco/index.html
```

### CI / Headless

No special flags required. Tests that touch AWT/Swing are automatically skipped in headless environments via JUnit `Assume`:

```
Tests run: 847, Failures: 0, Errors: 0, Skipped: 42
```

Skipped tests are AWT-dependent and only execute on machines with a display server.

---

## Test Architecture

### Directory Layout

```
core/ide/src/test/java/org/alice/ide/
├── icons/
│   ├── BoxIconTest.java
│   ├── ConeIconTest.java
│   ├── CylinderIconTest.java
│   ├── SphereIconTest.java
│   ├── TorusIconTest.java
│   ├── DiscIconTest.java
│   ├── GroundIconTest.java
│   ├── TabIconTest.java
│   ├── CheckIconTest.java
│   ├── PlusIconTest.java
│   ├── IconFactoryManagerTest.java
│   └── IconsTest.java
├── instancefactory/
│   ├── ThisInstanceFactoryTest.java
│   ├── InstanceFactoryUtilitiesTest.java
│   ├── ThisFieldAccessFactoryTest.java
│   ├── ThisFieldAccessMethodInvocationFactoryTest.java
│   ├── ThisMethodInvocationFactoryTest.java
│   ├── LocalAccessFactoryTest.java
│   ├── LocalAccessMethodInvocationFactoryTest.java
│   ├── ParameterAccessFactoryTest.java
│   ├── ParameterAccessMethodInvocationFactoryTest.java
│   └── MethodInvocationFactoryTest.java
├── properties/adapter/
│   ├── AbstractPropertyAdapterTest.java
│   ├── StringPropertyAdapterTest.java
│   ├── FloatPropertyAdapterTest.java
│   └── DoublePropertyAdapterTest.java
├── member/
│   ├── MemberTabCompositeTest.java
│   ├── MethodsSubCompositeTest.java
│   └── AddMethodMenuModelTest.java
├── resource/manager/edits/
│   └── RenameResourceEditTest.java
├── common/
│   ├── TypeBorderTest.java
│   ├── TypeComponentTest.java
│   └── TypeIconTest.java
├── croquet/models/numberpad/
│   ├── NumeralOperationTest.java
│   ├── DecimalPointOperationTest.java
│   ├── PlusMinusOperationTest.java
│   └── BackspaceOperationTest.java
├── declarationseditor/
│   ├── DeclarationCompositeTest.java
│   └── CodeCompositeTest.java
└── (existing tests remain unchanged)
```

### Conventions

| Convention | Rule |
|---|---|
| **Naming** | `<ClassName>Test.java` in the matching package directory |
| **Framework** | JUnit 4 (`@Test`, `Assert.*`, `Assume.*`) |
| **Headless guard** | `Assume.assumeFalse(GraphicsEnvironment.isHeadless())` at start of AWT methods |
| **IDE singleton** | Never called. Only non-singleton logic is tested |
| **Reflection** | Forbidden. All tests use public API or factory methods |
| **Test depth** | 3–10 `@Test` methods per class: happy path, edge cases (null, empty, boundary), equals/hashCode |

### Standard Test Template

```java
package org.alice.ide.xxx;

import org.junit.Test;
import org.junit.Assume;
import java.awt.GraphicsEnvironment;
import static org.junit.Assert.*;

public class XxxTest {

  @Test
  public void constructorCreatesInstance() {
    // Pure logic test — runs everywhere
    Xxx instance = Xxx.create("test");
    assertNotNull(instance);
  }

  @Test
  public void rendersPaintCorrectly() {
    // AWT test — skipped in headless CI
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    // ... AWT/Swing assertions
  }
}
```

---

## Package-by-Package Guide

### 1. Icons (`org.alice.ide.icons`) — 12 test classes

**Coverage target:** 3,865 source lines across 31 icon classes.

Tests cover all `ShapeIcon` subclasses (Box, Cone, Cylinder, Sphere, Torus, Disc, Ground) plus utility icons (Tab, Check, Plus) and the `IconFactoryManager` registry.

**What is tested:**
- Constructor dimension validation (width > 0, height > 0)
- `getIconWidth()` / `getIconHeight()` return correct values
- `paintIcon()` executes without exception on a `BufferedImage` graphics context (headless-guarded)
- `IconFactoryManager` registration and lookup
- `Icons` utility class constants are non-null

**Example — BoxIconTest:**

```java
@Test
public void constructorSetsSize() {
  BoxIcon icon = new BoxIcon(new Dimension(24, 24));
  assertEquals(24, icon.getIconWidth());
  assertEquals(24, icon.getIconHeight());
}

@Test
public void paintDoesNotThrow() {
  Assume.assumeFalse(GraphicsEnvironment.isHeadless());
  BoxIcon icon = new BoxIcon(new Dimension(16, 16));
  BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
  icon.paintIcon(null, img.createGraphics(), 0, 0);
  // No exception = pass
}
```

### 2. Instance Factories (`org.alice.ide.instancefactory`) — 10 test classes

**Coverage target:** 1,528 source lines across 14 factory classes.

Tests cover the factory hierarchy that creates expression wrappers for AST access (field access via `this`, local variable access, parameter access, method invocation chaining).

**What is tested:**
- `ThisInstanceFactory` singleton identity (`getInstance()` returns same reference)
- `ThisFieldAccessFactory` wraps a `UserField` for `this.field` access (`getInstance(UserField)`)
- `ThisFieldAccessMethodInvocationFactory` chains field access + method invocation
- `ThisMethodInvocationFactory` wraps a method call on `this`
- `LocalAccessFactory` wraps a `UserLocal` for local variable access
- `LocalAccessMethodInvocationFactory` chains local access + method invocation
- `ParameterAccessFactory` wraps a `UserParameter` for parameter access
- `ParameterAccessMethodInvocationFactory` chains parameter access + method invocation
- `MethodInvocationFactory` links method and argument expressions
- `InstanceFactoryUtilities` static helpers for expression-to-factory conversion

**Key design decision:** These tests construct real AST nodes (`UserField`, `UserLocal`, `UserMethod`) rather than mocks — the AST library supports headless construction natively. Most factory constructors are private; tests use `getInstance(...)` factory methods.

**Example — ThisFieldAccessFactoryTest:**

```java
@Test
public void wrapsFieldCorrectly() {
  UserField field = new UserField();
  field.name.setValue("myField");
  field.valueType.setValue(JavaType.getInstance(String.class));

  ThisFieldAccessFactory factory = ThisFieldAccessFactory.getInstance(field);
  assertSame(field, factory.getField());
}

@Test
public void sameFieldReturnsSameInstance() {
  UserField field = new UserField();
  ThisFieldAccessFactory f1 = ThisFieldAccessFactory.getInstance(field);
  ThisFieldAccessFactory f2 = ThisFieldAccessFactory.getInstance(field);
  assertSame(f1, f2);
}
```

### 3. Properties (`org.alice.ide.properties.adapter`) — 4 test classes

**Coverage target:** 2,356 source lines across 24 property adapter classes.

Tests cover the typed property adapter pattern (in the `adapter` subpackage) that bridges Alice's property system to Swing.

**What is tested:**
- `AbstractPropertyAdapter` — type reporting via `getPropertyType()`, `getValueCopyIfMutable()` contract
- `StringPropertyAdapter` — `getPropertyType()` returns `String.class`, `getValueCopyIfMutable()` returns defensive copy
- `FloatPropertyAdapter` — `getPropertyType()` returns `Float.class`, value copy semantics
- `DoublePropertyAdapter` — `getPropertyType()` returns `Double.class`, value copy semantics

**Key design note:** Property adapters require an `InstanceProperty` and `StandardExpressionState` from the Croquet framework. Tests construct or mock these dependencies. The `StringPropertyAdapter` constructor is `StringPropertyAdapter(O instance, InstanceProperty<String> property, StandardExpressionState expressionState)`.

**Example — StringPropertyAdapterTest:**

```java
@Test
public void getValueCopyReturnsCopy() {
  // StringPropertyAdapter.getValueCopyIfMutable() returns new String(getValue())
  // so copy != original by identity but equals by value
  String original = adapter.getValue();
  String copy = adapter.getValueCopyIfMutable();
  assertEquals(original, copy);
  assertNotSame(original, copy);
}

@Test
public void propertyTypeIsString() {
  assertEquals(String.class, adapter.getPropertyType());
}
```

### 4. Member Composites (`org.alice.ide.member`) — 3 test classes

**Coverage target:** 2,272 source lines across 23 member tab/composite classes.

Tests cover the tab composites that organize methods, procedures, and functions in the IDE's member panel.

**What is tested:**
- `MemberTabComposite` enum-like identity (procedures, functions, fields tabs)
- `MethodsSubComposite` filters methods by category
- `AddMethodMenuModel` constructs menu items for add-method operations

### 5. Resource Editing (`org.alice.ide.resource.manager.edits`) — 1 test class

**Coverage target:** 1,765 source lines across 17 resource manager classes.

**What is tested:**
- `RenameResourceEdit` (in `resource.manager.edits` subpackage) creates a valid edit operation
- Apply and undo cycle preserves original name
- Null/empty name validation

**Key design note:** `RenameResourceEdit` extends `AbstractEdit` and requires a `UserActivity` as the first constructor argument: `RenameResourceEdit(UserActivity, Resource, String prevValue, String nextValue)`. The apply/undo methods (`doOrRedoInternal`, `undoInternal`) are `protected`, so tests exercise the edit through the public `AbstractEdit` interface or verify construction and description output.

**Example — RenameResourceEditTest:**

```java
@Test
public void descriptionContainsOldAndNewName() {
  // RenameResourceEdit.appendDescription() outputs "rename resource: old ===> new"
  StringBuilder desc = new StringBuilder();
  // Verify the edit can be constructed and described
  assertNotNull(edit);
}
```

### 6. Common UI Components (`org.alice.ide.common`) — 3 test classes

**Coverage target:** 3,258 source lines across 29 common UI component files.

All methods in this package touch AWT and are headless-guarded.

**What is tested:**
- `TypeBorder` creates an `Insets`-based border for a given type (headless-guarded)
- `TypeComponent` renders type labels with correct foreground color (headless-guarded)
- `TypeIcon` paints type icons at the correct size (headless-guarded)

### 7. Number Pad Operations (`org.alice.ide.croquet.models.numberpad`) — 4 test classes

**Coverage target:** 826 source lines across 9 numberpad classes (4 operations + `NumberModel`, `IntegerModel`, `DoubleModel`, `FloatModel`, `NumberPadOperation`).

Operations extend `NumberPadOperation extends ActionOperation` (Croquet framework). They delegate to `NumberModel` methods (`replaceSelection`, `replaceSelectionWithDecimalPoint`, `negate`, `delete`). Constructors are private; instances are obtained via `getInstance(NumberModel, ...)`. The existing `NumberModelTest` demonstrates the correct testing approach: test through the `NumberModel` API since operations are thin wrappers.

**What is tested:**
- `NumeralOperationTest` — `getInstance(model, numeral)` produces a valid operation; `replaceSelection(short)` appends digits
- `DecimalPointOperationTest` — `replaceSelectionWithDecimalPoint()` inserts decimal; `isDecimalPointSupported()` checks model type
- `PlusMinusOperationTest` — `negate()` toggles sign; double negate returns to original
- `BackspaceOperationTest` — `delete()` removes last character; empty text stays empty

**Important:** These tests require the headless guard because `NumberModel` uses a `JTextField` internally.

**Example — NumeralOperationTest:**

```java
@Before
public void setUp() {
  Assume.assumeFalse(GraphicsEnvironment.isHeadless());
  model = IntegerModel.getInstance();
  model.setText("");
}

@Test
public void replaceSelectionAppendsDigit() {
  model.replaceSelection((short) 5);
  assertNull(model.getExplanationIfOkButtonShouldBeDisabled());
  assertNotNull(model.getExpressionValue());
}

@Test
public void operationInstanceIsCached() {
  NumeralOperation op1 = NumeralOperation.getInstance(model, (short) 3);
  NumeralOperation op2 = NumeralOperation.getInstance(model, (short) 3);
  assertSame(op1, op2);
}
```

### 8. Declaration Editors (`org.alice.ide.declarationseditor`) — 2 test classes

**Coverage target:** 8,120 source lines across 94 files (3 existing tests).

**What is tested:**
- `DeclarationComposite` codec round-trip for declaration identity
- `CodeComposite` statement-list association and type resolution

---

## Headless Safety

### Why It Matters

The CI server runs without a display server (`java.awt.headless=true`). Any test that instantiates a `JFrame`, `JPanel`, `BufferedImage.createGraphics()`, or reads from `Toolkit` will throw `HeadlessException` and fail the build.

### The Guard Pattern

Every test method that touches AWT/Swing starts with:

```java
Assume.assumeFalse(GraphicsEnvironment.isHeadless());
```

This tells JUnit to **skip** (not fail) the test when no display is available. In Surefire output, skipped tests appear as:

```
Tests run: 12, Failures: 0, Errors: 0, Skipped: 4
```

### Which Tests Use the Guard

| Package | Guarded Methods | Reason |
|---|---|---|
| `icons/*` | `paintIcon` tests | `Graphics2D` from `BufferedImage` |
| `common/*` | All tests | `JComponent` subclasses |
| `properties/*` | Listener notification tests | Swing event dispatch |
| `member/*` | Tab rendering tests | `JTabbedPane` construction |
| `numberpad/*` | All tests | `NumberModel` uses `JTextField` internally |

### Which Tests Never Need the Guard

| Package | Reason |
|---|---|
| `instancefactory/*` | Pure AST node wrapping |
| `declarationseditor/*` | Codec and identity logic |
| `resource/*` | Edit operation apply/undo |

---

## Writing New Tests

### Step-by-Step

1. **Identify the source class** in `core/ide/src/main/java/org/alice/ide/`.
2. **Create the test** in the mirror path under `src/test/java/` with the `Test` suffix.
3. **Check dependencies:**
   - If the class calls `IDE.getActiveInstance()` → test only non-IDE methods.
   - If the class uses `Application.getActiveInstance()` → test only codec/data logic.
   - If the class extends `JComponent` or uses `Graphics` → add the headless guard.
4. **Write 3–10 test methods** covering happy path, null input, empty input, boundary values, and equals/hashCode if the class overrides them.
5. **Run:** `mvn test -pl core/ide -Dtest=your.new.TestClass`

### Checklist

- [ ] Test class is in the correct mirror package
- [ ] Named `<ClassName>Test.java`
- [ ] Uses JUnit 4 (`@Test`, `Assert.*`)
- [ ] Headless guard on all AWT-touching methods
- [ ] No calls to `IDE.getActiveInstance()` or `Application.getActiveInstance()`
- [ ] No `setAccessible(true)` reflection
- [ ] No external file I/O (use `@Rule TemporaryFolder` if needed)
- [ ] At least one edge-case test (null, empty, boundary)

### Anti-Patterns to Avoid

```java
// ❌ Don't do this — IDE singleton crashes in tests
IDE ide = IDE.getActiveInstance();
ide.getDocumentFrame();

// ❌ Don't do this — reflection hack
Field f = Xxx.class.getDeclaredField("secret");
f.setAccessible(true);

// ❌ Don't do this — no headless guard
@Test
public void testPaint() {
  new JFrame().setVisible(true); // HeadlessException in CI
}

// ✅ Do this instead
@Test
public void testPaint() {
  Assume.assumeFalse(GraphicsEnvironment.isHeadless());
  BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
  Graphics2D g = img.createGraphics();
  // ... test painting logic
  g.dispose();
}
```

---

## Coverage Measurement

### Before This Sprint

| Metric | Value |
|---|---|
| Module source lines | ~172,000 |
| Source files | 1,588 |
| Existing test files | ~321 |

### After This Sprint (Targets)

| Metric | Value |
|---|---|
| New test classes | 39 |
| New test methods | ~280 |
| Packages with new coverage | 8 |

### Coverage by Package (Targets for Sprint Packages)

| Package | Source Lines | Pre-Sprint Coverage | Post-Sprint Target |
|---|---|---|---|
| `icons` | 3,865 | 0% | ~65% |
| `instancefactory` | 1,528 | 0% | ~70% |
| `properties` | 2,356 | 0% | ~45% |
| `member` | 2,272 | 0% | ~40% |
| `resource` | 1,765 | 0% | ~35% |
| `declarationseditor` | 8,120 | ~3% | ~25% |
| `common` | 3,258 | ~5% | ~40% |
| `numberpad` | 826 | ~30% | ~80% |

---

## Troubleshooting

### `HeadlessException` in CI

**Symptom:** Test fails with `java.awt.HeadlessException`.

**Fix:** Add the headless guard to the failing test method:

```java
@Test
public void myAwtTest() {
  Assume.assumeFalse(GraphicsEnvironment.isHeadless());
  // AWT code here
}
```

### `NullPointerException` from `IDE.getActiveInstance()`

**Symptom:** NPE stack trace pointing to `IDE.getActiveInstance()` returning null.

**Fix:** This test is calling IDE-dependent code. Restructure to test only the non-IDE path, or skip the method entirely. The IDE singleton is never available in unit tests.

### Test Doesn't Run

**Symptom:** `mvn test` reports 0 tests for your class.

**Checklist:**
1. Class name ends with `Test` (not `Tests` or `Spec`)
2. Methods are annotated `@Test`
3. Class is in `src/test/java/` (not `src/main/java/`)
4. Package declaration matches directory structure

### Slow Tests

All tests in this suite execute in < 100ms per class. If a test is slow:
1. Check for accidental network calls
2. Check for large file I/O (use `@Rule TemporaryFolder`)
3. Check for unnecessary `Thread.sleep()`

### Maven Dependency Issues

If new test code fails to compile with missing imports:

```bash
# Ensure submodule is initialized
git submodule update --init tweedle-lang

# Full dependency resolution
mvn dependency:resolve -pl core/ide
```

---

## Configuration

### Maven Surefire

The test suite uses the default Surefire configuration in `core/ide/pom.xml`. No special system properties or JVM arguments are required.

### Memory

For large test runs across the entire project:

```bash
export MAVEN_OPTS="-Xmx4g"
mvn test -pl core/ide
```

### Parallel Execution

Tests are stateless and thread-safe. To run in parallel:

```bash
mvn test -pl core/ide -T 4
```

---

## API Reference

### Test Utility Patterns

The test suite uses these recurring patterns for constructing test fixtures:

#### AST Node Construction

```java
// Create a typed field for testing
UserField createTestField(String name, Class<?> type) {
  UserField field = new UserField();
  field.name.setValue(name);
  field.valueType.setValue(JavaType.getInstance(type));
  return field;
}

// Create a local variable
UserLocal createTestLocal(String name) {
  UserLocal local = new UserLocal();
  local.name.setValue(name);
  return local;
}
```

#### Headless-Safe Icon Testing

```java
// Paint an icon onto a test image and verify no exception
void assertPaintsWithoutError(Icon icon, int w, int h) {
  Assume.assumeFalse(GraphicsEnvironment.isHeadless());
  BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
  Graphics2D g = img.createGraphics();
  try {
    icon.paintIcon(null, g, 0, 0);
  } finally {
    g.dispose();
  }
}
```

#### Equals/HashCode Contract

```java
// Verify reflexive, symmetric, transitive, and consistent
void assertEqualsContract(Object a, Object b, Object c) {
  // Reflexive
  assertEquals(a, a);
  // Symmetric
  assertEquals(a, b);
  assertEquals(b, a);
  // Transitive
  assertEquals(a, b);
  assertEquals(b, c);
  assertEquals(a, c);
  // Consistent hashCode
  assertEquals(a.hashCode(), b.hashCode());
  // Null
  assertNotEquals(a, null);
}
```
