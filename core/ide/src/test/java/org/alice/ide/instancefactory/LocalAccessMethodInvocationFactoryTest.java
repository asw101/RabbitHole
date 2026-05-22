package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.UserLocal;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import static org.junit.Assert.*;

public class LocalAccessMethodInvocationFactoryTest {
  private static UserMethod method(String name) {
    return new UserMethod(name, String.class, new UserParameter[0], new BlockStatement());
  }

  private static UserLocal local(String name) {
    return new UserLocal(name, String.class, false);
  }

  @Test
  public void getInstance_cachesSameInputs() {
    UserLocal local = local("hero");
    UserMethod m = method("getName");
    assertSame(
        LocalAccessMethodInvocationFactory.getInstance(local, m),
        LocalAccessMethodInvocationFactory.getInstance(local, m));
  }

  @Test
  public void getInstance_differentiatesByLocal() {
    UserMethod m = method("getName");
    assertNotSame(
        LocalAccessMethodInvocationFactory.getInstance(local("hero"), m),
        LocalAccessMethodInvocationFactory.getInstance(local("villain"), m));
  }

  @Test
  public void getLocal_returnsOriginal() {
    UserLocal local = local("hero");
    assertSame(local, LocalAccessMethodInvocationFactory.getInstance(local, method("getName")).getLocal());
  }

  @Test
  public void getRepr_includesLocalNameAndMethodSuffix() {
    String repr = LocalAccessMethodInvocationFactory.getInstance(local("hero"), method("getName")).getRepr();
    assertTrue(repr.contains("hero"));
    assertTrue(repr.contains("Name"));
  }
}
