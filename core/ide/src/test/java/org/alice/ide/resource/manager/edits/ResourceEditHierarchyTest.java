package org.alice.ide.resource.manager.edits;

import org.junit.Test;
import org.lgna.common.Resource;
import org.lgna.common.resources.AudioResource;

import java.util.UUID;

import static org.junit.Assert.*;

public class ResourceEditHierarchyTest {

  @Test
  public void addResourceEdit_extendsAddOrRemoveResourceEdit() {
    assertTrue(AddOrRemoveResourceEdit.class.isAssignableFrom(AddResourceEdit.class));
  }

  @Test
  public void removeResourceEdit_extendsAddOrRemoveResourceEdit() {
    assertTrue(AddOrRemoveResourceEdit.class.isAssignableFrom(RemoveResourceEdit.class));
  }

  @Test
  public void renameResourceEdit_isFinal() {
    assertTrue(java.lang.reflect.Modifier.isFinal(RenameResourceEdit.class.getModifiers()));
  }

  @Test
  public void addOrRemoveResourceEdit_isAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(AddOrRemoveResourceEdit.class.getModifiers()));
  }

  @Test
  public void addResourceEdit_hasDoOrRedoInternal() throws Exception {
    var method = AddResourceEdit.class.getDeclaredMethod("doOrRedoInternal", boolean.class);
    assertNotNull(method);
  }

  @Test
  public void removeResourceEdit_hasUndoInternal() throws Exception {
    var method = RemoveResourceEdit.class.getDeclaredMethod("undoInternal");
    assertNotNull(method);
  }

  @Test
  public void addOrRemoveResourceEdit_hasGetResource() throws Exception {
    var method = AddOrRemoveResourceEdit.class.getDeclaredMethod("getResource");
    assertNotNull(method);
  }
}
