package org.lgna.issue.swing;

import edu.cmu.cs.dennisc.issue.Issue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lgna.issue.IssueSubmissionProgressWorker;

import javax.swing.JButton;
import javax.swing.JRootPane;
import javax.swing.JTextArea;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class JProgressPaneTest {
  @BeforeClass
  public static void setHeadless() {
    System.setProperty("java.awt.headless", "true");
  }

  @Test
  public void addMessageAppendsLinesAndAddNotifySetsDefaultButton() throws Exception {
    RecordingWorker worker = new RecordingWorker();
    JProgressPane pane = new JProgressPane(worker);
    JRootPane rootPane = new JRootPane();
    rootPane.setContentPane(pane);

    assertFalse(pane.isBackgrounded());

    pane.addMessage("first");
    pane.addMessage("second");
    pane.addNotify();

    JTextArea textArea = getField(pane, "textArea", JTextArea.class);
    JButton runInBackgroundButton = getField(pane, "runInBackgroundButton", JButton.class);
    assertEquals("first\nsecond\n", textArea.getText());
    assertSame(runInBackgroundButton, rootPane.getDefaultButton());
  }

  @Test
  public void runInBackgroundActionMarksBackgroundedBeforeMissingRootStopsProcessing() throws Exception {
    RecordingWorker worker = new RecordingWorker();
    JProgressPane pane = new JProgressPane(worker);

    JButton runInBackgroundButton = getField(pane, "runInBackgroundButton", JButton.class);
    assertThrows(NullPointerException.class, runInBackgroundButton::doClick);

    assertTrue(pane.isBackgrounded());
    assertFalse(worker.hideOwnerDialogCalled);
  }

  private static <T> T getField(Object instance, String fieldName, Class<T> fieldType) throws Exception {
    Field field = instance.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    return fieldType.cast(field.get(instance));
  }

  private static final class RecordingWorker extends IssueSubmissionProgressWorker {
    private boolean hideOwnerDialogCalled;

    private RecordingWorker() {
      super(null, false);
    }

    @Override
    protected Boolean doInternal_onBackgroundThread(Issue.Builder issueBuilder) {
      return Boolean.TRUE;
    }

    @Override
    public void hideOwnerDialog() {
      this.hideOwnerDialogCalled = true;
    }
  }
}
