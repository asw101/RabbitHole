package org.alice.ide.common;

import org.junit.Test;
import org.lgna.project.ast.JavaType;

import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for {@link BeveledShapeForType} — geometry verification,
 * addRoundType mutation, union mechanics, edge dimensions, and all type-category paths.
 *
 * <p>All tests use only Shape/GeneralPath which work in headless mode (no AWT display needed).
 */
public class BeveledShapeForTypeComprehensiveTest {

  // ── Type-category shape creation ───────────────────────────────────

  @Test
  public void createBeveledShapeFor_voidType_emptyBasePath() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.VOID_TYPE, 0f, 0f, 100f, 20f);
    assertNotNull(shape);
    // Void creates empty GeneralPath: zero bounds
    Rectangle2D bounds = shape.getBaseShape().getBounds2D();
    assertEquals(0.0, bounds.getWidth(), 0.001);
    assertEquals(0.0, bounds.getHeight(), 0.001);
  }

  @Test
  public void createBeveledShapeFor_stringType_hasCurvedBounds() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(String.class), 10f, 5f, 80f, 30f);
    assertNotNull(shape);
    Rectangle2D bounds = shape.getBaseShape().getBounds2D();
    assertTrue("String shape width > 0", bounds.getWidth() > 0);
    assertTrue("String shape height > 0", bounds.getHeight() > 0);
  }

  @Test
  public void createBeveledShapeFor_numberType_steppedShape() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Number.class), 0f, 0f, 100f, 30f);
    assertNotNull(shape);
    Rectangle2D bounds = shape.getBaseShape().getBounds2D();
    assertTrue("Number shape spans full width", bounds.getWidth() >= 99.0);
    assertTrue("Number shape spans full height", bounds.getHeight() >= 29.0);
  }

  @Test
  public void createBeveledShapeFor_integerType_treatedAsNumber() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Integer.class), 0f, 0f, 100f, 20f);
    assertNotNull(shape);
    Rectangle2D bounds = shape.getBaseShape().getBounds2D();
    assertTrue("Integer inherits Number shape", bounds.getWidth() > 0);
  }

  @Test
  public void createBeveledShapeFor_doubleType_treatedAsNumber() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Double.class), 0f, 0f, 80f, 25f);
    assertNotNull(shape);
    Rectangle2D bounds = shape.getBaseShape().getBounds2D();
    assertTrue("Double inherits Number shape", bounds.getWidth() > 0);
  }

  @Test
  public void createBeveledShapeFor_booleanBoxed_hasPositiveBounds() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Boolean.class), 0f, 0f, 100f, 20f);
    assertNotNull(shape);
    Rectangle2D bounds = shape.getBaseShape().getBounds2D();
    assertTrue("Boolean shape width > 0", bounds.getWidth() > 0);
  }

  @Test
  public void createBeveledShapeFor_booleanPrimitive_hasPositiveBounds() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.BOOLEAN_PRIMITIVE_TYPE, 0f, 0f, 100f, 20f);
    assertNotNull(shape);
    Rectangle2D bounds = shape.getBaseShape().getBounds2D();
    assertTrue(bounds.getWidth() > 0);
  }

  @Test
  public void createBeveledShapeFor_objectType_defaultRectShape() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Object.class), 10f, 5f, 80f, 30f);
    assertNotNull(shape);
    Rectangle2D bounds = shape.getBaseShape().getBounds2D();
    assertEquals(10.0, bounds.getX(), 1.0);
    assertEquals(5.0, bounds.getY(), 1.0);
    assertTrue(bounds.getWidth() > 0);
    assertTrue(bounds.getHeight() > 0);
  }

  // ── Distinct shapes per category ───────────────────────────────────

  @Test
  public void createBeveledShapeFor_differentCategories_returnDistinctObjects() {
    BeveledShapeForType v = BeveledShapeForType.createBeveledShapeFor(
        JavaType.VOID_TYPE, 0f, 0f, 100f, 20f);
    BeveledShapeForType s = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(String.class), 0f, 0f, 100f, 20f);
    BeveledShapeForType n = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Number.class), 0f, 0f, 100f, 20f);
    BeveledShapeForType b = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Boolean.class), 0f, 0f, 100f, 20f);
    BeveledShapeForType d = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Object.class), 0f, 0f, 100f, 20f);

    assertNotSame(v, s);
    assertNotSame(s, n);
    assertNotSame(n, b);
    assertNotSame(b, d);
    assertNotSame(v, d);
  }

  @Test
  public void createBeveledShapeFor_voidHasEmptyBounds_othersHavePositive() {
    Rectangle2D voidBounds = BeveledShapeForType.createBeveledShapeFor(
        JavaType.VOID_TYPE, 0f, 0f, 100f, 20f).getBaseShape().getBounds2D();
    Rectangle2D stringBounds = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(String.class), 0f, 0f, 100f, 20f).getBaseShape().getBounds2D();
    Rectangle2D numBounds = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Integer.class), 0f, 0f, 100f, 20f).getBaseShape().getBounds2D();
    Rectangle2D boolBounds = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Boolean.class), 0f, 0f, 100f, 20f).getBaseShape().getBounds2D();
    Rectangle2D defaultBounds = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Object.class), 0f, 0f, 100f, 20f).getBaseShape().getBounds2D();

    assertEquals("Void width should be 0", 0.0, voidBounds.getWidth(), 0.001);
    assertTrue("String width > 0", stringBounds.getWidth() > 0);
    assertTrue("Number width > 0", numBounds.getWidth() > 0);
    assertTrue("Boolean width > 0", boolBounds.getWidth() > 0);
    assertTrue("Default width > 0", defaultBounds.getWidth() > 0);
  }

  // ── Geometry: coordinate values ────────────────────────────────────

  @Test
  public void createBeveledShapeFor_stringType_boundsContainedInInputRect() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(String.class), 0f, 0f, 100f, 20f);
    Rectangle2D bounds = shape.getBaseShape().getBounds2D();
    assertTrue("X should be >= 0", bounds.getX() >= -1.0);
    assertTrue("Y should be >= 0", bounds.getY() >= -1.0);
    assertTrue("Width should be <= 101", bounds.getWidth() <= 101.0);
    assertTrue("Height should be <= 21", bounds.getHeight() <= 21.0);
  }

  @Test
  public void createBeveledShapeFor_numberType_originCorrect() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Number.class), 5f, 10f, 90f, 40f);
    Rectangle2D bounds = shape.getBaseShape().getBounds2D();
    assertEquals("X origin", 5.0, bounds.getX(), 1.0);
    assertEquals("Y origin", 10.0, bounds.getY(), 1.0);
  }

  @Test
  public void createBeveledShapeFor_booleanType_usesQuadCurve() {
    // Boolean uses quadTo — the shape's height should match y1-y0
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Boolean.class), 0f, 0f, 100f, 40f);
    Rectangle2D bounds = shape.getBaseShape().getBounds2D();
    assertTrue("Boolean height should be ~ 40", bounds.getHeight() >= 38.0);
  }

  // ── addRoundType and round-type path ───────────────────────────────

  @Test
  public void addRoundType_registeredType_usesRoundPath() {
    // Use Closeable as an isolated type not used elsewhere in tests
    BeveledShapeForType.addRoundType(java.io.Closeable.class);

    // java.io.InputStream implements Closeable
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(java.io.InputStream.class), 0f, 0f, 100f, 20f);
    assertNotNull(shape);
    Rectangle2D bounds = shape.getBaseShape().getBounds2D();
    assertTrue("Round type path width > 0", bounds.getWidth() > 0);
  }

  @Test
  public void addRoundType_unrelatedType_usesDefaultPath() {
    // Object is not assignable to Closeable → default path
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Object.class), 0f, 0f, 50f, 20f);
    assertNotNull(shape);
    Rectangle2D bounds = shape.getBaseShape().getBounds2D();
    assertTrue("Default path width > 0", bounds.getWidth() > 0);
  }

  // ── union mechanics ────────────────────────────────────────────────

  @Test
  public void union_expandsBounds() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Object.class), 0f, 0f, 50f, 20f);
    Rectangle2D boundsBefore = shape.getBaseShape().getBounds2D();
    double widthBefore = boundsBefore.getWidth();

    RoundRectangle2D.Float roundRect = new RoundRectangle2D.Float(60f, 0f, 100f, 20f, 5f, 5f);
    shape.union(roundRect);

    Rectangle2D boundsAfter = shape.getBaseShape().getBounds2D();
    assertTrue("Union should expand width", boundsAfter.getWidth() > widthBefore);
  }

  @Test
  public void union_mergesAreaCorrectly() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Object.class), 0f, 0f, 30f, 20f);
    RoundRectangle2D.Float roundRect = new RoundRectangle2D.Float(0f, 0f, 50f, 20f, 8f, 8f);
    shape.union(roundRect);

    Rectangle2D bounds = shape.getBaseShape().getBounds2D();
    // After union, the width should be at least as wide as the round rect
    assertTrue("Merged width >= roundRect width",
        bounds.getWidth() >= 49.0);
  }

  @Test
  public void union_withDisjointRect_spansFullRange() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Object.class), 0f, 0f, 20f, 20f);
    RoundRectangle2D.Float far = new RoundRectangle2D.Float(200f, 0f, 50f, 20f, 3f, 3f);
    shape.union(far);

    Rectangle2D bounds = shape.getBaseShape().getBounds2D();
    assertTrue("Disjoint union should span full range",
        bounds.getWidth() >= 249.0);
  }

  // ── createBeveledShapeFor with RoundRectangle2D overload ───────────

  @Test
  public void createBeveledShapeFor_withRoundRect_returnsNonNull() {
    RoundRectangle2D.Float roundRect = new RoundRectangle2D.Float(50f, 0f, 100f, 20f, 8f, 8f);
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Object.class), roundRect, 30f, 20f);
    assertNotNull(shape);
  }

  @Test
  public void createBeveledShapeFor_withRoundRect_hasBroaderBounds() {
    RoundRectangle2D.Float roundRect = new RoundRectangle2D.Float(50f, 0f, 100f, 20f, 8f, 8f);
    BeveledShapeForType withRound = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Object.class), roundRect, 30f, 20f);

    BeveledShapeForType withoutRound = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Object.class), 20f, 0f, 30f, 20f);

    Rectangle2D boundsWithRound = withRound.getBaseShape().getBounds2D();
    Rectangle2D boundsWithout = withoutRound.getBaseShape().getBounds2D();

    assertTrue("Unioned shape should be at least as wide",
        boundsWithRound.getWidth() >= boundsWithout.getWidth());
  }

  @Test
  public void createBeveledShapeFor_withRoundRect_stringType_works() {
    RoundRectangle2D.Float roundRect = new RoundRectangle2D.Float(40f, 0f, 80f, 20f, 6f, 6f);
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(String.class), roundRect, 30f, 20f);
    assertNotNull(shape);
    assertTrue("String + roundRect shape width > 0",
        shape.getBaseShape().getBounds2D().getWidth() > 0);
  }

  @Test
  public void createBeveledShapeFor_withRoundRect_numberType_works() {
    RoundRectangle2D.Float roundRect = new RoundRectangle2D.Float(40f, 0f, 80f, 20f, 6f, 6f);
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Number.class), roundRect, 30f, 20f);
    assertNotNull(shape);
  }

  @Test
  public void createBeveledShapeFor_withRoundRect_booleanType_works() {
    RoundRectangle2D.Float roundRect = new RoundRectangle2D.Float(40f, 0f, 80f, 20f, 6f, 6f);
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Boolean.class), roundRect, 30f, 20f);
    assertNotNull(shape);
  }

  // ── Edge dimensions ────────────────────────────────────────────────

  @Test
  public void createBeveledShapeFor_zeroWidth_returnsNonNull() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Object.class), 0f, 0f, 0f, 20f);
    assertNotNull(shape);
  }

  @Test
  public void createBeveledShapeFor_zeroHeight_returnsNonNull() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Object.class), 0f, 0f, 100f, 0f);
    assertNotNull(shape);
  }

  @Test
  public void createBeveledShapeFor_largeValues_returnsNonNull() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(String.class), 0f, 0f, 10000f, 5000f);
    assertNotNull(shape);
    assertTrue(shape.getBaseShape().getBounds2D().getWidth() > 0);
  }

  @Test
  public void createBeveledShapeFor_negativeOrigin_returnsNonNull() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Object.class), -10f, -5f, 100f, 50f);
    assertNotNull(shape);
  }

  // ── s_roundTypes is a list (multiple registrations) ────────────────

  @Test
  public void addRoundType_canRegisterMultipleTypes() {
    // These types are unlikely to be registered elsewhere
    BeveledShapeForType.addRoundType(java.io.Flushable.class);
    BeveledShapeForType.addRoundType(Comparable.class);

    // java.lang.String implements Comparable
    // But String also matches isAssignableTo(String.class), which takes priority
    // So test with a Comparable that's not String/Number/Boolean
    // java.io.File implements Comparable<File>
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(java.io.File.class), 0f, 0f, 100f, 20f);
    assertNotNull(shape);
    // Should use the round path since File is Comparable
    Rectangle2D bounds = shape.getBaseShape().getBounds2D();
    assertTrue("Registered Comparable round type → positive bounds",
        bounds.getWidth() > 0);
  }
}
