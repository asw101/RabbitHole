package org.lgna.croquet.views;

import org.junit.Test;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Window;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class HeadlessWindowBehaviorTest {
  private static final sun.misc.Unsafe UNSAFE = getUnsafe();

  @Test
  public void lookupAndBasicStateRoundTripThroughAbstractWindow() throws Exception {
    FakeAbstractWindow window = new FakeAbstractWindow(newHeadlessWindow());

    window.setVisible(true);
    window.setLocation(5, 7);
    window.setSize(80, 40);
    window.setLocationByPlatform(true);

    assertSame(window, AbstractWindow.lookup(window.getAwtComponent()));
    assertEquals(5, window.getX());
    assertEquals(7, window.getY());
    assertEquals(80, window.getWidth());
    assertEquals(40, window.getHeight());
    assertTrue(window.isVisible());
    assertTrue(window.isLocationByPlatform());
    assertSame(window, window.getRoot());

    window.release();
    assertNull(AbstractWindow.lookup(window.getAwtComponent()));
  }

  @Test
  public void relativeBoundsAndTrackableShapeUseWindowGeometry() throws Exception {
    FakeAbstractWindow seenBy = new FakeAbstractWindow(newHeadlessWindow());
    FakeAbstractWindow target = new FakeAbstractWindow(newHeadlessWindow());
    seenBy.setVisible(true);
    target.setVisible(true);
    seenBy.setLocation(10, 20);
    target.setLocation(40, 70);
    target.setSize(100, 80);
    target.awt.rootPane.setSize(100, 15);

    Rectangle bounds = target.getBounds(seenBy);
    Shape closeButtonShape = target.getCloseButtonTrackableShape().getShape(seenBy, new Insets(0, 0, 0, 0));

    assertEquals(new Rectangle(30, 50, 100, 80), bounds);
    assertEquals(new Rectangle(30, 50, 100, 57), closeButtonShape.getBounds());
  }

  @Test
  public void contentAndRootPaneAccessorsStayAvailable() throws Exception {
    FakeAbstractWindow window = new FakeAbstractWindow(newHeadlessWindow());
    window.setVisible(true);
    window.setSize(25, 35);

    assertNotNull(window.getContentPane());
    assertNotNull(window.getRootPane());
    assertTrue(window.isInView());
    assertNull(window.getScrollPaneAncestor());
    assertEquals(new Rectangle(0, 0, 25, 35), window.getVisibleShape(window, new Insets(0, 0, 0, 0)).getBounds());
  }

  static HeadlessWindow newHeadlessWindow() throws InstantiationException {
    HeadlessWindow window = (HeadlessWindow) UNSAFE.allocateInstance(HeadlessWindow.class);
    window.location = new Point();
    window.size = new Dimension();
    window.contentPane = new JPanel(null);
    window.layeredPane = new HeadlessLayeredPane();
    window.rootPane = new JRootPane();
    window.rootPane.setLayeredPane(window.layeredPane);
    return window;
  }

  static final class FakeAbstractWindow extends AbstractWindow<HeadlessWindow> {
    final HeadlessWindow awt;

    FakeAbstractWindow(HeadlessWindow awt) {
      super(awt);
      this.awt = awt;
    }

    @Override
    Container getAwtContentPane() {
      return awt.contentPane;
    }

    @Override
    JRootPane getJRootPane() {
      return awt.rootPane;
    }

    @Override
    protected void setJMenuBar(javax.swing.JMenuBar jMenuBar) {
      awt.menuBar = jMenuBar;
    }
  }

  static class HeadlessLayeredPane extends JLayeredPane {
    Point screenLocation = new Point();

    @Override
    public Point getLocationOnScreen() {
      return new Point(screenLocation);
    }
  }

  public static class HeadlessWindow extends Window {
    Point location;
    Dimension size;
    boolean visible;
    boolean locationByPlatform;
    JPanel contentPane;
    JRootPane rootPane;
    HeadlessLayeredPane layeredPane;
    javax.swing.JMenuBar menuBar;

    private HeadlessWindow() {
      super((Window) null);
    }

    @Override
    public void dispose() {
      this.visible = false;
    }

    @Override
    public boolean isVisible() {
      return this.visible;
    }

    @Override
    public void setVisible(boolean b) {
      this.visible = b;
    }

    @Override
    public int getX() {
      return this.location.x;
    }

    @Override
    public int getY() {
      return this.location.y;
    }

    @Override
    public Point getLocationOnScreen() {
      return new Point(this.location);
    }

    @Override
    public void setLocation(Point p) {
      this.location = new Point(p);
    }

    @Override
    public void setLocation(int x, int y) {
      this.location = new Point(x, y);
    }

    @Override
    public boolean isLocationByPlatform() {
      return this.locationByPlatform;
    }

    @Override
    public void setLocationByPlatform(boolean b) {
      this.locationByPlatform = b;
    }

    @Override
    public int getWidth() {
      return this.size.width;
    }

    @Override
    public int getHeight() {
      return this.size.height;
    }

    @Override
    public void setSize(Dimension d) {
      this.size = new Dimension(d);
    }

    @Override
    public void setSize(int width, int height) {
      this.size = new Dimension(width, height);
    }
  }

  private static sun.misc.Unsafe getUnsafe() {
    try {
      Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      return (sun.misc.Unsafe) field.get(null);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }
}
