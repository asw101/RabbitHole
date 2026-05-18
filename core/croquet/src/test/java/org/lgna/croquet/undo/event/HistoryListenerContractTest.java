package org.lgna.croquet.undo.event;
import org.lgna.croquet.Group;
import org.lgna.croquet.undo.UndoHistory;
import org.junit.Test;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;
public class HistoryListenerContractTest {
  private static final Group G = Group.getInstance(UUID.fromString("a1000000-0000-0000-0000-000000000001"), "hlCt");
  private static class CL implements HistoryListener {
    AtomicInteger a=new AtomicInteger(),b=new AtomicInteger(),c=new AtomicInteger(),d=new AtomicInteger(),e=new AtomicInteger(),f=new AtomicInteger();
    @Override public void operationPushing(HistoryPushEvent ev){a.incrementAndGet();}
    @Override public void operationPushed(HistoryPushEvent ev){b.incrementAndGet();}
    @Override public void insertionIndexChanging(HistoryInsertionIndexEvent ev){c.incrementAndGet();}
    @Override public void insertionIndexChanged(HistoryInsertionIndexEvent ev){d.incrementAndGet();}
    @Override public void clearing(HistoryClearEvent ev){e.incrementAndGet();}
    @Override public void cleared(HistoryClearEvent ev){f.incrementAndGet();}
  }
  @Test public void pushing(){CL l=new CL();l.operationPushing(new HistoryPushEvent(new UndoHistory(G),null));assertEquals(1,l.a.get());}
  @Test public void pushed(){CL l=new CL();l.operationPushed(new HistoryPushEvent(new UndoHistory(G),null));assertEquals(1,l.b.get());}
  @Test public void clearing(){CL l=new CL();l.clearing(new HistoryClearEvent(new UndoHistory(G)));assertEquals(1,l.e.get());}
  @Test public void cleared(){CL l=new CL();l.cleared(new HistoryClearEvent(new UndoHistory(G)));assertEquals(1,l.f.get());}
  @Test public void isInterface(){assertTrue(HistoryListener.class.isInterface());}
  @Test public void hasSixMethods(){assertEquals(6,HistoryListener.class.getDeclaredMethods().length);}
}
