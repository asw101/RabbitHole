package org.lgna.croquet.views;

import org.junit.Test;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;

import static org.junit.Assert.*;

public class AwtComponentViewBehaviorTest {
  @Test
  public void lookup_returnsRegisteredView_andNullForNullComponent() {
    TestComponentView view = new TestComponentView();
    JPanel awt = view.getAwtComponent();

    assertSame(view, AwtComponentView.lookup(awt));
    assertNull(AwtComponentView.lookup(null));
  }

  @Test
  public void setters_updateUnderlyingAwtComponent() {
    TestComponentView view = new TestComponentView();

    view.setBackgroundColor(Color.BLUE);
    view.setForegroundColor(Color.WHITE);
    view.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    view.setLocation(5, 7);
    view.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));

    assertEquals(Color.BLUE, view.getAwtComponent().getBackground());
    assertEquals(Color.WHITE, view.getAwtComponent().getForeground());
    assertEquals(ComponentOrientation.RIGHT_TO_LEFT, view.getAwtComponent().getComponentOrientation());
    assertEquals(5, view.getX());
    assertEquals(7, view.getY());
    assertEquals(16, view.getFont().getSize());
  }

  @Test
  public void preferredSizeConstraintsAndMaximumClamp_areApplied() {
    TestComponentView view = new TestComponentView();
    view.setMinimumPreferredWidth(80);
    view.setMaximumPreferredWidth(90);
    view.setMinimumPreferredHeight(30);
    view.setMaximumPreferredHeight(35);
    view.setMaximumSizeClampedToPreferredSize(true);

    Dimension preferred = view.getAwtComponent().getPreferredSize();
    Dimension maximum = view.getAwtComponent().getMaximumSize();

    assertEquals(new Dimension(90, 35), preferred);
    assertEquals(preferred, maximum);
  }

  private static final class TestComponentView extends AwtComponentView<JPanel> {
    @Override
    protected JPanel createAwtComponent() {
      return new JPanel() {
        @Override
        public Dimension getPreferredSize() {
          return constrainPreferredSizeIfNecessary(new Dimension(100, 40));
        }

        @Override
        public Dimension getMaximumSize() {
          if (isMaximumSizeClampedToPreferredSize()) {
            return this.getPreferredSize();
          }
          return super.getMaximumSize();
        }
      };
    }
  }
}
