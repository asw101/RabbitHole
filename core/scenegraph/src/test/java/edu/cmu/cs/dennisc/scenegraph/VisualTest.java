package edu.cmu.cs.dennisc.scenegraph;

import edu.cmu.cs.dennisc.pattern.event.ReleaseEvent;
import edu.cmu.cs.dennisc.pattern.event.ReleaseListener;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Matrix3x3;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.assertPointEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class VisualTest {

  @Test
  public void getAxisAlignedMinimumBoundingBoxDelegatesToGeometryAndAppliesScale() {
    Visual visual = new Visual();
    Sphere sphere = new Sphere();
    sphere.radius.setValue(1.0);
    visual.setGeometry(sphere);

    // Scale 2x on all axes
    Matrix3x3 scale2x = Matrix3x3.create(
        new Vector3(2, 0, 0),
        new Vector3(0, 2, 0),
        new Vector3(0, 0, 2)
    );
    visual.scale.setValue(scale2x);

    AxisAlignedBox bbox = visual.getAxisAlignedMinimumBoundingBox();
    assertNotNull(bbox);
    assertPointEquals(new Point3(-2, -2, -2), bbox.minimum());
    assertPointEquals(new Point3(2, 2, 2), bbox.maximum());
  }

  @Test
  public void getAxisAlignedMinimumBoundingBoxReturnsNullWithNoGeometry() {
    Visual visual = new Visual();
    assertNull(visual.getAxisAlignedMinimumBoundingBox());
  }

  @Test
  public void setGeometryReplacesExistingGeometry() {
    Visual visual = new Visual();
    Sphere sphere = new Sphere();
    Cylinder cylinder = new Cylinder();

    visual.setGeometry(sphere);
    assertSame(sphere, visual.getGeometry());
    assertEquals(1, visual.getGeometryCount());

    visual.setGeometry(cylinder);
    assertSame(cylinder, visual.getGeometry());
    assertEquals(1, visual.getGeometryCount());
  }

  @Test
  public void setGeometryToNullClearsGeometries() {
    Visual visual = new Visual();
    visual.setGeometry(new Sphere());
    assertEquals(1, visual.getGeometryCount());

    visual.setGeometry(null);
    assertNull(visual.getGeometry());
    assertEquals(0, visual.getGeometryCount());
  }

  @Test
  public void geometryArraySupportsMultipleGeometries() {
    Visual visual = new Visual();
    Sphere sphere = new Sphere();
    Cylinder cylinder = new Cylinder();

    visual.geometries.setValue(new Geometry[] {sphere, cylinder});
    assertEquals(2, visual.getGeometryCount());
    assertSame(sphere, visual.getGeometryAt(0));
    assertSame(cylinder, visual.getGeometryAt(1));
  }

  @Test
  public void defaultPropertyValues() {
    Visual visual = new Visual();
    assertTrue(visual.isShowing.getValue());
    assertTrue(visual.isPickable.getValue());
    assertNull(visual.frontFacingAppearance.getValue());
    assertNull(visual.backFacingAppearance.getValue());
    assertEquals(Matrix3x3.IDENTITY, visual.scale.getValue());
  }

  @Test
  public void releaseReleasesAppearancesAndGeometries() {
    Visual visual = new Visual();
    SimpleAppearance appearance = new SimpleAppearance();
    Sphere sphere = new Sphere();

    visual.frontFacingAppearance.setValue(appearance);
    visual.setGeometry(sphere);

    List<String> releasingCalls = new ArrayList<>();
    List<String> releasedCalls = new ArrayList<>();

    ReleaseListener trackingListener = new ReleaseListener() {
      @Override
      public void releasing(ReleaseEvent e) {
        releasingCalls.add(e.getSource().getClass().getSimpleName());
      }

      @Override
      public void released(ReleaseEvent e) {
        releasedCalls.add(e.getSource().getClass().getSimpleName());
      }
    };

    visual.addReleaseListener(trackingListener);
    appearance.addReleaseListener(trackingListener);
    sphere.addReleaseListener(trackingListener);

    visual.release();

    assertEquals(3, releasingCalls.size());
    assertEquals(3, releasedCalls.size());
    assertTrue(releasingCalls.contains("Visual"));
    assertTrue(releasingCalls.contains("SimpleAppearance"));
    assertTrue(releasingCalls.contains("Sphere"));
  }

}
