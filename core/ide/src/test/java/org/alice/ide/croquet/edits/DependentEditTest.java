package org.alice.ide.croquet.edits;

import org.lgna.croquet.edits.AbstractEdit;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link DependentEdit} — edit that delegates to a ResponsibleModel.
 * Construction tests only; doOrRedo/undo depend on UserActivity -> CompletionModel chain.
 */
public class DependentEditTest {

  // ---- construction ----

  @Test
  public void construct_withNullUserActivity_succeeds() {
    DependentEdit<?> edit = new DependentEdit<>(null);
    assertNotNull(edit);
  }

  @Test
  public void construct_multipleInstances_areIndependent() {
    DependentEdit<?> edit1 = new DependentEdit<>(null);
    DependentEdit<?> edit2 = new DependentEdit<>(null);
    assertNotSame(edit1, edit2);
  }

  // ---- AbstractEdit inheritance ----

  @Test
  public void isInstanceOfAbstractEdit() {
    DependentEdit<?> edit = new DependentEdit<>(null);
    assertTrue(edit instanceof AbstractEdit);
  }

  // ---- doOrRedoInternal requires ResponsibleModel ----

  @Test(expected = RuntimeException.class)
  public void doOrRedoInternal_withoutModel_throws() {
    DependentEdit<?> edit = new DependentEdit<>(null);
    // getModel() returns null when UserActivity is null,
    // and the cast to ResponsibleModel fails with RuntimeException
    edit.doOrRedoInternal(true);
  }

  @Test(expected = RuntimeException.class)
  public void undoInternal_withoutModel_throws() {
    DependentEdit<?> edit = new DependentEdit<>(null);
    edit.undoInternal();
  }

  // ---- class structure ----

  @Test
  public void isFinal() {
    assertTrue(java.lang.reflect.Modifier.isFinal(DependentEdit.class.getModifiers()));
  }

  @Test
  public void hasPublicConstructor() throws NoSuchMethodException {
    assertNotNull(DependentEdit.class.getConstructor(
        org.lgna.croquet.history.UserActivity.class));
  }

  @Test
  public void hasBinaryDecoderConstructor() throws NoSuchMethodException {
    assertNotNull(DependentEdit.class.getConstructor(
        edu.cmu.cs.dennisc.codec.BinaryDecoder.class, Object.class));
  }
}
