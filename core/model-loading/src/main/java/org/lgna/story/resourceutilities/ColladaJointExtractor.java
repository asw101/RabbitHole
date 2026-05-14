package org.lgna.story.resourceutilities;

import edu.cmu.cs.dennisc.scenegraph.Component;
import edu.cmu.cs.dennisc.scenegraph.Joint;
import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import org.lgna.story.resourceutilities.exporterutils.collada.Matrix;
import org.lgna.story.resourceutilities.exporterutils.collada.Node;
import org.lgna.story.resourceutilities.exporterutils.collada.NodeType;
import org.lgna.story.resourceutilities.exporterutils.collada.ObjectFactory;

import java.util.Map;

/**
 * Extracts skeleton joint tree into COLLADA Node hierarchy.
 */
class ColladaJointExtractor {

  private static final boolean FLIP_COORDINATE_SPACE = true;

  private final ObjectFactory factory;
  private final Map<String, String> renamedJoints;

  ColladaJointExtractor(ObjectFactory factory, Map<String, String> renamedJoints) {
    this.factory = factory;
    this.renamedJoints = renamedJoints;
  }

  Node createNodeForJoint(Joint joint) {
    Node node = factory.createNode();
    String jointIdentifier = getUserJointIdentifier(joint.jointID.getValue());
    node.setName(jointIdentifier);
    node.setId(jointIdentifier);
    node.setSid(jointIdentifier);
    node.setType(NodeType.JOINT);

    Matrix matrix = factory.createMatrix();
    matrix.setSid("matrix");
    double[] matrixValues = joint.localTransformation.getValue().asRowMajorArray16();
    if (FLIP_COORDINATE_SPACE) {
      matrixValues = ColladaTransformUtilities.createFlippedRowMajorTransform(matrixValues);
    }
    for (double matrixValue : matrixValues) {
      matrix.getValue().add(matrixValue);
    }

    node.getLookatOrMatrixOrRotate().add(matrix);

    for (Component c : joint.getComponents()) {
      if (c instanceof Joint childJoint) {
        Node childNode = createNodeForJoint(childJoint);
        node.getNode().add(childNode);
      }
    }

    return node;
  }

  Node createSkeletonNodes(SkeletonVisual visual) {
    Joint rootJoint = visual.skeleton.getValue();
    if (rootJoint != null) {
      return createNodeForJoint(rootJoint);
    }
    return null;
  }

  String getUserJointIdentifier(String jointIdentifier) {
    return renamedJoints.getOrDefault(jointIdentifier, jointIdentifier);
  }
}
