package org.alice.stageide.custom;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Contract tests for KeyCustomExpressionCreatorComposite and KeyState.
 *
 * All tests are headless — reflection and source-analysis only.
 * These classes use singleton patterns and AWT components that prevent
 * direct instantiation in a headless CI environment.
 */
public class KeyCustomExpressionCreatorContractTest {

  private static final String COMPOSITE_CLASS =
      "org.alice.stageide.custom.KeyCustomExpressionCreatorComposite";
  private static final String STATE_CLASS =
      "org.alice.stageide.custom.KeyState";
  private static final String BASE_CLASS =
      "org.alice.ide.custom.CustomExpressionCreatorComposite";
  private static final String COMPOSITE_SRC =
      "src/main/java/org/alice/stageide/custom/KeyCustomExpressionCreatorComposite.java";
  private static final String STATE_SRC =
      "src/main/java/org/alice/stageide/custom/KeyState.java";
  private static final String VIEW_SRC =
      "src/main/java/org/alice/stageide/custom/components/KeyCustomExpressionCreatorView.java";

  // ---- KeyCustomExpressionCreatorComposite hierarchy ----

  @Test
  public void composite_extendsCustomExpressionCreatorComposite() throws Exception {
    Class<?> cls = Class.forName(COMPOSITE_CLASS);
    Class<?> parent = Class.forName(BASE_CLASS);
    assertTrue("Must extend CustomExpressionCreatorComposite",
        parent.isAssignableFrom(cls));
  }

  @Test
  public void composite_isPublic() throws Exception {
    Class<?> cls = Class.forName(COMPOSITE_CLASS);
    assertTrue("Must be public", Modifier.isPublic(cls.getModifiers()));
  }

  @Test
  public void composite_isConcrete() throws Exception {
    Class<?> cls = Class.forName(COMPOSITE_CLASS);
    assertFalse("Must not be abstract", Modifier.isAbstract(cls.getModifiers()));
  }

  // ---- Singleton pattern ----

  @Test
  public void composite_hasGetInstanceMethod() throws Exception {
    Class<?> cls = Class.forName(COMPOSITE_CLASS);
    Method m = cls.getMethod("getInstance");
    assertTrue("getInstance must be public", Modifier.isPublic(m.getModifiers()));
    assertTrue("getInstance must be static", Modifier.isStatic(m.getModifiers()));
    assertEquals("getInstance must return the composite type", cls, m.getReturnType());
  }

  @Test
  public void composite_constructorIsPrivate() throws Exception {
    Class<?> cls = Class.forName(COMPOSITE_CLASS);
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    for (Constructor<?> ctor : ctors) {
      assertTrue("All constructors must be private",
          Modifier.isPrivate(ctor.getModifiers()));
    }
  }

  @Test
  public void composite_hasSingletonHolderInnerClass() throws Exception {
    Class<?> cls = Class.forName(COMPOSITE_CLASS);
    Class<?>[] innerClasses = cls.getDeclaredClasses();
    boolean hasSingletonHolder = Arrays.stream(innerClasses)
        .anyMatch(c -> c.getSimpleName().equals("SingletonHolder"));
    assertTrue("Must have SingletonHolder inner class", hasSingletonHolder);
  }

  // ---- Key methods ----

  @Test
  public void composite_hasGetValueStateMethod() throws Exception {
    Class<?> cls = Class.forName(COMPOSITE_CLASS);
    Method m = cls.getMethod("getValueState");
    assertNotNull("Must have getValueState method", m);
    assertEquals("getValueState must return KeyState",
        Class.forName(STATE_CLASS), m.getReturnType());
  }

  @Test
  public void composite_hasGetPressAnyKeyLabelMethod() throws Exception {
    Class<?> cls = Class.forName(COMPOSITE_CLASS);
    Method m = cls.getMethod("getPressAnyKeyLabel");
    assertNotNull("Must have getPressAnyKeyLabel", m);
  }

  @Test
  public void composite_overridesCreateValue() throws Exception {
    Class<?> cls = Class.forName(COMPOSITE_CLASS);
    Method m = cls.getDeclaredMethod("createValue");
    assertNotNull("Must override createValue", m);
    assertTrue("createValue must be protected",
        Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void composite_overridesGetStatusPreRejectorCheck() throws Exception {
    Class<?> cls = Class.forName(COMPOSITE_CLASS);
    Method m = cls.getDeclaredMethod("getStatusPreRejectorCheck");
    assertNotNull("Must override getStatusPreRejectorCheck", m);
  }

  @Test
  public void composite_overridesCreateView() throws Exception {
    Class<?> cls = Class.forName(COMPOSITE_CLASS);
    Method m = cls.getDeclaredMethod("createView");
    assertNotNull("Must override createView", m);
    assertTrue("createView must be protected",
        Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void composite_overridesInitializeToPreviousExpression() throws Exception {
    Class<?> cls = Class.forName(COMPOSITE_CLASS);
    Method m = cls.getDeclaredMethod("initializeToPreviousExpression",
        org.lgna.project.ast.Expression.class);
    assertNotNull("Must override initializeToPreviousExpression", m);
  }

  @Test
  public void composite_overridesIsDefaultButtonDesired() throws Exception {
    Class<?> cls = Class.forName(COMPOSITE_CLASS);
    Method m = cls.getDeclaredMethod("isDefaultButtonDesired");
    assertNotNull("Must override isDefaultButtonDesired", m);
  }

  // ---- Source analysis ----

  @Test
  public void compositeSource_referencesKeyClass() throws Exception {
    String content = Files.readString(resolveSourceFile(COMPOSITE_SRC));
    assertTrue("Must reference org.lgna.story.Key",
        content.contains("org.lgna.story.Key"));
  }

  @Test
  public void compositeSource_createsFieldAccessExpression() throws Exception {
    String content = Files.readString(resolveSourceFile(COMPOSITE_SRC));
    assertTrue("Must create FieldAccess", content.contains("new FieldAccess"));
    assertTrue("Must create TypeExpression", content.contains("new TypeExpression"));
  }

  @Test
  public void compositeSource_hasUUID() throws Exception {
    String content = Files.readString(resolveSourceFile(COMPOSITE_SRC));
    assertTrue("Must contain UUID", content.contains("UUID.fromString"));
  }

  @Test
  public void compositeSource_hasErrorStatus() throws Exception {
    String content = Files.readString(resolveSourceFile(COMPOSITE_SRC));
    assertTrue("Must create keyRequiredError status",
        content.contains("keyRequiredError"));
  }

  // ---- KeyState class ----

  @Test
  public void keyState_extendsSimpleItemState() throws Exception {
    Class<?> cls = Class.forName(STATE_CLASS);
    Class<?> parent = cls.getSuperclass();
    assertEquals("KeyState must extend SimpleItemState",
        "SimpleItemState", parent.getSimpleName());
  }

  @Test
  public void keyState_isFinal() throws Exception {
    Class<?> cls = Class.forName(STATE_CLASS);
    assertTrue("KeyState must be final", Modifier.isFinal(cls.getModifiers()));
  }

  @Test
  public void keyState_hasGetInstanceMethod() throws Exception {
    Class<?> cls = Class.forName(STATE_CLASS);
    Method m = cls.getMethod("getInstance");
    assertTrue("getInstance must be public", Modifier.isPublic(m.getModifiers()));
    assertTrue("getInstance must be static", Modifier.isStatic(m.getModifiers()));
    assertEquals("getInstance must return KeyState", cls, m.getReturnType());
  }

  @Test
  public void keyState_constructorIsPrivate() throws Exception {
    Class<?> cls = Class.forName(STATE_CLASS);
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    for (Constructor<?> ctor : ctors) {
      assertTrue("KeyState constructors must be private",
          Modifier.isPrivate(ctor.getModifiers()));
    }
  }

  @Test
  public void keyState_hasHandleKeyPressedMethod() throws Exception {
    Class<?> cls = Class.forName(STATE_CLASS);
    Class<?> viewCtrlClass = Class.forName(
        "org.alice.stageide.custom.components.KeyViewController");
    Method m = cls.getMethod("handleKeyPressed",
        viewCtrlClass,
        java.awt.event.KeyEvent.class);
    assertNotNull("Must have handleKeyPressed", m);
    assertTrue("handleKeyPressed must be public",
        Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void keyState_hasCreateViewControllerMethod() throws Exception {
    Class<?> cls = Class.forName(STATE_CLASS);
    Method m = cls.getMethod("createViewController");
    assertNotNull("Must have createViewController", m);
  }

  @Test
  public void keyState_overridesGetSwingValue() throws Exception {
    Class<?> cls = Class.forName(STATE_CLASS);
    Method m = cls.getDeclaredMethod("getSwingValue");
    assertNotNull("Must override getSwingValue", m);
  }

  @Test
  public void keyState_overridesSetSwingValue() throws Exception {
    Class<?> cls = Class.forName(STATE_CLASS);
    // setSwingValue takes the Key type parameter
    Method m = cls.getDeclaredMethod("setSwingValue", org.lgna.story.Key.class);
    assertNotNull("Must override setSwingValue", m);
  }

  // ---- KeyState source analysis ----

  @Test
  public void keyStateSource_referencesKeyCodec() throws Exception {
    String content = Files.readString(resolveSourceFile(STATE_SRC));
    assertTrue("Must reference KeyCodec", content.contains("KeyCodec"));
  }

  @Test
  public void keyStateSource_referencesKeyGetInstanceFromKeyCode() throws Exception {
    String content = Files.readString(resolveSourceFile(STATE_SRC));
    assertTrue("Must call Key.getInstanceFromKeyCode",
        content.contains("Key.getInstanceFromKeyCode"));
  }

  @Test
  public void keyStateSource_hasUUID() throws Exception {
    String content = Files.readString(resolveSourceFile(STATE_SRC));
    assertTrue("Must contain UUID", content.contains("UUID.fromString"));
  }

  @Test
  public void keyStateSource_usesComponentManager() throws Exception {
    String content = Files.readString(resolveSourceFile(STATE_SRC));
    assertTrue("Must use ComponentManager",
        content.contains("ComponentManager.getComponents"));
  }

  // ---- View class exists ----

  @Test
  public void keyCustomExpressionCreatorView_exists() {
    Path viewPath = resolveSourceFile(VIEW_SRC);
    assertTrue("KeyCustomExpressionCreatorView.java must exist",
        Files.exists(viewPath));
  }

  @Test
  public void keyCustomExpressionCreatorView_isLoadable() throws Exception {
    Class<?> cls = Class.forName(
        "org.alice.stageide.custom.components.KeyCustomExpressionCreatorView");
    assertNotNull("Must be loadable", cls);
  }

  // ---- Field counts ----

  @Test
  public void composite_hasPressAnyKeyLabelField() throws Exception {
    Class<?> cls = Class.forName(COMPOSITE_CLASS);
    Field f = cls.getDeclaredField("pressAnyKeyLabel");
    assertNotNull("Must have pressAnyKeyLabel field", f);
    assertTrue("pressAnyKeyLabel must be private",
        Modifier.isPrivate(f.getModifiers()));
    assertTrue("pressAnyKeyLabel must be final",
        Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void composite_hasKeyRequiredErrorField() throws Exception {
    Class<?> cls = Class.forName(COMPOSITE_CLASS);
    Field f = cls.getDeclaredField("keyRequiredError");
    assertNotNull("Must have keyRequiredError field", f);
    assertTrue("keyRequiredError must be private",
        Modifier.isPrivate(f.getModifiers()));
    assertTrue("keyRequiredError must be final",
        Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void keyState_hasValueField() throws Exception {
    Class<?> cls = Class.forName(STATE_CLASS);
    Field f = cls.getDeclaredField("value");
    assertNotNull("Must have value field", f);
    assertTrue("value must be private",
        Modifier.isPrivate(f.getModifiers()));
  }

  private static Path resolveSourceFile(String relativePath) {
    Path cwd = Paths.get(System.getProperty("user.dir"));
    Path candidate = cwd.resolve(relativePath);
    if (Files.exists(candidate)) {
      return candidate;
    }
    Path dir = cwd;
    while (dir != null) {
      candidate = dir.resolve(relativePath);
      if (Files.exists(candidate)) {
        return candidate;
      }
      dir = dir.getParent();
    }
    fail("Cannot find source file: " + relativePath + " from " + cwd);
    return null;
  }
}
