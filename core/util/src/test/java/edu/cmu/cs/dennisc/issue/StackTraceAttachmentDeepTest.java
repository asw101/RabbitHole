package edu.cmu.cs.dennisc.issue;

import org.junit.Test;

import static org.junit.Assert.*;

public class StackTraceAttachmentDeepTest {

  @Test
  public void getFileName_returnsStacktraceTxt() {
    assertEquals("stacktrace.txt", new StackTraceAttachment(new RuntimeException("test")).getFileName());
  }

  @Test
  public void getBytes_returnsNonEmptyArray() {
    byte[] bytes = new StackTraceAttachment(new RuntimeException("test error")).getBytes();

    assertNotNull(bytes);
    assertTrue(bytes.length > 0);
  }

  @Test
  public void getBytes_containsExceptionClassName() {
    String content = new String(new StackTraceAttachment(new RuntimeException("specific error")).getBytes());

    assertTrue(content.contains("RuntimeException"));
  }

  @Test
  public void getBytes_containsExceptionMessage() {
    String content = new String(new StackTraceAttachment(new RuntimeException("my unique message 12345")).getBytes());

    assertTrue(content.contains("my unique message 12345"));
  }

  @Test
  public void getBytes_nestedCause() {
    Exception cause = new IllegalStateException("root cause");
    RuntimeException exception = new RuntimeException("wrapper", cause);
    String content = new String(new StackTraceAttachment(exception).getBytes());

    assertTrue(content.contains("RuntimeException"));
    assertTrue(content.contains("IllegalStateException"));
    assertTrue(content.contains("root cause"));
  }

  @Test
  public void getBytes_containsStackFrames() {
    String content = new String(new StackTraceAttachment(new RuntimeException("test")).getBytes());

    assertTrue(content.contains("at "));
  }

  @Test
  public void getFileName_isConsistent() {
    StackTraceAttachment attachment = new StackTraceAttachment(new Exception());

    assertEquals(attachment.getFileName(), attachment.getFileName());
  }

  @Test
  public void getBytes_repeatedCallsReturnEquivalentData() {
    StackTraceAttachment attachment = new StackTraceAttachment(new RuntimeException("repeatable"));

    assertArrayEquals(attachment.getBytes(), attachment.getBytes());
  }

  @Test
  public void getBytes_checkedExceptionIncludesMessage() {
    String content = new String(new StackTraceAttachment(new Exception("checked message")).getBytes());

    assertTrue(content.contains("checked message"));
  }

  @Test
  public void getBytes_defaultRuntimeExceptionIncludesClassName() {
    String content = new String(new StackTraceAttachment(new RuntimeException()).getBytes());

    assertTrue(content.contains("RuntimeException"));
  }
}
