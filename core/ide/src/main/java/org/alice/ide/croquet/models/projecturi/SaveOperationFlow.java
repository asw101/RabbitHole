package org.alice.ide.croquet.models.projecturi;

import edu.cmu.cs.dennisc.java.io.FileUtilities;

import java.io.File;
import java.io.IOException;

final class SaveOperationFlow {
  interface Context {
    File getCurrentFile();

    boolean isBackup();

    File getMainProjectFile();

    File getDefaultDirectory();

    File showSaveFileDialog(File directory, String filename, String extension);

    void showWaitCursor();

    void hideWaitCursor();

    void showError(String title, String message);

    void finish();

    void cancel();
  }

  interface PromptDecision {
    boolean isPromptNecessary(File file);
  }

  interface SaveAction {
    void save(File file) throws IOException;
  }

  private SaveOperationFlow() {
  }

  static void run(Context context, PromptDecision promptDecision, String extension, SaveAction saveAction) {
    File filePrevious = context.getCurrentFile();
    boolean isExceptionRaised = false;
    do {
      File fileNext;
      if (context.isBackup()) {
        File mainFile = context.getMainProjectFile();
        String newProjectName = "";

        if (mainFile != null) {
          newProjectName = FileUtilities.getBaseName(mainFile) + " Copy";
        }

        fileNext = context.showSaveFileDialog(context.getDefaultDirectory(), newProjectName, extension);
      } else if (isExceptionRaised || promptDecision.isPromptNecessary(filePrevious)) {
        fileNext = context.showSaveFileDialog(context.getDefaultDirectory(), FileUtilities.getBaseName(filePrevious), extension);
      } else {
        fileNext = filePrevious;
      }
      isExceptionRaised = false;
      if (fileNext != null) {
        try {
          context.showWaitCursor();
          saveAction.save(fileNext);
        } catch (IOException ioe) {
          isExceptionRaised = true;
          //TODO I18n
          context.showError("Unable to save file", ioe.getMessage());
        } finally {
          context.hideWaitCursor();
        }
        if (!isExceptionRaised) {
          context.finish();
        }
      } else {
        context.cancel();
      }
    } while (isExceptionRaised);
  }
}
