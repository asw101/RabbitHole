package org.alice.ide.codedrop;

import java.util.List;

final class CodePanelDropLogic {
  enum StatementDropAction {
    NONE,
    COPY,
    ENVELOP,
    MOVE
  }

  static final class PaneCandidate<T> {
    final T pane;
    final int height;
    final boolean contains;

    PaneCandidate(T pane, int height, boolean contains) {
      this.pane = pane;
      this.height = height;
      this.contains = contains;
    }
  }

  private CodePanelDropLogic() {
    throw new AssertionError();
  }

  static boolean isPotentiallyAccepting(boolean declarationMatches, boolean statementDrag, boolean expressionPotentialStatementCreator) {
    return declarationMatches && (statementDrag || expressionPotentialStatementCreator);
  }

  static <T> T choosePaneUnder(List<PaneCandidate<T>> candidates) {
    T selected = null;
    int bestHeight = Integer.MAX_VALUE;
    for (PaneCandidate<T> candidate : candidates) {
      if (candidate.contains && candidate.height < bestHeight) {
        selected = candidate.pane;
        bestHeight = candidate.height;
      }
    }
    return selected;
  }

  static boolean shouldHideDropProxy(boolean sameOwner, int prevIndex, int nextIndex) {
    return sameOwner && ((prevIndex == nextIndex) || (prevIndex == (nextIndex - 1)));
  }

  static int calculateDropProxyX(boolean leftToRight, int leftInset, int paneWidth, int rightInset, int proxyWidth) {
    return leftToRight ? leftInset : paneWidth - rightInset - proxyWidth;
  }

  static boolean recursionWouldBeDisallowed(boolean recursionAllowed, boolean recursiveTemplate) {
    return !recursionAllowed && recursiveTemplate;
  }

  static String getRecursionDisabledMessage() {
    return "<html>The code you have just dropped would create a <strong><em>recursive</em></strong> method call.<p><p>Recursion is disabled by default because otherwise many users unwittingly and mistakenly make recursive calls.<p><p>For more information on recursion see the Window -> Preferences menu.</html>";
  }

  static StatementDropAction chooseStatementDropAction(boolean quoteDown, boolean sameOwner, int prevIndex, int index,
                                                       boolean shiftDown, boolean candidateForEnvelop, int shiftMoveCount) {
    if (quoteDown) {
      return StatementDropAction.COPY;
    }
    if (shouldHideDropProxy(sameOwner, prevIndex, index)) {
      return StatementDropAction.NONE;
    }
    if (shiftDown && candidateForEnvelop) {
      return StatementDropAction.ENVELOP;
    }
    return shiftMoveCount > 0 ? StatementDropAction.MOVE : StatementDropAction.NONE;
  }
}
