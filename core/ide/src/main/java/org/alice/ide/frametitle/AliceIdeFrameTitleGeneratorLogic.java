package org.alice.ide.frametitle;

import java.io.File;

final class AliceIdeFrameTitleGeneratorLogic {
  private AliceIdeFrameTitleGeneratorLogic() {
    throw new AssertionError();
  }

  static String generateTitle(String applicationName, String versionAdornment, File mainProjectFile, boolean isBackup, boolean isDocumentUpToDateWithUri) {
    StringBuilder sb = new StringBuilder();
    sb.append(applicationName);
    sb.append(" ");
    sb.append(versionAdornment);
    sb.append(" ");
    if (mainProjectFile != null) {
      sb.append(mainProjectFile);
    }
    if (isBackup || !isDocumentUpToDateWithUri) {
      sb.append('*');
    }
    return sb.toString();
  }
}
