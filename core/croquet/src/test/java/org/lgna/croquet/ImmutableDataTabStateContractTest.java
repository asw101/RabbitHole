package org.lgna.croquet;
import org.junit.Test;
import static org.junit.Assert.*;
public class ImmutableDataTabStateContractTest {
  @Test public void extendsTabState() { assertTrue(TabState.class.isAssignableFrom(ImmutableDataTabState.class)); }
  @Test public void isConcreteClass() { assertFalse(java.lang.reflect.Modifier.isAbstract(ImmutableDataTabState.class.getModifiers())); }
  @Test public void mutableDataTabState_extendsTabState() { assertTrue(TabState.class.isAssignableFrom(MutableDataTabState.class)); }
  @Test public void mutableDataTabState_isConcreteClass() { assertFalse(java.lang.reflect.Modifier.isAbstract(MutableDataTabState.class.getModifiers())); }
  @Test public void tabState_isAbstract() { assertTrue(java.lang.reflect.Modifier.isAbstract(TabState.class.getModifiers())); }
}
