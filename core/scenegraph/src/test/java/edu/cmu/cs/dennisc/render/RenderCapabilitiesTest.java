package edu.cmu.cs.dennisc.render;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class RenderCapabilitiesTest {
  @Test
  public void builderDefaultsToZeroStencilBits() {
    RenderCapabilities capabilities = new RenderCapabilities.Builder().build();

    assertEquals(0, capabilities.getStencilBits());
  }

  @Test
  public void builderIsFluentAndCarriesStencilBitsIntoBuiltInstance() {
    RenderCapabilities.Builder builder = new RenderCapabilities.Builder();

    assertSame(builder, builder.stencilBits(8));
    assertEquals(8, builder.build().getStencilBits());
  }
}
