package org.lgna.story;

import org.junit.Test;
import org.lgna.story.resources.FlyerResource;

import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class SFlyerBehaviorTest {
  @Test
  public void jointAccessorsExposeWingNeckAndTailFacades() {
    SFlyer flyer = new SFlyer(new JointedModelStubSupport.StubFlyerResource());

    assertSame(flyer.getRoot(), flyer.getJoint(FlyerResource.ROOT));
    assertSame(flyer.getNeck(), flyer.getNeckArray()[0]);
    assertSame(flyer.getTail(), flyer.getTailArray()[0]);
    assertSame(flyer.getLeftWingWrist(), flyer.getJoint(FlyerResource.LEFT_WING_WRIST));
    assertSame(flyer.getTail2(), flyer.getJoint(FlyerResource.TAIL_1));
    assertSame(flyer.getTail3(), flyer.getJoint(FlyerResource.TAIL_2));
  }

  @Test
  public void walkToAndTouchShowHeadlessDialogsInHeadlessRuns() {
    SFlyer flyer = new SFlyer(new JointedModelStubSupport.StubFlyerResource());
    assertTrue(GraphicsEnvironment.isHeadless());

    assertThrows(HeadlessException.class, () -> flyer.walkTo(new SThingMarker()));
    assertThrows(HeadlessException.class, () -> flyer.touch(new SThingMarker()));
  }
}
