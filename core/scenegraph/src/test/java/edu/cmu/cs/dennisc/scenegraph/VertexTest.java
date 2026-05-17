package edu.cmu.cs.dennisc.scenegraph;

import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.texture.TextureCoordinate2f;
import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3f;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class VertexTest {
  private static final double EPSILON = 0.000001;

  @Test
  public void constructorWithAllFieldsSetsCorrectly() {
    Point3 pos = new Point3(1, 2, 3);
    Vector3f norm = new Vector3f(0, 1, 0);
    Color4f diffuse = new Color4f(1, 0, 0, 1);
    Color4f specular = new Color4f(0, 1, 0, 1);
    TextureCoordinate2f tc = new TextureCoordinate2f(0.5f, 0.75f);

    Vertex v = new Vertex(pos, norm, diffuse, specular, tc);

    assertEquals(1.0, v.position.x(), EPSILON);
    assertEquals(2.0, v.position.y(), EPSILON);
    assertEquals(3.0, v.position.z(), EPSILON);
    assertEquals(0.0f, v.normal.x(), EPSILON);
    assertEquals(1.0f, v.normal.y(), EPSILON);
    assertEquals(0.0f, v.normal.z(), EPSILON);
  }

  @Test
  public void constructorWithNullsFillsNaN() {
    Vertex v = new Vertex(null, null, null, null, null);
    assertTrue("position should be NaN", v.position.isNaN());
    assertTrue("normal should be NaN", v.normal.isNaN());
    assertTrue("diffuseColor should be NaN", v.diffuseColor.isNaN());
    assertTrue("specularHighlightColor should be NaN", v.specularHighlightColor.isNaN());
    assertTrue("textureCoordinate0 should be NaN", v.textureCoordinate0.isNaN());
  }

  @Test
  public void copyConstructorCreatesEqualVertex() {
    Vertex original = Vertex.createXYZIJKUV(1, 2, 3, 0.5f, 0.6f, 0.7f, 0.1f, 0.2f);
    Vertex copy = new Vertex(original);

    assertEquals(original, copy);
    assertEquals(original.position.x(), copy.position.x(), EPSILON);
    assertEquals(original.normal.x(), copy.normal.x(), EPSILON);
  }

  @Test
  public void createXYZSetsPositionOnly() {
    Vertex v = Vertex.createXYZ(new Point3(5, 6, 7));
    assertEquals(5.0, v.position.x(), EPSILON);
    assertEquals(6.0, v.position.y(), EPSILON);
    assertEquals(7.0, v.position.z(), EPSILON);
    assertTrue("normal should be NaN", v.normal.isNaN());
  }

  @Test
  public void createXYZWithDoublesWorks() {
    Vertex v = Vertex.createXYZ(10, 20, 30);
    assertEquals(10.0, v.position.x(), EPSILON);
    assertEquals(20.0, v.position.y(), EPSILON);
    assertEquals(30.0, v.position.z(), EPSILON);
  }

  @Test
  public void createXYZUVSetsPositionAndTexCoord() {
    Vertex v = Vertex.createXYZUV(1, 2, 3, 0.5f, 0.75f);
    assertEquals(1.0, v.position.x(), EPSILON);
    assertFalse("textureCoordinate0 should not be NaN", v.textureCoordinate0.isNaN());
  }

  @Test
  public void createXYZIJKSetsPositionAndNormal() {
    Vertex v = Vertex.createXYZIJK(1, 2, 3, 0, 1, 0);
    assertEquals(1.0, v.position.x(), EPSILON);
    assertEquals(1.0f, v.normal.y(), EPSILON);
    assertFalse("normal should not be NaN", v.normal.isNaN());
  }

  @Test
  public void createXYZIJKUVSetsAllThree() {
    Vertex v = Vertex.createXYZIJKUV(1, 2, 3, 0, 1, 0, 0.5f, 0.75f);
    assertFalse("position should not be NaN", v.position.isNaN());
    assertFalse("normal should not be NaN", v.normal.isNaN());
    assertFalse("textureCoordinate0 should not be NaN", v.textureCoordinate0.isNaN());
  }

  @Test
  public void createXYZRGBASetsPositionAndDiffuseColor() {
    Vertex v = Vertex.createXYZRGBA(1, 2, 3, 1.0f, 0.0f, 0.0f, 1.0f);
    assertEquals(1.0, v.position.x(), EPSILON);
    assertFalse("diffuseColor should not be NaN", v.diffuseColor.isNaN());
  }

  @Test
  public void createXYZRGBSetsAlphaToOne() {
    Vertex v = Vertex.createXYZRGB(1, 2, 3, 0.5f, 0.6f, 0.7f);
    assertFalse("diffuseColor should not be NaN", v.diffuseColor.isNaN());
  }

  @Test
  public void equalsReturnsTrueForIdenticalVertices() {
    Vertex a = Vertex.createXYZIJKUV(1, 2, 3, 0, 1, 0, 0.5f, 0.75f);
    Vertex b = Vertex.createXYZIJKUV(1, 2, 3, 0, 1, 0, 0.5f, 0.75f);
    assertEquals(a, b);
  }

  @Test
  public void equalsReturnsFalseForDifferentPositions() {
    Vertex a = Vertex.createXYZ(1, 2, 3);
    Vertex b = Vertex.createXYZ(4, 5, 6);
    assertFalse(a.equals(b));
  }

  @Test
  public void equalsReturnsFalseForDifferentNormals() {
    Vertex a = Vertex.createXYZIJK(1, 2, 3, 0, 1, 0);
    Vertex b = Vertex.createXYZIJK(1, 2, 3, 1, 0, 0);
    assertFalse(a.equals(b));
  }

  @Test
  public void equalsReturnsTrueForBothNaN() {
    Vertex a = new Vertex(null, null, null, null, null);
    Vertex b = new Vertex(null, null, null, null, null);
    assertEquals(a, b);
  }

  @Test
  public void equalsReturnsFalseForNonVertex() {
    Vertex v = Vertex.createXYZ(1, 2, 3);
    assertFalse(v.equals("not a vertex"));
  }

  @Test
  public void equalsReturnsFalseForOneNaNPositionOneNot() {
    Vertex a = new Vertex(null, null, null, null, null);
    Vertex b = Vertex.createXYZ(1, 2, 3);
    assertFalse(a.equals(b));
    assertFalse(b.equals(a));
  }

  @Test
  public void equalsReturnsFalseForOneNaNNormalOneNot() {
    Vertex a = Vertex.createXYZ(1, 2, 3); // normal is NaN
    Vertex b = Vertex.createXYZIJK(1, 2, 3, 0, 1, 0); // normal is set
    assertFalse(a.equals(b));
    assertFalse(b.equals(a));
  }

  @Test
  public void equalsReturnsFalseForDifferentDiffuseColors() {
    Vertex a = Vertex.createXYZRGBA(1, 2, 3, 1, 0, 0, 1);
    Vertex b = Vertex.createXYZRGBA(1, 2, 3, 0, 1, 0, 1);
    assertFalse(a.equals(b));
  }

  @Test
  public void equalsReturnsFalseForDifferentTextureCoords() {
    Vertex a = Vertex.createXYZUV(1, 2, 3, 0.1f, 0.2f);
    Vertex b = Vertex.createXYZUV(1, 2, 3, 0.9f, 0.8f);
    assertFalse(a.equals(b));
  }

  @Test
  public void getFormatReturnsCorrectBitmask() {
    Vertex full = Vertex.createXYZIJKUV(1, 2, 3, 0, 1, 0, 0.5f, 0.75f);
    int format = full.getFormat();
    assertTrue("Should have position", (format & Vertex.FORMAT_POSITION) != 0);
    assertTrue("Should have normal", (format & Vertex.FORMAT_NORMAL) != 0);
    assertTrue("Should have texcoord", (format & Vertex.FORMAT_TEXTURE_COORDINATE_0) != 0);
    assertFalse("Should not have diffuse", (format & Vertex.FORMAT_DIFFUSE_COLOR) != 0);
  }

  @Test
  public void getFormatWithColorIncludesDiffuse() {
    Vertex v = Vertex.createXYZRGBA(1, 2, 3, 1, 0, 0, 1);
    int format = v.getFormat();
    assertTrue("Should have position", (format & Vertex.FORMAT_POSITION) != 0);
    assertTrue("Should have diffuse color", (format & Vertex.FORMAT_DIFFUSE_COLOR) != 0);
    assertFalse("Should not have normal", (format & Vertex.FORMAT_NORMAL) != 0);
  }

  @Test
  public void getFormatForNaNVertexReturnsZero() {
    Vertex v = new Vertex(null, null, null, null, null);
    assertEquals(0, v.getFormat());
  }

  @Test
  public void transformMovesPositionAndNormal() {
    Vertex v = Vertex.createXYZIJK(1, 0, 0, 0, 1, 0);

    // Translation matrix: shift by (10, 20, 30)
    Matrix4x4 translate = Matrix4x4.fromTranslation(new Point3(10, 20, 30));
    v.transform(translate);

    assertEquals(11.0, v.position.x(), EPSILON);
    assertEquals(20.0, v.position.y(), EPSILON);
    assertEquals(30.0, v.position.z(), EPSILON);
  }

  @Test
  public void transformSkipsNaNPositionAndNormal() {
    Vertex v = new Vertex(null, null, null, null, null);
    Matrix4x4 translate = Matrix4x4.fromTranslation(new Point3(10, 20, 30));

    // Should not throw
    v.transform(translate);
    assertTrue("position should still be NaN", v.position.isNaN());
    assertTrue("normal should still be NaN", v.normal.isNaN());
  }

  @Test
  public void toStringContainsClassName() {
    Vertex v = Vertex.createXYZ(1, 2, 3);
    String s = v.toString();
    assertNotNull(s);
    assertTrue("toString should contain class name", s.contains("Vertex"));
    assertTrue("toString should contain position", s.contains("position="));
  }

  @Test
  public void equalsSameObjectReturnsTrue() {
    Vertex v = Vertex.createXYZ(1, 2, 3);
    assertTrue(v.equals(v));
  }
}
