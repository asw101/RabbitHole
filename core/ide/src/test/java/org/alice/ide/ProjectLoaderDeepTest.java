package org.alice.ide;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.alice.ide.frametitle.IdeFrameTitleGenerator;
import org.alice.ide.project.ProjectDocumentState;
import org.alice.ide.croquet.models.projecturi.BackupProjectOperation;
import org.alice.ide.uricontent.ProjectLoadOutcome;
import org.alice.ide.uricontent.UriProjectLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.croquet.Operation;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.Project;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.story.SProgram;

import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static org.junit.Assert.*;

public class ProjectLoaderDeepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Before
  public void resetSingletonsBefore() throws Exception {
    resetApplicationSingleton();
    ProjectDocumentState.getInstance().setValueTransactionlessly(null);
  }

  @After
  public void resetSingletonsAfter() throws Exception {
    ProjectDocumentState.getInstance().setValueTransactionlessly(null);
    resetApplicationSingleton();
  }

  @Test
  public void loadProject_successfulDeliveryUpdatesProjectAndCursorLifecycle() {
    TestProjectApplication application = new TestProjectApplication();
    Project loadedProject = projectNamed("LoadedProgram");
    File projectFile = new File("target/project-loader-deep/success.a3p");
    ImmediateProjectLoader loader = new ImmediateProjectLoader(projectFile, loadedProject);
    UserActivity activity = new UserActivity();
    Set<String> unloadableFiles = new HashSet<>();
    unloadableFiles.add("missing-resource.obj");

    application.getProjectLoader().loadProject(activity, loader, false, false, unloadableFiles);

    assertSame(loader, application.getUriProjectLoader());
    assertSame(loadedProject, application.getProject());
    assertEquals(1, application.showWaitCursorCount);
    assertEquals(1, application.hideWaitCursorCount);
    assertEquals(1, application.updateInterfaceAfterLoadCount);
    assertEquals(1, application.newProjectActivityCount);
    assertFalse(activity.isCanceled());
  }

  @Test
  public void loadProject_nonProjectFileSkipsUpdateButStillClearsWaitCursor() {
    TestProjectApplication application = new TestProjectApplication();
    Project originalProject = projectNamed("OriginalProgram");
    application.setProject(originalProject);
    application.clearObservations();
    File nonProjectFile = new File("target/project-loader-deep/not-a-project.txt");

    application.getProjectLoader().loadProject(
        new UserActivity(),
        new ImmediateProjectLoader(nonProjectFile, projectNamed("IgnoredProgram")),
        false,
        false,
        new HashSet<>());

    assertSame(originalProject, application.getProject());
    assertEquals(1, application.showWaitCursorCount);
    assertEquals(1, application.hideWaitCursorCount);
    assertEquals(0, application.updateInterfaceAfterLoadCount);
  }

  @Test
  public void loadProject_nonProjectFailureReturnsAfterExistingDialogPath() {
    TestProjectApplication application = new TestProjectApplication();
    Project originalProject = projectNamed("OriginalProgram");
    application.setProject(originalProject);
    application.clearObservations();
    File typeFile = new File("target/project-loader-deep/not-a-project.a3c");

    application.getProjectLoader().loadProject(
        new UserActivity(),
        new ImmediateProjectLoader(typeFile, ProjectLoadOutcome.typeFileNotProject(typeFile)),
        false,
        false,
        new HashSet<>());

    assertSame(originalProject, application.getProject());
    assertEquals(1, application.showWaitCursorCount);
    assertEquals(1, application.hideWaitCursorCount);
    assertEquals(0, application.updateInterfaceAfterLoadCount);
    assertEquals(0, application.newProjectActivityCount);
  }

  @Test
  public void loadProject_successUsesLoaderUriInsteadOfOutcomeDiagnosticFile() {
    TestProjectApplication application = new TestProjectApplication();
    Project loadedProject = projectNamed("LoadedProgram");
    File saveTarget = new File("target/project-loader-deep/success-vr.a3p");
    File sourceFile = new File("target/project-loader-deep/source.a3p");
    ImmediateProjectLoader loader = new ImmediateProjectLoader(
        saveTarget,
        ProjectLoadOutcome.success(loadedProject, sourceFile));

    application.getProjectLoader().loadProject(new UserActivity(), loader, false, false, new HashSet<>());

    assertSame(loadedProject, application.getProject());
    assertEquals(saveTarget, application.projectFileUtilities.appropriateBackupDirectoryFile);
  }

  @Test
  public void loadProject_nullLoaderLeavesUiUntouched() {
    TestProjectApplication application = new TestProjectApplication();

    application.getProjectLoader().loadProject(new UserActivity(), null);

    assertNull(application.getUriProjectLoader());
    assertEquals(0, application.showWaitCursorCount);
    assertEquals(0, application.hideWaitCursorCount);
    assertEquals(0, application.updateInterfaceAfterLoadCount);
  }

  @Test
  public void loadProject_failureUsesOutcomeFileForBackupRouting() {
    TestProjectApplication application = new TestProjectApplication();
    File loaderFile = new File("target/project-loader-deep/original.a3p");
    File failedFile = new File("target/project-loader-deep/failed-backup.a3p");
    ProjectLoadOutcome failure = ProjectLoadOutcome.ioFailure(failedFile, new IOException("broken"));
    ImmediateProjectLoader loader = new ImmediateProjectLoader(loaderFile, failure);
    UserActivity activity = new UserActivity();

    application.getProjectLoader().loadProject(activity, loader, true, false, new HashSet<>());

    assertEquals(failedFile, application.projectFileUtilities.appropriateBackupDirectoryFile);
    assertEquals(failedFile.getName(), application.backupProjectOperation.loadMainProjectPromptName);
    assertEquals(1, application.showWaitCursorCount);
    assertEquals(1, application.hideWaitCursorCount);
    assertTrue(activity.isCanceled());
  }

  @Test
  public void loadProject_newProjectFailureDoesNotUseBackupRecovery() {
    TestProjectApplication application = new TestProjectApplication();
    File starterFile = new File("target/project-loader-deep/starter.a3p");
    ImmediateProjectLoader loader = new ImmediateProjectLoader(
        starterFile,
        ProjectLoadOutcome.ioFailure(starterFile, new IOException("broken")),
        true);
    UserActivity activity = new UserActivity();

    application.getProjectLoader().loadProject(activity, loader, false, false, new HashSet<>());

    assertNull(application.projectFileUtilities.appropriateBackupDirectoryFile);
    assertEquals(1, application.newProjectActivityCount);
    assertEquals(1, application.showWaitCursorCount);
    assertEquals(1, application.hideWaitCursorCount);
    assertTrue(activity.isCanceled());
  }

  private static Project projectNamed(String name) {
    return new Project(programType(name), Project.SceneCameraType.WindowCamera);
  }

  private static NamedUserType programType(String name) {
    return AstUtilities.createType(name, JavaType.getInstance(SProgram.class));
  }

  private static void resetApplicationSingleton() throws Exception {
    Field field = Application.class.getDeclaredField("singleton");
    field.setAccessible(true);
    field.set(null, null);
  }

  private static final class TestProjectApplication extends ProjectApplication {
    private final TestProjectFileUtilities projectFileUtilities = new TestProjectFileUtilities(this);
    private final SilentBackupProjectOperation backupProjectOperation = new SilentBackupProjectOperation();
    private int showWaitCursorCount;
    private int hideWaitCursorCount;
    private int updateInterfaceAfterLoadCount;
    private int newProjectActivityCount;

    TestProjectApplication() {
      super((ProjectDocumentFrame) null);
    }

    void clearObservations() {
      this.showWaitCursorCount = 0;
      this.hideWaitCursorCount = 0;
      this.updateInterfaceAfterLoadCount = 0;
      this.newProjectActivityCount = 0;
    }

    @Override
    protected IdeFrameTitleGenerator createFrameTitleGenerator() {
      return (projectLoader, isDocumentUpToDateWithUri) -> "ProjectLoaderDeepTest";
    }

    @Override
    protected void updateTitle() {
    }

    @Override
    protected BufferedImage createThumbnail() {
      return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public void forceProjectCodeUpToDate() {
    }

    @Override
    public void ensureProjectCodeUpToDate() {
    }

    @Override
    protected Operation getAboutOperation() {
      return null;
    }

    @Override
    protected Operation getPreferencesOperation() {
      return null;
    }

    @Override
    protected void handleOpenFiles(List<File> files) {
    }

    @Override
    protected void handleWindowOpened(WindowEvent e) {
    }

    @Override
    public void handleQuit(UserActivity activity) {
    }

    @Override
    public String getApplicationSubPath() {
      return "project-loader-deep-test";
    }

    @Override
    public void showWaitCursor() {
      this.showWaitCursorCount++;
    }

    @Override
    public void hideWaitCursor() {
      this.hideWaitCursorCount++;
    }

    @Override
    UserActivity newProjectActivity() {
      this.newProjectActivityCount++;
      return new UserActivity();
    }

    @Override
    ProjectFileUtilities getProjectFileUtilities() {
      return this.projectFileUtilities;
    }

    @Override
    BackupProjectOperation getBackupProjectOperation() {
      return this.backupProjectOperation;
    }

    @Override
    void updateInterfaceAfterLoad() {
      this.updateInterfaceAfterLoadCount++;
    }
  }

  private static final class TestProjectFileUtilities extends ProjectFileUtilities {
    private File appropriateBackupDirectoryFile;

    TestProjectFileUtilities(ProjectApplication application) {
      super(application);
    }

    @Override
    public Path appropriateBackupDirectory(File saved) {
      this.appropriateBackupDirectoryFile = saved;
      return null;
    }
  }

  private static final class SilentBackupProjectOperation extends BackupProjectOperation {
    private String loadMainProjectPromptName;

    @Override
    public boolean showProjectLoadRecentBackupsErrorAndLoadMainDialog(String projectName) {
      this.loadMainProjectPromptName = projectName;
      return false;
    }
  }

  private static final class ImmediateProjectLoader extends UriProjectLoader {
    private final URI uri;
    private final File mainProjectFile;
    private final ProjectLoadOutcome outcome;
    private final boolean newProject;

    ImmediateProjectLoader(File projectFile, Project project) {
      this(projectFile, ProjectLoadOutcome.success(project, projectFile));
    }

    ImmediateProjectLoader(File projectFile, ProjectLoadOutcome outcome) {
      this(projectFile, outcome, false);
    }

    ImmediateProjectLoader(File projectFile, ProjectLoadOutcome outcome, boolean newProject) {
      super(false);
      this.uri = projectFile.toURI();
      this.mainProjectFile = projectFile;
      this.outcome = outcome;
      this.newProject = newProject;
    }

    @Override
    public URI getUri() {
      return this.uri;
    }

    @Override
    protected Project load() {
      return this.outcome.getProject();
    }

    @Override
    public synchronized void deliverContentOnEventDispatchThread(Consumer<Project> observer) {
      observer.accept(this.outcome.getProject());
    }

    @Override
    public synchronized void deliverLoadOutcomeOnEventDispatchThread(Consumer<ProjectLoadOutcome> observer) {
      observer.accept(this.outcome);
    }

    @Override
    public boolean isNewProject() {
      return this.newProject;
    }

    @Override
    public boolean isBackup() {
      return false;
    }

    @Override
    public boolean isDefaultBackup() {
      return false;
    }

    @Override
    public File getMainProjectFile() {
      return this.mainProjectFile;
    }
  }
}
