package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import static org.junit.Assert.*;

public class ThisMethodInvocationFactoryTest {
  private static UserMethod noArgMethod(String name) {
    return new UserMethod(name, String.class, new UserParameter[0], new BlockStatement());
  }

  @Test
  public void getInstance_cachesSameMethod() {
    UserMethod method = noArgMethod("getLabel");
    assertSame(
        ThisMethodInvocationFactory.getInstance(method),
        ThisMethodInvocationFactory.getInstance(method));
  }

  @Test
  public void getInstance_differentiatesByMethod() {
    assertNotSame(
        ThisMethodInvocationFactory.getInstance(noArgMethod("getLabel")),
        ThisMethodInvocationFactory.getInstance(noArgMethod("getValue")));
  }

  @Test
  public void getInstance_returnsNullForMethodsWithRequiredParameters() {
    UserMethod method = new UserMethod("getLabel", String.class,
        new UserParameter[] { new UserParameter("value", Integer.class) },
        new BlockStatement());
    assertNull(ThisMethodInvocationFactory.getInstance(method));
  }

  @Test
  public void getRepr_includesThisAndMethodSuffix() {
    String repr = ThisMethodInvocationFactory.getInstance(noArgMethod("getLabel")).getRepr();
    assertTrue(repr.contains("this"));
    assertTrue(repr.contains("Label"));
  }
}
