package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.edits.Edit;
import org.lgna.croquet.history.UserActivity;
import org.lgna.croquet.views.CompositeView;

import java.util.UUID;

import static org.junit.Assert.*;

public class InternalActionOperationRuntimeTest {
  private TestComposite composite;

  @Before
  public void setUp() {
    composite = new TestComposite();
  }

  @Test
  public void actionReturningNull_finishesActivity() {
    ActionOperation operation = composite.createAction((userActivity, source) -> null);
    UserActivity activity = new UserActivity();

    operation.fire(activity);

    assertTrue(activity.isSuccessfullyCompleted());
    assertNull(activity.getEdit());
    assertSame(operation, activity.getCompletionModel());
  }

  @Test
  public void actionReturningEdit_commitsAndInvokesEdit() {
    TrackingEdit edit = new TrackingEdit();
    ActionOperation operation = composite.createAction((userActivity, source) -> edit);
    UserActivity activity = new UserActivity();

    operation.fire(activity);

    assertSame(edit, activity.getEdit());
    assertEquals(1, edit.doOrRedoCount);
    assertTrue(activity.isSuccessfullyCompleted());
  }

  @Test
  public void actionThrowingCancelException_cancelsActivity() {
    ActionOperation operation = composite.createAction((userActivity, source) -> {
      throw new CancelException();
    });
    UserActivity activity = new UserActivity();

    operation.fire(activity);

    assertTrue(activity.isCanceled());
    assertNull(activity.getEdit());
  }

  private static final class TestComposite extends AbstractComposite<CompositeViewLifecycleTest.StubView> {
    private TestComposite() {
      super(UUID.fromString("00000000-0000-0000-1400-000000000001"));
    }

    @Override
    protected org.lgna.croquet.views.ScrollPane createScrollPaneIfDesired() {
      return null;
    }

    @Override
    protected CompositeViewLifecycleTest.StubView createView() {
      return new CompositeViewLifecycleTest.StubView();
    }

    private ActionOperation createAction(AbstractComposite.Action action) {
      return this.createActionOperation("runtimeAction", action);
    }
  }

  private static final class TrackingEdit implements Edit {
    private int doOrRedoCount;

    @Override
    public Group getGroup() {
      return Application.PROJECT_GROUP;
    }

    @Override
    public boolean canUndo() {
      return true;
    }

    @Override
    public boolean canRedo() {
      return true;
    }

    @Override
    public void doOrRedo(boolean isDo) {
      this.doOrRedoCount++;
    }

    @Override
    public void undo() {
    }

    @Override
    public String getRedoPresentation() {
      return "redo";
    }

    @Override
    public String getUndoPresentation() {
      return "undo";
    }

    @Override
    public String getTerseDescription() {
      return "terse";
    }

    @Override
    public String getDetailedDescription() {
      return "detailed";
    }

    @Override
    public String getLogDescription() {
      return "log";
    }
  }
}
