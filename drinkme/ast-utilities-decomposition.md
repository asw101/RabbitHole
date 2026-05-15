# AstUtilities decomposition into focused helper classes

The `org.lgna.project.ast.AstUtilities` class has been decomposed from a 649-line monolith into three focused classes. Two new helper classes — `AstTypeResolutionHelpers` and `AstMethodLookupHelpers` — now own the type-resolution and method-lookup responsibilities that previously inflated `AstUtilities` beyond its core purpose of AST node construction.

After decomposition, `AstUtilities` is under 500 lines and contains only AST node factory methods (copy, creation, assignment, lambda, parameter manipulation). The two new classes each hold a single coherent responsibility and live in the same package (`org.lgna.project.ast`), requiring no module or POM changes.

## Finished behavior

### AstTypeResolutionHelpers

`AstTypeResolutionHelpers` owns property getter discovery, setter resolution, keyword factory type lookup, parameter value type extraction, and declaring-type resolution. All methods are `public static` with no mutable state.

| Method | Purpose |
| --- | --- |
| `getDeclaredPersistentPropertyGetters(JavaType)` | Returns `@GetterTemplate(isPersistent=true)` methods declared directly on the given type. |
| `getPersistentPropertyGetters(AbstractType)` | Walks the type hierarchy collecting persistent property getters until `isFollowToSuperClassDesired()` returns false. |
| `getSetterForGetter(JavaMethod, JavaType)` | Resolves the setter method corresponding to a persistent property getter on a specific type. |
| `getSetterForGetter(JavaMethod)` | Convenience overload that uses the getter's own declaring type. |
| `getParameterValueTypes(AbstractMethod)` | Extracts an array of value types from a method's required parameters. |
| `getKeywordFactoryType(JavaKeyedArgument)` | Resolves the keyword factory type for a keyworded parameter's array component type. Returns null when the argument is not keyworded or has no component type. |
| `getDeclaringTypeIfMemberOrTypeItselfIfType(AbstractDeclaration)` | Returns the type itself if the declaration is a type, or the declaring type if it is a member. Throws `UnsupportedOperationException` for other declaration kinds. Returns null for null input. |

The private helper `updatePersistentPropertyGetters(List<JavaMethod>, JavaType)` moved alongside the public methods that depend on it.

### AstMethodLookupHelpers

`AstMethodLookupHelpers` owns method lookup by signature, single abstract method (SAM) resolution, overridden method discovery, method invocation crawling, and full method hierarchy traversal. All methods are `public static` with no mutable state.

| Method | Purpose |
| --- | --- |
| `lookupMethod(Class, String, Class...)` | Delegates to `JavaMethod.getInstance` for reflective method lookup. |
| `getSingleAbstractMethod(AbstractType)` | Returns the single abstract method declared on a functional interface type. Asserts exactly one method exists and that it is abstract. |
| `getOverridenMethod(AbstractMethod)` | Walks the supertype chain to find the method that the given method overrides. Returns null if no override exists. |
| `getAllInvokedMethods(UserMethod)` | Recursively crawls the body of a user method and returns the transitive set of all invoked `UserMethod` instances. |
| `getAllMethods(AbstractType)` | Recursively collects all methods from the type and its entire supertype hierarchy. |

The private helpers `getParameterTypes(AbstractMethod)`, `getOverridenMethod(AbstractType, String, AbstractType[])`, `addInvokedMethods(Set<UserMethod>, UserMethod)`, and `updateAllMethods(List<AbstractMethod>, AbstractType)` moved alongside their public entry points.

### AstUtilities (reduced)

`AstUtilities` retains all AST node creation and manipulation methods:

- `createCopy` — deep copy via XML round-trip
- Keyword expression helpers (`isKeywordExpression`, `getJavaKeyedArgumentSubArgument0Expression`)
- Factory methods for `UserMethod`, `NamedUserType`, statements, loops, conditionals
- `MethodInvocation` construction and completion
- `TypeExpression`, `InstanceCreation`, `ArrayInstanceCreation` factories
- Field, local, and parameter assignment factories
- `StringConcatenation` factory
- Parameter add/remove manipulation
- `createUserLambda` and `createLambdaExpression` — lambda construction
- `isAddEventListenerMethodInvocationStatement` — statement classification
- `fixRequiredArgumentsIfNecessary` — argument repair
- `getNamedUserTypes` — type crawling

The `createUserLambda` method now calls `AstMethodLookupHelpers.getSingleAbstractMethod(type)` instead of the previously co-located `getSingleAbstractMethod`. This is the only internal call-site change within `AstUtilities`.

## Migration guide

### Callers that used extracted methods

All extracted methods retain their original signatures, parameter types, return types, and behavioral contracts. Migration is mechanical: update the class qualifier and add the corresponding import.

**Before:**
```java
import org.lgna.project.ast.AstUtilities;

Iterable<JavaMethod> getters = AstUtilities.getPersistentPropertyGetters(type);
AbstractMethod overridden = AstUtilities.getOverridenMethod(method);
```

**After:**
```java
import org.lgna.project.ast.AstTypeResolutionHelpers;
import org.lgna.project.ast.AstMethodLookupHelpers;

Iterable<JavaMethod> getters = AstTypeResolutionHelpers.getPersistentPropertyGetters(type);
AbstractMethod overridden = AstMethodLookupHelpers.getOverridenMethod(method);
```

Callers that use both extracted methods and retained `AstUtilities` factory methods keep all three imports.

### Same-package callers

`JavaCodeGenerator` and `StatementCodeEmitter` in `org.lgna.project.ast` need no import changes (same package), only call-site qualifier updates.

### Cross-package callers requiring new imports

| Caller file | New import(s) needed |
| --- | --- |
| `TweedleEncoder` | `AstTypeResolutionHelpers` |
| `ReplaceCameraWithVR` | `AstMethodLookupHelpers` |
| `ChangeDeclaringClassForAxesSetVehicle` | `AstMethodLookupHelpers` |
| `MethodInvocationBlank` | `AstMethodLookupHelpers` |
| `BootstrapUtilities` | `AstMethodLookupHelpers` |
| `SetUpMethodGenerator` | `AstTypeResolutionHelpers`, `AstMethodLookupHelpers` |
| `ObjectMarkerMoveActionOperation` | `AstMethodLookupHelpers` |
| `InstanceFactoryState` | `AstTypeResolutionHelpers` |
| `SceneObjectPropertyManagerPanel` | `AstTypeResolutionHelpers` |
| `AstI18nFactory` | `AstTypeResolutionHelpers` |
| `SceneEditorUpdatingPropertyState` | `AstTypeResolutionHelpers` |
| `SilverThreadStudentProgramSaveReadbackTest` | `AstMethodLookupHelpers` |

### Callers that used only retained methods

Callers that only use `AstUtilities` factory methods (e.g., `createMethodInvocation`, `createInstanceCreation`, `createFieldAssignment`) require no changes.

## API reference

### AstTypeResolutionHelpers

```
package org.lgna.project.ast;

public final class AstTypeResolutionHelpers {
  private AstTypeResolutionHelpers() { throw new AssertionError(); }

  public static Iterable<JavaMethod> getDeclaredPersistentPropertyGetters(JavaType javaType)
  public static Iterable<JavaMethod> getPersistentPropertyGetters(AbstractType<?,?,?> type)
  public static JavaMethod getSetterForGetter(JavaMethod getter, JavaType type)
  public static JavaMethod getSetterForGetter(JavaMethod getter)
  public static AbstractType<?,?,?>[] getParameterValueTypes(AbstractMethod method)
  public static AbstractType<?,?,?> getKeywordFactoryType(JavaKeyedArgument argument)
  public static AbstractType<?,?,?> getDeclaringTypeIfMemberOrTypeItselfIfType(
      AbstractDeclaration declaration)
}
```

### AstMethodLookupHelpers

```
package org.lgna.project.ast;

public final class AstMethodLookupHelpers {
  private AstMethodLookupHelpers() { throw new AssertionError(); }

  public static JavaMethod lookupMethod(Class<?> cls, String methodName, Class<?>... parameterTypes)
  public static <M extends AbstractMethod> M getSingleAbstractMethod(AbstractType<?,M,?> type)
  public static AbstractMethod getOverridenMethod(AbstractMethod method)
  public static Set<UserMethod> getAllInvokedMethods(UserMethod seed)
  public static List<AbstractMethod> getAllMethods(AbstractType<?,?,?> type)
}
```

## Design rationale

### Why two helper classes instead of one

The type-resolution methods operate on types, fields, and property annotations. The method-lookup methods operate on method signatures, invocation crawling, and override chains. These are distinct responsibilities with distinct dependency sets. Combining them would create another unfocused utility class.

### Why not an interface or abstract class

All methods are stateless `public static` utilities. There is no polymorphic behavior, no instance state, and no inheritance contract. A `final class` with a private constructor is the correct Java idiom.

### Why same package

Both helper classes interact with package-private members of AST node types (e.g., `MethodInvocation.method`, `AbstractParameter` value accessors). Moving them to a sub-package would require visibility widening, which violates the encapsulation contract of the AST module.

### Why deprecation stubs are not needed

All callers are within the Alice 3 monorepo and are updated atomically in the same commit. There are no external consumers of `AstUtilities` (the class is not part of a published API). Deprecation stubs would add lines without providing value.

## Validation

The decomposition is validated by:

1. **Line count** — `AstUtilities.java` is under 500 lines after extraction.
2. **Maven compile** — `mvn compile -pl core/ast,core/story-api-migration,core/ide -am` succeeds with no errors.
3. **Existing test** — `SilverThreadStudentProgramSaveReadbackTest` continues to pass, confirming that the method lookup used by the IDE's student program path is correctly redirected.
4. **No behavioral change** — Every extracted method preserves its original implementation verbatim, including assertions, null handling, and recursive traversal logic.
