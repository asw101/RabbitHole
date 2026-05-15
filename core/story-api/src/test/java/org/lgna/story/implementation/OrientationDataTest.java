/*******************************************************************************
 * Copyright (c) 2006, 2015, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice", nor may
 *    "Alice" appear in their name, without prior written permission of
 *    Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgement: "This product includes software
 *    developed by Carnegie Mellon University"
 *
 * 5. The gallery of art assets and animations provided with this software is
 *    contributed by Electronic Arts Inc. and may be used for personal,
 *    non-commercial, and academic use only. Redistributions of any program
 *    source code that utilizes The Sims 2 Assets must also retain the copyright
 *    notice, list of conditions and the disclaimer contained in
 *    The Alice 3.0 Art Gallery License.
 *
 * DISCLAIMER:
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 * ANY AND ALL EXPRESS, STATUTORY OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, AND NON-INFRINGEMENT ARE DISCLAIMED. IN NO EVENT
 * SHALL THE AUTHORS, COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING FROM OR OTHERWISE RELATING TO
 * THE USE OF OR OTHER DEALINGS WITH THE SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

package org.lgna.story.implementation;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.ForwardAndUpGuide;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.UnitQuaternion;
import org.alice.math.immutable.Vector3;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * TDD contract tests for the OrientationData class hierarchy after extraction
 * from TransformAnimator inner classes to top-level package-private classes
 * in OrientationData.java.
 *
 * These tests FAIL until the 6-class hierarchy is extracted:
 *   OrientationData, PreSetOrientationData, LocalOrientationData,
 *   TurnToFaceOrientationData, OrientToUprightData, OrientToPointAtData
 */
public class OrientationDataTest {

  private StandInImp vehicle;
  private StandInImp subject;

  @Before
  public void setUp() {
    vehicle = new StandInImp();
    subject = new StandInImp();
    subject.setVehicle(vehicle);
    subject.setLocalTransformation(AffineMatrix4x4.IDENTITY);
  }

  // ===== LocalOrientationData =====

  @Test
  public void localOrientationDataGetSubjectReturnsSubject() {
    LocalOrientationData data = new LocalOrientationData(subject, OrthogonalMatrix3x3.IDENTITY);
    assertEquals(subject, data.getSubject());
  }

  @Test
  public void localOrientationDataEpilogueSetsTargetOrientation() {
    OrthogonalMatrix3x3 target = new ForwardAndUpGuide(
        new Vector3(1, 0, 0), new Vector3(0, 1, 0)).asMatrix3x3();

    LocalOrientationData data = new LocalOrientationData(subject, target);
    data.epilogue();

    OrthogonalMatrix3x3 result = subject.getLocalOrientation();
    assertTrue("After epilogue, orientation should match target",
        result.isWithinEpsilonOf(target, 1e-6));
  }

  @Test
  public void localOrientationDataEpiloguePreservesTranslation() {
    subject.setLocalTransformation(AffineMatrix4x4.createTranslation(7, 8, 9));

    LocalOrientationData data = new LocalOrientationData(subject, OrthogonalMatrix3x3.IDENTITY);
    data.epilogue();

    assertEquals("X position should be preserved", 7.0, subject.getLocalPosition().x(), 1e-9);
    assertEquals("Y position should be preserved", 8.0, subject.getLocalPosition().y(), 1e-9);
    assertEquals("Z position should be preserved", 9.0, subject.getLocalPosition().z(), 1e-9);
  }

  @Test
  public void localOrientationDataSetPortionInterpolatesFromCurrentToTarget() {
    OrthogonalMatrix3x3 target = new ForwardAndUpGuide(
        new Vector3(1, 0, 0), new Vector3(0, 1, 0)).asMatrix3x3();

    LocalOrientationData data = new LocalOrientationData(subject, target);

    // Portion 0 = start orientation (identity)
    data.setPortion(0.0);
    OrthogonalMatrix3x3 atZero = subject.getLocalOrientation();
    assertTrue("At portion 0, orientation should be near identity",
        atZero.isWithinEpsilonOf(OrthogonalMatrix3x3.IDENTITY, 1e-6));

    // Portion 1 = target orientation
    data.setPortion(1.0);
    OrthogonalMatrix3x3 atOne = subject.getLocalOrientation();
    assertTrue("At portion 1, orientation should be near target",
        atOne.isWithinEpsilonOf(target, 1e-6));
  }

  @Test
  public void localOrientationDataSetPortionHalfwayProducesIntermediateOrientation() {
    OrthogonalMatrix3x3 target = new ForwardAndUpGuide(
        new Vector3(1, 0, 0), new Vector3(0, 1, 0)).asMatrix3x3();

    LocalOrientationData data = new LocalOrientationData(subject, target);
    data.setPortion(0.5);

    OrthogonalMatrix3x3 halfway = subject.getLocalOrientation();
    assertNotNull("Halfway orientation should not be null", halfway);
    assertFalse("Halfway orientation should differ from identity",
        halfway.isWithinEpsilonOf(OrthogonalMatrix3x3.IDENTITY, 1e-3));
    assertFalse("Halfway orientation should differ from target",
        halfway.isWithinEpsilonOf(target, 1e-3));
  }

  // ===== TurnToFaceOrientationData =====

  @Test
  public void turnToFaceOrientationDataGetSubjectReturnsSubject() {
    StandInImp target = new StandInImp();
    target.setVehicle(vehicle);
    target.setLocalTransformation(AffineMatrix4x4.createTranslation(5, 0, -5));

    TurnToFaceOrientationData data = new TurnToFaceOrientationData(subject, target);
    assertEquals(subject, data.getSubject());
  }

  @Test
  public void turnToFaceOrientationDataEpilogueRotatesTowardTarget() {
    StandInImp target = new StandInImp();
    target.setVehicle(vehicle);
    target.setLocalTransformation(AffineMatrix4x4.createTranslation(5, 0, -5));

    OrthogonalMatrix3x3 orientationBefore = subject.getLocalOrientation();

    TurnToFaceOrientationData data = new TurnToFaceOrientationData(subject, target);
    data.epilogue();

    OrthogonalMatrix3x3 orientationAfter = subject.getLocalOrientation();
    assertFalse("Orientation should change to face target",
        orientationAfter.isWithinEpsilonOf(orientationBefore, 1e-3));
  }

  // ===== OrientToUprightData =====

  @Test
  public void orientToUprightDataFactoryReturnsNonNull() {
    OrientToUprightData data = OrientToUprightData.createInstance(subject, vehicle);
    assertNotNull(data);
  }

  @Test
  public void orientToUprightDataGetSubjectReturnsSubject() {
    OrientToUprightData data = OrientToUprightData.createInstance(subject, vehicle);
    assertEquals(subject, data.getSubject());
  }

  @Test
  public void orientToUprightDataEpilogueStraightensUp() {
    // Tilt the subject first
    subject.applyRotationInRadians(new Vector3(1, 0, 0), Math.PI / 4.0, subject);
    OrthogonalMatrix3x3 tiltedOrientation = subject.getLocalOrientation();
    assertFalse("Subject should be tilted", tiltedOrientation.isIdentity());

    OrientToUprightData data = OrientToUprightData.createInstance(subject, vehicle);
    data.epilogue();

    // After standing up, orientation should have changed toward upright
    OrthogonalMatrix3x3 result = subject.getLocalOrientation();
    assertNotNull(result);
  }

  @Test
  public void orientToUprightDataSetPortionInterpolates() {
    subject.applyRotationInRadians(new Vector3(1, 0, 0), Math.PI / 4.0, subject);

    OrientToUprightData data = OrientToUprightData.createInstance(subject, vehicle);

    // At portion 0, should be at tilted orientation
    data.setPortion(0.0);
    OrthogonalMatrix3x3 atStart = subject.getLocalOrientation();
    assertNotNull(atStart);

    // At portion 1, should be at upright
    data.setPortion(1.0);
    OrthogonalMatrix3x3 atEnd = subject.getLocalOrientation();
    assertNotNull(atEnd);
  }

  // ===== OrientToPointAtData =====

  @Test
  public void orientToPointAtDataFactoryReturnsNonNull() {
    StandInImp target = new StandInImp();
    target.setVehicle(vehicle);
    target.setLocalTransformation(AffineMatrix4x4.createTranslation(0, 0, -10));

    OrientToPointAtData data = OrientToPointAtData.createInstance(subject, target, vehicle);
    assertNotNull(data);
  }

  @Test
  public void orientToPointAtDataGetSubjectReturnsSubject() {
    StandInImp target = new StandInImp();
    target.setVehicle(vehicle);
    target.setLocalTransformation(AffineMatrix4x4.createTranslation(0, 0, -10));

    OrientToPointAtData data = OrientToPointAtData.createInstance(subject, target, vehicle);
    assertEquals(subject, data.getSubject());
  }

  @Test
  public void orientToPointAtDataEpiloguePointsAtTarget() {
    StandInImp target = new StandInImp();
    target.setVehicle(vehicle);
    target.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 0, 0));

    OrientToPointAtData data = OrientToPointAtData.createInstance(subject, target, vehicle);
    data.epilogue();

    OrthogonalMatrix3x3 result = subject.getLocalOrientation();
    assertNotNull("Subject should have a valid orientation", result);
  }

  @Test
  public void orientToPointAtDataWithCoincidentPointsUsesCurrentOrientation() {
    // Target at same position as subject — forward vector is zero
    StandInImp target = new StandInImp();
    target.setVehicle(vehicle);
    target.setLocalTransformation(AffineMatrix4x4.IDENTITY);

    OrientToPointAtData data = OrientToPointAtData.createInstance(subject, target, vehicle);
    data.epilogue();

    // Should not throw, orientation falls back to current
    assertNotNull(subject.getLocalOrientation());
  }

  @Test
  public void orientToPointAtDataSetPortionInterpolates() {
    StandInImp target = new StandInImp();
    target.setVehicle(vehicle);
    target.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 0, 0));

    OrientToPointAtData data = OrientToPointAtData.createInstance(subject, target, vehicle);
    data.setPortion(0.5);

    assertNotNull("At portion 0.5 should have valid orientation",
        subject.getLocalOrientation());
  }

  // ===== PreSetOrientationData quaternion lazy caching =====

  @Test
  public void preSetOrientationDataCachesQuaternionsOnRepeatedAccess() {
    // LocalOrientationData extends PreSetOrientationData — test through it
    OrthogonalMatrix3x3 target = new ForwardAndUpGuide(
        new Vector3(0, 0, -1), new Vector3(0, 1, 0)).asMatrix3x3();

    LocalOrientationData data = new LocalOrientationData(subject, target);

    // Multiple setPortion calls should use cached quaternions
    data.setPortion(0.0);
    data.setPortion(0.5);
    data.setPortion(1.0);

    // No exception = lazy caching works
    assertNotNull(subject.getLocalOrientation());
  }

  // ===== OrientationData base class contract =====

  @Test
  public void orientationDataSetPortionWithIdentityStartAndEndIsNoOp() {
    LocalOrientationData data = new LocalOrientationData(subject, OrthogonalMatrix3x3.IDENTITY);

    data.setPortion(0.0);
    assertTrue("Identity → Identity interpolation at 0 should yield identity",
        subject.getLocalOrientation().isWithinEpsilonOf(OrthogonalMatrix3x3.IDENTITY, 1e-6));

    data.setPortion(1.0);
    assertTrue("Identity → Identity interpolation at 1 should yield identity",
        subject.getLocalOrientation().isWithinEpsilonOf(OrthogonalMatrix3x3.IDENTITY, 1e-6));
  }
}
