package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import static org.junit.Assert.*;

public class ThisFieldAccessMethodInvocationFactoryTest {
  private static UserMethod createMethod(String name) {
    return new UserMethod(name, String.class, new UserParameter[0], new BlockStatement());
  }

  @Test
  public void getInstanceCachesFactoriesForSameFieldAndMethod() {
    UserField field = new UserField("camera", String.class);
    UserMethod method = createMethod("getName");

    ThisFieldAccessMethodInvocationFactory first = ThisFieldAccessMethodInvocationFactory.getInstance(field, method);
    ThisFieldAccessMethodInvocationFactory second = ThisFieldAccessMethodInvocationFactory.getInstance(field, method);

    assertSame(first, second);
  }

  @Test
  public void getInstanceReturnsDifferentFactoriesForDifferentFields() {
    UserMethod method = createMethod("getName");

    ThisFieldAccessMethodInvocationFactory first = ThisFieldAccessMethodInvocationFactory.getInstance(new UserField("camera", String.class), method);
    ThisFieldAccessMethodInvocationFactory second = ThisFieldAccessMethodInvocationFactory.getInstance(new UserField("light", String.class), method);

    assertNotSame(first, second);
  }

  @Test
  public void getFieldReturnsOriginalField() {
    UserField field = new UserField("camera", String.class);
    ThisFieldAccessMethodInvocationFactory factory = ThisFieldAccessMethodInvocationFactory.getInstance(field, createMethod("getName"));

    assertSame(field, factory.getField());
  }

  @Test
  public void getReprIncludesFieldNameAndMethodSuffix() {
    ThisFieldAccessMethodInvocationFactory factory = ThisFieldAccessMethodInvocationFactory.getInstance(
        new UserField("camera", String.class),
        createMethod("getName"));

    assertTrue(factory.getRepr().contains("this.camera"));
    assertTrue(factory.getRepr().contains("Name"));
  }
}
