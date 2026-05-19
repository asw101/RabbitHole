package edu.cmu.cs.dennisc.issue;

import org.junit.Test;

import static org.junit.Assert.*;

public class IssueDeepTest {

  @Test
  public void builder_returnsFluentInterface() {
    Issue.Builder builder = new Issue.Builder();
    assertSame(builder, builder.type(IssueType.BUG));
    assertSame(builder, builder.summary("s"));
    assertSame(builder, builder.description("d"));
    assertSame(builder, builder.steps("steps"));
    assertSame(builder, builder.environment("env"));
    assertSame(builder, builder.version("1.0"));
    assertSame(builder, builder.reportedBy("user"));
    assertSame(builder, builder.emailAddress("a@b.com"));
  }

  @Test
  public void builder_multipleAttachments() {
    Attachment a1 = new StackTraceAttachment(new RuntimeException("e1"));
    Attachment a2 = new StackTraceAttachment(new RuntimeException("e2"));
    Attachment a3 = new StackTraceAttachment(new RuntimeException("e3"));

    Issue issue = new Issue.Builder()
        .addAttachment(a1)
        .addAttachment(a2)
        .addAttachment(a3)
        .build();

    assertEquals(3, issue.getAttachments().length);
  }

  @Test
  public void builder_noAttachments_emptyArray() {
    Issue issue = new Issue.Builder().build();
    assertNotNull(issue.getAttachments());
    assertEquals(0, issue.getAttachments().length);
  }

  @Test
  public void builder_allIssueTypes() {
    for (IssueType type : IssueType.values()) {
      Issue issue = new Issue.Builder().type(type).build();
      assertEquals(type, issue.getType());
    }
  }

  @Test
  public void builder_threadAndThrowable() {
    Thread t = new Thread("test-thread");
    Throwable ex = new IllegalStateException("bad state");
    Issue issue = new Issue.Builder()
        .threadAndThrowable(t, ex)
        .build();
    assertSame(t, issue.getThread());
    assertSame(ex, issue.getThrowable());
  }

  @Test
  public void builder_independentInstances() {
    Issue.Builder builder = new Issue.Builder().summary("shared");
    Issue i1 = builder.type(IssueType.BUG).build();
    Issue i2 = builder.type(IssueType.IMPROVEMENT).build();
    assertEquals(IssueType.IMPROVEMENT, i2.getType());
    // i1 keeps BUG because it was already built
    assertEquals(IssueType.BUG, i1.getType());
  }

  // --- IssueType enum ---

  @Test
  public void issueType_valuesCount() {
    assertEquals(3, IssueType.values().length);
  }

  @Test
  public void issueType_valueOf_roundTrips() {
    for (IssueType t : IssueType.values()) {
      assertSame(t, IssueType.valueOf(t.name()));
    }
  }

  @Test
  public void issueType_bug() {
    assertEquals("BUG", IssueType.BUG.name());
  }

  @Test
  public void issueType_improvement() {
    assertEquals("IMPROVEMENT", IssueType.IMPROVEMENT.name());
  }

  @Test
  public void issueType_newFeature() {
    assertEquals("NEW_FEATURE", IssueType.NEW_FEATURE.name());
  }

  // --- StackTraceAttachment ---

  @Test
  public void stackTraceAttachment_fileName() {
    StackTraceAttachment att = new StackTraceAttachment(new RuntimeException("test"));
    assertEquals("stacktrace.txt", att.getFileName());
  }

  @Test
  public void stackTraceAttachment_bytesContainExceptionMessage() {
    StackTraceAttachment att = new StackTraceAttachment(new RuntimeException("unique_error_42"));
    byte[] bytes = att.getBytes();
    assertNotNull(bytes);
    assertTrue(bytes.length > 0);
    String content = new String(bytes);
    assertTrue(content.contains("unique_error_42"));
  }

  @Test
  public void stackTraceAttachment_bytesContainClassName() {
    StackTraceAttachment att = new StackTraceAttachment(new IllegalArgumentException("arg"));
    String content = new String(att.getBytes());
    assertTrue(content.contains("IllegalArgumentException"));
  }

  @Test
  public void stackTraceAttachment_nestedCause() {
    Throwable cause = new RuntimeException("root cause");
    Throwable wrapper = new RuntimeException("wrapper", cause);
    StackTraceAttachment att = new StackTraceAttachment(wrapper);
    String content = new String(att.getBytes());
    assertTrue(content.contains("root cause"));
  }

  @Test
  public void attachment_isInterface() {
    assertTrue(Attachment.class.isInterface());
  }
}
