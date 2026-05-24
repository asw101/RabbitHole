package org.alice.ide.codedrop;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CodePanelDropLogicBehaviorTest {
  @Test
  public void isPotentiallyAcceptingRequiresDeclarationMatchAndSomeInsertMode() {
    assertFalse(CodePanelDropLogic.isPotentiallyAccepting(false, true, true));
    assertFalse(CodePanelDropLogic.isPotentiallyAccepting(true, false, false));
    assertTrue(CodePanelDropLogic.isPotentiallyAccepting(true, true, false));
    assertTrue(CodePanelDropLogic.isPotentiallyAccepting(true, false, true));
  }

  @Test
  public void choosePaneUnderPrefersTheSmallestContainingPaneAndReturnsNullWhenNothingContainsPoint() {
    assertEquals("inner", CodePanelDropLogic.choosePaneUnder(List.of(
        new CodePanelDropLogic.PaneCandidate<>("outer", 200, true),
        new CodePanelDropLogic.PaneCandidate<>("inner", 80, true),
        new CodePanelDropLogic.PaneCandidate<>("miss", 10, false))));
    assertNull(CodePanelDropLogic.choosePaneUnder(List.of(
        new CodePanelDropLogic.PaneCandidate<>("outer", 200, false),
        new CodePanelDropLogic.PaneCandidate<>("inner", 80, false))));
  }

  @Test
  public void chooseStatementDropActionHonorsCopyHideEnvelopAndMovePriority() {
    assertEquals(CodePanelDropLogic.StatementDropAction.COPY,
        CodePanelDropLogic.chooseStatementDropAction(true, false, 1, 3, false, false, 1));
    assertEquals(CodePanelDropLogic.StatementDropAction.NONE,
        CodePanelDropLogic.chooseStatementDropAction(false, true, 2, 2, true, true, 4));
    assertEquals(CodePanelDropLogic.StatementDropAction.ENVELOP,
        CodePanelDropLogic.chooseStatementDropAction(false, false, 1, 3, true, true, 2));
    assertEquals(CodePanelDropLogic.StatementDropAction.MOVE,
        CodePanelDropLogic.chooseStatementDropAction(false, false, 1, 3, false, false, 2));
    assertEquals(CodePanelDropLogic.StatementDropAction.NONE,
        CodePanelDropLogic.chooseStatementDropAction(false, false, 1, 3, false, false, 0));
  }

  @Test
  public void recursionRulesAndMessagesMatchThePreferenceFlag() {
    assertTrue(CodePanelDropLogic.recursionWouldBeDisallowed(false, true));
    assertFalse(CodePanelDropLogic.recursionWouldBeDisallowed(true, true));
    assertTrue(CodePanelDropLogic.getRecursionDisabledMessage().contains("recursive"));
    assertTrue(CodePanelDropLogic.getRecursionDisabledMessage().contains("Window -> Preferences"));
  }
}
