package org.alice.ide.cascade;

import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.junit.Test;
import org.lgna.project.ast.Expression;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ExpressionCascadeContextStructureTest {
  @Test
  public void typeIsPublicInterface() {
    assertTrue(ExpressionCascadeContext.class.isInterface());
    assertTrue(Modifier.isPublic(ExpressionCascadeContext.class.getModifiers()));
  }

  @Test
  public void methodsMatchExpectedContract() throws Exception {
    Method previousExpression = ExpressionCascadeContext.class.getMethod("getPreviousExpression");
    Method blockStatementIndexPair = ExpressionCascadeContext.class.getMethod("getBlockStatementIndexPair");

    assertEquals(Expression.class, previousExpression.getReturnType());
    assertEquals(BlockStatementIndexPair.class, blockStatementIndexPair.getReturnType());
    assertEquals(0, previousExpression.getParameterCount());
    assertEquals(0, blockStatementIndexPair.getParameterCount());
  }

  @Test
  public void onlyExpectedMethodNamesAreDeclared() {
    assertEquals(
        Arrays.asList("getBlockStatementIndexPair", "getPreviousExpression"),
        Arrays.stream(ExpressionCascadeContext.class.getDeclaredMethods())
            .map(Method::getName)
            .sorted()
            .collect(Collectors.toList()));
  }
}
