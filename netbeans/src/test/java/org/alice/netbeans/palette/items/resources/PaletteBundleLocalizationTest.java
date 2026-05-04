package org.alice.netbeans.palette.items.resources;

import org.junit.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.assertTrue;

public class PaletteBundleLocalizationTest {

  @Test
  public void spanishPaletteTemplatesKeepRequiredOperands() {
    ResourceBundle bundle = ResourceBundle.getBundle(
        "org.alice.netbeans.palette.items.resources.Bundle",
        Locale.forLanguageTag("es"));

    assertContains(bundle, "HINT_html-ADDTIMELISTENER", "reemplazar_con_FRECUENCIA");
    assertContains(bundle, "HINT_html-COUNTLOOP", "reemplazar_con_CONTEO");
    assertContains(bundle, "HINT_html-FORALLTOGETHER", "reemplazar_con_CLASE");
    assertContains(bundle, "HINT_html-FORALLTOGETHER", "reemplazar_con_ARREGLO_DE_ELEMENTOS");
    assertContains(bundle, "HINT_html-FOREACHIN", "reemplazar_con_TIPO_DE_ELEMENTO");
    assertContains(bundle, "HINT_html-FOREACHIN", "reemplazar_con_NOMBRE_DE_ELEMENTO");
    assertContains(bundle, "HINT_html-FOREACHIN", "reemplazar_con_ARREGLO");
    assertContains(bundle, "HINT_html-IFSTATEMENT", "reemplazar_con_EXPRESION_BOOLEANA");
    assertContains(bundle, "HINT_html-WHILELOOP", "reemplazar_con_EXPRESION_BOOLEANA");
  }

  private static void assertContains(ResourceBundle bundle, String key, String expectedText) {
    String value = bundle.getString(key);
    assertTrue(value, value.contains(expectedText));
  }
}
