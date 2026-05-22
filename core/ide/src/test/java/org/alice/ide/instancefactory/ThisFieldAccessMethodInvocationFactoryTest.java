package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import static org.junit.Assert.*;

public class ThisFieldAccessMethodInvocationFactoryTest {
  private static UserMethod method(String name) {
    return new UserMethod(name, String.class, new UserParameter[0], new BlockStatement());
  }

  @Test
  public void getInstance_cachesSameInputs() {
    UserField field = new UserField("camera", String.class);
    UserMethod m = method("getName");
    assertSame(
        ThisFieldAccessMethodInvocationFactory.getInstance(field, m),
        ThisFieldAccessMethodInvocationFactory.getInstance(field, m));
  }

  @Test
  public void getInstance_differentiatesByField() {
    UserMethod m = method("getName");
    assertNotSame(
        ThisFieldAccessMethodInvocationFactory.getInstance(new UserField("camera", String.class), m),
        ThisFieldAccessMethodInvocationFactory.getInstance(new UserField("light", String.class), m));
  }

  @Test
  public void getField_returnsOriginal() {
    UserField field = new UserField("camera", String.class);
    assertSame(field, ThisFieldAccessMethodInvocationFactory.getInstance(field, method("getName")).getField());
  }

  @Test
  public void getRepr_includesFieldNameAndMethodSuffix() {
    String repr = ThisFieldAccessMethodInvocationFactory.getInstance(
        new UserField("camera", String.class), method("getName")).getRepr();
    assertTrue(repr.contains("this.camera"));
    assertTrue(repr.contains("Name"));
  }
}
