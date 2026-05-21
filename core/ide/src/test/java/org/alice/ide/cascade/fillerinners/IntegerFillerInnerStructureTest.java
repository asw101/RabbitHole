package org.alice.ide.cascade.fillerinners;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class IntegerFillerInnerStructureTest {
  @Test
  public void classExtendsAbstractNumberFillerInner() {
    assertEquals(AbstractNumberFillerInner.class, IntegerFillerInner.class.getSuperclass());
    assertTrue(Modifier.isPublic(IntegerFillerInner.class.getModifiers()));
  }

  @Test
  public void getLiteralsWithoutDetailsUsesExpectedDefaults() {
    assertArrayEquals(new int[] {0, 1, 2, 3}, IntegerFillerInner.getLiterals(null));
  }

  @Test
  public void appendItemsOverrideIsPublic() throws Exception {
    assertNotNull(IntegerFillerInner.class.getConstructor());
    Method method = IntegerFillerInner.class.getMethod(
        "appendItems", java.util.List.class, org.lgna.project.annotations.ValueDetails.class, boolean.class, org.lgna.project.ast.Expression.class);
    assertTrue(Modifier.isPublic(method.getModifiers()));
  }
}
