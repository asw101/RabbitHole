package org.alice.ide.x.components;

import java.awt.Insets;

final class StatementListPropertyViewHelper {
  private StatementListPropertyViewHelper() {
    throw new AssertionError();
  }

  static Insets createInsets(int requestedBottom, int leftInset, int rightInset, boolean isElse, boolean isDoInOrder, boolean isDoTogether) {
    int bottom = requestedBottom != 0 ? requestedBottom : ((isElse || isDoInOrder || isDoTogether) ? 8 : 0);
    int right = requestedBottom != 0 ? 0 : rightInset;
    return new Insets(StatementListPropertyView.INTRASTICIAL_PAD, leftInset, bottom, right);
  }

  static int getBoxLayoutPad(boolean isJava, boolean isDoTogether, int fontHeight) {
    if (isJava && isDoTogether) {
      return fontHeight + 8;
    }
    return StatementListPropertyView.INTRASTICIAL_PAD;
  }
}
