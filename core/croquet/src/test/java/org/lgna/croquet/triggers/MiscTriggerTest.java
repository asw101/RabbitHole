package org.lgna.croquet.triggers;

import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.lgna.croquet.DropReceptor;
import org.lgna.croquet.DropSite;
import org.lgna.croquet.Model;
import org.lgna.croquet.history.UserActivity;
import org.lgna.croquet.views.PopupMenu;
import org.lgna.croquet.views.ViewController;
import org.junit.Test;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.PopupMenuEvent;
import java.awt.Frame;
import java.awt.Point;
import java.awt.desktop.AboutEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class MiscTriggerTest {

  private static final sun.misc.Unsafe UNSAFE = getUnsafe();

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

  private static final class TestTrigger extends Trigger {
    private final ViewController<?, ?> viewController;
    private final AtomicInteger popupCalls = new AtomicInteger();

    private TestTrigger(UserActivity activity, ViewController<?, ?> viewController) {
      super(activity);
      this.viewController = viewController;
    }

    @Override
    public ViewController<?, ?> getViewController() {
      return this.viewController;
    }

    @Override
    public void showPopupMenu(PopupMenu popupMenu) {
      this.popupCalls.incrementAndGet();
    }
  }

  private static final class TestDropSite implements DropSite {
    private final String label;

    private TestDropSite(String label) {
      this.label = label;
    }

    @Override
    public void encode(BinaryEncoder binaryEncoder) {
      binaryEncoder.encode(this.label);
    }

    @Override
    public DropReceptor getOwningDropReceptor() {
      return null;
    }

    @Override
    public String toString() {
      return this.label;
    }
  }

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

  private static Frame allocateFrame() throws Exception {
    return (Frame) UNSAFE.allocateInstance(Frame.class);
  }

  private static PopupMenuEventTrigger allocatePopupMenuEventTrigger(PopupMenuEvent event) throws Exception {
    PopupMenuEventTrigger trigger = (PopupMenuEventTrigger) UNSAFE.allocateInstance(PopupMenuEventTrigger.class);
    setObjectField(trigger, EventObjectTrigger.class, "event", event);
    setObjectField(trigger, EventObjectTrigger.class, "viewController", null);
    setObjectField(trigger, Trigger.class, "userActivity", null);
    return trigger;
  }

  private static PropertyChangeEventTrigger allocatePropertyChangeEventTrigger(PropertyChangeEvent event, ViewController<?, ?> controller) throws Exception {
    PropertyChangeEventTrigger trigger = (PropertyChangeEventTrigger) UNSAFE.allocateInstance(PropertyChangeEventTrigger.class);
    setObjectField(trigger, EventObjectTrigger.class, "event", event);
    setObjectField(trigger, EventObjectTrigger.class, "viewController", controller);
    setObjectField(trigger, Trigger.class, "userActivity", null);
    return trigger;
  }

  private static void assertPrivateConstructor(Class<?> type, Class<?>... parameterTypes) throws Exception {
    Constructor<?> constructor = type.getDeclaredConstructor(parameterTypes);
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
  }

  @Test
  public void changeEventTrigger_createUserInstance_setsEventAndActivity() {
    UserActivity activity = new UserActivity();
    ChangeEvent event = new ChangeEvent("source");
    ChangeEventTrigger trigger = ChangeEventTrigger.createUserInstance(activity, event);

    assertSame(activity, trigger.getUserActivity());
    assertSame(event, trigger.getEvent());
    assertSame(trigger, activity.getTrigger());
  }

  @Test
  public void propertyChangeEventTrigger_allocatedInstance_returnsInjectedEventAndController() throws Exception {
    TestViewController controller = createViewController();
    PropertyChangeEvent event = new PropertyChangeEvent("source", "name", "before", "after");
    PropertyChangeEventTrigger trigger = allocatePropertyChangeEventTrigger(event, controller);

    assertSame(event, trigger.getEvent());
    assertSame(controller, trigger.getViewController());
  }

  @Test
  public void popupMenuEventTrigger_allocatedInstance_returnsInjectedEvent() throws Exception {
    PopupMenuEvent event = new PopupMenuEvent(new JPopupMenu());
    PopupMenuEventTrigger trigger = allocatePopupMenuEventTrigger(event);

    assertSame(event, trigger.getEvent());
  }

  @Test
  public void windowEventTrigger_setOnUserActivity_setsTriggerAndEvent() throws Exception {
    UserActivity activity = new UserActivity();
    WindowEvent event = new WindowEvent(allocateFrame(), WindowEvent.WINDOW_OPENED);

    WindowEventTrigger.setOnUserActivity(activity, event);

    assertTrue(activity.getTrigger() instanceof WindowEventTrigger);
    assertSame(event, ((WindowEventTrigger) activity.getTrigger()).getEvent());
  }

  @Test
  public void mouseEventTrigger_privateUserActivityConstructor_preservesPoint() throws Exception {
    UserActivity activity = new UserActivity();
    TestViewController controller = createViewController();
    MouseEvent event = new MouseEvent(controller.getAwtComponent(), MouseEvent.MOUSE_DRAGGED, 1L, 0, 8, 11, 0, false);
    Constructor<MouseEventTrigger> constructor = MouseEventTrigger.class.getDeclaredConstructor(UserActivity.class, ViewController.class, MouseEvent.class);
    constructor.setAccessible(true);
    MouseEventTrigger trigger = constructor.newInstance(activity, controller, event);

    assertEquals(new Point(8, 11), trigger.getPoint());
    assertSame(trigger, activity.getTrigger());
  }

  @Test
  public void dragTrigger_privateConstructor_preservesControllerAndPoint() throws Exception {
    TestViewController controller = createViewController();
    MouseEvent event = new MouseEvent(controller.getAwtComponent(), MouseEvent.MOUSE_PRESSED, 2L, 0, 4, 6, 1, false);
    Constructor<DragTrigger> constructor = DragTrigger.class.getDeclaredConstructor(ViewController.class, MouseEvent.class);
    constructor.setAccessible(true);
    DragTrigger trigger = constructor.newInstance(controller, event);

    assertSame(controller, trigger.getViewController());
    assertEquals(new Point(4, 6), trigger.getPoint());
  }

  @Test
  public void dropTrigger_privateConstructor_preservesDropSiteAndPoint() throws Exception {
    UserActivity activity = new UserActivity();
    TestViewController controller = createViewController();
    TestDropSite dropSite = new TestDropSite("slot-A");
    MouseEvent event = new MouseEvent(controller.getAwtComponent(), MouseEvent.MOUSE_RELEASED, 3L, 0, 10, 20, 1, false);
    Constructor<DropTrigger> constructor = DropTrigger.class.getDeclaredConstructor(UserActivity.class, ViewController.class, MouseEvent.class, org.lgna.croquet.DropSite.class);
    constructor.setAccessible(true);
    DropTrigger trigger = constructor.newInstance(activity, controller, event, dropSite);

    assertSame(dropSite, trigger.getDropSite());
    assertEquals(new Point(10, 20), trigger.getPoint());
  }

  @Test
  public void dropTrigger_appendRepr_includesDropSite() throws Exception {
    UserActivity activity = new UserActivity();
    TestDropSite dropSite = new TestDropSite("slot-B");
    MouseEvent event = new MouseEvent(new JPanel(), MouseEvent.MOUSE_RELEASED, 4L, 0, 1, 2, 1, false);
    Constructor<DropTrigger> constructor = DropTrigger.class.getDeclaredConstructor(UserActivity.class, ViewController.class, MouseEvent.class, org.lgna.croquet.DropSite.class);
    constructor.setAccessible(true);
    DropTrigger trigger = constructor.newInstance(activity, null, event, dropSite);
    StringBuilder repr = new StringBuilder();

    trigger.appendRepr(repr);

    assertTrue(repr.toString().contains("dropSite=slot-B"));
  }

  @Test
  public void dropTrigger_encode_writesDropSiteData() throws Exception {
    UserActivity activity = new UserActivity();
    TestDropSite dropSite = new TestDropSite("slot-C");
    MouseEvent event = new MouseEvent(new JPanel(), MouseEvent.MOUSE_RELEASED, 5L, 0, 3, 7, 1, false);
    Constructor<DropTrigger> constructor = DropTrigger.class.getDeclaredConstructor(UserActivity.class, ViewController.class, MouseEvent.class, org.lgna.croquet.DropSite.class);
    constructor.setAccessible(true);
    DropTrigger trigger = constructor.newInstance(activity, null, event, dropSite);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    BinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);

    trigger.encode(encoder);
    encoder.flush();

    assertTrue(baos.toByteArray().length > 0);
  }

  @Test
  public void cascadeAutomaticDeterminationTrigger_createChildActivity_setsDelegatingTrigger() throws Exception {
    UserActivity parent = new UserActivity();
    TestViewController controller = createViewController();
    new TestTrigger(parent, controller);

    UserActivity child = CascadeAutomaticDeterminationTrigger.createChildActivity(parent);

    assertTrue(child.getTrigger() instanceof CascadeAutomaticDeterminationTrigger);
    assertSame(controller, child.getTrigger().getViewController());
  }

  @Test
  public void cascadeAutomaticDeterminationTrigger_showPopupMenu_delegatesToPreviousTrigger() {
    UserActivity parent = new UserActivity();
    TestTrigger previousTrigger = new TestTrigger(parent, null);
    UserActivity child = CascadeAutomaticDeterminationTrigger.createChildActivity(parent);

    child.getTrigger().showPopupMenu(null);

    assertEquals(1, previousTrigger.popupCalls.get());
  }

  @Test
  public void iterationTrigger_createUserInstance_setsTriggerOnActivity() {
    UserActivity activity = new UserActivity();

    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);

    assertSame(activity, trigger.getUserActivity());
    assertSame(trigger, activity.getTrigger());
  }

  @Test
  public void appleApplicationEventTrigger_privateConstructor_setsEventAndActivity() throws Exception {
    UserActivity activity = new UserActivity();
    AboutEvent event = (AboutEvent) UNSAFE.allocateInstance(AboutEvent.class);
    Constructor<AppleApplicationEventTrigger> constructor = AppleApplicationEventTrigger.class.getDeclaredConstructor(UserActivity.class, java.awt.desktop.AppEvent.class);
    constructor.setAccessible(true);
    AppleApplicationEventTrigger trigger = constructor.newInstance(activity, event);

    assertSame(activity, trigger.getUserActivity());
    assertSame(event, trigger.getEvent());
  }

  @Test
  public void signatureOnlyTriggers_declareExpectedPrivateConstructors() throws Exception {
    assertPrivateConstructor(ActionEventTrigger.class, ActionEvent.class);
    assertPrivateConstructor(ItemEventTrigger.class, ItemEvent.class);
    assertPrivateConstructor(KeyEventTrigger.class, ViewController.class, KeyEvent.class);
    assertPrivateConstructor(TreeSelectionEventTrigger.class, ViewController.class, javax.swing.event.TreeSelectionEvent.class);
    assertPrivateConstructor(NullTrigger.class);
  }

  @Test
  public void hierarchyOnlyTriggers_extendExpectedBaseTypes() {
    assertSame(EventObjectTrigger.class, ActionEventTrigger.class.getSuperclass());
    assertSame(EventObjectTrigger.class, ItemEventTrigger.class.getSuperclass());
    assertSame(ComponentEventTrigger.class, KeyEventTrigger.class.getSuperclass());
    assertSame(EventObjectTrigger.class, TreeSelectionEventTrigger.class.getSuperclass());
    assertSame(Trigger.class, NullTrigger.class.getSuperclass());
  }
}
