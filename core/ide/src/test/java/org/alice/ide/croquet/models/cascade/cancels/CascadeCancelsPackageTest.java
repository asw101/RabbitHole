package org.alice.ide.croquet.models.cascade.cancels;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class CascadeCancelsPackageTest {
  @Test
  public void representativeClassesRemainInPackage() {
    assertEquals("org.alice.ide.croquet.models.cascade.cancels", CancelFillIn.class.getPackage().getName());
    assertEquals("org.alice.ide.croquet.models.cascade.cancels", TypeUnsetCancelFillIn.class.getPackage().getName());
  }

  @Test
  public void representativeClassesRemainConcreteTypes() {
    assertFalse(Modifier.isInterface(CancelFillIn.class.getModifiers()));
    assertFalse(Modifier.isInterface(TypeUnsetCancelFillIn.class.getModifiers()));
  }
}
