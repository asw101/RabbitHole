package org.lgna.ik.core.enforcer;

public class Displacement {
  double[] storage;

  public Displacement(double[] d) {
    storage = d;
  }

  public Displacement createThisMinusOther(Displacement other) {
    if (storage.length != other.storage.length) {
      throw new RuntimeException("Displacements have different lengths.");
    }
    double[] result = new double[storage.length];
    for (int i = 0; i < storage.length; ++i) {
      result[i] = storage[i] - other.storage[i];
    }

    return new Displacement(result);
  }

  public int size() {
    return storage.length;
  }
}
