package org.alice.ide.declarationseditor.type;

import org.junit.Test;
import org.lgna.croquet.PredeterminedMenuModel;
import org.lgna.croquet.StandardMenuItemPrepModel;
import org.lgna.project.ast.UserField;
import org.lgna.story.SMarker;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class FieldMenuModelBehaviorTest {
  private static StandardMenuItemPrepModel[] getModels(FieldMenuModel model) throws Exception {
    Field field = PredeterminedMenuModel.class.getDeclaredField("models");
    field.setAccessible(true);
    return (StandardMenuItemPrepModel[])field.get(model);
  }

  @Test
  public void getInstance_cachesPerField() {
    UserField field = new UserField("score", Object.class);
    assertSame(FieldMenuModel.getInstance(field), FieldMenuModel.getInstance(field));
  }

  @Test
  public void nonMarkerField_containsRenameAndDeletePrepModels() throws Exception {
    UserField field = new UserField("score", Object.class);
    FieldMenuModel model = FieldMenuModel.getInstance(field);

    assertSame(field, model.getMember());
    assertTrue(model.showScrollArrows());
    assertEquals(2, getModels(model).length);
  }

  @Test
  public void markerField_addsMarkerColorCascade() throws Exception {
    UserField field = new UserField("marker", SMarker.class);
    assertEquals(3, getModels(FieldMenuModel.getInstance(field)).length);
  }
}
