package org.alice.ide.common;

import org.lgna.project.ast.JavaType;
import org.junit.Assume;
import org.junit.Test;

import java.awt.GraphicsEnvironment;
import java.awt.geom.RoundRectangle2D;

import static org.junit.Assert.*;

public class BeveledShapeForTypeTest {

  @Test
  public void createBeveledShapeForVoidType() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.VOID_TYPE, 0f, 0f, 100f, 20f);
    assertNotNull("Void type should produce a shape", shape);
  }

  @Test
  public void createBeveledShapeForStringType() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(String.class), 0f, 0f, 100f, 20f);
    assertNotNull("String type should produce a shape", shape);
  }

  @Test
  public void createBeveledShapeForNumberType() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Number.class), 0f, 0f, 100f, 20f);
    assertNotNull("Number type should produce a shape", shape);
  }

  @Test
  public void createBeveledShapeForBooleanType() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Boolean.class), 0f, 0f, 100f, 20f);
    assertNotNull("Boolean type should produce a shape", shape);
  }

  @Test
  public void createBeveledShapeForBooleanPrimitiveType() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.BOOLEAN_PRIMITIVE_TYPE, 0f, 0f, 100f, 20f);
    assertNotNull("Boolean primitive type should produce a shape", shape);
  }

  @Test
  public void createBeveledShapeForDefaultType() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    // Object is not String, Number, Boolean, or void -> default shape
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Object.class), 0f, 0f, 100f, 20f);
    assertNotNull("Default type should produce a shape", shape);
  }

  @Test
  public void createBeveledShapeWithRoundRect() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    RoundRectangle2D.Float roundRect = new RoundRectangle2D.Float(50f, 0f, 100f, 20f, 8f, 8f);
    BeveledShapeForType shape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(Object.class), roundRect, 30f, 20f);
    assertNotNull("Round rect variant should produce a shape", shape);
  }

  @Test
  public void differentTypesCategoryReturnDistinctShapes() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    BeveledShapeForType voidShape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.VOID_TYPE, 0f, 0f, 100f, 20f);
    BeveledShapeForType stringShape = BeveledShapeForType.createBeveledShapeFor(
        JavaType.getInstance(String.class), 0f, 0f, 100f, 20f);
    // They should both be non-null and different objects
    assertNotNull(voidShape);
    assertNotNull(stringShape);
    assertNotSame("Different types should produce distinct shapes", voidShape, stringShape);
  }
}
