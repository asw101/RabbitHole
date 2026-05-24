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
package org.alice.stageide.sceneeditor;

import edu.cmu.cs.dennisc.java.lang.SystemUtilities;
import edu.cmu.cs.dennisc.scenegraph.AsSeenBy;
import org.alice.interact.DragAdapter.CameraView;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.nonfree.NebulousIde;
import org.alice.stageide.StageIDE;
import org.alice.stageide.sceneeditor.snap.SnapState;
import org.alice.stageide.sceneeditor.viewmanager.MoveActiveCameraToMarkerActionOperation;
import org.alice.stageide.sceneeditor.viewmanager.MoveMarkerToActiveCameraActionOperation;
import org.lgna.project.ast.*;
import org.lgna.project.virtualmachine.UserInstance;
import org.lgna.story.*;
import org.lgna.story.implementation.*;


/**
 * Manages scene lifecycle events: project open, scene activation, and field addition.
 * Extracted from StorytellingSceneEditor to reduce its size.
 */
class SceneEditorLifecycleManager {

  private static final String SHOW_JOINTED_MODEL_VISUALIZATIONS_KEY = StorytellingSceneEditor.class.getName() + ".showJointedModelVisualizations";

  private final StorytellingSceneEditor editor;

  SceneEditorLifecycleManager(StorytellingSceneEditor editor) {
    this.editor = editor;
  }

  void prepareForProject(org.lgna.project.Project nextProject) {
    if (editor.onscreenRenderTarget != null) {
      editor.onscreenRenderTarget.forgetAllCachedItems();
      NebulousIde.nonfree.unloadNebulousModelData();
    }
    NebulousIde.nonfree.unloadPerson();
    if (editor.globalDragAdapter != null) {
      editor.globalDragAdapter.clear();
    }
  }

  void activateScene(UserField sceneField) {
    if (editor.movableSceneCameraImp != null) {
      editor.movableSceneCameraImp.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    }

    if (sceneField != null) {
      SProgram program = editor.getProgramInstanceInJavaForDelegate();
      program.setSimulationSpeedFactor(Double.POSITIVE_INFINITY);

      UserInstance sceneAliceInstance = editor.getActiveSceneInstance();
      SScene scene = sceneAliceInstance.getJavaInstance(SScene.class);

      SceneImp ACCEPTABLE_HACK_sceneImp = scene.getImplementation();
      ACCEPTABLE_HACK_sceneImp.ACCEPTABLE_HACK_FOR_SCENE_EDITOR_pushPerformMinimalInitialization();
      try {
        program.setActiveScene(scene);
      } finally {
        ACCEPTABLE_HACK_sceneImp.ACCEPTABLE_HACK_FOR_SCENE_EDITOR_popPerformMinimalInitialization();
      }
      UserMethod generatedSetupMethod = sceneAliceInstance.getType().getDeclaredMethod(StageIDE.PERFORM_GENERATED_SET_UP_METHOD_NAME);
      useSceneAsVehicleForDisconnectedModels(generatedSetupMethod);
      editor.getVirtualMachine().ENTRY_POINT_invoke(sceneAliceInstance, generatedSetupMethod);

      editor.getPropertyPanel().setSceneInstance(sceneAliceInstance);

      editor.instanceFactorySelectionPanel.setType(sceneAliceInstance.getType());
      findAndSetCameras(sceneField);

      assert ((editor.globalDragAdapter != null) && (editor.sceneCameraImp != null) && (editor.orthographicCameraImp != null));
      editor.globalDragAdapter.clearCameraViews();
      editor.globalDragAdapter.addCameraView(CameraView.MAIN, editor.sceneCameraImp.getSgCamera());
      editor.globalDragAdapter.makeCameraActive(editor.sceneCameraImp.getSgCamera());

      SceneImp sceneImp = editor.getActiveSceneImplementation();
      sceneImp.getSgComposite().addComponent(editor.snapGrid);
      editor.snapGrid.setTranslationOnly(0, 0, 0, AsSeenBy.SCENE);
      editor.snapGrid.setShowing(SnapState.getInstance().shouldShowSnapGrid());

      editor.setCameras();

      MoveActiveCameraToMarkerActionOperation.getInstance().setCamera(editor.movableSceneCameraImp);
      MoveMarkerToActiveCameraActionOperation.getInstance().setCamera(editor.movableSceneCameraImp);

      sceneImp.getSgComposite().addComponent(editor.orthographicCameraImp.getSgCamera().getParent());
      sceneImp.getSgComposite().addComponent(editor.layoutCameraImp.getSgCamera().getParent());

      editor.mainCameraViewTracker.updateMarkersForNewScene(sceneImp, editor.movableSceneCameraImp);

      editor.savedSceneEditorViewSelection = null;
      editor.mainCameraViewTracker.trackStartingCameraView();
      editor.mainCameraViewSelector.refreshModel();

      editor.fieldManager.setSelectedCameraMarker(null);
      editor.fieldManager.setSelectedObjectMarker(null);

      initializeMarkersAndFields(sceneField);
      program.setSimulationSpeedFactor(1.0);
    }
  }

  private void findAndSetCameras(UserField sceneField) {
    for (AbstractField field : sceneField.getValueType().getDeclaredFields()) {
      if (field.getValueType().isAssignableTo(SCamera.class)) {
        editor.sceneCameraImp = editor.getImplementation(field);
        editor.movableSceneCameraImp = editor.sceneCameraImp;
        editor.setIsVrActive(false);
        break;
      }
      if (field.getValueType().isAssignableTo(SVRUser.class)) {
        VrUserImp vrUserImp = editor.getImplementation(field);
        editor.sceneCameraImp = vrUserImp.getAbstraction().getHeadset().getImplementation();
        editor.movableSceneCameraImp = vrUserImp;
        editor.setIsVrActive(true);
        break;
      }
    }
  }

  private void initializeMarkersAndFields(UserField sceneField) {
    for (AbstractField field : sceneField.getValueType().getDeclaredFields()) {
      if (field.getValueType() != null && field.getValueType().isAssignableTo(SMarker.class)) {
        SMarker marker = editor.getInstanceInJavaVMForField(field, SMarker.class);
        MarkerImp markerImp = marker.getImplementation();
        if (field.getValueType().isAssignableTo(CameraMarker.class)) {
          ((PerspectiveCameraMarkerImp) markerImp).setVrActive(editor.isVrActive());
        }
        markerImp.setDisplayVisuals(true);
        markerImp.setShowing(true);
      }
      if (field instanceof UserField userField) {
        if (userField.getManagementLevel() == ManagementLevel.MANAGED) {
          editor.setInitialCodeStateForFieldForDelegate(userField, editor.getCurrentStateCodeForField(userField));
        }
      }
    }
  }

  private void useSceneAsVehicleForDisconnectedModels(UserMethod generatedSetupMethod) {
    for (Statement statement : generatedSetupMethod.body.getValue().statements.getValue()) {
      MethodInvocation setVehicleCall = SceneFieldCodeGenerator.asSetVehicleCall(statement);
      if (setVehicleCall == null) {
        continue;
      }
      SceneEditorLifecycleManagerLogic.useSceneAsVehicle(setVehicleCall);
    }
  }

  void handleAddField(UserField field) {
    if (field.getValueType().isAssignableTo(SMarker.class)) {
      SMarker marker = editor.getInstanceInJavaVMForField(field, SMarker.class);
      MarkerImp markerImp = marker.getImplementation();
      markerImp.setDisplayVisuals(true);
      markerImp.setShowing(true);

      if (field.getValueType().isAssignableTo(CameraMarker.class)) {
        editor.fieldManager.setSelectedCameraMarker(field);
        ((PerspectiveCameraMarkerImp) markerImp).setVrActive(editor.isVrActive());
      } else if (field.getValueType().isAssignableTo(SThingMarker.class)) {
        editor.fieldManager.setSelectedObjectMarker(field);
      }
    }
    editor.setInitialCodeStateForFieldForDelegate(field, editor.getCurrentStateCodeForField(field));
    if (SystemUtilities.isPropertyTrue(SHOW_JOINTED_MODEL_VISUALIZATIONS_KEY)) {
      if (field.getValueType().isAssignableTo(SJointedModel.class)) {
        SJointedModel jointedModel = editor.getInstanceInJavaVMForField(field, SJointedModel.class);
        JointedModelImp jointedModelImp = jointedModel.getImplementation();
        jointedModelImp.opacity.setValue(0.25f);
        jointedModelImp.showVisualization();
      } else if (field.getValueType().isAssignableTo(SModel.class)) {
        SModel model = editor.getInstanceInJavaVMForField(field, SModel.class);
        ModelImp modelImp = model.getImplementation();
        modelImp.showVisualization();
      }
    }
  }
}
