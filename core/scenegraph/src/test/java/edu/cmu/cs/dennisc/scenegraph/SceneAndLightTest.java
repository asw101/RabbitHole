package edu.cmu.cs.dennisc.scenegraph;

import edu.cmu.cs.dennisc.color.Color4f;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.EPSILON;
import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.assertPointEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class SceneAndLightTest {

  // ── Scene ──────────────────────────────────────────

  @Test
  public void sceneAbsoluteTransformationIsIdentity() {
    Scene scene = new Scene();
    AffineMatrix4x4 abs = scene.getAbsoluteTransformation();
    assertPointEquals(Point3.ORIGIN, abs.translation());
  }

  @Test
  public void sceneInverseAbsoluteTransformationIsIdentity() {
    Scene scene = new Scene();
    AffineMatrix4x4 inv = scene.getInverseAbsoluteTransformation();
    assertPointEquals(Point3.ORIGIN, inv.translation());
  }

  @Test
  public void sceneGetRootReturnsSelf() {
    Scene scene = new Scene();
    assertSame(scene, scene.getRoot());
  }

  @Test
  public void sceneIsSceneOfReturnsTrue() {
    Scene scene = new Scene();
    Transformable child = new Transformable();
    scene.addComponent(child);
    assertTrue(scene.isSceneOf(child));
  }

  @Test
  public void sceneGlobalBrightnessDefault() {
    Scene scene = new Scene();
    assertEquals(1.0f, scene.globalBrightness.getValue(), EPSILON);
  }

  @Test
  public void sceneGlobalBrightnessCanBeSet() {
    Scene scene = new Scene();
    scene.globalBrightness.setValue(0.5f);
    assertEquals(0.5f, scene.globalBrightness.getValue(), EPSILON);
  }

  @Test
  public void sceneBackgroundDefaultsToNull() {
    Scene scene = new Scene();
    assertNull(scene.background.getValue());
  }

  // ── DirectionalLight in hierarchy ──────────────────

  @Test
  public void directionalLightAbsoluteTransformationComesFromParent() {
    Scene scene = new Scene();
    Transformable parent = new Transformable();
    parent.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 20, 30));
    DirectionalLight light = new DirectionalLight();

    scene.addComponent(parent);
    parent.addComponent(light);

    // Light's absolute transform comes from its vehicle (parent), not itself
    AffineMatrix4x4 abs = light.getAbsoluteTransformation();
    assertPointEquals(new Point3(10, 20, 30), abs.translation());
  }

  @Test
  public void directionalLightColorSetAndGet() {
    DirectionalLight light = new DirectionalLight();
    light.color.setValue(new Color4f(0.2f, 0.4f, 0.6f, 1.0f));
    Color4f c = light.color.getValue();
    assertEquals(0.2f, c.red, EPSILON);
    assertEquals(0.4f, c.green, EPSILON);
    assertEquals(0.6f, c.blue, EPSILON);
  }

  @Test
  public void pointLightAbsoluteTransformationFromVehicle() {
    Scene scene = new Scene();
    Transformable holder = new Transformable();
    holder.setLocalTransformation(AffineMatrix4x4.createTranslation(5, 5, 5));
    PointLight light = new PointLight();

    scene.addComponent(holder);
    holder.addComponent(light);

    AffineMatrix4x4 abs = light.getAbsoluteTransformation();
    assertPointEquals(new Point3(5, 5, 5), abs.translation());
  }

  @Test
  public void pointLightAttenuationProperties() {
    PointLight light = new PointLight();
    light.constantAttenuation.setValue(2.0);
    light.linearAttenuation.setValue(0.5);
    light.quadraticAttenuation.setValue(0.01);

    assertEquals(2.0, light.constantAttenuation.getValue(), EPSILON);
    assertEquals(0.5, light.linearAttenuation.getValue(), EPSILON);
    assertEquals(0.01, light.quadraticAttenuation.getValue(), EPSILON);
  }

  @Test
  public void ambientLightColorProperty() {
    AmbientLight light = new AmbientLight();
    light.color.setValue(new Color4f(0.1f, 0.2f, 0.3f, 1.0f));
    Color4f c = light.color.getValue();
    assertEquals(0.1f, c.red, EPSILON);
    assertEquals(0.2f, c.green, EPSILON);
  }

  @Test
  public void lightParentingAndDetaching() {
    Scene scene = new Scene();
    Transformable holder = new Transformable();
    DirectionalLight light = new DirectionalLight();

    scene.addComponent(holder);
    holder.addComponent(light);
    assertSame(holder, light.getParent());

    holder.removeComponent(light);
    assertNull(light.getParent());
  }

  @Test
  public void lightGetRootReturnsScene() {
    Scene scene = new Scene();
    DirectionalLight light = new DirectionalLight();
    scene.addComponent(light);
    assertSame(scene, light.getRoot());
  }

  @Test
  public void lightGetRootReturnsNullWhenDetached() {
    DirectionalLight light = new DirectionalLight();
    assertNull(light.getRoot());
  }

  @Test
  public void lightToStringContainsClassName() {
    DirectionalLight light = new DirectionalLight();
    light.setName("myLight");
    String s = light.toString();
    assertTrue(s.contains("DirectionalLight"));
    assertTrue(s.contains("myLight"));
  }

  // ── SpotLight ──────────────────────────────────────

  @Test
  public void spotLightDefaultProperties() {
    SpotLight light = new SpotLight();
    assertNotNull(light);
    // SpotLight extends PointLight, so has attenuation properties
    assertEquals(1.0, light.constantAttenuation.getValue(), EPSILON);
  }

  // ── ExponentialFog ──────────────────────────────────

  @Test
  public void exponentialFogDefaults() {
    ExponentialFog fog = new ExponentialFog();
    assertNotNull(fog);
  }

  // ── Scene property propagation ──────────────────────

  @Test
  public void sceneWithMultipleLightTypes() {
    Scene scene = new Scene();
    DirectionalLight dl = new DirectionalLight();
    dl.setName("dl");
    PointLight pl = new PointLight();
    pl.setName("pl");
    AmbientLight al = new AmbientLight();
    al.setName("al");

    Transformable holder = new Transformable();
    scene.addComponent(holder);
    holder.addComponent(dl);
    holder.addComponent(pl);
    holder.addComponent(al);

    assertEquals(3, holder.getComponentCount());
  }

}
