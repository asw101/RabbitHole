package edu.cmu.cs.dennisc.scenegraph;

import edu.cmu.cs.dennisc.color.Color4f;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExponentialSquaredFogTest {
  private static final double EPSILON = 1.0e-9;

  @Test
  public void exponentialSquaredFogStartsWithExpectedDefaults() {
    ExponentialSquaredFog fog = new ExponentialSquaredFog();

    assertEquals(1.0, fog.density.getValue(), EPSILON);
    assertEquals(Color4f.WHITE.red, fog.color.getValue().red, EPSILON);
    assertEquals(Color4f.WHITE.green, fog.color.getValue().green, EPSILON);
    assertEquals(Color4f.WHITE.blue, fog.color.getValue().blue, EPSILON);
  }

  @Test
  public void exponentialSquaredFogAcceptsDensityChanges() {
    ExponentialSquaredFog fog = new ExponentialSquaredFog();

    fog.density.setValue(0.125);

    assertEquals(0.125, fog.density.getValue(), EPSILON);
  }
}
