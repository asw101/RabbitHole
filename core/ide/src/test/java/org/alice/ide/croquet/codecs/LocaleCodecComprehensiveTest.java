package org.alice.ide.croquet.codecs;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

public class LocaleCodecComprehensiveTest {
  @Test public void singleton_notNull() { assertNotNull(LocaleCodec.SINGLETON); }
  @Test public void getValueClass_returnsLocaleClass() { assertEquals(Locale.class, LocaleCodec.SINGLETON.getValueClass()); }
  @Test public void appendRepresentation_nullLocale() {
    StringBuilder sb = new StringBuilder();
    LocaleCodec.SINGLETON.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }
  @Test public void appendRepresentation_usLocale() {
    StringBuilder sb = new StringBuilder();
    LocaleCodec.SINGLETON.appendRepresentation(sb, Locale.US);
    assertEquals(Locale.US.getDisplayName(Locale.US), sb.toString());
  }
  @Test public void appendRepresentation_franceLocale() {
    StringBuilder sb = new StringBuilder();
    LocaleCodec.SINGLETON.appendRepresentation(sb, Locale.FRANCE);
    assertEquals(Locale.FRANCE.getDisplayName(Locale.FRANCE), sb.toString());
  }
  @Test public void appendRepresentation_appendsToExistingBuilder() {
    StringBuilder sb = new StringBuilder("locale=");
    LocaleCodec.SINGLETON.appendRepresentation(sb, Locale.JAPAN);
    assertEquals("locale=" + Locale.JAPAN.getDisplayName(Locale.JAPAN), sb.toString());
  }
}
