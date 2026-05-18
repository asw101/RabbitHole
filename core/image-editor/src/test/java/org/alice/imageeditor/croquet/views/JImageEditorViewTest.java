package org.alice.imageeditor.croquet.views;

import org.alice.imageeditor.croquet.TestSupport;
import org.alice.imageeditor.croquet.Tool;
import org.junit.Test;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.Shape;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class JImageEditorViewTest {
  @Test
  public void privateHelpersClampPointsCreateShapesAndCacheScaledImages() throws Exception {
    TestSupport.onEdt(() -> {
      JImageEditorView view = new JImageEditorView(new TestSupport.HeadlessImageEditorFrame());
      view.setSize(30, 20);

      Shape shape = invokeCreateShape(new Point(3, 5), new Point(13, 19), 2.0, new Rectangle(4, 7, 0, 0));
      Rectangle2D bounds = shape.getBounds2D();
      assertEquals(5.0, bounds.getX(), 0.0);
      assertEquals(9.0, bounds.getY(), 0.0);
      assertEquals(5.0, bounds.getWidth(), 0.0);
      assertEquals(7.0, bounds.getHeight(), 0.0);
      assertNull(invokeCreateShape(new Point(1, 1), new Point(3, 3), 1.0, null));

      Point clamped = invokeGetClampedPoint(new MouseEvent(view, MouseEvent.MOUSE_DRAGGED, 1L, 0, -10, 99, 1, false));
      assertEquals(new Point(1, 18), clamped);

      BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
      Object firstScaled = TestSupport.invokeDeclared(JImageEditorView.class, view, "getScaledImage",
          new Class<?>[] {java.awt.Image.class, int.class, int.class}, image, 20, 20);
      Object secondScaled = TestSupport.invokeDeclared(JImageEditorView.class, view, "getScaledImage",
          new Class<?>[] {java.awt.Image.class, int.class, int.class}, image, 20, 20);
      Object resizedScaled = TestSupport.invokeDeclared(JImageEditorView.class, view, "getScaledImage",
          new Class<?>[] {java.awt.Image.class, int.class, int.class}, image, 25, 20);
      assertSame(firstScaled, secondScaled);
      assertNotSame(firstScaled, resizedScaled);
    });
  }

  @Test
  public void preferredSizePaintRenderAndMinimumSizeFollowFrameState() throws Exception {
    TestSupport.onEdt(() -> {
      TestSupport.HeadlessImageEditorFrame frame = new TestSupport.HeadlessImageEditorFrame();
      frame.getShowInScreenResolutionState().setValueTransactionlessly(false);
      frame.getShowDashedBorderState().setValueTransactionlessly(true);
      frame.getDropShadowState().setValueTransactionlessly(true);
      frame.getImageHolder().setValue(new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB));
      frame.addShape(new Rectangle2D.Double(2.0, 2.0, 6.0, 4.0));

      JImageEditorView view = new JImageEditorView(frame);
      view.setSize(22, 12);
      assertEquals(new Dimension(22, 12), view.getPreferredSize());
      assertEquals(view.getPreferredSize(), view.getMinimumSize());

      frame.getToolState().setValueTransactionlessly(Tool.CROP_SELECT);
      frame.getCropSelectHolder().setValue(new Rectangle(1, 1, 4, 3));
      TestSupport.setField(JImageEditorView.class, view, "ptPressed", new Point(2, 2));
      TestSupport.setField(JImageEditorView.class, view, "ptDragged", new Point(12, 8));
      BufferedImage painted = new BufferedImage(22, 12, BufferedImage.TYPE_INT_ARGB);
      java.awt.Graphics2D graphics = painted.createGraphics();
      view.paintComponent(graphics);
      graphics.dispose();
      assertNotNull(painted);

      frame.getCropCommitHolder().setValue(new Rectangle(1, 1, 5, 4));
      assertEquals(new Dimension(7, 6), view.getPreferredSize());
      BufferedImage rendered = new BufferedImage(20, 10, BufferedImage.TYPE_INT_ARGB);
      java.awt.Graphics2D renderGraphics = rendered.createGraphics();
      view.render(renderGraphics);
      renderGraphics.dispose();
      assertNotNull(rendered);
    });
  }

  @Test
  public void mouseReleaseCanCropOrAddRectangle() throws Exception {
    TestSupport.onEdt(() -> {
      TestSupport.HeadlessImageEditorFrame frame = new TestSupport.HeadlessImageEditorFrame();
      frame.getShowInScreenResolutionState().setValueTransactionlessly(false);
      frame.getImageHolder().setValue(new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB));
      JImageEditorView view = new JImageEditorView(frame);
      view.setSize(22, 22);

      frame.getToolState().setValueTransactionlessly(Tool.CROP_SELECT);
      TestSupport.setField(JImageEditorView.class, view, "ptPressed", new Point(2, 3));
      invokeHandleMouseReleased(view, new MouseEvent(view, MouseEvent.MOUSE_RELEASED, 2L, 0, 12, 14, 1, false, MouseEvent.BUTTON1));
      assertEquals(new Rectangle(1, 2, 10, 11), frame.getCropSelectHolder().getValue());

      frame.getToolState().setValueTransactionlessly(Tool.ADD_RECTANGLE);
      frame.getCropSelectHolder().setValue(null);
      TestSupport.setField(JImageEditorView.class, view, "ptPressed", new Point(4, 4));
      invokeHandleMouseReleased(view, new MouseEvent(view, MouseEvent.MOUSE_RELEASED, 3L, 0, 16, 18, 1, false, MouseEvent.BUTTON1));
      assertEquals(1, frame.getShapes().size());
      assertTrue(frame.getShapes().get(0).getBounds2D().getWidth() > 0.0);
    });
  }

  @Test
  public void addNotifyAndRemoveNotifyManageListeners() throws Exception {
    TestSupport.onEdt(() -> {
      JImageEditorView view = new JImageEditorView(new TestSupport.HeadlessImageEditorFrame());
      JPanel parent = new JPanel();
      parent.add(view);
      parent.addNotify();
      view.addNotify();
      view.removeNotify();
    });
  }

  private static Shape invokeCreateShape(Point a, Point b, double scale, Rectangle crop) throws Exception {
    return (Shape) TestSupport.invokeDeclared(JImageEditorView.class, null, "createShape",
        new Class<?>[] {Point.class, Point.class, double.class, Rectangle.class}, a, b, scale, crop);
  }

  private static Point invokeGetClampedPoint(MouseEvent event) throws Exception {
    return (Point) TestSupport.invokeDeclared(JImageEditorView.class, null, "getClampedPoint", new Class<?>[] {MouseEvent.class}, event);
  }

  private static void invokeHandleMouseReleased(JImageEditorView view, MouseEvent event) throws Exception {
    TestSupport.invokeDeclared(JImageEditorView.class, view, "handleMouseReleased", new Class<?>[] {MouseEvent.class}, event);
  }
}
