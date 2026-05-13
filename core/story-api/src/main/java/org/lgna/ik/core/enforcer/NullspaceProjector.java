package org.lgna.ik.core.enforcer;

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
      for (int col = 0; col < numAxes; ++col) {
        matrix[row][col] = 0.0;
      }
      matrix[row][row] = 1.0;
    }
  }

  public Jacobian createProjected(Jacobian input) {
    Jacobian result = new Jacobian(input.matrix.getRowDimension(), input.columnIndexToJacobianColumn, context);

    int[] localToGlobal = new int[result.matrix.getColumnDimension()];

    for (int ci = 0; ci < result.matrix.getColumnDimension(); ++ci) {
      localToGlobal[ci] = context.getGlobalIndexForAxis(result.columnIndexToJacobianColumn[ci]);
    }

    for (int outRowi = 0; outRowi < result.matrix.getRowDimension(); ++outRowi) {
      for (int outColi = 0; outColi < result.matrix.getColumnDimension(); ++outColi) {

        int outColGlobali = localToGlobal[outColi];

        double val = 0;
        for (int ci = 0; ci < result.matrix.getColumnDimension(); ++ci) {
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

    int[] localToGlobal = new int[jacobian.matrix.getColumnDimension()];

    for (int ci = 0; ci < jacobian.matrix.getColumnDimension(); ++ci) {
      localToGlobal[ci] = context.getGlobalIndexForAxis(jacobian.columnIndexToJacobianColumn[ci]);
    }

    int localDimension = jacobian.matrix.getColumnDimension();

    for (int rowi = 0; rowi < localDimension; ++rowi) {
      for (int coli = 0; coli < localDimension; ++coli) {

        double val = 0;
        for (int ri = 0; ri < jacobian.matrix.getRowDimension(); ++ri) {
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
