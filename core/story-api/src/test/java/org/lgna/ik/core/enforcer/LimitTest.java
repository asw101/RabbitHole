package org.lgna.ik.core.enforcer;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Characterization tests for the Limit class hierarchy:
 * - Limit (empty base)
 * - SingleAxisLimit (min/max angle bounds)
 * - IndependentBallJointLimit (3 axis limits for ball joints)
 *
 * These are clean extractions with no context dependency.
 */
public class LimitTest {

  private static final double DELTA = 1e-12;

  @Test
  public void limitBaseClassCanBeInstantiated() {
    Limit limit = new Limit();
    assertNotNull(limit);
  }

  @Test
  public void singleAxisLimitStoresMinMax() {
    SingleAxisLimit sal = new SingleAxisLimit(-1.5, 2.5);
    assertEquals(-1.5, sal.getMinAngle(), DELTA);
    assertEquals(2.5, sal.getMaxAngle(), DELTA);
  }

  @Test
  public void singleAxisLimitWithNegativeRange() {
    SingleAxisLimit sal = new SingleAxisLimit(-Math.PI, -Math.PI / 2);
    assertEquals(-Math.PI, sal.getMinAngle(), DELTA);
    assertEquals(-Math.PI / 2, sal.getMaxAngle(), DELTA);
  }

  @Test
  public void singleAxisLimitWithZeroRange() {
    SingleAxisLimit sal = new SingleAxisLimit(0.0, 0.0);
    assertEquals(0.0, sal.getMinAngle(), DELTA);
    assertEquals(0.0, sal.getMaxAngle(), DELTA);
  }

  @Test
  public void singleAxisLimitExtends_Limit() {
    SingleAxisLimit sal = new SingleAxisLimit(0, 1);
    assertTrue(sal instanceof Limit);
  }

  @Test
  public void independentBallJointLimitExtendsLimit() {
    IndependentBallJointLimit ibjl = new IndependentBallJointLimit();
    assertTrue(ibjl instanceof Limit);
  }

  @Test
  public void independentBallJointLimitFieldsDefaultNull() {
    IndependentBallJointLimit ibjl = new IndependentBallJointLimit();
    assertNull(ibjl.aroundFirstBoneLimit);
    assertNull(ibjl.aroundSecondBoneLimit);
    assertNull(ibjl.betweenBonesLimit);
  }

  @Test
  public void independentBallJointLimitAcceptsAxisLimits() {
    IndependentBallJointLimit ibjl = new IndependentBallJointLimit();
    ibjl.aroundFirstBoneLimit = new SingleAxisLimit(-1.0, 1.0);
    ibjl.aroundSecondBoneLimit = new SingleAxisLimit(-0.5, 0.5);
    ibjl.betweenBonesLimit = new SingleAxisLimit(0.0, Math.PI);

    assertEquals(-1.0, ibjl.aroundFirstBoneLimit.getMinAngle(), DELTA);
    assertEquals(0.5, ibjl.aroundSecondBoneLimit.getMaxAngle(), DELTA);
    assertEquals(Math.PI, ibjl.betweenBonesLimit.getMaxAngle(), DELTA);
  }
}
