package org.lgna.croquet;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.lgna.croquet.edits.Edit;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.lgna.croquet.history.UserActivity;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.lgna.croquet.views.Panel;
import org.junit.Rule;
import org.junit.rules.Timeout;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.Timeout;

public class OperationWizardDialogCoreCompositeXvfbTest {

  @Rule
  public Timeout globalTimeout = Timeout.seconds(30);
  @Test
  public void performAutoCommitCreatesAndCommitsEditWithoutShowingDialog() {
    XvfbCroquetTestSupport.tryOnEdt(() -> {
      TestOperationWizard wizard = new TestOperationWizard(true);
      wizard.addPage(new AutoAdvancePage(wizard));
      wizard.addPage(new AutoAdvancePage(wizard));

      UserActivity activity = XvfbCroquetTestSupport.newActivity();
      wizard.openingActivity = activity;
      wizard.perform(activity);

      assertEquals(1, wizard.createEditCount);
      assertEquals(0, wizard.showDialogCount);
      assertTrue(activity.isSuccessfullyCompleted());
    });
  }

  @Test
  public void performFallsBackToDialogWhenAnyPageRequiresInteraction() {
    XvfbCroquetTestSupport.tryOnEdt(() -> {
      TestOperationWizard wizard = new TestOperationWizard(true);
      wizard.addPage(new AutoAdvancePage(wizard));
      wizard.addPage(new BlockingPage(wizard));

      wizard.perform(XvfbCroquetTestSupport.newActivity());

      assertEquals(0, wizard.createEditCount);
      assertEquals(1, wizard.showDialogCount);
    });
  }

  private static final class TestOperationWizard extends OperationWizardDialogCoreComposite {
    private final boolean autoCommitWorthAttempting;
    private int createEditCount;
    private int showDialogCount;

    private TestOperationWizard(boolean autoCommitWorthAttempting) {
      super(java.util.UUID.randomUUID(), new WizardPageComposite[0]);
      this.autoCommitWorthAttempting = autoCommitWorthAttempting;
    }

    @Override
    protected boolean isAutoCommitWorthAttempting() {
      return this.autoCommitWorthAttempting;
    }

    @Override
    protected Edit createEdit() {
      this.createEditCount++;
      return null;
    }

    @Override
    protected void showDialog(UserActivity userActivity) {
      this.showDialogCount++;
      this.openingActivity = userActivity;
    }

    @Override
    protected String getDefaultTitleText() {
      return "wizard";
    }
  }

  private static class AutoAdvancePage extends WizardPageComposite<Panel, TestOperationWizard> {
    private AutoAdvancePage(TestOperationWizard owner) {
      super(java.util.UUID.randomUUID(), owner);
    }

    @Override
    public Status getPageStatus() {
      return IS_GOOD_TO_GO_STATUS;
    }

    @Override
    protected boolean isOptional() {
      return true;
    }

    @Override
    protected boolean isAutoAdvanceWorthAttempting() {
      return true;
    }

    @Override
    public void resetData() {
    }

    @Override
    protected Panel createView() {
      return new XvfbCroquetTestSupport.TestPanel(this, 160, 80);
    }
  }

  private static final class BlockingPage extends AutoAdvancePage {
    private BlockingPage(TestOperationWizard owner) {
      super(owner);
    }

    @Override
    protected boolean isAutoAdvanceWorthAttempting() {
      return false;
    }
  }
}
