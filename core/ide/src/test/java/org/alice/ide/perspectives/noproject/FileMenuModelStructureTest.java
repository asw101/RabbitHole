package org.alice.ide.perspectives.noproject;

import org.junit.Test;
import org.lgna.croquet.PredeterminedMenuModel;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class FileMenuModelStructureTest {
  @Test
  public void classExtendsPredeterminedMenuModel() {
    assertEquals(PredeterminedMenuModel.class, FileMenuModel.class.getSuperclass());
    assertTrue(Modifier.isPublic(FileMenuModel.class.getModifiers()));
  }

  @Test
  public void constructorAcceptsProjectDocumentFrame() throws Exception {
    Constructor<FileMenuModel> constructor = FileMenuModel.class.getConstructor(org.alice.ide.ProjectDocumentFrame.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void helperMethodsReturnExpectedTypes() throws Exception {
    Method createMenuItems = FileMenuModel.class.getDeclaredMethod("createMenuItemPrepModels", org.alice.ide.ProjectDocumentFrame.class);
    Method localization = FileMenuModel.class.getDeclaredMethod("getClassUsedForLocalization");

    assertEquals(org.lgna.croquet.StandardMenuItemPrepModel[].class, createMenuItems.getReturnType());
    assertEquals(Class.class, localization.getReturnType());
  }
}
