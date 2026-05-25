package org.alice.ide.croquet.models.project.stats.croquet;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class StatisticsMethodFrequencyTabCompositeHelperTest {
  @Test
  public void sortMethodsByNameOrdersAlphabetically() {
    List<UserMethod> methods = new ArrayList<>(Arrays.asList(
        createMethod("beta", void.class),
        createMethod("alpha", void.class),
        createMethod("gamma", void.class)));

    StatisticsMethodFrequencyTabCompositeHelper.sortMethodsByName(methods);

    assertEquals("alpha", methods.get(0).getName());
    assertEquals("beta", methods.get(1).getName());
    assertEquals("gamma", methods.get(2).getName());
  }

  @Test
  public void isMethodVisibleHonorsFunctionAndProcedureFlags() {
    UserMethod function = createMethod("size", String.class);
    UserMethod procedure = createMethod("reset", void.class);

    assertFalse(StatisticsMethodFrequencyTabCompositeHelper.isMethodVisible(function, false, true));
    assertTrue(StatisticsMethodFrequencyTabCompositeHelper.isMethodVisible(function, true, true));
    assertFalse(StatisticsMethodFrequencyTabCompositeHelper.isMethodVisible(procedure, true, false));
    assertTrue(StatisticsMethodFrequencyTabCompositeHelper.isMethodVisible(procedure, true, true));
  }

  @Test
  public void countInvocationsSkipsRootMethod() {
    UserMethod root = createMethod("Project", void.class);
    UserMethod helper = createMethod("helper", void.class);
    StatisticsMethodFrequencyTabComposite.InvocationCounts counts = new StatisticsMethodFrequencyTabComposite.InvocationCounts();
    counts.addMethod(root);
    counts.addMethod(helper);
    counts.addMethod(helper);

    assertEquals(2, StatisticsMethodFrequencyTabCompositeHelper.countInvocations(counts, root));
  }

  @Test
  public void countVisiblePairsUsesSameFilterAsColumns() {
    StatisticsMethodFrequencyTabComposite.InvocationCounts counts = new StatisticsMethodFrequencyTabComposite.InvocationCounts();
    counts.addMethod(createMethod("size", String.class));
    counts.addMethod(createMethod("reset", void.class));

    assertEquals(1, StatisticsMethodFrequencyTabCompositeHelper.countVisiblePairs(counts, true, false));
    assertEquals(1, StatisticsMethodFrequencyTabCompositeHelper.countVisiblePairs(counts, false, true));
    assertEquals(2, StatisticsMethodFrequencyTabCompositeHelper.countVisiblePairs(counts, true, true));
  }

  private static UserMethod createMethod(String name, Class<?> returnType) {
    return new UserMethod(name, returnType, new UserParameter[0], new BlockStatement());
  }
}
