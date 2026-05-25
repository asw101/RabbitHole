package org.alice.ide.ast.code;

import org.alice.ide.ast.code.edits.MoveStatementEdit;
import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Comment;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class MoveStatementOperationBehaviorTest {
  private static void invokePerform(MoveStatementOperation operation, UserActivity activity) throws Exception {
    Method method = MoveStatementOperation.class.getDeclaredMethod("perform", UserActivity.class);
    method.setAccessible(true);
    method.invoke(operation, activity);
  }

  @Test
  public void perform_movesStatementAndCommitsEdit() throws Exception {
    BlockStatement from = new BlockStatement();
    BlockStatement to = new BlockStatement();
    Comment statement = new Comment("move me");
    from.statements.add(statement);

    MoveStatementOperation operation = new MoveStatementOperation(
        new BlockStatementIndexPair(from, 0),
        statement,
        new BlockStatementIndexPair(to, 0),
        false);
    UserActivity activity = new UserActivity();

    invokePerform(operation, activity);

    assertTrue(from.statements.isEmpty());
    assertEquals(1, to.statements.size());
    assertSame(statement, to.statements.get(0));
    assertTrue(activity.isSuccessfullyCompleted());
    assertTrue(activity.getEdit() instanceof MoveStatementEdit);
  }

  @Test
  public void committedEdit_canUndoMovedStatement() throws Exception {
    BlockStatement from = new BlockStatement();
    BlockStatement to = new BlockStatement();
    Comment statement = new Comment("move me");
    from.statements.add(statement);

    MoveStatementOperation operation = new MoveStatementOperation(
        new BlockStatementIndexPair(from, 0),
        statement,
        new BlockStatementIndexPair(to, 0),
        false);
    UserActivity activity = new UserActivity();

    invokePerform(operation, activity);
    activity.getEdit().undo();

    assertEquals(1, from.statements.size());
    assertSame(statement, from.statements.get(0));
    assertTrue(to.statements.isEmpty());
  }
}
