package org.lgna.croquet.edits;

import org.junit.Test;
import org.lgna.croquet.State;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link StateEdit} — previous/next values,
 * canUndo/canRedo with null model, description formatting, and toString.
 */
public class StateEditCoverageTest {

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

  @Test
  public void constructor_integerValues() {
    StateEdit<Integer> edit = new StateEdit<>(null, 1, 2);
    assertEquals(Integer.valueOf(1), edit.getPreviousValue());
    assertEquals(Integer.valueOf(2), edit.getNextValue());
  }

  // ── canUndo / canRedo ─────────────────────────────────────────────

  @Test
  public void canUndo_nullModel_returnsFalse() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    assertFalse(edit.canUndo());
  }

  @Test
  public void canRedo_nullModel_returnsFalse() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    assertFalse(edit.canRedo());
  }

  // ── Description ───────────────────────────────────────────────────

  @Test
  public void terseDescription_containsArrow() {
    StateEdit<String> edit = new StateEdit<>(null, "old", "new");
    String desc = edit.getTerseDescription();
    assertTrue(desc.contains("===>"));
  }

  @Test
  public void terseDescription_containsValues() {
    StateEdit<String> edit = new StateEdit<>(null, "alpha", "beta");
    String desc = edit.getTerseDescription();
    assertTrue(desc.contains("alpha"));
    assertTrue(desc.contains("beta"));
  }

  @Test
  public void terseDescription_nullValues_containsNull() {
    StateEdit<String> edit = new StateEdit<>(null, null, null);
    String desc = edit.getTerseDescription();
    assertTrue(desc.contains("null"));
  }

  @Test
  public void redoPresentation_startsWithRedo() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    assertTrue(edit.getRedoPresentation().startsWith("Redo:"));
  }

  @Test
  public void undoPresentation_startsWithUndo() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    assertTrue(edit.getUndoPresentation().startsWith("Undo:"));
  }

  @Test
  public void detailedDescription_includesClassName() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    assertTrue(edit.getDetailedDescription().contains("StateEdit"));
  }

  // ── toString ──────────────────────────────────────────────────────

  @Test
  public void toString_matchesDetailedDescription() {
    StateEdit<String> edit = new StateEdit<>(null, "old", "new");
    assertEquals(edit.getDetailedDescription(), edit.toString());
  }

  // ── Model / Group ─────────────────────────────────────────────────

  @Test
  public void getModel_nullActivity_returnsNull() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    assertNull(edit.getModel());
  }

  @Test
  public void getGroup_nullActivity_returnsNull() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    assertNull(edit.getGroup());
  }

  // ── Encode ────────────────────────────────────────────────────────

  @Test
  public void encode_nullModel_noException() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    // encode calls super which is no-op, then tries to call state methods
    // With null model, the encode for prev/next will fail
    // Just verify the super.encode path works
    edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder encoder = new edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder();
    try {
      edit.encode(encoder);
      fail("Expected NullPointerException when model is null");
    } catch (NullPointerException expected) {
      // StateEdit.encode calls getModel().encodeValue which NPEs with null model
    }
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_extendsAbstractEdit() {
    assertTrue(AbstractEdit.class.isAssignableFrom(StateEdit.class));
  }

  @Test
  public void class_isFinal() {
    assertTrue(Modifier.isFinal(StateEdit.class.getModifiers()));
  }

  @Test
  public void class_implementsEdit() {
    assertTrue(Edit.class.isAssignableFrom(StateEdit.class));
  }
}
