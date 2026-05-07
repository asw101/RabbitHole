package edu.cmu.cs.dennisc.color;

import org.junit.Test;

import java.nio.FloatBuffer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class Color4fTest {
  private static final float EPSILON = 0.000001f;

  @Test
  public void rgbaIntsAreNormalizedToFloatChannels() {
    Color4f color = Color4f.createFromRgbaInts(64, 128, 192, 255);

    assertEquals(64 / 255.0f, color.red, EPSILON);
    assertEquals(128 / 255.0f, color.green, EPSILON);
    assertEquals(192 / 255.0f, color.blue, EPSILON);
    assertEquals(1.0f, color.alpha, EPSILON);
  }

  @Test
  public void awtColorFactoryPreservesAlphaChannel() {
    Color4f color = Color4f.createInstance(new java.awt.Color(10, 20, 30, 40));

    assertEquals(10 / 255.0f, color.red, EPSILON);
    assertEquals(20 / 255.0f, color.green, EPSILON);
    assertEquals(30 / 255.0f, color.blue, EPSILON);
    assertEquals(40 / 255.0f, color.alpha, EPSILON);
  }

  @Test
  public void interpolationBlendsEveryChannel() {
    Color4f start = new Color4f(0.0f, 0.25f, 0.5f, 0.75f);
    Color4f end = new Color4f(1.0f, 0.75f, 0.0f, 0.25f);

    Color4f midpoint = Color4f.createInterpolation(start, end, 0.5f);

    assertEquals(0.5f, midpoint.red, EPSILON);
    assertEquals(0.5f, midpoint.green, EPSILON);
    assertEquals(0.25f, midpoint.blue, EPSILON);
    assertEquals(0.5f, midpoint.alpha, EPSILON);
  }

  @Test
  public void getAsArrayReusesSuppliedArrayInChannelOrder() {
    Color4f color = new Color4f(0.1f, 0.2f, 0.3f, 0.4f);
    float[] values = new float[4];

    float[] returned = color.getAsArray(values);

    assertSame(values, returned);
    assertArrayEquals(new float[] {0.1f, 0.2f, 0.3f, 0.4f}, values, EPSILON);
  }

  @Test
  public void getAsFloatBufferRewindsAndWritesChannelsInOrder() {
    Color4f color = new Color4f(0.9f, 0.8f, 0.7f, 0.6f);
    FloatBuffer buffer = FloatBuffer.allocate(4);
    buffer.position(2);

    FloatBuffer returned = color.getAsFloatBuffer(buffer);

    assertSame(buffer, returned);
    assertEquals(0, buffer.position());
    assertEquals(0.9f, buffer.get(), EPSILON);
    assertEquals(0.8f, buffer.get(), EPSILON);
    assertEquals(0.7f, buffer.get(), EPSILON);
    assertEquals(0.6f, buffer.get(), EPSILON);
  }

  @Test
  public void equalityAndHashCodeUseAllChannels() {
    Color4f color = new Color4f(0.1f, 0.2f, 0.3f, 0.4f);
    Color4f same = new Color4f(color);
    Color4f differentAlpha = new Color4f(0.1f, 0.2f, 0.3f, 0.5f);

    assertEquals(color, same);
    assertEquals(color.hashCode(), same.hashCode());
    assertFalse(color.equals(differentAlpha));
  }

  @Test
  public void isNaNRecognizesAnyNaNChannel() {
    assertTrue(Color4f.createNaN().isNaN());
    assertFalse(Color4f.RED.isNaN());
  }
}
