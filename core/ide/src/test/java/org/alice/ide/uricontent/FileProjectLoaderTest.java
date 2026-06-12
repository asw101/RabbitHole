package org.alice.ide.uricontent;

import org.lgna.project.Project;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.io.IoUtilities;
import org.lgna.story.SProgram;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class FileProjectLoaderTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void savedTemporaryProjectLoadsAndCorruptTemporaryProjectIsRejected() throws IOException {
    File savedProject = temporaryFolder.newFile("saved-generated-world.a3p");
    Project project = new Project(programType("GeneratedProgram"), Project.SceneCameraType.WindowCamera);
    IoUtilities.writeProject(savedProject, project);

    Project loadedProject = new FileProjectLoader(savedProject).load();

    assertNotNull(loadedProject);
    assertEquals("GeneratedProgram", loadedProject.getProgramType().getName());

    File corruptProject = temporaryFolder.newFile("corrupt-generated-world.a3p");
    Files.writeString(corruptProject.toPath(), "not an Alice project archive", StandardCharsets.UTF_8);

    Project rejectedProject = new FileProjectLoader(corruptProject).load();

    assertNull(rejectedProject);
  }

  @Test
  public void savedProjectOutcomeCarriesLoadedProject() throws IOException {
    File savedProject = temporaryFolder.newFile("saved-outcome-world.a3p");
    Project project = new Project(programType("SavedOutcomeProgram"), Project.SceneCameraType.WindowCamera);
    IoUtilities.writeProject(savedProject, project);
    FileProjectLoader loader = new FileProjectLoader(savedProject);

    ProjectLoadOutcome outcome = loader.loadOutcome();

    assertEquals(ProjectLoadOutcome.Kind.SUCCESS, outcome.getKind());
    assertEquals(ProjectLoadOutcome.Status.LOADED, outcome.getStatus());
    assertNotNull(outcome.getProject());
    assertEquals("SavedOutcomeProgram", outcome.getProject().getProgramType().getName());
    assertEquals(savedProject, outcome.getFile());
  }

  @Test
  public void corruptProjectOutcomeIsIoFailureAndDelegatesIoHook() throws IOException {
    File corruptProject = temporaryFolder.newFile("corrupt-outcome-world.a3p");
    Files.writeString(corruptProject.toPath(), "not an Alice project archive", StandardCharsets.UTF_8);
    CapturingFileProjectLoader loader = new CapturingFileProjectLoader(corruptProject);

    ProjectLoadOutcome outcome = loader.loadOutcome();

    assertLoadFailure(ProjectLoadOutcome.Status.IO_FAILURE, outcome);
    assertEquals(corruptProject, outcome.getFile());
    assertTrue(outcome.getException() instanceof IOException);
    assertEquals(corruptProject, loader.file);
    assertTrue(loader.exception instanceof IOException);
  }

  @Test
  public void missingProjectOutcomeIsMissingFileWithoutIoHook() {
    File missingProject = new File(temporaryFolder.getRoot(), "missing-outcome-world.a3p");
    CapturingFileProjectLoader loader = new CapturingFileProjectLoader(missingProject);

    ProjectLoadOutcome outcome = loader.loadOutcome();

    assertLoadFailure(ProjectLoadOutcome.Status.MISSING_FILE, outcome);
    assertEquals(missingProject, outcome.getFile());
    assertNull(loader.file);
    assertNull(loader.exception);
  }

  @Test
  public void alice2WorldOutcomeIdentifiesLegacyWorldWithoutIoHook() throws IOException {
    File alice2World = temporaryFolder.newFile("legacy-outcome-world.a2w");
    CapturingFileProjectLoader loader = new CapturingFileProjectLoader(alice2World);

    ProjectLoadOutcome outcome = loader.loadOutcome();

    assertLoadFailure(ProjectLoadOutcome.Status.ALICE2_WORLD, outcome);
    assertEquals(alice2World, outcome.getFile());
    assertNull(loader.file);
    assertNull(loader.exception);
  }

  @Test
  public void typeFileOutcomeIdentifiesTypeFileWithoutIoHook() throws IOException {
    File typeFile = temporaryFolder.newFile("selected-outcome-class." + IoUtilities.TYPE_EXTENSION);
    CapturingFileProjectLoader loader = new CapturingFileProjectLoader(typeFile);

    ProjectLoadOutcome outcome = loader.loadOutcome();

    assertLoadFailure(ProjectLoadOutcome.Status.TYPE_FILE_NOT_PROJECT, outcome);
    assertEquals(typeFile, outcome.getFile());
    assertNull(loader.file);
    assertNull(loader.exception);
  }

  @Test
  public void exceptionOutcomesRequireDiagnosticException() throws IOException {
    File project = temporaryFolder.newFile("diagnostic-exception-world.a3p");

    assertThrows(NullPointerException.class, () -> ProjectLoadOutcome.versionNotSupported(project, null));
    assertThrows(NullPointerException.class, () -> ProjectLoadOutcome.ioFailure(project, null));
    assertThrows(NullPointerException.class, () -> ProjectLoadOutcome.runtimeException(project, null));
  }

  @Test
  public void deliveredRuntimeExceptionBecomesTypedOutcome() throws Exception {
    File project = temporaryFolder.newFile("runtime-exception-world.a3p");
    RuntimeException failure = new RuntimeException("unchecked load failure");
    RuntimeThrowingUriProjectLoader loader = new RuntimeThrowingUriProjectLoader(project, failure);
    CountDownLatch delivered = new CountDownLatch(1);
    AtomicReference<ProjectLoadOutcome> observed = new AtomicReference<>();

    loader.deliverLoadOutcomeOnEventDispatchThread(outcome -> {
      observed.set(outcome);
      delivered.countDown();
    });

    assertTrue(delivered.await(5, TimeUnit.SECONDS));
    assertLoadFailure(ProjectLoadOutcome.Status.RUNTIME_EXCEPTION, observed.get());
    assertEquals(project, observed.get().getFile());
    assertSame(failure, observed.get().getException());
  }

  @Test
  public void loadDelegatesIoFailureToHookAndReturnsNull() throws IOException {
    File corruptProject = temporaryFolder.newFile("corrupt-project.a3p");
    CapturingFileProjectLoader loader = new CapturingFileProjectLoader(corruptProject);

    Project project = loader.load();

    assertNull(project);
    assertEquals(corruptProject, loader.file);
    assertTrue(loader.exception instanceof IOException);
  }

  @Test
  public void missingProjectFileReturnsNullWithoutIoFailureHook() {
    File missingProject = new File(temporaryFolder.getRoot(), "missing-project.a3p");
    CapturingFileProjectLoader loader = new CapturingFileProjectLoader(missingProject);

    Project project = loader.load();

    assertNull(project);
    assertNull(loader.file);
    assertNull(loader.exception);
  }

  @Test
  public void alice2WorldReturnsNullWithoutIoFailureHook() throws IOException {
    File alice2World = temporaryFolder.newFile("legacy-world.a2w");
    CapturingFileProjectLoader loader = new CapturingFileProjectLoader(alice2World);

    Project project = loader.load();

    assertNull(project);
    assertNull(loader.file);
    assertNull(loader.exception);
  }

  @Test
  public void typeFileReturnsNullWithoutIoFailureHook() throws IOException {
    File typeFile = temporaryFolder.newFile("selected-class." + IoUtilities.TYPE_EXTENSION);
    CapturingFileProjectLoader loader = new CapturingFileProjectLoader(typeFile);

    Project project = loader.load();

    assertNull(project);
    assertNull(loader.file);
    assertNull(loader.exception);
  }

  @Test
  public void nonVrLoaderUsesOriginalProjectUriAndDoesNotRequireSaveWhenFileExists() throws IOException {
    File project = temporaryFolder.newFile("saved-project.a3p");
    FileProjectLoader loader = new FileProjectLoader(project);

    assertEquals(project.toURI(), loader.getUri());
    assertFalse(loader.shouldBeSaved());
  }

  @Test
  public void vrReadyLoaderUsesRenamedProjectUriAndRequiresSaveWhenVrCopyDoesNotExist() throws IOException {
    File project = temporaryFolder.newFile("saved-project.a3p");
    File vrProject = vrProjectFor(project);
    vrProject.delete();
    FileProjectLoader loader = new FileProjectLoader(project, true);

    assertEquals(vrProject.toURI(), loader.getUri());
    assertTrue(loader.shouldBeSaved());
  }

  @Test
  public void vrReadyLoaderDoesNotRequireSaveWhenVrCopyAlreadyExists() throws IOException {
    File project = temporaryFolder.newFile("saved-project.a3p");
    File vrProject = vrProjectFor(project);
    assertTrue(vrProject.createNewFile());
    FileProjectLoader loader = new FileProjectLoader(project, true);

    assertEquals(vrProject.toURI(), loader.getUri());
    assertFalse(loader.shouldBeSaved());
  }

  @Test
  public void normalProjectFileIsNotBackupAndIsItsOwnMainProjectFile() throws IOException {
    File project = temporaryFolder.newFile("world.a3p");
    FileProjectLoader loader = new FileProjectLoader(project);

    assertFalse(loader.isBackup());
    assertFalse(loader.isDefaultBackup());
    assertEquals(project, loader.getMainProjectFile());
  }

  @Test
  public void namedBackupFileDerivesMainProjectFileFromBackupDirectoryName() throws IOException {
    File backupDirectory = temporaryFolder.newFolder("world.bak");
    File backup = new File(backupDirectory, "auto20240102_120000.a3p");
    assertTrue(backup.createNewFile());
    FileProjectLoader loader = new FileProjectLoader(backup);

    assertTrue(loader.isBackup());
    assertFalse(loader.isDefaultBackup());
    assertEquals(new File(temporaryFolder.getRoot(), "world.a3p"), loader.getMainProjectFile());
  }

  @Test
  public void defaultBackupFileIsBackupWithoutMainProjectFile() throws IOException {
    File defaultBackupDirectory = temporaryFolder.newFolder(".defaultbak");
    File defaultBackup = new File(defaultBackupDirectory, "auto20240102_120000.a3p");
    assertTrue(defaultBackup.createNewFile());
    FileProjectLoader loader = new FileProjectLoader(defaultBackup);

    assertTrue(loader.isBackup());
    assertTrue(loader.isDefaultBackup());
    assertNull(loader.getMainProjectFile());
  }

  @Test
  public void newProjectLoaderIsNotBackupAndHasNoMainProjectFile() {
    UriProjectLoader loader = new NewProjectLoader();

    assertFalse(loader.isBackup());
    assertFalse(loader.isDefaultBackup());
    assertNull(loader.getMainProjectFile());
  }

  private static File vrProjectFor(File project) {
    String source = project.getAbsolutePath();
    return new File(source.substring(0, source.length() - 4) + " VR" + source.substring(source.length() - 4));
  }

  private static NamedUserType programType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(SProgram.class));
    return type;
  }

  private static void assertLoadFailure(ProjectLoadOutcome.Status status, ProjectLoadOutcome outcome) {
    assertEquals(ProjectLoadOutcome.Kind.FAILURE, outcome.getKind());
    assertEquals(status, outcome.getStatus());
    assertNull(outcome.getProject());
  }

  private static class NewProjectLoader extends UriProjectLoader {
    NewProjectLoader() {
      super(false);
    }

    @Override
    public URI getUri() {
      return URI.create("blank://project");
    }

    @Override
    public boolean isNewProject() {
      return true;
    }

    @Override
    protected Project load() {
      throw new AssertionError("load is outside UriProjectLoader path classification");
    }
  }

  private static class CapturingFileProjectLoader extends AbstractFileProjectLoader {
    private File file;
    private Exception exception;

    CapturingFileProjectLoader(File file) {
      super(file, false);
    }

    @Override
    public URI getUri() {
      return getFile().toURI();
    }

    @Override
    protected void handleLoadException(File file, Exception e) {
      this.file = file;
      this.exception = e;
    }

    @Override
    public boolean isNewProject() {
      return false;
    }
  }

  private static class RuntimeThrowingUriProjectLoader extends UriProjectLoader {
    private final File file;
    private final RuntimeException failure;

    RuntimeThrowingUriProjectLoader(File file, RuntimeException failure) {
      super(false);
      this.file = file;
      this.failure = failure;
    }

    @Override
    public URI getUri() {
      return this.file.toURI();
    }

    @Override
    protected Project load() {
      throw this.failure;
    }

    @Override
    public boolean isNewProject() {
      return false;
    }
  }
}
