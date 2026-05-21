package org.alice.stageide.ast.source;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Structural tests for source import value creator classes.
 */
public class SourceImportValueCreatorStructureTest {

  // ---- SourceImportValueCreator ----

  @Test
  public void sourceImportValueCreator_classIsAccessible() {
    assertNotNull(SourceImportValueCreator.class);
  }

  @Test
  public void sourceImportValueCreator_isPublic() {
    assertTrue(Modifier.isPublic(SourceImportValueCreator.class.getModifiers()));
  }

  @Test
  public void sourceImportValueCreator_isAbstract() {
    assertTrue(Modifier.isAbstract(SourceImportValueCreator.class.getModifiers()));
  }

  // ---- ImageSourceImportValueCreator ----

  @Test
  public void imageSourceImportValueCreator_classIsAccessible() {
    assertNotNull(ImageSourceImportValueCreator.class);
  }

  @Test
  public void imageSourceImportValueCreator_isPublic() {
    assertTrue(Modifier.isPublic(ImageSourceImportValueCreator.class.getModifiers()));
  }

  @Test
  public void imageSourceImportValueCreator_isNotAbstract() {
    assertFalse(Modifier.isAbstract(ImageSourceImportValueCreator.class.getModifiers()));
  }

  @Test
  public void imageSourceImportValueCreator_extendsSourceImportValueCreator() {
    assertTrue(SourceImportValueCreator.class.isAssignableFrom(ImageSourceImportValueCreator.class));
  }

  // ---- AudioSourceImportValueCreator ----

  @Test
  public void audioSourceImportValueCreator_classIsAccessible() {
    assertNotNull(AudioSourceImportValueCreator.class);
  }

  @Test
  public void audioSourceImportValueCreator_isPublic() {
    assertTrue(Modifier.isPublic(AudioSourceImportValueCreator.class.getModifiers()));
  }

  @Test
  public void audioSourceImportValueCreator_isNotAbstract() {
    assertFalse(Modifier.isAbstract(AudioSourceImportValueCreator.class.getModifiers()));
  }

  @Test
  public void audioSourceImportValueCreator_extendsSourceImportValueCreator() {
    assertTrue(SourceImportValueCreator.class.isAssignableFrom(AudioSourceImportValueCreator.class));
  }

  // ---- Concrete creators are distinct ----

  @Test
  public void imageAndAudioCreators_areDifferentClasses() {
    assertNotEquals(ImageSourceImportValueCreator.class, AudioSourceImportValueCreator.class);
  }

  // ---- Both concrete creators share the same base ----

  @Test
  public void bothCreators_shareSourceImportValueCreatorBase() {
    assertTrue(SourceImportValueCreator.class.isAssignableFrom(ImageSourceImportValueCreator.class));
    assertTrue(SourceImportValueCreator.class.isAssignableFrom(AudioSourceImportValueCreator.class));
  }

  // ---- All classes are in the expected package ----

  @Test
  public void allClasses_areInExpectedPackage() {
    String expectedPkg = "org.alice.stageide.ast.source";
    assertEquals(expectedPkg, SourceImportValueCreator.class.getPackage().getName());
    assertEquals(expectedPkg, ImageSourceImportValueCreator.class.getPackage().getName());
    assertEquals(expectedPkg, AudioSourceImportValueCreator.class.getPackage().getName());
  }
}
