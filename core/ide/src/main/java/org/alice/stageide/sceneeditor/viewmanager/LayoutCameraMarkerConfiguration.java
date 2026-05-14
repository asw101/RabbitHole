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
import org.alice.math.immutable.AngleInDegrees;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import org.alice.stageide.sceneeditor.CameraOption;
import org.lgna.story.Color;
import org.lgna.story.implementation.SceneImp;

class LayoutCameraMarkerConfiguration extends PerspectiveCameraMarkerConfiguration {
  private static final int DEFAULT_LAYOUT_CAMERA_ANGLE = -40;

  LayoutCameraMarkerConfiguration(CameraMarkerTracker tracker) {
    super(tracker, CameraOption.LAYOUT_SCENE_VIEW, "sceneEditorCamera");
  }

  @Override
  protected void initialize() {
    markerImp.setVrActive(tracker.getSceneEditor().isVrActive());
    markerImp.getAbstraction().setColorId(Color.LIGHT_BLUE);
    markerImp.setDisplayVisuals(true);
  }

  @Override
  protected void centerOn(AxisAlignedBox box) {
    setLocalTransformation(getViewingPerspective(box));
  }

  private void setLocalTransformation(AffineMatrix4x4 layoutTransform) {
    if (isActive()) {
      markerImp.setLocalTransformation(layoutTransform);
    } else {
      getCamera().getMovableParent().setLocalTransformation(layoutTransform);
    }
  }

  @Override
  protected AffineMatrix4x4 getTargetTransform() {
    return markerImp.getAbsoluteTransformation();
  }

  @Override
  void resetForScene(SceneImp sceneImp, AffineMatrix4x4 startingView) {
    Point3 translation = startingView.translation();
    AffineMatrix4x4 layoutTransform = startingView
        .withTranslation(
            new Point3(translation.x(),
                translation.y() + DEFAULT_LAYOUT_CAMERA_Y_OFFSET,
                translation.z() + DEFAULT_LAYOUT_CAMERA_Z_OFFSET))
        .rotateAboutXAxis(new AngleInDegrees(DEFAULT_LAYOUT_CAMERA_ANGLE));
    layoutTransform = adjustForVRIfNeeded(layoutTransform);
    getCamera().getMovableParent().setLocalTransformation(layoutTransform);
    markerImp.getSgComposite().setParent(getCamera().getMovableParent());
    markerImp.getSgComposite().setLocalTransformation(AffineMatrix4x4.IDENTITY);
  }

  @Override
  protected AbstractCamera getCamera() {
    return tracker.getLayoutCamera();
  }

  @Override
  protected void startTrackingCamera() {
    super.startTrackingCamera();
    // Hide visual when camera is active
    markerImp.setShowing(false);
  }

  @Override
  protected void stopTrackingCamera() {
    super.stopTrackingCamera();
    // Show visual when other views are active
    markerImp.setShowing(true);
  }
}
