package org.lgna.story;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TextScaleTest {
  @Test
  public void defaultValueIsOne() {
    assertEquals(1.0, ((Number) TextScale.getDefaultValue()).doubleValue(), 1e-6);
  }

  @Test
  public void getValueReturnsFirstTextScaleDetail() {
    Object[] details = {new TextScale(1.5), new TextScale(2.0)};

    assertEquals(1.5, TextScale.getValue(details), 1e-6);
  }

  @Test
  public void getValueFallsBackToDefaultWhenMissing() {
    Object[] details = {Color.RED, "not-a-scale"};

    assertEquals(1.0, TextScale.getValue(details), 1e-6);
  }
}
