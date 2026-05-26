package org.alice.ide.croquet.models.ast.cascade.statement;

import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.alice.ide.croquet.edits.ast.InsertStatementEdit;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.ArrayAccess;
import org.lgna.project.ast.AssignmentExpression;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.BooleanLiteral;
import org.lgna.project.ast.ConditionalStatement;
import org.lgna.project.ast.DoInOrder;
import org.lgna.project.ast.DoTogether;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.LocalAccess;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.ReturnStatement;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserLocal;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.ast.WhileLoop;
import org.lgna.project.ast.CountLoop;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class RemainingStatementCascadeItemsTest {
  @Test
  public void templateStatementOperations_cacheByPairAndBuildDoStatements() throws Exception {
    BlockStatementIndexPair pair = new BlockStatementIndexPair(new BlockStatement(), 2);
    DoInOrderInsertOperation doInOrder = DoInOrderInsertOperation.getInstance(pair, true);
    DoTogetherInsertOperation doTogether = DoTogetherInsertOperation.getInstance(pair, false);

    assertSame(doInOrder, DoInOrderInsertOperation.getInstance(pair, true));
    assertNotSame(doInOrder, DoInOrderInsertOperation.getInstance(pair, false));
    assertSame(doTogether, DoTogetherInsertOperation.getInstance(pair, false));
    assertNotSame(doTogether, DoTogetherInsertOperation.getInstance(pair, true));

    InsertStatementEdit<?> doInOrderEdit = (InsertStatementEdit<?>) doInOrder.createEdit(new UserActivity());
    InsertStatementEdit<?> doTogetherEdit = (InsertStatementEdit<?>) doTogether.createEdit(new UserActivity());

    assertTrue(doInOrderEdit.getStatement() instanceof DoInOrder);
    assertEquals(0, doInOrderEdit.getInitialExpressions().length);
    assertTrue(getIsEnveloping(doInOrderEdit));
    assertTrue(doTogetherEdit.getStatement() instanceof DoTogether);
    assertEquals(0, doTogetherEdit.getInitialExpressions().length);
    assertFalse(getIsEnveloping(doTogetherEdit));
  }

  @Test
  public void controlFlowStatementCascades_cacheByPairAndBuildExpectedStatements() throws Exception {
    BlockStatementIndexPair pair = new BlockStatementIndexPair(new BlockStatement(), 1);
    BooleanLiteral condition = new BooleanLiteral(true);
    IntegerLiteral count = new IntegerLiteral(4);

    ConditionalStatementInsertCascade conditional = ConditionalStatementInsertCascade.getInstance(pair, true);
    WhileLoopInsertCascade whileLoop = WhileLoopInsertCascade.getInstance(pair, false);
    CountLoopInsertCascade countLoop = CountLoopInsertCascade.getInstance(pair, true);

    assertSame(conditional, ConditionalStatementInsertCascade.getInstance(pair, true));
    assertNotSame(conditional, ConditionalStatementInsertCascade.getInstance(pair, false));
    assertSame(whileLoop, WhileLoopInsertCascade.getInstance(pair, false));
    assertNotSame(whileLoop, WhileLoopInsertCascade.getInstance(pair, true));
    assertSame(countLoop, CountLoopInsertCascade.getInstance(pair, true));
    assertNotSame(countLoop, CountLoopInsertCascade.getInstance(pair, false));

    InsertStatementEdit<?> conditionalEdit = conditional.createEdit(new UserActivity(), new Expression[] {condition});
    InsertStatementEdit<?> whileEdit = whileLoop.createEdit(new UserActivity(), new Expression[] {condition});
    InsertStatementEdit<?> countEdit = countLoop.createEdit(new UserActivity(), new Expression[] {count});

    ConditionalStatement conditionalStatement = (ConditionalStatement) conditionalEdit.getStatement();
    WhileLoop whileStatement = (WhileLoop) whileEdit.getStatement();
    CountLoop countStatement = (CountLoop) countEdit.getStatement();

    assertSame(condition, conditionalStatement.booleanExpressionBodyPairs.get(0).expression.getValue());
    assertTrue(getIsEnveloping(conditionalEdit));
    assertSame(condition, whileStatement.conditional.getValue());
    assertFalse(getIsEnveloping(whileEdit));
    assertSame(count, countStatement.count.getValue());
    assertTrue(getIsEnveloping(countEdit));
  }

  @Test
  public void returnAndArrayAssignmentCascades_buildExpectedStatements() {
    BlockStatement body = new BlockStatement();
    new UserMethod("compute", Integer.TYPE, new UserParameter[0], body);
    BlockStatementIndexPair returnPair = new BlockStatementIndexPair(body, 0);
    ReturnStatementInsertCascade returnCascade = ReturnStatementInsertCascade.getInstance(returnPair);
    IntegerLiteral returnValue = new IntegerLiteral(42);

    InsertStatementEdit<?> returnEdit = returnCascade.createEdit(new UserActivity(), new Expression[] {returnValue});
    ReturnStatement returnStatement = (ReturnStatement) returnEdit.getStatement();

    assertSame(JavaType.getInstance(Integer.TYPE), returnStatement.expressionType.getValue());
    assertSame(returnValue, returnStatement.expression.getValue());

    BlockStatementIndexPair pair = new BlockStatementIndexPair(new BlockStatement(), 0);
    UserField field = new UserField("values", JavaType.getInstance(String[].class), new NullLiteral());
    UserLocal local = new UserLocal("localValues", JavaType.getInstance(String[].class), false);
    IntegerLiteral index = new IntegerLiteral(1);
    StringLiteral fieldValue = new StringLiteral("field");
    StringLiteral localValue = new StringLiteral("local");

    FieldArrayAtIndexAssignmentInsertCascade fieldCascade = FieldArrayAtIndexAssignmentInsertCascade.getInstance(pair, field);
    LocalArrayAtIndexAssignmentInsertCascade localCascade = new LocalArrayAtIndexAssignmentInsertCascade(pair, local);

    assertSame(fieldCascade, FieldArrayAtIndexAssignmentInsertCascade.getInstance(pair, field));

    AssignmentExpression fieldAssignment = (AssignmentExpression) fieldCascade.createExpression(index, fieldValue);
    AssignmentExpression localAssignment = (AssignmentExpression) localCascade.createExpression(index, localValue);

    ArrayAccess fieldArrayAccess = (ArrayAccess) fieldAssignment.leftHandSide.getValue();
    ArrayAccess localArrayAccess = (ArrayAccess) localAssignment.leftHandSide.getValue();

    assertSame(JavaType.getInstance(String.class), fieldAssignment.expressionType.getValue());
    assertSame(index, fieldArrayAccess.index.getValue());
    assertSame(field, ((FieldAccess) fieldArrayAccess.array.getValue()).field.getValue());
    assertSame(fieldValue, fieldAssignment.rightHandSide.getValue());
    assertSame(local, ((LocalAccess) localArrayAccess.array.getValue()).local.getValue());
    assertSame(localValue, localAssignment.rightHandSide.getValue());
  }

  @Test
  public void fieldAssignmentFillIns_cacheAndCreateExpectedAssignments() {
    UserField scoreField = new UserField("score", Integer.TYPE, new IntegerLiteral(0));
    UserField valuesField = new UserField("values", JavaType.getInstance(String[].class), new NullLiteral());

    FieldAssignmentFillIn assignmentFillIn = FieldAssignmentFillIn.getInstance(scoreField);
    FieldArrayAtIndexAssignmentFillIn arrayFillIn = FieldArrayAtIndexAssignmentFillIn.getInstance(valuesField);
    IntegerLiteral scoreValue = new IntegerLiteral(7);
    IntegerLiteral arrayIndex = new IntegerLiteral(2);
    StringLiteral arrayValue = new StringLiteral("updated");

    assertSame(assignmentFillIn, FieldAssignmentFillIn.getInstance(scoreField));
    assertSame(arrayFillIn, FieldArrayAtIndexAssignmentFillIn.getInstance(valuesField));

    AssignmentExpression fieldAssignment = assignmentFillIn.createValue(new Expression[] {scoreValue});
    AssignmentExpression arrayAssignment = arrayFillIn.createValue(new Expression[] {arrayIndex, arrayValue});

    assertSame(scoreField, ((FieldAccess) fieldAssignment.leftHandSide.getValue()).field.getValue());
    assertSame(scoreValue, fieldAssignment.rightHandSide.getValue());

    ArrayAccess arrayAccess = (ArrayAccess) arrayAssignment.leftHandSide.getValue();
    assertSame(valuesField, ((FieldAccess) arrayAccess.array.getValue()).field.getValue());
    assertSame(arrayIndex, arrayAccess.index.getValue());
    assertSame(arrayValue, arrayAssignment.rightHandSide.getValue());
  }

  private static boolean getIsEnveloping(InsertStatementEdit<?> edit) throws Exception {
    Field field = InsertStatementEdit.class.getDeclaredField("isEnveloping");
    field.setAccessible(true);
    return field.getBoolean(edit);
  }
}
