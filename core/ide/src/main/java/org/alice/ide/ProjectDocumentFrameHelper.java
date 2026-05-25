package org.alice.ide;

import org.lgna.project.ast.AbstractCode;
import org.lgna.project.ast.AbstractDeclaration;

final class ProjectDocumentFrameHelper {
  private ProjectDocumentFrameHelper() {
    throw new AssertionError();
  }

  static boolean shouldRegisterRectangleCapture(boolean isFrameWindow) {
    return !isFrameWindow;
  }

  static AbstractCode getFocusedCode(AbstractDeclaration declaration) {
    return declaration instanceof AbstractCode code ? code : null;
  }

  static boolean canEnableRendering(int disableReasonCount) {
    return disableReasonCount > 0;
  }
}
