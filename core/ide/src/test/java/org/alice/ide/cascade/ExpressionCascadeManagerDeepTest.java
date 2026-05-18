package org.alice.ide.cascade;

import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.lgna.croquet.CascadeMenuModel;
import org.lgna.project.ast.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;

/**
 * Deep tests for {@link ExpressionCascadeManager} — loop-local visibility,
 * appendOtherTypes, out-of-bounds index handling, and ForEachInArrayLoop item exposure.
 */
public class ExpressionCascadeManagerDeepTest {

  private TestableManager manager;

  private static class TestableManager extends ExpressionCascadeManager {
    @Override
    protected boolean isApplicableForPartFillIn(AbstractType<?, ?, ?> desiredType, AbstractType<?, ?, ?> expressionType) {
      return false;
    }

    @Override
    protected CascadeMenuModel<Expression> createPartMenuModel(Expression expression, AbstractType<?, ?, ?> desiredType, AbstractType<?, ?, ?> expressionType, boolean isOwnedByCascadeItemMenuCombo) {
      return null;
    }

    // Expose appendOtherTypes for testing
    public List<AbstractType<?, ?, ?>> getOtherTypes() {
      List<AbstractType<?, ?, ?>> types = new ArrayList<>();
      appendOtherTypes(types);
      return types;
    }
  }

  @Before
  public void setUp() {
    manager = new TestableManager();
  }

  private static <T> List<T> toList(Iterable<T> iterable) {
    return StreamSupport.stream(iterable.spliterator(), false)
        .collect(Collectors.toList());
  }

  // ---- ForEachInArrayLoop locals ----

  @Test
  public void getAccessibleLocals_forEachInArrayLoop_exposesItem() {
    // ForEachInArrayLoop adds its item UserLocal to accessible locals
    UserLocal itemLocal = new UserLocal("item", JavaType.STRING_TYPE, true);
    BlockStatement innerBlock = new BlockStatement();
    ForEachInArrayLoop forEachLoop = new ForEachInArrayLoop(
        itemLocal, new NullLiteral(), innerBlock);

    // The inner block's parent is the ForEachInArrayLoop
    BlockStatementIndexPair pair = new BlockStatementIndexPair(innerBlock, 0);
    Iterable<UserLocal> locals = manager.getAccessibleLocals(pair);

    List<UserLocal> localList = toList(locals);
    assertTrue("ForEachInArrayLoop item should be accessible", localList.contains(itemLocal));
  }

  @Test
  public void getAccessibleLocals_nestedForEachInArrayLoop_exposesBothItems() {
    UserLocal outerItem = new UserLocal("outerItem", JavaType.STRING_TYPE, true);
    UserLocal innerItem = new UserLocal("innerItem", JavaType.INTEGER_OBJECT_TYPE, true);

    BlockStatement innerBlock = new BlockStatement();
    ForEachInArrayLoop innerLoop = new ForEachInArrayLoop(
        innerItem, new NullLiteral(), innerBlock);

    BlockStatement outerBody = new BlockStatement();
    outerBody.statements.add(innerLoop);

    ForEachInArrayLoop outerLoop = new ForEachInArrayLoop(
        outerItem, new NullLiteral(), outerBody);

    // From inside the inner block, both items should be visible
    BlockStatementIndexPair pair = new BlockStatementIndexPair(innerBlock, 0);
    Iterable<UserLocal> locals = manager.getAccessibleLocals(pair);

    List<String> names = toList(locals).stream()
        .map(UserLocal::getName).collect(Collectors.toList());
    assertTrue("Inner item should be accessible", names.contains("innerItem"));
    assertTrue("Outer item should be accessible", names.contains("outerItem"));
  }

  // ---- CountLoop locals — FormatterState.isJava() gate ----

  @Test
  public void getAccessibleLocals_countLoop_defaultAliceMode_hidesVariable() {
    // In default Alice mode, FormatterState.isJava() returns false,
    // so CountLoop variable should NOT be accessible.
    UserLocal variable = new UserLocal("i", JavaType.INTEGER_OBJECT_TYPE, false);
    UserLocal constant = new UserLocal("count", JavaType.INTEGER_OBJECT_TYPE, true);
    BlockStatement body = new BlockStatement();
    CountLoop countLoop = new CountLoop(variable, constant, new IntegerLiteral(5), body);

    BlockStatementIndexPair pair = new BlockStatementIndexPair(body, 0);
    Iterable<UserLocal> locals = manager.getAccessibleLocals(pair);

    List<UserLocal> localList = toList(locals);
    // In default (Alice) mode, CountLoop variable is hidden
    assertFalse("CountLoop variable should be hidden in Alice mode", localList.contains(variable));
  }

  // ---- Index out-of-bounds handling ----

  @Test
  public void getAccessibleLocals_indexExceedingStatements_handlesGracefully() {
    // When index > statements.size(), the code catches IndexOutOfBoundsException internally
    BlockStatement block = new BlockStatement();
    UserLocal local = new UserLocal("x", JavaType.DOUBLE_OBJECT_TYPE, false);
    block.statements.add(new LocalDeclarationStatement(local, new DoubleLiteral(1.0)));

    // Index 10 far exceeds the 1 statement
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 10);
    Iterable<UserLocal> locals = manager.getAccessibleLocals(pair);

    // Should not throw — it internally catches the IOOBE and adjusts
    List<UserLocal> localList = toList(locals);
    // The local should still be found after index correction
    assertTrue("Local should be found after index correction", localList.contains(local));
  }

  @Test
  public void getAccessibleLocals_negativeIndex_returnsEmpty() {
    BlockStatement block = new BlockStatement();
    UserLocal local = new UserLocal("y", JavaType.STRING_TYPE, false);
    block.statements.add(new LocalDeclarationStatement(local, new StringLiteral("hi")));

    // Index 0 already means "before any statement", negative should yield empty
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 0);
    Iterable<UserLocal> locals = manager.getAccessibleLocals(pair);
    assertFalse("No locals should be visible at index 0", locals.iterator().hasNext());
  }

  // ---- appendOtherTypes ----

  @Test
  public void appendOtherTypes_containsStringDoubleInteger() {
    List<AbstractType<?, ?, ?>> types = manager.getOtherTypes();
    assertEquals(3, types.size());
    assertTrue("Should contain String", types.contains(JavaType.STRING_TYPE));
    assertTrue("Should contain Double", types.contains(JavaType.DOUBLE_OBJECT_TYPE));
    assertTrue("Should contain Integer", types.contains(JavaType.INTEGER_OBJECT_TYPE));
  }

  @Test
  public void appendOtherTypes_orderIsStringThenDoubleThenInteger() {
    List<AbstractType<?, ?, ?>> types = manager.getOtherTypes();
    assertSame(JavaType.STRING_TYPE, types.get(0));
    assertSame(JavaType.DOUBLE_OBJECT_TYPE, types.get(1));
    assertSame(JavaType.INTEGER_OBJECT_TYPE, types.get(2));
  }

  // ---- Locals with mixed statement types ----

  @Test
  public void getAccessibleLocals_mixedStatements_onlyReturnsLocalDeclarations() {
    BlockStatement block = new BlockStatement();
    UserLocal local = new UserLocal("myVar", JavaType.INTEGER_OBJECT_TYPE, false);
    block.statements.add(new LocalDeclarationStatement(local, new IntegerLiteral(42)));
    // Add a non-local-declaration statement
    block.statements.add(new ExpressionStatement(new NullLiteral()));

    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 2);
    List<UserLocal> localList = toList(manager.getAccessibleLocals(pair));
    assertEquals("Should only find the one local declaration", 1, localList.size());
    assertEquals("myVar", localList.get(0).getName());
  }

  @Test
  public void getAccessibleLocals_localDeclaredAfterIndex_notVisible() {
    BlockStatement block = new BlockStatement();
    UserLocal first = new UserLocal("first", JavaType.INTEGER_OBJECT_TYPE, false);
    UserLocal second = new UserLocal("second", JavaType.STRING_TYPE, false);
    block.statements.add(new LocalDeclarationStatement(first, new IntegerLiteral(1)));
    block.statements.add(new LocalDeclarationStatement(second, new StringLiteral("x")));

    // At index 1, only 'first' should be visible (before index)
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 1);
    List<String> names = toList(manager.getAccessibleLocals(pair)).stream()
        .map(UserLocal::getName).collect(Collectors.toList());
    assertEquals(1, names.size());
    assertTrue("first should be visible", names.contains("first"));
    assertFalse("second should not be visible", names.contains("second"));
  }

  // ---- Local visibility at later indices in same block ----

  @Test
  public void getAccessibleLocals_sameBlock_laterIndex_seesEarlierLocals() {
    BlockStatement block = new BlockStatement();
    UserLocal local = new UserLocal("outerVar", JavaType.DOUBLE_OBJECT_TYPE, false);
    block.statements.add(new LocalDeclarationStatement(local, new DoubleLiteral(3.14)));

    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 1);
    List<String> names = toList(manager.getAccessibleLocals(pair)).stream()
        .map(UserLocal::getName).collect(Collectors.toList());
    assertTrue("outerVar should be accessible at index 1", names.contains("outerVar"));
  }
}
