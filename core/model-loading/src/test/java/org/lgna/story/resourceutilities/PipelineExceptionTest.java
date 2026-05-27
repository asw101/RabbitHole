package org.lgna.story.resourceutilities;

import org.junit.Test;

import static org.junit.Assert.*;

public class PipelineExceptionTest {
  @Test
  public void constructorsCaptureMessageDataAndCause() {
    Throwable cause = new IllegalStateException("boom");
    PipelineException exception = new PipelineException("problem", "cell-data", cause);

    assertEquals("problem", exception.getMessage());
    assertEquals("cell-data", exception.getData());
    assertSame(cause, exception.getCause());
  }

  @Test
  public void spreadsheetInfoAndToStringIncludeContext() {
    PipelineException exception = new PipelineException("problem", "value");
    exception.setSpreadsheetInfo("B", 7);

    assertEquals("B", exception.getColumnName());
    assertEquals(7, exception.getRowNumber());
    assertTrue(exception.toString().contains("COL: B"));
    assertTrue(exception.toString().contains("ROW: 7"));
    assertTrue(exception.toString().contains("DATA: value"));
  }

  @Test
  public void settersReplaceExistingContext() {
    PipelineException exception = new PipelineException("problem");
    exception.setData("new-data");
    exception.setColumnName("C");
    exception.setRowNumber(3);

    assertEquals("new-data", exception.getData());
    assertEquals("C", exception.getColumnName());
    assertEquals(3, exception.getRowNumber());
  }
}
