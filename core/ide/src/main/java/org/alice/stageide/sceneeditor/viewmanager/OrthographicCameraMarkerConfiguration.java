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

import edu.cmu.cs.dennisc.java.util.logging.Logger;
import edu.cmu.cs.dennisc.scenegraph.AbstractCamera;
import edu.cmu.cs.dennisc.scenegraph.AbstractTransformable;
import edu.cmu.cs.dennisc.scenegraph.AsSeenBy;
import edu.cmu.cs.dennisc.scenegraph.Component;
import edu.cmu.cs.dennisc.scenegraph.Composite;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.stageide.sceneeditor.CameraOption;
import org.lgna.story.OrthographicCameraMarker;
import org.lgna.story.implementation.OrthographicCameraMarkerImp;
import org.lgna.story.implementation.SceneImp;

abstract class OrthographicCameraMarkerConfiguration extends CameraMarkerConfiguration<OrthographicCameraMarkerImp> {

  OrthographicCameraMarkerConfiguration(CameraMarkerTracker tracker, CameraOption cameraOption, String iconName) {
    super(tracker, new OrthographicCameraMarker(), cameraOption, iconName);
  }

  @Override
  void resetForScene(SceneImp sceneImp, AffineMatrix4x4 startingView) {
    Component[] existingComponents = sceneImp.getSgComposite().getComponentsAsArray();
    boolean alreadyHasIt = false;
    for (Component c : existingComponents) {
      if (c == markerImp.getSgComposite()) {
        alreadyHasIt = true;
        break;
      }
    }
    if (!alreadyHasIt) {
      markerImp.setVehicle(sceneImp);
    }
    // Ortho cameras could override to focus on the starting camera or the overall scene
    // Default to initialize to remove any changes from previous scene editing
    initialize();
  }

  // Move marker in the scene graph to be directly under scene, not camera, while maintaining the absolute position
  @Override
  protected void stopTrackingCamera() {
    AffineMatrix4x4 previousMarkerTransform = markerImp.getTransformation(org.lgna.story.implementation.AsSeenBy.SCENE);
    markerImp.getSgComposite().setParent(markerImp.getSgComposite().getRoot());
    markerImp.getSgComposite().setTransformation(previousMarkerTransform, AsSeenBy.SCENE);
    markerImp.setShowing(true);
    tracker.getSceneEditor().setHandleVisibilityForObject(markerImp, true);
  }

  // Sets the given camera to the absolute orientation of the given marker
  // Parents the given marker to the camera and then zeros out the local transform
  @Override
  protected void startTrackingCamera() {
    AbstractTransformable cameraParent = getCamera().getMovableParent();
    Composite root = cameraParent.getRoot();
    if (root != null) {
      cameraParent.setTransformation(markerImp.getTransformation(org.lgna.story.implementation.AsSeenBy.SCENE), root);
    } else {
      Logger.severe(cameraParent);
    }
    markerImp.setShowing(false);
    markerImp.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    markerImp.getSgComposite().setParent(cameraParent);
    tracker.getSceneEditor().setHandleVisibilityForObject(markerImp, false);
  }

  @Override
  protected AbstractCamera getCamera() {
    return tracker.getOrthographicCamera();
  }

  @Override
  protected void switchToCamera() {
    tracker.getSceneEditor().switchToOrthographicCamera();
    tracker.getOrthographicCamera().picturePlane.setValue(markerImp.getPicturePlane());
  }

  @Override
  void updatePicturePlaneFromCamera() {
    markerImp.setPicturePlane(tracker.getOrthographicCamera().picturePlane.getValue());
  }

  @Override
  protected AffineMatrix4x4 getTargetTransform() {
    return markerImp.getTransformation(org.lgna.story.implementation.AsSeenBy.SCENE);
  }
}
