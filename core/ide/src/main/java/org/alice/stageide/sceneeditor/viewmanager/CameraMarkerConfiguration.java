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

import edu.cmu.cs.dennisc.animation.affine.PointOfViewAnimation;
import edu.cmu.cs.dennisc.scenegraph.AbstractCamera;
import edu.cmu.cs.dennisc.scenegraph.AbstractTransformable;
import edu.cmu.cs.dennisc.scenegraph.AsSeenBy;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.stageide.sceneeditor.CameraOption;
import org.lgna.story.CameraMarker;
import org.lgna.story.implementation.CameraMarkerImp;
import org.lgna.story.implementation.SceneImp;

abstract class CameraMarkerConfiguration<T extends CameraMarkerImp> {
  final T markerImp;
  final CameraOption cameraOption;
  final CameraMarkerTracker tracker;
  private boolean doEpilogue = true;
  private PointOfViewAnimation pointOfViewAnimation = null;

  @SuppressWarnings("unchecked")
  CameraMarkerConfiguration(CameraMarkerTracker tracker, CameraMarker marker, CameraOption cameraOption, String iconName) {
    this.tracker = tracker;
    this.cameraOption = cameraOption;
    markerImp = (T) marker.getImplementation();
    MarkerUtilities.addIconForCameraOption(cameraOption, iconName);
    markerImp.getAbstraction().setName(MarkerUtilities.getNameForCamera(cameraOption));
    initialize();
  }

  protected boolean isActive() {
    return this.equals(tracker.getActiveMarker());
  }

  protected abstract void initialize();
  protected abstract void centerOn(AxisAlignedBox box);
  abstract void resetForScene(SceneImp sceneImp, AffineMatrix4x4 startingView);
  protected abstract void stopTrackingCamera();

  // The local transform of the marker has been updated while it is active. Temporarily unhook
  // the marker from the parent camera, then animate the camera to reconnect them.
  void updateCameraToNewMarkerLocation() {
    markerImp.getSgComposite().setParent(markerImp.getSgComposite().getRoot());
    animateToTargetView(getCamera());
  }

  protected abstract void switchToCamera();
  protected abstract AbstractCamera getCamera();

  void animateToTargetView(AbstractCamera previousCamera) {
    AbstractTransformable cameraParent = getCamera().getMovableParent();
    AffineMatrix4x4 lastCamTransform =
        previousCamera == null
            ? AffineMatrix4x4.IDENTITY
            : previousCamera.getMovableParent().getAbsoluteTransformation();
    AffineMatrix4x4 targetTransform = getTargetTransform();
    cameraParent.setTransformation(lastCamTransform, AsSeenBy.SCENE);
    switchToCamera();

    if (pointOfViewAnimation != null) {
      doEpilogue = false;
      pointOfViewAnimation.complete(null);
      doEpilogue = true;
    }
    if (lastCamTransform.isWithinReasonableEpsilonOf(targetTransform)) {
      startTrackingCamera();
    } else {
      pointOfViewAnimation = new PointOfViewAnimation(cameraParent, AsSeenBy.SCENE, lastCamTransform, targetTransform) {
        @Override
        protected void epilogue() {
          if (doEpilogue) {
            startTrackingCamera();
          }
        }
      };
      tracker.getAnimator().invokeLater(pointOfViewAnimation, null);
    }
  }

  protected abstract AffineMatrix4x4 getTargetTransform();
  protected abstract void startTrackingCamera();
  abstract void updatePicturePlaneFromCamera();
}
