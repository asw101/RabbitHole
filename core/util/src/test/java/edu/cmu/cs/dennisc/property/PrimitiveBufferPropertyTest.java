package edu.cmu.cs.dennisc.property;

import edu.cmu.cs.dennisc.property.event.*;
import org.junit.Assert;
import org.junit.Test;

import java.nio.*;

public class PrimitiveBufferPropertyTest {
  private static class StubOwner implements InstancePropertyOwner {
    @Override public Iterable<InstanceProperty<?>> getProperties() { return java.util.Collections.emptyList(); }
    @Override public InstanceProperty<?> getPropertyNamed(String name) { return null; }
    @Override public String lookupNameFor(InstanceProperty<?> ip) { return "bufferProperty"; }
    @Override public void firePropertyChanging(PropertyEvent e) { }
    @Override public void firePropertyChanged(PropertyEvent e) { }
    @Override public void fireAdding(AddListPropertyEvent<?> e) { }
    @Override public void fireAdded(AddListPropertyEvent<?> e) { }
    @Override public void fireClearing(ClearListPropertyEvent<?> e) { }
    @Override public void fireCleared(ClearListPropertyEvent<?> e) { }
    @Override public void fireRemoving(RemoveListPropertyEvent<?> e) { }
    @Override public void fireRemoved(RemoveListPropertyEvent<?> e) { }
    @Override public void fireSetting(SetListPropertyEvent<?> e) { }
    @Override public void fireSet(SetListPropertyEvent<?> e) { }
  }

  @Test
  public void byteCharAndShortBufferPropertiesWrapPrimitiveArrays() {
    StubOwner owner = new StubOwner();
    ByteBufferProperty byteProperty = new ByteBufferProperty(owner, new byte[] {1, 2});
    byteProperty.setValue(new byte[] {3, 4});
    CharBufferProperty charProperty = new CharBufferProperty(owner, new char[] {'a', 'b'});
    charProperty.setValue(new char[] {'c'});
    ShortBufferProperty shortProperty = new ShortBufferProperty(owner, new short[] {5, 6});
    shortProperty.setValue(new short[] {7});

    Assert.assertArrayEquals(new byte[] {3, 4}, toByteArray(byteProperty.getValue()));
    Assert.assertArrayEquals(new char[] {'c'}, toCharArray(charProperty.getValue()));
    Assert.assertArrayEquals(new short[] {7}, toShortArray(shortProperty.getValue()));
  }

  @Test
  public void intLongFloatAndDoubleBufferPropertiesWrapPrimitiveArrays() {
    StubOwner owner = new StubOwner();
    IntBufferProperty intProperty = new IntBufferProperty(owner, new int[] {1, 2});
    intProperty.setValue(new int[] {3, 4, 5});
    LongBufferProperty longProperty = new LongBufferProperty(owner, new long[] {6L});
    longProperty.setValue(new long[] {7L, 8L});
    FloatBufferProperty floatProperty = new FloatBufferProperty(owner, new float[] {1.5f});
    floatProperty.setValue(new float[] {2.5f, 3.5f});
    DoubleBufferProperty doubleProperty = new DoubleBufferProperty(owner, new double[] {1.5});
    doubleProperty.setValue(new double[] {2.5, 3.5});

    Assert.assertArrayEquals(new int[] {3, 4, 5}, toIntArray(intProperty.getValue()));
    Assert.assertArrayEquals(new long[] {7L, 8L}, toLongArray(longProperty.getValue()));
    Assert.assertArrayEquals(new float[] {2.5f, 3.5f}, toFloatArray(floatProperty.getValue()), 0.0f);
    Assert.assertArrayEquals(new double[] {2.5, 3.5}, toDoubleArray(doubleProperty.getValue()), 0.0);
  }

  private static byte[] toByteArray(ByteBuffer buffer) {
    ByteBuffer copy = buffer.duplicate();
    byte[] values = new byte[copy.remaining()];
    copy.get(values);
    return values;
  }

  private static char[] toCharArray(CharBuffer buffer) {
    CharBuffer copy = buffer.duplicate();
    char[] values = new char[copy.remaining()];
    copy.get(values);
    return values;
  }

  private static short[] toShortArray(ShortBuffer buffer) {
    ShortBuffer copy = buffer.duplicate();
    short[] values = new short[copy.remaining()];
    copy.get(values);
    return values;
  }

  private static int[] toIntArray(IntBuffer buffer) {
    IntBuffer copy = buffer.duplicate();
    int[] values = new int[copy.remaining()];
    copy.get(values);
    return values;
  }

  private static long[] toLongArray(LongBuffer buffer) {
    LongBuffer copy = buffer.duplicate();
    long[] values = new long[copy.remaining()];
    copy.get(values);
    return values;
  }

  private static float[] toFloatArray(FloatBuffer buffer) {
    FloatBuffer copy = buffer.duplicate();
    float[] values = new float[copy.remaining()];
    copy.get(values);
    return values;
  }

  private static double[] toDoubleArray(DoubleBuffer buffer) {
    DoubleBuffer copy = buffer.duplicate();
    double[] values = new double[copy.remaining()];
    copy.get(values);
    return values;
  }
}
