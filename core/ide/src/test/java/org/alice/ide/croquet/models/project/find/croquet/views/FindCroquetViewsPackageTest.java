package org.alice.ide.croquet.models.project.find.croquet.views;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class FindCroquetViewsPackageTest {
  @Test
  public void representativeClassesRemainInPackage() {
    assertEquals("org.alice.ide.croquet.models.project.find.croquet.views", FindView.class.getPackage().getName());
    assertEquals("org.alice.ide.croquet.models.project.find.croquet.views", FindView.class.getPackage().getName());
  }

  @Test
  public void representativeClassesRemainConcreteTypes() {
    assertFalse(Modifier.isInterface(FindView.class.getModifiers()));
    assertFalse(Modifier.isInterface(FindView.class.getModifiers()));
  }
}
