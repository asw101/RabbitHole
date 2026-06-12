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
import edu.cmu.cs.dennisc.java.net.UriUtilities;
import edu.cmu.cs.dennisc.java.util.logging.Logger;
import edu.cmu.cs.dennisc.javax.swing.option.Dialogs;
import org.alice.ide.declarationseditor.TypeMenu;
import org.alice.ide.instancefactory.croquet.InstanceFactoryFillIn;
import org.alice.ide.uricontent.FileProjectLoader;
import org.alice.ide.uricontent.ProjectLoadOutcome;
import org.alice.ide.uricontent.UriProjectLoader;
import org.lgna.croquet.CancelException;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.Project;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages project loading, error handling, and success workflows
 * for {@link ProjectApplication}. Extracted to reduce class size.
 */
final class ProjectLoader {
  private final ProjectApplication application;

  ProjectLoader(ProjectApplication application) {
    this.application = application;
  }

  void loadProject(UserActivity activity, UriProjectLoader uriProjectLoader) {
    loadProject(activity, uriProjectLoader, false, false, new HashSet<>());
  }

  void loadProject(UserActivity activity, UriProjectLoader uriProjectLoader, boolean isLoadingBackups,
                   boolean isMainProjectCorrupted, Set<String> unloadableFiles) {
    application.setUriProjectLoader(uriProjectLoader);
    if (uriProjectLoader != null) {
      application.showWaitCursor();
      cleanupForNextProject();
      uriProjectLoader.deliverLoadOutcomeOnEventDispatchThread(outcome -> {
        try {
          projectLoaded(activity, outcome, isLoadingBackups, isMainProjectCorrupted, unloadableFiles);
        } catch (RuntimeException re) {
          handleProjectLoadException(re, activity);
        } finally {
          application.hideWaitCursor();
        }
      });
    }
  }

  private void cleanupForNextProject() {
    TypeMenu.reset();
    InstanceFactoryFillIn.reset();
  }

  private void projectLoaded(UserActivity activity, ProjectLoadOutcome outcome, boolean isLoadingBackups,
                             boolean isMainProjectCorrupted, Set<String> unloadableFiles) {
    if (outcome == null) {
      throw new IllegalStateException("Project loader returned no load outcome.");
    }

    RuntimeException loadException = getRuntimeException(outcome);
    if (loadException != null) {
      throw loadException;
    }

    boolean failed = outcome.getKind() == ProjectLoadOutcome.Kind.FAILURE;
    File saved = UriUtilities.getFile(application.getUri());
    File projectFile = projectFileForOutcome(outcome, saved);

    if (projectFile != null && !application.getProjectFileUtilities().isProject(projectFile)) {
      return;
    }

    boolean isBackup = application.getUriProjectLoader().isBackup();

    if (failed) {
      if (projectFile == null) {
        throw new IllegalStateException("Project load failed without a project file.");
      }
      if (!shouldUseBackupRecovery(application.getUriProjectLoader().isNewProject())) {
        application.setUriProjectLoader(null);
        activity.cancel();
        showNewProjectOperation();
        return;
      }
      handleProjectLoadError(projectFile, activity, isBackup, isLoadingBackups, isMainProjectCorrupted, unloadableFiles);
    } else {
      handleProjectLoadSuccess(outcome.getProject(), projectFile, activity, isBackup, isLoadingBackups, isMainProjectCorrupted, unloadableFiles);
    }
  }

  static File projectFileForOutcome(ProjectLoadOutcome outcome, File saved) {
    if (outcome.getKind() == ProjectLoadOutcome.Kind.FAILURE) {
      return outcome.getFile() != null ? outcome.getFile() : saved;
    }
    return saved != null ? saved : outcome.getFile();
  }

  static boolean shouldUseBackupRecovery(boolean isNewProject) {
    return !isNewProject;
  }

  private RuntimeException getRuntimeException(ProjectLoadOutcome outcome) {
    Exception exception = outcome.getException();
    return outcome.getStatus() == ProjectLoadOutcome.Status.RUNTIME_EXCEPTION && exception instanceof RuntimeException
        ? (RuntimeException) exception
        : null;
  }

  private void handleProjectLoadError(File projectFile, UserActivity activity, boolean isBackup,
                                     boolean isLoadingBackups, boolean isMainProjectCorrupted,
                                      Set<String> unloadableFiles) {
    Path backupPath = application.getProjectFileUtilities().appropriateBackupDirectory(projectFile);
    File backupDir = backupPath != null
            ? backupPath.toFile()
            : null;

    boolean makeVrReady = application.getUriProjectLoader().shouldMakeVrReady();
    boolean isDefaultBackup = application.getUriProjectLoader().isDefaultBackup();

    unloadableFiles.add(projectFile.getName());

    if (!isLoadingBackups) {
      isMainProjectCorrupted = true;
    }

    File mainProject = application.getUriProjectLoader().getMainProjectFile();
    LocalDateTime projectModifiedTime = mainProject != null
        ? FileUtilities.getModifiedDateTime(mainProject)
        : null;
    File backup = application.getBackupManager().getNextBackup(projectModifiedTime, backupDir, isMainProjectCorrupted, unloadableFiles);

    application.setUriProjectLoader(null);
    activity.cancel();

    ProjectLoadFailurePlan plan = ProjectLoadFailurePlan.choose(
        isBackup, isLoadingBackups, isMainProjectCorrupted, isDefaultBackup, backup, projectFile);

    ProjectLoadFailurePlan.Action failureAction = plan.getAction();
    boolean accepted = false;
    switch (failureAction) {
      case SHOW_BACKUP_LOAD_ERROR -> application.getBackupProjectOperation().showBackupLoadErrorDialog();
      case PROMPT_LOAD_BACKUP -> {
        accepted = application.getBackupProjectOperation().showProjectLoadErrorAndLoadBackupDialog(
            projectDisplayName(mainProject, projectFile), plan.getFailedBackupName(), isBackup);
      }
      case SHOW_UNSAVED_BACKUPS_LOAD_ERROR -> {
        application.getBackupProjectOperation().showUnsavedBackupsLoadErrorDialog();
      }
      case SHOW_PROJECT_AND_ALL_BACKUPS_LOAD_ERROR -> {
        application.getBackupProjectOperation().showProjectAndAllBackupsLoadErrorDialog(projectDisplayName(mainProject, projectFile));
      }
      case PROMPT_LOAD_MAIN_PROJECT -> {
        accepted = application.getBackupProjectOperation().showProjectLoadRecentBackupsErrorAndLoadMainDialog(projectFile.getName());
      }
    }

    ProjectLoadFailureDispatchPlan dispatch = ProjectLoadFailureDispatchPlan.afterUserChoice(failureAction, accepted);
    if (dispatch.getLoadTarget() == ProjectLoadFailureDispatchPlan.LoadTarget.BACKUP) {
      loadProject(
          application.newProjectActivity(),
          new FileProjectLoader(plan.getBackupToLoad(), makeVrReady),
          true,
          isMainProjectCorrupted,
          unloadableFiles);
    } else if (dispatch.getLoadTarget() == ProjectLoadFailureDispatchPlan.LoadTarget.MAIN_PROJECT) {
      if (mainProject != null) {
        loadProject(
            application.newProjectActivity(),
            new FileProjectLoader(mainProject, makeVrReady),
            false,
            isMainProjectCorrupted,
            unloadableFiles);
      }
    }

    if (dispatch.shouldShowNewProject()) {
      showNewProjectOperation();
    }
  }

  private String projectDisplayName(File preferred, File fallback) {
    File displayFile = preferred != null ? preferred : fallback;
    return displayFile != null ? displayFile.getName() : "project";
  }

  private void handleProjectLoadSuccess(Project project, File projectFile, UserActivity activity, boolean isBackup,
                                        boolean isLoadingBackups, boolean isMainProjectCorrupted,
                                        Set<String> unloadableFiles) {
    boolean isDefaultBackup = application.getUriProjectLoader().isDefaultBackup();

    updateInterface(project);

    ProjectLoadSuccessPlan plan = ProjectLoadSuccessPlan.choose(
        isBackup,
        isLoadingBackups,
        isDefaultBackup,
        application.getUriProjectLoader().isNewProject(),
        !unloadableFiles.isEmpty());

    if (plan.shouldCheckForMoreRecentBackups()) {
      Path backupPath = application.getProjectFileUtilities().appropriateBackupDirectory(projectFile);
      if (backupPath != null) {
        File backupDir = backupPath.toFile();

        LocalDateTime projectModifiedTime = FileUtilities.getModifiedDateTime(projectFile);

        File backup = application.getBackupManager().getNextBackup(projectModifiedTime, backupDir, false, unloadableFiles);

        if (backup != null && application.getBackupProjectOperation().showMoreRecentBackupsDialog()) {
          loadProject(application.newProjectActivity(), new FileProjectLoader(backup, application.getUriProjectLoader().shouldMakeVrReady()), true, isMainProjectCorrupted, unloadableFiles);

          return;
        }
      }
    }

    if (plan.shouldCreateProjectFromBackup()) {
      application.getBackupManager().createProjectFromBackup(projectFile, application.getUriProjectLoader().getMainProjectFile(), isMainProjectCorrupted);
    }
  }

  void handleProjectLoadException(RuntimeException re, UserActivity activity) {
    URI uri = null;
    try {
      uri = application.getUri();
    } catch (RuntimeException uriException) {
      re.addSuppressed(uriException);
    }
    var message = new StringBuilder("Errors reported in " + uri);
    Throwable cause = re;
    Logger.throwable(re, uri);
    do {
      var causeMessage = cause.getLocalizedMessage();
      if (causeMessage != null) {
        message.append("\n\n  ").append(causeMessage);
      }
      cause = cause.getCause();
    } while (cause != null);
    application.setUriProjectLoader(null);
    activity.cancel(new CancelException(re));
    Dialogs.showError("Unable to Load Project", message.toString());
    showNewProjectOperation();
  }

  void showNewProjectOperation() {
    application.setPerspective(application.getDocumentFrame().getNoProjectPerspective());
    UserActivity newActivity = application.getOverallUserActivity().getLatestActivity().newChildActivity();
    application.getDocumentFrame().getNewProjectOperation().fire(newActivity);
  }

  void updateInterface(Project project) {
    if ((application.getProject() != null) && (application.getProjectHistory() != null)) {
      application.getProjectHistory().removeHistoryListener(application.getHistoryManager().getProjectHistoryListener());
    }
    application.setProject(project);
    application.getProjectHistory().addHistoryListener(application.getHistoryManager().getProjectHistoryListener());
    application.updateInterfaceAfterLoad();
  }
}
