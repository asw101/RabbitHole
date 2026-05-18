package org.alice.ide.member;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;

import static org.junit.Assert.*;

public class AddMethodMenuModelTest {

  @Test
  public void addMethodMenuModel_isAbstract() {
    assertTrue(Modifier.isAbstract(AddMethodMenuModel.class.getModifiers()));
  }

  @Test
  public void addMethodMenuModel_hasUuidConstructor() throws Exception {
    assertNotNull(AddMethodMenuModel.class.getConstructor(UUID.class));
  }

  @Test
  public void addMethodMenuModel_exposesIsRelevantMethod() throws Exception {
    Method method = AddMethodMenuModel.class.getMethod("isRelevant");
    assertEquals(boolean.class, method.getReturnType());
  }

  @Test
  public void addMethodMenuModel_handlePopupMenuPrologueIsFinal() throws Exception {
    Method method = AddMethodMenuModel.class.getMethod("handlePopupMenuPrologue", org.lgna.croquet.views.PopupMenu.class);
    assertTrue(Modifier.isFinal(method.getModifiers()));
  }
}
