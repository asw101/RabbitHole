package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class GlTextRendererStructureTest {

  @Test
  public void classExists() {
    assertNotNull(GlTextRenderer.class);
  }

  @Test
  public void hasRenderMethods() {
    boolean found = false;
    for (Method m : GlTextRenderer.class.getDeclaredMethods()) {
      if (m.getName().contains("drawString") || m.getName().contains("remember") || m.getName().contains("forget")) {
        found = true;
        break;
      }
    }
    assertTrue("Should have text rendering methods", found);
  }

  @Test
  public void isPackagePrivateClass() {
    assertFalse("GlTextRenderer should be package-private",
        Modifier.isPublic(GlTextRenderer.class.getModifiers()));
  }
}
