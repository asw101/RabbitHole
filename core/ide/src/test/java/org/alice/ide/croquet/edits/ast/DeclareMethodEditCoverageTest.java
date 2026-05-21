package org.alice.ide.croquet.edits.ast;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserMethod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class DeclareMethodEditCoverageTest {

  @Test
  public void tutorialHackSetter_updatesMethodField() throws Exception {
    DeclareMethodEdit edit = new DeclareMethodEdit(null, createType(), "move", JavaType.VOID_TYPE, new BlockStatement());
    UserMethod method = new UserMethod();
    edit.EPIC_HACK_FOR_TUTORIAL_GENERATION_setMethod(method);
    Field field = DeclareMethodEdit.class.getDeclaredField("method");
    field.setAccessible(true);
    assertSame(method, field.get(edit));
  }

  @Test
  public void terseDescription_mentionsMethodName() {
    DeclareMethodEdit edit = new DeclareMethodEdit(null, createType(), "move", JavaType.VOID_TYPE, new BlockStatement());
    assertTrue(edit.getTerseDescription().contains("move"));
  }

  @Test
  public void preCopyAndPostCopy_doNotThrow() throws Exception {
    DeclareMethodEdit edit = new DeclareMethodEdit(null, createType(), "move", JavaType.VOID_TYPE, new BlockStatement());
    Method preCopy = DeclareMethodEdit.class.getDeclaredMethod("preCopy");
    preCopy.setAccessible(true);
    Method postCopy = DeclareMethodEdit.class.getDeclaredMethod("postCopy", org.lgna.croquet.edits.AbstractEdit.class);
    postCopy.setAccessible(true);
    preCopy.invoke(edit);
    postCopy.invoke(edit, edit);
  }

  private NamedUserType createType() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("Hero");
    return type;
  }
}
