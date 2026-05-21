package org.alice.ide.properties.adapter;

import org.junit.Test;

import static org.junit.Assert.*;

public class ColorPropertyAdapterTest {
  @Test
  public void adapterUsesColorUndoRedoDescription() {
    ColorPropertyAdapter<Object> adapter = new ColorPropertyAdapter<>(null, null, null);

    assertEquals("Color", adapter.getUndoRedoDescription());
    assertNull(adapter.getValue());
  }

  @Test
  public void mutableCopyDelegatesToCurrentValueWhenNull() {
    ColorPropertyAdapter<Object> adapter = new ColorPropertyAdapter<>(null, null, null);
    assertNull(adapter.getValueCopyIfMutable());
  }

  @Test
  public void classExtendsAbstractImplementationPropertyAdapter() {
    assertEquals(AbstractImplementationPropertyAdapter.class, ColorPropertyAdapter.class.getSuperclass());
  }
}
