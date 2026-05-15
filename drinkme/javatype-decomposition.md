# JavaType decomposition into focused helper classes

The `org.lgna.project.ast.JavaType` class has been decomposed from a 600-line monolith into three focused classes. Two new helper classes — `JavaTypePrimitiveMapping` and `JavaTypeMethodLookup` — now own the primitive-wrapper mapping and method-building responsibilities that previously inflated `JavaType` beyond its core purpose of representing a Java class within the Alice AST.

After decomposition, `JavaType` is under 500 lines (~458 lines) and contains only type identity, type reflection queries, lazy collection accessors, and static `getInstance` factory methods. The two new classes each hold a single coherent responsibility and live in the same package (`org.lgna.project.ast`), requiring no module or POM changes.

## Finished behavior

### JavaTypePrimitiveMapping

`JavaTypePrimitiveMapping` owns the bidirectional mapping between Java primitive types and their boxed wrapper types. The class is `final`, package-private, and non-instantiable (private constructor). All methods are `static` and package-private.

| Method | Purpose |
| --- | --- |
| `isWrapperType(AbstractType<?, ?, ?>)` | Returns `true` if the given type is one of the nine boxed wrapper types (Void, Boolean, Byte, Character, Short, Integer, Long, Float, Double). Called by `AbstractType` hierarchy and IDE code that needs to distinguish boxed types from primitives. |
| `getWrapperTypeIfNecessary(AbstractType<?, ?, ?>)` | If the given type is a `JavaType` for a primitive class, returns the corresponding wrapper `JavaType`. Otherwise returns the input unchanged. Logs `Logger.severe` if a primitive has no mapping (should never happen for well-formed types). |

**Static initialization**: A private `Map<JavaType, JavaType>` is populated in a static initializer block that calls `JavaType.getInstance()` for each of the nine primitive/wrapper pairs. This is safe because `JavaTypePrimitiveMapping` is loaded lazily — only when `isWrapperType()` or `getWrapperTypeIfNecessary()` is first called — at which point `JavaType`'s own static initializer (including `mapReflectionProxyToInstance`) has already completed.

**Migration**: The `mapPrimitiveToWrapper` field, the `addPrimitiveToWrapper` helper, the static initializer block, `isWrapperType()`, and `getWrapperTypeIfNecessary()` all moved verbatim from `JavaType` (lines 92–131 in the original). `JavaType` now contains thin one-line delegates that forward to `JavaTypePrimitiveMapping`.

### JavaTypeMethodLookup

`JavaTypeMethodLookup` owns method discovery, method-chain linking, and method-list construction for `JavaType`'s lazy `methods` field. The class is `final`, package-private, and non-instantiable (private constructor). All methods are `static` and package-private.

| Method | Purpose |
| --- | --- |
| `isMask(int modifiers, int required)` | Bitwise test: returns `true` when `(modifiers & required) != 0`. Used by `handleMthd` to test `PUBLIC` and `PROTECTED` visibility. |
| `trimLast(Class<?>[] src)` | Returns a copy of `src` with the last element removed. Used by `getNextShorterInChain` to walk overload chains. |
| `getNextShorterInChain(Method src)` | For a method `m(A, B, C)`, looks for `m(A, B)` on the same class with the same return type. Returns `null` if no shorter overload exists. Used by `handleMthd` to wire `@MethodTemplate` shorter/longer chains. |
| `handleMthd(Method mthd, List<JavaMethod> methods)` | Processes a single reflected method: checks visibility, resolves `@MethodTemplate` chain linking, and appends the resulting `JavaMethod` to the accumulator list. |
| `buildMethodList(Class<?> cls)` | Constructs the full unmodifiable `List<JavaMethod>` for a class. Iterates `ClassInfoManager.getMethodInfos()` first (if available), then fills in remaining `declaredMethods`, and finally wires setter value templates from `@GetterTemplate`/`@ValueTemplate` annotations. Returns `Collections.emptyList()` for `null` input. |

**Migration**: `isMask` and the commented-out `isNotMask` moved from `JavaType` lines 183–189. `trimLast` and `getNextShorterInChain` moved from lines 305–330. `handleMthd` moved from lines 441–472. The method-list construction body (lines 494–550, including the null-class guard and the setter-value-template wiring loop) was extracted into `buildMethodList()`. The dead `isNotMask` comment was removed. `JavaType`'s lazy `methods` field now calls `JavaTypeMethodLookup.buildMethodList(cls)` in a single line.

### JavaType (after decomposition)

`JavaType` retains:

- **Static fields**: All `public static final` type constants (`VOID_TYPE`, `BOOLEAN_PRIMITIVE_TYPE`, etc.) and the `mapReflectionProxyToInstance` cache.
- **Factory methods**: `getInstance(ClassReflectionProxy)`, `getInstance(Class<?>)`, `getInstances(Class<?>[])`, `getInstances(ClassReflectionProxy[])`.
- **Type identity and reflection**: `classReflectionProxy` field, `getClassReflectionProxy()`, `contentEquals`, `isEquivalentTo`, `isAssignableFromType`, `getEnclosingType`, `getComponentType`, `getArrayType`.
- **Type property queries**: `isPrimitive`, `isInterface`, `isStatic`, `isAbstract`, `isFinal`, `isStrictFloatingPoint`, `isArray`, `isEnum`, `isUserAuthored`, `getAccessLevel`, `getKeywordFactoryType`, `isFollowToSuperClassDesired`, `isConsumptionBySubClassDesired`.
- **Name and hierarchy**: `getName`, `getNamePropertyIfItExists`, `getPackage`, `getSuperType`, `getInterfaces`.
- **Lazy collection accessors**: `getDeclaredConstructors()`, `getDeclaredMethods()`, `getDeclaredFields()`, `getGetterSetterPairs()`. The `constructors`, `fields`, and `getterSetterPairs` lazy bodies remain inline (they are small). The `methods` lazy body delegates to `JavaTypeMethodLookup.buildMethodList()`.
- **Thin delegates**: `isWrapperType()` and `getWrapperTypeIfNecessary()` forward to `JavaTypePrimitiveMapping`.
- **Formatting**: `formatTypeName()`.

## Public API changes

**None.** All public and package-private methods on `JavaType` retain their original signatures. Callers that previously called `JavaType.isWrapperType()` or `JavaType.getWrapperTypeIfNecessary()` continue to call the same methods on `JavaType` — they are unaware that the bodies now delegate to `JavaTypePrimitiveMapping`. The two new classes are package-private and are not part of the public API.

## File inventory

| File | Lines (approx) | Status |
| --- | --- | --- |
| `core/ast/src/main/java/org/lgna/project/ast/JavaType.java` | ~458 | Modified (delegates + dead code removed) |
| `core/ast/src/main/java/org/lgna/project/ast/JavaTypePrimitiveMapping.java` | ~95 (55 + header) | New |
| `core/ast/src/main/java/org/lgna/project/ast/JavaTypeMethodLookup.java` | ~160 (120 + header) | New |
| `core/ast/src/test/java/org/lgna/project/ast/JavaTypePrimitiveMappingTest.java` | ~85 (45 + header) | New (characterization test) |
| `core/ast/src/test/java/org/lgna/project/ast/JavaTypeMethodLookupTest.java` | ~115 (70 + header) | New (characterization test) |

## Build and test

No POM changes are required. The new classes are in the same package and module as `JavaType`.

```bash
# Full module test from repository root
mvn -pl core/ast -am test -q

# Run only the new characterization tests
mvn -pl core/ast -am test -Dtest="JavaTypePrimitiveMappingTest,JavaTypeMethodLookupTest" -q
```

Both commands must exit 0 before the change is considered complete.

## Characterization tests

### JavaTypePrimitiveMappingTest

| Test | Asserts |
| --- | --- |
| `isWrapperType_returnsTrueForInteger` | `JavaType.isWrapperType(JavaType.INTEGER_OBJECT_TYPE)` is `true` |
| `isWrapperType_returnsFalseForPrimitive` | `JavaType.isWrapperType(JavaType.INTEGER_PRIMITIVE_TYPE)` is `false` |
| `isWrapperType_returnsFalseForArbitraryType` | `JavaType.isWrapperType(JavaType.STRING_TYPE)` is `false` |
| `getWrapperTypeIfNecessary_wrapsInt` | `JavaType.getWrapperTypeIfNecessary(JavaType.INTEGER_PRIMITIVE_TYPE)` equals `JavaType.INTEGER_OBJECT_TYPE` |
| `getWrapperTypeIfNecessary_passesThrough` | `JavaType.getWrapperTypeIfNecessary(JavaType.STRING_TYPE)` equals `JavaType.STRING_TYPE` |
| `allNinePrimitivesHaveWrappers` | For each of `{void, boolean, byte, char, short, int, long, float, double}`, `getWrapperTypeIfNecessary` returns a non-primitive type |

### JavaTypeMethodLookupTest

| Test | Asserts |
| --- | --- |
| `getDeclaredMethods_returnsNonEmpty` | `JavaType.getInstance(String.class).getDeclaredMethods()` is not empty |
| `getDeclaredMethods_containsExpectedMethod` | Method list for `String.class` contains a method named `"length"` |
| `getDeclaredMethods_isUnmodifiable` | Calling `.add()` on the returned list throws `UnsupportedOperationException` |
| `getDeclaredMethods_excludesPrivateMethods` | No method in the list has private visibility (only public/protected are included) |
| `buildMethodList_returnsEmptyForNullClass` | `JavaTypeMethodLookup.buildMethodList(null)` returns an empty list (package-private test access via same-package test class) |
| `getDeclaredMethods_calledTwiceReturnsSameInstance` | Calling `getDeclaredMethods()` twice on the same `JavaType` returns the same list instance (Lazy caching) |
| `buildMethodList_wiresMethodTemplateChains` | For a class with `@MethodTemplate(isFollowedByLongerMethod=true)` overloads, the shorter/longer chain links are correctly set via `getNextShorterInChain`/`getNextLongerInChain` |

## Design decisions

1. **Package-private, final, non-instantiable**: Both new classes follow the utility-class pattern. They cannot be subclassed or instantiated. This matches the project's existing convention (see `AstTypeResolutionHelpers`, `AstMethodLookupHelpers`).

2. **Static methods only**: No instance state. All extracted methods were already `private static` or `static` in `JavaType`; they remain `static` in their new homes. Access is widened from `private` to package-private where the delegate call crosses the class boundary.

3. **Dead code removal**: The commented-out `isNotMask` method (lines 187–189 in the original) was removed rather than migrated. It has been commented out since the initial commit and has no callers.

4. **Lazy body delegation pattern**: `JavaType`'s `methods` lazy field previously contained a 60-line anonymous `create()` method. After extraction, the body is:
   ```java
   @Override
   protected List<JavaMethod> create() {
     return JavaTypeMethodLookup.buildMethodList(classReflectionProxy.getReification());
   }
   ```

5. **No access modifier widening on public API**: Every public and package-private method signature on `JavaType` is unchanged. The only widening is `private static` → package-private `static` for methods that moved to a separate class in the same package.

## Risks and mitigations

| Risk | Mitigation |
| --- | --- |
| Static init ordering between `JavaType` and `JavaTypePrimitiveMapping` | `JavaTypePrimitiveMapping` is loaded lazily (first call to `isWrapperType` or `getWrapperTypeIfNecessary`). By that time, `JavaType.mapReflectionProxyToInstance` is fully initialized. The static init block in `JavaTypePrimitiveMapping` calls `JavaType.getInstance()` which is safe because `JavaType` is the trigger class. |
| Reflection-based method lookup in `buildMethodList` depends on classloader state | No change from before — the logic is identical, just in a different class file. |
| Package-private access from `AbstractType` to `getWrapperTypeIfNecessary` | Preserved: `JavaType.getWrapperTypeIfNecessary()` is still the call target. The delegate to `JavaTypePrimitiveMapping` is an implementation detail. |
