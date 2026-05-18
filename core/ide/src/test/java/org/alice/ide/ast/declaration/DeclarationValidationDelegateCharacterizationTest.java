package org.alice.ide.ast.declaration;

import org.alice.ide.ast.ReflectionTestHelper;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Characterization tests for {@link DeclarationValidationDelegate} (issue #637).
 * Verifies structural contract: method signatures, return types, visibility,
 * field types, separation of concerns from lifecycle delegate.
 *
 * Complements the contract tests in {@link DeclarationValidationDelegateTest}
 * with deeper structural analysis.
 * Pure reflection — no GUI, no singleton instantiation.
 */
public class DeclarationValidationDelegateCharacterizationTest {

  private static final String DELEGATE_FQCN =
      "org.alice.ide.ast.declaration.DeclarationValidationDelegate";
  private static final String COMPOSITE_FQCN =
      "org.alice.ide.ast.declaration.DeclarationLikeSubstanceComposite";

  private static Class<?> delegateClass;
  private static Class<?> compositeClass;

  @BeforeClass
  public static void loadClasses() {
    try {
      delegateClass = Class.forName(DELEGATE_FQCN);
    } catch (ClassNotFoundException e) {
      fail("DeclarationValidationDelegate not found: " + e.getMessage());
    }
    try {
      compositeClass = Class.forName(COMPOSITE_FQCN);
    } catch (ClassNotFoundException e) {
      fail("DeclarationLikeSubstanceComposite not found: " + e.getMessage());
    }
  }

  // ── Class-level invariants ─────────────────────────────────────

  @Test
  public void isFinalPackagePrivateClass() {
    ReflectionTestHelper.assertFinalPackagePrivateClass(delegateClass);
  }

  @Test
  public void doesNotExtendAnyClass_exceptObject() {
    ReflectionTestHelper.assertExtendsOnlyObject(delegateClass);
  }

  @Test
  public void implementsNoInterfaces() {
    ReflectionTestHelper.assertImplementsNoInterfaces(delegateClass);
  }

  @Test
  public void isNotAnInnerClass() {
    ReflectionTestHelper.assertIsTopLevelClass(delegateClass);
  }

  @Test
  public void hasNoStaticMembers() {
    ReflectionTestHelper.assertNoStaticMembers(delegateClass);
  }

  // ── Composite field ────────────────────────────────────────────

  @Test
  public void hasCompositeField() {
    boolean found = Arrays.stream(delegateClass.getDeclaredFields())
        .anyMatch(f -> compositeClass.isAssignableFrom(f.getType()));
    assertTrue("Must have a field of type DeclarationLikeSubstanceComposite", found);
  }

  @Test
  public void compositeFieldIsPrivateFinal() {
    Field compositeField = Arrays.stream(delegateClass.getDeclaredFields())
        .filter(f -> compositeClass.isAssignableFrom(f.getType()))
        .findFirst().orElse(null);
    assertNotNull("Composite field must exist", compositeField);
    ReflectionTestHelper.assertFieldIsPrivateFinal(delegateClass, compositeField.getName());
  }

  // ── Constructor ────────────────────────────────────────────────

  @Test
  public void hasSinglePackagePrivateConstructor() {
    ReflectionTestHelper.assertSinglePackagePrivateConstructor(delegateClass);
  }

  @Test
  public void constructorTakesCompositeWithWildcard() {
    Constructor<?> ctor = delegateClass.getDeclaredConstructors()[0];
    Class<?>[] params = ctor.getParameterTypes();
    assertEquals("Constructor takes exactly 1 parameter", 1, params.length);
    assertTrue("Parameter must be assignable from composite",
        compositeClass.isAssignableFrom(params[0]));
  }

  // ── Method inventory ───────────────────────────────────────────

  @Test
  public void hasExactlyFiveMethods() {
    Method[] methods = delegateClass.getDeclaredMethods();
    assertEquals("Should have 5 methods: getValueTypeExplanation, getNameExplanation, " +
            "getInitializerExplanation, isNullAllowedForInitializerUnderAnyCircumstances, computeStatus",
        5, methods.length);
  }

  @Test
  public void allMethodsArePackagePrivate() {
    ReflectionTestHelper.assertAllMethodsPackagePrivate(delegateClass);
  }

  // ── Explanation method signatures ──────────────────────────────

  @Test
  public void getValueTypeExplanation_takesAbstractType() {
    Method m = findMethod("getValueTypeExplanation");
    assertNotNull(m);
    assertEquals("Must take 1 parameter", 1, m.getParameterCount());
    // Parameter type should be AbstractType or a superclass
    assertTrue("Parameter should be AbstractType-compatible",
        m.getParameterTypes()[0].getSimpleName().contains("AbstractType")
            || m.getParameterTypes()[0] == Object.class);
  }

  @Test
  public void getNameExplanation_takesString() {
    Method m = findMethod("getNameExplanation");
    assertNotNull(m);
    assertEquals("Must take 1 parameter", 1, m.getParameterCount());
    assertEquals("Parameter should be String", String.class, m.getParameterTypes()[0]);
  }

  @Test
  public void getInitializerExplanation_takesExpression() {
    Method m = findMethod("getInitializerExplanation");
    assertNotNull(m);
    assertEquals("Must take 1 parameter", 1, m.getParameterCount());
  }

  @Test
  public void computeStatus_returnsBoolean() {
    Method m = findMethod("computeStatus");
    assertNotNull(m);
    assertEquals("Must return boolean", boolean.class, m.getReturnType());
  }

  @Test
  public void isNullAllowed_returnsBoolean() {
    Method m = findMethod("isNullAllowedForInitializerUnderAnyCircumstances");
    assertNotNull(m);
    assertEquals("Must return boolean", boolean.class, m.getReturnType());
  }

  @Test
  public void isNullAllowed_takesNoParameters() {
    Method m = findMethod("isNullAllowedForInitializerUnderAnyCircumstances");
    assertNotNull(m);
    assertEquals("Must take no parameters", 0, m.getParameterCount());
  }

  // ── Separation of concerns: no lifecycle methods ───────────────

  @Test
  public void doesNotDeclareWireListeners() {
    ReflectionTestHelper.assertMethodAbsent(delegateClass, "wireListeners", "lifecycle delegate");
  }

  @Test
  public void doesNotDeclareUnwireListeners() {
    ReflectionTestHelper.assertMethodAbsent(delegateClass, "unwireListeners", "lifecycle delegate");
  }

  @Test
  public void doesNotDeclareHandleValueTypeChanging() {
    ReflectionTestHelper.assertMethodAbsent(delegateClass, "handleValueTypeChanging", "lifecycle delegate");
  }

  @Test
  public void doesNotDeclareHandleValueTypeChanged() {
    ReflectionTestHelper.assertMethodAbsent(delegateClass, "handleValueTypeChanged", "lifecycle delegate");
  }

  @Test
  public void doesNotDeclareClearTypeToInitializerCache() {
    ReflectionTestHelper.assertMethodAbsent(delegateClass, "clearTypeToInitializerCache", "lifecycle delegate");
  }

  // ── No listener fields ─────────────────────────────────────────

  @Test
  public void hasNoListenerFields() {
    for (Field f : delegateClass.getDeclaredFields()) {
      String typeName = f.getType().getSimpleName();
      assertFalse("Should not have listener field: " + f.getName(),
          typeName.contains("Listener") || typeName.contains("ValueListener"));
    }
  }

  @Test
  public void hasNoMapFields() {
    for (Field f : delegateClass.getDeclaredFields()) {
      String typeName = f.getType().getSimpleName();
      assertFalse("Should not have Map field: " + f.getName(),
          typeName.contains("Map"));
    }
  }

  // ── Helpers ────────────────────────────────────────────────────

  private static Method findMethod(String name) {
    return ReflectionTestHelper.findMethod(delegateClass, name);
  }
}
