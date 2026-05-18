package org.lgna.croquet.undo.event;
import org.lgna.croquet.Group;
import org.lgna.croquet.undo.UndoHistory;
import org.junit.Test;
import java.util.UUID;
import static org.junit.Assert.*;
public class HistoryEventContractTest {
  private static final Group G = Group.getInstance(UUID.fromString("ae000000-0000-0000-0000-000000000001"), "heCt");
  @Test public void clearEvent_extends() { assertTrue(new HistoryClearEvent(new UndoHistory(G)) instanceof HistoryEvent); }
  @Test public void pushEvent_extends() { assertTrue(new HistoryPushEvent(new UndoHistory(G), null) instanceof HistoryEvent); }
  @Test public void indexEvent_extends() { assertTrue(new HistoryInsertionIndexEvent(new UndoHistory(G), 0, 1) instanceof HistoryEvent); }
  @Test public void clearEvent_source() { UndoHistory h=new UndoHistory(G); assertSame(h, new HistoryClearEvent(h).getSource()); }
  @Test public void pushEvent_source() { UndoHistory h=new UndoHistory(G); assertSame(h, new HistoryPushEvent(h,null).getSource()); }
  @Test public void indexEvent_indices() { HistoryInsertionIndexEvent e=new HistoryInsertionIndexEvent(new UndoHistory(G),5,10); assertEquals(5,e.getPrevIndex()); assertEquals(10,e.getNextIndex()); }
  @Test public void pushEvent_nullEdit() { assertNull(new HistoryPushEvent(new UndoHistory(G),null).getEdit()); }
  @Test public void historyEvent_isAbstract() { assertTrue(java.lang.reflect.Modifier.isAbstract(HistoryEvent.class.getModifiers())); }
  @Test public void clearEvent_toString() { assertNotNull(new HistoryClearEvent(new UndoHistory(G)).toString()); }
}
