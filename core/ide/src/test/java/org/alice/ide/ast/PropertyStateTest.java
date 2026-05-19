package org.alice.ide.ast;

import org.alice.ide.croquet.models.StandardExpressionState;
import org.junit.Test;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class PropertyStateTest {
  @Test
  public void isAbstract() {
    assertTrue(Modifier.isAbstract(PropertyState.class.getModifiers()));
  }
  @Test
  public void extendsStandardExpressionState() {
    assertTrue(StandardExpressionState.class.isAssignableFrom(PropertyState.class));
  }
  @Test
  public void hasSetterField() throws Exception {
    Field f = PropertyState.class.getDeclaredField("setter");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }
  @Test
  public void hasGetSetterMethod() throws Exception {
    Method m = PropertyState.class.getMethod("getSetter");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }
  @Test
  public void hasGetValueOrNullLiteralMethod() throws Exception {
    Method m = PropertyState.class.getMethod("getValueOrNullLiteral");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }
  @Test
  public void hasGetTypeAbstractMethod() {
    boolean found = java.util.Arrays.stream(PropertyState.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getType") && Modifier.isProtected(m.getModifiers()));
    assertTrue("Must have protected getType()", found);
  }
  @Test
  public void hasGetValueDetailsAbstractMethod() {
    boolean found = java.util.Arrays.stream(PropertyState.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getValueDetails") && Modifier.isProtected(m.getModifiers()));
    assertTrue("Must have protected getValueDetails()", found);
  }
}
