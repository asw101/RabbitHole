package org.alice.ide.ast.delete;

import org.alice.ide.ast.delete.edits.DeleteStatementEdit;
import org.junit.Test;
import org.lgna.croquet.ActionOperation;
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
 * Characterization tests for DeleteStatementOperation and DeleteStatementEdit.
 * Uses reflection for structure verification and behavioral tests where
 * the edit's do/undo logic can be invoked headlessly via reflection.
 */
public class DeleteStatementEditCharacterizationTest {

  // ══════════════════════════════════════════════════════════════
  // DeleteStatementOperation — structural characterization
  // ══════════════════════════════════════════════════════════════

  @Test
  public void deleteOp_isPublic() {
    assertTrue(Modifier.isPublic(DeleteStatementOperation.class.getModifiers()));
  }

  @Test
  public void deleteOp_extendsActionOperation() {
    assertTrue(ActionOperation.class.isAssignableFrom(DeleteStatementOperation.class));
  }

  @Test
  public void deleteOp_hasStatementField() throws Exception {
    Field f = DeleteStatementOperation.class.getDeclaredField("statement");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertEquals(Statement.class, f.getType());
  }

  @Test
  public void deleteOp_constructorTakesStatement() {
    boolean found = Arrays.stream(DeleteStatementOperation.class.getDeclaredConstructors())
        .anyMatch(c -> {
          Class<?>[] params = c.getParameterTypes();
          return params.length == 1 && Statement.class.isAssignableFrom(params[0]);
        });
    assertTrue("Constructor must accept Statement", found);
  }

  @Test
  public void deleteOp_hasPerformMethod() {
    boolean found = Arrays.stream(DeleteStatementOperation.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("perform"));
    assertTrue("Must override perform()", found);
  }

  @Test
  public void deleteOp_inCorrectPackage() {
    assertEquals("org.alice.ide.ast.delete",
        DeleteStatementOperation.class.getPackage().getName());
  }

  // ══════════════════════════════════════════════════════════════
  // DeleteStatementEdit — structural characterization
  // ══════════════════════════════════════════════════════════════

  @Test
  public void deleteEdit_isPublic() {
    assertTrue(Modifier.isPublic(DeleteStatementEdit.class.getModifiers()));
  }

  @Test
  public void deleteEdit_extendsAbstractEdit() {
    assertTrue(AbstractEdit.class.isAssignableFrom(DeleteStatementEdit.class));
  }

  @Test
  public void deleteEdit_inCorrectPackage() {
    assertEquals("org.alice.ide.ast.delete.edits",
        DeleteStatementEdit.class.getPackage().getName());
  }

  @Test
  public void deleteEdit_hasBlockStatementField() throws Exception {
    Field f = DeleteStatementEdit.class.getDeclaredField("blockStatement");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertEquals(BlockStatement.class, f.getType());
  }

  @Test
  public void deleteEdit_hasIndexField() throws Exception {
    Field f = DeleteStatementEdit.class.getDeclaredField("index");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertEquals(int.class, f.getType());
  }

  @Test
  public void deleteEdit_hasDoOrRedoInternalMethod() {
    boolean found = Arrays.stream(DeleteStatementEdit.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("doOrRedoInternal")
            && Modifier.isFinal(m.getModifiers()));
    assertTrue("doOrRedoInternal must be declared and final", found);
  }

  @Test
  public void deleteEdit_hasUndoInternalMethod() {
    boolean found = Arrays.stream(DeleteStatementEdit.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("undoInternal")
            && Modifier.isFinal(m.getModifiers()));
    assertTrue("undoInternal must be declared and final", found);
  }

  @Test
  public void deleteEdit_hasEncodeMethod() {
    boolean found = Arrays.stream(DeleteStatementEdit.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("encode"));
    assertTrue("Must override encode()", found);
  }

  @Test
  public void deleteEdit_hasAppendDescriptionMethod() {
    boolean found = Arrays.stream(DeleteStatementEdit.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("appendDescription"));
    assertTrue("Must override appendDescription()", found);
  }

  @Test
  public void deleteEdit_hasTwoConstructors() {
    Constructor<?>[] constructors = DeleteStatementEdit.class.getDeclaredConstructors();
    assertEquals("Should have 2 constructors (UserActivity and BinaryDecoder)", 2, constructors.length);
  }

  // ══════════════════════════════════════════════════════════════
  // DeleteStatementEdit — behavioral: do/undo via reflection
  // ══════════════════════════════════════════════════════════════

  @Test
  public void deleteEdit_doRemovesStatementFromBlock() throws Exception {
    BlockStatement block = new BlockStatement();
    Comment stmt = new Comment("test comment");
    block.statements.add(stmt);
    assertEquals(1, block.statements.size());

    DeleteStatementEdit edit = createEditViaReflection(stmt, block, 0);
    invokeDoOrRedo(edit, true);

    assertEquals("Statement should be removed", 0, block.statements.size());
  }

  @Test
  public void deleteEdit_undoRestoresStatementToBlock() throws Exception {
    BlockStatement block = new BlockStatement();
    Comment stmt = new Comment("test comment");
    block.statements.add(stmt);

    DeleteStatementEdit edit = createEditViaReflection(stmt, block, 0);
    invokeDoOrRedo(edit, true);
    assertEquals(0, block.statements.size());

    invokeUndo(edit);
    assertEquals("Statement should be restored", 1, block.statements.size());
    assertSame(stmt, block.statements.get(0));
  }

  @Test
  public void deleteEdit_doUndoRoundTrip_multipleStatements() throws Exception {
    BlockStatement block = new BlockStatement();
    Comment s0 = new Comment("first");
    Comment s1 = new Comment("second");
    Comment s2 = new Comment("third");
    block.statements.add(s0);
    block.statements.add(s1);
    block.statements.add(s2);

    DeleteStatementEdit edit = createEditViaReflection(s1, block, 1);
    invokeDoOrRedo(edit, true);

    assertEquals(2, block.statements.size());
    assertSame(s0, block.statements.get(0));
    assertSame(s2, block.statements.get(1));

    invokeUndo(edit);
    assertEquals(3, block.statements.size());
    assertSame(s0, block.statements.get(0));
    assertSame(s1, block.statements.get(1));
    assertSame(s2, block.statements.get(2));
  }

  @Test
  public void deleteEdit_doUndoRoundTrip_firstStatement() throws Exception {
    BlockStatement block = new BlockStatement();
    Comment s0 = new Comment("first");
    Comment s1 = new Comment("second");
    block.statements.add(s0);
    block.statements.add(s1);

    DeleteStatementEdit edit = createEditViaReflection(s0, block, 0);
    invokeDoOrRedo(edit, true);

    assertEquals(1, block.statements.size());
    assertSame(s1, block.statements.get(0));

    invokeUndo(edit);
    assertEquals(2, block.statements.size());
    assertSame(s0, block.statements.get(0));
    assertSame(s1, block.statements.get(1));
  }

  @Test
  public void deleteEdit_doUndoRoundTrip_lastStatement() throws Exception {
    BlockStatement block = new BlockStatement();
    Comment s0 = new Comment("first");
    Comment s1 = new Comment("last");
    block.statements.add(s0);
    block.statements.add(s1);

    DeleteStatementEdit edit = createEditViaReflection(s1, block, 1);
    invokeDoOrRedo(edit, true);

    assertEquals(1, block.statements.size());
    assertSame(s0, block.statements.get(0));

    invokeUndo(edit);
    assertEquals(2, block.statements.size());
    assertSame(s1, block.statements.get(1));
  }

  @Test
  public void deleteEdit_negativeIndexDoesNotCrash() throws Exception {
    Comment orphan = new Comment("orphan");
    DeleteStatementEdit edit = createEditViaReflection(orphan, null, -1);

    // Should just log warning, not throw
    invokeDoOrRedo(edit, true);
    invokeUndo(edit);
  }

  @Test
  public void deleteEdit_redoWorksAfterUndo() throws Exception {
    BlockStatement block = new BlockStatement();
    Comment stmt = new Comment("test");
    block.statements.add(stmt);

    DeleteStatementEdit edit = createEditViaReflection(stmt, block, 0);

    invokeDoOrRedo(edit, true);
    assertEquals(0, block.statements.size());

    invokeUndo(edit);
    assertEquals(1, block.statements.size());

    invokeDoOrRedo(edit, false);
    assertEquals(0, block.statements.size());
  }

  // ── Helpers ─────────────────────────────────────────────────

  @SuppressWarnings("unchecked")
  private DeleteStatementEdit createEditViaReflection(Statement stmt, BlockStatement block, int index) throws Exception {
    for (Constructor<?> c : DeleteStatementEdit.class.getDeclaredConstructors()) {
      c.setAccessible(true);
      Class<?>[] paramTypes = c.getParameterTypes();
      if (paramTypes.length == 2 && Statement.class.isAssignableFrom(paramTypes[1])) {
        DeleteStatementEdit edit = (DeleteStatementEdit) c.newInstance(null, stmt);
        Field blockField = DeleteStatementEdit.class.getDeclaredField("blockStatement");
        blockField.setAccessible(true);
        blockField.set(edit, block);

        Field indexField = DeleteStatementEdit.class.getDeclaredField("index");
        indexField.setAccessible(true);
        indexField.set(edit, index);

        return edit;
      }
    }
    throw new IllegalStateException("No matching constructor found");
  }

  private void invokeDoOrRedo(DeleteStatementEdit edit, boolean isDo) throws Exception {
    Method method = findMethod(DeleteStatementEdit.class, "doOrRedoInternal", boolean.class);
    method.setAccessible(true);
    method.invoke(edit, isDo);
  }

  private void invokeUndo(DeleteStatementEdit edit) throws Exception {
    Method method = findMethod(DeleteStatementEdit.class, "undoInternal");
    method.setAccessible(true);
    method.invoke(edit);
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
