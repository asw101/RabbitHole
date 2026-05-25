package org.alice.stageide.sceneeditor.views;

import org.junit.Test;

import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

public class InstanceFactorySelectionPanelLayoutLogicTest {
  private static void assertConstructorThrowsAssertionError(Class<?> type) throws Exception {
    Constructor<?> constructor = type.getDeclaredConstructor();
    constructor.setAccessible(true);
    try {
      constructor.newInstance();
      fail("Expected constructor to reject instantiation");
    } catch (InvocationTargetException ite) {
      assertTrue(ite.getCause() instanceof AssertionError);
    }
  }

  @Test
  public void utilityConstructorRejectsInstantiation() throws Exception {
    assertConstructorThrowsAssertionError(InstanceFactorySelectionPanelLayoutLogic.class);
  }

  @Test
  public void getIndentedXOnlyOffsetsNestedButtons() {
    assertEquals(12, InstanceFactorySelectionPanelLayoutLogic.getIndentedX(12, 0, 16));
    assertEquals(28, InstanceFactorySelectionPanelLayoutLogic.getIndentedX(12, 1, 16));
  }

  @Test
  public void analyzeOverflowHidesOverflowControlWhenEverythingFits() {
    Rectangle[] bounds = {
        new Rectangle(0, 0, 40, 20),
        new Rectangle(16, 20, 40, 20),
        new Rectangle(16, 40, 20, 20)
    };
    boolean[] selected = {false, true, false};

    InstanceFactorySelectionPanelLayoutLogic.OverflowLayout layout =
        InstanceFactorySelectionPanelLayoutLogic.analyzeOverflow(bounds, selected, 80, 0);

    assertTrue(layout.shouldHideOverflowControl());
    assertFalse(layout.shouldCollapse(0));
    assertFalse(layout.shouldCollapse(1));
    assertNull(layout.getOverflowControlLocation());
  }

  @Test
  public void analyzeOverflowKeepsSelectedButtonVisibleAtOverflowBoundary() {
    Rectangle[] bounds = {
        new Rectangle(0, 0, 40, 20),
        new Rectangle(16, 20, 40, 20),
        new Rectangle(16, 40, 40, 20),
        new Rectangle(16, 60, 20, 20)
    };
    boolean[] selected = {false, false, true, false};

    InstanceFactorySelectionPanelLayoutLogic.OverflowLayout layout =
        InstanceFactorySelectionPanelLayoutLogic.analyzeOverflow(bounds, selected, 55, 0);

    assertFalse(layout.shouldHideOverflowControl());
    assertTrue(layout.shouldCollapse(1));
    assertFalse(layout.shouldCollapse(2));
    assertEquals(2, layout.getSelectedIndex());
    assertEquals(new Point(16, 20), layout.getSelectedLocation());
    assertEquals(new Point(56, 20), layout.getOverflowControlLocation());
  }

  @Test
  public void analyzeOverflowAnchorsOverflowControlWhenSelectedButtonIsEarlier() {
    Rectangle[] bounds = {
        new Rectangle(0, 0, 40, 20),
        new Rectangle(16, 20, 40, 20),
        new Rectangle(16, 40, 40, 20),
        new Rectangle(16, 60, 20, 20)
    };
    boolean[] selected = {true, false, false, false};

    InstanceFactorySelectionPanelLayoutLogic.OverflowLayout layout =
        InstanceFactorySelectionPanelLayoutLogic.analyzeOverflow(bounds, selected, 55, 0);

    assertTrue(layout.shouldCollapse(1));
    assertTrue(layout.shouldCollapse(2));
    assertNull(layout.getSelectedLocation());
    assertEquals(new Point(16, 20), layout.getOverflowControlLocation());
  }
}
