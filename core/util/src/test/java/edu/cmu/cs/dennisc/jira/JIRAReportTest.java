package edu.cmu.cs.dennisc.jira;

import edu.cmu.cs.dennisc.issue.Issue;
import edu.cmu.cs.dennisc.issue.IssueType;
import org.junit.Test;

import static org.junit.Assert.*;

public class JIRAReportTest {

  private Issue.Builder baseIssue() {
    return new Issue.Builder()
        .type(IssueType.BUG)
        .summary("Test summary")
        .description("Test description")
        .steps("Step 1\nStep 2")
        .environment("Linux")
        .version("3.7.0")
        .reportedBy("tester")
        .emailAddress("test@example.com");
  }

  @Test
  public void constructor_setsProjectKey() {
    JIRAReport report = new JIRAReport(baseIssue().build(), "ALICE");
    assertEquals("ALICE", report.getProjectKey());
  }

  @Test
  public void getType_returnsBug() {
    JIRAReport report = new JIRAReport(baseIssue().build(), "PROJ");
    assertEquals(IssueType.BUG, report.getType());
  }

  @Test
  public void getTypeID_bug_returns1() {
    JIRAReport report = new JIRAReport(baseIssue().type(IssueType.BUG).build(), "P");
    assertEquals(1, report.getTypeID());
  }

  @Test
  public void getTypeID_newFeature_returns2() {
    JIRAReport report = new JIRAReport(baseIssue().type(IssueType.NEW_FEATURE).build(), "P");
    assertEquals(2, report.getTypeID());
  }

  @Test
  public void getTypeID_improvement_returns4() {
    JIRAReport report = new JIRAReport(baseIssue().type(IssueType.IMPROVEMENT).build(), "P");
    assertEquals(4, report.getTypeID());
  }

  @Test
  public void getDescription_returnsValue() {
    JIRAReport report = new JIRAReport(baseIssue().description("desc text").build(), "P");
    assertEquals("desc text", report.getDescription());
  }

  @Test
  public void getSteps_returnsValue() {
    JIRAReport report = new JIRAReport(baseIssue().steps("do this").build(), "P");
    assertEquals("do this", report.getSteps());
  }

  @Test
  public void getEnvironment_returnsValue() {
    JIRAReport report = new JIRAReport(baseIssue().environment("Win10").build(), "P");
    assertEquals("Win10", report.getEnvironment());
  }

  @Test
  public void getAffectsVersions_withVersion() {
    JIRAReport report = new JIRAReport(baseIssue().version("3.7.0").build(), "P");
    String[] versions = report.getAffectsVersions();
    assertEquals(1, versions.length);
    assertEquals("3.7.0", versions[0]);
  }

  @Test
  public void getAffectsVersions_nullVersion() {
    JIRAReport report = new JIRAReport(baseIssue().version(null).build(), "P");
    String[] versions = report.getAffectsVersions();
    assertEquals(0, versions.length);
  }

  @Test
  public void getAffectsVersionText_withVersion() {
    JIRAReport report = new JIRAReport(baseIssue().version("1.0").build(), "P");
    assertEquals("1.0", report.getAffectsVersionText());
  }

  @Test
  public void getAffectsVersionText_noVersion() {
    JIRAReport report = new JIRAReport(baseIssue().version(null).build(), "P");
    assertEquals("", report.getAffectsVersionText());
  }

  @Test
  public void getReportedBy_returnsValue() {
    JIRAReport report = new JIRAReport(baseIssue().reportedBy("alice").build(), "P");
    assertEquals("alice", report.getReportedBy());
  }

  @Test
  public void getEmailAddress_returnsValue() {
    JIRAReport report = new JIRAReport(baseIssue().emailAddress("a@b.com").build(), "P");
    assertEquals("a@b.com", report.getEmailAddress());
  }

  @Test
  public void getException_withThrowable() {
    Issue issue = baseIssue()
        .threadAndThrowable(Thread.currentThread(), new RuntimeException("boom"))
        .build();
    JIRAReport report = new JIRAReport(issue, "P");
    assertTrue(report.getException().contains("boom"));
    assertTrue(report.getException().contains("RuntimeException"));
  }

  @Test
  public void getException_nullThrowable() {
    JIRAReport report = new JIRAReport(baseIssue().build(), "P");
    assertEquals("", report.getException());
  }

  @Test
  public void getTruncatedSummary_shortSummary() {
    JIRAReport report = new JIRAReport(baseIssue().summary("short").build(), "P");
    assertEquals("short", report.getTruncatedSummary());
  }

  @Test
  public void getTruncatedSummary_longSummary() {
    StringBuilder longSummary = new StringBuilder();
    for (int i = 0; i < 300; i++) {
      longSummary.append('x');
    }
    JIRAReport report = new JIRAReport(baseIssue().summary(longSummary.toString()).build(), "P");
    assertEquals(254, report.getTruncatedSummary().length());
  }

  @Test
  public void getCreditedDescription_includesReportedBy() {
    JIRAReport report = new JIRAReport(baseIssue()
        .description("Main desc")
        .reportedBy("Bob")
        .emailAddress("bob@test.com")
        .version("2.0")
        .build(), "P");
    String credited = report.getCreditedDescription();
    assertTrue(credited.contains("Main desc"));
    assertTrue(credited.contains("Reported by: Bob"));
    assertTrue(credited.contains("Email address: bob@test.com"));
    assertTrue(credited.contains("Affects version: 2.0"));
  }

  @Test
  public void getCreditedDescription_nullReportedBy() {
    JIRAReport report = new JIRAReport(baseIssue()
        .reportedBy(null)
        .emailAddress(null)
        .build(), "P");
    String credited = report.getCreditedDescription();
    assertFalse(credited.contains("Reported by:"));
    assertFalse(credited.contains("Email address:"));
  }

  @Test
  public void addAttachment_works() {
    JIRAReport report = new JIRAReport(baseIssue().build(), "P");
    edu.cmu.cs.dennisc.issue.Attachment attachment = new edu.cmu.cs.dennisc.issue.Attachment() {
      @Override public String getFileName() { return "log.txt"; }
      @Override public byte[] getBytes() { return new byte[]{1, 2}; }
    };
    report.addAttachment(attachment);
    assertEquals(1, report.getAttachments().size());
    report.removeAttachment(attachment);
    assertEquals(0, report.getAttachments().size());
  }
}
