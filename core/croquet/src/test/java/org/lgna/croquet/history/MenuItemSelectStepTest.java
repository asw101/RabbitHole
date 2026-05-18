package org.lgna.croquet.history;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

public class MenuItemSelectStepTest {

  @After
  public void clearSelection() {
    javax.swing.MenuSelectionManager.defaultManager().clearSelectedPath();
  }

  @Test
  public void createAndAddToActivityAddsStep() {
    UserActivity activity = new UserActivity();
    MenuSelection selection = HistoryTestSupport.createSelection("file", "open");

    MenuItemSelectStep.createAndAddToActivity(activity, selection, null);

    assertSame(activity.findFirstMenuSelectStep(), activity.getChildAt(0));
  }

  @Test
  public void createdStepStoresMenuSelection() {
    UserActivity activity = new UserActivity();
    MenuSelection selection = HistoryTestSupport.createSelection("file", "open");

    MenuItemSelectStep.createAndAddToActivity(activity, selection, null);

    assertSame(selection, activity.findFirstMenuSelectStep().getMenuSelection());
  }

  @Test
  public void createdStepUsesLastMenuItemPrepModel() {
    UserActivity activity = new UserActivity();
    MenuSelection selection = HistoryTestSupport.createSelection("file", "open");

    MenuItemSelectStep.createAndAddToActivity(activity, selection, null);

    assertSame(selection.getLastMenuItemPrepModel(), activity.findFirstMenuSelectStep().getModel());
  }

  @Test
  public void createdStepUsesParentTrigger() {
    UserActivity activity = new UserActivity();
    activity.setTrigger(new HistoryTestSupport.TestTrigger(activity, new HistoryTestSupport.TestView()));
    MenuSelection selection = HistoryTestSupport.createSelection("file", "open");

    MenuItemSelectStep.createAndAddToActivity(activity, selection, activity.getTrigger());

    assertSame(activity.getTrigger(), activity.findFirstMenuSelectStep().getTrigger());
  }

  @Test
  public void createdStepBelongsToParentActivity() {
    UserActivity activity = new UserActivity();
    MenuSelection selection = HistoryTestSupport.createSelection("file", "open");

    MenuItemSelectStep.createAndAddToActivity(activity, selection, null);

    assertSame(activity, activity.findFirstMenuSelectStep().getUserActivity());
  }

  @Test
  public void createdStepAppearsInIndexLookup() {
    UserActivity activity = new UserActivity();
    MenuSelection selection = HistoryTestSupport.createSelection("file", "open");

    MenuItemSelectStep.createAndAddToActivity(activity, selection, null);

    assertEquals(0, activity.getIndexOfPrepStep(activity.findFirstMenuSelectStep()));
  }

  @Test
  public void toStringContainsClassName() {
    UserActivity activity = new UserActivity();
    MenuSelection selection = HistoryTestSupport.createSelection("file", "open");

    MenuItemSelectStep.createAndAddToActivity(activity, selection, null);

    assertTrue(activity.findFirstMenuSelectStep().toString().contains("MenuItemSelectStep"));
  }

  @Test
  public void createdStepIncrementsChildStepCount() {
    UserActivity activity = new UserActivity();
    MenuSelection selection = HistoryTestSupport.createSelection("file", "open");

    MenuItemSelectStep.createAndAddToActivity(activity, selection, null);

    assertEquals(1, activity.getChildStepCount());
  }
}
