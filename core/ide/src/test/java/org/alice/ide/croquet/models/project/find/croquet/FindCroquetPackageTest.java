package org.alice.ide.croquet.models.project.find.croquet;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class FindCroquetPackageTest {
  @Test
  public void representativeClassesRemainInPackage() {
    assertEquals("org.alice.ide.croquet.models.project.find.croquet", AbstractFindComposite.class.getPackage().getName());
    assertEquals("org.alice.ide.croquet.models.project.find.croquet", FindComposite.class.getPackage().getName());
  }

  @Test
  public void representativeClassesRemainConcreteTypes() {
    assertFalse(Modifier.isInterface(AbstractFindComposite.class.getModifiers()));
    assertFalse(Modifier.isInterface(FindComposite.class.getModifiers()));
  }
}
