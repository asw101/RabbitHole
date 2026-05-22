package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.views.Panel;
import org.lgna.croquet.views.ScrollPane;
import org.lgna.croquet.views.SplitPane;

import javax.swing.JSplitPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.LayoutManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class CompositeTabManagerFactoryBehaviorTest {
  private CompositeTabManager manager;

  @Before
  public void setUp() {
    CroquetTestUtils.ensureTestApplication();
    manager = new CompositeTabManager();
  }

  @Test
  public void createAndRegisterCardOwnerCompositeTracksComposite() {
    CardOwnerComposite cardOwnerComposite = manager.createAndRegisterCardOwnerComposite(new TestComposite(), new TestComposite());

    assertSame(cardOwnerComposite, manager.getSubComposites().get(0));
    assertEquals(1, manager.getSubComposites().size());
  }

  @Test
  public void createCardOwnerCompositeButDoNotRegisterLeavesManagerUnchanged() {
    CardOwnerComposite cardOwnerComposite = manager.createCardOwnerCompositeButDoNotRegister(new TestComposite(), new TestComposite());

    assertTrue(manager.getSubComposites().isEmpty());
    assertFalse(manager.getSubComposites().contains(cardOwnerComposite));
  }

  @Test
  public void createHorizontalSplitCompositeUsesHorizontalOrientationAndResizeWeight() {
    SplitComposite splitComposite = manager.createHorizontalSplitComposite(new TestComposite(), new TestComposite(), 0.25);

    SplitPane splitPane = splitComposite.getView();

    assertSame(splitComposite, manager.getSubComposites().get(0));
    assertEquals(JSplitPane.HORIZONTAL_SPLIT, splitPane.getAwtComponent().getOrientation());
    assertEquals(0.25, splitPane.getAwtComponent().getResizeWeight(), 0.0);
  }

  @Test
  public void createVerticalSplitCompositeUsesVerticalOrientationAndResizeWeight() {
    SplitComposite splitComposite = manager.createVerticalSplitComposite(new TestComposite(), new TestComposite(), 0.75);

    SplitPane splitPane = splitComposite.getView();

    assertSame(splitComposite, manager.getSubComposites().get(0));
    assertEquals(JSplitPane.VERTICAL_SPLIT, splitPane.getAwtComponent().getOrientation());
    assertEquals(0.75, splitPane.getAwtComponent().getResizeWeight(), 0.0);
  }

  private static final class TestComposite extends AbstractComposite<Panel> {
    private TestComposite() {
      super(CroquetTestUtils.nextTestUUID());
    }

    @Override
    protected ScrollPane createScrollPaneIfDesired() {
      return null;
    }

    @Override
    protected Panel createView() {
      return new TestPanel(this);
    }

    @Override
    public void appendUserRepr(StringBuilder sb) {
      sb.append("test");
    }
  }

  private static final class TestPanel extends Panel {
    private TestPanel(Composite<?> composite) {
      super(composite);
    }

    @Override
    protected LayoutManager createLayoutManager(JPanel jPanel) {
      return new BorderLayout();
    }
  }
}
