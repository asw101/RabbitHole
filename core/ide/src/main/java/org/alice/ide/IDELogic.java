package org.alice.ide;

import org.lgna.project.ast.Node;

final class IDELogic {
  private IDELogic() {
    throw new AssertionError();
  }

  static boolean isDropDownDesired(boolean isKeywordExpression, boolean isTypeExpression, boolean isResourceExpression) {
    return !isKeywordExpression && !isTypeExpression && !isResourceExpression;
  }

  static <E extends Node> E getAncestor(Node node, Class<E> cls) {
    Node ancestor = node.getParent();
    while (ancestor != null) {
      if (cls.isInstance(ancestor)) {
        break;
      }
      ancestor = ancestor.getParent();
    }
    return cls.cast(ancestor);
  }
}
