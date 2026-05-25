package org.alice.stageide.gallerybrowser;

import org.alice.tweedle.file.ModelManifest;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ImportGalleryResourceCompositeHelperTest {
  @Test
  public void isSkeletonMissingRequiresBaseJointsAndMissingSkeleton() {
    assertTrue(ImportGalleryResourceCompositeHelper.isSkeletonMissing(null, List.of(joint("hip", null))));
    assertFalse(ImportGalleryResourceCompositeHelper.isSkeletonMissing(new Object(), List.of(joint("hip", null))));
    assertFalse(ImportGalleryResourceCompositeHelper.isSkeletonMissing(null, List.of()));
  }

  @Test
  public void buildMissingJointsMessageInterpolatesClassAndJointList() {
    String message = ImportGalleryResourceCompositeHelper.buildMissingJointsMessage(
        "Missing </class/> joints: </joints/>",
        "Bunny",
        List.of(joint("hip", null), joint("knee", "hip")));

    assertEquals("Missing Bunny joints: hip, knee (->hip)", message);
  }

  @Test
  public void calculateScaleChangeUsesPreviousScale() {
    assertEquals(1.5, ImportGalleryResourceCompositeHelper.calculateScaleChange(3.0, 2.0), 0.00001);
  }

  private static ModelManifest.Joint joint(String name, String parent) {
    ModelManifest.Joint joint = new ModelManifest.Joint();
    joint.name = name;
    joint.parent = parent;
    return joint;
  }
}
