package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import static org.junit.Assert.*;

public class ParameterAccessMethodInvocationFactoryTest {
  private static UserMethod method(String name) {
    return new UserMethod(name, String.class, new UserParameter[0], new BlockStatement());
  }

  @Test
  public void getInstance_cachesSameInputs() {
    UserParameter param = new UserParameter("target", String.class);
    UserMethod m = method("getName");
    assertSame(
        ParameterAccessMethodInvocationFactory.getInstance(param, m),
        ParameterAccessMethodInvocationFactory.getInstance(param, m));
  }

  @Test
  public void getInstance_differentiatesByParameter() {
    UserMethod m = method("getName");
    assertNotSame(
        ParameterAccessMethodInvocationFactory.getInstance(new UserParameter("first", String.class), m),
        ParameterAccessMethodInvocationFactory.getInstance(new UserParameter("second", String.class), m));
  }

  @Test
  public void getParameter_returnsOriginal() {
    UserParameter param = new UserParameter("target", String.class);
    assertSame(param, ParameterAccessMethodInvocationFactory.getInstance(param, method("getName")).getParameter());
  }

  @Test
  public void getRepr_includesParameterNameAndMethodSuffix() {
    String repr = ParameterAccessMethodInvocationFactory.getInstance(
        new UserParameter("target", String.class), method("getName")).getRepr();
    assertTrue(repr.contains("target"));
    assertTrue(repr.contains("Name"));
  }
}
