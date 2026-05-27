package edu.cmu.cs.dennisc.javax.swing.components;

import edu.cmu.cs.dennisc.javax.swing.ColorCustomizer;
import org.junit.Test;

import java.awt.Color;
import java.awt.Dimension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class JSuggestiveTextFieldBehaviorTest {

  @Test
  public void constructorStoresInitialTextAndBlankSuggestion() {
    JSuggestiveTextField field = new JSuggestiveTextField("hello", "type here");

    assertEquals("hello", field.getText());
    assertEquals("type here", field.getTextForBlankCondition());
  }

  @Test
  public void foregroundCustomizerOverridesReportedForeground() {
    JSuggestiveTextField field = new JSuggestiveTextField();
    field.setForeground(Color.BLACK);
    field.setForegroundCustomizer(defaultColor -> Color.ORANGE);

    assertEquals(Color.ORANGE, field.getForeground());
  }

  @Test
  public void maximumSizeUsesPreferredHeight() {
    JSuggestiveTextField field = new JSuggestiveTextField("sized");
    field.setColumns(12);

    Dimension preferred = field.getPreferredSize();
    Dimension maximum = field.getMaximumSize();

    assertEquals(preferred.height, maximum.height);
    assertTrue(maximum.width >= preferred.width);
  }

  @Test
  public void gettersExposeInstalledForegroundCustomizer() {
    JSuggestiveTextField field = new JSuggestiveTextField();
    ColorCustomizer customizer = defaultColor -> defaultColor == null ? Color.GRAY : defaultColor.brighter();

    assertNull(field.getForegroundCustomizer());
    field.setForegroundCustomizer(customizer);

    assertSame(customizer, field.getForegroundCustomizer());
  }
}
