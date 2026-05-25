package org.alice.netbeans.palette.items.views;

import org.junit.Assert;
import org.junit.Test;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import java.awt.Color;

public class CountLoopCustomizerBehaviorTest {
  @Test
  public void evaluateInputTogglesValidationStateForIdentifierNames() throws Exception {
    CountLoopCustomizer customizer = new CountLoopCustomizer(new JTextPane());
    JTextField variableNameField = (JTextField) readField(customizer, "variableNameTextField");
    JButton okButton = (JButton) readField(customizer, "okButton");

    variableNameField.setText("1count");
    invoke(customizer, "evaluateInput");
    Assert.assertFalse((Boolean) invoke(customizer, "isInputValid"));
    Assert.assertEquals(Color.red, variableNameField.getForeground());
    Assert.assertFalse(okButton.isEnabled());

    variableNameField.setText("$count");
    invoke(customizer, "evaluateInput");
    Assert.assertTrue((Boolean) invoke(customizer, "isInputValid"));
    Assert.assertEquals(Color.black, variableNameField.getForeground());
    Assert.assertTrue(okButton.isEnabled());
    Assert.assertEquals("$count", customizer.getVariableName());
  }

  private static Object readField(Object target, String name) {
    try {
      var field = CountLoopCustomizer.class.getDeclaredField(name);
      field.setAccessible(true);
      return field.get(target);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static Object invoke(Object target, String name) {
    try {
      var method = CountLoopCustomizer.class.getDeclaredMethod(name);
      method.setAccessible(true);
      return method.invoke(target);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
