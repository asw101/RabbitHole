package org.alice.ide;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.alice.ide.frametitle.IdeFrameTitleGenerator;
import org.alice.ide.project.ProjectDocumentState;
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
import java.lang.reflect.Field;
import java.net.URI;
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
  public void loadProject_nullLoaderLeavesUiUntouched() {
    TestProjectApplication application = new TestProjectApplication();

    application.getProjectLoader().loadProject(new UserActivity(), null);

    assertNull(application.getUriProjectLoader());
    assertEquals(0, application.showWaitCursorCount);
    assertEquals(0, application.hideWaitCursorCount);
    assertEquals(0, application.updateInterfaceAfterLoadCount);
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
    void updateInterfaceAfterLoad() {
      this.updateInterfaceAfterLoadCount++;
    }
  }

  private static final class TestProjectFileUtilities extends ProjectFileUtilities {
    TestProjectFileUtilities(ProjectApplication application) {
      super(application);
    }
  }

  private static final class ImmediateProjectLoader extends UriProjectLoader {
    private final URI uri;
    private final File mainProjectFile;
    private final Project project;

    ImmediateProjectLoader(File projectFile, Project project) {
      super(false);
      this.uri = projectFile.toURI();
      this.mainProjectFile = projectFile;
      this.project = project;
    }

    @Override
    public URI getUri() {
      return this.uri;
    }

    @Override
    protected Project load() {
      return this.project;
    }

    @Override
    public synchronized void deliverContentOnEventDispatchThread(Consumer<Project> observer) {
      observer.accept(this.project);
    }

    @Override
    public boolean isNewProject() {
      return false;
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
