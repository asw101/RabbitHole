package edu.cmu.cs.dennisc.java.awt;

import edu.cmu.cs.dennisc.java.awt.font.TextFamily;
import edu.cmu.cs.dennisc.java.awt.font.TextPosture;
import edu.cmu.cs.dennisc.java.awt.font.TextWeight;
import org.junit.Test;

import java.awt.Font;

import static org.junit.Assert.*;

public class FontUtilitiesTest {
  private static final Font BASE_FONT = new Font("Serif", Font.PLAIN, 12);

  private static void assertFontAttribute(Font font, java.awt.font.TextAttribute attribute, Object expected) {
    assertEquals(expected, font.getAttributes().get(attribute));
  }

  @Test
  public void deriveFont_singleAttribute_nullFont_returnsNull() {
    assertNull(FontUtilities.deriveFont((Font) null, java.awt.font.TextAttribute.WEIGHT, java.awt.font.TextAttribute.WEIGHT_BOLD));
  }

  @Test
  public void deriveFont_twoAttributes_nullFont_returnsNull() {
    assertNull(FontUtilities.deriveFont((Font) null,
            java.awt.font.TextAttribute.WEIGHT, java.awt.font.TextAttribute.WEIGHT_BOLD,
            java.awt.font.TextAttribute.POSTURE, java.awt.font.TextAttribute.POSTURE_OBLIQUE));
  }

  @Test
  public void deriveFont_threeAttributes_nullFont_returnsNull() {
    assertNull(FontUtilities.deriveFont((Font) null,
            java.awt.font.TextAttribute.FAMILY, "Monospaced",
            java.awt.font.TextAttribute.WEIGHT, java.awt.font.TextAttribute.WEIGHT_BOLD,
            java.awt.font.TextAttribute.POSTURE, java.awt.font.TextAttribute.POSTURE_OBLIQUE));
  }

  @Test
  public void deriveFont_varargs_nullFont_returnsNull() {
    assertNull(FontUtilities.deriveFont((Font) null, TextWeight.BOLD, TextPosture.OBLIQUE));
  }

  @Test
  public void scaleFont_nullFont_returnsNull() {
    assertNull(FontUtilities.scaleFont((Font) null, 1.5f));
  }

  @Test
  public void deriveFont_singleAttribute_appliesRequestedAttribute() {
    Font derived = FontUtilities.deriveFont(BASE_FONT, java.awt.font.TextAttribute.WEIGHT, java.awt.font.TextAttribute.WEIGHT_BOLD);

    assertNotNull(derived);
    assertNotSame(BASE_FONT, derived);
    assertFontAttribute(derived, java.awt.font.TextAttribute.WEIGHT, java.awt.font.TextAttribute.WEIGHT_BOLD);
  }

  @Test
  public void deriveFont_twoAttributes_appliesBothAttributes() {
    Font derived = FontUtilities.deriveFont(BASE_FONT,
            java.awt.font.TextAttribute.WEIGHT, java.awt.font.TextAttribute.WEIGHT_BOLD,
            java.awt.font.TextAttribute.POSTURE, java.awt.font.TextAttribute.POSTURE_OBLIQUE);

    assertFontAttribute(derived, java.awt.font.TextAttribute.WEIGHT, java.awt.font.TextAttribute.WEIGHT_BOLD);
    assertFontAttribute(derived, java.awt.font.TextAttribute.POSTURE, java.awt.font.TextAttribute.POSTURE_OBLIQUE);
  }

  @Test
  public void deriveFont_threeAttributes_appliesAllAttributes() {
    Font derived = FontUtilities.deriveFont(BASE_FONT,
            java.awt.font.TextAttribute.FAMILY, "Monospaced",
            java.awt.font.TextAttribute.WEIGHT, java.awt.font.TextAttribute.WEIGHT_BOLD,
            java.awt.font.TextAttribute.POSTURE, java.awt.font.TextAttribute.POSTURE_OBLIQUE);

    assertFontAttribute(derived, java.awt.font.TextAttribute.FAMILY, "Monospaced");
    assertFontAttribute(derived, java.awt.font.TextAttribute.WEIGHT, java.awt.font.TextAttribute.WEIGHT_BOLD);
    assertFontAttribute(derived, java.awt.font.TextAttribute.POSTURE, java.awt.font.TextAttribute.POSTURE_OBLIQUE);
  }

  @Test
  public void deriveFont_varargs_appliesAllAttributes() {
    Font derived = FontUtilities.deriveFont(BASE_FONT, TextFamily.MONOSPACED, TextWeight.BOLD, TextPosture.OBLIQUE);

    assertFontAttribute(derived, java.awt.font.TextAttribute.FAMILY, TextFamily.MONOSPACED.getValue());
    assertFontAttribute(derived, java.awt.font.TextAttribute.WEIGHT, TextWeight.BOLD.getValue());
    assertFontAttribute(derived, java.awt.font.TextAttribute.POSTURE, TextPosture.OBLIQUE.getValue());
  }

  @Test
  public void deriveFont_varargs_emptyAttributes_preservesCoreProperties() {
    Font derived = FontUtilities.deriveFont(BASE_FONT);

    assertNotNull(derived);
    assertEquals(BASE_FONT.getFamily(), derived.getFamily());
    assertEquals(BASE_FONT.getSize(), derived.getSize());
    assertEquals(BASE_FONT.getStyle(), derived.getStyle());
  }

  @Test
  public void scaleFont_factorOne_returnsSameInstance() {
    Font scaled = FontUtilities.scaleFont(BASE_FONT, 1.0f);

    assertSame(BASE_FONT, scaled);
  }

  @Test
  public void scaleFont_factorGreaterThanOne_scalesUpFontSize() {
    Font scaled = FontUtilities.scaleFont(BASE_FONT, 2.0f);

    assertNotSame(BASE_FONT, scaled);
    assertEquals(24.0f, scaled.getSize2D(), 0.0f);
  }

  @Test
  public void scaleFont_fractionalFactor_scalesFontSizePrecisely() {
    Font scaled = FontUtilities.scaleFont(BASE_FONT, 0.5f);

    assertEquals(6.0f, scaled.getSize2D(), 0.0f);
  }
}
