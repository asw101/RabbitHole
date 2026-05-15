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
import edu.cmu.cs.dennisc.animation.DurationBasedAnimation;
import edu.cmu.cs.dennisc.animation.Style;
import edu.cmu.cs.dennisc.math.polynomial.HermiteCubic;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;

/**
 * Hermite-interpolation based position animations for smooth movement.
 * Co-located because SmoothPositionAnimation extends SmoothAffineMatrix4x4Animation
 * and accesses its package-private fields.
 * Extracted from TransformAnimator to reduce file size.
 */
abstract class SmoothAffineMatrix4x4Animation extends DurationBasedAnimation {
  // Tangent magnitude for Hermite curves; controls how strongly orientation
  // influences the path curvature (negative = approach along backward axis).
  private static final double HERMITE_TANGENT_SCALE = -8.0;

  final AffineMatrix4x4 m1;
  final HermiteCubic xHermite;
  final HermiteCubic yHermite;
  final HermiteCubic zHermite;

  SmoothAffineMatrix4x4Animation(AffineMatrix4x4 m0, AffineMatrix4x4 m1, double duration, Style style) {
    super(duration, style);
    this.m1 = m1;

    Point3 t0 = m0.translation();
    Point3 t1 = m1.translation();
    Vector3 b0 = m0.orientation().backward();
    Vector3 b1 = m1.orientation().backward();
    this.xHermite = new HermiteCubic(t0.x(), t1.x(), HERMITE_TANGENT_SCALE * b0.x(), HERMITE_TANGENT_SCALE * b1.x());
    this.yHermite = new HermiteCubic(t0.y(), t1.y(), HERMITE_TANGENT_SCALE * b0.y(), HERMITE_TANGENT_SCALE * b1.y());
    this.zHermite = new HermiteCubic(t0.z(), t1.z(), HERMITE_TANGENT_SCALE * b0.z(), HERMITE_TANGENT_SCALE * b1.z());
  }

  @Override
  protected void prologue() {
  }
}

class SmoothPositionAnimation extends SmoothAffineMatrix4x4Animation {
  private final AbstractTransformableImp subject;
  private final edu.cmu.cs.dennisc.scenegraph.ReferenceFrame sgRef;

  SmoothPositionAnimation(AbstractTransformableImp subject, AffineMatrix4x4 m1, ReferenceFrame asSeenBy, double duration, Style style) {
    super(subject.getTransformation(asSeenBy), m1, duration, style);
    this.subject = subject;
    this.sgRef = asSeenBy.getSgReferenceFrame();
  }

  @Override
  public Animated getAnimated() {
    return subject;
  }

  @Override
  protected void setPortion(double portion) {
    double x = this.xHermite.evaluate(portion);
    double y = this.yHermite.evaluate(portion);
    double z = this.zHermite.evaluate(portion);

    this.subject.getSgComposite().setTranslationOnly(x, y, z, this.sgRef);
  }

  @Override
  protected void epilogue() {
    this.subject.getSgComposite().setTranslationOnly(this.m1.translation(), this.sgRef);
  }
}
