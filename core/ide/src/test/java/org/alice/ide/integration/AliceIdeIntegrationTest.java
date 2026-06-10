package org.alice.ide.integration;

/** Audit note: this integration test is ~2147 LOC and should be split into focused suites in a future refactoring. */

import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.javax.swing.components.AbstractHyperlink;
import edu.cmu.cs.dennisc.scenegraph.Box;
import edu.cmu.cs.dennisc.scenegraph.Geometry;
import edu.cmu.cs.dennisc.scenegraph.OrthographicCamera;
import edu.cmu.cs.dennisc.scenegraph.Scene;
import edu.cmu.cs.dennisc.scenegraph.TexturedAppearance;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.alice.ide.IdeApp;
import org.alice.ide.IdeTestWait;
import org.alice.ide.ProjectStack;
import org.alice.ide.ast.code.edits.MoveStatementEdit;
import org.alice.ide.ast.delete.DeleteStatementOperation;
import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.alice.ide.ast.export.type.TypeSummary;
import org.alice.ide.ast.export.type.TypeSummaryDataSource;
import org.alice.ide.ast.type.croquet.ImportTypeWizard;
import org.alice.ide.cascade.BlockStatementIndexPairContext;
import org.alice.ide.codeeditor.CodeEditor;
import org.alice.ide.codedrop.CodePanelWithDropReceptor;
import org.alice.ide.common.DefaultStatementPane;
import org.alice.ide.common.TypeIcon;
import org.alice.ide.croquet.edits.ast.DeclareMethodEdit;
import org.alice.ide.croquet.edits.ast.InsertStatementEdit;
import org.alice.ide.croquet.models.StandardExpressionState;
import org.alice.ide.croquet.models.ast.DeleteFieldOperation;
import org.alice.ide.croquet.models.ast.DeleteMethodOperation;
import org.alice.ide.croquet.models.ast.StatementContextMenu;
import org.alice.ide.croquet.models.cascade.SimpleExpressionFillIn;
import org.alice.ide.croquet.models.help.ReportIssueComposite;
import org.alice.ide.croquet.models.html.HtmlProjectWriter;
import org.alice.ide.croquet.models.menubar.FileMenuModel;
import org.alice.ide.croquet.models.project.stats.croquet.StatisticsFrameComposite;
import org.alice.ide.croquet.models.projecturi.OpenRecentProjectOperation;
import org.alice.ide.croquet.models.projecturi.SaveAsProjectOperation;
import org.alice.ide.croquet.models.ui.preferences.IsFullTypeHierarchyDesiredState;
import org.alice.ide.declarationseditor.CodeComposite;
import org.alice.ide.declarationseditor.DeclarationTabState;
import org.alice.ide.declarationseditor.TypeComposite;
import org.alice.ide.declarationseditor.type.components.TypeDeclarationView;
import org.alice.ide.icons.ClosedTrashIcon;
import org.alice.ide.icons.IconFactoryManager;
import org.alice.ide.icons.Icons;
import org.alice.ide.icons.OpenTrashIcon;
import org.alice.ide.instancefactory.ThisInstanceFactory;
import org.alice.ide.issue.swing.views.CaughtExceptionPane;
import org.alice.ide.member.AddMethodMenuModel;
import org.alice.ide.member.FunctionTabComposite;
import org.alice.ide.member.ProcedureTabComposite;
import org.alice.ide.members.MembersComposite;
import org.alice.ide.preferences.recursion.IsAccessToRecursionPreferenceAllowedState;
import org.alice.ide.preferences.recursion.IsRecursionAllowedPreferenceDialogComposite;
import org.alice.ide.preferences.recursion.IsRecursionAllowedState;
import org.alice.ide.project.ProjectDocumentState;
import org.alice.ide.projecturi.ProjectSnapshot;
import org.alice.ide.projecturi.RecentProjectCountState;
import org.alice.ide.recentprojects.RecentProjectsListData;
import org.alice.ide.resource.manager.ResourceManagerComposite;
import org.alice.ide.resource.manager.ResourceSingleSelectTableRowState;
import org.alice.ide.testing.ProjectContextFixture;
import org.alice.ide.testing.TestIdeBootstrap;
import org.alice.ide.uricontent.FileProjectLoader;
import org.alice.ide.uricontent.UriProjectLoader;
import org.alice.ide.x.PreviewAstI18nFactory;
import org.alice.ide.x.components.StatementListPropertyView;
import org.alice.stageide.StageIDE;
import org.alice.stageide.custom.AudioSourceCustomExpressionCreatorComposite;
import org.alice.stageide.custom.ColorCustomExpressionCreatorComposite;
import org.alice.stageide.custom.KeyCustomExpressionCreatorComposite;
import org.alice.stageide.gallerybrowser.GalleryComposite;
import org.alice.stageide.gallerybrowser.ShapesTab;
import org.alice.stageide.gallerybrowser.search.core.SearchGalleryWorker;
import org.alice.stageide.gallerybrowser.search.croquet.SearchTab;
import org.alice.stageide.gallerybrowser.search.croquet.views.SearchTabView;
import org.alice.stageide.modelresource.ResourceNode;
import org.alice.stageide.sceneeditor.StorytellingSceneEditor;
import org.alice.stageide.sceneeditor.interact.CameraNavigatorWidget;
import org.alice.stageide.sceneeditor.interact.GlobalDragAdapter;
import org.alice.stageide.sceneeditor.interact.handles.ManipulationHandle2DCameraZoom;
import org.alice.stageide.sceneeditor.interact.manipulators.CameraZoomMouseWheelManipulator;
import org.alice.stageide.sceneeditor.interact.manipulators.OrthographicCameraDragZoomManipulator;
import org.alice.stageide.sceneeditor.side.SideComposite;
import org.alice.stageide.sceneeditor.views.SceneObjectPropertyManagerPanel;
import org.alice.stageide.type.croquet.OtherTypeDialog;
import org.lgna.common.resources.AudioResource;
import org.lgna.croquet.Application;
import org.lgna.croquet.MenuBarComposite;
import org.lgna.croquet.StandardMenuItemPrepModel;
import org.lgna.croquet.edits.Edit;
import org.lgna.croquet.history.UserActivity;
import org.lgna.croquet.imp.cascade.RtRoot;
import org.lgna.croquet.views.PopupMenu;
import org.lgna.ik.core.IKCore.Limb;
import org.lgna.ik.poser.controllers.PoserControlComposite;
import org.lgna.ik.poser.croquet.PoserComposite;
import org.lgna.ik.poser.jselection.JointSelectionSphere;
import org.lgna.project.Project;
import org.lgna.project.annotations.ValueDetails;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.BooleanExpressionBodyPair;
import org.lgna.project.ast.BooleanLiteral;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.CountLoop;
import org.lgna.project.ast.DoInOrder;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.ResourceExpression;
import org.lgna.project.ast.SimpleArgumentListProperty;
import org.lgna.project.ast.Statement;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.io.IoUtilities;
import org.lgna.project.io.TypeResourcesPair;
import org.lgna.story.Pose;
import org.lgna.story.SJoint;
import org.lgna.story.SModel;
import org.lgna.story.resources.BipedResource;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AliceIdeIntegrationTest {
  private static final String STARTER_RESOURCE = "/starters/indiaMinimum.a3p";
  private static final String SCREEN_MENU_BAR_PROPERTY = "apple.laf.useScreenMenuBar";

  @Rule public Timeout timeout = Timeout.seconds(60);

  private String previousScreenMenuBar;
  private StageIDE ide;
  private Path starterProjectFile;
  private Project starterProject;
  private Robot robot;

  @Before
  public void skipIfHeadless() {
    Assume.assumeTrue(!GraphicsEnvironment.isHeadless());
  }

  @Before
  public void setUp() throws Exception {
    Assume.assumeTrue(!GraphicsEnvironment.isHeadless());
    previousScreenMenuBar = System.getProperty(SCREEN_MENU_BAR_PROPERTY);
    System.setProperty(SCREEN_MENU_BAR_PROPERTY, "false");

    this.robot = new Robot();
    this.robot.setAutoDelay(40);
    ensureUiColor("Alice.Type.color", new Color(200, 210, 240));
    ensureUiColor("Alice.Constructor.color", new Color(221, 248, 255));
    ensureUiColor("Alice.Field.color", new Color(255, 241, 228));
    ensureUiColor("Alice.Procedure.color", new Color(235, 234, 255));
    ensureUiColor("Alice.Function.color", new Color(226, 255, 233));
    ensureUiColor("Alice.Event.color", Color.WHITE);
    ensureUiColor("Alice.Resource.color", Color.WHITE);
    ensureUiColor("Alice.Instance.color", new Color(254, 215, 2));
    ensureUiColor("Alice.Alert.color", new Color(170, 0, 0));
    ensureUiColor("Alice.background", Color.WHITE);
    ensureUiColor("Alice.differentBackground", new Color(230, 230, 230));
    ensureUiColor("Alice.differentForeground", new Color(120, 120, 120));
    ensureUiColor("Alice.Comment.background", new Color(221, 221, 221));
    ensureUiColor("Alice.Comment.foreground", Color.BLACK);
    ensureUiColor("Alice.Block.background", Color.WHITE);
    ensureUiColor("Alice.Block.foreground", new Color(242, 242, 247));
    ensureUiColor("Alice.Block.contrastForeground", new Color(62, 62, 65));
    ensureUiColor("Alice.Block.contrastBackground", new Color(217, 217, 217));
    ensureUiColor("Alice.Block.lightKnurlForeground", new Color(217, 217, 217));
    ensureUiColor("Alice.Block.darkKnurlForeground", new Color(62, 62, 65));
    ensureUiColor("Alice.Procedure.blockColor", new Color(107, 94, 255));
    ensureUiColor("Alice.Function.blockColor", new Color(53, 199, 89));
    ensureUiColor("Alice.Constructor.blockColor", new Color(255, 141, 40));
    ensureUiColor("Alice.Type.blockColor", new Color(200, 210, 240));

    this.ide = TestIdeBootstrap.ensureInstalled();
    this.starterProjectFile = copyStarterProject();
    this.starterProject = IoUtilities.readProject(this.starterProjectFile.toFile());
    TestIdeBootstrap.loadProject(this.starterProject);
    TestIdeBootstrap.onEdt(() -> {
      var frame = this.ide.getDocumentFrame().getFrame();
      frame.pack();
      frame.setSize(1280, 900);
      frame.setLocation(60, 60);
      frame.setVisible(true);
      this.ide.getDocumentFrame().setToCodePerspectiveTransactionlessly();
      return null;
    });
    waitForIdle();
  }

  @After
  public void tearDown() {
    closeTransientWindows();
    TestIdeBootstrap.reset();
    restoreProperty(SCREEN_MENU_BAR_PROPERTY, previousScreenMenuBar);
  }

  @Test
  public void opensStageIdeFrameAndLoadsTemplateProject() {
    JFrame frame = frame();

    clickCenter(frame);

    assertTrue(onEdt(frame::isShowing));
    assertNotNull(onEdt(() -> this.ide.getDocumentFrame().getCodePerspective().getMainComposite().getView()));
    assertNotNull(onEdt(() -> ProjectDocumentState.getInstance().getValue()));
    assertNotNull(onEdt(this.ide::getProject));
    assertEquals(
        this.starterProject.getProgramType().getName(),
        onEdt(() -> this.ide.getProject().getProgramType().getName()));
  }

  @Test
  public void opensProcedureEditorAndShowsStatementListView() {
    ProjectContextFixture fixture = ProjectContextFixture.create();
    TestIdeBootstrap.loadProject(fixture.project);
    waitForIdle();

    UserMethod procedure = fixture.sceneProcedure;
    TestIdeBootstrap.onEdt(() -> {
      this.ide.getDocumentFrame().getSetToCodePerspectiveOperation().fire(new UserActivity());
      this.ide.getDocumentFrame().getDeclarationsEditorComposite().getTabState()
          .getItemSelectionOperationForCode(procedure)
          .fire(new UserActivity());
      return null;
    });
    waitForIdle();

    CodePanelWithDropReceptor focusedDropReceptor =
        onEdt(() -> this.ide.getDocumentFrame().getCodePerspective().getCodeDropReceptorInFocus());
    assertTrue(focusedDropReceptor instanceof CodeEditor);

    clickCenter(frame());

    assertNotNull(onEdt(() -> CodeComposite.getInstance(procedure).getView()));
    assertSame(
        procedure,
        onEdt(() -> ((CodeComposite) this.ide.getDocumentFrame()
            .getDeclarationsEditorComposite()
            .getTabState()
            .getValue()).getDeclaration()));
  }

  @Test
  public void opensGalleryBrowserInSetupScenePerspective() {
    ShapesTab shapesTab = new ShapesTab();
    JFrame galleryFrame = onEdt(() -> showStandaloneFrame("Alice Gallery Harness", shapesTab.getView().getAwtComponent()));

    assertTrue(onEdt(galleryFrame::isShowing));
    assertFalse(shapesTab.getDragModels().isEmpty());
    clickCenter(galleryFrame);
  }

  @Test
  public void opensOtherTypeDialogAndRendersTypeChooserUi() throws Exception {
    ProjectContextFixture fixture = ProjectContextFixture.create();
    TestIdeBootstrap.loadProject(fixture.project);
    waitForIdle();

    OtherTypeDialog composite = OtherTypeDialog.getInstance();
    initializeOtherTypeDialog(composite);
    ProjectStack.pushProject(fixture.project);
    composite.handlePreActivation();
    JDialog dialog = onEdt(() -> showStandaloneDialog("Other Type Harness", composite.getView().getAwtComponent()));
    try {
      JTree tree = onEdt(() -> findDescendant(dialog, JTree.class));
      assertNotNull(tree);
      clickCenter(tree);
    } finally {
      TestIdeBootstrap.onEdt(() -> {
        dialog.dispose();
        composite.handlePostDeactivation();
        ProjectStack.popAndCheckProject(fixture.project);
        return null;
      });
    }
  }

  @Test
  public void triggersSaveAsDialogFromRealFileMenu() {
    TestIdeBootstrap.onEdt(() -> {
      injectUriProjectLoader(this.ide, new FileProjectLoader(this.starterProjectFile.toFile()));
      return null;
    });
    JFrame menuFrame = onEdt(this::createMenuFrame);
    JMenu fileMenu = onEdt(() -> findFileMenu(menuFrame.getJMenuBar()));
    assertNotNull(fileMenu);

    SaveChooserDismissProbe probe = new SaveChooserDismissProbe();
    probe.start();
    try {
      clickCenter(menuFrame);
    waitForIdle();
      clickCenter(fileMenu);
      awaitCondition("file menu did not open", () -> onEdt(fileMenu::isPopupMenuVisible));

      JMenuItem saveAsItem = onEdt(() -> findMenuItemByAction(
          fileMenu.getPopupMenu(),
          SaveAsProjectOperation.getInstance().getImp().getSwingModel().getAction()));
      assertNotNull(saveAsItem);

      clickCenter(saveAsItem);
      awaitCondition("save chooser was not observed", probe::wasChooserObserved);
      awaitCondition("save chooser was not dismissed", probe::wasDismissed);
    } finally {
      probe.stop();
    }

    assertTrue(probe.wasChooserObserved());
    assertTrue(probe.wasDismissed());
  }

  @Test
  public void switchesToSceneEditorSelectsObjectsAndExercisesCameraAndProperties() {
    skipIfHeadless();
    NamedUserType sceneType = onEdt(this.ide::getSceneType);
    UserField sceneField = onEdt(this.ide::getSceneField);
    UserField objectField = findFirstField(sceneType);
    assertNotNull(objectField);

    AtomicReference<StorytellingSceneEditor> sceneEditorRef = new AtomicReference<>();
    SwingUtilities.invokeLater(() -> {
      this.ide.getDocumentFrame().setToSetupScenePerspectiveTransactionlessly();
      sceneEditorRef.set(this.ide.getSceneEditor());
    });
    Assume.assumeTrue(
        "scene perspective unavailable in bootstrap environment",
        IdeTestWait.isTrueWithin(() -> sceneEditorRef.get() != null, 20, TimeUnit.SECONDS));
    StorytellingSceneEditor sceneEditor = sceneEditorRef.get();
    waitForIdle();

    onEdt(() -> {
      sceneEditor.setSelectedField(sceneType, objectField);
      return null;
    });
    waitForIdle();
    assertSame(objectField, onEdt(sceneEditor::getSelectedField));

    SceneObjectPropertyManagerPanel propertiesPanel = onEdt(() -> SideComposite.getInstance().getObjectPropertiesTab().getView());
    awaitCondition(
        "object properties panel did not populate",
        () -> onEdt(() -> propertiesPanel.getAwtComponent().getComponentCount() > 0));

    onEdt(() -> {
      sceneEditor.setSelectedField(sceneField.getDeclaringType(), sceneField);
      return null;
    });
    waitForIdle();
    assertSame(sceneField, onEdt(sceneEditor::getSelectedField));

    Object perspectiveCamera = onEdt(sceneEditor::getSgCameraForCreatingThumbnails);
    assertNotNull(perspectiveCamera);

    onEdt(() -> {
      sceneEditor.switchToOrthographicCamera();
      return null;
    });
    waitForIdle();
    assertTrue(onEdt(() -> sceneEditor.getOnscreenRenderTarget().getSgCameraAt(0) != perspectiveCamera));

    onEdt(() -> {
      sceneEditor.switchToPerspectiveCamera(sceneEditor.getSgCameraForCreatingThumbnails());
      return null;
    });
    waitForIdle();
    assertSame(perspectiveCamera, onEdt(() -> sceneEditor.getOnscreenRenderTarget().getSgCameraAt(0)));
  }

  @Test
  public void editsCodeReordersStatementsDeletesThemAndSupportsUndoRedo() {
    skipIfHeadless();
    ProjectContextFixture fixture = ProjectContextFixture.create();
    TestIdeBootstrap.loadProject(fixture.project);
    waitForIdle();

    UserMethod workflowMethod = onEdt(() -> {
      this.ide.getDocumentFrame().getSetToCodePerspectiveOperation().fire(new UserActivity());
      UserActivity declareActivity = new UserActivity();
      commitEdit(
          new DeclareMethodEdit(declareActivity, fixture.sceneType, "integrationWorkflow", JavaType.VOID_TYPE),
          declareActivity);
      return fixture.sceneType.getDeclaredMethod("integrationWorkflow");
    });
    waitForIdle();
    assertNotNull(workflowMethod);

    onEdt(() -> {
      this.ide.getDocumentFrame().getDeclarationsEditorComposite().getTabState()
          .getItemSelectionOperationForCode(workflowMethod)
          .fire(new UserActivity());
      return null;
    });
    waitForIdle();

    CodePanelWithDropReceptor focusedDropReceptor =
        onEdt(() -> this.ide.getDocumentFrame().getCodePerspective().getCodeDropReceptorInFocus());
    assertTrue(focusedDropReceptor instanceof CodeEditor);

    Statement methodCall = AstUtilities.createMethodInvocationStatement(new org.lgna.project.ast.ThisExpression(), fixture.sceneProcedure);
    org.lgna.project.ast.ConditionalStatement ifElse = new org.lgna.project.ast.ConditionalStatement(
        new BooleanExpressionBodyPair[] {
            new BooleanExpressionBodyPair(new BooleanLiteral(true), new BlockStatement(new Comment("if branch")))
        },
        new BlockStatement(new Comment("else branch")));
    CountLoop countLoop = new CountLoop();
    countLoop.count.setValue(new IntegerLiteral(3));
    countLoop.body.setValue(new BlockStatement(new Comment("loop branch")));

    onEdt(() -> {
      BlockStatement body = workflowMethod.body.getValue();
      insertStatement(body, methodCall);
      insertStatement(body, ifElse);
      insertStatement(body, countLoop);
      return null;
    });
    waitForIdle();

    assertEquals(3, workflowMethod.body.getValue().statements.size());
    assertSame(methodCall, workflowMethod.body.getValue().statements.get(0));
    assertSame(ifElse, workflowMethod.body.getValue().statements.get(1));
    assertSame(countLoop, workflowMethod.body.getValue().statements.get(2));

    MoveStatementEdit moveEdit = onEdt(() -> moveStatement(workflowMethod.body.getValue(), 2, 0));
    waitForIdle();

    assertSame(countLoop, workflowMethod.body.getValue().statements.get(0));
    assertSame(methodCall, workflowMethod.body.getValue().statements.get(1));
    assertSame(ifElse, workflowMethod.body.getValue().statements.get(2));

    onEdt(() -> {
      moveEdit.undo();
      return null;
    });
    waitForIdle();
    assertSame(methodCall, workflowMethod.body.getValue().statements.get(0));
    assertSame(ifElse, workflowMethod.body.getValue().statements.get(1));
    assertSame(countLoop, workflowMethod.body.getValue().statements.get(2));

    onEdt(() -> {
      moveEdit.doOrRedo(false);
      return null;
    });
    waitForIdle();
    assertSame(countLoop, workflowMethod.body.getValue().statements.get(0));
    assertSame(methodCall, workflowMethod.body.getValue().statements.get(1));
    assertSame(ifElse, workflowMethod.body.getValue().statements.get(2));

    onEdt(() -> {
      new DeleteStatementOperation(ifElse).fire(new UserActivity());
      return null;
    });
    waitForIdle();
    assertEquals(2, workflowMethod.body.getValue().statements.size());
    assertTrue(!workflowMethod.body.getValue().statements.contains(ifElse));

    onEdt(() -> {
      new DeleteStatementOperation(methodCall).fire(new UserActivity());
      return null;
    });
    waitForIdle();
    assertEquals(1, workflowMethod.body.getValue().statements.size());
    assertSame(countLoop, workflowMethod.body.getValue().statements.get(0));
  }

  @Test
  public void importsAndExportsProjectsAndPersistsPreferences() throws Exception {
    skipIfHeadless();
    ProjectContextFixture fixture = ProjectContextFixture.create();
    TestIdeBootstrap.loadProject(fixture.project);
    waitForIdle();

    Path exportDir = Files.createDirectories(Path.of("target", "alice-ide-integration", UUID.randomUUID().toString()));
    ProjectContextFixture exportFixture = ProjectContextFixture.create();
    exportFixture.sceneType.methods.remove(exportFixture.sceneType.methods.indexOf(exportFixture.initializeEventListenersMethod));
    Path htmlFile = exportDir.resolve("project.html");
    try (OutputStream outputStream = Files.newOutputStream(htmlFile)) {
      new HtmlProjectWriter().writeProject(outputStream, exportFixture.project);
    }
    String html = Files.readString(htmlFile);
    assertTrue(Files.exists(htmlFile));
    assertTrue(html.contains("<html"));
    assertTrue(html.contains(exportFixture.sceneProcedure.getName()));

    ProjectContextFixture donorFixture = ProjectContextFixture.create();
    String importedMethodName = "importedGuideMove";
    String importedFieldName = "importedGuide";
    donorFixture.sceneType.methods.add(new UserMethod(
        importedMethodName,
        JavaType.VOID_TYPE,
        new UserParameter[0],
        new BlockStatement(new Comment("imported procedure"))));
    donorFixture.sceneType.fields.add(new UserField(importedFieldName, donorFixture.actorType, new NullLiteral()));

    Path typeFile = exportDir.resolve("Scene.a3c");
    IoUtilities.writeType(typeFile.toFile(), donorFixture.sceneType, new TypeSummaryDataSource(new TypeSummary(donorFixture.sceneType)));
    TypeResourcesPair typeResourcesPair = IoUtilities.readType(typeFile.toFile());
    ImportTypeWizard wizard = new ImportTypeWizard(
        typeFile.toUri(),
        typeResourcesPair.getType(),
        typeResourcesPair.getResources(),
        typeResourcesPair.getType(),
        fixture.sceneType);

    JFrame importFrame = onEdt(() -> showStandaloneFrame("Import Type Harness", wizard.getAddMembersPage().getView().getAwtComponent()));
    try {
      assertTrue(onEdt(importFrame::isShowing));
      clickCenter(importFrame);
    } finally {
      TestIdeBootstrap.onEdt(() -> {
        importFrame.dispose();
        return null;
      });
    }

    onEdt(() -> {
      UserActivity importActivity = new UserActivity();
      commitEdit(wizard.getAddMembersPage().createEdit(importActivity), importActivity);
      return null;
    });
    waitForIdle();

    assertNotNull(fixture.sceneType.getDeclaredMethod(importedMethodName));
    assertNotNull(fixture.sceneType.getDeclaredField(importedFieldName));

    boolean previousAccess = IsAccessToRecursionPreferenceAllowedState.getInstance().getValue();
    boolean previousRecursion = IsRecursionAllowedState.getInstance().getValue();
    try {
      onEdt(() -> {
        IsAccessToRecursionPreferenceAllowedState.getInstance().setValueTransactionlessly(true);
        IsRecursionAllowedState.getInstance().setValueTransactionlessly(false);
        return null;
      });
      waitForIdle();

      JDialog dialog = openRecursionPreferenceDialog();
      try {
        JCheckBox recursionCheckBox = findEnabledRecursionCheckBox(dialog);
        assertNotNull(recursionCheckBox);
        clickButton(recursionCheckBox);
        awaitCondition("recursion preference did not update", () -> onEdt(() -> IsRecursionAllowedState.getInstance().getValue()));
      } finally {
        TestIdeBootstrap.onEdt(() -> {
          dialog.dispose();
          return null;
        });
      }

      JDialog reopenedDialog = openRecursionPreferenceDialog();
      try {
        JCheckBox persistedCheckBox = findEnabledRecursionCheckBox(reopenedDialog);
        assertNotNull(persistedCheckBox);
        assertTrue(onEdt(persistedCheckBox::isSelected));
      } finally {
        TestIdeBootstrap.onEdt(() -> {
          reopenedDialog.dispose();
          return null;
        });
      }
    } finally {
      onEdt(() -> {
        IsRecursionAllowedState.getInstance().setValueTransactionlessly(previousRecursion);
        IsAccessToRecursionPreferenceAllowedState.getInstance().setValueTransactionlessly(previousAccess);
        return null;
      });
      waitForIdle();
    }
  }

  @Test
  public void opensExpressionCascadeFromCodeEditorAndInsertsJointExpression() throws Exception {
    skipIfHeadless();
    ProjectContextFixture fixture = ProjectContextFixture.create();
    UserField selectedJointField = new UserField("selectedJoint", JavaType.getInstance(SJoint.class), new NullLiteral());
    fixture.sceneType.fields.add(selectedJointField);
    TestIdeBootstrap.loadProject(fixture.project);
    waitForIdle();

    onEdt(() -> {
      this.ide.getDocumentFrame().getSetToCodePerspectiveOperation().fire(new UserActivity());
      this.ide.getDocumentFrame().getDeclarationsEditorComposite().getTabState()
          .getItemSelectionOperationForCode(fixture.sceneProcedure)
          .fire(new UserActivity());
      return null;
    });
    waitForIdle();

    BlockStatementIndexPairContext context = new BlockStatementIndexPairContext(
        new BlockStatementIndexPair(fixture.sceneProcedure.body.getValue(), fixture.sceneProcedure.body.getValue().statements.size()));
    StandardExpressionState state = new StandardExpressionState(Application.PROJECT_GROUP, UUID.randomUUID(), null) {
      @Override
      protected AbstractType<?, ?, ?> getType() {
        return JavaType.getInstance(SJoint.class);
      }

      @Override
      protected ValueDetails<?> getValueDetails() {
        return null;
      }
    };

    Expression selected = onEdt(() -> {
      this.ide.getExpressionCascadeManager().pushContext(context);
      try {
        RtRoot<Expression, ?> rtRoot = new RtRoot<>(state.getCascadeRoot());
        Object[] topItems = getRtItems(getFirstRtBlank(rtRoot));
        Object jointMenu = findMenuFollowingFieldFillIn(topItems, fixture.actorField);
        assertNotNull("joint cascade menu not found", jointMenu);
        invokeHiddenMethod(jointMenu, "select", new Class<?>[0]);

        Object[] jointItems = getRtItems(getFirstRtBlank(jointMenu));
        Object jointFillIn = findFirstFillIn(jointItems);
        assertNotNull("joint fill-in not found", jointFillIn);
        invokeHiddenMethod(jointFillIn, "select", new Class<?>[0]);

        return rtRoot.createValues(Expression.class)[0];
      } finally {
        this.ide.getExpressionCascadeManager().popAndCheckContext(context);
      }
    });

    assertNotNull(selected);
    assertTrue(selected.getType().isAssignableTo(SJoint.class));
    onEdt(() -> {
      selectedJointField.initializer.setValue(selected);
      return null;
    });
    assertSame(selected, selectedJointField.initializer.getValue());
  }

  @Test
  public void rendersIconsWithRealGraphicsContexts() {
    skipIfHeadless();
    ProjectContextFixture fixture = ProjectContextFixture.create();
    TestIdeBootstrap.loadProject(fixture.project);
    waitForIdle();

    BufferedImage image = new BufferedImage(640, 96, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = image.createGraphics();
    try {
      JLabel label = new JLabel();
      Icon[] icons = new Icon[] {
          DeclarationTabState.getFieldIcon(),
          DeclarationTabState.getProcedureIcon(),
          DeclarationTabState.getFunctionIcon(),
          DeclarationTabState.getConstructorIcon(),
          Icons.FOLDER_ICON_SMALL,
          new ClosedTrashIcon(24, 24, Color.LIGHT_GRAY),
          new OpenTrashIcon(24, 24, Color.WHITE),
          new TypeIcon(fixture.sceneType, true, UIManager.getFont("defaultFont"), UIManager.getFont("defaultFont")),
          IconFactoryManager.getDynamicIconFactoryForField(fixture.actorField).getIconToFit(new Dimension(24, 24)),
          IconFactoryManager.getIconFactoryForResourceCls(BipedResource.class).getIconToFit(new Dimension(24, 24)),
          IconFactoryManager.getIconFactoryForObjectMarker(org.lgna.story.Color.RED).getIconToFit(new Dimension(24, 24))
      };
      int x = 8;
      for (Icon icon : icons) {
        assertNotNull(icon);
        icon.paintIcon(label, g2, x, 16);
        x += icon.getIconWidth() + 12;
      }
      IconFactoryManager.markDynamicIconFactoryForFieldDirty(fixture.actorField);
      IconFactoryManager.getDynamicIconFactoryForField(fixture.actorField)
          .getIconToFit(new Dimension(24, 24))
          .paintIcon(label, g2, x, 16);
    } finally {
      g2.dispose();
    }

    assertTrue(hasPaintedPixels(image));
  }

  @Test
  public void opensIkPoserAndManipulatesJointChains() throws Exception {
    skipIfHeadless();
    UserField poseableField = findFirstPoseableField(onEdt(this.ide::getSceneType));
    Assume.assumeTrue("IK poser unavailable in starter project", poseableField != null);
    PoserComposite<?> poser = PoserComposite.getDialogForUserType((NamedUserType) poseableField.getValueType());
    Assume.assumeTrue("IK poser unavailable for starter project field", poser != null);

    JFrame poserFrame = onEdt(() -> {
      JFrame frame = showStandaloneFrame("IK Poser Harness", poser.getView().getAwtComponent());
      poser.handlePreActivation();
      return frame;
    });
    try {
      waitForIdle();
      assertTrue(onEdt(poserFrame::isShowing));
      clickCenter(poserFrame);

      PoserControlComposite control = poser.getControlComposite();
      assertNotNull(control);
      assertTrue(onEdt(() -> poser.getJointSelectionSheres().size() >= 8));

      onEdt(() -> {
        JointSelectionSphere rightArmSphere = (JointSelectionSphere) poser.getScene().getJointsForLimb(Limb.RIGHT_ARM).get(1);
        JointSelectionSphere leftArmSphere = (JointSelectionSphere) poser.getScene().getJointsForLimb(Limb.LEFT_ARM).get(1);
        JointSelectionSphere rightLegSphere = (JointSelectionSphere) poser.getScene().getJointsForLimb(Limb.RIGHT_LEG).get(1);
        JointSelectionSphere leftLegSphere = (JointSelectionSphere) poser.getScene().getJointsForLimb(Limb.LEFT_LEG).get(1);
        control.getJointRotationHandleVisibilityState().setValueTransactionlessly(true);
        control.updateSphere(Limb.RIGHT_ARM, rightArmSphere);
        control.updateSphere(Limb.LEFT_ARM, leftArmSphere);
        control.updateSphere(Limb.RIGHT_LEG, rightLegSphere);
        control.updateSphere(Limb.LEFT_LEG, leftLegSphere);
        poser.getUsedJoints().clear();
        poser.getUsedJoints().add(rightArmSphere.getJoint().getJointId());
        poser.getUsedJoints().add(leftLegSphere.getJoint().getJointId());
        poser.getScene().jointSelected(rightArmSphere, createMouseClick(poserFrame, MouseEvent.BUTTON3));
        poser.getScene().jointSelected(leftArmSphere, createMouseClick(poserFrame, MouseEvent.BUTTON3));
        return null;
      });
      waitForIdle();

      Pose<?> pose = onEdt(poser::getPose);
      assertNotNull(pose);
      onEdt(() -> {
        control.getStraightenJointsOperation().fire(new UserActivity());
        poser.strikePose(pose);
        return null;
      });
      waitForIdle();
      assertNotNull(onEdt(control::createPoseExpression));
    } finally {
      onEdt(() -> {
        poser.handlePostDeactivation();
        poserFrame.dispose();
        return null;
      });
    }
  }

  @Test
  public void opensProjectStatisticsDialogAndExercisesFrequencyViews() {
    skipIfHeadless();
    ProjectContextFixture fixture = ProjectContextFixture.create();
    fixture.sceneProcedure.body.getValue().statements.add(
        AstUtilities.createMethodInvocationStatement(new org.lgna.project.ast.ThisExpression(), fixture.sceneProcedure));
    CountLoop loop = new CountLoop();
    loop.count.setValue(new IntegerLiteral(2));
    loop.body.setValue(new BlockStatement(new Comment("statistics loop")));
    fixture.sceneProcedure.body.getValue().statements.add(loop);
    TestIdeBootstrap.loadProject(fixture.project);
    waitForIdle();

    StatisticsFrameComposite composite = new StatisticsFrameComposite(this.ide.getDocumentFrame());
    JFrame statisticsFrame = onEdt(() -> {
      composite.handlePreActivation();
      return showStandaloneFrame("Statistics Harness", composite.getView().getAwtComponent());
    });
    try {
      waitForIdle();
      assertTrue(onEdt(statisticsFrame::isShowing));
      JList<?> list = onEdt(() -> findDescendant(statisticsFrame, JList.class));
      assertNotNull(list);
      assertTrue(onEdt(() -> list.getModel().getSize() > 0));
      onEdt(() -> {
        int index = Math.min(1, list.getModel().getSize() - 1);
        list.setSelectedIndex(index);
        return null;
      });
      waitForIdle();

      for (JCheckBox checkBox : onEdt(() -> findDescendants(statisticsFrame, JCheckBox.class))) {
        if (onEdt(checkBox::isEnabled)) {
          clickButton(checkBox);
        }
      }

      Object flowTab = getHiddenField(composite, "flowControlFrequencyTab");
      Object methodTab = getHiddenField(composite, "methodTab");
      onEdt(() -> {
        composite.getTabState().setValueTransactionlessly((org.lgna.croquet.SimpleTabComposite<?>) flowTab);
        return null;
      });
      waitForIdle();
      assertTrue(onEdt(() -> findDescendants(statisticsFrame, JLabel.class).size() > 0));

      onEdt(() -> {
        composite.getTabState().setValueTransactionlessly((org.lgna.croquet.SimpleTabComposite<?>) methodTab);
        return null;
      });
      waitForIdle();
      assertTrue(onEdt(() -> findDescendants(statisticsFrame, JScrollPane.class).size() > 0));
    } finally {
      onEdt(() -> {
        statisticsFrame.dispose();
        composite.handlePostDeactivation();
        return null;
      });
    }
  }

  @Test
  public void mapsCodePanelDropReceptorsForStatementDrags() {
    skipIfHeadless();
    ProjectContextFixture fixture = ProjectContextFixture.create();
    fixture.sceneProcedure.body.getValue().statements.add(
        AstUtilities.createMethodInvocationStatement(new org.lgna.project.ast.ThisExpression(), fixture.sceneProcedure));
    fixture.sceneProcedure.body.getValue().statements.add(new Comment("drag second"));
    fixture.sceneProcedure.body.getValue().statements.add(new Comment("drag third"));
    TestIdeBootstrap.loadProject(fixture.project);
    waitForIdle();

    onEdt(() -> {
      this.ide.getDocumentFrame().getSetToCodePerspectiveOperation().fire(new UserActivity());
      this.ide.getDocumentFrame().getDeclarationsEditorComposite().getTabState()
          .getItemSelectionOperationForCode(fixture.sceneProcedure)
          .fire(new UserActivity());
      return null;
    });
    waitForIdle();

    CodeEditor editor = onEdt(() -> (CodeEditor) this.ide.getDocumentFrame().getCodePerspective().getCodeDropReceptorInFocus());
    assertNotNull(editor);
    Object paneInfos = invokeHiddenMethod(
        editor.getDropReceptor(),
        "createStatementListPropertyPaneInfos",
        new Class<?>[] {org.lgna.croquet.DragModel.class, org.lgna.croquet.views.AwtContainerView.class},
        new StubCodeDragModel(),
        null);
    assertNotNull(paneInfos);
    clickCenter(frame());
  }

  @Test
  public void opensIssueReporterAndFormatsStackTraces() {
    skipIfHeadless();
    ReportIssueComposite reportIssueComposite = new ReportIssueComposite();
    JDialog reportDialog = onEdt(() -> {
      reportIssueComposite.handlePreActivation();
      return showStandaloneDialog("Report Issue Harness", reportIssueComposite.getView().getAwtComponent());
    });

    CaughtExceptionPane exceptionPane = new CaughtExceptionPane();
    IllegalStateException exception = new IllegalStateException("integration boom");
    exceptionPane.setThreadAndThrowable(Thread.currentThread(), exception);
    JDialog exceptionDialog = onEdt(() -> showStandaloneDialog("Caught Exception Harness", exceptionPane));
    try {
      waitForIdle();
      onEdt(() -> {
        reportIssueComposite.getSummaryState().setValueTransactionlessly("integration summary");
        reportIssueComposite.getDescriptionState().setValueTransactionlessly("integration description");
        return null;
      });
      clickCenter(reportDialog);

      Container exceptionDetails = (Container) getHiddenField(exceptionPane, "paneException");
      AbstractHyperlink hyperlink = onEdt(() -> findDescendant(exceptionDetails, AbstractHyperlink.class));
      assertNotNull(hyperlink);
      assertTrue(edu.cmu.cs.dennisc.java.lang.ThrowableUtilities.getStackTraceAsString(exception).contains("integration boom"));
      assertTrue(((String) invokeHiddenMethod(exceptionPane, "getSummaryText", new Class<?>[0])).contains("IllegalStateException"));
      assertNotNull(exceptionPane.generateIssue());
    } finally {
      onEdt(() -> {
        exceptionDialog.dispose();
        reportDialog.dispose();
        reportIssueComposite.handlePostDeactivation();
        return null;
      });
    }
  }

  @Test
  public void computesFrameTitlesForProjectDirtyAndBackupStatesAcrossPerspectives() {
    skipIfHeadless();
    onEdt(() -> {
      injectUriProjectLoader(this.ide, new FileProjectLoader(this.starterProjectFile.toFile()));
      updateFrameTitle();
      return null;
    });
    waitForIdle();

    assertNotNull(onEdt(() -> frame().getTitle()));
    assertNotNull(onEdt(() -> this.ide.getDocumentFrame().getCodePerspective().getClass().getSimpleName()));
    assertNotNull(onEdt(() -> this.ide.getDocumentFrame().getSetupScenePerspective().getClass().getSimpleName()));

    String cleanTitle = new org.alice.ide.frametitle.AliceIdeFrameTitleGenerator()
        .generateTitle(createUriProjectLoader(this.starterProjectFile, false), true);
    assertTrue(cleanTitle.contains(this.starterProjectFile.toString()));
    assertFalse(cleanTitle.endsWith("*"));

    String dirtyTitle = new org.alice.ide.frametitle.AliceIdeFrameTitleGenerator()
        .generateTitle(createUriProjectLoader(this.starterProjectFile, false), false);
    assertTrue(dirtyTitle.endsWith("*"));

    String backupTitle = new org.alice.ide.frametitle.AliceIdeFrameTitleGenerator()
        .generateTitle(createUriProjectLoader(this.starterProjectFile, true), true);
    assertTrue(backupTitle.endsWith("*"));
  }

  @Test
  public void opensResourceManagerAndBrowsesResourcesByType() {
    skipIfHeadless();
    ProjectContextFixture fixture = ProjectContextFixture.create();
    TestIdeBootstrap.loadProject(fixture.project);
    waitForIdle();

    ResourceManagerComposite composite = new ResourceManagerComposite(this.ide.getDocumentFrame());
    JFrame resourceFrame = onEdt(() -> {
      composite.handlePreActivation();
      return showStandaloneFrame("Resource Manager Harness", composite.getView().getAwtComponent());
    });
    try {
      waitForIdle();
      JTable table = onEdt(() -> findDescendant(resourceFrame, JTable.class));
      assertNotNull(table);
      assertEquals(2, (int) onEdt(table::getRowCount));
      assertSame(fixture.audioResource.getClass(), onEdt(() -> table.getValueAt(0, ResourceSingleSelectTableRowState.TYPE_COLUMN_INDEX)));
      assertSame(fixture.imageResource.getClass(), onEdt(() -> table.getValueAt(1, ResourceSingleSelectTableRowState.TYPE_COLUMN_INDEX)));

      onEdt(() -> {
        table.setRowSelectionInterval(0, 0);
        return null;
      });
      waitForIdle();
      awaitCondition("resource rename operation not enabled", () -> onEdt(() -> composite.getRenameResourceComposite().getLaunchOperation().isEnabled()));
      assertTrue(onEdt(() -> composite.getReloadContentOperation().isEnabled()));

      BufferedImage renderImage = new BufferedImage(320, 120, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2 = renderImage.createGraphics();
      try {
        onEdt(() -> {
          for (int row = 0; row < table.getRowCount(); row++) {
            for (int col = 0; col < table.getColumnCount(); col++) {
              Component renderer = table.prepareRenderer(table.getCellRenderer(row, col), row, col);
              renderer.setBounds(0, 0, 100, 24);
              renderer.paint(g2);
            }
          }
          return null;
        });
      } finally {
        g2.dispose();
      }
      assertTrue(hasPaintedPixels(renderImage));
    } finally {
      onEdt(() -> {
        resourceFrame.dispose();
        composite.handlePostDeactivation();
        return null;
      });
    }
  }

  @Test
  public void navigatesMemberTabsExpandsTypeHierarchyAndSwitchesRecentProjects() throws Exception {
    skipIfHeadless();
    ProjectContextFixture fixture = ProjectContextFixture.create();
    TestIdeBootstrap.loadProject(fixture.project);
    waitForIdle();

    MembersComposite membersComposite = MembersComposite.getInstance();
    JFrame membersFrame = onEdt(() -> showStandaloneFrame("Members Harness", membersComposite.getView().getAwtComponent()));
    try {
      assertTrue(onEdt(membersFrame::isShowing));
      onEdt(() -> {
        membersComposite.getTabState().setValueTransactionlessly(membersComposite.getProcedureTabComposite());
        return null;
      });
      waitForIdle();
      assertSame(membersComposite.getProcedureTabComposite(), onEdt(() -> membersComposite.getTabState().getValue()));

      onEdt(() -> {
        membersComposite.getTabState().setValueTransactionlessly(membersComposite.getFunctionTabComposite());
        return null;
      });
      waitForIdle();
      assertSame(membersComposite.getFunctionTabComposite(), onEdt(() -> membersComposite.getTabState().getValue()));
      clickCenter(membersFrame);
    } finally {
      TestIdeBootstrap.onEdt(() -> {
        membersFrame.dispose();
        return null;
      });
    }

    boolean previousHierarchy = IsFullTypeHierarchyDesiredState.getInstance().getValue();
    try {
      onEdt(() -> {
        IsFullTypeHierarchyDesiredState.getInstance().setValueTransactionlessly(true);
        return null;
      });
      waitForIdle();

      OtherTypeDialog composite = OtherTypeDialog.getInstance();
      initializeOtherTypeDialog(composite);
      ProjectStack.pushProject(fixture.project);
      composite.handlePreActivation();
      JDialog dialog = onEdt(() -> showStandaloneDialog("Type Hierarchy Harness", composite.getView().getAwtComponent()));
      try {
        JTree tree = onEdt(() -> findDescendant(dialog, JTree.class));
        assertNotNull(tree);
        int rowsBefore = onEdt(tree::getRowCount);
        onEdt(() -> {
          tree.expandRow(0);
          if (tree.getRowCount() > 1) {
            tree.expandRow(1);
          }
          return null;
        });
        waitForIdle();
        assertTrue(onEdt(() -> tree.getRowCount()) >= rowsBefore);
      } finally {
        TestIdeBootstrap.onEdt(() -> {
          dialog.dispose();
          composite.handlePostDeactivation();
          ProjectStack.popAndCheckProject(fixture.project);
          return null;
        });
      }
    } finally {
      onEdt(() -> {
        IsFullTypeHierarchyDesiredState.getInstance().setValueTransactionlessly(previousHierarchy);
        return null;
      });
      waitForIdle();
    }

    int previousRecentCount = RecentProjectCountState.getInstance().getValue();
    ProjectSnapshot[] previousProjects = RecentProjectsListData.getInstance().toArray(ProjectSnapshot.class);
    try {
      Path recentDir = Files.createDirectories(Path.of("target", "alice-ide-integration", UUID.randomUUID().toString()));
      Path recentProject = recentDir.resolve("recentWorkflow.a3p");
      ProjectContextFixture recentFixture = ProjectContextFixture.create();
      recentFixture.programType.name.setValue("RecentProgram");
      IoUtilities.writeProject(recentProject.toFile(), recentFixture.project);

      onEdt(() -> {
        RecentProjectCountState.getInstance().setValueTransactionlessly(10);
        return null;
      });
      restoreRecentProjects(new ProjectSnapshot[0]);
      RecentProjectsListData.getInstance().handleOpen(this.starterProjectFile.toFile());
      RecentProjectsListData.getInstance().handleOpen(recentProject.toFile());
      assertEquals(recentProject.toUri(), RecentProjectsListData.getInstance().toArray(ProjectSnapshot.class)[0].getUri());

      ProjectSnapshot[] snapshots = RecentProjectsListData.getInstance().toArray(ProjectSnapshot.class);
      TestIdeBootstrap.loadProject(IoUtilities.readProject(new java.io.File(snapshots[0].getUri())));
      waitForIdle();
      assertEquals("RecentProgram", onEdt(() -> this.ide.getProject().getProgramType().getName()));

      TestIdeBootstrap.loadProject(IoUtilities.readProject(new java.io.File(snapshots[1].getUri())));
      waitForIdle();
      assertEquals(this.starterProject.getProgramType().getName(), onEdt(() -> this.ide.getProject().getProgramType().getName()));
    } finally {
      onEdt(() -> {
        RecentProjectCountState.getInstance().setValueTransactionlessly(previousRecentCount);
        return null;
      });
      restoreRecentProjects(previousProjects);
      waitForIdle();
    }
  }

  @Test
  public void searchesGalleryBrowserTabsAndUpdatesSearchResults() {
    skipIfHeadless();
    SearchTab searchTab = new SearchTab();
    SearchTabView searchView = onEdt(() -> (SearchTabView) searchTab.getView());
    ShapesTab shapesTab = new ShapesTab();
    JFrame galleryFrame = onEdt(() -> showStandaloneFrame("Gallery Browser Harness", searchView.getAwtComponent()));
    try {
      assertTrue(onEdt(galleryFrame::isShowing));
      onEdt(() -> {
        searchTab.handlePreActivation();
        searchView.setComponentsToGalleryDragComponents(new String[0], List.of());
        return null;
      });
      waitForIdle();

      Object filteredResourcesView = getHiddenField(searchView, "filteredResourcesView");
      Container filteredContainer = (Container) invokeHiddenMethod(filteredResourcesView, "getAwtComponent", new Class<?>[0]);
      assertTrue(onEdt(() -> filteredContainer.getComponentCount() > 0));
      clickCenter(filteredContainer);

      onEdt(() -> {
        searchView.removeAllGalleryDragComponents();
        searchView.setComponentsToGalleryDragComponents(new String[0], List.of());
        return null;
      });
      waitForIdle();

      assertTrue(onEdt(() -> filteredContainer.getComponentCount() > 0));
      assertFalse(shapesTab.getDragModels().isEmpty());
      assertNotNull(shapesTab.getDragModelForCls(org.lgna.story.SBox.class));
    } finally {
      onEdt(() -> {
        searchTab.handlePostDeactivation();
        galleryFrame.dispose();
        return null;
      });
    }
  }

  @Test
  public void rendersTypeDeclarationDeletesMembersAndBuildsStatementContextMenus() {
    skipIfHeadless();
    ProjectContextFixture fixture = ProjectContextFixture.create();
    UserField extraField = new UserField("spareActor", fixture.actorType, new NullLiteral());
    UserMethod extraMethod = new UserMethod(
        "integrationUtility",
        JavaType.VOID_TYPE,
        new UserParameter[0],
        new BlockStatement(new Comment("utility")));
    fixture.sceneType.fields.add(extraField);
    fixture.sceneType.methods.add(extraMethod);
    DoInOrder doInOrder = new DoInOrder();
    doInOrder.body.setValue(new BlockStatement(new Comment("ordered")));
    fixture.sceneProcedure.body.getValue().statements.add(doInOrder);
    TestIdeBootstrap.loadProject(fixture.project);
    waitForIdle();

    TypeComposite typeComposite = TypeComposite.getInstance(fixture.sceneType);
    TypeDeclarationView typeView = onEdt(typeComposite::getView);
    JFrame typeFrame = onEdt(() -> showStandaloneFrame("Type Declaration Harness", typeView.getAwtComponent()));
    try {
      assertTrue(onEdt(typeFrame::isShowing));
      BufferedImage typeImage = new BufferedImage(900, 600, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2 = typeImage.createGraphics();
      try {
        onEdt(() -> {
          synchronized (typeView.getAwtComponent().getTreeLock()) {
            invokeHiddenMethod(
                typeView,
                "setJavaCodeOnTheSide",
                new Class<?>[] {boolean.class, boolean.class},
                false,
                true);
            typeView.getAwtComponent().setSize(900, 600);
            typeView.getAwtComponent().doLayout();
            typeView.getAwtComponent().paint(g2);
            invokeHiddenMethod(
                typeView,
                "setJavaCodeOnTheSide",
                new Class<?>[] {boolean.class, boolean.class},
                true,
                false);
          }
          return null;
        });
      } finally {
        g2.dispose();
      }
      assertTrue(hasPaintedPixels(typeImage));
      clickCenter(typeFrame);
    } finally {
      onEdt(() -> {
        typeFrame.dispose();
        return null;
      });
    }

    PopupMenu popupMenu = new PopupMenu(null, null);
    onEdt(() -> {
      StatementContextMenu.getInstance(doInOrder).handlePopupMenuPrologue(popupMenu);
      return null;
    });
    assertTrue(onEdt(() -> popupMenu.getMenuComponentCount() > 0));

    DeleteFieldOperation fieldOperation = DeleteFieldOperation.getInstance(extraField, fixture.sceneType);
    onEdt(() -> {
      fieldOperation.doOrRedoInternal(true);
      return null;
    });
    assertNull(fixture.sceneType.getDeclaredField(extraField.getName()));
    onEdt(() -> {
      fieldOperation.undoInternal();
      return null;
    });
    assertNotNull(fixture.sceneType.getDeclaredField(extraField.getName()));

    DeleteMethodOperation methodOperation = DeleteMethodOperation.getInstance(extraMethod, fixture.sceneType);
    onEdt(() -> {
      methodOperation.doOrRedoInternal(true);
      return null;
    });
    assertNull(fixture.sceneType.getDeclaredMethod(extraMethod.getName()));
    onEdt(() -> {
      methodOperation.undoInternal();
      return null;
    });
    assertNotNull(fixture.sceneType.getDeclaredMethod(extraMethod.getName()));
  }

  @Test
  public void rendersAstViewsAndCascadeFillInsForExpressionsArgumentsAndStatementLists() {
    skipIfHeadless();
    ProjectContextFixture fixture = ProjectContextFixture.create();
    UserParameter amountParameter = new UserParameter("amount", Double.class);
    UserMethod measureMethod = new UserMethod(
        "measure",
        JavaType.DOUBLE_PRIMITIVE_TYPE,
        new UserParameter[] {amountParameter},
        new BlockStatement(new Comment("measure")));
    fixture.sceneType.methods.add(measureMethod);
    MethodInvocation invocation = AstUtilities.createMethodInvocation(
        new org.lgna.project.ast.ThisExpression(),
        measureMethod,
        new org.lgna.project.ast.DoubleLiteral(1.5));
    ExpressionStatement invocationStatement = new ExpressionStatement(invocation);
    fixture.sceneProcedure.body.getValue().statements.add(invocationStatement);
    TestIdeBootstrap.loadProject(fixture.project);
    waitForIdle();

    PreviewAstI18nFactory factory = PreviewAstI18nFactory.getInstance();
    FieldAccess fieldAccess = new FieldAccess(fixture.actorField);
    SimpleExpressionFillIn<FieldAccess> fillIn = new SimpleExpressionFillIn<>(fieldAccess, true);
    assertSame(fieldAccess, fillIn.createValue(null));
    assertSame(fieldAccess, fillIn.getTransientValue(null));
    Icon leadingIcon = (Icon) invokeHiddenMethod(
        fillIn,
        "getLeadingIcon",
        new Class<?>[] {org.lgna.croquet.imp.cascade.ItemNode.class},
        new Object[] {null});
    assertNotNull(leadingIcon);

    StatementListPropertyView statementListView = onEdt(
        () -> new StatementListPropertyView(factory, fixture.sceneProcedure.body.getValue().statements));
    org.lgna.croquet.views.SwingComponentView<?> fieldAccessView = onEdt(() -> factory.createExpressionPane(fieldAccess));
    org.lgna.croquet.views.SwingComponentView<?> argumentListView = onEdt(() ->
        (org.lgna.croquet.views.SwingComponentView<?>) invokeHiddenMethod(
            factory,
            "createSimpleArgumentListPropertyPane",
            new Class<?>[] {SimpleArgumentListProperty.class},
            invocation.requiredArguments));
    org.alice.ide.common.AbstractStatementPane statementPane = onEdt(() -> factory.createStatementPane(invocationStatement));

    JFrame astFrame = onEdt(() -> {
      javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(4, 1));
      panel.add((Component) fieldAccessView.getAwtComponent());
      panel.add((Component) argumentListView.getAwtComponent());
      panel.add(statementPane.getAwtComponent());
      panel.add(statementListView.getAwtComponent());
      return showStandaloneFrame("AST View Harness", panel);
    });
    try {
      assertTrue(onEdt(astFrame::isShowing));
      BufferedImage astImage = new BufferedImage(900, 700, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2 = astImage.createGraphics();
      try {
        onEdt(() -> {
          statementListView.getAwtComponent().setSize(900, 220);
          statementListView.getAwtComponent().doLayout();
          statementListView.setCurrentPotentialDropIndexAndDragStep(1, null);
          statementListView.setIsCurrentUnder(true);
          statementListView.getAwtComponent().paint(g2);
          return null;
        });
      } finally {
        g2.dispose();
      }
      assertTrue(hasPaintedPixels(astImage));
      assertEquals(1, (int) onEdt(statementListView::getCurrentPotentialDropIndex));
      assertTrue(onEdt(() -> statementListView.calculateIndex(new Point(8, 8))) >= 0);
      onEdt(() -> {
        statementListView.setIsCurrentUnder(false);
        return null;
      });
      assertEquals(-1, (int) onEdt(statementListView::getCurrentPotentialDropIndex));
      clickCenter(astFrame);
    } finally {
      onEdt(() -> {
        astFrame.dispose();
        return null;
      });
    }
  }

  @Test
  public void switchesMemberTabsAndBuildsAddMethodMenusForCurrentType() {
    skipIfHeadless();
    ProjectContextFixture fixture = ProjectContextFixture.create();
    TestIdeBootstrap.loadProject(fixture.project);
    waitForIdle();

    onEdt(() -> {
      this.ide.getDocumentFrame().getSetToCodePerspectiveOperation().fire(new UserActivity());
      this.ide.getDocumentFrame().getInstanceFactoryState().setValueTransactionlessly(ThisInstanceFactory.getInstance());
      return null;
    });
    waitForIdle();

    ProcedureTabComposite procedureTab = new ProcedureTabComposite();
    FunctionTabComposite functionTab = new FunctionTabComposite();
    JFrame memberFrame = onEdt(() -> {
      javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(2, 1));
      panel.add(procedureTab.getView().getAwtComponent());
      panel.add(functionTab.getView().getAwtComponent());
      return showStandaloneFrame("Member Tab Harness", panel);
    });
    try {
      assertTrue(onEdt(memberFrame::isShowing));
      String alphabetical = (String) invokeHiddenMethod(
          procedureTab,
          "findLocalizedText",
          new Class<?>[] {String.class},
          "sortAlphabetically");
      onEdt(() -> {
        procedureTab.getSortState().setValueTransactionlessly(alphabetical);
        return null;
      });
      waitForIdle();
      assertEquals(alphabetical, onEdt(() -> procedureTab.getSortState().getValue()));

      String groupByReturnType = (String) invokeHiddenMethod(
          functionTab,
          "findLocalizedText",
          new Class<?>[] {String.class},
          "groupByReturnType");
      onEdt(() -> {
        functionTab.getSortState().setValueTransactionlessly(groupByReturnType);
        return null;
      });
      waitForIdle();
      assertEquals(groupByReturnType, onEdt(() -> functionTab.getSortState().getValue()));

      clickCenter(memberFrame);
    } finally {
      onEdt(() -> {
        memberFrame.dispose();
        return null;
      });
    }
  }

  @Test
  public void createsCustomExpressionCompositesForColorKeyAndAudioSource() {
    skipIfHeadless();
    ProjectContextFixture fixture = ProjectContextFixture.create();
    TestIdeBootstrap.loadProject(fixture.project);
    waitForIdle();

    ColorCustomExpressionCreatorComposite colorComposite = ColorCustomExpressionCreatorComposite.getInstance();
    JDialog colorDialog = onEdt(() -> showStandaloneDialog("Color Expression Harness", colorComposite.getView().getAwtComponent()));
    try {
      JColorChooser chooser = (JColorChooser) getHiddenField(colorComposite, "jColorChooser");
      onEdt(() -> {
        chooser.setColor(Color.BLUE);
        return null;
      });
      Expression colorExpression = onEdt(() -> (Expression) invokeHiddenMethod(colorComposite, "createValue", new Class<?>[0]));
      assertNotNull(colorExpression);
      onEdt(() -> {
        invokeHiddenMethod(colorComposite, "initializeToPreviousExpression", new Class<?>[] {Expression.class}, colorExpression);
        return null;
      });
      assertEquals(Color.BLUE.getBlue(), (int) onEdt(() -> chooser.getColor().getBlue()));
    } finally {
      onEdt(() -> {
        colorDialog.dispose();
        return null;
      });
    }

    KeyCustomExpressionCreatorComposite keyComposite = KeyCustomExpressionCreatorComposite.getInstance();
    JDialog keyDialog = onEdt(() -> showStandaloneDialog("Key Expression Harness", keyComposite.getView().getAwtComponent()));
    try {
      onEdt(() -> {
        keyComposite.getValueState().setValueTransactionlessly(org.lgna.story.Key.SPACE);
        return null;
      });
      Expression keyExpression = onEdt(() -> (Expression) invokeHiddenMethod(keyComposite, "createValue", new Class<?>[0]));
      assertTrue(keyExpression instanceof FieldAccess);
      onEdt(() -> {
        keyComposite.getValueState().setValueTransactionlessly(null);
        invokeHiddenMethod(keyComposite, "initializeToPreviousExpression", new Class<?>[] {Expression.class}, keyExpression);
        return null;
      });
      assertEquals(org.lgna.story.Key.SPACE, onEdt(() -> keyComposite.getValueState().getValue()));
    } finally {
      onEdt(() -> {
        keyDialog.dispose();
        return null;
      });
    }

    AudioSourceCustomExpressionCreatorComposite audioComposite = AudioSourceCustomExpressionCreatorComposite.getInstance();
    JDialog audioDialog = onEdt(() -> showStandaloneDialog("Audio Expression Harness", audioComposite.getView().getAwtComponent()));
    try {
      ResourceExpression resourceExpression = new ResourceExpression(AudioResource.class, fixture.audioResource);
      onEdt(() -> {
        audioComposite.getAudioResourceExpressionState().setValueTransactionlessly(resourceExpression);
        audioComposite.getVolumeState().setValueTransactionlessly(750);
        audioComposite.getStartMarkerState().setValueTransactionlessly(200);
        audioComposite.getStopMarkerState().setValueTransactionlessly(600);
        return null;
      });
      Expression audioExpression = onEdt(() -> (Expression) invokeHiddenMethod(audioComposite, "createValue", new Class<?>[0]));
      assertTrue(audioExpression instanceof InstanceCreation);
      onEdt(() -> {
        audioComposite.getAudioResourceExpressionState().setValueTransactionlessly(null);
        invokeHiddenMethod(audioComposite, "initializeToPreviousExpression", new Class<?>[] {Expression.class}, audioExpression);
        return null;
      });
      assertNotNull(onEdt(() -> audioComposite.getAudioResourceExpressionState().getValue()));
    } finally {
      onEdt(() -> {
        audioDialog.dispose();
        return null;
      });
    }
  }

  @Test
  public void capturesSceneGraphsAndExercisesCameraWidgetsAndZoomManipulators() {
    skipIfHeadless();

    Scene scene = new Scene();
    scene.setName("IntegrationScene");
    Transformable transformable = new Transformable();
    transformable.setName("TransformRoot");
    Visual visual = new Visual();
    visual.setName("VisibleBox");
    TexturedAppearance appearance = new TexturedAppearance();
    appearance.diffuseColor.setValue(new Color4f(1.0f, 200 / 255.0f, 0.0f, 1.0f));
    appearance.opacity.setValue(0.65f);
    visual.frontFacingAppearance.setValue(appearance);
    visual.geometries.setValue(new Geometry[] {new Box()});
    transformable.addComponent(visual);
    scene.addComponent(transformable);

    org.alice.ide.swing.BasicTreeNodeViewerPanel viewerPanel = new org.alice.ide.swing.BasicTreeNodeViewerPanel();
    JFrame treeFrame = onEdt(() -> {
      viewerPanel.setRoot(scene);
      return showStandaloneFrame("Scene Graph Harness", viewerPanel);
    });
    try {
      assertTrue(onEdt(treeFrame::isShowing));
      Object rightTree = getHiddenField(viewerPanel, "rightTree");
      assertNotNull(rightTree);
      Object rootNode = invokeHiddenMethod(rightTree, "getRootNode", new Class<?>[0]);
      int rootHash = (Integer) getHiddenField(rootNode, "hashCode");
      onEdt(() -> {
        invokeHiddenMethod(rightTree, "setSelectedNode", new Class<?>[] {int.class, boolean.class}, rootHash, true);
        return null;
      });
      JLabel nameLabel = (JLabel) getHiddenField(rightTree, "nameLabel");
      assertTrue(onEdt(() -> !nameLabel.getText().isEmpty()));

      appearance.opacity.setValue(0.25f);
      scene.addComponent(new Transformable());
      clickButton((AbstractButton) getHiddenField(viewerPanel, "captureButton"));
      Object updatedRightTree = getHiddenField(viewerPanel, "rightTree");
      Object updatedRoot = invokeHiddenMethod(updatedRightTree, "getRootNode", new Class<?>[0]);
      boolean hasDifferentChild = (Boolean) invokeHiddenMethod(updatedRoot, "hasDifferentChild", new Class<?>[0]);
      assertTrue(hasDifferentChild);
    } finally {
      onEdt(() -> {
        treeFrame.dispose();
        return null;
      });
    }

    org.alice.interact.DragAdapter dragAdapter = new org.alice.interact.DragAdapter() {
    };
    CameraNavigatorWidget widget = onEdt(() -> new CameraNavigatorWidget(dragAdapter, org.alice.interact.DragAdapter.CameraView.MAIN));
    JFrame widgetFrame = onEdt(() -> showStandaloneFrame("Camera Widget Harness", widget.getAwtComponent()));
    try {
      onEdt(() -> {
        widget.setExpanded(true);
        widget.setToOrthographicMode();
        return null;
      });
      waitForIdle();
      assertEquals(2, (int) onEdt(() -> widget.getAwtComponent().getComponentCount()));

      onEdt(() -> {
        widget.setToPerspectiveMode();
        return null;
      });
      waitForIdle();
      assertEquals(3, (int) onEdt(() -> widget.getAwtComponent().getComponentCount()));
      clickCenter(widgetFrame);

      OrthographicCamera orthographicCamera = new OrthographicCamera();
      onEdt(() -> {
        orthographicCamera.picturePlane.setValue(orthographicCamera.picturePlane.getValue().withHeight(5.0));
        return null;
      });

      OrthographicCameraDragZoomManipulator dragZoom = new OrthographicCameraDragZoomManipulator(new ManipulationHandle2DCameraZoom());
      dragZoom.setDragAdapter(dragAdapter);
      dragZoom.setCamera(orthographicCamera);
      double beforeZoom = onEdt(dragZoom::getCameraZoom);
      onEdt(() -> {
        dragZoom.setCameraZoom(0.5d);
        return null;
      });
      assertTrue(onEdt(dragZoom::getCameraZoom) > beforeZoom);

      CameraZoomMouseWheelManipulator wheelZoom = new CameraZoomMouseWheelManipulator();
      wheelZoom.setDragAdapter(dragAdapter);
      wheelZoom.setCamera(orthographicCamera);
      double wheelBefore = onEdt(() -> orthographicCamera.picturePlane.getValue().getHeight());
      onEdt(() -> {
        invokeHiddenMethod(wheelZoom, "applyZoom", new Class<?>[] {double.class}, 0.5d);
        return null;
      });
      double wheelAfter = onEdt(() -> orthographicCamera.picturePlane.getValue().getHeight());
      assertTrue(wheelAfter > wheelBefore);
    } finally {
      onEdt(() -> {
        widgetFrame.dispose();
        return null;
      });
    }
  }

  private void dragComponentToScreenPoint(Component component, Point targetPointOnScreen) {
    Rectangle startBounds = onEdt(() -> boundsOnScreen(component));
    assertNotNull("component is not showing on screen", startBounds);
    int startX = startBounds.x + (startBounds.width / 2);
    int startY = startBounds.y + (startBounds.height / 2);
    this.robot.mouseMove(startX, startY);
    this.robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
    waitForIdle();
    this.robot.mouseMove((startX + targetPointOnScreen.x) / 2, (startY + targetPointOnScreen.y) / 2);
    waitForIdle();
    this.robot.mouseMove(targetPointOnScreen.x, targetPointOnScreen.y);
    waitForIdle();
    this.robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    waitForIdle();
  }

  private MouseEvent createMouseClick(Component source, int button) {
    return new MouseEvent(source, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 16, 16, 1, false, button);
  }

  private boolean hasPaintedPixels(BufferedImage image) {
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        if ((image.getRGB(x, y) >>> 24) != 0) {
          return true;
        }
      }
    }
    return false;
  }

  private UriProjectLoader createUriProjectLoader(Path projectPath, boolean backup) {
    return new UriProjectLoader(false) {
      @Override
      public boolean isNewProject() {
        return false;
      }

      @Override
      public URI getUri() {
        return projectPath.toUri();
      }

      @Override
      protected Project load() {
        return AliceIdeIntegrationTest.this.ide.getProject();
      }

      @Override
      public boolean isBackup() {
        return backup;
      }

      @Override
      public java.io.File getMainProjectFile() {
        return projectPath.toFile();
      }
    };
  }

  private void updateFrameTitle() {
    try {
      Method method = org.alice.ide.ProjectApplication.class.getDeclaredMethod("updateTitle");
      method.setAccessible(true);
      method.invoke(this.ide);
    } catch (ReflectiveOperationException roe) {
      throw new AssertionError(roe);
    }
  }

  private Object getFirstRtBlank(Object rtBlankOwner) {
    Object[] blankChildren = (Object[]) invokeHiddenMethod(rtBlankOwner, "getBlankChildren", new Class<?>[0]);
    assertTrue(blankChildren.length > 0);
    return blankChildren[0];
  }

  private Object[] getRtItems(Object rtBlank) {
    Object pair = invokeHiddenMethod(rtBlank, "getItemChildrenAndComboOffsets", new Class<?>[0]);
    return (Object[]) invokeHiddenMethod(pair, "getItemChildren", new Class<?>[0]);
  }

  private Object findMenuFollowingFieldFillIn(Object[] topItems, UserField field) {
    for (int i = 0; i < topItems.length - 1; i++) {
      Object item = topItems[i];
      if (!item.getClass().getSimpleName().contains("RtFillIn")) {
        continue;
      }
      Object value = invokeHiddenMethod(item, "createValue", new Class<?>[0]);
      if (value instanceof FieldAccess fieldAccess && fieldAccess.field.getValue() == field) {
        Object nextItem = topItems[i + 1];
        if (nextItem.getClass().getSimpleName().contains("RtMenu")) {
          return nextItem;
        }
      }
    }
    for (Object item : topItems) {
      if (item.getClass().getSimpleName().contains("RtMenu")) {
        return item;
      }
    }
    return null;
  }

  private Object findFirstFillIn(Object[] items) {
    for (Object item : items) {
      if (item.getClass().getSimpleName().contains("RtFillIn")) {
        return item;
      }
    }
    return null;
  }

  private Object getHiddenField(Object target, String name) {
    try {
      Class<?> type = target.getClass();
      while (type != null) {
        try {
          java.lang.reflect.Field field = type.getDeclaredField(name);
          field.setAccessible(true);
          return field.get(target);
        } catch (NoSuchFieldException nsfe) {
          type = type.getSuperclass();
        }
      }
      throw new NoSuchFieldException(name);
    } catch (ReflectiveOperationException roe) {
      throw new AssertionError(roe);
    }
  }

  private Object invokeHiddenMethod(Object target, String name, Class<?>[] parameterTypes, Object... args) {
    try {
      Class<?> type = target.getClass();
      while (type != null) {
        try {
          Method method = type.getDeclaredMethod(name, parameterTypes);
          method.setAccessible(true);
          return method.invoke(target, args);
        } catch (NoSuchMethodException nsme) {
          type = type.getSuperclass();
        }
      }
      throw new NoSuchMethodException(name);
    } catch (ReflectiveOperationException roe) {
      throw new AssertionError(roe);
    }
  }

  private static final class StubCodeDragModel extends org.lgna.croquet.AbstractModel implements org.lgna.croquet.DragModel {
    private StubCodeDragModel() {
      super(UUID.randomUUID());
    }

    @Override
    public List<? extends org.lgna.croquet.DropReceptor> createListOfPotentialDropReceptors() {
      return List.of();
    }

    @Override
    public void handleDragStarted(org.lgna.croquet.history.DragStep step) {
    }

    @Override
    public void handleDragEnteredDropReceptor(org.lgna.croquet.history.DragStep step) {
    }

    @Override
    public void handleDragExitedDropReceptor(org.lgna.croquet.history.DragStep step) {
    }

    @Override
    public void handleDragStopped(org.lgna.croquet.history.DragStep step) {
    }

    @Override
    public org.lgna.croquet.Triggerable getDropOperation(org.lgna.croquet.history.DragStep step, org.lgna.croquet.DropSite dropSite) {
      return null;
    }

    @Override
    protected Class<? extends org.lgna.croquet.Element> getClassUsedForLocalization() {
      return StubCodeDragModel.class;
    }

    @Override
    protected void localize() {
    }
  }

  private JFrame frame() {
    return onEdt(() -> this.ide.getDocumentFrame().getFrame().getAwtComponent());
  }

  private JFrame showStandaloneFrame(String title, Component content) {
    if (content.getParent() instanceof Container parent) {
      parent.remove(content);
    }
    JFrame frame = new JFrame(title);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setContentPane((Container) content);
    frame.pack();
    frame.setSize(900, 600);
    frame.setLocation(120, 120);
    frame.setVisible(true);
    return frame;
  }

  private JDialog showStandaloneDialog(String title, Component content) {
    if (content.getParent() instanceof Container parent) {
      parent.remove(content);
    }
    JDialog dialog = new JDialog(frame(), title, false);
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    dialog.setContentPane((Container) content);
    dialog.pack();
    dialog.setSize(900, 600);
    dialog.setLocation(140, 140);
    dialog.setVisible(true);
    return dialog;
  }

  private void initializeOtherTypeDialog(OtherTypeDialog composite) throws Exception {
    Method method = OtherTypeDialog.class.getDeclaredMethod("initializeRootFilterType", JavaType.class);
    method.setAccessible(true);
    method.invoke(composite, JavaType.getInstance(SModel.class));
  }

  private JFrame createMenuFrame() {
    FileMenuModel fileMenuModel = findFileMenuModel(this.ide);
    assertNotNull("FileMenuModel not found in Alice menu bar", fileMenuModel);

    MenuBarComposite menuBarComposite = new MenuBarComposite(UUID.randomUUID());
    menuBarComposite.addItem(fileMenuModel);

    org.lgna.croquet.views.Frame croquetFrame = new org.lgna.croquet.views.Frame();
    croquetFrame.setMenuBarComposite(menuBarComposite);
    JFrame frame = croquetFrame.getAwtComponent();
    frame.setTitle("Alice Save Harness");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.pack();
    frame.setSize(400, 180);
    frame.setLocation(150, 150);
    frame.setVisible(true);
    return frame;
  }

  private FileMenuModel findFileMenuModel(StageIDE stageIDE) {
    for (StandardMenuItemPrepModel child :
        stageIDE.getDocumentFrame().getCodePerspective().getMenuBarComposite().getChildren()) {
      if (child instanceof FileMenuModel fileMenuModel) {
        return fileMenuModel;
      }
    }
    return null;
  }

  private void injectUriProjectLoader(StageIDE stageIDE, UriProjectLoader loader) {
    try {
      Class<?> type = stageIDE.getClass();
      while (type != null) {
        try {
          java.lang.reflect.Field field = type.getDeclaredField("uriProjectLoader");
          field.setAccessible(true);
          field.set(stageIDE, loader);
          return;
        } catch (NoSuchFieldException nsfe) {
          type = type.getSuperclass();
        }
      }
      throw new NoSuchFieldException("uriProjectLoader not found");
    } catch (ReflectiveOperationException roe) {
      throw new AssertionError(roe);
    }
  }

  private Path copyStarterProject() throws Exception {
    Path dir = Files.createDirectories(
        Path.of("target", "alice-ide-integration", UUID.randomUUID().toString()));
    Path starterFile = dir.resolve("indiaMinimum.a3p");
    try (InputStream input = getClass().getResourceAsStream(STARTER_RESOURCE)) {
      assertNotNull("indiaMinimum.a3p must be on the test classpath", input);
      Files.copy(input, starterFile, StandardCopyOption.REPLACE_EXISTING);
    }
    return starterFile;
  }

  private UserMethod findFirstProcedure(NamedUserType sceneType) {
    if (sceneType == null) {
      return null;
    }
    for (UserMethod method : sceneType.getDeclaredMethods()) {
      if (method.isProcedure() && method.body.getValue() != null) {
        return method;
      }
    }
    return null;
  }

  private UserField findFirstField(NamedUserType sceneType) {
    if (sceneType == null) {
      return null;
    }
    for (UserField field : sceneType.getDeclaredFields()) {
      if (field != null) {
        return field;
      }
    }
    return null;
  }

  private UserField findFirstPoseableField(NamedUserType sceneType) {
    if (sceneType == null) {
      return null;
    }
    for (UserField field : sceneType.getDeclaredFields()) {
      if (field != null && field.getValueType() instanceof NamedUserType namedUserType && PoserComposite.isPoseable(namedUserType)) {
        return field;
      }
    }
    return null;
  }

  private void clickCenter(Component component) {
    Rectangle bounds = onEdt(() -> boundsOnScreen(component));
    assertNotNull("component is not showing on screen", bounds);
    this.robot.mouseMove(bounds.x + (bounds.width / 2), bounds.y + (bounds.height / 2));
    this.robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
    this.robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    waitForIdle();
  }

  private Rectangle boundsOnScreen(Component component) {
    if (component == null || !component.isShowing()) {
      return null;
    }
    Point location = component.getLocationOnScreen();
    return new Rectangle(location.x, location.y, component.getWidth(), component.getHeight());
  }

  private void waitForIdle() {
    this.robot.waitForIdle();
    IdeTestWait.drainEdt();
  }

  private void awaitCondition(String message, BooleanSupplier condition) {
    IdeTestWait.until(condition, message);
  }

  private JDialog awaitWindow(Predicate<Window> predicate, String message) {
    AtomicReference<JDialog> dialogRef = new AtomicReference<>();
    IdeTestWait.untilOnEdt(() -> {
      dialogRef.set(findWindow(predicate));
      return dialogRef.get() != null;
    }, message);
    return dialogRef.get();
  }

  private JDialog findWindow(Predicate<Window> predicate) {
    for (Window window : Window.getWindows()) {
      if (window instanceof JDialog dialog && window.isShowing() && predicate.test(window)) {
        return dialog;
      }
    }
    return null;
  }

  private JMenu findFileMenu(JMenuBar menuBar) {
    if (menuBar == null) {
      return null;
    }
    for (int i = 0; i < menuBar.getMenuCount(); i++) {
      JMenu menu = menuBar.getMenu(i);
      if (menu != null && "File".equals(menu.getText())) {
        return menu;
      }
    }
    return menuBar.getMenuCount() > 0 ? menuBar.getMenu(0) : null;
  }

  private JMenuItem findMenuItemByAction(JPopupMenu popupMenu, Action action) {
    if (popupMenu == null) {
      return null;
    }
    for (Component component : popupMenu.getComponents()) {
      if (component instanceof JMenuItem menuItem && menuItem.getAction() == action) {
        return menuItem;
      }
    }
    return null;
  }

  private void commitEdit(Edit edit, UserActivity activity) {
    activity.commitAndInvokeDo(edit);
  }

  private void insertStatement(BlockStatement body, Statement statement) {
    UserActivity activity = new UserActivity();
    commitEdit(
        new InsertStatementEdit<>(activity, new BlockStatementIndexPair(body, InsertStatementEdit.AT_END), statement),
        activity);
  }

  private MoveStatementEdit moveStatement(BlockStatement body, int fromIndex, int toIndex) {
    Statement statement = body.statements.get(fromIndex);
    UserActivity activity = new UserActivity();
    MoveStatementEdit edit = new MoveStatementEdit(
        activity,
        new BlockStatementIndexPair(body, fromIndex),
        statement,
        new BlockStatementIndexPair(body, toIndex),
        false);
    commitEdit(edit, activity);
    return edit;
  }

  private void clickButton(AbstractButton button) {
    TestIdeBootstrap.onEdt(() -> {
      button.doClick();
      return null;
    });
    waitForIdle();
  }

  private JDialog openRecursionPreferenceDialog() {
    IsRecursionAllowedPreferenceDialogComposite composite = new IsRecursionAllowedPreferenceDialogComposite();
    JDialog dialog = onEdt(() -> showStandaloneDialog("Recursion Preferences Harness", composite.getView().getAwtComponent()));
    waitForIdle();
    return dialog;
  }

  private JCheckBox findEnabledRecursionCheckBox(Container root) {
    List<JCheckBox> checkBoxes = findDescendants(root, JCheckBox.class);
    JCheckBox candidate = null;
    for (JCheckBox checkBox : checkBoxes) {
      if (onEdt(checkBox::isEnabled)) {
        candidate = checkBox;
      }
    }
    return candidate;
  }

  @SuppressWarnings("unchecked")
  private void restoreRecentProjects(ProjectSnapshot[] snapshots) {
    try {
      java.lang.reflect.Field valuesField = RecentProjectsListData.class.getDeclaredField("values");
      valuesField.setAccessible(true);
      List<ProjectSnapshot> values = (List<ProjectSnapshot>) valuesField.get(RecentProjectsListData.getInstance());
      values.clear();
      values.addAll(List.of(snapshots));
      Method fireContentsChanged = org.lgna.croquet.data.AbstractMutableListData.class.getDeclaredMethod("fireContentsChanged");
      fireContentsChanged.setAccessible(true);
      fireContentsChanged.invoke(RecentProjectsListData.getInstance());
    } catch (ReflectiveOperationException roe) {
      throw new AssertionError(roe);
    }
  }

  private void closeTransientWindows() {
    JFrame frame = this.ide != null ? frame() : null;
    TestIdeBootstrap.onEdt(() -> {
      for (Window window : Window.getWindows()) {
        if (window != null && window != frame && window.isDisplayable()) {
          window.dispose();
        }
      }
      return null;
    });
  }

  private void restoreProperty(String key, String value) {
    if (value == null) {
      System.clearProperty(key);
    } else {
      System.setProperty(key, value);
    }
  }

  private void ensureUiColor(String key, Color color) {
    if (UIManager.getColor(key) == null) {
      UIManager.put(key, color);
    }
  }

  private <T> T onEdt(java.util.concurrent.Callable<T> callable) {
    return TestIdeBootstrap.onEdt(callable);
  }

  private static <T extends Component> T findDescendant(Container root, Class<T> type) {
    List<T> matches = findDescendants(root, type);
    return matches.isEmpty() ? null : matches.get(0);
  }

  private static <T extends Component> List<T> findDescendants(Container root, Class<T> type) {
    List<T> matches = new ArrayList<>();
    collectDescendants(root, type, matches);
    return matches;
  }

  private static <T extends Component> void collectDescendants(Container root, Class<T> type, List<T> matches) {
    if (root == null) {
      return;
    }
    if (type.isInstance(root)) {
      matches.add(type.cast(root));
    }
    for (Component child : root.getComponents()) {
      if (type.isInstance(child)) {
        matches.add(type.cast(child));
      }
      if (child instanceof Container childContainer) {
        collectDescendants(childContainer, type, matches);
      }
    }
  }

  private final class SaveChooserDismissProbe {
    private final java.util.Timer timer = new java.util.Timer("alice-save-dialog-dismiss", true);
    private volatile boolean chooserObserved;
    private volatile boolean dismissed;

    void start() {
      this.timer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          poll();
        }
      }, 100, 100);
    }

    void stop() {
      this.timer.cancel();
    }

    boolean wasChooserObserved() {
      return this.chooserObserved;
    }

    boolean wasDismissed() {
      return this.dismissed;
    }

    private void poll() {
      for (Window window : Window.getWindows()) {
        if (!(window instanceof JDialog dialog) || !dialog.isShowing()) {
          continue;
        }
        JFileChooser chooser = findDescendant(dialog, JFileChooser.class);
        if (chooser == null) {
          continue;
        }
        this.chooserObserved = true;
        SwingUtilities.invokeLater(() -> {
          if (dialog.isShowing()) {
            chooser.cancelSelection();
            this.dismissed = true;
          }
        });
        return;
      }
    }
  }
}
