package org.alice.stageide.cascade.fillerinners;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Contract tests for the SourceFillerInner hierarchy:
 * - SourceFillerInner (abstract base)
 * - AudioSourceFillerInner
 * - ImageSourceFillerInner
 *
 * All tests are headless — reflection and source-analysis only.
 */
public class FillerInnerResourceContractTest {

  private static final String PACKAGE = "org.alice.stageide.cascade.fillerinners";
  private static final String SRC_DIR = "src/main/java/org/alice/stageide/cascade/fillerinners";

  // ---- SourceFillerInner base class ----

  @Test
  public void sourceFillerInner_isAbstract() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".SourceFillerInner");
    assertTrue("SourceFillerInner must be abstract",
        Modifier.isAbstract(cls.getModifiers()));
  }

  @Test
  public void sourceFillerInner_extendsExpressionFillerInner() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".SourceFillerInner");
    Class<?> parent = Class.forName("org.alice.ide.cascade.fillerinners.ExpressionFillerInner");
    assertTrue("SourceFillerInner must extend ExpressionFillerInner",
        parent.isAssignableFrom(cls));
  }

  @Test
  public void sourceFillerInner_hasTypeParameter() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".SourceFillerInner");
    assertEquals("SourceFillerInner must have exactly 1 type parameter",
        1, cls.getTypeParameters().length);
  }

  @Test
  public void sourceFillerInner_typeParameterBoundedByResource() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".SourceFillerInner");
    Type[] bounds = cls.getTypeParameters()[0].getBounds();
    assertTrue("Type parameter must be bounded by Resource",
        bounds.length > 0);
    String boundName = bounds[0].getTypeName();
    assertTrue("Bound must reference Resource, got: " + boundName,
        boundName.contains("Resource"));
  }

  @Test
  public void sourceFillerInner_declaresGetResourceFillIn() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".SourceFillerInner");
    Method m = cls.getDeclaredMethod("getResourceFillIn", org.lgna.common.Resource.class);
    assertTrue("getResourceFillIn must be abstract",
        Modifier.isAbstract(m.getModifiers()));
    assertTrue("getResourceFillIn must be protected",
        Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void sourceFillerInner_declaresGetImportFillIn() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".SourceFillerInner");
    Method m = cls.getDeclaredMethod("getImportFillIn");
    assertTrue("getImportFillIn must be abstract",
        Modifier.isAbstract(m.getModifiers()));
    assertTrue("getImportFillIn must be protected",
        Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void sourceFillerInner_constructorTakesTwoClassParams() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".SourceFillerInner");
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    boolean hasTwoClassCtor = Arrays.stream(ctors)
        .anyMatch(c -> c.getParameterCount() == 2
            && c.getParameterTypes()[0] == Class.class
            && c.getParameterTypes()[1] == Class.class);
    assertTrue("SourceFillerInner must have (Class, Class) constructor", hasTwoClassCtor);
  }

  // ---- AudioSourceFillerInner ----

  @Test
  public void audioSourceFillerInner_extendsSourceFillerInner() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".AudioSourceFillerInner");
    Class<?> parent = Class.forName(PACKAGE + ".SourceFillerInner");
    assertTrue("AudioSourceFillerInner must extend SourceFillerInner",
        parent.isAssignableFrom(cls));
  }

  @Test
  public void audioSourceFillerInner_isConcrete() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".AudioSourceFillerInner");
    assertFalse("AudioSourceFillerInner must not be abstract",
        Modifier.isAbstract(cls.getModifiers()));
  }

  @Test
  public void audioSourceFillerInner_hasNoArgConstructor() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".AudioSourceFillerInner");
    Constructor<?> ctor = cls.getDeclaredConstructor();
    assertTrue("AudioSourceFillerInner constructor must be public",
        Modifier.isPublic(ctor.getModifiers()));
  }

  @Test
  public void audioSourceFillerInner_implementsGetResourceFillIn() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".AudioSourceFillerInner");
    Method m = cls.getDeclaredMethod("getResourceFillIn", org.lgna.common.resources.AudioResource.class);
    assertNotNull("AudioSourceFillerInner must implement getResourceFillIn", m);
    assertFalse("getResourceFillIn must not be abstract",
        Modifier.isAbstract(m.getModifiers()));
  }

  @Test
  public void audioSourceFillerInner_implementsGetImportFillIn() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".AudioSourceFillerInner");
    Method m = cls.getDeclaredMethod("getImportFillIn");
    assertNotNull("AudioSourceFillerInner must implement getImportFillIn", m);
    assertFalse("getImportFillIn must not be abstract",
        Modifier.isAbstract(m.getModifiers()));
  }

  @Test
  public void audioSourceFillerInner_overridesAppendItems() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".AudioSourceFillerInner");
    Method m = cls.getDeclaredMethod("appendItems",
        java.util.List.class,
        org.lgna.project.annotations.ValueDetails.class,
        boolean.class,
        org.lgna.project.ast.Expression.class);
    assertEquals("appendItems must be declared on AudioSourceFillerInner",
        cls, m.getDeclaringClass());
  }

  @Test
  public void audioSourceFillerInner_constructsWithoutException() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".AudioSourceFillerInner");
    Object instance = cls.getDeclaredConstructor().newInstance();
    assertNotNull(instance);
  }

  @Test
  public void audioSourceFillerInner_sourceReferencesAudioResource() throws Exception {
    Path src = resolveSourceFile(SRC_DIR + "/" + "AudioSourceFillerInner.java");
    String content = Files.readString(src);
    assertTrue("Must reference AudioResource", content.contains("AudioResource"));
    assertTrue("Must reference AudioSource", content.contains("AudioSource"));
  }

  @Test
  public void audioSourceFillerInner_sourceReferencesCustomComposite() throws Exception {
    Path src = resolveSourceFile(SRC_DIR + "/" + "AudioSourceFillerInner.java");
    String content = Files.readString(src);
    assertTrue("Must reference AudioSourceCustomExpressionCreatorComposite",
        content.contains("AudioSourceCustomExpressionCreatorComposite"));
  }

  // ---- ImageSourceFillerInner ----

  @Test
  public void imageSourceFillerInner_extendsSourceFillerInner() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".ImageSourceFillerInner");
    Class<?> parent = Class.forName(PACKAGE + ".SourceFillerInner");
    assertTrue("ImageSourceFillerInner must extend SourceFillerInner",
        parent.isAssignableFrom(cls));
  }

  @Test
  public void imageSourceFillerInner_isConcrete() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".ImageSourceFillerInner");
    assertFalse("ImageSourceFillerInner must not be abstract",
        Modifier.isAbstract(cls.getModifiers()));
  }

  @Test
  public void imageSourceFillerInner_hasNoArgConstructor() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".ImageSourceFillerInner");
    Constructor<?> ctor = cls.getDeclaredConstructor();
    assertTrue("ImageSourceFillerInner constructor must be public",
        Modifier.isPublic(ctor.getModifiers()));
  }

  @Test
  public void imageSourceFillerInner_implementsGetResourceFillIn() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".ImageSourceFillerInner");
    Method m = cls.getDeclaredMethod("getResourceFillIn", org.lgna.common.resources.ImageResource.class);
    assertNotNull("ImageSourceFillerInner must implement getResourceFillIn", m);
  }

  @Test
  public void imageSourceFillerInner_implementsGetImportFillIn() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".ImageSourceFillerInner");
    Method m = cls.getDeclaredMethod("getImportFillIn");
    assertNotNull("ImageSourceFillerInner must implement getImportFillIn", m);
  }

  @Test
  public void imageSourceFillerInner_doesNotOverrideAppendItems() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".ImageSourceFillerInner");
    try {
      Method m = cls.getDeclaredMethod("appendItems",
          java.util.List.class,
          org.lgna.project.annotations.ValueDetails.class,
          boolean.class,
          org.lgna.project.ast.Expression.class);
      fail("ImageSourceFillerInner should inherit appendItems from SourceFillerInner, not override it");
    } catch (NoSuchMethodException expected) {
      // correct — inherits from SourceFillerInner
    }
  }

  @Test
  public void imageSourceFillerInner_constructsWithoutException() throws Exception {
    Class<?> cls = Class.forName(PACKAGE + ".ImageSourceFillerInner");
    Object instance = cls.getDeclaredConstructor().newInstance();
    assertNotNull(instance);
  }

  @Test
  public void imageSourceFillerInner_sourceReferencesImageResource() throws Exception {
    Path src = resolveSourceFile(SRC_DIR + "/" + "ImageSourceFillerInner.java");
    String content = Files.readString(src);
    assertTrue("Must reference ImageResource", content.contains("ImageResource"));
    assertTrue("Must reference ImageSource", content.contains("ImageSource"));
  }

  // ---- Cross-family: both source filler-inners in same package ----

  @Test
  public void sourceFillerInnerSubclasses_areExactlyTwo() throws Exception {
    Class<?> baseClass = Class.forName(PACKAGE + ".SourceFillerInner");
    String[] expectedSubclasses = {"AudioSourceFillerInner", "ImageSourceFillerInner"};
    for (String name : expectedSubclasses) {
      Class<?> sub = Class.forName(PACKAGE + "." + name);
      assertTrue(name + " must extend SourceFillerInner", baseClass.isAssignableFrom(sub));
    }
  }

  @Test
  public void sourceFillerInner_sourceUsesResourceFromProject() throws Exception {
    Path src = resolveSourceFile(SRC_DIR + "/" + "SourceFillerInner.java");
    String content = Files.readString(src);
    assertTrue("SourceFillerInner must reference IDE.getActiveInstance()",
        content.contains("IDE.getActiveInstance()"));
    assertTrue("SourceFillerInner must reference project.getResources()",
        content.contains("getResources()"));
  }

  @Test
  public void sourceFillerInner_sourceUsesCascadeLineSeparator() throws Exception {
    Path src = resolveSourceFile(SRC_DIR + "/" + "SourceFillerInner.java");
    String content = Files.readString(src);
    assertTrue("SourceFillerInner must use CascadeLineSeparator",
        content.contains("CascadeLineSeparator"));
  }

  // ---- Guard: AudioSource has more methods than ImageSource ----

  @Test
  public void audioSource_hasMoreDeclaredMethodsThanImageSource() throws Exception {
    Class<?> audio = Class.forName(PACKAGE + ".AudioSourceFillerInner");
    Class<?> image = Class.forName(PACKAGE + ".ImageSourceFillerInner");
    int audioMethods = audio.getDeclaredMethods().length;
    int imageMethods = image.getDeclaredMethods().length;
    assertTrue("AudioSourceFillerInner should have more declared methods (overrides appendItems)",
        audioMethods > imageMethods);
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
