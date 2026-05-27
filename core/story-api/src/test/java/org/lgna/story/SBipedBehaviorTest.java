package org.lgna.story;

import org.junit.Test;
import org.lgna.ik.core.IKCore.Limb;
import org.lgna.story.resources.BipedResource;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;

public class SBipedBehaviorTest {
  @Test
  public void jointAccessorsReturnCachedFacadeJoints() {
    SBiped biped = new SBiped(new JointedModelStubSupport.StubBipedResource());

    assertSame(biped.getRoot(), biped.getJoint(BipedResource.ROOT));
    assertSame(biped.getHead(), biped.getJoint(BipedResource.HEAD));
    assertSame(biped.getRightWrist(), biped.getJoint(BipedResource.RIGHT_WRIST));
    assertSame(biped.getLeftHand(), biped.getLeftHand());
  }

  @Test
  public void walkToLeavesJointCacheStable() {
    SBiped biped = new SBiped(new JointedModelStubSupport.StubBipedResource());
    SJoint head = biped.getHead();

    biped.walkTo(new SThingMarker());

    assertSame(head, biped.getHead());
  }

  @Test
  public void touchAndReachForRequireNonNullTargets() {
    SBiped biped = new SBiped(new JointedModelStubSupport.StubBipedResource());

    assertThrows(NullPointerException.class, () -> biped.reachFor(Limb.RIGHT_ARM, null));
    assertThrows(NullPointerException.class, () -> biped.touch(null));
    assertSame(biped.getRightClavicle(), biped.getJoint(BipedResource.RIGHT_CLAVICLE));
  }
}
