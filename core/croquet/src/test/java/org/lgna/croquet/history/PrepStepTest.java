package org.lgna.croquet.history;

import org.junit.Test;

import static org.junit.Assert.*;

public class PrepStepTest {

  @Test
  public void constructorAddsPrepStepToParentActivity() {
    UserActivity activity = new UserActivity();
    TestPrepStep step = new TestPrepStep(activity, new HistoryTestSupport.TestPrepModel("prep"), null);

    assertSame(step, activity.getChildAt(0));
  }

  @Test
  public void getUserActivityReturnsOwner() {
    UserActivity activity = new UserActivity();
    TestPrepStep step = new TestPrepStep(activity, new HistoryTestSupport.TestPrepModel(), null);

    assertSame(activity, step.getUserActivity());
  }

  @Test
  public void getViewControllerIsNullWhenTriggerIsNull() {
    UserActivity activity = new UserActivity();
    TestPrepStep step = new TestPrepStep(activity, new HistoryTestSupport.TestPrepModel("prep"), null);

    assertNull(step.getViewController());
  }

  @Test
  public void getViewControllerDelegatesToTrigger() {
    UserActivity activity = new UserActivity();
    HistoryTestSupport.TestView view = new HistoryTestSupport.TestView();
    TestPrepStep step = new TestPrepStep(activity, new HistoryTestSupport.TestPrepModel("prep"),
        new HistoryTestSupport.TestTrigger(activity, view));

    assertSame(view, step.getViewController());
  }

  @Test
  public void getModelReturnsPrepModel() {
    HistoryTestSupport.TestPrepModel model = new HistoryTestSupport.TestPrepModel("prep");
    TestPrepStep step = new TestPrepStep(new UserActivity(), model, null);

    assertSame(model, step.getModel());
  }

  @Test
  public void toStringContainsClassName() {
    TestPrepStep step = new TestPrepStep(new UserActivity(), new HistoryTestSupport.TestPrepModel("prep"), null);
    assertTrue(step.toString().contains("TestPrepStep"));
  }

  @Test
  public void toStringContainsTriggerWhenPresent() {
    UserActivity activity = new UserActivity();
    TestPrepStep step = new TestPrepStep(activity, new HistoryTestSupport.TestPrepModel("prep"),
        new HistoryTestSupport.TestTrigger(activity, new HistoryTestSupport.TestView()));

    assertTrue(step.toString().contains("trigger="));
  }

  @Test
  public void cancelActivityCancelsUserActivity() {
    UserActivity activity = new UserActivity();
    TestPrepStep step = new TestPrepStep(activity, new HistoryTestSupport.TestPrepModel("prep"), null);

    step.cancelActivity();

    assertTrue(activity.isCanceled());
  }

  private static final class TestPrepStep extends PrepStep<HistoryTestSupport.TestPrepModel> {
    private TestPrepStep(UserActivity parent, HistoryTestSupport.TestPrepModel model,
        org.lgna.croquet.triggers.Trigger trigger) {
      super(parent, model, trigger);
    }
  }
}
