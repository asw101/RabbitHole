package org.lgna.ik.core.enforcer;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import org.lgna.ik.core.IkConstants;

class SvdInfo {
  boolean isTransposed;
  Matrix u;
  Matrix sForDamped;
  Matrix sForRegular;
  Matrix v;
  boolean isDampedReady;
  boolean isRegularReady;

  Matrix dampedPseudoInverse;
  Matrix regularPseudoInverse;

  public SvdInfo(Matrix mj) {
    int m = mj.getRowDimension();
    int n = mj.getColumnDimension();
    if (m < n) {
      isTransposed = true;
      mj = mj.transpose();
    } else {
      isTransposed = false;
    }

    SingularValueDecomposition svd = new SingularValueDecomposition(mj);

    u = svd.getU();
    Matrix s = svd.getS();
    v = svd.getV();

    sForRegular = s.copy();
    sForDamped = s;
  }

  Matrix createDampedInverse() {
    if (isDampedReady) {
      return dampedPseudoInverse;
    }

    reduceAndInvertSofSvdByDamping(sForDamped, IkConstants.SVD_DAMPING_CONSTANT);

    Matrix pseudoInverseForMotion = v.times(sForDamped).times(u.transpose());

    if (isTransposed) { //TODO perhaps record the fact that matrices are transposed and act accordingly.
      pseudoInverseForMotion = pseudoInverseForMotion.transpose();
    }

    dampedPseudoInverse = pseudoInverseForMotion;
    isDampedReady = true;

    return dampedPseudoInverse;
  }

  private void reduceAndInvertSofSvdByDamping(Matrix s, double svdDampingConstant) {
    assert (s.getRowDimension() == s.getColumnDimension());
    for (int i = 0; i < s.getRowDimension(); ++i) {
      double d = s.get(i, i);
      s.set(i, i, d / ((d * d) + (svdDampingConstant * svdDampingConstant)));
    }
  }

  Matrix createRegularInverse() {
    if (isRegularReady) {
      return regularPseudoInverse;
    }

    reduceAndInvertSofSvdBasically(sForRegular);

    Matrix pseudoInverseForNullspace = v.times(sForRegular).times(u.transpose());

    if (isTransposed) { //TODO perhaps record the fact that matrices are transposed and act accordingly.
      pseudoInverseForNullspace = pseudoInverseForNullspace.transpose();
    }

    regularPseudoInverse = pseudoInverseForNullspace;
    isRegularReady = true;

    return regularPseudoInverse;
  }

  private void reduceAndInvertSofSvdBasically(Matrix s) {
    assert (s.getRowDimension() == s.getColumnDimension());
    for (int i = 0; i < s.getRowDimension(); ++i) {
      s.set(i, i, 1.0 / s.get(i, i));
    }
  }
}
