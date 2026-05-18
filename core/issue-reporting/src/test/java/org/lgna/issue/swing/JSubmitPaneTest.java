package org.lgna.issue.swing;

import edu.cmu.cs.dennisc.issue.Issue;
import edu.cmu.cs.dennisc.issue.IssueType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lgna.issue.ApplicationIssueConfiguration;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JToggleButton;
import javax.swing.text.JTextComponent;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class JSubmitPaneTest {
  @BeforeClass
  public static void setHeadless() {
    System.setProperty("java.awt.headless", "true");
  }

  @Test
  public void submitPaneExposesConfigAndDelegatesIssueCreation() throws Exception {
    RecordingConfiguration config = new RecordingConfiguration();
    Thread thread = new Thread("submit-thread");
    IllegalArgumentException originalThrowable = new IllegalArgumentException("submit failure");
    Throwable targetThrowable = new RuntimeException("target");

    JSubmitPane pane = new JSubmitPane(thread, originalThrowable, targetThrowable, config);

    assertSame(config, pane.getConfig());
    assertSame(thread, config.headerThread);
    assertSame(originalThrowable, config.headerOriginalThrowable);
    assertSame(targetThrowable, config.headerResolvedThrowable);
    assertFalse(pane.isSubmitAttempted());

    pane.setSubmitAttempted(true);
    assertTrue(pane.isSubmitAttempted());
    pane.setSubmitAttempted(false);
    assertFalse(pane.isSubmitAttempted());

    JInsightPane insightPane = getField(pane, "insightPane", JInsightPane.class);
    setText(insightPane, "descriptionTextArea", "Submit description");
    setText(insightPane, "stepsTextArea", "Submit steps");
    setText(insightPane, "reportedByTextField", "Submitter");
    setText(insightPane, "emailAddressTextField", "submitter@example.com");

    Issue issue = pane.createIssueBuilder().build();

    assertEquals(IssueType.BUG, issue.getType());
    assertEquals("Submit description", issue.getDescription());
    assertEquals("Submit steps", issue.getSteps());
    assertEquals("Submitter", issue.getReportedBy());
    assertEquals("submitter@example.com", issue.getEmailAddress());
    assertSame(thread, issue.getThread());
    assertSame(originalThrowable, issue.getThrowable());

    JToggleButton toggleButton = getField(pane, "toggleButton", JToggleButton.class);
    assertEquals("Provide details", toggleButton.getText());
    assertNotNull(toggleButton.getIcon());
  }

  @Test
  public void submitButtonInvokesConfigurationAndAddNotifySetsDefaultButton() throws Exception {
    RecordingConfiguration config = new RecordingConfiguration();
    JSubmitPane pane = new JSubmitPane(new Thread("submit-action-thread"), new RuntimeException("boom"), new RuntimeException("target"), config);
    JRootPane rootPane = new JRootPane();
    rootPane.setContentPane(pane);

    pane.addNotify();

    JButton submitButton = getField(pane, "submitButton", JButton.class);
    assertSame(submitButton, rootPane.getDefaultButton());

    submitButton.doClick();

    assertSame(pane, config.submittedPane);
  }

  private static void setText(JInsightPane pane, String fieldName, String value) throws Exception {
    JTextComponent textComponent = getField(pane, fieldName, JTextComponent.class);
    textComponent.setText(value);
  }

  private static <T> T getField(Object instance, String fieldName, Class<T> fieldType) throws Exception {
    Field field = instance.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    return fieldType.cast(field.get(instance));
  }

  private static final class RecordingConfiguration implements ApplicationIssueConfiguration {
    private Thread headerThread;
    private Throwable headerOriginalThrowable;
    private Throwable headerResolvedThrowable;
    private JSubmitPane submittedPane;

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
      this.headerThread = thread;
      this.headerOriginalThrowable = originalThrowable;
      this.headerResolvedThrowable = originalThrowableOrTarget;
      return new JPanel();
    }

    @Override
    public void submit(JSubmitPane jSubmitPane) {
      this.submittedPane = jSubmitPane;
    }
  }
}
