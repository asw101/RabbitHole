package org.alice.ide.ast.draganddrop.statement;

import org.junit.Before;
import org.junit.Test;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.DoInOrder;
import org.lgna.project.ast.SuperConstructorInvocationStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.Statement;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import static org.junit.Assert.*;

public class StatementDragModelComprehensiveTest {
  @Before
  public void clearCache() throws Exception {
    Field field = StatementDragModel.class.getDeclaredField("map");
    field.setAccessible(true);
    ((Map<?, ?>) field.get(null)).clear();
  }

  @Test
  public void classIsPublicAndConcrete() {
    assertTrue(Modifier.isPublic(StatementDragModel.class.getModifiers()));
    assertFalse(Modifier.isAbstract(StatementDragModel.class.getModifiers()));
  }

  @Test
  public void classExtendsAbstractStatementDragModel() {
    assertTrue(AbstractStatementDragModel.class.isAssignableFrom(StatementDragModel.class));
  }

  @Test
  public void getInstanceReturnsNullForConstructorInvocationStatements() {
    assertNull(StatementDragModel.getInstance(new SuperConstructorInvocationStatement()));
  }

  @Test
  public void getInstanceCachesSameStatementInstance() {
    Statement statement = new Comment("same");

    assertSame(StatementDragModel.getInstance(statement), StatementDragModel.getInstance(statement));
  }

  @Test
  public void getInstanceCreatesDifferentModelsForDifferentStatements() {
    assertNotSame(StatementDragModel.getInstance(new Comment("a")), StatementDragModel.getInstance(new Comment("b")));
  }

  @Test
  public void getStatementReturnsOriginalStatement() {
    Comment statement = new Comment("comment");

    assertSame(statement, StatementDragModel.getInstance(statement).getStatement());
  }

  @Test
  public void getTypeReturnsVoidType() {
    assertSame(JavaType.VOID_TYPE, StatementDragModel.getInstance(new Comment("type")).getType());
  }

  @Test
  public void isAddEventListenerLikeSubstanceIsFalseForComment() {
    assertFalse(StatementDragModel.getInstance(new Comment("listener")).isAddEventListenerLikeSubstance());
  }

  @Test
  public void isAddEventListenerLikeSubstanceIsFalseForDoInOrder() {
    assertFalse(StatementDragModel.getInstance(new DoInOrder()).isAddEventListenerLikeSubstance());
  }

  @Test
  public void getDropOperationThrowsRuntimeException() {
    try {
      StatementDragModel.getInstance(new Comment("drop")).getDropOperation(null, null);
      fail();
    } catch (RuntimeException expected) {
    }
  }

  @Test
  public void getDropOperationUsesTodoMessage() {
    try {
      StatementDragModel.getInstance(new Comment("drop")).getDropOperation(null, null);
      fail();
    } catch (RuntimeException expected) {
      assertEquals("todo", expected.getMessage());
    }
  }

  @Test
  public void statementFieldIsPrivate() throws Exception {
    assertTrue(Modifier.isPrivate(StatementDragModel.class.getDeclaredField("statement").getModifiers()));
  }

  @Test
  public void mapFieldIsPrivateStatic() throws Exception {
    Field field = StatementDragModel.class.getDeclaredField("map");

    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isStatic(field.getModifiers()));
  }

  @Test
  public void constructorIsPrivateAndAcceptsStatement() {
    Constructor<?> constructor = StatementDragModel.class.getDeclaredConstructors()[0];

    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    assertEquals(1, constructor.getParameterTypes().length);
    assertSame(Statement.class, constructor.getParameterTypes()[0]);
  }

  @Test
  public void getInstanceMethodIsSynchronized() throws Exception {
    Method method = StatementDragModel.class.getMethod("getInstance", Statement.class);

    assertTrue(Modifier.isSynchronized(method.getModifiers()));
    assertTrue(Modifier.isStatic(method.getModifiers()));
  }

  @Test
  public void nullStatementCanBeCached() {
    assertSame(StatementDragModel.getInstance(null), StatementDragModel.getInstance(null));
  }

  @Test
  public void nullStatementModelReportsNullStatement() {
    assertNull(StatementDragModel.getInstance(null).getStatement());
  }
  @Test
  public void getStatementMethodReturnsStatementType() throws Exception {
    assertSame(Statement.class, StatementDragModel.class.getMethod("getStatement").getReturnType());
  }

  @Test
  public void classPackageMatchesStatementPackage() {
    assertEquals("org.alice.ide.ast.draganddrop.statement", StatementDragModel.class.getPackage().getName());
  }

  @Test
  public void classNameMatchesExpectedValue() {
    assertEquals("StatementDragModel", StatementDragModel.class.getSimpleName());
  }

  @Test
  public void classIsNotEnum() {
    assertFalse(StatementDragModel.class.isEnum());
  }
}
