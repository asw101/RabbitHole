package edu.cmu.cs.dennisc.scenegraph.io;

import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.scenegraph.Vertex;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BinaryArrayDecoderBehaviorTest {
  @Test
  public void decodeIntArrayVersionOnePreservesFaceOrder() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(1);
    dos.writeInt(2);
    dos.writeInt(3);
    dos.writeInt(0);
    dos.writeInt(1);
    dos.writeInt(2);
    dos.writeInt(3);
    dos.writeInt(4);
    dos.writeInt(5);

    int[] decoded = BinaryArrayDecoder.decodeIntArray(new ByteArrayInputStream(baos.toByteArray()));

    assertArrayEquals(new int[] {0, 1, 2, 3, 4, 5}, decoded);
  }

  @Test
  public void decodeIntArrayVersionTwoSwapsTriangleWindingPerFace() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(2);
    dos.writeInt(6);
    dos.writeInt(0);
    dos.writeInt(1);
    dos.writeInt(2);
    dos.writeInt(3);
    dos.writeInt(4);
    dos.writeInt(5);

    int[] decoded = BinaryArrayDecoder.decodeIntArray(new ByteArrayInputStream(baos.toByteArray()));

    assertArrayEquals(new int[] {2, 1, 0, 5, 4, 3}, decoded);
  }

  @Test
  public void decodeDoubleArrayVersionTwoReadsExactValues() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(2);
    dos.writeInt(4);
    dos.writeDouble(Math.PI);
    dos.writeDouble(Math.E);
    dos.writeDouble(-0.125);
    dos.writeDouble(Double.POSITIVE_INFINITY);

    double[] decoded = BinaryArrayDecoder.decodeDoubleArray(new ByteArrayInputStream(baos.toByteArray()));

    assertArrayEquals(new double[] {Math.PI, Math.E, -0.125, Double.POSITIVE_INFINITY}, decoded, 0.0);
  }

  @Test
  public void decodeDoubleArrayVersionOneReturnsNullBecauseNoFormatExisted() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(1);

    assertNull(BinaryArrayDecoder.decodeDoubleArray(new ByteArrayInputStream(baos.toByteArray())));
  }

  @Test
  public void decodeVertexArrayVersionThreeReadsFloatBackedAttributes() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(3);
    dos.writeInt(1);
    int format = Vertex.FORMAT_POSITION
        | Vertex.FORMAT_NORMAL
        | Vertex.FORMAT_DIFFUSE_COLOR
        | Vertex.FORMAT_SPECULAR_HIGHLIGHT_COLOR
        | Vertex.FORMAT_TEXTURE_COORDINATE_0;
    dos.writeInt(format);
    dos.writeDouble(1.0);
    dos.writeDouble(2.0);
    dos.writeDouble(3.0);
    dos.writeFloat(0.0f);
    dos.writeFloat(1.0f);
    dos.writeFloat(0.0f);
    dos.writeFloat(0.1f);
    dos.writeFloat(0.2f);
    dos.writeFloat(0.3f);
    dos.writeFloat(0.4f);
    dos.writeFloat(0.5f);
    dos.writeFloat(0.6f);
    dos.writeFloat(0.7f);
    dos.writeFloat(0.8f);
    dos.writeFloat(0.25f);
    dos.writeFloat(0.75f);

    Vertex[] decoded = BinaryArrayDecoder.decodeVertexArray(new ByteArrayInputStream(baos.toByteArray()));

    assertEquals(1, decoded.length);
    assertEquals(1.0, decoded[0].position.x(), 0.0);
    assertEquals(2.0, decoded[0].position.y(), 0.0);
    assertEquals(3.0, decoded[0].position.z(), 0.0);
    assertEquals(0.0f, decoded[0].normal.x(), 0.0f);
    assertEquals(1.0f, decoded[0].normal.y(), 0.0f);
    assertEquals(0.1f, decoded[0].diffuseColor.red, 0.0f);
    assertEquals(new Color4f(0.5f, 0.6f, 0.7f, 0.8f), decoded[0].specularHighlightColor);
    assertEquals(0.25f, decoded[0].textureCoordinate0.u, 0.0f);
    assertEquals(0.75f, decoded[0].textureCoordinate0.v, 0.0f);
  }
}
