package org.alice.stageide.sceneeditor.viewmanager;

import edu.cmu.cs.dennisc.scenegraph.OrthographicCamera;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class CameraMarkerTrackerListenerBehaviorTest {

  @Test
  public void setCamerasRegistersTrackerOnOrthographicPicturePlane() throws Exception {
    CameraMarkerTracker tracker = allocateTracker();
    OrthographicCamera orthographicCamera = new OrthographicCamera();

    tracker.setCameras(new SymmetricPerspectiveCamera(), new SymmetricPerspectiveCamera(), orthographicCamera);

    assertTrue(orthographicCamera.picturePlane.getPropertyListeners().contains(tracker));
  }

  @Test
  public void setCamerasMovesListenerWhenOrthographicCameraChanges() throws Exception {
    CameraMarkerTracker tracker = allocateTracker();
    OrthographicCamera first = new OrthographicCamera();
    OrthographicCamera second = new OrthographicCamera();

    tracker.setCameras(new SymmetricPerspectiveCamera(), new SymmetricPerspectiveCamera(), first);
    tracker.setCameras(new SymmetricPerspectiveCamera(), new SymmetricPerspectiveCamera(), second);

    assertFalse(first.picturePlane.getPropertyListeners().contains(tracker));
    assertTrue(second.picturePlane.getPropertyListeners().contains(tracker));
  }

  @Test
  public void setCamerasWithSameOrthographicCameraDoesNotDuplicateListener() throws Exception {
    CameraMarkerTracker tracker = allocateTracker();
    OrthographicCamera orthographicCamera = new OrthographicCamera();

    tracker.setCameras(new SymmetricPerspectiveCamera(), new SymmetricPerspectiveCamera(), orthographicCamera);
    tracker.setCameras(new SymmetricPerspectiveCamera(), new SymmetricPerspectiveCamera(), orthographicCamera);

    assertEquals(1, orthographicCamera.picturePlane.getPropertyListeners().size());
  }

  @Test
  public void setCamerasWithSameOrthographicCameraStillUpdatesPerspectiveCameras() throws Exception {
    CameraMarkerTracker tracker = allocateTracker();
    OrthographicCamera orthographicCamera = new OrthographicCamera();
    SymmetricPerspectiveCamera initialMain = new SymmetricPerspectiveCamera();
    SymmetricPerspectiveCamera initialLayout = new SymmetricPerspectiveCamera();
    SymmetricPerspectiveCamera replacementMain = new SymmetricPerspectiveCamera();
    SymmetricPerspectiveCamera replacementLayout = new SymmetricPerspectiveCamera();

    tracker.setCameras(initialMain, initialLayout, orthographicCamera);
    tracker.setCameras(replacementMain, replacementLayout, orthographicCamera);

    assertSame(replacementMain, tracker.getMainCamera());
    assertSame(replacementLayout, tracker.getLayoutCamera());
    assertSame(orthographicCamera, tracker.getOrthographicCamera());
  }

  private static CameraMarkerTracker allocateTracker() throws Exception {
    return (CameraMarkerTracker) getUnsafe().allocateInstance(CameraMarkerTracker.class);
  }

  private static sun.misc.Unsafe getUnsafe() throws Exception {
    Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
    field.setAccessible(true);
    return (sun.misc.Unsafe) field.get(null);
  }
}
