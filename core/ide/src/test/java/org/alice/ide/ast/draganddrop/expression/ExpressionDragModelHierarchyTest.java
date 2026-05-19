package org.alice.ide.ast.draganddrop.expression;

import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class ExpressionDragModelHierarchyTest {
  @Test
  public void abstractExpressionDragModel_isAbstract() {
    assertTrue(Modifier.isAbstract(AbstractExpressionDragModel.class.getModifiers()));
  }
  @Test
  public void fieldAccessDragModel_extendsAbstract() {
    assertTrue(AbstractExpressionDragModel.class.isAssignableFrom(FieldAccessDragModel.class));
  }
  @Test
  public void fieldArrayAtIndexDragModel_extendsAbstract() {
    assertTrue(AbstractExpressionDragModel.class.isAssignableFrom(FieldArrayAtIndexDragModel.class));
  }
  @Test
  public void fieldArrayLengthDragModel_extendsAbstract() {
    assertTrue(AbstractExpressionDragModel.class.isAssignableFrom(FieldArrayLengthDragModel.class));
  }
  @Test
  public void functionInvocationDragModel_extendsAbstract() {
    assertTrue(AbstractExpressionDragModel.class.isAssignableFrom(FunctionInvocationDragModel.class));
  }
  @Test
  public void localAccessDragModel_extendsAbstract() {
    assertTrue(AbstractExpressionDragModel.class.isAssignableFrom(LocalAccessDragModel.class));
  }
  @Test
  public void parameterAccessDragModel_extendsAbstract() {
    assertTrue(AbstractExpressionDragModel.class.isAssignableFrom(ParameterAccessDragModel.class));
  }
  @Test
  public void thisExpressionDragModel_extendsAbstract() {
    assertTrue(AbstractExpressionDragModel.class.isAssignableFrom(ThisExpressionDragModel.class));
  }
  @Test
  public void allConcreteExpressionDragModels_areNotAbstract() {
    Class<?>[] concretes = { FieldAccessDragModel.class, FieldArrayAtIndexDragModel.class,
        FieldArrayLengthDragModel.class, FunctionInvocationDragModel.class,
        LocalAccessDragModel.class, ParameterAccessDragModel.class,
        ThisExpressionDragModel.class };
    for (Class<?> c : concretes) {
      assertFalse(c.getSimpleName() + " should be concrete", Modifier.isAbstract(c.getModifiers()));
    }
  }
}
