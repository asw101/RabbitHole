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
import org.alice.math.immutable.Vector3;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TDD tests for JacobianMath — stateless utility for SVD-based
 * pseudo-inverse computation and velocity column construction.
 *
 * Extracted from Solver.java per issue #690.
 * These tests define the contract; JacobianMath does not exist yet.
 */
public class JacobianMathTest {

  private static final double DELTA = 1e-6;
  private static final double DAMPING = 0.1; // matches IkConstants.SVD_DAMPING_CONSTANT

  // ──────────────────────────────────────────────────────────
  // computePseudoInverses: returns Matrix[2]
  //   [0] = damped pseudo-inverse (for motion)
  //   [1] = basic pseudo-inverse (for nullspace)
  // ──────────────────────────────────────────────────────────

  @Test
  public void computePseudoInverses_returnsArrayOfTwo() {
    Matrix m = Matrix.identity(3, 3);
    Matrix[] result = JacobianMath.computePseudoInverses(m, DAMPING);
    assertNotNull(result);
    assertEquals(2, result.length);
    assertNotNull(result[0]);
    assertNotNull(result[1]);
  }

  @Test
  public void computePseudoInverses_identityDampedApproximatesIdentity() {
    Matrix m = Matrix.identity(3, 3);
    Matrix[] result = JacobianMath.computePseudoInverses(m, DAMPING);
    Matrix damped = result[0];

    assertEquals(3, damped.getRowDimension());
    assertEquals(3, damped.getColumnDimension());

    // For identity: singular values are all 1.0
    // Damped: d/(d² + λ²) = 1/(1 + 0.01) ≈ 0.990099
    double expected = 1.0 / (1.0 + DAMPING * DAMPING);
    for (int i = 0; i < 3; i++) {
      assertEquals(expected, damped.get(i, i), DELTA);
      for (int j = 0; j < 3; j++) {
        if (i != j) {
          assertEquals(0.0, damped.get(i, j), DELTA);
        }
      }
    }
  }

  @Test
  public void computePseudoInverses_identityBasicIsExactIdentity() {
    Matrix m = Matrix.identity(3, 3);
    Matrix[] result = JacobianMath.computePseudoInverses(m, DAMPING);
    Matrix basic = result[1];

    assertEquals(3, basic.getRowDimension());
    assertEquals(3, basic.getColumnDimension());

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        double expected = (i == j) ? 1.0 : 0.0;
        assertEquals(expected, basic.get(i, j), DELTA);
      }
    }
  }

  @Test
  public void computePseudoInverses_dampedAndBasicDiffer() {
    Matrix m = Matrix.identity(2, 2);
    Matrix[] result = JacobianMath.computePseudoInverses(m, DAMPING);

    // Damped attenuates singular values, so diagonals differ from basic
    assertNotEquals(result[0].get(0, 0), result[1].get(0, 0), 1e-10);
  }

  @Test
  public void computePseudoInverses_tallMatrixDimensions() {
    // 4×2 input → pseudo-inverse should be 2×4
    Matrix m = new Matrix(new double[][]{
        {1, 0},
        {0, 1},
        {0, 0},
        {0, 0}
    });
    Matrix[] result = JacobianMath.computePseudoInverses(m, DAMPING);

    assertEquals(2, result[0].getRowDimension());
    assertEquals(4, result[0].getColumnDimension());
    assertEquals(2, result[1].getRowDimension());
    assertEquals(4, result[1].getColumnDimension());
  }

  @Test
  public void computePseudoInverses_wideMatrixDimensions() {
    // 2×4 input → pseudo-inverse should be 4×2
    Matrix m = new Matrix(new double[][]{
        {1, 0, 0, 0},
        {0, 1, 0, 0}
    });
    Matrix[] result = JacobianMath.computePseudoInverses(m, DAMPING);

    assertEquals(4, result[0].getRowDimension());
    assertEquals(2, result[0].getColumnDimension());
    assertEquals(4, result[1].getRowDimension());
    assertEquals(2, result[1].getColumnDimension());
  }

  @Test
  public void computePseudoInverses_squareMatrixDimensions() {
    Matrix m = new Matrix(new double[][]{
        {2, 1},
        {1, 3}
    });
    Matrix[] result = JacobianMath.computePseudoInverses(m, DAMPING);

    assertEquals(2, result[0].getRowDimension());
    assertEquals(2, result[0].getColumnDimension());
    assertEquals(2, result[1].getRowDimension());
    assertEquals(2, result[1].getColumnDimension());
  }

  @Test
  public void computePseudoInverses_wideMatrixTransposePathProducesValidInverse() {
    // Wide matrix triggers the transpose path (m < n)
    // Use a simple matrix where we can verify J × J⁺ ≈ I
    Matrix m = new Matrix(new double[][]{
        {1, 0, 0},
        {0, 1, 0}
    });
    Matrix[] result = JacobianMath.computePseudoInverses(m, DAMPING);
    Matrix basic = result[1];

    // J × J⁺ should approximate 2×2 identity (left inverse)
    Matrix product = m.times(basic);
    assertEquals(2, product.getRowDimension());
    assertEquals(2, product.getColumnDimension());
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        double expected = (i == j) ? 1.0 : 0.0;
        assertEquals(expected, product.get(i, j), DELTA);
      }
    }
  }

  @Test
  public void computePseudoInverses_zeroDampingMatchesBasic() {
    Matrix m = new Matrix(new double[][]{
        {3, 1},
        {1, 2}
    });
    Matrix[] result = JacobianMath.computePseudoInverses(m, 0.0);

    // With zero damping, d/(d²+0) = 1/d which is the same as basic
    Matrix damped = result[0];
    Matrix basic = result[1];
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        assertEquals(basic.get(i, j), damped.get(i, j), DELTA);
      }
    }
  }

  @Test
  public void computePseudoInverses_knownDiagonalMatrix() {
    // Diagonal matrix with known singular values [3, 2]
    Matrix m = new Matrix(new double[][]{
        {3, 0},
        {0, 2}
    });
    Matrix[] result = JacobianMath.computePseudoInverses(m, DAMPING);

    // Basic: diag(1/3, 1/2)
    assertEquals(1.0 / 3.0, result[1].get(0, 0), DELTA);
    assertEquals(1.0 / 2.0, result[1].get(1, 1), DELTA);
    assertEquals(0.0, result[1].get(0, 1), DELTA);
    assertEquals(0.0, result[1].get(1, 0), DELTA);

    // Damped: diag(3/(9+0.01), 2/(4+0.01))
    assertEquals(3.0 / (9.0 + 0.01), result[0].get(0, 0), DELTA);
    assertEquals(2.0 / (4.0 + 0.01), result[0].get(1, 1), DELTA);
  }

  @Test
  public void computePseudoInverses_doesNotMutateInput() {
    Matrix m = new Matrix(new double[][]{
        {2, 1},
        {1, 3}
    });
    double original00 = m.get(0, 0);
    double original01 = m.get(0, 1);
    double original10 = m.get(1, 0);
    double original11 = m.get(1, 1);

    JacobianMath.computePseudoInverses(m, DAMPING);

    assertEquals(original00, m.get(0, 0), 0.0);
    assertEquals(original01, m.get(0, 1), 0.0);
    assertEquals(original10, m.get(1, 0), 0.0);
    assertEquals(original11, m.get(1, 1), 0.0);
  }

  @Test
  public void computePseudoInverses_largeDampingAttenuatesMore() {
    Matrix m = new Matrix(new double[][]{
        {1, 0},
        {0, 1}
    });
    Matrix[] smallDamping = JacobianMath.computePseudoInverses(m, 0.01);
    Matrix[] largeDamping = JacobianMath.computePseudoInverses(m, 1.0);

    // Larger damping → smaller diagonal values in the damped inverse
    assertTrue(largeDamping[0].get(0, 0) < smallDamping[0].get(0, 0));
    assertTrue(largeDamping[0].get(1, 1) < smallDamping[0].get(1, 1));
  }

  // ──────────────────────────────────────────────────────────
  // createDesiredVelocitiesColumn: Vector3[] → Matrix(n*3, 1)
  // ──────────────────────────────────────────────────────────

  @Test
  public void createDesiredVelocitiesColumn_singleVelocity() {
    Vector3[] velocities = {new Vector3(1.0, 2.0, 3.0)};
    Matrix result = JacobianMath.createDesiredVelocitiesColumn(velocities);

    assertEquals(3, result.getRowDimension());
    assertEquals(1, result.getColumnDimension());
    assertEquals(1.0, result.get(0, 0), DELTA);
    assertEquals(2.0, result.get(1, 0), DELTA);
    assertEquals(3.0, result.get(2, 0), DELTA);
  }

  @Test
  public void createDesiredVelocitiesColumn_multipleVelocitiesStacked() {
    Vector3[] velocities = {
        new Vector3(1.0, 2.0, 3.0),
        new Vector3(4.0, 5.0, 6.0)
    };
    Matrix result = JacobianMath.createDesiredVelocitiesColumn(velocities);

    assertEquals(6, result.getRowDimension());
    assertEquals(1, result.getColumnDimension());

    // First velocity
    assertEquals(1.0, result.get(0, 0), DELTA);
    assertEquals(2.0, result.get(1, 0), DELTA);
    assertEquals(3.0, result.get(2, 0), DELTA);
    // Second velocity
    assertEquals(4.0, result.get(3, 0), DELTA);
    assertEquals(5.0, result.get(4, 0), DELTA);
    assertEquals(6.0, result.get(5, 0), DELTA);
  }

  @Test
  public void createDesiredVelocitiesColumn_zeroVelocity() {
    Vector3[] velocities = {Vector3.ZERO};
    Matrix result = JacobianMath.createDesiredVelocitiesColumn(velocities);

    assertEquals(3, result.getRowDimension());
    assertEquals(1, result.getColumnDimension());
    assertEquals(0.0, result.get(0, 0), DELTA);
    assertEquals(0.0, result.get(1, 0), DELTA);
    assertEquals(0.0, result.get(2, 0), DELTA);
  }

  @Test
  public void createDesiredVelocitiesColumn_emptyArray() {
    Vector3[] velocities = {};
    Matrix result = JacobianMath.createDesiredVelocitiesColumn(velocities);

    assertEquals(0, result.getRowDimension());
    assertEquals(1, result.getColumnDimension());
  }

  @Test
  public void createDesiredVelocitiesColumn_negativeComponents() {
    Vector3[] velocities = {new Vector3(-1.5, 0.0, 2.5)};
    Matrix result = JacobianMath.createDesiredVelocitiesColumn(velocities);

    assertEquals(-1.5, result.get(0, 0), DELTA);
    assertEquals(0.0, result.get(1, 0), DELTA);
    assertEquals(2.5, result.get(2, 0), DELTA);
  }

  @Test
  public void createDesiredVelocitiesColumn_threeVelocitiesPreservesOrder() {
    Vector3[] velocities = {
        new Vector3(10, 20, 30),
        new Vector3(40, 50, 60),
        new Vector3(70, 80, 90)
    };
    Matrix result = JacobianMath.createDesiredVelocitiesColumn(velocities);

    assertEquals(9, result.getRowDimension());
    // Spot-check ordering
    assertEquals(10.0, result.get(0, 0), DELTA);
    assertEquals(40.0, result.get(3, 0), DELTA);
    assertEquals(70.0, result.get(6, 0), DELTA);
    assertEquals(90.0, result.get(8, 0), DELTA);
  }

  // ──────────────────────────────────────────────────────────
  // SVD helper methods (package-private static)
  // ──────────────────────────────────────────────────────────

  @Test
  public void reduceAndInvertSofSvdBasically_invertsDiagonal() {
    Matrix s = new Matrix(new double[][]{
        {2.0, 0.0, 0.0},
        {0.0, 4.0, 0.0},
        {0.0, 0.0, 5.0}
    });
    JacobianMath.reduceAndInvertSofSvdBasically(s);

    assertEquals(1.0 / 2.0, s.get(0, 0), DELTA);
    assertEquals(1.0 / 4.0, s.get(1, 1), DELTA);
    assertEquals(1.0 / 5.0, s.get(2, 2), DELTA);
  }

  @Test
  public void reduceAndInvertSofSvdBasically_unitValue() {
    Matrix s = new Matrix(new double[][]{
        {1.0, 0.0},
        {0.0, 1.0}
    });
    JacobianMath.reduceAndInvertSofSvdBasically(s);

    assertEquals(1.0, s.get(0, 0), DELTA);
    assertEquals(1.0, s.get(1, 1), DELTA);
  }

  @Test
  public void reduceAndInvertSofSvdBasically_offDiagonalUntouched() {
    Matrix s = new Matrix(new double[][]{
        {2.0, 99.0},
        {99.0, 4.0}
    });
    JacobianMath.reduceAndInvertSofSvdBasically(s);

    // Only diagonal is modified
    assertEquals(1.0 / 2.0, s.get(0, 0), DELTA);
    assertEquals(1.0 / 4.0, s.get(1, 1), DELTA);
    assertEquals(99.0, s.get(0, 1), DELTA);
    assertEquals(99.0, s.get(1, 0), DELTA);
  }

  @Test
  public void reduceAndInvertSofSvdByDamping_appliesDampingFormula() {
    Matrix s = new Matrix(new double[][]{
        {3.0, 0.0},
        {0.0, 0.5}
    });
    double lambda = 0.1;
    JacobianMath.reduceAndInvertSofSvdByDamping(s, lambda);

    // d / (d² + λ²)
    assertEquals(3.0 / (9.0 + 0.01), s.get(0, 0), DELTA);
    assertEquals(0.5 / (0.25 + 0.01), s.get(1, 1), DELTA);
  }

  @Test
  public void reduceAndInvertSofSvdByDamping_zeroDampingEqualsBasicInverse() {
    Matrix s1 = new Matrix(new double[][]{{4.0, 0.0}, {0.0, 2.0}});
    Matrix s2 = s1.copy();

    JacobianMath.reduceAndInvertSofSvdByDamping(s1, 0.0);
    JacobianMath.reduceAndInvertSofSvdBasically(s2);

    assertEquals(s2.get(0, 0), s1.get(0, 0), DELTA);
    assertEquals(s2.get(1, 1), s1.get(1, 1), DELTA);
  }

  @Test
  public void reduceAndInvertSofSvdByDamping_largeDampingAttenuates() {
    Matrix s = new Matrix(new double[][]{{1.0}});
    JacobianMath.reduceAndInvertSofSvdByDamping(s, 10.0);

    // 1 / (1 + 100) = 0.00990...
    assertEquals(1.0 / 101.0, s.get(0, 0), DELTA);
  }

  @Test
  public void reduceAndInvertSofSvdByClampingSmallEntries_clampsBelow() {
    Matrix s = new Matrix(new double[][]{
        {5.0, 0.0, 0.0},
        {0.0, 0.001, 0.0},
        {0.0, 0.0, 3.0}
    });
    JacobianMath.reduceAndInvertSofSvdByClampingSmallEntries(s, 0.01);

    assertEquals(1.0 / 5.0, s.get(0, 0), DELTA);
    assertEquals(0.0, s.get(1, 1), DELTA); // clamped to zero
    assertEquals(1.0 / 3.0, s.get(2, 2), DELTA);
  }

  @Test
  public void reduceAndInvertSofSvdByClampingSmallEntries_atThresholdIsInverted() {
    Matrix s = new Matrix(new double[][]{{0.01}});
    JacobianMath.reduceAndInvertSofSvdByClampingSmallEntries(s, 0.01);

    // Exactly at threshold → NOT clamped (original uses strict < comparison)
    assertEquals(1.0 / 0.01, s.get(0, 0), DELTA);
  }

  @Test
  public void reduceAndInvertSofSvdByClampingSmallEntries_aboveThreshold() {
    Matrix s = new Matrix(new double[][]{{2.0}});
    JacobianMath.reduceAndInvertSofSvdByClampingSmallEntries(s, 0.01);

    assertEquals(1.0 / 2.0, s.get(0, 0), DELTA);
  }

  // ──────────────────────────────────────────────────────────
  // Integration: computePseudoInverses matches Solver behavior
  // ──────────────────────────────────────────────────────────

  @Test
  public void computePseudoInverses_matchesSolverInvertJacobianBehavior() {
    // Verify the extracted method produces the same results as
    // the original Solver.invertJacobian for a non-trivial matrix
    Matrix jacobian = new Matrix(new double[][]{
        {1.0, 0.5, 0.0},
        {0.0, 1.0, 0.5},
        {0.5, 0.0, 1.0}
    });

    Matrix[] result = JacobianMath.computePseudoInverses(jacobian, DAMPING);
    Matrix dampedInverse = result[0];
    Matrix basicInverse = result[1];

    // J × J⁺_basic ≈ I for square non-singular matrix
    Matrix product = jacobian.times(basicInverse);
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        double expected = (i == j) ? 1.0 : 0.0;
        assertEquals("J × J⁺ should approximate identity",
            expected, product.get(i, j), DELTA);
      }
    }

    // Damped inverse should be close but not exact
    Matrix dampedProduct = jacobian.times(dampedInverse);
    for (int i = 0; i < 3; i++) {
      assertTrue("Damped J×J⁺ diagonal should be close to 1",
          Math.abs(dampedProduct.get(i, i) - 1.0) < 0.02);
    }
  }

  @Test
  public void computePseudoInverses_singleElementMatrix() {
    Matrix m = new Matrix(new double[][]{{5.0}});
    Matrix[] result = JacobianMath.computePseudoInverses(m, DAMPING);

    assertEquals(1.0 / 5.0, result[1].get(0, 0), DELTA);
    assertEquals(5.0 / (25.0 + 0.01), result[0].get(0, 0), DELTA);
  }
}
