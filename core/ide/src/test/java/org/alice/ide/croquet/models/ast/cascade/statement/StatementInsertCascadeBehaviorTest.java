package org.alice.ide.croquet.models.ast.cascade.statement;

import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.alice.ide.croquet.edits.ast.InsertStatementEdit;
import org.alice.ide.croquet.models.cascade.ExpressionBlank;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.AssignmentExpression;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.LocalAccess;
import org.lgna.project.ast.Statement;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserLocal;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class StatementInsertCascadeBehaviorTest {
  @Test
  public void createEdit_preservesBlockPairExpressionsAndEnvelopingFlag() throws Exception {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 3);
    ExpressionStatement statement = new ExpressionStatement(new IntegerLiteral(5));
    TestStatementInsertCascade cascade = new TestStatementInsertCascade(pair, true, statement);
    IntegerLiteral rightHandSide = new IntegerLiteral(9);

    InsertStatementEdit<?> edit = cascade.createEditForTest(rightHandSide);

    assertSame(block, edit.getBlockStatement());
    assertEquals(3, edit.getSpecifiedIndex());
    assertSame(statement, edit.getStatement());
    assertSame(rightHandSide, edit.getInitialExpressions()[0]);
    assertTrue(getIsEnveloping(edit));
  }

  @Test
  public void prologueAndEpilogue_toggleEpicHackFlag() {
    BlockStatementIndexPair pair = new BlockStatementIndexPair(new BlockStatement(), 0);
    TestStatementInsertCascade cascade = new TestStatementInsertCascade(pair, false, new ExpressionStatement());

    assertFalse(StatementInsertCascade.EPIC_HACK_isActive());
    cascade.runPrologue();
    assertTrue(StatementInsertCascade.EPIC_HACK_isActive());
    cascade.runEpilogue();
    assertFalse(StatementInsertCascade.EPIC_HACK_isActive());
  }

  @Test
  public void fieldAssignmentInsertCascade_cachesInstancesAndBuildsFieldAssignments() {
    BlockStatementIndexPair pair = new BlockStatementIndexPair(new BlockStatement(), 0);
    UserField field = new UserField("score", Integer.TYPE, new IntegerLiteral(0));
    IntegerLiteral value = new IntegerLiteral(42);
    FieldAssignmentInsertCascade first = FieldAssignmentInsertCascade.getInstance(pair, field);
    FieldAssignmentInsertCascade second = FieldAssignmentInsertCascade.getInstance(pair, field);

    AssignmentExpression expression = (AssignmentExpression)first.createExpression(value);

    assertSame(first, second);
    assertEquals(JavaType.getInstance(Integer.TYPE), expression.expressionType.getValue());
    assertSame(AssignmentExpression.Operator.ASSIGN, expression.operator.getValue());
    assertSame(value, expression.rightHandSide.getValue());
    assertSame(field, ((FieldAccess)expression.leftHandSide.getValue()).field.getValue());
  }

  @Test
  public void localAssignmentInsertCascade_buildsLocalAssignmentUsingFirstBlankAsRightHandSide() {
    BlockStatementIndexPair pair = new BlockStatementIndexPair(new BlockStatement(), 0);
    UserLocal local = new UserLocal("score", Integer.TYPE, false);
    LocalAssignmentInsertCascade cascade = new LocalAssignmentInsertCascade(pair, local);
    IntegerLiteral value = new IntegerLiteral(7);

    AssignmentExpression expression = (AssignmentExpression)cascade.createExpression(value);

    assertEquals(JavaType.getInstance(Integer.TYPE), expression.expressionType.getValue());
    assertSame(AssignmentExpression.Operator.ASSIGN, expression.operator.getValue());
    assertSame(value, expression.rightHandSide.getValue());
    assertSame(local, ((LocalAccess)expression.leftHandSide.getValue()).local.getValue());
  }

  private static boolean getIsEnveloping(InsertStatementEdit<?> edit) throws Exception {
    Field field = InsertStatementEdit.class.getDeclaredField("isEnveloping");
    field.setAccessible(true);
    return field.getBoolean(edit);
  }

  private static final class TestStatementInsertCascade extends StatementInsertCascade {
    private final Statement statement;

    private TestStatementInsertCascade(BlockStatementIndexPair blockStatementIndexPair, boolean isEnveloping, Statement statement) {
      super(UUID.fromString("20c4fd83-c976-4f6e-9f6e-95d403d75fd6"), blockStatementIndexPair, isEnveloping,
          ExpressionBlank.createBlanks(Integer.TYPE));
      this.statement = statement;
    }

    @Override
    protected Statement createStatement(Expression... expressions) {
      return this.statement;
    }

    private InsertStatementEdit<?> createEditForTest(Expression... expressions) {
      return this.createEdit(new UserActivity(), expressions);
    }

    private void runPrologue() {
      this.prologue();
    }

    private void runEpilogue() {
      this.epilogue();
    }
  }
}
