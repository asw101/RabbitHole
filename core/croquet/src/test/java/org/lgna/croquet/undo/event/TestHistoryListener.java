package org.lgna.croquet.undo.event;

/**
 * No-op {@link HistoryListener} base for undo-history tests.
 * Subclass and override only the callback(s) you want to verify.
 */
public class TestHistoryListener implements HistoryListener {
  @Override public void operationPushing(HistoryPushEvent e) {}
  @Override public void operationPushed(HistoryPushEvent e) {}
  @Override public void insertionIndexChanging(HistoryInsertionIndexEvent e) {}
  @Override public void insertionIndexChanged(HistoryInsertionIndexEvent e) {}
  @Override public void clearing(HistoryClearEvent e) {}
  @Override public void cleared(HistoryClearEvent e) {}
}
