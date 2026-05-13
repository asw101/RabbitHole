package org.lgna.ik.core.enforcer;

import java.util.Arrays;

class NullspaceProjector {
  double[][] matrix;
  private final int numAxes;
  private final IkEnforcerContext context;

  public NullspaceProjector(int numAxes, IkEnforcerContext context) {
    this.numAxes = numAxes;
    this.context = context;
    matrix = new double[numAxes][numAxes];
  }

  public void initializeToIdentity() {
    for (int row = 0; row < numAxes; ++row) {
      Arrays.fill(matrix[row], 0.0);
      matrix[row][row] = 1.0;
    }
  }

  public Jacobian createProjected(Jacobian input) {
    Jacobian result = new Jacobian(input.matrix.getRowDimension(), input.columnIndexToJacobianColumn, context);

    int colDim = result.matrix.getColumnDimension();
    int rowDim = result.matrix.getRowDimension();

    int[] localToGlobal = new int[colDim];
    for (int ci = 0; ci < colDim; ++ci) {
      localToGlobal[ci] = context.getGlobalIndexForAxis(result.columnIndexToJacobianColumn[ci]);
    }

    for (int outRowi = 0; outRowi < rowDim; ++outRowi) {
      for (int outColi = 0; outColi < colDim; ++outColi) {

        int outColGlobali = localToGlobal[outColi];

        double val = 0;
        for (int ci = 0; ci < colDim; ++ci) {
          val += input.matrix.get(outRowi, ci) * matrix[localToGlobal[ci]][outColGlobali];
        }
        result.matrix.set(outRowi, outColi, val);
      }
    }

    result.matrixWasUpdated();

    return result;
  }

  public void subtractInverseTimesJacobian(InvertedJacobian inverse, Jacobian jacobian) {
    assert inverse.straight == jacobian;

    int localDimension = jacobian.matrix.getColumnDimension();
    int jacobianRows = jacobian.matrix.getRowDimension();

    for (int rowi = 0; rowi < localDimension; ++rowi) {
      for (int coli = 0; coli < localDimension; ++coli) {

        double val = 0;
        for (int ri = 0; ri < jacobianRows; ++ri) {
          val += inverse.matrix.get(rowi, ri) * jacobian.matrix.get(ri, coli);
        }

        matrix[rowi][coli] -= val;
      }
    }
  }

  public void setIndexToLocked(int index) {
    for (int i = 0; i < matrix.length; ++i) {
      if (i == index) {
        matrix[i][i] = 1;
      } else {
        matrix[i][i] = 0;
      }
    }
  }
}
