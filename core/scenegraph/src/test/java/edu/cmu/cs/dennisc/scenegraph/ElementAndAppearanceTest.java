package edu.cmu.cs.dennisc.scenegraph;

import edu.cmu.cs.dennisc.color.Color4f;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.EPSILON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ElementAndAppearanceTest {

  // ── Element base class ──────────────────────────────────────────

  @Test
  public void elementNameDefaultsToNull() {
    Transformable t = new Transformable();
    assertNull(t.getName());
  }

  @Test
  public void elementSetNameAndGetName() {
    Transformable t = new Transformable();
    t.setName("test");
    assertEquals("test", t.getName());
  }

  @Test
  public void elementToStringContainsClassName() {
    Transformable t = new Transformable();
    t.setName("myNode");
    String s = t.toString();
    assertNotNull(s);
    assertTrue("toString should contain class name", s.contains("Transformable"));
    assertTrue("toString should contain name", s.contains("myNode"));
  }

  @Test
  public void elementToStringWithNullName() {
    Transformable t = new Transformable();
    String s = t.toString();
    assertNotNull(s);
    assertTrue(s.contains("Transformable"));
  }

  @Test
  public void bonusDataPutAndGet() {
    Transformable t = new Transformable();
    Element.Key<String> key = Element.Key.createInstance("testKey");
    t.putBonusDataFor(key, "hello");

    assertTrue(t.containsBonusDataFor(key));
    assertEquals("hello", t.getBonusDataFor(key));
  }

  @Test
  public void bonusDataRemove() {
    Transformable t = new Transformable();
    Element.Key<Integer> key = Element.Key.createInstance("intKey");
    t.putBonusDataFor(key, 42);

    assertTrue(t.containsBonusDataFor(key));
    t.removeBonusDataFor(key);
    assertFalse(t.containsBonusDataFor(key));
  }

  @Test
  public void bonusDataNotContainedByDefault() {
    Transformable t = new Transformable();
    Element.Key<String> key = Element.Key.createInstance("noKey");
    assertFalse(t.containsBonusDataFor(key));
    assertNull(t.getBonusDataFor(key));
  }

  @Test
  public void elementKeyToString() {
    Element.Key<String> key = Element.Key.createInstance("myKey");
    assertEquals("myKey", key.toString());
  }

  @Test
  public void newCopyPreservesNameAndType() {
    Transformable original = new Transformable();
    original.setName("original");
    original.setLocalTransformation(AffineMatrix4x4.createTranslation(1, 2, 3));

    Element copy = original.newCopy();
    assertNotNull(copy);
    assertTrue(copy instanceof Transformable);
    assertEquals("original", copy.getName());

    Transformable tCopy = (Transformable) copy;
    Point3 translation = tCopy.getLocalTransformation().translation();
    assertEquals(1.0, translation.x(), EPSILON);
    assertEquals(2.0, translation.y(), EPSILON);
    assertEquals(3.0, translation.z(), EPSILON);
  }

  // ── SimpleAppearance ──────────────────────────────────────────

  @Test
  public void simpleAppearanceDefaultDiffuseColor() {
    SimpleAppearance app = new SimpleAppearance();
    Color4f diffuse = app.diffuseColor.getValue();
    assertNotNull(diffuse);
    assertEquals(1.0f, diffuse.red, EPSILON);
    assertEquals(1.0f, diffuse.green, EPSILON);
    assertEquals(1.0f, diffuse.blue, EPSILON);
    assertEquals(1.0f, diffuse.alpha, EPSILON);
  }

  @Test
  public void simpleAppearanceDefaultOpacity() {
    SimpleAppearance app = new SimpleAppearance();
    assertEquals(1.0f, app.opacity.getValue(), EPSILON);
  }

  @Test
  public void simpleAppearanceDefaultSpecularHighlight() {
    SimpleAppearance app = new SimpleAppearance();
    Color4f spec = app.specularHighlightColor.getValue();
    assertNotNull(spec);
    // Default is BLACK
    assertEquals(0.0f, spec.red, EPSILON);
    assertEquals(0.0f, spec.green, EPSILON);
    assertEquals(0.0f, spec.blue, EPSILON);
  }

  @Test
  public void simpleAppearanceDefaultEmissiveColor() {
    SimpleAppearance app = new SimpleAppearance();
    Color4f emissive = app.emissiveColor.getValue();
    assertNotNull(emissive);
    assertEquals(0.0f, emissive.red, EPSILON);
  }

  @Test
  public void simpleAppearanceDefaultFillingStyle() {
    SimpleAppearance app = new SimpleAppearance();
    assertEquals(FillingStyle.SOLID, app.fillingStyle.getValue());
  }

  @Test
  public void simpleAppearanceDefaultShadingStyle() {
    SimpleAppearance app = new SimpleAppearance();
    assertEquals(ShadingStyle.SMOOTH, app.shadingStyle.getValue());
  }

  @Test
  public void simpleAppearanceDefaultIsEthereal() {
    SimpleAppearance app = new SimpleAppearance();
    assertFalse(app.isEthereal.getValue());
  }

  @Test
  public void simpleAppearanceDefaultSpecularExponent() {
    SimpleAppearance app = new SimpleAppearance();
    assertEquals(0.0f, app.specularHighlightExponent.getValue(), EPSILON);
  }

  @Test
  public void simpleAppearanceAmbientColorDefaultsToNaN() {
    SimpleAppearance app = new SimpleAppearance();
    Color4f ambient = app.ambientColor.getValue();
    assertNotNull(ambient);
    assertTrue("Ambient color should default to NaN", ambient.isNaN());
  }

  @Test
  public void setDiffuseColorUpdatesProperty() {
    SimpleAppearance app = new SimpleAppearance();
    Color4f red = new Color4f(1, 0, 0, 1);
    app.setDiffuseColor(red);
    Color4f result = app.diffuseColor.getValue();
    assertEquals(1.0f, result.red, EPSILON);
    assertEquals(0.0f, result.green, EPSILON);
  }

  @Test
  public void setOpacityUpdatesProperty() {
    SimpleAppearance app = new SimpleAppearance();
    app.setOpacity(0.5f);
    assertEquals(0.5f, app.opacity.getValue(), EPSILON);
  }

  @Test
  public void setAmbientColorNullBecomesNaN() {
    SimpleAppearance app = new SimpleAppearance();
    app.setAmbientColor(null);
    assertTrue("Ambient color set to null should become NaN", app.ambientColor.getValue().isNaN());
  }

  @Test
  public void setAmbientColorUpdatesProperty() {
    SimpleAppearance app = new SimpleAppearance();
    app.setAmbientColor(new Color4f(0.5f, 0.5f, 0.5f, 1.0f));
    Color4f result = app.ambientColor.getValue();
    assertEquals(0.5f, result.red, EPSILON);
  }

  @Test
  public void setSpecularHighlightExponentUpdatesProperty() {
    SimpleAppearance app = new SimpleAppearance();
    app.setSpecularHighlightExponent(64.0f);
    assertEquals(64.0f, app.specularHighlightExponent.getValue(), EPSILON);
  }

  // ── Light properties ──────────────────────────────────────────

  @Test
  public void directionalLightDefaultColor() {
    DirectionalLight light = new DirectionalLight();
    Color4f color = light.color.getValue();
    assertNotNull(color);
    assertEquals(1.0f, color.red, EPSILON);
    assertEquals(1.0f, color.green, EPSILON);
    assertEquals(1.0f, color.blue, EPSILON);
  }

  @Test
  public void directionalLightDefaultBrightness() {
    DirectionalLight light = new DirectionalLight();
    assertEquals(1.0f, light.brightness.getValue(), EPSILON);
  }

  @Test
  public void pointLightDefaultAttenuation() {
    PointLight light = new PointLight();
    assertEquals(1.0, light.constantAttenuation.getValue(), EPSILON);
    assertEquals(0.0, light.linearAttenuation.getValue(), EPSILON);
    assertEquals(0.0, light.quadraticAttenuation.getValue(), EPSILON);
  }

  @Test
  public void ambientLightDefaultColor() {
    AmbientLight light = new AmbientLight();
    Color4f color = light.color.getValue();
    assertNotNull(color);
    assertEquals(1.0f, color.red, EPSILON);
  }

  @Test
  public void lightColorCanBeSet() {
    DirectionalLight light = new DirectionalLight();
    light.color.setValue(new Color4f(0.5f, 0.3f, 0.1f, 1.0f));
    Color4f c = light.color.getValue();
    assertEquals(0.5f, c.red, EPSILON);
    assertEquals(0.3f, c.green, EPSILON);
    assertEquals(0.1f, c.blue, EPSILON);
  }

  @Test
  public void lightBrightnessCanBeSet() {
    DirectionalLight light = new DirectionalLight();
    light.brightness.setValue(0.75f);
    assertEquals(0.75f, light.brightness.getValue(), EPSILON);
  }

  @Test
  public void pointLightAttenuationCanBeSet() {
    PointLight light = new PointLight();
    light.constantAttenuation.setValue(0.5);
    light.linearAttenuation.setValue(0.1);
    light.quadraticAttenuation.setValue(0.01);
    assertEquals(0.5, light.constantAttenuation.getValue(), EPSILON);
    assertEquals(0.1, light.linearAttenuation.getValue(), EPSILON);
    assertEquals(0.01, light.quadraticAttenuation.getValue(), EPSILON);
  }
}
