package org.alice.ide.frametitle;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Structural tests for frame title generator classes.
 */
public class FrameTitleGeneratorStructureTest {

  @Test
  public void ideFrameTitleGenerator_classIsAccessible() {
    assertNotNull(IdeFrameTitleGenerator.class);
  }

  @Test
  public void ideFrameTitleGenerator_isPublic() {
    assertTrue(Modifier.isPublic(IdeFrameTitleGenerator.class.getModifiers()));
  }

  @Test
  public void ideFrameTitleGenerator_isAbstract() {
    assertTrue(Modifier.isAbstract(IdeFrameTitleGenerator.class.getModifiers()));
  }

  @Test
  public void aliceIdeFrameTitleGenerator_classIsAccessible() {
    assertNotNull(AliceIdeFrameTitleGenerator.class);
  }

  @Test
  public void aliceIdeFrameTitleGenerator_isPublic() {
    assertTrue(Modifier.isPublic(AliceIdeFrameTitleGenerator.class.getModifiers()));
  }

  @Test
  public void aliceIdeFrameTitleGenerator_isNotAbstract() {
    assertFalse(Modifier.isAbstract(AliceIdeFrameTitleGenerator.class.getModifiers()));
  }

  @Test
  public void aliceIdeFrameTitleGenerator_extendsIdeFrameTitleGenerator() {
    assertTrue(IdeFrameTitleGenerator.class.isAssignableFrom(AliceIdeFrameTitleGenerator.class));
  }

  @Test
  public void allClasses_inExpectedPackage() {
    assertEquals("org.alice.ide.frametitle", IdeFrameTitleGenerator.class.getPackage().getName());
    assertEquals("org.alice.ide.frametitle", AliceIdeFrameTitleGenerator.class.getPackage().getName());
  }
}
