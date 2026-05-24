package org.alice.stageide.gallerybrowser;

import edu.cmu.cs.dennisc.scenegraph.Joint;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import org.alice.tweedle.file.ModelManifest;
import org.junit.Test;
import org.lgna.project.annotations.Visibility;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ImportGalleryResourceLogicTest {
  private static ModelManifest.Joint joint(String name, String parent) {
    ModelManifest.Joint joint = new ModelManifest.Joint();
    joint.name = name;
    joint.parent = parent;
    return joint;
  }

  @Test
  public void getModelJointsTraversesHierarchy() {
    Joint root = new Joint();
    root.jointID.setValue("root");
    Joint arm = new Joint();
    arm.jointID.setValue("arm");
    arm.setParent(root);

    List<ModelManifest.Joint> joints = ImportGalleryResourceLogic.getModelJoints(root);

    assertEquals(2, joints.size());
    assertEquals("root", joints.get(0).name);
    assertEquals("arm", joints.get(1).name);
    assertEquals("root", joints.get(1).parent);
  }

  @Test
  public void getExtraJointsMarksVisibilityWhenRequested() {
    ModelManifest.Joint base = joint("hip", null);
    ModelManifest.Joint extra = joint("tail", "hip");

    List<ModelManifest.Joint> extraJoints = ImportGalleryResourceLogic.getExtraJoints(new ArrayList<>(List.of(base, extra)), List.of(base), true);

    assertEquals(List.of(extra), extraJoints);
    assertEquals(Visibility.PRIME_TIME, extra.visibility);
  }

  @Test
  public void getMissingJointsReturnsRequiredJointNotPresentInModel() {
    ModelManifest.Joint hip = joint("hip", null);
    ModelManifest.Joint knee = joint("knee", "hip");

    List<ModelManifest.Joint> missing = ImportGalleryResourceLogic.getMissingJoints(List.of(hip), List.of(hip, knee));

    assertEquals(List.of(knee), missing);
  }

  @Test
  public void getRootJointsFiltersOutChildren() {
    ModelManifest.Joint hip = joint("hip", null);
    ModelManifest.Joint knee = joint("knee", "hip");

    List<ModelManifest.Joint> roots = ImportGalleryResourceLogic.getRootJoints(List.of(hip, knee));

    assertEquals(List.of(hip), roots);
  }

  @Test
  public void createSimpleManifestCapturesBoundsAndResourceNames() {
    AxisAlignedBox box = new AxisAlignedBox(new Point3(-1, -2, -3), new Point3(4, 5, 6));

    ModelManifest manifest = ImportGalleryResourceLogic.createSimpleManifest("Dragon", "Alice", "org.example.DragonResource", box);

    assertEquals("org.example.DragonResource", manifest.parentClass);
    assertEquals("Dragon", manifest.description.name);
    assertEquals("Alice", manifest.provenance.creator);
    assertEquals(List.of(-1.0f, -2.0f, -3.0f), manifest.boundingBox.min);
    assertEquals(List.of(4.0f, 5.0f, 6.0f), manifest.boundingBox.max);
    assertEquals("Dragon", manifest.resources.get(0).name);
    assertEquals("Dragon", manifest.textureSets.get(0).name);
    assertEquals("Dragon", manifest.models.get(0).structure);
  }
}
