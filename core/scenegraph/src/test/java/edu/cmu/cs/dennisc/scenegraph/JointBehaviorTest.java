package edu.cmu.cs.dennisc.scenegraph;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Matrix3x3;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.assertBoxEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class JointBehaviorTest {
  @Test
  public void getJointAndGetJointsSearchWholeHierarchy() {
    Joint root = joint("ROOT");
    Joint arm = joint("ARM");
    Joint hand = joint("HAND");
    Joint leg = joint("LEG");
    arm.setParent(root);
    hand.setParent(arm);
    leg.setParent(root);

    assertSame(hand, root.getJoint("HAND"));
    assertSame(arm, root.getJoint("ARM"));
    assertNull(root.getJoint("MISSING"));
    assertEquals(List.of("ARM"), ids(root.getJoints("ARM")));
    assertEquals(List.of("HAND"), ids(root.getJoints("HAND")));
  }

  @Test
  public void visitJointsTraversesDepthFirstAndToStringIncludesJointId() {
    Joint root = joint("ROOT");
    Joint child = joint("CHILD");
    Joint grandchild = joint("GRANDCHILD");
    child.setParent(root);
    grandchild.setParent(child);

    List<String> visited = new ArrayList<>();
    root.visitJoints(joint -> visited.add(joint.jointID.getValue()));

    assertEquals(List.of("ROOT", "CHILD", "GRANDCHILD"), visited);
    assertTrue(root.toString().contains("jointId=ROOT"));
  }

  @Test
  public void scaleRecursivelyUpdatesTranslationsAndBoundingBoxes() {
    Joint root = joint("ROOT");
    Joint child = joint("CHILD");
    child.setParent(root);
    root.localTransformation.setValue(AffineMatrix4x4.createTranslation(1, 2, 3));
    child.localTransformation.setValue(AffineMatrix4x4.createTranslation(2, 0, 0));
    root.boundingBox.setValue(new AxisAlignedBox(new Point3(0, 0, 0), new Point3(1, 1, 1)));
    child.boundingBox.setValue(new AxisAlignedBox(new Point3(-1, -1, -1), new Point3(1, 1, 1)));

    root.scale(2.0);

    assertEquals(new Point3(2, 4, 6), root.localTransformation.getValue().translation());
    assertEquals(new Point3(4, 0, 0), child.localTransformation.getValue().translation());
    assertBoxEquals(
        new AxisAlignedBox(new Point3(0, 0, 0), new Point3(2, 2, 2)),
        root.boundingBox.getValue()
    );
    assertBoxEquals(
        new AxisAlignedBox(new Point3(-2, -2, -2), new Point3(2, 2, 2)),
        child.boundingBox.getValue()
    );
  }

  @Test
  public void cumulativeBoundingBoxIncludesChildrenAndParentVisualScale() {
    Joint root = joint("ROOT");
    Joint child = joint("CHILD");
    child.setParent(root);
    child.localTransformation.setValue(AffineMatrix4x4.createTranslation(3, 0, 0));
    root.boundingBox.setValue(new AxisAlignedBox(new Point3(0, 0, 0), new Point3(1, 1, 1)));
    child.boundingBox.setValue(new AxisAlignedBox(new Point3(0, 0, 0), new Point3(1, 1, 1)));

    SkeletonVisual visual = new SkeletonVisual();
    visual.scale.setValue(Matrix3x3.IDENTITY.scale(2.0));
    root.setParentVisual(visual);

    assertBoxEquals(
        new AxisAlignedBox(new Point3(0, 0, 0), new Point3(2, 2, 2)),
        root.getBoundingBox(false)
    );
    assertBoxEquals(
        new AxisAlignedBox(new Point3(0, 0, 0), new Point3(5, 2, 2)),
        root.getBoundingBox(true)
    );
  }

  private static Joint joint(String id) {
    Joint joint = new Joint();
    joint.jointID.setValue(id);
    return joint;
  }

  private static List<String> ids(Joint[] joints) {
    List<String> ids = new ArrayList<>();
    for (Joint joint : joints) {
      ids.add(joint.jointID.getValue());
    }
    return ids;
  }
}
