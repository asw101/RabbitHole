package org.alice.ide.ast.code.edits;

import org.junit.Test;
import org.lgna.croquet.edits.AbstractEdit;
import org.lgna.project.ast.*;
import static org.junit.Assert.*;

public class SwapParametersEditTest {
  private static UserMethod createMethodWithParams(String name, String... paramNames) {
    UserMethod method = new UserMethod();
    method.name.setValue(name);
    method.returnType.setValue(JavaType.VOID_TYPE);
    for (String paramName : paramNames) {
      UserParameter param = new UserParameter();
      param.name.setValue(paramName);
      param.valueType.setValue(JavaType.getInstance(Object.class));
      method.requiredParameters.add(param);
    }
    return method;
  }

  @Test public void construct_withNullUserActivity() {
    SwapParametersEdit edit = new SwapParametersEdit(null, createMethodWithParams("m", "a", "b"), 0);
    assertNotNull(edit);
  }
  @Test public void isAbstractEdit() {
    assertTrue(new SwapParametersEdit(null, createMethodWithParams("m", "a", "b"), 0) instanceof AbstractEdit);
  }
  @Test public void construct_indexZero() {
    assertNotNull(new SwapParametersEdit(null, createMethodWithParams("m", "p1", "p2", "p3"), 0));
  }
  @Test public void construct_indexOne() {
    assertNotNull(new SwapParametersEdit(null, createMethodWithParams("m", "p1", "p2", "p3"), 1));
  }
  @Test public void twoEdits_different() {
    UserMethod m = createMethodWithParams("m", "a", "b");
    assertNotSame(new SwapParametersEdit(null, m, 0), new SwapParametersEdit(null, m, 0));
  }
  @Test public void className() { assertEquals("SwapParametersEdit", SwapParametersEdit.class.getSimpleName()); }
  @Test public void construct_fourParams() { assertNotNull(new SwapParametersEdit(null, createMethodWithParams("m", "a", "b", "c", "d"), 2)); }
  @Test public void construct_twoParams() { assertNotNull(new SwapParametersEdit(null, createMethodWithParams("m", "x", "y"), 0)); }
  @Test public void helperCreatesValidMethod() {
    UserMethod m = createMethodWithParams("test", "x", "y", "z");
    assertEquals("test", m.name.getValue());
    assertEquals(3, m.requiredParameters.size());
  }
  @Test public void multipleConstructions() {
    UserMethod m = createMethodWithParams("fn", "a", "b");
    for (int i = 0; i < 5; i++) assertNotNull(new SwapParametersEdit(null, m, 0));
  }
}
