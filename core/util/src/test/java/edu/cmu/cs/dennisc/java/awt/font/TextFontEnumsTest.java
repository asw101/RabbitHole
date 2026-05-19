package edu.cmu.cs.dennisc.java.awt.font;

import org.junit.Test;

import static org.junit.Assert.*;

public class TextFontEnumsTest {

  @Test
  public void textFamily_hasThreeValues() {
    assertEquals(3, TextFamily.values().length);
  }

  @Test
  public void textFamily_serif_getValue() {
    assertEquals("Serif", TextFamily.SERIF.getValue());
  }

  @Test
  public void textFamily_sansSerif_getValue() {
    assertEquals("SansSerif", TextFamily.SANS_SERIF.getValue());
  }

  @Test
  public void textFamily_monospaced_getValue() {
    assertEquals("Monospaced", TextFamily.MONOSPACED.getValue());
  }

  @Test
  public void textFamily_serif_getKey() {
    assertSame(java.awt.font.TextAttribute.FAMILY, TextFamily.SERIF.getKey());
  }

  @Test
  public void textFamily_allHaveSameKey() {
    for (TextFamily family : TextFamily.values()) {
      assertSame(java.awt.font.TextAttribute.FAMILY, family.getKey());
    }
  }

  @Test
  public void textFamily_allValuesRoundTripByName() {
    for (TextFamily family : TextFamily.values()) {
      assertSame(family, TextFamily.valueOf(family.name()));
      assertNotNull(family.getValue());
    }
  }

  @Test
  public void textPosture_hasTwoValues() {
    assertEquals(2, TextPosture.values().length);
  }

  @Test
  public void textPosture_regular_getValue() {
    assertEquals(java.awt.font.TextAttribute.POSTURE_REGULAR, TextPosture.REGULAR.getValue());
  }

  @Test
  public void textPosture_oblique_getValue() {
    assertEquals(java.awt.font.TextAttribute.POSTURE_OBLIQUE, TextPosture.OBLIQUE.getValue());
  }

  @Test
  public void textPosture_getKey() {
    assertSame(java.awt.font.TextAttribute.POSTURE, TextPosture.REGULAR.getKey());
  }

  @Test
  public void textPosture_allValuesHaveSameKey() {
    for (TextPosture posture : TextPosture.values()) {
      assertSame(java.awt.font.TextAttribute.POSTURE, posture.getKey());
    }
  }

  @Test
  public void textWeight_hasElevenValues() {
    assertEquals(11, TextWeight.values().length);
  }

  @Test
  public void textWeight_regular_getValue() {
    assertEquals(java.awt.font.TextAttribute.WEIGHT_REGULAR, TextWeight.REGULAR.getValue());
  }

  @Test
  public void textWeight_bold_getValue() {
    assertEquals(java.awt.font.TextAttribute.WEIGHT_BOLD, TextWeight.BOLD.getValue());
  }

  @Test
  public void textWeight_getKey() {
    assertSame(java.awt.font.TextAttribute.WEIGHT, TextWeight.REGULAR.getKey());
  }

  @Test
  public void textWeight_allHaveSameKey() {
    for (TextWeight weight : TextWeight.values()) {
      assertSame(java.awt.font.TextAttribute.WEIGHT, weight.getKey());
      assertNotNull(weight.getValue());
    }
  }

  @Test
  public void textWeight_extraLight_getValue() {
    assertEquals(java.awt.font.TextAttribute.WEIGHT_EXTRA_LIGHT, TextWeight.EXTRA_LIGHT.getValue());
  }

  @Test
  public void textWeight_heavy_getValue() {
    assertEquals(java.awt.font.TextAttribute.WEIGHT_HEAVY, TextWeight.HEAVY.getValue());
  }

  @Test
  public void textWeight_allValuesRoundTripByName() {
    for (TextWeight weight : TextWeight.values()) {
      assertSame(weight, TextWeight.valueOf(weight.name()));
    }
  }

  @Test
  public void textWidth_hasFiveValues() {
    assertEquals(5, TextWidth.values().length);
  }

  @Test
  public void textWidth_regular_getValue() {
    assertEquals(java.awt.font.TextAttribute.WIDTH_REGULAR, TextWidth.REGULAR.getValue());
  }

  @Test
  public void textWidth_condensed_getValue() {
    assertEquals(java.awt.font.TextAttribute.WIDTH_CONDENSED, TextWidth.CONDENSED.getValue());
  }

  @Test
  public void textWidth_extended_getValue() {
    assertEquals(java.awt.font.TextAttribute.WIDTH_EXTENDED, TextWidth.EXTENDED.getValue());
  }

  @Test
  public void textWidth_getKey() {
    assertSame(java.awt.font.TextAttribute.WIDTH, TextWidth.REGULAR.getKey());
  }

  @Test
  public void textWidth_allHaveSameKey() {
    for (TextWidth width : TextWidth.values()) {
      assertSame(java.awt.font.TextAttribute.WIDTH, width.getKey());
      assertNotNull(width.getValue());
    }
  }

  @Test
  public void textWidth_allValuesRoundTripByName() {
    for (TextWidth width : TextWidth.values()) {
      assertSame(width, TextWidth.valueOf(width.name()));
    }
  }

  @Test
  public void textFamily_valueOf() {
    assertSame(TextFamily.SERIF, TextFamily.valueOf("SERIF"));
  }

  @Test
  public void textPosture_valueOf() {
    assertSame(TextPosture.OBLIQUE, TextPosture.valueOf("OBLIQUE"));
  }

  @Test
  public void textWeight_valueOf() {
    assertSame(TextWeight.BOLD, TextWeight.valueOf("BOLD"));
  }

  @Test
  public void textWidth_valueOf() {
    assertSame(TextWidth.CONDENSED, TextWidth.valueOf("CONDENSED"));
  }
}
