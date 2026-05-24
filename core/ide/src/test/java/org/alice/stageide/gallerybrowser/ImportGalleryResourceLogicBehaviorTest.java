package org.alice.stageide.gallerybrowser;

import edu.cmu.cs.dennisc.scenegraph.Joint;
import org.alice.tweedle.file.ModelManifest;
import org.junit.Test;
import org.lgna.project.annotations.Visibility;
import org.lgna.story.resources.JointId;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ImportGalleryResourceLogicBehaviorTest {
  private static final class NamedJointId extends JointId {
    private final String name;
    private final Visibility visibility;

    NamedJointId(String name, NamedJointId parent, Visibility visibility) {
      super(parent, null);
      this.name = name;
      this.visibility = visibility;
    }

    @Override
    public Visibility getVisibility() {
      return visibility;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  private static ModelManifest.Joint joint(String name, String parent) {
    ModelManifest.Joint joint = new ModelManifest.Joint();
    joint.name = name;
    joint.parent = parent;
    return joint;
  }

  private static void assertConstructorThrowsAssertionError(Class<?> type) throws Exception {
    Constructor<?> constructor = type.getDeclaredConstructor();
    constructor.setAccessible(true);
    try {
      constructor.newInstance();
      fail("Expected constructor to reject instantiation");
    } catch (InvocationTargetException ite) {
      assertTrue(ite.getCause() instanceof AssertionError);
    }
  }

  @Test
  public void utilityConstructorRejectsInstantiation() throws Exception {
    assertConstructorThrowsAssertionError(ImportGalleryResourceLogic.class);
  }

  @Test
  public void addMissingJointsRevisitsChildrenAfterTheirParentsAreAdded() {
    Joint skeletonRoot = new Joint();
    skeletonRoot.jointID.setValue("root");
    Joint hip = new Joint();
    hip.jointID.setValue("hip");
    hip.setParent(skeletonRoot);
    List<ModelManifest.Joint> missing = new ArrayList<>(List.of(joint("knee", "hip"), joint("ankle", "knee")));

    ImportGalleryResourceLogic.addMissingJoints(skeletonRoot, missing);

    assertTrue(missing.isEmpty());
    assertNotNull(skeletonRoot.getJoint("knee"));
    assertNotNull(skeletonRoot.getJoint("ankle"));
  }

  @Test
  public void createJointAndGetBaseJointsPreserveHierarchyAndVisibility() {
    NamedJointId hip = new NamedJointId("HIP", null, Visibility.PRIME_TIME);
    NamedJointId knee = new NamedJointId("KNEE", hip, Visibility.COMPLETELY_HIDDEN);

    ModelManifest.Joint joint = ImportGalleryResourceLogic.createJoint(knee);
    List<ModelManifest.Joint> baseJoints = ImportGalleryResourceLogic.getBaseJoints(List.of(hip, knee));

    assertEquals("KNEE", joint.name);
    assertEquals("HIP", joint.parent);
    assertEquals(Visibility.COMPLETELY_HIDDEN, joint.visibility);
    assertEquals(List.of("HIP", "KNEE"), baseJoints.stream().map(baseJoint -> baseJoint.name).toList());
    assertEquals("HIP", baseJoints.get(1).parent);
  }

  @Test
  public void getExtraJointsLeavesVisibilityUntouchedWhenNotRequested() {
    ModelManifest.Joint base = joint("hip", null);
    ModelManifest.Joint extra = joint("tail", "hip");

    List<ModelManifest.Joint> extraJoints = ImportGalleryResourceLogic.getExtraJoints(new ArrayList<>(List.of(base, extra)), List.of(base), false);

    assertEquals(List.of(extra), extraJoints);
    assertEquals(null, extra.visibility);
  }
}
