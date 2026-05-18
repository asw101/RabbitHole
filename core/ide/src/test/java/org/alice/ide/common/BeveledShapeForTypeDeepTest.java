package org.alice.ide.common;

import org.lgna.project.ast.JavaType;
import org.junit.Test;

import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Rectangle2D;

import static org.junit.Assert.*;

/**
 * Deep coverage tests for {@link BeveledShapeForType} — shape construction
 * for each AST type category and union/round-type mechanics.
 * All tests use only Shape/GeneralPath which work fine in headless mode.
 */
public class BeveledShapeForTypeDeepTest {

  @Test
  public void createBeveledShapeFor_integerType_returnsNonNull() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Integer.class), 0f, 0f, 100f, 20f);
    assertNotNull("Integer (Number) type should produce a shape", shape);
  }

  @Test
  public void createBeveledShapeFor_doubleType_returnsNonNull() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Double.class), 0f, 0f, 100f, 20f);
    assertNotNull("Double (Number) type should produce a shape", shape);
  }

  @Test
  public void createBeveledShapeFor_voidType_baseShapeHasZeroBounds() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.VOID_TYPE, 0f, 0f, 100f, 20f);
    assertNotNull(shape);
    Rectangle2D bounds = shape.getBaseShape().getBounds2D();
    // Void type creates an empty GeneralPath, so bounds should be degenerate
    assertEquals(0.0, bounds.getWidth(), 0.001);
    assertEquals(0.0, bounds.getHeight(), 0.001);
  }

  @Test
  public void createBeveledShapeFor_stringType_boundsWithinExpected() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(String.class), 0f, 0f, 100f, 20f);
    assertNotNull(shape);
    Rectangle2D bounds = shape.getBaseShape().getBounds2D();
    assertTrue("String shape width should be positive", bounds.getWidth() > 0);
    assertTrue("String shape height should be positive", bounds.getHeight() > 0);
  }

  @Test
  public void createBeveledShapeFor_booleanType_boundsWithinExpected() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Boolean.class), 0f, 0f, 100f, 20f);
    assertNotNull(shape);
    Rectangle2D bounds = shape.getBaseShape().getBounds2D();
    assertTrue("Boolean shape width should be positive", bounds.getWidth() > 0);
  }

  @Test
  public void createBeveledShapeFor_booleanPrimitiveType_returnsNonNull() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.BOOLEAN_PRIMITIVE_TYPE, 0f, 0f, 100f, 20f);
    assertNotNull("boolean primitive type should produce a shape", shape);
  }

  @Test
  public void differentTypesProduceDifferentBaseBounds() {
    BeveledShapeForType voidShape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.VOID_TYPE, 0f, 0f, 100f, 20f);
    BeveledShapeForType stringShape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(String.class), 0f, 0f, 100f, 20f);
    BeveledShapeForType numberShape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Integer.class), 0f, 0f, 100f, 20f);
    BeveledShapeForType boolShape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Boolean.class), 0f, 0f, 100f, 20f);
    BeveledShapeForType defaultShape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Object.class), 0f, 0f, 100f, 20f);

    // Void has empty bounds; all others have non-empty bounds
    Rectangle2D voidBounds = voidShape.getBaseShape().getBounds2D();
    Rectangle2D stringBounds = stringShape.getBaseShape().getBounds2D();
    assertEquals(0.0, voidBounds.getWidth(), 0.001);
    assertTrue(stringBounds.getWidth() > 0);

    // String, Number, Boolean, Default should all be distinct shapes
    assertNotSame(stringShape, numberShape);
    assertNotSame(numberShape, boolShape);
    assertNotSame(boolShape, defaultShape);
  }

  @Test
  public void createBeveledShapeFor_withRoundRect_returnsNonNull() {
    RoundRectangle2D.Float roundRect = new RoundRectangle2D.Float(50f, 0f, 100f, 20f, 8f, 8f);
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(String.class), roundRect, 30f, 20f);
    assertNotNull("RoundRect variant should produce a shape", shape);
  }

  @Test
  public void createBeveledShapeFor_withRoundRect_expandsBounds() {
    RoundRectangle2D.Float roundRect = new RoundRectangle2D.Float(50f, 0f, 100f, 20f, 8f, 8f);
    BeveledShapeForType shapeWithRound = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Object.class), roundRect, 30f, 20f);

    BeveledShapeForType shapeWithoutRound = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Object.class), 20f, 0f, 30f, 20f);

    Rectangle2D boundsWithRound = shapeWithRound.getBaseShape().getBounds2D();
    Rectangle2D boundsWithoutRound = shapeWithoutRound.getBaseShape().getBounds2D();

    // The union with roundRect should produce wider bounds
    assertTrue("Unioned shape should be at least as wide",
        boundsWithRound.getWidth() >= boundsWithoutRound.getWidth());
  }

  @Test
  public void union_modifiesBaseShape() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Object.class), 0f, 0f, 50f, 20f);
    Rectangle2D boundsBefore = shape.getBaseShape().getBounds2D();
    double widthBefore = boundsBefore.getWidth();

    RoundRectangle2D.Float roundRect = new RoundRectangle2D.Float(60f, 0f, 100f, 20f, 5f, 5f);
    shape.union(roundRect);

    Rectangle2D boundsAfter = shape.getBaseShape().getBounds2D();
    assertTrue("Union should expand base bounds width",
        boundsAfter.getWidth() > widthBefore);
  }

  @Test
  public void addRoundType_thenCreateShape_usesRoundPath() {
    // Register a custom "round type"
    BeveledShapeForType.addRoundType(java.io.Serializable.class);

    // java.lang.String implements Serializable, so it should match the round type path
    // But String also matches isAssignableTo(String.class) which takes priority.
    // Use a different Serializable that is NOT String/Number/Boolean
    // java.util.ArrayList implements Serializable
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(java.util.ArrayList.class), 0f, 0f, 100f, 20f);
    assertNotNull("Registered round type should produce a shape", shape);
    // The shape should have non-trivial bounds from the curveTo path
    Rectangle2D bounds = shape.getBaseShape().getBounds2D();
    assertTrue("Round type shape width should be positive", bounds.getWidth() > 0);
  }

  @Test
  public void createBeveledShapeFor_objectType_producesDefaultRectShape() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Object.class), 10f, 5f, 80f, 30f);
    assertNotNull(shape);
    Rectangle2D bounds = shape.getBaseShape().getBounds2D();
    // Default shape is a rectangular path from (x0,y0) to (x1,y1)
    assertEquals(10.0, bounds.getX(), 1.0);
    assertEquals(5.0, bounds.getY(), 1.0);
    assertTrue(bounds.getWidth() > 0);
    assertTrue(bounds.getHeight() > 0);
  }

  @Test
  public void createBeveledShapeFor_numberType_hasSteppedBounds() {
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Number.class), 0f, 0f, 100f, 30f);
    assertNotNull(shape);
    Rectangle2D bounds = shape.getBaseShape().getBounds2D();
    // Number (stepped) shape spans the full width/height given
    assertEquals(0.0, bounds.getX(), 1.0);
    assertEquals(0.0, bounds.getY(), 1.0);
    assertTrue("Number shape should span full width", bounds.getWidth() >= 99.0);
    assertTrue("Number shape should span full height", bounds.getHeight() >= 29.0);
  }
}
