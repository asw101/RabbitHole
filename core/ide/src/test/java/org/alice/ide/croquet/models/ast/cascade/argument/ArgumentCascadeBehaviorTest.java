package org.alice.ide.croquet.models.ast.cascade.argument;

import org.alice.ide.croquet.models.ast.cascade.AbstractArgumentCascade;
import org.alice.ide.croquet.models.ast.cascade.ArgumentCascade;
import org.alice.ide.croquet.models.cascade.ParameterBlank;
import org.junit.Test;
import org.lgna.croquet.CascadeBlank;
import org.lgna.croquet.ImmutableCascade;
import org.lgna.project.ast.DoubleLiteral;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.SimpleArgument;
import org.lgna.project.ast.UserParameter;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class ArgumentCascadeBehaviorTest {
  @Test
  public void getInstanceCachesBySimpleArgumentAndUsesParameterBlank() throws Exception {
    UserParameter parameter = new UserParameter("amount", Number.class);
    SimpleArgument argument = new SimpleArgument(parameter, new DoubleLiteral(1.5));

    ArgumentCascade first = ArgumentCascade.getInstance(argument);
    ArgumentCascade second = ArgumentCascade.getInstance(argument);

    assertSame(first, second);
    assertSame(argument, first.getArgument());
    assertSame(argument.expression, first.getExpressionProperty());

    List<? extends CascadeBlank<Expression>> blanks = getBlanks(first);
    assertEquals(1, blanks.size());
    assertSame(ParameterBlank.getInstance(parameter), blanks.get(0));
  }

  @Test
  public void getInstanceKeepsDistinctArgumentNodesSeparate() {
    UserParameter sharedParameter = new UserParameter("amount", Number.class);
    SimpleArgument firstArgument = new SimpleArgument(sharedParameter, new DoubleLiteral(1.0));
    SimpleArgument secondArgument = new SimpleArgument(sharedParameter, new DoubleLiteral(2.0));

    assertNotSame(ArgumentCascade.getInstance(firstArgument), ArgumentCascade.getInstance(secondArgument));
  }

  @Test
  public void abstractArgumentCascadeBuildsReplacementFromSingleExpression() {
    SimpleArgument argument = new SimpleArgument(new UserParameter("count", Integer.class), new IntegerLiteral(1));
    TestArgumentCascade cascade = new TestArgumentCascade(argument);
    IntegerLiteral replacement = new IntegerLiteral(7);

    assertSame(replacement, cascade.compute(replacement));
    assertSame(argument, cascade.getArgument());
  }

  @SuppressWarnings("unchecked")
  private static List<? extends CascadeBlank<Expression>> getBlanks(ArgumentCascade cascade) throws Exception {
    Method method = ImmutableCascade.class.getDeclaredMethod("getBlanks");
    method.setAccessible(true);
    return (List<? extends CascadeBlank<Expression>>) method.invoke(cascade);
  }

  private static final class TestArgumentCascade extends AbstractArgumentCascade {
    private TestArgumentCascade(SimpleArgument argument) {
      super(UUID.fromString("497ec217-ee62-4a48-b576-f4a8d5f0cb55"), argument);
    }

    private Expression compute(Expression expression) {
      return createExpression(new Expression[] {expression});
    }
  }
}
