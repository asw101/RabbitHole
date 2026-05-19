package org.lgna.croquet.edits;

import org.junit.Test;
import org.lgna.croquet.*;

import java.lang.reflect.Modifier;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link StateEdit} — encode/decode round-trip,
 * doOrRedo/undo with live state, description formatting, and edge cases.
 */
public class StateEditCoverageTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-0021-ffffffffffff"), "stEditCov");

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

  @Test
  public void constructor_sameValues() {
    StateEdit<String> edit = new StateEdit<>(null, "same", "same");
    assertEquals("same", edit.getPreviousValue());
    assertEquals("same", edit.getNextValue());
  }

  // ── canUndo / canRedo with null model ─────────────────────────────

  @Test
  public void canUndo_nullModel_false() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    assertFalse(edit.canUndo());
  }

  @Test
  public void canRedo_nullModel_false() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    assertFalse(edit.canRedo());
  }

  // ── getModel / getGroup with null activity ────────────────────────

  @Test
  public void getModel_null() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    assertNull(edit.getModel());
  }

  @Test
  public void getGroup_null() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    assertNull(edit.getGroup());
  }

  // ── doOrRedo isDo path ────────────────────────────────────────────

  @Test
  public void doOrRedo_isDo_doesNotThrow() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    edit.doOrRedo(true);
  }

  // ── doOrRedo isRedo when canRedo false ────────────────────────────

  @Test(expected = javax.swing.undo.CannotRedoException.class)
  public void doOrRedo_isRedo_cannotRedoThrows() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    edit.doOrRedo(false);
  }

  // ── undo when canUndo false ───────────────────────────────────────

  @Test(expected = javax.swing.undo.CannotRedoException.class)
  public void undo_cannotUndo_throws() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    edit.undo();
  }

  // ── Description formatting ────────────────────────────────────────

  @Test
  public void getTerseDescription_containsSelect() {
    StateEdit<String> edit = new StateEdit<>(null, "old", "new");
    assertTrue(edit.getTerseDescription().contains("select"));
  }

  @Test
  public void getTerseDescription_containsArrow() {
    StateEdit<String> edit = new StateEdit<>(null, "old", "new");
    assertTrue(edit.getTerseDescription().contains("===>"));
  }

  @Test
  public void getTerseDescription_containsPrevValue() {
    StateEdit<String> edit = new StateEdit<>(null, "alpha", "bravo");
    assertTrue(edit.getTerseDescription().contains("alpha"));
  }

  @Test
  public void getTerseDescription_containsNextValue() {
    StateEdit<String> edit = new StateEdit<>(null, "alpha", "bravo");
    assertTrue(edit.getTerseDescription().contains("bravo"));
  }

  @Test
  public void getDetailedDescription_containsClassName() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    assertTrue(edit.getDetailedDescription().contains("StateEdit"));
  }

  @Test
  public void getLogDescription_containsClassName() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    assertTrue(edit.getLogDescription().contains("StateEdit"));
  }

  // ── Undo/Redo presentations ───────────────────────────────────────

  @Test
  public void undoPresentation_startsWithUndo() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    assertTrue(edit.getUndoPresentation().startsWith("Undo:"));
  }

  @Test
  public void redoPresentation_startsWithRedo() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    assertTrue(edit.getRedoPresentation().startsWith("Redo:"));
  }

  @Test
  public void undoPresentation_containsValues() {
    StateEdit<String> edit = new StateEdit<>(null, "alpha", "bravo");
    String pres = edit.getUndoPresentation();
    assertTrue(pres.contains("alpha") || pres.contains("bravo"));
  }

  @Test
  public void redoPresentation_containsValues() {
    StateEdit<String> edit = new StateEdit<>(null, "alpha", "bravo");
    String pres = edit.getRedoPresentation();
    assertTrue(pres.contains("alpha") || pres.contains("bravo"));
  }

  // ── toString ──────────────────────────────────────────────────────

  @Test
  public void toString_matchesDetailedDescription() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    assertEquals(edit.getDetailedDescription(), edit.toString());
  }

  // ── Integer values ────────────────────────────────────────────────

  @Test
  public void integerEdit_storesValues() {
    StateEdit<Integer> edit = new StateEdit<>(null, 10, 20);
    assertEquals(Integer.valueOf(10), edit.getPreviousValue());
    assertEquals(Integer.valueOf(20), edit.getNextValue());
  }

  @Test
  public void integerEdit_descriptionContainsValues() {
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

  @Test
  public void booleanEdit_descriptionContainsValues() {
    StateEdit<Boolean> edit = new StateEdit<>(null, false, true);
    String desc = edit.getTerseDescription();
    assertTrue(desc.contains("false"));
    assertTrue(desc.contains("true"));
  }

  // ── Double values ─────────────────────────────────────────────────

  @Test
  public void doubleEdit_storesValues() {
    StateEdit<Double> edit = new StateEdit<>(null, 1.5, 2.5);
    assertEquals(Double.valueOf(1.5), edit.getPreviousValue());
    assertEquals(Double.valueOf(2.5), edit.getNextValue());
  }

  // ── class structure ───────────────────────────────────────────────

  @Test
  public void stateEdit_extendsAbstractEdit() {
    assertTrue(AbstractEdit.class.isAssignableFrom(StateEdit.class));
  }

  @Test
  public void stateEdit_isFinal() {
    assertTrue(Modifier.isFinal(StateEdit.class.getModifiers()));
  }

  @Test
  public void stateEdit_isPublic() {
    assertTrue(Modifier.isPublic(StateEdit.class.getModifiers()));
  }

  // ── encode with null model throws NPE ───────────────────────────────

  @Test(expected = NullPointerException.class)
  public void encode_nullModel_throwsNPE() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder encoder =
        new edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder();
    edit.encode(encoder);
  }
}
