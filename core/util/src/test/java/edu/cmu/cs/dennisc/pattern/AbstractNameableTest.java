package edu.cmu.cs.dennisc.pattern;

import edu.cmu.cs.dennisc.pattern.event.NameEvent;
import edu.cmu.cs.dennisc.pattern.event.NameListener;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AbstractNameableTest {
  private static class TestNameable extends AbstractNameable {
  }

  @Test
  public void setNameNotifiesChangingAndChangedListeners() {
    TestNameable nameable = new TestNameable();
    List<String> events = new ArrayList<>();
    nameable.addNameListener(new NameListener() {
      @Override
      public void nameChanging(NameEvent e) {
        events.add("changing:" + e.getPreviousName() + "->" + e.getNextName());
      }

      @Override
      public void nameChanged(NameEvent e) {
        events.add("changed:" + e.getPreviousName() + "->" + e.getNextName());
      }
    });

    nameable.setName("alpha");

    Assert.assertEquals("alpha", nameable.getName());
    Assert.assertEquals(java.util.Arrays.asList("changing:null->alpha", "changed:null->alpha"), events);
  }

  @Test
  public void settingSameNameTwiceDoesNotNotifyAgain() {
    TestNameable nameable = new TestNameable();
    List<NameEvent> events = new ArrayList<>();
    NameListener listener = new NameListener() {
      @Override
      public void nameChanging(NameEvent e) {
        events.add(e);
      }

      @Override
      public void nameChanged(NameEvent e) {
        events.add(e);
      }
    };
    nameable.addNameListener(listener);

    nameable.setName("alpha");
    nameable.setName("alpha");
    nameable.removeNameListener(listener);
    nameable.setName("beta");

    Assert.assertEquals(2, events.size());
    Assert.assertEquals("beta", nameable.getName());
  }

  @Test
  public void getNameListenersIsUnmodifiableAndSafeGetNameHandlesNull() {
    TestNameable nameable = new TestNameable();
    nameable.setName("value");

    Assert.assertEquals("value", NameableUtilities.safeGetName(nameable));
    Assert.assertNull(NameableUtilities.safeGetName(null));

    try {
      nameable.getNameListeners().clear();
      Assert.fail("Expected unmodifiable collection");
    } catch (UnsupportedOperationException expected) {
      Assert.assertEquals("value", nameable.getName());
    }
  }
}
