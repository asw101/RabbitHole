package org.alice.ide.codedrop;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class CodePanelDropLogicTest {
  @Test
  public void choosePaneUnderPrefersSmallestContainingPane() {
    String pane = CodePanelDropLogic.choosePaneUnder(List.of(
        new CodePanelDropLogic.PaneCandidate<>("outer", 200, true),
        new CodePanelDropLogic.PaneCandidate<>("inner", 80, true),
        new CodePanelDropLogic.PaneCandidate<>("miss", 10, false)));

    assertEquals("inner", pane);
  }

  @Test
  public void chooseStatementDropActionReturnsCopyWhenQuoteModifierDown() {
    assertEquals(CodePanelDropLogic.StatementDropAction.COPY,
        CodePanelDropLogic.chooseStatementDropAction(true, false, 1, 3, false, false, 1));
  }

  @Test
  public void chooseStatementDropActionReturnsNoneForSameSlotDrop() {
    assertEquals(CodePanelDropLogic.StatementDropAction.NONE,
        CodePanelDropLogic.chooseStatementDropAction(false, true, 2, 2, false, false, 1));
  }

  @Test
  public void chooseStatementDropActionReturnsEnvelopForShiftCandidate() {
    assertEquals(CodePanelDropLogic.StatementDropAction.ENVELOP,
        CodePanelDropLogic.chooseStatementDropAction(false, false, 1, 3, true, true, 2));
  }

  @Test
  public void recursionMessageMentionsRecursiveCall() {
    assertTrue(CodePanelDropLogic.getRecursionDisabledMessage().contains("recursive"));
    assertTrue(CodePanelDropLogic.recursionWouldBeDisallowed(false, true));
    assertFalse(CodePanelDropLogic.recursionWouldBeDisallowed(true, true));
  }
}
