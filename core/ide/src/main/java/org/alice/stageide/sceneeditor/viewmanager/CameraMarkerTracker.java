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

import edu.cmu.cs.dennisc.animation.Animator;
import edu.cmu.cs.dennisc.java.util.Maps;
import edu.cmu.cs.dennisc.java.util.logging.Logger;
import edu.cmu.cs.dennisc.property.InstancePropertyOwner;
import edu.cmu.cs.dennisc.property.event.PropertyEvent;
import edu.cmu.cs.dennisc.property.event.PropertyListener;
import edu.cmu.cs.dennisc.scenegraph.AbstractCamera;
import edu.cmu.cs.dennisc.scenegraph.OrthographicCamera;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import org.alice.ide.IDE;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.stageide.sceneeditor.CameraOption;
import org.alice.stageide.sceneeditor.StorytellingSceneEditor;
import org.alice.stageide.sceneeditor.viewmanager.edits.MoveTransformableEdit;
import org.lgna.croquet.EditOperation;
import org.lgna.croquet.event.ValueEvent;
import org.lgna.croquet.event.ValueListener;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.UserField;
import org.lgna.story.SCamera;
import org.lgna.story.SThing;
import org.lgna.story.SVRUser;
import org.lgna.story.implementation.EntityImp;
import org.lgna.story.implementation.SceneImp;
import org.lgna.story.implementation.TransformableImp;

import java.util.Map;
import java.util.Objects;

// This handles the fact that we have multiple cameras (perspective and orthographic) and each of those cameras
// could be set to one of several different "markers" which can be re-positioned by outside sources (and allow us to switch
// between views without losing our previous position, since the marker remains where we used to be)

public class CameraMarkerTracker implements PropertyListener, ValueListener<CameraOption> {
  private StartingCameraMarkerConfiguration startingCameraConfig;
  private SymmetricPerspectiveCamera mainCamera = null;
  private SymmetricPerspectiveCamera layoutCamera = null;
  private OrthographicCamera orthographicCamera = null;
  private final Animator animator;
  private final StorytellingSceneEditor sceneEditor;
  private CameraMarkerConfiguration<?> activeMarker;

  private final Map<CameraOption, CameraMarkerConfiguration<?>> mapViewToMarker = Maps.newHashMap();

  public CameraMarkerTracker(StorytellingSceneEditor sceneEditor, Animator animator) {
    this.sceneEditor = sceneEditor;
    this.animator = animator;
    initializeCameraMarkers();
  }

  public void setCameras(SymmetricPerspectiveCamera mainCamera, SymmetricPerspectiveCamera layoutCamera, OrthographicCamera orthographicCamera) {
    this.mainCamera = mainCamera;
    this.layoutCamera = layoutCamera;
    if (this.orthographicCamera == orthographicCamera) {
      return;
    }
    if (this.orthographicCamera != null) {
      this.orthographicCamera.picturePlane.removePropertyListener(this);
    }
    this.orthographicCamera = orthographicCamera;
    if (this.orthographicCamera != null) {
      this.orthographicCamera.picturePlane.addPropertyListener(this);
    }
  }

  @Override
  public void valueChanged(ValueEvent<CameraOption> e) {
    CameraMarkerConfiguration<?> nextMarker = mapViewToMarker.get(e.getNextValue());
    if (isMissingAnyCameras() || nextMarker == null || nextMarker == activeMarker) {
      return;
    }
    AbstractCamera previousCamera = null;
    if (activeMarker != null) {
      activeMarker.stopTrackingCamera();
      previousCamera = activeMarker.getCamera();
    }
    activeMarker = nextMarker;
    activeMarker.animateToTargetView(previousCamera);
  }

  private boolean isMissingAnyCameras() {
    return mainCamera == null || layoutCamera == null || orthographicCamera == null;
  }

  public void trackStartingCameraView() {
    if (isMissingAnyCameras()) {
      return;
    }
    activeMarker = mapViewToMarker.get(CameraOption.STARTING_CAMERA_VIEW);
    activeMarker.switchToCamera();
    activeMarker.startTrackingCamera();
  }

  private void initializeCameraMarkers() {
    startingCameraConfig = new StartingCameraMarkerConfiguration(this);
    addMarker(startingCameraConfig);
    addMarker(new LayoutCameraMarkerConfiguration(this));
    addMarker(new TopCameraMarkerConfiguration(this));
    addMarker(new SideCameraMarkerConfiguration(this));
    addMarker(new FrontCameraMarkerConfiguration(this));
  }

  private void addMarker(CameraMarkerConfiguration<?> cameraMarker) {
    mapViewToMarker.put(cameraMarker.cameraOption, cameraMarker);
  }

  public void updateMarkersForNewScene(SceneImp sceneImp, TransformableImp startingCamera) {
    AffineMatrix4x4 openingViewTransform = startingCamera.getAbsoluteTransformation();
    for (CameraMarkerConfiguration<?> marker : mapViewToMarker.values()) {
      marker.resetForScene(sceneImp, openingViewTransform);
    }
    startingCameraConfig.intializeOnStartingCamera(startingCamera);
  }

  public void centerCameraOnField(UserActivity activity, TransformableImp movableCamera, UserField field) {
    if (activeMarker == startingCameraConfig) {
      // Don't move the main camera to look at itself
      if (!field.getValueType().isAssignableTo(SCamera.class) && !field.getValueType().isAssignableTo(SVRUser.class)) {
        centerMainCameraOnField(activity, movableCamera, field);
      }
    } else {
      centerMarkersOn(field);
    }
  }

  private void centerMarkersOn(UserField field) {
    // Changes the markers for the layout camera and the 3 ortho camera views, not just whatever marker is active.
    AxisAlignedBox alignedBox = getBoundingBox(field);
    for (CameraMarkerConfiguration<?> marker : mapViewToMarker.values()) {
      marker.centerOn(alignedBox);
    }
    // Update camera to the latest marker
    activeMarker.updateCameraToNewMarkerLocation();
  }

  private void centerMainCameraOnField(UserActivity activity, TransformableImp movableCamera, UserField field) {
    AffineMatrix4x4 end = startingCameraConfig.getViewingPerspective(getBoundingBox(field));
    MoveTransformableEdit edit = new MoveTransformableEdit(activity, movableCamera, end);
    new EditOperation(edit).fire(activity);
  }

  private static AxisAlignedBox getBoundingBox(UserField field) {
    Object instanceInJava = IDE.getActiveInstance().getSceneEditor().getInstanceInJavaVMForField(field);
    EntityImp target = ((SThing) instanceInJava).getImplementation();
    return target.getDynamicAxisAlignedMinimumBoundingBox(org.lgna.story.implementation.AsSeenBy.SCENE);
  }

  double clampCameraValue(double val) {
    final double CAMERA_CLAMP_MAX = 100.0;
    final double CAMERA_CLAMP_MIN = 2.0;
    return Math.min(CAMERA_CLAMP_MAX, Math.max(CAMERA_CLAMP_MIN, val));
  }

  double clampPictureValue(double val) {
    // we intentionally allow a slightly larger max here, and the manipulator deals with it later.
    final double PICTURE_CLAMP_MAX = 100;
    final double PICTURE_CLAMP_MIN = 1.5;
    return Math.min(PICTURE_CLAMP_MAX, Math.max(PICTURE_CLAMP_MIN, val));
  }

  public void setIsVrActive(boolean isVrScene) {
    startingCameraConfig.setVrActive(isVrScene);
  }

  @Override
  public void propertyChanged(PropertyEvent e) {
    InstancePropertyOwner owner = e.getOwner();
    if (!(owner instanceof OrthographicCamera)) {
      return;
    }
    if (!orthographicCamera.equals(owner)) {
      Logger.warning("Multiple Orthogonal cameras in use");
      return;
    }
    if (!Objects.equals(e.getTypedSource(), orthographicCamera.picturePlane)) {
      return;
    }
    activeMarker.updatePicturePlaneFromCamera();
  }

  // Package-private accessors for extracted marker configurations
  StorytellingSceneEditor getSceneEditor() { return sceneEditor; }
  Animator getAnimator() { return animator; }
  CameraMarkerConfiguration<?> getActiveMarker() { return activeMarker; }
  SymmetricPerspectiveCamera getMainCamera() { return mainCamera; }
  SymmetricPerspectiveCamera getLayoutCamera() { return layoutCamera; }
  OrthographicCamera getOrthographicCamera() { return orthographicCamera; }
}
