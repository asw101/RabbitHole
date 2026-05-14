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

import edu.cmu.cs.dennisc.java.awt.CursorUtilities;
import edu.cmu.cs.dennisc.java.lang.ClassUtilities;
import edu.cmu.cs.dennisc.java.lang.SystemUtilities;
import edu.cmu.cs.dennisc.java.net.UriUtilities;
import edu.cmu.cs.dennisc.javax.swing.option.Dialogs;
import org.alice.ide.croquet.models.projecturi.BackupProjectOperation;
import org.alice.ide.frametitle.IdeFrameTitleGenerator;
import org.alice.ide.project.ProjectDocumentState;
import org.alice.ide.recentprojects.RecentProjectsListData;
import org.alice.ide.uricontent.UriProjectLoader;
import org.lgna.croquet.Group;
import org.lgna.croquet.PerspectiveApplication;
import org.lgna.croquet.history.UserActivity;
import org.lgna.croquet.undo.UndoHistory;
import org.lgna.croquet.undo.event.HistoryInsertionIndexEvent;
import org.lgna.project.ProgramTypeUtilities;
import org.lgna.project.Project;
import org.lgna.project.ProjectVersion;
import org.lgna.project.VersionNotSupportedException;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;

import javax.swing.RootPaneContainer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * @author Dennis Cosgrove
 */
public abstract class ProjectApplication extends PerspectiveApplication<ProjectDocumentFrame> {
  public static final Group HISTORY_GROUP = Group.getInstance(UUID.fromString("303e94ca-64ef-4e3a-b95c-038468c68438"), "HISTORY_GROUP");
  public static final Group URI_GROUP = Group.getInstance(UUID.fromString("79bf8341-61a4-4395-9469-0448e66d9ac6"), "URI_GROUP");


  public static ProjectApplication getActiveInstance() {
    return ClassUtilities.getInstance(PerspectiveApplication.getActiveInstance(), ProjectApplication.class);
  }

  private BackupProjectOperation backupProjectOperation;

  private UserActivity projectActivity;
  private final ProjectUndoRedoManager undoRedoManager;
  private final ProjectLoader projectLoader;
  private final ProjectBackupManager backupManager;

  public ProjectApplication(ApiConfigurationManager apiConfigurationManager) {
    this.projectFileUtilities = new ProjectFileUtilities(this);
    this.projectDocumentFrame = new ProjectDocumentFrame(apiConfigurationManager);
    this.undoRedoManager = new ProjectUndoRedoManager(this);
    this.projectLoader = new ProjectLoader(this);
    this.backupManager = new ProjectBackupManager(this);
    this.updateTitle();
  }

  ProjectApplication(ProjectDocumentFrame projectDocumentFrame) {
    this.projectFileUtilities = new ProjectFileUtilities(this);
    this.projectDocumentFrame = projectDocumentFrame;
    this.undoRedoManager = new ProjectUndoRedoManager(this);
    this.projectLoader = new ProjectLoader(this);
    this.backupManager = new ProjectBackupManager(this);
    this.updateTitle();
  }

  BackupProjectOperation getBackupProjectOperation() {
    if (backupProjectOperation == null) {
      backupProjectOperation = new BackupProjectOperation();
    }
    return backupProjectOperation;
  }

  ProjectUndoRedoManager getHistoryManager() {
    return undoRedoManager;
  }

  ProjectLoader getProjectLoader() {
    return projectLoader;
  }

  ProjectBackupManager getBackupManager() {
    return backupManager;
  }

  @Override
  public ProjectDocumentFrame getDocumentFrame() {
    return this.projectDocumentFrame;
  }

  private void updateUndoRedoEnabled() {
    undoRedoManager.updateUndoRedoEnabled();
  }

  protected void handleInsertionIndexChanged(HistoryInsertionIndexEvent e) {
    this.updateTitle();
    UndoHistory source = e.getTypedSource();
    if (source.getGroup() == PROJECT_GROUP) {
      this.updateUndoRedoEnabled();
    }
  }

  public static String getApplicationName() {
    return "Alice";
  }

  public static String getVersionAdornment() {
    return String.valueOf(ProjectVersion.getCurrentVersion().getAliceIdentifier());
  }

  @Override
  public String getApplicationSubPath() {
    String rv = getApplicationName();
    if ("Alice".equals(rv)) {
      rv = "Alice3";
    }
    return rv.replaceAll(" ", "");
  }

  public void handleVersionNotSupported(File file, VersionNotSupportedException vnse) {
    Dialogs.showUnableToOpenFileDialog(file, "%s is not backwards compatible with:\n    File Version: %s\n    (Minimum Supported Version: %s)".formatted(getApplicationName(), vnse.getVersion(), vnse.getMinimumSupportedVersion()));
  }

  private UriProjectLoader uriProjectLoader;

  public final URI getUri() {
    return this.uriProjectLoader != null ? this.uriProjectLoader.getUri() : null;
  }

  public final boolean isNewProject() {
    return this.uriProjectLoader != null && this.uriProjectLoader.isNewProject();
  }

  public final boolean isBackup() {
    return this.uriProjectLoader != null && this.uriProjectLoader.isBackup();
  }

  public final File getMainProjectFile() {
    return this.uriProjectLoader != null ? this.uriProjectLoader.getMainProjectFile() : null;
  }

  @Deprecated
  final UndoHistory getProjectHistory() {
    return this.getProjectHistory(PROJECT_GROUP);
  }

  @Deprecated
  final UndoHistory getProjectHistory(Group group) {
    if (this.getDocument() == null) {
      return null;
    } else {
      return this.getDocument().getUndoHistory(group);
    }
  }

  boolean hasUriProjectLoaderThatShouldBeSaved() {
    return uriProjectLoader != null && uriProjectLoader.shouldBeSaved();
  }

  public boolean isProjectUpToDateWithFile() {
    return undoRedoManager.isProjectUpToDateWithFile();
  }

  protected boolean isProjectUpToDateWithSceneSetUp() {
    return undoRedoManager.isProjectUpToDateWithSceneSetUp();
  }

  protected boolean isProjectUpToDateWithBackups() {
    return undoRedoManager.isProjectUpToDateWithBackups();
  }

  private void updateHistoryIndexFileSync() {
    undoRedoManager.updateHistoryIndexFileSync();
  }

  protected void updateHistoryIndexSceneSetUpSync() {
    undoRedoManager.updateHistoryIndexSceneSetUpSync();
  }

  private void updateHistoryIndexBackupSync() {
    undoRedoManager.updateHistoryIndexBackupSync();
  }

  private IdeFrameTitleGenerator frameTitleGenerator;

  protected abstract IdeFrameTitleGenerator createFrameTitleGenerator();

  protected void updateTitle() {
    if (frameTitleGenerator == null) {
      this.frameTitleGenerator = this.createFrameTitleGenerator();
    }
    ProjectDocumentFrame documentFrame = Objects.requireNonNull(this.getDocumentFrame(),
        "ProjectApplication requires documentFrame before updating title");
    documentFrame.getFrame().setTitle(this.frameTitleGenerator.generateTitle(uriProjectLoader, isProjectUpToDateWithFile()));
  }

  private ProjectDocument getDocument() {
    return ProjectDocumentState.getInstance().getValue();
  }

  private void setDocument(ProjectDocument document) {
    ProjectDocumentState.getInstance().setValueTransactionlessly(document);
  }

  public Project getProject() {
    ProjectDocument document = this.getDocument();
    return document != null ? document.getProject() : null;
  }

  public void setProject(Project project) {
    // TODO I18N
    StringBuilder sb = new StringBuilder();
    Set<NamedUserType> types = project.getNamedUserTypes();
    for (NamedUserType type : types) {
      boolean wasNullMethodRemoved = false;
      ListIterator<UserMethod> methodIterator = type.getDeclaredMethods().listIterator();
      while (methodIterator.hasNext()) {
        UserMethod method = methodIterator.next();
        if (method == null) {
          methodIterator.remove();
          wasNullMethodRemoved = true;
        }
      }
      boolean wasNullFieldRemoved = false;
      ListIterator<UserField> fieldIterator = type.getDeclaredFields().listIterator();
      while (fieldIterator.hasNext()) {
        UserField field = fieldIterator.next();
        if (field == null) {
          fieldIterator.remove();
          wasNullFieldRemoved = true;
        }
      }
      if (wasNullMethodRemoved) {
        if (sb.length() > 0) {
          sb.append("\n");
        }
        sb.append("null method was removed from ");
        sb.append(type.getName());
        sb.append(".");
      }
      if (wasNullFieldRemoved) {
        if (sb.length() > 0) {
          sb.append("\n");
        }
        sb.append("null field was removed from ");
        sb.append(type.getName());
        sb.append(".");
      }
    }
    if (sb.length() > 0) {
      Dialogs.showWarning("A Problem With Your Project Has Been Fixed", sb.toString());
    }
    String typeCheck = ProgramTypeUtilities.sanityCheckAllTypes(project);
    if (typeCheck.length() > 0) {
      Dialogs.showError("Problems With Your Project Were Not Fixed", "These may cause errors when editing or running.\nProceed with caution.\n\n" + typeCheck);
    }
    this.setDocument(new ProjectDocument(project, newProjectActivity()));
  }

  UserActivity newProjectActivity() {
    // If present, this is the activity of opening or creating a project
    UserActivity openChild = getOpenActivity();
    if (openChild != null) {
      openChild.finish();
    }
    // If there was a project the new one replaces it.
    if (projectActivity != null) {
      projectActivity.finish();
    }
    // Create a new project activity under the top level user activity
    projectActivity = getOverallUserActivity().newChildActivity();
    return projectActivity;
  }

  public UserActivity getProjectUserActivity() {
    return getDocument().getUserActivity();
  }

  //Look for an open child, if any. Otherwise, return null.
  @Override
  public UserActivity getOpenActivity() {
    UserActivity latest = super.getOpenActivity();
    return latest == projectActivity ? null : latest;
  }

  public final void loadProject(UserActivity activity, UriProjectLoader uriProjectLoader) {
    projectLoader.loadProject(activity, uriProjectLoader);
  }

  void setUriProjectLoader(UriProjectLoader loader) {
    this.uriProjectLoader = loader;
  }

  UriProjectLoader getUriProjectLoader() {
    return this.uriProjectLoader;
  }

  ProjectFileUtilities getProjectFileUtilities() {
    return this.projectFileUtilities;
  }

  protected boolean loadNewProjectBackup() {
    return backupManager.loadNewProjectBackup();
  }

  protected File[] getSortedBackups(final String type, File backupDir) {
    return backupManager.getSortedBackups(type, backupDir);
  }

  void updateInterfaceAfterLoad() {
    if (SystemUtilities.isMac()) {
      getDocumentFrame().getFrame().rebuildMenuBar();
    }
    URI uri = getUri();
    File file = UriUtilities.getFile(uri);
    try {
      if ((file != null) && file.canWrite()) {
        RecentProjectsListData.getInstance().handleOpen(file);
      }
    } catch (Throwable throwable) {
      edu.cmu.cs.dennisc.java.util.logging.Logger.throwable(throwable, file);
    }

    updateHistoryIndexFileSync();
    updateUndoRedoEnabled();

    projectFileUtilities.clearBackupFails();
    projectFileUtilities.startAutoSaving();
  }

  protected abstract BufferedImage createThumbnail() throws Throwable;

  public final void saveProjectTo(File file) throws IOException {
    ProjectSaveTargetPlan saveTargetPlan = ProjectSaveTargetPlan.choose(uriProjectLoader, file);
    UriProjectLoader previousLoader = uriProjectLoader;

    if (saveTargetPlan.shouldCopyDefaultBackupDirectory()) {
      projectFileUtilities.copyDefaultBackupDirectory(file);
    }

    uriProjectLoader = saveTargetPlan.getNextLoader();

    //    long startTime = System.currentTimeMillis();

    try {
      projectFileUtilities.saveProjectTo(file, saveTargetPlan.isBackupSave());
    } catch (IOException e) {
      uriProjectLoader = previousLoader;
      throw e;
    }

    if (saveTargetPlan.shouldCopyDefaultBackupDirectory()) {
      updateInterfaceAfterLoad();
    }

    //    long endTime = System.currentTimeMillis();
    //    double saveTime = ( endTime - startTime ) * .001;
    //    System.out.println( "Save time: " + saveTime );
    RecentProjectsListData.getInstance().handleSave(file);

    this.updateHistoryIndexFileSync();
  }

  public void backupActiveProject() {
    try {
      projectFileUtilities.backupActiveProject();
    } catch (IOException e) {
      edu.cmu.cs.dennisc.java.util.logging.Logger.throwable(e, "Unable to backup project.");
    }
  }

  public final void updateBackupIndexAndSaveProjectTo(File file) throws IOException {
    projectFileUtilities.saveCopyOfProjectTo(file);

    updateHistoryIndexBackupSync();
  }

  public final void exportProjectTo(File file) throws IOException {
    projectFileUtilities.exportCopyOfProjectTo(file);
  }

  final Project getForcedUpToDateProject() {
    forceProjectCodeUpToDate();
    return getProject();
  }

  public abstract void forceProjectCodeUpToDate();

  public final Project getUpToDateProject() {
    ensureProjectCodeUpToDate();
    return getProject();
  }

  public abstract void ensureProjectCodeUpToDate();

  public void showWaitCursor() {
    CursorUtilities.pushAndSetRootWait(getRootComponent());
  }

  public void hideWaitCursor() {
    CursorUtilities.popAndSetRoot(getRootComponent());
  }

  private RootPaneContainer getRootComponent() {
    return getDocumentFrame().getFrame().getAwtComponent();
  }

  private final ProjectDocumentFrame projectDocumentFrame;
  private final ProjectFileUtilities projectFileUtilities;

  public String getAuthorName() {
    return getPreferencesManager().getValue("authorName", System.getProperty("user.name"));
  }

  public void setAuthorName(String newName) {
    getPreferencesManager().setValue("authorName", newName);
  }
}
