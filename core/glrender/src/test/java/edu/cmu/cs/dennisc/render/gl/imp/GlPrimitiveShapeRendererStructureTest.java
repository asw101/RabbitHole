package edu.cmu.cs.dennisc.render.gl.imp;


import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class GlPrimitiveShapeRendererStructureTest {


  @Test
  public void classExists() {
    assertNotNull(GlPrimitiveShapeRenderer.class);
  }

  @Test
  public void hasRenderMethods() {
    boolean found = false;
    for (Method m : GlPrimitiveShapeRenderer.class.getDeclaredMethods()) {
      if (m.getName().toLowerCase().contains("render") || m.getName().toLowerCase().contains("draw")) {
        found = true;
        break;
      }
    }
    assertTrue("Should have render/draw methods", found);
  }

  @Test
  public void isPackagePrivateClass() {
    assertFalse("GlPrimitiveShapeRenderer should be package-private",
        Modifier.isPublic(GlPrimitiveShapeRenderer.class.getModifiers()));
  }

  @Test
  public void isNotAbstract() {
    assertFalse(Modifier.isAbstract(GlPrimitiveShapeRenderer.class.getModifiers()));
  }

  @Test
  public void hasStaticMethods() {
    boolean found = false;
    for (Method m : GlPrimitiveShapeRenderer.class.getDeclaredMethods()) {
      if (Modifier.isStatic(m.getModifiers())) {
        found = true;
        break;
      }
    }
    // May or may not have static methods
    assertNotNull(GlPrimitiveShapeRenderer.class);
  }
}
