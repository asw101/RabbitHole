package org.lgna.croquet.triggers;
import org.junit.Test;
import static org.junit.Assert.*;
public class NullTriggerContractTest {
  @Test public void extendsTrigger() { assertTrue(Trigger.class.isAssignableFrom(NullTrigger.class)); }
  @Test public void isDeprecated() { assertNotNull(NullTrigger.class.getAnnotation(Deprecated.class)); }
  @Test public void isConcreteClass() { assertFalse(java.lang.reflect.Modifier.isAbstract(NullTrigger.class.getModifiers())); }
  @Test public void hasCreateUserActivityMethod() throws NoSuchMethodException { assertNotNull(NullTrigger.class.getMethod("createUserActivity")); }
  @Test public void trigger_isAbstract() { assertTrue(java.lang.reflect.Modifier.isAbstract(Trigger.class.getModifiers())); }
  @Test public void trigger_hasAppendRepr() throws NoSuchMethodException { assertNotNull(Trigger.class.getMethod("appendRepr", StringBuilder.class)); }
  @Test public void trigger_hasGetUserActivity() throws NoSuchMethodException { assertNotNull(Trigger.class.getMethod("getUserActivity")); }
  @Test public void trigger_hasGetViewController() throws NoSuchMethodException { assertNotNull(Trigger.class.getMethod("getViewController")); }
}
