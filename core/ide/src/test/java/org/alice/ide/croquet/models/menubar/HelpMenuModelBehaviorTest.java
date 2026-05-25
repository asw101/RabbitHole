package org.alice.ide.croquet.models.menubar;

import edu.cmu.cs.dennisc.java.lang.SystemUtilities;
import org.alice.ide.IdeApp;
import org.junit.Test;
import org.lgna.croquet.MenuModel;
import org.lgna.croquet.PredeterminedMenuModel;
import org.lgna.croquet.StandardMenuItemPrepModel;

import java.lang.reflect.Method;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class HelpMenuModelBehaviorTest {
  private static StandardMenuItemPrepModel[] invokeCreateMenuItemPrepModels(IdeApp app) throws Exception {
    Method method = HelpMenuModel.class.getDeclaredMethod("createMenuItemPrepModels", IdeApp.class);
    method.setAccessible(true);
    return (StandardMenuItemPrepModel[]) method.invoke(null, app);
  }

  private static StandardMenuItemPrepModel[] invokeCreateModels(HelpMenuModel model) throws Exception {
    Method method = PredeterminedMenuModel.class.getDeclaredMethod("createModels");
    method.setAccessible(true);
    return (StandardMenuItemPrepModel[]) method.invoke(model);
  }

  @Test
  public void createMenuItemPrepModelsKeepsHelpReportAndAboutOrdering() throws Exception {
    StandardMenuItemPrepModel[] items = invokeCreateMenuItemPrepModels(IdeApp.INSTANCE);

    assertSame(IdeApp.INSTANCE.getHelpDialogLaunchOperation().getMenuItemPrepModel(), items[0]);
    assertSame(MenuModel.SEPARATOR, items[1]);
    assertSame(IdeApp.INSTANCE.getReportBugLaunchOperation().getMenuItemPrepModel(), items[2]);
    if (SystemUtilities.isMac()) {
      assertEquals(3, items.length);
    } else {
      assertEquals(5, items.length);
      assertSame(MenuModel.SEPARATOR, items[3]);
      assertSame(IdeApp.INSTANCE.getAboutDialogLaunchOperation().getMenuItemPrepModel(), items[4]);
    }
  }

  @Test
  public void constructorUsesThePredeterminedMenuItemsFromTheFactoryMethod() throws Exception {
    StandardMenuItemPrepModel[] expected = invokeCreateMenuItemPrepModels(IdeApp.INSTANCE);
    HelpMenuModel model = new HelpMenuModel(IdeApp.INSTANCE);

    assertArrayEquals(expected, invokeCreateModels(model));
  }
}
