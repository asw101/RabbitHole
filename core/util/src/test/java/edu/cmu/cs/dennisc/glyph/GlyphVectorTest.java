package edu.cmu.cs.dennisc.glyph;

import org.alice.math.immutable.Point2f;
import org.junit.Test;

import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.List;

import static org.junit.Assert.*;

public class GlyphVectorTest {
  private Font font() {
    return new Font("SansSerif", Font.PLAIN, 24);
  }

  @Test
  public void constructorStoresTextAndFont() {
    GlyphVector glyphVector = new GlyphVector("Alice", font(), 1.0f, 1.0f);
    assertEquals("Alice", glyphVector.getText());
    assertEquals(font(), glyphVector.getFont());
  }

  @Test
  public void facesShapeIsCached() {
    GlyphVector glyphVector = new GlyphVector("Alice", font(), 1.0f, 1.0f);
    Shape first = glyphVector.getFacesShape();
    Shape second = glyphVector.getFacesShape();
    assertSame(first, second);
  }

  @Test
  public void outlinesShapeIsCached() {
    GlyphVector glyphVector = new GlyphVector("Alice", font(), 1.0f, 1.0f);
    Shape first = glyphVector.getOutlinesShape();
    Shape second = glyphVector.getOutlinesShape();
    assertSame(first, second);
  }

  @Test
  public void acquireFaceContoursReturnsNonEmptyCachedContours() {
    GlyphVector glyphVector = new GlyphVector("A", font(), 1.0f, 1.0f);
    List<List<Point2f>> first = glyphVector.acquireFaceContours();
    List<List<Point2f>> second = glyphVector.acquireFaceContours();

    assertFalse(first.isEmpty());
    assertSame(first, second);
    glyphVector.releaseFaceContours();
  }

  @Test
  public void acquireOutlineLinesReturnsNonEmptyCachedLines() {
    GlyphVector glyphVector = new GlyphVector("B", font(), 1.0f, 1.0f);
    List<List<Point2f>> first = glyphVector.acquireOutlineLines();
    List<List<Point2f>> second = glyphVector.acquireOutlineLines();

    assertFalse(first.isEmpty());
    assertSame(first, second);
    glyphVector.releaseOutlineLines();
  }

  @Test
  public void getBoundsForEmptyTextContainsNaN() {
    GlyphVector glyphVector = new GlyphVector("", font(), 1.0f, 1.0f);
    Rectangle2D.Float bounds = glyphVector.getBounds();

    assertTrue(Float.isNaN(bounds.x));
    assertTrue(Float.isNaN(bounds.y));
  }

  @Test
  public void setTextReturnsTrueOnlyWhenValueChangesAndInvalidatesCache() {
    GlyphVector glyphVector = new GlyphVector("A", font(), 1.0f, 1.0f);
    Shape original = glyphVector.getFacesShape();

    assertFalse(glyphVector.setText("A"));
    assertTrue(glyphVector.setText("AB"));
    assertNotSame(original, glyphVector.getFacesShape());
  }

  @Test
  public void setFontReturnsTrueOnlyWhenValueChangesAndInvalidatesCache() {
    GlyphVector glyphVector = new GlyphVector("A", font(), 1.0f, 1.0f);
    Shape original = glyphVector.getOutlinesShape();

    assertFalse(glyphVector.setFont(font()));
    assertTrue(glyphVector.setFont(font().deriveFont(Font.BOLD)));
    assertNotSame(original, glyphVector.getOutlinesShape());
  }

  @Test
  public void scalingFactorsAffectBounds() {
    GlyphVector small = new GlyphVector("Scale", font(), 1.0f, 1.0f);
    GlyphVector large = new GlyphVector("Scale", font(), 2.0f, 3.0f);

    Rectangle2D.Float smallBounds = small.getBounds();
    Rectangle2D.Float largeBounds = large.getBounds();

    assertTrue(largeBounds.width > smallBounds.width);
    assertTrue(largeBounds.height > smallBounds.height);
  }
}
