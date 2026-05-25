package org.lgna.croquet.views;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lgna.croquet.CroquetTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class SpringPanelPlacementHeadlessTest {
  @BeforeClass
  public static void ensureApplication() {
    CroquetTestUtils.ensureTestApplication();
  }

  @Test
  public void cornerSpringPanel_adds_and_replaces_corner_components() {
    CornerSpringPanel panel = new CornerSpringPanel();
    Label northWest = new Label("nw");
    Label northEast = new Label("ne");
    Label southWest = new Label("sw");
    Label southEast = new Label("se");

    panel.setNorthWestComponent(northWest);
    panel.setNorthEastComponent(northEast);
    panel.setSouthWestComponent(southWest);
    panel.setSouthEastComponent(southEast);

    assertSame(northWest, panel.getNorthWestComponent());
    assertSame(northEast, panel.getNorthEastComponent());
    assertSame(southWest, panel.getSouthWestComponent());
    assertSame(southEast, panel.getSouthEastComponent());
    assertEquals(4, panel.getAwtComponent().getComponentCount());

    panel.setNorthWestComponent(null);
    panel.setSouthEastComponent(null);
    assertNull(panel.getNorthWestComponent());
    assertNull(panel.getSouthEastComponent());
    assertEquals(2, panel.getAwtComponent().getComponentCount());
  }

  @Test
  public void compassPointSpringPanel_adds_and_replaces_edge_components() {
    CompassPointSpringPanel panel = new CompassPointSpringPanel();
    Label north = new Label("north");
    Label east = new Label("east");
    Label south = new Label("south");
    Label west = new Label("west");

    panel.setNorthComponent(north);
    panel.setEastComponent(east);
    panel.setSouthComponent(south);
    panel.setWestComponent(west);

    assertSame(north, panel.getNorthComponent());
    assertSame(east, panel.getEastComponent());
    assertSame(south, panel.getSouthComponent());
    assertSame(west, panel.getWestComponent());
    assertEquals(4, panel.getAwtComponent().getComponentCount());

    panel.setNorthComponent(null);
    panel.setEastComponent(east);
    panel.setSouthComponent(null);
    panel.setWestComponent(null);

    assertNull(panel.getNorthComponent());
    assertNull(panel.getSouthComponent());
    assertNull(panel.getWestComponent());
    assertEquals(1, panel.getAwtComponent().getComponentCount());
  }
}
