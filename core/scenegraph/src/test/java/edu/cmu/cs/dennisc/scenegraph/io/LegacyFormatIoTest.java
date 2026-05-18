package edu.cmu.cs.dennisc.scenegraph.io;

import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.scenegraph.IndexedTriangleArray;
import edu.cmu.cs.dennisc.scenegraph.Vertex;
import edu.cmu.cs.dennisc.texture.TextureCoordinate2f;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3f;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.IntBuffer;

import static org.junit.Assert.*;

public class LegacyFormatIoTest {
  private static final double EPSILON = 0.000001;

  @Test
  public void binLoadVerticesSupportsAllVersionsAndStoreRoundtrip() throws Exception {
    Vertex[] version1 = BIN.loadVertices(new ByteArrayInputStream(createBinVertexVersion1Bytes()));
    assertEquals(1, version1.length);
    assertEquals(1.0, version1[0].position.x(), EPSILON);
    assertEquals(0.75f, version1[0].textureCoordinate0.v, EPSILON);

    Vertex[] version2 = BIN.loadVertices(new ByteArrayInputStream(createBinVertexVersion2Bytes()));
    assertEquals(1, version2.length);
    assertEquals(4.0, version2[0].position.x(), EPSILON);
    assertEquals(new Color4f(0.1f, 0.2f, 0.3f, 0.4f), version2[0].diffuseColor);

    Vertex original = new Vertex(new Point3(7, 8, 9), new Vector3f(0, 1, 0), new Color4f(0.5f, 0.6f, 0.7f, 0.8f), new Color4f(0.2f, 0.3f, 0.4f, 0.5f), new TextureCoordinate2f(0.25f, 0.75f));
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    BIN.storeVertices(new Vertex[]{original}, baos);
    Vertex[] version3 = BIN.loadVertices(new ByteArrayInputStream(baos.toByteArray()));
    assertEquals(1, version3.length);
    assertEquals(original, version3[0]);
  }

  @Test(expected = RuntimeException.class)
  public void binLoadVerticesRejectsInvalidVersion() throws Exception {
    BIN.loadVertices(new ByteArrayInputStream(createIntOnlyStream(99, 0).toByteArray()));
  }

  @Test
  public void binTriangleDataSupportsLegacyAndCurrentFormats() throws Exception {
    int[] version1 = BIN.loadTriangleData(new ByteArrayInputStream(createBinTriangleVersion1Bytes()));
    assertArrayEquals(new int[]{0, 1, 2, 2, 3, 0}, version1);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    BIN.storeTriangleData(new int[]{9, 8, 7, 6, 5, 4}, baos);
    int[] version2 = BIN.loadTriangleData(new ByteArrayInputStream(baos.toByteArray()));
    assertArrayEquals(new int[]{9, 8, 7, 6, 5, 4}, version2);
  }

  @Test(expected = RuntimeException.class)
  public void binTriangleDataRejectsInvalidVersion() throws Exception {
    BIN.loadTriangleData(new ByteArrayInputStream(createIntOnlyStream(77, 0).toByteArray()));
  }

  @Test
  public void binaryArrayDecoderSupportsVersionsAndValidation() throws Exception {
    Vertex[] version1 = BinaryArrayDecoder.decodeVertexArray(new ByteArrayInputStream(createBinaryArrayVertexVersion1Bytes()));
    assertEquals(1.0, version1[0].position.x(), EPSILON);

    Vertex[] version2 = BinaryArrayDecoder.decodeVertexArray(new ByteArrayInputStream(createBinaryArrayVertexVersion2Bytes()));
    assertEquals(4.0, version2[0].position.x(), EPSILON);
    assertEquals(0.6f, version2[0].textureCoordinate0.v, EPSILON);

    ByteArrayOutputStream version3Bytes = new ByteArrayOutputStream();
    ASG.encodeVertexArrayInBinary(new Vertex[]{new Vertex(new Point3(10, 11, 12), new Vector3f(1, 0, 0), new Color4f(0.3f, 0.4f, 0.5f, 1.0f), new Color4f(0.6f, 0.7f, 0.8f, 1.0f), new TextureCoordinate2f(0.2f, 0.9f))}, version3Bytes);
    Vertex[] version3 = BinaryArrayDecoder.decodeVertexArray(new ByteArrayInputStream(version3Bytes.toByteArray()));
    assertEquals(10.0, version3[0].position.x(), EPSILON);
    assertNotNull(version3[0].specularHighlightColor);
    assertFalse(version3[0].specularHighlightColor.isNaN());

    int[] intsV1 = BinaryArrayDecoder.decodeIntArray(new ByteArrayInputStream(createIntArrayVersion1Bytes()));
    assertArrayEquals(new int[]{1, 2, 3, 4, 5, 6}, intsV1);

    ByteArrayOutputStream intsV2Bytes = new ByteArrayOutputStream();
    ASG.encodeIntArrayInBinary(new int[]{1, 2, 3, 4, 5, 6}, intsV2Bytes);
    int[] intsV2 = BinaryArrayDecoder.decodeIntArray(new ByteArrayInputStream(intsV2Bytes.toByteArray()));
    assertArrayEquals(new int[]{3, 2, 1, 6, 5, 4}, intsV2);

    ByteArrayOutputStream doublesBytes = new ByteArrayOutputStream();
    ASG.encodeDoubleArrayInBinary(new double[]{1.25, 2.5, 3.75}, doublesBytes);
    double[] doubles = BinaryArrayDecoder.decodeDoubleArray(new ByteArrayInputStream(doublesBytes.toByteArray()));
    assertArrayEquals(new double[]{1.25, 2.5, 3.75}, doubles, EPSILON);
  }

  @Test(expected = RuntimeException.class)
  public void binaryArrayDecoderRejectsInvalidVertexVersion() throws Exception {
    BinaryArrayDecoder.decodeVertexArray(new ByteArrayInputStream(createIntOnlyStream(1234, 0).toByteArray()));
  }

  @Test(expected = RuntimeException.class)
  public void binaryArrayDecoderRejectsInvalidIntArrayVersion() throws Exception {
    BinaryArrayDecoder.decodeIntArray(new ByteArrayInputStream(createIntOnlyStream(1234, 0).toByteArray()));
  }

  @Test(expected = RuntimeException.class)
  public void binaryArrayDecoderRejectsInvalidDoubleArrayVersion() throws Exception {
    BinaryArrayDecoder.decodeDoubleArray(new ByteArrayInputStream(createIntOnlyStream(1234, 0).toByteArray()));
  }

  @Test
  public void vfbStoreAndLoadPreserveLegacyConventions() throws Exception {
    Vertex[] vertices = new Vertex[]{Vertex.createXYZIJKUV(1, 2, 3, 0.5f, 0.25f, -0.75f, 0.1f, 0.9f)};
    int[] indices = new int[]{0, 1, 2};

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    VFB.store(baos, vertices, indices);

    Object[] loaded = VFB.load(new java.io.BufferedInputStream(new ByteArrayInputStream(baos.toByteArray())));
    Vertex[] decodedVertices = (Vertex[]) loaded[0];
    int[] decodedIndices = (int[]) loaded[1];
    assertEquals(-1.0, decodedVertices[0].position.x(), EPSILON);
    assertEquals(2.0, decodedVertices[0].position.y(), EPSILON);
    assertEquals(-0.5f, decodedVertices[0].normal.x(), EPSILON);
    assertArrayEquals(new int[]{2, 1, 0}, decodedIndices);

    Vertex[] delegatedVertices = VFB.loadVertices(new ByteArrayInputStream(baos.toByteArray()));
    int[] delegatedIndices = VFB.loadIndices(new ByteArrayInputStream(baos.toByteArray()));
    assertEquals(decodedVertices[0], delegatedVertices[0]);
    assertArrayEquals(decodedIndices, delegatedIndices);
  }

  @Test
  public void vfbLoadReturnsNullPayloadsForUnknownVersion() throws Exception {
    Object[] loaded = VFB.load(new java.io.BufferedInputStream(new ByteArrayInputStream(createIntOnlyStream(2).toByteArray())));
    assertNull(loaded[0]);
    assertNull(loaded[1]);
  }

  @Test
  public void objDecodeSupportsTriplesPairsNegativeIndicesAndExponents() throws Exception {
    String triplesObj = "v 1E2 0 0\n"
        + "v 0 1 0\n"
        + "v 0 0 1\n"
        + "vt 0.1 0.2\n"
        + "vt 0.3 0.4\n"
        + "vt 0.5 0.6\n"
        + "vn 1 0 0\n"
        + "vn 0 1 0\n"
        + "vn 0 0 1\n"
        + "f 1/1/1 2/2/2 3/3/3\n";
    IndexedTriangleArray triples = OBJ.decode(new ByteArrayInputStream(triplesObj.getBytes()));
    assertEquals(100.0, triples.vertices.getValue()[0].position.x(), EPSILON);
    assertArrayEquals(new int[]{0, 1, 2}, triples.polygonData.getValueAsArray());

    String pairsObj = "v 1 0 0\n"
        + "v 0 1 0\n"
        + "v 0 0 1\n"
        + "vt 0.1 0.2\n"
        + "vt 0.3 0.4\n"
        + "vt 0.5 0.6\n"
        + "f 1/1 2/2 3/3\n";
    IndexedTriangleArray pairs = OBJ.decode(new ByteArrayInputStream(pairsObj.getBytes()));
    assertArrayEquals(new int[]{0, 1, 2}, pairs.polygonData.getValueAsArray());

    String negativeIndexObj = "v 1 0 0\n"
        + "v 0 1 0\n"
        + "v 0 0 1\n"
        + "f -3 -2 -1\n";
    IndexedTriangleArray negative = OBJ.decode(new ByteArrayInputStream(negativeIndexObj.getBytes()));
    assertArrayEquals(new int[]{0, 1, 2}, negative.polygonData.getValueAsArray());
    assertEquals(1.0f, negative.vertices.getValue()[0].normal.y(), EPSILON);
  }

  @Test(expected = RuntimeException.class)
  public void objDecodeRejectsUnhandledFaceSizes() throws Exception {
    String obj = "v 0 0 0\n"
        + "v 1 0 0\n"
        + "v 0 1 0\n"
        + "v 0 0 1\n"
        + "f 1 2 3 4\n";
    OBJ.decode(new ByteArrayInputStream(obj.getBytes()));
  }

  @Test
  public void objEncodeWritesGroupVerticesTextureCoordinatesNormalsAndFaces() {
    IndexedTriangleArray ita = new IndexedTriangleArray();
    ita.vertices.setValue(new Vertex[]{
        Vertex.createXYZIJKUV(1, 2, 3, 0, 1, 0, 0.25f, 0.5f),
        Vertex.createXYZIJKUV(4, 5, 6, 1, 0, 0, 0.5f, 0.75f),
        Vertex.createXYZIJKUV(7, 8, 9, 0, 0, 1, 0.9f, 1.0f)
    });
    ita.polygonData.setValue(IntBuffer.wrap(new int[]{0, 1, 2}));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OBJ.encode(baos, ita, AffineMatrix4x4.createTranslation(10, 20, 30), "groupA");
    String text = baos.toString();

    assertTrue(text.contains("g groupA"));
    assertTrue(text.contains("v 1.0 2.0 3.0"));
    assertTrue(text.contains("vt 0.25 0.5"));
    assertTrue(text.contains("vn 0.0 1.0 0.0"));
    assertTrue(text.contains("f -3/-3/-3 -2/-2/-2 -1/-1/-1"));
  }

  private static byte[] createBinVertexVersion1Bytes() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(1);
    dos.writeInt(1);
    dos.writeDouble(1.0);
    dos.writeDouble(2.0);
    dos.writeDouble(3.0);
    dos.writeDouble(0.0);
    dos.writeDouble(1.0);
    dos.writeDouble(0.0);
    dos.writeDouble(0.5);
    dos.writeDouble(0.75);
    dos.flush();
    return baos.toByteArray();
  }

  private static byte[] createBinVertexVersion2Bytes() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(2);
    dos.writeInt(1);
    int format = Vertex.FORMAT_POSITION | Vertex.FORMAT_NORMAL | Vertex.FORMAT_DIFFUSE_COLOR | Vertex.FORMAT_TEXTURE_COORDINATE_0;
    dos.writeInt(format);
    dos.writeDouble(4.0);
    dos.writeDouble(5.0);
    dos.writeDouble(6.0);
    dos.writeDouble(1.0);
    dos.writeDouble(0.0);
    dos.writeDouble(0.0);
    dos.writeDouble(0.1);
    dos.writeDouble(0.2);
    dos.writeDouble(0.3);
    dos.writeDouble(0.4);
    dos.writeDouble(0.6);
    dos.writeDouble(0.7);
    dos.flush();
    return baos.toByteArray();
  }

  private static byte[] createBinTriangleVersion1Bytes() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(1);
    dos.writeInt(2);
    dos.writeInt(3);
    for (int value : new int[]{0, 1, 2, 2, 3, 0}) {
      dos.writeInt(value);
    }
    dos.flush();
    return baos.toByteArray();
  }

  private static byte[] createBinaryArrayVertexVersion1Bytes() throws Exception {
    return createBinVertexVersion1Bytes();
  }

  private static byte[] createBinaryArrayVertexVersion2Bytes() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(2);
    dos.writeInt(1);
    int format = Vertex.FORMAT_POSITION | Vertex.FORMAT_NORMAL | Vertex.FORMAT_DIFFUSE_COLOR | Vertex.FORMAT_TEXTURE_COORDINATE_0;
    dos.writeInt(format);
    dos.writeDouble(4.0);
    dos.writeDouble(5.0);
    dos.writeDouble(6.0);
    dos.writeFloat(0.0f);
    dos.writeFloat(1.0f);
    dos.writeFloat(0.0f);
    dos.writeDouble(0.1);
    dos.writeDouble(0.2);
    dos.writeDouble(0.3);
    dos.writeDouble(0.4);
    dos.writeDouble(0.5);
    dos.writeDouble(0.6);
    dos.flush();
    return baos.toByteArray();
  }

  private static byte[] createIntArrayVersion1Bytes() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(1);
    dos.writeInt(2);
    dos.writeInt(3);
    for (int value : new int[]{1, 2, 3, 4, 5, 6}) {
      dos.writeInt(value);
    }
    dos.flush();
    return baos.toByteArray();
  }

  private static ByteArrayOutputStream createIntOnlyStream(int... values) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    for (int value : values) {
      dos.writeInt(value);
    }
    dos.flush();
    return baos;
  }
}
