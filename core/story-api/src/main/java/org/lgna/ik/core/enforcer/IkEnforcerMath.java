package org.lgna.ik.core.enforcer;

import org.alice.math.immutable.Vector3;

import java.util.function.DoubleUnaryOperator;

final class IkEnforcerMath {
  private IkEnforcerMath() {
    throw new AssertionError();
  }

  static Vector3 clampToMagnitude(Vector3 vector, double maxMagnitude) {
    if ((vector == null) || (maxMagnitude < 0.0)) {
      return vector;
    }
    if (vector.magnitudeSquared() > (maxMagnitude * maxMagnitude)) {
      return vector.normalized().times(maxMagnitude);
    }
    return vector;
  }

  static double reduceDeltaTimeForError(double attemptedDeltaTime, double minDeltaTime, double maxAbsError, DoubleUnaryOperator errorForDeltaTime) {
    double deltaTimeToUse = attemptedDeltaTime;
    double error = errorForDeltaTime.applyAsDouble(deltaTimeToUse);
    while ((Math.abs(error) > maxAbsError) && ((deltaTimeToUse * 0.5) > minDeltaTime)) {
      deltaTimeToUse *= 0.5;
      error = errorForDeltaTime.applyAsDouble(deltaTimeToUse);
    }
    return deltaTimeToUse;
  }

  static Vector3 computeWeightedAxisContribution(Vector3 localAxis, double deltaTime, double speed, double weight) {
    return localAxis.times(deltaTime * speed * weight);
  }
}
