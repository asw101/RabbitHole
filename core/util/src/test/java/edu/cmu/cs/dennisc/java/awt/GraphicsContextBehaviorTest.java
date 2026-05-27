package edu.cmu.cs.dennisc.java.awt;

import org.junit.Test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GraphicsContextBehaviorTest {

  @Test
  public void pushAndPopRestoreIndividualGraphicsSettings() {
    BufferedImage image = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
    java.awt.Graphics2D graphics = image.createGraphics();
    graphics.setFont(new Font("Dialog", Font.PLAIN, 12));
    graphics.setPaint(Color.BLUE);
    graphics.setStroke(new BasicStroke(1.5f));
    graphics.setTransform(new AffineTransform());

    GraphicsContext context = new GraphicsContext();
    context.pushAll(graphics);

    context.pushFont();
    graphics.setFont(new Font("Dialog", Font.BOLD, 18));
    context.popFont();

    context.pushPaint();
    graphics.setPaint(Color.RED);
    context.popPaint();

    context.pushStroke();
    graphics.setStroke(new BasicStroke(3f));
    context.popStroke();

    context.pushTransform();
    graphics.translate(4, 5);
    context.popTransform();

    assertEquals(new Font("Dialog", Font.PLAIN, 12), graphics.getFont());
    assertEquals(Color.BLUE, graphics.getPaint());
    assertEquals(1.5f, ((BasicStroke) graphics.getStroke()).getLineWidth(), 0.0f);
    assertEquals(new AffineTransform(), graphics.getTransform());
  }

  @Test
  public void popAllRestoresRenderingHintsAndOriginalState() {
    BufferedImage image = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
    java.awt.Graphics2D graphics = image.createGraphics();
    graphics.setPaint(Color.BLACK);
    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

    GraphicsContext context = new GraphicsContext();
    context.pushAll(graphics);
    context.pushPaint();
    context.pushAndSetAntialiasing(true);
    context.pushAndSetTextAntialiasing(true);
    graphics.setPaint(Color.GREEN);

    context.popAll();

    assertEquals(Color.BLACK, graphics.getPaint());
    assertEquals(RenderingHints.VALUE_ANTIALIAS_OFF, graphics.getRenderingHint(RenderingHints.KEY_ANTIALIASING));
    assertEquals(RenderingHints.VALUE_TEXT_ANTIALIAS_OFF, graphics.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING));
  }

  @Test
  public void instanceFactoryProvidesContextForCurrentThread() {
    BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
    java.awt.Graphics2D graphics = image.createGraphics();

    GraphicsContext context = GraphicsContext.getInstanceAndPushGraphics(graphics);
    assertNotNull(context);
    context.popAll();
  }
}
