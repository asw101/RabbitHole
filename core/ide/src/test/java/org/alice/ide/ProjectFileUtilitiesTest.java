package org.alice.ide;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.lgna.common.Resource;
import org.lgna.project.Project;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.io.IoUtilities;
import org.lgna.project.io.ProjectIo;
import org.lgna.story.SProgram;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.zip.ZipFile;

import static org.junit.Assert.*;

public class ProjectFileUtilitiesTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private final ProjectFileUtilities utilities = new ProjectFileUtilities(null);

  @Test
  public void savedProjectBackupDirectoryUsesSiblingBakDirectory() throws IOException {
    File project = temporaryFolder.newFile("world.a3p");

    Path backupDirectory = utilities.backupDirectory(project, false);

    assertEquals(temporaryFolder.getRoot().toPath().resolve("world.bak"), backupDirectory);
    assertTrue(Files.isDirectory(backupDirectory));
  }

  @Test
  public void nonProjectFileBackupDirectoryUsesFullFileName() throws IOException {
    File project = temporaryFolder.newFile("world.txt");

    Path backupDirectory = utilities.backupDirectory(project, false);

    assertEquals(temporaryFolder.getRoot().toPath().resolve("world.txt.bak"), backupDirectory);
    assertTrue(Files.isDirectory(backupDirectory));
  }

  @Test
  public void backupFileUsesParentDirectory() throws IOException {
    File backupDirectory = temporaryFolder.newFolder("world.bak");
    File backupFile = new File(backupDirectory, "auto20240102_120000.a3p");

    Path resolvedDirectory = utilities.backupDirectory(backupFile, true);

    assertEquals(backupDirectory.toPath(), resolvedDirectory);
  }

  @Test
  public void parentlessBackupFileHasNoBackupDirectory() {
    File backupFile = new File("auto20240102_120000.a3p");

    Path resolvedDirectory = utilities.backupDirectory(backupFile, true);

    assertNull(resolvedDirectory);
  }

  @Test
  public void exportCopyWritesPlayerArchiveWithThumbnailAndManifest() throws IOException {
    Project project = new Project(programType("Program"), Project.SceneCameraType.WindowCamera);
    ProjectFileUtilities exportUtilities = new ProjectFileUtilities(null) {
      @Override
      Project getForcedUpToDateProject() {
        return project;
      }

      @Override
      BufferedImage createThumbnail() {
        return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
      }
    };
    File exportFile = temporaryFolder.newFile("exported.a3p");

    exportUtilities.exportCopyOfProjectTo(exportFile);

    try (ZipFile zipFile = new ZipFile(exportFile)) {
      assertNotNull(zipFile.getEntry(ProjectIo.VERSION_ENTRY_NAME));
      assertNotNull(zipFile.getEntry(ProjectIo.MANIFEST_ENTRY_NAME));
      assertNotNull(zipFile.getEntry("thumbnail.png"));
      assertNotNull(zipFile.getEntry("src/Program.twe"));
      String manifest = new String(
          zipFile.getInputStream(zipFile.getEntry(ProjectIo.MANIFEST_ENTRY_NAME)).readAllBytes(),
          StandardCharsets.UTF_8);
      assertTrue(manifest, manifest.contains("\"name\":\"Program\""));
      assertTrue(manifest, manifest.contains("\"icon\":\"thumbnail.png\""));
    }
  }

  @Test
  public void saveCopyWritesReadableEditorArchiveWithResourceManifestAndThumbnail() throws Exception {
    byte[] data = "hello alice".getBytes(StandardCharsets.UTF_8);
    Project project = new Project(programType("Program"), Project.SceneCameraType.WindowCamera);
    TestResource resource = new TestResource("note.txt", "text/plain", data);
    project.addResource(resource);
    ProjectFileUtilities saveUtilities = new ProjectFileUtilities(null) {
      @Override
      Project getUpToDateProject() {
        return project;
      }

      @Override
      BufferedImage createThumbnail() {
        return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
      }
    };
    File saveFile = temporaryFolder.newFile("saved-copy.a3p");

    saveUtilities.saveCopyOfProjectTo(saveFile);

    try (ZipFile zipFile = new ZipFile(saveFile)) {
      assertNotNull(zipFile.getEntry(ProjectIo.VERSION_ENTRY_NAME));
      assertNotNull(zipFile.getEntry(ProjectIo.MANIFEST_ENTRY_NAME));
      assertNotNull(zipFile.getEntry("thumbnail.png"));
      assertNotNull(zipFile.getEntry("programType.xml"));
      assertNotNull(zipFile.getEntry("resources.xml"));
      assertNotNull(zipFile.getEntry("resources/note.txt"));
      String manifest = new String(
          zipFile.getInputStream(zipFile.getEntry(ProjectIo.MANIFEST_ENTRY_NAME)).readAllBytes(),
          StandardCharsets.UTF_8);
      assertTrue(manifest, manifest.contains("\"name\":\"Program\""));
      assertTrue(manifest, manifest.contains("\"icon\":\"thumbnail.png\""));
    }

    Project readProject = IoUtilities.readProject(saveFile);
    assertEquals("Program", readProject.getProgramType().getName());
    assertEquals(Project.SceneCameraType.WindowCamera, readProject.createSaveManifest().projectStructure.sceneCameraType);
    assertEquals(1, readProject.getResources().size());
    Resource readResource = readProject.getResources().iterator().next();
    assertEquals(TestResource.class, readResource.getClass());
    assertEquals(resource.getId(), readResource.getId());
    assertEquals("note.txt", readResource.getOriginalFileName());
    assertEquals("note.txt", readResource.getName());
    assertEquals("text/plain", readResource.getContentType());
    assertArrayEquals(data, readResource.getData());
  }

  private static NamedUserType programType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(SProgram.class));
    return type;
  }

  public static class TestResource extends Resource {
    public TestResource(String fileName, String contentType, byte[] data) {
      super(fileName, contentType, data);
    }

    private TestResource(UUID uuid) {
      super(uuid);
    }

    public static TestResource valueOf(String uuidText) {
      return new TestResource(UUID.fromString(uuidText));
    }
  }
}
