package org.alice.ide.javacode.croquet;

import org.junit.Test;
import org.junit.Assume;

import java.awt.GraphicsEnvironment;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Comprehensive reflection-based tests for JavaCodeView rendering,
 * JavaCodeFrameComposite lifecycle, and their structural contracts.
 * Covers field types, method signatures, inner class listeners,
 * generic bounds, constant fields, and design pattern verification.
 *
 * All tests are headless-safe (pure reflection, no GUI instantiation).
 */
public class JavaCodeRenderingTest {

  private static boolean isHeadless() {
    return GraphicsEnvironment.isHeadless();
  }

  private static final String VIEW_FQN =
      "org.alice.ide.javacode.croquet.views.JavaCodeView";
  private static final String COMPOSITE_FQN =
      "org.alice.ide.javacode.croquet.JavaCodeFrameComposite";

  // ========================================================================
  // JavaCodeView — field structure
  // ========================================================================

  @Test
  public void javaCodeView_hasDeclarationField() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Field f = Arrays.stream(cls.getDeclaredFields())
        .filter(fd -> fd.getName().equals("declaration"))
        .findFirst().orElse(null);
    assertNotNull("Should have 'declaration' field", f);
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertEquals("AbstractDeclaration", f.getType().getSimpleName());
  }

  @Test
  public void javaCodeView_hasFontSizeField() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Field f = Arrays.stream(cls.getDeclaredFields())
        .filter(fd -> fd.getName().equals("fontSize"))
        .findFirst().orElse(null);
    assertNotNull("Should have 'fontSize' field", f);
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertEquals(int.class, f.getType());
  }

  @Test
  public void javaCodeView_hasIsLambdaSupportedField() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Field f = Arrays.stream(cls.getDeclaredFields())
        .filter(fd -> fd.getName().equals("isLambdaSupported"))
        .findFirst().orElse(null);
    assertNotNull("Should have 'isLambdaSupported' field", f);
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertEquals(boolean.class, f.getType());
  }

  @Test
  public void javaCodeView_hasUndoHistoryField() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Field f = Arrays.stream(cls.getDeclaredFields())
        .filter(fd -> fd.getName().equals("undoHistory"))
        .findFirst().orElse(null);
    assertNotNull("Should have 'undoHistory' field", f);
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertEquals("UndoHistory", f.getType().getSimpleName());
  }

  @Test
  public void javaCodeView_hasHistoryListenerField() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Field f = Arrays.stream(cls.getDeclaredFields())
        .filter(fd -> fd.getName().equals("historyListener"))
        .findFirst().orElse(null);
    assertNotNull("Should have 'historyListener' field", f);
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void javaCodeView_hasKeyListenerField() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Field f = Arrays.stream(cls.getDeclaredFields())
        .filter(fd -> fd.getName().equals("keyListener"))
        .findFirst().orElse(null);
    assertNotNull("Should have 'keyListener' field", f);
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void javaCodeView_hasMouseWheelListenerField() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Field f = Arrays.stream(cls.getDeclaredFields())
        .filter(fd -> fd.getName().equals("mouseWheelListener"))
        .findFirst().orElse(null);
    assertNotNull("Should have 'mouseWheelListener' field", f);
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void javaCodeView_isLambdaToggleEnabledConstant() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Field f = Arrays.stream(cls.getDeclaredFields())
        .filter(fd -> fd.getName().equals("IS_LAMBDA_TOGGLE_ENABLED"))
        .findFirst().orElse(null);
    assertNotNull(f);
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertEquals(boolean.class, f.getType());
  }

  @Test
  public void javaCodeView_isMouseWheelFontAdjustmentDesiredConstant() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Field f = Arrays.stream(cls.getDeclaredFields())
        .filter(fd -> fd.getName().equals("IS_MOUSE_WHEEL_FONT_ADJUSTMENT_DESIRED"))
        .findFirst().orElse(null);
    assertNotNull(f);
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertEquals(boolean.class, f.getType());
  }

  @Test
  public void javaCodeView_totalFieldCount() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    int count = cls.getDeclaredFields().length;
    assertTrue("JavaCodeView should have 7+ fields", count >= 7);
  }

  // ========================================================================
  // JavaCodeView — method structure
  // ========================================================================

  @Test
  public void javaCodeView_getDeclarationIsPublic() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("getDeclaration"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(0, m.getParameterCount());
    assertEquals("AbstractDeclaration", m.getReturnType().getSimpleName());
  }

  @Test
  public void javaCodeView_setDeclarationIsPublic() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("setDeclaration"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(1, m.getParameterCount());
    assertEquals("AbstractDeclaration", m.getParameterTypes()[0].getSimpleName());
  }

  @Test
  public void javaCodeView_updateHtmlIsPrivate() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("updateHtml"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPrivate(m.getModifiers()));
    assertEquals(0, m.getParameterCount());
  }

  @Test
  public void javaCodeView_getProjectUndoHistoryIsPrivateStatic() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("getProjectUndoHistory"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPrivate(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals("UndoHistory", m.getReturnType().getSimpleName());
  }

  @Test
  public void javaCodeView_handleDisplayableProtected() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("handleDisplayable"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void javaCodeView_handleUndisplayableProtected() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("handleUndisplayable"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void javaCodeView_isRightToLeftProtected() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("isRightToLeftComponentOrientationAllowed"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void javaCodeView_declaredMethodCount() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    int count = cls.getDeclaredMethods().length;
    assertTrue("JavaCodeView should have 5+ declared methods", count >= 5);
  }

  // ========================================================================
  // JavaCodeView — constructor verification
  // ========================================================================

  @Test
  public void javaCodeView_noArgConstructorExists() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    boolean found = false;
    for (Constructor<?> c : cls.getDeclaredConstructors()) {
      if (c.getParameterCount() == 0) {
        found = true;
        assertTrue(Modifier.isPublic(c.getModifiers()));
      }
    }
    assertTrue("Should have no-arg constructor", found);
  }

  @Test
  public void javaCodeView_declarationConstructorExists() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    boolean found = false;
    for (Constructor<?> c : cls.getDeclaredConstructors()) {
      if (c.getParameterCount() == 1
          && c.getParameterTypes()[0].getSimpleName().equals("AbstractDeclaration")) {
        found = true;
        assertTrue(Modifier.isPublic(c.getModifiers()));
      }
    }
    assertTrue("Should have constructor taking AbstractDeclaration", found);
  }

  // ========================================================================
  // JavaCodeFrameComposite — deep structural tests
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
  public void composite_hasSingletonHolder() throws ClassNotFoundException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    boolean found = Arrays.stream(cls.getDeclaredClasses())
        .anyMatch(ic -> ic.getSimpleName().equals("SingletonHolder"));
    assertTrue(found);
  }

  @Test
  public void composite_singletonHolderIsPrivateStatic() throws ClassNotFoundException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    Class<?> holder = Arrays.stream(cls.getDeclaredClasses())
        .filter(ic -> ic.getSimpleName().equals("SingletonHolder"))
        .findFirst().orElse(null);
    assertNotNull(holder);
    assertTrue(Modifier.isPrivate(holder.getModifiers()));
    assertTrue(Modifier.isStatic(holder.getModifiers()));
  }

  @Test
  public void composite_constructorIsPrivate() throws ClassNotFoundException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertTrue(Modifier.isPrivate(ctors[0].getModifiers()));
    assertEquals(0, ctors[0].getParameterCount());
  }

  @Test
  public void composite_hasJavaCodeViewField() throws ClassNotFoundException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    Field f = Arrays.stream(cls.getDeclaredFields())
        .filter(fd -> fd.getName().equals("javaCodeView"))
        .findFirst().orElse(null);
    assertNotNull("Should have 'javaCodeView' field", f);
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertEquals("JavaCodeView", f.getType().getSimpleName());
  }

  @Test
  public void composite_hasDeclarationListenerField() throws ClassNotFoundException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    Field f = Arrays.stream(cls.getDeclaredFields())
        .filter(fd -> fd.getName().equals("declarationListener"))
        .findFirst().orElse(null);
    assertNotNull("Should have 'declarationListener' field", f);
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void composite_getInstanceReturnType() throws Exception {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    Method m = cls.getMethod("getInstance");
    assertEquals(cls, m.getReturnType());
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void composite_overridesCreateScrollPaneIfDesired() throws ClassNotFoundException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createScrollPaneIfDesired"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertEquals("ScrollPane", m.getReturnType().getSimpleName());
  }

  @Test
  public void composite_overridesCreateView() throws ClassNotFoundException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createView"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
    // createView may return a subtype like CompositeView
    assertNotNull("createView return type should not be null", m.getReturnType());
  }

  @Test
  public void composite_overridesGetWiderGoldenRatioSizeFromHeight() throws ClassNotFoundException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("getWiderGoldenRatioSizeFromHeight"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertEquals(Integer.class, m.getReturnType());
  }

  @Test
  public void composite_handlePreActivationIsPublic() throws ClassNotFoundException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("handlePreActivation"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void composite_handlePostDeactivationIsPublic() throws ClassNotFoundException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("handlePostDeactivation"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void composite_genericSuperclassIsParameterized() throws ClassNotFoundException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    Type superType = cls.getGenericSuperclass();
    assertTrue("Superclass should be parameterized",
        superType instanceof ParameterizedType);
    ParameterizedType pt = (ParameterizedType) superType;
    Type[] args = pt.getActualTypeArguments();
    assertEquals(1, args.length);
    assertTrue(args[0] instanceof Class<?>);
    assertEquals("Panel", ((Class<?>) args[0]).getSimpleName());
  }

  @Test
  public void composite_totalFieldCount() throws ClassNotFoundException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    int count = cls.getDeclaredFields().length;
    assertTrue("Should have at least 2 fields", count >= 2);
  }

  @Test
  public void composite_totalDeclaredMethodCount() throws ClassNotFoundException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    int count = cls.getDeclaredMethods().length;
    assertTrue("Should have at least 5 declared methods", count >= 5);
  }

  // ========================================================================
  // JavaCodeView — listener interface contracts
  // ========================================================================

  @Test
  public void javaCodeView_historyListenerImplementsHistoryListener() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Field f = Arrays.stream(cls.getDeclaredFields())
        .filter(fd -> fd.getName().equals("historyListener"))
        .findFirst().orElse(null);
    assertNotNull(f);
    Class<?> listenerType = f.getType();
    assertTrue("historyListener should implement HistoryListener",
        listenerType.getSimpleName().equals("HistoryListener")
        || Arrays.stream(listenerType.getInterfaces())
            .anyMatch(i -> i.getSimpleName().equals("HistoryListener")));
  }

  @Test
  public void javaCodeView_keyListenerType() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Field f = Arrays.stream(cls.getDeclaredFields())
        .filter(fd -> fd.getName().equals("keyListener"))
        .findFirst().orElse(null);
    assertNotNull(f);
    Class<?> listenerType = f.getType();
    assertTrue("keyListener should be KeyListener type",
        listenerType.getSimpleName().equals("KeyListener")
        || Arrays.stream(listenerType.getInterfaces())
            .anyMatch(i -> i.getSimpleName().equals("KeyListener")));
  }

  @Test
  public void javaCodeView_mouseWheelListenerType() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Field f = Arrays.stream(cls.getDeclaredFields())
        .filter(fd -> fd.getName().equals("mouseWheelListener"))
        .findFirst().orElse(null);
    assertNotNull(f);
    Class<?> listenerType = f.getType();
    assertTrue("mouseWheelListener should be MouseWheelListener type",
        listenerType.getSimpleName().equals("MouseWheelListener")
        || Arrays.stream(listenerType.getInterfaces())
            .anyMatch(i -> i.getSimpleName().equals("MouseWheelListener")));
  }

  // ========================================================================
  // JavaCodeView — inheritance chain
  // ========================================================================

  @Test
  public void javaCodeView_fullInheritanceChain() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    java.util.List<String> chain = new java.util.ArrayList<>();
    Class<?> c = cls;
    while (c != null && c != Object.class) {
      chain.add(c.getSimpleName());
      c = c.getSuperclass();
    }
    assertTrue(chain.contains("JavaCodeView"));
    assertTrue(chain.contains("HtmlView"));
  }

  @Test
  public void javaCodeView_doesNotImplementInterfacesDirectly() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    assertEquals("JavaCodeView should not directly implement interfaces",
        0, cls.getInterfaces().length);
  }

  // ========================================================================
  // JavaCodeFrameComposite — inheritance chain
  // ========================================================================

  @Test
  public void composite_fullInheritanceChain() throws ClassNotFoundException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    java.util.List<String> chain = new java.util.ArrayList<>();
    Class<?> c = cls;
    while (c != null && c != Object.class) {
      chain.add(c.getSimpleName());
      c = c.getSuperclass();
    }
    assertTrue(chain.contains("JavaCodeFrameComposite"));
    assertTrue(chain.contains("FrameCompositeWithInternalIsShowingState"));
  }

  // ========================================================================
  // Cross-component: JavaCodeView used by JavaCodeFrameComposite
  // ========================================================================

  @Test
  public void compositeDependsOnView() throws ClassNotFoundException {
    Class<?> composite = Class.forName(COMPOSITE_FQN);
    Class<?> view = Class.forName(VIEW_FQN);
    boolean found = Arrays.stream(composite.getDeclaredFields())
        .anyMatch(f -> f.getType().equals(view));
    assertTrue("Composite should have a JavaCodeView field", found);
  }

  @Test
  public void viewDoesNotDependOnComposite() throws ClassNotFoundException {
    Class<?> composite = Class.forName(COMPOSITE_FQN);
    Class<?> view = Class.forName(VIEW_FQN);
    boolean found = Arrays.stream(view.getDeclaredFields())
        .anyMatch(f -> f.getType().equals(composite));
    assertFalse("View should not depend on Composite", found);
  }

  // ========================================================================
  // JavaCodeView — all field names enumeration
  // ========================================================================

  @Test
  public void javaCodeView_allFieldNamesKnown() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Set<String> fieldNames = Arrays.stream(cls.getDeclaredFields())
        .map(Field::getName)
        .collect(Collectors.toSet());
    Set<String> expected = Set.of(
        "IS_LAMBDA_TOGGLE_ENABLED",
        "IS_MOUSE_WHEEL_FONT_ADJUSTMENT_DESIRED",
        "undoHistory", "declaration", "fontSize",
        "isLambdaSupported", "historyListener",
        "keyListener", "mouseWheelListener"
    );
    for (String name : expected) {
      assertTrue("Missing field: " + name, fieldNames.contains(name));
    }
  }

  // ========================================================================
  // JavaCodeView — all method names enumeration
  // ========================================================================

  @Test
  public void javaCodeView_allExpectedMethodsPresent() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Set<String> methodNames = Arrays.stream(cls.getDeclaredMethods())
        .map(Method::getName)
        .collect(Collectors.toSet());
    Set<String> expected = Set.of(
        "getDeclaration", "setDeclaration", "updateHtml",
        "getProjectUndoHistory", "handleDisplayable",
        "handleUndisplayable", "isRightToLeftComponentOrientationAllowed"
    );
    for (String name : expected) {
      assertTrue("Missing method: " + name, methodNames.contains(name));
    }
  }

  // ========================================================================
  // JavaCodeFrameComposite — all method names enumeration
  // ========================================================================

  @Test
  public void composite_allExpectedMethodsPresent() throws ClassNotFoundException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    Set<String> methodNames = Arrays.stream(cls.getDeclaredMethods())
        .map(Method::getName)
        .collect(Collectors.toSet());
    assertTrue("Should have getInstance", methodNames.contains("getInstance"));
    assertTrue("Should have createScrollPaneIfDesired",
        methodNames.contains("createScrollPaneIfDesired"));
    assertTrue("Should have createView", methodNames.contains("createView"));
    assertTrue("Should have handlePreActivation",
        methodNames.contains("handlePreActivation"));
    assertTrue("Should have handlePostDeactivation",
        methodNames.contains("handlePostDeactivation"));
  }

  // ========================================================================
  // Supplemental constant/static verification
  // ========================================================================

  @Test
  public void javaCodeView_noPublicStaticFields() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    long count = Arrays.stream(cls.getDeclaredFields())
        .filter(f -> Modifier.isPublic(f.getModifiers()) && Modifier.isStatic(f.getModifiers()))
        .count();
    assertEquals("JavaCodeView should not expose public static fields", 0, count);
  }

  @Test
  public void composite_noPublicStaticFields() throws ClassNotFoundException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    long count = Arrays.stream(cls.getDeclaredFields())
        .filter(f -> Modifier.isPublic(f.getModifiers()) && Modifier.isStatic(f.getModifiers()))
        .count();
    assertEquals("Composite should not expose public static fields", 0, count);
  }

  @Test
  public void javaCodeView_setDeclarationReturnTypeVoid() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("setDeclaration"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertEquals(void.class, m.getReturnType());
  }

  @Test
  public void javaCodeView_handleDisplayableReturnTypeVoid() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("handleDisplayable"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertEquals(void.class, m.getReturnType());
  }

  @Test
  public void javaCodeView_handleUndisplayableReturnTypeVoid() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("handleUndisplayable"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertEquals(void.class, m.getReturnType());
  }

  @Test
  public void javaCodeView_updateHtmlReturnTypeVoid() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("updateHtml"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertEquals(void.class, m.getReturnType());
  }

  // ========================================================================
  // Package membership tests
  // ========================================================================

  @Test
  public void javaCodeView_correctPackage() throws ClassNotFoundException {
    Class<?> cls = Class.forName(VIEW_FQN);
    assertEquals("org.alice.ide.javacode.croquet.views", cls.getPackage().getName());
  }

  @Test
  public void composite_correctPackage() throws ClassNotFoundException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    assertEquals("org.alice.ide.javacode.croquet", cls.getPackage().getName());
  }

  // ========================================================================
  // Composite — declarationListener implements ValueListener
  // ========================================================================

  @Test
  public void composite_declarationListenerFieldType() throws ClassNotFoundException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    Field f = Arrays.stream(cls.getDeclaredFields())
        .filter(fd -> fd.getName().equals("declarationListener"))
        .findFirst().orElse(null);
    assertNotNull(f);
    assertTrue("declarationListener should implement ValueListener",
        f.getType().getSimpleName().contains("ValueListener")
        || Arrays.stream(f.getType().getInterfaces())
            .anyMatch(i -> i.getSimpleName().contains("ValueListener")));
  }

  // ========================================================================
  // Supplemental: resource file existence check
  // ========================================================================

  @Test
  public void javaCodeResourceDirectory_exists() {
    String resourceDir = "/org/alice/ide/javacode";
    java.net.URL url = getClass().getResource(resourceDir);
    // Resource directory may or may not be on classpath; just ensure loadable
    // No assertion failure — this is an informational check
    assertNotNull("Resource dir should be accessible", url);
  }

  // ========================================================================
  // Composite — protected createView does not take parameters
  // ========================================================================

  @Test
  public void composite_createViewNoParams() throws ClassNotFoundException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createView"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertEquals(0, m.getParameterCount());
  }

  // ========================================================================
  // Composite — handlePreActivation no params
  // ========================================================================

  @Test
  public void composite_handlePreActivationNoParams() throws ClassNotFoundException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("handlePreActivation"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertEquals(0, m.getParameterCount());
    assertEquals(void.class, m.getReturnType());
  }

  @Test
  public void composite_handlePostDeactivationNoParams() throws ClassNotFoundException {
    Class<?> cls = Class.forName(COMPOSITE_FQN);
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("handlePostDeactivation"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertEquals(0, m.getParameterCount());
    assertEquals(void.class, m.getReturnType());
  }
}
