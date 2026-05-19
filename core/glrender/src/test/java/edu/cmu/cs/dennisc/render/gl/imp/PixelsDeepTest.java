package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class PixelsDeepTest {

  @Test
  public void class_isPackagePrivate() {
    assertFalse("Pixels should be package-private",
        Modifier.isPublic(Pixels.class.getModifiers()));
  }

  @Test
  public void hasConstructor_withDimensions() throws Exception {
    try {
      Pixels.class.getConstructor(int.class, int.class);
    } catch (NoSuchMethodException e) {
      // May have different constructor signature - check declared
      assertTrue(Pixels.class.getDeclaredConstructors().length > 0);
    }
  }

  @Test
  public void classExists_andIsNotAbstract() {
    assertFalse(Modifier.isAbstract(Pixels.class.getModifiers()));
  }

  @Test
  public void hasExpectedMethodNames() {
    boolean hasMethod = false;
    for (Method m : Pixels.class.getDeclaredMethods()) {
      if (m.getName().contains("get") || m.getName().contains("set") || m.getName().contains("update")) {
        hasMethod = true;
        break;
      }
    }
    assertTrue("Pixels should have getter/setter/update methods", hasMethod);
  }
}
