package org.alice.ide.croquet.models;

import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class ResponsibleModelTest {
  @Test
  public void isInterface() {
    assertTrue(Modifier.isInterface(ResponsibleModel.class.getModifiers()));
  }
  @Test
  public void hasDoOrRedoInternalMethod() throws Exception {
    Method m = ResponsibleModel.class.getMethod("doOrRedoInternal", boolean.class);
    assertNotNull(m);
  }
  @Test
  public void hasUndoInternalMethod() throws Exception {
    Method m = ResponsibleModel.class.getMethod("undoInternal");
    assertNotNull(m);
  }
  @Test
  public void hasAppendDescriptionMethod() throws Exception {
    Method m = ResponsibleModel.class.getMethod("appendDescription", StringBuilder.class, boolean.class);
    assertNotNull(m);
  }
  @Test
  public void exactlyThreePublicMethods() {
    long count = java.util.Arrays.stream(ResponsibleModel.class.getDeclaredMethods())
        .filter(m -> !m.isSynthetic())
        .count();
    assertEquals(3, count);
  }
}
