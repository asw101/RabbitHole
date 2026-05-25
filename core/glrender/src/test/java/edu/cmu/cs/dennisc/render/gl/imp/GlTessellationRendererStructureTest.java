package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class GlTessellationRendererStructureTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  @Test
  public void classExists() {
    assertNotNull(GlTessellationRenderer.class);
  }

  @Test
  public void hasRenderMethods() {
    boolean found = false;
    for (Method m : GlTessellationRenderer.class.getDeclaredMethods()) {
      if (m.getName().contains("fill")) {
        found = true;
        break;
      }
    }
    assertTrue("Should have fill method for tessellation", found);
  }

  @Test
  public void isPackagePrivateClass() {
    assertFalse("GlTessellationRenderer should be package-private",
        Modifier.isPublic(GlTessellationRenderer.class.getModifiers()));
  }
}
