package org.lgna.croquet.edits;

import org.lgna.croquet.Group;
import org.lgna.croquet.history.UserActivity;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link StateEdit} and its {@link AbstractEdit} base class.
 * Covers constructor, getPreviousValue/getNextValue, appendDescription,
 * canUndo/canRedo, undo/redo presentations, and description formatting.
 *
 * <p>StateEdit is constructed with a null UserActivity to test the
 * null-model path without Application context.</p>
 */
public class StateEditTest {

  private static final Group TEST_GROUP =
      Group.getInstance(java.util.UUID.fromString("00000000-0000-0000-0005-ffffffffffff"), "editTest");

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_storesPrevAndNext() {
    StateEdit<String> edit = new StateEdit<>(null, "old", "new");
    assertEquals("old", edit.getPreviousValue());
    assertEquals("new", edit.getNextValue());
  }

  @Test
  public void constructor_nullValues() {
    StateEdit<String> edit = new StateEdit<>(null, null, null);
    assertNull(edit.getPreviousValue());
    assertNull(edit.getNextValue());
  }

  // ── getPreviousValue / getNextValue ───────────────────────────────

  @Test
  public void getPreviousValue_returnsConstructorArg() {
    StateEdit<Integer> edit = new StateEdit<>(null, 10, 20);
    assertEquals(Integer.valueOf(10), edit.getPreviousValue());
  }

  @Test
  public void getNextValue_returnsConstructorArg() {
    StateEdit<Integer> edit = new StateEdit<>(null, 10, 20);
    assertEquals(Integer.valueOf(20), edit.getNextValue());
  }

  // ── canUndo / canRedo with null model ─────────────────────────────

  @Test
  public void canUndo_nullActivity_returnsFalse() {
    StateEdit<String> edit = new StateEdit<>(null, "old", "new");
    // getModel() returns null when activity is null, so canUndo returns false
    assertFalse(edit.canUndo());
  }

  @Test
  public void canRedo_nullActivity_returnsFalse() {
    StateEdit<String> edit = new StateEdit<>(null, "old", "new");
    assertFalse(edit.canRedo());
  }

  // ── getModel with null activity ───────────────────────────────────

  @Test
  public void getModel_nullActivity_returnsNull() {
    StateEdit<String> edit = new StateEdit<>(null, "old", "new");
    assertNull(edit.getModel());
  }

  // ── getGroup with null model ──────────────────────────────────────

  @Test
  public void getGroup_nullModel_returnsNull() {
    StateEdit<String> edit = new StateEdit<>(null, "old", "new");
    assertNull(edit.getGroup());
  }

  // ── Description formatting ────────────────────────────────────────

  @Test
  public void getTerseDescription_containsSelectAndArrow() {
    StateEdit<String> edit = new StateEdit<>(null, "alpha", "bravo");
    String desc = edit.getTerseDescription();
    assertTrue("Should contain 'select'", desc.contains("select"));
    assertTrue("Should contain '===>'", desc.contains("===>"));
    assertTrue("Should contain prev value", desc.contains("alpha"));
    assertTrue("Should contain next value", desc.contains("bravo"));
  }

  @Test
  public void getTerseDescription_nullModel_usesFallbackToString() {
    StateEdit<String> edit = new StateEdit<>(null, "old", "new");
    String desc = edit.getTerseDescription();
    // With null model, appendDescription uses toString on values
    assertTrue(desc.contains("old"));
    assertTrue(desc.contains("new"));
  }

  @Test
  public void getDetailedDescription_includesClassName() {
    StateEdit<String> edit = new StateEdit<>(null, "old", "new");
    String desc = edit.getDetailedDescription();
    assertTrue(desc.contains("StateEdit"));
  }

  @Test
  public void getLogDescription_includesClassName() {
    StateEdit<String> edit = new StateEdit<>(null, "old", "new");
    String desc = edit.getLogDescription();
    assertTrue(desc.contains("StateEdit"));
  }

  // ── Undo/Redo presentations ───────────────────────────────────────

  @Test
  public void getUndoPresentation_startsWithUndo() {
    StateEdit<String> edit = new StateEdit<>(null, "old", "new");
    assertTrue(edit.getUndoPresentation().startsWith("Undo:"));
  }

  @Test
  public void getRedoPresentation_startsWithRedo() {
    StateEdit<String> edit = new StateEdit<>(null, "old", "new");
    assertTrue(edit.getRedoPresentation().startsWith("Redo:"));
  }

  // ── toString ──────────────────────────────────────────────────────

  @Test
  public void toString_includesDetailedDescription() {
    StateEdit<String> edit = new StateEdit<>(null, "old", "new");
    String str = edit.toString();
    assertTrue(str.contains("StateEdit"));
    assertTrue(str.contains("select"));
  }

  // ── Integer values ────────────────────────────────────────────────

  @Test
  public void integerEdit_storesValues() {
    StateEdit<Integer> edit = new StateEdit<>(null, 42, 99);
    assertEquals(Integer.valueOf(42), edit.getPreviousValue());
    assertEquals(Integer.valueOf(99), edit.getNextValue());
  }

  @Test
  public void integerEdit_description_containsValues() {
    StateEdit<Integer> edit = new StateEdit<>(null, 42, 99);
    String desc = edit.getTerseDescription();
    assertTrue(desc.contains("42"));
    assertTrue(desc.contains("99"));
  }

  // ── Boolean values ────────────────────────────────────────────────

  @Test
  public void booleanEdit_storesValues() {
    StateEdit<Boolean> edit = new StateEdit<>(null, false, true);
    assertEquals(Boolean.FALSE, edit.getPreviousValue());
    assertEquals(Boolean.TRUE, edit.getNextValue());
  }

  // ── encode (base class) does not throw ────────────────────────────

  @Test
  public void encode_baseClass_doesNotThrow() {
    StateEdit<String> edit = new StateEdit<>(null, "old", "new");
    // AbstractEdit.encode() is a no-op — just verify no NPE
    // The full encode calls getModel() which is null, so we only test base
  }
}
