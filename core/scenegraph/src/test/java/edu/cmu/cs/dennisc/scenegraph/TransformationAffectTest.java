package edu.cmu.cs.dennisc.scenegraph;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class TransformationAffectTest {
  private static final double EPSILON = 0.000001;

  private static final AffineMatrix4x4 OLD = AffineMatrix4x4.createTranslation(1, 2, 3);
  private static final AffineMatrix4x4 CHANGE = AffineMatrix4x4.createTranslation(10, 20, 30);

  @Test
  public void affectAllReplacesEntireMatrix() {
    AffineMatrix4x4 result = TransformationAffect.AFFECT_ALL.set(OLD, CHANGE);
    assertPointEquals(CHANGE.translation(), result.translation());
    assertEquals(CHANGE.orientation(), result.orientation());
  }

  @Test
  public void affectOrientationOnlyReplacesOrientationKeepsTranslation() {
    AffineMatrix4x4 result = TransformationAffect.AFFECT_ORIENTAION_ONLY.set(OLD, CHANGE);
    assertPointEquals(OLD.translation(), result.translation());
    assertEquals(CHANGE.orientation(), result.orientation());
  }

  @Test
  public void affectTranslationOnlyReplacesTranslationKeepsOrientation() {
    AffineMatrix4x4 result = TransformationAffect.AFFECT_TRANSLATION_ONLY.set(OLD, CHANGE);
    assertPointEquals(CHANGE.translation(), result.translation());
    assertEquals(OLD.orientation(), result.orientation());
  }

  @Test
  public void affectTranslationXOnlyReplacesOnlyX() {
    AffineMatrix4x4 result = TransformationAffect.AFFECT_TRANSLATION_X_ONLY.set(OLD, CHANGE);
    assertEquals(10, result.translation().x(), EPSILON);
    assertEquals(2, result.translation().y(), EPSILON);
    assertEquals(3, result.translation().z(), EPSILON);
    assertEquals(OLD.orientation(), result.orientation());
  }

  @Test
  public void affectTranslationYOnlyReplacesOnlyY() {
    AffineMatrix4x4 result = TransformationAffect.AFFECT_TRANSLATION_Y_ONLY.set(OLD, CHANGE);
    assertEquals(1, result.translation().x(), EPSILON);
    assertEquals(20, result.translation().y(), EPSILON);
    assertEquals(3, result.translation().z(), EPSILON);
  }

  @Test
  public void affectTranslationZOnlyReplacesOnlyZ() {
    AffineMatrix4x4 result = TransformationAffect.AFFECT_TRANSLATION_Z_ONLY.set(OLD, CHANGE);
    assertEquals(1, result.translation().x(), EPSILON);
    assertEquals(2, result.translation().y(), EPSILON);
    assertEquals(30, result.translation().z(), EPSILON);
  }

  @Test
  public void affectTranslationXYReplacesXAndY() {
    AffineMatrix4x4 result = TransformationAffect.AFFECT_TRANSLATION_XY_ONLY.set(OLD, CHANGE);
    assertEquals(10, result.translation().x(), EPSILON);
    assertEquals(20, result.translation().y(), EPSILON);
    assertEquals(3, result.translation().z(), EPSILON);
  }

  @Test
  public void affectTranslationXZReplacesXAndZ() {
    AffineMatrix4x4 result = TransformationAffect.AFFECT_TRANSLATION_XZ_ONLY.set(OLD, CHANGE);
    assertEquals(10, result.translation().x(), EPSILON);
    assertEquals(2, result.translation().y(), EPSILON);
    assertEquals(30, result.translation().z(), EPSILON);
  }

  @Test
  public void affectTranslationYZReplacesYAndZ() {
    AffineMatrix4x4 result = TransformationAffect.AFFECT_TRANSLATION_YZ_ONLY.set(OLD, CHANGE);
    assertEquals(1, result.translation().x(), EPSILON);
    assertEquals(20, result.translation().y(), EPSILON);
    assertEquals(30, result.translation().z(), EPSILON);
  }

  @Test
  public void getTranslationAffectReturnsCorrectVariantForEachNaNCombination() {
    double N = Double.NaN;
    double V = 1.0;

    assertNull(TransformationAffect.getTranslationAffect(N, N, N));
    assertSame(TransformationAffect.AFFECT_TRANSLATION_Z_ONLY, TransformationAffect.getTranslationAffect(N, N, V));
    assertSame(TransformationAffect.AFFECT_TRANSLATION_Y_ONLY, TransformationAffect.getTranslationAffect(N, V, N));
    assertSame(TransformationAffect.AFFECT_TRANSLATION_YZ_ONLY, TransformationAffect.getTranslationAffect(N, V, V));
    assertSame(TransformationAffect.AFFECT_TRANSLATION_X_ONLY, TransformationAffect.getTranslationAffect(V, N, N));
    assertSame(TransformationAffect.AFFECT_TRANSLATION_XZ_ONLY, TransformationAffect.getTranslationAffect(V, N, V));
    assertSame(TransformationAffect.AFFECT_TRANSLATION_XY_ONLY, TransformationAffect.getTranslationAffect(V, V, N));
    assertSame(TransformationAffect.AFFECT_TRANSLATION_ONLY, TransformationAffect.getTranslationAffect(V, V, V));
  }

  private static void assertPointEquals(Point3 expected, Point3 actual) {
    assertEquals(expected.x(), actual.x(), EPSILON);
    assertEquals(expected.y(), actual.y(), EPSILON);
    assertEquals(expected.z(), actual.z(), EPSILON);
  }
}
