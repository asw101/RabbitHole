package org.alice.imageeditor.croquet;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ToolTest {
  @Test
  public void valuesAndOrdinalsMatchBaseline() {
    assertArrayEquals(new Tool[] {Tool.ADD_RECTANGLE, Tool.CROP_SELECT}, Tool.values());
    assertSame(Tool.ADD_RECTANGLE, Tool.valueOf("ADD_RECTANGLE"));
    assertSame(Tool.CROP_SELECT, Tool.valueOf("CROP_SELECT"));
    assertEquals(0, Tool.ADD_RECTANGLE.ordinal());
    assertEquals(1, Tool.CROP_SELECT.ordinal());
  }
}
