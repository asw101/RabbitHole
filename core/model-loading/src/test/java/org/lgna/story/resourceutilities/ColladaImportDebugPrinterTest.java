package org.lgna.story.resourceutilities;

import com.dddviewr.collada.Accessor;
import com.dddviewr.collada.FloatArray;
import com.dddviewr.collada.Input;
import com.dddviewr.collada.Source;
import com.dddviewr.collada.geometry.Geometry;
import com.dddviewr.collada.geometry.Mesh;
import com.dddviewr.collada.geometry.Triangles;
import com.dddviewr.collada.geometry.Vertices;
import edu.cmu.cs.dennisc.scenegraph.InverseAbsoluteTransformationWeightsPair;
import edu.cmu.cs.dennisc.scenegraph.Joint;
import edu.cmu.cs.dennisc.scenegraph.WeightInfo;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;

public class ColladaImportDebugPrinterTest {
  @Test
  public void printGeometryInfoReportsPrimitiveCounts() {
    String output = captureStdOut(() -> ColladaImportDebugPrinter.printGeometryInfo(createGeometry()));
    assertTrue(output.contains("Tris:  1, normals: 3, vertices: 3, uvs: 3"));
  }

  @Test
  public void printJointsWalksChildren() {
    Joint root = new Joint();
    root.jointID.setValue("ROOT");
    root.localTransformation.setValue(AffineMatrix4x4.IDENTITY);

    Joint child = new Joint();
    child.jointID.setValue("CHILD");
    child.localTransformation.setValue(new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, new Point3(0, 1, 0)));
    child.setParent(root);

    String output = captureStdOut(() -> ColladaImportDebugPrinter.printJoints(root, ""));
    assertTrue(output.contains("Joint ROOT"));
    assertTrue(output.contains("Joint CHILD"));
    assertTrue(output.contains("local transform"));
    assertTrue(output.contains("absolute transform"));
  }

  @Test
  public void printWeightInfoReportsWeightsAndIndices() {
    WeightInfo weightInfo = new WeightInfo();
    weightInfo.addReference("ROOT", InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
        new float[]{0.25f, 0.0f, 0.75f}, AffineMatrix4x4.IDENTITY));

    String output = captureStdOut(() -> ColladaImportDebugPrinter.printWeightInfo(weightInfo));
    assertTrue(output.contains("ROOT:"));
    assertTrue(output.contains("weight count = 2"));
    assertTrue(output.contains("weights: [0.25, 0.75]"));
    assertTrue(output.contains("indices: [0, 2]"));
  }

  private static Geometry createGeometry() {
    Source positions = source("positions", new float[]{
        0f, 0f, 0f,
        1f, 0f, 0f,
        0f, 1f, 0f
    }, 3);
    Source normals = source("normals", new float[]{
        0f, 0f, 1f,
        0f, 0f, 1f,
        0f, 0f, 1f
    }, 3);
    Source texcoords = source("texcoords", new float[]{
        0f, 0f,
        1f, 0f,
        0f, 1f
    }, 2);

    Mesh mesh = new Mesh();
    mesh.addSource(positions);
    mesh.addSource(normals);
    mesh.addSource(texcoords);

    Vertices vertices = new Vertices("vertices");
    vertices.addInput(new Input("POSITION", "#positions"));
    mesh.setVertices(vertices);

    Triangles triangles = new Triangles("material", 1);
    Input normalInput = new Input("NORMAL", "#normals");
    normalInput.setOffset(0);
    triangles.addInput(normalInput);
    Input texcoordInput = new Input("TEXCOORD", "#texcoords");
    texcoordInput.setOffset(1);
    triangles.addInput(texcoordInput);
    mesh.addPrimitives(triangles);

    Geometry geometry = new Geometry("geometry", "geometry");
    geometry.setMesh(mesh);
    return geometry;
  }

  private static Source source(String id, float[] data, int stride) {
    Source source = new Source(id, id);
    FloatArray floatArray = new FloatArray(id + "-array", data.length);
    floatArray.setData(data);
    source.setFloatArray(floatArray);
    source.setAccessor(new Accessor("#" + floatArray.getId(), data.length / stride, stride));
    return source;
  }

  private static String captureStdOut(Runnable runnable) {
    PrintStream original = System.out;
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    PrintStream capture = new PrintStream(buffer);
    try {
      System.setOut(capture);
      runnable.run();
    } finally {
      System.setOut(original);
    }
    return buffer.toString();
  }
}
