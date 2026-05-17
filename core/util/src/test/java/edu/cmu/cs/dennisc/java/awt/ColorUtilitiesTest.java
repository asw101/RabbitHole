package edu.cmu.cs.dennisc.java.awt;

import edu.cmu.cs.dennisc.color.Color4f;
import org.junit.Test;

import java.awt.Color;

import static org.junit.Assert.*;

public class ColorUtilitiesTest {

  @Test
  public void interpolate_atZero_returnsA() {
    Color a = Color.RED;
    Color b = Color.BLUE;
    Color result = ColorUtilities.interpolate(a, b, 0.0f);
    assertEquals(a.getRed(), result.getRed());
    assertEquals(a.getGreen(), result.getGreen());
    assertEquals(a.getBlue(), result.getBlue());
  }

  @Test
  public void interpolate_atOne_returnsB() {
    Color a = Color.RED;
    Color b = Color.BLUE;
    Color result = ColorUtilities.interpolate(a, b, 1.0f);
    assertEquals(b.getRed(), result.getRed());
    assertEquals(b.getGreen(), result.getGreen());
    assertEquals(b.getBlue(), result.getBlue());
  }

  @Test
  public void interpolate_atHalf() {
    Color a = new Color(0, 0, 0);
    Color b = new Color(255, 255, 255);
    Color result = ColorUtilities.interpolate(a, b, 0.5f);
    assertTrue(Math.abs(result.getRed() - 128) <= 1);
    assertTrue(Math.abs(result.getGreen() - 128) <= 1);
    assertTrue(Math.abs(result.getBlue() - 128) <= 1);
  }

  @Test
  public void shiftHSB_noShift_returnsSameColor() {
    Color c = Color.RED;
    Color result = ColorUtilities.shiftHSB(c, 0, 0, 0);
    assertEquals(c.getRGB(), result.getRGB());
  }

  @Test
  public void shiftHSB_brightnessDown() {
    Color c = Color.RED;
    Color result = ColorUtilities.shiftHSB(c, 0, 0, -0.5);
    assertTrue(ColorUtilities.getBrightness(result) < ColorUtilities.getBrightness(c));
  }

  @Test
  public void scaleHSB_identity() {
    Color c = Color.RED;
    Color result = ColorUtilities.scaleHSB(c, 1.0, 1.0, 1.0);
    assertEquals(c.getRGB(), result.getRGB());
  }

  @Test
  public void scaleHSB_halfBrightness() {
    Color c = Color.WHITE;
    Color result = ColorUtilities.scaleHSB(c, 1.0, 1.0, 0.5);
    assertTrue(ColorUtilities.getBrightness(result) < 0.6f);
  }

  @Test
  public void getBrightness_white() {
    float b = ColorUtilities.getBrightness(Color.WHITE);
    assertEquals(1.0f, b, 0.01f);
  }

  @Test
  public void getBrightness_black() {
    float b = ColorUtilities.getBrightness(Color.BLACK);
    assertEquals(0.0f, b, 0.01f);
  }

  @Test
  public void toHashText_red() {
    String hash = ColorUtilities.toHashText(Color.RED);
    assertTrue(hash.startsWith("#"));
    assertEquals("#ff0000", hash);
  }

  @Test
  public void toHashText_black() {
    String hash = ColorUtilities.toHashText(Color.BLACK);
    assertEquals("#0", hash);
  }

  @Test
  public void toAwtColor_null_returnsNull() {
    assertNull(ColorUtilities.toAwtColor(null));
  }

  @Test
  public void toAwtColor_nan_returnsNull() {
    Color4f nan = new Color4f(Float.NaN, 0, 0, 1);
    assertNull(ColorUtilities.toAwtColor(nan));
  }

  @Test
  public void toAwtColor_valid() {
    Color4f c = new Color4f(1.0f, 0.0f, 0.0f, 1.0f);
    Color result = ColorUtilities.toAwtColor(c);
    assertNotNull(result);
    assertEquals(255, result.getRed());
    assertEquals(0, result.getGreen());
    assertEquals(0, result.getBlue());
  }

  @Test
  public void garishColor_isMagenta() {
    assertEquals(Color.MAGENTA, ColorUtilities.GARISH_COLOR);
  }
}
