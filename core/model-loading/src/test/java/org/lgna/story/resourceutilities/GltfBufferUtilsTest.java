package org.lgna.story.resourceutilities;

import org.junit.Test;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import static org.junit.Assert.*;

public class GltfBufferUtilsTest {

  @Test
  public void normalize_unitVector_remainsUnit() {
    FloatBuffer buf = FloatBuffer.wrap(new float[]{1.0f, 0.0f, 0.0f});
    GltfBufferUtils.normalize(buf);
    assertEquals(1.0f, buf.get(0), 1e-6f);
    assertEquals(0.0f, buf.get(1), 1e-6f);
    assertEquals(0.0f, buf.get(2), 1e-6f);
  }

  @Test
  public void normalize_arbitraryVector_becomesUnitLength() {
    FloatBuffer buf = FloatBuffer.wrap(new float[]{3.0f, 4.0f, 0.0f});
    GltfBufferUtils.normalize(buf);
    float x = buf.get(0), y = buf.get(1), z = buf.get(2);
    double length = Math.sqrt(x * x + y * y + z * z);
    assertEquals(1.0, length, 1e-5);
    assertEquals(0.6f, x, 1e-5f);
    assertEquals(0.8f, y, 1e-5f);
  }

  @Test
  public void normalize_zeroVector_becomesDefaultXAxis() {
    FloatBuffer buf = FloatBuffer.wrap(new float[]{0.0f, 0.0f, 0.0f});
    GltfBufferUtils.normalize(buf);
    assertEquals(1.0f, buf.get(0), 1e-6f);
    assertEquals(0.0f, buf.get(1), 1e-6f);
    assertEquals(0.0f, buf.get(2), 1e-6f);
  }

  @Test
  public void normalize_nanVector_becomesDefaultXAxis() {
    FloatBuffer buf = FloatBuffer.wrap(new float[]{Float.NaN, Float.NaN, Float.NaN});
    GltfBufferUtils.normalize(buf);
    assertEquals(1.0f, buf.get(0), 1e-6f);
    assertEquals(0.0f, buf.get(1), 1e-6f);
    assertEquals(0.0f, buf.get(2), 1e-6f);
  }

  @Test
  public void normalize_multipleNormals() {
    FloatBuffer buf = FloatBuffer.wrap(new float[]{
        0.0f, 0.0f, 0.0f,   // zero-length → (1,0,0)
        0.0f, 5.0f, 0.0f    // arbitrary → (0,1,0)
    });
    GltfBufferUtils.normalize(buf);
    // First normal: default
    assertEquals(1.0f, buf.get(0), 1e-6f);
    assertEquals(0.0f, buf.get(1), 1e-6f);
    assertEquals(0.0f, buf.get(2), 1e-6f);
    // Second normal: normalized
    assertEquals(0.0f, buf.get(3), 1e-6f);
    assertEquals(1.0f, buf.get(4), 1e-5f);
    assertEquals(0.0f, buf.get(5), 1e-6f);
  }

  @Test
  public void normalize_alreadyNormalized_unchangedWithinTolerance() {
    float inv = (float)(1.0 / Math.sqrt(3.0));
    FloatBuffer buf = FloatBuffer.wrap(new float[]{inv, inv, inv});
    GltfBufferUtils.normalize(buf);
    double length = Math.sqrt(buf.get(0) * buf.get(0) + buf.get(1) * buf.get(1) + buf.get(2) * buf.get(2));
    assertEquals(1.0, length, 1e-5);
  }

  @Test
  public void convertToFloatArray_nullBuffer_returnsEmptyArray() {
    float[] result = GltfBufferUtils.convertToFloatArray(null);
    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  public void convertToFloatArray_threeDoubles_convertsToFloats() {
    DoubleBuffer buf = DoubleBuffer.wrap(new double[]{1.5, 2.5, 3.5});
    float[] result = GltfBufferUtils.convertToFloatArray(buf);
    assertEquals(3, result.length);
    assertEquals(1.5f, result[0], 1e-6f);
    assertEquals(2.5f, result[1], 1e-6f);
    assertEquals(3.5f, result[2], 1e-6f);
  }

  @Test
  public void convertToFloatArray_sixDoubles_convertsTwoTriples() {
    DoubleBuffer buf = DoubleBuffer.wrap(new double[]{1.0, 2.0, 3.0, 4.0, 5.0, 6.0});
    float[] result = GltfBufferUtils.convertToFloatArray(buf);
    assertEquals(6, result.length);
    assertEquals(1.0f, result[0], 1e-6f);
    assertEquals(6.0f, result[5], 1e-6f);
  }

  @Test
  public void convertToFloatArray_rewindsBuffer() {
    DoubleBuffer buf = DoubleBuffer.wrap(new double[]{1.0, 2.0, 3.0});
    GltfBufferUtils.convertToFloatArray(buf);
    assertEquals(0, buf.position());
  }
}
