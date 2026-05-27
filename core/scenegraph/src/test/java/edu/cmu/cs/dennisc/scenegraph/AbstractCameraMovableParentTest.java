package edu.cmu.cs.dennisc.scenegraph;

import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class AbstractCameraMovableParentTest {
  @Test
  public void movableParentIsDirectVehicleForRegularCameraParents() {
    Transformable vehicle = new Transformable();
    OrthographicCamera camera = new OrthographicCamera();

    vehicle.addComponent(camera);

    assertSame(vehicle, camera.getMovableParent());
  }

  @Test
  public void movableParentSkipsVrHeadsetComposite() {
    Transformable vrUser = new Transformable();
    Transformable headset = new Transformable();
    headset.setName("VRHeadset.sgComposite");
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();

    vrUser.addComponent(headset);
    headset.addComponent(camera);

    assertSame(vrUser, camera.getMovableParent());
  }

  @Test
  public void detachedCameraHasNoMovableParent() {
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();

    assertNull(camera.getMovableParent());
  }
}
