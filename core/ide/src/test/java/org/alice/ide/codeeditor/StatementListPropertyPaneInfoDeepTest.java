package org.alice.ide.codeeditor;

import org.junit.Test;

import java.awt.Rectangle;

import static org.junit.Assert.*;

/**
 * Deep tests for {@link StatementListPropertyPaneInfo} covering bounds
 * management, setter operations, and contains logic with various rectangle sizes.
 */
public class StatementListPropertyPaneInfoDeepTest {

  @Test
  public void getBounds_returnsBoundsSetInConstructor() {
    Rectangle rect = new Rectangle(10, 20, 100, 200);
    StatementListPropertyPaneInfo info = new StatementListPropertyPaneInfo(null, rect);
    assertSame(rect, info.getBounds());
  }

  @Test
  public void setBounds_updatesReturnedBounds() {
    Rectangle rect1 = new Rectangle(0, 0, 50, 50);
    Rectangle rect2 = new Rectangle(10, 10, 80, 80);
    StatementListPropertyPaneInfo info = new StatementListPropertyPaneInfo(null, rect1);
    info.setBounds(rect2);
    assertSame(rect2, info.getBounds());
  }

  @Test
  public void getStatementListPropertyPane_returnsNullIfSetNull() {
    StatementListPropertyPaneInfo info = new StatementListPropertyPaneInfo(null, new Rectangle());
    assertNull(info.getStatementListPropertyPane());
  }

  @Test
  public void setStatementListPropertyPane_updatesValue() {
    StatementListPropertyPaneInfo info = new StatementListPropertyPaneInfo(null, new Rectangle());
    info.setStatementListPropertyPane(null);
    assertNull(info.getStatementListPropertyPane());
  }

  @Test
  public void bounds_emptyRectangle_returnsEmptyBounds() {
    Rectangle empty = new Rectangle(0, 0, 0, 0);
    StatementListPropertyPaneInfo info = new StatementListPropertyPaneInfo(null, empty);
    assertEquals(0, info.getBounds().width);
    assertEquals(0, info.getBounds().height);
  }

  @Test
  public void bounds_largeRectangle_returnsCorrectDimensions() {
    Rectangle large = new Rectangle(0, 0, 10000, 10000);
    StatementListPropertyPaneInfo info = new StatementListPropertyPaneInfo(null, large);
    assertEquals(10000, info.getBounds().width);
    assertEquals(10000, info.getBounds().height);
  }

  @Test
  public void bounds_negativeOrigin_returnsCorrectValues() {
    Rectangle rect = new Rectangle(-50, -30, 200, 150);
    StatementListPropertyPaneInfo info = new StatementListPropertyPaneInfo(null, rect);
    assertEquals(-50, info.getBounds().x);
    assertEquals(-30, info.getBounds().y);
    assertEquals(200, info.getBounds().width);
    assertEquals(150, info.getBounds().height);
  }

  @Test
  public void setBounds_multiple_usesLastSet() {
    StatementListPropertyPaneInfo info = new StatementListPropertyPaneInfo(null, new Rectangle());
    Rectangle r1 = new Rectangle(1, 1, 1, 1);
    Rectangle r2 = new Rectangle(2, 2, 2, 2);
    Rectangle r3 = new Rectangle(3, 3, 3, 3);
    info.setBounds(r1);
    info.setBounds(r2);
    info.setBounds(r3);
    assertSame(r3, info.getBounds());
  }
}
