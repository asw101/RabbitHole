package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class GlImageRendererStructureTest {

  @Test
  public void classExists() {
    assertNotNull(GlImageRenderer.class);
  }

  @Test
  public void hasRenderMethods() {
    boolean found = false;
    for (Method m : GlImageRenderer.class.getDeclaredMethods()) {
      if (m.getName().toLowerCase().contains("render") || m.getName().toLowerCase().contains("image")) {
        found = true;
        break;
      }
    }
    assertTrue("Should have render/image methods", found);
  }

  @Test
  public void isPublicClass() {
    assertTrue(Modifier.isPublic(GlImageRenderer.class.getModifiers()));
  }
}
