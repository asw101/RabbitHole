package org.lgna.ik.core.enforcer;

class AngleDeltas {
  double[] storage;
  private final IkEnforcerContext context;

  public AngleDeltas(int numAllPossibleAxes, IkEnforcerContext context) {
    storage = new double[numAllPossibleAxes];
    this.context = context;
  }

  public void add(AngleDeltas other) {
    assert storage.length == other.storage.length;
    for (int i = 0; i < storage.length; ++i) {
      storage[i] += other.storage[i];
    }
  }

  public double getByGlobalIndex(int coli) {
    return storage[coli];
  }

  public double getForAxis(JacobianAxis axis) {
    int globalIndex = context.getAxisToIndex().get(axis);
    double delta = this.getByGlobalIndex(globalIndex);
    return delta;
  }

  public void correctDeltaForAxis(int index, double correction) {
    storage[index] -= correction;
  }
}
