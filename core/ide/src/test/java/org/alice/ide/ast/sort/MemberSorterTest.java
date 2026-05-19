package org.alice.ide.ast.sort;

import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class MemberSorterTest {
  @Test
  public void isInterface() {
    assertTrue(Modifier.isInterface(MemberSorter.class.getModifiers()));
  }
  @Test
  public void hasCreateSortedListMethod() throws Exception {
    Method m = MemberSorter.class.getMethod("createSortedList", java.util.List.class);
    assertNotNull(m);
  }
  @Test
  public void createSortedListReturnsListType() throws Exception {
    Method m = MemberSorter.class.getMethod("createSortedList", java.util.List.class);
    assertEquals(java.util.List.class, m.getReturnType());
  }
}
