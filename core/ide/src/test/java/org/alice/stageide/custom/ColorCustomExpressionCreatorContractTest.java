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
 * Contract tests for ColorCustomExpressionCreatorComposite.
 *
 * All tests are headless — reflection and source-analysis only.
 * The composite uses JColorChooser and singleton pattern, preventing
 * direct instantiation in headless CI.
 */
public class ColorCustomExpressionCreatorContractTest {

  private static final String COMPOSITE_CLASS =
      "org.alice.stageide.custom.ColorCustomExpressionCreatorComposite";
  private static final String BASE_CLASS =
      "org.alice.ide.custom.CustomExpressionCreatorComposite";
  private static final String COMPOSITE_SRC =
      "src/main/java/org/alice/stageide/custom/ColorCustomExpressionCreatorComposite.java";

  // ---- Hierarchy ----

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
    assertEquals("getInstance must return ColorCustomExpressionCreatorComposite",
        cls, m.getReturnType());
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
  public void composite_overridesHandlePreShowDialog() throws Exception {
    Class<?> cls = Class.forName(COMPOSITE_CLASS);
    Method m = cls.getDeclaredMethod("handlePreShowDialog",
        org.lgna.croquet.views.Dialog.class);
    assertNotNull("Must override handlePreShowDialog", m);
  }

  @Test
  public void composite_overridesHandlePostHideDialog() throws Exception {
    Class<?> cls = Class.forName(COMPOSITE_CLASS);
    Method m = cls.getDeclaredMethod("handlePostHideDialog");
    assertNotNull("Must override handlePostHideDialog", m);
  }

  // ---- JColorChooser field ----

  @Test
  public void composite_hasJColorChooserField() throws Exception {
    Class<?> cls = Class.forName(COMPOSITE_CLASS);
    Field f = cls.getDeclaredField("jColorChooser");
    assertNotNull("Must have jColorChooser field", f);
    assertTrue("jColorChooser must be private",
        Modifier.isPrivate(f.getModifiers()));
    assertTrue("jColorChooser must be final",
        Modifier.isFinal(f.getModifiers()));
    assertEquals("jColorChooser must be JColorChooser type",
        javax.swing.JColorChooser.class, f.getType());
  }

  @Test
  public void composite_hasChangeListenerField() throws Exception {
    Class<?> cls = Class.forName(COMPOSITE_CLASS);
    Field f = cls.getDeclaredField("changeListener");
    assertNotNull("Must have changeListener field", f);
    assertTrue("changeListener must be private",
        Modifier.isPrivate(f.getModifiers()));
    assertTrue("changeListener must be final",
        Modifier.isFinal(f.getModifiers()));
  }

  // ---- Source analysis ----

  @Test
  public void compositeSource_referencesJColorChooser() throws Exception {
    String content = Files.readString(resolveSourceFile(COMPOSITE_SRC));
    assertTrue("Must reference JColorChooser",
        content.contains("JColorChooser"));
    assertTrue("Must import JColorChooser",
        content.contains("javax.swing.JColorChooser"));
  }

  @Test
  public void compositeSource_referencesColorClass() throws Exception {
    String content = Files.readString(resolveSourceFile(COMPOSITE_SRC));
    assertTrue("Must reference org.lgna.story.Color",
        content.contains("org.lgna.story.Color"));
  }

  @Test
  public void compositeSource_usesExpressionCreator() throws Exception {
    String content = Files.readString(resolveSourceFile(COMPOSITE_SRC));
    assertTrue("Must use ExpressionCreator",
        content.contains("ExpressionCreator"));
    assertTrue("Must call expressionCreator.createExpression",
        content.contains("createExpression"));
  }

  @Test
  public void compositeSource_createsColorFromAwtColor() throws Exception {
    String content = Files.readString(resolveSourceFile(COMPOSITE_SRC));
    assertTrue("Must call jColorChooser.getColor()",
        content.contains("jColorChooser.getColor()"));
    assertTrue("Must call Color.createInstance",
        content.contains("Color.createInstance"));
  }

  @Test
  public void compositeSource_handlesCannotCreateExpression() throws Exception {
    String content = Files.readString(resolveSourceFile(COMPOSITE_SRC));
    assertTrue("Must catch CannotCreateExpressionException",
        content.contains("CannotCreateExpressionException"));
  }

  @Test
  public void compositeSource_hasUUID() throws Exception {
    String content = Files.readString(resolveSourceFile(COMPOSITE_SRC));
    assertTrue("Must contain UUID", content.contains("UUID.fromString"));
  }

  @Test
  public void compositeSource_registersAndUnregistersChangeListener() throws Exception {
    String content = Files.readString(resolveSourceFile(COMPOSITE_SRC));
    assertTrue("Must add change listener",
        content.contains("addChangeListener"));
    assertTrue("Must remove change listener",
        content.contains("removeChangeListener"));
  }

  @Test
  public void compositeSource_usesSwingAdapter() throws Exception {
    String content = Files.readString(resolveSourceFile(COMPOSITE_SRC));
    assertTrue("Must use SwingAdapter to wrap JColorChooser",
        content.contains("SwingAdapter"));
  }

  @Test
  public void compositeSource_usesColor4f() throws Exception {
    String content = Files.readString(resolveSourceFile(COMPOSITE_SRC));
    assertTrue("Must use Color4f for conversion",
        content.contains("Color4f"));
    assertTrue("Must call toColor4f()",
        content.contains("toColor4f()"));
  }

  @Test
  public void compositeSource_usesColorUtilities() throws Exception {
    String content = Files.readString(resolveSourceFile(COMPOSITE_SRC));
    assertTrue("Must use ColorUtilities.toAwtColor",
        content.contains("ColorUtilities.toAwtColor"));
  }

  // ---- Inner view class ----

  @Test
  public void compositeSource_hasInnerViewClass() throws Exception {
    String content = Files.readString(resolveSourceFile(COMPOSITE_SRC));
    assertTrue("Must define ColorCustomExpressionCreatorView inner class",
        content.contains("class ColorCustomExpressionCreatorView"));
  }

  @Test
  public void compositeSource_innerViewOverridesCreateMainComponent() throws Exception {
    String content = Files.readString(resolveSourceFile(COMPOSITE_SRC));
    assertTrue("Inner view must override createMainComponent",
        content.contains("createMainComponent"));
  }

  // ---- Method count ----

  @Test
  public void composite_declaredMethodCount() throws Exception {
    Class<?> cls = Class.forName(COMPOSITE_CLASS);
    Method[] methods = cls.getDeclaredMethods();
    assertTrue("Must have at least 6 declared methods (createValue, createView, " +
            "getStatusPreRejectorCheck, initializeToPreviousExpression, handlePreShowDialog, handlePostHideDialog)",
        methods.length >= 6);
  }

  // ---- Consistent with KeyCustomExpressionCreatorComposite pattern ----

  @Test
  public void colorAndKeyComposites_shareSameBaseClass() throws Exception {
    Class<?> color = Class.forName(COMPOSITE_CLASS);
    Class<?> key = Class.forName("org.alice.stageide.custom.KeyCustomExpressionCreatorComposite");
    assertEquals("Both must extend the same base class",
        color.getSuperclass(), key.getSuperclass());
  }

  @Test
  public void colorAndKeyComposites_bothHaveGetInstance() throws Exception {
    Class<?> color = Class.forName(COMPOSITE_CLASS);
    Class<?> key = Class.forName("org.alice.stageide.custom.KeyCustomExpressionCreatorComposite");
    assertNotNull(color.getMethod("getInstance"));
    assertNotNull(key.getMethod("getInstance"));
  }

  @Test
  public void colorAndKeyComposites_bothHavePrivateConstructors() throws Exception {
    Class<?> color = Class.forName(COMPOSITE_CLASS);
    Class<?> key = Class.forName("org.alice.stageide.custom.KeyCustomExpressionCreatorComposite");
    for (Constructor<?> c : color.getDeclaredConstructors()) {
      assertTrue(Modifier.isPrivate(c.getModifiers()));
    }
    for (Constructor<?> c : key.getDeclaredConstructors()) {
      assertTrue(Modifier.isPrivate(c.getModifiers()));
    }
  }

  // ---- StatusPreRejectorCheck always returns good ----

  @Test
  public void compositeSource_statusIsAlwaysGoodToGo() throws Exception {
    String content = Files.readString(resolveSourceFile(COMPOSITE_SRC));
    assertTrue("getStatusPreRejectorCheck must return IS_GOOD_TO_GO_STATUS",
        content.contains("IS_GOOD_TO_GO_STATUS"));
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
