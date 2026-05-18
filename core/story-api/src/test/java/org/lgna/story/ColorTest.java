package org.lgna.story;

import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.color.property.Color4fProperty;
import org.junit.Test;

import static org.junit.Assert.*;

public class ColorTest {

  @Test
  public void colorConstantsAreNonNull() {
    assertNotNull(Color.BLACK);
    assertNotNull(Color.BLUE);
    assertNotNull(Color.CYAN);
    assertNotNull(Color.DARK_GRAY);
    assertNotNull(Color.GRAY);
    assertNotNull(Color.GREEN);
    assertNotNull(Color.LIGHT_GRAY);
    assertNotNull(Color.MAGENTA);
    assertNotNull(Color.ORANGE);
    assertNotNull(Color.PINK);
    assertNotNull(Color.RED);
    assertNotNull(Color.WHITE);
    assertNotNull(Color.YELLOW);
    assertNotNull(Color.LIGHT_BLUE);
    assertNotNull(Color.DARK_BLUE);
    assertNotNull(Color.PURPLE);
    assertNotNull(Color.BROWN);
  }

  @Test
  public void blackComponentsAreZero() {
    assertEquals(0.0, Color.BLACK.getRed(), 1e-6);
    assertEquals(0.0, Color.BLACK.getGreen(), 1e-6);
    assertEquals(0.0, Color.BLACK.getBlue(), 1e-6);
  }

  @Test
  public void whiteComponentsAreOne() {
    assertEquals(1.0, Color.WHITE.getRed(), 1e-6);
    assertEquals(1.0, Color.WHITE.getGreen(), 1e-6);
    assertEquals(1.0, Color.WHITE.getBlue(), 1e-6);
  }

  @Test
  public void lightBlueMatchesDeclaredFractions() {
    assertEquals(149.0 / 255.0, Color.LIGHT_BLUE.getRed(), 1e-6);
    assertEquals(166.0 / 255.0, Color.LIGHT_BLUE.getGreen(), 1e-6);
    assertEquals(216.0 / 255.0, Color.LIGHT_BLUE.getBlue(), 1e-6);
  }

  @Test
  public void darkBlueMatchesDeclaredFractions() {
    assertEquals(0.0, Color.DARK_BLUE.getRed(), 1e-6);
    assertEquals(0.0, Color.DARK_BLUE.getGreen(), 1e-6);
    assertEquals(150.0 / 255.0, Color.DARK_BLUE.getBlue(), 1e-6);
  }

  @Test
  public void rgbConstructorStoresComponents() {
    Color color = new Color(0.25, 0.50, 0.75);
    assertEquals(0.25, color.getRed(), 1e-6);
    assertEquals(0.50, color.getGreen(), 1e-6);
    assertEquals(0.75, color.getBlue(), 1e-6);
  }

  @Test
  public void rgbConstructorAcceptsIntegralNumbers() {
    Color color = new Color(1, 0, 1);
    assertEquals(1.0, color.getRed(), 1e-6);
    assertEquals(0.0, color.getGreen(), 1e-6);
    assertEquals(1.0, color.getBlue(), 1e-6);
  }

  @Test
  public void createInstanceFromAwtColorRoundTripsChannels() {
    java.awt.Color awt = new java.awt.Color(64, 128, 192);
    Color color = Color.createInstance(awt);
    assertNotNull(color);
    assertEquals(64 / 255.0, color.getRed(), 1e-6);
    assertEquals(128 / 255.0, color.getGreen(), 1e-6);
    assertEquals(192 / 255.0, color.getBlue(), 1e-6);
  }

  @Test
  public void createInstanceFromNullReturnsNull() {
    assertNull(Color.createInstance((java.awt.Color) null));
  }

  @Test
  public void fromPropertyReturnsEquivalentColor() {
    Color4fProperty property = new Color4fProperty(null, Color4f.RED);
    property.setValue(new Color4f(0.1f, 0.2f, 0.3f, 1.0f));

    Color color = Color.fromProperty(property);

    assertEquals(0.1, color.getRed(), 1e-6);
    assertEquals(0.2, color.getGreen(), 1e-6);
    assertEquals(0.3, color.getBlue(), 1e-6);
  }

  @Test
  public void toColor4fMatchesGetters() {
    Color color = new Color(0.2, 0.4, 0.6);
    Color4f color4f = color.toColor4f();
    assertEquals((float) color.getRed().doubleValue(), color4f.red, 1e-6f);
    assertEquals((float) color.getGreen().doubleValue(), color4f.green, 1e-6f);
    assertEquals((float) color.getBlue().doubleValue(), color4f.blue, 1e-6f);
    assertEquals(1.0f, color4f.alpha, 1e-6f);
  }

  @Test
  public void toAwtColorRoundsExpectedChannels() {
    Color color = new Color(0.2, 0.4, 0.6);
    java.awt.Color awt = color.toAwtColor();
    assertEquals(51, awt.getRed());
    assertEquals(102, awt.getGreen());
    assertEquals(153, awt.getBlue());
  }

  @Test
  public void equalsReturnsTrueForSameComponents() {
    assertEquals(new Color(0.1, 0.2, 0.3), new Color(0.1, 0.2, 0.3));
  }

  @Test
  public void equalsReturnsFalseForDifferentComponents() {
    assertNotEquals(new Color(0.1, 0.2, 0.3), new Color(0.1, 0.2, 0.4));
  }

  @Test
  public void equalsReturnsFalseForDifferentType() {
    assertFalse(new Color(0.1, 0.2, 0.3).equals("not a color"));
  }

  @Test
  public void hashCodeMatchesForEqualColors() {
    Color a = new Color(0.7, 0.1, 0.9);
    Color b = new Color(0.7, 0.1, 0.9);
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test
  public void interpolateToHalfwayProducesAverage() {
    Color start = new Color(0.0, 0.0, 0.0);
    Color end = new Color(1.0, 0.5, 0.25);
    Color mid = start.interpolateTo(end, 0.5);
    assertEquals(0.5, mid.getRed(), 1e-6);
    assertEquals(0.25, mid.getGreen(), 1e-6);
    assertEquals(0.125, mid.getBlue(), 1e-6);
  }

  @Test
  public void interpolateToZeroReturnsStartColor() {
    Color start = new Color(0.3, 0.4, 0.5);
    Color end = new Color(0.9, 0.9, 0.9);
    assertEquals(start, start.interpolateTo(end, 0.0));
  }

  @Test
  public void interpolateToOneReturnsTargetColor() {
    Color start = new Color(0.3, 0.4, 0.5);
    Color end = new Color(0.9, 0.9, 0.9);
    assertEquals(end, start.interpolateTo(end, 1.0));
  }

  @Test
  public void getColor4fOrWhiteReturnsUnderlyingColorForColorPaint() {
    Color color = new Color(0.1, 0.2, 0.3);
    assertEquals(color.toColor4f(), Color.getColor4fOrWhite(color));
  }

  @Test
  public void getColor4fOrWhiteReturnsWhiteForNonColorPaint() {
    Paint paint = new Paint() {
    };
    assertEquals(Color4f.WHITE, Color.getColor4fOrWhite(paint));
  }

  @Test
  public void rgbStringContainsFloatComponents() {
    Color color = new Color(0.25, 0.5, 0.75);
    assertEquals("Color(0.25,0.5,0.75)", color.rgbString());
  }

  @Test
  public void applyToMultipliesRgbChannelsAndPreservesAlpha() {
    Color tint = new Color(0.5, 0.25, 1.0);
    java.awt.Color base = new java.awt.Color(0.8f, 0.4f, 0.2f, 0.6f);

    java.awt.Color result = tint.applyTo(base);

    assertEquals(0.4f, result.getRGBComponents(null)[0], 1e-6f);
    assertEquals(0.1f, result.getRGBComponents(null)[1], 1e-6f);
    assertEquals(0.2f, result.getRGBComponents(null)[2], 1e-6f);
    assertEquals(0.6f, result.getRGBComponents(null)[3], 1e-6f);
  }
}
