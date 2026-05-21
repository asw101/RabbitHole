package org.alice.ide.perspectives;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Structural tests for ProjectPerspective.
 */
public class ProjectPerspectiveStructureTest {

  @Test
  public void classIsAccessible() {
    assertNotNull(ProjectPerspective.class);
  }

  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(ProjectPerspective.class.getModifiers()));
  }

  @Test
  public void isAbstract() {
    assertTrue(Modifier.isAbstract(ProjectPerspective.class.getModifiers()));
  }

  @Test
  public void inExpectedPackage() {
    assertEquals("org.alice.ide.perspectives", ProjectPerspective.class.getPackage().getName());
  }

  @Test
  public void hasPublicMethods() {
    assertTrue(ProjectPerspective.class.getMethods().length > 0);
  }
}
