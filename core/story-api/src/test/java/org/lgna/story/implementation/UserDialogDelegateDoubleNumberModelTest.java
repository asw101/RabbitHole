package org.lgna.story.implementation;

import org.junit.Test;

import java.text.DecimalFormatSymbols;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class UserDialogDelegateDoubleNumberModelTest {
  @Test
  public void doubleNumberModelParsesLocalizedDecimalsAndNegation() throws Exception {
    Object model = UserDialogDelegateTestSupport.newDoubleNumberModel("distance");
    char separator = new DecimalFormatSymbols().getDecimalSeparator();

    UserDialogDelegateTestSupport.appendDigit(model, 1);
    UserDialogDelegateTestSupport.appendDigit(model, 2);
    UserDialogDelegateTestSupport.appendDecimalPoint(model);
    UserDialogDelegateTestSupport.appendDigit(model, 5);
    UserDialogDelegateTestSupport.negate(model);

    assertEquals("-12" + separator + "5", UserDialogDelegateTestSupport.getDocumentText(model));
    assertEquals(-12.5, UserDialogDelegateTestSupport.getValue(model).doubleValue(), 0.000001);
  }

  @Test
  public void doubleNumberModelReturnsNullForMalformedInput() throws Exception {
    Object model = UserDialogDelegateTestSupport.newDoubleNumberModel("distance");
    char separator = new DecimalFormatSymbols().getDecimalSeparator();

    UserDialogDelegateTestSupport.setDocumentText(model, "1" + separator + separator + "5");

    assertNull(UserDialogDelegateTestSupport.getValue(model));
    assertNotNull(UserDialogDelegateTestSupport.getDecimalPointAction(model));
  }
}
