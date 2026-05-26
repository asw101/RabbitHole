package org.alice.interact.manipulator.scenegraph;

import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import org.alice.math.immutable.AffineMatrix4x4;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SnapGridBehaviorTest {
  @Test
  public void currentCameraPositionSnapsToDefaultGridSpacing() {
    SnapGrid grid = new SnapGrid();
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    Transformable cameraParent = new Transformable();
    camera.setParent(cameraParent);
    cameraParent.setLocalTransformation(AffineMatrix4x4.createTranslation(1.24, 7.0, 2.76));

    grid.setCurrentCamera(camera);

    assertEquals(1.0, grid.getLocalTransformation().translation().x(), 1e-6);
    assertEquals(0.0, grid.getLocalTransformation().translation().y(), 1e-6);
    assertEquals(2.5, grid.getLocalTransformation().translation().z(), 1e-6);
  }

  @Test
  public void changingSpacingRecomputesSnapLocationForCurrentCamera() {
    SnapGrid grid = new SnapGrid();
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    Transformable cameraParent = new Transformable();
    camera.setParent(cameraParent);
    cameraParent.setLocalTransformation(AffineMatrix4x4.createTranslation(1.24, 0.0, 2.76));
    grid.setCurrentCamera(camera);

    grid.setSpacing(1.0);

    assertEquals(1.0, grid.getLocalTransformation().translation().x(), 1e-6);
    assertEquals(0.0, grid.getLocalTransformation().translation().y(), 1e-6);
    assertEquals(2.0, grid.getLocalTransformation().translation().z(), 1e-6);
  }

  @Test
  public void trackedCameraMovementUpdatesGridTranslation() {
    SnapGrid grid = new SnapGrid();
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    Transformable cameraParent = new Transformable();
    camera.setParent(cameraParent);
    grid.addCamera(camera);
    grid.setCurrentCamera(camera);

    cameraParent.localTransformation.setValue(AffineMatrix4x4.createTranslation(3.76, 0.0, -0.24));

    assertEquals(3.5, grid.getLocalTransformation().translation().x(), 1e-6);
    assertEquals(0.0, grid.getLocalTransformation().translation().y(), 1e-6);
    assertEquals(0.0, grid.getLocalTransformation().translation().z(), 1e-6);
  }
}
