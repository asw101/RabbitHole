package org.lgna.common;

import org.junit.Test;

import static org.junit.Assert.*;

public class ProgramClosedExceptionDeepTest {

  @Test
  public void defaultConstructor_createsInstance() {
    assertNotNull(new ProgramClosedException());
  }

  @Test
  public void messageConstructor_storesMessage() {
    assertEquals("test message", new ProgramClosedException("test message").getMessage());
  }

  @Test
  public void messageAndCause_storesBoth() {
    Exception cause = new IllegalStateException("cause");
    ProgramClosedException exception = new ProgramClosedException("wrapper", cause);

    assertEquals("wrapper", exception.getMessage());
    assertSame(cause, exception.getCause());
  }

  @Test
  public void messageAndCause_canBeCaughtAsProgramClosedException() {
    Exception cause = new IllegalStateException("cause");

    try {
      throw new ProgramClosedException("wrapper", cause);
    } catch (ProgramClosedException exception) {
      assertEquals("wrapper", exception.getMessage());
      assertSame(cause, exception.getCause());
    }
  }

  @Test
  public void extendsRuntimeException() {
    assertTrue(new ProgramClosedException() instanceof RuntimeException);
  }

  @Test
  public void invokeAndCatch_normalCompletion() {
    final boolean[] ran = { false };

    ProgramClosedException.invokeAndCatchProgramClosedException(() -> ran[0] = true);

    assertTrue(ran[0]);
  }

  @Test
  public void invokeAndCatch_catchesProgramClosedException() {
    ProgramClosedException.invokeAndCatchProgramClosedException(() -> {
      throw new ProgramClosedException("test");
    });
  }

  @Test
  public void invokeAndCatch_catchesDefaultProgramClosedException() {
    ProgramClosedException.invokeAndCatchProgramClosedException(() -> {
      throw new ProgramClosedException();
    });
  }

  @Test(expected = RuntimeException.class)
  public void invokeAndCatch_rethrowsOtherRuntimeException() {
    ProgramClosedException.invokeAndCatchProgramClosedException(() -> {
      throw new IllegalStateException("not program closed");
    });
  }

  @Test
  public void invokeAndCatch_catchesNestedProgramClosedException() {
    ProgramClosedException.invokeAndCatchProgramClosedException(() -> {
      throw new RuntimeException(new ProgramClosedException("nested"));
    });
  }

  @Test
  public void invokeAndCatch_catchesDeeplyNested() {
    ProgramClosedException.invokeAndCatchProgramClosedException(() -> {
      throw new RuntimeException(new RuntimeException(new ProgramClosedException("deep")));
    });
  }

  @Test(expected = RuntimeException.class)
  public void invokeAndCatch_rethrowsDeepNonProgram() {
    ProgramClosedException.invokeAndCatchProgramClosedException(() -> {
      throw new RuntimeException(new IllegalArgumentException("not program"));
    });
  }

  @Test
  public void defaultConstructor_nullMessage() {
    assertNull(new ProgramClosedException().getMessage());
  }

  @Test
  public void defaultConstructor_nullCause() {
    assertNull(new ProgramClosedException().getCause());
  }

  @Test
  public void messageConstructor_nullCause() {
    assertNull(new ProgramClosedException("msg").getCause());
  }

  @Test
  public void toString_containsMessage() {
    assertTrue(new ProgramClosedException("visible message").toString().contains("visible message"));
  }

  @Test
  public void canBeCaughtAsRuntimeException() {
    try {
      throw new ProgramClosedException("test");
    } catch (RuntimeException runtimeException) {
      assertTrue(runtimeException instanceof ProgramClosedException);
    }
  }
}
