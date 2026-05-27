package org.lgna.story.resourceutilities;

import com.dddviewr.collada.Accessor;
import com.dddviewr.collada.FloatArray;
import com.dddviewr.collada.Input;
import com.dddviewr.collada.Source;
import com.dddviewr.collada.geometry.Geometry;
import com.dddviewr.collada.geometry.Mesh;
import com.dddviewr.collada.geometry.Triangles;
import com.dddviewr.collada.geometry.Vertices;
import edu.cmu.cs.dennisc.print.PrintUtilities;
import edu.cmu.cs.dennisc.scenegraph.InverseAbsoluteTransformationWeightsPair;
import edu.cmu.cs.dennisc.scenegraph.Joint;
import edu.cmu.cs.dennisc.scenegraph.WeightInfo;
import org.alice.math.immutable.AffineMatrix4x4;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertTrue;

public class ColladaImportDebugPrinterTest {
  @Test
  public void printGeometryInfoReportsPrimitiveAndAttributeCounts() {
    Geometry geometry = createGeometry("geo-1", "body");

    String output = captureOutput(() -> ColladaImportDebugPrinter.printGeometryInfo(geometry));

    assertTrue(output.contains("Tris:  1"));
    assertTrue(output.contains("normals: 3"));
    assertTrue(output.contains("vertices: 3"));
    assertTrue(output.contains("uvs: 3"));
  }

  @Test
  public void printJointsTraversesHierarchyAndIncludesTransforms() {
    Joint root = new Joint();
    root.jointID.setValue("root");
    root.setLocalTransformation(AffineMatrix4x4.createTranslation(1, 2, 3));
    Joint child = new Joint();
    child.jointID.setValue("child");
    child.setLocalTransformation(AffineMatrix4x4.createTranslation(4, 5, 6));
    root.addComponent(child);

    String output = captureOutput(() -> ColladaImportDebugPrinter.printJoints(root, ""));

    assertTrue(output.contains("Joint root"));
    assertTrue(output.contains("Joint child"));
    assertTrue(output.contains("local transform:"));
    assertTrue(output.contains("absolute transform:"));
  }

  @Test
  public void printWeightInfoListsWeightsAndIndices() {
    WeightInfo weightInfo = new WeightInfo();
    InverseAbsoluteTransformationWeightsPair pair =
        InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
            new float[]{0.25f, 0.75f},
            AffineMatrix4x4.IDENTITY
        );
    weightInfo.addReference("hip", pair);

    String output = captureOutput(() -> ColladaImportDebugPrinter.printWeightInfo(weightInfo));

    assertTrue(output.contains("hip:"));
    assertTrue(output.contains("weight count = 2"));
    assertTrue(output.contains("weights: [0.25, 0.75]"));
    assertTrue(output.contains("indices: [0, 1]"));
  }

  private static String captureOutput(Runnable action) {
    PrintStream original = System.out;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream capture = new PrintStream(baos, true, StandardCharsets.UTF_8);
    try {
      System.setOut(capture);
      PrintUtilities.pushPrintStream();
      PrintUtilities.setPrintStream(capture);
      action.run();
    } finally {
      PrintUtilities.popPrintStream();
      System.setOut(original);
      capture.close();
    }
    return new String(baos.toByteArray(), StandardCharsets.UTF_8);
  }

  private static Geometry createGeometry(String id, String name) {
    Mesh mesh = new Mesh();
    mesh.addSource(floatSource("positions", new float[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, 3));
    mesh.addSource(floatSource("normals", new float[]{0, 0, 1, 0, 1, 0, 1, 0, 0}, 3));
    mesh.addSource(floatSource("texcoords", new float[]{0, 0, 1, 0, 0, 1}, 2));

    Vertices vertices = new Vertices("vertices");
    vertices.addInput(new Input("POSITION", "#positions"));
    mesh.setVertices(vertices);

    Triangles triangles = new Triangles("mat0", 1);
    Input vertexInput = new Input("VERTEX", "#vertices");
    vertexInput.setOffset(0);
    triangles.addInput(vertexInput);
    Input normalInput = new Input("NORMAL", "#normals");
    normalInput.setOffset(0);
    triangles.addInput(normalInput);
    Input texcoordInput = new Input("TEXCOORD", "#texcoords");
    texcoordInput.setOffset(0);
    triangles.addInput(texcoordInput);
    triangles.setData(new int[]{0, 1, 2});
    mesh.addPrimitives(triangles);

    Geometry geometry = new Geometry(id, name);
    geometry.setMesh(mesh);
    return geometry;
  }

  private static Source floatSource(String id, float[] data, int stride) {
    Source source = new Source(id, id);
    FloatArray array = new FloatArray(id + "-array", data.length);
    array.setData(data);
    source.setFloatArray(array);
    source.setAccessor(new Accessor("#" + array.getId(), data.length / stride, stride));
    return source;
  }
}
