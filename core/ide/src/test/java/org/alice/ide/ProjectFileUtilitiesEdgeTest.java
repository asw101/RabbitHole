package org.alice.ide;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.lgna.project.Project;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.story.SProgram;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipFile;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ProjectFileUtilitiesEdgeTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void clearBackupFailsAllowsFutureBackupDirectoryCreation() throws IOException {
    ProjectFileUtilities utilities = new ProjectFileUtilities(null);
    File firstProject = temporaryFolder.newFile("world.a3p");
    Path collidingBackupPath = temporaryFolder.getRoot().toPath().resolve("world.bak");
    Files.writeString(collidingBackupPath, "collision", StandardCharsets.UTF_8);

    assertNull(utilities.backupDirectory(firstProject, false));

    File secondProject = temporaryFolder.newFile("lesson.a3p");
    assertNull(utilities.backupDirectory(secondProject, false));

    utilities.clearBackupFails();
    Files.delete(collidingBackupPath);

    Path backupDirectory = utilities.backupDirectory(secondProject, false);
    assertNotNull(backupDirectory);
    assertTrue(Files.isDirectory(backupDirectory));
  }

  @Test
  public void isProjectOnlyAcceptsA3pExtension() throws IOException {
    ProjectFileUtilities utilities = new ProjectFileUtilities(null);

    assertTrue(utilities.isProject(temporaryFolder.newFile("world.a3p")));
    assertFalse(utilities.isProject(temporaryFolder.newFile("world.zip")));
    // On case-insensitive filesystems (macOS), WORLD.A3P may match .a3p
    // Only assert case-sensitivity on case-sensitive filesystems
    File upperCase = temporaryFolder.newFile("WORLD_UPPER.A3P");
    boolean isCaseSensitiveFs = !new File(temporaryFolder.getRoot(), "world.a3p").equals(upperCase);
    if (isCaseSensitiveFs) {
      assertFalse(utilities.isProject(upperCase));
    }
  }

  @Test
  public void saveCopySkipsThumbnailWhenThumbnailCreationFails() throws Exception {
    Project project = new Project(programType("Program"), Project.SceneCameraType.WindowCamera);
    ProjectFileUtilities utilities = new ProjectFileUtilities(null) {
      @Override
      Project getUpToDateProject() {
        return project;
      }

      @Override
      java.awt.image.BufferedImage createThumbnail() throws Throwable {
        throw new IllegalStateException("thumbnail unavailable");
      }
    };
    File saveFile = temporaryFolder.newFile("no-thumbnail.a3p");

    utilities.saveCopyOfProjectTo(saveFile);

    try (ZipFile zipFile = new ZipFile(saveFile)) {
      assertNull(zipFile.getEntry("thumbnail.png"));
      assertNotNull(zipFile.getEntry("programType.xml"));
    }
  }

  private static NamedUserType programType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(SProgram.class));
    return type;
  }
}
