package org.alice.ide;

import org.alice.ide.frametitle.IdeFrameTitleGenerator;
import org.alice.ide.project.ProjectDocumentState;
import org.alice.ide.uricontent.FileProjectLoader;
import org.alice.ide.uricontent.UriProjectLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProjectFileUtilitiesBackupBehaviorTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Before
  public void setUp() throws Exception {
    resetApplicationSingleton();
    ProjectDocumentState.getInstance().setValueTransactionlessly(null);
  }

  @After
  public void tearDown() throws Exception {
    ProjectDocumentState.getInstance().setValueTransactionlessly(null);
    resetApplicationSingleton();
  }

  @Test
  public void backupActiveProjectSkipsWhenProjectIsAlreadyUpToDateWithBackups() throws Exception {
    Path defaultBackupDirectory = temporaryFolder.getRoot().toPath().resolve(".defaultbak");
    TestProjectApplication application = new TestProjectApplication(projectNamed("Program"), true, defaultBackupDirectory);

    application.getProjectFileUtilities().backupActiveProject();

    assertFalse(Files.exists(defaultBackupDirectory));
  }

  @Test
  public void appropriateBackupDirectoryUsesDefaultDirectoryForNewProjects() {
    Path defaultBackupDirectory = temporaryFolder.getRoot().toPath().resolve(".defaultbak");
    TestProjectApplication application = new TestProjectApplication(projectNamed("Program"), false, defaultBackupDirectory);
    application.setUriProjectLoader(new StubUriProjectLoader(null, true));

    assertEquals(defaultBackupDirectory, application.getProjectFileUtilities().appropriateBackupDirectory(new File("ignored.a3p")));
  }

  @Test
  public void appropriateBackupDirectoryUsesParentDirectoryForBackupFiles() throws Exception {
    Path namedBackupDirectory = temporaryFolder.newFolder("lesson.bak").toPath();
    File backupFile = namedBackupDirectory.resolve("auto20240102_120000.a3p").toFile();
    TestProjectApplication application = new TestProjectApplication(projectNamed("Program"), false, temporaryFolder.getRoot().toPath().resolve(".defaultbak"));
    application.setUriProjectLoader(new FileProjectLoader(backupFile));

    assertEquals(namedBackupDirectory, application.getProjectFileUtilities().appropriateBackupDirectory(backupFile));
  }

  private static Project projectNamed(String name) {
    return new Project(AstUtilities.createType(name, JavaType.getInstance(SProgram.class)), Project.SceneCameraType.WindowCamera);
  }

  private static void resetApplicationSingleton() throws Exception {
    Field field = Application.class.getDeclaredField("singleton");
    field.setAccessible(true);
    field.set(null, null);
  }

  private static final class TestProjectApplication extends ProjectApplication {
    private final TestProjectFileUtilities projectFileUtilities;
    private final boolean projectUpToDateWithBackups;

    TestProjectApplication(Project project, boolean projectUpToDateWithBackups, Path defaultBackupDirectory) {
      super((ProjectDocumentFrame) null);
      this.projectFileUtilities = new TestProjectFileUtilities(this, defaultBackupDirectory, project);
      this.projectUpToDateWithBackups = projectUpToDateWithBackups;
    }

    @Override
    protected IdeFrameTitleGenerator createFrameTitleGenerator() {
      return (projectLoader, isDocumentUpToDateWithUri) -> "ProjectFileUtilitiesBackupBehaviorTest";
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
      return "project-file-utilities-backup-behavior-test";
    }

    @Override
    ProjectFileUtilities getProjectFileUtilities() {
      return this.projectFileUtilities;
    }

    @Override
    protected boolean isProjectUpToDateWithBackups() {
      return this.projectUpToDateWithBackups;
    }
  }

  private static final class TestProjectFileUtilities extends ProjectFileUtilities {
    private final Path defaultBackupDirectory;
    private final Project project;

    TestProjectFileUtilities(ProjectApplication app, Path defaultBackupDirectory, Project project) {
      super(app);
      this.defaultBackupDirectory = defaultBackupDirectory;
      this.project = project;
    }

    @Override
    Project getUpToDateProject() {
      return this.project;
    }

    @Override
    Project getForcedUpToDateProject() {
      return this.project;
    }

    @Override
    public Path defaultBackupDirectory() {
      return this.defaultBackupDirectory;
    }
  }

  private static final class StubUriProjectLoader extends UriProjectLoader {
    private final URI uri;
    private final boolean newProject;

    private StubUriProjectLoader(URI uri, boolean newProject) {
      super(false);
      this.uri = uri;
      this.newProject = newProject;
    }

    @Override
    public URI getUri() {
      return this.uri;
    }

    @Override
    public boolean isNewProject() {
      return this.newProject;
    }

    @Override
    protected Project load() {
      return null;
    }
  }
}
