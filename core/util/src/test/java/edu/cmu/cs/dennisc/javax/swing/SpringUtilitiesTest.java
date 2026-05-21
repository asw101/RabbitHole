package edu.cmu.cs.dennisc.javax.swing;

import org.junit.Test;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class SpringUtilitiesTest {
  @Test
  public void constructorThrowsAssertionError() throws Exception {
    Constructor<SpringUtilities> constructor = SpringUtilities.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    try {
      constructor.newInstance();
      fail();
    } catch (InvocationTargetException ite) {
      assertTrue(ite.getCause() instanceof AssertionError);
    }
  }

  @Test
  public void createColumn0LabelProducesTrailingJLabel() {
    Component component = SpringUtilities.createColumn0Label("Name");

    assertTrue(component instanceof JLabel);
    assertEquals(SwingConstants.TRAILING, ((JLabel) component).getHorizontalAlignment());
    assertEquals("Name", ((JLabel) component).getText());
  }

  @Test
  public void createRowReplacesNullsWithRigidAreas() {
    Component[] row = SpringUtilities.createRow(new JButton("A"), null, new JButton("B"));

    assertEquals(3, row.length);
    assertNotNull(row[1]);
    assertNotSame(row[0], row[1]);
    assertEquals(new Dimension(0, 0), row[1].getPreferredSize());
  }

  @Test
  public void addConvenienceMethodsApplyExpectedLayoutPositions() {
    JPanel panel = new JPanel(new SpringLayout());
    panel.setSize(200, 120);

    JButton north = sizedButton();
    JButton center = sizedButton();
    JButton southWest = sizedButton();
    JButton east = sizedButton();

    SpringUtilities.addNorth(panel, north, 10);
    SpringUtilities.addCenter(panel, center, 0);
    SpringUtilities.addSouthWest(panel, southWest, 12);
    SpringUtilities.addEast(panel, east, 8);
    panel.doLayout();

    assertEquals(10, north.getY());
    assertEquals((200 - center.getPreferredSize().width) / 2, center.getX());
    assertEquals((120 - center.getPreferredSize().height) / 2, center.getY());
    assertEquals(12, southWest.getX());
    assertEquals(120 - southWest.getPreferredSize().height - 12, southWest.getY());
    assertEquals(200 - east.getPreferredSize().width - 8, east.getX());
  }

  @Test
  public void springItUpANotchNormalizesRowsAndSetsContainerConstraints() {
    JPanel panel = new JPanel();
    JButton a = sizedButton();
    JButton b = sizedButton();
    JButton c = sizedButton();
    JButton d = sizedButton();

    Container result = SpringUtilities.springItUpANotch(panel, List.of(
        SpringUtilities.createRow(a, b),
        SpringUtilities.createRow(c, d)), 6, 4);

    assertSame(panel, result);
    assertTrue(panel.getLayout() instanceof SpringLayout);
    assertEquals(4, panel.getComponentCount());

    SpringLayout layout = (SpringLayout) panel.getLayout();
    SpringLayout.Constraints constraints = layout.getConstraints(panel);
    assertTrue(constraints.getConstraint(SpringLayout.EAST).getValue() > 0);
    assertTrue(constraints.getConstraint(SpringLayout.SOUTH).getValue() > 0);
  }

  @Test
  public void expandToBoundsUsesProvidedInsetsAndCreatesSpringLayoutWhenNeeded() {
    JPanel container = new JPanel();
    JButton child = sizedButton();
    container.add(child);

    SpringUtilities.expandToBounds(child, container, 7, 9);

    assertTrue(container.getLayout() instanceof SpringLayout);
    SpringLayout layout = (SpringLayout) container.getLayout();
    SpringLayout.Constraints constraints = layout.getConstraints(child);
    assertEquals(7, constraints.getConstraint(SpringLayout.WEST).getValue());
    assertEquals(9, constraints.getConstraint(SpringLayout.NORTH).getValue());
    assertEquals(-7, constraints.getConstraint(SpringLayout.EAST).getValue());
    assertEquals(-9, constraints.getConstraint(SpringLayout.SOUTH).getValue());
  }

  @Test
  public void expandToBoundsOverloadsReturnSameComponent() {
    JPanel container = new JPanel(new SpringLayout());
    JButton child = sizedButton();
    container.add(child);

    assertSame(child, SpringUtilities.expandToBounds(child, 1, 2));
    assertSame(child, SpringUtilities.expandToBounds(child));
  }

  private static JButton sizedButton() {
    JButton button = new JButton("X");
    button.setPreferredSize(new Dimension(20, 10));
    return button;
  }
}
