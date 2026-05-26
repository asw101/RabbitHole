package org.alice.ide.croquet.models.project;

import org.alice.ide.croquet.models.project.find.core.SearchResult;
import org.alice.ide.croquet.models.project.stats.croquet.StatisticsMethodFrequencyTabComposite;
import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import static org.junit.Assert.*;

public class ProjectPackageBehaviorTest {
  @Test
  public void projectNamespaceCombinesFindResultsAndStatisticsCounts() {
    UserMethod method = new UserMethod("storyAction", void.class, new UserParameter[0], new BlockStatement());
    SearchResult result = new SearchResult(method);
    ThisExpression reference = new ThisExpression();

    result.addReference(reference);

    StatisticsMethodFrequencyTabComposite.InvocationCounts counts = new StatisticsMethodFrequencyTabComposite.InvocationCounts();
    counts.addMethod(method);
    counts.addMethod(method);

    assertEquals("storyAction", result.getName());
    assertEquals(1, result.getReferences().size());
    assertSame(reference, result.getReferences().get(0));
    assertEquals(1, counts.size());
    assertEquals(2, counts.get(method).getCount());
  }

  @Test
  public void representativeClassesRemainNestedUnderProjectNamespace() {
    assertTrue(SearchResult.class.getPackage().getName().startsWith("org.alice.ide.croquet.models.project"));
    assertTrue(StatisticsMethodFrequencyTabComposite.class.getPackage().getName().startsWith("org.alice.ide.croquet.models.project"));
  }
}
