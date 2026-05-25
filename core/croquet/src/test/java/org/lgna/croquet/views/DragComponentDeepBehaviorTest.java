package org.lgna.croquet.views;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.AbstractModel;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.DragModel;
import org.lgna.croquet.DropReceptor;
import org.lgna.croquet.DropSite;
import org.lgna.croquet.Triggerable;
import org.lgna.croquet.history.DragStep;
import org.lgna.croquet.views.HeadlessWindowBehaviorTest.FakeAbstractWindow;
import org.lgna.croquet.views.HeadlessWindowBehaviorTest.HeadlessLayeredPane;
import org.lgna.croquet.views.HeadlessWindowBehaviorTest.HeadlessWindow;
import org.lgna.croquet.views.imp.JDragView;

import javax.swing.SwingUtilities;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class DragComponentDeepBehaviorTest {
  private FakeAbstractWindow root;
  private TrackingDragModel model;
  private TrackingDragComponent component;
  private HeadlessLayeredPane layeredPane;

  @Before
  public void setUp() throws Exception {
    CroquetTestUtils.ensureTestApplication();
    HeadlessWindow window = HeadlessWindowBehaviorTest.newHeadlessWindow();
    root = new FakeAbstractWindow(window);
    model = new TrackingDragModel();
    component = new TrackingDragComponent(model, root);
    layeredPane = window.layeredPane;
    layeredPane.screenLocation = new Point(0, 0);
    component.view.screenLocation = new Point(20, 30);
    component.getAwtComponent().setLocation(20, 30);
    component.getAwtComponent().setSize(60, 40);
  }

  @Test
  public void clickAndMouseNavigationHooksRunWithoutShowingUi() {
    invokeProtected("handleMouseQuoteEnteredUnquote");
    assertTrue(component.isActive());

    invokeProtected("handleMouseQuoteExitedUnquote");
    assertFalse(component.isActive());

    invokePrivate("handleMousePressed", mouse(MouseEvent.MOUSE_PRESSED, 5, 5, MouseEvent.BUTTON1));
    invokePrivate("handleMouseReleased", mouse(MouseEvent.MOUSE_RELEASED, 5, 5, MouseEvent.BUTTON1));
    invokeProtected("handleBackButtonClicked", mouse(MouseEvent.MOUSE_CLICKED, 5, 5, MouseEvent.BUTTON1));
    invokeProtected("handleForwardButtonClicked", mouse(MouseEvent.MOUSE_CLICKED, 5, 5, MouseEvent.BUTTON1));

    assertEquals(1, component.leftClickCount);
    assertEquals(1, component.backCount);
    assertEquals(1, component.forwardCount);
  }

  @Test
  public void dragLifecycleAddsDragProxyAndCompletesDragStep() {
    invokePrivate("handleMousePressed", mouse(MouseEvent.MOUSE_PRESSED, 5, 5, MouseEvent.BUTTON1));
    invokePrivate("handleLeftMouseButtonDraggedOutsideOfClickThreshold", mouse(MouseEvent.MOUSE_DRAGGED, 30, 25, MouseEvent.BUTTON1));

    assertSame(layeredPane, component.getDragProxy().getParent());
    assertEquals(1, model.dragStartedCount);
    assertNotNull(getField("dragStep"));

    component.handleCancel(mouse(MouseEvent.MOUSE_RELEASED, 30, 25, MouseEvent.BUTTON1));

    assertNull(component.getDragProxy().getParent());
    assertNull(getField("dragStep"));
    assertEquals(1, model.stoppedCount);
  }

  @Test
  public void dropProxyLocationAndVisibilityFollowLayeredPaneCoordinates() {
    Point p = new Point(10, 12);
    component.setDropProxyLocationAndShowIfNecessary(p, component, 30, 16);

    assertSame(layeredPane, component.getDropProxy().getParent());
    assertEquals(new Point(30, 49), component.getDropProxy().getLocation());

    component.hideDropProxyIfNecessary();
    assertNull(component.getDropProxy().getParent());
  }

  @Test
  public void clickThresholdAndProxyVisibilityAreMutable() {
    component.setClickThreshold(9.5f);
    component.showDragProxy();
    component.hideDragProxy();

    assertEquals(9.5f, component.getClickThreshold(), 0.0001f);
    assertFalse(component.getDragProxy().isVisible());
    assertEquals(component.getDropProxy().getSize(), component.getDropProxySize());
    assertSame(component, component.getSubject());
  }

  @Test
  public void displayableLifecycle_registersListeners_and_componentResize_updatesProxySizes() {
    component.handleDisplayable();

    assertEquals(1, component.getAwtComponent().getMouseListeners().length);
    assertEquals(1, component.getAwtComponent().getMouseMotionListeners().length);
    assertEquals(1, component.getAwtComponent().getComponentListeners().length);

    component.getAwtComponent().setSize(80, 50);
    component.getAwtComponent().getComponentListeners()[0].componentResized(new java.awt.event.ComponentEvent(component.getAwtComponent(), java.awt.event.ComponentEvent.COMPONENT_RESIZED));

    assertEquals(component.getDragProxy().getProxySize(), component.getDragProxy().getSize());
    assertEquals(component.getDropProxy().getProxySize(), component.getDropProxy().getSize());

    component.handleUndisplayable();
    assertEquals(0, component.getAwtComponent().getMouseListeners().length);
    assertEquals(0, component.getAwtComponent().getMouseMotionListeners().length);
    assertEquals(0, component.getAwtComponent().getComponentListeners().length);
  }

  @Test
  public void installedListeners_drive_hover_click_drag_and_cancel_paths() {
    component.handleDisplayable();
    MouseEvent enter = mouse(MouseEvent.MOUSE_ENTERED, 3, 3, MouseEvent.NOBUTTON);
    MouseEvent press = mouse(MouseEvent.MOUSE_PRESSED, 5, 5, MouseEvent.BUTTON1);
    MouseEvent drag = mouse(MouseEvent.MOUSE_DRAGGED, 25, 18, MouseEvent.BUTTON1);
    MouseEvent exit = mouse(MouseEvent.MOUSE_EXITED, 25, 18, MouseEvent.NOBUTTON);

    for (MouseListener listener : component.getAwtComponent().getMouseListeners()) {
      listener.mouseEntered(enter);
      listener.mousePressed(press);
    }
    for (MouseMotionListener listener : component.getAwtComponent().getMouseMotionListeners()) {
      listener.mouseDragged(drag);
    }
    for (MouseListener listener : component.getAwtComponent().getMouseListeners()) {
      listener.mouseExited(exit);
    }

    assertNotNull(getField("mousePressedEvent"));

    component.handleCancel(drag);

    assertNull(component.getDragProxy().getParent());
    component.handleUndisplayable();
  }

  private MouseEvent mouse(int id, int x, int y, int button) {
    return new MouseEvent(component.getAwtComponent(), id, System.currentTimeMillis(), 0, x, y, 1, false, button);
  }

  private void invokePrivate(String name, Object... args) {
    try {
      Class<?>[] types = new Class<?>[args.length];
      for (int i = 0; i < args.length; i++) {
        types[i] = args[i].getClass();
      }
      Method method = DragComponent.class.getDeclaredMethod(name, types);
      method.setAccessible(true);
      method.invoke(component, args);
    } catch (ReflectiveOperationException e) {
      throw new AssertionError(e);
    }
  }

  private void invokeProtected(String name, Object... args) {
    try {
      Class<?>[] types = new Class<?>[args.length];
      for (int i = 0; i < args.length; i++) {
        types[i] = args[i].getClass();
      }
      Method method = DragComponent.class.getDeclaredMethod(name, types);
      method.setAccessible(true);
      method.invoke(component, args);
    } catch (ReflectiveOperationException e) {
      throw new AssertionError(e);
    }
  }

  private Object getField(String name) {
    try {
      Field field = DragComponent.class.getDeclaredField(name);
      field.setAccessible(true);
      return field.get(component);
    } catch (ReflectiveOperationException e) {
      throw new AssertionError(e);
    }
  }

  private static final class TrackingDragModel extends AbstractModel implements DragModel {
    int dragStartedCount;
    int stoppedCount;

    private TrackingDragModel() {
      super(CroquetTestUtils.nextTestUUID());
    }

    @Override protected void localize() {
    }

    @Override public List<? extends DropReceptor> createListOfPotentialDropReceptors() {
      return Collections.emptyList();
    }

    @Override public void handleDragStarted(DragStep step) {
      this.dragStartedCount++;
    }

    @Override public void handleDragEnteredDropReceptor(DragStep step) {
    }

    @Override public void handleDragExitedDropReceptor(DragStep step) {
    }

    @Override public void handleDragStopped(DragStep step) {
      this.stoppedCount++;
    }

    @Override public Triggerable getDropOperation(DragStep step, DropSite dropSite) {
      return null;
    }
  }

  private static final class TrackingDragComponent extends DragComponent<TrackingDragModel> {
    private final FakeAbstractWindow root;
    private final HeadlessDragView view;
    int leftClickCount;
    int backCount;
    int forwardCount;

    private TrackingDragComponent(TrackingDragModel model, FakeAbstractWindow root) {
      super(model, false);
      this.root = root;
      this.view = (HeadlessDragView) super.getAwtComponent();
    }

    @Override
    public AbstractWindow<?> getRoot() {
      return this.root;
    }

    @Override
    protected HeadlessDragView createAwtComponent() {
      return new HeadlessDragView();
    }

    @Override
    protected void fillBounds(Graphics2D g2, int x, int y, int width, int height) {
      g2.fillRect(x, y, width, height);
    }

    @Override
    protected void paintPrologue(Graphics2D g2, int x, int y, int width, int height) {
    }

    @Override
    protected void paintEpilogue(Graphics2D g2, int x, int y, int width, int height) {
    }

    @Override
    protected void handleLeftMouseButtonQuoteClickedUnquote(MouseEvent e) {
      this.leftClickCount++;
    }

    @Override
    protected void handleBackButtonClicked(MouseEvent e) {
      this.backCount++;
    }

    @Override
    protected void handleForwardButtonClicked(MouseEvent e) {
      this.forwardCount++;
    }
  }

  private static final class HeadlessDragView extends JDragView {
    Point screenLocation = new Point();

    @Override
    public Point getLocationOnScreen() {
      return new Point(this.screenLocation);
    }
  }
}
