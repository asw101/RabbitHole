package org.alice.math.immutable;

import org.junit.jupiter.api.Test;

import java.awt.Rectangle;

import static org.junit.jupiter.api.Assertions.*;

class ClippedZPlaneTest {

  static final double EPSILON = 1e-10;

  // --- Record accessors ---

  @Test
  void halfWidth_returnsConstructedValue() {
    ClippedZPlane p = new ClippedZPlane(2.0, 3.0);
    assertEquals(2.0, p.halfWidth(), EPSILON);
  }

  @Test
  void halfHeight_returnsConstructedValue() {
    ClippedZPlane p = new ClippedZPlane(2.0, 3.0);
    assertEquals(3.0, p.halfHeight(), EPSILON);
  }

  // --- DEFAULT constant ---

  @Test
  void default_hasNaNHalfWidth() {
    assertTrue(Double.isNaN(ClippedZPlane.DEFAULT.halfWidth()));
  }

  @Test
  void default_hasExpectedHalfHeight() {
    assertEquals(0.1, ClippedZPlane.DEFAULT.halfHeight(), EPSILON);
  }

  // --- createWithHeight ---

  @Test
  void createWithHeight_setsHalfHeightToHalf() {
    ClippedZPlane p = ClippedZPlane.createWithHeight(0.4);
    assertEquals(0.2, p.halfHeight(), EPSILON);
    assertTrue(Double.isNaN(p.halfWidth()));
  }

  // --- createWithHalfHeight ---

  @Test
  void createWithHalfHeight_setsHalfHeight() {
    ClippedZPlane p = ClippedZPlane.createWithHalfHeight(0.5);
    assertEquals(0.5, p.halfHeight(), EPSILON);
    assertTrue(Double.isNaN(p.halfWidth()));
  }

  // --- withHeight ---

  @Test
  void withHeight_updatesHalfHeight() {
    ClippedZPlane p = new ClippedZPlane(1.0, 2.0);
    ClippedZPlane r = p.withHeight(6.0);
    assertEquals(1.0, r.halfWidth(), EPSILON);
    assertEquals(3.0, r.halfHeight(), EPSILON);
  }

  // --- completeFrom(Rectangle) ---

  @Test
  void completeFrom_rectangle_completesWithAspectRatio() {
    ClippedZPlane p = ClippedZPlane.createWithHalfHeight(0.1);
    Rectangle viewport = new Rectangle(0, 0, 800, 400);
    ClippedZPlane r = p.completeFrom(viewport);
    assertEquals(0.1, r.halfHeight(), EPSILON);
    assertEquals(0.2, r.halfWidth(), EPSILON);
  }

  // --- completeFrom(FixedRectangle) ---

  @Test
  void completeFrom_fixedRectangle_completesWithAspectRatio() {
    ClippedZPlane p = ClippedZPlane.createWithHalfHeight(0.1);
    FixedRectangle viewport = new FixedRectangle(0, 0, 800, 400);
    ClippedZPlane r = p.completeFrom(viewport);
    assertEquals(0.1, r.halfHeight(), EPSILON);
    assertEquals(0.2, r.halfWidth(), EPSILON);
  }

  // --- completeFrom: already complete ---

  @Test
  void completeFrom_alreadyComplete_returnsSelf() {
    ClippedZPlane p = new ClippedZPlane(0.5, 0.3);
    ClippedZPlane r = p.completeFrom(new Rectangle(0, 0, 800, 600));
    assertEquals(0.5, r.halfWidth(), EPSILON);
    assertEquals(0.3, r.halfHeight(), EPSILON);
  }

  // --- completeFrom: only halfWidth set ---

  @Test
  void completeFrom_onlyHalfWidthSet_derivesHalfHeight() {
    ClippedZPlane p = new ClippedZPlane(0.4, Double.NaN);
    ClippedZPlane r = p.completeFrom(new Rectangle(0, 0, 800, 400));
    assertEquals(0.4, r.halfWidth(), EPSILON);
    assertEquals(0.2, r.halfHeight(), EPSILON);
  }

  // --- completeFrom: both NaN defaults ---

  @Test
  void completeFrom_bothNaN_usesDefaults() {
    ClippedZPlane p = new ClippedZPlane(Double.NaN, Double.NaN);
    ClippedZPlane r = p.completeFrom(new Rectangle(0, 0, 800, 400));
    assertEquals(0.1, r.halfHeight(), EPSILON);
    assertEquals(0.2, r.halfWidth(), EPSILON);
  }

  // --- getWidth / getHeight ---

  @Test
  void getWidth_returnsDoubleHalfWidth() {
    ClippedZPlane p = new ClippedZPlane(3.0, 2.0);
    assertEquals(6.0, p.getWidth(), EPSILON);
  }

  @Test
  void getHeight_returnsDoubleHalfHeight() {
    ClippedZPlane p = new ClippedZPlane(3.0, 2.0);
    assertEquals(4.0, p.getHeight(), EPSILON);
  }

  // --- getXMinimum / getXMaximum ---

  @Test
  void getXMinimum_returnsNegativeHalfWidth() {
    ClippedZPlane p = new ClippedZPlane(3.0, 2.0);
    assertEquals(-3.0, p.getXMinimum(), EPSILON);
  }

  @Test
  void getXMaximum_returnsHalfWidth() {
    ClippedZPlane p = new ClippedZPlane(3.0, 2.0);
    assertEquals(3.0, p.getXMaximum(), EPSILON);
  }

  // --- getYMinimum / getYMaximum ---

  @Test
  void getYMinimum_returnsNegativeHalfHeight() {
    ClippedZPlane p = new ClippedZPlane(3.0, 2.0);
    assertEquals(-2.0, p.getYMinimum(), EPSILON);
  }

  @Test
  void getYMaximum_returnsHalfHeight() {
    ClippedZPlane p = new ClippedZPlane(3.0, 2.0);
    assertEquals(2.0, p.getYMaximum(), EPSILON);
  }

  // --- record equality ---

  @Test
  void equals_sameValues() {
    ClippedZPlane p1 = new ClippedZPlane(1.0, 2.0);
    ClippedZPlane p2 = new ClippedZPlane(1.0, 2.0);
    assertEquals(p1, p2);
  }

  @Test
  void equals_differentValues() {
    ClippedZPlane p1 = new ClippedZPlane(1.0, 2.0);
    ClippedZPlane p2 = new ClippedZPlane(3.0, 4.0);
    assertNotEquals(p1, p2);
  }
}
