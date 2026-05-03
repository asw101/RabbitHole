package edu.cmu.cs.dennisc.issue;

import edu.cmu.cs.dennisc.jira.JIRAReport;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IssueReportWorkerTest {
  private static final URI REPORT_SUBMISSION = URI.create("https://example.invalid/");

  @Test
  public void successfulSubmissionCreatesIssueAndUploadsAttachments() {
    RecordingIssueSubmissionService service = new RecordingIssueSubmissionService(0);
    JIRAReport report = createReport();
    report.addAttachment(new TestAttachment("alice.log"));

    Boolean result = createWorker(service, report, 3).doInBackground();

    assertTrue(result);
    assertEquals(1, service.attempts);
    assertEquals(List.of("alice.log"), service.submittedIssue.attachmentNames);
  }

  @Test
  public void transientSubmissionFailureIsRetried() {
    RecordingIssueSubmissionService service = new RecordingIssueSubmissionService(2);

    Boolean result = createWorker(service, createReport(), 3).doInBackground();

    assertTrue(result);
    assertEquals(3, service.attempts);
  }

  @Test
  public void exhaustedRetriesReturnFailure() {
    RecordingIssueSubmissionService service = new RecordingIssueSubmissionService(3);

    Boolean result = createWorker(service, createReport(), 2).doInBackground();

    assertFalse(result);
    assertEquals(2, service.attempts);
  }

  @Test
  public void configurationFailureIsNotRetried() {
    RecordingIssueSubmissionService service = new RecordingIssueSubmissionService(new IllegalStateException("missing configuration"));

    Boolean result = createWorker(service, createReport(), 3).doInBackground();

    assertFalse(result);
    assertEquals(1, service.attempts);
  }

  private static IssueReportWorker createWorker(RecordingIssueSubmissionService service, JIRAReport report, int maxAttempts) {
    return new IssueReportWorker(new RecordingWorkerListener(), report, REPORT_SUBMISSION, service, new IssueSubmissionRetryPolicy(maxAttempts, 0));
  }

  private static JIRAReport createReport() {
    Issue issue = new Issue.Builder()
        .type(IssueType.BUG)
        .summary("Alice issue")
        .description("Description")
        .steps("Steps")
        .environment("Environment")
        .version("1.0")
        .threadAndThrowable(Thread.currentThread(), null)
        .build();
    return new JIRAReport(issue, "ALICE");
  }

  private static final class RecordingIssueSubmissionService implements IssueSubmissionService {
    private final int failuresBeforeSuccess;
    private final Exception failure;
    private final RecordingSubmittedIssue submittedIssue = new RecordingSubmittedIssue();
    private int attempts;

    private RecordingIssueSubmissionService(int failuresBeforeSuccess) {
      this.failuresBeforeSuccess = failuresBeforeSuccess;
      this.failure = new IOException("temporary failure");
    }

    private RecordingIssueSubmissionService(Exception failure) {
      this.failuresBeforeSuccess = Integer.MAX_VALUE;
      this.failure = failure;
    }

    @Override
    public SubmittedIssue createIssue(URI reportSubmission, JIRAReport jiraReport) throws Exception {
      this.attempts++;
      if (this.attempts <= this.failuresBeforeSuccess) {
        throw this.failure;
      }
      return this.submittedIssue;
    }
  }

  private static final class RecordingSubmittedIssue implements SubmittedIssue {
    private final List<String> attachmentNames = new ArrayList<>();

    @Override
    public void addAttachment(File file) {
      this.attachmentNames.add(file.getPath());
    }
  }

  private static final class RecordingWorkerListener implements WorkerListener {
    @Override
    public void process(List<String> chunks) {
    }

    @Override
    public void done(boolean isSuccessful) {
    }
  }

  private static final class TestAttachment implements Attachment {
    private final String fileName;

    private TestAttachment(String fileName) {
      this.fileName = fileName;
    }

    @Override
    public String getFileName() {
      return this.fileName;
    }

    @Override
    public byte[] getBytes() {
      return new byte[0];
    }
  }
}
