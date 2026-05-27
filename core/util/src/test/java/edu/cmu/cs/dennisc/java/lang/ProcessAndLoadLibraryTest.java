package edu.cmu.cs.dennisc.java.lang;

import org.junit.Test;

import java.io.IOException;
import java.util.EnumSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ProcessAndLoadLibraryTest {
  @Test
  public void processStartExceptionExposesTheOriginalIoCause() {
    IOException cause = new IOException("missing runtime");
    ProcessStartException exception = new ProcessStartException(cause);

    assertSame(cause, exception.getCause());
    assertEquals("missing runtime", exception.getCause().getMessage());
  }

  @Test
  public void loadLibraryReportStyleEnumeratesAllSupportedModes() {
    EnumSet<LoadLibraryReportStyle> styles = EnumSet.allOf(LoadLibraryReportStyle.class);

    assertEquals(3, styles.size());
    assertTrue(styles.contains(LoadLibraryReportStyle.EXCEPTION));
    assertTrue(styles.contains(LoadLibraryReportStyle.SEVERE));
    assertTrue(styles.contains(LoadLibraryReportStyle.SILENT));
  }
}
