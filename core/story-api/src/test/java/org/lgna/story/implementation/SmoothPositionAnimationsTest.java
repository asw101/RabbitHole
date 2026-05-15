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

import edu.cmu.cs.dennisc.animation.Animated;
import edu.cmu.cs.dennisc.animation.TraditionalStyle;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Point3;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * TDD contract tests for SmoothAffineMatrix4x4Animation and SmoothPositionAnimation
 * after extraction from TransformAnimator inner classes to SmoothPositionAnimations.java.
 *
 * These tests FAIL until the 2 classes are extracted to the new file.
 * SmoothPositionAnimation extends SmoothAffineMatrix4x4Animation and accesses its
 * package-private fields (m1, xHermite, yHermite, zHermite), so they must be co-located.
 */
public class SmoothPositionAnimationsTest {

  private StandInImp vehicle;
  private StandInImp subject;

  @Before
  public void setUp() {
    vehicle = new StandInImp();
    subject = new StandInImp();
    subject.setVehicle(vehicle);
    subject.setLocalTransformation(AffineMatrix4x4.IDENTITY);
  }

  // ===== SmoothPositionAnimation construction =====

  @Test
  public void smoothPositionAnimationCanBeConstructed() {
    AffineMatrix4x4 target = AffineMatrix4x4.createTranslation(10, 0, 0);

    SmoothPositionAnimation anim = new SmoothPositionAnimation(
        subject, target, vehicle, 1.0, TraditionalStyle.BEGIN_AND_END_GENTLY);
    assertNotNull(anim);
  }

  @Test
  public void smoothPositionAnimationGetAnimatedReturnsSubject() {
    AffineMatrix4x4 target = AffineMatrix4x4.createTranslation(10, 0, 0);

    SmoothPositionAnimation anim = new SmoothPositionAnimation(
        subject, target, vehicle, 1.0, TraditionalStyle.BEGIN_AND_END_GENTLY);
    Animated animated = anim.getAnimated();
    assertEquals("getAnimated should return the subject", subject, animated);
  }

  // ===== Hermite interpolation at boundaries =====

  @Test
  public void smoothPositionAnimationAtPortion0StartsAtOrigin() {
    AffineMatrix4x4 target = AffineMatrix4x4.createTranslation(10, 0, 0);

    SmoothPositionAnimation anim = new SmoothPositionAnimation(
        subject, target, vehicle, 1.0, TraditionalStyle.BEGIN_AND_END_GENTLY);
    anim.prologue();
    anim.setPortion(0.0);

    Point3 pos = subject.getLocalPosition();
    // At t=0, Hermite should evaluate to start position (0,0,0)
    assertEquals("X at portion 0 should be at start", 0.0, pos.x(), 1e-3);
    assertEquals("Y at portion 0 should be at start", 0.0, pos.y(), 1e-3);
    assertEquals("Z at portion 0 should be at start", 0.0, pos.z(), 1e-3);
  }

  @Test
  public void smoothPositionAnimationEpilogueSnapsToTarget() {
    AffineMatrix4x4 target = AffineMatrix4x4.createTranslation(10, 5, -3);

    SmoothPositionAnimation anim = new SmoothPositionAnimation(
        subject, target, vehicle, 1.0, TraditionalStyle.BEGIN_AND_END_GENTLY);
    anim.prologue();
    anim.epilogue();

    Point3 pos = subject.getLocalPosition();
    assertEquals("X should snap to target", 10.0, pos.x(), 1e-6);
    assertEquals("Y should snap to target", 5.0, pos.y(), 1e-6);
    assertEquals("Z should snap to target", -3.0, pos.z(), 1e-6);
  }

  // ===== Hermite interpolation midpoint =====

  @Test
  public void smoothPositionAnimationAtHalfwayIsIntermediate() {
    AffineMatrix4x4 target = AffineMatrix4x4.createTranslation(10, 0, 0);

    SmoothPositionAnimation anim = new SmoothPositionAnimation(
        subject, target, vehicle, 1.0, TraditionalStyle.BEGIN_AND_END_GENTLY);
    anim.prologue();
    anim.setPortion(0.5);

    Point3 pos = subject.getLocalPosition();
    // Hermite midpoint should be somewhere between start and end
    assertTrue("X at halfway should be between 0 and 10", pos.x() > 0 && pos.x() < 10);
  }

  @Test
  public void smoothPositionAnimationMonotonicallyApproachesTarget() {
    AffineMatrix4x4 target = AffineMatrix4x4.createTranslation(10, 0, 0);

    SmoothPositionAnimation anim = new SmoothPositionAnimation(
        subject, target, vehicle, 1.0, TraditionalStyle.BEGIN_AND_END_GENTLY);
    anim.prologue();

    // Sample the animation at increasing portions
    anim.setPortion(0.25);
    double x25 = subject.getLocalPosition().x();

    anim.setPortion(0.75);
    double x75 = subject.getLocalPosition().x();

    assertTrue("X at 0.75 should be greater than at 0.25 for straight-ahead motion",
        x75 > x25);
  }

  // ===== SmoothAffineMatrix4x4Animation Hermite cubics initialization =====

  @Test
  public void smoothPositionAnimationSetsPortion1ToTargetTranslation() {
    AffineMatrix4x4 target = AffineMatrix4x4.createTranslation(7, 3, -2);

    SmoothPositionAnimation anim = new SmoothPositionAnimation(
        subject, target, vehicle, 1.0, TraditionalStyle.BEGIN_AND_END_GENTLY);
    anim.prologue();
    anim.setPortion(1.0);

    Point3 pos = subject.getLocalPosition();
    // At t=1, Hermite evaluates to end position
    assertEquals("X at portion 1 should be target", 7.0, pos.x(), 1e-3);
    assertEquals("Y at portion 1 should be target", 3.0, pos.y(), 1e-3);
    assertEquals("Z at portion 1 should be target", -2.0, pos.z(), 1e-3);
  }

  // ===== Multi-axis interpolation =====

  @Test
  public void smoothPositionAnimationInterpolatesAllThreeAxes() {
    AffineMatrix4x4 target = AffineMatrix4x4.createTranslation(10, 20, 30);

    SmoothPositionAnimation anim = new SmoothPositionAnimation(
        subject, target, vehicle, 1.0, TraditionalStyle.BEGIN_AND_END_GENTLY);
    anim.prologue();
    anim.setPortion(0.5);

    Point3 pos = subject.getLocalPosition();
    assertFalse("X should not be zero at midpoint", Math.abs(pos.x()) < 1e-6);
    assertFalse("Y should not be zero at midpoint", Math.abs(pos.y()) < 1e-6);
    assertFalse("Z should not be zero at midpoint", Math.abs(pos.z()) < 1e-6);
  }

  // ===== Duration and style preserved =====

  @Test
  public void smoothPositionAnimationPreservesDuration() {
    AffineMatrix4x4 target = AffineMatrix4x4.createTranslation(10, 0, 0);

    SmoothPositionAnimation anim = new SmoothPositionAnimation(
        subject, target, vehicle, 2.5, TraditionalStyle.BEGIN_AND_END_GENTLY);

    assertEquals("Duration should be preserved", 2.5, anim.getDuration(), 1e-9);
  }

  @Test
  public void smoothPositionAnimationPreservesStyle() {
    AffineMatrix4x4 target = AffineMatrix4x4.createTranslation(10, 0, 0);

    SmoothPositionAnimation anim = new SmoothPositionAnimation(
        subject, target, vehicle, 1.0, TraditionalStyle.BEGIN_GENTLY_AND_END_ABRUPTLY);

    assertEquals("Style should be preserved",
        TraditionalStyle.BEGIN_GENTLY_AND_END_ABRUPTLY, anim.getStyle());
  }
}
