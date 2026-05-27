package Jama;

import org.junit.Test;

import static Jama.MatrixTestSupport.TOLERANCE;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class MatrixConstructionAndAccessTest {
  @Test
  public void constructorsAndArrayViewsCoverCopyAndReferenceSemantics() {
    Matrix zeros = new Matrix(2, 3);
    assertEquals(2, zeros.getRowDimension());
    assertEquals(3, zeros.getColumnDimension());
    assertEquals(0.0, zeros.get(1, 2), 0.0);

    Matrix constant = new Matrix(2, 2, 2.5);
    assertEquals(2.5, constant.get(0, 0), 0.0);
    assertEquals(2.5, constant.get(1, 1), 0.0);

    double[][] backing = new double[][]{{1.0, 2.0}, {3.0, 4.0}};
    Matrix shared = new Matrix(backing);
    backing[0][1] = 9.0;
    assertEquals(9.0, shared.get(0, 1), 0.0);

    double[][] quickBacking = new double[][]{{5.0}, {6.0}};
    Matrix quick = new Matrix(quickBacking, 2, 1);
    quickBacking[1][0] = 7.5;
    assertEquals(7.5, quick.get(1, 0), 0.0);

    Matrix packed = new Matrix(new double[]{1.0, 2.0, 3.0, 4.0}, 2);
    assertArrayEquals(new double[]{1.0, 2.0, 3.0, 4.0}, packed.getColumnPackedCopy(), 0.0);
    assertArrayEquals(new double[]{1.0, 3.0, 2.0, 4.0}, packed.getRowPackedCopy(), 0.0);

    double[][] source = new double[][]{{8.0, 9.0}, {10.0, 11.0}};
    Matrix copied = Matrix.constructWithCopy(source);
    source[0][0] = -1.0;
    assertEquals(8.0, copied.get(0, 0), 0.0);

    Matrix copy = shared.copy();
    Matrix clone = (Matrix) shared.clone();
    assertNotSame(shared, copy);
    assertNotSame(shared, clone);
    shared.set(1, 0, 12.0);
    assertEquals(3.0, copy.get(1, 0), 0.0);
    assertEquals(3.0, clone.get(1, 0), 0.0);

    double[][] arrayCopy = shared.getArrayCopy();
    arrayCopy[0][0] = -99.0;
    assertEquals(1.0, shared.get(0, 0), 0.0);

    double[][] array = shared.getArray();
    assertSame(array, shared.getArray());
    array[0][0] = 14.0;
    assertEquals(14.0, shared.get(0, 0), 0.0);
  }

  @Test
  public void invalidConstructionInputsThrowHelpfulExceptions() {
    try {
      new Matrix(new double[][]{{1.0}, {2.0, 3.0}});
      fail("ragged arrays should be rejected");
    } catch (IllegalArgumentException expected) {
      assertEquals("All rows must have the same length.", expected.getMessage());
    }

    try {
      Matrix.constructWithCopy(new double[][]{{1.0}, {2.0, 3.0}});
      fail("ragged arrays should be rejected when copied");
    } catch (IllegalArgumentException expected) {
      assertEquals("All rows must have the same length.", expected.getMessage());
    }

    try {
      new Matrix(new double[]{1.0, 2.0, 3.0}, 2);
      fail("packed arrays must evenly divide by row count");
    } catch (IllegalArgumentException expected) {
      assertEquals("Array length must be a multiple of m.", expected.getMessage());
    }

    Matrix emptyRows = new Matrix(new double[0], 0);
    assertEquals(0, emptyRows.getRowDimension());
    assertEquals(0, emptyRows.getColumnDimension());
    assertArrayEquals(new double[0], emptyRows.getColumnPackedCopy(), TOLERANCE);
  }
}
