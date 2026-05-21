package org.alice.ide.croquet.edits.ast;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class AddParameterEditCoverageTest {
  @Test
  public void canUndoAndRedo_areTrueBeforePerforming() {
    AddParameterEdit edit = new AddParameterEdit(null, createMethod(), new UserParameter("value", String.class));
    assertTrue(edit.canUndo());
    assertTrue(edit.canRedo());
  }

  @Test
  public void terseDescription_mentionsDeclaration() {
    AddParameterEdit edit = new AddParameterEdit(null, createMethod(), new UserParameter("value", String.class));
    assertTrue(edit.getTerseDescription().startsWith("declare:"));
  }

  @Test
  public void indexField_defaultsToZero() throws Exception {
    AddParameterEdit edit = new AddParameterEdit(null, createMethod(), new UserParameter("value", String.class));
    Field field = AddParameterEdit.class.getDeclaredField("index");
    field.setAccessible(true);
    assertEquals(0, field.getInt(edit));
  }

  private UserMethod createMethod() {
    return new UserMethod("sample", Object.class, new UserParameter[0], new BlockStatement());
  }
}
