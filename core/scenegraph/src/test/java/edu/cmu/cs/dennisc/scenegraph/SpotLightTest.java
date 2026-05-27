package edu.cmu.cs.dennisc.scenegraph;

import org.alice.math.immutable.AngleInRadians;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SpotLightTest {
  private static final double EPSILON = 1.0e-9;

  @Test
  public void spotLightStartsWithExpectedBeamAndFalloffDefaults() {
    SpotLight light = new SpotLight();

    assertEquals(1.0, light.constantAttenuation.getValue(), EPSILON);
    assertEquals(0.4, light.innerBeamAngle.getValue().getAsRadians(), EPSILON);
    assertEquals(0.5, light.outerBeamAngle.getValue().getAsRadians(), EPSILON);
    assertEquals(1.0, light.falloff.getValue(), EPSILON);
  }

  @Test
  public void spotLightAllowsIndependentBeamAngleUpdates() {
    SpotLight light = new SpotLight();

    light.innerBeamAngle.setValue(new AngleInRadians(0.2));
    light.outerBeamAngle.setValue(new AngleInRadians(0.6));
    light.falloff.setValue(3.5);

    assertEquals(0.2, light.innerBeamAngle.getValue().getAsRadians(), EPSILON);
    assertEquals(0.6, light.outerBeamAngle.getValue().getAsRadians(), EPSILON);
    assertEquals(3.5, light.falloff.getValue(), EPSILON);
  }
}
