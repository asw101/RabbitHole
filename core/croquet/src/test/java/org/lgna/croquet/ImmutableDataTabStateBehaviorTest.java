package org.lgna.croquet;

import org.junit.Test;
import org.lgna.croquet.codecs.SimpleTabCompositeCodec;
import org.lgna.croquet.views.CompositeView;

import javax.swing.JPanel;

import static org.junit.Assert.*;

public class ImmutableDataTabStateBehaviorTest {
  @Test
  public void selectionChange_updatesCurrentValueAndLifecycleCallbacks() {
    CroquetTestUtils.ensureTestApplication();
    TestTab alpha = new TestTab("alpha");
    TestTab beta = new TestTab("beta");
    ImmutableDataTabState<TestTab> state = new ImmutableDataTabState<>(Application.DOCUMENT_UI_GROUP, CroquetTestUtils.nextTestUUID(), 0, SimpleTabCompositeCodec.getInstance(TestTab.class), alpha, beta);

    state.getSwingModel().setSelectionIndex(1);

    assertSame(beta, state.getValue());
    assertEquals(1, alpha.postDeactivationCount);
    assertEquals(1, beta.preActivationCount);
  }

  @Test
  public void handlePreAndPostActivation_delegateToSelectedTab() {
    CroquetTestUtils.ensureTestApplication();
    TestTab alpha = new TestTab("alpha");
    ImmutableDataTabState<TestTab> state = new ImmutableDataTabState<>(Application.DOCUMENT_UI_GROUP, CroquetTestUtils.nextTestUUID(), 0, SimpleTabCompositeCodec.getInstance(TestTab.class), alpha);

    state.handlePreActivation();
    state.handlePostDeactivation();

    assertEquals(1, alpha.preActivationCount);
    assertEquals(1, alpha.postDeactivationCount);
  }

  public static class TestTab extends SimpleTabComposite<StubView> {
    private final String text;
    private int preActivationCount;
    private int postDeactivationCount;

    public TestTab(String text) {
      super(CroquetTestUtils.nextTestUUID(), IsCloseable.FALSE);
      this.text = text;
    }

    @Override
    protected StubView createView() {
      return new StubView();
    }

    @Override
    public void appendUserRepr(StringBuilder userRepr) {
      userRepr.append(this.text);
    }

    @Override
    public void handlePreActivation() {
      this.preActivationCount++;
      super.handlePreActivation();
    }

    @Override
    public void handlePostDeactivation() {
      this.postDeactivationCount++;
      super.handlePostDeactivation();
    }
  }

  private static final class StubView extends CompositeView<JPanel, Composite<?>> {
    private StubView() {
      super(null);
    }

    @Override
    protected JPanel createAwtComponent() {
      return new JPanel();
    }
  }
}
