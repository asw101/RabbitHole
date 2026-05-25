package org.alice.ide.declarationseditor;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class DeclarationCompositeHistoryLogicTest {
  @Test
  public void createAppendedHistoryTruncatesForwardEntriesAndMovesDuplicatesToFront() {
    List<String> updated = DeclarationCompositeHistoryLogic.createAppendedHistory(new LinkedList<>(List.of("current", "older", "oldest")), 1, "oldest");

    assertEquals(List.of("oldest", "older"), updated);
  }

  @Test
  public void forwardAndBackwardListsReflectCurrentIndex() {
    List<String> history = new LinkedList<>(List.of("current", "older", "oldest"));

    assertEquals(List.of("older", "oldest"), DeclarationCompositeHistoryLogic.getBackwardList(history, 0));
    assertEquals(List.of("older", "current"), DeclarationCompositeHistoryLogic.getForwardList(new LinkedList<>(history), 2));
    assertTrue(DeclarationCompositeHistoryLogic.isBackEnabled(0, history.size()));
    assertTrue(DeclarationCompositeHistoryLogic.isForwardEnabled(2));
  }
}
