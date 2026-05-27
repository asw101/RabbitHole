package org.alice.math.immutable;

import org.junit.jupiter.api.Test;

import java.awt.Rectangle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FixedRectangleTest {
  @Test
  void fromRectangleCopiesAllCoordinates() {
    Rectangle rectangle = new Rectangle(12, 24, 640, 480);

    FixedRectangle fixedRectangle = FixedRectangle.fromRectangle(rectangle);

    assertEquals(new FixedRectangle(12, 24, 640, 480), fixedRectangle);
  }

  @Test
  void fromRectangleReturnsNullWhenRectangleIsNull() {
    assertNull(FixedRectangle.fromRectangle(null));
  }
}
