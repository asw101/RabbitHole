package org.alice.ide.ast.declaration;

import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Contract tests for DeclarationValidationDelegate (issue #637).
 *
 * Specifies the API extracted from DeclarationLikeSubstanceComposite's
 * validation/explanation methods. All tests FAIL until the delegate is created.
 *
 * Pure reflection — no GUI, no singleton instantiation.
 */
public class DeclarationValidationDelegateTest {

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
      fail("DeclarationValidationDelegate class not found — extraction not yet implemented: "
          + e.getMessage());
    }
    try {
      compositeClass = Class.forName(COMPOSITE_FQCN);
    } catch (ClassNotFoundException e) {
      fail("DeclarationLikeSubstanceComposite not found: " + e.getMessage());
    }
  }

  // ── Class structure ─────────────────────────────────────────────

  @Test
  public void isTopLevelClass() {
    assertNull("DeclarationValidationDelegate must be top-level (no enclosing class)",
        delegateClass.getEnclosingClass());
  }

  @Test
  public void isPackagePrivate() {
    int mods = delegateClass.getModifiers();
    assertFalse("must not be public", Modifier.isPublic(mods));
    assertFalse("must not be private", Modifier.isPrivate(mods));
    assertFalse("must not be protected", Modifier.isProtected(mods));
  }

  @Test
  public void isNotAbstract() {
    assertFalse("DeclarationValidationDelegate must be concrete",
        Modifier.isAbstract(delegateClass.getModifiers()));
  }

  @Test
  public void isFinalClass() {
    assertTrue("DeclarationValidationDelegate should be final",
        Modifier.isFinal(delegateClass.getModifiers()));
  }

  // ── Constructor ─────────────────────────────────────────────────

  @Test
  public void hasConstructorTakingComposite() {
    boolean found = Arrays.stream(delegateClass.getDeclaredConstructors())
        .anyMatch(c -> {
          Class<?>[] params = c.getParameterTypes();
          return params.length == 1 && compositeClass.isAssignableFrom(params[0]);
        });
    assertTrue("Must have constructor taking DeclarationLikeSubstanceComposite", found);
  }

  // ── Explanation methods (moved from composite) ──────────────────

  @Test
  public void hasGetValueTypeExplanation() {
    assertHasMethod("getValueTypeExplanation");
  }

  @Test
  public void getValueTypeExplanationReturnsString() {
    Method m = findMethod("getValueTypeExplanation");
    assertNotNull("getValueTypeExplanation must exist", m);
    assertEquals("Must return String", String.class, m.getReturnType());
  }

  @Test
  public void hasGetNameExplanation() {
    assertHasMethod("getNameExplanation");
  }

  @Test
  public void getNameExplanationReturnsString() {
    Method m = findMethod("getNameExplanation");
    assertNotNull("getNameExplanation must exist", m);
    assertEquals("Must return String", String.class, m.getReturnType());
  }

  @Test
  public void hasGetInitializerExplanation() {
    assertHasMethod("getInitializerExplanation");
  }

  @Test
  public void getInitializerExplanationReturnsString() {
    Method m = findMethod("getInitializerExplanation");
    assertNotNull("getInitializerExplanation must exist", m);
    assertEquals("Must return String", String.class, m.getReturnType());
  }

  // ── Core validation method ──────────────────────────────────────

  @Test
  public void hasComputeStatus() {
    assertHasMethod("computeStatus");
  }

  @Test
  public void computeStatusTakesErrorStatus() {
    Method m = findMethod("computeStatus");
    assertNotNull("computeStatus must exist", m);
    assertTrue("computeStatus must take at least one parameter",
        m.getParameterCount() >= 1);
  }

  // ── Internal helper (moved from composite) ──────────────────────

  @Test
  public void hasIsNullAllowedForInitializerUnderAnyCircumstances() {
    assertHasMethod("isNullAllowedForInitializerUnderAnyCircumstances");
  }

  @Test
  public void isNullAllowedReturnsBoolean() {
    Method m = findMethod("isNullAllowedForInitializerUnderAnyCircumstances");
    assertNotNull(m);
    assertEquals("Must return boolean", boolean.class, m.getReturnType());
  }

  // ── Delegate must NOT have lifecycle concerns ───────────────────

  @Test
  public void doesNotHaveWireListeners() {
    assertDoesNotDeclare("wireListeners");
  }

  @Test
  public void doesNotHaveUnwireListeners() {
    assertDoesNotDeclare("unwireListeners");
  }

  @Test
  public void doesNotHaveHandleValueTypeChanging() {
    assertDoesNotDeclare("handleValueTypeChanging");
  }

  @Test
  public void doesNotHaveHandleValueTypeChanged() {
    assertDoesNotDeclare("handleValueTypeChanged");
  }

  // ── Helpers ─────────────────────────────────────────────────────

  private static void assertHasMethod(String name) {
    boolean found = Arrays.stream(delegateClass.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals(name));
    assertTrue("DeclarationValidationDelegate must have method: " + name, found);
  }

  private static Method findMethod(String name) {
    return Arrays.stream(delegateClass.getDeclaredMethods())
        .filter(m -> m.getName().equals(name))
        .findFirst().orElse(null);
  }

  private static void assertDoesNotDeclare(String name) {
    Set<String> methods = Arrays.stream(delegateClass.getDeclaredMethods())
        .map(Method::getName)
        .collect(Collectors.toSet());
    assertFalse("ValidationDelegate should not have '" + name + "' (belongs to lifecycle delegate)",
        methods.contains(name));
  }
}
