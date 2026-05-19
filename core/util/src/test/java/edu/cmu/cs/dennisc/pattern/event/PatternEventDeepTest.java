package edu.cmu.cs.dennisc.pattern.event;

import edu.cmu.cs.dennisc.pattern.Nameable;
import edu.cmu.cs.dennisc.pattern.Releasable;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;

public class PatternEventDeepTest {

  // --- Event (abstract base) via NameEvent ---

  @Test
  public void event_getSource_returnsOriginalSource() {
    Nameable source = new StubNameable("alice");
    NameEvent event = new NameEvent(source, "old", "new");
    assertSame(source, event.getSource());
  }

  @Test
  public void event_getTypedSource_returnsCastSource() {
    Nameable source = new StubNameable("bob");
    NameEvent event = new NameEvent(source, "prev", "next");
    Nameable typed = event.getTypedSource();
    assertSame(source, typed);
  }

  @Test
  public void event_isReservedForReuse_defaultFalse() {
    Nameable source = new StubNameable("test");
    NameEvent event = new NameEvent(source, "a", "b");
    assertFalse(event.isReservedForReuse());
  }

  // --- NameEvent ---

  @Test
  public void nameEvent_previousName() {
    Nameable source = new StubNameable("thing");
    NameEvent event = new NameEvent(source, "oldName", "newName");
    assertEquals("oldName", event.getPreviousName());
  }

  @Test
  public void nameEvent_nextName() {
    Nameable source = new StubNameable("thing");
    NameEvent event = new NameEvent(source, "oldName", "newName");
    assertEquals("newName", event.getNextName());
  }

  @Test
  public void nameEvent_nullNames() {
    Nameable source = new StubNameable(null);
    NameEvent event = new NameEvent(source, null, null);
    assertNull(event.getPreviousName());
    assertNull(event.getNextName());
  }

  @Test
  public void nameEvent_sameNameTransition() {
    Nameable source = new StubNameable("same");
    NameEvent event = new NameEvent(source, "same", "same");
    assertEquals(event.getPreviousName(), event.getNextName());
  }

  // --- ReleaseEvent ---

  @Test
  public void releaseEvent_sourceIsReleasable() {
    StubReleasable source = new StubReleasable();
    ReleaseEvent event = new ReleaseEvent(source);
    assertSame(source, event.getSource());
    assertSame(source, event.getTypedSource());
  }

  @Test
  public void releaseEvent_isReservedForReuse_defaultFalse() {
    StubReleasable source = new StubReleasable();
    ReleaseEvent event = new ReleaseEvent(source);
    assertFalse(event.isReservedForReuse());
  }

  // --- NameListener / ReleaseListener are marker interfaces ---

  @Test
  public void nameListener_isInterface() {
    assertTrue(NameListener.class.isInterface());
  }

  @Test
  public void releaseListener_isInterface() {
    assertTrue(ReleaseListener.class.isInterface());
  }

  // --- Stubs ---

  private static class StubNameable implements Nameable {
    private String name;

    StubNameable(String name) {
      this.name = name;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public void setName(String name) {
      this.name = name;
    }
  }

  private static class StubReleasable implements Releasable {
    @Override
    public void release() {}

    @Override
    public void addReleaseListener(ReleaseListener l) {}

    @Override
    public void removeReleaseListener(ReleaseListener l) {}

    @Override
    public Collection<ReleaseListener> getReleaseListeners() { return Collections.emptyList(); }
  }
}
