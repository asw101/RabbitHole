package org.alice.ide.ast.declaration;

import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Contract tests for DeclarationDialogLifecycleDelegate (issue #637).
 *
 * Specifies the API extracted from DeclarationLikeSubstanceComposite's
 * listener management, type→initializer cache, and dialog lifecycle wiring.
 * All tests FAIL until the delegate is created.
 *
 * Pure reflection — no GUI, no singleton instantiation.
 */
public class DeclarationDialogLifecycleDelegateTest {

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
      fail("DeclarationDialogLifecycleDelegate class not found — extraction not yet implemented: "
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
    assertNull("DeclarationDialogLifecycleDelegate must be top-level (no enclosing class)",
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
    assertFalse("DeclarationDialogLifecycleDelegate must be concrete",
        Modifier.isAbstract(delegateClass.getModifiers()));
  }

  @Test
  public void isFinalClass() {
    assertTrue("DeclarationDialogLifecycleDelegate should be final",
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

  // ── Listener wiring/unwiring ────────────────────────────────────

  @Test
  public void hasWireListeners() {
    assertHasMethod("wireListeners");
  }

  @Test
  public void wireListenersReturnsVoid() {
    Method m = findMethod("wireListeners");
    assertNotNull("wireListeners must exist", m);
    assertEquals("wireListeners must return void", void.class, m.getReturnType());
  }

  @Test
  public void hasUnwireListeners() {
    assertHasMethod("unwireListeners");
  }

  @Test
  public void unwireListenersReturnsVoid() {
    Method m = findMethod("unwireListeners");
    assertNotNull("unwireListeners must exist", m);
    assertEquals("unwireListeners must return void", void.class, m.getReturnType());
  }

  // ── Type→initializer cache ──────────────────────────────────────

  @Test
  public void hasClearTypeToInitializerCache() {
    assertHasMethod("clearTypeToInitializerCache");
  }

  @Test
  public void clearCacheReturnsVoid() {
    Method m = findMethod("clearTypeToInitializerCache");
    assertNotNull(m);
    assertEquals("clearTypeToInitializerCache must return void", void.class, m.getReturnType());
  }

  @Test
  public void hasMapTypeToInitializerField() {
    assertFieldExists("mapTypeToInitializer");
  }

  @Test
  public void mapTypeToInitializerIsFinal() {
    try {
      Field f = delegateClass.getDeclaredField("mapTypeToInitializer");
      assertTrue("mapTypeToInitializer must be final", Modifier.isFinal(f.getModifiers()));
    } catch (NoSuchFieldException e) {
      fail("Missing mapTypeToInitializer field");
    }
  }

  // ── Value-type-change handlers ──────────────────────────────────

  @Test
  public void hasHandleValueTypeChanging() {
    assertHasMethod("handleValueTypeChanging");
  }

  @Test
  public void handleValueTypeChangingReturnsVoid() {
    Method m = findMethod("handleValueTypeChanging");
    assertNotNull(m);
    assertEquals(void.class, m.getReturnType());
  }

  @Test
  public void hasHandleValueTypeChanged() {
    assertHasMethod("handleValueTypeChanged");
  }

  @Test
  public void handleValueTypeChangedReturnsVoid() {
    Method m = findMethod("handleValueTypeChanged");
    assertNotNull(m);
    assertEquals(void.class, m.getReturnType());
  }

  // ── Delegate must NOT have validation concerns ──────────────────

  @Test
  public void doesNotHaveGetValueTypeExplanation() {
    assertDoesNotDeclare("getValueTypeExplanation");
  }

  @Test
  public void doesNotHaveGetNameExplanation() {
    assertDoesNotDeclare("getNameExplanation");
  }

  @Test
  public void doesNotHaveGetInitializerExplanation() {
    assertDoesNotDeclare("getInitializerExplanation");
  }

  @Test
  public void doesNotHaveComputeStatus() {
    assertDoesNotDeclare("computeStatus");
  }

  // ── Listener symmetry: wire and unwire are structurally paired ──

  @Test
  public void wireAndUnwireHaveSameParameterCount() {
    Method wire = findMethod("wireListeners");
    Method unwire = findMethod("unwireListeners");
    assertNotNull("wireListeners must exist", wire);
    assertNotNull("unwireListeners must exist", unwire);
    assertEquals("wireListeners and unwireListeners must take same number of parameters",
        wire.getParameterCount(), unwire.getParameterCount());
  }

  // ── Notify-view method for initializer changes ──────────────────

  @Test
  public void hasNotifyInitializerChanged() {
    // The delegate should have a method to notify the view of initializer changes,
    // or the listener should callback to the composite. Either way, the delegate
    // must expose this coordination point.
    boolean hasNotify = Arrays.stream(delegateClass.getDeclaredMethods())
        .anyMatch(m -> m.getName().contains("nitializerChanged") ||
            m.getName().contains("nitializerListener"));
    // Allow either approach: explicit notify method or listener accessor
    boolean hasListenerField = false;
    try {
      delegateClass.getDeclaredField("initializerListener");
      hasListenerField = true;
    } catch (NoSuchFieldException ignored) {
    }
    assertTrue("Delegate must handle initializer-change notification (method or field)",
        hasNotify || hasListenerField);
  }

  // ═══════════════════════════════════════════════════════════════════
  // Helpers
  // ═══════════════════════════════════════════════════════════════════

  private static void assertHasMethod(String name) {
    boolean found = Arrays.stream(delegateClass.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals(name));
    assertTrue("DeclarationDialogLifecycleDelegate must have method: " + name, found);
  }

  private static Method findMethod(String name) {
    return Arrays.stream(delegateClass.getDeclaredMethods())
        .filter(m -> m.getName().equals(name))
        .findFirst().orElse(null);
  }

  private static void assertFieldExists(String name) {
    try {
      delegateClass.getDeclaredField(name);
    } catch (NoSuchFieldException e) {
      fail("DeclarationDialogLifecycleDelegate must have field: " + name);
    }
  }

  private static void assertDoesNotDeclare(String name) {
    Set<String> methods = Arrays.stream(delegateClass.getDeclaredMethods())
        .map(Method::getName)
        .collect(Collectors.toSet());
    assertFalse("LifecycleDelegate should not have '" + name + "' (belongs to validation delegate)",
        methods.contains(name));
  }
}
