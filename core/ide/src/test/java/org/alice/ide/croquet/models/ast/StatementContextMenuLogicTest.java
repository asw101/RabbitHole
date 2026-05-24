package org.alice.ide.croquet.models.ast;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.DoInOrder;
import org.lgna.project.ast.DoTogether;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaKeyedArgument;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.SimpleArgument;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class StatementContextMenuLogicTest {
  @Test
  public void shouldAddExecutionControls_skipsComments() {
    assertFalse(StatementContextMenuLogic.shouldAddExecutionControls(new Comment("note")));
    assertTrue(StatementContextMenuLogic.shouldAddExecutionControls(new ExpressionStatement()));
  }

  @Test
  public void getInvokedUserMethod_returnsUserMethodOnly() {
    UserMethod userMethod = new UserMethod("helper", void.class, new UserParameter[0], new BlockStatement());
    ExpressionStatement statement = new ExpressionStatement(new MethodInvocation(new NullLiteral(), userMethod));

    assertSame(userMethod, StatementContextMenuLogic.getInvokedUserMethod(statement));
    assertSame(null, StatementContextMenuLogic.getInvokedUserMethod(new ExpressionStatement(new MethodInvocation(new StringLiteral("hello"), JavaMethod.getInstance(String.class, "trim")))));
  }

  @Test
  public void shouldAddDelete_requiresBlockParent() {
    ExpressionStatement detached = new ExpressionStatement();
    ExpressionStatement attached = new ExpressionStatement();
    new BlockStatement().statements.add(attached);

    assertFalse(StatementContextMenuLogic.shouldAddDelete(detached));
    assertTrue(StatementContextMenuLogic.shouldAddDelete(attached));
  }

  @Test
  public void getConversionAction_distinguishesLoopTypes() {
    assertEquals(StatementContextMenuLogic.ConversionAction.DO_IN_ORDER_TO_DO_TOGETHER, StatementContextMenuLogic.getConversionAction(new DoInOrder()));
    assertEquals(StatementContextMenuLogic.ConversionAction.DO_TOGETHER_TO_DO_IN_ORDER, StatementContextMenuLogic.getConversionAction(new DoTogether()));
    assertEquals(StatementContextMenuLogic.ConversionAction.NONE, StatementContextMenuLogic.getConversionAction(new ExpressionStatement()));
  }

  @Test
  public void getRemovableKeyedArguments_returnsKeyedArgumentsForExpressionStatements() {
    JavaMethod parameterOwner = JavaMethod.getInstance(String.class, "substring", int.class);
    JavaKeyedArgument keyedArgument = new JavaKeyedArgument(parameterOwner.getRequiredParameters().get(0), JavaMethod.getInstance(String.class, "valueOf", int.class), new IntegerLiteral(2));
    MethodInvocation invocation = new MethodInvocation(
        new StringLiteral("hello"),
        parameterOwner,
        new SimpleArgument[] {new SimpleArgument(parameterOwner.getRequiredParameters().get(0), new IntegerLiteral(1))},
        null,
        new JavaKeyedArgument[] {keyedArgument});

    List<JavaKeyedArgument> removable = StatementContextMenuLogic.getRemovableKeyedArguments(new ExpressionStatement(invocation));

    assertEquals(1, removable.size());
    assertSame(keyedArgument, removable.get(0));
  }
}
