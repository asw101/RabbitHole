package org.alice.ide.codeeditor;

import org.junit.Assume;
import org.junit.Test;

import java.awt.GraphicsEnvironment;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import static org.junit.Assert.*;

public class StatementListBorderAdditionalTest {

  @Test
  public void statementListBorder_reportsMinimumFromConstructor() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    StatementListBorder border = new StatementListBorder(true, null, new Insets(1, 2, 3, 4), 1);
    assertEquals(1, border.getMinimum());
  }

  @Test
  public void statementListBorder_isVirtuallyEmptyWhenComponentCountAtMinimum() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    StatementListBorder border = new StatementListBorder(true, null, new Insets(1, 2, 3, 4), 1);
    JPanel panel = new JPanel();
    panel.add(new JLabel("child"));
    assertTrue(border.isVirtuallyEmpty(panel));
  }

  @Test
  public void statementListBorder_isNotVirtuallyEmptyWhenComponentCountExceedsMinimum() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    StatementListBorder border = new StatementListBorder(true, null, new Insets(1, 2, 3, 4), 1);
    JPanel panel = new JPanel();
    panel.add(new JLabel("a"));
    panel.add(new JLabel("b"));
    assertFalse(border.isVirtuallyEmpty(panel));
  }

  @Test
  public void statementListBorder_returnsNormalInsetsWhenNotVirtuallyEmpty() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    Insets expected = new Insets(1, 2, 3, 4);
    StatementListBorder border = new StatementListBorder(true, null, expected, 0);
    JPanel panel = new JPanel();
    panel.add(new JLabel("a"));
    assertEquals(expected, border.getBorderInsets(panel));
  }

  @Test
  public void statementListBorder_drawingFlagCanBeToggled() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    StatementListBorder border = new StatementListBorder(true, null, new Insets(1, 2, 3, 4), 0);
    assertTrue(border.isDrawingDesired());
    border.setDrawingDesired(false);
    assertFalse(border.isDrawingDesired());
  }
}
