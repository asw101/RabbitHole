package org.lgna.story;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.story.resources.QuadrupedResource;

import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;

public class SQuadrupedBehaviorTest {
  @Test
  public void jointAccessorsExposeTailAndLegFacades() {
    SQuadruped quadruped = new SQuadruped(new JointedModelStubSupport.StubQuadrupedResource());

    assertSame(quadruped.getRoot(), quadruped.getJoint(QuadrupedResource.ROOT));
    assertSame(quadruped.getFrontLeftFoot(), quadruped.getJoint(QuadrupedResource.FRONT_LEFT_FOOT));
    assertSame(quadruped.getBackRightToe(), quadruped.getJoint(QuadrupedResource.BACK_RIGHT_TOE));
    assertSame(quadruped.getTail(), quadruped.getTailArray()[0]);
  }

  @Test
  public void deprecatedTailAliasesStillPointAtTailSegments() {
    SQuadruped quadruped = new SQuadruped(new JointedModelStubSupport.StubQuadrupedResource());

    assertSame(quadruped.getTail(), quadruped.getTail1());
    assertSame(quadruped.getTail2(), quadruped.getJoint(QuadrupedResource.TAIL_1));
    assertSame(quadruped.getTail3(), quadruped.getJoint(QuadrupedResource.TAIL_2));
    assertSame(quadruped.getTail4(), quadruped.getJoint(QuadrupedResource.TAIL_3));
  }

  @Test
  public void walkToAndTouchShowHeadlessDialogsInHeadlessRuns() {
    SQuadruped quadruped = new SQuadruped(new JointedModelStubSupport.StubQuadrupedResource());
    Assume.assumeTrue("HeadlessException behavior only applies in headless mode",
        GraphicsEnvironment.isHeadless());

    assertThrows(HeadlessException.class, () -> quadruped.walkTo(new SThingMarker()));
    assertThrows(HeadlessException.class, () -> quadruped.touch(new SThingMarker()));
  }
}
