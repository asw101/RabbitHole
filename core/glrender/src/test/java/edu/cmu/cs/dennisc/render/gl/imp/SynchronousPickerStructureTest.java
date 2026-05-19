package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class SynchronousPickerStructureTest {

  @Test
  public void classExists() {
    assertNotNull(SynchronousPicker.class);
  }

  @Test
  public void hasPickMethods() {
    boolean found = false;
    for (Method m : SynchronousPicker.class.getDeclaredMethods()) {
      if (m.getName().toLowerCase().contains("pick")) {
        found = true;
        break;
      }
    }
    assertTrue("Should have pick methods", found);
  }

  @Test
  public void isNotAbstract() {
    assertFalse(Modifier.isAbstract(SynchronousPicker.class.getModifiers()));
  }
}
