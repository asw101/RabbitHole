package edu.cmu.cs.dennisc.javax.swing.components;

import org.junit.Test;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import java.awt.Component;
import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

public class JExpandPaneBehaviorTest {

  @Test
  public void constructorStartsCollapsed() {
    TestExpandPane pane = new TestExpandPane();

    assertFalse(pane.isSelected());
    assertEquals("collapsed-label", getLabel(pane).getText());
    assertFalse(containsCenter(pane));
    assertSame(pane.getCenterComponent(), pane.center);
  }

  @Test
  public void setSelectedTrueAddsCenterAndUpdatesLabel() {
    TestExpandPane pane = new TestExpandPane();

    pane.setSelected(true);

    assertTrue(pane.isSelected());
    assertEquals("expanded-label", getLabel(pane).getText());
    assertTrue(containsCenter(pane));
  }

  @Test
  public void toggleButtonClickTracksSelection() {
    TestExpandPane pane = new TestExpandPane();
    JToggleButton toggle = getToggle(pane);

    toggle.doClick();
    assertTrue(pane.isSelected());

    toggle.doClick();
    assertFalse(pane.isSelected());
  }

  @Test
  public void togglePreferredSizeAccountsForButtonText() {
    TestExpandPane pane = new TestExpandPane();
    JToggleButton toggle = getToggle(pane);

    assertTrue(toggle.getPreferredSize().width > 0);
    assertTrue(toggle.getPreferredSize().height > 0);
  }

  @Test
  public void togglePaintRendersText() {
    TestExpandPane pane = new TestExpandPane();
    JToggleButton toggle = getToggle(pane);
    toggle.setSize(toggle.getPreferredSize());

    BufferedImage image = new BufferedImage(toggle.getWidth(), toggle.getHeight(), BufferedImage.TYPE_INT_ARGB);
    toggle.paint(image.getGraphics());

    assertTrue(hasNonTransparentPixel(image));
  }

  private static JLabel getLabel(TestExpandPane pane) {
    JPanel top = (JPanel) pane.getComponent(0);
    return (JLabel) top.getComponent(0);
  }

  private static JToggleButton getToggle(TestExpandPane pane) {
    JPanel top = (JPanel) pane.getComponent(0);
    return (JToggleButton) top.getComponent(1);
  }

  private static boolean containsCenter(TestExpandPane pane) {
    for (Component component : pane.getComponents()) {
      if (component == pane.center) {
        return true;
      }
    }
    return false;
  }

  private static boolean hasNonTransparentPixel(BufferedImage image) {
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        if ((image.getRGB(x, y) >>> 24) != 0) {
          return true;
        }
      }
    }
    return false;
  }

  private static final class TestExpandPane extends JExpandPane {
    private JPanel center;

    @Override
    protected String getExpandedLabelText() {
      return "expanded-label";
    }

    @Override
    protected String getCollapsedLabelText() {
      return "collapsed-label";
    }

    @Override
    protected String getExpandedButtonText() {
      return "EXPANDED";
    }

    @Override
    protected String getCollapsedButtonText() {
      return "COLLAPSED";
    }

    @Override
    protected JComponent createCenterPane() {
      if (center == null) {
        center = new JPanel();
      }
      return center;
    }
  }
}
