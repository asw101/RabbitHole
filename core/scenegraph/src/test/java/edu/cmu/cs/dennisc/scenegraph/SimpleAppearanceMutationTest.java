package edu.cmu.cs.dennisc.scenegraph;

import edu.cmu.cs.dennisc.color.Color4f;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SimpleAppearanceMutationTest {
  @Test
  public void setterMethodsUpdateAllAppearanceProperties() {
    SimpleAppearance appearance = new SimpleAppearance();
    Color4f ambient = new Color4f(0.1f, 0.2f, 0.3f, 1.0f);
    Color4f diffuse = new Color4f(0.4f, 0.5f, 0.6f, 0.7f);
    Color4f specular = new Color4f(0.8f, 0.7f, 0.6f, 1.0f);
    Color4f emissive = new Color4f(0.3f, 0.2f, 0.1f, 1.0f);

    appearance.setAmbientColor(ambient);
    appearance.setDiffuseColor(diffuse);
    appearance.setOpacity(0.25f);
    appearance.setSpecularHighlightExponent(32.0f);
    appearance.setSpecularHighlightColor(specular);
    appearance.setEmissiveColor(emissive);
    appearance.setFillingStyle(FillingStyle.WIREFRAME);
    appearance.setShadingStyle(ShadingStyle.FLAT);
    appearance.setEthereal(true);

    assertEquals(ambient, appearance.ambientColor.getValue());
    assertEquals(diffuse, appearance.diffuseColor.getValue());
    assertEquals(0.25f, appearance.opacity.getValue(), ScenegraphTestAssertions.EPSILON);
    assertEquals(32.0f, appearance.specularHighlightExponent.getValue(), ScenegraphTestAssertions.EPSILON);
    assertEquals(specular, appearance.specularHighlightColor.getValue());
    assertEquals(emissive, appearance.emissiveColor.getValue());
    assertEquals(FillingStyle.WIREFRAME, appearance.fillingStyle.getValue());
    assertEquals(ShadingStyle.FLAT, appearance.shadingStyle.getValue());
    assertTrue(appearance.isEthereal.getValue());
  }

  @Test
  public void ambientColorNormalizesNullWithoutChangingOtherProperties() {
    SimpleAppearance appearance = new SimpleAppearance();
    appearance.setDiffuseColor(new Color4f(1.0f, 0.0f, 0.0f, 1.0f));
    appearance.setAmbientColor(null);

    assertTrue(appearance.ambientColor.getValue().isNaN());
    assertFalse(appearance.diffuseColor.getValue().isNaN());
    assertEquals(1.0f, appearance.diffuseColor.getValue().red, ScenegraphTestAssertions.EPSILON);
  }
}
