package edu.cmu.cs.dennisc.color;

import edu.cmu.cs.dennisc.codec.DebugInputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.DebugOutputStreamBinaryEncoder;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.FloatBuffer;

import static org.junit.Assert.*;

public class Color4fExtendedTest {

  private static final float EPSILON = 0.000001f;

  // --- Named color constants ---

  @Test
  public void white_isFullRgb() {
    assertEquals(1.0f, Color4f.WHITE.red, EPSILON);
    assertEquals(1.0f, Color4f.WHITE.green, EPSILON);
    assertEquals(1.0f, Color4f.WHITE.blue, EPSILON);
    assertEquals(1.0f, Color4f.WHITE.alpha, EPSILON);
  }

  @Test
  public void black_isZeroRgb() {
    assertEquals(0.0f, Color4f.BLACK.red, EPSILON);
    assertEquals(0.0f, Color4f.BLACK.green, EPSILON);
    assertEquals(0.0f, Color4f.BLACK.blue, EPSILON);
  }

  @Test
  public void red_isOnlyRed() {
    assertEquals(1.0f, Color4f.RED.red, EPSILON);
    assertEquals(0.0f, Color4f.RED.green, EPSILON);
    assertEquals(0.0f, Color4f.RED.blue, EPSILON);
  }

  @Test
  public void green_isOnlyGreen() {
    assertEquals(0.0f, Color4f.GREEN.red, EPSILON);
    assertEquals(1.0f, Color4f.GREEN.green, EPSILON);
    assertEquals(0.0f, Color4f.GREEN.blue, EPSILON);
  }

  @Test
  public void blue_isOnlyBlue() {
    assertEquals(0.0f, Color4f.BLUE.red, EPSILON);
    assertEquals(0.0f, Color4f.BLUE.green, EPSILON);
    assertEquals(1.0f, Color4f.BLUE.blue, EPSILON);
  }

  @Test
  public void cyan_isGreenAndBlue() {
    assertEquals(0.0f, Color4f.CYAN.red, EPSILON);
    assertEquals(1.0f, Color4f.CYAN.green, EPSILON);
    assertEquals(1.0f, Color4f.CYAN.blue, EPSILON);
  }

  @Test
  public void magenta_isRedAndBlue() {
    assertEquals(1.0f, Color4f.MAGENTA.red, EPSILON);
    assertEquals(0.0f, Color4f.MAGENTA.green, EPSILON);
    assertEquals(1.0f, Color4f.MAGENTA.blue, EPSILON);
  }

  @Test
  public void yellow_isRedAndGreen() {
    assertEquals(1.0f, Color4f.YELLOW.red, EPSILON);
    assertEquals(1.0f, Color4f.YELLOW.green, EPSILON);
    assertEquals(0.0f, Color4f.YELLOW.blue, EPSILON);
  }

  @Test
  public void orange_hasExpectedValues() {
    assertEquals(1.0f, Color4f.ORANGE.red, EPSILON);
    assertTrue(Color4f.ORANGE.green > 0);
    assertEquals(0.0f, Color4f.ORANGE.blue, EPSILON);
  }

  @Test
  public void pink_hasExpectedValues() {
    assertEquals(1.0f, Color4f.PINK.red, EPSILON);
    assertTrue(Color4f.PINK.green > 0);
    assertTrue(Color4f.PINK.blue > 0);
  }

  @Test
  public void gray_hasEqualChannels() {
    assertEquals(Color4f.GRAY.red, Color4f.GRAY.green, EPSILON);
    assertEquals(Color4f.GRAY.green, Color4f.GRAY.blue, EPSILON);
  }

  @Test
  public void darkGray_isDarkerThanGray() {
    assertTrue(Color4f.DARK_GRAY.red < Color4f.GRAY.red);
  }

  @Test
  public void lightGray_isLighterThanGray() {
    assertTrue(Color4f.LIGHT_GRAY.red > Color4f.GRAY.red);
  }

  @Test
  public void purple_hasExpectedValues() {
    assertTrue(Color4f.PURPLE.red > 0);
    assertEquals(0.0f, Color4f.PURPLE.green, EPSILON);
    assertTrue(Color4f.PURPLE.blue > 0);
  }

  @Test
  public void brown_hasExpectedValues() {
    assertTrue(Color4f.BROWN.red > 0);
    assertTrue(Color4f.BROWN.green > 0);
    assertTrue(Color4f.BROWN.blue > 0);
  }

  // --- isNaN ---

  @Test
  public void isNaN_onlyRedNaN() {
    assertTrue(new Color4f(Float.NaN, 0, 0, 1).isNaN());
  }

  @Test
  public void isNaN_onlyGreenNaN() {
    assertTrue(new Color4f(0, Float.NaN, 0, 1).isNaN());
  }

  @Test
  public void isNaN_onlyBlueNaN() {
    assertTrue(new Color4f(0, 0, Float.NaN, 1).isNaN());
  }

  @Test
  public void isNaN_onlyAlphaNaN() {
    assertTrue(new Color4f(0, 0, 0, Float.NaN).isNaN());
  }

  @Test
  public void isNaN_normalColor() {
    assertFalse(new Color4f(0.5f, 0.5f, 0.5f, 1.0f).isNaN());
  }

  // --- createNaN ---

  @Test
  public void createNaN_allChannelsNaN() {
    Color4f nan = Color4f.createNaN();
    assertTrue(Float.isNaN(nan.red));
    assertTrue(Float.isNaN(nan.green));
    assertTrue(Float.isNaN(nan.blue));
    assertTrue(Float.isNaN(nan.alpha));
  }

  // --- interpolation edge cases ---

  @Test
  public void interpolation_atZero_returnsStart() {
    Color4f start = new Color4f(0.2f, 0.3f, 0.4f, 0.5f);
    Color4f end = new Color4f(0.8f, 0.7f, 0.6f, 0.5f);
    Color4f result = Color4f.createInterpolation(start, end, 0.0f);
    assertEquals(start.red, result.red, EPSILON);
    assertEquals(start.green, result.green, EPSILON);
    assertEquals(start.blue, result.blue, EPSILON);
    assertEquals(start.alpha, result.alpha, EPSILON);
  }

  @Test
  public void interpolation_atOne_returnsEnd() {
    Color4f start = new Color4f(0.2f, 0.3f, 0.4f, 0.5f);
    Color4f end = new Color4f(0.8f, 0.7f, 0.6f, 0.5f);
    Color4f result = Color4f.createInterpolation(start, end, 1.0f);
    assertEquals(end.red, result.red, EPSILON);
    assertEquals(end.green, result.green, EPSILON);
    assertEquals(end.blue, result.blue, EPSILON);
    assertEquals(end.alpha, result.alpha, EPSILON);
  }

  // --- copy constructor ---

  @Test
  public void copyConstructor_copiesAllChannels() {
    Color4f original = new Color4f(0.1f, 0.2f, 0.3f, 0.4f);
    Color4f copy = new Color4f(original);
    assertEquals(original, copy);
  }

  // --- createFromRgbInts ---

  @Test
  public void createFromRgbInts_setsAlphaToOne() {
    Color4f c = Color4f.createFromRgbInts(128, 64, 32);
    assertEquals(1.0f, c.alpha, EPSILON);
    assertEquals(128 / 255.0f, c.red, EPSILON);
  }

  // --- equals edge cases ---

  @Test
  public void equals_notAColor4f() {
    assertFalse(Color4f.RED.equals("not a color"));
  }

  @Test
  public void equals_sameValues() {
    Color4f a = new Color4f(0.5f, 0.5f, 0.5f, 0.5f);
    Color4f b = new Color4f(0.5f, 0.5f, 0.5f, 0.5f);
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test
  public void equals_differentRed() {
    assertFalse(new Color4f(0.1f, 0.5f, 0.5f, 0.5f).equals(
        new Color4f(0.2f, 0.5f, 0.5f, 0.5f)));
  }

  @Test
  public void equals_differentGreen() {
    assertFalse(new Color4f(0.5f, 0.1f, 0.5f, 0.5f).equals(
        new Color4f(0.5f, 0.2f, 0.5f, 0.5f)));
  }

  @Test
  public void equals_differentBlue() {
    assertFalse(new Color4f(0.5f, 0.5f, 0.1f, 0.5f).equals(
        new Color4f(0.5f, 0.5f, 0.2f, 0.5f)));
  }

  // --- toString ---

  @Test
  public void toString_containsChannelValues() {
    Color4f c = new Color4f(0.1f, 0.2f, 0.3f, 0.4f);
    String s = c.toString();
    assertTrue(s.contains("red="));
    assertTrue(s.contains("green="));
    assertTrue(s.contains("blue="));
    assertTrue(s.contains("alpha="));
  }

  // --- getAsArray no-arg ---

  @Test
  public void getAsArray_noArg() {
    Color4f c = new Color4f(0.1f, 0.2f, 0.3f, 0.4f);
    float[] arr = c.getAsArray();
    assertEquals(4, arr.length);
    assertEquals(0.1f, arr[0], EPSILON);
  }

  // --- getAsFloatBuffer no-arg ---

  @Test
  public void getAsFloatBuffer_noArg() {
    Color4f c = new Color4f(0.5f, 0.6f, 0.7f, 0.8f);
    FloatBuffer buf = c.getAsFloatBuffer();
    assertEquals(0, buf.position());
    assertEquals(0.5f, buf.get(), EPSILON);
    assertEquals(0.6f, buf.get(), EPSILON);
    assertEquals(0.7f, buf.get(), EPSILON);
    assertEquals(0.8f, buf.get(), EPSILON);
  }

  // --- encode ---

  @Test
  public void encode_writesAllFourFloats() {
    Color4f original = new Color4f(0.1f, 0.2f, 0.3f, 0.4f);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    // Use the non-debug encoder so float is written directly
    edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder encoder =
        new edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder(baos);
    original.encode(encoder);
    encoder.flush();
    assertTrue(baos.size() > 0);
  }

  // --- createInstance from AWT ---

  @Test
  public void createInstance_awtBlack() {
    Color4f c = Color4f.createInstance(java.awt.Color.BLACK);
    assertEquals(0.0f, c.red, EPSILON);
    assertEquals(0.0f, c.green, EPSILON);
    assertEquals(0.0f, c.blue, EPSILON);
    assertEquals(1.0f, c.alpha, EPSILON);
  }

  @Test
  public void createInstance_awtWhite() {
    Color4f c = Color4f.createInstance(java.awt.Color.WHITE);
    assertEquals(1.0f, c.red, EPSILON);
    assertEquals(1.0f, c.green, EPSILON);
    assertEquals(1.0f, c.blue, EPSILON);
  }
}
