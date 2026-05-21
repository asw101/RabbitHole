package org.alice.ide.typehierarchy;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Structural tests for TypeHierarchyComposite.
 */
public class TypeHierarchyCompositeStructureTest {

  @Test
  public void classIsAccessible() throws ClassNotFoundException {
    assertNotNull(Class.forName("org.alice.ide.typehierarchy.TypeHierarchyComposite"));
  }

  @Test
  public void isPublic() throws ClassNotFoundException {
    assertTrue(Modifier.isPublic(Class.forName("org.alice.ide.typehierarchy.TypeHierarchyComposite").getModifiers()));
  }

  @Test
  public void isNotAbstract() throws ClassNotFoundException {
    assertFalse(Modifier.isAbstract(Class.forName("org.alice.ide.typehierarchy.TypeHierarchyComposite").getModifiers()));
  }

  @Test
  public void inExpectedPackage() throws ClassNotFoundException {
    assertEquals("org.alice.ide.typehierarchy",
      Class.forName("org.alice.ide.typehierarchy.TypeHierarchyComposite").getPackage().getName());
  }
}
