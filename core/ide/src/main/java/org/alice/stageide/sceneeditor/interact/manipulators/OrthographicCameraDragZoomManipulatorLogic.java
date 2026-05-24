package org.alice.stageide.sceneeditor.interact.manipulators;

import org.alice.math.immutable.Vector2;

import java.awt.Color;

final class OrthographicCameraDragZoomManipulatorLogic {
  private OrthographicCameraDragZoomManipulatorLogic() {
    throw new AssertionError();
  }

  static double getZoomValueForColor(Color color, Color inColor, Color outColor, double initialZoomFactor) {
    if (inColor.equals(color)) {
      return -initialZoomFactor;
    }
    if (outColor.equals(color)) {
      return initialZoomFactor;
    }
    return 0.0d;
  }

  static double computeRelativeZoomAmount(Vector2 mousePos, Vector2 initialMousePosition, double zoomsPerSecond, double time) {
    return mousePos.minus(initialMousePosition).y() * zoomsPerSecond * time;
  }

  static double computeTotalZoomAmount(Vector2 mousePos, Vector2 initialMousePosition, double initialZoomValue, double zoomsPerSecond, double time) {
    double relativeZoomAmount = computeRelativeZoomAmount(mousePos, initialMousePosition, zoomsPerSecond, time);
    double initialZoomAmount = initialZoomValue * zoomsPerSecond * time;
    return relativeZoomAmount + initialZoomAmount;
  }

  static double computeNextZoom(double currentZoom, double amount, double minZoom, double maxZoom) {
    double newZoom = currentZoom + amount;
    if (newZoom > maxZoom) {
      return amount > 0.0d ? currentZoom : newZoom;
    }
    if (newZoom < minZoom) {
      return amount < 0.0d ? currentZoom : newZoom;
    }
    return newZoom;
  }
}
