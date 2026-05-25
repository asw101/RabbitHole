package org.alice.ide.ast.code;

import org.alice.ide.ast.code.edits.EnvelopStatementsEdit;
import org.alice.ide.ast.code.edits.MoveStatementEdit;
import org.alice.ide.ast.code.edits.SwapParametersEdit;
import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NodeUtilities;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CodeGenerationHelperBehaviorTest {
  @Test
  public void moveStatementEditBuildsDescriptionFromMovedStatement() {
    BlockStatement fromBlock = blockWithComments("move me");
    Comment statement = (Comment) fromBlock.statements.get(0);
    MoveStatementEdit edit = new MoveStatementEdit(
        null,
        pair(fromBlock, 0),
        statement,
        pair(new BlockStatement(), 0),
        false);

    StringBuilder expected = new StringBuilder("move: ");
    NodeUtilities.safeAppendRepr(expected, statement, Application.getLocale());

    assertEquals(expected.toString(), edit.getTerseDescription());
    assertTrue(edit.getRedoPresentation().contains(expected.toString()));
    assertTrue(edit.getUndoPresentation().contains(expected.toString()));
  }

  @Test
  public void envelopStatementsEditUsesStatementAtFromLocation() {
    BlockStatement block = blockWithComments("before", "enveloped");
    Comment enveloped = (Comment) block.statements.get(1);
    EnvelopStatementsEdit edit = new EnvelopStatementsEdit(
        null,
        pair(block, 1),
        pair(new BlockStatement(), 0));

    StringBuilder expected = new StringBuilder("envelop: ");
    NodeUtilities.safeAppendRepr(expected, enveloped, Application.getLocale());

    assertEquals(expected.toString(), edit.getTerseDescription());
  }

  @Test
  public void swapParametersEditDescribesTheIndexedAdjacentPair() {
    UserMethod method = new UserMethod();
    method.name.setValue("doSomething");
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.requiredParameters.add(new UserParameter("first", String.class));
    UserParameter second = new UserParameter("second", Integer.class);
    UserParameter third = new UserParameter("third", Double.class);
    method.requiredParameters.add(second);
    method.requiredParameters.add(third);

    SwapParametersEdit edit = new SwapParametersEdit(null, method, 1);

    StringBuilder expected = new StringBuilder("Swap Parameters ");
    NodeUtilities.safeAppendRepr(expected, second, null);
    expected.append(" ");
    NodeUtilities.safeAppendRepr(expected, third, null);

    assertEquals(expected.toString(), edit.getTerseDescription());
    assertTrue(edit.getDetailedDescription().endsWith(expected.toString()));
  }

  private static BlockStatement blockWithComments(String... texts) {
    BlockStatement block = new BlockStatement();
    for (String text : texts) {
      block.statements.add(new Comment(text));
    }
    return block;
  }

  private static BlockStatementIndexPair pair(BlockStatement block, int index) {
    return new BlockStatementIndexPair(block, index);
  }
}
