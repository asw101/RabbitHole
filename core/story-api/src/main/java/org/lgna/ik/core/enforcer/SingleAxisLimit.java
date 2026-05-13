package org.lgna.ik.core.enforcer;

class SingleAxisLimit extends Limit {
  private final double minAngle;
  private final double maxAngle;

  public SingleAxisLimit(double minAngle, double maxAngle) {
    this.minAngle = minAngle;
    this.maxAngle = maxAngle;
  }

  public double getMinAngle() {
    return minAngle;
  }

  public double getMaxAngle() {
    return maxAngle;
  }
}
