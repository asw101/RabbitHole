package org.alice.stageide.gallerybrowser;

import edu.cmu.cs.dennisc.scenegraph.Joint;
import org.alice.tweedle.file.ModelManifest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ImportGalleryResourceLogicEdgeTest {
  private static ModelManifest.Joint joint(String name, String parent) {
    ModelManifest.Joint joint = new ModelManifest.Joint();
    joint.name = name;
    joint.parent = parent;
    return joint;
  }

  @Test
  public void addJointsToListIgnoresNullScenegraphRoots() {
    List<ModelManifest.Joint> joints = new ArrayList<>();

    ImportGalleryResourceLogic.addJointsToList(null, joints);

    assertTrue(joints.isEmpty());
  }

  @Test
  public void addMissingJointsStopsWhenTheCurrentJointHasNoParent() {
    Joint skeletonRoot = new Joint();
    skeletonRoot.jointID.setValue("root");
    List<ModelManifest.Joint> missing = new ArrayList<>(List.of(
        joint("floating", null),
        joint("child", "floating")));

    ImportGalleryResourceLogic.addMissingJoints(skeletonRoot, missing);

    assertEquals(List.of("floating", "child"), missing.stream().map(joint -> joint.name).toList());
    assertNull(skeletonRoot.getJoint("floating"));
    assertNull(skeletonRoot.getJoint("child"));
  }

  @Test
  public void createJointFromScenegraphRootLeavesParentAndVisibilityUnset() {
    Joint root = new Joint();
    root.jointID.setValue("root");

    ModelManifest.Joint manifestJoint = ImportGalleryResourceLogic.createJoint(root);

    assertEquals("root", manifestJoint.name);
    assertNull(manifestJoint.parent);
    assertNull(manifestJoint.visibility);
  }
}
