package org.alice.ide.declarationseditor;

import org.lgna.croquet.Operation;
import org.lgna.project.ast.AbstractCode;
import org.lgna.project.ast.UserMethod;

public final class ProcedureTabSelection {
  private ProcedureTabSelection() {
  }

  public static Operation getSelectionOperation(DeclarationsEditorComposite editor, UserMethod procedure) {
    requireProcedure(procedure);
    return editor.getTabState().getItemSelectionOperationForMethod(procedure);
  }

  public static UserMethod getSelectedProcedure(DeclarationsEditorComposite editor) {
    DeclarationComposite<?, ?> selection = editor.getTabState().getValue();
    if (selection instanceof CodeComposite codeComposite) {
      AbstractCode code = codeComposite.getDeclaration();
      if (code instanceof UserMethod method && method.isProcedure()) {
        return method;
      }
    }
    return null;
  }

  private static void requireProcedure(UserMethod method) {
    if (method == null) {
      throw new IllegalArgumentException("procedure is required");
    }
    if (!method.isProcedure()) {
      throw new IllegalArgumentException("method must be a procedure: " + method.getName());
    }
  }
}
