package org.lgna.ik.core.enforcer;

import Jama.Matrix;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Jacobian {
  Matrix matrix;
  JacobianAxis[] columnIndexToJacobianColumn;
  private final IkEnforcerContext context;

  public Jacobian(int rowDimension, JacobianAxis[] inputColumns, IkEnforcerContext context) {
    matrix = new Matrix(rowDimension, inputColumns.length);
    columnIndexToJacobianColumn = Arrays.copyOf(inputColumns, inputColumns.length);
    this.context = context;
  }

  public Jacobian(Jacobian[] jacobians, IkEnforcerContext context) {
    this.context = context;

    Set<JacobianAxis> resultantJacobianAxisSet = new HashSet<JacobianAxis>();
    int resultantRowCount = 0;

    for (Jacobian jacobian : jacobians) {
      resultantRowCount += jacobian.getRowCount();
      Collections.addAll(resultantJacobianAxisSet, jacobian.columnIndexToJacobianColumn);
    }

    JacobianAxis[] resultantJacobianAxes = resultantJacobianAxisSet.toArray(new JacobianAxis[resultantJacobianAxisSet.size()]);
    columnIndexToJacobianColumn = resultantJacobianAxes;

    int resultantColumnCount = columnIndexToJacobianColumn.length;
    matrix = new Matrix(resultantRowCount, resultantColumnCount);

    for (int rowi = 0; rowi < resultantRowCount; ++rowi) {
      for (int coli = 0; coli < resultantColumnCount; ++coli) {
        matrix.set(rowi, coli, 0.0);
      }
    }

    int resultantRowOffset = 0;

    for (Jacobian jacobian : jacobians) {
      int rowCount = jacobian.getRowCount();

      for (int columnIndex = 0; columnIndex < resultantColumnCount; ++columnIndex) {

        JacobianAxis jacobianAxis = columnIndexToJacobianColumn[columnIndex];

        boolean found = false;

        for (int jci = 0; jci < jacobian.columnIndexToJacobianColumn.length; ++jci) {
          if (jacobian.columnIndexToJacobianColumn[jci] == jacobianAxis) {
            found = true;

            for (int rowi = 0; rowi < rowCount; ++rowi) {
              matrix.set(resultantRowOffset + rowi, columnIndex, jacobian.matrix.get(rowi, jci));
            }
          }
        }

        // if the jacobian has this column, copy it from it; else leave it zero
      }

      resultantRowOffset += rowCount;
    }
  }

  public Jacobian(Matrix mj, JacobianAxis[] jacobianAxes, IkEnforcerContext context) {
    matrix = mj;
    columnIndexToJacobianColumn = jacobianAxes;
    this.context = context;
  }

  public Displacement multiplyWithAngleDeltas(AngleDeltas angleDeltas) {
    double[] d = new double[matrix.getRowDimension()];

    for (int rowi = 0; rowi < matrix.getRowDimension(); ++rowi) {
      d[rowi] = 0.0;
      for (int coli = 0; coli < matrix.getColumnDimension(); ++coli) {
        JacobianAxis jacobianAxis = columnIndexToJacobianColumn[coli];
        int globalIndex = context.getGlobalIndexForAxis(jacobianAxis);
        d[rowi] += angleDeltas.getByGlobalIndex(globalIndex) * matrix.get(rowi, coli);
      }
    }

    return new Displacement(d);
  }

  public AngleDeltas multiplyDisplacementWithInverseForMoving(Displacement displacement) {
    Matrix inverseForMoving = createInverseMatrixForMoving();

    double[] compactResult = new double[matrix.getColumnDimension()];

    for (int rowi = 0; rowi < inverseForMoving.getRowDimension(); ++rowi) {
      double val = 0;
      for (int coli = 0; coli < inverseForMoving.getColumnDimension(); ++coli) {
        val += inverseForMoving.get(rowi, coli) * displacement.storage[coli];
      }
      compactResult[rowi] = val;
    }

    AngleDeltas result = new AngleDeltas(context.getIndexToAxis().size(), context);

    for (int i = 0; i < result.storage.length; ++i) {
      result.storage[i] = 0;
    }

    for (int ci = 0; ci < columnIndexToJacobianColumn.length; ++ci) {
      result.storage[context.getGlobalIndexForAxis(columnIndexToJacobianColumn[ci])] = compactResult[ci];
    }

    return result;
  }

  private Matrix createInverseMatrixForMoving() {
    prepareSvdInfoIfNecessary();

    return svdInfo.createDampedInverse();
  }

  public InvertedJacobian createInverseForNullProjection() {
    prepareSvdInfoIfNecessary();

    Matrix regularInverse = svdInfo.createRegularInverse();

    return new InvertedJacobian(regularInverse, this);
  }

  SvdInfo svdInfo;

  private void prepareSvdInfoIfNecessary() {
    if (svdInfo == null) {
      svdInfo = new SvdInfo(matrix);
    }
  }

  void matrixWasUpdated() {
    svdInfo = null;
  }

  public int getRowCount() {
    return matrix.getRowDimension();
  }
}
