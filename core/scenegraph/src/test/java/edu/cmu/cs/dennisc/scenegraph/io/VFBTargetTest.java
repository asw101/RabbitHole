package edu.cmu.cs.dennisc.scenegraph.io;

import edu.cmu.cs.dennisc.scenegraph.Vertex;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

public class VFBTargetTest {
  @Test
  public void loadSupportsVariableLengthFacesByIgnoringExtraIndices() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    writeIntLE(baos, 1);
    writeIntLE(baos, 3);
    for (int i = 0; i < 3; i++) {
      writeFloatLE(baos, i + 1);
      writeFloatLE(baos, 0);
      writeFloatLE(baos, 0);
      writeFloatLE(baos, 1);
      writeFloatLE(baos, 0);
      writeFloatLE(baos, 0);
      writeFloatLE(baos, 0);
      writeFloatLE(baos, 0);
    }
    writeIntLE(baos, 1);
    writeIntLE(baos, 4);
    writeIntLE(baos, 0);
    writeIntLE(baos, 4);
    writeIntLE(baos, 0);
    writeIntLE(baos, 1);
    writeIntLE(baos, 2);
    writeIntLE(baos, 99);

    Object[] loaded = VFB.load(new BufferedInputStream(new ByteArrayInputStream(baos.toByteArray())));

    Vertex[] vertices = (Vertex[]) loaded[0];
    int[] indices = (int[]) loaded[1];
    assertEquals(3, vertices.length);
    assertArrayEquals(new int[]{0, 1, 2}, indices);
  }

  @Test
  public void storeRoundtripPreservesLegacyAxisFlipAndIndexOrder() throws Exception {
    Vertex[] vertices = new Vertex[]{Vertex.createXYZIJKUV(1, 2, 3, 0.25f, 0.5f, 0.75f, 0.1f, 0.9f)};
    int[] indices = new int[]{0, 1, 2};

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    VFB.store(baos, vertices, indices);
    Object[] loaded = VFB.load(new BufferedInputStream(new ByteArrayInputStream(baos.toByteArray())));

    Vertex decoded = ((Vertex[]) loaded[0])[0];
    assertEquals(-1.0, decoded.position.x(), 0.0001);
    assertEquals(-0.25f, decoded.normal.x(), 0.0001);
    assertArrayEquals(new int[]{2, 1, 0}, (int[]) loaded[1]);
  }

  private static void writeIntLE(ByteArrayOutputStream baos, int value) {
    baos.write(value & 0xFF);
    baos.write((value >> 8) & 0xFF);
    baos.write((value >> 16) & 0xFF);
    baos.write((value >> 24) & 0xFF);
  }

  private static void writeFloatLE(ByteArrayOutputStream baos, float value) {
    writeIntLE(baos, Float.floatToIntBits(value));
  }
}
