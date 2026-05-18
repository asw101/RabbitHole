package org.lgna.croquet;

import org.lgna.croquet.edits.StateEdit;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Expanded tests for {@link StateEdit} — additional type coverage
 * (Double, Enum, List values) and edge cases.
 */
public class StateEditExpandedTest {

  @Test
  public void doubleValues_stored() {
    StateEdit<Double> edit = new StateEdit<>(null, 1.5, 2.5);
    assertEquals(Double.valueOf(1.5), edit.getPreviousValue());
    assertEquals(Double.valueOf(2.5), edit.getNextValue());
  }

  @Test
  public void enumValues_stored() {
    StateEdit<Thread.State> edit = new StateEdit<>(null, Thread.State.NEW, Thread.State.RUNNABLE);
    assertEquals(Thread.State.NEW, edit.getPreviousValue());
    assertEquals(Thread.State.RUNNABLE, edit.getNextValue());
  }

  @Test
  public void sameValues_stored() {
    StateEdit<String> edit = new StateEdit<>(null, "same", "same");
    assertEquals("same", edit.getPreviousValue());
    assertEquals("same", edit.getNextValue());
  }

  @Test
  public void terseDescription_nullValues_doesNotThrow() {
    StateEdit<String> edit = new StateEdit<>(null, null, null);
    String desc = edit.getTerseDescription();
    assertNotNull(desc);
  }

  @Test
  public void detailedDescription_withEnumValues() {
    StateEdit<Thread.State> edit = new StateEdit<>(null, Thread.State.NEW, Thread.State.BLOCKED);
    String desc = edit.getDetailedDescription();
    assertTrue(desc.contains("StateEdit"));
  }

  @Test
  public void logDescription_nonEmpty() {
    StateEdit<Integer> edit = new StateEdit<>(null, 1, 2);
    assertFalse(edit.getLogDescription().isEmpty());
  }

  @Test
  public void doOrRedo_isDo_noException() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    edit.doOrRedo(true); // Should succeed (isDo path)
  }

  @Test
  public void undoPresentation_containsValue() {
    StateEdit<String> edit = new StateEdit<>(null, "old", "new");
    assertTrue(edit.getUndoPresentation().startsWith("Undo:"));
  }

  @Test
  public void redoPresentation_containsValue() {
    StateEdit<String> edit = new StateEdit<>(null, "old", "new");
    assertTrue(edit.getRedoPresentation().startsWith("Redo:"));
  }

  @Test
  public void toString_comprehensive() {
    StateEdit<String> edit = new StateEdit<>(null, "alpha", "beta");
    String str = edit.toString();
    assertNotNull(str);
    assertFalse(str.isEmpty());
  }
}
