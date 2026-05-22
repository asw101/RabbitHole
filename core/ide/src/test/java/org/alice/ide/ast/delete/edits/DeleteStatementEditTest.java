package org.alice.ide.ast.delete.edits;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.Statement;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class DeleteStatementEditTest {

  private DeleteStatementEdit newEdit(Statement statement) {
    return new DeleteStatementEdit((org.lgna.croquet.history.UserActivity) null, statement);
  }

  private void invokeDo(DeleteStatementEdit edit, boolean isDo) throws Exception {
    Method method = DeleteStatementEdit.class.getDeclaredMethod("doOrRedoInternal", boolean.class);
    method.setAccessible(true);
    method.invoke(edit, isDo);
  }

  private void invokeUndo(DeleteStatementEdit edit) throws Exception {
    Method method = DeleteStatementEdit.class.getDeclaredMethod("undoInternal");
    method.setAccessible(true);
    method.invoke(edit);
  }

  private Object readField(DeleteStatementEdit edit, String name) throws Exception {
    Field field = DeleteStatementEdit.class.getDeclaredField(name);
    field.setAccessible(true);
    return field.get(edit);
  }

  @Test
  public void classIsPublic() {
    assertTrue(Modifier.isPublic(DeleteStatementEdit.class.getModifiers()));
  }

  @Test
  public void simpleNameMatchesSource() {
    assertEquals("DeleteStatementEdit", DeleteStatementEdit.class.getSimpleName());
  }

  @Test
  public void packageNameMatchesSource() {
    assertEquals("org.alice.ide.ast.delete.edits", DeleteStatementEdit.class.getPackage().getName());
  }

  @Test
  public void constructorForUserActivityAndStatementExists() throws Exception {
    assertNotNull(DeleteStatementEdit.class.getDeclaredConstructor(org.lgna.croquet.history.UserActivity.class, Statement.class));
  }

  @Test
  public void binaryConstructorExists() throws Exception {
    assertNotNull(DeleteStatementEdit.class.getDeclaredConstructor(edu.cmu.cs.dennisc.codec.BinaryDecoder.class, Object.class));
  }

  @Test
  public void blockStatementFieldIsPrivateFinal() throws Exception {
    Field field = DeleteStatementEdit.class.getDeclaredField("blockStatement");
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void indexFieldIsPrivateFinal() throws Exception {
    Field field = DeleteStatementEdit.class.getDeclaredField("index");
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void constructorCapturesOwningBlock() throws Exception {
    BlockStatement block = new BlockStatement();
    Comment statement = new Comment("first");
    block.statements.add(statement);
    assertSame(block, readField(newEdit(statement), "blockStatement"));
  }

  @Test
  public void constructorCapturesFirstIndex() throws Exception {
    BlockStatement block = new BlockStatement();
    Comment statement = new Comment("first");
    block.statements.add(statement);
    assertEquals(0, readField(newEdit(statement), "index"));
  }

  @Test
  public void constructorCapturesMiddleIndex() throws Exception {
    BlockStatement block = new BlockStatement();
    block.statements.add(new Comment("a"));
    Comment statement = new Comment("b");
    block.statements.add(statement);
    block.statements.add(new Comment("c"));
    assertEquals(1, readField(newEdit(statement), "index"));
  }

  @Test
  public void orphanStatementProducesNullBlock() throws Exception {
    assertNull(readField(newEdit(new Comment("orphan")), "blockStatement"));
  }

  @Test
  public void orphanStatementProducesNegativeIndex() throws Exception {
    assertEquals(-1, readField(newEdit(new Comment("orphan")), "index"));
  }

  @Test
  public void doRemovesFirstStatement() throws Exception {
    BlockStatement block = new BlockStatement();
    Comment first = new Comment("first");
    block.statements.add(first);
    block.statements.add(new Comment("second"));
    invokeDo(newEdit(first), true);
    assertEquals(1, block.statements.size());
    assertEquals("second", ((Comment) block.statements.get(0)).text.getValue());
  }

  @Test
  public void doRemovesMiddleStatement() throws Exception {
    BlockStatement block = new BlockStatement();
    block.statements.add(new Comment("first"));
    Comment middle = new Comment("middle");
    block.statements.add(middle);
    block.statements.add(new Comment("last"));
    invokeDo(newEdit(middle), true);
    assertEquals(2, block.statements.size());
  }

  @Test
  public void doRemovesLastStatement() throws Exception {
    BlockStatement block = new BlockStatement();
    block.statements.add(new Comment("first"));
    Comment last = new Comment("last");
    block.statements.add(last);
    invokeDo(newEdit(last), true);
    assertEquals(1, block.statements.size());
    assertEquals("first", ((Comment) block.statements.get(0)).text.getValue());
  }

  @Test
  public void undoRestoresRemovedStatementAtOriginalIndex() throws Exception {
    BlockStatement block = new BlockStatement();
    block.statements.add(new Comment("first"));
    Comment middle = new Comment("middle");
    block.statements.add(middle);
    block.statements.add(new Comment("last"));
    DeleteStatementEdit edit = newEdit(middle);
    invokeDo(edit, true);
    invokeUndo(edit);
    assertSame(middle, block.statements.get(1));
  }

  @Test
  public void redoRemovesStatementAgainAfterUndo() throws Exception {
    BlockStatement block = new BlockStatement();
    Comment statement = new Comment("value");
    block.statements.add(statement);
    DeleteStatementEdit edit = newEdit(statement);
    invokeDo(edit, true);
    invokeUndo(edit);
    invokeDo(edit, false);
    assertTrue(block.statements.isEmpty());
  }

  @Test
  public void doOnOrphanDoesNotThrow() throws Exception {
    invokeDo(newEdit(new Comment("orphan")), true);
  }

  @Test
  public void undoOnOrphanDoesNotThrow() throws Exception {
    invokeUndo(newEdit(new Comment("orphan")));
  }

  @Test
  public void doMethodExistsAndIsFinal() throws Exception {
    Method method = DeleteStatementEdit.class.getDeclaredMethod("doOrRedoInternal", boolean.class);
    assertTrue(Modifier.isFinal(method.getModifiers()));
  }

  @Test
  public void undoMethodExistsAndIsFinal() throws Exception {
    Method method = DeleteStatementEdit.class.getDeclaredMethod("undoInternal");
    assertTrue(Modifier.isFinal(method.getModifiers()));
  }

  @Test
  public void encodeMethodExists() throws Exception {
    assertNotNull(DeleteStatementEdit.class.getDeclaredMethod("encode", edu.cmu.cs.dennisc.codec.BinaryEncoder.class));
  }

  @Test
  public void appendDescriptionMethodExists() {
    boolean found = false;
    for (Method method : DeleteStatementEdit.class.getDeclaredMethods()) {
      if ("appendDescription".equals(method.getName()) && method.getParameterCount() == 2) {
        found = true;
      }
    }
    assertTrue(found);
  }

  @Test
  public void declaredFieldCountIncludesEditState() {
    assertTrue(DeleteStatementEdit.class.getDeclaredFields().length >= 2);
  }

  @Test
  public void declaredMethodCountIsFour() {
    assertEquals(4, DeleteStatementEdit.class.getDeclaredMethods().length);
  }
}
