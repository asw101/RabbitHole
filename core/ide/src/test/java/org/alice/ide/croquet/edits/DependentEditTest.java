package org.alice.ide.croquet.edits;

import org.junit.Test;
import org.lgna.croquet.edits.AbstractEdit;

import static org.junit.Assert.*;

/**
 * Tests for {@link DependentEdit} — wrapper edit that delegates to a ResponsibleModel.
 * Limited to construction; doOrRedo/undo require a wired model.
 */
public class DependentEditTest {

  // ---- construction ----

  @Test
  public void construct_withNullActivity_succeeds() {
    DependentEdit<?> edit = new DependentEdit<>(null);
    assertNotNull(edit);
  }

  // ---- inheritance ----

  @Test
  public void extendsAbstractEdit() {
    DependentEdit<?> edit = new DependentEdit<>(null);
    assertTrue(edit instanceof AbstractEdit);
  }

  // ---- multiple instances ----

  @Test
  public void multipleInstances_areDistinct() {
    DependentEdit<?> edit1 = new DependentEdit<>(null);
    DependentEdit<?> edit2 = new DependentEdit<>(null);
    assertNotSame(edit1, edit2);
  }

  @Test
  public void construct_repeatedCalls_allSucceed() {
    for (int i = 0; i < 10; i++) {
      DependentEdit<?> edit = new DependentEdit<>(null);
      assertNotNull("Iteration " + i, edit);
    }
  }
}
