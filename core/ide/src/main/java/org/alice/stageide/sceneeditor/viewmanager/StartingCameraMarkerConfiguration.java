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

import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.scenegraph.AbstractCamera;
import edu.cmu.cs.dennisc.scenegraph.SimpleAppearance;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Vector3;
import org.alice.stageide.sceneeditor.CameraOption;
import org.lgna.story.Color;
import org.lgna.story.implementation.PerspectiveCameraMarkerImp;
import org.lgna.story.implementation.SceneImp;
import org.lgna.story.implementation.TransformableImp;
import org.lgna.story.implementation.VrUserImp;

class StartingCameraMarkerConfiguration extends PerspectiveCameraMarkerConfiguration {
  private Visual[] visuals;

  StartingCameraMarkerConfiguration(CameraMarkerTracker tracker) {
    super(tracker, CameraOption.STARTING_CAMERA_VIEW, "mainCamera");
    markerImp.setShowing(false);
  }

  @Override
  protected void initialize() {
    markerImp.setVrActive(tracker.getSceneEditor().isVrActive());
    markerImp.setDisplayVisuals(false);
  }

  @Override
  protected void centerOn(AxisAlignedBox box) {
    // This is for the staging cameras. The starting camera does not move.
  }

  @Override
  protected AffineMatrix4x4 getTargetTransform() {
    return markerImp.getAbsoluteTransformation();
  }

  @Override
  void resetForScene(SceneImp sceneImp, AffineMatrix4x4 startingView) {
    markerImp.setLocalTransformation(startingView);
  }

  @Override
  protected AbstractCamera getCamera() {
    return tracker.getMainCamera();
  }

  void setVrActive(boolean isVrScene) {
    markerImp.setVrActive(isVrScene);
    MarkerUtilities.addIconForCameraOption(CameraOption.STARTING_CAMERA_VIEW, isVrScene ? "vrHeadset" : "mainCamera");
  }

  void intializeOnStartingCamera(TransformableImp startingCamera) {
    SimpleAppearance sgAppearance = new SimpleAppearance();
    Color4f darkGrey = Color.DARK_GRAY.toColor4f();
    sgAppearance.diffuseColor.setValue(darkGrey);
    if (tracker.getSceneEditor().isVrActive() && startingCamera instanceof VrUserImp vrUser) {
      visuals = PerspectiveCameraMarkerImp.createVRVisual(sgAppearance, startingCamera.getSgComposite());
      scaleVisuals(vrUser.scale.getValue());
      vrUser.scale.addPropertyListener(() -> scaleVisuals(vrUser.scale.getValue()));
    } else {
      visuals = PerspectiveCameraMarkerImp.createCameraVisuals(sgAppearance, startingCamera.getSgComposite());
    }
  }

  private void scaleVisuals(Double scale) {
    OrthogonalMatrix3x3 scaleMatrix =
        (new Vector3(scale, scale, scale)).asScaleMatrix();
    for (Visual visual : visuals) {
      visual.scale.setValue(scaleMatrix);
    }
  }

  @Override
  protected AffineMatrix4x4 adjustForVRIfNeeded(AffineMatrix4x4 layoutTransform) {
    if (!tracker.getSceneEditor().isVrActive()) {
      return layoutTransform;
    }
    AffineMatrix4x4 adjusted = super.adjustForVRIfNeeded(layoutTransform);
    // Level the VRUser, for their own health.
    OrthogonalMatrix3x3 stoodup = adjusted.orientation().asStandUp();
    return layoutTransform.withOrientation(stoodup);
  }

  @Override
  protected void startTrackingCamera() {
    super.startTrackingCamera();
    // Hide visuals when camera is active
    for (Visual v : visuals) {
      v.isShowing.setValue(false);
    }
  }

  @Override
  protected void stopTrackingCamera() {
    super.stopTrackingCamera();
    // Show visuals when other views are active
    for (Visual v : visuals) {
      v.isShowing.setValue(true);
    }
  }
}
