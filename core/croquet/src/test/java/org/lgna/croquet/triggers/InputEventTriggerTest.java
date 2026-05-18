package org.lgna.croquet.triggers;

import org.lgna.croquet.Model;
import org.lgna.croquet.views.ViewController;
import org.junit.Test;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class InputEventTriggerTest {

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

  private static final sun.misc.Unsafe UNSAFE = getUnsafe();

  private static sun.misc.Unsafe getUnsafe() {
    try {
      Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      return (sun.misc.Unsafe) field.get(null);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static void setObjectField(Object target, Class<?> ownerClass, String name, Object value) throws Exception {
    Field field = ownerClass.getDeclaredField(name);
    field.setAccessible(true);
    UNSAFE.putObject(target, UNSAFE.objectFieldOffset(field), value);
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

  private static InputEventTrigger allocateTrigger(InputEvent event) throws Exception {
    return allocateTrigger(event, null);
  }

  private static InputEventTrigger allocateTrigger(InputEvent event, ViewController<?, ?> viewController) throws Exception {
    InputEventTrigger trigger = (InputEventTrigger) UNSAFE.allocateInstance(InputEventTrigger.class);
    setObjectField(trigger, EventObjectTrigger.class, "event", event);
    setObjectField(trigger, EventObjectTrigger.class, "viewController", viewController);
    setObjectField(trigger, Trigger.class, "userActivity", null);
    return trigger;
  }

  @Test
  public void getPoint_withMouseEvent_returnsMouseLocation() throws Exception {
    JPanel panel = new JPanel();
    MouseEvent mouseEvent = new MouseEvent(panel, MouseEvent.MOUSE_PRESSED, 1L, 0, 12, 34, 1, false);
    InputEventTrigger trigger = allocateTrigger(mouseEvent);

    assertEquals(new Point(12, 34), trigger.getPoint());
  }

  @Test
  public void getPoint_withKeyEvent_returnsNull() throws Exception {
    JPanel panel = new JPanel();
    KeyEvent keyEvent = new KeyEvent(panel, KeyEvent.KEY_PRESSED, 1L, 0, KeyEvent.VK_A, 'a');
    InputEventTrigger trigger = allocateTrigger(keyEvent);

    assertNull(trigger.getPoint());
  }

  @Test
  public void getEvent_returnsInjectedMouseEvent() throws Exception {
    MouseEvent mouseEvent = new MouseEvent(new JPanel(), MouseEvent.MOUSE_RELEASED, 2L, 0, 3, 4, 1, false);
    InputEventTrigger trigger = allocateTrigger(mouseEvent);

    assertSame(mouseEvent, trigger.getEvent());
  }

  @Test
  public void getComponent_withMouseEvent_returnsEventComponent() throws Exception {
    JPanel panel = new JPanel();
    MouseEvent mouseEvent = new MouseEvent(panel, MouseEvent.MOUSE_MOVED, 3L, 0, 5, 6, 0, false);
    InputEventTrigger trigger = allocateTrigger(mouseEvent);

    assertSame(panel, trigger.getComponent());
  }

  @Test
  public void getComponent_withKeyEvent_returnsEventComponent() throws Exception {
    JPanel panel = new JPanel();
    KeyEvent keyEvent = new KeyEvent(panel, KeyEvent.KEY_TYPED, 4L, 0, KeyEvent.VK_UNDEFINED, 'b');
    InputEventTrigger trigger = allocateTrigger(keyEvent);

    assertSame(panel, trigger.getComponent());
  }

  @Test
  public void getViewController_withInjectedController_returnsController() throws Exception {
    TestViewController controller = createViewController();
    InputEventTrigger trigger = allocateTrigger(new MouseEvent(controller.getAwtComponent(), MouseEvent.MOUSE_CLICKED, 5L, 0, 1, 2, 1, false), controller);

    assertSame(controller, trigger.getViewController());
  }

  @Test
  public void hierarchy_extendsComponentEventTrigger() {
    assertSame(ComponentEventTrigger.class, InputEventTrigger.class.getSuperclass());
  }

  @Test
  public void privateConstructor_acceptsSingleInputEvent() throws Exception {
    Constructor<InputEventTrigger> constructor = InputEventTrigger.class.getDeclaredConstructor(InputEvent.class);

    assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
  }

  @Test
  public void createUserActivity_declaresUserActivityReturnType() throws Exception {
    Method method = InputEventTrigger.class.getDeclaredMethod("createUserActivity", InputEvent.class);

    assertEquals(org.lgna.croquet.history.UserActivity.class, method.getReturnType());
  }
}
