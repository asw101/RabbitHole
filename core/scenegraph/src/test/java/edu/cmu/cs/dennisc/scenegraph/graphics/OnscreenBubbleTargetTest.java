package edu.cmu.cs.dennisc.scenegraph.graphics;

import org.junit.Test;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import static org.junit.Assert.assertEquals;

public class OnscreenBubbleTargetTest {
  @Test
  public void setPositionMaintainsTailAndTextOffsets() {
    OnscreenBubble bubble = new OnscreenBubble(
        new Point2D.Float(10, 15),
        new Point2D.Float(20, 30),
        new RoundRectangle2D.Double(5, 10, 100, 60, 10, 10),
        new Rectangle2D.Double(15, 20, 80, 40),
        Bubble.PositionPreference.TOP_LEFT
    );

    bubble.setPosition(50, 70);

    assertEquals(60.0, bubble.getTextBounds().x, 0.0001);
    assertEquals(80.0, bubble.getTextBounds().y, 0.0001);
    assertEquals(65.0, bubble.getEndOfTail().x, 0.0001);
    assertEquals(90.0, bubble.getEndOfTail().y, 0.0001);
  }

  @Test
  public void updateOriginOfTailUsesViewportPercentageForTailEnd() {
    OnscreenBubble bubble = new OnscreenBubble(
        new Point2D.Float(0, 0),
        new Point2D.Float(20, 30),
        new RoundRectangle2D.Double(100, 120, 100, 60, 10, 10),
        new Rectangle2D.Double(110, 130, 80, 40),
        Bubble.PositionPreference.TOP_CENTER
    );

    bubble.updateOriginOfTail(new Point2D.Float(150, 10), new Rectangle(0, 0, 300, 200));

    assertEquals(150.0, bubble.getOriginOfTail().x, 0.0001);
    assertEquals(150.0, bubble.getEndOfTail().x, 0.0001);
    assertEquals(10.0, bubble.getHorizontalPadding(), 0.0001);
  }
}
