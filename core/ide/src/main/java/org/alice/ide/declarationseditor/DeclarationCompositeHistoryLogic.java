package org.alice.ide.declarationseditor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

final class DeclarationCompositeHistoryLogic {
  private DeclarationCompositeHistoryLogic() {
    throw new AssertionError();
  }

  static <T> List<T> createAppendedHistory(List<T> history, int index, T declarationComposite) {
    List<T> updatedHistory = new LinkedList<>();
    if (declarationComposite == null) {
      return updatedHistory;
    }
    List<T> source = history == null ? Collections.emptyList() : history;
    if ((index > 0) && (index <= source.size())) {
      source = source.subList(index, source.size());
    }
    updatedHistory.addAll(source);
    updatedHistory.removeIf(current -> current == declarationComposite);
    updatedHistory.addFirst(declarationComposite);
    return updatedHistory;
  }

  static boolean isBackEnabled(int index, int historySize) {
    return (index + 1) < historySize;
  }

  static boolean isForwardEnabled(int index) {
    return 0 < index;
  }

  static <T> List<T> getBackwardList(List<T> history, int index) {
    int minInclusive = index + 1;
    int maxExclusive = history.size();
    if (minInclusive < maxExclusive) {
      return history.subList(minInclusive, maxExclusive);
    }
    return Collections.emptyList();
  }

  static <T> List<T> getForwardList(List<T> history, int index) {
    int minInclusive = 0;
    int maxExclusive = index;
    if (minInclusive < maxExclusive) {
      List<T> rv = history.subList(minInclusive, maxExclusive);
      Collections.reverse(rv);
      return rv;
    }
    return Collections.emptyList();
  }
}
