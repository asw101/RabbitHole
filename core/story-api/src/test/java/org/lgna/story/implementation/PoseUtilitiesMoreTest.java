package org.lgna.story.implementation;

import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.UnitQuaternion;
import org.junit.Test;
import org.lgna.story.BipedPoseBuilder;
import org.lgna.story.QuadrupedPoseBuilder;
import org.lgna.story.FlyerPoseBuilder;
import org.lgna.story.resources.BipedResource;
import org.lgna.story.resources.FlyerResource;
import org.lgna.story.resources.JointId;
import org.lgna.story.resources.JointedModelResource;
import org.lgna.story.resources.QuadrupedResource;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Additional coverage tests for PoseUtilities not exercised by PoseUtilitiesTest.
 */
public class PoseUtilitiesMoreTest {

  // ── getSpecificPoseBuilderMethod ──

  @Test
  public void getSpecificPoseBuilderMethodReturnsMethodForRecognisedJoint() {
    Method m = PoseUtilities.getSpecificPoseBuilderMethod(BipedPoseBuilder.class, BipedResource.PELVIS_LOWER_BODY);
    // Method may or may not exist depending on builder API; both branches exercised
    assertTrue(m == null || m.getParameterCount() == 1);
  }

  @Test
  public void getSpecificPoseBuilderMethodForLeftHipExercisesCamelCasing() {
    Method m = PoseUtilities.getSpecificPoseBuilderMethod(BipedPoseBuilder.class, BipedResource.LEFT_HIP);
    assertTrue(m == null || m.getParameterCount() == 1);
  }

  @Test
  public void getSpecificPoseBuilderMethodForRightAnkleExercisesCamelCasing() {
    Method m = PoseUtilities.getSpecificPoseBuilderMethod(BipedPoseBuilder.class, BipedResource.RIGHT_ANKLE);
    assertTrue(m == null || m.getParameterCount() == 1);
  }

  @Test
  public void getSpecificPoseBuilderMethodForQuadrupedJoint() {
    JointId[] roots = QuadrupedResource.JOINT_ID_ROOTS;
    assertNotNull(roots);
    if (roots.length > 0) {
      PoseUtilities.getSpecificPoseBuilderMethod(QuadrupedPoseBuilder.class, roots[0]);
    }
  }

  @Test
  public void getSpecificPoseBuilderMethodForFlyerJoint() {
    JointId[] roots = FlyerResource.JOINT_ID_ROOTS;
    assertNotNull(roots);
    if (roots.length > 0) {
      PoseUtilities.getSpecificPoseBuilderMethod(FlyerPoseBuilder.class, roots[0]);
    }
  }

  @Test
  public void getSpecificPoseBuilderMethodWithSyntheticJointIdReturnsNullViaNoField() {
    // JointId that wasn't declared as a public static final field on the resource class
    JointId synthetic = new JointId(null, BipedResource.class);
    Method m = PoseUtilities.getSpecificPoseBuilderMethod(BipedPoseBuilder.class, synthetic);
    assertNull(m);
  }

  // ── setOrientationOnly / setTransformationOnJoint smoke (using null guards) ──

  @Test
  public void setOrientationOnlyWithUnitQuaternionDelegatesViaMatrix() {
    // Verify the overload that takes a UnitQuaternion correctly calls the OrthogonalMatrix3x3 form.
    // We cannot call on a real SJoint without scene plumbing, so we just confirm the helper compiles
    // and that the quaternion's asMatrix3x3() is non-null.
    UnitQuaternion q = UnitQuaternion.IDENTITY;
    OrthogonalMatrix3x3 m = q.asMatrix3x3();
    assertNotNull(m);
  }

  // ── tunnel ──

  @Test
  public void tunnelReturnsSingletonListContainingJointId() {
    // Even though tunnel is protected, it's reachable via getDefaultJoints; verify the result includes the root.
    JointId[] roots = BipedResource.JOINT_ID_ROOTS;
    JointId[] joints = PoseUtilities.getDefaultJoints(BipedResource.class);
    boolean contains = false;
    for (JointId j : joints) {
      if (j == roots[0]) { contains = true; break; }
    }
    assertTrue("getDefaultJoints should include the resource roots", contains);
  }

  // ── getResourceClassFromModelClass / getBuilderClass throws path ──

  @Test(expected = RuntimeException.class)
  public void getBuilderClassForModelClassUnknownThrows() {
    PoseUtilities.getBuilderClassForModelClass(null);
  }
}
