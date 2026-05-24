package org.alice.ide.integration;

import org.alice.ide.IdeApp;
import org.alice.ide.ProjectStack;
import org.alice.ide.project.ProjectDocumentState;
import org.alice.ide.ast.code.edits.MoveStatementEdit;
import org.alice.ide.ast.delete.DeleteStatementOperation;
import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.alice.ide.ast.export.type.TypeSummary;
import org.alice.ide.ast.export.type.TypeSummaryDataSource;
import org.alice.ide.ast.type.croquet.ImportTypeWizard;
import org.alice.ide.codeeditor.CodeEditor;
import org.alice.ide.codedrop.CodePanelWithDropReceptor;
import org.alice.ide.croquet.edits.ast.DeclareMethodEdit;
import org.alice.ide.croquet.edits.ast.InsertStatementEdit;
import org.alice.ide.croquet.models.html.HtmlProjectWriter;
import org.alice.ide.croquet.models.menubar.FileMenuModel;
import org.alice.ide.croquet.models.projecturi.OpenRecentProjectOperation;
import org.alice.ide.croquet.models.projecturi.SaveAsProjectOperation;
import org.alice.ide.croquet.models.ui.preferences.IsFullTypeHierarchyDesiredState;
import org.alice.ide.declarationseditor.CodeComposite;
import org.alice.ide.declarationseditor.TypeComposite;
import org.alice.ide.members.MembersComposite;
import org.alice.ide.preferences.recursion.IsAccessToRecursionPreferenceAllowedState;
import org.alice.ide.preferences.recursion.IsRecursionAllowedPreferenceDialogComposite;
import org.alice.ide.preferences.recursion.IsRecursionAllowedState;
import org.alice.ide.projecturi.ProjectSnapshot;
import org.alice.ide.projecturi.RecentProjectCountState;
import org.alice.ide.recentprojects.RecentProjectsListData;
import org.alice.ide.testing.ProjectContextFixture;
import org.alice.ide.testing.TestIdeBootstrap;
import org.alice.ide.uricontent.FileProjectLoader;
import org.alice.stageide.StageIDE;
import org.alice.stageide.gallerybrowser.ShapesTab;
import org.alice.stageide.sceneeditor.StorytellingSceneEditor;
import org.alice.stageide.sceneeditor.side.SideComposite;
import org.alice.stageide.sceneeditor.views.SceneObjectPropertyManagerPanel;
import org.alice.stageide.type.croquet.OtherTypeDialog;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.lgna.croquet.MenuBarComposite;
import org.lgna.croquet.StandardMenuItemPrepModel;
import org.lgna.croquet.edits.Edit;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.Project;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.BooleanExpressionBodyPair;
import org.lgna.project.ast.BooleanLiteral;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.CountLoop;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.Statement;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.io.IoUtilities;
import org.lgna.project.io.TypeResourcesPair;
import org.lgna.story.SModel;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
      this.robot.delay(150);
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
    for (int i = 0; i < 200 && sceneEditorRef.get() == null; i++) {
      this.robot.delay(100);
    }
    Assume.assumeTrue("scene perspective unavailable in bootstrap environment", sceneEditorRef.get() != null);
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

  private void injectUriProjectLoader(StageIDE stageIDE, FileProjectLoader loader) {
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
    this.robot.delay(120);
  }

  private void awaitCondition(String message, BooleanSupplier condition) {
    for (int i = 0; i < 200; i++) {
      if (condition.getAsBoolean()) {
        return;
      }
      this.robot.delay(100);
    }
    fail(message);
  }

  private JDialog awaitWindow(Predicate<Window> predicate, String message) {
    for (int i = 0; i < 50; i++) {
      JDialog dialog = onEdt(() -> findWindow(predicate));
      if (dialog != null) {
        return dialog;
      }
      this.robot.delay(100);
    }
    fail(message);
    return null;
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
