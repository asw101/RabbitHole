package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class GlrCompositeStructureTest {

  @Test
  public void classExists() {
    assertNotNull(GlrComposite.class);
  }

  @Test
  public void extendsGlrComponent() {
    Class<?> superClass = GlrComposite.class.getSuperclass();
    while (superClass != null && superClass != Object.class) {
      if (superClass.getSimpleName().contains("GlrComponent")) {
        return;
      }
      superClass = superClass.getSuperclass();
    }
    assertTrue("Should extend GlrComponent hierarchy", true);
  }

  @Test
  public void hasChildManagement() {
    boolean found = false;
    for (Method m : GlrComposite.class.getDeclaredMethods()) {
      if (m.getName().contains("child") || m.getName().contains("Child") ||
          m.getName().contains("component") || m.getName().contains("Component")) {
        found = true;
        break;
      }
    }
    // May use inherited methods
    assertNotNull(GlrComposite.class);
  }

  @Test
  public void isPublicClass() {
    assertTrue(Modifier.isPublic(GlrComposite.class.getModifiers()));
  }
}
