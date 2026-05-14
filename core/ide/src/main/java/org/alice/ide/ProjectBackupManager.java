/*******************************************************************************
 * Copyright (c) 2006, 2015, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice", nor may
 *    "Alice" appear in their name, without prior written permission of
 *    Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgement: "This product includes software
 *    developed by Carnegie Mellon University"
 *
 * 5. The gallery of art assets and animations provided with this software is
 *    contributed by Electronic Arts Inc. and may be used for personal,
 *    non-commercial, and academic use only. Redistributions of any program
 *    source code that utilizes The Sims 2 Assets must also retain the copyright
 *    notice, list of conditions and the disclaimer contained in
 *    The Alice 3.0 Art Gallery License.
 *
 * DISCLAIMER:
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 * ANY AND ALL EXPRESS, STATUTORY OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, AND NON-INFRINGEMENT ARE DISCLAIMED. IN NO EVENT
 * SHALL THE AUTHORS, COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING FROM OR OTHERWISE RELATING TO
 * THE USE OF OR OTHER DEALINGS WITH THE SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

package org.alice.ide;

import edu.cmu.cs.dennisc.java.io.FileUtilities;
import edu.cmu.cs.dennisc.java.util.logging.Logger;
import edu.cmu.cs.dennisc.javax.swing.option.Dialogs;
import edu.cmu.cs.dennisc.javax.swing.option.YesNoCancelResult;
import org.alice.ide.croquet.models.projecturi.BackupProjectOperation;
import org.alice.ide.uricontent.FileProjectLoader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import static edu.cmu.cs.dennisc.java.io.FileUtilities.listFiles;
import static org.alice.ide.ProjectFileUtilities.BACKUP_AUTO;

/**
 * Manages project backup operations including loading backups,
 * creating projects from backups, and sorting backup files
 * for {@link ProjectApplication}. Extracted to reduce class size.
 */
final class ProjectBackupManager {
  private final ProjectApplication application;
  private final ProjectBackupSelector projectBackupSelector = new ProjectBackupSelector();

  ProjectBackupManager(ProjectApplication application) {
    this.application = application;
  }

  boolean loadNewProjectBackup() {
    Path backupPath = application.getProjectFileUtilities().defaultBackupDirectory();
    if (backupPath == null) {
      return false;
    }

    File backupDir = backupPath.toFile();
    File backup = getNextBackup(null, backupDir, false, new HashSet<>());
    if (backup == null) {
      return false;
    }

    YesNoCancelResult result = application.getBackupProjectOperation().showUnsavedBackupProjectOpenedDialog();

    return switch (result) {
      case YES -> {
        application.getProjectLoader().loadProject(application.newProjectActivity(), new FileProjectLoader(backup, false), true, true, new HashSet<>());

        yield true;
      }
      case NO -> {
        try {
          FileUtils.deleteDirectory(backupDir);
        } catch (IOException e) {
          Logger.throwable(e, "Unable to delete default backup directory.");
        }

        yield false;
      }
      case CANCEL ->
        false;
    };
  }

  void createProjectFromBackup(File backup, File original, boolean isMainProjectCorrupted) {
    YesNoCancelResult result = application.getBackupProjectOperation().showBackupProjectOpenedDialog(original.getName(), backup.getName(), isMainProjectCorrupted);
    ProjectBackupAdoptionPlan plan = ProjectBackupAdoptionPlan.afterUserChoice(result);

    switch (plan.getAction()) {
      case SAVE_BACKUP_TO_ORIGINAL_PROJECT -> {
        try {
          application.saveProjectTo(original);
        } catch (IOException ioe) {
          Dialogs.showError("Unable to save file", ioe.getMessage());
        }
      }
      case KEEP_BACKUP_AS_CURRENT_PROJECT -> {
        // Do nothing for now
        // When the user saves, a new project will be created from this one
      }
      case RELOAD_ORIGINAL_PROJECT -> {
        application.getProjectLoader().loadProject(application.newProjectActivity(), new FileProjectLoader(original, application.getUriProjectLoader().shouldMakeVrReady()));
      }
    }
  }

  File getNextBackup(LocalDateTime modifiedTime, File backupDir, boolean isMainProjectCorrupted, Set<String> unloadableFiles) {
    if (backupDir == null) {
      return null;
    }

    String typeFilter = isMainProjectCorrupted ? "" : BACKUP_AUTO;
    File[] backups = getSortedBackups(typeFilter, backupDir);

    return projectBackupSelector.getNextBackup(modifiedTime, backupDir, backups, isMainProjectCorrupted, unloadableFiles);
  }

  File[] getSortedBackups(final String type, File backupDir) {
    File[] backups = listFiles(backupDir, file -> file.isFile() && file.getName().startsWith(type));

    Arrays.sort(backups, new Comparator<File>() {
      @Override
      public int compare(final File f1, final File f2) {
        LocalDateTime f1Creation = FileUtilities.getCreatedDateTime(f1);
        LocalDateTime f2Creation = FileUtilities.getCreatedDateTime(f2);
        return f1Creation.compareTo(f2Creation);
      }
    });

    Collections.reverse(Arrays.asList(backups));

    return backups;
  }
}
