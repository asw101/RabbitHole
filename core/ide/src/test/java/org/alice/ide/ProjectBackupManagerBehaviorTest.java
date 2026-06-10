package org.alice.ide;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.alice.ide.ProjectFileUtilities.BACKUP_AUTO;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ProjectBackupManagerBehaviorTest {
  private Path backupDirectory;

  @Before
  public void setUp() throws IOException {
    this.backupDirectory = Path.of("target", "test-generated", "project-backup-manager-" + System.nanoTime());
    Files.createDirectories(this.backupDirectory);
  }

  @After
  public void tearDown() throws IOException {
    if (this.backupDirectory != null && Files.exists(this.backupDirectory)) {
      Files.walk(this.backupDirectory)
          .sorted((left, right) -> right.compareTo(left))
          .forEach(path -> {
            try {
              Files.deleteIfExists(path);
            } catch (IOException ioe) {
              throw new RuntimeException(ioe);
            }
          });
    }
  }

  @Test
  public void getSortedBackupsFiltersByPrefixAndReturnsNewestFirst() throws IOException {
    ProjectBackupManager manager = new ProjectBackupManager(null);
    File olderAuto = createBackup(BACKUP_AUTO + "20240102_120000.a3p");
    pauseForDistinctCreationTimes();
    createBackup("manual20240102_123000.a3p");
    pauseForDistinctCreationTimes();
    File newerAuto = createBackup(BACKUP_AUTO + "20240102_130000.a3p");

    File[] backups = manager.getSortedBackups(BACKUP_AUTO, this.backupDirectory.toFile());

    assertArrayEquals(new File[] {newerAuto, olderAuto}, backups);
  }

  @Test
  public void getNextBackupUsesOnlyAutoBackupsForHealthyProjectsButAllBackupsForCorruptedProjects() throws IOException {
    ProjectBackupManager manager = new ProjectBackupManager(null);
    File autoBackup = createBackup(BACKUP_AUTO + "20240102_120000.a3p");
    pauseForDistinctCreationTimes();
    File manualBackup = createBackup("manual20240102_130000.a3p");

    assertEquals(autoBackup, manager.getNextBackup(null, this.backupDirectory.toFile(), false, java.util.Set.of()));
    assertEquals(manualBackup, manager.getNextBackup(null, this.backupDirectory.toFile(), true, java.util.Set.of()));
  }

  private File createBackup(String name) throws IOException {
    Path path = this.backupDirectory.resolve(name);
    Files.writeString(path, name);
    return path.toFile();
  }

  private static void pauseForDistinctCreationTimes() {
    // Intentional real-time wait: backup ordering depends on filesystem creation timestamps.
    IdeTestWait.sleepForSemanticTime(1100, TimeUnit.MILLISECONDS,
        "distinct backup file creation timestamps");
  }
}
