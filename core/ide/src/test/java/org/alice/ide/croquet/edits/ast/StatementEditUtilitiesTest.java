package org.alice.ide.croquet.edits.ast;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

public class StatementEditUtilitiesTest {

  @Test
  public void insertStatementEditAtEnd() {
    assertEquals(Short.MAX_VALUE, InsertStatementEdit.AT_END);
  }

  @Test
  public void insertStatementEditConstruction() {
    BlockStatement blockStatement = new BlockStatement();
    org.alice.ide.ast.draganddrop.BlockStatementIndexPair pair =
        new org.alice.ide.ast.draganddrop.BlockStatementIndexPair(blockStatement, 0);
    ExpressionStatement statement = new ExpressionStatement();

    InsertStatementEdit<?> edit = new InsertStatementEdit<>(null, pair, statement);
    assertNotNull(edit);
    assertSame(statement, edit.getStatement());
    assertSame(blockStatement, edit.getBlockStatement());
    assertEquals(0, edit.getSpecifiedIndex());
    assertNotNull(edit.getInitialExpressions());
    assertEquals(0, edit.getInitialExpressions().length);
  }

  @Test
  public void insertStatementEditWithExpressions() {
    BlockStatement blockStatement = new BlockStatement();
    org.alice.ide.ast.draganddrop.BlockStatementIndexPair pair =
        new org.alice.ide.ast.draganddrop.BlockStatementIndexPair(blockStatement, 0);
    Comment comment = new Comment("test");
    Expression[] exprs = { new IntegerLiteral(1), new StringLiteral("test") };

    InsertStatementEdit<?> edit = new InsertStatementEdit<>(null, pair, comment, exprs);
    assertNotNull(edit);
    assertEquals(2, edit.getInitialExpressions().length);
  }

  @Test
  public void insertStatementDoAndUndo() {
    BlockStatement blockStatement = new BlockStatement();
    org.alice.ide.ast.draganddrop.BlockStatementIndexPair pair =
        new org.alice.ide.ast.draganddrop.BlockStatementIndexPair(blockStatement, 0);
    Comment statement = new Comment("test comment");

    InsertStatementEdit<?> edit = new InsertStatementEdit<>(null, pair, statement);
    edit.doOrRedo(true);
    assertEquals(1, blockStatement.statements.size());
    assertSame(statement, blockStatement.statements.get(0));

    edit.doOrRedo(false);
    assertEquals(2, blockStatement.statements.size());
  }

  @Test
  public void insertStatementEditAppendDescription() {
    BlockStatement blockStatement = new BlockStatement();
    org.alice.ide.ast.draganddrop.BlockStatementIndexPair pair =
        new org.alice.ide.ast.draganddrop.BlockStatementIndexPair(blockStatement, 0);
    Comment statement = new Comment("hello");

    InsertStatementEdit<?> edit = new InsertStatementEdit<>(null, pair, statement);
    assertNotNull(edit.toString());
  }

  @Test
  public void expressionPropertyEditSetValue() {
    // Create a MethodInvocation with an expression property we can test
    ExpressionStatement es = new ExpressionStatement();
    ExpressionProperty prop = es.expression;
    IntegerLiteral prev = new IntegerLiteral(1);
    IntegerLiteral next = new IntegerLiteral(2);
    prop.setValue(prev);

    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, prop, prev, next);
    assertNotNull(edit);

    edit.doOrRedo(true);
    assertSame(next, prop.getValue());

    edit.undo();
    assertSame(prev, prop.getValue());
  }

  @Test
  public void declareMethodEditGetters() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("TestType");
    type.superType.setValue(JavaType.getInstance(Object.class));

    DeclareMethodEdit edit = new DeclareMethodEdit(null, type, "testMethod", JavaType.getInstance(Void.TYPE));
    assertEquals("testMethod", edit.getMethodName());
    assertSame(type, edit.getDeclaringType());
    assertSame(JavaType.getInstance(Void.TYPE), edit.getReturnType());
  }

  @Test
  public void declareMethodEditDescription() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("TestType");
    type.superType.setValue(JavaType.getInstance(Object.class));

    DeclareMethodEdit edit = new DeclareMethodEdit(null, type, "myMethod", JavaType.getInstance(Void.TYPE));
    assertNotNull(edit.toString());
  }
}
