package edu.cmu.cs.dennisc.javax.swing.components;

import org.junit.Test;

import javax.swing.BoundedRangeModel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JViewport;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class JSideBySideScrollPaneTest {
  @Test
  public void constructorCreatesExpectedChildComponentsAndDefaults() {
    JSideBySideScrollPane pane = new JSideBySideScrollPane();

    assertEquals(5, pane.getComponentCount());
    assertNotNull(pane.getLayout());
    assertEquals(12, pane.getHorizontalScrollBar().getUnitIncrement());
    assertEquals(24, pane.getVerticalScrollBar().getBlockIncrement());
    assertTrue(pane.getVerticalScrollBar().getBorder() instanceof EmptyBorder);
    assertEquals(Color.DARK_GRAY, component("divider", pane).getBackground());
  }

  @Test
  public void leadingAndTrailingViewsRoundTrip() {
    JPanel leading = view(300, 200);
    JPanel trailing = view(500, 150);
    JSideBySideScrollPane pane = new JSideBySideScrollPane(leading, trailing);

    assertSame(leading, pane.getLeadingView());
    assertSame(trailing, pane.getTrailingView());

    JPanel replacement = view(100, 80);
    pane.setTrailingView(replacement);
    assertSame(replacement, pane.getTrailingView());
  }

  @Test
  public void layoutPositionsViewportsDividerAndScrollBars() {
    JSideBySideScrollPane pane = new JSideBySideScrollPane(view(400, 300), view(200, 500));
    pane.setSize(240, 160);
    pane.doLayout();

    Rectangle leadingBounds = component("leadingViewport", pane).getBounds();
    Rectangle dividerBounds = component("divider", pane).getBounds();
    Rectangle trailingBounds = component("trailingViewport", pane).getBounds();
    Rectangle verticalBounds = pane.getVerticalScrollBar().getBounds();
    Rectangle horizontalBounds = pane.getHorizontalScrollBar().getBounds();

    assertTrue(leadingBounds.width > 0);
    assertEquals(leadingBounds.x + leadingBounds.width, dividerBounds.x);
    assertEquals(dividerBounds.x + dividerBounds.width, trailingBounds.x);
    assertEquals(trailingBounds.x + trailingBounds.width, verticalBounds.x);
    assertEquals(leadingBounds.height, dividerBounds.height);
    assertEquals(verticalBounds.y + verticalBounds.height, horizontalBounds.y);
  }

  @Test
  public void updateScrollBarsUsesViewportSizes() throws Exception {
    JSideBySideScrollPane pane = new JSideBySideScrollPane(view(400, 300), view(200, 500));
    pane.setSize(240, 160);
    pane.doLayout();

    method("updateScrollBars").invoke(pane);

    BoundedRangeModel horizontal = pane.getHorizontalScrollBar().getModel();
    BoundedRangeModel vertical = pane.getVerticalScrollBar().getModel();
    assertTrue(horizontal.getExtent() > 0);
    assertTrue(horizontal.getMaximum() >= horizontal.getExtent());
    assertTrue(vertical.getMaximum() >= vertical.getExtent());
  }

  @Test
  public void mouseWheelPrefersVerticalThenHorizontalScrolling() throws Exception {
    JSideBySideScrollPane pane = new JSideBySideScrollPane(view(400, 300), view(200, 500));
    pane.setSize(240, 160);
    pane.doLayout();
    method("updateScrollBars").invoke(pane);

    MouseWheelEvent forward = new MouseWheelEvent(pane, MouseWheelEvent.MOUSE_WHEEL, 0L, 0, 5, 5, 0, false,
        MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, 1);
    method("handleMouseWheelMoved", MouseWheelEvent.class).invoke(pane, forward);
    assertTrue(pane.getVerticalScrollBar().getValue() > 0 || pane.getHorizontalScrollBar().getValue() > 0);
  }

  @Test
  public void scrollbarAndViewportListenersSynchronizeViewPositions() throws Exception {
    JSideBySideScrollPane pane = new JSideBySideScrollPane(view(400, 300), view(400, 300));
    pane.setSize(240, 160);
    pane.doLayout();
    method("updateScrollBars").invoke(pane);

    pane.getHorizontalScrollBar().setValue(20);
    pane.getVerticalScrollBar().setValue(15);
    listener("horizontalScrollListener", pane).stateChanged(new javax.swing.event.ChangeEvent(pane.getHorizontalScrollBar().getModel()));
    listener("verticalScrollListener", pane).stateChanged(new javax.swing.event.ChangeEvent(pane.getVerticalScrollBar().getModel()));

    JViewport leadingViewport = (JViewport) component("leadingViewport", pane);
    JViewport trailingViewport = (JViewport) component("trailingViewport", pane);
    assertEquals(new Point(20, 15), leadingViewport.getViewPosition());
    assertEquals(new Point(20, 15), trailingViewport.getViewPosition());
  }

  @Test
  public void dividerDragAdjustsLeadingPortion() throws Exception {
    JSideBySideScrollPane pane = new JSideBySideScrollPane(view(400, 300), view(400, 300));
    pane.setSize(240, 160);
    pane.doLayout();
    Component divider = component("divider", pane);
    Method handleMouseDragged = divider.getClass().getDeclaredMethod("handleMouseDragged", java.awt.event.MouseEvent.class);
    handleMouseDragged.setAccessible(true);

    handleMouseDragged.invoke(divider, new java.awt.event.MouseEvent(divider, java.awt.event.MouseEvent.MOUSE_DRAGGED, 0L, 0, 150, 10, 1, false));
    pane.doLayout();

    assertTrue(component("leadingViewport", pane).getWidth() > component("trailingViewport", pane).getWidth());
  }

  @Test
  public void addNotifyAndRemoveNotifyRegisterAndUnregisterListenersWhenParented() {
    JPanel parent = new JPanel();
    JSideBySideScrollPane pane = new JSideBySideScrollPane(view(200, 100), view(200, 100));
    parent.add(pane);

    parent.addNotify();
    try {
      assertTrue(pane.getMouseWheelListeners().length > 0);
      Component divider = component("divider", pane);
      assertTrue(divider.getMouseListeners().length > 0);
      assertTrue(divider.getMouseMotionListeners().length > 0);
    } finally {
      parent.removeNotify();
    }

    assertEquals(0, pane.getMouseWheelListeners().length);
    assertEquals(0, component("divider", pane).getMouseListeners().length);
    assertEquals(0, component("divider", pane).getMouseMotionListeners().length);
  }

  @Test
  public void dividerMouseDragListenerUpdatesLeadingPortion() {
    JPanel parent = new JPanel();
    JSideBySideScrollPane pane = new JSideBySideScrollPane(view(400, 300), view(400, 300));
    parent.add(pane);
    pane.setSize(240, 160);
    pane.doLayout();

    parent.addNotify();
    try {
      Component divider = component("divider", pane);
      MouseMotionListener mouseMotionListener = divider.getMouseMotionListeners()[0];
      mouseMotionListener.mouseDragged(new java.awt.event.MouseEvent(divider, java.awt.event.MouseEvent.MOUSE_DRAGGED, 0L, 0, 180, 10, 180, 10, 1, false, java.awt.event.MouseEvent.NOBUTTON));
      pane.doLayout();

      assertTrue(component("leadingViewport", pane).getWidth() > component("trailingViewport", pane).getWidth());
    } finally {
      parent.removeNotify();
    }
  }

  @Test
  public void customScrollBarUsesPaneBackgroundAndCanPaint() throws Exception {
    JSideBySideScrollPane pane = new JSideBySideScrollPane();
    pane.setBackground(Color.ORANGE);
    JScrollBar scrollBar = pane.getHorizontalScrollBar();
    scrollBar.setSize(40, 12);

    assertEquals(Color.ORANGE, scrollBar.getBackground());
    BufferedImage image = new BufferedImage(40, 12, BufferedImage.TYPE_INT_ARGB);
    scrollBar.paint(image.getGraphics());
  }

  private static JPanel view(int width, int height) {
    JPanel panel = new JPanel();
    panel.setPreferredSize(new Dimension(width, height));
    panel.setSize(width, height);
    return panel;
  }

  private static Component component(String fieldName, JSideBySideScrollPane pane) {
    try {
      Field field = JSideBySideScrollPane.class.getDeclaredField(fieldName);
      field.setAccessible(true);
      return (Component) field.get(pane);
    } catch (ReflectiveOperationException e) {
      throw new AssertionError(e);
    }
  }

  private static ChangeListener listener(String fieldName, JSideBySideScrollPane pane) {
    try {
      Field field = JSideBySideScrollPane.class.getDeclaredField(fieldName);
      field.setAccessible(true);
      return (ChangeListener) field.get(pane);
    } catch (ReflectiveOperationException e) {
      throw new AssertionError(e);
    }
  }

  private static Method method(String name, Class<?>... parameterTypes) throws Exception {
    Method method = JSideBySideScrollPane.class.getDeclaredMethod(name, parameterTypes);
    method.setAccessible(true);
    return method;
  }
}
