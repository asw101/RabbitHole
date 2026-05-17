package org.lgna.croquet.views;

import org.junit.Test;

import javax.swing.Box;
import java.awt.Dimension;

import static org.junit.Assert.*;

/**
 * Tests for {@link BoxUtilities} — static factory methods for Box.Filler
 * wrappers (glue, struts, slivers, rigid areas).
 */
public class BoxUtilitiesTest {

  @Test
  public void createGlue_expandsBothDirections() {
    SwingComponentView<Box.Filler> glue = BoxUtilities.createGlue();
    Box.Filler filler = glue.getAwtComponent();
    assertEquals(new Dimension(0, 0), filler.getMinimumSize());
    assertEquals(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE), filler.getMaximumSize());
  }

  @Test
  public void createHorizontalGlue_expandsHorizontalOnly() {
    SwingComponentView<Box.Filler> glue = BoxUtilities.createHorizontalGlue();
    Box.Filler filler = glue.getAwtComponent();
    assertEquals(0, filler.getMaximumSize().height);
    assertEquals(Short.MAX_VALUE, filler.getMaximumSize().width);
  }

  @Test
  public void createVerticalGlue_expandsVerticalOnly() {
    SwingComponentView<Box.Filler> glue = BoxUtilities.createVerticalGlue();
    Box.Filler filler = glue.getAwtComponent();
    assertEquals(0, filler.getMaximumSize().width);
    assertEquals(Short.MAX_VALUE, filler.getMaximumSize().height);
  }

  @Test
  public void createHorizontalSliver_fixedWidthZeroHeight() {
    SwingComponentView<Box.Filler> sliver = BoxUtilities.createHorizontalSliver(10);
    Box.Filler filler = sliver.getAwtComponent();
    assertEquals(10, filler.getPreferredSize().width);
    assertEquals(0, filler.getPreferredSize().height);
    assertEquals(10, filler.getMaximumSize().width);
  }

  @Test
  public void createVerticalSliver_fixedHeightZeroWidth() {
    SwingComponentView<Box.Filler> sliver = BoxUtilities.createVerticalSliver(10);
    Box.Filler filler = sliver.getAwtComponent();
    assertEquals(0, filler.getPreferredSize().width);
    assertEquals(10, filler.getPreferredSize().height);
    assertEquals(10, filler.getMaximumSize().height);
  }

  @Test
  public void createHorizontalStrut_fixedWidthFlexibleHeight() {
    SwingComponentView<Box.Filler> strut = BoxUtilities.createHorizontalStrut(5);
    Box.Filler filler = strut.getAwtComponent();
    assertEquals(5, filler.getPreferredSize().width);
    assertEquals(5, filler.getMaximumSize().width);
    assertEquals(Short.MAX_VALUE, filler.getMaximumSize().height);
  }

  @Test
  public void createVerticalStrut_fixedHeightFlexibleWidth() {
    SwingComponentView<Box.Filler> strut = BoxUtilities.createVerticalStrut(5);
    Box.Filler filler = strut.getAwtComponent();
    assertEquals(5, filler.getPreferredSize().height);
    assertEquals(5, filler.getMaximumSize().height);
    assertEquals(Short.MAX_VALUE, filler.getMaximumSize().width);
  }

  @Test
  public void createRigidArea_dimension_allSizesMatch() {
    Dimension d = new Dimension(20, 20);
    SwingComponentView<Box.Filler> area = BoxUtilities.createRigidArea(d);
    Box.Filler filler = area.getAwtComponent();
    assertEquals(d, filler.getMinimumSize());
    assertEquals(d, filler.getPreferredSize());
    assertEquals(d, filler.getMaximumSize());
  }

  @Test
  public void createRigidArea_widthHeight_delegatesToDimensionOverload() {
    SwingComponentView<Box.Filler> area = BoxUtilities.createRigidArea(30, 40);
    Box.Filler filler = area.getAwtComponent();
    assertEquals(new Dimension(30, 40), filler.getPreferredSize());
    assertEquals(new Dimension(30, 40), filler.getMaximumSize());
  }
}
