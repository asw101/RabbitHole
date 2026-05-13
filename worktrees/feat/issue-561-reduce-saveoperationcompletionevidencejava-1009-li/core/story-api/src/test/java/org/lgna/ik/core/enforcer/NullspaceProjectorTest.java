package org.lgna.ik.core.enforcer;

import Jama.Matrix;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Characterization tests for NullspaceProjector — a square matrix implementing
 * nullspace projection for priority-based IK. Used in the priority loop to
 * project lower-priority Jacobians onto the nullspace of higher-priority ones.
 *
 * Context-dependent: createProjected() and subtractInverseTimesJacobian() use
 * getGlobalIndexForAxis() to map local Jacobian column indices to global indices.
 */
public class NullspaceProjectorTest {

  private static final double DELTA = 1e-6;

  private IkEnforcerContextStub context;
  private List<JacobianAxis> axes;

  @Before
  public void setUp() {
    context = new IkEnforcerContextStub(3);
    axes = context.getIndexToAxis();
  }

  @Test
  public void initializeToIdentityCreatesIdentityMatrix() {
    NullspaceProjector nsp = new NullspaceProjector(3, context);
    nsp.initializeToIdentity();

    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        double expected = (r == c) ? 1.0 : 0.0;
        assertEquals("matrix[" + r + "][" + c + "]", expected, nsp.matrix[r][c], DELTA);
      }
    }
  }

  @Test
  public void createProjectedWithIdentityPreservesJacobian() {
    NullspaceProjector nsp = new NullspaceProjector(3, context);
    nsp.initializeToIdentity();

    Matrix m = new Matrix(new double[][]{
        {1.0, 2.0, 3.0},
        {4.0, 5.0, 6.0}
    });
    JacobianAxis[] cols = {axes.get(0), axes.get(1), axes.get(2)};
    Jacobian input = new Jacobian(m, cols, context);

    Jacobian projected = nsp.createProjected(input);

    assertEquals(2, projected.getRowCount());
    assertEquals(3, projected.matrix.getColumnDimension());

    for (int r = 0; r < 2; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals("projected[" + r + "][" + c + "]",
            m.get(r, c), projected.matrix.get(r, c), DELTA);
      }
    }
  }

  @Test
  public void createProjectedWithZeroProjectorZerosJacobian() {
    NullspaceProjector nsp = new NullspaceProjector(3, context);
    // Leave all zeros (default)

    Matrix m = new Matrix(new double[][]{{1.0, 2.0}, {3.0, 4.0}});
    JacobianAxis[] cols = {axes.get(0), axes.get(1)};
    Jacobian input = new Jacobian(m, cols, context);

    Jacobian projected = nsp.createProjected(input);

    for (int r = 0; r < 2; r++) {
      for (int c = 0; c < 2; c++) {
        assertEquals(0.0, projected.matrix.get(r, c), DELTA);
      }
    }
  }

  @Test
  public void subtractInverseTimesJacobianReducesProjector() {
    NullspaceProjector nsp = new NullspaceProjector(3, context);
    nsp.initializeToIdentity();

    // For identity Jacobian, J⁻¹ × J = I, so P = I - I = 0
    Matrix m = Matrix.identity(3, 3);
    JacobianAxis[] cols = {axes.get(0), axes.get(1), axes.get(2)};
    Jacobian j = new Jacobian(m, cols, context);
    InvertedJacobian inv = j.createInverseForNullProjection();

    nsp.subtractInverseTimesJacobian(inv, j);

    // Should be approximately zero matrix
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 3; c++) {
        assertEquals("matrix[" + r + "][" + c + "]", 0.0, nsp.matrix[r][c], DELTA);
      }
    }
  }


  public void projectedJacobianInvalidatesSvdCache() {
    NullspaceProjector nsp = new NullspaceProjector(3, context);
    nsp.initializeToIdentity();

    Matrix m = Matrix.identity(3, 3);
    JacobianAxis[] cols = {axes.get(0), axes.get(1), axes.get(2)};
    Jacobian input = new Jacobian(m, cols, context);

    Jacobian projected = nsp.createProjected(input);
    // matrixWasUpdated() is called internally, so svdInfo should be null
    assertNull(projected.svdInfo);
  }

  @Test
  public void subtractWithPartialJacobianAffectsOnlyRelevantIndices() {
    NullspaceProjector nsp = new NullspaceProjector(3, context);
    nsp.initializeToIdentity();

    // Jacobian only uses axes 0 and 1 (not 2)
    Matrix m = Matrix.identity(2, 2);
    JacobianAxis[] cols = {axes.get(0), axes.get(1)};
    Jacobian j = new Jacobian(m, cols, context);
    InvertedJacobian inv = j.createInverseForNullProjection();

    nsp.subtractInverseTimesJacobian(inv, j);

    // Index 2 should remain untouched (still identity diagonal)
    assertEquals(1.0, nsp.matrix[2][2], DELTA);
    // Indices 0,1 should be affected (reduced toward zero)
    assertTrue(Math.abs(nsp.matrix[0][0]) < 0.5);
    assertTrue(Math.abs(nsp.matrix[1][1]) < 0.5);
  }
}
