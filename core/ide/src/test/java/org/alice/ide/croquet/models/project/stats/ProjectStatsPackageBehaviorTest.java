package org.alice.ide.croquet.models.project.stats;

import org.alice.ide.croquet.models.project.stats.croquet.StatisticsFrameComposite;
import org.alice.ide.croquet.models.project.stats.croquet.StatisticsMethodFrequencyTabComposite;
import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import static org.junit.Assert.*;

public class ProjectStatsPackageBehaviorTest {
  @Test
  public void invocationCountsSortMethodsAlphabeticallyAndAccumulateDuplicates() {
    UserMethod beta = new UserMethod("beta", void.class, new UserParameter[0], new BlockStatement());
    UserMethod alpha = new UserMethod("alpha", void.class, new UserParameter[0], new BlockStatement());
    StatisticsMethodFrequencyTabComposite.InvocationCounts counts = new StatisticsMethodFrequencyTabComposite.InvocationCounts();

    counts.addMethod(beta);
    counts.addMethod(alpha);
    counts.addMethod(beta);

    assertEquals(2, counts.size());
    assertSame(alpha, counts.get(0));
    assertSame(beta, counts.get(1));
    assertEquals(2, counts.get(beta).getCount());
  }

  @Test
  public void frameSizingContractKeepsSummaryPanelLargerThanDetailPanel() {
    assertTrue(StatisticsFrameComposite.TOP_SIZE > StatisticsFrameComposite.BOTTOM_SIZE);
    assertTrue(StatisticsFrameComposite.BOTTOM_SIZE > 0);
    assertTrue(StatisticsMethodFrequencyTabComposite.class.getPackage().getName().startsWith("org.alice.ide.croquet.models.project.stats"));
  }
}
