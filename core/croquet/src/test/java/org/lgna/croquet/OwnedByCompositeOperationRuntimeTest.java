package org.lgna.croquet;

import org.junit.Test;
import org.lgna.croquet.history.UserActivity;
import org.lgna.croquet.views.Panel;

import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class OwnedByCompositeOperationRuntimeTest {
  @Test
  public void fire_runsInitializerAndCompositePerform() {
    TestComposite composite = new TestComposite();
    AtomicBoolean initializerCalled = new AtomicBoolean();
    OwnedByCompositeOperation<TestComposite> operation = new OwnedByCompositeOperation<>(
        Application.PROJECT_GROUP,
        composite,
        new OwnedByCompositeOperationSubKey(composite, "launch"),
        c -> initializerCalled.set(true));
    UserActivity activity = new UserActivity();

    operation.fire(activity);

    assertTrue(initializerCalled.get());
    assertTrue(composite.localized);
    assertEquals(1, composite.performCount);
    assertTrue(activity.isSuccessfullyCompleted());
    assertSame(operation, activity.getCompletionModel());
  }

  @Test
  public void accessors_delegateToCompositeAndSubKey() {
    TestComposite composite = new TestComposite();
    OwnedByCompositeOperation<TestComposite> operation = new OwnedByCompositeOperation<>(
        Application.PROJECT_GROUP,
        composite,
        new OwnedByCompositeOperationSubKey(composite, "launch"),
        null);

    assertSame(composite, operation.getComposite());
    assertEquals("launch", operation.getSubKeyForLocalization());
    assertEquals(TestComposite.class, operation.getClassUsedForLocalization());
    assertEquals("decorated-name", operation.modifyNameIfNecessary("name"));
  }

  private static final class TestComposite extends AbstractComposite<Panel> implements OperationOwningComposite<Panel> {
    private boolean localized;
    private int performCount;

    private TestComposite() {
      super(UUID.fromString("00000000-0000-0000-1500-000000000001"));
    }

    @Override
    protected void localize() {
      this.localized = true;
    }

    @Override
    protected org.lgna.croquet.views.ScrollPane createScrollPaneIfDesired() {
      return null;
    }

    @Override
    protected Panel createView() {
      return new Panel(this) {
        @Override
        protected LayoutManager createLayoutManager(JPanel jPanel) {
          return new FlowLayout();
        }
      };
    }

    @Override
    public void perform(UserActivity userActivity) {
      this.performCount++;
      userActivity.finish();
    }

    @Override
    public String modifyNameIfNecessary(String text) {
      return "decorated-" + text;
    }
  }
}
