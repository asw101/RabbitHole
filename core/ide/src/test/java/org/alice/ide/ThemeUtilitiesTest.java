package org.alice.ide;

import org.junit.After;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

public class ThemeUtilitiesTest {
  @After
  public void tearDown() throws Exception {
    clearFallbackTheme();
  }

  @Test
  public void getActiveTheme_returnsStableThemeInstance() throws Exception {
    clearFallbackTheme();

    Theme first = ThemeUtilities.getActiveTheme();
    Theme second = ThemeUtilities.getActiveTheme();

    assertNotNull(first);
    if (IDE.getActiveInstance() == null) {
      assertTrue(first instanceof DefaultTheme);
    }
    assertSame(first, second);
  }

  @Test
  public void constructor_isGuardedUtilityConstructor() throws Exception {
    Constructor<ThemeUtilities> constructor = ThemeUtilities.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    try {
      constructor.newInstance();
      fail("ThemeUtilities constructor should throw AssertionError");
    } catch (InvocationTargetException ite) {
      assertTrue(ite.getCause() instanceof AssertionError);
    }
  }

  private static void clearFallbackTheme() throws Exception {
    Field field = ThemeUtilities.class.getDeclaredField("fallbackTheme");
    field.setAccessible(true);
    field.set(null, null);
  }
}
