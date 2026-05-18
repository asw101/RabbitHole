package org.lgna.croquet.edits;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the {@link Edit} interface contract.
 */
public class EditInterfaceTest {

  @Test
  public void isInterface() {
    assertTrue(Edit.class.isInterface());
  }

  @Test
  public void hasGetGroup() throws NoSuchMethodException {
    assertNotNull(Edit.class.getDeclaredMethod("getGroup"));
  }

  @Test
  public void hasCanUndo() throws NoSuchMethodException {
    assertNotNull(Edit.class.getDeclaredMethod("canUndo"));
  }

  @Test
  public void hasCanRedo() throws NoSuchMethodException {
    assertNotNull(Edit.class.getDeclaredMethod("canRedo"));
  }

  @Test
  public void hasDoOrRedo() throws NoSuchMethodException {
    assertNotNull(Edit.class.getDeclaredMethod("doOrRedo", boolean.class));
  }

  @Test
  public void hasUndo() throws NoSuchMethodException {
    assertNotNull(Edit.class.getDeclaredMethod("undo"));
  }

  @Test
  public void hasGetRedoPresentation() throws NoSuchMethodException {
    assertNotNull(Edit.class.getDeclaredMethod("getRedoPresentation"));
  }

  @Test
  public void hasGetUndoPresentation() throws NoSuchMethodException {
    assertNotNull(Edit.class.getDeclaredMethod("getUndoPresentation"));
  }

  @Test
  public void hasGetTerseDescription() throws NoSuchMethodException {
    assertNotNull(Edit.class.getDeclaredMethod("getTerseDescription"));
  }

  @Test
  public void hasGetDetailedDescription() throws NoSuchMethodException {
    assertNotNull(Edit.class.getDeclaredMethod("getDetailedDescription"));
  }

  @Test
  public void hasGetLogDescription() throws NoSuchMethodException {
    assertNotNull(Edit.class.getDeclaredMethod("getLogDescription"));
  }

  @Test
  public void methodCount() {
    assertEquals(10, Edit.class.getDeclaredMethods().length);
  }
}
