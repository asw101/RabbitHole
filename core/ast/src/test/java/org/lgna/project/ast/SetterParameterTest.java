package org.lgna.project.ast;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class SetterParameterTest {
  private static SetterParameter createParameter() {
    UserField field = new UserField("score", Integer.class, new IntegerLiteral(0));
    Setter setter = field.getSetter();
    return (SetterParameter) setter.getRequiredParameters().get(0);
  }

  @Test
  public void getCodeReturnsOwningSetter() {
    UserField field = new UserField("score", Integer.class, new IntegerLiteral(0));
    Setter setter = field.getSetter();
    SetterParameter parameter = (SetterParameter) setter.getRequiredParameters().get(0);

    assertSame(setter, parameter.getCode());
  }

  @Test
  public void valueTypeDelegatesToOwningField() {
    SetterParameter parameter = createParameter();

    assertEquals(JavaType.getInstance(Integer.class), parameter.getValueType());
  }

  @Test
  public void nameMatchesOwningFieldName() {
    SetterParameter parameter = createParameter();

    assertEquals("score", parameter.getName());
  }

  @Test
  public void metadataFlagsMatchSetterParameterContract() {
    SetterParameter parameter = createParameter();

    assertNull(parameter.getNamePropertyIfItExists());
    assertNull(parameter.getDetails());
    assertTrue(parameter.isUserAuthored());
    assertFalse(parameter.isVariableLength());
  }
}
