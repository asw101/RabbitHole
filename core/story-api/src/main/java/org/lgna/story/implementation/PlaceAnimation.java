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
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.UnitQuaternion;

/**
 * Animation for spatial placement (moveTo/place operations).
 * Extracted from TransformAnimator to reduce file size.
 */
class PlaceAnimation extends DurationBasedAnimation {
  private final TransformOperations.PlaceData placeData;
  private Point3 p0;
  private UnitQuaternion q0;
  private Point3 p1;
  private UnitQuaternion q1;
  private AffineMatrix4x4 finalTransform;

  PlaceAnimation(TransformOperations.PlaceData placeData, double duration, Style style) {
    super(duration, style);
    this.placeData = placeData;
  }

  @Override
  protected void prologue() {
    AffineMatrix4x4 m0 = this.placeData.calculateTranslation0();
    AffineMatrix4x4 m1 = this.placeData.calculateTranslation1(m0);
    this.p0 = m0.translation();
    this.q0 = m0.orientation().asUnitQuaternion();
    this.p1 = m1.translation();
    this.q1 = m1.orientation().asUnitQuaternion();
    this.finalTransform = m1;
  }

  @Override
  protected void setPortion(double portion) {
    Point3 p = p0.interpolate(p1, portion);
    UnitQuaternion q = q0.interpolate(q1, portion);
    this.placeData.setTranslation(new AffineMatrix4x4(q.asMatrix3x3(), p));
  }

  @Override
  public Animated getAnimated() {
    return placeData.subject;
  }

  @Override
  protected void epilogue() {
    this.placeData.setTranslation(this.finalTransform);
  }
}
