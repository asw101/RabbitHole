package edu.cmu.cs.dennisc.raytrace;

import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.scenegraph.*;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.*;

public class POVRayUtilitiesTest {

  private Scene createSceneWithCamera() {
    Scene scene = new Scene();
    Background bg = new Background();
    bg.color.setValue(new Color4f(0.5f, 0.5f, 0.5f, 1.0f));
    scene.background.setValue(bg);
    return scene;
  }

  private SymmetricPerspectiveCamera addCamera(Scene scene) {
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    camera.setParent(scene);
    return camera;
  }

  private Visual addVisualWithGeometry(Scene scene, Geometry geometry) {
    Visual visual = new Visual();
    visual.geometries.setValue(new Geometry[]{geometry});
    visual.setParent(scene);
    TexturedAppearance ta = new TexturedAppearance();
    ta.diffuseColor.setValue(Color4f.RED);
    ta.opacity.setValue(1.0f);
    visual.frontFacingAppearance.setValue(ta);
    return visual;
  }

  private String exportToString(AbstractCamera camera) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    POVRayUtilities.export(pw, camera);
    return sw.toString();
  }

  @Test
  public void exportWithEmptyScene() {
    Scene scene = createSceneWithCamera();
    SymmetricPerspectiveCamera camera = addCamera(scene);
    String output = exportToString(camera);
    assertTrue("Should contain background block", output.contains("background"));
    assertTrue("Should contain camera block", output.contains("camera"));
  }

  @Test
  public void exportWithSphere() {
    Scene scene = createSceneWithCamera();
    SymmetricPerspectiveCamera camera = addCamera(scene);
    Sphere sphere = new Sphere();
    sphere.radius.setValue(1.0);
    addVisualWithGeometry(scene, sphere);
    String output = exportToString(camera);
    assertTrue("Should contain sphere", output.contains("sphere"));
  }

  @Test
  public void exportWithBox() {
    Scene scene = createSceneWithCamera();
    SymmetricPerspectiveCamera camera = addCamera(scene);
    Box box = new Box();
    box.xMinimum.setValue(-1.0);
    box.xMaximum.setValue(1.0);
    box.yMinimum.setValue(-1.0);
    box.yMaximum.setValue(1.0);
    box.zMinimum.setValue(-1.0);
    box.zMaximum.setValue(1.0);
    addVisualWithGeometry(scene, box);
    String output = exportToString(camera);
    assertTrue("Should contain box", output.contains("box"));
  }

  @Test
  public void exportWithTorus() {
    Scene scene = createSceneWithCamera();
    SymmetricPerspectiveCamera camera = addCamera(scene);
    Torus torus = new Torus();
    torus.majorRadius.setValue(2.0);
    torus.minorRadius.setValue(0.5);
    addVisualWithGeometry(scene, torus);
    String output = exportToString(camera);
    assertTrue("Should contain torus", output.contains("torus"));
  }

  @Test
  public void exportWithCylinder() {
    Scene scene = createSceneWithCamera();
    SymmetricPerspectiveCamera camera = addCamera(scene);
    Cylinder cylinder = new Cylinder();
    cylinder.bottomRadius.setValue(1.0);
    cylinder.length.setValue(2.0);
    addVisualWithGeometry(scene, cylinder);
    String output = exportToString(camera);
    assertTrue("Should contain cone (cylinder maps to POV cone)", output.contains("cone"));
  }

  @Test
  public void exportWithDisc() {
    Scene scene = createSceneWithCamera();
    SymmetricPerspectiveCamera camera = addCamera(scene);
    Disc disc = new Disc();
    disc.outerRadius.setValue(2.0);
    disc.innerRadius.setValue(0.5);
    addVisualWithGeometry(scene, disc);
    String output = exportToString(camera);
    assertTrue("Should contain disc", output.contains("disc"));
  }

  @Test
  public void exportWithPointLight() {
    Scene scene = createSceneWithCamera();
    SymmetricPerspectiveCamera camera = addCamera(scene);
    PointLight pointLight = new PointLight();
    pointLight.color.setValue(new Color4f(1.0f, 1.0f, 1.0f, 1.0f));
    pointLight.setParent(scene);
    String output = exportToString(camera);
    assertTrue("Should contain light_source", output.contains("light_source"));
  }

  @Test
  public void exportWithDirectionalLight() {
    Scene scene = createSceneWithCamera();
    SymmetricPerspectiveCamera camera = addCamera(scene);
    DirectionalLight dirLight = new DirectionalLight();
    dirLight.color.setValue(new Color4f(1.0f, 1.0f, 1.0f, 1.0f));
    dirLight.setParent(scene);
    String output = exportToString(camera);
    assertTrue("Should contain parallel", output.contains("parallel"));
    assertTrue("Should contain point_at", output.contains("point_at"));
  }

  @Test
  public void exportPreservesColor() {
    Scene scene = createSceneWithCamera();
    SymmetricPerspectiveCamera camera = addCamera(scene);
    Sphere sphere = new Sphere();
    sphere.radius.setValue(1.0);
    Visual visual = new Visual();
    visual.geometries.setValue(new Geometry[]{sphere});
    visual.setParent(scene);
    TexturedAppearance ta = new TexturedAppearance();
    ta.diffuseColor.setValue(new Color4f(0.25f, 0.5f, 0.75f, 1.0f));
    ta.opacity.setValue(1.0f);
    visual.frontFacingAppearance.setValue(ta);
    String output = exportToString(camera);
    assertTrue("Should contain red component", output.contains("0.25"));
    assertTrue("Should contain green component", output.contains("0.5"));
    assertTrue("Should contain blue component", output.contains("0.75"));
  }
}
