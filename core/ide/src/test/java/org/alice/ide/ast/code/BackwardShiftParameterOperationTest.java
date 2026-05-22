package org.alice.ide.ast.code;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import static org.junit.Assert.*;

public class BackwardShiftParameterOperationTest {

  private static final class ExposedBackwardShiftParameterOperation extends BackwardShiftParameterOperation {
    private ExposedBackwardShiftParameterOperation(UserMethod method, UserParameter parameter) {
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
  public void firstParameter_isAppropriateWhenAnotherParameterFollows() {
    UserMethod method = createMethodWithParameters("first", "second");
    ExposedBackwardShiftParameterOperation operation = new ExposedBackwardShiftParameterOperation(method, method.requiredParameters.get(0));

    assertTrue(operation.isIndexAppropriate());
  }

  @Test
  public void lastParameter_isNotAppropriate() {
    UserMethod method = createMethodWithParameters("first", "second");
    ExposedBackwardShiftParameterOperation operation = new ExposedBackwardShiftParameterOperation(method, method.requiredParameters.get(1));

    assertFalse(operation.isIndexAppropriate());
  }

  @Test
  public void getIndexA_returnsCurrentParameterIndex() {
    UserMethod method = createMethodWithParameters("first", "second", "third");
    ExposedBackwardShiftParameterOperation operation = new ExposedBackwardShiftParameterOperation(method, method.requiredParameters.get(1));

    assertEquals(1, operation.exposeIndexA());
  }

  @Test
  public void middleParameter_remainsAppropriate() {
    UserMethod method = createMethodWithParameters("first", "second", "third");
    ExposedBackwardShiftParameterOperation operation = new ExposedBackwardShiftParameterOperation(method, method.requiredParameters.get(1));

    assertTrue(operation.isIndexAppropriate());
  }
}
