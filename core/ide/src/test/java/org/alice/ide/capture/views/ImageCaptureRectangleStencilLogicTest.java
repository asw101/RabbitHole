package org.alice.ide.capture.views;

import org.junit.Test;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ImageCaptureRectangleStencilLogicTest {
  @Test
  public void pressCreatesZeroSizedValidHole() {
    Rectangle hole = ImageCaptureRectangleStencilLogic.createHoleOnPress(10, 20);

    assertEquals(new Rectangle(10, 20, 0, 0), hole);
    assertTrue(ImageCaptureRectangleStencilLogic.isHoleValid(hole));
    assertFalse(ImageCaptureRectangleStencilLogic.shouldCapture(hole));
  }

  @Test
  public void dragNormalizesReverseCoordinates() {
    Rectangle hole = ImageCaptureRectangleStencilLogic.createHoleOnDrag(10, 9, 4, 5);

    assertEquals(new Rectangle(4, 5, 6, 4), hole);
    assertTrue(ImageCaptureRectangleStencilLogic.shouldCapture(hole));
  }

  @Test
  public void invalidHoleDisablesCapture() {
    Rectangle hole = ImageCaptureRectangleStencilLogic.createInvalidHole();

    assertEquals(new Rectangle(-1, -1, -1, -1), hole);
    assertFalse(ImageCaptureRectangleStencilLogic.isHoleValid(hole));
    assertFalse(ImageCaptureRectangleStencilLogic.shouldCapture(hole));
  }

  @Test
  public void windowLocationOffsetsFromPointer() {
    assertEquals(new Point(42, 52), ImageCaptureRectangleStencilLogic.getWindowLocation(10, 20));
  }

  @Test
  public void stencilShapeExcludesPositiveHoleArea() {
    Shape shape = ImageCaptureRectangleStencilLogic.createStencilShape(new Rectangle(0, 0, 20, 20), new Rectangle(5, 5, 5, 5));

    assertTrue(shape.contains(1, 1));
    assertFalse(shape.contains(6, 6));
    assertTrue(shape.contains(18, 18));
  }

  @Test
  public void outlineExpandsHoleByOnePixel() {
    assertEquals(new Rectangle(4, 4, 6, 6), ImageCaptureRectangleStencilLogic.createHoleOutline(new Rectangle(5, 5, 5, 5)));
  }
}
