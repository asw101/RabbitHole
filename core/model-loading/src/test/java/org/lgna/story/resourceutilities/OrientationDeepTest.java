package org.lgna.story.resourceutilities;

import edu.cmu.cs.dennisc.property.DoubleBufferProperty;
import edu.cmu.cs.dennisc.property.FloatBufferProperty;
import edu.cmu.cs.dennisc.scenegraph.WeightedMesh;
import org.alice.math.immutable.AffineMatrix4x4;
import org.junit.Test;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class OrientationDeepTest {
  private static final double EPSILON = 0.000001;

  @Test
  public void unknownUpAxisMatchesAliceForVerticesAndTranslation() {
    float[] sourceVertices = new float[]{1f, 2f, 3f, -4f, 5f, -6f};
    DoubleBufferProperty aliceDestination = new DoubleBufferProperty(new WeightedMesh(), (DoubleBuffer) null);
    DoubleBufferProperty fallbackDestination = new DoubleBufferProperty(new WeightedMesh(), (DoubleBuffer) null);

    double[] aliceVertices = Orientation.forAlice().orientVertices(sourceVertices, aliceDestination);
    double[] fallbackVertices = Orientation.forUpAxis("Y_UP").orientVertices(sourceVertices, fallbackDestination);

    assertArrayEquals(new double[]{-1.0, 2.0, -3.0, 4.0, 5.0, 6.0}, aliceVertices, EPSILON);
    assertArrayEquals(aliceVertices, fallbackVertices, EPSILON);
    assertArrayEquals(aliceVertices, read(aliceDestination.getValue()), EPSILON);
    assertArrayEquals(fallbackVertices, read(fallbackDestination.getValue()), EPSILON);

    AffineMatrix4x4 oriented = Orientation.forAlice().orientMatrixToAlice(AffineMatrix4x4.createTranslation(1, 2, 3));
    assertEquals(-1.0, oriented.translation().x(), EPSILON);
    assertEquals(2.0, oriented.translation().y(), EPSILON);
    assertEquals(-3.0, oriented.translation().z(), EPSILON);
  }

  @Test
  public void xUpAndZUpProduceDistinctVertexAndNormalTransforms() {
    float[] source = new float[]{1f, 2f, 3f};
    DoubleBufferProperty xVerticesDestination = new DoubleBufferProperty(new WeightedMesh(), (DoubleBuffer) null);
    DoubleBufferProperty zVerticesDestination = new DoubleBufferProperty(new WeightedMesh(), (DoubleBuffer) null);

    double[] xVertices = Orientation.forUpAxis("X_UP").orientVertices(source, xVerticesDestination);
    double[] zVertices = Orientation.forUpAxis("Z_UP").orientVertices(source, zVerticesDestination);

    assertEquals(3, xVertices.length);
    assertEquals(3, zVertices.length);
    assertFalse(Arrays.equals(xVertices, zVertices));
    assertEquals(14.0, magnitudeSquared(xVertices), EPSILON);
    assertEquals(14.0, magnitudeSquared(zVertices), EPSILON);
    assertArrayEquals(xVertices, read(xVerticesDestination.getValue()), EPSILON);
    assertArrayEquals(zVertices, read(zVerticesDestination.getValue()), EPSILON);

    FloatBufferProperty xNormalsDestination = new FloatBufferProperty(new WeightedMesh(), (FloatBuffer) null);
    FloatBufferProperty zNormalsDestination = new FloatBufferProperty(new WeightedMesh(), (FloatBuffer) null);

    Orientation.forUpAxis("X_UP").orientNormals(source, xNormalsDestination);
    Orientation.forUpAxis("Z_UP").orientNormals(source, zNormalsDestination);

    float[] xNormals = read(xNormalsDestination.getValue());
    float[] zNormals = read(zNormalsDestination.getValue());
    assertFalse(Arrays.equals(xNormals, zNormals));
    assertEquals(14.0f, magnitudeSquared(xNormals), 0.000001f);
    assertEquals(14.0f, magnitudeSquared(zNormals), 0.000001f);
  }

  private static double[] read(DoubleBuffer buffer) {
    double[] values = new double[buffer.remaining()];
    for (int i = 0; i < values.length; i++) {
      values[i] = buffer.get(i);
    }
    return values;
  }

  private static float[] read(FloatBuffer buffer) {
    float[] values = new float[buffer.remaining()];
    for (int i = 0; i < values.length; i++) {
      values[i] = buffer.get(i);
    }
    return values;
  }

  private static double magnitudeSquared(double[] values) {
    double sum = 0.0;
    for (double value : values) {
      sum += value * value;
    }
    return sum;
  }

  private static float magnitudeSquared(float[] values) {
    float sum = 0.0f;
    for (float value : values) {
      sum += value * value;
    }
    return sum;
  }
}
