package org.alice.nonfree;

import org.alice.stageide.StoryApiConfigurationManager;
import org.alice.stageide.ast.ExpressionCreator;
import org.alice.stageide.cascade.ExpressionCascadeManager;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NebulousIdeTest {
  @Test
  public void fallbackReportsNonfreeDisabledWhenExtensionIsAbsent() {
    assertFalse(NebulousIde.nonfree.isNonFreeEnabled());
    assertTrue(NebulousIde.nonfree.newExpressionCascadeManager() instanceof ExpressionCascadeManager);
    assertTrue(NebulousIde.nonfree.newStoryApiConfigurationManager() instanceof StoryApiConfigurationManager);
    assertTrue(NebulousIde.nonfree.newExpressionCreator() instanceof ExpressionCreator);
  }
}
