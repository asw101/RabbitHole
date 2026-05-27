package org.lgna.story.implementation;

import org.junit.Test;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserDialogDelegateNumPadTest {
  @Test
  public void numPadContainsDigitsAndControlButtons() throws Exception {
    Object doubleModel = UserDialogDelegateTestSupport.newDoubleNumberModel("distance");
    JPanel doublePad = UserDialogDelegateTestSupport.createComponent(doubleModel);
    List<String> doubleLabels = UserDialogDelegateTestSupport.findButtons(doublePad)
        .stream()
        .map(JButton::getText)
        .collect(Collectors.toList());

    assertTrue(doubleLabels.contains("0"));
    assertTrue(doubleLabels.contains("9"));
    assertTrue(doubleLabels.contains("←"));
    assertTrue(doubleLabels.contains("±"));
    assertEquals(13, doubleLabels.size());

    Object integerModel = UserDialogDelegateTestSupport.newIntegerNumberModel("count");
    JPanel integerPad = UserDialogDelegateTestSupport.createComponent(integerModel);
    List<String> integerLabels = UserDialogDelegateTestSupport.findButtons(integerPad)
        .stream()
        .map(JButton::getText)
        .collect(Collectors.toList());

    assertEquals(12, integerLabels.size());
    assertTrue(integerLabels.contains("←"));
    assertTrue(integerLabels.contains("±"));
  }

  @Test
  public void addAndRemoveListenersManageAncestorRegistrationOnTextField() throws Exception {
    Object model = UserDialogDelegateTestSupport.newDoubleNumberModel("distance");
    Object numPad = UserDialogDelegateTestSupport.createComponent(model);
    JTextField textField = UserDialogDelegateTestSupport.getTextField(numPad);

    assertEquals(0, textField.getAncestorListeners().length);
    UserDialogDelegateTestSupport.addListeners(numPad);
    assertEquals(1, textField.getAncestorListeners().length);
    UserDialogDelegateTestSupport.removeListeners(numPad);
    assertEquals(0, textField.getAncestorListeners().length);
  }
}
