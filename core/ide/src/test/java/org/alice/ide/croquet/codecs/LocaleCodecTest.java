package org.alice.ide.croquet.codecs;

import org.junit.Test;
import java.util.Locale;
import static org.junit.Assert.*;

public class LocaleCodecTest {

  @Test
  public void singleton_isNotNull() {
    assertNotNull(LocaleCodec.SINGLETON);
  }

  @Test
  public void getValueClass_returnsLocaleClass() {
    assertEquals(Locale.class, LocaleCodec.SINGLETON.getValueClass());
  }

  @Test
  public void appendRepresentation_usLocale_containsEnglish() {
    StringBuilder sb = new StringBuilder();
    LocaleCodec.SINGLETON.appendRepresentation(sb, Locale.US);
    String result = sb.toString();
    assertFalse(result.isEmpty());
    assertTrue(result.contains("English") || result.contains("english") || result.contains("en"));
  }

  @Test
  public void appendRepresentation_nullLocale_appendsNull() {
    StringBuilder sb = new StringBuilder();
    LocaleCodec.SINGLETON.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  @Test
  public void appendRepresentation_nonEnglishLocale_nonEmpty() {
    StringBuilder sb = new StringBuilder();
    LocaleCodec.SINGLETON.appendRepresentation(sb, Locale.FRENCH);
    assertFalse("Non-English locale should produce non-empty representation", sb.toString().isEmpty());
  }

  @Test
  public void appendRepresentation_appendsToExistingBuilder() {
    StringBuilder sb = new StringBuilder("locale=");
    LocaleCodec.SINGLETON.appendRepresentation(sb, Locale.ENGLISH);
    assertTrue(sb.toString().startsWith("locale="));
    assertTrue(sb.length() > "locale=".length());
  }
}
