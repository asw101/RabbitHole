package org.alice.ide.croquet.models.ui.locale;

import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LocaleStateBehaviorTest {
  @Test
  public void supportedLocales_preserveExpectedOrdering() {
    LocaleState state = LocaleState.getInstance();
    List<Locale> expected = List.of(
        Locale.of("en", "US"),
        Locale.of("pt", "BR"),
        Locale.of("es"),
        Locale.of("el"),
        Locale.of("ro"),
        Locale.of("sl"),
        Locale.of("ru"),
        Locale.of("uk"),
        Locale.of("tr"),
        Locale.of("ar"),
        Locale.of("zh", "CN"),
        Locale.of("ja"),
        Locale.of("bg"));

    assertEquals(expected.size(), state.getItemCount());
    for (int i = 0; i < expected.size(); i++) {
      assertEquals(expected.get(i), state.getItemAt(i));
    }
  }

  @Test
  public void supportedLocales_useUniqueLanguageTags_withEnglishUnitedStatesFirst() {
    LocaleState state = LocaleState.getInstance();
    Set<String> languageTags = new LinkedHashSet<>();
    for (int i = 0; i < state.getItemCount(); i++) {
      assertTrue(languageTags.add(state.getItemAt(i).toLanguageTag()));
    }
    assertEquals(Locale.of("en", "US"), state.getItemAt(0));
  }
}
