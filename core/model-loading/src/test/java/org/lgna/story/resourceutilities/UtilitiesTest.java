package org.lgna.story.resourceutilities;

import org.junit.Test;

import java.nio.ByteOrder;
import java.nio.DoubleBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UtilitiesTest {
  @Test
  public void createDoubleBufferReturnsNullForNullInput() {
    assertNull(Utilities.createDoubleBuffer(null));
  }

  @Test
  public void createDoubleBufferCreatesDirectNativeOrderedReadReadyBuffer() {
    DoubleBuffer buffer = Utilities.createDoubleBuffer(new double[] {1.25, -3.5, 9.0});

    assertTrue(buffer.isDirect());
    assertEquals(ByteOrder.nativeOrder(), buffer.order());
    assertEquals(0, buffer.position());
    assertEquals(3, buffer.limit());
    assertEquals(1.25, buffer.get(0), 0.0);
    assertEquals(-3.5, buffer.get(1), 0.0);
    assertEquals(9.0, buffer.get(2), 0.0);
  }

  @Test
  public void createDoubleBufferCopiesInputValues() {
    double[] values = new double[] {2.0, 4.0};
    DoubleBuffer buffer = Utilities.createDoubleBuffer(values);
    values[0] = 99.0;

    assertEquals(2.0, buffer.get(0), 0.0);
    assertEquals(4.0, buffer.get(1), 0.0);
  }
}
