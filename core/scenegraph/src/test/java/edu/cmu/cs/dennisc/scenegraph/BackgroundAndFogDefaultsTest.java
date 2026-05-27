package edu.cmu.cs.dennisc.scenegraph;

import edu.cmu.cs.dennisc.color.Color4f;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class BackgroundAndFogDefaultsTest {
  private static final double TOLERANCE = 1.0e-9;

  @Test
  public void backgroundAndFogStartWithIndependentWhiteColorProperties() {
    Background background = new Background();
    LinearFog linearFog = new LinearFog();
    ExponentialFog exponentialFog = new ExponentialFog();
    ExponentialSquaredFog exponentialSquaredFog = new ExponentialSquaredFog();

    assertEquals(Color4f.WHITE, background.color.getValue());
    assertEquals(Color4f.WHITE, linearFog.color.getValue());
    assertEquals(Color4f.WHITE, exponentialFog.color.getValue());
    assertEquals(Color4f.WHITE, exponentialSquaredFog.color.getValue());

    background.color.setValue(Color4f.BLACK);
    linearFog.color.setValue(Color4f.RED);

    assertEquals(Color4f.BLACK, background.color.getValue());
    assertEquals(Color4f.RED, linearFog.color.getValue());
    assertEquals(Color4f.WHITE, exponentialFog.color.getValue());
    assertNotSame(background.color, linearFog.color);
  }

  @Test
  public void fogSubclassesExposeStableDefaultDistancesAndDensities() {
    LinearFog linearFog = new LinearFog();
    ExponentialFog exponentialFog = new ExponentialFog();
    ExponentialSquaredFog exponentialSquaredFog = new ExponentialSquaredFog();

    assertEquals(1.0, linearFog.nearDistance.getValue(), TOLERANCE);
    assertEquals(256.0, linearFog.farDistance.getValue(), TOLERANCE);
    assertEquals(1.0, exponentialFog.density.getValue(), TOLERANCE);
    assertEquals(1.0, exponentialSquaredFog.density.getValue(), TOLERANCE);
  }
}
