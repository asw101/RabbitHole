package org.lgna.croquet.views.imp;

import org.junit.Test;
import org.lgna.croquet.DragModel;
import org.lgna.croquet.views.DragComponent;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

public class JProxyBehaviorTest {
  @Test
  public void proxySizeTracksSubjectAndFlagsToggle() {
    TestDragComponent component = new TestDragComponent();
    component.getAwtComponent().setSize(30, 15);
    TestProxy proxy = new TestProxy(component);

    assertTrue(proxy.getProxySize().width > 0);
    assertTrue(proxy.getProxySize().height > 0);
    assertFalse(proxy.isOverDropAcceptor());
    assertFalse(proxy.isCopyDesired());

    proxy.setOverDropAcceptor(true);
    proxy.setCopyDesired(true);

    assertTrue(proxy.isOverDropAcceptor());
    assertTrue(proxy.isCopyDesired());
  }

  @Test
  public void paintComponentUsesOffscreenImageAndAvailableHeight() {
    TestDragComponent component = new TestDragComponent();
    component.getAwtComponent().setSize(40, 20);
    TestProxy proxy = new TestProxy(component);
    proxy.setSize(40, 20);

    BufferedImage image = new BufferedImage(40, 20, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = image.createGraphics();
    proxy.paint(graphics);
    graphics.dispose();

    assertEquals(40, proxy.getDropWidth());
    assertEquals(20, proxy.getDropHeight());
    assertEquals(10, proxy.getAvailableHeight());
  }

  private static final class TestProxy extends JProxy {
    private TestProxy(DragComponent<?> dragComponent) {
      super(dragComponent);
    }

    @Override
    protected void paintProxy(Graphics2D g2) {
      fillBounds(g2);
    }

    @Override
    protected float getAlpha() {
      return 0.5f;
    }

    @Override
    public int getAvailableHeight() {
      return 10;
    }
  }

  private static final class TestDragComponent extends DragComponent<DragModel> {
    private TestDragComponent() {
      super(null, false);
    }

    @Override
    protected JDragView createAwtComponent() {
      return new JDragView();
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
  }
}
