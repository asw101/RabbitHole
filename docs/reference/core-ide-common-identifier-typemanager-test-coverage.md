# core/ide test coverage — common, identifier, and typemanager packages

The `org.alice.ide.common`, `org.alice.ide.identifier`, and
`org.alice.ide.typemanager` packages contain the IDE's type-visualization
components, identifier-name generation utilities, and AST type management
logic. This coverage sprint adds 9 new JUnit 4 test files (~1,620 raw source
lines) bringing these three previously-undertested packages from near-zero
coverage to comprehensive characterization-level testing.

All tests run headlessly in CI without Swing rendering or display
dependencies. Methods that require a live `Graphics2D` context,
`FormatterState` singleton, or croquet `Application` instance are either
headless-guarded with `Assume.assumeFalse(GraphicsEnvironment.isHeadless())`
or tested via reflection-only contracts.

## Test inventory

### Group 1 — typemanager package (~510 lines)

| Test file | Source classes covered | Est. lines |
| --- | --- | ---: |
| `ResourceTypeUtilitiesTest` | `ResourceTypeUtilities.getResourceType()`, `getResourceFieldOrType()` | 200 |
| `ResourceTypeUtilitiesEdgeCaseTest` | `ResourceTypeUtilities` boundary/null-safety paths | 120 |
| `TypeManagerExtendedTest` | `TypeManager.getNamedUserTypesFromSuperTypes()`, `getTypeCache()`, constructor guard, `MatchesNameTypeCriterion`, `ExtendsTypeCriterion` hierarchy | 190 |

### Group 2 — identifier package (~220 lines)

| Test file | Source classes covered | Est. lines |
| --- | --- | ---: |
| `IdentifierNameGeneratorComprehensiveTest` | `IdentifierNameGenerator.createIdentifierNameFromInstanceCreation()`, S-prefix stripping with AST, `convertConstantNameToMethodName()` advanced patterns, `createIdentifierNameFromResourceKey()` null paths | 220 |

### Group 3 — common package (~890 lines)

| Test file | Source classes covered | Est. lines |
| --- | --- | ---: |
| `TypeBorderTest` | `TypeBorder.getSingletonFor()`, `getSingletonForUserType()`, `getBorderInsets()`, `isBorderOpaque()`, singleton identity | 160 |
| `BeveledShapeForTypeComprehensiveTest` | `BeveledShapeForType.createBeveledShapeFor()` all type categories, `addRoundType()` mutation, `union()` mechanics, geometry verification | 230 |
| `TypeIconTest` | `TypeIcon.getInstance()`, constructor reflection, field presence, headless-guarded `getIconWidth()`/`getIconHeight()` | 180 |
| `TypeComponentTest` | `TypeComponent.createInstance()`, `SwingComponentView` hierarchy, headless-guarded `createAwtComponent()` | 150 |
| `ExpressionCreatorPaneTest` | `ExpressionCreatorPane` abstract contract via reflection, `isClickAndClackAppropriate()`, `getExpressionType()` delegation | 150 |

**Total estimated new lines:** ~1,620

## Source class API summary

### ResourceTypeUtilities

Utility class for extracting resource type information from `NamedUserType`
AST nodes. The two public methods navigate the constructor → super-constructor
invocation → argument expression chain to find the resource `JavaType` or
`JavaField`.

```
ResourceTypeUtilities.getResourceType(NamedUserType)       → JavaType or null
ResourceTypeUtilities.getResourceFieldOrType(NamedUserType) → Declaration or null
```

**Key branches tested:**
- Single constructor, zero required parameters, zero super-args → `null`
- Single constructor, zero required parameters, one super-arg with `FieldAccess` → the field
- Single constructor, zero required parameters, one super-arg without `FieldAccess` → `null`
- Single constructor, one required parameter → the parameter's value type
- Single constructor, 2+ required parameters → `null`
- Single constructor, zero required parameters, multiple super-args → `null`
- Multiple constructors → `null`

### TypeManager

Static factory for creating `NamedUserType` instances from `JavaType`
supertypes. Key public methods:

```
TypeManager.createClassNameFromSuperType(AbstractType)              → String
TypeManager.getEnumConstantFieldIfOneAndOnly(AbstractType)          → JavaField or null
TypeManager.getNamedUserTypeFromSuperType(JavaType)                 → NamedUserType
TypeManager.getNamedUserTypesFromSuperTypes(Collection<JavaType>)   → List<NamedUserType>
TypeManager.getTypeCache()                                          → Set<NamedUserType>
```

**S-prefix convention:** Story API types like `SBiped`, `SScene`, `SCamera`
have their leading `S` stripped when generating class names. The rule applies
only when the name starts with `S` followed by an uppercase letter. `String`,
`Slower`, and single-character names are unchanged.

**Type cache:** `getTypeCache()` returns the project's `NamedUserType` set via
`ProjectStack.peekProject()`. Without an active project stack, it returns an
empty `HashSet` — the null-project codepath tested by `TypeManagerExtendedTest`.

### IdentifierNameGenerator

Singleton enum (`IdentifierNameGenerator.SINGLETON`) that converts between
naming conventions:

```
convertConstantNameToMethodName("MY_CONSTANT")        → "myConstant"
convertConstantNameToMethodName("COLOR", "set")       → "setColor"
createIdentifierNameFromClassName("MyClass")           → "myClass"
createIdentifierNameFromInstanceCreation(InstanceCreation) → "biped" (from SBiped)
createIdentifierNameFromResourceKey(ResourceKey)       → "biped" or ""
```

**S-prefix stripping in instance creation:** When the declaring type of an
`InstanceCreation`'s constructor is a `JavaType` whose name starts with `S`
followed by an uppercase letter, the `S` is stripped and the result is
lowercased. This mirrors the `TypeManager.createClassNameFromSuperType`
convention.

### TypeBorder

Implements `javax.swing.border.Border` with a hexagonal shape for AST type
display. Three singletons route based on type:

```
TypeBorder.getSingletonFor(null)           → singletonForNull
TypeBorder.getSingletonFor(NamedUserType)  → singletonForUser
TypeBorder.getSingletonFor(JavaType)       → singletonForJava
TypeBorder.getSingletonForUserType()       → singletonForUser
```

Static initialization loads `FILL_COLOR` from `ThemeUtilities.getActiveTheme()`,
which falls back to `DefaultTheme` in headless environments.

### BeveledShapeForType

Generates type-category-specific beveled shapes for the IDE's visual type
representations:

| AST type category | Shape style | Method |
| --- | --- | --- |
| `void` | Empty path (degenerate bounds) | Direct in `createBeveledShapeFor` |
| `String` | S-curve with curveTo | `getBeveledShapeForString` |
| `Number` | Stepped staircase | `getBeveledShapeForNumber` |
| `Boolean` / `boolean` | Quad-curve tab | `getBeveledShapeForBoolean` |
| Registered round type | curveTo arc | Round-type loop |
| Default (everything else) | Rectangle path | `getDefaultBeveledShape` |

**Static state mutation:** `addRoundType(Class)` adds to a shared static
`LinkedList`. Tests use isolated types (e.g., `java.io.Serializable`,
`java.io.Closeable`) that are not used by other test files to avoid
cross-test interference.

### TypeIcon

Implements `javax.swing.Icon` for type display in the IDE. Factory method:

```
TypeIcon.getInstance(AbstractType) → TypeIcon
```

Depends on `FormatterState.getInstance()` for text rendering and
`GraphicsUtilities.getGraphics()` for font metrics — both require a headed
environment. Tests that call `getIconWidth()` or `getIconHeight()` are guarded
with `Assume.assumeFalse(GraphicsEnvironment.isHeadless())`.

### TypeComponent

Extends `SwingComponentView<JLabel>` with a factory method:

```
TypeComponent.createInstance(AbstractType) → TypeComponent
```

The `createAwtComponent()` override creates a `JLabel` with a `TypeIcon` —
requires a headed environment. Tests validate the factory and class hierarchy
via reflection in headless mode.

### ExpressionCreatorPane

Abstract class extending `ExpressionLikeSubstance`. No direct instantiation
possible. Tests verify the abstract contract:

- `isClickAndClackAppropriate()` returns `true`
- `getExpressionType()` delegates to `AbstractExpressionDragModel.getType()`
- `handleMouseQuoteEnteredUnquote/ExitedUnquote` safely handle `IDE.getActiveInstance() == null`

## Test style and conventions

All tests use JUnit 4 (`org.junit.Test`, `org.junit.Assert`) to match the
existing `core/ide` test style.

Key conventions:

- **Headless guards** where a method touches AWT or requires a graphics
  context: `Assume.assumeFalse(GraphicsEnvironment.isHeadless())`.
- **`ExceptionInInitializerError` catch** for `TypeBorder` static
  initialization that may fail if `ThemeUtilities` cannot load a theme.
- **Programmatic AST construction** for `ResourceTypeUtilities` tests —
  building `NamedUserType`, `NamedUserConstructor`, `ConstructorBlockStatement`,
  `SuperConstructorInvocationStatement`, `FieldAccess`, and `UserParameter`
  trees in helper methods.
- **Reflection-only tests** for `ExpressionCreatorPane` — verifying class
  hierarchy, abstract methods, and method signatures without instantiation.
- **Isolated round types** for `BeveledShapeForType.addRoundType()` tests —
  using `java.io.Closeable` or `java.io.Serializable` to avoid polluting the
  shared static list with types used by other tests.
- **Descriptive method names** following
  `methodUnderTest_condition_expectedResult` pattern.
- **≥3 meaningful `@Test` methods per file** — each exercising distinct
  behavior.
- **AST construction via `JavaType.getInstance(Class)`** for type references.
- **No test data files** — all test content is generated in-memory.

## Static state management

| Class | Static state | Restoration pattern |
| --- | --- | --- |
| `BeveledShapeForType` | `s_roundTypes` list via `addRoundType()` | Use isolated types not used elsewhere; list is append-only |
| `TypeBorder` | Three singleton instances, `FILL_COLOR` | Read-only — no restoration needed |
| `TypeManager` | Stateless static methods; `getTypeCache()` depends on `ProjectStack` | No project stack in test → returns empty set |
| `IdentifierNameGenerator` | `SINGLETON` enum instance | Stateless — no restoration needed |
| `TypeIcon` | No static state | Create fresh instances per test |
| `TypeComponent` | No static state | Create fresh instances per test |

## Excluded source code

The following methods/paths are excluded from direct testing because they
require a live display, croquet application singleton, or OpenGL context:

| Exclusion | Reason |
| --- | --- |
| `TypeBorder.paintBorder()` | Requires `Graphics2D` context |
| `TypeBorder.getFillPaint()` (full path) | Requires enabled `Component` |
| `TypeIcon.paintIcon()` | Requires `Graphics2D` rendering |
| `TypeIcon.getIconWidth()`/`getIconHeight()` | Requires `GraphicsUtilities.getGraphics()` — headless-guarded |
| `TypeIcon.getTypeText()` | Requires `FormatterState.getInstance()` singleton |
| `TypeComponent.createAwtComponent()` | Requires `JLabel` instantiation — headless-guarded |
| `ExpressionCreatorPane.handleMouseQuoteEnteredUnquote()` | Requires `IDE.getActiveInstance()` |
| `ExpressionCreatorPane.handleMouseQuoteExitedUnquote()` | Requires `IDE.getActiveInstance()` |
| `TypeManager.createTypeFor()` (IDE augmentation path) | Requires `IDE.getActiveInstance()` |

## Security considerations

- No authentication, network access, or sensitive data in any test target —
  purely in-memory AST construction and geometry verification.
- Synthetic names used in all AST construction (e.g., `"TestBiped"`,
  `"TestResource"`) following existing project conventions.
- No external dependencies or test fixtures beyond JUnit 4 and existing
  project AST classes.
- No `Runtime.exec` or `ProcessBuilder` in any test — confirmed zero
  instances in target classes.

## Source file locations

```
core/ide/src/test/java/
├── org/alice/ide/
│   ├── common/
│   │   ├── BeveledShapeForTypeTest.java              (existing)
│   │   ├── BeveledShapeForTypeDeepTest.java           (existing)
│   │   ├── BeveledShapeForTypeComprehensiveTest.java  (new)
│   │   ├── TypeBorderTest.java                        (new)
│   │   ├── TypeIconTest.java                          (new)
│   │   ├── TypeComponentTest.java                     (new)
│   │   └── ExpressionCreatorPaneTest.java             (new)
│   ├── identifier/
│   │   ├── IdentifierNameGeneratorTest.java           (existing)
│   │   ├── IdentifierNameGeneratorExtendedTest.java   (existing)
│   │   └── IdentifierNameGeneratorComprehensiveTest.java (new)
│   └── typemanager/
│       ├── TypeManagerTest.java                       (existing)
│       ├── TypeManagerDeepTest.java                   (existing)
│       ├── TypeManagerExtendedTest.java               (new)
│       ├── ResourceTypeUtilitiesTest.java             (new)
│       └── ResourceTypeUtilitiesEdgeCaseTest.java     (new)
```

## Running the tests

Run only the new test files:

```sh
mvn test -pl core/ide -am \
  -Dtest="ResourceTypeUtilitiesTest,ResourceTypeUtilitiesEdgeCaseTest,TypeManagerExtendedTest,IdentifierNameGeneratorComprehensiveTest,TypeBorderTest,BeveledShapeForTypeComprehensiveTest,TypeIconTest,TypeComponentTest,ExpressionCreatorPaneTest"
```

Run all tests in the three packages:

```sh
mvn test -pl core/ide -am \
  -Dtest="org.alice.ide.common.*,org.alice.ide.identifier.*,org.alice.ide.typemanager.*"
```

Run with coverage reporting:

```sh
mvn -DincludeSims=false -Dinstall4j.skip -Pcoverage verify
python3 scripts/summarize-jacoco-coverage.py \
  --output coverage-summary.md \
  --evidence-manifest coverage-evidence-manifest.json \
  --target-aggregate-line-percent 70.0 \
  --min-aggregate-line-percent 8.0 \
  --min-module-line-percent core/ide=25.0
```

## See also

- [How to raise core/ide common, identifier, and typemanager coverage](../howto/raise-core-ide-common-identifier-typemanager-coverage.md) — step-by-step guide
- [Tutorial: Trace the common/identifier/typemanager coverage effort](../tutorials/core-ide-common-identifier-typemanager-test-coverage.md) — guided walkthrough
- [Reference: core/ide test coverage](core-ide-test-coverage.md) — full module coverage inventory
- [Coverage reporting and ratchets](coverage-reporting.md) — ratchet workflow
