package org.alice.ide.properties.adapter;

import org.junit.Test;

import static org.junit.Assert.*;

public class AbstractPropertyAdapterTest {

  @Test
  public void getLocalizedString_nullKey_returnsNull() {
    assertNull(AbstractPropertyAdapter.getLocalizedString(null));
  }

  @Test
  public void getLocalizedString_unknownKey_returnsKey() {
    String key = "thisKeyDoesNotExistInBundle_xyz";
    String result = AbstractPropertyAdapter.getLocalizedString(key);
    assertEquals(key, result);
  }

  @Test
  public void getLocalizedString_emptyString_returnsEmptyOrKey() {
    String result = AbstractPropertyAdapter.getLocalizedString("");
    assertNotNull(result);
  }

  @Test
  public void innerInterface_valueChangeObserver_exists() {
    assertNotNull(AbstractPropertyAdapter.ValueChangeObserver.class);
  }

  @Test
  public void class_isAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(AbstractPropertyAdapter.class.getModifiers()));
  }

  @Test
  public void class_hasGetRepr() throws Exception {
    assertNotNull(AbstractPropertyAdapter.class.getMethod("getRepr"));
  }

  @Test
  public void class_hasGetValue() throws Exception {
    assertNotNull(AbstractPropertyAdapter.class.getMethod("getValue"));
  }

  @Test
  public void class_hasSetValue() throws Exception {
    assertNotNull(AbstractPropertyAdapter.class.getMethod("setValue", Object.class));
  }

  @Test
  public void class_hasGetLastSetValue() throws Exception {
    assertNotNull(AbstractPropertyAdapter.class.getMethod("getLastSetValue"));
  }

  @Test
  public void class_hasGetInstance() throws Exception {
    assertNotNull(AbstractPropertyAdapter.class.getMethod("getInstance"));
  }

  @Test
  public void class_hasObserverMethods() throws Exception {
    assertNotNull(AbstractPropertyAdapter.class.getMethod("addValueChangeObserver",
        AbstractPropertyAdapter.ValueChangeObserver.class));
    assertNotNull(AbstractPropertyAdapter.class.getMethod("removeValueChangeObserver",
        AbstractPropertyAdapter.ValueChangeObserver.class));
  }
}
