package org.alice.ide.ast.draganddrop.statement;

import org.junit.Test;
import static org.junit.Assert.*;

public class StatementTemplateDragModelComprehensiveTest {
  @Test public void commentInstance_notNull() { assertNotNull(CommentTemplateDragModel.getInstance()); }
  @Test public void commentInstance_singleton() { assertSame(CommentTemplateDragModel.getInstance(), CommentTemplateDragModel.getInstance()); }
  @Test public void commentInstance_statementCls() { assertEquals(org.lgna.project.ast.Comment.class, CommentTemplateDragModel.getInstance().getStatementCls()); }
  @Test public void commentInstance_possiblyIncomplete_notNull() { assertNotNull(CommentTemplateDragModel.getInstance().getPossiblyIncompleteStatement()); }
  @Test public void commentInstance_notEventListener() { assertFalse(CommentTemplateDragModel.getInstance().isAddEventListenerLikeSubstance()); }
  @Test public void commentInstance_isTemplate() { assertTrue(CommentTemplateDragModel.getInstance() instanceof StatementTemplateDragModel); }
  @Test public void conditionalInstance_notNull() { assertNotNull(ConditionalStatementTemplateDragModel.getInstance()); }
  @Test public void conditionalInstance_singleton() { assertSame(ConditionalStatementTemplateDragModel.getInstance(), ConditionalStatementTemplateDragModel.getInstance()); }
  @Test public void conditionalInstance_statementCls() { assertEquals(org.lgna.project.ast.ConditionalStatement.class, ConditionalStatementTemplateDragModel.getInstance().getStatementCls()); }
  @Test public void conditionalInstance_possiblyIncomplete() { assertNotNull(ConditionalStatementTemplateDragModel.getInstance().getPossiblyIncompleteStatement()); }
  @Test public void countLoopInstance_notNull() { assertNotNull(CountLoopTemplateDragModel.getInstance()); }
  @Test public void countLoopInstance_singleton() { assertSame(CountLoopTemplateDragModel.getInstance(), CountLoopTemplateDragModel.getInstance()); }
  @Test public void countLoopInstance_statementCls() { assertEquals(org.lgna.project.ast.CountLoop.class, CountLoopTemplateDragModel.getInstance().getStatementCls()); }
  @Test public void whileLoopInstance_notNull() { assertNotNull(WhileLoopTemplateDragModel.getInstance()); }
  @Test public void whileLoopInstance_singleton() { assertSame(WhileLoopTemplateDragModel.getInstance(), WhileLoopTemplateDragModel.getInstance()); }
  @Test public void whileLoopInstance_statementCls() { assertEquals(org.lgna.project.ast.WhileLoop.class, WhileLoopTemplateDragModel.getInstance().getStatementCls()); }
  @Test public void doInOrderInstance_notNull() { assertNotNull(DoInOrderTemplateDragModel.getInstance()); }
  @Test public void doInOrderInstance_singleton() { assertSame(DoInOrderTemplateDragModel.getInstance(), DoInOrderTemplateDragModel.getInstance()); }
  @Test public void doInOrderInstance_statementCls() { assertEquals(org.lgna.project.ast.DoInOrder.class, DoInOrderTemplateDragModel.getInstance().getStatementCls()); }
  @Test public void doTogetherInstance_notNull() { assertNotNull(DoTogetherTemplateDragModel.getInstance()); }
  @Test public void doTogetherInstance_singleton() { assertSame(DoTogetherTemplateDragModel.getInstance(), DoTogetherTemplateDragModel.getInstance()); }
  @Test public void doTogetherInstance_statementCls() { assertEquals(org.lgna.project.ast.DoTogether.class, DoTogetherTemplateDragModel.getInstance().getStatementCls()); }
  @Test public void forEachInstance_notNull() { assertNotNull(ForEachInArrayLoopTemplateDragModel.getInstance()); }
  @Test public void eachInArrayInstance_notNull() { assertNotNull(EachInArrayTogetherTemplateDragModel.getInstance()); }
  @Test public void declareLocalInstance_notNull() { assertNotNull(DeclareLocalDragModel.getInstance()); }
  @Test public void allInstances_different() {
    assertNotSame(CommentTemplateDragModel.getInstance(), ConditionalStatementTemplateDragModel.getInstance());
    assertNotSame(CommentTemplateDragModel.getInstance(), CountLoopTemplateDragModel.getInstance());
    assertNotSame(CommentTemplateDragModel.getInstance(), WhileLoopTemplateDragModel.getInstance());
  }
  @Test public void allInstances_areTemplates() {
    assertTrue(CommentTemplateDragModel.getInstance() instanceof StatementTemplateDragModel);
    assertTrue(ConditionalStatementTemplateDragModel.getInstance() instanceof StatementTemplateDragModel);
    assertTrue(CountLoopTemplateDragModel.getInstance() instanceof StatementTemplateDragModel);
  }
  @Test public void allInstances_notEventListener() {
    assertFalse(CountLoopTemplateDragModel.getInstance().isAddEventListenerLikeSubstance());
    assertFalse(WhileLoopTemplateDragModel.getInstance().isAddEventListenerLikeSubstance());
    assertFalse(DoInOrderTemplateDragModel.getInstance().isAddEventListenerLikeSubstance());
  }
}
