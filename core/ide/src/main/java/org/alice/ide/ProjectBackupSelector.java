package org.alice.ide;

import edu.cmu.cs.dennisc.java.io.FileUtilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
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

  File getNextBackup(LocalDateTime modifiedTime, File backupDirectory, File[] newestFirstBackups,
                      boolean isMainProjectCorrupted, Set<String> unloadableFiles) {
    Path trustedBackupDirectory = trustedBackupDirectory(backupDirectory);
    if (trustedBackupDirectory == null) {
      return null;
    }
    for (File backup : newestFirstBackups) {
      if ((backup != null)
          && !unloadableFiles.contains(backup.getName())
          && isAvailableBackupCandidate(backup, trustedBackupDirectory)) {
        // If the main project is corrupted, return the latest backup.
        if (isMainProjectCorrupted || modifiedTime == null || LocalDateTime.MIN.equals(modifiedTime)) {
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

  private static boolean isAvailableBackupCandidate(File backup, Path trustedBackupDirectory) {
    if ((backup == null) || !isRegularBackupFile(backup)) {
      return false;
    }
    try {
      Path candidatePath = backup.toPath().toRealPath();
      return candidatePath.startsWith(trustedBackupDirectory) && isRegularBackupFile(backup);
    } catch (IOException ioe) {
      throw new IllegalStateException("Unable to validate backup candidate path: " + backup, ioe);
    }
  }

  private static boolean isRegularBackupFile(File backup) {
    try {
      return Files.readAttributes(
          backup.toPath(),
          BasicFileAttributes.class,
          LinkOption.NOFOLLOW_LINKS).isRegularFile();
    } catch (NoSuchFileException nsfe) {
      return false;
    } catch (IOException ioe) {
      throw new IllegalStateException("Unable to inspect backup candidate path: " + backup, ioe);
    }
  }

  private static Path trustedBackupDirectory(File backupDirectory) {
    if (backupDirectory == null) {
      return null;
    }
    try {
      BasicFileAttributes attributes = Files.readAttributes(
          backupDirectory.toPath(),
          BasicFileAttributes.class,
          LinkOption.NOFOLLOW_LINKS);
      if (!attributes.isDirectory()) {
        return null;
      }
      return backupDirectory.toPath().toRealPath();
    } catch (NoSuchFileException nsfe) {
      return null;
    } catch (IOException ioe) {
      throw new IllegalStateException("Unable to validate backup directory path: " + backupDirectory, ioe);
    }
  }

  interface BackupTimeSource {
    LocalDateTime getCreatedDateTime(File file);
  }
}
