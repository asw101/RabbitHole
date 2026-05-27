package edu.cmu.cs.dennisc.scenegraph;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TexturedVisualTest {
  private static class StubTexturedVisual extends TexturedVisual {
  }

  @Test
  public void constructorCreatesClampedTexturedAppearance() {
    StubTexturedVisual visual = new StubTexturedVisual();

    assertNotNull(visual.getAppearance());
    assertTrue(visual.getAppearance().isDiffuseColorTextureClamped.getValue());
  }

  @Test
  public void frontFacingAppearanceUsesTexturedAppearanceInstance() {
    StubTexturedVisual visual = new StubTexturedVisual();

    assertSame(visual.getAppearance(), visual.frontFacingAppearance.getValue());
  }

  @Test
  public void textureDefaultsToNullAndNullAssignmentRoundTrips() {
    StubTexturedVisual visual = new StubTexturedVisual();
    assertNull(visual.getTexture());

    visual.setTexture(null);

    assertNull(visual.getTexture());
  }
}
