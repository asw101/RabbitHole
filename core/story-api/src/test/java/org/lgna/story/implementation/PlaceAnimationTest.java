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
import org.alice.math.immutable.Point3;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * TDD contract tests for PlaceAnimation after extraction from TransformAnimator
 * inner class to top-level package-private class in PlaceAnimation.java.
 *
 * These tests FAIL until PlaceAnimation is extracted to its own file.
 * PlaceAnimation depends on TransformOperations.PlaceData (a nested static class
 * in TransformOperations — already cross-class access within the package).
 */
public class PlaceAnimationTest {

  private StandInImp vehicle;
  private StandInImp subject;
  private StandInImp target;

  @Before
  public void setUp() {
    vehicle = new StandInImp();
    subject = new StandInImp();
    target = new StandInImp();
    subject.setVehicle(vehicle);
    target.setVehicle(vehicle);
    subject.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    target.setLocalTransformation(AffineMatrix4x4.createTranslation(5, 0, 0));
  }

  // ===== Construction =====

  @Test
  public void placeAnimationCanBeConstructed() {
    TransformOperations.PlaceData placeData = new TransformOperations.PlaceData(
        subject, SpatialRelationImp.ABOVE, target, 0.0, target);

    PlaceAnimation anim = new PlaceAnimation(placeData, 1.0, TraditionalStyle.BEGIN_AND_END_GENTLY);
    assertNotNull(anim);
  }

  // ===== getAnimated =====

  @Test
  public void placeAnimationGetAnimatedReturnsSubject() {
    TransformOperations.PlaceData placeData = new TransformOperations.PlaceData(
        subject, SpatialRelationImp.ABOVE, target, 0.0, target);

    PlaceAnimation anim = new PlaceAnimation(placeData, 1.0, TraditionalStyle.BEGIN_AND_END_GENTLY);
    Animated animated = anim.getAnimated();
    assertEquals("getAnimated should return the placeData subject", subject, animated);
  }

  // ===== Prologue captures start/end positions =====

  @Test
  public void placeAnimationPrologueDoesNotThrow() {
    TransformOperations.PlaceData placeData = new TransformOperations.PlaceData(
        subject, SpatialRelationImp.ABOVE, target, 0.0, target);

    PlaceAnimation anim = new PlaceAnimation(placeData, 1.0, TraditionalStyle.BEGIN_AND_END_GENTLY);
    // prologue should not throw
    anim.prologue();
  }

  // ===== setPortion interpolates =====

  @Test
  public void placeAnimationSetPortionDoesNotThrow() {
    TransformOperations.PlaceData placeData = new TransformOperations.PlaceData(
        subject, SpatialRelationImp.ABOVE, target, 0.0, target);

    PlaceAnimation anim = new PlaceAnimation(placeData, 1.0, TraditionalStyle.BEGIN_AND_END_GENTLY);
    anim.prologue();

    // Should not throw at various portions
    anim.setPortion(0.0);
    anim.setPortion(0.25);
    anim.setPortion(0.5);
    anim.setPortion(0.75);
    anim.setPortion(1.0);

    assertNotNull(subject.getLocalPosition());
  }

  // ===== Epilogue sets final transform =====

  @Test
  public void placeAnimationEpilogueSetsSubjectPosition() {
    TransformOperations.PlaceData placeData = new TransformOperations.PlaceData(
        subject, SpatialRelationImp.ABOVE, target, 0.0, target);

    PlaceAnimation anim = new PlaceAnimation(placeData, 1.0, TraditionalStyle.BEGIN_AND_END_GENTLY);
    anim.prologue();
    anim.epilogue();

    // After epilogue, subject should have been repositioned
    Point3 pos = subject.getLocalPosition();
    assertNotNull("Subject should have a position after epilogue", pos);
  }

  @Test
  public void placeAnimationEpilogueMatchesPrologueFinalTransform() {
    TransformOperations.PlaceData placeData = new TransformOperations.PlaceData(
        subject, SpatialRelationImp.ABOVE, target, 0.0, target);

    PlaceAnimation anim = new PlaceAnimation(placeData, 1.0, TraditionalStyle.BEGIN_AND_END_GENTLY);
    anim.prologue();

    // After setPortion(1.0) and epilogue, the result should be the same
    anim.setPortion(1.0);
    Point3 atPortion1 = subject.getLocalPosition();

    anim.epilogue();
    Point3 afterEpilogue = subject.getLocalPosition();

    assertEquals("Epilogue position X should match portion 1",
        atPortion1.x(), afterEpilogue.x(), 1e-6);
    assertEquals("Epilogue position Y should match portion 1",
        atPortion1.y(), afterEpilogue.y(), 1e-6);
    assertEquals("Epilogue position Z should match portion 1",
        atPortion1.z(), afterEpilogue.z(), 1e-6);
  }

  // ===== Duration preserved =====

  @Test
  public void placeAnimationPreservesDuration() {
    TransformOperations.PlaceData placeData = new TransformOperations.PlaceData(
        subject, SpatialRelationImp.ABOVE, target, 0.0, target);

    PlaceAnimation anim = new PlaceAnimation(placeData, 3.0, TraditionalStyle.BEGIN_AND_END_GENTLY);
    assertEquals("Duration should be preserved", 3.0, anim.getDuration(), 1e-9);
  }

  // ===== With nonzero offset =====

  @Test
  public void placeAnimationWithOffsetDoesNotThrow() {
    TransformOperations.PlaceData placeData = new TransformOperations.PlaceData(
        subject, SpatialRelationImp.ABOVE, target, 2.5, target);

    PlaceAnimation anim = new PlaceAnimation(placeData, 1.0, TraditionalStyle.BEGIN_AND_END_GENTLY);
    anim.prologue();
    anim.setPortion(0.5);
    anim.epilogue();

    assertNotNull(subject.getLocalPosition());
  }
}
