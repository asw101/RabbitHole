package edu.cmu.cs.dennisc.render.gl;



import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class GlrOnscreenRenderTargetStructureTest {



  @Test
  public void classExists() {
    assertNotNull(GlrOnscreenRenderTarget.class);
  }

  @Test
  public void extendsGlrRenderTarget() {
    assertTrue(GlrRenderTarget.class.isAssignableFrom(GlrOnscreenRenderTarget.class));
  }

  @Test
  public void isPackagePrivateClass() {
    assertFalse("GlrOnscreenRenderTarget should be package-private",
        Modifier.isPublic(GlrOnscreenRenderTarget.class.getModifiers()));
  }

  @Test
  public void isNotAbstract() {
    assertFalse(Modifier.isAbstract(GlrOnscreenRenderTarget.class.getModifiers()));
  }
}
