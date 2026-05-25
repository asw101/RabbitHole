package org.alice.ide.declarationseditor;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class DeclarationCompositeHistoryLogicEdgeTest {
  @Test
  public void createAppendedHistoryReturnsEmptyWhenDeclarationIsNull() {
    assertTrue(DeclarationCompositeHistoryLogic.createAppendedHistory(List.of("current"), 0, null).isEmpty());
  }

  @Test
  public void createAppendedHistoryCreatesSingletonHistoryWhenSourceIsNull() {
    assertEquals(List.of("current"), DeclarationCompositeHistoryLogic.createAppendedHistory(null, -1, "current"));
  }

  @Test
  public void createAppendedHistoryOnlyDeduplicatesByReferenceIdentity() {
    String existing = new String("duplicate");
    String incoming = new String("duplicate");

    List<String> updated = DeclarationCompositeHistoryLogic.createAppendedHistory(new LinkedList<>(List.of(existing)), 0, incoming);

    assertEquals(2, updated.size());
    assertSame(incoming, updated.get(0));
    assertSame(existing, updated.get(1));
  }

  @Test
  public void navigationFlagsDisableAtHistoryBoundaries() {
    assertFalse(DeclarationCompositeHistoryLogic.isBackEnabled(2, 3));
    assertFalse(DeclarationCompositeHistoryLogic.isForwardEnabled(0));
  }

  @Test
  public void forwardAndBackwardListsAreEmptyAtEdges() {
    List<String> history = new LinkedList<>(List.of("current", "older"));

    assertTrue(DeclarationCompositeHistoryLogic.getBackwardList(history, history.size() - 1).isEmpty());
    assertTrue(DeclarationCompositeHistoryLogic.getForwardList(new LinkedList<>(history), 0).isEmpty());
  }
}
