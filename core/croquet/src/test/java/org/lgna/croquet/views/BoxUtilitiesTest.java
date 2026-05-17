package org.lgna.croquet.views;

import org.junit.Test;

import javax.swing.Box;

import static org.junit.Assert.*;

/**
 * Tests for {@link BoxUtilities} — static factory methods for Box.Filler
 * wrappers (glue, struts, slivers, rigid areas).
 */
public class BoxUtilitiesTest {

  @Test
  public void createGlue_returnsNonNull() {
    SwingComponentView<Box.Filler> glue = BoxUtilities.createGlue();
    assertNotNull(glue);
  }

  @Test
  public void createHorizontalGlue_returnsNonNull() {
    SwingComponentView<Box.Filler> glue = BoxUtilities.createHorizontalGlue();
    assertNotNull(glue);
  }

  @Test
  public void createVerticalGlue_returnsNonNull() {
    SwingComponentView<Box.Filler> glue = BoxUtilities.createVerticalGlue();
    assertNotNull(glue);
  }

  @Test
  public void createHorizontalSliver_returnsNonNull() {
    SwingComponentView<Box.Filler> sliver = BoxUtilities.createHorizontalSliver(10);
    assertNotNull(sliver);
  }

  @Test
  public void createVerticalSliver_returnsNonNull() {
    SwingComponentView<Box.Filler> sliver = BoxUtilities.createVerticalSliver(10);
    assertNotNull(sliver);
  }

  @Test
  public void createHorizontalStrut_returnsNonNull() {
    SwingComponentView<Box.Filler> strut = BoxUtilities.createHorizontalStrut(5);
    assertNotNull(strut);
  }

  @Test
  public void createVerticalStrut_returnsNonNull() {
    SwingComponentView<Box.Filler> strut = BoxUtilities.createVerticalStrut(5);
    assertNotNull(strut);
  }

  @Test
  public void createRigidArea_dimension_returnsNonNull() {
    SwingComponentView<Box.Filler> area = BoxUtilities.createRigidArea(new java.awt.Dimension(20, 20));
    assertNotNull(area);
  }

  @Test
  public void createRigidArea_widthHeight_returnsNonNull() {
    SwingComponentView<Box.Filler> area = BoxUtilities.createRigidArea(30, 40);
    assertNotNull(area);
  }
}
