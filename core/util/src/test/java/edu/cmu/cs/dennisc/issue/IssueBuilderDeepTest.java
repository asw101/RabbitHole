package edu.cmu.cs.dennisc.issue;

import org.junit.Test;

import static org.junit.Assert.*;

public class IssueBuilderDeepTest {

  @Test
  public void builder_allFieldsSet() {
    Thread t = Thread.currentThread();
    Throwable throwable = new RuntimeException("test");
    Attachment attachment = new StackTraceAttachment(throwable);

    Issue issue = new Issue.Builder()
        .type(IssueType.BUG)
        .summary("summary text")
        .description("description text")
        .steps("step 1, step 2")
        .environment("Linux x64")
        .threadAndThrowable(t, throwable)
        .version("3.7.0")
        .reportedBy("tester")
        .emailAddress("test@example.com")
        .addAttachment(attachment)
        .build();

    assertEquals(IssueType.BUG, issue.getType());
    assertEquals("summary text", issue.getSummary());
    assertEquals("description text", issue.getDescription());
    assertEquals("step 1, step 2", issue.getSteps());
    assertEquals("Linux x64", issue.getEnvironment());
    assertSame(t, issue.getThread());
    assertSame(throwable, issue.getThrowable());
    assertEquals("3.7.0", issue.getVersion());
    assertEquals("tester", issue.getReportedBy());
    assertEquals("test@example.com", issue.getEmailAddress());
    assertEquals(1, issue.getAttachments().length);
  }

  @Test
  public void builder_noFieldsSet() {
    Issue issue = new Issue.Builder().build();
    assertNull(issue.getType());
    assertNull(issue.getSummary());
    assertNull(issue.getDescription());
    assertNull(issue.getSteps());
    assertNull(issue.getEnvironment());
    assertNull(issue.getThread());
    assertNull(issue.getThrowable());
    assertNull(issue.getVersion());
    assertNull(issue.getReportedBy());
    assertNull(issue.getEmailAddress());
    assertNotNull(issue.getAttachments());
    assertEquals(0, issue.getAttachments().length);
  }

  @Test
  public void builder_multipleAttachments() {
    StackTraceAttachment a1 = new StackTraceAttachment(new RuntimeException("e1"));
    StackTraceAttachment a2 = new StackTraceAttachment(new RuntimeException("e2"));
    StackTraceAttachment a3 = new StackTraceAttachment(new RuntimeException("e3"));

    Issue issue = new Issue.Builder()
        .addAttachment(a1)
        .addAttachment(a2)
        .addAttachment(a3)
        .build();

    assertEquals(3, issue.getAttachments().length);
  }

  @Test
  public void builder_fluentChainingReturnsSameBuilder() {
    Issue.Builder builder = new Issue.Builder();
    assertSame(builder, builder.type(IssueType.IMPROVEMENT));
    assertSame(builder, builder.summary("s"));
    assertSame(builder, builder.description("d"));
    assertSame(builder, builder.steps("st"));
    assertSame(builder, builder.environment("e"));
    assertSame(builder, builder.version("v"));
    assertSame(builder, builder.reportedBy("r"));
    assertSame(builder, builder.emailAddress("e@e.com"));
    assertSame(builder, builder.threadAndThrowable(null, null));
    assertSame(builder, builder.addAttachment(new StackTraceAttachment(new Exception())));
  }

  @Test
  public void builder_typeImprovement() {
    Issue issue = new Issue.Builder().type(IssueType.IMPROVEMENT).build();
    assertEquals(IssueType.IMPROVEMENT, issue.getType());
  }

  @Test
  public void builder_typeNewFeature() {
    Issue issue = new Issue.Builder().type(IssueType.NEW_FEATURE).build();
    assertEquals(IssueType.NEW_FEATURE, issue.getType());
  }

  @Test
  public void builder_overwriteFields() {
    Issue issue = new Issue.Builder()
        .summary("first")
        .summary("second")
        .build();
    assertEquals("second", issue.getSummary());
  }

  @Test
  public void builder_emptyStrings() {
    Issue issue = new Issue.Builder()
        .summary("")
        .description("")
        .build();
    assertEquals("", issue.getSummary());
    assertEquals("", issue.getDescription());
  }

  @Test
  public void builder_threadAndThrowable_separateAccess() {
    Thread t = new Thread("test-thread");
    Throwable th = new IllegalStateException("state error");
    Issue issue = new Issue.Builder()
        .threadAndThrowable(t, th)
        .build();
    assertSame(t, issue.getThread());
    assertSame(th, issue.getThrowable());
  }

  @Test
  public void builder_versionString() {
    Issue issue = new Issue.Builder().version("1.2.3-SNAPSHOT").build();
    assertEquals("1.2.3-SNAPSHOT", issue.getVersion());
  }
}
