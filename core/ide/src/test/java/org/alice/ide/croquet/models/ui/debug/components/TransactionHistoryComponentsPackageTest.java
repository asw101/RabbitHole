package org.alice.ide.croquet.models.ui.debug.components;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class TransactionHistoryComponentsPackageTest {
  @Test
  public void representativeClassesRemainInPackage() {
    assertEquals("org.alice.ide.croquet.models.ui.debug.components", TransactionHistoryCellRenderer.class.getPackage().getName());
    assertEquals("org.alice.ide.croquet.models.ui.debug.components", TransactionHistoryView.class.getPackage().getName());
  }

  @Test
  public void representativeClassesRemainConcreteTypes() {
    assertFalse(Modifier.isInterface(TransactionHistoryCellRenderer.class.getModifiers()));
    assertFalse(Modifier.isInterface(TransactionHistoryView.class.getModifiers()));
  }
}
