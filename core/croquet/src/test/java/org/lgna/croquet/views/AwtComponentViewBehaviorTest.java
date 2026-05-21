package org.lgna.croquet.views;

import org.junit.Test;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;

import static org.junit.Assert.*;

public class AwtComponentViewBehaviorTest {
  @Test
  public void lookup_returnsExistingViewForCreatedComponent() {
    TestView view = new TestView();
    JPanel awtComponent = view.getAwtComponent();

    assertSame(view, AwtComponentView.lookup(awtComponent));
    assertEquals(TestView.class.getName(), awtComponent.getName());
  }

  @Test
  public void release_removesOriginalLookupMapping() {
    TestView view = new TestView();
    JPanel awtComponent = view.getAwtComponent();

    view.releasePublic();

    assertNotSame(view, AwtComponentView.lookup(awtComponent));
  }

  @Test
  public void setters_delegateToUnderlyingAwtComponent() {
    TestView view = new TestView();
    Font font = new Font(Font.SANS_SERIF, Font.BOLD, 18);
    Color background = new Color(10, 20, 30);
    Color foreground = new Color(30, 20, 10);

    view.setFont(font);
    view.setBackgroundColor(background);
    view.setForegroundColor(foreground);
    view.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    view.setVisible(false);

    assertEquals(font, view.getAwtComponent().getFont());
    assertEquals(background, view.getAwtComponent().getBackground());
    assertEquals(foreground, view.getAwtComponent().getForeground());
    assertEquals(ComponentOrientation.RIGHT_TO_LEFT, view.getAwtComponent().getComponentOrientation());
    assertFalse(view.isVisible());
  }

  private static final class TestView extends AwtComponentView<JPanel> {
    @Override
    protected JPanel createAwtComponent() {
      return new JPanel();
    }

    private void releasePublic() {
      this.release();
    }
  }
}
