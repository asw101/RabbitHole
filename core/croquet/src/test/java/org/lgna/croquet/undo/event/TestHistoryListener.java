package org.lgna.croquet.undo.event;
import java.util.ArrayList;
import java.util.List;
public class TestHistoryListener implements HistoryListener {
  public final List<HistoryEvent> events = new ArrayList<>();
  @Override public void operationPushing(HistoryPushEvent e){events.add(e);}
  @Override public void operationPushed(HistoryPushEvent e){events.add(e);}
  @Override public void insertionIndexChanging(HistoryInsertionIndexEvent e){events.add(e);}
  @Override public void insertionIndexChanged(HistoryInsertionIndexEvent e){events.add(e);}
  @Override public void clearing(HistoryClearEvent e){events.add(e);}
  @Override public void cleared(HistoryClearEvent e){events.add(e);}
}
