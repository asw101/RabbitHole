package org.lgna.croquet.triggers;

import org.lgna.croquet.Model;
import org.lgna.croquet.history.UserActivity;
import org.lgna.croquet.views.ViewController;
import org.junit.Test;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class ComponentEventTriggerTest {

  private static final class TestComponentEventTrigger extends ComponentEventTrigger<ComponentEvent> {
    private TestComponentEventTrigger(UserActivity userActivity, ViewController<?, ?> viewController, ComponentEvent event) {
      super(userActivity, viewController, event);
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
    JPanel panel = new JPanel();
    ComponentEvent event = new ComponentEvent(panel, ComponentEvent.COMPONENT_MOVED);
    TestComponentEventTrigger trigger = new TestComponentEventTrigger(null, null, event);

    assertSame(event, trigger.getEvent());
  }

  @Test
  public void constructorSetsTriggerOnActivity() {
    UserActivity activity = new UserActivity();
    ComponentEvent event = new ComponentEvent(new JPanel(), ComponentEvent.COMPONENT_RESIZED);
    TestComponentEventTrigger trigger = new TestComponentEventTrigger(activity, null, event);

    assertSame(trigger, activity.getTrigger());
  }

  @Test
  public void getComponent_returnsControllerComponentWhenPresent() throws Exception {
    TestViewController controller = createViewController();
    ComponentEvent event = new ComponentEvent(new JPanel(), ComponentEvent.COMPONENT_SHOWN);
    TestComponentEventTrigger trigger = new TestComponentEventTrigger(null, controller, event);

    assertSame(controller.getAwtComponent(), trigger.getComponent());
  }

  @Test
  public void getComponent_returnsEventComponentWhenControllerMissing() {
    JPanel panel = new JPanel();
    ComponentEvent event = new ComponentEvent(panel, ComponentEvent.COMPONENT_HIDDEN);
    TestComponentEventTrigger trigger = new TestComponentEventTrigger(null, null, event);

    assertSame(panel, trigger.getComponent());
  }

  @Test
  public void getViewController_returnsExplicitController() throws Exception {
    TestViewController controller = createViewController();
    ComponentEvent event = new ComponentEvent(new JPanel(), ComponentEvent.COMPONENT_MOVED);
    TestComponentEventTrigger trigger = new TestComponentEventTrigger(null, controller, event);

    assertSame(controller, trigger.getViewController());
  }

  @Test
  public void getViewController_looksUpControllerFromEventSource() throws Exception {
    TestViewController controller = createViewController();
    ComponentEvent event = new ComponentEvent(controller.getAwtComponent(), ComponentEvent.COMPONENT_RESIZED);
    TestComponentEventTrigger trigger = new TestComponentEventTrigger(null, null, event);

    assertSame(controller, trigger.getViewController());
  }

  @Test
  public void getPoint_inheritsNullDefault() {
    ComponentEvent event = new ComponentEvent(new JPanel(), ComponentEvent.COMPONENT_MOVED);
    TestComponentEventTrigger trigger = new TestComponentEventTrigger(null, null, event);

    assertNull(trigger.getPoint());
  }

  @Test
  public void hierarchy_extendsEventObjectTrigger() {
    assertSame(EventObjectTrigger.class, ComponentEventTrigger.class.getSuperclass());
  }
}
