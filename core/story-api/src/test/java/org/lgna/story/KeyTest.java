package org.lgna.story;

import org.junit.Test;

import java.awt.event.KeyEvent;
import java.util.EnumSet;

import static org.junit.Assert.*;

public class KeyTest {

  @Test
  public void valuesLengthMatchesExpectedCount() {
    assertEquals(189, Key.values().length);
  }

  @Test
  public void valueOfFindsCommonKeys() {
    assertSame(Key.ENTER, Key.valueOf("ENTER"));
    assertSame(Key.SPACE, Key.valueOf("SPACE"));
    assertSame(Key.LEFT, Key.valueOf("LEFT"));
    assertSame(Key.DIGIT_0, Key.valueOf("DIGIT_0"));
    assertSame(Key.A, Key.valueOf("A"));
    assertSame(Key.F12, Key.valueOf("F12"));
  }

  @Test
  public void enumContainsDirectionalKeys() {
    EnumSet<Key> directions = EnumSet.of(Key.LEFT, Key.RIGHT, Key.UP, Key.DOWN, Key.KP_LEFT, Key.KP_RIGHT, Key.KP_UP, Key.KP_DOWN);
    assertTrue(directions.contains(Key.LEFT));
    assertTrue(directions.contains(Key.RIGHT));
    assertTrue(directions.contains(Key.UP));
    assertTrue(directions.contains(Key.DOWN));
    assertTrue(directions.contains(Key.KP_LEFT));
    assertTrue(directions.contains(Key.KP_RIGHT));
  }

  @Test
  public void enumContainsDigitKeys() {
    for (int i = 0; i <= 9; i++) {
      assertNotNull(Key.valueOf("DIGIT_" + i));
      assertNotNull(Key.valueOf("NUMPAD" + i));
    }
  }

  @Test
  public void enumContainsLetterKeys() {
    for (char ch = 'A'; ch <= 'Z'; ch++) {
      assertNotNull(Key.valueOf(String.valueOf(ch)));
    }
  }

  @Test
  public void enumContainsFunctionKeys() {
    for (int i = 1; i <= 24; i++) {
      assertNotNull(Key.valueOf("F" + i));
    }
  }

  @Test
  public void enterKeyCodeMatchesAwtConstant() {
    assertEquals(KeyEvent.VK_ENTER, Key.ENTER.getKeyCode());
  }

  @Test
  public void escapeKeyCodeMatchesAwtConstant() {
    assertEquals(KeyEvent.VK_ESCAPE, Key.ESCAPE.getKeyCode());
  }

  @Test
  public void letterKeyCodeMatchesAwtConstant() {
    assertEquals(KeyEvent.VK_A, Key.A.getKeyCode());
    assertEquals(KeyEvent.VK_Z, Key.Z.getKeyCode());
  }

  @Test
  public void digitKeyCodeMatchesAwtConstant() {
    assertEquals(KeyEvent.VK_0, Key.DIGIT_0.getKeyCode());
    assertEquals(KeyEvent.VK_9, Key.DIGIT_9.getKeyCode());
  }

  @Test
  public void keypadDigitKeyCodesMatchAwtConstants() {
    assertEquals(KeyEvent.VK_NUMPAD0, Key.NUMPAD0.getKeyCode());
    assertEquals(KeyEvent.VK_NUMPAD9, Key.NUMPAD9.getKeyCode());
  }

  @Test
  public void functionKeyCodesMatchAwtConstants() {
    assertEquals(KeyEvent.VK_F1, Key.F1.getKeyCode());
    assertEquals(KeyEvent.VK_F12, Key.F12.getKeyCode());
    assertEquals(KeyEvent.VK_F24, Key.F24.getKeyCode());
  }

  @Test
  public void getInstanceFromKeyCodeReturnsExpectedCommonKeys() {
    assertSame(Key.ENTER, Key.getInstanceFromKeyCode(KeyEvent.VK_ENTER));
    assertSame(Key.LEFT, Key.getInstanceFromKeyCode(KeyEvent.VK_LEFT));
    assertSame(Key.A, Key.getInstanceFromKeyCode(KeyEvent.VK_A));
    assertSame(Key.DIGIT_5, Key.getInstanceFromKeyCode(KeyEvent.VK_5));
  }

  @Test
  public void getInstanceFromKeyCodeReturnsExpectedNumpadKeys() {
    assertSame(Key.NUMPAD2, Key.getInstanceFromKeyCode(KeyEvent.VK_NUMPAD2));
    assertSame(Key.NUMPAD8, Key.getInstanceFromKeyCode(KeyEvent.VK_NUMPAD8));
  }

  @Test
  public void getInstanceFromKeyCodeReturnsSeparatorForSharedSeparatorKeyCode() {
    assertSame(Key.SEPARATOR, Key.getInstanceFromKeyCode(KeyEvent.VK_SEPARATOR));
  }

  @Test
  public void getInstanceFromKeyCodeReturnsUndefinedForUndefinedCode() {
    assertSame(Key.UNDEFINED, Key.getInstanceFromKeyCode(KeyEvent.VK_UNDEFINED));
  }

  @Test
  public void getInstanceFromKeyCodeReturnsNullForUnknownCode() {
    assertNull(Key.getInstanceFromKeyCode(-12345));
  }

  @Test
  public void keyCodesAreStableForModifierKeys() {
    assertEquals(KeyEvent.VK_SHIFT, Key.SHIFT.getKeyCode());
    assertEquals(KeyEvent.VK_CONTROL, Key.CONTROL.getKeyCode());
    assertEquals(KeyEvent.VK_ALT, Key.ALT.getKeyCode());
    assertEquals(KeyEvent.VK_ALT_GRAPH, Key.ALT_GRAPH.getKeyCode());
  }

  @Test
  public void keyCodesAreStableForPunctuationKeys() {
    assertEquals(KeyEvent.VK_COMMA, Key.COMMA.getKeyCode());
    assertEquals(KeyEvent.VK_PERIOD, Key.PERIOD.getKeyCode());
    assertEquals(KeyEvent.VK_SLASH, Key.SLASH.getKeyCode());
    assertEquals(KeyEvent.VK_BACK_QUOTE, Key.BACK_QUOTE.getKeyCode());
  }

  @Test
  public void allEnumValuesHaveAResolvableMapping() {
    for (Key key : Key.values()) {
      assertNotNull("Key code should resolve to some enum: " + key, Key.getInstanceFromKeyCode(key.getKeyCode()));
    }
  }
}
