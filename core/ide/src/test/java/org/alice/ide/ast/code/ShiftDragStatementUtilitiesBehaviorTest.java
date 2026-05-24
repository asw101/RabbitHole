package org.alice.ide.ast.code;

import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.alice.ide.ast.draganddrop.statement.DoTogetherTemplateDragModel;
import org.alice.ide.ast.draganddrop.statement.StatementDragModel;
import org.junit.Test;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.DoInOrder;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.Statement;
import org.lgna.project.ast.UserField;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ShiftDragStatementUtilitiesBehaviorTest {
  private static Statement statement(String name) {
    UserField field = new UserField(name, Object.class);
    return new ExpressionStatement(new MethodInvocation(new FieldAccess(field), JavaMethod.getInstance(Object.class, "toString")));
  }

  @Test
  public void calculateShiftMoveCountUsesDirectionWithinTheSameBlock() {
    BlockStatement block = new BlockStatement();
    block.statements.add(statement("a"));
    block.statements.add(statement("b"));
    block.statements.add(statement("c"));
    block.statements.add(statement("d"));

    assertEquals(2, ShiftDragStatementUtilities.calculateShiftMoveCount(
        new BlockStatementIndexPair(block, 2), new BlockStatementIndexPair(block, 1)));
    assertEquals(0, ShiftDragStatementUtilities.calculateShiftMoveCount(
        new BlockStatementIndexPair(block, 1), new BlockStatementIndexPair(block, 3)));
  }

  @Test
  public void calculateShiftMoveCountStopsWhenTheDestinationIsInsideAnAncestorStatement() {
    BlockStatement outer = new BlockStatement();
    DoInOrder container = AstUtilities.createDoInOrder();
    outer.statements.add(statement("before"));
    outer.statements.add(container);
    outer.statements.add(statement("after"));

    assertEquals(1, ShiftDragStatementUtilities.calculateShiftMoveCount(
        new BlockStatementIndexPair(outer, 0), new BlockStatementIndexPair(container.body.getValue(), 0)));
  }

  @Test
  public void isCandidateForEnvelopAcceptsTemplateDragModelsButNotExistingStatements() {
    assertTrue(ShiftDragStatementUtilities.isCandidateForEnvelop(DoTogetherTemplateDragModel.getInstance()));
    assertFalse(ShiftDragStatementUtilities.isCandidateForEnvelop(StatementDragModel.getInstance(AstUtilities.createDoTogether())));
  }
}
