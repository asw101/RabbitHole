package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class GlTessellationRendererStructureTest {

  @Test
  public void classExists() {
    assertNotNull(GlTessellationRenderer.class);
  }

  @Test
  public void hasRenderMethods() {
    boolean found = false;
    for (Method m : GlTessellationRenderer.class.getDeclaredMethods()) {
      if (m.getName().toLowerCase().contains("render") || m.getName().toLowerCase().contains("tessellat")) {
        found = true;
        break;
      }
    }
    assertTrue("Should have render/tessellation methods", found);
  }

  @Test
  public void isPublicClass() {
    assertTrue(Modifier.isPublic(GlTessellationRenderer.class.getModifiers()));
  }
}
