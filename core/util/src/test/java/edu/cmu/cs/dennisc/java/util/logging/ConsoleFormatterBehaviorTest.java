package edu.cmu.cs.dennisc.java.util.logging;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConsoleFormatterBehaviorTest {

  @Test
  public void getStackTraceIndexSkipsLoggerFrames() throws Exception {
    ConsoleFormatter formatter = new ConsoleFormatter();
    Method method = ConsoleFormatter.class.getDeclaredMethod("getStackTraceIndex", StackTraceElement[].class);
    method.setAccessible(true);
    StackTraceElement[] stack = {
        new StackTraceElement("before.Logger", "call", "Before.java", 10),
        new StackTraceElement(Logger.class.getName(), "first", "Logger.java", 20),
        new StackTraceElement(Logger.class.getName(), "second", "Logger.java", 21),
        new StackTraceElement("after.Logger", "done", "After.java", 30)
    };

    int index = (Integer) method.invoke(formatter, new Object[] {stack});

    assertEquals(3, index);
  }

  @Test
  public void formatWithoutLoggerFramesReturnsSingleLineMessage() {
    ConsoleFormatter formatter = new ConsoleFormatter();

    String formatted = formatter.format(new LogRecord(Level.INFO, "hello"));

    assertEquals("INFO: hello\n", formatted);
  }

  @Test
  public void formatIncludesStackTraceWhenConfiguredClassNameMatchesCaller() throws Exception {
    Field field = ConsoleFormatter.class.getDeclaredField("CLASS_NAME");
    field.setAccessible(true);
    String previous = (String) field.get(null);
    field.set(null, ConsoleFormatterBehaviorTest.class.getName());
    try {
      String formatted = formatSevereFromHelper();
      assertTrue(formatted.startsWith("SEVERE: boom\n"));
      assertTrue(formatted.contains("\tat "));
      assertFalse(formatted.equals("SEVERE: boom\n"));
    } finally {
      field.set(null, previous);
    }
  }

  private String formatSevereFromHelper() {
    return new ConsoleFormatter().format(new LogRecord(Level.SEVERE, "boom"));
  }
}
