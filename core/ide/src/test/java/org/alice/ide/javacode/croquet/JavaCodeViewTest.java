package org.alice.ide.javacode.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Supplemental reflection-based tests for JavaCodeView and JavaCodeFrameComposite.
 * Complements (but does not overlap with) the existing JavaCodeUtilitiesTest which
 * covers: loadability, getInstance(), superclass name, setDeclaration().
 *
 * This file adds: field verification, constructor signatures, method access modifiers,
 * generic superclass params, inner class structure, and cross-class type checks.
 *
 * All tests are headless-safe (pure reflection, no GUI instantiation).
 */
public class JavaCodeViewTest {

  private static final String VIEW_FQN =
      "org.alice.ide.javacode.croquet.views.JavaCodeView";
  private static final String COMPOSITE_FQN =
      "org.alice.ide.javacode.croquet.JavaCodeFrameComposite";

  // ========================================================================
  // JavaCodeView — structural details beyond JavaCodeUtilitiesTest
  // ========================================================================

  @Test
  public void javaCodeView_extendsHtmlView() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    assertEquals("HtmlView", cls.getSuperclass().getSimpleName());
  }

  @Test
  public void javaCodeView_htmlViewFQN() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    assertEquals("org.lgna.croquet.views.HtmlView", cls.getSuperclass().getName());
  }

  @Test
  public void javaCodeView_isPublic() throws ClassNotFoundException {
    assertTrue(Modifier.isPublic(Class.forName(VIEW_FQN).getModifiers()));
  }

  @Test
  public void javaCodeView_isConcrete() throws ClassNotFoundException {
    assertFalse(Modifier.isAbstract(Class.forName(VIEW_FQN).getModifiers()));
  }

  @Test
  public void javaCodeView_hasTwoConstructors() throws ClassNotFoundException {
    Constructor<?>[] ctors = Class.forName(VIEW_FQN).getDeclaredConstructors();
    assertEquals(2, ctors.length);
  }

  @Test
  public void javaCodeView_hasNoArgConstructor() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    boolean found = false;
    for (Constructor<?> c : cls.getDeclaredConstructors()) {
      if (c.getParameterCount() == 0) {
        found = true;
        assertTrue(Modifier.isPublic(c.getModifiers()));
      }
    }
    assertTrue("No-arg constructor not found", found);
  }

  @Test
  public void javaCodeView_hasDeclarationConstructor() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    boolean found = false;
    for (Constructor<?> c : cls.getDeclaredConstructors()) {
      if (c.getParameterCount() == 1) {
        found = true;
        assertTrue(Modifier.isPublic(c.getModifiers()));
        assertEquals("AbstractDeclaration", c.getParameterTypes()[0].getSimpleName());
      }
    }
    assertTrue("Declaration constructor not found", found);
  }

  @Test
  public void javaCodeView_hasGetDeclaration() throws ClassNotFoundException, NoSuchMethodException {
    Method m = Class.forName(VIEW_FQN).getMethod("getDeclaration");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals("AbstractDeclaration", m.getReturnType().getSimpleName());
  }

  @Test
  public void javaCodeView_hasIsLambdaToggleEnabledField() throws ClassNotFoundException, NoSuchFieldException {
    Field f = Class.forName(VIEW_FQN).getDeclaredField("IS_LAMBDA_TOGGLE_ENABLED");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertEquals(boolean.class, f.getType());
  }

  @Test
  public void javaCodeView_hasIsMouseWheelFontField() throws ClassNotFoundException, NoSuchFieldException {
    Field f = Class.forName(VIEW_FQN).getDeclaredField("IS_MOUSE_WHEEL_FONT_ADJUSTMENT_DESIRED");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertEquals(boolean.class, f.getType());
  }

  @Test
  public void javaCodeView_hasDeclarationField() throws ClassNotFoundException, NoSuchFieldException {
    Field f = Class.forName(VIEW_FQN).getDeclaredField("declaration");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertEquals("AbstractDeclaration", f.getType().getSimpleName());
  }

  @Test
  public void javaCodeView_hasUndoHistoryField() throws ClassNotFoundException, NoSuchFieldException {
    Field f = Class.forName(VIEW_FQN).getDeclaredField("undoHistory");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
  }

  @Test
  public void javaCodeView_hasFontSizeField() throws ClassNotFoundException, NoSuchFieldException {
    Field f = Class.forName(VIEW_FQN).getDeclaredField("fontSize");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertEquals(int.class, f.getType());
  }

  @Test
  public void javaCodeView_hasIsLambdaSupportedField() throws ClassNotFoundException, NoSuchFieldException {
    Field f = Class.forName(VIEW_FQN).getDeclaredField("isLambdaSupported");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertEquals(boolean.class, f.getType());
  }

  @Test
  public void javaCodeView_hasHandleDisplayable() throws ClassNotFoundException, NoSuchMethodException {
    Method m = Class.forName(VIEW_FQN).getDeclaredMethod("handleDisplayable");
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void javaCodeView_hasHandleUndisplayable() throws ClassNotFoundException, NoSuchMethodException {
    Method m = Class.forName(VIEW_FQN).getDeclaredMethod("handleUndisplayable");
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void javaCodeView_hasIsRightToLeftAllowed() throws ClassNotFoundException, NoSuchMethodException {
    Method m = Class.forName(VIEW_FQN).getDeclaredMethod(
        "isRightToLeftComponentOrientationAllowed", boolean.class);
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void javaCodeView_hasPrivateUpdateHtml() throws ClassNotFoundException, NoSuchMethodException {
    Method m = Class.forName(VIEW_FQN).getDeclaredMethod("updateHtml");
    assertTrue(Modifier.isPrivate(m.getModifiers()));
  }

  @Test
  public void javaCodeView_hasStaticGetProjectUndoHistory() throws ClassNotFoundException, NoSuchMethodException {
    Method m = Class.forName(VIEW_FQN).getDeclaredMethod("getProjectUndoHistory");
    assertTrue(Modifier.isPrivate(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void javaCodeView_packageCorrect() throws ClassNotFoundException {
    assertEquals("org.alice.ide.javacode.croquet.views",
        Class.forName(VIEW_FQN).getPackage().getName());
  }

  // ========================================================================
  // JavaCodeFrameComposite — structural details beyond JavaCodeUtilitiesTest
  // ========================================================================

  @Test
  public void composite_isPublic() throws ClassNotFoundException {
    assertTrue(Modifier.isPublic(Class.forName(COMPOSITE_FQN).getModifiers()));
  }

  @Test
  public void composite_isConcrete() throws ClassNotFoundException {
    assertFalse(Modifier.isAbstract(Class.forName(COMPOSITE_FQN).getModifiers()));
  }

  @Test
  public void composite_hasPrivateConstructor() throws ClassNotFoundException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertTrue(Modifier.isPrivate(ctors[0].getModifiers()));
    assertEquals(0, ctors[0].getParameterCount());
  }

  @Test
  public void composite_hasSingletonHolder() throws ClassNotFoundException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    boolean found = false;
    for (Class<?> inner : cls.getDeclaredClasses()) {
      if (inner.getSimpleName().equals("SingletonHolder")) {
        found = true;
      }
    }
    assertTrue("SingletonHolder not found", found);
  }

  @Test
  public void composite_hasDeclarationListenerField() throws ClassNotFoundException, NoSuchFieldException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    Field f = cls.getDeclaredField("declarationListener");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void composite_declarationListenerFieldType() throws ClassNotFoundException, NoSuchFieldException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    Field f = cls.getDeclaredField("declarationListener");
    String typeName = f.getType().getSimpleName();
    // It's a MetaDeclarationFauxState.ValueListener or similar interface
    assertTrue("Expected listener type, got: " + typeName,
        typeName.contains("Listener") || typeName.contains("ValueListener"));
  }

  @Test
  public void composite_hasJavaCodeViewField() throws ClassNotFoundException, NoSuchFieldException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    Field f = cls.getDeclaredField("javaCodeView");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertEquals("JavaCodeView", f.getType().getSimpleName());
  }

  @Test
  public void composite_hasCreateScrollPaneIfDesired() throws ClassNotFoundException, NoSuchMethodException {
    Method m = Class.forName(COMPOSITE_FQN).getDeclaredMethod("createScrollPaneIfDesired");
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertEquals("ScrollPane", m.getReturnType().getSimpleName());
  }

  @Test
  public void composite_hasCreateView() throws ClassNotFoundException, NoSuchMethodException {
    Method m = Class.forName(COMPOSITE_FQN).getDeclaredMethod("createView");
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertEquals("Panel", m.getReturnType().getSimpleName());
  }

  @Test
  public void composite_hasGetWiderGoldenRatioSizeFromHeight() throws ClassNotFoundException, NoSuchMethodException {
    Method m = Class.forName(COMPOSITE_FQN).getDeclaredMethod("getWiderGoldenRatioSizeFromHeight");
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertEquals(Integer.class, m.getReturnType());
  }

  @Test
  public void composite_hasHandlePreActivation() throws ClassNotFoundException, NoSuchMethodException {
    Method m = Class.forName(COMPOSITE_FQN).getMethod("handlePreActivation");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void composite_hasHandlePostDeactivation() throws ClassNotFoundException, NoSuchMethodException {
    Method m = Class.forName(COMPOSITE_FQN).getMethod("handlePostDeactivation");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void composite_genericSuperclassUsesPanel() throws ClassNotFoundException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    Type genericSuper = cls.getGenericSuperclass();
    assertTrue(genericSuper instanceof ParameterizedType);
    ParameterizedType pt = (ParameterizedType) genericSuper;
    Type[] args = pt.getActualTypeArguments();
    assertEquals(1, args.length);
    assertTrue(args[0].toString().contains("Panel"));
  }

  @Test
  public void composite_packageCorrect() throws ClassNotFoundException {
    assertEquals("org.alice.ide.javacode.croquet",
        Class.forName(COMPOSITE_FQN).getPackage().getName());
  }

  // ========================================================================
  // Cross-class verification
  // ========================================================================

  @Test
  public void composite_javaCodeViewFieldMatchesViewClass() throws ClassNotFoundException, NoSuchFieldException {
    Class<?> composite = Class.forName(COMPOSITE_FQN);
    Class<?> view = Class.forName(VIEW_FQN);
    Field f = composite.getDeclaredField("javaCodeView");
    assertEquals(view, f.getType());
  }

  @Test
  public void view_setDeclarationParamMatchesGetDeclarationReturn() throws ClassNotFoundException, NoSuchMethodException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Method setter = cls.getMethod("setDeclaration",
        org.lgna.project.ast.AbstractDeclaration.class);
    Method getter = cls.getMethod("getDeclaration");
    assertEquals(setter.getParameterTypes()[0], getter.getReturnType());
  }

  @Test
  public void bothClasses_inDifferentPackages() throws ClassNotFoundException {
    Class<?> composite = Class.forName(COMPOSITE_FQN);
    Class<?> view = Class.forName(VIEW_FQN);
    assertNotEquals(composite.getPackage().getName(), view.getPackage().getName());
    assertTrue(view.getPackage().getName().startsWith(
        composite.getPackage().getName()));
  }

  @Test
  public void view_fieldCount() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Field[] fields = cls.getDeclaredFields();
    // IS_LAMBDA_TOGGLE_ENABLED, IS_MOUSE_WHEEL_FONT_ADJUSTMENT_DESIRED,
    // undoHistory, declaration, fontSize, isLambdaSupported = at least 6
    assertTrue("Expected at least 6 fields, got " + fields.length,
        fields.length >= 6);
  }

  @Test
  public void composite_fieldCount() throws ClassNotFoundException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    Field[] fields = cls.getDeclaredFields();
    // declarationListener, javaCodeView = at least 2
    assertTrue("Expected at least 2 fields, got " + fields.length,
        fields.length >= 2);
  }
}
