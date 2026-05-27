package org.lgna.project.ast;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class UserParameterTest {
  @Test
  public void constructorStoresNameAndValueType() {
    UserParameter parameter = new UserParameter("value", String.class);

    assertEquals("value", parameter.getName());
    assertSame(JavaType.getInstance(String.class), parameter.getValueType());
    assertSame(parameter.name, parameter.getNamePropertyIfItExists());
    assertTrue(parameter.isUserAuthored());
    assertFalse(parameter.isVariableLength());
    assertFalse(parameter.isKeyworded());
    assertNull(parameter.getDetails());
  }

  @Test
  public void getCodeReturnsOwningUserMethod() {
    UserParameter parameter = new UserParameter("count", Integer.class);
    UserMethod method = new UserMethod(
        "update",
        Void.TYPE,
        new UserParameter[] {parameter},
        new BlockStatement());

    assertSame(method, parameter.getCode());
    assertSame(method, parameter.getParent());
  }
}
