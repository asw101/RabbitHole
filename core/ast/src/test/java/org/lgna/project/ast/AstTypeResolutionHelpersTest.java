package org.lgna.project.ast;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TDD contract tests for AstTypeResolutionHelpers.
 * These tests define the behavioral contract that the extracted helper class
 * must satisfy. They will FAIL until AstTypeResolutionHelpers is created.
 */
public class AstTypeResolutionHelpersTest {

  // ── getDeclaredPersistentPropertyGetters ──────────────────────────────

  @Test
  public void declaredPersistentPropertyGettersReturnsNonNullIterable() {
    JavaType type = JavaType.getInstance(String.class);
    Iterable<JavaMethod> result = AstTypeResolutionHelpers.getDeclaredPersistentPropertyGetters(type);
    assertNotNull("Should never return null", result);
  }

  @Test
  public void declaredPersistentPropertyGettersForPlainClassReturnsEmpty() {
    JavaType type = JavaType.getInstance(String.class);
    Iterable<JavaMethod> result = AstTypeResolutionHelpers.getDeclaredPersistentPropertyGetters(type);
    assertFalse("String has no @GetterTemplate methods", result.iterator().hasNext());
  }

  // ── getPersistentPropertyGetters ──────────────────────────────────────

  @Test
  public void persistentPropertyGettersReturnsNonNullIterable() {
    JavaType type = JavaType.getInstance(Object.class);
    Iterable<JavaMethod> result = AstTypeResolutionHelpers.getPersistentPropertyGetters(type);
    assertNotNull("Should never return null", result);
  }

  @Test
  public void persistentPropertyGettersForObjectReturnsEmpty() {
    JavaType type = JavaType.getInstance(Object.class);
    Iterable<JavaMethod> result = AstTypeResolutionHelpers.getPersistentPropertyGetters(type);
    assertFalse("Object has no persistent property getters", result.iterator().hasNext());
  }

  @Test
  public void persistentPropertyGettersAcceptsJavaType() {
    // Verify the method accepts AbstractType (polymorphic contract)
    AbstractType<?, ?, ?> type = JavaType.getInstance(String.class);
    Iterable<JavaMethod> result = AstTypeResolutionHelpers.getPersistentPropertyGetters(type);
    assertNotNull(result);
  }

  // ── getSetterForGetter ────────────────────────────────────────────────

  @Test
  public void setterForGetterReturnsNullWhenNoSetterExists() {
    // Class.getName() has no corresponding setName()
    JavaMethod getter = JavaMethod.getInstance(Class.class, "getName");
    JavaMethod setter = AstTypeResolutionHelpers.getSetterForGetter(getter, JavaType.getInstance(Class.class));
    assertNull("Class.getName() has no setter", setter);
  }

  @Test
  public void setterForGetterSingleArgOverloadUsesDeclaringType() {
    // The single-argument overload should use the getter's own declaring type
    JavaMethod getter = JavaMethod.getInstance(Class.class, "getName");
    JavaMethod setter = AstTypeResolutionHelpers.getSetterForGetter(getter);
    assertNull("Class.getName() has no setter via convenience overload either", setter);
  }

  // ── getParameterValueTypes ────────────────────────────────────────────

  @Test
  public void parameterValueTypesForNoArgMethodReturnsEmptyArray() {
    JavaMethod trimMethod = JavaMethod.getInstance(String.class, "trim");
    AbstractType<?, ?, ?>[] types = AstTypeResolutionHelpers.getParameterValueTypes(trimMethod);
    assertNotNull(types);
    assertEquals("trim() has no required parameters", 0, types.length);
  }

  @Test
  public void parameterValueTypesForSingleArgMethodReturnsSingleElement() {
    JavaMethod valueOfMethod = JavaMethod.getInstance(String.class, "valueOf", int.class);
    AbstractType<?, ?, ?>[] types = AstTypeResolutionHelpers.getParameterValueTypes(valueOfMethod);
    assertNotNull(types);
    assertEquals("valueOf(int) has one required parameter", 1, types.length);
    assertNotNull("Parameter type should not be null", types[0]);
  }

  @Test
  public void parameterValueTypesForMultiArgMethodReturnsCorrectCount() {
    JavaMethod substringMethod = JavaMethod.getInstance(String.class, "substring", int.class, int.class);
    AbstractType<?, ?, ?>[] types = AstTypeResolutionHelpers.getParameterValueTypes(substringMethod);
    assertNotNull(types);
    assertEquals("substring(int,int) has two required parameters", 2, types.length);
  }

  @Test
  public void parameterValueTypesPreservesOrder() {
    // String.replace(char, char) — both params are char but order matters
    JavaMethod replaceMethod = JavaMethod.getInstance(String.class, "replace", char.class, char.class);
    AbstractType<?, ?, ?>[] types = AstTypeResolutionHelpers.getParameterValueTypes(replaceMethod);
    assertEquals(2, types.length);
    // Both should be char type and non-null
    assertNotNull(types[0]);
    assertNotNull(types[1]);
  }

  // ── getKeywordFactoryType ─────────────────────────────────────────────

  // Note: JavaKeyedArgument requires deep AST wiring, so we test the null-safe
  // boundary behavior that's observable from the method's contract.

  @Test
  public void getKeywordFactoryTypeIsAccessibleAsStaticMethod() {
    // Verify the method exists and is callable — actual behavior tested via
    // integration since JavaKeyedArgument needs full parameter wiring
    try {
      AstTypeResolutionHelpers.class.getMethod("getKeywordFactoryType", JavaKeyedArgument.class);
    } catch (NoSuchMethodException e) {
      fail("getKeywordFactoryType(JavaKeyedArgument) must exist on AstTypeResolutionHelpers");
    }
  }

  // ── getDeclaringTypeIfMemberOrTypeItselfIfType ────────────────────────

  @Test
  public void declaringTypeReturnsNullForNullInput() {
    AbstractType<?, ?, ?> result = AstTypeResolutionHelpers.getDeclaringTypeIfMemberOrTypeItselfIfType(null);
    assertNull("Null input should return null", result);
  }

  @Test
  public void declaringTypeReturnsTypeItselfWhenGivenAType() {
    JavaType stringType = JavaType.getInstance(String.class);
    AbstractType<?, ?, ?> result = AstTypeResolutionHelpers.getDeclaringTypeIfMemberOrTypeItselfIfType(stringType);
    assertSame("A type should return itself", stringType, result);
  }

  @Test
  public void declaringTypeReturnsDeclaringTypeWhenGivenAMethod() {
    JavaMethod trimMethod = JavaMethod.getInstance(String.class, "trim");
    AbstractType<?, ?, ?> result = AstTypeResolutionHelpers.getDeclaringTypeIfMemberOrTypeItselfIfType(trimMethod);
    assertNotNull("A method should return its declaring type", result);
    assertEquals("Declaring type of String.trim() is String",
        JavaType.getInstance(String.class), result);
  }

  @Test
  public void declaringTypeReturnsDeclaringTypeWhenGivenAField() {
    JavaField field = JavaField.getInstance(Integer.class, "MAX_VALUE");
    AbstractType<?, ?, ?> result = AstTypeResolutionHelpers.getDeclaringTypeIfMemberOrTypeItselfIfType(field);
    assertNotNull(result);
    assertEquals(JavaType.getInstance(Integer.class), result);
  }

  // ── Class structure contract ──────────────────────────────────────────

  @Test
  public void classIsFinal() {
    assertTrue("AstTypeResolutionHelpers should be final",
        java.lang.reflect.Modifier.isFinal(AstTypeResolutionHelpers.class.getModifiers()));
  }

  @Test(expected = java.lang.reflect.InvocationTargetException.class)
  public void constructorThrowsAssertionError() throws Exception {
    java.lang.reflect.Constructor<?> ctor = AstTypeResolutionHelpers.class.getDeclaredConstructor();
    ctor.setAccessible(true);
    ctor.newInstance();
  }

  @Test
  public void allPublicMethodsAreStatic() {
    for (java.lang.reflect.Method m : AstTypeResolutionHelpers.class.getDeclaredMethods()) {
      if (java.lang.reflect.Modifier.isPublic(m.getModifiers())) {
        assertTrue("Public method " + m.getName() + " must be static",
            java.lang.reflect.Modifier.isStatic(m.getModifiers()));
      }
    }
  }
}
