package org.lgna.story;

import org.junit.Test;
import org.lgna.story.implementation.JointedModelImp;
import org.lgna.story.resources.BipedResource;
import org.lgna.story.resources.FlyerResource;
import org.lgna.story.resources.JointId;
import org.lgna.story.resources.QuadrupedResource;
import org.lgna.story.resources.SwimmerResource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class JointedModelImplementationBehaviorTest {
  @Test
  public void bipedImplementationKeepsStubResourceAndJointAbstractions() {
    JointedModelStubSupport.StubBipedResource resource = new JointedModelStubSupport.StubBipedResource();
    SBiped biped = new SBiped(resource);

    assertImplementationState(resource, biped, BipedResource.HEAD, biped.getHead());
    assertSame(biped.getRightWrist(), biped.getImplementation().getJointImplementation(BipedResource.RIGHT_WRIST).getAbstraction());
  }

  @Test
  public void quadrupedImplementationKeepsStubResourceAndJointAbstractions() {
    JointedModelStubSupport.StubQuadrupedResource resource = new JointedModelStubSupport.StubQuadrupedResource();
    SQuadruped quadruped = new SQuadruped(resource);

    assertImplementationState(resource, quadruped, QuadrupedResource.HEAD, quadruped.getHead());
    assertSame(quadruped.getMouth(), quadruped.getImplementation().getJointImplementation(QuadrupedResource.MOUTH).getAbstraction());
  }

  @Test
  public void flyerImplementationKeepsStubResourceAndJointAbstractions() {
    JointedModelStubSupport.StubFlyerResource resource = new JointedModelStubSupport.StubFlyerResource();
    SFlyer flyer = new SFlyer(resource);

    assertImplementationState(resource, flyer, FlyerResource.HEAD, flyer.getHead());
    assertSame(flyer.getLeftWingWrist(), flyer.getImplementation().getJointImplementation(FlyerResource.LEFT_WING_WRIST).getAbstraction());
  }

  @Test
  public void swimmerImplementationKeepsStubResourceAndJointAbstractions() {
    JointedModelStubSupport.StubSwimmerResource resource = new JointedModelStubSupport.StubSwimmerResource();
    SSwimmer swimmer = new SSwimmer(resource);

    assertImplementationState(resource, swimmer, SwimmerResource.HEAD, swimmer.getHead());
    assertSame(swimmer.getTail(), swimmer.getImplementation().getJointImplementation(SwimmerResource.TAIL).getAbstraction());
  }

  private static void assertImplementationState(
      Object resource,
      SJointedModel abstraction,
      JointId jointId,
      SJoint expectedJoint
  ) {
    JointedModelImp<?, ?> implementation = abstraction.getImplementation();

    assertSame(abstraction, implementation.getAbstraction());
    assertSame(resource, implementation.getResource());
    assertNotNull(implementation.getSgVisuals());
    assertEquals(1, implementation.getSgVisuals().length);
    assertNotNull(implementation.getSgVisuals()[0]);
    assertSame(expectedJoint, implementation.getJointImplementation(jointId).getAbstraction());
  }
}
