package edu.cmu.cs.dennisc.scenegraph;

import edu.cmu.cs.dennisc.color.Color4f;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LightDefaultsTest {
  private static final double EPSILON = 1.0e-9;

  @Test
  public void ambientAndDirectionalLightsStartWithIndependentDefaults() {
    AmbientLight ambient = new AmbientLight();
    DirectionalLight directional = new DirectionalLight();

    assertEquals(1.0f, ambient.brightness.getValue(), EPSILON);
    assertEquals(1.0f, directional.brightness.getValue(), EPSILON);
    assertEquals(Color4f.WHITE.red, ambient.color.getValue().red, EPSILON);
    assertEquals(Color4f.WHITE.green, ambient.color.getValue().green, EPSILON);
    assertEquals(Color4f.WHITE.blue, ambient.color.getValue().blue, EPSILON);

    ambient.brightness.setValue(0.25f);
    ambient.color.setValue(new Color4f(0.2f, 0.3f, 0.4f, 1.0f));

    assertEquals(0.25f, ambient.brightness.getValue(), EPSILON);
    assertEquals(1.0f, directional.brightness.getValue(), EPSILON);
    assertEquals(Color4f.WHITE.red, directional.color.getValue().red, EPSILON);
    assertEquals(Color4f.WHITE.green, directional.color.getValue().green, EPSILON);
    assertEquals(Color4f.WHITE.blue, directional.color.getValue().blue, EPSILON);
  }
}
