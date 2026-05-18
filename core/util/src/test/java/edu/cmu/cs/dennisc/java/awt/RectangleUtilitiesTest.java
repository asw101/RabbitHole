package edu.cmu.cs.dennisc.java.awt;

import org.junit.Test;

import javax.swing.SwingConstants;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import static org.junit.Assert.*;

public class RectangleUtilitiesTest {

  @Test
  public void grow_xyPad_expandsRectangle() {
    Rectangle rectangle = new Rectangle(10, 20, 30, 40);

    Rectangle result = RectangleUtilities.grow(rectangle, 2, 3);

    assertSame(rectangle, result);
    assertEquals(new Rectangle(8, 17, 34, 46), result);
  }

  @Test
  public void grow_singlePad_expandsSymmetrically() {
    Rectangle rectangle = new Rectangle(5, 6, 7, 8);

    Rectangle result = RectangleUtilities.grow(rectangle, 4);

    assertEquals(new Rectangle(1, 2, 15, 16), result);
  }

  @Test
  public void grow_zeroPad_leavesRectangleUnchanged() {
    Rectangle rectangle = new Rectangle(1, 2, 3, 4);

    RectangleUtilities.grow(rectangle, 0, 0);

    assertEquals(new Rectangle(1, 2, 3, 4), rectangle);
  }

  @Test
  public void grow_negativePad_shrinksRectangle() {
    Rectangle rectangle = new Rectangle(10, 10, 20, 20);

    RectangleUtilities.grow(rectangle, -3, -4);

    assertEquals(new Rectangle(13, 14, 14, 12), rectangle);
  }

  @Test
  public void grow_returnsSameInstance() {
    Rectangle rectangle = new Rectangle(0, 0, 1, 1);

    assertSame(rectangle, RectangleUtilities.grow(rectangle, 1));
  }

  @Test
  public void getPoint_leadingLeading_returnsUpperLeft() {
    Rectangle rectangle = new Rectangle(10, 20, 30, 40);

    Point point = RectangleUtilities.getPoint(rectangle, SwingConstants.LEADING, SwingConstants.LEADING);

    assertEquals(new Point(10, 20), point);
  }

  @Test
  public void getPoint_trailingTrailing_returnsLowerRight() {
    Rectangle rectangle = new Rectangle(10, 20, 30, 40);

    Point point = RectangleUtilities.getPoint(rectangle, SwingConstants.TRAILING, SwingConstants.TRAILING);

    assertEquals(new Point(40, 60), point);
  }

  @Test
  public void getPoint_centerCenter_returnsMidpoint() {
    Rectangle rectangle = new Rectangle(10, 21, 31, 41);

    Point point = RectangleUtilities.getPoint(rectangle, SwingConstants.CENTER, SwingConstants.CENTER);

    assertEquals(new Point(25, 41), point);
  }

  @Test
  public void getPoint_mixedConstraints_returnsExpectedPoint() {
    Rectangle rectangle = new Rectangle(3, 4, 10, 20);

    Point point = RectangleUtilities.getPoint(rectangle, SwingConstants.LEADING, SwingConstants.CENTER);

    assertEquals(new Point(3, 14), point);
  }

  @Test
  public void inset_withInsets_expandsByInsetValues() {
    Rectangle rectangle = new Rectangle(10, 20, 30, 40);
    Insets insets = new Insets(1, 2, 3, 4);

    Rectangle result = RectangleUtilities.inset(rectangle, insets);

    assertSame(rectangle, result);
    assertEquals(new Rectangle(8, 19, 36, 44), result);
  }

  @Test
  public void inset_zeroInsets_leavesRectangleUnchanged() {
    Rectangle rectangle = new Rectangle(10, 20, 30, 40);

    RectangleUtilities.inset(rectangle, new Insets(0, 0, 0, 0));

    assertEquals(new Rectangle(10, 20, 30, 40), rectangle);
  }

  @Test
  public void inset_nullInsets_returnsSameInstance() {
    Rectangle rectangle = new Rectangle(10, 20, 30, 40);

    Rectangle result = RectangleUtilities.inset(rectangle, null);

    assertSame(rectangle, result);
    assertEquals(new Rectangle(10, 20, 30, 40), result);
  }

  @Test
  public void inset_nullRectangleWithInsets_returnsNull() {
    assertNull(RectangleUtilities.inset(null, new Insets(1, 2, 3, 4)));
  }

  @Test
  public void setBounds_forwardCoordinates_usesGivenBounds() {
    Rectangle rectangle = new Rectangle();

    RectangleUtilities.setBounds(rectangle, 1, 2, 11, 22);

    assertEquals(new Rectangle(1, 2, 10, 20), rectangle);
  }

  @Test
  public void setBounds_reversedCoordinates_normalizesBounds() {
    Rectangle rectangle = new Rectangle();

    RectangleUtilities.setBounds(rectangle, 11, 22, 1, 2);

    assertEquals(new Rectangle(1, 2, 10, 20), rectangle);
  }

  @Test
  public void setBounds_verticalLine_setsZeroWidth() {
    Rectangle rectangle = new Rectangle();

    RectangleUtilities.setBounds(rectangle, 5, 4, 5, 20);

    assertEquals(new Rectangle(5, 4, 0, 16), rectangle);
  }

  @Test
  public void setBounds_horizontalLine_setsZeroHeight() {
    Rectangle rectangle = new Rectangle();

    RectangleUtilities.setBounds(rectangle, 5, 7, 15, 7);

    assertEquals(new Rectangle(5, 7, 10, 0), rectangle);
  }
}
