package edu.cmu.cs.dennisc.scenegraph.io;

import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.image.ImageUtilities;
import edu.cmu.cs.dennisc.scenegraph.FillingStyle;
import edu.cmu.cs.dennisc.scenegraph.Vertex;
import edu.cmu.cs.dennisc.texture.TextureCoordinate2f;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Matrix3x3;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector2f;
import org.alice.math.immutable.Vector3;
import org.alice.math.immutable.Vector3f;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static org.junit.Assert.*;

public class PropertyValueParserTest {
  private static final double EPSILON = 0.000001;

  @Test
  public void xmlHelpersFindChildrenAndConcatenateText() throws Exception {
    Element root = parseElement("<property><skip/><wanted> first </wanted><wanted>second</wanted><text>a<![CDATA[b]]>c</text></property>");

    Element firstWanted = PropertyValueParser.getFirstChild(root, "wanted");
    Element[] wantedChildren = PropertyValueParser.getChildren(root, "wanted");

    assertNotNull(firstWanted);
    assertEquals("first", PropertyValueParser.getNodeText(firstWanted));
    assertEquals(2, wantedChildren.length);
    assertEquals("abc", PropertyValueParser.getNodeText(PropertyValueParser.getFirstChild(root, "text")));
  }

  @Test
  public void parseValueFromXmlSupportsMatricesColorsArraysVerticesAndValueOf() throws Exception {
    AffineMatrix4x4 matrix = (AffineMatrix4x4) PropertyValueParser.parseValue(parseElement(
        "<property>"
            + "<row>1 0 0 10</row>"
            + "<row>0 1 0 20</row>"
            + "<row>0 0 1 30</row>"
            + "<row>0 0 0 1</row>"
            + "</property>"),
        AffineMatrix4x4.class, new HashMap<String, InputStream>());
    assertEquals(10.0, matrix.translation().x(), EPSILON);
    assertEquals(20.0, matrix.translation().y(), EPSILON);
    assertEquals(30.0, matrix.translation().z(), EPSILON);

    Matrix3x3 matrix3 = (Matrix3x3) PropertyValueParser.parseValue(parseElement(
        "<property>"
            + "<row>1 2 3</row>"
            + "<row>4 5 6</row>"
            + "<row>7 8 9</row>"
            + "</property>"),
        Matrix3x3.class, new HashMap<String, InputStream>());
    assertEquals(1.0, matrix3.getRight().x(), EPSILON);
    assertEquals(8.0, matrix3.getUp().z(), EPSILON);
    assertEquals(9.0, matrix3.getBackward().z(), EPSILON);

    Color4f color = (Color4f) PropertyValueParser.parseValue(parseElement(
        "<property><red>0.1</red><green>0.2</green><blue>0.3</blue><alpha>0.4</alpha></property>"),
        Color4f.class, new HashMap<String, InputStream>());
    assertEquals(0.1f, color.red, EPSILON);
    assertEquals(0.4f, color.alpha, EPSILON);

    int[] ints = (int[]) PropertyValueParser.parseValue(parseElement("<property length='4'>1 2 3 4</property>"), int[].class, new HashMap<String, InputStream>());
    assertArrayEquals(new int[]{1, 2, 3, 4}, ints);

    double[] doubles = (double[]) PropertyValueParser.parseValue(parseElement("<property length='3'>1.5 2.5 3.5</property>"), double[].class, new HashMap<String, InputStream>());
    assertArrayEquals(new double[]{1.5, 2.5, 3.5}, doubles, EPSILON);

    Point3[] points = (Point3[]) PropertyValueParser.parseValue(parseElement(
        "<property><point>1 2 3</point><point>4 5 6</point></property>"),
        Point3[].class, new HashMap<String, InputStream>());
    assertEquals(2, points.length);
    assertEquals(4.0, points[1].x(), EPSILON);

    Vector3f[] normals = (Vector3f[]) PropertyValueParser.parseValue(parseElement(
        "<property><normal>0 1 2</normal><normal>3 4 5</normal></property>"),
        Vector3f[].class, new HashMap<String, InputStream>());
    assertEquals(2, normals.length);
    assertEquals(5.0f, normals[1].z(), EPSILON);

    Vector2f[] textureCoordinates = (Vector2f[]) PropertyValueParser.parseValue(parseElement(
        "<property><textureCoordinate>0.25 0.75</textureCoordinate></property>"),
        Vector2f[].class, new HashMap<String, InputStream>());
    assertEquals(1, textureCoordinates.length);
    assertEquals(0.75f, textureCoordinates[0].x(), EPSILON);
    assertEquals(0.75f, textureCoordinates[0].y(), EPSILON);

    Vertex[] vertices = (Vertex[]) PropertyValueParser.parseValue(parseElement(
        "<property>"
            + "<vertex><position>1 2 3</position><normal>0 1 0</normal><diffuseColor>1 0 0 1</diffuseColor><textureCoordinate0>0.5 0.25</textureCoordinate0></vertex>"
            + "<vertex><position>4 5 6</position></vertex>"
            + "</property>"),
        Vertex[].class, new HashMap<String, InputStream>());
    assertEquals(2, vertices.length);
    assertEquals(new Point3(1, 2, 3), vertices[0].position);
    assertEquals(0.5f, vertices[0].textureCoordinate0.u, EPSILON);
    assertEquals(0.25f, vertices[0].textureCoordinate0.v, EPSILON);
    assertTrue(vertices[1].normal.isNaN());

    Object stringValue = PropertyValueParser.parseValue(parseElement("<property>Hello world</property>"), String.class, new HashMap<String, InputStream>());
    assertEquals("Hello world", stringValue);
    assertEquals(Double.POSITIVE_INFINITY, (Double) PropertyValueParser.parseValue(parseElement("<property>Infinity</property>"), Double.class, new HashMap<String, InputStream>()), 0.0);
    assertTrue(Double.isNaN((Double) PropertyValueParser.parseValue(parseElement("<property>NaN</property>"), Double.class, new HashMap<String, InputStream>())));
    assertSame(FillingStyle.WIREFRAME, PropertyValueParser.parseValue(parseElement("<property>WIREFRAME</property>"), FillingStyle.class, new HashMap<String, InputStream>()));
  }

  @Test
  public void parseValueFromXmlSupportsImageRows() throws Exception {
    Image image = (Image) PropertyValueParser.parseValue(parseElement(
        "<property width='2' height='1'><row>FF0000FF 00FF00FF</row></property>"),
        Image.class, new HashMap<String, InputStream>());

    assertEquals(2, ImageUtilities.getWidth(image));
    assertEquals(1, ImageUtilities.getHeight(image));
    int[] pixels = ImageUtilities.getPixels(image, 2, 1);
    assertEquals(0xFF0000FF, pixels[0]);
    assertEquals(0x00FF00FF, pixels[1]);
  }

  @Test
  public void parseValueFromFileSupportsImageAndBinaryArrays() throws Exception {
    HashMap<String, InputStream> streams = new HashMap<String, InputStream>();

    BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    bufferedImage.setRGB(0, 0, 0x11223344);
    ByteArrayOutputStream imageOut = new ByteArrayOutputStream();
    ImageUtilities.write(ImageUtilities.PNG_CODEC_NAME, imageOut, bufferedImage);
    streams.put("sample.png", new ByteArrayInputStream(imageOut.toByteArray()));

    ByteArrayOutputStream vertexOut = new ByteArrayOutputStream();
    ASG.encodeVertexArrayInBinary(new Vertex[]{Vertex.createXYZIJKUV(7, 8, 9, 0, 1, 0, 0.1f, 0.2f)}, vertexOut);
    streams.put("vertices.bin", new ByteArrayInputStream(vertexOut.toByteArray()));

    ByteArrayOutputStream intOut = new ByteArrayOutputStream();
    ASG.encodeIntArrayInBinary(new int[]{1, 2, 3, 4, 5, 6}, intOut);
    streams.put("ints.bin", new ByteArrayInputStream(intOut.toByteArray()));

    ByteArrayOutputStream doubleOut = new ByteArrayOutputStream();
    ASG.encodeDoubleArrayInBinary(new double[]{3.25, 4.5}, doubleOut);
    streams.put("doubles.bin", new ByteArrayInputStream(doubleOut.toByteArray()));

    Image decodedImage = (Image) PropertyValueParser.parseValue(parseElement("<property filename='sample.png'/>") , Image.class, streams);
    assertEquals(1, ImageUtilities.getWidth(decodedImage));
    assertEquals(1, ImageUtilities.getHeight(decodedImage));

    Vertex[] decodedVertices = (Vertex[]) PropertyValueParser.parseValue(parseElement("<property filename='vertices.bin'/>") , Vertex[].class, streams);
    assertEquals(1, decodedVertices.length);
    assertEquals(7.0, decodedVertices[0].position.x(), EPSILON);

    int[] decodedInts = (int[]) PropertyValueParser.parseValue(parseElement("<property filename='ints.bin'/>") , int[].class, streams);
    assertArrayEquals(new int[]{3, 2, 1, 6, 5, 4}, decodedInts);

    double[] decodedDoubles = (double[]) PropertyValueParser.parseValue(parseElement("<property filename='doubles.bin'/>") , double[].class, streams);
    assertArrayEquals(new double[]{3.25, 4.5}, decodedDoubles, EPSILON);
  }

  @Test(expected = RuntimeException.class)
  public void parseValueFromFileThrowsWhenFilenameMissing() throws Exception {
    PropertyValueParser.parseValue(parseElement("<property filename='missing.bin'/>") , int[].class, new HashMap<String, InputStream>());
  }

  private static Element parseElement(String xml) throws Exception {
    Document document = DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    return document.getDocumentElement();
  }
}
