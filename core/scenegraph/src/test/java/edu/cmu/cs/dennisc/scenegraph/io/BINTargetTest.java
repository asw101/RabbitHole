package edu.cmu.cs.dennisc.scenegraph.io;

import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.scenegraph.Vertex;
import edu.cmu.cs.dennisc.texture.TextureCoordinate2f;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3f;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import static org.junit.Assert.*;

public class BINTargetTest {
  @Test
  public void storeVerticesRoundtripPreservesOptionalFields() throws Exception {
    Vertex vertex = new Vertex(
        new Point3(1, 2, 3),
        new Vector3f(0, 1, 0),
        new Color4f(0.1f, 0.2f, 0.3f, 0.4f),
        new Color4f(0.5f, 0.6f, 0.7f, 0.8f),
        new TextureCoordinate2f(0.25f, 0.75f)
    );
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    BIN.storeVertices(new Vertex[]{vertex}, baos);

    Vertex[] loaded = BIN.loadVertices(new ByteArrayInputStream(baos.toByteArray()));

    assertEquals(1, loaded.length);
    assertEquals(vertex, loaded[0]);
    assertEquals(vertex.specularHighlightColor, loaded[0].specularHighlightColor);
  }

  @Test
  public void loadTriangleDataSupportsLegacyVersionOne() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(1);
    dos.writeInt(2);
    dos.writeInt(3);
    for (int value : new int[]{0, 1, 2, 2, 3, 0}) {
      dos.writeInt(value);
    }
    dos.flush();

    assertArrayEquals(new int[]{0, 1, 2, 2, 3, 0}, BIN.loadTriangleData(new ByteArrayInputStream(baos.toByteArray())));
  }

  @Test(expected = RuntimeException.class)
  public void loadVerticesRejectsInvalidVersion() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(99);
    dos.writeInt(0);
    dos.flush();
    BIN.loadVertices(new ByteArrayInputStream(baos.toByteArray()));
  }
}
