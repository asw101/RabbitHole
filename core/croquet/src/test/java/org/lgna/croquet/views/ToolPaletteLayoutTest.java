package org.lgna.croquet.views;

import org.junit.Test;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;

import static org.junit.Assert.assertEquals;

public class ToolPaletteLayoutTest {

  @Test
  public void collapsedPaletteUsesOnlyTitleSizeAndHidesCenter() {
    JToggleButton.ToggleButtonModel expansionModel = new JToggleButton.ToggleButtonModel();
    JPanel palette = createPalette(expansionModel);
    JComponent title = fixedSizeComponent(80, 12);
    JComponent center = fixedSizeComponent(120, 40);

    palette.add(title, BorderLayout.PAGE_START);
    palette.add(center, BorderLayout.CENTER);
    palette.setSize(140, 60);
    palette.doLayout();

    assertEquals(new Dimension(80, 12), palette.getPreferredSize());
    assertEquals(new Rectangle(0, 0, 140, 12), title.getBounds());
    assertEquals(new Rectangle(0, 12, 0, 0), center.getBounds());
  }

  @Test
  public void expandedPaletteIncludesCenterSizeAndFillsRemainingHeight() {
    JToggleButton.ToggleButtonModel expansionModel = new JToggleButton.ToggleButtonModel();
    expansionModel.setSelected(true);
    JPanel palette = createPalette(expansionModel);
    JComponent title = fixedSizeComponent(80, 12);
    JComponent center = fixedSizeComponent(120, 40);

    palette.add(title, BorderLayout.PAGE_START);
    palette.add(center, BorderLayout.CENTER);
    palette.setSize(140, 60);
    palette.doLayout();

    assertEquals(new Dimension(120, 52), palette.getPreferredSize());
    assertEquals(new Rectangle(0, 0, 140, 12), title.getBounds());
    assertEquals(new Rectangle(0, 12, 140, 48), center.getBounds());
  }

  @Test
  public void removedComponentsNoLongerContributeToPreferredSize() {
    JToggleButton.ToggleButtonModel expansionModel = new JToggleButton.ToggleButtonModel();
    expansionModel.setSelected(true);
    JPanel palette = createPalette(expansionModel);
    JComponent title = fixedSizeComponent(80, 12);
    JComponent center = fixedSizeComponent(120, 40);

    palette.add(title, BorderLayout.PAGE_START);
    palette.add(center, BorderLayout.CENTER);
    palette.remove(center);

    assertEquals(new Dimension(80, 12), palette.getPreferredSize());

    palette.remove(title);

    assertEquals(new Dimension(0, 0), palette.getPreferredSize());
  }

  @Test
  public void paletteTracksExpansionModelChangesAcrossLayouts() {
    JToggleButton.ToggleButtonModel expansionModel = new JToggleButton.ToggleButtonModel();
    JPanel palette = createPalette(expansionModel);
    JComponent title = fixedSizeComponent(80, 12);
    JComponent center = fixedSizeComponent(120, 40);

    palette.add(title, BorderLayout.PAGE_START);
    palette.add(center, BorderLayout.CENTER);
    palette.setSize(140, 60);
    palette.doLayout();

    assertEquals(new Dimension(80, 12), palette.getPreferredSize());
    assertEquals(new Rectangle(0, 12, 0, 0), center.getBounds());

    expansionModel.setSelected(true);
    palette.doLayout();

    assertEquals(new Dimension(120, 52), palette.getPreferredSize());
    assertEquals(new Rectangle(0, 12, 140, 48), center.getBounds());

    expansionModel.setSelected(false);
    palette.doLayout();

    assertEquals(new Dimension(80, 12), palette.getPreferredSize());
    assertEquals(new Rectangle(0, 12, 0, 0), center.getBounds());
  }

  private static JPanel createPalette(JToggleButton.ToggleButtonModel expansionModel) {
    return new JPanel(new ToolPaletteLayout(expansionModel));
  }

  private static JComponent fixedSizeComponent(int width, int height) {
    JPanel component = new JPanel();
    component.setPreferredSize(new Dimension(width, height));
    return component;
  }
}
