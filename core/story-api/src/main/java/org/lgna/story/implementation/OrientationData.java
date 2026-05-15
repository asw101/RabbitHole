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
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.UnitQuaternion;
import org.alice.math.immutable.Vector3;

/**
 * Hierarchy of orientation-data classes used by TransformAnimator for
 * quaternion-interpolated orientation animations.
 * Extracted from TransformAnimator to reduce file size.
 */
abstract class OrientationData {
  private final AbstractTransformableImp subject;

  OrientationData(AbstractTransformableImp subject) {
    this.subject = subject;
  }

  public AbstractTransformableImp getSubject() {
    return this.subject;
  }

  protected abstract void setM(OrthogonalMatrix3x3 m);

  protected final void setQ(UnitQuaternion q) {
    this.setM(q.asMatrix3x3());
  }

  protected abstract OrthogonalMatrix3x3 getM0();

  protected abstract OrthogonalMatrix3x3 getM1();

  protected abstract UnitQuaternion getQ0();

  protected abstract UnitQuaternion getQ1();

  public void setPortion(double portion) {
    UnitQuaternion q0 = this.getQ0();
    UnitQuaternion q1 = this.getQ1();
    assert !q0.isNaN() : this;
    assert !q1.isNaN() : this;
    this.setQ(q0.interpolate(q1, portion));
  }

  public void epilogue() {
    this.setM(this.getM1());
  }
}

abstract class PreSetOrientationData extends OrientationData {
  private final OrthogonalMatrix3x3 m0;
  private final OrthogonalMatrix3x3 m1;
  private UnitQuaternion q0;
  private UnitQuaternion q1;

  PreSetOrientationData(AbstractTransformableImp subject, OrthogonalMatrix3x3 m0, OrthogonalMatrix3x3 m1) {
    super(subject);
    this.m0 = m0;
    this.m1 = m1;
  }

  @Override
  protected final OrthogonalMatrix3x3 getM0() {
    return this.m0;
  }

  @Override
  protected final OrthogonalMatrix3x3 getM1() {
    return this.m1;
  }

  @Override
  protected UnitQuaternion getQ0() {
    if (this.q0 == null) {
      this.q0 = this.m0.asUnitQuaternion();
    }
    return this.q0;
  }

  @Override
  protected UnitQuaternion getQ1() {
    if (this.q1 == null) {
      this.q1 = this.m1.asUnitQuaternion();
    }
    return this.q1;
  }
}

class LocalOrientationData extends PreSetOrientationData {
  LocalOrientationData(AbstractTransformableImp subject, OrthogonalMatrix3x3 m1) {
    super(subject, subject.getSgComposite().getLocalTransformation().orientation(), m1);
  }

  @Override
  protected void setM(OrthogonalMatrix3x3 orientation) {
    AffineMatrix4x4 prevM = this.getSubject().getSgComposite().getLocalTransformation();
    AffineMatrix4x4 nextM = new AffineMatrix4x4(orientation, prevM.translation());
    this.getSubject().getSgComposite().setLocalTransformation(nextM);
  }
}

class TurnToFaceOrientationData extends LocalOrientationData {
  TurnToFaceOrientationData(AbstractTransformableImp subject, EntityImp target) {
    super(subject, VehicleManager.calculateTurnToFaceAxes(subject, target));
  }
}

class OrientToUprightData extends PreSetOrientationData {
  private final edu.cmu.cs.dennisc.scenegraph.ReferenceFrame sgRef;

  public static OrientToUprightData createInstance(AbstractTransformableImp subject, ReferenceFrame upAsSeenBy) {
    OrthogonalMatrix3x3 orientation0 = subject.getTransformation(upAsSeenBy).orientation();
    OrthogonalMatrix3x3 orientation1 = orientation0.asStandUp();
    return new OrientToUprightData(subject, orientation0, orientation1, upAsSeenBy);
  }

  private OrientToUprightData(AbstractTransformableImp subject, OrthogonalMatrix3x3 orientation0, OrthogonalMatrix3x3 orientation1, ReferenceFrame upAsSeenBy) {
    super(subject, orientation0, orientation1);
    this.sgRef = upAsSeenBy.getSgReferenceFrame();
  }

  @Override
  protected void setM(OrthogonalMatrix3x3 m) {
    this.getSubject().getSgComposite().setAxesOnly(m, this.sgRef);
  }
}

class OrientToPointAtData extends PreSetOrientationData {
  private final edu.cmu.cs.dennisc.scenegraph.ReferenceFrame sgRef;

  public static OrientToPointAtData createInstance(AbstractTransformableImp subject, EntityImp target, ReferenceFrame upAsSeenBy) {
    AffineMatrix4x4 m0 = subject.getTransformation(upAsSeenBy);
    Point3 t0 = m0.translation();
    Point3 t1 = target.getTransformation(upAsSeenBy).translation();
    Vector3 forward = t1.minus(t0);
    OrthogonalMatrix3x3 o1;
    if (forward.isZero()) {
      o1 = m0.orientation();
      //no op
    } else {
      o1 = new ForwardAndUpGuide(forward, null).asMatrix3x3();
    }
    return new OrientToPointAtData(subject, m0.orientation(), o1, upAsSeenBy);
  }

  private OrientToPointAtData(AbstractTransformableImp subject, OrthogonalMatrix3x3 orientation0, OrthogonalMatrix3x3 orientation1, ReferenceFrame upAsSeenBy) {
    super(subject, orientation0, orientation1);
    this.sgRef = upAsSeenBy.getSgReferenceFrame();
  }

  @Override
  protected void setM(OrthogonalMatrix3x3 m) {
    this.getSubject().getSgComposite().setAxesOnly(m, this.sgRef);
  }
}
