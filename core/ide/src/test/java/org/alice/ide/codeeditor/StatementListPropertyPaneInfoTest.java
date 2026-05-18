package org.alice.ide.codeeditor;

import org.alice.ide.x.components.StatementListPropertyView;
import org.junit.Test;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

import static org.junit.Assert.*;

/**
 * Tests for {@link StatementListPropertyPaneInfo} — constructor, getters, setters,
 * contains check, and bounds management.
 */
public class StatementListPropertyPaneInfoTest {

  // ---- Constructor and getters ----

  @Test
  public void constructor_storesPane() {
    StatementListPropertyPaneInfo info = new StatementListPropertyPaneInfo(null, new Rectangle(0, 0, 100, 50));
    assertNull("Pane should be null when constructed with null", info.getStatementListPropertyPane());
  }

  @Test
  public void constructor_storesBounds() {
    Rectangle bounds = new Rectangle(10, 20, 300, 150);
    StatementListPropertyPaneInfo info = new StatementListPropertyPaneInfo(null, bounds);
    assertSame(bounds, info.getBounds());
  }

  @Test
  public void getBounds_returnsExactRectangle() {
    Rectangle bounds = new Rectangle(5, 10, 200, 100);
    StatementListPropertyPaneInfo info = new StatementListPropertyPaneInfo(null, bounds);

    assertEquals(5, info.getBounds().x);
    assertEquals(10, info.getBounds().y);
    assertEquals(200, info.getBounds().width);
    assertEquals(100, info.getBounds().height);
  }

  // ---- Setters ----

  @Test
  public void setBounds_updatesBounds() {
    StatementListPropertyPaneInfo info = new StatementListPropertyPaneInfo(null, new Rectangle(0, 0, 10, 10));
    Rectangle newBounds = new Rectangle(50, 60, 400, 300);
    info.setBounds(newBounds);
    assertSame(newBounds, info.getBounds());
  }

  @Test
  public void setStatementListPropertyPane_updatesPane() {
    StatementListPropertyPaneInfo info = new StatementListPropertyPaneInfo(null, new Rectangle(0, 0, 10, 10));
    assertNull(info.getStatementListPropertyPane());
    // We can't easily construct a StatementListPropertyView headlessly,
    // but we can verify the setter doesn't throw and the getter returns what we set
    info.setStatementListPropertyPane(null);
    assertNull(info.getStatementListPropertyPane());
  }

  // ---- contains ----

  @Test
  public void contains_pointInside_returnsTrue() {
    Rectangle bounds = new Rectangle(0, 0, 100, 50);
    StatementListPropertyPaneInfo info = new StatementListPropertyPaneInfo(null, bounds);

    JPanel source = new JPanel();
    MouseEvent inside = new MouseEvent(source, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
        0, 25, 25, 1, false);
    assertTrue("Point (25,25) should be inside (0,0,100,50)", info.contains(inside));
  }

  @Test
  public void contains_pointOutside_returnsFalse() {
    Rectangle bounds = new Rectangle(0, 0, 100, 50);
    StatementListPropertyPaneInfo info = new StatementListPropertyPaneInfo(null, bounds);

    JPanel source = new JPanel();
    MouseEvent outside = new MouseEvent(source, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
        0, 200, 200, 1, false);
    assertFalse("Point (200,200) should be outside (0,0,100,50)", info.contains(outside));
  }

  @Test
  public void contains_pointOnBorder_returnsTrue() {
    Rectangle bounds = new Rectangle(0, 0, 100, 50);
    StatementListPropertyPaneInfo info = new StatementListPropertyPaneInfo(null, bounds);

    JPanel source = new JPanel();
    MouseEvent onBorder = new MouseEvent(source, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
        0, 0, 0, 1, false);
    assertTrue("Point (0,0) on border should be inside", info.contains(onBorder));
  }

  @Test
  public void contains_pointJustOutsideRight_returnsFalse() {
    Rectangle bounds = new Rectangle(0, 0, 100, 50);
    StatementListPropertyPaneInfo info = new StatementListPropertyPaneInfo(null, bounds);

    JPanel source = new JPanel();
    MouseEvent justOutside = new MouseEvent(source, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
        0, 100, 25, 1, false);
    assertFalse("Point (100,25) should be outside (width is exclusive)", info.contains(justOutside));
  }

  @Test
  public void contains_pointJustOutsideBottom_returnsFalse() {
    Rectangle bounds = new Rectangle(0, 0, 100, 50);
    StatementListPropertyPaneInfo info = new StatementListPropertyPaneInfo(null, bounds);

    JPanel source = new JPanel();
    MouseEvent justOutside = new MouseEvent(source, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
        0, 50, 50, 1, false);
    assertFalse("Point (50,50) should be outside", info.contains(justOutside));
  }

  // ---- Bounds offset ----

  @Test
  public void contains_offsetBounds_pointInsideOffset() {
    Rectangle bounds = new Rectangle(100, 200, 50, 50);
    StatementListPropertyPaneInfo info = new StatementListPropertyPaneInfo(null, bounds);

    JPanel source = new JPanel();
    MouseEvent inside = new MouseEvent(source, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
        0, 120, 220, 1, false);
    assertTrue("Point (120,220) should be inside (100,200,50,50)", info.contains(inside));
  }

  @Test
  public void contains_offsetBounds_pointBeforeOffset() {
    Rectangle bounds = new Rectangle(100, 200, 50, 50);
    StatementListPropertyPaneInfo info = new StatementListPropertyPaneInfo(null, bounds);

    JPanel source = new JPanel();
    MouseEvent before = new MouseEvent(source, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
        0, 50, 150, 1, false);
    assertFalse("Point (50,150) should be outside offset bounds", info.contains(before));
  }

  // ---- setBounds then check contains ----

  @Test
  public void setBounds_thenContains_usesNewBounds() {
    StatementListPropertyPaneInfo info = new StatementListPropertyPaneInfo(null, new Rectangle(0, 0, 10, 10));
    info.setBounds(new Rectangle(50, 50, 100, 100));

    JPanel source = new JPanel();
    MouseEvent inside = new MouseEvent(source, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
        0, 75, 75, 1, false);
    assertTrue(info.contains(inside));

    MouseEvent oldBounds = new MouseEvent(source, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
        0, 5, 5, 1, false);
    assertFalse("Old bounds area should no longer match", info.contains(oldBounds));
  }
}
