package org.lgna.ik.core.enforcer;

import Jama.Matrix;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
    // Matrix(int, int) zero-initializes — no explicit zeroing needed
    matrix = new Matrix(resultantRowCount, resultantColumnCount);

    int resultantRowOffset = 0;

    for (Jacobian jacobian : jacobians) {
      int rowCount = jacobian.getRowCount();

      // Pre-build axis→column map for O(1) lookup instead of O(n) linear scan
      Map<JacobianAxis, Integer> axisToCol = new HashMap<>();
      for (int jci = 0; jci < jacobian.columnIndexToJacobianColumn.length; ++jci) {
        axisToCol.put(jacobian.columnIndexToJacobianColumn[jci], jci);
      }

      for (int columnIndex = 0; columnIndex < resultantColumnCount; ++columnIndex) {
        Integer jci = axisToCol.get(columnIndexToJacobianColumn[columnIndex]);
        if (jci != null) {
          for (int rowi = 0; rowi < rowCount; ++rowi) {
            matrix.set(resultantRowOffset + rowi, columnIndex, jacobian.matrix.get(rowi, jci));
          }
        }
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
    int rows = matrix.getRowDimension();
    int cols = matrix.getColumnDimension();

    // Pre-compute global indices to avoid per-row HashMap lookups
    int[] globalIndices = new int[cols];
    for (int coli = 0; coli < cols; ++coli) {
      globalIndices[coli] = context.getGlobalIndexForAxis(columnIndexToJacobianColumn[coli]);
    }

    double[] d = new double[rows];
    for (int rowi = 0; rowi < rows; ++rowi) {
      double sum = 0;
      for (int coli = 0; coli < cols; ++coli) {
        sum += angleDeltas.getByGlobalIndex(globalIndices[coli]) * matrix.get(rowi, coli);
      }
      d[rowi] = sum;
    }

    return new Displacement(d);
  }

  public AngleDeltas multiplyDisplacementWithInverseForMoving(Displacement displacement) {
    Matrix inverseForMoving = createInverseMatrixForMoving();

    int invRows = inverseForMoving.getRowDimension();
    int invCols = inverseForMoving.getColumnDimension();
    double[] compactResult = new double[matrix.getColumnDimension()];

    for (int rowi = 0; rowi < invRows; ++rowi) {
      double sum = 0;
      for (int coli = 0; coli < invCols; ++coli) {
        sum += inverseForMoving.get(rowi, coli) * displacement.storage[coli];
      }
      compactResult[rowi] = sum;
    }

    // Java zero-initializes arrays — no explicit zeroing needed
    AngleDeltas result = new AngleDeltas(context.getIndexToAxis().size(), context);

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
