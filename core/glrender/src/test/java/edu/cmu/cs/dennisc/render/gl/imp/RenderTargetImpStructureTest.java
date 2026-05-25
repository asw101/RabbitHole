package edu.cmu.cs.dennisc.render.gl.imp;


import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class RenderTargetImpStructureTest {


  @Test
  public void classExists() {
    assertNotNull(RenderTargetImp.class);
  }

  @Test
  public void isPublicClass() {
    assertTrue(Modifier.isPublic(RenderTargetImp.class.getModifiers()));
  }

  @Test
  public void hasDisplayMethods() {
    boolean found = false;
    for (Method m : RenderTargetImp.class.getDeclaredMethods()) {
      if (m.getName().toLowerCase().contains("display") || m.getName().toLowerCase().contains("render")) {
        found = true;
        break;
      }
    }
    assertTrue("Should have display/render methods", found);
  }

  @Test
  public void hasSizeMethods() {
    boolean found = false;
    for (Method m : RenderTargetImp.class.getDeclaredMethods()) {
      if (m.getName().toLowerCase().contains("size") || m.getName().toLowerCase().contains("viewport")) {
        found = true;
        break;
      }
    }
    assertTrue("Should have size/viewport methods", found);
  }
}
