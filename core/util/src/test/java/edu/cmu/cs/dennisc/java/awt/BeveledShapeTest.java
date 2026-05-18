package edu.cmu.cs.dennisc.java.awt;

import org.junit.Test;

import java.awt.Rectangle;
import java.awt.geom.GeneralPath;

import static org.junit.Assert.*;

public class BeveledShapeTest {

  private static GeneralPath createPath(float startX, float startY) {
    GeneralPath path = new GeneralPath();
    path.moveTo(startX, startY);
    path.lineTo(startX + 1.0f, startY + 1.0f);
    return path;
  }

  @Test
  public void defaultConstructor_baseShapeIsNull() {
    BeveledShape shape = new BeveledShape();

    assertNull(shape.getBaseShape());
  }

  @Test
  public void defaultConstructor_raisedPathsAreNull() {
    BeveledShape shape = new BeveledShape();

    assertNull(shape.getPathForRaisedHighlight());
    assertNull(shape.getPathForRaisedNeutral());
    assertNull(shape.getPathForRaisedShadow());
  }

  @Test
  public void defaultConstructor_sunkenPathsAreNull() {
    BeveledShape shape = new BeveledShape();

    assertNull(shape.getPathForSunkenHighlight());
    assertNull(shape.getPathForSunkenNeutral());
    assertNull(shape.getPathForSunkenShadow());
  }

  @Test
  public void sevenArgumentConstructor_setsBaseShape() {
    Rectangle base = new Rectangle(1, 2, 3, 4);

    BeveledShape shape = new BeveledShape(base, createPath(1, 1), createPath(2, 2), createPath(3, 3), createPath(4, 4), createPath(5, 5), createPath(6, 6));

    assertSame(base, shape.getBaseShape());
  }

  @Test
  public void sevenArgumentConstructor_setsRaisedPaths() {
    GeneralPath highlight = createPath(1, 1);
    GeneralPath neutral = createPath(2, 2);
    GeneralPath shadow = createPath(3, 3);

    BeveledShape shape = new BeveledShape(new Rectangle(), highlight, neutral, shadow, createPath(4, 4), createPath(5, 5), createPath(6, 6));

    assertSame(highlight, shape.getPathForRaisedHighlight());
    assertSame(neutral, shape.getPathForRaisedNeutral());
    assertSame(shadow, shape.getPathForRaisedShadow());
  }

  @Test
  public void sevenArgumentConstructor_setsSunkenPaths() {
    GeneralPath highlight = createPath(4, 4);
    GeneralPath neutral = createPath(5, 5);
    GeneralPath shadow = createPath(6, 6);

    BeveledShape shape = new BeveledShape(new Rectangle(), createPath(1, 1), createPath(2, 2), createPath(3, 3), highlight, neutral, shadow);

    assertSame(highlight, shape.getPathForSunkenHighlight());
    assertSame(neutral, shape.getPathForSunkenNeutral());
    assertSame(shadow, shape.getPathForSunkenShadow());
  }

  @Test
  public void fourArgumentConstructor_setsRaisedPaths() {
    GeneralPath highlight = createPath(1, 1);
    GeneralPath neutral = createPath(2, 2);
    GeneralPath shadow = createPath(3, 3);

    BeveledShape shape = new BeveledShape(new Rectangle(), highlight, neutral, shadow);

    assertSame(highlight, shape.getPathForRaisedHighlight());
    assertSame(neutral, shape.getPathForRaisedNeutral());
    assertSame(shadow, shape.getPathForRaisedShadow());
  }

  @Test
  public void fourArgumentConstructor_setsSunkenHighlightFromRaisedShadow() {
    GeneralPath shadow = createPath(3, 3);

    BeveledShape shape = new BeveledShape(new Rectangle(), createPath(1, 1), createPath(2, 2), shadow);

    assertSame(shadow, shape.getPathForSunkenHighlight());
  }

  @Test
  public void fourArgumentConstructor_setsSunkenNeutralFromRaisedNeutral() {
    GeneralPath neutral = createPath(2, 2);

    BeveledShape shape = new BeveledShape(new Rectangle(), createPath(1, 1), neutral, createPath(3, 3));

    assertSame(neutral, shape.getPathForSunkenNeutral());
  }

  @Test
  public void fourArgumentConstructor_setsSunkenShadowFromRaisedHighlight() {
    GeneralPath highlight = createPath(1, 1);

    BeveledShape shape = new BeveledShape(new Rectangle(), highlight, createPath(2, 2), createPath(3, 3));

    assertSame(highlight, shape.getPathForSunkenShadow());
  }

  @Test
  public void initializeSevenArguments_replacesAllFields() {
    BeveledShape shape = new BeveledShape();
    Rectangle base = new Rectangle(4, 5, 6, 7);
    GeneralPath raisedHighlight = createPath(1, 1);
    GeneralPath raisedNeutral = createPath(2, 2);
    GeneralPath raisedShadow = createPath(3, 3);
    GeneralPath sunkenHighlight = createPath(4, 4);
    GeneralPath sunkenNeutral = createPath(5, 5);
    GeneralPath sunkenShadow = createPath(6, 6);

    shape.initialize(base, raisedHighlight, raisedNeutral, raisedShadow, sunkenHighlight, sunkenNeutral, sunkenShadow);

    assertSame(base, shape.getBaseShape());
    assertSame(raisedHighlight, shape.getPathForRaisedHighlight());
    assertSame(raisedNeutral, shape.getPathForRaisedNeutral());
    assertSame(raisedShadow, shape.getPathForRaisedShadow());
    assertSame(sunkenHighlight, shape.getPathForSunkenHighlight());
    assertSame(sunkenNeutral, shape.getPathForSunkenNeutral());
    assertSame(sunkenShadow, shape.getPathForSunkenShadow());
  }

  @Test
  public void initializeFourArguments_swapsRaisedAndSunkenPaths() {
    BeveledShape shape = new BeveledShape();
    GeneralPath highlight = createPath(1, 1);
    GeneralPath neutral = createPath(2, 2);
    GeneralPath shadow = createPath(3, 3);

    shape.initialize(new Rectangle(), highlight, neutral, shadow);

    assertSame(highlight, shape.getPathForRaisedHighlight());
    assertSame(neutral, shape.getPathForRaisedNeutral());
    assertSame(shadow, shape.getPathForRaisedShadow());
    assertSame(shadow, shape.getPathForSunkenHighlight());
    assertSame(neutral, shape.getPathForSunkenNeutral());
    assertSame(highlight, shape.getPathForSunkenShadow());
  }

  @Test
  public void initializeFourArguments_preservesBaseShape() {
    BeveledShape shape = new BeveledShape();
    Rectangle base = new Rectangle(7, 8, 9, 10);

    shape.initialize(base, createPath(1, 1), createPath(2, 2), createPath(3, 3));

    assertSame(base, shape.getBaseShape());
  }

  @Test
  public void initialize_canStoreNullPaths() {
    BeveledShape shape = new BeveledShape();

    shape.initialize(new Rectangle(), null, null, null, null, null, null);

    assertNull(shape.getPathForRaisedHighlight());
    assertNull(shape.getPathForRaisedNeutral());
    assertNull(shape.getPathForRaisedShadow());
    assertNull(shape.getPathForSunkenHighlight());
    assertNull(shape.getPathForSunkenNeutral());
    assertNull(shape.getPathForSunkenShadow());
  }
}
