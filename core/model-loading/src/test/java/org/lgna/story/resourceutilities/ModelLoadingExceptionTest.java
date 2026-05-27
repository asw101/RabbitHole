package org.lgna.story.resourceutilities;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ModelLoadingExceptionTest {
  @Test
  public void messageOnlyConstructorPreservesMessage() {
    ModelLoadingException exception = new ModelLoadingException("unable to load");

    assertEquals("unable to load", exception.getMessage());
  }

  @Test
  public void causeConstructorPreservesMessageAndCause() {
    IllegalArgumentException cause = new IllegalArgumentException("bad mesh");
    ModelLoadingException exception = new ModelLoadingException("unable to load", cause);

    assertEquals("unable to load", exception.getMessage());
    assertSame(cause, exception.getCause());
  }
}
