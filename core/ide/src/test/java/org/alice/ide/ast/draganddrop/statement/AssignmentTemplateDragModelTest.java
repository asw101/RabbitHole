package org.alice.ide.ast.draganddrop.statement;

import org.junit.Test;
import org.lgna.project.ast.ExpressionStatement;
import static org.junit.Assert.*;

public class AssignmentTemplateDragModelTest {
  @Test public void getInstance_notNull() { assertNotNull(AssignmentTemplateDragModel.getInstance()); }
  @Test public void getInstance_singleton() { assertSame(AssignmentTemplateDragModel.getInstance(), AssignmentTemplateDragModel.getInstance()); }
  @Test public void statementCls_expressionStatement() { assertEquals(ExpressionStatement.class, AssignmentTemplateDragModel.getInstance().getStatementCls()); }
  @Test public void possiblyIncompleteStatement_notNull() { assertNotNull(AssignmentTemplateDragModel.getInstance().getPossiblyIncompleteStatement()); }
  @Test public void possiblyIncompleteStatement_isExpressionStatement() { assertTrue(AssignmentTemplateDragModel.getInstance().getPossiblyIncompleteStatement() instanceof ExpressionStatement); }
  @Test public void isAddEventListenerLikeSubstance_false() { assertFalse(AssignmentTemplateDragModel.getInstance().isAddEventListenerLikeSubstance()); }
  @Test public void hierarchy_isStatementTemplateDragModel() { assertTrue(AssignmentTemplateDragModel.getInstance() instanceof StatementTemplateDragModel); }
  @Test public void hierarchy_isExpressionStatementTemplateDragModel() { assertTrue(AssignmentTemplateDragModel.getInstance() instanceof ExpressionStatementTemplateDragModel); }
}
