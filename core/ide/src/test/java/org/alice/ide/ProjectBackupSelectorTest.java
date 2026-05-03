package org.alice.ide;

import org.junit.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class ProjectBackupSelectorTest {
  private static final LocalDateTime PROJECT_MODIFIED_TIME = LocalDateTime.of(2024, 1, 2, 12, 0);

  @Test
  public void corruptedMainProjectUsesLatestAvailableBackup() {
    File newest = backup("auto20240102_130000.a3p");
    File older = backup("auto20240102_120000.a3p");
    ProjectBackupSelector selector = new ProjectBackupSelector(file -> {
      throw new AssertionError("corrupted main project should not compare backup times");
    });

    File backup = selector.getNextBackup(PROJECT_MODIFIED_TIME, new File[] {newest, older}, true, Set.of());

    assertEquals(newest, backup);
  }

  @Test
  public void skipsBackupsAlreadyKnownToBeUnloadable() {
    File newest = backup("auto20240102_130000.a3p");
    File older = backup("auto20240102_120000.a3p");
    ProjectBackupSelector selector = new ProjectBackupSelector(file -> {
      throw new AssertionError("corrupted main project should not compare backup times");
    });

    File backup = selector.getNextBackup(PROJECT_MODIFIED_TIME, new File[] {newest, older}, true, Set.of(newest.getName()));

    assertEquals(older, backup);
  }

  @Test
  public void recentBackupProbeUsesLatestBackupOnlyWhenNewerThanProject() {
    File newest = backup("auto20240102_130000.a3p");
    ProjectBackupSelector selector = new ProjectBackupSelector(timeSource(Map.of(newest.getName(), PROJECT_MODIFIED_TIME.plusMinutes(10))));

    File backup = selector.getNextBackup(PROJECT_MODIFIED_TIME, new File[] {newest}, false, Set.of());

    assertEquals(newest, backup);
  }

  @Test
  public void recentBackupProbeStopsWhenLatestCandidateIsNotNewerThanProject() {
    File newest = backup("auto20240102_110000.a3p");
    File older = backup("auto20240102_100000.a3p");
    ProjectBackupSelector selector = new ProjectBackupSelector(file -> {
      assertEquals(newest.getName(), file.getName());
      return PROJECT_MODIFIED_TIME.minusMinutes(10);
    });

    File backup = selector.getNextBackup(PROJECT_MODIFIED_TIME, new File[] {newest, older}, false, Set.of());

    assertNull(backup);
  }

  @Test
  public void recentBackupProbeSkipsUnloadableNewestAndUsesNextOnlyWhenNewerThanProject() {
    File unloadableNewest = backup("auto20240102_140000.a3p");
    File next = backup("auto20240102_130000.a3p");
    ProjectBackupSelector selector = new ProjectBackupSelector(file -> {
      assertEquals(next.getName(), file.getName());
      return PROJECT_MODIFIED_TIME.plusMinutes(10);
    });

    File backup = selector.getNextBackup(
        PROJECT_MODIFIED_TIME,
        new File[] {unloadableNewest, next},
        false,
        Set.of(unloadableNewest.getName()));

    assertEquals(next, backup);
  }

  @Test
  public void recentBackupProbeSkipsUnloadableNewestAndStopsWhenNextIsNotNewerThanProject() {
    File unloadableNewest = backup("auto20240102_140000.a3p");
    File older = backup("auto20240102_110000.a3p");
    ProjectBackupSelector selector = new ProjectBackupSelector(file -> {
      assertEquals(older.getName(), file.getName());
      return PROJECT_MODIFIED_TIME.minusMinutes(10);
    });

    File backup = selector.getNextBackup(
        PROJECT_MODIFIED_TIME,
        new File[] {unloadableNewest, older},
        false,
        Set.of(unloadableNewest.getName()));

    assertNull(backup);
  }

  @Test
  public void missingMainProjectTimestampUsesLatestAvailableBackup() {
    File newest = backup("auto20240102_130000.a3p");
    ProjectBackupSelector selector = new ProjectBackupSelector(file -> {
      throw new AssertionError("missing main project timestamp should not compare backup times");
    });

    File backup = selector.getNextBackup(LocalDateTime.MIN, new File[] {newest}, false, Set.of());

    assertEquals(newest, backup);
  }

  @Test
  public void returnsNullWhenNoBackupCandidatesRemain() {
    File newest = backup("auto20240102_130000.a3p");
    ProjectBackupSelector selector = new ProjectBackupSelector(timeSource(Map.of(newest.getName(), PROJECT_MODIFIED_TIME.plusMinutes(10))));

    File backup = selector.getNextBackup(PROJECT_MODIFIED_TIME, new File[] {newest}, false, Set.of(newest.getName()));

    assertNull(backup);
  }

  private static File backup(String name) {
    return new File(name);
  }

  private static ProjectBackupSelector.BackupTimeSource timeSource(Map<String, LocalDateTime> createdTimes) {
    return file -> createdTimes.get(file.getName());
  }
}
