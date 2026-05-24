package org.alice.ide.croquet.models.html;

import org.lgna.project.ast.Expression;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.code.ProcessableNode;
import org.lgna.project.ast.Statement;
import org.lgna.project.ast.UserMethod;

import java.util.List;
import java.util.Map;
import java.util.Set;

final class HtmlEncoderLogic {
  private HtmlEncoderLogic() {
    throw new AssertionError();
  }

  static boolean isSectionToInclude(String key, Set<String> sectionsToSkip) {
    return !sectionsToSkip.contains(key);
  }

  static boolean isClassEmpty(Map<String, List<ProcessableNode>> sections, Set<String> sectionsToSkip) {
    for (Map.Entry<String, List<ProcessableNode>> entry : sections.entrySet()) {
      if (isSectionToInclude(entry.getKey(), sectionsToSkip) && !entry.getValue().isEmpty()) {
        for (ProcessableNode item : entry.getValue()) {
          if (!(item instanceof UserMethod) || !((UserMethod) item).getManagementLevel().isGenerated()) {
            return false;
          }
        }
      }
    }
    return true;
  }

  static MethodInvocation getListenerInvocation(Statement listener) {
    if (listener instanceof ExpressionStatement statement) {
      Expression exp = statement.expression.getValue();
      if (exp instanceof MethodInvocation invocation) {
        return invocation;
      }
    }
    return null;
  }
}
