package org.alice.ide.properties.adapter;

import org.junit.Test;

import static org.junit.Assert.*;

public class DoublePropertyAdapterTest {
  @Test
  public void constructorPreservesConfiguredRepresentation() {
    DoublePropertyAdapter<Object> adapter = new DoublePropertyAdapter<>("Scale", null, null, null);
    assertEquals("Scale", adapter.getRepr());
  }

  @Test
  public void getValueCopyIfMutableReturnsCurrentValueWhenNull() {
    DoublePropertyAdapter<Object> adapter = new DoublePropertyAdapter<>("Scale", null, null, null);
    assertNull(adapter.getValueCopyIfMutable());
    assertNull(adapter.getValue());
  }

  @Test
  public void classExtendsAbstractImplementationPropertyAdapter() {
    assertEquals(AbstractImplementationPropertyAdapter.class, DoublePropertyAdapter.class.getSuperclass());
  }
}
