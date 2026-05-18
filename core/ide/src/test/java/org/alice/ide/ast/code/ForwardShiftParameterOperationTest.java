package org.alice.ide.ast.code;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import static org.junit.Assert.*;

public class ForwardShiftParameterOperationTest {

  private static final class ExposedForwardShiftParameterOperation extends ForwardShiftParameterOperation {
    private ExposedForwardShiftParameterOperation(UserMethod method, UserParameter parameter) {
      super(method.requiredParameters, parameter);
    }

    private int exposeIndexA() {
      return this.getIndexA();
    }
  }

  private static UserMethod createMethodWithParameters(String... names) {
    UserMethod method = new UserMethod("test", JavaType.VOID_TYPE, new UserParameter[0], new BlockStatement());
    for (String name : names) {
      method.requiredParameters.add(new UserParameter(name, JavaType.STRING_TYPE));
    }
    return method;
  }

  @Test
  public void firstParameter_isNotAppropriate() {
    UserMethod method = createMethodWithParameters("first", "second");
    ExposedForwardShiftParameterOperation operation = new ExposedForwardShiftParameterOperation(method, method.requiredParameters.get(0));

    assertFalse(operation.isIndexAppropriate());
  }

  @Test
  public void nonFirstParameter_isAppropriate() {
    UserMethod method = createMethodWithParameters("first", "second");
    ExposedForwardShiftParameterOperation operation = new ExposedForwardShiftParameterOperation(method, method.requiredParameters.get(1));

    assertTrue(operation.isIndexAppropriate());
  }

  @Test
  public void getIndexA_returnsPreviousParameterIndex() {
    UserMethod method = createMethodWithParameters("first", "second", "third");
    ExposedForwardShiftParameterOperation operation = new ExposedForwardShiftParameterOperation(method, method.requiredParameters.get(2));

    assertEquals(1, operation.exposeIndexA());
  }

  @Test
  public void middleParameter_usesImmediatePreviousIndex() {
    UserMethod method = createMethodWithParameters("first", "second", "third");
    ExposedForwardShiftParameterOperation operation = new ExposedForwardShiftParameterOperation(method, method.requiredParameters.get(1));

    assertEquals(0, operation.exposeIndexA());
  }
}
