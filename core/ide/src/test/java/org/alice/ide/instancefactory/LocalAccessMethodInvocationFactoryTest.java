package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.UserLocal;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import static org.junit.Assert.*;

public class LocalAccessMethodInvocationFactoryTest {
  private static UserMethod createMethod(String name) {
    return new UserMethod(name, String.class, new UserParameter[0], new BlockStatement());
  }

  @Test
  public void getInstanceCachesFactoriesForSameLocalAndMethod() {
    UserLocal local = new UserLocal("hero", String.class, false);
    UserMethod method = createMethod("getName");

    LocalAccessMethodInvocationFactory first = LocalAccessMethodInvocationFactory.getInstance(local, method);
    LocalAccessMethodInvocationFactory second = LocalAccessMethodInvocationFactory.getInstance(local, method);

    assertSame(first, second);
  }

  @Test
  public void getInstanceReturnsDifferentFactoriesForDifferentLocals() {
    UserMethod method = createMethod("getName");

    LocalAccessMethodInvocationFactory first = LocalAccessMethodInvocationFactory.getInstance(new UserLocal("hero", String.class, false), method);
    LocalAccessMethodInvocationFactory second = LocalAccessMethodInvocationFactory.getInstance(new UserLocal("villain", String.class, false), method);

    assertNotSame(first, second);
  }

  @Test
  public void getLocalReturnsOriginalLocal() {
    UserLocal local = new UserLocal("hero", String.class, false);
    LocalAccessMethodInvocationFactory factory = LocalAccessMethodInvocationFactory.getInstance(local, createMethod("getName"));

    assertSame(local, factory.getLocal());
  }

  @Test
  public void getReprIncludesLocalNameAndMethodSuffix() {
    LocalAccessMethodInvocationFactory factory = LocalAccessMethodInvocationFactory.getInstance(
        new UserLocal("hero", String.class, false),
        createMethod("getName"));

    assertTrue(factory.getRepr().contains("hero"));
    assertTrue(factory.getRepr().contains("Name"));
  }
}
