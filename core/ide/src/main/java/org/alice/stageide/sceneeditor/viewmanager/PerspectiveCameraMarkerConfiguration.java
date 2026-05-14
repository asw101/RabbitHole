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

package org.alice.stageide.sceneeditor.viewmanager;

import edu.cmu.cs.dennisc.scenegraph.AbstractCamera;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.ForwardAndUpGuide;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Ray;
import org.alice.math.immutable.Vector3;
import org.alice.stageide.sceneeditor.CameraOption;
import org.lgna.story.PerspectiveCameraMarker;
import org.lgna.story.implementation.PerspectiveCameraMarkerImp;

abstract class PerspectiveCameraMarkerConfiguration extends CameraMarkerConfiguration<PerspectiveCameraMarkerImp> {
  static final double DEFAULT_LAYOUT_CAMERA_Y_OFFSET = 12.0;
  static final double DEFAULT_LAYOUT_CAMERA_Z_OFFSET = 10.0;

  PerspectiveCameraMarkerConfiguration(CameraMarkerTracker tracker, CameraOption cameraOption, String iconName) {
    super(tracker, new PerspectiveCameraMarker(), cameraOption, iconName);
  }

  @Override
  protected void switchToCamera() {
    tracker.getSceneEditor().switchToPerspectiveCamera(getCamera());
  }

  @Override
  void updatePicturePlaneFromCamera() {
    // No change for perspective camera
  }

  AffineMatrix4x4 getViewingPerspective(AxisAlignedBox box) {
    // Attempting to find the least disruptive move, ideally a bit above of the object (because looking up through the
    // object/ground feels bad) and far enough away that we can see most of it, but hopefully close enough that there
    // isn't another object in between. Usually we succeed.
    final double targetHeight = box.getHeight();
    final Point3 targetTranslation = box.getCenter();
    // some things (e.g. other cameras) don't have a diagonal.
    double targetDiagonal = box.getDiagonal();
    targetDiagonal = targetDiagonal > 0 ? targetDiagonal : 4;

    // Since targetTranslation is already centered in y, this is effectively height * 1.5.
    final double adjustedY = targetTranslation.y() + Math.max(targetHeight, targetDiagonal * .5);
    final Point3 adjustedPos = new Point3(targetTranslation.x(), adjustedY, targetTranslation.z());

    // if the camera is already above it, great, otherwise we get the best results by using the same adjusted y.
    Point3 adjustedCameraPos = getCamera().getAbsoluteTransformation().translation();
    adjustedCameraPos.withY(Math.max(adjustedCameraPos.y(), adjustedY));

    Vector3 direction = adjustedCameraPos.minus(adjustedPos);
    if (direction.isZero()) {
      direction = new Vector3(0, DEFAULT_LAYOUT_CAMERA_Y_OFFSET, DEFAULT_LAYOUT_CAMERA_Z_OFFSET);
    }
    direction = direction.normalized();

    final Ray ray = new Ray(adjustedPos, direction);
    Point3 layoutCamTranslation = ray.getPointAlong(targetDiagonal * 1.5);

    // orientation calculated from wherever our camera ended up to look at the center of the object.
    Vector3 cameraDirection = layoutCamTranslation.minus(targetTranslation).normalized();
    final ForwardAndUpGuide forwardAndUpGuide = new ForwardAndUpGuide(cameraDirection.negate(), null);
    OrthogonalMatrix3x3 layoutCamOrientation = forwardAndUpGuide.asMatrix3x3();

    AffineMatrix4x4 layoutTransform = AffineMatrix4x4.IDENTITY
        .withTranslation(layoutCamTranslation)
        .withOrientation(layoutCamOrientation);
    return adjustForVRIfNeeded(layoutTransform);
  }

  protected AffineMatrix4x4 adjustForVRIfNeeded(AffineMatrix4x4 layoutTransform) {
    if (!tracker.getSceneEditor().isVrActive()) {
      return layoutTransform;
    }
    AbstractCamera cam = getCamera();
    AffineMatrix4x4 camTransform = cam.getTransformation(cam.getMovableParent()).invert();
    return layoutTransform.times(camTransform);
  }

  // Starting and Layout markers directly track their cameras
  @Override
  protected void startTrackingCamera() {
    markerImp.getSgComposite().setParent(getCamera().getMovableParent());
    markerImp.getSgComposite().setLocalTransformation(AffineMatrix4x4.IDENTITY);
    tracker.getSceneEditor().setHandleVisibilityForObject(markerImp, false);
  }

  // Starting and Layout markers remain on their cameras at all times
  @Override
  protected void stopTrackingCamera() {
    tracker.getSceneEditor().setHandleVisibilityForObject(markerImp, true);
  }
}
