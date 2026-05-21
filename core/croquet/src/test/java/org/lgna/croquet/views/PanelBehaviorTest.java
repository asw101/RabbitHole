package org.lgna.croquet.views;

import org.junit.Test;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.LayoutManager;

import static org.junit.Assert.*;

public class PanelBehaviorTest {
  @Test
  public void refreshIfNecessary_runsWhenInvokedAndRefreshLaterReenablesIt() {
    TestPanel panel = new TestPanel();

    panel.refreshIfNecessary();
    panel.refreshIfNecessary();
    panel.refreshLater();
    panel.refreshIfNecessary();

    assertEquals(2, panel.refreshCount);
  }

  @Test
  public void maximumSize_canBeClampedToPreferredSize() {
    TestPanel panel = new TestPanel();
    panel.getAwtComponent().setPreferredSize(new Dimension(80, 25));
    panel.setMaximumSizeClampedToPreferredSize(true);

    assertEquals(panel.getAwtComponent().getPreferredSize(), panel.getAwtComponent().getMaximumSize());
  }

  @Test
  public void forgetAndRemoveAllComponents_releasesChildMappings() {
    TestPanel panel = new TestPanel();
    LabelView child = new LabelView();
    java.awt.Component oldComponent = child.getAwtComponent();
    panel.addChild(child);
    assertEquals(1, panel.getComponentCount());

    panel.forgetAndRemoveAllComponents();

    assertEquals(0, panel.getComponentCount());
    assertNotSame(child, AwtComponentView.lookup(oldComponent));
  }

  private static final class TestPanel extends Panel {
    private int refreshCount;

    private TestPanel() {
      super(null);
    }

    @Override
    protected LayoutManager createLayoutManager(JPanel jPanel) {
      return new BorderLayout();
    }

    @Override
    protected void internalRefresh() {
      this.refreshCount++;
    }

    private void addChild(AwtComponentView<?> child) {
      synchronized (this.getTreeLock()) {
        this.internalAddComponent(child);
      }
    }

  }

  private static final class LabelView extends AwtComponentView<JLabel> {
    @Override
    protected JLabel createAwtComponent() {
      return new JLabel("child");
    }
  }
}
