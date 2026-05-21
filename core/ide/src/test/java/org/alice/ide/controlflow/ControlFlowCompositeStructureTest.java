package org.alice.ide.controlflow;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Structural tests for ControlFlowComposite.
 */
public class ControlFlowCompositeStructureTest {

  @Test
  public void classIsAccessible() {
    assertNotNull(ControlFlowComposite.class);
  }

  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(ControlFlowComposite.class.getModifiers()));
  }

  @Test
  public void isNotAbstract() {
    assertFalse(Modifier.isAbstract(ControlFlowComposite.class.getModifiers()));
  }

  @Test
  public void isNotFinal() {
    assertFalse(Modifier.isFinal(ControlFlowComposite.class.getModifiers()));
  }

  @Test
  public void inExpectedPackage() {
    assertEquals("org.alice.ide.controlflow", ControlFlowComposite.class.getPackage().getName());
  }

  @Test
  public void hasConstructors() {
    assertTrue(ControlFlowComposite.class.getDeclaredConstructors().length > 0);
  }
}
