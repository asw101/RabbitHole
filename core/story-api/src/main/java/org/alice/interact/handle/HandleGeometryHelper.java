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
package org.alice.interact.handle;

import edu.cmu.cs.dennisc.java.util.logging.Logger;
import edu.cmu.cs.dennisc.pattern.Criterion;
import edu.cmu.cs.dennisc.scenegraph.AbstractTransformable;
import edu.cmu.cs.dennisc.scenegraph.Component;
import edu.cmu.cs.dennisc.scenegraph.Composite;
import edu.cmu.cs.dennisc.scenegraph.scale.Scalable;
import org.alice.interact.PickHint;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AngleInRadians;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;
import org.lgna.story.implementation.BoundingBoxUtilities;

/**
 * Package-private helper with geometry, scale, and pick-criterion logic
 * extracted from {@link ManipulationHandle3D}.
 */
final class HandleGeometryHelper {

  private HandleGeometryHelper() {
  }

  static final Criterion<Component> NOT_3D_HANDLE_CRITERION = new Criterion<Component>() {
    protected boolean isHandle(Component c) {
      if (c == null) {
        return false;
      }
      Object bonusData = c.getBonusDataFor(PickHint.PICK_HINT_KEY);
      if ((bonusData instanceof PickHint hint) && hint.intersects(PickHint.PickType.THREE_D_HANDLE.pickHint())) {
        return true;
      } else {
        return isHandle(c.getParent());
      }
    }

    @Override
    public boolean accept(Component c) {
      return !isHandle(c);
    }
  };

  static Scalable getScalable(AbstractTransformable object) {
    if (object instanceof Scalable scalable) {
      return scalable;
    }
    if (object != null) {
      return object.getBonusDataFor(Scalable.KEY);
    }
    return null;
  }

  static AffineMatrix4x4 getTransformationForAxis(Vector3 axis) {
    double upDot = axis.dotProduct(Vector3.POSITIVE_Y_AXIS);
    OrthogonalMatrix3x3 orientation = OrthogonalMatrix3x3.IDENTITY;
    if (Math.abs(upDot) != 1.0d) {
      Vector3 rightAxis = axis.crossProduct(Vector3.POSITIVE_Y_AXIS).normalized();
      Vector3 upAxis = axis;
      Vector3 backwardAxis = rightAxis.crossProduct(upAxis).normalized();
      orientation = new OrthogonalMatrix3x3(rightAxis, upAxis, backwardAxis);
    } else if (upDot == -1.0d) {
      orientation = orientation.applyRotationAboutArbitraryAxis(Vector3.POSITIVE_X_AXIS, (new AngleInRadians(Math.PI)));
    }
    return AffineMatrix4x4.createOrientation(orientation);
  }

  static AffineMatrix4x4 invertParentScale(AffineMatrix4x4 localTransform, Composite parent) {
    OrthogonalMatrix3x3 local = localTransform.orientation().normalized();
    if (parent != null) {
      OrthogonalMatrix3x3 parentOrientation = parent.getAbsoluteTransformation().orientation();
      local = new OrthogonalMatrix3x3(
          local.getRight().times(1 / parentOrientation.getRight().magnitude()),
          local.getUp().times(1 / parentOrientation.getUp().magnitude()),
          local.getBackward().times(1 / parentOrientation.getBackward().magnitude()));
    }
    return new AffineMatrix4x4(local, localTransform.translation());
  }

  static double computeObjectScale(AxisAlignedBox bbox) {
    if ((bbox == null) || bbox.isNaN()) {
      return 1.0d;
    }
    final double VOLUME_NORMALIZER = 1d;
    Point3 max = bbox.maximum().withY(0);
    Point3 min = bbox.minimum().withY(0);
    double scale = max.distanceFrom(min) / VOLUME_NORMALIZER;
    if (Double.isNaN(scale)) {
      return 1;
    }
    if (scale < .25d) {
      scale = .25d;
    }
    if (scale > 2.0d) {
      scale = 2.0d;
    }
    return scale;
  }

  static AxisAlignedBox computeManipulatedObjectBox(AbstractTransformable manipulatedObject) {
    AxisAlignedBox boundingBox = BoundingBoxUtilities.getSGTransformableScaledBBox(manipulatedObject, false);
    if (boundingBox == null) {
      boundingBox = new AxisAlignedBox(new Point3(-1, 0, -1), new Point3(1, 1, 1));
    }
    return boundingBox;
  }

  static AbstractTransformable resolveParentTransformable(AbstractTransformable manipulatedObject, Composite parent) {
    if (manipulatedObject == null) {
      return null;
    }
    if (parent instanceof AbstractTransformable transformable) {
      return transformable;
    }
    if (parent != null) {
      Logger.severe("Unknown parent type for handle: " + parent);
    }
    Logger.severe("NULL parent for handle.");
    return null;
  }

  static float calculateCameraRelativeOpacity(AbstractTransformable parentTransformable, Point3 cameraPosition) {
    if ((parentTransformable != null) && (cameraPosition != null)) {
      Point3 handlePosition = parentTransformable.getAbsoluteTransformation().translation();
      double distance = cameraPosition.distanceFrom(handlePosition);
      if (distance < .2) {
        return 0.0f;
      } else if (distance < .5) {
        return (float) ((distance - .2f) / (.5 - .2));
      }
    }
    return 1;
  }
}
