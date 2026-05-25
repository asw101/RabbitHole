package edu.cmu.cs.dennisc.ui.lookingglass;

import org.alice.math.immutable.Point3;
import org.junit.Test;

import java.awt.event.KeyEvent;

import static org.junit.Assert.*;

public class CameraNavigationFunctionDeepBehaviorTest {
  @Test
  public void requestDistanceClampsAndOrbitAccumulates() {
    CameraNavigationFunction function = new CameraNavigationFunction();

    function.requestDistance(200.0);
    function.requestOrbit(0.5, 0.25);

    assertEquals(100.0, function.getDistanceRequested(), 0.00001);
    assertEquals(Math.PI + 0.5, function.getYawRequested().getAsRadians(), 0.00001);
    assertEquals(0.25, function.getPitchRequested().getAsRadians(), 0.00001);
  }

  @Test
  public void updateMovesTargetAndClampsBelowGround() {
    CameraNavigationFunction function = new CameraNavigationFunction();
    function.requestTarget(new Point3(1, -2, 3));
    function.setKeyPressed(KeyEvent.VK_UP, true);
    function.setKeyPressed(KeyEvent.VK_RIGHT, true);

    function.update(new CameraNavigationDerivative(), new CameraNavigationDerivative(), new CameraNavigationDerivative(), new CameraNavigationDerivative(), 0.5);

    Point3 target = function.getTargetRequested();
    assertEquals(2.0, target.x(), 0.00001);
    assertEquals(0.0, target.y(), 0.00001);
    assertEquals(2.0, target.z(), 0.00001);
  }

  @Test
  public void requestDirectionUsesAccelerationAndDecelerationSigns() {
    assertEquals(4.0, CameraNavigationFunctionLogic.requestDirection(2.0, 1.0, 4.0, -8.0), 0.00001);
    assertEquals(-8.0, CameraNavigationFunctionLogic.requestDirection(1.0, 2.0, 4.0, -8.0), 0.00001);
    assertEquals(8.0, CameraNavigationFunctionLogic.requestDirection(-1.0, -2.0, 4.0, -8.0), 0.00001);
  }
}
