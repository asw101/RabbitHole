package org.alice.ide.croquet.models;

import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class StandardExpressionStateTest {
  @Test
  public void isAbstract() {
    assertTrue(Modifier.isAbstract(StandardExpressionState.class.getModifiers()));
  }
  @Test
  public void extendsExpressionState() {
    assertTrue(ExpressionState.class.isAssignableFrom(StandardExpressionState.class));
  }
  @Test
  public void hasGetTypeAbstractMethod() {
    boolean found = java.util.Arrays.stream(StandardExpressionState.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getType") && Modifier.isAbstract(m.getModifiers()));
    assertTrue(found);
  }
  @Test
  public void hasGetValueDetailsAbstractMethod() {
    boolean found = java.util.Arrays.stream(StandardExpressionState.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getValueDetails") && Modifier.isAbstract(m.getModifiers()));
    assertTrue(found);
  }
}
