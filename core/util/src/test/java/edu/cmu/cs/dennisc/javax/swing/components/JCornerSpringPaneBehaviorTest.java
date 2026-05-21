package edu.cmu.cs.dennisc.javax.swing.components;

import org.junit.Test;

import javax.swing.JButton;

import static org.junit.Assert.*;

public class JCornerSpringPaneBehaviorTest {

  @Test
  public void settingCornerComponentsStoresAndAddsThem() {
    JCornerSpringPane pane = new JCornerSpringPane();
    JButton nw = new JButton("nw");
    JButton ne = new JButton("ne");
    JButton sw = new JButton("sw");
    JButton se = new JButton("se");

    pane.setNorthWestComponent(nw);
    pane.setNorthEastComponent(ne);
    pane.setSouthWestComponent(sw);
    pane.setSouthEastComponent(se);

    assertSame(nw, pane.getNorthWestComponent());
    assertSame(ne, pane.getNorthEastComponent());
    assertSame(sw, pane.getSouthWestComponent());
    assertSame(se, pane.getSouthEastComponent());
    assertEquals(4, pane.getComponentCount());
  }

  @Test
  public void replacingCornerComponentRemovesPreviousOne() {
    JCornerSpringPane pane = new JCornerSpringPane();
    JButton first = new JButton("first");
    JButton second = new JButton("second");

    pane.setNorthWestComponent(first);
    pane.setNorthWestComponent(second);

    assertSame(second, pane.getNorthWestComponent());
    assertSame(pane, second.getParent());
    assertNull(first.getParent());
    assertEquals(1, pane.getComponentCount());
  }

  @Test
  public void settingCornerToNullRemovesExistingComponent() {
    JCornerSpringPane pane = new JCornerSpringPane();
    JButton button = new JButton("button");

    pane.setSouthEastComponent(button);
    pane.setSouthEastComponent(null);

    assertNull(pane.getSouthEastComponent());
    assertNull(button.getParent());
    assertEquals(0, pane.getComponentCount());
  }
}
