package org.alice.ide.member;

import org.junit.Test;

import static org.junit.Assert.*;

public class AddMethodMenuModelTest {

  @Test
  public void class_isAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(AddMethodMenuModel.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(java.lang.reflect.Modifier.isPublic(AddMethodMenuModel.class.getModifiers()));
  }

  @Test
  public void class_hasIsRelevant() throws Exception {
    assertNotNull(AddMethodMenuModel.class.getDeclaredMethod("isRelevant"));
  }

  @Test
  public void subclasses_exist() {
    assertTrue(AddProcedureMenuModel.class.getSuperclass() == AddMethodMenuModel.class
        || AddFunctionMenuModel.class.getSuperclass() == AddMethodMenuModel.class);
  }
}
