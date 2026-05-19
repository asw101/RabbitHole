package org.alice.ide.ast;

import org.alice.ide.croquet.models.StandardExpressionState;
import org.junit.Test;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class FieldInitializerInstanceCreationArgument0StateTest {
  @Test
  public void extendsStandardExpressionState() {
    assertTrue(StandardExpressionState.class.isAssignableFrom(FieldInitializerInstanceCreationArgument0State.class));
  }
  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(FieldInitializerInstanceCreationArgument0State.class.getModifiers()));
  }
  @Test
  public void hasGetInstanceStaticMethod() throws Exception {
    Method m = FieldInitializerInstanceCreationArgument0State.class.getMethod("getInstance", org.lgna.project.ast.UserField.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }
  @Test
  public void getInstanceReturnsNullForNullField() {
    assertNull(FieldInitializerInstanceCreationArgument0State.getInstance(null));
  }
  @Test
  public void hasFieldAndInstanceCreationFields() throws Exception {
    Field f1 = FieldInitializerInstanceCreationArgument0State.class.getDeclaredField("field");
    Field f2 = FieldInitializerInstanceCreationArgument0State.class.getDeclaredField("instanceCreation");
    assertTrue(Modifier.isPrivate(f1.getModifiers()));
    assertTrue(Modifier.isFinal(f1.getModifiers()));
    assertTrue(Modifier.isPrivate(f2.getModifiers()));
    assertTrue(Modifier.isFinal(f2.getModifiers()));
  }
}
