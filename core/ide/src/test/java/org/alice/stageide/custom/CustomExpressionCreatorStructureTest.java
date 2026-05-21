package org.alice.stageide.custom;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Structural tests for custom expression creator composites.
 */
public class CustomExpressionCreatorStructureTest {

  // ---- ColorCustomExpressionCreatorComposite ----

  @Test
  public void colorCustomExpressionCreatorComposite_classIsAccessible() {
    assertNotNull(ColorCustomExpressionCreatorComposite.class);
  }

  @Test
  public void colorCustomExpressionCreatorComposite_isPublic() {
    assertTrue(Modifier.isPublic(ColorCustomExpressionCreatorComposite.class.getModifiers()));
  }

  @Test
  public void colorCustomExpressionCreatorComposite_isNotAbstract() {
    assertFalse(Modifier.isAbstract(ColorCustomExpressionCreatorComposite.class.getModifiers()));
  }

  // ---- KeyCustomExpressionCreatorComposite ----

  @Test
  public void keyCustomExpressionCreatorComposite_classIsAccessible() {
    assertNotNull(KeyCustomExpressionCreatorComposite.class);
  }

  @Test
  public void keyCustomExpressionCreatorComposite_isPublic() {
    assertTrue(Modifier.isPublic(KeyCustomExpressionCreatorComposite.class.getModifiers()));
  }

  @Test
  public void keyCustomExpressionCreatorComposite_isNotAbstract() {
    assertFalse(Modifier.isAbstract(KeyCustomExpressionCreatorComposite.class.getModifiers()));
  }

  // ---- VolumeLevelCustomExpressionCreatorComposite ----

  @Test
  public void volumeLevelCustomExpressionCreatorComposite_classIsAccessible() {
    assertNotNull(VolumeLevelCustomExpressionCreatorComposite.class);
  }

  @Test
  public void volumeLevelCustomExpressionCreatorComposite_isPublic() {
    assertTrue(Modifier.isPublic(VolumeLevelCustomExpressionCreatorComposite.class.getModifiers()));
  }

  @Test
  public void volumeLevelCustomExpressionCreatorComposite_isNotAbstract() {
    assertFalse(Modifier.isAbstract(VolumeLevelCustomExpressionCreatorComposite.class.getModifiers()));
  }

  // ---- AudioSourceCustomExpressionCreatorComposite ----

  @Test
  public void audioSourceCustomExpressionCreatorComposite_classIsAccessible() {
    assertNotNull(AudioSourceCustomExpressionCreatorComposite.class);
  }

  @Test
  public void audioSourceCustomExpressionCreatorComposite_isPublic() {
    assertTrue(Modifier.isPublic(AudioSourceCustomExpressionCreatorComposite.class.getModifiers()));
  }

  @Test
  public void audioSourceCustomExpressionCreatorComposite_isNotAbstract() {
    assertFalse(Modifier.isAbstract(AudioSourceCustomExpressionCreatorComposite.class.getModifiers()));
  }

  // ---- All custom expression composites are public ----

  @Test
  public void allCustomExpressionComposites_arePublic() {
    Class<?>[] classes = {
      ColorCustomExpressionCreatorComposite.class,
      KeyCustomExpressionCreatorComposite.class,
      VolumeLevelCustomExpressionCreatorComposite.class,
      AudioSourceCustomExpressionCreatorComposite.class
    };
    for (Class<?> cls : classes) {
      assertTrue(cls.getSimpleName() + " should be public",
        Modifier.isPublic(cls.getModifiers()));
    }
  }

  // ---- All custom expression composites are concrete ----

  @Test
  public void allCustomExpressionComposites_areConcrete() {
    Class<?>[] classes = {
      ColorCustomExpressionCreatorComposite.class,
      KeyCustomExpressionCreatorComposite.class,
      VolumeLevelCustomExpressionCreatorComposite.class,
      AudioSourceCustomExpressionCreatorComposite.class
    };
    for (Class<?> cls : classes) {
      assertFalse(cls.getSimpleName() + " should be concrete",
        Modifier.isAbstract(cls.getModifiers()));
    }
  }

  // ---- All classes in expected package ----

  @Test
  public void allClasses_inExpectedPackage() {
    String pkg = "org.alice.stageide.custom";
    assertEquals(pkg, ColorCustomExpressionCreatorComposite.class.getPackage().getName());
    assertEquals(pkg, KeyCustomExpressionCreatorComposite.class.getPackage().getName());
    assertEquals(pkg, VolumeLevelCustomExpressionCreatorComposite.class.getPackage().getName());
    assertEquals(pkg, AudioSourceCustomExpressionCreatorComposite.class.getPackage().getName());
  }
}
