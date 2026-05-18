package org.lgna.croquet.meta;

import org.lgna.croquet.event.ValueEvent;
import org.lgna.croquet.event.ValueListener;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for {@link MetaState} listener lifecycle and value change detection.
 */
public class MetaStateTest {

  private static class TestMetaState extends MetaState<String> {
    private String value;

    TestMetaState(String initial) {
      this.value = initial;
      this.setPrevValue(initial);
    }

    @Override
    public String getValue() {
      return value;
    }

    void updateValue(String newValue) {
      this.value = newValue;
      checkValueAndFireIfAppropriate();
    }
  }

  @Test
  public void getValue_returnsInitialValue() {
    TestMetaState ms = new TestMetaState("init");
    assertEquals("init", ms.getValue());
  }

  @Test
  public void updateValue_changesGetValue() {
    TestMetaState ms = new TestMetaState("init");
    ms.updateValue("changed");
    assertEquals("changed", ms.getValue());
  }

  @Test
  public void addValueListener_firesOnChange() {
    TestMetaState ms = new TestMetaState("old");
    List<ValueEvent<String>> events = new ArrayList<>();
    ms.addValueListener(events::add);
    ms.updateValue("new");
    assertEquals(1, events.size());
    assertEquals("old", events.get(0).getPreviousValue());
    assertEquals("new", events.get(0).getNextValue());
  }

  @Test
  public void addValueListener_noFireOnSameValue() {
    TestMetaState ms = new TestMetaState("same");
    List<ValueEvent<String>> events = new ArrayList<>();
    ms.addValueListener(events::add);
    ms.updateValue("same");
    assertTrue(events.isEmpty());
  }

  @Test
  public void addAndInvokeValueListener_firesImmediately() {
    TestMetaState ms = new TestMetaState("init");
    List<ValueEvent<String>> events = new ArrayList<>();
    ms.addAndInvokeValueListener(events::add);
    assertEquals(1, events.size());
    assertEquals("init", events.get(0).getNextValue());
  }

  @Test
  public void addAndInvokeValueListener_alsoFiresOnSubsequentChange() {
    TestMetaState ms = new TestMetaState("init");
    List<ValueEvent<String>> events = new ArrayList<>();
    ms.addAndInvokeValueListener(events::add);
    ms.updateValue("next");
    assertEquals(2, events.size());
  }

  @Test
  public void multipleListeners_allNotified() {
    TestMetaState ms = new TestMetaState("a");
    List<String> l1 = new ArrayList<>();
    List<String> l2 = new ArrayList<>();
    ms.addValueListener(e -> l1.add(e.getNextValue()));
    ms.addValueListener(e -> l2.add(e.getNextValue()));
    ms.updateValue("b");
    assertEquals(1, l1.size());
    assertEquals(1, l2.size());
  }

  @Test
  public void multipleChanges_fireEach() {
    TestMetaState ms = new TestMetaState("a");
    List<ValueEvent<String>> events = new ArrayList<>();
    ms.addValueListener(events::add);
    ms.updateValue("b");
    ms.updateValue("c");
    ms.updateValue("d");
    assertEquals(3, events.size());
  }

  @Test
  public void nullInitialValue() {
    TestMetaState ms = new TestMetaState(null);
    assertNull(ms.getValue());
  }

  @Test
  public void nullToNonNull_fires() {
    TestMetaState ms = new TestMetaState(null);
    List<ValueEvent<String>> events = new ArrayList<>();
    ms.addValueListener(events::add);
    ms.updateValue("val");
    assertEquals(1, events.size());
  }

  @Test
  public void nonNullToNull_fires() {
    TestMetaState ms = new TestMetaState("val");
    List<ValueEvent<String>> events = new ArrayList<>();
    ms.addValueListener(events::add);
    ms.updateValue(null);
    assertEquals(1, events.size());
  }

  @Test
  public void isAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(MetaState.class.getModifiers()));
  }
}
