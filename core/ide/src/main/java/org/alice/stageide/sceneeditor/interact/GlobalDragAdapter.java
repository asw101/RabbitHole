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

package org.alice.stageide.sceneeditor.interact;

import edu.cmu.cs.dennisc.java.util.logging.Logger;
import edu.cmu.cs.dennisc.render.RenderCapabilities;
import edu.cmu.cs.dennisc.scenegraph.AbstractTransformable;
import edu.cmu.cs.dennisc.scenegraph.Silhouette;
import edu.cmu.cs.dennisc.scenegraph.scale.Resizer;
import org.alice.interact.*;
import org.alice.interact.ModifierMask.ModifierKey;
import org.alice.interact.condition.*;
import org.alice.interact.handle.*;
import org.alice.interact.manipulator.*;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Angle;
import org.alice.stageide.sceneeditor.StorytellingSceneEditor;
import org.alice.stageide.sceneeditor.interact.croquet.AbstractPredeterminedSetLocalTransformationActionOperation;
import org.alice.stageide.sceneeditor.interact.croquet.PredeterminedSetLocalJointTransformationActionOperation;
import org.alice.stageide.sceneeditor.interact.croquet.PredeterminedSetLocalTransformationActionOperation;
import org.alice.stageide.sceneeditor.interact.manipulators.*;
import org.alice.stageide.sceneeditor.side.SideComposite;
import org.alice.stageide.sceneeditor.snap.SnapState;
import org.lgna.croquet.Application;
import org.lgna.croquet.Group;
import org.lgna.croquet.ImmutableDataSingleSelectListState;
import org.lgna.croquet.event.ValueEvent;
import org.lgna.croquet.event.ValueListener;
import org.lgna.project.ast.UserField;
import org.lgna.story.SJoint;
import org.lgna.story.SThing;
import org.lgna.story.implementation.EntityImp;
import org.lgna.story.implementation.JointImp;

import java.awt.event.MouseEvent;

/**
 * @author David Culyba
 */
public class GlobalDragAdapter extends CroquetSupportingDragAdapter {
  private TargetManipulator dropTargetManipulator;

  private final StorytellingSceneEditor sceneEditor;

  public GlobalDragAdapter(StorytellingSceneEditor sceneEditor) {
    this.sceneEditor = sceneEditor;
    this.setUpControls();
  }

  @Override
  public boolean hasSceneEditor() {
    return this.sceneEditor != null;
  }

  private void setUpControls() {
    ModifierMask noModifiers = new ModifierMask(ModifierMask.NO_MODIFIERS_DOWN);

    // Camera Keyboard Control

    // camera translation
    MovementKey[] combinedKeys = new MovementKey[DEFAULT_MOVEMENT_KEYS.length + DEFAULT_ZOOM_KEYS.length];
    System.arraycopy(DEFAULT_MOVEMENT_KEYS, 0, combinedKeys, 0, DEFAULT_MOVEMENT_KEYS.length);
    System.arraycopy(DEFAULT_ZOOM_KEYS, 0, combinedKeys, DEFAULT_MOVEMENT_KEYS.length, DEFAULT_ZOOM_KEYS.length);
    CameraTranslateKeyManipulator cameraTranslateManip = new CameraTranslateKeyManipulator(combinedKeys);
    ManipulatorConditionSet cameraTranslate = new ManipulatorConditionSet(cameraTranslateManip);

    for (MovementKey movementKey : DEFAULT_MOVEMENT_KEYS) {
      AndInputCondition keyAndNotSelected = new AndInputCondition(new KeyPressCondition(movementKey.keyValue), new SelectedObjectCondition(PickHint.getNonInteractiveHint(), InvertedSelectedObjectCondition.ObjectSwitchBehavior.IGNORE_SWITCH));
      cameraTranslate.addCondition(keyAndNotSelected);
    }
    for (MovementKey zoomKey : DEFAULT_ZOOM_KEYS) {
      // almost the same as the move keys, but specifically with no modifiers pressed
      AndInputCondition keyAndNotSelected = new AndInputCondition(new KeyPressCondition(zoomKey.keyValue, noModifiers), new SelectedObjectCondition(PickHint.getNonInteractiveHint(), InvertedSelectedObjectCondition.ObjectSwitchBehavior.IGNORE_SWITCH));
      cameraTranslate.addCondition(keyAndNotSelected);
    }
    this.addManipulatorConditionSet(cameraTranslate);

    // camera rotation
    ManipulatorConditionSet cameraRotate = new ManipulatorConditionSet(new CameraRotateKeyManipulator(DEFAULT_ROTATE_KEYS));
    for (MovementKey turnKey : DEFAULT_ROTATE_KEYS) {
      AndInputCondition keyAndNotSelected = new AndInputCondition(new KeyPressCondition(turnKey.keyValue), new SelectedObjectCondition(PickHint.getNonInteractiveHint(), InvertedSelectedObjectCondition.ObjectSwitchBehavior.IGNORE_SWITCH));
      cameraRotate.addCondition(keyAndNotSelected);
    }
    this.addManipulatorConditionSet(cameraRotate);

    // Camera Mouse Control
    addCameraMouseControl();

    ManipulatorConditionSet mouseWheelCameraZoom = new ManipulatorConditionSet(new CameraZoomMouseWheelManipulator());
    MouseWheelCondition mouseWheelCondition = new MouseWheelCondition(new ModifierMask(ModifierMask.NO_MODIFIERS_DOWN));
    mouseWheelCameraZoom.addCondition(mouseWheelCondition);
    this.addManipulatorConditionSet(mouseWheelCameraZoom);

    // Object Manipulation

    // Object Keyboard Control
    // this can only work when the the scene editor is getting keyboard events, so, specifically NOT when an object is
    // selected via the dropdown on the right
    ManipulatorConditionSet objectTranslate = new ManipulatorConditionSet(new ObjectTranslateKeyManipulator(DEFAULT_MOVEMENT_KEYS));
    for (MovementKey movementKey : DEFAULT_MOVEMENT_KEYS) {
      AndInputCondition keyAndSelected = new AndInputCondition(new KeyPressCondition(movementKey.keyValue), new SelectedObjectCondition(PickHint.PickType.MOVEABLE.pickHint()));
      objectTranslate.addCondition(keyAndSelected);
    }
    this.addManipulatorConditionSet(objectTranslate);

    // Ability to drag stuff in from gallery
    OmniDirectionalBoundingBoxManipulator boundingBoxManipulator = new OmniDirectionalBoundingBoxManipulator();
    this.dropTargetManipulator = boundingBoxManipulator;
    ManipulatorConditionSet dragFromGallery = new ManipulatorConditionSet(boundingBoxManipulator, "Bounding Box Translate");
    dragFromGallery.addCondition(new DragAndDropCondition());
    this.addManipulatorConditionSet(dragFromGallery);

    // movable objects- translate, default interaction group
    ManipulatorConditionSet leftClickMouseTranslateObject = new ManipulatorConditionSet(new OmniDirectionalDragManipulator(), "Mouse Translate");
    MouseDragCondition leftClickMoveableObjects = new MouseDragCondition(MouseEvent.BUTTON1, new PickCondition(PickHint.PickType.MOVEABLE.pickHint()), noModifiers);
    leftClickMouseTranslateObject.addCondition(leftClickMoveableObjects);
    this.addManipulatorConditionSet(leftClickMouseTranslateObject);

    // turnable objects- rotate
    // This manipulation is used only when the "rotation" interaction group is selected. Disabled by default.
    ManipulatorConditionSet leftClickMouseRotateObjectLeftRight = new ManipulatorConditionSet(new HandlelessObjectRotateDragManipulator(MovementDirection.UP));
    MouseDragCondition leftClickTurnableObjects = new MouseDragCondition(MouseEvent.BUTTON1, new PickCondition(PickHint.PickType.TURNABLE.pickHint()), noModifiers);
    leftClickMouseRotateObjectLeftRight.addCondition(leftClickTurnableObjects);
    leftClickMouseRotateObjectLeftRight.setEnabled(false);
    this.addManipulatorConditionSet(leftClickMouseRotateObjectLeftRight);

    // resizable objects - scale
    // This manipulation is used only when the "resize" interaction group is selected. Disabled by default.
    ManipulatorConditionSet leftClickMouseResizeObject = new ManipulatorConditionSet(new ResizeDragManipulator(Resizer.UNIFORM, Resizer.XY_PLANE, Resizer.XZ_PLANE, Resizer.YZ_PLANE));
    MouseDragCondition leftClickResizableObjects = new MouseDragCondition(MouseEvent.BUTTON1, new PickCondition(PickHint.PickType.RESIZABLE.pickHint()), noModifiers);
    leftClickMouseResizeObject.addCondition(leftClickResizableObjects);
    leftClickMouseResizeObject.setEnabled(false);
    this.addManipulatorConditionSet(leftClickMouseResizeObject);

    // shift + drag -> movement in up/down direction only
    ManipulatorConditionSet mouseUpDownTranslateObject = new ManipulatorConditionSet(new ObjectUpDownDragManipulator());
    MouseDragCondition moveableObjectWithShift = new MouseDragCondition(MouseEvent.BUTTON1, new PickCondition(PickHint.PickType.MOVEABLE.pickHint()), new ModifierMask(ModifierKey.SHIFT));
    mouseUpDownTranslateObject.addCondition(moveableObjectWithShift);
    this.addManipulatorConditionSet(mouseUpDownTranslateObject);

    // ctrl + drag -> rotate
    ManipulatorConditionSet mouseRotateObjectLeftRight = new ManipulatorConditionSet(new HandlelessObjectRotateDragManipulator(MovementDirection.UP));
    MouseDragCondition moveableObjectWithCtrl = new MouseDragCondition(MouseEvent.BUTTON1, new PickCondition(PickHint.PickType.TURNABLE.pickHint()), new ModifierMask(ModifierKey.CONTROL));
    mouseRotateObjectLeftRight.addCondition(moveableObjectWithCtrl);
    this.addManipulatorConditionSet(mouseRotateObjectLeftRight);

    // alt + drag -> copy
    ManipulatorConditionSet mouseCopyAndMoveObject = new ManipulatorConditionSet(new CopyObjectDragManipulator());
    MouseDragCondition copyObjectWithAlt = new MouseDragCondition(MouseEvent.BUTTON1, new PickCondition(PickHint.PickType.MOVEABLE.pickHint()), new ModifierMask(ModifierKey.ALT));
    mouseCopyAndMoveObject.addCondition(copyObjectWithAlt);
    this.addManipulatorConditionSet(mouseCopyAndMoveObject);

    // click + drag -> drag/move
    ManipulatorConditionSet mouseHandleDrag = new ManipulatorConditionSet(new ObjectGlobalHandleDragManipulator());
    MouseDragCondition handleObjectCondition = new MouseDragCondition(MouseEvent.BUTTON1, new PickCondition(PickHint.PickType.THREE_D_HANDLE.pickHint()), noModifiers);
    MouseCondition handleObjectClickCondition = new MouseCondition(MouseEvent.BUTTON1, new PickCondition(PickHint.PickType.TWO_D_HANDLE.pickHint()), noModifiers);
    mouseHandleDrag.addCondition(handleObjectCondition);
    mouseHandleDrag.addCondition(handleObjectClickCondition);
    this.addManipulatorConditionSet(mouseHandleDrag);

    // select object on either left or right click
    ManipulatorConditionSet selectObject = new ManipulatorConditionSet(new SelectObjectDragManipulator(this));
    selectObject.addCondition(new MousePressCondition(MouseEvent.BUTTON1, new PickCondition(PickHint.PickType.SELECTABLE.pickHint())));
    selectObject.addCondition(new MousePressCondition(MouseEvent.BUTTON3, new PickCondition(PickHint.PickType.SELECTABLE.pickHint())));
    this.addManipulatorConditionSet(selectObject);

    // double click
    ManipulatorConditionSet getAGoodLookAtObject = new ManipulatorConditionSet(new GetAGoodLookAtManipulator());
    getAGoodLookAtObject.addCondition(new DoubleClickedObjectCondition(MouseEvent.BUTTON1, new PickCondition(PickHint.PickType.VIEWABLE.pickHint()), new ModifierMask(ModifierMask.JUST_CONTROL)));
    this.addManipulatorConditionSet(getAGoodLookAtObject);

    // right click is defined in the scene editor, for reasons I suppose

    // ux handles
    HandleSetupDelegate.setupHandles(this);

    // Interaction groups
    final InteractionGroup.PossibleObjects notJointObjects = new InteractionGroup.PossibleObjects(ObjectType.MODEL, ObjectType.OBJECT_MARKER, ObjectType.CAMERA_MARKER, ObjectType.MAIN_CAMERA);
    final InteractionGroup.PossibleObjects joints = new InteractionGroup.PossibleObjects(ObjectType.JOINT);
    final InteractionGroup.PossibleObjects anyObjects = new InteractionGroup.PossibleObjects(ObjectType.ANY);

    InteractionGroup defaultInteraction = new InteractionGroup(new InteractionGroup.InteractionInfo(anyObjects, HandleSet.DEFAULT_INTERACTION, leftClickMouseTranslateObject, PickHint.PickType.MOVEABLE));

    // rotation, translation, resize interaction groups
    //TODO: Make joint and non joint interactions
    InteractionGroup rotationInteraction = new InteractionGroup();
    rotationInteraction.addInteractionInfo(notJointObjects, HandleSet.ROTATION_INTERACTION, leftClickMouseRotateObjectLeftRight, PickHint.PickType.TURNABLE);
    rotationInteraction.addInteractionInfo(joints, HandleSet.JOINT_ROTATION_INTERACTION, leftClickMouseRotateObjectLeftRight, PickHint.PickType.TURNABLE);

    InteractionGroup translationInteraction = new InteractionGroup();
    translationInteraction.addInteractionInfo(notJointObjects, HandleSet.ABSOLUTE_TRANSLATION_INTERACTION, leftClickMouseTranslateObject, PickHint.PickType.MOVEABLE);
    translationInteraction.addInteractionInfo(joints, HandleSet.JOINT_TRANSLATION_INTERACTION, leftClickMouseTranslateObject, PickHint.PickType.MOVEABLE);

    InteractionGroup resizeInteraction = new InteractionGroup(new InteractionGroup.InteractionInfo(notJointObjects, HandleSet.RESIZE_INTERACTION, leftClickMouseResizeObject, PickHint.PickType.RESIZABLE));

    this.mapHandleStyleToInteractionGroup.put(HandleStyle.DEFAULT, defaultInteraction);
    this.mapHandleStyleToInteractionGroup.put(HandleStyle.ROTATION, rotationInteraction);
    this.mapHandleStyleToInteractionGroup.put(HandleStyle.TRANSLATION, translationInteraction);
    this.mapHandleStyleToInteractionGroup.put(HandleStyle.RESIZE, resizeInteraction);
    SideComposite.getInstance().getHandleStyleState().addAndInvokeNewSchoolValueListener(this.handleStyleListener);
    this.setHandleSelectionState(HandleStyle.DEFAULT);

    RenderCapabilities renderCapabilities = this.sceneEditor.getOnscreenRenderTarget().getActualCapabilities();
    if (renderCapabilities.getStencilBits() > 0) {
      Silhouette sgSilhouette = new Silhouette();
      //sgSilhouette.color.setValue( Color4f.YELLOW );
      //sgSilhouette.width.setValue( 1.5f );
      this.setSgSilhouette(sgSilhouette);
    }
  }

  public void addClickAdapter(ManipulatorClickAdapter clickAdapter, InputCondition... conditions) {
    ManipulatorConditionSet conditionSet = new ManipulatorConditionSet(new ClickAdapterManipulator(clickAdapter));
    for (InputCondition condition : conditions) {
      conditionSet.addCondition(condition);
    }
    this.addManipulatorConditionSet(conditionSet);
  }

  @Override
  protected ImmutableDataSingleSelectListState<HandleStyle> getHandleStyleState() {
    return SideComposite.getInstance().getHandleStyleState();
  }

  public AffineMatrix4x4 getDropTargetTransformation() {
    return this.dropTargetManipulator.getTargetTransformation();
  }

  private final ValueListener<HandleStyle> handleStyleListener = new ValueListener<HandleStyle>() {
    @Override
    public void valueChanged(ValueEvent<HandleStyle> e) {
      setInteractionState(e.getNextValue());
    }
  };

  @Override
  public boolean shouldSnapToRotation() {
    return SnapState.getInstance().shouldSnapToRotation();
  }

  @Override
  public boolean shouldSnapToGround() {
    return SnapState.getInstance().shouldSnapToGround();
  }

  @Override
  public boolean shouldSnapToGrid() {
    return SnapState.getInstance().shouldSnapToGrid();
  }

  @Override
  public double getGridSpacing() {
    return SnapState.getInstance().getGridSpacing();
  }

  @Override
  public Angle getRotationSnapAngle() {
    return SnapState.getInstance().getRotationSnapAngle();
  }

  @Override
  public void undoRedoEndManipulation(AbstractManipulator manipulator, AffineMatrix4x4 originalTransformation) {
    AbstractTransformable sgManipulatedTransformable = manipulator.getManipulatedTransformable();
    if (sgManipulatedTransformable != null) {
      AffineMatrix4x4 newTransformation = sgManipulatedTransformable.getLocalTransformation();

      if (newTransformation.equals(originalTransformation)) {
        Logger.warning("Adding an undoable action for a manipulation that didn't actually change the transformation.");
      }
      if (originalTransformation == null) {
        Logger.severe("Ending manipulation where the original transformation is null.");
      }

      SThing aliceThing = EntityImp.getAbstractionFromSgElement(sgManipulatedTransformable);
      if (aliceThing == null) {
        return;
      }
      AbstractPredeterminedSetLocalTransformationActionOperation undoOperation;
      if (aliceThing instanceof SJoint) {
        JointImp jointImp = (JointImp) EntityImp.getInstance(sgManipulatedTransformable);
        SThing jointedModelThing = jointImp.getJointedModelParent().getAbstraction();
        UserField manipulatedField = sceneEditor.getFieldForInstanceInJavaVM(jointedModelThing);
        undoOperation = new PredeterminedSetLocalJointTransformationActionOperation(Application.PROJECT_GROUP, false, this.getAnimator(), manipulatedField, jointImp.getJointId(), originalTransformation, newTransformation, manipulator.getUndoRedoDescription());
        undoOperation.fire();
      } else {
        UserField manipulatedField = sceneEditor.getFieldForInstanceInJavaVM(aliceThing);
        Group group = GlobalDragAdapterLogic.resolveUndoGroup(manipulatedField);
        undoOperation = new PredeterminedSetLocalTransformationActionOperation(group, false, this.getAnimator(), manipulatedField, originalTransformation, newTransformation, manipulator.getUndoRedoDescription());
        undoOperation.fire();
      }
    }
  }
}
