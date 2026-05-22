package org.alice.ide.ast.code.edits;

import org.alice.ide.ast.code.MoveStatementOperation;
import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.alice.ide.testing.TestIdeBootstrap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.edits.AbstractEdit;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.Statement;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Characterization and behavioral tests for MoveStatementEdit.
 * The edit has real do/undo logic for moving statements between blocks
 * that we test via reflection to bypass the UserActivity requirement.
 */
public class MoveStatementEditBehavioralTest {
  @Before
  public void setUpIdeContext() {
    TestIdeBootstrap.ensureInstalled();
  }

  @After
  public void tearDownIdeContext() {
    TestIdeBootstrap.reset();
  }

  // ══════════════════════════════════════════════════════════════
  // Structural characterization
  // ══════════════════════════════════════════════════════════════

  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(MoveStatementEdit.class.getModifiers()));
  }

  @Test
  public void extendsAbstractEdit() {
    assertTrue(AbstractEdit.class.isAssignableFrom(MoveStatementEdit.class));
  }

  @Test
  public void inCorrectPackage() {
    assertEquals("org.alice.ide.ast.code.edits",
        MoveStatementEdit.class.getPackage().getName());
  }

  @Test
  public void hasFromLocationField() throws Exception {
    Field f = MoveStatementEdit.class.getDeclaredField("fromLocation");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertEquals(BlockStatementIndexPair.class, f.getType());
  }

  @Test
  public void hasToLocationField() throws Exception {
    Field f = MoveStatementEdit.class.getDeclaredField("toLocation");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertEquals(BlockStatementIndexPair.class, f.getType());
  }

  @Test
  public void hasIsMultipleField() throws Exception {
    Field f = MoveStatementEdit.class.getDeclaredField("isMultiple");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertEquals(boolean.class, f.getType());
  }

  @Test
  public void hasCountField() throws Exception {
    Field f = MoveStatementEdit.class.getDeclaredField("count");
    assertEquals(int.class, f.getType());
  }

  @Test
  public void hasGetToDeltaMethod() throws Exception {
    Method m = MoveStatementEdit.class.getDeclaredMethod("getToDelta");
    assertTrue(Modifier.isPrivate(m.getModifiers()));
    assertEquals(int.class, m.getReturnType());
  }

  @Test
  public void hasEncodeMethod() {
    boolean found = Arrays.stream(MoveStatementEdit.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("encode"));
    assertTrue(found);
  }

  @Test
  public void hasAppendDescriptionMethod() {
    boolean found = Arrays.stream(MoveStatementEdit.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("appendDescription"));
    assertTrue(found);
  }

  @Test
  public void hasTwoConstructors() {
    assertEquals(2, MoveStatementEdit.class.getDeclaredConstructors().length);
  }

  // ══════════════════════════════════════════════════════════════
  // Behavioral: getToDelta via reflection
  // ══════════════════════════════════════════════════════════════

  @Test
  public void getToDelta_sameBlock_fromBeforeTo_returnsMinusOne() throws Exception {
    BlockStatement block = new BlockStatement();
    Comment s0 = new Comment("a");
    Comment s1 = new Comment("b");
    Comment s2 = new Comment("c");
    block.statements.add(s0);
    block.statements.add(s1);
    block.statements.add(s2);

    BlockStatementIndexPair from = new BlockStatementIndexPair(block, 0);
    BlockStatementIndexPair to = new BlockStatementIndexPair(block, 2);

    MoveStatementEdit edit = createEdit(from, s0, to, false);
    int delta = invokeGetToDelta(edit);
    assertEquals(-1, delta);
  }

  @Test
  public void getToDelta_sameBlock_fromAfterTo_returnsZero() throws Exception {
    BlockStatement block = new BlockStatement();
    Comment s0 = new Comment("a");
    Comment s1 = new Comment("b");
    block.statements.add(s0);
    block.statements.add(s1);

    BlockStatementIndexPair from = new BlockStatementIndexPair(block, 1);
    BlockStatementIndexPair to = new BlockStatementIndexPair(block, 0);

    MoveStatementEdit edit = createEdit(from, s1, to, false);
    int delta = invokeGetToDelta(edit);
    assertEquals(0, delta);
  }

  @Test
  public void getToDelta_differentBlocks_returnsZero() throws Exception {
    BlockStatement block1 = new BlockStatement();
    BlockStatement block2 = new BlockStatement();
    Comment stmt = new Comment("stmt");
    block1.statements.add(stmt);

    BlockStatementIndexPair from = new BlockStatementIndexPair(block1, 0);
    BlockStatementIndexPair to = new BlockStatementIndexPair(block2, 0);

    MoveStatementEdit edit = createEdit(from, stmt, to, false);
    int delta = invokeGetToDelta(edit);
    assertEquals(0, delta);
  }

  @Test
  public void getToDelta_sameBlock_sameIndex_returnsZero() throws Exception {
    BlockStatement block = new BlockStatement();
    Comment stmt = new Comment("a");
    block.statements.add(stmt);

    BlockStatementIndexPair from = new BlockStatementIndexPair(block, 0);
    BlockStatementIndexPair to = new BlockStatementIndexPair(block, 0);

    MoveStatementEdit edit = createEdit(from, stmt, to, false);
    int delta = invokeGetToDelta(edit);
    // from == to, not from < to, so delta is 0
    assertEquals(0, delta);
  }

  // ══════════════════════════════════════════════════════════════
  // Behavioral: doOrRedo/undo — cross-block moves
  // ══════════════════════════════════════════════════════════════

  @Test
  public void move_acrossBlocks_single() throws Exception {
    BlockStatement from = new BlockStatement();
    BlockStatement to = new BlockStatement();
    Comment stmt = new Comment("moving");
    from.statements.add(stmt);

    BlockStatementIndexPair fromPair = new BlockStatementIndexPair(from, 0);
    BlockStatementIndexPair toPair = new BlockStatementIndexPair(to, 0);

    MoveStatementEdit edit = createEdit(fromPair, stmt, toPair, false);
    invokeDoOrRedo(edit, true);

    assertEquals(0, from.statements.size());
    assertEquals(1, to.statements.size());
    assertSame(stmt, to.statements.get(0));
  }

  @Test
  public void move_acrossBlocks_undoRestores() throws Exception {
    BlockStatement from = new BlockStatement();
    BlockStatement to = new BlockStatement();
    Comment stmt = new Comment("moving");
    from.statements.add(stmt);

    BlockStatementIndexPair fromPair = new BlockStatementIndexPair(from, 0);
    BlockStatementIndexPair toPair = new BlockStatementIndexPair(to, 0);

    MoveStatementEdit edit = createEdit(fromPair, stmt, toPair, false);
    invokeDoOrRedo(edit, true);
    invokeUndo(edit);

    assertEquals(1, from.statements.size());
    assertEquals(0, to.statements.size());
    assertSame(stmt, from.statements.get(0));
  }

  @Test
  public void move_acrossBlocks_redoAfterUndo() throws Exception {
    BlockStatement from = new BlockStatement();
    BlockStatement to = new BlockStatement();
    Comment stmt = new Comment("moving");
    from.statements.add(stmt);

    BlockStatementIndexPair fromPair = new BlockStatementIndexPair(from, 0);
    BlockStatementIndexPair toPair = new BlockStatementIndexPair(to, 0);

    MoveStatementEdit edit = createEdit(fromPair, stmt, toPair, false);
    invokeDoOrRedo(edit, true);
    invokeUndo(edit);
    invokeDoOrRedo(edit, false);

    assertEquals(0, from.statements.size());
    assertEquals(1, to.statements.size());
  }

  @Test
  public void move_acrossBlocks_toNonEmptyBlock() throws Exception {
    BlockStatement from = new BlockStatement();
    BlockStatement to = new BlockStatement();
    Comment stmtA = new Comment("A");
    Comment existing = new Comment("existing");
    from.statements.add(stmtA);
    to.statements.add(existing);

    BlockStatementIndexPair fromPair = new BlockStatementIndexPair(from, 0);
    BlockStatementIndexPair toPair = new BlockStatementIndexPair(to, 0);

    MoveStatementEdit edit = createEdit(fromPair, stmtA, toPair, false);
    invokeDoOrRedo(edit, true);

    assertEquals(0, from.statements.size());
    assertEquals(2, to.statements.size());
    assertSame(stmtA, to.statements.get(0));
    assertSame(existing, to.statements.get(1));
  }

  // ══════════════════════════════════════════════════════════════
  // Behavioral: within same block
  // ══════════════════════════════════════════════════════════════

  @Test
  public void move_withinSameBlock_fromHigherToLower() throws Exception {
    BlockStatement block = new BlockStatement();
    Comment s0 = new Comment("a");
    Comment s1 = new Comment("b");
    Comment s2 = new Comment("c");
    block.statements.add(s0);
    block.statements.add(s1);
    block.statements.add(s2);

    // Move from index 2 to index 0
    BlockStatementIndexPair fromPair = new BlockStatementIndexPair(block, 2);
    BlockStatementIndexPair toPair = new BlockStatementIndexPair(block, 0);

    MoveStatementEdit edit = createEdit(fromPair, s2, toPair, false);
    invokeDoOrRedo(edit, true);

    assertEquals(3, block.statements.size());
    assertSame(s2, block.statements.get(0));
    assertSame(s0, block.statements.get(1));
    assertSame(s1, block.statements.get(2));
  }

  // ══════════════════════════════════════════════════════════════
  // EnvelopStatementsEdit — structural
  // ══════════════════════════════════════════════════════════════

  @Test
  public void envelopEdit_isPublic() {
    assertTrue(Modifier.isPublic(EnvelopStatementsEdit.class.getModifiers()));
  }

  @Test
  public void envelopEdit_extendsAbstractEdit() {
    assertTrue(AbstractEdit.class.isAssignableFrom(EnvelopStatementsEdit.class));
  }

  @Test
  public void envelopEdit_hasFromLocationField() throws Exception {
    Field f = EnvelopStatementsEdit.class.getDeclaredField("fromLocation");
    assertEquals(BlockStatementIndexPair.class, f.getType());
  }

  @Test
  public void envelopEdit_hasToLocationField() throws Exception {
    Field f = EnvelopStatementsEdit.class.getDeclaredField("toLocation");
    assertEquals(BlockStatementIndexPair.class, f.getType());
  }

  @Test
  public void envelopEdit_hasCountField() throws Exception {
    Field f = EnvelopStatementsEdit.class.getDeclaredField("count");
    assertEquals(int.class, f.getType());
  }

  @Test
  public void envelopEdit_hasTwoConstructors() {
    assertEquals(2, EnvelopStatementsEdit.class.getDeclaredConstructors().length);
  }

  // ══════════════════════════════════════════════════════════════
  // SwapParametersEdit — structural
  // ══════════════════════════════════════════════════════════════

  @Test
  public void swapEdit_isPublic() {
    assertTrue(Modifier.isPublic(SwapParametersEdit.class.getModifiers()));
  }

  @Test
  public void swapEdit_extendsAbstractEdit() {
    assertTrue(AbstractEdit.class.isAssignableFrom(SwapParametersEdit.class));
  }

  @Test
  public void swapEdit_hasMethodField() throws Exception {
    Field f = SwapParametersEdit.class.getDeclaredField("method");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void swapEdit_hasAIndexField() throws Exception {
    Field f = SwapParametersEdit.class.getDeclaredField("aIndex");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertEquals(int.class, f.getType());
  }

  @Test
  public void swapEdit_hasSwapPrivateMethod() {
    boolean found = Arrays.stream(SwapParametersEdit.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("swap") && Modifier.isPrivate(m.getModifiers()));
    assertTrue("Must have private swap() method", found);
  }

  @Test
  public void swapEdit_doOrRedoIsFinal() {
    boolean found = Arrays.stream(SwapParametersEdit.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("doOrRedoInternal") && Modifier.isFinal(m.getModifiers()));
    assertTrue(found);
  }

  @Test
  public void swapEdit_undoIsFinal() {
    boolean found = Arrays.stream(SwapParametersEdit.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("undoInternal") && Modifier.isFinal(m.getModifiers()));
    assertTrue(found);
  }

  // ── Helpers ─────────────────────────────────────────────────

  @SuppressWarnings("unchecked")
  private MoveStatementEdit createEdit(BlockStatementIndexPair from, Statement stmt,
      BlockStatementIndexPair to, boolean isMultiple) throws Exception {
    for (Constructor<?> c : MoveStatementEdit.class.getDeclaredConstructors()) {
      c.setAccessible(true);
      Class<?>[] paramTypes = c.getParameterTypes();
      if (paramTypes.length == 5) {
        return (MoveStatementEdit) c.newInstance(null, from, stmt, to, isMultiple);
      }
    }
    throw new IllegalStateException("No matching constructor");
  }

  private int invokeGetToDelta(MoveStatementEdit edit) throws Exception {
    Method m = MoveStatementEdit.class.getDeclaredMethod("getToDelta");
    m.setAccessible(true);
    return (int) m.invoke(edit);
  }

  private void invokeDoOrRedo(MoveStatementEdit edit, boolean isDo) throws Exception {
    Method m = findMethod(MoveStatementEdit.class, "doOrRedoInternal", boolean.class);
    m.setAccessible(true);
    m.invoke(edit, isDo);
  }

  private void invokeUndo(MoveStatementEdit edit) throws Exception {
    Method m = findMethod(MoveStatementEdit.class, "undoInternal");
    m.setAccessible(true);
    m.invoke(edit);
  }

  private Method findMethod(Class<?> cls, String name, Class<?>... paramTypes) throws NoSuchMethodException {
    try {
      return cls.getDeclaredMethod(name, paramTypes);
    } catch (NoSuchMethodException e) {
      if (cls.getSuperclass() != null) {
        return findMethod(cls.getSuperclass(), name, paramTypes);
      }
      throw e;
    }
  }
}
