package org.alice.stageide.fontchooser;

import org.junit.Test;
import org.lgna.story.Font;
import org.lgna.story.fontattributes.FamilyConstant;
import org.lgna.story.fontattributes.PostureConstant;
import org.lgna.story.fontattributes.SizeValue;
import org.lgna.story.fontattributes.WeightConstant;

import javax.swing.JLabel;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FontChooserBehaviorTest {
  @Test
  public void sampleTextFallsBackAndValueRoundTripsThroughSelections() throws Exception {
    Font expected = new Font(
        FamilyConstant.SERIF,
        WeightConstant.BOLD,
        PostureConstant.OBLIQUE,
        new SizeValue(24.0f)
    );
    FontChooser chooser = new FontChooser(expected);

    chooser.setSampleText(null);
    assertEquals("AaBbYyZz", sampleLabel(chooser).getText());

    chooser.setSampleText("Preview");
    assertEquals("Preview", sampleLabel(chooser).getText());

    Font actual = chooser.getValue();
    assertNotNull(actual);
    assertEquals(expected.getFamily(), actual.getFamily());
    assertEquals(expected.getWeight(), actual.getWeight());
    assertEquals(expected.getPosture(), actual.getPosture());
    assertEquals(expected.getSize().getValue(), actual.getSize().getValue());
  }

  private static JLabel sampleLabel(FontChooser chooser) throws Exception {
    Field field = FontChooser.class.getDeclaredField("m_sample");
    field.setAccessible(true);
    return (JLabel) field.get(chooser);
  }
}
