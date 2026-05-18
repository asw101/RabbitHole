package org.lgna.croquet.triggers;

import org.lgna.croquet.Model;
import org.lgna.croquet.history.UserActivity;
import org.lgna.croquet.views.ViewController;
import org.junit.Test;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Point;
import java.util.EventObject;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class EventObjectTriggerTest {

  private static final class TestEventObjectTrigger extends EventObjectTrigger<EventObject> {
    private final Point point;

    private TestEventObjectTrigger(UserActivity userActivity, ViewController<?, ?> viewController, EventObject event, Point point) {
      super(userActivity, viewController, event);
      this.point = point;
    }

    @Override
    protected Point getPoint() {
      return this.point;
    }
  }

  private static final class TestViewController extends ViewController<JPanel, Model> {
    private final JPanel panel;

    private TestViewController(JPanel panel) {
      super(null);
      this.panel = panel;
    }

    @Override
    protected JPanel createAwtComponent() {
      return this.panel;
    }
  }

  private static TestViewController createViewController() throws Exception {
    AtomicReference<TestViewController> ref = new AtomicReference<>();
    SwingUtilities.invokeAndWait(() -> {
      TestViewController controller = new TestViewController(new JPanel());
      controller.getAwtComponent();
      ref.set(controller);
    });
    return ref.get();
  }

  @Test
  public void constructorStoresEvent() {
    EventObject event = new EventObject("source");
    TestEventObjectTrigger trigger = new TestEventObjectTrigger(null, null, event, null);

    assertSame(event, trigger.getEvent());
  }

  @Test
  public void constructorStoresUserActivityAndSetsTrigger() {
    UserActivity activity = new UserActivity();
    TestEventObjectTrigger trigger = new TestEventObjectTrigger(activity, null, new EventObject("source"), null);

    assertSame(activity, trigger.getUserActivity());
    assertSame(trigger, activity.getTrigger());
  }

  @Test
  public void getViewController_returnsExplicitController() throws Exception {
    TestViewController controller = createViewController();
    TestEventObjectTrigger trigger = new TestEventObjectTrigger(null, controller, new EventObject("source"), null);

    assertSame(controller, trigger.getViewController());
  }

  @Test
  public void getViewController_looksUpControllerFromEventSource() throws Exception {
    TestViewController controller = createViewController();
    EventObject event = new EventObject(controller.getAwtComponent());
    TestEventObjectTrigger trigger = new TestEventObjectTrigger(null, null, event, null);

    assertSame(controller, trigger.getViewController());
  }

  @Test
  public void getViewController_returnsNullForNonComponentSource() {
    TestEventObjectTrigger trigger = new TestEventObjectTrigger(null, null, new EventObject("source"), null);

    assertNull(trigger.getViewController());
  }

  @Test
  public void getComponent_returnsControllerComponentWhenPresent() throws Exception {
    TestViewController controller = createViewController();
    JPanel eventSource = new JPanel();
    TestEventObjectTrigger trigger = new TestEventObjectTrigger(null, controller, new EventObject(eventSource), null);

    assertSame(controller.getAwtComponent(), trigger.getComponent());
  }

  @Test
  public void getComponent_returnsEventSourceComponentWhenControllerMissing() {
    JPanel panel = new JPanel();
    TestEventObjectTrigger trigger = new TestEventObjectTrigger(null, null, new EventObject(panel), null);

    assertSame(panel, trigger.getComponent());
  }

  @Test
  public void getComponent_returnsNullForNonComponentSource() {
    TestEventObjectTrigger trigger = new TestEventObjectTrigger(null, null, new EventObject("source"), null);

    assertNull(trigger.getComponent());
  }

  @Test
  public void getPoint_returnsSubclassProvidedPoint() {
    Point point = new Point(7, 9);
    TestEventObjectTrigger trigger = new TestEventObjectTrigger(null, null, new EventObject(new JPanel()), point);

    assertEquals(point, trigger.getPoint());
  }
}
