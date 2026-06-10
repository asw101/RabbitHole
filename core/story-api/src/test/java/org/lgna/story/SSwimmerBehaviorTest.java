package org.lgna.story;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.story.resources.SwimmerResource;

import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;

public class SSwimmerBehaviorTest {
  @Test
  public void jointAccessorsExposeHeadFinAndTailFacades() {
    SSwimmer swimmer = new SSwimmer(new JointedModelStubSupport.StubSwimmerResource());

    assertSame(swimmer.getRoot(), swimmer.getJoint(SwimmerResource.ROOT));
    assertSame(swimmer.getHead(), swimmer.getJoint(SwimmerResource.HEAD));
    assertSame(swimmer.getFrontLeftFin(), swimmer.getJoint(SwimmerResource.FRONT_LEFT_FIN));
    assertSame(swimmer.getTail(), swimmer.getJoint(SwimmerResource.TAIL));
    assertSame(swimmer.getRightEye(), swimmer.getJoint(SwimmerResource.RIGHT_EYE));
  }

  @Test
  public void swimToShowsHeadlessDialogInHeadlessRuns() {
    SSwimmer swimmer = new SSwimmer(new JointedModelStubSupport.StubSwimmerResource());
    Assume.assumeTrue("HeadlessException behavior only applies in headless mode",
        GraphicsEnvironment.isHeadless());

    assertThrows(HeadlessException.class, () -> swimmer.swimTo(new SThingMarker()));
  }
}
