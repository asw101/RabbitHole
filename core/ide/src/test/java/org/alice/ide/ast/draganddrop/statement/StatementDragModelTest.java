package org.alice.ide.ast.draganddrop.statement;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

public class StatementDragModelTest {

  @Test
  public void getInstanceForConstructorInvocationReturnsNull() {
    ConstructorInvocationStatement cis = new SuperConstructorInvocationStatement();
    StatementDragModel model = StatementDragModel.getInstance(cis);
    assertNull(model);
  }

  @Test
  public void getInstanceForCommentReturnsNonNull() {
    Comment comment = new Comment("test");
    StatementDragModel model = StatementDragModel.getInstance(comment);
    assertNotNull(model);
    assertSame(comment, model.getStatement());
  }

  @Test
  public void getInstanceReturnsSameForSameStatement() {
    Comment comment = new Comment("same");
    StatementDragModel m1 = StatementDragModel.getInstance(comment);
    StatementDragModel m2 = StatementDragModel.getInstance(comment);
    assertSame(m1, m2);
  }

  @Test
  public void getTypeReturnsVoid() {
    Comment comment = new Comment("type test");
    StatementDragModel model = StatementDragModel.getInstance(comment);
    assertEquals(JavaType.VOID_TYPE, model.getType());
  }

  @Test
  public void isAddEventListenerLikeSubstance() {
    Comment comment = new Comment("listener test");
    StatementDragModel model = StatementDragModel.getInstance(comment);
    assertFalse(model.isAddEventListenerLikeSubstance());
  }

  @Test
  public void getDropOperationThrows() {
    Comment comment = new Comment("drop test");
    StatementDragModel model = StatementDragModel.getInstance(comment);
    try {
      model.getDropOperation(null, null);
      fail("Expected RuntimeException");
    } catch (RuntimeException e) {
      assertEquals("todo", e.getMessage());
    }
  }

  @Test
  public void getInstanceForExpressionStatement() {
    ExpressionStatement es = new ExpressionStatement();
    StatementDragModel model = StatementDragModel.getInstance(es);
    assertNotNull(model);
    assertSame(es, model.getStatement());
  }

  @Test
  public void getInstanceForDoInOrder() {
    DoInOrder dio = new DoInOrder();
    StatementDragModel model = StatementDragModel.getInstance(dio);
    assertNotNull(model);
    assertFalse(model.isAddEventListenerLikeSubstance());
  }
}
