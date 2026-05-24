package org.alice.ide.integration;

import org.alice.ide.ProjectStack;
import org.alice.ide.project.ProjectDocumentState;
import org.alice.ide.codeeditor.CodeEditor;
import org.alice.ide.codedrop.CodePanelWithDropReceptor;
import org.alice.ide.croquet.models.menubar.FileMenuModel;
import org.alice.ide.croquet.models.projecturi.SaveAsProjectOperation;
import org.alice.ide.declarationseditor.CodeComposite;
import org.alice.ide.uricontent.FileProjectLoader;
import org.alice.ide.testing.ProjectContextFixture;
import org.alice.ide.testing.TestIdeBootstrap;
import org.alice.stageide.StageIDE;
import org.alice.stageide.type.croquet.OtherTypeDialog;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.lgna.croquet.MenuBarComposite;
import org.lgna.croquet.StandardMenuItemPrepModel;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.Project;
import org.alice.stageide.gallerybrowser.ShapesTab;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.io.IoUtilities;
import org.lgna.story.SModel;

import javax.swing.Action;
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
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
    if (UIManager.getColor("Alice.Type.color") == null) {
      UIManager.put("Alice.Type.color", new Color(200, 210, 240));
    }

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
    for (int i = 0; i < 50; i++) {
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

  private <T> T onEdt(java.util.concurrent.Callable<T> callable) {
    return TestIdeBootstrap.onEdt(callable);
  }

  private static <T extends Component> T findDescendant(Container root, Class<T> type) {
    if (root == null) {
      return null;
    }
    if (type.isInstance(root)) {
      return type.cast(root);
    }
    for (Component child : root.getComponents()) {
      if (type.isInstance(child)) {
        return type.cast(child);
      }
      if (child instanceof Container childContainer) {
        T match = findDescendant(childContainer, type);
        if (match != null) {
          return match;
        }
      }
    }
    return null;
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
