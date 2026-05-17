package edu.cmu.cs.dennisc.java.lang;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests for StreamHandler (package-private) via RuntimeUtilities which
 * creates StreamHandler instances for stdout/stderr capture.
 */
public class StreamHandlerTest {

  private static final File NULL_DIR = null;
  private static final Map<String, String> NULL_ENV = null;

  @Test
  public void streamHandler_capturesStdout() throws InterruptedException {
    ByteArrayOutputStream capture = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(capture);
    int rc = RuntimeUtilities.exec(NULL_DIR, NULL_ENV, new String[]{"echo", "hello stream"}, ps, null);
    assertEquals(0, rc);
    Thread.sleep(200);
    String output = capture.toString().trim();
    assertEquals("hello stream", output);
  }

  @Test
  public void streamHandler_capturesStderr() throws InterruptedException {
    ByteArrayOutputStream errCapture = new ByteArrayOutputStream();
    PrintStream errPs = new PrintStream(errCapture);
    int rc = RuntimeUtilities.exec(NULL_DIR, NULL_ENV, new String[]{"bash", "-c", "echo error-msg >&2"}, null, errPs);
    assertEquals(0, rc);
    Thread.sleep(200);
    String errOutput = errCapture.toString().trim();
    assertEquals("error-msg", errOutput);
  }

  @Test
  public void streamHandler_capturesMultipleLines() throws InterruptedException {
    ByteArrayOutputStream capture = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(capture);
    int rc = RuntimeUtilities.exec(NULL_DIR, NULL_ENV,
        new String[]{"bash", "-c", "echo line1; echo line2; echo line3"}, ps, null);
    assertEquals(0, rc);
    Thread.sleep(200);
    String output = capture.toString().trim();
    String[] lines = output.split("\\R");
    assertEquals(3, lines.length);
    assertEquals("line1", lines[0]);
    assertEquals("line2", lines[1]);
    assertEquals("line3", lines[2]);
  }

  @Test
  public void streamHandler_emptyOutput() throws InterruptedException {
    ByteArrayOutputStream capture = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(capture);
    int rc = RuntimeUtilities.exec(NULL_DIR, NULL_ENV, new String[]{"true"}, ps, null);
    assertEquals(0, rc);
    Thread.sleep(200);
    assertEquals("", capture.toString().trim());
  }

  @Test
  public void streamHandler_nullPrintStream_doesNotThrow() {
    int rc = RuntimeUtilities.exec(NULL_DIR, NULL_ENV, new String[]{"echo", "silent"}, null, null);
    assertEquals(0, rc);
  }

  @Test
  public void streamHandler_bothStreamsCapture() throws InterruptedException {
    ByteArrayOutputStream outCapture = new ByteArrayOutputStream();
    ByteArrayOutputStream errCapture = new ByteArrayOutputStream();
    int rc = RuntimeUtilities.exec(NULL_DIR, NULL_ENV,
        new String[]{"bash", "-c", "echo out-msg; echo err-msg >&2"},
        new PrintStream(outCapture), new PrintStream(errCapture));
    assertEquals(0, rc);
    Thread.sleep(200);
    assertTrue(outCapture.toString().contains("out-msg"));
    assertTrue(errCapture.toString().contains("err-msg"));
  }

  @Test
  public void streamHandler_withEnvironmentVariables() throws InterruptedException {
    ByteArrayOutputStream capture = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(capture);
    Map<String, String> env = new HashMap<>();
    env.put("MY_VAR", "hello_env");
    int rc = RuntimeUtilities.exec(NULL_DIR, env,
        new String[]{"bash", "-c", "echo $MY_VAR"}, ps, null);
    assertEquals(0, rc);
    Thread.sleep(200);
    assertEquals("hello_env", capture.toString().trim());
  }

  @Test
  public void streamHandler_withWorkingDirectory() throws InterruptedException {
    ByteArrayOutputStream capture = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(capture);
    File dir = new File(System.getProperty("user.dir"));
    int rc = RuntimeUtilities.exec(dir, NULL_ENV, new String[]{"pwd"}, ps, null);
    assertEquals(0, rc);
    Thread.sleep(200);
    assertFalse(capture.toString().trim().isEmpty());
  }
}
