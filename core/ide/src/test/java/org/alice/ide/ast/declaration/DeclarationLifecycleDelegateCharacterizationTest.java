package org.alice.ide.ast.declaration;

import org.alice.ide.ast.ReflectionTestHelper;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Characterization tests for {@link DeclarationDialogLifecycleDelegate} (issue #637).
 * Verifies structural contract: listener fields, wire/unwire symmetry,
 * type-to-initializer cache, method signatures, visibility.
 *
 * Complements DeclarationDialogLifecycleDelegateExtendedTest.
 * Pure reflection — no GUI, no singleton instantiation.
 */
public class DeclarationLifecycleDelegateCharacterizationTest {

  private static final String DELEGATE_FQCN =
      "org.alice.ide.ast.declaration.DeclarationDialogLifecycleDelegate";
  private static final String COMPOSITE_FQCN =
      "org.alice.ide.ast.declaration.DeclarationLikeSubstanceComposite";

  private static Class<?> delegateClass;
  private static Class<?> compositeClass;

  @BeforeClass
  public static void loadClasses() {
    try {
      delegateClass = Class.forName(DELEGATE_FQCN);
    } catch (ClassNotFoundException e) {
      fail("DeclarationDialogLifecycleDelegate not found: " + e.getMessage());
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

  // ── Composite field ────────────────────────────────────────────

  @Test
  public void hasCompositeField() {
    ReflectionTestHelper.assertFieldIsPrivateFinal(delegateClass, "composite");
  }

  // ── Constructor ────────────────────────────────────────────────

  @Test
  public void hasSinglePackagePrivateConstructor() {
    ReflectionTestHelper.assertSinglePackagePrivateConstructor(delegateClass);
  }

  @Test
  public void constructorTakesComposite() {
    Constructor<?> ctor = delegateClass.getDeclaredConstructors()[0];
    Class<?>[] params = ctor.getParameterTypes();
    assertEquals("Constructor takes exactly 1 parameter", 1, params.length);
    assertTrue("Parameter must be assignable from composite",
        compositeClass.isAssignableFrom(params[0]));
  }

  // ── Listener fields ────────────────────────────────────────────

  @Test
  public void hasIsArrayValueTypeListenerField() {
    ReflectionTestHelper.assertFieldIsPrivateFinal(delegateClass, "isArrayValueTypeListener");
  }

  @Test
  public void hasValueComponentTypeListenerField() {
    ReflectionTestHelper.assertFieldIsPrivateFinal(delegateClass, "valueComponentTypeListener");
  }

  @Test
  public void hasInitializerListenerField() {
    ReflectionTestHelper.assertFieldIsPrivateFinal(delegateClass, "initializerListener");
  }

  // ── Type-to-initializer cache ──────────────────────────────────

  @Test
  public void hasMapTypeToInitializerField() {
    ReflectionTestHelper.assertFieldIsPrivateFinal(delegateClass, "mapTypeToInitializer");
  }

  @Test
  public void mapTypeToInitializerIsMapType() {
    Field f = findField("mapTypeToInitializer");
    assertNotNull(f);
    assertTrue("Must be a Map type",
        Map.class.isAssignableFrom(f.getType()));
  }

  // ── Method inventory ───────────────────────────────────────────

  @Test
  public void hasWireListenersMethod() {
    ReflectionTestHelper.assertMethodPresent(delegateClass, "wireListeners");
  }

  @Test
  public void hasUnwireListenersMethod() {
    ReflectionTestHelper.assertMethodPresent(delegateClass, "unwireListeners");
  }

  @Test
  public void hasClearTypeToInitializerCacheMethod() {
    ReflectionTestHelper.assertMethodPresent(delegateClass, "clearTypeToInitializerCache");
  }

  @Test
  public void hasHandleValueTypeChangingMethod() {
    ReflectionTestHelper.assertMethodPresent(delegateClass, "handleValueTypeChanging");
  }

  @Test
  public void hasHandleValueTypeChangedMethod() {
    ReflectionTestHelper.assertMethodPresent(delegateClass, "handleValueTypeChanged");
  }

  // ── Wire/unwire symmetry ───────────────────────────────────────

  @Test
  public void wireAndUnwire_sameVisibility() {
    Method wire = findMethod("wireListeners");
    Method unwire = findMethod("unwireListeners");
    assertNotNull("wireListeners must exist", wire);
    assertNotNull("unwireListeners must exist", unwire);
    assertEquals("wire and unwire must have same modifiers",
        wire.getModifiers(), unwire.getModifiers());
  }

  @Test
  public void wireListeners_returnsVoid() {
    Method m = findMethod("wireListeners");
    assertNotNull(m);
    assertEquals("wireListeners must return void", void.class, m.getReturnType());
  }

  @Test
  public void unwireListeners_returnsVoid() {
    Method m = findMethod("unwireListeners");
    assertNotNull(m);
    assertEquals("unwireListeners must return void", void.class, m.getReturnType());
  }

  @Test
  public void wireListeners_takesNoParameters() {
    Method m = findMethod("wireListeners");
    assertNotNull(m);
    assertEquals("wireListeners must take no parameters", 0, m.getParameterCount());
  }

  @Test
  public void unwireListeners_takesNoParameters() {
    Method m = findMethod("unwireListeners");
    assertNotNull(m);
    assertEquals("unwireListeners must take no parameters", 0, m.getParameterCount());
  }

  // ── Method visibility ──────────────────────────────────────────

  @Test
  public void allMethodsArePackagePrivate() {
    ReflectionTestHelper.assertAllMethodsPackagePrivate(delegateClass);
  }

  // ── No validation methods (those belong to validation delegate)

  @Test
  public void doesNotDeclareGetValueTypeExplanation() {
    ReflectionTestHelper.assertMethodAbsent(delegateClass, "getValueTypeExplanation", "validation delegate");
  }

  @Test
  public void doesNotDeclareGetNameExplanation() {
    ReflectionTestHelper.assertMethodAbsent(delegateClass, "getNameExplanation", "validation delegate");
  }

  @Test
  public void doesNotDeclareGetInitializerExplanation() {
    ReflectionTestHelper.assertMethodAbsent(delegateClass, "getInitializerExplanation", "validation delegate");
  }

  @Test
  public void doesNotDeclareComputeStatus() {
    ReflectionTestHelper.assertMethodAbsent(delegateClass, "computeStatus", "validation delegate");
  }

  @Test
  public void doesNotDeclareIsNullAllowed() {
    ReflectionTestHelper.assertMethodAbsent(delegateClass, "isNullAllowedForInitializerUnderAnyCircumstances", "validation delegate");
  }

  // ── No static members ──────────────────────────────────────────

  @Test
  public void hasNoStaticMembers() {
    ReflectionTestHelper.assertNoStaticMembers(delegateClass);
  }

  // ── Handle methods return void ─────────────────────────────────

  @Test
  public void handleValueTypeChanging_returnsVoid() {
    Method m = findMethod("handleValueTypeChanging");
    assertNotNull(m);
    assertEquals("Must return void", void.class, m.getReturnType());
  }

  @Test
  public void handleValueTypeChanged_returnsVoid() {
    Method m = findMethod("handleValueTypeChanged");
    assertNotNull(m);
    assertEquals("Must return void", void.class, m.getReturnType());
  }

  @Test
  public void clearTypeToInitializerCache_returnsVoid() {
    Method m = findMethod("clearTypeToInitializerCache");
    assertNotNull(m);
    assertEquals("Must return void", void.class, m.getReturnType());
  }

  // ── Helpers ────────────────────────────────────────────────────

  private static Field findField(String name) {
    return ReflectionTestHelper.findField(delegateClass, name);
  }

  private static Method findMethod(String name) {
    return ReflectionTestHelper.findMethod(delegateClass, name);
  }
}
