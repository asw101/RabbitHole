package org.lgna.croquet;

import org.junit.Test;
import org.lgna.croquet.views.CardPanel;
import org.lgna.croquet.views.Panel;
import org.lgna.croquet.views.ScrollPane;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.LayoutManager;

import static org.junit.Assert.*;

public class CardOwnerCompositeBehaviorTest {
  @Test
  public void addCard_setsFirstCardAsShowingCardAndIgnoresDuplicates() {
    TestCardOwnerComposite owner = new TestCardOwnerComposite();
    TestComposite alpha = new TestComposite("alpha", 40, 20);

    owner.addCard(alpha);
    owner.addCard(alpha);

    assertSame(alpha, owner.getShowingCard());
    assertEquals(1, owner.getCards().size());
  }

  @Test
  public void showCard_updatesShowingCardAndActivationCallbacks() {
    TestCardOwnerComposite owner = new TestCardOwnerComposite();
    TestComposite alpha = new TestComposite("alpha", 40, 20);
    TestComposite beta = new TestComposite("beta", 60, 30);
    owner.addCard(alpha);
    owner.addCard(beta);
    owner.getView();

    owner.showCard(beta);

    assertSame(beta, owner.getShowingCard());
    assertEquals(1, alpha.postDeactivationCount);
    assertEquals(1, beta.preActivationCount);
  }

  @Test
  public void showCardRefrainingFromActivation_changesCardWithoutLifecycleCallbacks() {
    TestCardOwnerComposite owner = new TestCardOwnerComposite();
    TestComposite alpha = new TestComposite("alpha", 40, 20);
    TestComposite beta = new TestComposite("beta", 60, 30);
    owner.addCard(alpha);
    owner.addCard(beta);
    owner.getView();

    owner.showCardRefrainingFromActivation(beta);

    assertSame(beta, owner.getShowingCard());
    assertEquals(0, alpha.postDeactivationCount);
    assertEquals(0, beta.preActivationCount);
  }

  @Test
  public void contains_checksCardsForRegisteredModels() {
    TestCardOwnerComposite owner = new TestCardOwnerComposite();
    TestComposite alpha = new TestComposite("alpha", 40, 20);
    owner.addCard(alpha);

    assertTrue(owner.contains(alpha.flag));
  }

  @Test
  public void releaseView_releasesCardViewsToo() {
    TestCardOwnerComposite owner = new TestCardOwnerComposite();
    TestComposite alpha = new TestComposite("alpha", 40, 20);
    owner.addCard(alpha);
    owner.getView();
    alpha.getView();

    owner.releaseView();

    assertNull(alpha.peekViewPublic());
    assertNull(owner.peekViewPublic());
  }

  private static final class TestCardOwnerComposite extends CardOwnerComposite {
    private TestCardOwnerComposite() {
      super(CroquetTestUtils.nextTestUUID());
    }

    private CardPanel peekViewPublic() {
      return this.peekView();
    }
  }

  public static final class TestComposite extends AbstractComposite<TestPanel> {
    private final Dimension preferredSize;
    private final String userRepr;
    private int preActivationCount;
    private int postDeactivationCount;
    public final BooleanState flag;

    private TestComposite(String userRepr, int width, int height) {
      super(CroquetTestUtils.nextTestUUID());
      this.userRepr = userRepr;
      this.preferredSize = new Dimension(width, height);
      this.flag = this.createBooleanState(userRepr + "Flag", false);
    }

    @Override
    protected ScrollPane createScrollPaneIfDesired() {
      return null;
    }

    @Override
    protected TestPanel createView() {
      return new TestPanel(this, this.preferredSize);
    }

    @Override
    public void appendUserRepr(StringBuilder sb) {
      sb.append(this.userRepr);
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

    private TestPanel peekViewPublic() {
      return this.peekView();
    }
  }

  public static final class TestPanel extends Panel {
    private final Dimension preferredSize;

    private TestPanel(Composite<?> composite, Dimension preferredSize) {
      super(composite);
      this.preferredSize = preferredSize;
    }

    @Override
    protected LayoutManager createLayoutManager(JPanel jPanel) {
      return new BorderLayout();
    }

    @Override
    protected JPanel createJPanel() {
      return new JPanel() {
        @Override
        public Dimension getPreferredSize() {
          return new Dimension(TestPanel.this.preferredSize);
        }
      };
    }
  }
}
