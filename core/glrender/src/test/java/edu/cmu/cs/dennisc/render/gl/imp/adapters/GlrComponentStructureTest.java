package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class GlrComponentStructureTest {

  @Test
  public void classExists() {
    assertNotNull(GlrComponent.class);
  }

  @Test
  public void isPublicClass() {
    assertTrue(Modifier.isPublic(GlrComponent.class.getModifiers()));
  }

  @Test
  public void isAbstractClass() {
    assertTrue(Modifier.isAbstract(GlrComponent.class.getModifiers()));
  }

  @Test
  public void hasDeclaredMethods() {
    assertTrue(GlrComponent.class.getDeclaredMethods().length > 0);
  }

  @Test
  public void hasPropertyChangeHandling() {
    boolean found = false;
    for (Method m : GlrComponent.class.getDeclaredMethods()) {
      if (m.getName().contains("property") || m.getName().contains("Property") ||
          m.getName().contains("changed") || m.getName().contains("Changed") ||
          m.getName().contains("handle") || m.getName().contains("Handle")) {
        found = true;
        break;
      }
    }
    assertTrue("Should handle property changes", found);
  }
}
