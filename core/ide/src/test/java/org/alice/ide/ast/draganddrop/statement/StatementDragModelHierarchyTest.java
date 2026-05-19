package org.alice.ide.ast.draganddrop.statement;

import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class StatementDragModelHierarchyTest {
  @Test
  public void abstractStatementDragModel_isAbstract() {
    assertTrue(Modifier.isAbstract(AbstractStatementDragModel.class.getModifiers()));
  }
  @Test
  public void statementDragModel_isNotAbstract() {
    assertFalse(Modifier.isAbstract(StatementDragModel.class.getModifiers()));
  }
  @Test
  public void statementDragModel_extendsAbstract() {
    assertTrue(AbstractStatementDragModel.class.isAssignableFrom(StatementDragModel.class));
  }
  @Test
  public void statementTemplateDragModel_extendsAbstract() {
    assertTrue(AbstractStatementDragModel.class.isAssignableFrom(StatementTemplateDragModel.class));
  }
  @Test
  public void allTemplateDragModels_extendStatementTemplate() {
    Class<?>[] templates = { AssignmentTemplateDragModel.class, CommentTemplateDragModel.class,
        ConditionalStatementTemplateDragModel.class, CountLoopTemplateDragModel.class,
        DoInOrderTemplateDragModel.class, DoTogetherTemplateDragModel.class,
        ForEachInArrayLoopTemplateDragModel.class, ReturnStatementTemplateDragModel.class,
        WhileLoopTemplateDragModel.class };
    for (Class<?> c : templates) {
      assertTrue(c.getSimpleName() + " should extend StatementTemplateDragModel",
          StatementTemplateDragModel.class.isAssignableFrom(c));
    }
  }
  @Test
  public void declareLocalDragModel_extendsStatementTemplate() {
    assertTrue(StatementTemplateDragModel.class.isAssignableFrom(DeclareLocalDragModel.class));
  }
  @Test
  public void expressionStatementTemplateDragModel_extendsAbstract() {
    assertTrue(AbstractStatementDragModel.class.isAssignableFrom(ExpressionStatementTemplateDragModel.class));
  }
  @Test
  public void procedureInvocationTemplateDragModel_extendsAbstract() {
    assertTrue(AbstractStatementDragModel.class.isAssignableFrom(ProcedureInvocationTemplateDragModel.class));
  }
  @Test
  public void potentiallyEnvelopingStatementTemplate_isAbstractOrConcrete() {
    // Just verify the class loads and extends the hierarchy
    assertTrue(AbstractStatementDragModel.class.isAssignableFrom(PotentiallyEnvelopingStatementTemplateDragModel.class));
  }
  @Test
  public void eachInArrayTogetherTemplate_extendsAbstract() {
    assertTrue(AbstractStatementDragModel.class.isAssignableFrom(EachInArrayTogetherTemplateDragModel.class));
  }
}
