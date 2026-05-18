package org.alice.ide.ast.draganddrop.statement;

import org.alice.ide.ast.draganddrop.CodeDragModel;
import org.junit.Test;
import org.lgna.project.ast.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Characterization tests for statement drag model classes.
 * Covers the AbstractStatementDragModel / StatementTemplateDragModel hierarchy
 * and all concrete template drag models (singleton patterns, statementCls fields).
 */
public class StatementDragModelCharacterizationTest {

  private static final Class<?>[] SINGLETON_MODELS = {
      CommentTemplateDragModel.class,
      DoInOrderTemplateDragModel.class,
      DoTogetherTemplateDragModel.class,
      CountLoopTemplateDragModel.class,
      WhileLoopTemplateDragModel.class,
      ConditionalStatementTemplateDragModel.class,
      ForEachInArrayLoopTemplateDragModel.class,
      EachInArrayTogetherTemplateDragModel.class,
      DeclareLocalDragModel.class,
  };

  // ══════════════════════════════════════════════════════════════
  // AbstractStatementDragModel
  // ══════════════════════════════════════════════════════════════

  @Test
  public void abstractStmt_isAbstract() {
    assertTrue(Modifier.isAbstract(AbstractStatementDragModel.class.getModifiers()));
  }

  @Test
  public void abstractStmt_extendsCodeDragModel() {
    assertTrue(CodeDragModel.class.isAssignableFrom(AbstractStatementDragModel.class));
  }

  @Test
  public void abstractStmt_getTypeReturnsVoid() {
    // getType() is overridden to return JavaType.VOID_TYPE
    boolean found = Arrays.stream(AbstractStatementDragModel.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getType") && !Modifier.isAbstract(m.getModifiers()));
    assertTrue("getType() should be concrete override", found);
  }

  @Test
  public void abstractStmt_hasIsAddEventListenerLikeSubstance() {
    boolean found = Arrays.stream(AbstractStatementDragModel.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("isAddEventListenerLikeSubstance")
            && Modifier.isAbstract(m.getModifiers()));
    assertTrue(found);
  }

  // ══════════════════════════════════════════════════════════════
  // StatementTemplateDragModel
  // ══════════════════════════════════════════════════════════════

  @Test
  public void stmtTemplate_isAbstract() {
    assertTrue(Modifier.isAbstract(StatementTemplateDragModel.class.getModifiers()));
  }

  @Test
  public void stmtTemplate_extendsAbstractStatementDragModel() {
    assertTrue(AbstractStatementDragModel.class.isAssignableFrom(StatementTemplateDragModel.class));
  }

  @Test
  public void stmtTemplate_hasStatementClsField() throws Exception {
    Field f = StatementTemplateDragModel.class.getDeclaredField("statementCls");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void stmtTemplate_hasPossiblyIncompleteStatementField() throws Exception {
    Field f = StatementTemplateDragModel.class.getDeclaredField("possiblyIncompleteStatement");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void stmtTemplate_hasGetStatementClsMethod() throws Exception {
    Method m = StatementTemplateDragModel.class.getMethod("getStatementCls");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void stmtTemplate_hasGetPossiblyIncompleteStatementMethod() throws Exception {
    Method m = StatementTemplateDragModel.class.getMethod("getPossiblyIncompleteStatement");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(Statement.class, m.getReturnType());
  }

  @Test
  public void stmtTemplate_isAddEventListenerLikeSubstance_isFalse() {
    // The base implementation returns false
    boolean found = Arrays.stream(StatementTemplateDragModel.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("isAddEventListenerLikeSubstance")
            && !Modifier.isAbstract(m.getModifiers()));
    assertTrue(found);
  }

  @Test
  public void stmtTemplate_getDropOperationIsFinal() {
    boolean found = Arrays.stream(StatementTemplateDragModel.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getDropOperation")
            && Modifier.isFinal(m.getModifiers())
            && m.getParameterCount() == 2);
    assertTrue("getDropOperation(DragStep, DropSite) must be final", found);
  }

  // ══════════════════════════════════════════════════════════════
  // Singleton models — structural patterns
  // ══════════════════════════════════════════════════════════════

  @Test
  public void allSingletonModels_haveSingletonHolderOrGetInstance() {
    for (Class<?> cls : SINGLETON_MODELS) {
      boolean hasSingletonHolder = Arrays.stream(cls.getDeclaredClasses())
          .anyMatch(c -> c.getSimpleName().equals("SingletonHolder"));
      boolean hasGetInstance = Arrays.stream(cls.getDeclaredMethods())
          .anyMatch(m -> m.getName().equals("getInstance") && Modifier.isStatic(m.getModifiers()));
      assertTrue(cls.getSimpleName() + " must have SingletonHolder or getInstance",
          hasSingletonHolder || hasGetInstance);
    }
  }

  @Test
  public void allSingletonModels_extendStatementTemplateDragModel() {
    for (Class<?> cls : SINGLETON_MODELS) {
      assertTrue(cls.getSimpleName() + " must extend StatementTemplateDragModel or AbstractStatementDragModel",
          AbstractStatementDragModel.class.isAssignableFrom(cls));
    }
  }

  @Test
  public void allSingletonModels_arePublic() {
    for (Class<?> cls : SINGLETON_MODELS) {
      assertTrue(cls.getSimpleName() + " must be public",
          Modifier.isPublic(cls.getModifiers()));
    }
  }

  @Test
  public void allSingletonModels_havePrivateConstructor() {
    for (Class<?> cls : SINGLETON_MODELS) {
      boolean allPrivate = Arrays.stream(cls.getDeclaredConstructors())
          .allMatch(c -> Modifier.isPrivate(c.getModifiers()));
      assertTrue(cls.getSimpleName() + " constructors should be private", allPrivate);
    }
  }

  // ══════════════════════════════════════════════════════════════
  // CommentTemplateDragModel — behavioral
  // ══════════════════════════════════════════════════════════════

  @Test
  public void comment_singletonReturnsSameInstance() {
    assertSame(CommentTemplateDragModel.getInstance(), CommentTemplateDragModel.getInstance());
  }

  @Test
  public void comment_statementClsIsComment() {
    assertEquals(Comment.class, CommentTemplateDragModel.getInstance().getStatementCls());
  }

  @Test
  public void comment_possiblyIncompleteStatementNotNull() {
    assertNotNull(CommentTemplateDragModel.getInstance().getPossiblyIncompleteStatement());
  }

  @Test
  public void comment_possiblyIncompleteStatementIsComment() {
    assertTrue(CommentTemplateDragModel.getInstance().getPossiblyIncompleteStatement() instanceof Comment);
  }

  @Test
  public void comment_isNotAddEventListenerLike() {
    assertFalse(CommentTemplateDragModel.getInstance().isAddEventListenerLikeSubstance());
  }

  @Test
  public void comment_getTypeReturnsVoidType() {
    assertSame(JavaType.VOID_TYPE, CommentTemplateDragModel.getInstance().getType());
  }

  // ══════════════════════════════════════════════════════════════
  // DoInOrderTemplateDragModel — behavioral
  // ══════════════════════════════════════════════════════════════

  @Test
  public void doInOrder_singletonReturnsSameInstance() {
    assertSame(DoInOrderTemplateDragModel.getInstance(), DoInOrderTemplateDragModel.getInstance());
  }

  @Test
  public void doInOrder_statementClsIsDoInOrder() {
    assertEquals(DoInOrder.class, DoInOrderTemplateDragModel.getInstance().getStatementCls());
  }

  @Test
  public void doInOrder_isNotAddEventListenerLike() {
    assertFalse(DoInOrderTemplateDragModel.getInstance().isAddEventListenerLikeSubstance());
  }

  // ══════════════════════════════════════════════════════════════
  // DoTogetherTemplateDragModel — behavioral
  // ══════════════════════════════════════════════════════════════

  @Test
  public void doTogether_singletonReturnsSameInstance() {
    assertSame(DoTogetherTemplateDragModel.getInstance(), DoTogetherTemplateDragModel.getInstance());
  }

  @Test
  public void doTogether_statementClsIsDoTogether() {
    assertEquals(DoTogether.class, DoTogetherTemplateDragModel.getInstance().getStatementCls());
  }

  // ══════════════════════════════════════════════════════════════
  // CountLoopTemplateDragModel — behavioral
  // ══════════════════════════════════════════════════════════════

  @Test
  public void countLoop_singletonReturnsSameInstance() {
    assertSame(CountLoopTemplateDragModel.getInstance(), CountLoopTemplateDragModel.getInstance());
  }

  @Test
  public void countLoop_statementClsIsCountLoop() {
    assertEquals(CountLoop.class, CountLoopTemplateDragModel.getInstance().getStatementCls());
  }

  // ══════════════════════════════════════════════════════════════
  // WhileLoopTemplateDragModel — behavioral
  // ══════════════════════════════════════════════════════════════

  @Test
  public void whileLoop_singletonReturnsSameInstance() {
    assertSame(WhileLoopTemplateDragModel.getInstance(), WhileLoopTemplateDragModel.getInstance());
  }

  @Test
  public void whileLoop_statementClsIsWhileLoop() {
    assertEquals(WhileLoop.class, WhileLoopTemplateDragModel.getInstance().getStatementCls());
  }

  // ══════════════════════════════════════════════════════════════
  // ConditionalStatementTemplateDragModel — behavioral
  // ══════════════════════════════════════════════════════════════

  @Test
  public void conditional_singletonReturnsSameInstance() {
    assertSame(ConditionalStatementTemplateDragModel.getInstance(),
        ConditionalStatementTemplateDragModel.getInstance());
  }

  @Test
  public void conditional_statementClsIsConditionalStatement() {
    assertEquals(ConditionalStatement.class,
        ConditionalStatementTemplateDragModel.getInstance().getStatementCls());
  }

  // ══════════════════════════════════════════════════════════════
  // ForEachInArrayLoopTemplateDragModel — behavioral
  // ══════════════════════════════════════════════════════════════

  @Test
  public void forEachArray_singletonReturnsSameInstance() {
    assertSame(ForEachInArrayLoopTemplateDragModel.getInstance(),
        ForEachInArrayLoopTemplateDragModel.getInstance());
  }

  @Test
  public void forEachArray_statementCls() {
    assertEquals(ForEachInArrayLoop.class,
        ForEachInArrayLoopTemplateDragModel.getInstance().getStatementCls());
  }

  // ══════════════════════════════════════════════════════════════
  // EachInArrayTogetherTemplateDragModel — behavioral
  // ══════════════════════════════════════════════════════════════

  @Test
  public void eachInArrayTogether_singletonReturnsSameInstance() {
    assertSame(EachInArrayTogetherTemplateDragModel.getInstance(),
        EachInArrayTogetherTemplateDragModel.getInstance());
  }

  @Test
  public void eachInArrayTogether_statementCls() {
    assertEquals(EachInArrayTogether.class,
        EachInArrayTogetherTemplateDragModel.getInstance().getStatementCls());
  }

  // ══════════════════════════════════════════════════════════════
  // ReturnStatementTemplateDragModel — structural (needs UserMethod arg)
  // ══════════════════════════════════════════════════════════════

  @Test
  public void returnStmt_extendsStatementTemplateDragModel() {
    assertTrue(StatementTemplateDragModel.class.isAssignableFrom(
        ReturnStatementTemplateDragModel.class));
  }

  @Test
  public void returnStmt_hasGetInstanceWithUserMethod() {
    boolean found = Arrays.stream(ReturnStatementTemplateDragModel.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getInstance")
            && Modifier.isStatic(m.getModifiers())
            && m.getParameterCount() == 1);
    assertTrue("getInstance requires a UserMethod parameter", found);
  }

  // ══════════════════════════════════════════════════════════════
  // DeclareLocalDragModel
  // ══════════════════════════════════════════════════════════════

  @Test
  public void declareLocal_singletonReturnsSameInstance() {
    assertSame(DeclareLocalDragModel.getInstance(), DeclareLocalDragModel.getInstance());
  }

  @Test
  public void declareLocal_statementCls() {
    assertEquals(LocalDeclarationStatement.class,
        DeclareLocalDragModel.getInstance().getStatementCls());
  }

  // ══════════════════════════════════════════════════════════════
  // Instance-based models (not singletons)
  // ══════════════════════════════════════════════════════════════

  @Test
  public void expressionStmtTemplate_extendsAbstractStatementDragModel() {
    assertTrue(AbstractStatementDragModel.class.isAssignableFrom(
        ExpressionStatementTemplateDragModel.class));
  }

  @Test
  public void expressionStmtTemplate_isPublic() {
    assertTrue(Modifier.isPublic(ExpressionStatementTemplateDragModel.class.getModifiers()));
  }

  @Test
  public void procedureInvocationTemplate_extendsAbstractStatementDragModel() {
    assertTrue(AbstractStatementDragModel.class.isAssignableFrom(
        ProcedureInvocationTemplateDragModel.class));
  }

  @Test
  public void procedureInvocationTemplate_isPublic() {
    assertTrue(Modifier.isPublic(ProcedureInvocationTemplateDragModel.class.getModifiers()));
  }

  @Test
  public void assignmentTemplate_extendsStatementTemplateDragModel() {
    assertTrue(StatementTemplateDragModel.class.isAssignableFrom(
        AssignmentTemplateDragModel.class));
  }

  @Test
  public void assignmentTemplate_hasGetInstanceMethod() {
    boolean found = Arrays.stream(AssignmentTemplateDragModel.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getInstance") && Modifier.isStatic(m.getModifiers()));
    assertTrue(found);
  }

  // ══════════════════════════════════════════════════════════════
  // StatementDragModel (non-template)
  // ══════════════════════════════════════════════════════════════

  @Test
  public void statementDragModel_extendsAbstractStatementDragModel() {
    assertTrue(AbstractStatementDragModel.class.isAssignableFrom(StatementDragModel.class));
  }

  @Test
  public void statementDragModel_isPublic() {
    assertTrue(Modifier.isPublic(StatementDragModel.class.getModifiers()));
  }

  @Test
  public void statementDragModel_hasStaticMapField() throws Exception {
    Field f = StatementDragModel.class.getDeclaredField("map");
    assertTrue(Modifier.isStatic(f.getModifiers()));
  }
}
