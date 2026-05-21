package org.alice.stageide.program;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Structural tests for RunProgramContext.
 */
public class RunProgramContextStructureTest {

  @Test
  public void classIsAccessible() throws ClassNotFoundException {
    assertNotNull(Class.forName("org.alice.stageide.program.RunProgramContext"));
  }

  @Test
  public void isPublic() throws ClassNotFoundException {
    assertTrue(Modifier.isPublic(
      Class.forName("org.alice.stageide.program.RunProgramContext").getModifiers()));
  }

  @Test
  public void isNotAbstract() throws ClassNotFoundException {
    assertFalse(Modifier.isAbstract(
      Class.forName("org.alice.stageide.program.RunProgramContext").getModifiers()));
  }

  @Test
  public void hasConstructor() throws ClassNotFoundException {
    assertTrue(Class.forName("org.alice.stageide.program.RunProgramContext")
      .getDeclaredConstructors().length > 0);
  }

  @Test
  public void inExpectedPackage() throws ClassNotFoundException {
    assertEquals("org.alice.stageide.program",
      Class.forName("org.alice.stageide.program.RunProgramContext").getPackage().getName());
  }
}
