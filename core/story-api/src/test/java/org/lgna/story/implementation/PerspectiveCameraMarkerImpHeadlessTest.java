package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.scenegraph.Box;
import edu.cmu.cs.dennisc.scenegraph.Cylinder;
import edu.cmu.cs.dennisc.scenegraph.QuadArray;
import edu.cmu.cs.dennisc.scenegraph.SimpleAppearance;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.Vertex;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/** Additional headless-safe tests for PerspectiveCameraMarkerImp geometry helpers. */
public class PerspectiveCameraMarkerImpHeadlessTest {

  @Test
  public void reflectedFilmCylinderUsesPositiveXAxis() throws Exception {
    Cylinder cylinder = (Cylinder) invokePrivateNoArgs("createFilmCylinder");
    assertEquals(Cylinder.BottomToTopAxis.POSITIVE_X, cylinder.bottomToTopAxis.getValue());
  }

  @Test
  public void reflectedFilmCylinderHasCapsAndPositiveDimensions() throws Exception {
    Cylinder cylinder = (Cylinder) invokePrivateNoArgs("createFilmCylinder");
    assertTrue(cylinder.hasTopCap.getValue());
    assertTrue(cylinder.hasBottomCap.getValue());
    assertTrue(cylinder.length.getValue() > 0.0);
  }

  @Test
  public void reflectedBoxVisualUsesProvidedParentAndAppearance() throws Exception {
    SimpleAppearance appearance = new SimpleAppearance();
    Transformable parent = new Transformable();
    Visual visual = (Visual) invokePrivateTwoArgs("createBoxVisual", SimpleAppearance.class, Transformable.class, appearance, parent);
    assertSame(parent, visual.getParent());
    assertSame(appearance, visual.frontFacingAppearance.getValue());
  }

  @Test
  public void reflectedBoxVisualContainsExpectedGeometryBounds() throws Exception {
    Visual visual = (Visual) invokePrivateTwoArgs("createBoxVisual", SimpleAppearance.class, Transformable.class, new SimpleAppearance(), new Transformable());
    Box box = (Box) visual.geometries.getValue()[0];
    assertEquals(-0.075, box.getMinimum().x(), 1e-6);
    assertEquals(0.075, box.getMaximum().x(), 1e-6);
    assertEquals(0.75, box.getMaximum().z(), 1e-6);
  }

  @Test
  public void reflectedCylinderVisualCreatesParentTransformable() throws Exception {
    Transformable parent = new Transformable();
    Visual visual = (Visual) invokePrivateFourArgs("createCylinderVisual", String.class, double.class, SimpleAppearance.class, Transformable.class,
        "Camera Cylinder 1", 0.1875, new SimpleAppearance(), parent);
    assertNotSame(parent, visual.getParent());
    assertSame(parent, visual.getParent().getParent());
  }

  @Test
  public void reflectedCylinderVisualContainsCylinderGeometry() throws Exception {
    Visual visual = (Visual) invokePrivateFourArgs("createCylinderVisual", String.class, double.class, SimpleAppearance.class, Transformable.class,
        "Camera Cylinder 2", 0.5625, new SimpleAppearance(), new Transformable());
    assertTrue(visual.geometries.getValue()[0] instanceof Cylinder);
  }

  @Test
  public void reflectedLensVisualContainsThirtyTwoVertices() throws Exception {
    Visual visual = (Visual) invokePrivateTwoArgs("createLensVisual", SimpleAppearance.class, Transformable.class, new SimpleAppearance(), new Transformable());
    QuadArray quadArray = (QuadArray) visual.geometries.getValue()[0];
    Vertex[] vertices = quadArray.vertices.getValue();
    assertEquals(32, vertices.length);
  }

  @Test
  public void reflectedLensVisualUsesProvidedParentAndName() throws Exception {
    Transformable parent = new Transformable();
    Visual visual = (Visual) invokePrivateTwoArgs("createLensVisual", SimpleAppearance.class, Transformable.class, new SimpleAppearance(), parent);
    assertEquals("Camera Lens Hood Visual", visual.getName());
    assertSame(parent, visual.getParent());
  }

  @Test
  public void createCameraVisualsContainExpectedGeometryTypes() {
    Visual[] visuals = PerspectiveCameraMarkerImp.createCameraVisuals(new SimpleAppearance(), new Transformable());
    assertTrue(visuals[0].geometries.getValue()[0] instanceof QuadArray);
    assertTrue(visuals[1].geometries.getValue()[0] instanceof Cylinder);
    assertTrue(visuals[2].geometries.getValue()[0] instanceof Cylinder);
    assertTrue(visuals[3].geometries.getValue()[0] instanceof Box);
  }

  private static Object invokePrivateNoArgs(String methodName) throws Exception {
    Method method = PerspectiveCameraMarkerImp.class.getDeclaredMethod(methodName);
    method.setAccessible(true);
    return method.invoke(null);
  }

  private static Object invokePrivateTwoArgs(String methodName, Class<?> p0, Class<?> p1, Object a0, Object a1) throws Exception {
    Method method = PerspectiveCameraMarkerImp.class.getDeclaredMethod(methodName, p0, p1);
    method.setAccessible(true);
    return method.invoke(null, a0, a1);
  }

  private static Object invokePrivateFourArgs(String methodName, Class<?> p0, Class<?> p1, Class<?> p2, Class<?> p3,
                                              Object a0, Object a1, Object a2, Object a3) throws Exception {
    Method method = PerspectiveCameraMarkerImp.class.getDeclaredMethod(methodName, p0, p1, p2, p3);
    method.setAccessible(true);
    return method.invoke(null, a0, a1, a2, a3);
  }
}
