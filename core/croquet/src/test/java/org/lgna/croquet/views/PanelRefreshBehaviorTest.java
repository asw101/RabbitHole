package org.lgna.croquet.views;

import org.junit.Test;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.LayoutManager;

import static org.junit.Assert.*;

public class PanelRefreshBehaviorTest {
  @Test
  public void refreshLater_thenRefreshIfNecessary_invokesInternalRefreshOnce() {
    CountingPanel panel = new CountingPanel();
    panel.getAwtComponent();

    panel.refreshLater();
    panel.refreshIfNecessary();
    panel.refreshIfNecessary();

    assertEquals(1, panel.refreshCount);
    assertEquals(1, panel.getAwtComponent().getComponentCount());
  }

  @Test
  public void createAwtComponent_replacesCustomPanelLayoutManager() {
    ReplacingPanel panel = new ReplacingPanel();

    JPanel awt = panel.getAwtComponent();

    assertTrue(awt.getLayout() instanceof FlowLayout);
  }

  private static final class CountingPanel extends Panel {
    private int refreshCount;

    @Override
    protected LayoutManager createLayoutManager(JPanel jPanel) {
      return new FlowLayout();
    }

    @Override
    protected void internalRefresh() {
      this.refreshCount++;
      if (this.getAwtComponent().getComponentCount() == 0) {
        this.getAwtComponent().add(new JLabel("refreshed"));
      }
    }
  }

  private static final class ReplacingPanel extends Panel {
    @Override
    protected JPanel createJPanel() {
      JPanel panel = new JPanel(new BorderLayout());
      panel.add(new JLabel("existing"), BorderLayout.CENTER);
      return panel;
    }

    @Override
    protected LayoutManager createLayoutManager(JPanel jPanel) {
      return new FlowLayout();
    }
  }
}
