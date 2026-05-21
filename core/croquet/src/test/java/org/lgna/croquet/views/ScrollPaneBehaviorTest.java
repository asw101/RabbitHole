package org.lgna.croquet.views;

import org.junit.Test;

import javax.swing.JLabel;
import javax.swing.JScrollPane;

import static org.junit.Assert.*;

public class ScrollPaneBehaviorTest {
  @Test
  public void setViewportView_setsViewAndDefaultScrollIncrements() {
    ScrollPane scrollPane = new ScrollPane();
    LabelView view = new LabelView();

    scrollPane.setViewportView(view);

    assertSame(view, scrollPane.getViewportView());
    assertEquals(12, scrollPane.getAwtComponent().getHorizontalScrollBar().getUnitIncrement());
    assertEquals(24, scrollPane.getAwtComponent().getHorizontalScrollBar().getBlockIncrement());
    assertEquals(12, scrollPane.getAwtComponent().getVerticalScrollBar().getUnitIncrement());
    assertEquals(24, scrollPane.getAwtComponent().getVerticalScrollBar().getBlockIncrement());
  }

  @Test
  public void policies_delegateToSwingScrollPane() {
    ScrollPane scrollPane = new ScrollPane();

    scrollPane.setVerticalScrollbarPolicy(ScrollPane.VerticalScrollbarPolicy.NEVER);
    scrollPane.setHorizontalScrollbarPolicy(ScrollPane.HorizontalScrollbarPolicy.ALWAYS);

    assertEquals(JScrollPane.VERTICAL_SCROLLBAR_NEVER, scrollPane.getAwtComponent().getVerticalScrollBarPolicy());
    assertEquals(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS, scrollPane.getAwtComponent().getHorizontalScrollBarPolicy());
  }

  @Test
  public void setBothScrollBarIncrements_updatesBothBars() {
    ScrollPane scrollPane = new ScrollPane();

    scrollPane.setBothScrollBarIncrements(7, 21);

    assertEquals(7, scrollPane.getAwtComponent().getHorizontalScrollBar().getUnitIncrement());
    assertEquals(21, scrollPane.getAwtComponent().getHorizontalScrollBar().getBlockIncrement());
    assertEquals(7, scrollPane.getAwtComponent().getVerticalScrollBar().getUnitIncrement());
    assertEquals(21, scrollPane.getAwtComponent().getVerticalScrollBar().getBlockIncrement());
  }

  private static final class LabelView extends AwtComponentView<JLabel> {
    @Override
    protected JLabel createAwtComponent() {
      return new JLabel("viewport");
    }
  }
}
