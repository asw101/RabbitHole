# Tutorial: Trace the common/identifier/typemanager coverage effort

This tutorial walks through the test architecture for the `org.alice.ide.common`,
`org.alice.ide.identifier`, and `org.alice.ide.typemanager` package coverage
sprint. Follow along to understand the class selection strategy, test patterns
for GUI-adjacent classes, and programmatic AST construction for
`ResourceTypeUtilities`.

## What you will learn

- How to test utility classes that navigate complex AST constructor chains
- How to write headless-safe tests for Swing `Border` and `Icon` classes
- How to test abstract GUI classes via reflection without instantiation
- How to handle static state mutation (`addRoundType`) safely
- How to use the `ExceptionInInitializerError` guard pattern for theme-dependent classes
- How to extend the existing `IdentifierNameGenerator` test suite with AST-backed tests

## Prerequisites

- JDK 17+
- Maven 3.8+
- Tweedle grammar submodule initialized: `git submodule update --init tweedle-lang`
- Familiarity with JUnit 4 (`@Test`, `@Before`, `Assume`)
- Basic understanding of the Alice AST model (`JavaType`, `NamedUserType`,
  `NamedUserConstructor`)

## Step 1: Understand the target packages

The three packages serve distinct roles in the IDE:

| Package | Role | Key classes | Testability |
| --- | --- | --- | --- |
| `org.alice.ide.typemanager` | AST type creation and resource resolution | `TypeManager`, `ResourceTypeUtilities` | Pure logic — fully headless |
| `org.alice.ide.identifier` | Name generation from AST nodes | `IdentifierNameGenerator` | Pure logic — fully headless |
| `org.alice.ide.common` | Visual type components | `TypeBorder`, `TypeIcon`, `TypeComponent`, `BeveledShapeForType`, `ExpressionCreatorPane` | Mixed — some headless, some guarded |

The typemanager and identifier packages are pure logic with zero GUI
dependencies — the highest-value targets. The common package classes are
GUI-adjacent but have testable surfaces (singletons, factories, geometry
math, class hierarchies) that can be exercised headlessly.

## Step 2: Explore ResourceTypeUtilities — programmatic AST construction

`ResourceTypeUtilities` is a utility class with two public static methods
that navigate `NamedUserType` constructor chains:

```
getResourceType(NamedUserType)       → JavaType or null
getResourceFieldOrType(NamedUserType) → Declaration or null
```

The method walks: `type → constructors → first constructor → required params
→ super-constructor invocation → arguments → FieldAccess → field`.

To test this, you need to build AST trees programmatically:

```java
// Build a minimal NamedUserType with the shape:
//   constructor() { super(MyResource.FIELD); }
NamedUserType type = new NamedUserType();
type.name.setValue("TestBiped");
type.superType.setValue(JavaType.getInstance(SBiped.class));

NamedUserConstructor ctor = new NamedUserConstructor();
ConstructorBlockStatement body = new ConstructorBlockStatement();
SuperConstructorInvocationStatement superCall =
    new SuperConstructorInvocationStatement();

// Point the super-call at the SBiped constructor
superCall.constructor.setValue(
    JavaType.getInstance(SBiped.class)
        .getDeclaredConstructors().get(0));

// Add a FieldAccess argument: MyResource.SOME_FIELD
JavaField field = JavaField.getInstance(SomeResource.class, "FIELD_NAME");
FieldAccess access = new FieldAccess(
    new TypeExpression(field.getDeclaringType()), field);
superCall.requiredArguments.add(new SimpleArgument(
    superCall.constructor.getValue()
        .getRequiredParameters().get(0),
    access));

body.constructorInvocationStatement.setValue(superCall);
ctor.body.setValue(body);
type.constructors.add(ctor);
```

### Testing each branch

The `getResourceFieldOrType` method has 7 distinct branches based on
constructor count, parameter count, super-argument count, and argument type:

```
CONSTRUCTOR_COUNT != 1                              → null
  REQUIRED_PARAMETER_COUNT > 1                      → null
  REQUIRED_PARAMETER_COUNT == 1                     → parameter's valueType
  REQUIRED_PARAMETER_COUNT == 0:
    SUPER_ARG_COUNT == 0                            → null
    SUPER_ARG_COUNT == 1 && FieldAccess             → the field
    SUPER_ARG_COUNT == 1 && not FieldAccess         → null
    SUPER_ARG_COUNT > 1                             → null
```

Each branch gets its own `@Test` method with a purpose-built AST tree.

> **Tip:** Extract helper methods like `buildTypeWithFieldAccessArg()` and
> `buildTypeWithNoArgs()` to keep test methods focused on assertions.

## Step 3: Explore TypeManager extensions

The existing `TypeManagerTest` and `TypeManagerDeepTest` cover
`createClassNameFromSuperType`, `getEnumConstantFieldIfOneAndOnly`, and
`getNamedUserTypeFromSuperType`. The new `TypeManagerExtendedTest` fills
remaining gaps:

### Testing the constructor guard

```java
@Test(expected = AssertionError.class)
public void constructor_throwsAssertionError() {
  new TypeManager();
}
```

### Testing getTypeCache without ProjectStack

```java
@Test
public void getTypeCache_withoutProjectStack_returnsEmptySet() {
  Set<NamedUserType> cache = TypeManager.getTypeCache();
  assertNotNull(cache);
  assertTrue(cache.isEmpty());
}
```

### Testing getNamedUserTypesFromSuperTypes

```java
@Test
public void getNamedUserTypesFromSuperTypes_multipleTypes_returnsAll() {
  List<JavaType> superTypes = Arrays.asList(
      JavaType.getInstance(SBiped.class),
      JavaType.getInstance(SScene.class),
      JavaType.getInstance(SCamera.class));
  List<NamedUserType> result =
      TypeManager.getNamedUserTypesFromSuperTypes(superTypes);
  assertEquals(3, result.size());
  assertEquals("Biped", result.get(0).getName());
  assertEquals("Scene", result.get(1).getName());
  assertEquals("Camera", result.get(2).getName());
}
```

### Testing inner criterion classes

The `MatchesNameTypeCriterion` and `ExtendsTypeCriterion` hierarchy are
`private static` inner classes. Even though the test lives in the same package
(`org.alice.ide.typemanager`), it cannot access them directly. Test them
indirectly through the public methods that use them —
`getNamedUserTypeFor` / `getNamedUserTypeFromSuperType`.

## Step 4: Explore IdentifierNameGenerator extensions

The existing tests cover `convertConstantNameToMethodName` and
`createIdentifierNameFromClassName` comprehensively. The new
`IdentifierNameGeneratorComprehensiveTest` adds AST-backed tests:

### Testing S-prefix stripping with InstanceCreation

The `createIdentifierNameFromInstanceCreation` method strips the leading `S`
from Story API type names. Testing this requires building an
`InstanceCreation` AST node:

```java
@Test
public void createIdentifierNameFromInstanceCreation_SBiped_returnsBiped() {
  // Build InstanceCreation for SBiped's constructor
  JavaType bipedType = JavaType.getInstance(SBiped.class);
  List<? extends AbstractConstructor> constructors =
      bipedType.getDeclaredConstructors();
  // Find a constructor to create the InstanceCreation from
  // ... build and test
}
```

### Testing consecutive underscores

```java
@Test
public void convertConstant_consecutiveUnderscores_treatsAsOneSeparator() {
  String result = IdentifierNameGenerator.SINGLETON
      .convertConstantNameToMethodName("MY__DOUBLE__UNDER");
  // Each underscore triggers isUpperNext=true, but empty segments
  // just continue the flag
  assertEquals("myDoubleUnder", result);
}
```

### Testing leading underscore

```java
@Test
public void convertConstant_leadingUnderscore_startsWithUpperCase() {
  String result = IdentifierNameGenerator.SINGLETON
      .convertConstantNameToMethodName("_PRIVATE");
  assertEquals("Private", result);
}
```

## Step 5: Explore TypeBorder — ExceptionInInitializerError guard

`TypeBorder` is the first GUI-adjacent class. It has a static field:

```java
private static final Color FILL_COLOR =
    ThemeUtilities.getActiveTheme().getColorFor(TypeExpression.class);
```

`ThemeUtilities.getActiveTheme()` falls back to `DefaultTheme` in headless
environments, so this usually works. But if it fails, the entire class throws
`ExceptionInInitializerError`. The guard pattern:

```java
@Test
public void getSingletonFor_namedUserType_returnsSingletonForUser() {
  try {
    NamedUserType userType = new NamedUserType();
    userType.name.setValue("TestType");
    userType.superType.setValue(JavaType.getInstance(Object.class));
    TypeBorder border = TypeBorder.getSingletonFor(userType);
    assertSame(TypeBorder.getSingletonForUserType(), border);
  } catch (ExceptionInInitializerError e) {
    Assume.assumeNoException(
        "TypeBorder requires theme initialization", e);
  }
}
```

### What the tests verify

- `getSingletonFor(NamedUserType)` → same reference as `getSingletonForUserType()`
- `getSingletonFor(JavaType)` → different reference from user type singleton
- `getSingletonFor(null)` → different reference from both user and Java type singletons
- `getBorderInsets(null)` → non-null `Insets` with positive left/top values
- `isBorderOpaque()` → `false`
- Singleton identity: user ≠ java ≠ null

## Step 6: Explore BeveledShapeForType — geometry and static mutation

The existing `BeveledShapeForTypeTest` (headless-guarded) and
`BeveledShapeForTypeDeepTest` (headless-safe) cover shape creation for each
type category. The new `BeveledShapeForTypeComprehensiveTest` adds:

### Geometry verification

```java
@Test
public void createBeveledShapeFor_stringType_hasNonDegenerateBounds() {
  BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
      JavaType.getInstance(String.class), 0f, 0f, 100f, 20f);
  Rectangle2D bounds = shape.getBaseShape().getBounds2D();
  assertTrue("Width should be > 50% of input width",
      bounds.getWidth() > 50.0);
  assertTrue("Height should be > 50% of input height",
      bounds.getHeight() > 10.0);
}
```

### addRoundType mutation

```java
@Test
public void addRoundType_closeable_thenCloseableGetsRoundShape() {
  // Register Closeable as a round type (isolated — no other test uses it)
  BeveledShapeForType.addRoundType(java.io.Closeable.class);

  // java.io.StringReader implements Closeable
  BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
      JavaType.getInstance(java.io.StringReader.class),
      0f, 0f, 100f, 20f);
  assertNotNull(shape);

  // The round type path uses curveTo, producing wider bounds
  // than the default rectangular path at the same dimensions
  Rectangle2D bounds = shape.getBaseShape().getBounds2D();
  assertTrue(bounds.getWidth() > 0);
}
```

### union() mechanics

```java
@Test
public void union_expandsBoundsToIncludeRoundRect() {
  BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
      JavaType.getInstance(Object.class), 0f, 0f, 50f, 20f);
  double widthBefore = shape.getBaseShape().getBounds2D().getWidth();

  RoundRectangle2D.Float rr = new RoundRectangle2D.Float(
      60f, 0f, 100f, 20f, 5f, 5f);
  shape.union(rr);

  double widthAfter = shape.getBaseShape().getBounds2D().getWidth();
  assertTrue("Union should expand width", widthAfter > widthBefore);
}
```

## Step 7: Explore TypeIcon and TypeComponent — two-tier testing

These classes depend on AWT for rendering. The test strategy uses two tiers:

### Tier 1: Reflection tests (always run)

```java
@Test
public void getInstance_returnsTypeIcon() {
  // TypeIcon.getInstance is a static factory
  assertNotNull(TypeIcon.class.getDeclaredMethod(
      "getInstance", AbstractType.class));
}

@Test
public void typeIcon_implementsIcon() {
  assertTrue(javax.swing.Icon.class.isAssignableFrom(TypeIcon.class));
}
```

### Tier 2: Functional tests (headless-guarded)

```java
@Test
public void getIconWidth_withStringType_returnsPositive() {
  Assume.assumeFalse(GraphicsEnvironment.isHeadless());
  TypeIcon icon = TypeIcon.getInstance(
      JavaType.getInstance(String.class));
  assertTrue(icon.getIconWidth() > 0);
}
```

`TypeComponentTest` follows the same pattern — reflection in tier 1,
`createAwtComponent()` in tier 2.

## Step 8: Explore ExpressionCreatorPane — abstract class testing

`ExpressionCreatorPane` is abstract and requires an
`AbstractExpressionDragModel` (a croquet model) to instantiate. Tests use
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

@Test
public void isClickAndClackAppropriate_isDeclared() throws Exception {
  Method method = ExpressionCreatorPane.class.getDeclaredMethod(
      "isClickAndClackAppropriate");
  assertNotNull(method);
  // Method is protected, not abstract — it has an implementation
  assertFalse(Modifier.isAbstract(method.getModifiers()));
}
```

## Step 9: Run and verify

### Run all new tests

```sh
mvn test -pl core/ide -am \
  -Dtest="ResourceTypeUtilitiesTest,ResourceTypeUtilitiesEdgeCaseTest,TypeManagerExtendedTest,IdentifierNameGeneratorComprehensiveTest,TypeBorderTest,BeveledShapeForTypeComprehensiveTest,TypeIconTest,TypeComponentTest,ExpressionCreatorPaneTest"
```

### Check for skipped tests

In CI (headless), some tests will be skipped:

```
Tests run: 45, Failures: 0, Errors: 0, Skipped: 8
```

The skipped tests are headless-guarded tests for `TypeBorder.paintBorder`,
`TypeIcon.getIconWidth/Height`, and `TypeComponent.createAwtComponent`.

### Run coverage

```sh
mvn -DincludeSims=false -Dinstall4j.skip -Pcoverage verify
python3 scripts/summarize-jacoco-coverage.py \
  --output coverage-summary.md \
  --evidence-manifest coverage-evidence-manifest.json \
  --target-aggregate-line-percent 70.0 \
  --min-aggregate-line-percent 8.0 \
  --min-module-line-percent core/ide=25.0
```

## Summary

| Group | Package | New test files | Est. lines |
| ---: | --- | ---: | ---: |
| 1 | `typemanager` | 3 | 510 |
| 2 | `identifier` | 1 | 220 |
| 3 | `common` | 5 | 890 |
| **Total** | | **9** | **~1,620** |

### Key patterns introduced

| Pattern | Used in | Purpose |
| --- | --- | --- |
| Programmatic AST construction | `ResourceTypeUtilitiesTest` | Build `NamedUserType` trees with specific constructor shapes |
| `ExceptionInInitializerError` guard | `TypeBorderTest` | Handle theme-dependent static initialization |
| Two-tier (reflection + headless-guarded) | `TypeIconTest`, `TypeComponentTest` | Test class structure without AWT, test rendering with AWT |
| Reflection-only abstract class testing | `ExpressionCreatorPaneTest` | Verify abstract contract without instantiation |
| Isolated `addRoundType` types | `BeveledShapeForTypeComprehensiveTest` | Prevent static state cross-contamination |

## Next steps

- Read the [reference](../reference/core-ide-common-identifier-typemanager-test-coverage.md) for the complete test inventory and API summary.
- Read the [how-to](../howto/raise-core-ide-common-identifier-typemanager-coverage.md) for running, extending, and troubleshooting.
- Follow [Expand coverage ratchets](../howto/expand-coverage-ratchets.md) to raise the CI floor after merging.
