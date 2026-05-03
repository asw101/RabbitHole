package org.alice.ide;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
}
