package org.lgna.story.resources;

import org.junit.Test;

import static org.junit.Assert.*;

public class ResourceClassesTest {

  // ══════════════════════════════════════════════════════════════════════════
  //  JointId basics
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void jointIdConstructionWithNullParent() {
    JointId jid = new JointId(null, BipedResource.class);
    assertNotNull(jid);
    assertNull(jid.getParent());
  }

  @Test
  public void jointIdConstructionWithParent() {
    JointId parent = new JointId(null, BipedResource.class);
    JointId child = new JointId(parent, BipedResource.class);
    assertSame(parent, child.getParent());
  }

  @Test
  public void jointIdHierarchyDepthIncreases() {
    JointId root = new JointId(null, BipedResource.class);
    JointId child = new JointId(root, BipedResource.class);
    JointId grandchild = new JointId(child, BipedResource.class);
    assertTrue(child.hierarchyDepth() > root.hierarchyDepth());
    assertTrue(grandchild.hierarchyDepth() > child.hierarchyDepth());
  }

  @Test
  public void jointIdToStringNotNull() {
    JointId jid = new JointId(null, BipedResource.class);
    assertNotNull(jid.toString());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  BipedResource static JointIds
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void bipedResourceRootNotNull() {
    assertNotNull(BipedResource.ROOT);
    assertNull(BipedResource.ROOT.getParent());
  }

  @Test
  public void bipedResourceSpineJointsNotNull() {
    assertNotNull(BipedResource.SPINE_BASE);
    assertNotNull(BipedResource.SPINE_MIDDLE);
    assertNotNull(BipedResource.SPINE_UPPER);
  }

  @Test
  public void bipedResourceHeadJointsNotNull() {
    assertNotNull(BipedResource.NECK);
    assertNotNull(BipedResource.HEAD);
    assertNotNull(BipedResource.MOUTH);
    assertNotNull(BipedResource.LEFT_EYE);
    assertNotNull(BipedResource.RIGHT_EYE);
    assertNotNull(BipedResource.LEFT_EYELID);
    assertNotNull(BipedResource.RIGHT_EYELID);
  }

  @Test
  public void bipedResourceLegJointsNotNull() {
    assertNotNull(BipedResource.PELVIS_LOWER_BODY);
    assertNotNull(BipedResource.LEFT_HIP);
    assertNotNull(BipedResource.LEFT_KNEE);
    assertNotNull(BipedResource.LEFT_ANKLE);
    assertNotNull(BipedResource.LEFT_FOOT);
    assertNotNull(BipedResource.RIGHT_HIP);
    assertNotNull(BipedResource.RIGHT_KNEE);
    assertNotNull(BipedResource.RIGHT_ANKLE);
    assertNotNull(BipedResource.RIGHT_FOOT);
  }

  @Test
  public void bipedResourceArmJointsNotNull() {
    assertNotNull(BipedResource.RIGHT_CLAVICLE);
    assertNotNull(BipedResource.RIGHT_SHOULDER);
    assertNotNull(BipedResource.RIGHT_ELBOW);
    assertNotNull(BipedResource.RIGHT_WRIST);
    assertNotNull(BipedResource.RIGHT_HAND);
    assertNotNull(BipedResource.LEFT_CLAVICLE);
    assertNotNull(BipedResource.LEFT_SHOULDER);
    assertNotNull(BipedResource.LEFT_ELBOW);
    assertNotNull(BipedResource.LEFT_WRIST);
    assertNotNull(BipedResource.LEFT_HAND);
  }

  @Test
  public void bipedResourceFingerJointsNotNull() {
    assertNotNull(BipedResource.RIGHT_THUMB);
    assertNotNull(BipedResource.RIGHT_THUMB_KNUCKLE);
    assertNotNull(BipedResource.RIGHT_INDEX_FINGER);
    assertNotNull(BipedResource.RIGHT_INDEX_FINGER_KNUCKLE);
    assertNotNull(BipedResource.RIGHT_MIDDLE_FINGER);
    assertNotNull(BipedResource.RIGHT_MIDDLE_FINGER_KNUCKLE);
    assertNotNull(BipedResource.RIGHT_PINKY_FINGER);
    assertNotNull(BipedResource.RIGHT_PINKY_FINGER_KNUCKLE);
    assertNotNull(BipedResource.LEFT_THUMB);
    assertNotNull(BipedResource.LEFT_THUMB_KNUCKLE);
    assertNotNull(BipedResource.LEFT_INDEX_FINGER);
    assertNotNull(BipedResource.LEFT_INDEX_FINGER_KNUCKLE);
    assertNotNull(BipedResource.LEFT_MIDDLE_FINGER);
    assertNotNull(BipedResource.LEFT_MIDDLE_FINGER_KNUCKLE);
    assertNotNull(BipedResource.LEFT_PINKY_FINGER);
    assertNotNull(BipedResource.LEFT_PINKY_FINGER_KNUCKLE);
  }

  @Test
  public void bipedResourceParentRelationships() {
    // LEFT_HIP's parent should be PELVIS_LOWER_BODY
    assertSame(BipedResource.PELVIS_LOWER_BODY, BipedResource.LEFT_HIP.getParent());
    assertSame(BipedResource.LEFT_HIP, BipedResource.LEFT_KNEE.getParent());
    assertSame(BipedResource.LEFT_KNEE, BipedResource.LEFT_ANKLE.getParent());
    // RIGHT arm chain
    assertSame(BipedResource.RIGHT_SHOULDER, BipedResource.RIGHT_ELBOW.getParent());
    assertSame(BipedResource.RIGHT_ELBOW, BipedResource.RIGHT_WRIST.getParent());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  QuadrupedResource static JointIds
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void quadrupedResourceRootNotNull() {
    assertNotNull(QuadrupedResource.ROOT);
    assertNull(QuadrupedResource.ROOT.getParent());
  }

  @Test
  public void quadrupedResourceSpineJointsNotNull() {
    assertNotNull(QuadrupedResource.SPINE_BASE);
    assertNotNull(QuadrupedResource.SPINE_MIDDLE);
    assertNotNull(QuadrupedResource.SPINE_UPPER);
  }

  @Test
  public void quadrupedResourceHeadJointsNotNull() {
    assertNotNull(QuadrupedResource.NECK);
    assertNotNull(QuadrupedResource.HEAD);
    assertNotNull(QuadrupedResource.MOUTH);
    assertNotNull(QuadrupedResource.LEFT_EYE);
    assertNotNull(QuadrupedResource.RIGHT_EYE);
    assertNotNull(QuadrupedResource.LEFT_EYELID);
    assertNotNull(QuadrupedResource.RIGHT_EYELID);
    assertNotNull(QuadrupedResource.LEFT_EAR);
    assertNotNull(QuadrupedResource.RIGHT_EAR);
  }

  @Test
  public void quadrupedResourceFrontLegJointsNotNull() {
    assertNotNull(QuadrupedResource.FRONT_LEFT_CLAVICLE);
    assertNotNull(QuadrupedResource.FRONT_LEFT_SHOULDER);
    assertNotNull(QuadrupedResource.FRONT_LEFT_KNEE);
    assertNotNull(QuadrupedResource.FRONT_LEFT_ANKLE);
    assertNotNull(QuadrupedResource.FRONT_LEFT_FOOT);
    assertNotNull(QuadrupedResource.FRONT_LEFT_TOE);
    assertNotNull(QuadrupedResource.FRONT_RIGHT_CLAVICLE);
    assertNotNull(QuadrupedResource.FRONT_RIGHT_SHOULDER);
    assertNotNull(QuadrupedResource.FRONT_RIGHT_KNEE);
    assertNotNull(QuadrupedResource.FRONT_RIGHT_ANKLE);
    assertNotNull(QuadrupedResource.FRONT_RIGHT_FOOT);
    assertNotNull(QuadrupedResource.FRONT_RIGHT_TOE);
  }

  @Test
  public void quadrupedResourceBackLegJointsNotNull() {
    assertNotNull(QuadrupedResource.PELVIS_LOWER_BODY);
    assertNotNull(QuadrupedResource.BACK_LEFT_HIP);
    assertNotNull(QuadrupedResource.BACK_LEFT_KNEE);
    assertNotNull(QuadrupedResource.BACK_LEFT_HOCK);
    assertNotNull(QuadrupedResource.BACK_LEFT_ANKLE);
    assertNotNull(QuadrupedResource.BACK_LEFT_FOOT);
    assertNotNull(QuadrupedResource.BACK_LEFT_TOE);
    assertNotNull(QuadrupedResource.BACK_RIGHT_HIP);
    assertNotNull(QuadrupedResource.BACK_RIGHT_KNEE);
    assertNotNull(QuadrupedResource.BACK_RIGHT_HOCK);
    assertNotNull(QuadrupedResource.BACK_RIGHT_ANKLE);
    assertNotNull(QuadrupedResource.BACK_RIGHT_FOOT);
    assertNotNull(QuadrupedResource.BACK_RIGHT_TOE);
  }

  @Test
  public void quadrupedResourceParentRelationships() {
    assertSame(QuadrupedResource.FRONT_LEFT_CLAVICLE, QuadrupedResource.FRONT_LEFT_SHOULDER.getParent());
    assertSame(QuadrupedResource.FRONT_LEFT_SHOULDER, QuadrupedResource.FRONT_LEFT_KNEE.getParent());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  FlyerResource static JointIds
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void flyerResourceRootNotNull() {
    assertNotNull(FlyerResource.ROOT);
    assertNull(FlyerResource.ROOT.getParent());
  }

  @Test
  public void flyerResourceSpineJointsNotNull() {
    assertNotNull(FlyerResource.SPINE_BASE);
    assertNotNull(FlyerResource.SPINE_MIDDLE);
    assertNotNull(FlyerResource.SPINE_UPPER);
  }

  @Test
  public void flyerResourceHeadJointsNotNull() {
    assertNotNull(FlyerResource.HEAD);
    assertNotNull(FlyerResource.MOUTH);
    assertNotNull(FlyerResource.LEFT_EYE);
    assertNotNull(FlyerResource.RIGHT_EYE);
    assertNotNull(FlyerResource.LEFT_EYELID);
    assertNotNull(FlyerResource.RIGHT_EYELID);
  }

  @Test
  public void flyerResourceWingJointsNotNull() {
    assertNotNull(FlyerResource.LEFT_WING_SHOULDER);
    assertNotNull(FlyerResource.LEFT_WING_ELBOW);
    assertNotNull(FlyerResource.LEFT_WING_WRIST);
    assertNotNull(FlyerResource.LEFT_WING_TIP);
    assertNotNull(FlyerResource.RIGHT_WING_SHOULDER);
    assertNotNull(FlyerResource.RIGHT_WING_ELBOW);
    assertNotNull(FlyerResource.RIGHT_WING_WRIST);
    assertNotNull(FlyerResource.RIGHT_WING_TIP);
  }

  @Test
  public void flyerResourceLegJointsNotNull() {
    assertNotNull(FlyerResource.PELVIS_LOWER_BODY);
    assertNotNull(FlyerResource.LEFT_HIP);
    assertNotNull(FlyerResource.LEFT_KNEE);
    assertNotNull(FlyerResource.LEFT_ANKLE);
    assertNotNull(FlyerResource.LEFT_FOOT);
    assertNotNull(FlyerResource.RIGHT_HIP);
    assertNotNull(FlyerResource.RIGHT_KNEE);
    assertNotNull(FlyerResource.RIGHT_ANKLE);
    assertNotNull(FlyerResource.RIGHT_FOOT);
  }

  @Test
  public void flyerResourceWingParentRelationships() {
    assertSame(FlyerResource.LEFT_WING_SHOULDER, FlyerResource.LEFT_WING_ELBOW.getParent());
    assertSame(FlyerResource.LEFT_WING_ELBOW, FlyerResource.LEFT_WING_WRIST.getParent());
    assertSame(FlyerResource.LEFT_WING_WRIST, FlyerResource.LEFT_WING_TIP.getParent());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  SwimmerResource static JointIds
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void swimmerResourceRootNotNull() {
    assertNotNull(SwimmerResource.ROOT);
    assertNull(SwimmerResource.ROOT.getParent());
  }

  @Test
  public void swimmerResourceJointsNotNull() {
    assertNotNull(SwimmerResource.NECK);
    assertNotNull(SwimmerResource.HEAD);
    assertNotNull(SwimmerResource.MOUTH);
    assertNotNull(SwimmerResource.LEFT_EYE);
    assertNotNull(SwimmerResource.RIGHT_EYE);
    assertNotNull(SwimmerResource.LEFT_EYELID);
    assertNotNull(SwimmerResource.RIGHT_EYELID);
    assertNotNull(SwimmerResource.FRONT_LEFT_FIN);
    assertNotNull(SwimmerResource.FRONT_RIGHT_FIN);
    assertNotNull(SwimmerResource.SPINE_BASE);
    assertNotNull(SwimmerResource.SPINE_MIDDLE);
    assertNotNull(SwimmerResource.TAIL);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  SlithererResource static JointIds
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void slithererResourceRootNotNull() {
    assertNotNull(SlithererResource.ROOT);
    assertNull(SlithererResource.ROOT.getParent());
  }

  @Test
  public void slithererResourceSpineJointsNotNull() {
    assertNotNull(SlithererResource.SPINE_BASE);
    assertNotNull(SlithererResource.SPINE_MIDDLE);
    assertNotNull(SlithererResource.SPINE_UPPER);
  }

  @Test
  public void slithererResourceHeadJointsNotNull() {
    assertNotNull(SlithererResource.NECK);
    assertNotNull(SlithererResource.HEAD);
    assertNotNull(SlithererResource.MOUTH);
    assertNotNull(SlithererResource.LEFT_EYE);
    assertNotNull(SlithererResource.RIGHT_EYE);
    assertNotNull(SlithererResource.LEFT_EYELID);
    assertNotNull(SlithererResource.RIGHT_EYELID);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  JointId descendantComparison
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void jointIdDescendantComparisonSelf() {
    assertEquals(0, BipedResource.ROOT.descendantComparison(BipedResource.ROOT));
  }

  @Test
  public void jointIdDescendantComparisonParentVsChild() {
    // Parent is less deep → negative when parent.descendantComparison(child)
    int result = BipedResource.PELVIS_LOWER_BODY.descendantComparison(BipedResource.LEFT_HIP);
    assertTrue(result != 0);
  }
}
