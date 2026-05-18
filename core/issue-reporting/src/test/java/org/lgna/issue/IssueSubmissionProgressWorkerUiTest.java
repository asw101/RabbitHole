package org.lgna.issue;

import edu.cmu.cs.dennisc.issue.Issue;
import edu.cmu.cs.dennisc.issue.IssueType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lgna.issue.swing.JProgressPane;
import org.lgna.issue.swing.JSubmitPane;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;

public class IssueSubmissionProgressWorkerUiTest {
  @BeforeClass
  public static void setHeadless() {
    System.setProperty("java.awt.headless", "true");
  }

  @Test
  public void createIssueBuilderDelegatesToOwnerAndProcessAppendsMessages() throws Exception {
    Thread thread = new Thread("worker-ui-thread");
    IllegalStateException originalThrowable = new IllegalStateException("worker boom");
    JSubmitPane owner = new JSubmitPane(thread, originalThrowable, originalThrowable, new StubConfiguration());
    setInsightText(owner, "descriptionTextArea", "Worker description");
    setInsightText(owner, "stepsTextArea", "Worker steps");
    setInsightText(owner, "reportedByTextField", "Worker Reporter");
    setInsightText(owner, "emailAddressTextField", "worker@example.com");
    IssueSubmissionProgressWorker worker = new IssueSubmissionProgressWorker(owner, true);

    Issue issue = worker.createIssueBuilder().build();
    worker.handleProcess_onEventDispatchThread(List.of("queued", "uploading"));

    assertEquals(IssueType.BUG, issue.getType());
    assertEquals("Worker description", issue.getDescription());
    assertEquals("Worker steps", issue.getSteps());
    assertEquals("Worker Reporter", issue.getReportedBy());
    assertEquals("worker@example.com", issue.getEmailAddress());
    assertSame(thread, issue.getThread());
    assertSame(originalThrowable, issue.getThrowable());

    JProgressPane progressPane = getField(worker, "progressPane", JProgressPane.class);
    JTextArea textArea = getField(progressPane, "textArea", JTextArea.class);
    assertNotNull(progressPane);
    assertEquals("queued\nuploading\n", textArea.getText());
  }

  @Test
  public void handleDoneUsesLoggingBranchWhenProgressPaneIsBackgroundedAndHideOwnerDialogRequiresRoot() throws Exception {
    JSubmitPane owner = new JSubmitPane(new Thread("worker-hide-thread"), new RuntimeException("hide"), new RuntimeException("target"), new StubConfiguration());
    IssueSubmissionProgressWorker worker = new IssueSubmissionProgressWorker(owner, false);
    JProgressPane progressPane = new JProgressPane(worker);
    setField(worker, "progressPane", progressPane);
    setField(progressPane, "isBackgrounded", true);

    worker.handleDone_onEventDispatchThread(Boolean.TRUE);

    assertSame(progressPane, getField(worker, "progressPane", JProgressPane.class));
    assertThrows(NullPointerException.class, worker::hideOwnerDialog);
  }

  private static void setInsightText(JSubmitPane submitPane, String fieldName, String value) throws Exception {
    JSubmitPane owner = submitPane;
    Field insightPaneField = owner.getClass().getDeclaredField("insightPane");
    insightPaneField.setAccessible(true);
    Object insightPane = insightPaneField.get(owner);
    Field textField = insightPane.getClass().getDeclaredField(fieldName);
    textField.setAccessible(true);
    ((JTextComponent) textField.get(insightPane)).setText(value);
  }

  private static void setField(Object instance, String fieldName, Object value) throws Exception {
    Field field = instance.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(instance, value);
  }

  private static <T> T getField(Object instance, String fieldName, Class<T> fieldType) throws Exception {
    Field field = instance.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    return fieldType.cast(field.get(instance));
  }

  private static final class StubConfiguration implements ApplicationIssueConfiguration {
    @Override
    public String getApplicationName() {
      return "Test";
    }

    @Override
    public String getDownloadUrlSpec() {
      return "http://test";
    }

    @Override
    public String getDownloadUrlText() {
      return "test";
    }

    @Override
    public String getSubmitActionName() {
      return "submit";
    }

    @Override
    public JPanel createHeaderPane(Thread thread, Throwable originalThrowable, Throwable originalThrowableOrTarget) {
      return new JPanel();
    }

    @Override
    public void submit(JSubmitPane jSubmitPane) {
    }
  }
}
