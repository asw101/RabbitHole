package org.lgna.common;

import org.junit.Test;

import static org.junit.Assert.*;

public class ProgramClosedExceptionTest {

  @Test
  public void defaultConstructor() {
    ProgramClosedException ex = new ProgramClosedException();
    assertNull(ex.getMessage());
    assertNull(ex.getCause());
  }

  @Test
  public void messageConstructor() {
    ProgramClosedException ex = new ProgramClosedException("closed");
    assertEquals("closed", ex.getMessage());
    assertNull(ex.getCause());
  }

  @Test
  public void messageAndCauseConstructor() {
    Throwable cause = new IllegalStateException("root");
    ProgramClosedException ex = new ProgramClosedException("msg", cause);
    assertEquals("msg", ex.getMessage());
    assertSame(cause, ex.getCause());
  }

  @Test
  public void isRuntimeException() {
    ProgramClosedException ex = new ProgramClosedException();
    assertTrue(ex instanceof RuntimeException);
  }

  @Test
  public void invokeAndCatch_catchesProgramClosedException() {
    // Should not throw — ProgramClosedException is caught internally
    ProgramClosedException.invokeAndCatchProgramClosedException(() -> {
      throw new ProgramClosedException("test");
    });
  }

  @Test
  public void invokeAndCatch_catchesWrappedProgramClosedException() {
    // A RuntimeException whose cause is ProgramClosedException should be caught
    ProgramClosedException.invokeAndCatchProgramClosedException(() -> {
      throw new RuntimeException(new ProgramClosedException("wrapped"));
    });
  }

  @Test
  public void invokeAndCatch_catchesDeeplyWrappedProgramClosedException() {
    ProgramClosedException.invokeAndCatchProgramClosedException(() -> {
      throw new RuntimeException(new RuntimeException(new ProgramClosedException("deep")));
    });
  }

  @Test(expected = RuntimeException.class)
  public void invokeAndCatch_rethrowsNonProgramClosedException() {
    ProgramClosedException.invokeAndCatchProgramClosedException(() -> {
      throw new IllegalArgumentException("not program closed");
    });
  }

  @Test
  public void invokeAndCatch_normalRunnable() {
    final boolean[] ran = {false};
    ProgramClosedException.invokeAndCatchProgramClosedException(() -> ran[0] = true);
    assertTrue(ran[0]);
  }

  @Test(expected = RuntimeException.class)
  public void invokeAndCatch_rethrowsRuntimeWithNullCause() {
    ProgramClosedException.invokeAndCatchProgramClosedException(() -> {
      throw new RuntimeException("no cause");
    });
  }

  @Test
  public void invokeAndCatch_directProgramClosedExceptionIsCaught() {
    // Direct throw of ProgramClosedException (which is a RuntimeException)
    ProgramClosedException.invokeAndCatchProgramClosedException(() -> {
      throw new ProgramClosedException("direct");
    });
    // No exception should escape
  }
}
