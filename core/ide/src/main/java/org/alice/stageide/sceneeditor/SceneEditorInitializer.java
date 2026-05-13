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

import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.alice.ide.IDE;
import org.alice.ide.ProjectDocumentFrame;
import org.alice.ide.icons.Icons;
import org.alice.interact.PickHint;
import org.alice.interact.condition.ClickedObjectCondition;
import org.alice.interact.condition.PickCondition;
import org.alice.interact.event.SelectionEvent;
import org.alice.interact.event.SelectionListener;
import org.alice.interact.manipulator.ManipulatorClickAdapter;
import org.alice.interact.InputState;
import org.alice.stageide.sceneeditor.interact.CameraNavigatorWidget;
import org.alice.stageide.sceneeditor.interact.GlobalDragAdapter;
import org.alice.stageide.sceneeditor.side.SideComposite;
import org.alice.stageide.sceneeditor.snap.SnapState;
import org.alice.stageide.sceneeditor.viewmanager.CameraMarkerTracker;
import org.alice.stageide.sceneeditor.viewmanager.CameraViewCellRenderer;
import org.alice.stageide.sceneeditor.views.InstanceFactorySelectionPanel;
import org.alice.interact.DragAdapter.CameraView;
import org.lgna.croquet.views.SpringPanel.Horizontal;
import org.lgna.croquet.views.SpringPanel.Vertical;
import org.lgna.story.implementation.OrthographicCameraImp;

import javax.swing.Icon;
import java.awt.event.MouseEvent;

/**
 * Handles one-time initialization of the scene editor UI components.
 * Extracted from StorytellingSceneEditor to reduce its size.
 */
class SceneEditorInitializer {

  private static final Icon EXPAND_ICON = new FlatSVGIcon(Icons.class.getResource("images/expand.svg")).derive(24, 24);
  private static final Icon CONTRACT_ICON = new FlatSVGIcon(Icons.class.getResource("images/contract.svg")).derive(24, 24);

  private final StorytellingSceneEditor editor;

  SceneEditorInitializer(StorytellingSceneEditor editor) {
    this.editor = editor;
  }

  void initialize() {
    editor.snapGrid = new org.alice.interact.manipulator.scenegraph.SnapGrid();
    SnapState.getInstance().getShowSnapGridState().addAndInvokeNewSchoolValueListener(editor.listeners.showSnapGridListener);
    SnapState.getInstance().getIsSnapEnabledState().addAndInvokeNewSchoolValueListener(editor.listeners.snapEnabledListener);
    SnapState.getInstance().getSnapGridSpacingState().addAndInvokeNewSchoolValueListener(editor.listeners.snapGridSpacingListener);

    ProjectDocumentFrame docFrame = IDE.getActiveInstance().getDocumentFrame();
    docFrame.getInstanceFactoryState().addAndInvokeNewSchoolValueListener(editor.listeners.instanceFactorySelectionListener);

    editor.globalDragAdapter = new GlobalDragAdapter(editor);
    editor.globalDragAdapter.setOnscreenRenderTarget(editor.onscreenRenderTarget);
    editor.onscreenRenderTarget.addRenderTargetListener(editor.renderTargetListener);
    editor.globalDragAdapter.setAnimator(editor.animator);
    if (editor.getSelectedField() != null) {
      editor.fieldManager.setSelectedFieldOnManipulator(editor.getSelectedField());
    }

    editor.mainCameraNavigatorWidget = new CameraNavigatorWidget(editor.globalDragAdapter, CameraView.MAIN);

    editor.expandButton = docFrame.getSetToSetupScenePerspectiveOperation().createButton();
    editor.expandButton.setClobberIcon(EXPAND_ICON);
    editor.contractButton = docFrame.getSetToCodePerspectiveOperation().createButton();
    editor.contractButton.setClobberIcon(CONTRACT_ICON);
    editor.instanceFactorySelectionPanel = new InstanceFactorySelectionPanel();
    editor.orthographicCameraImp = new OrthographicCameraImp();
    editor.orthographicCameraImp.getSgCamera().nearClippingPlaneDistance.setValue(.01d);

    editor.globalDragAdapter.addSelectionListener(new SelectionListener() {
      @Override
      public void selecting(SelectionEvent e) {
      }

      @Override
      public void selected(SelectionEvent e) {
        editor.fieldManager.handleManipulatorSelection(e);
      }
    });

    ClickedObjectCondition rightMouseAndInteractive = new ClickedObjectCondition(MouseEvent.BUTTON3, new PickCondition(PickHint.PickType.TURNABLE.pickHint()));
    ManipulatorClickAdapter rightClickAdapter = new ManipulatorClickAdapter() {
      @Override
      public void onClick(InputState clickInput) {
        editor.fieldManager.showRightClickMenuForModel(clickInput);
      }
    };
    editor.globalDragAdapter.addClickAdapter(rightClickAdapter, rightMouseAndInteractive);

    editor.mainCameraViewTracker = new CameraMarkerTracker(editor, editor.animator);
    editor.mainCameraViewSelector = editor.mainCameraMarkerList.getPrepModel().createComboBox();
    editor.mainCameraViewSelector.setRenderer(new CameraViewCellRenderer());
    editor.mainCameraViewSelector.setFontSize(15);
    editor.mainCameraMarkerList.addAndInvokeNewSchoolValueListener(editor.mainCameraViewTracker);
    editor.mainCameraMarkerList.addAndInvokeNewSchoolValueListener(editor.listeners.mainCameraViewSelectionObserver);
    editor.lookingGlassPanel.addComponent(editor.mainCameraViewSelector, Horizontal.CENTER, 0, Vertical.NORTH, 20);

    SideComposite.getInstance().getCameraMarkersTab().getMarkerListState().addAndInvokeNewSchoolValueListener(editor.listeners.cameraMarkerFieldSelectionListener);
    SideComposite.getInstance().getObjectMarkersTab().getMarkerListState().addAndInvokeNewSchoolValueListener(editor.listeners.objectMarkerFieldSelectionListener);
  }
}
