package org.alice.ide.issue.swing.views;

import edu.cmu.cs.dennisc.issue.IssueType;
import edu.cmu.cs.dennisc.javax.swing.components.JExpandPane;
import edu.cmu.cs.dennisc.javax.swing.components.JFauxHyperlink;
import org.junit.Test;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class InnerClassLogicTest {
  @Test
  public void submitActionInvokesOuterSubmit() {
    TestIssueReportPane pane = new TestIssueReportPane();

    pane.getSubmitAction().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "submit"));

    assertEquals(1, pane.submitCount);
  }

  @Test
  public void systemPropertiesPaneShowsConfiguredPropertiesAndLink() throws Exception {
    TestCaughtExceptionPane pane = new TestCaughtExceptionPane();
    JPanel environmentPane = (JPanel) readField(AbstractCaughtExceptionPane.class, "paneEnvironment", pane);

    assertEquals(IssueReportPane.getSystemPropertiesForEnvironmentField().size() + 1, environmentPane.getComponentCount());
    for (int i = 0; i < IssueReportPane.getSystemPropertiesForEnvironmentField().size(); i++) {
      JLabel label = (JLabel) environmentPane.getComponent(i);
      String propertyName = IssueReportPane.getSystemPropertiesForEnvironmentField().get(i);
      assertTrue(label.getText().startsWith(propertyName + ": "));
    }
    assertTrue(environmentPane.getComponent(environmentPane.getComponentCount() - 1) instanceof JFauxHyperlink);
  }

  @Test
  public void expandPaneUsesInnerClassLabelsAndCenterComponent() throws Exception {
    TestCaughtExceptionPane pane = new TestCaughtExceptionPane();
    JExpandPane expandPane = (JExpandPane) readField(AbstractCaughtExceptionPane.class, "expandPane", pane);
    JPanel topPane = (JPanel) expandPane.getComponent(0);
    JLabel label = (JLabel) topPane.getComponent(0);
    JToggleButton toggle = (JToggleButton) topPane.getComponent(1);

    assertEquals("Can you provide insight into this problem?", label.getText());
    assertFalse(containsComponent(expandPane, expandPane.getCenterComponent()));

    toggle.doClick();

    assertEquals("Please provide insight:", label.getText());
    assertTrue(containsComponent(expandPane, expandPane.getCenterComponent()));
  }

  @Test
  public void summaryFallsBackToThrowableDetailsWhenSummaryFieldIsBlank() {
    TestCaughtExceptionPane pane = new TestCaughtExceptionPane();
    IllegalStateException throwable = new IllegalStateException("boom");
    throwable.setStackTrace(new StackTraceElement[] {
        new StackTraceElement("example.Type", "run", "Sample.java", 42)
    });

    pane.setThreadAndThrowable(Thread.currentThread(), throwable);

    String summary = pane.getSummaryText();
    assertTrue(summary.contains(IllegalStateException.class.getName()));
    assertTrue(summary.contains("message: boom"));
    assertTrue(summary.contains("stack[0]: example.Type.run(Sample.java:42)"));
    assertSame(throwable, pane.getThrowable());
  }

  private static boolean containsComponent(JComponent container, Component target) {
    for (Component component : container.getComponents()) {
      if (component == target) {
        return true;
      }
    }
    return false;
  }

  private static Object readField(Class<?> type, String fieldName, Object instance) throws Exception {
    Field field = type.getDeclaredField(fieldName);
    field.setAccessible(true);
    return field.get(instance);
  }

  private static final class TestIssueReportPane extends IssueReportPane {
    private int submitCount;

    @Override
    protected int getPreferredDescriptionHeight() {
      return 64;
    }

    @Override
    protected int getPreferredStepsHeight() {
      return 64;
    }

    @Override
    protected boolean isSummaryRequired() {
      return false;
    }

    @Override
    protected String getJIRAProjectKey() {
      return "TEST";
    }

    @Override
    protected IssueType getIssueType() {
      return IssueType.BUG;
    }

    @Override
    protected String getEnvironmentText() {
      return "test-environment";
    }

    @Override
    protected Thread getThread() {
      return Thread.currentThread();
    }

    @Override
    protected Throwable getThrowable() {
      return null;
    }

    @Override
    protected String[] getAffectsVersions() {
      return new String[] {"test-version"};
    }

    @Override
    protected String getSMTPReplyTo() {
      return "";
    }

    @Override
    protected String getSMTPReplyToPersonal() {
      return "";
    }

    @Override
    protected boolean isInclusionOfCompleteSystemPropertiesDesired() {
      return false;
    }

    @Override
    protected void submit() {
      this.submitCount++;
    }
  }

  private static final class TestCaughtExceptionPane extends AbstractCaughtExceptionPane {
    @Override
    protected String getJIRAProjectKey() {
      return "TEST";
    }

    @Override
    protected String[] getAffectsVersions() {
      return new String[] {"test-version"};
    }
  }
}
