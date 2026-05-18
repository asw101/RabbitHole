package org.lgna.croquet.history.event;
import org.junit.Test;
import static org.junit.Assert.*;
public class ActivityEventContractTest {
  @Test public void cancelEvent_impl() { assertTrue(ActivityEvent.class.isAssignableFrom(CancelEvent.class)); }
  @Test public void changeEvent_impl() { assertTrue(ActivityEvent.class.isAssignableFrom(ChangeEvent.class)); }
  @Test public void editCommittedEvent_impl() { assertTrue(ActivityEvent.class.isAssignableFrom(EditCommittedEvent.class)); }
  @Test public void finishedEvent_impl() { assertTrue(ActivityEvent.class.isAssignableFrom(FinishedEvent.class)); }
  @Test public void popupMenuResizedEvent_impl() { assertTrue(ActivityEvent.class.isAssignableFrom(PopupMenuResizedEvent.class)); }
  @Test public void activityEvent_isInterface() { assertTrue(ActivityEvent.class.isInterface()); }
  @Test public void cancelEvent_concrete() { assertFalse(java.lang.reflect.Modifier.isAbstract(CancelEvent.class.getModifiers())); }
  @Test public void changeEvent_concrete() { assertFalse(java.lang.reflect.Modifier.isAbstract(ChangeEvent.class.getModifiers())); }
  @Test public void finishedEvent_concrete() { assertFalse(java.lang.reflect.Modifier.isAbstract(FinishedEvent.class.getModifiers())); }
  @Test public void editCommittedEvent_concrete() { assertFalse(java.lang.reflect.Modifier.isAbstract(EditCommittedEvent.class.getModifiers())); }
}
