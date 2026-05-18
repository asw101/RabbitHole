package org.alice.stageide.cascade.fillerinners;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * Contract tests for the expression-type filler-inners:
 * - ColorFillerInner
 * - KeyFillerInner
 * - ImagePaintFillerInner
 * - ModelResourceFillerInner
 *
 * All tests are headless — reflection and source-analysis only.
 */
public class FillerInnerExpressionContractTest {

  private static final String PACKAGE = "org.alice.stageide.cascade.fillerinners";
  private static final String SRC_DIR = "src/main/java/org/alice/stageide/cascade/fillerinners";

  // ---- ColorFillerInner ----

  @Test
  public void colorFillerInner_extendsExpressionFillerInner() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".ColorFillerInner");
    Class<?> parent = Class.forName("org.alice.ide.cascade.fillerinners.ExpressionFillerInner");
    assertTrue("ColorFillerInner must extend ExpressionFillerInner",
        parent.isAssignableFrom(cls));
  }

  @Test
  public void colorFillerInner_doesNotExtendSourceFillerInner() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".ColorFillerInner");
    Class<?> sourceFI = Class.forName(PACKAGE + ".SourceFillerInner");
    assertFalse("ColorFillerInner must NOT extend SourceFillerInner",
        sourceFI.isAssignableFrom(cls));
  }

  @Test
  public void colorFillerInner_isConcrete() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".ColorFillerInner");
    assertFalse("ColorFillerInner must not be abstract",
        Modifier.isAbstract(cls.getModifiers()));
  }

  @Test
  public void colorFillerInner_hasNoArgConstructor() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".ColorFillerInner");
    Constructor<?> ctor = cls.getDeclaredConstructor();
    assertTrue("Constructor must be public", Modifier.isPublic(ctor.getModifiers()));
  }

  @Test
  public void colorFillerInner_constructsWithoutException() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".ColorFillerInner");
    Object instance = cls.getDeclaredConstructor().newInstance();
    assertNotNull(instance);
  }

  @Test
  public void colorFillerInner_sourceTargetsColorClass() throws Exception {
    Path src = resolveSourceFile(SRC_DIR + "/" + "ColorFillerInner.java");
    String content = Files.readString(src);
    assertTrue("Must call super(Color.class)", content.contains("Color.class"));
  }

  @Test
  public void colorFillerInner_sourceReferencesCustomComposite() throws Exception {
    Path src = resolveSourceFile(SRC_DIR + "/" + "ColorFillerInner.java");
    String content = Files.readString(src);
    assertTrue("Must reference ColorCustomExpressionCreatorComposite",
        content.contains("ColorCustomExpressionCreatorComposite"));
  }

  @Test
  public void colorFillerInner_sourceReferencesStaticFieldAccess() throws Exception {
    Path src = resolveSourceFile(SRC_DIR + "/" + "ColorFillerInner.java");
    String content = Files.readString(src);
    assertTrue("Must reference StaticFieldAccessFillIn for Color constants",
        content.contains("StaticFieldAccessFillIn"));
  }

  @Test
  public void colorFillerInner_sourceUsesCascadeLineSeparator() throws Exception {
    Path src = resolveSourceFile(SRC_DIR + "/" + "ColorFillerInner.java");
    String content = Files.readString(src);
    assertTrue("Must use CascadeLineSeparator between constants and custom",
        content.contains("CascadeLineSeparator"));
  }

  @Test
  public void colorFillerInner_appendItemsDeclaredLocally() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".ColorFillerInner");
    Method m = cls.getDeclaredMethod("appendItems",
        java.util.List.class,
        org.lgna.project.annotations.ValueDetails.class,
        boolean.class,
        org.lgna.project.ast.Expression.class);
    assertEquals("appendItems must be declared on ColorFillerInner",
        cls, m.getDeclaringClass());
  }

  // ---- KeyFillerInner ----

  @Test
  public void keyFillerInner_extendsExpressionFillerInner() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".KeyFillerInner");
    Class<?> parent = Class.forName("org.alice.ide.cascade.fillerinners.ExpressionFillerInner");
    assertTrue("KeyFillerInner must extend ExpressionFillerInner",
        parent.isAssignableFrom(cls));
  }

  @Test
  public void keyFillerInner_doesNotExtendSourceFillerInner() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".KeyFillerInner");
    Class<?> sourceFI = Class.forName(PACKAGE + ".SourceFillerInner");
    assertFalse("KeyFillerInner must NOT extend SourceFillerInner",
        sourceFI.isAssignableFrom(cls));
  }

  @Test
  public void keyFillerInner_isConcrete() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".KeyFillerInner");
    assertFalse("KeyFillerInner must not be abstract",
        Modifier.isAbstract(cls.getModifiers()));
  }

  @Test
  public void keyFillerInner_hasNoArgConstructor() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".KeyFillerInner");
    Constructor<?> ctor = cls.getDeclaredConstructor();
    assertTrue("Constructor must be public", Modifier.isPublic(ctor.getModifiers()));
  }

  @Test
  public void keyFillerInner_constructsWithoutException() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".KeyFillerInner");
    Object instance = cls.getDeclaredConstructor().newInstance();
    assertNotNull(instance);
  }

  @Test
  public void keyFillerInner_sourceTargetsKeyClass() throws Exception {
    Path src = resolveSourceFile(SRC_DIR + "/" + "KeyFillerInner.java");
    String content = Files.readString(src);
    assertTrue("Must call super(Key.class)", content.contains("Key.class"));
  }

  @Test
  public void keyFillerInner_sourceReferencesKeyMenus() throws Exception {
    Path src = resolveSourceFile(SRC_DIR + "/" + "KeyFillerInner.java");
    String content = Files.readString(src);
    assertTrue("Must reference LettersKeyCascadeMenu", content.contains("LettersKeyCascadeMenu"));
    assertTrue("Must reference DigitsKeyCascadeMenu", content.contains("DigitsKeyCascadeMenu"));
    assertTrue("Must reference ArrowsKeyCascadeMenu", content.contains("ArrowsKeyCascadeMenu"));
  }

  @Test
  public void keyFillerInner_sourceReferencesCustomComposite() throws Exception {
    Path src = resolveSourceFile(SRC_DIR + "/" + "KeyFillerInner.java");
    String content = Files.readString(src);
    assertTrue("Must reference KeyCustomExpressionCreatorComposite",
        content.contains("KeyCustomExpressionCreatorComposite"));
  }

  @Test
  public void keyFillerInner_appendItemsDeclaredLocally() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".KeyFillerInner");
    Method m = cls.getDeclaredMethod("appendItems",
        java.util.List.class,
        org.lgna.project.annotations.ValueDetails.class,
        boolean.class,
        org.lgna.project.ast.Expression.class);
    assertEquals("appendItems must be declared on KeyFillerInner",
        cls, m.getDeclaringClass());
  }

  // ---- ImagePaintFillerInner ----

  @Test
  public void imagePaintFillerInner_extendsExpressionFillerInner() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".ImagePaintFillerInner");
    Class<?> parent = Class.forName("org.alice.ide.cascade.fillerinners.ExpressionFillerInner");
    assertTrue("ImagePaintFillerInner must extend ExpressionFillerInner",
        parent.isAssignableFrom(cls));
  }

  @Test
  public void imagePaintFillerInner_isConcrete() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".ImagePaintFillerInner");
    assertFalse("ImagePaintFillerInner must not be abstract",
        Modifier.isAbstract(cls.getModifiers()));
  }

  @Test
  public void imagePaintFillerInner_hasNoArgConstructor() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".ImagePaintFillerInner");
    Constructor<?> ctor = cls.getDeclaredConstructor();
    assertTrue("Constructor must be public", Modifier.isPublic(ctor.getModifiers()));
  }

  @Test
  public void imagePaintFillerInner_constructsWithoutException() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".ImagePaintFillerInner");
    Object instance = cls.getDeclaredConstructor().newInstance();
    assertNotNull(instance);
  }

  @Test
  public void imagePaintFillerInner_sourceTargetsImagePaintClass() throws Exception {
    Path src = resolveSourceFile(SRC_DIR + "/" + "ImagePaintFillerInner.java");
    String content = Files.readString(src);
    assertTrue("Must call super(ImagePaint.class)", content.contains("ImagePaint.class"));
  }

  @Test
  public void imagePaintFillerInner_sourceUsesPublicStaticFieldValueDetails() throws Exception {
    Path src = resolveSourceFile(SRC_DIR + "/" + "ImagePaintFillerInner.java");
    String content = Files.readString(src);
    assertTrue("Must use PublicStaticFieldValueDetails pattern matching",
        content.contains("PublicStaticFieldValueDetails"));
  }

  @Test
  public void imagePaintFillerInner_appendItemsDeclaredLocally() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".ImagePaintFillerInner");
    Method m = cls.getDeclaredMethod("appendItems",
        java.util.List.class,
        org.lgna.project.annotations.ValueDetails.class,
        boolean.class,
        org.lgna.project.ast.Expression.class);
    assertEquals("appendItems must be declared on ImagePaintFillerInner",
        cls, m.getDeclaringClass());
  }

  // ---- ModelResourceFillerInner ----

  @Test
  public void modelResourceFillerInner_extendsExpressionFillerInner() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".ModelResourceFillerInner");
    Class<?> parent = Class.forName("org.alice.ide.cascade.fillerinners.ExpressionFillerInner");
    assertTrue("ModelResourceFillerInner must extend ExpressionFillerInner",
        parent.isAssignableFrom(cls));
  }

  @Test
  public void modelResourceFillerInner_isConcrete() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".ModelResourceFillerInner");
    assertFalse("ModelResourceFillerInner must not be abstract",
        Modifier.isAbstract(cls.getModifiers()));
  }

  @Test
  public void modelResourceFillerInner_hasNoArgConstructor() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".ModelResourceFillerInner");
    Constructor<?> ctor = cls.getDeclaredConstructor();
    assertTrue("Constructor must be public", Modifier.isPublic(ctor.getModifiers()));
  }

  @Test
  public void modelResourceFillerInner_constructsWithoutException() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".ModelResourceFillerInner");
    Object instance = cls.getDeclaredConstructor().newInstance();
    assertNotNull(instance);
  }

  @Test
  public void modelResourceFillerInner_sourceTargetsJointedModelResource() throws Exception {
    Path src = resolveSourceFile(SRC_DIR + "/" + "ModelResourceFillerInner.java");
    String content = Files.readString(src);
    assertTrue("Must reference JointedModelResource",
        content.contains("JointedModelResource"));
  }

  @Test
  public void modelResourceFillerInner_sourceUsesPublicStaticFieldValueDetails() throws Exception {
    Path src = resolveSourceFile(SRC_DIR + "/" + "ModelResourceFillerInner.java");
    String content = Files.readString(src);
    assertTrue("Must use PublicStaticFieldValueDetails pattern matching",
        content.contains("PublicStaticFieldValueDetails"));
  }

  // ---- Cross-family: ImagePaint and ModelResource share pattern ----

  @Test
  public void imagePaintAndModelResource_shareStaticFieldAccessPattern() throws Exception {
    Path ipSrc = resolveSourceFile(SRC_DIR + "/" + "ImagePaintFillerInner.java");
    Path mrSrc = resolveSourceFile(SRC_DIR + "/" + "ModelResourceFillerInner.java");
    String ipContent = Files.readString(ipSrc);
    String mrContent = Files.readString(mrSrc);
    assertTrue("Both must use StaticFieldAccessFillIn",
        ipContent.contains("StaticFieldAccessFillIn") && mrContent.contains("StaticFieldAccessFillIn"));
    assertTrue("Both must use PublicStaticFieldValueDetails",
        ipContent.contains("PublicStaticFieldValueDetails") && mrContent.contains("PublicStaticFieldValueDetails"));
  }

  // ---- All four expression filler-inners are not SourceFillerInner subclasses ----

  @Test
  public void colorAndKeyFillerInners_notSourceFillerInnerSubclasses() throws Exception {
    Class<?> sourceFI = Class.forName(PACKAGE + ".SourceFillerInner");
    Class<?> color = Class.forName(PACKAGE + ".ColorFillerInner");
    Class<?> key = Class.forName(PACKAGE + ".KeyFillerInner");
    assertFalse(sourceFI.isAssignableFrom(color));
    assertFalse(sourceFI.isAssignableFrom(key));
  }

  @Test
  public void imagePaintAndModelResource_notSourceFillerInnerSubclasses() throws Exception {
    Class<?> sourceFI = Class.forName(PACKAGE + ".SourceFillerInner");
    Class<?> ip = Class.forName(PACKAGE + ".ImagePaintFillerInner");
    Class<?> mr = Class.forName(PACKAGE + ".ModelResourceFillerInner");
    assertFalse(sourceFI.isAssignableFrom(ip));
    assertFalse(sourceFI.isAssignableFrom(mr));
  }

  // ---- All four source files exist ----

  @Test
  public void allExpressionFillerInner_sourceFilesExist() {
    String[] names = {"ColorFillerInner", "KeyFillerInner", "ImagePaintFillerInner", "ModelResourceFillerInner"};
    for (String name : names) {
      Path src = resolveSourceFile(SRC_DIR + "/" + name + ".java");
      assertTrue(name + ".java must exist", Files.exists(src));
    }
  }

  // ---- All four classes have no static fields ----

  @Test
  public void allExpressionFillerInners_haveNoStaticFields() throws Exception {
    String[] names = {"ColorFillerInner", "KeyFillerInner", "ImagePaintFillerInner", "ModelResourceFillerInner"};
    for (String name : names) {
      Class<?> cls = Class.forName(PACKAGE + "." + name);
      long staticCount = java.util.Arrays.stream(cls.getDeclaredFields())
          .filter(f -> Modifier.isStatic(f.getModifiers()))
          .count();
      assertEquals(name + " should have no static fields", 0, staticCount);
    }
  }

  // ---- Color and Key reference custom composites, ImagePaint and ModelResource do not ----

  @Test
  public void imagePaintFillerInner_doesNotReferenceCustomComposite() throws Exception {
    Path src = resolveSourceFile(SRC_DIR + "/" + "ImagePaintFillerInner.java");
    String content = Files.readString(src);
    assertFalse("ImagePaintFillerInner should not reference CustomExpressionCreatorComposite",
        content.contains("CustomExpressionCreatorComposite"));
  }

  @Test
  public void modelResourceFillerInner_doesNotReferenceCustomComposite() throws Exception {
    Path src = resolveSourceFile(SRC_DIR + "/" + "ModelResourceFillerInner.java");
    String content = Files.readString(src);
    assertFalse("ModelResourceFillerInner should not reference CustomExpressionCreatorComposite",
        content.contains("CustomExpressionCreatorComposite"));
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
