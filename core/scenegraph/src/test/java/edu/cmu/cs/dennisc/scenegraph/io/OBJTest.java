package edu.cmu.cs.dennisc.scenegraph.io;

import edu.cmu.cs.dennisc.scenegraph.IndexedTriangleArray;
import edu.cmu.cs.dennisc.scenegraph.Vertex;
import org.alice.math.immutable.AffineMatrix4x4;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OBJTest {
  private static final double EPSILON = 0.000001;

  @Test
  public void decodeParsesVerticesNormalsFacesAndTrailingMaterialReference() throws Exception {
    String obj = "v 0 0 0\n"
        + "v 1 0 0\n"
        + "v 0 1 0\n"
        + "vt 0.1 0.2\n"
        + "vt 0.3 0.4\n"
        + "vt 0.5 0.6\n"
        + "vn 0 0 1\n"
        + "vn 0 1 0\n"
        + "vn 1 0 0\n"
        + "f 1/1/1 2/2/2 3/3/3\n"
        + "usemtl matte\n";

    IndexedTriangleArray ita = OBJ.decode(new ByteArrayInputStream(obj.getBytes(StandardCharsets.UTF_8)));

    Vertex[] vertices = ita.vertices.getValue();
    assertEquals(3, vertices.length);
    assertEquals(0.0, vertices[0].position.x(), EPSILON);
    assertEquals(1.0, vertices[1].position.x(), EPSILON);
    assertEquals(0.6f, vertices[2].textureCoordinate0.v, EPSILON);
    assertEquals(1.0f, vertices[0].normal.z(), EPSILON);
    assertArrayEquals(new int[] {0, 1, 2}, ita.polygonData.getValueAsArray());
  }

  @Test
  public void encodeWritesGroupVerticesNormalsTextureCoordinatesAndFaces() {
    IndexedTriangleArray ita = new IndexedTriangleArray();
    ita.vertices.setValue(new Vertex[] {
        Vertex.createXYZIJKUV(1, 2, 3, 0, 1, 0, 0.25f, 0.5f),
        Vertex.createXYZIJKUV(4, 5, 6, 1, 0, 0, 0.5f, 0.75f),
        Vertex.createXYZIJKUV(7, 8, 9, 0, 0, 1, 0.9f, 1.0f)
    });
    ita.polygonData.setValue(IntBuffer.wrap(new int[] {0, 1, 2}));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OBJ.encode(baos, ita, AffineMatrix4x4.IDENTITY, "triangle");
    String text = baos.toString(StandardCharsets.UTF_8);

    assertTrue(text.contains("g triangle"));
    assertTrue(text.contains("v 1.0 2.0 3.0"));
    assertTrue(text.contains("vt 0.25 0.5"));
    assertTrue(text.contains("vn 0.0 1.0 0.0"));
    assertTrue(text.contains("f -3/-3/-3 -2/-2/-2 -1/-1/-1"));
  }
}
