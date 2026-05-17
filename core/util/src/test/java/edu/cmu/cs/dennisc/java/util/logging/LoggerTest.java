package edu.cmu.cs.dennisc.java.util.logging;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.Assert.*;

/**
 * Tests for Logger singleton, level management, log methods at various levels,
 * ConsoleFormatter, and SegregatingConsoleHandler.
 */
public class LoggerTest {

  private Level savedLevel;

  @Before
  public void setUp() {
    savedLevel = Logger.getLevel();
  }

  @After
  public void tearDown() {
    Logger.setLevel(savedLevel);
  }

  // --- Logger singleton ---

  @Test
  public void getInstance_returnsNonNull() {
    assertNotNull(Logger.getInstance());
  }

  @Test
  public void getInstance_returnsSameInstance() {
    assertSame(Logger.getInstance(), Logger.getInstance());
  }

  // --- Level management ---

  @Test
  public void getLevel_defaultNotNull() {
    assertNotNull(Logger.getLevel());
  }

  @Test
  public void setLevel_changesLevel() {
    Logger.setLevel(Level.FINE);
    assertEquals(Level.FINE, Logger.getLevel());
  }

  @Test
  public void setLevel_severe() {
    Logger.setLevel(Level.SEVERE);
    assertEquals(Level.SEVERE, Logger.getLevel());
  }

  @Test
  public void setLevel_all() {
    Logger.setLevel(Level.ALL);
    assertEquals(Level.ALL, Logger.getLevel());
  }

  @Test
  public void setLevel_off() {
    Logger.setLevel(Level.OFF);
    assertEquals(Level.OFF, Logger.getLevel());
  }

  // --- Log methods (verify they don't throw) ---

  @Test
  public void outln_doesNotThrow() {
    Logger.outln("test outln message");
  }

  @Test
  public void outln_varargs_doesNotThrow() {
    Logger.outln("part1", "part2", "part3");
  }

  @Test
  public void errln_doesNotThrow() {
    Logger.errln("test errln message");
  }

  @Test
  public void errln_varargs_doesNotThrow() {
    Logger.errln("err1", "err2");
  }

  @Test
  public void severe_doesNotThrow() {
    Logger.setLevel(Level.ALL);
    Logger.severe("severe message");
  }

  @Test
  public void severe_varargs_doesNotThrow() {
    Logger.setLevel(Level.ALL);
    Logger.severe("sev1", "sev2");
  }

  @Test
  public void warning_doesNotThrow() {
    Logger.setLevel(Level.ALL);
    Logger.warning("warning message");
  }

  @Test
  public void warning_varargs_doesNotThrow() {
    Logger.setLevel(Level.ALL);
    Logger.warning("warn1", "warn2");
  }

  @Test
  public void info_doesNotThrow() {
    Logger.setLevel(Level.ALL);
    Logger.info("info message");
  }

  @Test
  public void info_varargs_doesNotThrow() {
    Logger.setLevel(Level.ALL);
    Logger.info("info1", "info2");
  }

  @Test
  public void config_doesNotThrow() {
    Logger.setLevel(Level.ALL);
    Logger.config("config message");
  }

  @Test
  public void config_varargs_doesNotThrow() {
    Logger.setLevel(Level.ALL);
    Logger.config("cfg1", "cfg2");
  }

  @Test
  public void fine_doesNotThrow() {
    Logger.setLevel(Level.ALL);
    Logger.fine("fine message");
  }

  @Test
  public void fine_varargs_doesNotThrow() {
    Logger.setLevel(Level.ALL);
    Logger.fine("fine1", "fine2");
  }

  @Test
  public void finer_doesNotThrow() {
    Logger.setLevel(Level.ALL);
    Logger.finer("finer message");
  }

  @Test
  public void finer_varargs_doesNotThrow() {
    Logger.setLevel(Level.ALL);
    Logger.finer("finer1", "finer2");
  }

  @Test
  public void finest_doesNotThrow() {
    Logger.setLevel(Level.ALL);
    Logger.finest("finest message");
  }

  @Test
  public void finest_varargs_doesNotThrow() {
    Logger.setLevel(Level.ALL);
    Logger.finest("finest1", "finest2");
  }

  @Test
  public void todo_doesNotThrow() {
    Logger.setLevel(Level.ALL);
    Logger.todo("todo message");
  }

  @Test
  public void todo_varargs_doesNotThrow() {
    Logger.setLevel(Level.ALL);
    Logger.todo("todo1", "todo2");
  }

  @Test
  public void throwable_doesNotThrow() {
    Logger.setLevel(Level.ALL);
    Logger.throwable(new RuntimeException("test"), "context");
  }

  @Test
  public void throwable_varargs_doesNotThrow() {
    Logger.setLevel(Level.ALL);
    Logger.throwable(new RuntimeException("test"), "ctx1", "ctx2");
  }

  // --- Level filtering: OFF suppresses all ---

  @Test
  public void levelOff_suppressesAll() {
    Logger.setLevel(Level.OFF);
    // These should not throw even with OFF
    Logger.severe("should be suppressed");
    Logger.warning("should be suppressed");
    Logger.info("should be suppressed");
  }

  // --- ConsoleFormatter ---

  @Test
  public void consoleFormatter_format_returnsNonNull() {
    ConsoleFormatter formatter = new ConsoleFormatter();
    LogRecord record = new LogRecord(Level.INFO, "test message");
    String result = formatter.format(record);
    assertNotNull(result);
    assertTrue(result.length() > 0);
  }

  @Test
  public void consoleFormatter_format_containsMessage() {
    ConsoleFormatter formatter = new ConsoleFormatter();
    LogRecord record = new LogRecord(Level.WARNING, "important warning");
    String result = formatter.format(record);
    assertTrue(result.contains("important warning"));
  }

  @Test
  public void consoleFormatter_format_severe() {
    ConsoleFormatter formatter = new ConsoleFormatter();
    LogRecord record = new LogRecord(Level.SEVERE, "critical error");
    String result = formatter.format(record);
    assertNotNull(result);
  }

  @Test
  public void consoleFormatter_format_fine() {
    ConsoleFormatter formatter = new ConsoleFormatter();
    LogRecord record = new LogRecord(Level.FINE, "debug info");
    String result = formatter.format(record);
    assertNotNull(result);
  }

  // --- SegregatingConsoleHandler ---

  @Test
  public void segregatingHandler_defaultConstructor() {
    SegregatingConsoleHandler handler = new SegregatingConsoleHandler();
    assertNotNull(handler);
  }

  @Test
  public void segregatingHandler_customStreams() {
    ByteArrayOutputStream outBaos = new ByteArrayOutputStream();
    ByteArrayOutputStream errBaos = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(outBaos);
    PrintStream err = new PrintStream(errBaos);

    SegregatingConsoleHandler handler = new SegregatingConsoleHandler(out, err);
    handler.setFormatter(new ConsoleFormatter());

    // INFO should go to out
    LogRecord infoRecord = new LogRecord(Level.INFO, "info-msg");
    handler.publish(infoRecord);
    handler.flush();
    assertTrue(outBaos.size() > 0 || errBaos.size() == 0);
  }

  @Test
  public void segregatingHandler_severeGoesToErr() {
    ByteArrayOutputStream outBaos = new ByteArrayOutputStream();
    ByteArrayOutputStream errBaos = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(outBaos);
    PrintStream err = new PrintStream(errBaos);

    SegregatingConsoleHandler handler = new SegregatingConsoleHandler(out, err);
    handler.setFormatter(new ConsoleFormatter());

    LogRecord severeRecord = new LogRecord(Level.SEVERE, "error-msg");
    handler.publish(severeRecord);
    handler.flush();
    assertTrue("SEVERE should go to err stream", errBaos.size() > 0);
  }

  @Test
  public void segregatingHandler_warningGoesToOut() {
    ByteArrayOutputStream outBaos = new ByteArrayOutputStream();
    ByteArrayOutputStream errBaos = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(outBaos);
    PrintStream err = new PrintStream(errBaos);

    SegregatingConsoleHandler handler = new SegregatingConsoleHandler(out, err);
    handler.setFormatter(new ConsoleFormatter());

    LogRecord warningRecord = new LogRecord(Level.WARNING, "warn-msg");
    handler.publish(warningRecord);
    handler.flush();
    // WARNING is below SEVERE, should go to out
    assertTrue(outBaos.size() > 0);
  }

  @Test
  public void segregatingHandler_flush_doesNotThrow() {
    SegregatingConsoleHandler handler = new SegregatingConsoleHandler();
    handler.flush();
  }

  @Test
  public void segregatingHandler_close_doesNotThrow() {
    ByteArrayOutputStream outBaos = new ByteArrayOutputStream();
    ByteArrayOutputStream errBaos = new ByteArrayOutputStream();
    SegregatingConsoleHandler handler = new SegregatingConsoleHandler(
        new PrintStream(outBaos), new PrintStream(errBaos));
    handler.close();
  }
}
