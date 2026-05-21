package org.alice.ide.properties.adapter;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringPropertyAdapterTest {
  @Test
  public void defaultConstructorUsesStringUndoRedoDescription() {
    StringPropertyAdapter<Object> adapter = new StringPropertyAdapter<>(null, null, null);

    assertEquals("String", adapter.getUndoRedoDescription());
    assertEquals(String.class, adapter.getPropertyType());
    assertNull(adapter.getValue());
  }

  @Test
  public void customReprIsPreserved() {
    StringPropertyAdapter<Object> adapter = new StringPropertyAdapter<>("Label", null, null, null);
    assertEquals("Label", adapter.getRepr());
  }

  @Test
  public void classExtendsAbstractInstancePropertyAdapter() {
    assertEquals(AbstractInstancePropertyAdapter.class, StringPropertyAdapter.class.getSuperclass());
  }
}
