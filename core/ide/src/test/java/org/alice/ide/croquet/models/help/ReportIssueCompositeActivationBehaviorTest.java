package org.alice.ide.croquet.models.help;

import edu.cmu.cs.dennisc.issue.IssueType;
import edu.cmu.cs.dennisc.issue.StackTraceAttachment;
import edu.cmu.cs.dennisc.issue.SystemPropertiesAttachment;
import edu.cmu.cs.dennisc.jira.JIRAReport;
import org.alice.ide.croquet.models.help.views.AbstractIssueView;
import org.alice.ide.issue.CurrentProjectAttachment;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReportIssueCompositeActivationBehaviorTest {
  private static final class FakeIssueComposite extends AbstractIssueComposite<AbstractIssueView> {
    private final boolean isPublic;
    private final boolean attachProject;
    private final Throwable throwable;

    private FakeIssueComposite(boolean isPublic, boolean attachProject, Throwable throwable) {
      super(UUID.fromString("11111111-1111-1111-1111-111111111111"), IsModal.FALSE);
      this.isPublic = isPublic;
      this.attachProject = attachProject;
      this.throwable = throwable;
      this.getStepsState().setValueTransactionlessly("Click run, then save.");
    }

    @Override
    protected String getDefaultTitleText() {
      return "Issue";
    }

    @Override
    protected boolean isClearedToSubmitBug() {
      return true;
    }

    @Override
    protected boolean isPublic() {
      return this.isPublic;
    }

    @Override
    protected IssueType getReportType() {
      return IssueType.BUG;
    }

    @Override
    protected String getSummaryText() {
      return "A generated issue";
    }

    @Override
    protected String getDescriptionText() {
      return "The generated issue preserves summary, description, and attachments.";
    }

    @Override
    protected Thread getThread() {
      return Thread.currentThread();
    }

    @Override
    protected Throwable getThrowable() {
      return this.throwable;
    }

    @Override
    protected boolean isProjectAttachmentDesired() {
      return this.attachProject;
    }

    @Override
    protected AbstractIssueView createView() {
      return null;
    }
  }

  @Test
  public void generateIssueUsesPublicProjectKeyAndAllRequestedAttachments() {
    FakeIssueComposite composite = new FakeIssueComposite(true, true, new IllegalStateException("boom"));

    JIRAReport report = composite.generateIssue();

    assertEquals("AIII", report.getProjectKey());
    assertEquals(IssueType.BUG, report.getType());
    assertEquals("A generated issue", report.getTruncatedSummary());
    assertEquals("Click run, then save.", report.getSteps());
    assertEquals(3, report.getAttachments().size());
    assertTrue(report.getAttachments().stream().anyMatch(SystemPropertiesAttachment.class::isInstance));
    assertTrue(report.getAttachments().stream().anyMatch(StackTraceAttachment.class::isInstance));
    assertTrue(report.getAttachments().stream().anyMatch(CurrentProjectAttachment.class::isInstance));
  }

  @Test
  public void generateIssueUsesPrivateProjectKeyWhenReportIsNotPublic() {
    FakeIssueComposite composite = new FakeIssueComposite(false, false, null);

    JIRAReport report = composite.generateIssue();

    assertEquals("AIIIP", report.getProjectKey());
    assertEquals(1, report.getAttachments().size());
    assertTrue(report.getAttachments().stream().allMatch(SystemPropertiesAttachment.class::isInstance));
  }
}
