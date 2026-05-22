package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.render.gl.imp.PickContext;
import edu.cmu.cs.dennisc.render.gl.imp.PickParameters;
import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Component;
import edu.cmu.cs.dennisc.scenegraph.Cylinder;
import edu.cmu.cs.dennisc.scenegraph.Disc;
import edu.cmu.cs.dennisc.scenegraph.FillingStyle;
import edu.cmu.cs.dennisc.scenegraph.Geometry;
import edu.cmu.cs.dennisc.scenegraph.Scene;
import edu.cmu.cs.dennisc.scenegraph.ShadingStyle;
import edu.cmu.cs.dennisc.scenegraph.SimpleAppearance;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AngleInDegrees;

import java.awt.Point;

final class AdapterRenderTestSupport {
  private AdapterRenderTestSupport() {
  }

  static void resetFactory() {
    AdapterFactory.forgetAllElements();
  }

  static HeadlessRecordingGL2 gl() {
    return new HeadlessRecordingGL2();
  }

  static RenderContext renderContext(HeadlessRecordingGL2 gl) {
    RenderContext rc = new RenderContext();
    rc.setGL(gl);
    rc.initialize();
    return rc;
  }

  static PickContext pickContext(HeadlessRecordingGL2 gl) {
    PickContext pc = new PickContext(true);
    pc.setGL(gl);
    pc.initialize();
    return pc;
  }

  static PickParameters requiredPickParameters() {
    return new PickParameters(null, null, new Point(5, 5), true, null);
  }

  static SimpleAppearance appearance(float opacity) {
    SimpleAppearance appearance = new SimpleAppearance();
    appearance.opacity.setValue(opacity);
    appearance.shadingStyle.setValue(ShadingStyle.SMOOTH);
    appearance.fillingStyle.setValue(FillingStyle.SOLID);
    appearance.isEthereal.setValue(false);
    return appearance;
  }

  static SimpleAppearance wireframeAppearance(float opacity, boolean ethereal) {
    SimpleAppearance appearance = appearance(opacity);
    appearance.fillingStyle.setValue(FillingStyle.WIREFRAME);
    appearance.isEthereal.setValue(ethereal);
    return appearance;
  }

  static Visual visualWith(Geometry geometry, SimpleAppearance appearance) {
    Visual visual = new Visual();
    visual.frontFacingAppearance.setValue(appearance);
    visual.backFacingAppearance.setValue(appearance);
    visual.geometries.setValue(new Geometry[]{geometry});
    return visual;
  }

  @SuppressWarnings("unchecked")
  static GlrVisual<Visual> visualAdapter(Visual visual) {
    return (GlrVisual<Visual>) AdapterFactory.getAdapterFor(visual);
  }

  static Transformable transformableWith(Component child, double x, double y, double z) {
    Transformable transformable = new Transformable();
    transformable.localTransformation.setValue(AffineMatrix4x4.createTranslation(x, y, z));
    transformable.addComponent(child);
    return transformable;
  }

  static Scene sceneWith(Component... components) {
    Scene scene = new Scene();
    for (Component component : components) {
      scene.addComponent(component);
    }
    return scene;
  }

  static SymmetricPerspectiveCamera perspectiveCamera() {
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    camera.verticalViewingAngle.setValue(new AngleInDegrees(60));
    camera.horizontalViewingAngle.setValue(new AngleInDegrees(60));
    camera.nearClippingPlaneDistance.setValue(1.0);
    camera.farClippingPlaneDistance.setValue(100.0);
    return camera;
  }

  static GlrScene sceneAdapter(Scene scene) {
    return AdapterFactory.getAdapterFor(scene);
  }

  static GlrSymmetricPerspectiveCamera cameraAdapter(SymmetricPerspectiveCamera camera) {
    return AdapterFactory.getAdapterFor(camera);
  }

  static Cylinder configuredCylinder() {
    Cylinder cylinder = new Cylinder();
    cylinder.length.setValue(4.0);
    cylinder.bottomRadius.setValue(2.0);
    cylinder.topRadius.setValue(1.0);
    cylinder.hasBottomCap.setValue(true);
    cylinder.hasTopCap.setValue(true);
    cylinder.originAlignment.setValue(Cylinder.OriginAlignment.CENTER);
    cylinder.bottomToTopAxis.setValue(Cylinder.BottomToTopAxis.POSITIVE_Y);
    return cylinder;
  }

  static Disc configuredDisc() {
    Disc disc = new Disc();
    disc.innerRadius.setValue(0.5);
    disc.outerRadius.setValue(2.0);
    disc.isFrontFaceVisible.setValue(true);
    disc.isBackFaceVisible.setValue(true);
    disc.axis.setValue(Disc.Axis.Y);
    return disc;
  }
}
