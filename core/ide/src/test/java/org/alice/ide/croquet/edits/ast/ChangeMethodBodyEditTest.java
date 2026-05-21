package org.alice.ide.croquet.edits.ast;

import org.junit.Test;
import org.lgna.croquet.*;
import org.lgna.croquet.edits.AbstractEdit;
import org.lgna.project.ast.*;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class ChangeMethodBodyEditTest {
  private static Object readField(Object instance, String fieldName) {
    try {
      Field field = instance.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.get(instance);
    } catch (ReflectiveOperationException roe) {
      throw new AssertionError(roe);
    }
  }

  private static UserMethod createMethod(BlockStatement body) {
    return new UserMethod("sample", Object.class, new UserParameter[0], body);
  }

  @Test
  public void construct_withNullUserActivity_succeeds() {
    ChangeMethodBodyEdit edit = new ChangeMethodBodyEdit(null, createMethod(new BlockStatement()), new BlockStatement());
    assertNotNull(edit);
  }

  @Test
  public void construct_capturesMethodPrevBodyAndNewBody() {
    BlockStatement prevBody = new BlockStatement();
    UserMethod method = createMethod(prevBody);
    BlockStatement newBody = new BlockStatement();
    ChangeMethodBodyEdit edit = new ChangeMethodBodyEdit(null, method, newBody);

    assertSame(method, readField(edit, "method"));
    assertSame(prevBody, readField(edit, "prevBody"));
    assertSame(newBody, readField(edit, "newBody"));
  }

  @Test
  public void construct_withNullNewBody_preservesNull() {
    UserMethod method = createMethod(new BlockStatement());
    ChangeMethodBodyEdit edit = new ChangeMethodBodyEdit(null, method, null);

    assertNull(readField(edit, "newBody"));
  }

  @Test
  public void construct_extendsAbstractEdit() {
    ChangeMethodBodyEdit edit = new ChangeMethodBodyEdit(null, createMethod(new BlockStatement()), new BlockStatement());

    assertTrue(edit instanceof AbstractEdit);
  }

  @Test
  public void doOrRedoInternal_whenDo_setsMethodBodyToNewBody() {
    BlockStatement prevBody = new BlockStatement();
    UserMethod method = createMethod(prevBody);
    BlockStatement newBody = new BlockStatement();
    ChangeMethodBodyEdit edit = new ChangeMethodBodyEdit(null, method, newBody);

    edit.doOrRedoInternal(true);

    assertSame(newBody, method.body.getValue());
  }

  @Test
  public void doOrRedoInternal_whenRedo_setsMethodBodyToNewBody() {
    BlockStatement prevBody = new BlockStatement();
    UserMethod method = createMethod(prevBody);
    BlockStatement newBody = new BlockStatement();
    ChangeMethodBodyEdit edit = new ChangeMethodBodyEdit(null, method, newBody);

    edit.doOrRedoInternal(false);

    assertSame(newBody, method.body.getValue());
  }

  @Test
  public void undoInternal_afterDo_restoresPreviousBody() {
    BlockStatement prevBody = new BlockStatement();
    UserMethod method = createMethod(prevBody);
    BlockStatement newBody = new BlockStatement();
    ChangeMethodBodyEdit edit = new ChangeMethodBodyEdit(null, method, newBody);

    edit.doOrRedoInternal(true);
    edit.undoInternal();

    assertSame(prevBody, method.body.getValue());
  }

  @Test
  public void doUndoRedo_cycle_restoresNewBody() {
    BlockStatement prevBody = new BlockStatement();
    UserMethod method = createMethod(prevBody);
    BlockStatement newBody = new BlockStatement();
    ChangeMethodBodyEdit edit = new ChangeMethodBodyEdit(null, method, newBody);

    edit.doOrRedoInternal(true);
    edit.undoInternal();
    edit.doOrRedoInternal(false);

    assertSame(newBody, method.body.getValue());
  }

  @Test
  public void undoInternal_afterNullBodyRedo_restoresPreviousBody() {
    BlockStatement prevBody = new BlockStatement();
    UserMethod method = createMethod(prevBody);
    ChangeMethodBodyEdit edit = new ChangeMethodBodyEdit(null, method, null);

    edit.doOrRedoInternal(true);
    assertNull(method.body.getValue());

    edit.undoInternal();
    assertSame(prevBody, method.body.getValue());
  }

  @Test
  public void getTerseDescription_whenAppendDescriptionEmpty_fallsBackToClassName() {
    ChangeMethodBodyEdit edit = new ChangeMethodBodyEdit(null, createMethod(new BlockStatement()), new BlockStatement());

    assertEquals("ChangeMethodBodyEdit", edit.getTerseDescription());
  }

  @Test
  public void getDetailedDescription_containsClassNameEvenWhenDescriptionEmpty() {
    ChangeMethodBodyEdit edit = new ChangeMethodBodyEdit(null, createMethod(new BlockStatement()), new BlockStatement());

    assertTrue(edit.getDetailedDescription().contains(ChangeMethodBodyEdit.class.getName()));
  }

  @Test
  public void toString_matchesDetailedDescription() {
    ChangeMethodBodyEdit edit = new ChangeMethodBodyEdit(null, createMethod(new BlockStatement()), new BlockStatement());

    assertEquals(edit.getDetailedDescription(), edit.toString());
  }
}
