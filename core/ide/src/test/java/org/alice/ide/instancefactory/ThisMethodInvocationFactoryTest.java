package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import static org.junit.Assert.*;

public class ThisMethodInvocationFactoryTest {
  private static UserMethod createNoArgMethod(String name) {
    return new UserMethod(name, String.class, new UserParameter[0], new BlockStatement());
  }

  @Test
  public void getInstanceCachesFactoriesForSameMethod() {
    UserMethod method = createNoArgMethod("getLabel");

    ThisMethodInvocationFactory first = ThisMethodInvocationFactory.getInstance(method);
    ThisMethodInvocationFactory second = ThisMethodInvocationFactory.getInstance(method);

    assertSame(first, second);
  }

  @Test
  public void getInstanceReturnsDifferentFactoriesForDifferentMethods() {
    ThisMethodInvocationFactory first = ThisMethodInvocationFactory.getInstance(createNoArgMethod("getLabel"));
    ThisMethodInvocationFactory second = ThisMethodInvocationFactory.getInstance(createNoArgMethod("getValue"));

    assertNotSame(first, second);
  }

  @Test
  public void getInstanceReturnsNullForMethodsWithRequiredParameters() {
    UserMethod method = new UserMethod(
        "getLabel",
        String.class,
        new UserParameter[] { new UserParameter("value", Integer.class) },
        new BlockStatement());

    assertNull(ThisMethodInvocationFactory.getInstance(method));
  }

  @Test
  public void getReprIncludesThisAndMethodSuffix() {
    ThisMethodInvocationFactory factory = ThisMethodInvocationFactory.getInstance(createNoArgMethod("getLabel"));

    assertTrue(factory.getRepr().contains("this"));
    assertTrue(factory.getRepr().contains("Label"));
  }
}
