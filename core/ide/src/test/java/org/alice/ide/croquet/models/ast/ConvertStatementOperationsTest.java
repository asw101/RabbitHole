package org.alice.ide.croquet.models.ast;

import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class ConvertStatementOperationsTest {
  @Test
  public void convertStatementWithBody_isAbstract() {
    assertTrue(Modifier.isAbstract(ConvertStatementWithBodyOperation.class.getModifiers()));
  }
  @Test
  public void convertDoInOrderToDoTogether_extendsBase() {
    assertTrue(ConvertStatementWithBodyOperation.class.isAssignableFrom(ConvertDoInOrderToDoTogetherOperation.class));
  }
  @Test
  public void convertDoTogetherToDoInOrder_extendsBase() {
    assertTrue(ConvertStatementWithBodyOperation.class.isAssignableFrom(ConvertDoTogetherToDoInOrderOperation.class));
  }
  @Test
  public void convertDoInOrderToDoTogether_hasGetInstance() throws Exception {
    Method m = ConvertDoInOrderToDoTogetherOperation.class.getMethod("getInstance", org.lgna.project.ast.DoInOrder.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }
  @Test
  public void convertDoTogetherToDoInOrder_hasGetInstance() throws Exception {
    Method m = ConvertDoTogetherToDoInOrderOperation.class.getMethod("getInstance", org.lgna.project.ast.DoTogether.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }
  @Test
  public void convertStatementWithBody_hasGetOriginalMethod() throws Exception {
    Method m = ConvertStatementWithBodyOperation.class.getMethod("getOriginal");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }
  @Test
  public void convertStatementWithBody_hasCreateReplacementMethod() {
    boolean found = java.util.Arrays.stream(ConvertStatementWithBodyOperation.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("createReplacement") && Modifier.isAbstract(m.getModifiers()));
    assertTrue(found);
  }
}
