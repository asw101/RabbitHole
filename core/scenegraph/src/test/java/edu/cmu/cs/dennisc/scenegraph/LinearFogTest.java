package edu.cmu.cs.dennisc.scenegraph;

import edu.cmu.cs.dennisc.color.Color4f;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LinearFogTest {
  private static final double EPSILON = 1.0e-9;

  @Test
  public void linearFogStartsWithExpectedDistancesAndColor() {
    LinearFog fog = new LinearFog();

    assertEquals(1.0, fog.nearDistance.getValue(), EPSILON);
    assertEquals(256.0, fog.farDistance.getValue(), EPSILON);
    assertEquals(Color4f.WHITE.red, fog.color.getValue().red, EPSILON);
    assertEquals(Color4f.WHITE.green, fog.color.getValue().green, EPSILON);
    assertEquals(Color4f.WHITE.blue, fog.color.getValue().blue, EPSILON);
  }

  @Test
  public void linearFogSupportsDistanceAndColorMutation() {
    LinearFog fog = new LinearFog();

    fog.nearDistance.setValue(3.0);
    fog.farDistance.setValue(40.0);
    fog.color.setValue(new Color4f(0.1f, 0.2f, 0.3f, 1.0f));

    assertEquals(3.0, fog.nearDistance.getValue(), EPSILON);
    assertEquals(40.0, fog.farDistance.getValue(), EPSILON);
    assertEquals(0.1f, fog.color.getValue().red, EPSILON);
    assertEquals(0.2f, fog.color.getValue().green, EPSILON);
    assertEquals(0.3f, fog.color.getValue().blue, EPSILON);
  }
}
