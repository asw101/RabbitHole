package edu.cmu.cs.dennisc.pattern;

import edu.cmu.cs.dennisc.pattern.event.ReleaseEvent;
import edu.cmu.cs.dennisc.pattern.event.ReleaseListener;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AbstractReleasableTest {
  private static class TestReleasable extends AbstractReleasable {
    private int releaseCount;

    @Override
    protected void actuallyRelease() {
      this.releaseCount++;
    }
  }

  @Test
  public void releaseWithoutListenersSkipsActuallyRelease() {
    TestReleasable releasable = new TestReleasable();

    releasable.release();

    Assert.assertEquals(0, releasable.releaseCount);
  }

  @Test
  public void releaseWithListenerNotifiesBeforeAndAfterRelease() {
    TestReleasable releasable = new TestReleasable();
    List<String> events = new ArrayList<>();
    ReleaseListener listener = new ReleaseListener() {
      @Override
      public void releasing(ReleaseEvent e) {
        events.add("releasing:" + ((TestReleasable) e.getTypedSource()).releaseCount);
      }

      @Override
      public void released(ReleaseEvent e) {
        events.add("released:" + ((TestReleasable) e.getTypedSource()).releaseCount);
      }
    };
    releasable.addReleaseListener(listener);

    releasable.release();

    Assert.assertEquals(1, releasable.releaseCount);
    Assert.assertEquals(java.util.Arrays.asList("releasing:0", "released:1"), events);
  }

  @Test
  public void removeReleaseListenerStopsNotifications() {
    TestReleasable releasable = new TestReleasable();
    List<ReleaseEvent> events = new ArrayList<>();
    ReleaseListener listener = new ReleaseListener() {
      @Override
      public void releasing(ReleaseEvent e) {
        events.add(e);
      }

      @Override
      public void released(ReleaseEvent e) {
        events.add(e);
      }
    };
    releasable.addReleaseListener(listener);
    releasable.removeReleaseListener(listener);

    releasable.release();

    Assert.assertTrue(events.isEmpty());
  }

  @Test
  public void getReleaseListenersIsUnmodifiable() {
    TestReleasable releasable = new TestReleasable();

    try {
      releasable.getReleaseListeners().add(new ReleaseListener() {
        @Override
        public void releasing(ReleaseEvent e) {
        }

        @Override
        public void released(ReleaseEvent e) {
        }
      });
      Assert.fail("Expected unmodifiable collection");
    } catch (UnsupportedOperationException expected) {
      Assert.assertTrue(releasable.getReleaseListeners().isEmpty());
    }
  }
}
