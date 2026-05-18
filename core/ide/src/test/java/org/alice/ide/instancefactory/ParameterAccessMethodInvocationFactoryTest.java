package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import static org.junit.Assert.*;

public class ParameterAccessMethodInvocationFactoryTest {
  private static UserMethod createMethod(String name) {
    return new UserMethod(name, String.class, new UserParameter[0], new BlockStatement());
  }

  @Test
  public void getInstanceCachesFactoriesForSameParameterAndMethod() {
    UserParameter parameter = new UserParameter("target", String.class);
    UserMethod method = createMethod("getName");

    ParameterAccessMethodInvocationFactory first = ParameterAccessMethodInvocationFactory.getInstance(parameter, method);
    ParameterAccessMethodInvocationFactory second = ParameterAccessMethodInvocationFactory.getInstance(parameter, method);

    assertSame(first, second);
  }

  @Test
  public void getInstanceReturnsDifferentFactoriesForDifferentParameters() {
    UserMethod method = createMethod("getName");

    ParameterAccessMethodInvocationFactory first = ParameterAccessMethodInvocationFactory.getInstance(new UserParameter("first", String.class), method);
    ParameterAccessMethodInvocationFactory second = ParameterAccessMethodInvocationFactory.getInstance(new UserParameter("second", String.class), method);

    assertNotSame(first, second);
  }

  @Test
  public void getParameterReturnsOriginalParameter() {
    UserParameter parameter = new UserParameter("target", String.class);
    ParameterAccessMethodInvocationFactory factory = ParameterAccessMethodInvocationFactory.getInstance(parameter, createMethod("getName"));

    assertSame(parameter, factory.getParameter());
  }

  @Test
  public void getReprIncludesParameterNameAndMethodSuffix() {
    ParameterAccessMethodInvocationFactory factory = ParameterAccessMethodInvocationFactory.getInstance(
        new UserParameter("target", String.class),
        createMethod("getName"));

    assertTrue(factory.getRepr().contains("target"));
    assertTrue(factory.getRepr().contains("Name"));
  }
}
