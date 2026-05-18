package org.alice.ide.instancefactory;

import org.alice.ide.instancefactory.croquet.ParametersVariablesAndConstantsSeparator;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class ParametersVariablesAndConstantsSeparatorTest {

  @Test
  public void constructor_createsInstance() {
    assertNotNull(new ParametersVariablesAndConstantsSeparator());
  }

  @Test
  public void getMenuItemIconProxyText_defaultsToNull() throws Exception {
    assertNull(getMenuItemIconProxyText(new ParametersVariablesAndConstantsSeparator()));
  }

  @Test
  public void setMenuItemText_updatesProxyText() throws Exception {
    ParametersVariablesAndConstantsSeparator separator = new ParametersVariablesAndConstantsSeparator();

    separator.setMenuItemText("Parameters");

    assertEquals("Parameters", getMenuItemIconProxyText(separator));
  }

  @Test
  public void setMenuItemText_overwritesPreviousValue() throws Exception {
    ParametersVariablesAndConstantsSeparator separator = new ParametersVariablesAndConstantsSeparator();
    separator.setMenuItemText("Parameters");

    separator.setMenuItemText("Variables");

    assertEquals("Variables", getMenuItemIconProxyText(separator));
  }

  private static String getMenuItemIconProxyText(ParametersVariablesAndConstantsSeparator separator) throws Exception {
    Method method = ParametersVariablesAndConstantsSeparator.class.getDeclaredMethod("getMenuItemIconProxyText");
    method.setAccessible(true);
    return (String) method.invoke(separator);
  }
}
