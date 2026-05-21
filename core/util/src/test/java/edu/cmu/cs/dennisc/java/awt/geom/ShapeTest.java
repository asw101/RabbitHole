package edu.cmu.cs.dennisc.java.awt.geom;

import org.alice.math.immutable.AngleInDegrees;
import org.junit.Test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

public class ShapeTest {
  @Test
  public void paintAppliesTransformAndRestoresGraphicsState() {
    RectangleShape shape = new RectangleShape();
    shape.setFillPaint(Color.RED);
    shape.setDrawPaint(Color.BLUE);
    shape.applyTranslation(10, 6);

    BufferedImage image = new BufferedImage(40, 30, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = image.createGraphics();
    AffineTransform original = g2.getTransform();
    GraphicsContext gc = new GraphicsContext();
    gc.initialize(g2);

    shape.paint(gc);

    assertEquals(original, g2.getTransform());
    assertNotEquals(0, image.getRGB(12, 8));
  }

  @Test
  public void getAreaUsesCurrentAffineTransformAndFilledState() {
    RectangleShape shape = new RectangleShape();
    shape.applyTranslation(5, 4);
    TransformContext context = new TransformContext();
    context.initialize();

    Area area = shape.getArea(context);
    Rectangle2D bounds = area.getBounds2D();
    assertEquals(5.0, bounds.getX(), 0.01);
    assertEquals(4.0, bounds.getY(), 0.01);

    shape.setFilled(false);
    context.initialize();
    assertTrue(shape.getArea(context).isEmpty());
  }

  @Test
  public void affineAndInverseTransformsTrackScaleRotationAndTranslation() {
    RectangleShape shape = new RectangleShape();
    shape.applyScale(2.0, 3.0);
    shape.applyRotation(new AngleInDegrees(90));
    shape.applyTranslation(7, 11);

    AffineTransform transform = shape.getAffineTransform();
    AffineTransform inverse = shape.getInverseAffineTransform();
    AffineTransform combined = new AffineTransform(transform);
    combined.concatenate(inverse);

    assertEquals(1.0, combined.getScaleX(), 1e-6);
    assertEquals(1.0, combined.getScaleY(), 1e-6);
    assertEquals(0.0, combined.getTranslateX(), 1e-6);
    assertEquals(0.0, combined.getTranslateY(), 1e-6);
  }

  @Test
  public void fillAndDrawStateAndPaintsRoundTrip() {
    RectangleShape shape = new RectangleShape();
    Paint fill = Color.GREEN;
    Paint draw = Color.MAGENTA;

    shape.setFilled(false);
    shape.setDrawn(false);
    shape.setFillPaint(fill);
    shape.setDrawPaint(draw);

    assertFalse(shape.isFilled());
    assertFalse(shape.isDrawn());
    assertEquals(fill, shape.getFillPaint());
    assertEquals(draw, shape.getDrawPaint());
  }

  private static final class RectangleShape extends Shape {
    @Override
    protected java.awt.Shape getFillShape() {
      return new Rectangle2D.Double(0, 0, 10, 5);
    }

    @Override
    protected java.awt.Shape getDrawShape() {
      return new Rectangle2D.Double(0, 0, 10, 5);
    }
  }
}
