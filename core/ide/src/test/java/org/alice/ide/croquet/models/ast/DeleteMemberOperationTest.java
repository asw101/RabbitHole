package org.alice.ide.croquet.models.ast;

import org.alice.ide.croquet.models.ResponsibleModel;
import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class DeleteMemberOperationTest {
  @Test
  public void isAbstract() {
    assertTrue(Modifier.isAbstract(DeleteMemberOperation.class.getModifiers()));
  }
  @Test
  public void implementsResponsibleModel() {
    assertTrue(ResponsibleModel.class.isAssignableFrom(DeleteMemberOperation.class));
  }
  @Test
  public void extendsActionOperation() {
    assertTrue(org.lgna.croquet.ActionOperation.class.isAssignableFrom(DeleteMemberOperation.class));
  }
  @Test
  public void deleteFieldOperation_extendsDeleteMember() {
    assertTrue(DeleteMemberOperation.class.isAssignableFrom(DeleteFieldOperation.class));
  }
  @Test
  public void deleteMethodOperation_extendsDeleteMember() {
    assertTrue(DeleteMemberOperation.class.isAssignableFrom(DeleteMethodOperation.class));
  }
  @Test
  public void deleteFieldOperation_hasGetInstance() throws Exception {
    java.lang.reflect.Method m = DeleteFieldOperation.class.getMethod("getInstance", org.lgna.project.ast.UserField.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }
  @Test
  public void deleteMethodOperation_hasGetInstance() throws Exception {
    java.lang.reflect.Method m = DeleteMethodOperation.class.getMethod("getInstance", org.lgna.project.ast.UserMethod.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }
}
