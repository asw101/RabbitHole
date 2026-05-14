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

import org.lgna.croquet.Application;
import org.lgna.croquet.Group;
import org.lgna.croquet.undo.UndoHistory;
import org.lgna.croquet.undo.event.HistoryClearEvent;
import org.lgna.croquet.undo.event.HistoryInsertionIndexEvent;
import org.lgna.croquet.undo.event.HistoryListener;
import org.lgna.croquet.undo.event.HistoryPushEvent;

/**
 * Manages undo/redo history tracking and index synchronization
 * for {@link ProjectApplication}. Extracted to reduce class size.
 */
final class ProjectUndoRedoManager {
  //todo: investigate
  private static final int PROJECT_HISTORY_INDEX_IF_PROJECT_HISTORY_IS_NULL = 0;

  private final ProjectApplication application;
  private final HistoryListener projectHistoryListener;

  private int projectHistoryIndexFile = 0;
  private int projectHistoryIndexSceneSetUp = 0;
  private int projectHistoryIndexBackups = 0;

  ProjectUndoRedoManager(ProjectApplication application) {
    this.application = application;
    this.projectHistoryListener = createProjectHistoryListener();
  }

  HistoryListener getProjectHistoryListener() {
    return projectHistoryListener;
  }

  private HistoryListener createProjectHistoryListener() {
    return new HistoryListener() {
      @Override
      public void operationPushing(HistoryPushEvent e) {
      }

      @Override
      public void operationPushed(HistoryPushEvent e) {
      }

      @Override
      public void insertionIndexChanging(HistoryInsertionIndexEvent e) {
      }

      @Override
      public void insertionIndexChanged(HistoryInsertionIndexEvent e) {
        application.handleInsertionIndexChanged(e);
      }

      @Override
      public void clearing(HistoryClearEvent e) {
      }

      @Override
      public void cleared(HistoryClearEvent e) {
      }
    };
  }

  void updateUndoRedoEnabled() {
    UndoHistory historyManager = application.getProjectHistory(Application.PROJECT_GROUP);
    boolean isUndoEnabled;
    boolean isRedoEnabled;
    if (historyManager != null) {
      int index = historyManager.getInsertionIndex();
      int size = historyManager.getStack().size();
      isUndoEnabled = index > 0;
      isRedoEnabled = index < size;
    } else {
      isUndoEnabled = false;
      isRedoEnabled = false;
    }

    ProjectDocumentFrame documentFrame = application.getDocumentFrame();
    documentFrame.getUndoOperation().setEnabled(isUndoEnabled);
    documentFrame.getRedoOperation().setEnabled(isRedoEnabled);
  }

  private boolean isProjectUpToDateWith(int index) {
    UndoHistory history = application.getProjectHistory();
    return history == null || index == history.getInsertionIndex();
  }

  boolean isProjectUpToDateWithFile() {
    return (!application.hasUriProjectLoaderThatShouldBeSaved())
            && isProjectUpToDateWith(projectHistoryIndexFile);
  }

  boolean isProjectUpToDateWithSceneSetUp() {
    return isProjectUpToDateWith(projectHistoryIndexSceneSetUp);
  }

  boolean isProjectUpToDateWithBackups() {
    return isProjectUpToDateWith(projectHistoryIndexBackups);
  }

  private int getHistoryIndex() {
    UndoHistory history = application.getProjectHistory();
    return history != null ? history.getInsertionIndex() : PROJECT_HISTORY_INDEX_IF_PROJECT_HISTORY_IS_NULL;
  }

  void updateHistoryIndexFileSync() {
    this.projectHistoryIndexFile = getHistoryIndex();
    this.updateHistoryIndexSceneSetUpSync();
    application.updateTitle();
  }

  void updateHistoryIndexSceneSetUpSync() {
    this.projectHistoryIndexSceneSetUp = getHistoryIndex();
  }

  void updateHistoryIndexBackupSync() {
    this.projectHistoryIndexBackups = getHistoryIndex();
  }
}
