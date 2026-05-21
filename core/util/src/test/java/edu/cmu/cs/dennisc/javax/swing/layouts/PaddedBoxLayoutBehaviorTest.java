package edu.cmu.cs.dennisc.javax.swing.layouts;

import org.junit.Test;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Dimension;

import static org.junit.Assert.*;

public class PaddedBoxLayoutBehaviorTest {

  @Test
  public void preferredAndMinimumSizesIncludePaddingForXAxis() {
    JPanel panel = new JPanel();
    PaddedBoxLayout layout = new PaddedBoxLayout(panel, javax.swing.BoxLayout.X_AXIS, 7);
    panel.setLayout(layout);
    panel.add(new FixedSizeButton(10, 8));
    panel.add(new FixedSizeButton(10, 8));

    Dimension preferred = layout.preferredLayoutSize(panel);
    Dimension minimum = layout.minimumLayoutSize(panel);

    assertEquals(preferred.width, minimum.width);
    assertTrue(preferred.width >= 27);
  }

  @Test
  public void preferredAndMaximumSizesIncludePaddingForYAxis() {
    JPanel panel = new JPanel();
    PaddedBoxLayout layout = new PaddedBoxLayout(panel, javax.swing.BoxLayout.Y_AXIS, 5);
    panel.setLayout(layout);
    panel.add(new FixedSizeButton(8, 10));
    panel.add(new FixedSizeButton(8, 10));

    Dimension preferred = layout.preferredLayoutSize(panel);
    Dimension maximum = layout.maximumLayoutSize(panel);

    assertTrue(preferred.height >= 25);
    assertTrue(maximum.height >= 25);
  }

  @Test
  public void layoutContainerOffsetsComponentsByPadding() {
    JPanel panel = new JPanel();
    PaddedBoxLayout layout = new PaddedBoxLayout(panel, javax.swing.BoxLayout.X_AXIS, 9);
    panel.setLayout(layout);
    JButton left = new FixedSizeButton(10, 10);
    JButton right = new FixedSizeButton(10, 10);
    panel.add(left);
    panel.add(right);
    panel.setSize(80, 20);

    layout.layoutContainer(panel);

    assertTrue(right.getX() >= left.getX() + left.getWidth() + 9);
  }

  private static final class FixedSizeButton extends JButton {
    private final Dimension size;

    private FixedSizeButton(int width, int height) {
      this.size = new Dimension(width, height);
    }

    @Override
    public Dimension getPreferredSize() {
      return size;
    }

    @Override
    public Dimension getMinimumSize() {
      return size;
    }

    @Override
    public Dimension getMaximumSize() {
      return size;
    }
  }
}
