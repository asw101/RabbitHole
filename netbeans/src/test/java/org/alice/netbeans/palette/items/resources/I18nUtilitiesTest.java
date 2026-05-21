package org.alice.netbeans.palette.items.resources;

import org.alice.netbeans.palette.items.CountLoop;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class I18nUtilitiesTest {
  @Test
  public void getCodeStripsHtmlScaffoldingAndDecodesAngleBrackets() {
    String code = I18nUtilities.getCode(CountLoop.class);

    Assert.assertFalse(code.contains("<html>"));
    Assert.assertFalse(code.contains("</pre>"));
    Assert.assertTrue(code.contains("for(Integer i=0; i<replace_with_COUNT; i++)"));
    Assert.assertTrue(code.contains("//TODO: Code goes here."));
  }

  @Test
  public void privateConstructorThrowsAssertionError() throws Exception {
    Constructor<I18nUtilities> constructor = I18nUtilities.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    try {
      constructor.newInstance();
      Assert.fail("Expected constructor to throw");
    } catch (InvocationTargetException expected) {
      Assert.assertTrue(expected.getCause() instanceof AssertionError);
    }
  }
}
