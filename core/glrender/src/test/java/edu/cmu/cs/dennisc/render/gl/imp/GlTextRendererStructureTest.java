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
      if (m.getName().toLowerCase().contains("render") || m.getName().toLowerCase().contains("text")) {
        found = true;
        break;
      }
    }
    assertTrue("Should have render/text methods", found);
  }

  @Test
  public void isPublicClass() {
    assertTrue(Modifier.isPublic(GlTextRenderer.class.getModifiers()));
  }
}
