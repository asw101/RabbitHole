package org.lgna.story.resourceutilities;

import com.dddviewr.collada.nodes.Node;
import com.dddviewr.collada.visualscene.Matrix;
import com.dddviewr.collada.visualscene.Rotate;
import com.dddviewr.collada.visualscene.Scale;
import com.dddviewr.collada.visualscene.Translate;
import edu.cmu.cs.dennisc.scenegraph.Joint;
import org.alice.math.immutable.AffineMatrix4x4;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class JointedModelColladaImporterTest {
  @Test
  public void findRootNodePrefersNamedRootAndFallsBackToFirstJoint() throws Exception {
    JointedModelColladaImporter importer = importer();

    Node firstJoint = node("arm", "arm", "JOINT");
    Node nestedRoot = node("Root", "Root", "JOINT");
    Node wrapper = node("wrapper", "wrapper", "NODE");
    wrapper.addNode(nestedRoot);

    assertSame(nestedRoot, invokeFindRootNode(importer, Arrays.asList(firstJoint, wrapper)));
    assertSame(firstJoint, invokeFindRootNode(importer, Collections.singletonList(firstJoint)));
    assertNull(invokeFindRootNode(importer, Collections.singletonList(node("mesh", "mesh", "NODE"))));
  }

  @Test
  public void collapsedRootTransformReturnsIdentityAncestorTransformOrNull() throws Exception {
    JointedModelColladaImporter importer = importer();
    Node root = node("root", "root", "JOINT");
    assertEquals(AffineMatrix4x4.IDENTITY, invokeCollapsedRootTransform(importer, Collections.singletonList(root), root));

    Node parent = node("parent", "parent", "NODE");
    Translate translate = new Translate("translate");
    translate.parse(new StringBuilder("1 2 3"));
    parent.addXform(translate);
    parent.addNode(root);

    AffineMatrix4x4 collapsed = invokeCollapsedRootTransform(importer, Collections.singletonList(parent), root);
    assertEquals(1.0, collapsed.translation().x(), 0.000001);
    assertEquals(2.0, collapsed.translation().y(), 0.000001);
    assertEquals(3.0, collapsed.translation().z(), 0.000001);

    assertNull(invokeCollapsedRootTransform(importer, Collections.singletonList(parent), node("missing", "missing", "JOINT")));
  }

  @Test
  public void createAliceSkeletonUsesJointIdentifiersAndRecursesIntoChildren() throws Exception {
    JointedModelColladaImporter importer = importer();
    setOrientation(importer, Orientation.forAlice());

    Node root = node("rootName", "ROOT_SID", "JOINT");
    Translate translate = new Translate("translate");
    translate.parse(new StringBuilder("4 5 6"));
    root.addXform(translate);

    Node child = node("childName", null, "JOINT");
    root.addNode(child);

    Joint skeleton = invokeCreateAliceSkeletonFromNode(importer, root);
    assertEquals("ROOT_SID", skeleton.jointID.getValue());
    assertEquals("ROOT_SID", skeleton.getName());
    assertEquals(4.0, skeleton.localTransformation.getValue().translation().x(), 0.000001);
    assertNotNull(skeleton.getJoint("childName"));
  }

  @Test
  public void getNodeTransformHandlesMatrixTranslateScaleAndRotate() throws Exception {
    JointedModelColladaImporter importer = importer();
    setOrientation(importer, Orientation.forAlice());

    Node matrixNode = node("matrix", "matrix", "JOINT");
    Matrix matrix = new Matrix("matrix");
    matrix.setData(new float[]{
        1, 0, 0, 0,
        0, 1, 0, 0,
        0, 0, 1, 0,
        0, 0, 0, 1
    });
    matrixNode.addXform(matrix);
    assertNotNull(invokeGetNodeTransform(importer, matrixNode));

    Node transformed = node("transform", "transform", "JOINT");
    Translate translate = new Translate("translate");
    translate.parse(new StringBuilder("1 2 3"));
    transformed.addXform(translate);
    Scale scale = new Scale("scale");
    scale.parse(new StringBuilder("2 3 4"));
    transformed.addXform(scale);
    Rotate rotate = new Rotate("rotate");
    rotate.parse(new StringBuilder("0 0 1 90"));
    transformed.addXform(rotate);

    AffineMatrix4x4 transform = invokeGetNodeTransform(importer, transformed);
    assertEquals(1.0, transform.translation().x(), 0.000001);
    assertEquals(2.0, transform.translation().y(), 0.000001);
    assertEquals(3.0, transform.translation().z(), 0.000001);
    assertNotNull(transform.orientation());
  }

  @Test
  public void loadSkeletonVisualWrapsReadFailuresAndBaseFileNameHandlesVariants() throws Exception {
    File missing = new File("target/definitely-missing-model.dae");
    JointedModelColladaImporter importer = new JointedModelColladaImporter(missing, Logger.getLogger("test"));

    try {
      importer.loadSkeletonVisual();
      fail("Expected missing file to throw");
    } catch (ModelLoadingException exception) {
      assertTrue(exception.getMessage().contains("Failed to load collada file"));
      assertTrue(exception.getMessage().contains("definitely-missing-model.dae"));
      assertNotNull(exception.getCause());
    }

    assertEquals("Model", invokeBaseFileName(importer, null));
    assertEquals("Model", invokeBaseFileName(importer, ""));
    assertEquals("robot", invokeBaseFileName(importer, "robot.dae"));
    assertEquals("robot", invokeBaseFileName(importer, "robot"));
  }

  private static JointedModelColladaImporter importer() {
    return new JointedModelColladaImporter(new File("target/importer-test.dae"), Logger.getLogger("test"));
  }

  private static Node node(String name, String sid, String type) {
    Node node = new Node(name, name, sid, type);
    node.setName(name);
    node.setSid(sid);
    node.setType(type);
    return node;
  }

  private static void setOrientation(JointedModelColladaImporter importer, Orientation orientation) throws Exception {
    Field field = JointedModelColladaImporter.class.getDeclaredField("orientation");
    field.setAccessible(true);
    field.set(importer, orientation);
  }

  @SuppressWarnings("unchecked")
  private static Node invokeFindRootNode(JointedModelColladaImporter importer, List<Node> nodes) throws Exception {
    Method method = JointedModelColladaImporter.class.getDeclaredMethod("findRootNode", List.class);
    method.setAccessible(true);
    return (Node) method.invoke(importer, nodes);
  }

  private static AffineMatrix4x4 invokeCollapsedRootTransform(JointedModelColladaImporter importer, List<Node> nodes, Node root) throws Exception {
    Method method = JointedModelColladaImporter.class.getDeclaredMethod("collapsedRootTransform", List.class, Node.class);
    method.setAccessible(true);
    return (AffineMatrix4x4) method.invoke(importer, nodes, root);
  }

  private static Joint invokeCreateAliceSkeletonFromNode(JointedModelColladaImporter importer, Node node) throws Exception {
    Method method = JointedModelColladaImporter.class.getDeclaredMethod("createAliceSkeletonFromNode", Node.class);
    method.setAccessible(true);
    return (Joint) method.invoke(importer, node);
  }

  private static AffineMatrix4x4 invokeGetNodeTransform(JointedModelColladaImporter importer, Node node) throws Exception {
    Method method = JointedModelColladaImporter.class.getDeclaredMethod("getNodeTransform", Node.class);
    method.setAccessible(true);
    return (AffineMatrix4x4) method.invoke(importer, node);
  }

  private static String invokeBaseFileName(JointedModelColladaImporter importer, String fileName) throws Exception {
    Method method = JointedModelColladaImporter.class.getDeclaredMethod("baseFileName", String.class);
    method.setAccessible(true);
    return (String) method.invoke(importer, fileName);
  }
}
