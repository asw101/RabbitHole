package org.lgna.story.resourceutilities;

import com.dddviewr.collada.geometry.Geometry;
import com.dddviewr.collada.geometry.Triangles;
import edu.cmu.cs.dennisc.print.PrintUtilities;
import edu.cmu.cs.dennisc.scenegraph.Component;
import edu.cmu.cs.dennisc.scenegraph.InverseAbsoluteTransformationWeightsPair;
import edu.cmu.cs.dennisc.scenegraph.Joint;
import edu.cmu.cs.dennisc.scenegraph.WeightInfo;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Point3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * Debugging helpers for COLLADA import diagnostics.
 * Package-private utility extracted from {@link JointedModelColladaImporter}.
 */
class ColladaImportDebugPrinter {

  static void printGeometryInfo(Geometry geometry) {
    float[] normalData = geometry.getMesh().getNormalData();
    int normalCount = normalData.length / 3;
    float[] vertexData = geometry.getMesh().getPositionData();
    int vertexCount = vertexData.length / 3;
    float[] uvData = geometry.getMesh().getTexCoordData();
    int uvCount = uvData.length / 2;
    Triangles tris = (Triangles) geometry.getMesh().getPrimitives().getFirst();
    int triCount = tris.getCount();

    System.out.println("Tris:  " + triCount + ", normals: " + normalCount + ", vertices: " + vertexCount + ", uvs: " + uvCount);
  }

  static void printJoints(Joint j, String indent) {
    System.out.println(indent + "Joint " + j.jointID.getValue());
    PrintUtilities.print(indent + "    local transform: ", j.localTransformation.getValue().translation(), j.localTransformation.getValue().orientation());
    System.out.println();
    AffineMatrix4x4 absoluteTransform = j.getAbsoluteTransformation();
    PrintUtilities.print(indent + " absolute transform: ", absoluteTransform.translation(), absoluteTransform.orientation());
    System.out.println();
    for (int i = 0; i < j.getComponentCount(); i++) {
      Component comp = j.getComponentAt(i);
      if (comp instanceof Joint joint) {
        printJoints(joint, indent + "  ");
      }
    }
  }

  static void printWeightInfo(WeightInfo wi) {
    for (Entry<String, InverseAbsoluteTransformationWeightsPair> entry : wi.getMap().entrySet()) {
      InverseAbsoluteTransformationWeightsPair iatwp = entry.getValue();
      Point3 t = iatwp.getInverseAbsoluteTransformation().translation();
      OrthogonalMatrix3x3 o = iatwp.getInverseAbsoluteTransformation().orientation();

      System.out.println(entry.getKey() + ":");
      System.out.println(" inverse transform = (" + t.x() + ", " + t.y() + ", " + t.z() + "), [[" + o.right().x() + ", " + o.right().y() + ", " + o.right().z() + "], [" + o.up().x() + ", " + o.up().y() + ", " + o.up().z() + "], [" + o.backward().x() + ", " + o.backward().y() + ", " + o.backward().z() + "]]");
      List<Float> weights = new ArrayList<>();
      List<Integer> indices = new ArrayList<>();
      InverseAbsoluteTransformationWeightsPair.WeightIterator weightIterator = iatwp.getIterator();
      while (weightIterator.hasNext()) {
        indices.add(weightIterator.getIndex());
        weights.add(weightIterator.next());
      }
      System.out.println("  weight count = " + weights.size());
      System.out.println("  weights: " + weights);
      System.out.println(" indices: " + indices);
    }
  }
}
