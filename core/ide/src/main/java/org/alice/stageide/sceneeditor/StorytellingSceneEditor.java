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

import edu.cmu.cs.dennisc.animation.Animator;
import edu.cmu.cs.dennisc.animation.ClockBasedAnimator;
import edu.cmu.cs.dennisc.render.OnscreenRenderTarget;
import edu.cmu.cs.dennisc.render.RenderCapabilities;
import edu.cmu.cs.dennisc.render.event.AutomaticDisplayEvent;
import edu.cmu.cs.dennisc.render.event.AutomaticDisplayListener;
import edu.cmu.cs.dennisc.render.gl.GlrRenderFactory;
import edu.cmu.cs.dennisc.scenegraph.AbstractCamera;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import org.alice.ide.ReasonToDisableSomeAmountOfRendering;
import org.alice.ide.instancefactory.InstanceFactory;
import org.alice.ide.instancefactory.croquet.InstanceFactoryState;
import org.alice.ide.preferences.IsToolBarShowing;
import org.alice.ide.sceneeditor.AbstractSceneEditor;
import org.alice.interact.manipulator.scenegraph.SnapGrid;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.stageide.StageIDE;
import org.alice.stageide.croquet.models.sceneditor.ViewListSelectionState;
import org.alice.stageide.run.RunComposite;
import org.alice.stageide.sceneeditor.interact.CameraNavigatorWidget;
import org.alice.stageide.sceneeditor.interact.GlobalDragAdapter;
import org.alice.stageide.sceneeditor.side.SideComposite;
import org.alice.stageide.sceneeditor.viewmanager.CameraMarkerTracker;
import org.alice.stageide.sceneeditor.views.InstanceFactorySelectionPanel;
import org.alice.stageide.sceneeditor.views.SceneObjectPropertyManagerPanel;
import org.lgna.croquet.DropReceptor;
import org.lgna.croquet.ImmutableDataSingleSelectListState;
import org.lgna.croquet.history.UserActivity;
import org.lgna.croquet.views.Button;
import org.lgna.croquet.views.ComboBox;
import org.lgna.project.Project;
import org.lgna.project.ast.*;
import org.lgna.project.virtualmachine.UserInstance;
import org.lgna.story.*;
import org.lgna.story.implementation.*;
import org.alice.stageide.sceneeditor.viewmanager.MoveActiveCameraToMarkerActionOperation;
import org.alice.stageide.sceneeditor.viewmanager.MoveMarkerToActiveCameraActionOperation;
import org.alice.stageide.sceneeditor.viewmanager.MoveMarkerToSelectedObjectActionOperation;
import org.alice.stageide.sceneeditor.viewmanager.MoveSelectedObjectToMarkerActionOperation;
import org.alice.tools.EatmeSceneObjectAddedEvidence;

import javax.swing.SwingUtilities;
import java.util.Map;

/**
 * @author dculyba
 */
public class StorytellingSceneEditor extends AbstractSceneEditor {
  private boolean isVrScene;

  private static class SingletonHolder {
    private static StorytellingSceneEditor instance = new StorytellingSceneEditor();
  }

  public static StorytellingSceneEditor getInstance() {
    return SingletonHolder.instance;
  }

  private final SceneEditorDropReceptor dropReceptor = new SceneEditorDropReceptor(this);
  final SceneEditorFieldManager fieldManager = new SceneEditorFieldManager(this);
  final SceneRenderTargetListener renderTargetListener = new SceneRenderTargetListener(this);
  private final SceneEditorInitializer initializer = new SceneEditorInitializer(this);
  private final SceneEditorLifecycleManager lifecycleManager = new SceneEditorLifecycleManager(this);

  private final Runnable uiRefresher = () -> {
    revalidateAndRepaint();
    SideComposite.getInstance().getObjectPropertiesTab().getView().revalidateAndRepaint();
    SideComposite.getInstance().getObjectMarkersTab().getView().revalidateAndRepaint();
  };

  private StorytellingSceneEditor() {
  }

  public DropReceptor getDropReceptor() {
    return this.dropReceptor;
  }

  AutomaticDisplayListener automaticDisplayListener = new AutomaticDisplayListener() {
    @Override
    public void automaticDisplayCompleted(AutomaticDisplayEvent e) {
      StorytellingSceneEditor.this.animator.update();
    }
  };
  OnscreenRenderTarget onscreenRenderTarget = GlrRenderFactory.getInstance().createOnscreenRenderTarget(new RenderCapabilities.Builder().stencilBits(0).build());

  boolean isInitialized = false;

  ClockBasedAnimator animator = new ClockBasedAnimator();
  LookingGlassPanel lookingGlassPanel = new LookingGlassPanel(onscreenRenderTarget);
  GlobalDragAdapter globalDragAdapter;
  final SceneEditorListeners listeners = new SceneEditorListeners(this);
  // The location of the Camera or VR user ground. Same as sceneCameraImp for non VR scenes.
  TransformableImp movableSceneCameraImp;
  // The camera or VR headset. Same as movableSceneCameraImp for non VR scenes.
  CameraImp<SymmetricPerspectiveCamera> sceneCameraImp;
  CameraNavigatorWidget mainCameraNavigatorWidget = null;
  Button expandButton;
  Button contractButton;
  InstanceFactorySelectionPanel instanceFactorySelectionPanel = null;

  private final Button runButton = IsToolBarShowing.getValue() ? null : RunComposite.getInstance().getLaunchOperation().createButton();

  OrthographicCameraImp orthographicCameraImp = null;
  final SymmetricPerspectiveCameraImp layoutCameraImp = new SymmetricPerspectiveCameraImp(null);

  ComboBox<CameraOption> mainCameraViewSelector;
  CameraMarkerTracker mainCameraViewTracker;
  CameraOption savedSceneEditorViewSelection = null;

  ImmutableDataSingleSelectListState<CameraOption> mainCameraMarkerList = ViewListSelectionState.getInstance();

  boolean selectionIsFromInstanceSelector = false;
  private boolean selectionIsFromMain = false;

  SnapGrid snapGrid;

  public boolean isStartingCameraView() {
    return mainCameraViewSelector.getModel().getListSelectionState().getValue() == CameraOption.STARTING_CAMERA_VIEW;
  }

  public void setStartingCameraMarkerTransformation(AffineMatrix4x4 transform) {
    movableSceneCameraImp.setLocalTransformation(transform);
  }

  public static class SceneEditorProgramImp extends ProgramImp {
    public SceneEditorProgramImp(SProgram abstraction) {
      super(abstraction, StorytellingSceneEditor.getInstance().onscreenRenderTarget);
    }

    @Override
    public Animator getAnimator() {
      return StorytellingSceneEditor.getInstance().animator;
    }
  }

  @Override
  protected UserInstance createProgramInstance() {
    try (ProgramImp.FactoryScope ignored = ProgramImp.useFactory(SceneEditorProgramImp::new)) {
      return super.createProgramInstance();
    }
  }

  void setSelectedInstance(InstanceFactory instanceFactory) {
    fieldManager.setSelectedInstance(instanceFactory);
  }

  public void setSelectedExpression(Expression expression) {
    if (!this.selectionIsFromMain) {
      this.selectionIsFromMain = true;
      if (this.globalDragAdapter != null) {
        fieldManager.setSelectedExpressionOnManipulator(expression);
      }
      this.selectionIsFromMain = false;
    }
  }

  public void centerCameraOnSelectedField(UserActivity activity) {
    UserField field = getSelectedField();
    if (getActiveSceneField() != field) {
      mainCameraViewTracker.centerCameraOnField(activity, movableSceneCameraImp, field);
    }
  }

  @Override
  public void setSelectedField(UserType<?> declaringType, UserField field) {
    if (!this.selectionIsFromMain) {
      this.selectionIsFromMain = true;
      final AbstractType<?, ?, ?> valueType = field.getValueType();
      if (isSelectableType(valueType)) {
        super.setSelectedField(declaringType, field);

        MoveSelectedObjectToMarkerActionOperation.getInstance().setSelectedField(field);
        MoveMarkerToSelectedObjectActionOperation.getInstance().setSelectedField(field);
        if (!this.selectionIsFromInstanceSelector) {
          StageIDE ide = StageIDE.getActiveInstance();
          InstanceFactoryState instanceFactoryState = ide.getDocumentFrame().getInstanceFactoryState();
          StorytellingSceneEditorLogic.InstanceFactorySelection selection =
              StorytellingSceneEditorLogic.determineInstanceFactorySelection(field == this.getActiveSceneField());
          if (selection == StorytellingSceneEditorLogic.InstanceFactorySelection.SCENE) {
            instanceFactoryState.setValueTransactionlessly(ide.getInstanceFactoryForScene());
          } else {
            instanceFactoryState.setValueTransactionlessly(ide.getInstanceFactoryForSceneField(field));
          }
        }
      }
      fieldManager.setSelectedFieldOnManipulator(field);
      this.selectionIsFromMain = false;
    }

    try {
      SwingUtilities.invokeLater(uiRefresher);
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  private boolean isSelectableType(AbstractType<?, ?, ?> valueType) {
    return StorytellingSceneEditorLogic.isSelectableType(valueType);
  }

  public boolean isVrActive() {
    return isVrScene;
  }

  void setIsVrActive(boolean isActive) {
    isVrScene = isActive;
    mainCameraViewTracker.setIsVrActive(isActive);

    // Update camera marker move buttons
    MoveActiveCameraToMarkerActionOperation.getInstance().updateDisplay();
    MoveMarkerToActiveCameraActionOperation.getInstance().updateDisplay();
  }

  void setCameras() {
    SymmetricPerspectiveCamera mainCamera = sceneCameraImp.getSgCamera();
    SymmetricPerspectiveCamera layoutCamera = layoutCameraImp.getSgCamera();
    onscreenRenderTarget.setLetterboxed(layoutCamera, false);
    edu.cmu.cs.dennisc.scenegraph.OrthographicCamera orthographicCamera = orthographicCameraImp.getSgCamera();
    globalDragAdapter.addCameraView(org.alice.interact.DragAdapter.CameraView.MAIN, mainCamera, layoutCamera, orthographicCamera);
    globalDragAdapter.makeCameraActive(mainCamera);
    mainCameraViewTracker.setCameras(mainCamera, layoutCamera, orthographicCamera);
    snapGrid.addCamera(mainCamera);
    snapGrid.addCamera(layoutCamera);
    snapGrid.addCamera(orthographicCamera);
    snapGrid.setCurrentCamera(mainCamera);
  }

  @Override
  protected void handleExpandContractChange(boolean isExpanded) {
    //todo
    synchronized (this.getTreeLock()) {
      this.mainCameraNavigatorWidget.setExpanded(isExpanded);
      if (this.runButton != null) {
        this.lookingGlassPanel.setNorthEastComponent(this.runButton);
      }
      StorytellingSceneEditorLogic.ExpandContractPlan plan = StorytellingSceneEditorLogic.createExpandContractPlan(isExpanded);
      if (plan.showSelectionPanel) {
        this.lookingGlassPanel.setNorthWestComponent(this.instanceFactorySelectionPanel);
        this.lookingGlassPanel.setSouthEastComponent(this.contractButton);
        this.lookingGlassPanel.setSouthComponent(this.mainCameraNavigatorWidget);
        if (plan.restoreSavedSelection && (this.savedSceneEditorViewSelection != null)) {
          this.mainCameraMarkerList.setValueTransactionlessly(this.savedSceneEditorViewSelection);
        }
      } else {
        this.lookingGlassPanel.setNorthWestComponent(null);
        this.lookingGlassPanel.setSouthEastComponent(this.expandButton);
        this.lookingGlassPanel.setSouthComponent(null);

        this.savedSceneEditorViewSelection = this.mainCameraMarkerList.getValue();
        this.mainCameraMarkerList.setValueTransactionlessly(plan.forcedSelection);
      }
      this.mainCameraViewSelector.setVisible(isExpanded);
    }
  }

  SceneObjectPropertyManagerPanel getPropertyPanel() {
    return SideComposite.getInstance().getObjectPropertiesTab().getView();
  }

  void handleCameraMarkerFieldSelection(UserField cameraMarkerField) {
    fieldManager.handleCameraMarkerFieldSelection(cameraMarkerField);
  }

  void handleObjectMarkerFieldSelection(UserField objectMarkerField) {
    fieldManager.handleObjectMarkerFieldSelection(objectMarkerField);
  }

  public void setSelectedObjectMarker(UserField objectMarkerField) {
    fieldManager.setSelectedObjectMarker(objectMarkerField);
  }

  void handleMainCameraViewSelection() {
    fieldManager.handleMainCameraViewSelection();
  }

  public void switchToOrthographicCamera() {
    fieldManager.switchToOrthographicCamera();
  }

  public void switchToPerspectiveCamera(AbstractCamera sgCamera) {
    fieldManager.switchToPerspectiveCamera(sgCamera);
  }

  @Override
  protected void initializeComponents() {
    if (this.isInitialized) {
      return;
    }
    this.initializer.initialize();
    this.isInitialized = true;
  }

  @Override
  public void addField(UserType<?> declaringType, UserField field, int index, Statement... statements) {
    super.addField(declaringType, field, index, statements);
    String objectClassName = field.getValueType() != null ? field.getValueType().getName() : null;
    int fieldCountAfter = declaringType.getDeclaredFields().size();
    EatmeSceneObjectAddedEvidence.recordSceneObjectAdded(objectClassName, fieldCountAfter);
    lifecycleManager.handleAddField(field);
  }

  @Override
  protected void setActiveScene(UserField sceneField) {
    super.setActiveScene(sceneField);
    lifecycleManager.activateScene(sceneField);
  }

  @Override
  public void enableRendering(ReasonToDisableSomeAmountOfRendering reasonToDisableSomeAmountOfRendering) {
    fieldManager.enableRendering(reasonToDisableSomeAmountOfRendering);
  }

  @Override
  public void disableRendering(ReasonToDisableSomeAmountOfRendering reasonToDisableSomeAmountOfRendering) {
    fieldManager.disableRendering(reasonToDisableSomeAmountOfRendering);
  }

  @Override
  public void preScreenCapture() {
    fieldManager.preScreenCapture();
  }

  @Override
  public void postScreenCapture() {
    fieldManager.postScreenCapture();
  }

  @Override
  public void setFieldToState(UserField field, Statement... statements) {
    EntityImp fieldImp = getImplementation(field);
    AffineMatrix4x4 originalTransform = fieldImp.getAbsoluteTransformation();
    super.setFieldToState(field, statements);
    if (StorytellingSceneEditorLogic.shouldRestoreSceneCameraTransform(fieldImp == movableSceneCameraImp, mainCameraMarkerList.getValue())) {
      movableSceneCameraImp.setTransformation(movableSceneCameraImp.getScene(), originalTransform);
    }
  }

  @Override
  public Statement getCurrentStateCodeForField(UserField field) {
    return fieldManager.getCurrentStateCodeForField(field);
  }

  @Override
  public void generateCodeForSetUp(StatementListProperty bodyStatementsProperty) {
    fieldManager.generateCodeForSetUp(bodyStatementsProperty);
  }

  @Override
  public Statement[] getDoStatementsForCopyField(UserField fieldToCopy, UserField newField, AffineMatrix4x4 initialTransform) {
    return fieldManager.getDoStatementsForCopyField(fieldToCopy, newField, initialTransform);
  }

  @Override
  public Statement[] getDoStatementsForAddField(UserField field, AffineMatrix4x4 initialTransform) {
    return fieldManager.getDoStatementsForAddField(field, initialTransform);
  }

  @Override
  public Statement[] getUndoStatementsForAddField(UserField field) {
    return fieldManager.getUndoStatementsForAddField(field);
  }

  public Map<AbstractField, Statement> getRiders(UserField vehicle) {
    return fieldManager.getRiders(vehicle);
  }

  @Override
  public Statement[] getDoStatementsForRemoveField(UserField field, Map<AbstractField, Statement> riders) {
    return fieldManager.getDoStatementsForRemoveField(field, riders);
  }

  @Override
  public Statement[] getUndoStatementsForRemoveField(UserField field, Map<AbstractField, Statement> riders) {
    return fieldManager.getUndoStatementsForRemoveField(field, riders);
  }

  @Override
  protected void handleProjectOpened(Project nextProject) {
    lifecycleManager.prepareForProject(nextProject);
    super.handleProjectOpened(nextProject);
  }

  public void handleShowing() {
    fieldManager.handleShowing();
  }

  public void handleHiding() {
    fieldManager.handleHiding();
  }

  public void setHandleVisibilityForObject(TransformableImp imp, boolean b) {
    fieldManager.setHandleVisibilityForObject(imp, b);
  }

  public AffineMatrix4x4 getTransformForNewCameraMarker() {
    return fieldManager.getTransformForNewCameraMarker();
  }

  public AffineMatrix4x4 getTransformForNewObjectMarker() {
    return fieldManager.getTransformForNewObjectMarker();
  }

  public Color getColorForNewObjectMarker() {
    return fieldManager.getColorForNewObjectMarker();
  }

  public Color getColorForNewCameraMarker() {
    return fieldManager.getColorForNewCameraMarker();
  }

  public AffineMatrix4x4 getGoodPointOfViewInSceneForObject(AxisAlignedBox box) {
    return fieldManager.getGoodPointOfViewInSceneForObject(box);
  }

  public MarkerImp getMarkerForField(UserField field) {
    return fieldManager.getMarkerForField(field);
  }

  public AbstractCamera getSgCameraForCreatingThumbnails() {
    return fieldManager.getSgCameraForCreatingThumbnails();
  }

  public void setShowSnapGrid(boolean showSnapGrid) {
    if (this.snapGrid != null) {
      this.snapGrid.setShowing(showSnapGrid);
    }
  }

  public void setSnapGridSpacing(double gridSpacing) {
    if (this.snapGrid != null) {
      this.snapGrid.setSpacing(gridSpacing);
    }
  }

  public OnscreenRenderTarget getOnscreenRenderTarget() {
    return this.onscreenRenderTarget;
  }

  // Package-private forwarding methods for delegates that need access to protected parent methods
  SProgram getProgramInstanceInJavaForDelegate() {
    return getProgramInstanceInJava();
  }

  void setInitialCodeStateForFieldForDelegate(UserField field, Statement code) {
    setInitialCodeStateForField(field, code);
  }
}
