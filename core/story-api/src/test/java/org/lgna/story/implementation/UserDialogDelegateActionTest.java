package org.lgna.story.implementation;

import org.junit.Test;

import javax.swing.Action;
import java.awt.event.ActionEvent;
import java.text.DecimalFormatSymbols;

import static org.junit.Assert.assertEquals;

public class UserDialogDelegateActionTest {
  @Test
  public void numeralBackspaceAndNegateActionsMutateDocument() throws Exception {
    Object model = UserDialogDelegateTestSupport.newIntegerNumberModel("count");

    Action seven = UserDialogDelegateTestSupport.getNumeralAction(model, 7);
    Action three = UserDialogDelegateTestSupport.getNumeralAction(model, 3);
    Action negate = UserDialogDelegateTestSupport.getNegateAction(model);
    Action backspace = UserDialogDelegateTestSupport.getBackspaceAction(model);

    seven.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "7"));
    three.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "3"));
    negate.actionPerformed(null);
    backspace.actionPerformed(null);

    assertEquals("-7", UserDialogDelegateTestSupport.getDocumentText(model));
  }

  @Test
  public void decimalPointActionAppendsCurrentLocaleSeparator() throws Exception {
    Object model = UserDialogDelegateTestSupport.newDoubleNumberModel("distance");
    Action decimalPoint = UserDialogDelegateTestSupport.getDecimalPointAction(model);
    char separator = new DecimalFormatSymbols().getDecimalSeparator();

    UserDialogDelegateTestSupport.appendDigit(model, 9);
    decimalPoint.actionPerformed(null);
    UserDialogDelegateTestSupport.appendDigit(model, 1);

    assertEquals(String.valueOf(separator), decimalPoint.getValue(Action.NAME));
    assertEquals("9" + separator + "1", UserDialogDelegateTestSupport.getDocumentText(model));
  }
}
