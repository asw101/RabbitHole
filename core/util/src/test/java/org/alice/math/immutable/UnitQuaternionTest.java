package org.alice.math.immutable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UnitQuaternionTest {

  @Test
  void identityUnitQuaternionConversionsToAndFromShouldBeEqual() {
    checkConversionsAndBack(UnitQuaternion.IDENTITY);
  }

  @Test
  void arbitraryUnitQuaternionConversionsToAndFromShouldBeEqual() {
    checkConversionsAndBack(new UnitQuaternion(0.36801338078947526, -0.11105403604599343, 0.6129703620993356, 0.6902901475652142));
  }

  private static void checkConversionsAndBack(UnitQuaternion src) {
    assertSame(src, src.asUnitQuaternion(), "Should be equal to original angles");
    compareTo(src, src.asMatrix3x3());
    compareTo(src, src.asForwardAndUpGuide());
    compareTo(src, src.asAxisRotation());
    compareTo(src, src.asEulerAngles());
  }

  private static void compareTo(UnitQuaternion src, Orientation uq) {
    assertTrue(src.isAlignedWith(uq), "Source:\n" + src + "\nShould be the same as destination:\n" + uq);
  }

  @Test
  void isNaN_identity() {
    assertFalse(UnitQuaternion.IDENTITY.isNaN());
  }

  @Test
  void isNaN_nan() {
    assertTrue(UnitQuaternion.NaN.isNaN());
  }

  @Test
  void isIdentity_identity() {
    assertTrue(UnitQuaternion.IDENTITY.isIdentity());
  }

  @Test
  void isIdentity_nonIdentity() {
    UnitQuaternion q = new UnitQuaternion(0, 0.707, 0, 0.707);
    assertFalse(q.isIdentity());
  }

  @Test
  void isUnit_identity() {
    assertTrue(UnitQuaternion.IDENTITY.isUnit());
  }

  @Test
  void isUnit_unnormalized() {
    UnitQuaternion q = new UnitQuaternion(1, 1, 1, 1);
    assertFalse(q.isUnit());
  }

  @Test
  void negated_negatesAll() {
    UnitQuaternion q = new UnitQuaternion(1, 2, 3, 4);
    UnitQuaternion neg = q.negated();
    assertEquals(-1.0, neg.x(), 1e-10);
    assertEquals(-2.0, neg.y(), 1e-10);
    assertEquals(-3.0, neg.z(), 1e-10);
    assertEquals(-4.0, neg.w(), 1e-10);
  }

  @Test
  void plus_addsComponents() {
    UnitQuaternion a = new UnitQuaternion(1, 2, 3, 4);
    UnitQuaternion b = new UnitQuaternion(5, 6, 7, 8);
    UnitQuaternion result = a.plus(b);
    assertEquals(6.0, result.x(), 1e-10);
    assertEquals(8.0, result.y(), 1e-10);
    assertEquals(10.0, result.z(), 1e-10);
    assertEquals(12.0, result.w(), 1e-10);
  }

  @Test
  void times_quaternion() {
    UnitQuaternion a = new UnitQuaternion(1, 2, 3, 4);
    UnitQuaternion b = new UnitQuaternion(2, 3, 4, 5);
    UnitQuaternion result = a.times(b);
    assertEquals(2.0, result.x(), 1e-10);
    assertEquals(6.0, result.y(), 1e-10);
    assertEquals(12.0, result.z(), 1e-10);
    assertEquals(20.0, result.w(), 1e-10);
  }

  @Test
  void times_scalar() {
    UnitQuaternion q = new UnitQuaternion(1, 2, 3, 4);
    UnitQuaternion result = q.times(2.0);
    assertEquals(2.0, result.x(), 1e-10);
    assertEquals(4.0, result.y(), 1e-10);
    assertEquals(6.0, result.z(), 1e-10);
    assertEquals(8.0, result.w(), 1e-10);
  }

  @Test
  void normalized_identity_returnsSame() {
    assertSame(UnitQuaternion.IDENTITY, UnitQuaternion.IDENTITY.normalized());
  }

  @Test
  void normalized_unnormalized_becomesUnit() {
    UnitQuaternion q = new UnitQuaternion(0, 0, 0, 2);
    UnitQuaternion n = q.normalized();
    assertTrue(n.isUnit());
  }

  @Test
  void interpolate_atZero_returnsSelf() {
    UnitQuaternion a = UnitQuaternion.IDENTITY;
    UnitQuaternion b = new UnitQuaternion(0, 0.707, 0, 0.707);
    UnitQuaternion result = a.interpolate(b, 0.0);
    assertSame(a, result);
  }

  @Test
  void interpolate_atOne_returnsTarget() {
    UnitQuaternion a = UnitQuaternion.IDENTITY;
    UnitQuaternion b = new UnitQuaternion(0, 0.707, 0, 0.707);
    UnitQuaternion result = a.interpolate(b, 1.0);
    assertSame(b, result);
  }

  @Test
  void interpolate_atHalf() {
    UnitQuaternion a = UnitQuaternion.IDENTITY;
    UnitQuaternion b = new UnitQuaternion(0, 0.3827, 0, 0.9239);
    UnitQuaternion result = a.interpolate(b, 0.5);
    assertNotNull(result);
    assertFalse(result.isNaN());
  }

  @Test
  void interpolate_sameQuaternion_returnsTarget() {
    UnitQuaternion q = UnitQuaternion.IDENTITY;
    UnitQuaternion result = q.interpolate(q, 0.5);
    assertSame(q, result);
  }

  @Test
  void interpolate_negativeDot_usesNegated() {
    // Two quaternions representing the same rotation but with opposite signs
    UnitQuaternion a = new UnitQuaternion(0.3827, 0, 0, 0.9239);
    UnitQuaternion b = a.negated();
    UnitQuaternion result = a.interpolate(b, 0.5);
    assertNotNull(result);
  }

  @Test
  void asMatrix3x3_identity() {
    OrthogonalMatrix3x3 m = UnitQuaternion.IDENTITY.asMatrix3x3();
    assertTrue(m.isIdentity());
  }

  @Test
  void asMatrix3x3_90degAroundY() {
    double halfAngle = Math.PI / 4;
    UnitQuaternion q = new UnitQuaternion(0, Math.sin(halfAngle), 0, Math.cos(halfAngle));
    OrthogonalMatrix3x3 m = q.asMatrix3x3();
    assertFalse(m.isIdentity());
  }

  @Test
  void asAxisRotation_identity() {
    AxisRotation ar = UnitQuaternion.IDENTITY.asAxisRotation();
    assertEquals(0.0, ar.angle().getAsRadians(), 0.01);
  }

  @Test
  void asAxisRotation_nonTrivial() {
    double halfAngle = Math.PI / 4;
    UnitQuaternion q = new UnitQuaternion(0, Math.sin(halfAngle), 0, Math.cos(halfAngle));
    AxisRotation ar = q.asAxisRotation();
    assertEquals(Math.PI / 2, ar.angle().getAsRadians(), 0.01);
  }

  @Test
  void asEulerAngles_identity() {
    EulerAngles ea = UnitQuaternion.IDENTITY.asEulerAngles();
    assertEquals(0.0, ea.pitch().getAsRadians(), 0.01);
  }

  @Test
  void asForwardAndUpGuide_identity() {
    ForwardAndUpGuide fug = UnitQuaternion.IDENTITY.asForwardAndUpGuide();
    assertNotNull(fug);
  }

  @Test
  void isAlignedWith_sameOrientation() {
    assertTrue(UnitQuaternion.IDENTITY.isAlignedWith(UnitQuaternion.IDENTITY));
  }

  @Test
  void isAlignedWith_negatedIsSame() {
    UnitQuaternion q = new UnitQuaternion(0, 0.707, 0, 0.707);
    assertTrue(q.isAlignedWith(q.negated()));
  }
}
