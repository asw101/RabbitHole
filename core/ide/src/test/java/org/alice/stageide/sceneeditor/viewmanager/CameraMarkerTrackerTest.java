package org.alice.stageide.sceneeditor.viewmanager;

import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class CameraMarkerTrackerTest {

  private static Method clampCameraMethod;
  private static Method clampPictureMethod;
  private static CameraMarkerTracker tracker;

  @BeforeClass
  public static void setUp() throws Exception {
    // Allocate without calling constructor (which needs graphics context)
    sun.misc.Unsafe unsafe = getUnsafe();
    tracker = (CameraMarkerTracker) unsafe.allocateInstance(CameraMarkerTracker.class);

    clampCameraMethod = CameraMarkerTracker.class.getDeclaredMethod("clampCameraValue", double.class);
    clampCameraMethod.setAccessible(true);
    clampPictureMethod = CameraMarkerTracker.class.getDeclaredMethod("clampPictureValue", double.class);
    clampPictureMethod.setAccessible(true);
  }

  private static sun.misc.Unsafe getUnsafe() throws Exception {
    java.lang.reflect.Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
    f.setAccessible(true);
    return (sun.misc.Unsafe) f.get(null);
  }

  @Test
  public void clampCameraValue_withinRange_returnsValue() throws Exception {
    assertEquals(50.0, (double) clampCameraMethod.invoke(tracker, 50.0), 0.001);
  }

  @Test
  public void clampCameraValue_belowMin_clampsToMin() throws Exception {
    assertEquals(2.0, (double) clampCameraMethod.invoke(tracker, 0.5), 0.001);
  }

  @Test
  public void clampCameraValue_aboveMax_clampsToMax() throws Exception {
    assertEquals(100.0, (double) clampCameraMethod.invoke(tracker, 200.0), 0.001);
  }

  @Test
  public void clampCameraValue_atMin_returnsMin() throws Exception {
    assertEquals(2.0, (double) clampCameraMethod.invoke(tracker, 2.0), 0.001);
  }

  @Test
  public void clampCameraValue_atMax_returnsMax() throws Exception {
    assertEquals(100.0, (double) clampCameraMethod.invoke(tracker, 100.0), 0.001);
  }

  @Test
  public void clampPictureValue_withinRange_returnsValue() throws Exception {
    assertEquals(50.0, (double) clampPictureMethod.invoke(tracker, 50.0), 0.001);
  }

  @Test
  public void clampPictureValue_belowMin_clampsToMin() throws Exception {
    assertEquals(1.5, (double) clampPictureMethod.invoke(tracker, 0.5), 0.001);
  }

  @Test
  public void clampPictureValue_aboveMax_clampsToMax() throws Exception {
    assertEquals(100.0, (double) clampPictureMethod.invoke(tracker, 200.0), 0.001);
  }

  @Test
  public void clampPictureValue_atMin_returnsMin() throws Exception {
    assertEquals(1.5, (double) clampPictureMethod.invoke(tracker, 1.5), 0.001);
  }

  @Test
  public void clampPictureValue_negativeValue_clampsToMin() throws Exception {
    assertEquals(1.5, (double) clampPictureMethod.invoke(tracker, -10.0), 0.001);
  }

  @Test
  public void clampCameraValue_exactlyAtBoundary_2() throws Exception {
    assertEquals(2.0, (double) clampCameraMethod.invoke(tracker, 2.0), 0.001);
  }

  @Test
  public void clampPictureValue_exactlyAtBoundary_100() throws Exception {
    assertEquals(100.0, (double) clampPictureMethod.invoke(tracker, 100.0), 0.001);
  }
}
