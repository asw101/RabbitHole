package org.lgna.croquet;

import edu.cmu.cs.dennisc.java.awt.event.InputEventUtilities;
import org.junit.Before;
import org.junit.Test;

import javax.swing.KeyStroke;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for {@link AbstractElement} — localization, key stroke parsing,
 * repr generation, and initialization lifecycle.
 * Uses a minimal concrete subclass to avoid Application dependencies.
 */
public class AbstractElementTest {

  private TestElement element;

  @Before
  public void setUp() {
    element = new TestElement();
  }

  // ── getKeyStroke ──────────────────────────────────────────────────

  @Test
  public void getKeyStroke_nullText_returnsNull() {
    assertNull(AbstractElement.getKeyStroke(null));
  }

  @Test
  public void getKeyStroke_emptyText_returnsNull() {
    assertNull(AbstractElement.getKeyStroke(""));
  }

  @Test
  public void getKeyStroke_validFunctionKey_returnsKeyStroke() {
    KeyStroke ks = AbstractElement.getKeyStroke("VK_F1");
    assertNotNull(ks);
    assertEquals(KeyEvent.VK_F1, ks.getKeyCode());
    // F-keys get no accelerator mask
    assertEquals(0, ks.getModifiers());
  }

  @Test
  public void getKeyStroke_functionKeyF12_noAcceleratorMask() {
    KeyStroke ks = AbstractElement.getKeyStroke("VK_F12");
    assertNotNull(ks);
    assertEquals(KeyEvent.VK_F12, ks.getKeyCode());
    assertEquals(0, ks.getModifiers());
  }

  @Test
  public void getKeyStroke_letterKey_hasAcceleratorMask() {
    KeyStroke ks = AbstractElement.getKeyStroke("VK_S");
    assertNotNull(ks);
    assertEquals(KeyEvent.VK_S, ks.getKeyCode());
    int acceleratorMask = InputEventUtilities.getAcceleratorMask();
    assertTrue("Letter key should include platform accelerator",
        (ks.getModifiers() & acceleratorMask) != 0);
  }

  @Test
  public void getKeyStroke_withShiftModifier() {
    KeyStroke ks = AbstractElement.getKeyStroke("VK_S,SHIFT_DOWN_MASK");
    assertNotNull(ks);
    assertEquals(KeyEvent.VK_S, ks.getKeyCode());
    assertTrue((ks.getModifiers() & InputEvent.SHIFT_DOWN_MASK) != 0);
  }

  @Test
  public void getKeyStroke_withPlatformAccelerator() {
    KeyStroke ks = AbstractElement.getKeyStroke("VK_Z,PLATFORM_ACCELERATOR_MASK");
    assertNotNull(ks);
    assertEquals(KeyEvent.VK_Z, ks.getKeyCode());
    int acceleratorMask = InputEventUtilities.getAcceleratorMask();
    assertTrue("PLATFORM_ACCELERATOR_MASK should resolve to platform modifier",
        (ks.getModifiers() & acceleratorMask) != 0);
  }

  @Test
  public void getKeyStroke_invalidFieldName_returnsNull() {
    assertNull(AbstractElement.getKeyStroke("VK_NONEXISTENT_KEY"));
  }

  @Test
  public void getKeyStroke_multipleModifiers() {
    KeyStroke ks = AbstractElement.getKeyStroke("VK_N,SHIFT_DOWN_MASK|PLATFORM_ACCELERATOR_MASK");
    assertNotNull(ks);
    assertEquals(KeyEvent.VK_N, ks.getKeyCode());
    assertTrue((ks.getModifiers() & InputEvent.SHIFT_DOWN_MASK) != 0);
  }

  // ── getKeyCode ────────────────────────────────────────────────────

  @Test
  public void getKeyCode_validKey_returnsCode() {
    assertEquals(KeyEvent.VK_A, AbstractElement.getKeyCode("VK_A"));
  }

  @Test
  public void getKeyCode_nullName_returnsZero() {
    assertEquals(0, AbstractElement.getKeyCode(null));
  }

  @Test
  public void getKeyCode_invalidName_returnsZero() {
    assertEquals(0, AbstractElement.getKeyCode("NOT_A_KEY"));
  }

  // ── findLocalizedText (static) ────────────────────────────────────

  @Test
  public void findLocalizedText_nullClass_returnsNull() {
    assertNull(AbstractElement.findLocalizedText(null, null));
  }

  @Test
  public void findLocalizedText_noBundle_returnsNull() {
    assertNull(AbstractElement.findLocalizedText(TestElement.class, null));
  }

  @Test
  public void findLocalizedText_noBundle_withSubKey_returnsNull() {
    assertNull(AbstractElement.findLocalizedText(TestElement.class, "someSubKey"));
  }

  // ── createRepr / toString ─────────────────────────────────────────

  @Test
  public void toString_containsClassName() {
    String repr = element.toString();
    assertTrue(repr.contains("TestElement"));
  }

  @Test
  public void toString_hasBrackets() {
    String repr = element.toString();
    assertTrue(repr.contains("["));
    assertTrue(repr.contains("]"));
  }

  // ── appendUserRepr ────────────────────────────────────────────────

  @Test
  public void appendUserRepr_containsTodoOverride() {
    StringBuilder sb = new StringBuilder();
    element.appendUserRepr(sb);
    assertTrue(sb.toString().contains("todo: override appendUserString"));
  }

  @Test
  public void appendUserRepr_containsClassName() {
    StringBuilder sb = new StringBuilder();
    element.appendUserRepr(sb);
    assertTrue(sb.toString().contains(TestElement.class.getName()));
  }

  // ── initializeIfNecessary ─────────────────────────────────────────

  @Test
  public void initializeIfNecessary_callsLocalize() {
    assertFalse(element.localizedCalled);
    element.initializeIfNecessary();
    assertTrue(element.localizedCalled);
  }

  @Test
  public void initializeIfNecessary_idempotent() {
    element.initializeIfNecessary();
    element.localizedCalled = false;
    element.initializeIfNecessary();
    assertFalse(element.localizedCalled);
  }

  // ── findLocalizedText (instance, protected) ───────────────────────

  @Test
  public void findLocalizedText_instance_noBundle_returnsNull() {
    assertNull(element.callFindLocalizedText(null));
  }

  @Test
  public void findLocalizedText_instance_withSubKey_returnsNull() {
    assertNull(element.callFindLocalizedText("subkey"));
  }

  // ── Concrete test element ─────────────────────────────────────────

  static class TestElement extends AbstractElement {
    boolean localizedCalled = false;

    TestElement() {
      super(CroquetTestUtils.nextTestUUID());
    }

    public UUID getMigrationId() {
      return UUID.fromString("00000000-0000-0000-aaaa-000000000001");
    }

    public Group getGroup() {
      return Group.getInstance(UUID.fromString("00000000-0000-0000-aaaa-000000000002"), "test");
    }

    public boolean isEnabled() {
      return true;
    }

    public void setEnabled(boolean isEnabled) {
    }

    @Override
    protected void localize() {
      localizedCalled = true;
    }

    public void relocalize() {
      localize();
    }

    String callFindLocalizedText(String subKey) {
      return this.findLocalizedText(subKey);
    }
  }
}
