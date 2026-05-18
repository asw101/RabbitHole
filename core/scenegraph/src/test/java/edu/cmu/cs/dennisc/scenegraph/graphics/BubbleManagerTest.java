package edu.cmu.cs.dennisc.scenegraph.graphics;

import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.render.RenderTarget;
import edu.cmu.cs.dennisc.scenegraph.AbstractCamera;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import static org.junit.Assert.*;

public class BubbleManagerTest {
  private static final Bubble.Originator ORIGINATOR = new Bubble.Originator() {
    @Override
    public void calculate(Point2D.Float outOriginOfTail, Point2D.Float outBodyConnectionLocationOfTail, Point2D.Float outTextBoundsOffset, Bubble bubble, RenderTarget renderTarget, Rectangle actualViewport, AbstractCamera camera, Dimension2D textSize) {
      outOriginOfTail.setLocation(0, 0);
      outBodyConnectionLocationOfTail.setLocation(0, 0);
      outTextBoundsOffset.setLocation(0, 0);
    }
  };

  @Test
  public void bubbleDefaultsAndConstructorsExposeConfiguredProperties() {
    SpeechBubble automatic = new SpeechBubble(ORIGINATOR);
    ThoughtBubble topRight = new ThoughtBubble(ORIGINATOR, Bubble.PositionPreference.TOP_RIGHT);
    MainTitle title = new MainTitle();
    Subtitle subtitle = new Subtitle();

    assertSame(ORIGINATOR, automatic.getOriginator());
    assertEquals(Bubble.PositionPreference.AUTOMATIC, automatic.getPositionPreference());
    assertEquals(Color4f.BLACK, automatic.textColor.getValue());
    assertEquals(Color4f.WHITE, automatic.fillColor.getValue());
    assertEquals(Color4f.BLACK, automatic.outlineColor.getValue());
    assertEquals(Bubble.PositionPreference.TOP_RIGHT, topRight.getPositionPreference());
    assertEquals(Color4f.WHITE, title.textColor.getValue());
    assertEquals(24, title.font.getValue().getSize());
    assertEquals("", subtitle.text.getValue());
  }

  @Test
  public void bubbleManagerAddsPlacesGetsAndRemovesBubbles() {
    BubbleManager manager = BubbleManager.getInstance();
    assertSame(manager, BubbleManager.getInstance());

    Rectangle viewport = new Rectangle(0, 0, 300, 200);
    Dimension textSize = new Dimension(60, 20);
    SpeechBubble leftBubble = new SpeechBubble(ORIGINATOR);
    SpeechBubble centerBubble = new SpeechBubble(ORIGINATOR);
    SpeechBubble rightBubble = new SpeechBubble(ORIGINATOR);

    OnscreenBubble left = manager.addBubble(leftBubble, new Point2D.Float(10, 20), textSize, 10.0f, 1.0f, viewport);
    OnscreenBubble center = manager.addBubble(centerBubble, new Point2D.Float(150, 30), textSize, 10.0f, 1.0f, viewport);
    OnscreenBubble right = manager.addBubble(rightBubble, new Point2D.Float(290, 40), textSize, 10.0f, 1.0f, viewport);

    assertEquals(Bubble.PositionPreference.TOP_LEFT, left.getPositionPreference());
    assertEquals(Bubble.PositionPreference.TOP_CENTER, center.getPositionPreference());
    assertEquals(Bubble.PositionPreference.TOP_RIGHT, right.getPositionPreference());
    assertTrue(center.getBubbleRect().getY() >= left.getBubbleRect().getY());
    assertSame(center, manager.getBubble(centerBubble));

    manager.packBubbles(viewport);
    manager.removeBubble(centerBubble);
    assertNull(manager.getBubble(centerBubble));
    manager.removeBubble(leftBubble);
    manager.removeBubble(rightBubble);
  }

  @Test
  public void onscreenBubblePositioningAndTailUpdatesRespectPadding() {
    OnscreenBubble bubble = new OnscreenBubble(
        new Point2D.Float(10, 15),
        new Point2D.Float(20, 30),
        new RoundRectangle2D.Double(5, 10, 100, 60, 10, 10),
        new Rectangle2D.Double(15, 20, 80, 40),
        Bubble.PositionPreference.TOP_LEFT);

    bubble.setPosition(50, 70);
    assertEquals(65.0, bubble.getEndOfTail().x, 0.0001);
    assertEquals(90.0, bubble.getEndOfTail().y, 0.0001);
    assertEquals(60.0, bubble.getTextBounds().x, 0.0001);
    assertEquals(80.0, bubble.getTextBounds().y, 0.0001);
    assertEquals(10.0, bubble.getHorizontalPadding(), 0.0001);
    assertEquals(10.0, bubble.getVerticalPadding(), 0.0001);

    bubble.setPosition(new Point2D.Double(100, 120));
    bubble.updateOriginOfTail(new Point2D.Float(150, 10), new Rectangle(0, 0, 300, 200));
    assertEquals(150.0, bubble.getOriginOfTail().x, 0.0001);
    assertEquals(150.0, bubble.getEndOfTail().x, 0.0001);
  }
}
