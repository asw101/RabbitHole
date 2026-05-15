/*
 * Copyright (c) 2006-2010, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice", nor may
 *    "Alice" appear in their name, without prior written permission of
 *    Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgement: "This product includes software
 *    developed by Carnegie Mellon University"
 *
 * 5. The gallery of art assets and animations provided with this software is
 *    contributed by Electronic Arts Inc. and may be used for personal,
 *    non-commercial, and academic use only. Redistributions of any program
 *    source code that utilizes The Sims 2 Assets must also retain the copyright
 *    notice, list of conditions and the disclaimer contained in
 *    The Alice 3.0 Art Gallery License.
 *
 * DISCLAIMER:
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 * ANY AND ALL EXPRESS, STATUTORY OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, AND NON-INFRINGEMENT ARE DISCLAIMED. IN NO EVENT
 * SHALL THE AUTHORS, COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING FROM OR OTHERWISE RELATING TO
 * THE USE OF OR OTHER DEALINGS WITH THE SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.lgna.ik.core.solver;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import org.alice.math.immutable.Vector3;

/**
 * Stateless utility for SVD-based Jacobian pseudo-inverse computation.
 * Extracted from {@link Solver} to reduce its size and isolate pure math.
 */
public final class JacobianMath {

  private JacobianMath() {
  }

  /**
   * Computes two pseudo-inverses of the given Jacobian matrix via SVD:
   * <ul>
   *   <li>[0] — damped pseudo-inverse (for motion)</li>
   *   <li>[1] — basic pseudo-inverse (for nullspace projection)</li>
   * </ul>
   *
   * @param jacobian           the Jacobian matrix (not mutated)
   * @param svdDampingConstant damping constant λ for the damped inverse
   * @return a two-element array of pseudo-inverse matrices
   */
  public static Matrix[] computePseudoInverses(Matrix jacobian, double svdDampingConstant) {
    boolean transposed = jacobian.getRowDimension() < jacobian.getColumnDimension();
    Matrix mj = transposed ? jacobian.transpose() : jacobian;

    SingularValueDecomposition svd = new SingularValueDecomposition(mj);

    Matrix u = svd.getU();
    Matrix s = svd.getS();
    Matrix v = svd.getV();

    Matrix sForBasic = s.copy();

    reduceAndInvertSofSvdByDamping(s, svdDampingConstant);
    reduceAndInvertSofSvdBasically(sForBasic);

    Matrix uTranspose = u.transpose();
    Matrix pseudoInverseForMotion = v.times(s).times(uTranspose);
    Matrix pseudoInverseForNullspace = v.times(sForBasic).times(uTranspose);

    if (transposed) {
      pseudoInverseForMotion = pseudoInverseForMotion.transpose();
      pseudoInverseForNullspace = pseudoInverseForNullspace.transpose();
    }

    return new Matrix[]{pseudoInverseForMotion, pseudoInverseForNullspace};
  }

  /**
   * Converts an array of 3D velocities into a stacked column matrix (n*3 × 1).
   *
   * @param velocities array of velocity vectors
   * @return column matrix with x, y, z components stacked sequentially
   */
  public static Matrix createDesiredVelocitiesColumn(Vector3[] velocities) {
    Matrix rv = new Matrix(velocities.length * 3, 1);

    int row = 0;
    for (Vector3 velocity : velocities) {
      rv.set(row, 0, velocity.x());
      rv.set(row + 1, 0, velocity.y());
      rv.set(row + 2, 0, velocity.z());
      row += 3;
    }

    return rv;
  }

  static void reduceAndInvertSofSvdBasically(Matrix s) {
    assert (s.getRowDimension() == s.getColumnDimension());
    for (int i = 0; i < s.getRowDimension(); ++i) {
      s.set(i, i, 1.0 / s.get(i, i));
    }
  }

  static void reduceAndInvertSofSvdByDamping(Matrix s, double svdDampingConstant) {
    assert (s.getRowDimension() == s.getColumnDimension());
    for (int i = 0; i < s.getRowDimension(); ++i) {
      double d = s.get(i, i);
      s.set(i, i, d / ((d * d) + (svdDampingConstant * svdDampingConstant)));
    }
  }

  static void reduceAndInvertSofSvdByClampingSmallEntries(Matrix s, double threshold) {
    assert (s.getRowDimension() == s.getColumnDimension());
    for (int i = 0; i < s.getRowDimension(); ++i) {
      if (s.get(i, i) < threshold) {
        s.set(i, i, 0.0);
        assert threshold > 0.0;
      } else {
        s.set(i, i, 1.0 / s.get(i, i));
      }
    }
  }
}
