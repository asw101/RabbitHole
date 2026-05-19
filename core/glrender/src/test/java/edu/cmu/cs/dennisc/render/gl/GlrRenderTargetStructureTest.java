package edu.cmu.cs.dennisc.render.gl;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class GlrRenderTargetStructureTest {

  @Test
  public void classExists() {
    assertNotNull(GlrRenderTarget.class);
  }

  @Test
  public void isAbstract() {
    assertTrue(Modifier.isAbstract(GlrRenderTarget.class.getModifiers()));
  }

  @Test
  public void hasPickMethods() {
    boolean found = false;
    for (Method m : GlrRenderTarget.class.getDeclaredMethods()) {
      if (m.getName().toLowerCase().contains("pick")) {
        found = true;
        break;
      }
    }
    assertTrue("Should have pick methods", found);
  }

  @Test
  public void hasCameraMethods() {
    boolean found = false;
    for (Method m : GlrRenderTarget.class.getDeclaredMethods()) {
      if (m.getName().toLowerCase().contains("camera")) {
        found = true;
        break;
      }
    }
    assertTrue("Should have camera methods", found);
  }

  @Test
  public void hasRenderMethods() {
    boolean found = false;
    for (Method m : GlrRenderTarget.class.getDeclaredMethods()) {
      if (m.getName().toLowerCase().contains("render")) {
        found = true;
        break;
      }
    }
    assertTrue("Should have render methods", found);
  }
}
