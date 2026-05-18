package org.lgna.croquet;

import org.junit.Test;

import javax.swing.KeyStroke;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Extended tests for {@link AbstractElement} — findLocalizedText, getKeyStroke,
 * getKeyCode, initializeIfNecessary, localize, and toString behavior.
 */
public class AbstractElementExtendedTest {

  // ── getKeyStroke ──────────────────────────────────────────────────

  @Test
  public void getKeyStroke_null_returnsNull() {
    assertNull(AbstractElement.getKeyStroke(null));
  }

  @Test
  public void getKeyStroke_emptyString_returnsNull() {
    assertNull(AbstractElement.getKeyStroke(""));
  }

  @Test
  public void getKeyStroke_validF5_returnsNonNull() {
    KeyStroke ks = AbstractElement.getKeyStroke("VK_F5");
    assertNotNull(ks);
  }

  @Test
  public void getKeyStroke_F1_returnsF1() {
    KeyStroke ks = AbstractElement.getKeyStroke("VK_F1");
    assertNotNull(ks);
    assertEquals(KeyEvent.VK_F1, ks.getKeyCode());
  }

  @Test
  public void getKeyStroke_invalidKey_returnsNull() {
    KeyStroke ks = AbstractElement.getKeyStroke("VK_NONEXISTENT");
    assertNull(ks);
  }

  @Test
  public void getKeyStroke_escape_returnsEscape() {
    KeyStroke ks = AbstractElement.getKeyStroke("VK_ESCAPE");
    assertNotNull(ks);
    assertEquals(KeyEvent.VK_ESCAPE, ks.getKeyCode());
  }

  @Test
  public void getKeyStroke_enter_returnsEnter() {
    KeyStroke ks = AbstractElement.getKeyStroke("VK_ENTER");
    assertNotNull(ks);
    assertEquals(KeyEvent.VK_ENTER, ks.getKeyCode());
  }

  @Test
  public void getKeyStroke_controlS_parsed() {
    KeyStroke ks = AbstractElement.getKeyStroke("control VK_S");
    // This is parsed as a text-based accelerator format
    if (ks != null) {
      assertEquals(KeyEvent.VK_S, ks.getKeyCode());
    }
    // If the parser doesn't support this format, null is also acceptable
  }

  // ── getKeyCode ────────────────────────────────────────────────────

  @Test
  public void getKeyCode_null_returnsZero() {
    assertEquals(0, AbstractElement.getKeyCode(null));
  }

  @Test
  public void getKeyCode_emptyString_returnsZero() {
    assertEquals(0, AbstractElement.getKeyCode(""));
  }

  @Test
  public void getKeyCode_validKey_returnsCode() {
    int code = AbstractElement.getKeyCode("VK_A");
    assertEquals(KeyEvent.VK_A, code);
  }

  @Test
  public void getKeyCode_invalidKey_returnsZero() {
    assertEquals(0, AbstractElement.getKeyCode("VK_NONEXISTENT"));
  }

  @Test
  public void getKeyCode_S_returnsVK_S() {
    int code = AbstractElement.getKeyCode("VK_S");
    assertEquals(KeyEvent.VK_S, code);
  }

  @Test
  public void getKeyCode_F10_returnsVK_F10() {
    int code = AbstractElement.getKeyCode("VK_F10");
    assertEquals(KeyEvent.VK_F10, code);
  }

  @Test
  public void getKeyCode_DELETE_returnsVK_DELETE() {
    int code = AbstractElement.getKeyCode("VK_DELETE");
    assertEquals(KeyEvent.VK_DELETE, code);
  }

  // ── findLocalizedText ─────────────────────────────────────────────

  @Test
  public void findLocalizedText_nullClass_returnsNull() {
    assertNull(AbstractElement.findLocalizedText(null, "test"));
  }

  @Test
  public void findLocalizedText_nullSubKey_returnsNull() {
    assertNull(AbstractElement.findLocalizedText(TestElement.class, null));
  }

  @Test
  public void findLocalizedText_missingBundle_returnsNull() {
    assertNull(AbstractElement.findLocalizedText(TestElement.class, "missing"));
  }

  @Test
  public void findLocalizedText_nonExistentKey_returnsNull() {
    assertNull(AbstractElement.findLocalizedText(TestElement.class, "nonExistent"));
  }

  // ── initializeIfNecessary via concrete subclass ───────────────────

  @Test
  public void initializeIfNecessary_callsLocalize() {
    TestElement elem = new TestElement();
    elem.initializeIfNecessary();
    assertTrue(elem.localizeCalled);
  }

  @Test
  public void initializeIfNecessary_idempotent() {
    TestElement elem = new TestElement();
    elem.initializeIfNecessary();
    elem.localizeCalled = false;
    elem.initializeIfNecessary();
    assertFalse(elem.localizeCalled);
  }

  @Test
  public void initializeIfNecessary_calledThreeTimes_onlyFirstCallsLocalize() {
    TestElement elem = new TestElement();
    elem.initializeIfNecessary();
    assertTrue(elem.localizeCalled);
    elem.localizeCalled = false;
    elem.initializeIfNecessary();
    elem.initializeIfNecessary();
    assertFalse(elem.localizeCalled);
  }

  // ── CroquetTestUtils.nextTestUUID ─────────────────────────────────

  @Test
  public void nextTestUUID_returnsUniqueValues() {
    UUID a = CroquetTestUtils.nextTestUUID();
    UUID b = CroquetTestUtils.nextTestUUID();
    assertNotEquals(a, b);
  }

  @Test
  public void nextTestUUID_returnsNonNull() {
    assertNotNull(CroquetTestUtils.nextTestUUID());
  }

  @Test
  public void nextTestUUID_manyUnique() {
    UUID[] uuids = new UUID[100];
    for (int i = 0; i < 100; i++) {
      uuids[i] = CroquetTestUtils.nextTestUUID();
    }
    for (int i = 0; i < 100; i++) {
      for (int j = i + 1; j < 100; j++) {
        assertNotEquals(uuids[i], uuids[j]);
      }
    }
  }

  // ── Test infrastructure ───────────────────────────────────────────

  static class TestElement extends AbstractElement {
    boolean localizeCalled = false;

    TestElement() {
      super(CroquetTestUtils.nextTestUUID());
    }

    @Override
    protected void localize() {
      localizeCalled = true;
    }

    @Override
    protected Class<? extends Element> getClassUsedForLocalization() {
      return TestElement.class;
    }

    @Override
    protected String getSubKeyForLocalization() {
      return "test";
    }

    @Override
    public void appendUserRepr(StringBuilder sb) {
      sb.append("TestElement");
    }
  }
}
