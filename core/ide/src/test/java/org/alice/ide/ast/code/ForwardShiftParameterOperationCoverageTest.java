package org.alice.ide.ast.code;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.NodeListProperty;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import static org.junit.Assert.*;

public class ForwardShiftParameterOperationCoverageTest {
  private static final class TestableForwardShiftParameterOperation extends ForwardShiftParameterOperation {
    private TestableForwardShiftParameterOperation(NodeListProperty<UserParameter> parametersProperty, UserParameter parameter) {
      super(parametersProperty, parameter);
    }

    private int exposeIndexA() {
      return this.getIndexA();
    }
  }

  @Test
  public void firstParameter_isIndexAppropriate_returnsFalse() {
    UserMethod method = createMethod();
    TestableForwardShiftParameterOperation operation = new TestableForwardShiftParameterOperation(
        method.getRequiredParamtersProperty(), method.requiredParameters.get(0));
    assertFalse(operation.isIndexAppropriate());
  }

  @Test
  public void middleParameter_isIndexAppropriate_returnsTrue() {
    UserMethod method = createMethod();
    TestableForwardShiftParameterOperation operation = new TestableForwardShiftParameterOperation(
        method.getRequiredParamtersProperty(), method.requiredParameters.get(1));
    assertTrue(operation.isIndexAppropriate());
  }

  @Test
  public void getIndexA_returnsPreviousIndex() {
    UserMethod method = createMethod();
    TestableForwardShiftParameterOperation operation = new TestableForwardShiftParameterOperation(
        method.getRequiredParamtersProperty(), method.requiredParameters.get(2));
    assertEquals(1, operation.exposeIndexA());
  }

  @Test
  public void secondParameter_isIndexAppropriate_returnsTrue() {
    UserMethod method = createMethod();
    TestableForwardShiftParameterOperation operation = new TestableForwardShiftParameterOperation(
        method.getRequiredParamtersProperty(), method.requiredParameters.get(1));
    assertTrue(operation.isIndexAppropriate());
  }

  private UserMethod createMethod() {
    return new UserMethod("sample", Object.class, new UserParameter[]{
        new UserParameter("first", String.class),
        new UserParameter("second", Integer.class),
        new UserParameter("third", Double.class)
    }, new BlockStatement());
  }
}
