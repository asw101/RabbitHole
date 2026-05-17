package org.lgna.story.event;

import org.junit.Test;
import org.lgna.story.Key;

import java.awt.Canvas;

import static org.junit.Assert.*;

public class EventClassesTest {
  private static final Canvas SOURCE = new Canvas();

  // ══════════════════════════════════════════════════════════════════════════
  //  NumberKeyEvent
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void numberKeyEventFromDigit0() {
    NumberKeyEvent nke = new NumberKeyEvent(awtKeyEvent(java.awt.event.KeyEvent.VK_0, '0'));
    assertEquals(Integer.valueOf(0), nke.getNumber());
  }

  @Test
  public void numberKeyEventFromDigit1() {
    NumberKeyEvent nke = new NumberKeyEvent(awtKeyEvent(java.awt.event.KeyEvent.VK_1, '1'));
    assertEquals(Integer.valueOf(1), nke.getNumber());
  }

  @Test
  public void numberKeyEventFromDigit5() {
    NumberKeyEvent nke = new NumberKeyEvent(awtKeyEvent(java.awt.event.KeyEvent.VK_5, '5'));
    assertEquals(Integer.valueOf(5), nke.getNumber());
  }

  @Test
  public void numberKeyEventFromDigit9() {
    NumberKeyEvent nke = new NumberKeyEvent(awtKeyEvent(java.awt.event.KeyEvent.VK_9, '9'));
    assertEquals(Integer.valueOf(9), nke.getNumber());
  }

  @Test
  public void numberKeyEventFromNumpad0() {
    NumberKeyEvent nke = new NumberKeyEvent(awtKeyEvent(java.awt.event.KeyEvent.VK_NUMPAD0, '0'));
    assertEquals(Integer.valueOf(0), nke.getNumber());
  }

  @Test
  public void numberKeyEventFromNumpad9() {
    NumberKeyEvent nke = new NumberKeyEvent(awtKeyEvent(java.awt.event.KeyEvent.VK_NUMPAD9, '9'));
    assertEquals(Integer.valueOf(9), nke.getNumber());
  }

  @Test
  public void numberKeyEventAllDigitRowKeys() {
    for (int i = 0; i <= 9; i++) {
      int vk = java.awt.event.KeyEvent.VK_0 + i;
      NumberKeyEvent nke = new NumberKeyEvent(awtKeyEvent(vk, (char) ('0' + i)));
      assertEquals("Digit " + i, Integer.valueOf(i), nke.getNumber());
    }
  }

  @Test
  public void numberKeyEventAllNumpadKeys() {
    for (int i = 0; i <= 9; i++) {
      int vk = java.awt.event.KeyEvent.VK_NUMPAD0 + i;
      NumberKeyEvent nke = new NumberKeyEvent(awtKeyEvent(vk, (char) ('0' + i)));
      assertEquals("Numpad " + i, Integer.valueOf(i), nke.getNumber());
    }
  }

  @Test
  public void numberKeyEventNumbersListHas20Entries() {
    assertNotNull(NumberKeyEvent.NUMBERS);
    assertEquals(20, NumberKeyEvent.NUMBERS.size());
  }

  @Test
  public void numberKeyEventNumbersListContainsDigitKeys() {
    assertTrue(NumberKeyEvent.NUMBERS.contains(Key.DIGIT_0));
    assertTrue(NumberKeyEvent.NUMBERS.contains(Key.DIGIT_5));
    assertTrue(NumberKeyEvent.NUMBERS.contains(Key.DIGIT_9));
  }

  @Test
  public void numberKeyEventNumbersListContainsNumpadKeys() {
    assertTrue(NumberKeyEvent.NUMBERS.contains(Key.NUMPAD0));
    assertTrue(NumberKeyEvent.NUMBERS.contains(Key.NUMPAD5));
    assertTrue(NumberKeyEvent.NUMBERS.contains(Key.NUMPAD9));
  }

  @Test
  public void numberKeyEventCopyFromKeyEvent() {
    KeyEvent base = new KeyEvent(awtKeyEvent(java.awt.event.KeyEvent.VK_3, '3'));
    NumberKeyEvent nke = new NumberKeyEvent(base);
    assertEquals(Integer.valueOf(3), nke.getNumber());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  OcclusionEvent
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void occlusionEventConstructionWithNulls() {
    OcclusionEvent oe = new OcclusionEvent(null, null);
    assertNotNull(oe);
  }

  @Test
  public void occlusionEventGettersReturnNullWhenNullConstructed() {
    OcclusionEvent oe = new OcclusionEvent(null, null);
    assertNull(oe.getForegroundModel());
    assertNull(oe.getBackgroundModel());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  AbstractBinarySThingEvent
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void binarySThingEventConstructionWithNulls() {
    AbstractBinarySThingEvent evt = new AbstractBinarySThingEvent(null, null);
    assertNotNull(evt);
  }

  @Test
  public void binarySThingEventGettersReturnNull() {
    AbstractBinarySThingEvent evt = new AbstractBinarySThingEvent(null, null);
    assertNull(evt.getSThingFromSetA());
    assertNull(evt.getSThingFromSetB());
  }

  @SuppressWarnings("deprecation")
  @Test
  public void binarySThingEventGetModelsReturnsArray() {
    AbstractBinarySThingEvent evt = new AbstractBinarySThingEvent(null, null);
    assertNotNull(evt.getModels());
    assertEquals(2, evt.getModels().length);
    // With null arguments, both entries are null
    assertNull(evt.getModels()[0]);
    assertNull(evt.getModels()[1]);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  MouseClickEventImp
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void mouseClickEventImpConstruction() {
    java.awt.event.MouseEvent me = new java.awt.event.MouseEvent(
        SOURCE, java.awt.event.MouseEvent.MOUSE_CLICKED, 0L, 0, 100, 200, 1, false);
    MouseClickEventImp imp = new MouseClickEventImp(me, null);
    assertNotNull(imp);
  }

  @Test
  public void mouseClickEventImpGetEvent() {
    java.awt.event.MouseEvent me = new java.awt.event.MouseEvent(
        SOURCE, java.awt.event.MouseEvent.MOUSE_CLICKED, 0L, 0, 100, 200, 1, false);
    MouseClickEventImp imp = new MouseClickEventImp(me, null);
    assertSame(me, imp.getEvent());
  }

  @Test
  public void mouseClickEventImpGetSceneReturnsNull() {
    java.awt.event.MouseEvent me = new java.awt.event.MouseEvent(
        SOURCE, java.awt.event.MouseEvent.MOUSE_CLICKED, 0L, 0, 50, 75, 1, false);
    MouseClickEventImp imp = new MouseClickEventImp(me, null);
    assertNull(imp.getScene());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Helper
  // ══════════════════════════════════════════════════════════════════════════

  private static java.awt.event.KeyEvent awtKeyEvent(int keyCode, char keyChar) {
    return new java.awt.event.KeyEvent(SOURCE, java.awt.event.KeyEvent.KEY_PRESSED,
        0L, 0, keyCode, keyChar);
  }
}
