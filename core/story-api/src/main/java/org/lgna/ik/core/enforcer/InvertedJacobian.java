package org.lgna.ik.core.enforcer;

import Jama.Matrix;

class InvertedJacobian {

  public InvertedJacobian(Matrix inverse, Jacobian jacobian) {
    matrix = inverse;
    straight = jacobian;
  }

  Matrix matrix;
  Jacobian straight;
}
