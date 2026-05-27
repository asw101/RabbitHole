package edu.cmu.cs.dennisc.scenegraph;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PointLightTest {
  private static final double EPSILON = 1.0e-9;

  @Test
  public void pointLightStartsWithExpectedAttenuationDefaults() {
    PointLight light = new PointLight();

    assertEquals(1.0, light.constantAttenuation.getValue(), EPSILON);
    assertEquals(0.0, light.linearAttenuation.getValue(), EPSILON);
    assertEquals(0.0, light.quadraticAttenuation.getValue(), EPSILON);
  }

  @Test
  public void pointLightAttenuationMutationsDoNotLeakAcrossInstances() {
    PointLight first = new PointLight();
    PointLight second = new PointLight();

    first.constantAttenuation.setValue(2.5);
    first.linearAttenuation.setValue(0.75);
    first.quadraticAttenuation.setValue(0.125);

    assertEquals(2.5, first.constantAttenuation.getValue(), EPSILON);
    assertEquals(0.75, first.linearAttenuation.getValue(), EPSILON);
    assertEquals(0.125, first.quadraticAttenuation.getValue(), EPSILON);
    assertEquals(1.0, second.constantAttenuation.getValue(), EPSILON);
    assertEquals(0.0, second.linearAttenuation.getValue(), EPSILON);
    assertEquals(0.0, second.quadraticAttenuation.getValue(), EPSILON);
  }
}
