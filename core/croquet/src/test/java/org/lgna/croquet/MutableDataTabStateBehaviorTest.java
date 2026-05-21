package org.lgna.croquet;

import org.junit.Test;
import org.lgna.croquet.codecs.SimpleTabCompositeCodec;
import org.lgna.croquet.views.BooleanStateButton;
import org.lgna.croquet.views.CompositeView;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

public class MutableDataTabStateBehaviorTest {
  @Test
  public void selectionChange_deactivatesPreviousTabAndActivatesNextTab() {
    CroquetTestUtils.ensureTestApplication();
    TestTab alpha = new TestTab("alpha");
    TestTab beta = new TestTab("beta");
    MutableDataTabState<TestTab> state = new MutableDataTabState<>(Application.DOCUMENT_UI_GROUP, CroquetTestUtils.nextTestUUID(), 0, SimpleTabCompositeCodec.getInstance(TestTab.class), alpha, beta);

    state.getSwingModel().setSelectionIndex(1);

    assertSame(beta, state.getValue());
    assertEquals(1, alpha.postDeactivationCount);
    assertEquals(1, beta.preActivationCount);
  }

  @Test
  public void handlePreAndPostActivation_delegateToSelectedTab() {
    CroquetTestUtils.ensureTestApplication();
    TestTab alpha = new TestTab("alpha");
    MutableDataTabState<TestTab> state = new MutableDataTabState<>(Application.DOCUMENT_UI_GROUP, CroquetTestUtils.nextTestUUID(), 0, SimpleTabCompositeCodec.getInstance(TestTab.class), alpha);

    state.handlePreActivation();
    state.handlePostDeactivation();

    assertEquals(1, alpha.preActivationCount);
    assertEquals(1, alpha.postDeactivationCount);
  }

  @Test
  public void setItemIconForBothTrueAndFalse_updatesBackedBooleanState() {
    CroquetTestUtils.ensureTestApplication();
    TestTab alpha = new TestTab("alpha");
    MutableDataTabState<TestTab> state = new MutableDataTabState<>(Application.DOCUMENT_UI_GROUP, CroquetTestUtils.nextTestUUID(), 0, SimpleTabCompositeCodec.getInstance(TestTab.class), alpha);
    Icon icon = new ImageIcon(new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB));

    state.setItemIconForBothTrueAndFalse(alpha, icon);
    BooleanState selectedState = state.getItemSelectedState(alpha);

    assertSame(icon, selectedState.getIconFor(true));
    assertSame(icon, selectedState.getIconFor(false));
  }

  public static class TestTab extends SimpleTabComposite<StubView> {
    private final String userRepr;
    private int preActivationCount;
    private int postDeactivationCount;

    public TestTab(String userRepr) {
      super(CroquetTestUtils.nextTestUUID(), IsCloseable.FALSE);
      this.userRepr = userRepr;
    }

    @Override
    protected StubView createView() {
      return new StubView();
    }

    @Override
    public void appendUserRepr(StringBuilder userRepr) {
      userRepr.append(this.userRepr);
    }

    @Override
    public void customizeTitleComponentAppearance(BooleanStateButton<?> button) {
      super.customizeTitleComponentAppearance(button);
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
