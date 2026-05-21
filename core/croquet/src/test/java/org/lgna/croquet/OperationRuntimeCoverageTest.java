package org.lgna.croquet;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class OperationRuntimeCoverageTest {
  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-1200-000000000001"), "operationRuntime");

  @BeforeClass
  public static void setUpClass() {
    CroquetTestUtils.ensureTestApplication();
  }

  @Test
  public void fire_enabled_initializesAndPerformsActionOperation() {
    TrackingActionOperation operation = new TrackingActionOperation();
    UserActivity activity = new UserActivity();

    operation.fire(activity);

    assertTrue(operation.localized);
    assertTrue(operation.performed);
    assertSame(activity, operation.lastActivity);
    assertSame(operation, activity.getCompletionModel());
  }

  @Test
  @SuppressWarnings("deprecation")
  public void deprecatedFire_usesNullTriggerBackedActivity() {
    TrackingActionOperation operation = new TrackingActionOperation();

    operation.fire();

    assertNotNull(operation.lastActivity);
    assertNotNull(operation.lastActivity.getTrigger());
    assertEquals("NullTrigger", operation.lastActivity.getTrigger().getClass().getSimpleName());
    assertSame(operation, operation.lastActivity.getCompletionModel());
  }

  @Test
  public void iteratingOperation_successfulIteration_finishesAndInvokesAllSteps() {
    ProducedValueTriggerable first = new ProducedValueTriggerable("alpha", true);
    ProducedValueTriggerable second = new ProducedValueTriggerable("omega", true);
    ExposedIteratingOperation operation = new ExposedIteratingOperation(Arrays.asList(first, second), false);
    UserActivity activity = new UserActivity();

    operation.runIterate(activity);

    assertTrue(activity.isSuccessfullyCompleted());
    assertEquals(1, first.fireCount);
    assertEquals(1, second.fireCount);
  }

  @Test
  public void iteratingOperation_exposeLastValueReturnsProducedValueFromLastStep() {
    ExposedIteratingOperation operation = new ExposedIteratingOperation(List.of(), false);
    UserActivity first = new UserActivity();
    first.setProducedValue("alpha");
    UserActivity second = new UserActivity();
    second.setProducedValue("omega");

    assertEquals("omega", operation.exposeLastValue(Arrays.asList(first, second)));
  }

  @Test
  public void iteratingOperation_nullNextCancelsActivity() {
    ExposedIteratingOperation operation = new ExposedIteratingOperation(List.of(), true);
    UserActivity activity = new UserActivity();

    operation.runIterate(activity);

    assertTrue(activity.isCanceled());
    assertTrue(activity.isCanceledByError());
  }

  @Test
  public void iteratingOperation_pendingChildCancelsActivity() {
    ExposedIteratingOperation operation = new ExposedIteratingOperation(List.of(new PendingTriggerable()), false);
    UserActivity activity = new UserActivity();

    operation.runIterate(activity);

    assertTrue(activity.isCanceled());
    assertTrue(activity.isCanceledByError());
    assertEquals(1, activity.getChildActivities().size());
    assertTrue(activity.getChildActivities().get(0).isPending());
  }

  @Test
  public void singleThreadIteratingOperation_fireDelegatesToIterateOverSubModels() {
    ProducedValueTriggerable step = new ProducedValueTriggerable("done", true);
    TrackingSingleThreadIteratingOperation operation = new TrackingSingleThreadIteratingOperation(List.of(step));
    UserActivity activity = new UserActivity();

    operation.fire(activity);

    assertTrue(activity.isSuccessfullyCompleted());
    assertEquals(1, step.fireCount);
    assertSame(operation, activity.getCompletionModel());
  }

  private static final class TrackingActionOperation extends ActionOperation {
    private boolean localized;
    private boolean performed;
    private UserActivity lastActivity;

    private TrackingActionOperation() {
      super(TEST_GROUP, CroquetTestUtils.nextTestUUID());
    }

    @Override
    protected void localize() {
      this.localized = true;
    }

    @Override
    protected void perform(UserActivity activity) {
      this.performed = true;
      this.lastActivity = activity;
    }
  }

  private static final class ExposedIteratingOperation extends IteratingOperation {
    private final List<Triggerable> steps;
    private final boolean returnNull;

    private ExposedIteratingOperation(List<Triggerable> steps, boolean returnNull) {
      super(TEST_GROUP, CroquetTestUtils.nextTestUUID());
      this.steps = steps;
      this.returnNull = returnNull;
    }

    private void runIterate(UserActivity activity) {
      this.iterateOverSubModels(activity);
    }

    private Object exposeLastValue(List<UserActivity> finishedSteps) {
      return this.getLastValueProduced(finishedSteps);
    }

    @Override
    protected boolean hasNext(List<UserActivity> finishedSteps) {
      return this.returnNull ? finishedSteps.isEmpty() : finishedSteps.size() < this.steps.size();
    }

    @Override
    protected Triggerable getNext(List<UserActivity> finishedSteps) {
      if (this.returnNull) {
        return null;
      }
      return this.steps.get(finishedSteps.size());
    }

    @Override
    protected void performInActivity(UserActivity userActivity) {
      throw new AssertionError("Use runIterate in tests");
    }
  }

  private static final class TrackingSingleThreadIteratingOperation extends SingleThreadIteratingOperation {
    private final List<Triggerable> steps;

    private TrackingSingleThreadIteratingOperation(List<Triggerable> steps) {
      super(TEST_GROUP, CroquetTestUtils.nextTestUUID());
      this.steps = steps;
    }

    @Override
    protected boolean hasNext(List<UserActivity> finishedSteps) {
      return finishedSteps.size() < this.steps.size();
    }

    @Override
    protected Triggerable getNext(List<UserActivity> finishedSteps) {
      return this.steps.get(finishedSteps.size());
    }
  }

  private static final class ProducedValueTriggerable implements Triggerable {
    private final Object value;
    private final boolean finish;
    private int fireCount;

    private ProducedValueTriggerable(Object value, boolean finish) {
      this.value = value;
      this.finish = finish;
    }

    @Override
    public void fire(UserActivity activity) {
      this.fireCount++;
      activity.setProducedValue(this.value);
      if (this.finish) {
        activity.finish();
      }
    }
  }

  private static final class PendingTriggerable implements Triggerable {
    @Override
    public void fire(UserActivity activity) {
    }
  }
}
