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

import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.scenegraph.scale.Resizer;
import org.alice.interact.*;
import org.alice.interact.condition.MovementDescription;
import org.alice.interact.event.ManipulationEvent;
import org.alice.interact.event.ManipulationEventCriteria;
import org.alice.interact.handle.*;
import org.alice.interact.manipulator.*;
import org.alice.stageide.sceneeditor.interact.manipulators.ScaleDragManipulator;

/**
 * Stateless delegate that creates all visual handles for object interaction modes.
 * Extracted from {@link GlobalDragAdapter} to reduce class size.
 */
class HandleSetupDelegate {

  static void setupHandles(DragAdapter adapter) {
    ManipulationAxes handleAxis = new ManipulationAxes();
    handleAxis.addToGroup(HandleSet.HandleGroup.VISUALIZATION);
    handleAxis.addCondition(new ManipulationEventCriteria(ManipulationEvent.EventType.Rotate, null, PickHint.getAnythingHint()));
    handleAxis.addCondition(new ManipulationEventCriteria(ManipulationEvent.EventType.Translate, null, PickHint.getAnythingHint()));
    adapter.addManipulationListener(handleAxis);
    handleAxis.setDragAdapterAndAddHandle(adapter);
    handleAxis.setName("handleAxis");

    StoodUpRotationRingHandle rotateAboutYAxisStoodUp = new StoodUpRotationRingHandle(MovementDirection.UP, RotationRingHandle.HandlePosition.BOTTOM);
    rotateAboutYAxisStoodUp.setManipulation(new ObjectRotateDragManipulator() {
      @Override
      protected HandleSet getHandleSetToEnable() {
        return new HandleSet(HandleSet.HandleGroup.Y_AXIS, HandleSet.HandleGroup.VISUALIZATION, HandleSet.HandleGroup.STOOD_UP_ROTATION);
      }
    });
    rotateAboutYAxisStoodUp.addToSet(HandleSet.DEFAULT_INTERACTION);
    rotateAboutYAxisStoodUp.addToGroups(HandleSet.HandleGroup.DEFAULT, HandleSet.HandleGroup.Y_AXIS, HandleSet.HandleGroup.VISUALIZATION, HandleSet.HandleGroup.STOOD_UP_ROTATION);
    rotateAboutYAxisStoodUp.addCondition(new ManipulationEventCriteria(ManipulationEvent.EventType.Rotate, new MovementDescription(MovementDirection.UP, MovementType.STOOD_UP), PickHint.PickType.TURNABLE.pickHint()));
    rotateAboutYAxisStoodUp.addCondition(new ManipulationEventCriteria(ManipulationEvent.EventType.Rotate, new MovementDescription(MovementDirection.DOWN, MovementType.STOOD_UP), PickHint.PickType.TURNABLE.pickHint()));
    adapter.addManipulationListener(rotateAboutYAxisStoodUp);
    rotateAboutYAxisStoodUp.setDragAdapterAndAddHandle(adapter);
    rotateAboutYAxisStoodUp.setName("rotateAboutYAxisStoodUp");

    RotationRingHandle rotateAboutYAxis = new RotationRingHandle(MovementDirection.UP, Color4f.RED);
    rotateAboutYAxis.setManipulation(new ObjectRotateDragManipulator());
    rotateAboutYAxis.addToSet(HandleSet.ROTATION_INTERACTION);
    rotateAboutYAxis.addToGroups(HandleSet.HandleGroup.Y_AXIS, HandleSet.HandleGroup.VISUALIZATION);
    rotateAboutYAxis.setDragAdapterAndAddHandle(adapter);
    rotateAboutYAxis.setName("rotateAboutYAxis");

    RotationRingHandle rotateAboutXAxis = new RotationRingHandle(MovementDirection.LEFT, Color4f.BLUE);
    rotateAboutXAxis.setManipulation(new ObjectRotateDragManipulator());
    rotateAboutXAxis.addToSet(HandleSet.ROTATION_INTERACTION);
    rotateAboutXAxis.addToGroups(HandleSet.HandleGroup.X_AXIS, HandleSet.HandleGroup.VISUALIZATION);
    rotateAboutXAxis.setDragAdapterAndAddHandle(adapter);
    rotateAboutXAxis.setName("rotateAboutXAxis");

    RotationRingHandle rotateAboutZAxis = new RotationRingHandle(MovementDirection.BACKWARD, Color4f.WHITE);
    rotateAboutZAxis.setManipulation(new ObjectRotateDragManipulator());
    rotateAboutZAxis.addToSet(HandleSet.ROTATION_INTERACTION);
    rotateAboutZAxis.addToGroups(HandleSet.HandleGroup.Z_AXIS, HandleSet.HandleGroup.VISUALIZATION);
    rotateAboutZAxis.setDragAdapterAndAddHandle(adapter);
    rotateAboutZAxis.setName("rotateAboutZAxis");

    JointRotationRingHandle rotateJointAboutZAxis = new JointRotationRingHandle(MovementDirection.BACKWARD, Color4f.WHITE);
    rotateJointAboutZAxis.setManipulation(new ObjectRotateDragManipulator());
    rotateJointAboutZAxis.addToSet(HandleSet.JOINT_ROTATION_INTERACTION);
    rotateJointAboutZAxis.addToGroups(HandleSet.HandleGroup.Z_AXIS, HandleSet.HandleGroup.VISUALIZATION, HandleSet.HandleGroup.JOINT);
    rotateJointAboutZAxis.setDragAdapterAndAddHandle(adapter);
    rotateJointAboutZAxis.setName("rotateJointAboutZAxis");

    JointRotationRingHandle rotateJointAboutYAxis = new JointRotationRingHandle(MovementDirection.UP, Color4f.RED);
    rotateJointAboutYAxis.setManipulation(new ObjectRotateDragManipulator());
    rotateJointAboutYAxis.addToSet(HandleSet.JOINT_ROTATION_INTERACTION);
    rotateJointAboutYAxis.addToGroups(HandleSet.HandleGroup.Y_AXIS, HandleSet.HandleGroup.VISUALIZATION, HandleSet.HandleGroup.JOINT);
    rotateJointAboutYAxis.setDragAdapterAndAddHandle(adapter);
    rotateJointAboutYAxis.setName("rotateJointAboutYAxis");

    JointRotationRingHandle rotateJointAboutXAxis = new JointRotationRingHandle(MovementDirection.LEFT, Color4f.BLUE);
    rotateJointAboutXAxis.setManipulation(new ObjectRotateDragManipulator());
    rotateJointAboutXAxis.addToSet(HandleSet.JOINT_ROTATION_INTERACTION);
    rotateJointAboutXAxis.addToGroups(HandleSet.HandleGroup.X_AXIS, HandleSet.HandleGroup.VISUALIZATION, HandleSet.HandleGroup.JOINT);
    rotateJointAboutXAxis.setDragAdapterAndAddHandle(adapter);
    rotateJointAboutXAxis.setName("rotateJointAboutXAxis");

    LinearTranslateHandle translateJointYAxis = new LinearTranslateHandle(new MovementDescription(MovementDirection.UP, MovementType.LOCAL), Color4f.GREEN);
    translateJointYAxis.setManipulation(new LinearDragManipulator());
    translateJointYAxis.addToGroups(HandleSet.HandleGroup.LOCAL, HandleSet.HandleGroup.Y_AXIS, HandleSet.HandleGroup.VISUALIZATION, HandleSet.HandleGroup.JOINT);
    translateJointYAxis.addToSet(HandleSet.JOINT_TRANSLATION_INTERACTION);
    translateJointYAxis.setDragAdapterAndAddHandle(adapter);
    translateJointYAxis.setName("translateJointYAxis");

    LinearTranslateHandle translateJointXAxis = new LinearTranslateHandle(new MovementDescription(MovementDirection.RIGHT, MovementType.LOCAL), Color4f.RED);
    translateJointXAxis.setManipulation(new LinearDragManipulator());
    translateJointXAxis.addToGroups(HandleSet.HandleGroup.LOCAL, HandleSet.HandleGroup.X_AXIS, HandleSet.HandleGroup.VISUALIZATION, HandleSet.HandleGroup.JOINT);
    translateJointXAxis.addToSet(HandleSet.JOINT_TRANSLATION_INTERACTION);
    translateJointXAxis.setDragAdapterAndAddHandle(adapter);
    translateJointXAxis.setName("translateJointXAxis");

    LinearTranslateHandle translateJointZAxis = new LinearTranslateHandle(new MovementDescription(MovementDirection.FORWARD, MovementType.LOCAL), Color4f.WHITE);
    translateJointZAxis.setManipulation(new LinearDragManipulator());
    translateJointZAxis.addToGroups(HandleSet.HandleGroup.LOCAL, HandleSet.HandleGroup.Z_AXIS, HandleSet.HandleGroup.VISUALIZATION, HandleSet.HandleGroup.JOINT);
    translateJointZAxis.addToSet(HandleSet.JOINT_TRANSLATION_INTERACTION);
    translateJointZAxis.setDragAdapterAndAddHandle(adapter);
    translateJointZAxis.setName("translateJointZAxis");

    LinearTranslateHandle translateUp = new LinearTranslateHandle(new MovementDescription(MovementDirection.UP, MovementType.ABSOLUTE), Color4f.YELLOW);
    LinearTranslateHandle translateDown = new LinearTranslateHandle(new MovementDescription(MovementDirection.DOWN, MovementType.ABSOLUTE), Color4f.YELLOW);
    translateUp.setManipulation(new LinearDragManipulator());
    translateUp.addToGroups(HandleSet.HandleGroup.ABSOLUTE_TRANSLATION, HandleSet.HandleGroup.Y_AXIS, HandleSet.HandleGroup.VISUALIZATION);
    translateDown.addToGroups(HandleSet.HandleGroup.ABSOLUTE_TRANSLATION, HandleSet.HandleGroup.Y_AXIS, HandleSet.HandleGroup.VISUALIZATION);
    translateUp.addToGroup(HandleSet.HandleGroup.INTERACTION);
    translateUp.addToGroup(HandleSet.HandleGroup.VISUALIZATION);
    translateDown.addToGroup(HandleSet.HandleGroup.VISUALIZATION);
    translateDown.addCondition(new ManipulationEventCriteria(ManipulationEvent.EventType.Translate, new MovementDescription(MovementDirection.DOWN, MovementType.ABSOLUTE), PickHint.PickType.MOVEABLE.pickHint()));
    translateUp.addCondition(new ManipulationEventCriteria(ManipulationEvent.EventType.Translate, new MovementDescription(MovementDirection.UP, MovementType.ABSOLUTE), PickHint.PickType.MOVEABLE.pickHint()));
    adapter.addManipulationListener(translateUp);
    adapter.addManipulationListener(translateDown);
    translateDown.setDragAdapterAndAddHandle(adapter);
    translateUp.setDragAdapterAndAddHandle(adapter);
    translateDown.setName("translateDown");
    translateUp.setName("translateUp");

    LinearTranslateHandle translateXAxisRight = new LinearTranslateHandle(new MovementDescription(MovementDirection.RIGHT, MovementType.ABSOLUTE), Color4f.YELLOW);
    LinearTranslateHandle translateXAxisLeft = new LinearTranslateHandle(new MovementDescription(MovementDirection.LEFT, MovementType.ABSOLUTE), Color4f.YELLOW);
    translateXAxisLeft.setManipulation(new LinearDragManipulator());
    //Add the left handle to the group to be shown by the system
    translateXAxisLeft.addToGroups(HandleSet.HandleGroup.ABSOLUTE_TRANSLATION, HandleSet.HandleGroup.X_AXIS, HandleSet.HandleGroup.X_AND_Z_AXIS);
    translateXAxisRight.addToGroups(HandleSet.HandleGroup.ABSOLUTE_TRANSLATION, HandleSet.HandleGroup.X_AXIS, HandleSet.HandleGroup.X_AND_Z_AXIS);
    translateXAxisLeft.addToGroup(HandleSet.HandleGroup.INTERACTION);
    translateXAxisLeft.addToGroup(HandleSet.HandleGroup.VISUALIZATION);
    translateXAxisRight.addToGroup(HandleSet.HandleGroup.VISUALIZATION);
    translateXAxisLeft.addCondition(new ManipulationEventCriteria(ManipulationEvent.EventType.Translate, new MovementDescription(MovementDirection.LEFT, MovementType.ABSOLUTE), PickHint.PickType.MOVEABLE.pickHint()));
    translateXAxisRight.addCondition(new ManipulationEventCriteria(ManipulationEvent.EventType.Translate, new MovementDescription(MovementDirection.RIGHT, MovementType.ABSOLUTE), PickHint.PickType.MOVEABLE.pickHint()));
    adapter.addManipulationListener(translateXAxisRight);
    adapter.addManipulationListener(translateXAxisLeft);
    translateXAxisRight.setDragAdapterAndAddHandle(adapter);
    translateXAxisLeft.setDragAdapterAndAddHandle(adapter);
    translateXAxisRight.setName("translateXAxisRight");
    translateXAxisLeft.setName("translateXAxisLeft");

    LinearTranslateHandle translateForward = new LinearTranslateHandle(new MovementDescription(MovementDirection.FORWARD, MovementType.ABSOLUTE), Color4f.YELLOW);
    LinearTranslateHandle translateBackward = new LinearTranslateHandle(new MovementDescription(MovementDirection.BACKWARD, MovementType.ABSOLUTE), Color4f.YELLOW);
    translateForward.setManipulation(new LinearDragManipulator());
    translateForward.addToGroups(HandleSet.HandleGroup.ABSOLUTE_TRANSLATION, HandleSet.HandleGroup.Z_AXIS, HandleSet.HandleGroup.X_AND_Z_AXIS, HandleSet.HandleGroup.VISUALIZATION);
    translateBackward.addToGroups(HandleSet.HandleGroup.ABSOLUTE_TRANSLATION, HandleSet.HandleGroup.Z_AXIS, HandleSet.HandleGroup.X_AND_Z_AXIS, HandleSet.HandleGroup.VISUALIZATION);
    translateForward.addToGroup(HandleSet.HandleGroup.INTERACTION);
    translateForward.addToGroup(HandleSet.HandleGroup.VISUALIZATION);
    translateBackward.addToGroup(HandleSet.HandleGroup.VISUALIZATION);
    translateBackward.addCondition(new ManipulationEventCriteria(ManipulationEvent.EventType.Translate, new MovementDescription(MovementDirection.BACKWARD, MovementType.ABSOLUTE), PickHint.PickType.MOVEABLE.pickHint()));
    translateForward.addCondition(new ManipulationEventCriteria(ManipulationEvent.EventType.Translate, new MovementDescription(MovementDirection.FORWARD, MovementType.ABSOLUTE), PickHint.PickType.MOVEABLE.pickHint()));
    adapter.addManipulationListener(translateForward);
    adapter.addManipulationListener(translateBackward);
    translateForward.setDragAdapterAndAddHandle(adapter);
    translateBackward.setDragAdapterAndAddHandle(adapter);
    translateForward.setName("translateForward");
    translateBackward.setName("translateBackward");

    LinearScaleHandle scaleAxisUniform = LinearScaleHandle.createFromResizer(Resizer.UNIFORM);
    scaleAxisUniform.setManipulation(new ScaleDragManipulator());
    scaleAxisUniform.addToSet(HandleSet.RESIZE_INTERACTION);
    scaleAxisUniform.addToGroups(HandleSet.HandleGroup.RESIZE_AXIS, HandleSet.HandleGroup.VISUALIZATION);
    scaleAxisUniform.addCondition(new ManipulationEventCriteria(ManipulationEvent.EventType.Scale, scaleAxisUniform.getMovementDescription(), PickHint.PickType.RESIZABLE.pickHint()));
    scaleAxisUniform.setDragAdapterAndAddHandle(adapter);
    scaleAxisUniform.setName("scaleAxisUniform");

    LinearScaleHandle scaleAxisX = LinearScaleHandle.createFromResizer(Resizer.X_AXIS);
    scaleAxisX.setManipulation(new ScaleDragManipulator());
    scaleAxisX.addToSet(HandleSet.RESIZE_INTERACTION);
    scaleAxisX.addToGroups(HandleSet.HandleGroup.X_AXIS, HandleSet.HandleGroup.VISUALIZATION);
    scaleAxisX.addCondition(new ManipulationEventCriteria(ManipulationEvent.EventType.Scale, scaleAxisX.getMovementDescription(), PickHint.PickType.RESIZABLE.pickHint()));
    scaleAxisX.setDragAdapterAndAddHandle(adapter);
    scaleAxisX.setName("scaleAxisX");

    LinearScaleHandle scaleAxisY = LinearScaleHandle.createFromResizer(Resizer.Y_AXIS);
    scaleAxisY.setManipulation(new ScaleDragManipulator());
    scaleAxisY.addToSet(HandleSet.RESIZE_INTERACTION);
    scaleAxisY.addToGroups(HandleSet.HandleGroup.Y_AXIS, HandleSet.HandleGroup.VISUALIZATION);
    scaleAxisY.addCondition(new ManipulationEventCriteria(ManipulationEvent.EventType.Scale, scaleAxisY.getMovementDescription(), PickHint.PickType.RESIZABLE.pickHint()));
    scaleAxisY.setDragAdapterAndAddHandle(adapter);
    scaleAxisY.setName("scaleAxisY");

    LinearScaleHandle scaleAxisZ = LinearScaleHandle.createFromResizer(Resizer.Z_AXIS);
    scaleAxisZ.setManipulation(new ScaleDragManipulator());
    scaleAxisZ.addToSet(HandleSet.RESIZE_INTERACTION);
    scaleAxisZ.addToGroups(HandleSet.HandleGroup.Z_AXIS, HandleSet.HandleGroup.VISUALIZATION);
    scaleAxisZ.addCondition(new ManipulationEventCriteria(ManipulationEvent.EventType.Scale, scaleAxisZ.getMovementDescription(), PickHint.PickType.RESIZABLE.pickHint()));
    scaleAxisZ.setDragAdapterAndAddHandle(adapter);
    scaleAxisZ.setName("scaleAxisZ");

    LinearScaleHandle scaleAxisXY = LinearScaleHandle.createFromResizer(Resizer.XY_PLANE);
    scaleAxisXY.setManipulation(new ScaleDragManipulator());
    scaleAxisXY.addToSet(HandleSet.RESIZE_INTERACTION);
    scaleAxisXY.addToGroups(HandleSet.HandleGroup.X_AND_Y_AXIS, HandleSet.HandleGroup.VISUALIZATION);
    scaleAxisXY.addCondition(new ManipulationEventCriteria(ManipulationEvent.EventType.Scale, scaleAxisXY.getMovementDescription(), PickHint.PickType.RESIZABLE.pickHint()));
    scaleAxisXY.setDragAdapterAndAddHandle(adapter);
    scaleAxisXY.setName("scaleAxisXY");

    LinearScaleHandle scaleAxisXZ = LinearScaleHandle.createFromResizer(Resizer.XZ_PLANE);
    scaleAxisXZ.setManipulation(new ScaleDragManipulator());
    scaleAxisXZ.addToSet(HandleSet.RESIZE_INTERACTION);
    scaleAxisXZ.addToGroups(HandleSet.HandleGroup.X_AND_Z_AXIS, HandleSet.HandleGroup.VISUALIZATION);
    scaleAxisXZ.addCondition(new ManipulationEventCriteria(ManipulationEvent.EventType.Scale, scaleAxisXZ.getMovementDescription(), PickHint.PickType.RESIZABLE.pickHint()));
    scaleAxisXZ.setDragAdapterAndAddHandle(adapter);
    scaleAxisXZ.setName("scaleAxisXZ");

    LinearScaleHandle scaleAxisYZ = LinearScaleHandle.createFromResizer(Resizer.YZ_PLANE);
    scaleAxisYZ.setManipulation(new ScaleDragManipulator());
    scaleAxisYZ.addToSet(HandleSet.RESIZE_INTERACTION);
    scaleAxisYZ.addToGroups(HandleSet.HandleGroup.Y_AND_Z_AXIS, HandleSet.HandleGroup.VISUALIZATION);
    scaleAxisYZ.addCondition(new ManipulationEventCriteria(ManipulationEvent.EventType.Scale, scaleAxisYZ.getMovementDescription(), PickHint.PickType.RESIZABLE.pickHint()));
    scaleAxisYZ.setDragAdapterAndAddHandle(adapter);
    scaleAxisYZ.setName("scaleAxisYZ");
  }

  private HandleSetupDelegate() {
  }
}
