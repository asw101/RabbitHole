package org.lgna.croquet.triggers;
import org.junit.Test;
import static org.junit.Assert.*;
public class WindowEventTriggerTest {
  @Test public void extendsComponentEventTrigger() { assertTrue(ComponentEventTrigger.class.isAssignableFrom(WindowEventTrigger.class)); }
  @Test public void extendsTrigger() { assertTrue(Trigger.class.isAssignableFrom(WindowEventTrigger.class)); }
  @Test public void isConcreteClass() { assertFalse(java.lang.reflect.Modifier.isAbstract(WindowEventTrigger.class.getModifiers())); }
  @Test public void hasSetOnUserActivityMethod() throws NoSuchMethodException { assertNotNull(WindowEventTrigger.class.getMethod("setOnUserActivity", org.lgna.croquet.history.UserActivity.class, java.awt.event.WindowEvent.class)); }
  @Test public void hasCreateUserActivityMethod() throws NoSuchMethodException { assertNotNull(WindowEventTrigger.class.getMethod("createUserActivity", java.awt.event.WindowEvent.class)); }
  @Test public void constructorIsPrivate() { for (var c : WindowEventTrigger.class.getDeclaredConstructors()) { assertTrue(java.lang.reflect.Modifier.isPrivate(c.getModifiers())); } }
  @Test public void inheritsGetEvent() throws NoSuchMethodException { assertNotNull(WindowEventTrigger.class.getMethod("getEvent")); }
  @Test public void inheritsAppendRepr() throws NoSuchMethodException { assertNotNull(WindowEventTrigger.class.getMethod("appendRepr", StringBuilder.class)); }
}
