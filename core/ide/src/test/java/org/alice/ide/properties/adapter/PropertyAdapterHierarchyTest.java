package org.alice.ide.properties.adapter;

import org.junit.Test;

import static org.junit.Assert.*;

public class PropertyAdapterHierarchyTest {

  @Test
  public void colorPropertyAdapter_isConcreteSubclass() {
    assertFalse(java.lang.reflect.Modifier.isAbstract(ColorPropertyAdapter.class.getModifiers()));
  }

  @Test
  public void doublePropertyAdapter_isConcreteSubclass() {
    assertFalse(java.lang.reflect.Modifier.isAbstract(DoublePropertyAdapter.class.getModifiers()));
  }

  @Test
  public void floatPropertyAdapter_isConcreteSubclass() {
    assertFalse(java.lang.reflect.Modifier.isAbstract(FloatPropertyAdapter.class.getModifiers()));
  }

  @Test
  public void stringPropertyAdapter_isConcreteSubclass() {
    assertFalse(java.lang.reflect.Modifier.isAbstract(StringPropertyAdapter.class.getModifiers()));
  }

  @Test
  public void abstractInstancePropertyAdapter_isAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(AbstractInstancePropertyAdapter.class.getModifiers()));
  }

  @Test
  public void abstractPropertyAdapter_hasGetPropertyType() throws Exception {
    assertNotNull(AbstractPropertyAdapter.class.getMethod("getPropertyType"));
  }

  @Test
  public void abstractPropertyAdapter_hasGetValueCopyIfMutable() throws Exception {
    assertNotNull(AbstractPropertyAdapter.class.getMethod("getValueCopyIfMutable"));
  }

  @Test
  public void sceneFogDensityAdapter_exists() {
    assertNotNull(SceneFogDensityAdapter.class);
  }
}
