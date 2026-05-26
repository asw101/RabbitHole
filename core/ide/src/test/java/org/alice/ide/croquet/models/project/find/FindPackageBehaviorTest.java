package org.alice.ide.croquet.models.project.find;

import org.alice.ide.croquet.models.project.find.core.SearchResult;
import org.alice.ide.croquet.models.project.find.croquet.FindComposite;
import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import static org.junit.Assert.*;

public class FindPackageBehaviorTest {
  @Test
  public void compositeSplitsSearchTextIntoFindTerms() {
    FindComposite composite = new FindComposite(null);
    composite.getSearchState().setValueTransactionlessly("score_keeper+timer");

    assertArrayEquals(new String[] {"score", "keeper", "timer"}, composite.getSearchTerms());
  }

  @Test
  public void findNamespaceStillConnectsCoreResultsAndCroquetEntryPoint() {
    SearchResult result = new SearchResult(new UserMethod("storyAction", void.class, new UserParameter[0], new BlockStatement()));

    assertEquals("storyAction", result.getName());
    assertTrue(FindComposite.class.getPackage().getName().startsWith("org.alice.ide.croquet.models.project.find"));
  }
}
