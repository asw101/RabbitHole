package wrap;

import org.junit.Test;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import static org.junit.Assert.assertEquals;

public class WrappedFlowLayoutTest {
  @Test
  public void preferredLayoutSizeWrapsComponentsAcrossMultipleRows() {
    WrappedFlowLayout layout = new WrappedFlowLayout(FlowLayout.LEFT, 5, 5);
    JPanel panel = new JPanel(layout);
    panel.setSize(70, 100);
    panel.add(componentWithSizes(30, 10, 20, 8));
    panel.add(componentWithSizes(30, 10, 20, 8));
    panel.add(componentWithSizes(30, 10, 20, 8));

    assertEquals(new Dimension(70, 35), layout.preferredLayoutSize(panel));
  }

  @Test
  public void minimumLayoutSizeUsesMinimumComponentSizes() {
    WrappedFlowLayout layout = new WrappedFlowLayout(FlowLayout.LEFT, 5, 5);
    JPanel panel = new JPanel(layout);
    panel.setSize(120, 100);
    panel.add(componentWithSizes(30, 10, 20, 8));
    panel.add(componentWithSizes(30, 10, 20, 8));

    assertEquals(new Dimension(55, 18), layout.minimumLayoutSize(panel));
  }

  @Test
  public void layoutContainerValidatesOnlyWhenPreferredSizeChanges() {
    WrappedFlowLayout layout = new WrappedFlowLayout(FlowLayout.LEFT, 5, 5);
    TrackingPanel top = new TrackingPanel();
    JPanel panel = new JPanel(layout);
    panel.setSize(70, 100);
    panel.add(componentWithSizes(30, 10, 20, 8));
    panel.add(componentWithSizes(30, 10, 20, 8));
    top.setLayout(new BorderLayout());
    top.add(panel, BorderLayout.CENTER);

    layout.layoutContainer(panel);
    layout.layoutContainer(panel);

    assertEquals(1, top.validateCount);
    Component firstChild = panel.getComponent(0);
    assertEquals(5, firstChild.getX());
    assertEquals(5, firstChild.getY());
  }

  private static JComponent componentWithSizes(int preferredWidth, int preferredHeight, int minimumWidth, int minimumHeight) {
    JComponent component = new JPanel();
    component.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
    component.setMinimumSize(new Dimension(minimumWidth, minimumHeight));
    return component;
  }

  private static final class TrackingPanel extends JPanel {
    private int validateCount = 0;

    @Override
    public void validate() {
      this.validateCount += 1;
    }
  }
}
