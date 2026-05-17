package org.lgna.story.implementation;

import org.junit.Test;
import org.lgna.story.BipedPose;
import org.lgna.story.BipedPoseBuilder;
import org.lgna.story.FlyerPose;
import org.lgna.story.FlyerPoseBuilder;
import org.lgna.story.Pose;
import org.lgna.story.PoseBuilder;
import org.lgna.story.QuadrupedPose;
import org.lgna.story.QuadrupedPoseBuilder;
import org.lgna.story.SBiped;
import org.lgna.story.SFlyer;
import org.lgna.story.SQuadruped;
import org.lgna.story.resources.BipedResource;
import org.lgna.story.resources.FlyerResource;
import org.lgna.story.resources.JointId;
import org.lgna.story.resources.JointedModelResource;
import org.lgna.story.resources.QuadrupedResource;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Tests for the pure-static utility methods in PoseUtilities.
 * All methods tested here are class-mapping lookups with no AWT/rendering dependency.
 */
public class PoseUtilitiesTest {

  // ── getPoseClassForModelClass ──

  @Test
  public void getPoseClassForBipedReturnsBipedPose() {
    Class<?> cls = PoseUtilities.getPoseClassForModelClass(SBiped.class);
    assertEquals(BipedPose.class, cls);
  }

  @Test
  public void getPoseClassForQuadrupedReturnsQuadrupedPose() {
    Class<?> cls = PoseUtilities.getPoseClassForModelClass(SQuadruped.class);
    assertEquals(QuadrupedPose.class, cls);
  }

  @Test
  public void getPoseClassForFlyerReturnsFlyerPose() {
    Class<?> cls = PoseUtilities.getPoseClassForModelClass(SFlyer.class);
    assertEquals(FlyerPose.class, cls);
  }

  @Test(expected = RuntimeException.class)
  public void getPoseClassForUnknownThrows() {
    PoseUtilities.getPoseClassForModelClass(null);
  }

  // ── getBuilderClassForModelClass ──

  @Test
  public void getBuilderClassForBipedReturnsBipedPoseBuilder() {
    Class<?> cls = PoseUtilities.getBuilderClassForModelClass(SBiped.class);
    assertEquals(BipedPoseBuilder.class, cls);
  }

  @Test
  public void getBuilderClassForQuadrupedReturnsQuadrupedPoseBuilder() {
    Class<?> cls = PoseUtilities.getBuilderClassForModelClass(SQuadruped.class);
    assertEquals(QuadrupedPoseBuilder.class, cls);
  }

  @Test
  public void getBuilderClassForFlyerReturnsFlyerPoseBuilder() {
    Class<?> cls = PoseUtilities.getBuilderClassForModelClass(SFlyer.class);
    assertEquals(FlyerPoseBuilder.class, cls);
  }

  @Test(expected = RuntimeException.class)
  public void getBuilderClassForUnknownModelThrows() {
    PoseUtilities.getBuilderClassForModelClass(null);
  }

  // ── getBuilderClassForPoseClass ──

  @Test
  public void getBuilderClassForBipedPoseReturnsBipedPoseBuilder() {
    Class<?> cls = PoseUtilities.getBuilderClassForPoseClass(BipedPose.class);
    assertEquals(BipedPoseBuilder.class, cls);
  }

  @Test
  public void getBuilderClassForQuadrupedPoseReturnsQuadrupedPoseBuilder() {
    Class<?> cls = PoseUtilities.getBuilderClassForPoseClass(QuadrupedPose.class);
    assertEquals(QuadrupedPoseBuilder.class, cls);
  }

  @Test
  public void getBuilderClassForFlyerPoseReturnsFlyerPoseBuilder() {
    Class<?> cls = PoseUtilities.getBuilderClassForPoseClass(FlyerPose.class);
    assertEquals(FlyerPoseBuilder.class, cls);
  }

  @Test(expected = RuntimeException.class)
  public void getBuilderClassForUnknownPoseThrows() {
    PoseUtilities.getBuilderClassForPoseClass(null);
  }

  // ── createBuilderForPoseClass ──

  @Test
  public void createBuilderForBipedPoseCreatesInstance() {
    PoseBuilder<?, ?> builder = PoseUtilities.createBuilderForPoseClass(BipedPose.class);
    assertNotNull(builder);
    assertTrue(builder instanceof BipedPoseBuilder);
  }

  @Test
  public void createBuilderForQuadrupedPoseCreatesInstance() {
    PoseBuilder<?, ?> builder = PoseUtilities.createBuilderForPoseClass(QuadrupedPose.class);
    assertNotNull(builder);
    assertTrue(builder instanceof QuadrupedPoseBuilder);
  }

  @Test
  public void createBuilderForFlyerPoseCreatesInstance() {
    PoseBuilder<?, ?> builder = PoseUtilities.createBuilderForPoseClass(FlyerPose.class);
    assertNotNull(builder);
    assertTrue(builder instanceof FlyerPoseBuilder);
  }

  // ── getResourceClassFromModelClass ──

  @Test
  public void getResourceClassForBipedReturnsBipedResource() {
    Class<?> cls = PoseUtilities.getResourceClassFromModelClass(SBiped.class);
    assertEquals(BipedResource.class, cls);
  }

  @Test
  public void getResourceClassForQuadrupedReturnsQuadrupedResource() {
    Class<?> cls = PoseUtilities.getResourceClassFromModelClass(SQuadruped.class);
    assertEquals(QuadrupedResource.class, cls);
  }

  @Test
  public void getResourceClassForFlyerReturnsFlyerResource() {
    Class<?> cls = PoseUtilities.getResourceClassFromModelClass(SFlyer.class);
    assertEquals(FlyerResource.class, cls);
  }

  @Test(expected = RuntimeException.class)
  public void getResourceClassForUnknownThrows() {
    PoseUtilities.getResourceClassFromModelClass(null);
  }

  // ── getBuilderClassForModelResourceClass ──

  @Test
  public void getBuilderClassForBipedResourceReturnsBipedPoseBuilder() {
    Class<?> cls = PoseUtilities.getBuilderClassForModelResourceClass(BipedResource.class);
    assertEquals(BipedPoseBuilder.class, cls);
  }

  @Test
  public void getBuilderClassForQuadrupedResourceReturnsQuadrupedPoseBuilder() {
    Class<?> cls = PoseUtilities.getBuilderClassForModelResourceClass(QuadrupedResource.class);
    assertEquals(QuadrupedPoseBuilder.class, cls);
  }

  @Test
  public void getBuilderClassForFlyerResourceReturnsFlyerPoseBuilder() {
    Class<?> cls = PoseUtilities.getBuilderClassForModelResourceClass(FlyerResource.class);
    assertEquals(FlyerPoseBuilder.class, cls);
  }

  @Test
  public void getBuilderClassForUnknownResourceReturnsNull() {
    Class<?> cls = PoseUtilities.getBuilderClassForModelResourceClass(JointedModelResource.class);
    assertNull(cls);
  }

  // ── getJointIdRoots ──

  @Test
  public void getJointIdRootsForBipedReturnsNonEmpty() {
    JointId[] roots = PoseUtilities.getJointIdRoots(BipedResource.class);
    assertNotNull(roots);
    assertTrue(roots.length > 0);
  }

  @Test
  public void getJointIdRootsForQuadrupedReturnsNonEmpty() {
    JointId[] roots = PoseUtilities.getJointIdRoots(QuadrupedResource.class);
    assertNotNull(roots);
    assertTrue(roots.length > 0);
  }

  @Test
  public void getJointIdRootsForFlyerReturnsNonEmpty() {
    JointId[] roots = PoseUtilities.getJointIdRoots(FlyerResource.class);
    assertNotNull(roots);
    assertTrue(roots.length > 0);
  }

  @Test(expected = RuntimeException.class)
  public void getJointIdRootsForUnknownThrows() {
    PoseUtilities.getJointIdRoots(JointedModelResource.class);
  }

  // ── getDefaultJoints ──

  @Test
  public void getDefaultJointsForBipedReturnsNonEmpty() {
    JointId[] joints = PoseUtilities.getDefaultJoints(BipedResource.class);
    assertNotNull(joints);
    assertTrue(joints.length > 0);
  }

  @Test
  public void getDefaultJointsForQuadrupedReturnsNonEmpty() {
    JointId[] joints = PoseUtilities.getDefaultJoints(QuadrupedResource.class);
    assertNotNull(joints);
    assertTrue(joints.length > 0);
  }

  @Test
  public void getDefaultJointsForFlyerReturnsNonEmpty() {
    JointId[] joints = PoseUtilities.getDefaultJoints(FlyerResource.class);
    assertNotNull(joints);
    assertTrue(joints.length > 0);
  }

  // ── getCatchAllPoseBuilderMethod ──

  @Test
  public void getCatchAllPoseBuilderMethodForBipedIsNotNull() {
    Method m = PoseUtilities.getCatchAllPoseBuilderMethod(BipedPoseBuilder.class);
    assertNotNull(m);
    assertEquals(2, m.getParameterCount());
  }

  @Test
  public void getCatchAllPoseBuilderMethodForQuadrupedIsNotNull() {
    Method m = PoseUtilities.getCatchAllPoseBuilderMethod(QuadrupedPoseBuilder.class);
    assertNotNull(m);
  }

  @Test
  public void getCatchAllPoseBuilderMethodForFlyerIsNotNull() {
    Method m = PoseUtilities.getCatchAllPoseBuilderMethod(FlyerPoseBuilder.class);
    assertNotNull(m);
  }
}
