package org.alice.ide.ast.code;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import java.util.UUID;

import static org.junit.Assert.*;

public class SwapParametersOperationCoverageTest {
  private static final class TestSwapParametersOperation extends SwapParametersOperation {
    private final int indexA;

    private TestSwapParametersOperation(UserMethod method, UserParameter parameter, int indexA) {
      super(UUID.fromString("11111111-1111-1111-1111-111111111111"), method.getRequiredParamtersProperty(), parameter);
      this.indexA = indexA;
    }

    @Override
    protected boolean isAppropriate(int index, int n) {
      return index >= 0 && index < (n - 1);
    }

    @Override
    protected int getIndexA() {
      return this.indexA;
    }

    private UserParameter exposeParameter() {
      return this.getParameter();
    }
  }

  @Test
  public void isIndexAppropriate_usesSubclassPredicate() {
    UserMethod method = createMethod();
    TestSwapParametersOperation operation = new TestSwapParametersOperation(method, method.requiredParameters.get(1), 1);
    assertTrue(operation.isIndexAppropriate());
  }

  @Test
  public void lastParameter_isIndexAppropriate_returnsFalse() {
    UserMethod method = createMethod();
    TestSwapParametersOperation operation = new TestSwapParametersOperation(method, method.requiredParameters.get(2), 1);
    assertFalse(operation.isIndexAppropriate());
  }

  @Test
  public void subclassCanExposeProtectedParameter() {
    UserMethod method = createMethod();
    UserParameter parameter = method.requiredParameters.get(0);
    TestSwapParametersOperation operation = new TestSwapParametersOperation(method, parameter, 0);
    assertSame(parameter, operation.exposeParameter());
  }

  @Test
  public void getIndexA_returnsSubclassValue() {
    UserMethod method = createMethod();
    TestSwapParametersOperation operation = new TestSwapParametersOperation(method, method.requiredParameters.get(1), 7);
    assertEquals(7, operation.getIndexA());
  }

  private UserMethod createMethod() {
    return new UserMethod("sample", Object.class, new UserParameter[]{
        new UserParameter("first", String.class),
        new UserParameter("second", Integer.class),
        new UserParameter("third", Double.class)
    }, new BlockStatement());
  }
}
