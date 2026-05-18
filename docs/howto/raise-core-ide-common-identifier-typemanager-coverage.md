# Raise core/ide common, identifier, and typemanager test coverage

Use this guide to understand, run, extend, and verify the test suite for the
`org.alice.ide.common`, `org.alice.ide.identifier`, and
`org.alice.ide.typemanager` packages.

## Prerequisites

```sh
git submodule update --init tweedle-lang
```

Verify that `java -version` reports JDK 17+. The tests use JUnit 4 via the
parent POM's `junit` dependency.

## Run the tests

### Quick check — all three packages

```sh
mvn test -pl core/ide -am \
  -Dtest="org.alice.ide.common.*,org.alice.ide.identifier.*,org.alice.ide.typemanager.*"
```

The `-am` flag builds upstream dependencies (`core/ast`, `core/util`,
`core/story-api`, etc.). Expect approximately 1–2 minutes.

### Run only the new test files

```sh
mvn test -pl core/ide -am \
  -Dtest="ResourceTypeUtilitiesTest,ResourceTypeUtilitiesEdgeCaseTest,TypeManagerExtendedTest,IdentifierNameGeneratorComprehensiveTest,TypeBorderTest,BeveledShapeForTypeComprehensiveTest,TypeIconTest,TypeComponentTest,ExpressionCreatorPaneTest"
```

### Run a single test file

```sh
mvn test -pl core/ide -am -Dtest=ResourceTypeUtilitiesTest
```

### Full coverage report

```sh
mvn -DincludeSims=false -Dinstall4j.skip -Pcoverage verify
```

Open the per-module HTML report:

```sh
open core/ide/target/site/jacoco/index.html
```

Navigate to the `org.alice.ide.common`, `org.alice.ide.identifier`, or
`org.alice.ide.typemanager` package rows to see per-class coverage.

## Test architecture by package

### typemanager tests

**ResourceTypeUtilitiesTest** tests all branches of `getResourceType()` and
`getResourceFieldOrType()` by programmatically constructing `NamedUserType`
AST trees with varying constructor shapes:

```java
// Build a NamedUserType with one constructor, zero params,
// one super-arg containing a FieldAccess
NamedUserType type = new NamedUserType();
type.name.setValue("TestBiped");
type.superType.setValue(JavaType.getInstance(SBiped.class));

NamedUserConstructor constructor = new NamedUserConstructor();
ConstructorBlockStatement body = new ConstructorBlockStatement();
SuperConstructorInvocationStatement superInvocation =
    new SuperConstructorInvocationStatement();

// Add a FieldAccess argument
JavaField resourceField = JavaField.getInstance(
    MyResource.class, "MY_RESOURCE");
FieldAccess fieldAccess = new FieldAccess(
    new TypeExpression(resourceField.getDeclaringType()),
    resourceField);
superInvocation.requiredArguments.add(
    new SimpleArgument(someParam, fieldAccess));

body.constructorInvocationStatement.setValue(superInvocation);
constructor.body.setValue(body);
type.constructors.add(constructor);

// Now test
Declaration result = ResourceTypeUtilities.getResourceFieldOrType(type);
assertNotNull(result);
assertTrue(result instanceof JavaField);
```

**ResourceTypeUtilitiesEdgeCaseTest** covers boundary conditions:
- Type with zero constructors → `null`
- Constructor with 2+ required parameters → `null`
- Super-constructor argument that is not a `FieldAccess` → `null`
- Constructor with exactly one required parameter → parameter's value type

**TypeManagerExtendedTest** extends the existing `TypeManagerTest` and
`TypeManagerDeepTest` with:
- `getNamedUserTypesFromSuperTypes()` — batch type creation from a collection
- `getTypeCache()` without `ProjectStack` — returns empty `HashSet`
- Constructor guard — `new TypeManager()` throws `AssertionError`
- `MatchesNameTypeCriterion` — accepts types with matching names, rejects
  mismatches
- `ExtendsTypeCriterion` subclasses — validates super-type matching logic

### identifier tests

**IdentifierNameGeneratorComprehensiveTest** extends the existing
`IdentifierNameGeneratorTest` and `IdentifierNameGeneratorExtendedTest` with:

```java
// Test S-prefix stripping in createIdentifierNameFromInstanceCreation
// by building a real InstanceCreation AST node
JavaType bipedType = JavaType.getInstance(SBiped.class);
JavaConstructor constructor = bipedType.getDeclaredConstructors().get(0);
InstanceCreation creation = AstUtilities.createInstanceCreation(
    constructor, new Expression[0]);
String result = IdentifierNameGenerator.SINGLETON
    .createIdentifierNameFromInstanceCreation(creation);
assertEquals("biped", result);
```

Additional coverage:
- `convertConstantNameToMethodName` with consecutive underscores, leading
  underscores, digits-only segments
- `createIdentifierNameFromResourceKey(null)` → `""`
- `createIdentifierNameFromClassName` with all-lowercase, Unicode characters

### common tests

**TypeBorderTest** validates the singleton routing pattern:

```java
@Test
public void getSingletonFor_namedUserType_returnsSingletonForUser() {
  NamedUserType userType = new NamedUserType();
  userType.name.setValue("TestType");
  userType.superType.setValue(JavaType.getInstance(Object.class));
  TypeBorder border = TypeBorder.getSingletonFor(userType);
  assertSame(TypeBorder.getSingletonForUserType(), border);
}

@Test
public void getSingletonFor_javaType_returnsSingletonForJava() {
  TypeBorder border = TypeBorder.getSingletonFor(
      JavaType.getInstance(String.class));
  assertNotNull(border);
  assertNotSame(TypeBorder.getSingletonForUserType(), border);
}

@Test
public void getSingletonFor_null_returnsSingletonForNull() {
  TypeBorder border = TypeBorder.getSingletonFor(null);
  assertNotNull(border);
}

@Test
public void getBorderInsets_returnsNonNullInsets() {
  TypeBorder border = TypeBorder.getSingletonForUserType();
  Insets insets = border.getBorderInsets(null);
  assertNotNull(insets);
  assertTrue(insets.left > 0);
  assertTrue(insets.top > 0);
}

@Test
public void isBorderOpaque_returnsFalse() {
  assertFalse(TypeBorder.getSingletonForUserType().isBorderOpaque());
}
```

**Note:** `TypeBorder` has a static `FILL_COLOR` field initialized from
`ThemeUtilities.getActiveTheme()`. If this fails in a headless environment,
the test catches `ExceptionInInitializerError` and uses
`Assume.assumeNoException()` to skip gracefully.

**BeveledShapeForTypeComprehensiveTest** adds geometry verification and
`addRoundType` mutation testing beyond the existing `BeveledShapeForTypeTest`
and `BeveledShapeForTypeDeepTest`:

- Verifies bounds dimensions for each type category (not just non-null)
- Tests `addRoundType` with `java.io.Closeable` (isolated type)
- Tests `union()` with chained round rectangles
- Verifies `getBaseShape()` containment after union

**TypeIconTest** and **TypeComponentTest** use a two-tier approach:

1. **Reflection tests** (always run): Verify factory methods, class hierarchy,
   field types, and constructor signatures.
2. **Headless-guarded tests** (skipped in CI): Verify `getIconWidth()`,
   `getIconHeight()`, `createAwtComponent()` with live AWT.

**ExpressionCreatorPaneTest** tests the abstract class contract via
reflection only:

```java
@Test
public void classIsAbstract() {
  assertTrue(Modifier.isAbstract(
      ExpressionCreatorPane.class.getModifiers()));
}

@Test
public void extendsExpressionLikeSubstance() {
  assertTrue(ExpressionLikeSubstance.class.isAssignableFrom(
      ExpressionCreatorPane.class));
}

@Test
public void getExpressionType_isFinalMethod() throws Exception {
  Method method = ExpressionCreatorPane.class.getDeclaredMethod(
      "getExpressionType");
  assertTrue(Modifier.isFinal(method.getModifiers()));
}
```

## How to add more tests

### 1. Choose a target class

Look at the JaCoCo HTML report for the `org.alice.ide.common`,
`org.alice.ide.identifier`, or `org.alice.ide.typemanager` packages. Sort by
"Missed Lines" to find remaining gaps. Exclude rendering methods that require
`Graphics2D` or `FormatterState`.

### 2. Create the test file

Place it in the mirror package under `core/ide/src/test/java/`. For example,
to test `org.alice.ide.common.FieldNameLabel`:

```
core/ide/src/test/java/org/alice/ide/common/FieldNameLabelTest.java
```

### 3. Follow conventions

```java
package org.alice.ide.common;

import org.junit.Assume;
import org.junit.Test;
import java.awt.GraphicsEnvironment;
import static org.junit.Assert.*;

public class FieldNameLabelTest {

  @Test
  public void factory_withNonNullField_createsLabel() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    // ... test body
  }

  @Test
  public void classHierarchy_extendsExpectedParent() {
    // Reflection test — always runs
    assertTrue(SomeParent.class.isAssignableFrom(FieldNameLabel.class));
  }
}
```

Key rules:

- Use JUnit 4 imports (`org.junit.Test`, `org.junit.Assert`).
- Guard AWT-dependent paths with
  `Assume.assumeFalse(GraphicsEnvironment.isHeadless())`.
- Catch `ExceptionInInitializerError` for classes with static `Color` or
  `Theme` initialization.
- Name methods: `methodUnderTest_condition_expectedResult`.
- Include ≥3 meaningful `@Test` methods per file.
- Use `JavaType.getInstance(Class)` for type construction.
- Build `NamedUserType` trees programmatically for `ResourceTypeUtilities`
  tests.

### 4. Verify

```sh
mvn test -pl core/ide -am -Dtest=YourNewTest
```

### 5. Update the ratchet

After merging, follow [Expand coverage ratchets](expand-coverage-ratchets.md)
to raise the `core/ide` floor if measured coverage has increased.

## AST construction patterns

### Building a NamedUserType with constructor for ResourceTypeUtilities

```java
private NamedUserType buildTypeWithFieldAccessArg(
    JavaType superType, JavaField resourceField) {
  NamedUserType type = new NamedUserType();
  type.name.setValue("Test" + superType.getName());
  type.superType.setValue(superType);

  NamedUserConstructor constructor = new NamedUserConstructor();
  ConstructorBlockStatement body = new ConstructorBlockStatement();
  SuperConstructorInvocationStatement superInvocation =
      new SuperConstructorInvocationStatement();

  // Wire the super-constructor reference
  superInvocation.constructor.setValue(
      superType.getDeclaredConstructors().get(0));

  // Add a FieldAccess as the single argument
  FieldAccess fieldAccess = new FieldAccess(
      new TypeExpression(resourceField.getDeclaringType()),
      resourceField);
  AbstractParameter superParam = superType.getDeclaredConstructors()
      .get(0).getRequiredParameters().get(0);
  superInvocation.requiredArguments.add(
      new SimpleArgument(superParam, fieldAccess));

  body.constructorInvocationStatement.setValue(superInvocation);
  constructor.body.setValue(body);
  type.constructors.add(constructor);
  return type;
}
```

### Building a NamedUserType with parameter-based constructor

```java
private NamedUserType buildTypeWithParamConstructor(
    JavaType superType, JavaType paramType) {
  NamedUserType type = new NamedUserType();
  type.name.setValue("Test" + superType.getName());
  type.superType.setValue(superType);

  NamedUserConstructor constructor = new NamedUserConstructor();
  UserParameter param = new UserParameter("resource", paramType);
  constructor.requiredParameters.add(param);

  ConstructorBlockStatement body = new ConstructorBlockStatement();
  SuperConstructorInvocationStatement superInvocation =
      new SuperConstructorInvocationStatement();
  superInvocation.constructor.setValue(
      superType.getDeclaredConstructors().get(0));
  superInvocation.requiredArguments.add(
      new SimpleArgument(
          superType.getDeclaredConstructors().get(0)
              .getRequiredParameters().get(0),
          new ParameterAccess(param)));

  body.constructorInvocationStatement.setValue(superInvocation);
  constructor.body.setValue(body);
  type.constructors.add(constructor);
  return type;
}
```

## Troubleshooting

### TypeBorder tests fail with ExceptionInInitializerError

`TypeBorder` statically initializes `FILL_COLOR` from
`ThemeUtilities.getActiveTheme().getColorFor(TypeExpression.class)`. If the
theme system fails to initialize, the entire class fails to load.

**Fix:** Wrap test body in a try-catch:

```java
try {
  TypeBorder border = TypeBorder.getSingletonForUserType();
  // ... assertions
} catch (ExceptionInInitializerError e) {
  Assume.assumeNoException("TypeBorder requires theme initialization", e);
}
```

### TypeIcon tests fail with NullPointerException

`TypeIcon.getTypeText()` calls `FormatterState.getInstance().getValue()`,
which requires the croquet `Application` singleton. Tests that trigger this
path must be headless-guarded.

### BeveledShapeForType addRoundType pollutes other tests

`addRoundType()` modifies a shared static `LinkedList`. Use types that no
other test file registers:

- ✅ `java.io.Closeable` — not used by production code or other tests
- ✅ `java.io.Serializable` — used by existing `BeveledShapeForTypeDeepTest`
  with `ArrayList`
- ❌ `String`, `Number`, `Boolean` — these have dedicated shape branches

### ResourceTypeUtilities tests fail with ClassCastException

Ensure that `FieldAccess` nodes are constructed with the correct `JavaField`
reference. The `JavaField.getInstance(Class, String)` factory requires the
field to actually exist on the class.

### Tests pass locally but skip in CI

Tests guarded with `Assume.assumeFalse(GraphicsEnvironment.isHeadless())`
will be skipped (not failed) in headless CI. This is expected. Check the
Maven output for "Tests run: X, Failures: 0, Errors: 0, Skipped: Y".

## Verification checklist

After all 9 new test files are implemented, verify:

- [ ] `mvn test -pl core/ide -am -Dtest="org.alice.ide.common.*,org.alice.ide.identifier.*,org.alice.ide.typemanager.*"` — all tests pass
- [ ] No modifications to existing test files
- [ ] `mvn -DincludeSims=false -Dinstall4j.skip -Pcoverage verify` completes
- [ ] `core/ide` line coverage ≥ 25% in JaCoCo HTML report
- [ ] No `HeadlessException` in test output (headless tests skip, not fail)
- [ ] No hardcoded file paths in any test

## See also

- [Reference: common/identifier/typemanager test coverage](../reference/core-ide-common-identifier-typemanager-test-coverage.md)
- [Tutorial: Trace the common/identifier/typemanager coverage effort](../tutorials/core-ide-common-identifier-typemanager-test-coverage.md)
- [How to raise core/ide test coverage](raise-core-ide-test-coverage.md) — broader module guide
- [Expand coverage ratchets](expand-coverage-ratchets.md)
