package org.lgna.story.implementation;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UserDialogDelegateIntegerNumberModelTest {
  @Test
  public void integerNumberModelParsesSignedWholeNumbers() throws Exception {
    Object model = UserDialogDelegateTestSupport.newIntegerNumberModel("count");

    UserDialogDelegateTestSupport.appendDigit(model, 4);
    UserDialogDelegateTestSupport.appendDigit(model, 2);
    UserDialogDelegateTestSupport.negate(model);

    assertEquals("-42", UserDialogDelegateTestSupport.getDocumentText(model));
    assertEquals(-42, UserDialogDelegateTestSupport.getValue(model).intValue());
    assertNull(UserDialogDelegateTestSupport.getDecimalPointAction(model));
  }

  @Test
  public void integerNumberModelReturnsNullForNonNumericText() throws Exception {
    Object model = UserDialogDelegateTestSupport.newIntegerNumberModel("count");

    UserDialogDelegateTestSupport.setDocumentText(model, "12a");

    assertNull(UserDialogDelegateTestSupport.getValue(model));
  }
}
