package org.alice.ide.codeeditor;

import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.ConstructorBlockStatement;
import org.lgna.project.ast.ConstructorInvocationStatement;

import java.awt.Rectangle;

final class CodeEditorLogic {
  private CodeEditorLogic() {
    throw new AssertionError();
  }

  static ConstructorInvocationStatement getLeadingConstructorInvocation(BlockStatement body) {
    if (body instanceof ConstructorBlockStatement constructorBlockStatement) {
      return constructorBlockStatement.constructorInvocationStatement.getValue();
    }
    return null;
  }

  static int capMinimum(int yPotentialMinimumBound, int y, StatementListPropertyPaneInfo[] statementListPropertyPaneInfos, int index) {
    int rv = yPotentialMinimumBound;
    final int n = statementListPropertyPaneInfos.length;
    for (int i = 0; i < n; i++) {
      if (i != index) {
        Rectangle bounds = statementListPropertyPaneInfos[i].getBounds();
        int bottom = bounds.y + bounds.height;
        if (bottom < y) {
          rv = Math.max(rv, bottom);
        }
      }
    }
    return rv;
  }

  static int capMaximum(int yMaximum, int yPlusHeight, StatementListPropertyPaneInfo[] statementListPropertyPaneInfos, int index) {
    int rv = yMaximum;
    final int n = statementListPropertyPaneInfos.length;
    for (int i = 0; i < n; i++) {
      if (i != index) {
        Rectangle bounds = statementListPropertyPaneInfos[i].getBounds();
        if (bounds.y > yPlusHeight) {
          rv = Math.min(rv, bounds.y);
        }
      }
    }
    return rv;
  }
}
