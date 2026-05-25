package org.alice.netbeans.logging;

import edu.cmu.cs.dennisc.java.util.logging.ConsoleFormatter;
import org.junit.Test;

import java.util.logging.Level;
import java.util.logging.LogRecord;

public class IoLoggingHandlerBehaviorTest {
  @Test
  public void loggingHandlerInitializesPublishesAndFlushesWithoutThrowing() {
    IoLoggingHandler.initialize();
    try {
      IoLoggingHandler handler = new IoLoggingHandler();
      handler.setFormatter(new ConsoleFormatter());

      handler.publish(new LogRecord(Level.INFO, "informational message"));
      handler.publish(new LogRecord(Level.SEVERE, "serious message"));
      IoLoggingHandler.errln("single-line error");
      IoLoggingHandler.errln("joined", 42, new String[] {"values"});
      IoLoggingHandler.printStackTrace(new RuntimeException("boom"));
      handler.flush();
      handler.close();
    } finally {
      IoLoggingHandler.uninitialize();
    }
  }
}
