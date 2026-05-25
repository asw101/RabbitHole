package org.alice.ide.croquet.models.project.stats.croquet;

import org.lgna.project.ast.AbstractMethod;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

final class StatisticsMethodFrequencyTabCompositeHelper {
  private StatisticsMethodFrequencyTabCompositeHelper() {
    throw new AssertionError();
  }

  static <T extends AbstractMethod> void sortMethodsByName(List<T> methods) {
    Collections.sort(methods, Comparator.comparing(AbstractMethod::getName));
  }

  static boolean isMethodVisible(AbstractMethod method, boolean showFunctions, boolean showProcedures) {
    return (!method.isFunction() || showFunctions) && (!method.isProcedure() || showProcedures);
  }

  static int countInvocations(StatisticsMethodFrequencyTabComposite.InvocationCounts invocationCounts, AbstractMethod rootMethod) {
    int count = 0;
    for (StatisticsMethodFrequencyTabComposite.MethodCountPair pair : invocationCounts.getMethodCountPairs()) {
      if (pair.getMethod() != rootMethod) {
        count += pair.getCount();
      }
    }
    return count;
  }

  static int countVisiblePairs(StatisticsMethodFrequencyTabComposite.InvocationCounts invocationCounts, boolean showFunctions, boolean showProcedures) {
    int count = 0;
    for (StatisticsMethodFrequencyTabComposite.MethodCountPair pair : invocationCounts.getMethodCountPairs()) {
      if (isMethodVisible(pair.getMethod(), showFunctions, showProcedures)) {
        count++;
      }
    }
    return count;
  }
}
