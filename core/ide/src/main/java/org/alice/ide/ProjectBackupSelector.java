package org.alice.ide;

import edu.cmu.cs.dennisc.java.io.FileUtilities;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Set;

final class ProjectBackupSelector {
  private final BackupTimeSource backupTimeSource;

  ProjectBackupSelector() {
    this(FileUtilities::getCreatedDateTime);
  }

  ProjectBackupSelector(BackupTimeSource backupTimeSource) {
    this.backupTimeSource = backupTimeSource;
  }

  File getNextBackup(LocalDateTime modifiedTime, File[] newestFirstBackups,
                     boolean isMainProjectCorrupted, Set<String> unloadableFiles) {
    for (File backup : newestFirstBackups) {
      if (!unloadableFiles.contains(backup.getName())) {
        // If the main project is corrupted, return the latest backup.
        if (isMainProjectCorrupted || modifiedTime == null || modifiedTime == LocalDateTime.MIN) {
          return backup;
        }

        LocalDateTime backupCreatedTime = backupTimeSource.getCreatedDateTime(backup);

        if (backupCreatedTime.isAfter(modifiedTime)) {
          return backup;
        } else {
          return null;
        }
      }
    }

    return null;
  }

  interface BackupTimeSource {
    LocalDateTime getCreatedDateTime(File file);
  }
}
