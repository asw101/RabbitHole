package org.alice.ide.declarationseditor.type;

import org.alice.ide.ast.rename.RenameFieldComposite;
import org.alice.ide.croquet.models.ast.DeleteFieldOperation;
import org.alice.stageide.sceneeditor.side.MarkerColorIdCascade;
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
    StandardMenuItemPrepModel[] models = getModels(FieldMenuModel.getInstance(field));

    assertEquals(3, models.length);
    assertSame(RenameFieldComposite.getInstance(field).getLaunchOperation().getMenuItemPrepModel(), models[0]);
    assertSame(DeleteFieldOperation.getInstance(field).getMenuItemPrepModel(), models[1]);
    assertSame(MarkerColorIdCascade.getInstance(field).getMenuModel(), models[2]);
  }

  @Test
  public void distinctFields_receiveDistinctMenuModelInstances() {
    UserField first = new UserField("score", Object.class);
    UserField second = new UserField("score", Object.class);

    assertNotSame(FieldMenuModel.getInstance(first), FieldMenuModel.getInstance(second));
  }
}
