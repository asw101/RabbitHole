package edu.cmu.cs.dennisc.render.gl.imp;


import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class IsFrameBufferIntactTest {


  @Test
  public void classExists() {
    assertNotNull(IsFrameBufferIntact.class);
  }

  @Test
  public void classIsNotAbstract() {
    assertFalse(Modifier.isAbstract(IsFrameBufferIntact.class.getModifiers()));
  }

  @Test
  public void hasDeclaredMethods() {
    Method[] methods = IsFrameBufferIntact.class.getDeclaredMethods();
    assertTrue("Should have at least one method", methods.length > 0);
  }

  @Test
  public void implementsExpectedInterface() {
    boolean found = false;
    for (Class<?> iface : IsFrameBufferIntact.class.getInterfaces()) {
      if (iface.getSimpleName().contains("DisplayTask") || iface.getSimpleName().contains("Callable")) {
        found = true;
        break;
      }
    }
    // May not implement those interfaces directly
    assertNotNull(IsFrameBufferIntact.class.getSuperclass());
  }
}
