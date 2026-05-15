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
import edu.cmu.cs.dennisc.math.EpsilonUtilities;
import edu.cmu.cs.dennisc.math.animation.AffineMatrix4x4Animation;
import edu.cmu.cs.dennisc.math.animation.Point3Animation;
import edu.cmu.cs.dennisc.math.animation.UnitQuaternionAnimation;
import edu.cmu.cs.dennisc.scenegraph.AsSeenBy;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Angle;
import org.alice.math.immutable.Orientation;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.UnitQuaternion;
import org.alice.math.immutable.Vector3;

/**
 * Instance helper for all animate* methods.
 * Orientation data hierarchy, smooth position animations, and place animation
 * are extracted to OrientationData.java, SmoothPositionAnimations.java,
 * and PlaceAnimation.java respectively.
 * Extracted from AbstractTransformableImp.
 */
class TransformAnimator {

  private static final boolean DEFAULT_IS_SMOOTH = true;

  private final AbstractTransformableImp owner;

  TransformAnimator(AbstractTransformableImp owner) {
    this.owner = owner;
  }

  // --- Translation animation ---

  void animateApplyTranslation(Point3 translation, ReferenceFrame asSeenBy, double duration, Style style) {
    assert !translation.isNaN();
    assert duration >= 0 : "Invalid argument: duration " + duration + " must be >= 0";
    assert style != null;
    assert asSeenBy != null;
    duration = owner.adjustDurationIfNecessary(duration);
    if (EpsilonUtilities.isWithinReasonableEpsilon(duration, PropertyOwnerImp.RIGHT_NOW)) {
      owner.applyTranslation(translation, asSeenBy);
      owner.applyAnimation();
    } else {
      class TranslateAnimation extends DurationBasedAnimation {
        private final ReferenceFrame asSeenBy;
        private final double x;
        private final double y;
        private final double z;
        private double xSum;
        private double ySum;
        private double zSum;

        private TranslateAnimation(Number duration, Style style, Point3 translation, ReferenceFrame asSeenBy) {
          super(duration, style);
          this.x = translation.x();
          this.y = translation.y();
          this.z = translation.z();
          this.asSeenBy = asSeenBy;
        }

        @Override
        protected void prologue() {
          this.xSum = 0;
          this.ySum = 0;
          this.zSum = 0;
        }

        @Override
        protected void setPortion(double portion) {
          double xPortion = (this.x * portion) - this.xSum;
          double yPortion = (this.y * portion) - this.ySum;
          double zPortion = (this.z * portion) - this.zSum;

          owner.applyTranslation(xPortion, yPortion, zPortion, this.asSeenBy);

          this.xSum += xPortion;
          this.ySum += yPortion;
          this.zSum += zPortion;
        }

        @Override
        protected void epilogue() {
          owner.applyTranslation(this.x - this.xSum, this.y - this.ySum, this.z - this.zSum, this.asSeenBy);
        }

        @Override
        public Animated getAnimated() {
          return owner;
        }
      }
      owner.perform(new TranslateAnimation(duration, style, translation, asSeenBy));
    }
  }

  void animateApplyTranslation(double x, double y, double z, ReferenceFrame asSeenBy, double duration, Style style) {
    animateApplyTranslation(new Point3(x, y, z), asSeenBy, duration, style);
  }

  // --- Rotation animation ---

  void animateApplyRotationInRadians(Vector3 axis, double angleInRadians, ReferenceFrame asSeenBy, double duration, Style style) {
    assert axis != null;
    assert duration >= 0 : "Invalid argument: duration " + duration + " must be >= 0";
    duration = owner.adjustDurationIfNecessary(duration);
    if (EpsilonUtilities.isWithinReasonableEpsilon(duration, PropertyOwnerImp.RIGHT_NOW)) {
      owner.applyRotationInRadians(axis, angleInRadians, asSeenBy);
      owner.applyAnimation();
    } else {
      class RotateAnimation extends DurationBasedAnimation {
        private final ReferenceFrame asSeenBy;
        private final Vector3 axis;
        private final double angleInRadians;
        private double angleSumInRadians;

        private RotateAnimation(Number duration, Style style, Vector3 axis, double angleInRadians, ReferenceFrame asSeenBy) {
          super(duration, style);
          this.axis = axis;
          this.angleInRadians = angleInRadians;
          this.asSeenBy = asSeenBy;
        }

        @Override
        protected void prologue() {
          this.angleSumInRadians = 0;
        }

        @Override
        protected void setPortion(double portion) {
          double anglePortionInRadians = (this.angleInRadians * portion) - this.angleSumInRadians;

          owner.applyRotationInRadians(this.axis, anglePortionInRadians, this.asSeenBy);

          this.angleSumInRadians += anglePortionInRadians;
        }

        @Override
        protected void epilogue() {
          owner.applyRotationInRadians(this.axis, this.angleInRadians - this.angleSumInRadians, this.asSeenBy);
        }

        @Override
        public Animated getAnimated() {
          return owner;
        }
      }
      owner.perform(new RotateAnimation(duration, style, axis, angleInRadians, asSeenBy));
    }
  }

  void animateApplyRotationInRevolutions(Vector3 axis, double angleInRevolutions, ReferenceFrame asSeenBy, double duration, Style style) {
    animateApplyRotationInRadians(axis, angleInRevolutions * Angle.REVOLUTIONS_TO_RADIANS, asSeenBy, duration, style);
  }

  // --- Orientation methods ---

  private void setOrientationOnly(OrientationData data) {
    data.epilogue();
  }

  private void animateOrientationOnly(final OrientationData data, double duration, Style style) {
    duration = owner.adjustDurationIfNecessary(duration);
    if (EpsilonUtilities.isWithinReasonableEpsilon(duration, PropertyOwnerImp.RIGHT_NOW)) {
      data.epilogue();
    } else {
      owner.perform(new DurationBasedAnimation(duration, style) {
        @Override
        public Animated getAnimated() {
          return data.getSubject();
        }

        @Override
        protected void prologue() {
        }

        @Override
        protected void setPortion(double portion) {
          data.setPortion(portion);
        }

        @Override
        protected void epilogue() {
          data.epilogue();
        }
      });
    }
  }

  void setLocalOrientationOnly(OrthogonalMatrix3x3 localOrientation) {
    setOrientationOnly(new LocalOrientationData(owner, localOrientation));
  }

  void animateLocalOrientationOnly(OrthogonalMatrix3x3 localOrientation, double duration, Style style) {
    animateOrientationOnly(new LocalOrientationData(owner, localOrientation), duration, style);
  }

  private void setOrientationOnly(EntityImp target, Orientation offset) {
    owner.getSgComposite().setAxesOnly(offset != null ? offset : OrthogonalMatrix3x3.IDENTITY, target.getSgReferenceFrame());
  }

  void animateOrientationOnly(final EntityImp target, Orientation offset, double duration, Style style) {
    duration = owner.adjustDurationIfNecessary(duration);
    if (EpsilonUtilities.isWithinReasonableEpsilon(duration, PropertyOwnerImp.RIGHT_NOW)) {
      setOrientationOnly(target, offset);
      owner.applyAnimation();
    } else {
      final OrthogonalMatrix3x3 targetOrientation = owner.getTransformation(target).orientation().normalized();
      UnitQuaternion q0 = targetOrientation.asUnitQuaternion();
      UnitQuaternion q1 = offset == null ? UnitQuaternion.IDENTITY : offset.asUnitQuaternion();
      owner.perform(new UnitQuaternionAnimation(duration, style, q0, q1) {
        @Override
        protected void updateValue(UnitQuaternion q) {
          setOrientationOnly(target, q);
        }
        @Override
        public Animated getAnimated() {
          return owner;
        }
      });
    }
  }

  void animateOrientationOnlyToFace(EntityImp target, Point3 offset, double duration, Style style) {
    animateOrientationOnly(new TurnToFaceOrientationData(owner, target), duration, style);
  }

  void animateOrientationToUpright(ReferenceFrame upAsSeenBy, double duration, Style style) {
    animateOrientationOnly(OrientToUprightData.createInstance(owner, upAsSeenBy), duration, style);
  }

  void animateOrientationToPointAt(EntityImp target, ReferenceFrame upAsSeenBy, double duration, Style style) {
    animateOrientationOnly(OrientToPointAtData.createInstance(owner, target, upAsSeenBy), duration, style);
  }

  void setOrientationOnlyToPointAt(ReferenceFrame target) {
    owner.getSgComposite().setAxesOnlyToPointAt(target.getActualEntityImplementation(owner).getSgComposite());
  }

  // --- Position animation ---

  private void setPositionOnly(EntityImp target, Point3 offset) {
    owner.getSgComposite().setTranslationOnly(offset != null ? offset : Point3.ORIGIN,
                                              target != null ? target.getSgComposite() : AsSeenBy.SCENE);
  }

  void animatePositionOnly(final EntityImp target, Point3 offset, boolean isSmooth, double duration, Style style) {
    duration = owner.adjustDurationIfNecessary(duration);
    if (EpsilonUtilities.isWithinReasonableEpsilon(duration, PropertyOwnerImp.RIGHT_NOW)) {
      setPositionOnly(target, offset);
      owner.applyAnimation();
    } else {
      if (isSmooth) {
        owner.perform(new SmoothPositionAnimation(owner, AffineMatrix4x4.IDENTITY, target, duration, style));
      } else {
        AffineMatrix4x4 m0 = owner.getTransformation(target);
        owner.perform(new Point3Animation(duration, style, m0.translation(), offset != null ? offset : Point3.ORIGIN) {
          @Override
          public Animated getAnimated() {
            return owner;
          }

          @Override
          protected void updateValue(Point3 t) {
            setPositionOnly(target, t);
          }
        });
      }
    }
  }

  // --- Place animation ---

  void animatePlace(SpatialRelationImp spatialRelation, EntityImp target, double alongAxisOffset, ReferenceFrame asSeenBy, boolean isSmooth, double duration, Style style) {
    TransformOperations.PlaceData placeData = new TransformOperations.PlaceData(owner, spatialRelation, target, alongAxisOffset, asSeenBy);
    duration = owner.adjustDurationIfNecessary(duration);
    if (EpsilonUtilities.isWithinReasonableEpsilon(duration, PropertyOwnerImp.RIGHT_NOW)) {
      AffineMatrix4x4 m0 = placeData.calculateTranslation0();
      assert !m0.isNaN() : owner;
      AffineMatrix4x4 m1 = placeData.calculateTranslation1(m0);
      assert !m1.isNaN() : owner;
      placeData.setTranslation(m1);
    } else {
      owner.perform(new PlaceAnimation(placeData, duration, style));
    }
  }

  // --- Transformation animation ---

  void animateTransformation(final ReferenceFrame target, AffineMatrix4x4 offset, boolean isSmooth, double duration, Style style) {
    duration = owner.adjustDurationIfNecessary(duration);
    if (EpsilonUtilities.isWithinReasonableEpsilon(duration, PropertyOwnerImp.RIGHT_NOW)) {
      if ((offset == null) || !offset.isNaN()) {
        owner.setTransformation(target, offset);
        owner.applyAnimation();
      }
    } else {
      AffineMatrix4x4 m1;
      if (offset != null) {
        m1 = offset;
      } else {
        m1 = AffineMatrix4x4.IDENTITY;
      }
      AffineMatrix4x4 m0 = owner.getTransformation(target);
      owner.perform(new AffineMatrix4x4Animation(duration, style, m0, m1) {
        @Override
        public Animated getAnimated() {
          return owner;
        }

        @Override
        protected void updateValue(AffineMatrix4x4 m) {
          owner.getSgComposite().setTransformation(m, target.getSgReferenceFrame());
        }

        @Override
        protected void epilogue() {
          super.epilogue();
          owner.getSgComposite().notifyTransformationListeners();
        }
      });
    }
  }

  void animateTransformation(ReferenceFrame target, AffineMatrix4x4 offset) {
    animateTransformation(target, offset, DEFAULT_IS_SMOOTH, EntityImp.DEFAULT_DURATION, EntityImp.DEFAULT_STYLE);
  }
}
