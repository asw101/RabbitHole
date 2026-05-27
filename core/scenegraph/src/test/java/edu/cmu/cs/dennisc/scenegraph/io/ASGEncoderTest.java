package edu.cmu.cs.dennisc.scenegraph.io;

import edu.cmu.cs.dennisc.scenegraph.Component;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.Vertex;
import edu.cmu.cs.dennisc.texture.TextureCoordinate2f;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3f;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.assertPointEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ASGEncoderTest {
  @Test
  public void binaryArrayEncodingRoundTripsThroughDecoder() {
    Vertex[] vertices = new Vertex[] {
        new Vertex(new Point3(1, 2, 3), new Vector3f(0, 1, 0), null, null, new TextureCoordinate2f(0.25f, 0.5f)),
        new Vertex(new Point3(4, 5, 6), new Vector3f(1, 0, 0), null, null, new TextureCoordinate2f(0.75f, 1.0f))
    };
    int[] indices = new int[] {0, 1, 2, 3, 4, 5};
    double[] doubles = new double[] {1.5, 2.5, 3.5};

    ByteArrayOutputStream vertexBytes = new ByteArrayOutputStream();
    ASGEncoder.encodeVertexArrayInBinary(vertices, vertexBytes);
    ByteArrayOutputStream indexBytes = new ByteArrayOutputStream();
    ASGEncoder.encodeIntArrayInBinary(indices, indexBytes);
    ByteArrayOutputStream doubleBytes = new ByteArrayOutputStream();
    ASGEncoder.encodeDoubleArrayInBinary(doubles, doubleBytes);

    Vertex[] decodedVertices = BinaryArrayDecoder.decodeVertexArray(new ByteArrayInputStream(vertexBytes.toByteArray()));
    int[] decodedIndices = BinaryArrayDecoder.decodeIntArray(new ByteArrayInputStream(indexBytes.toByteArray()));
    double[] decodedDoubles = BinaryArrayDecoder.decodeDoubleArray(new ByteArrayInputStream(doubleBytes.toByteArray()));

    assertEquals(2, decodedVertices.length);
    assertEquals(1.0, decodedVertices[0].position.x(), 0.0);
    assertTrue(!decodedVertices[0].textureCoordinate0.isNaN());
    assertArrayEquals(new int[] {2, 1, 0, 5, 4, 3}, decodedIndices);
    assertArrayEquals(doubles, decodedDoubles, 0.0);
  }

  @Test
  public void encodeProducesZipRootAndRoundTripsSimpleHierarchy() throws Exception {
    Transformable root = new Transformable();
    root.setName("root");
    root.setLocalTransformation(AffineMatrix4x4.createTranslation(1, 2, 3));
    Transformable child = new Transformable();
    child.setName("child");
    child.setLocalTransformation(AffineMatrix4x4.createTranslation(4, 5, 6));
    root.addComponent(child);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ASGEncoder.encode(root, baos);

    boolean foundRootXml = false;
    try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        if (ASG.ROOT_FILENAME.equals(entry.getName())) {
          foundRootXml = true;
        }
        zis.closeEntry();
      }
    }
    assertTrue(foundRootXml);

    Component decoded = ASG.decodeZip(new ByteArrayInputStream(baos.toByteArray()));
    assertNotNull(decoded);
    assertTrue(decoded instanceof Transformable);
    Transformable decodedRoot = (Transformable) decoded;
    assertEquals("root", decodedRoot.getName());
    assertPointEquals(new Point3(1, 2, 3), decodedRoot.getLocalTransformation().translation());
    Transformable decodedChild = (Transformable) decodedRoot.getComponentAt(0);
    assertEquals("child", decodedChild.getName());
    assertPointEquals(new Point3(4, 5, 6), decodedChild.getLocalTransformation().translation());
  }
}
