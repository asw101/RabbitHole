package org.alice.stageide.program;

import org.junit.Test;

import static org.junit.Assert.*;

public class ProgramContextTest {

  @Test
  public void classExists() {
    // ProgramContext is abstract and requires NamedUserType with full VM context.
    // Verify class is loadable and abstract.
    assertTrue(java.lang.reflect.Modifier.isAbstract(ProgramContext.class.getModifiers()));
  }
}
