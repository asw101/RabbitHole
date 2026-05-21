package org.alice.ide.properties.adapter;

import org.junit.Test;

import static org.junit.Assert.*;

public class FloatPropertyAdapterTest {
  @Test
  public void adapterUsesFloatUndoRedoDescription() {
    FloatPropertyAdapter<Object> adapter = new FloatPropertyAdapter<>(null, null, null);

    assertEquals("Float", adapter.getUndoRedoDescription());
    assertNull(adapter.getValue());
  }

  @Test
  public void mutableCopyDelegatesToCurrentValueWhenNull() {
    FloatPropertyAdapter<Object> adapter = new FloatPropertyAdapter<>(null, null, null);
    assertNull(adapter.getValueCopyIfMutable());
  }

  @Test
  public void classExtendsAbstractImplementationPropertyAdapter() {
    assertEquals(AbstractImplementationPropertyAdapter.class, FloatPropertyAdapter.class.getSuperclass());
  }
}
