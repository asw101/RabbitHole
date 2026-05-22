package org.lgna.croquet.views;

import org.junit.Test;

import javax.swing.JPanel;
import java.awt.Dimension;

import static org.junit.Assert.*;

public class LayerBehaviorTest {
  @Test
  public void setComponentSizesAndSwapsViews() throws Exception {
    HeadlessWindowBehaviorTest.FakeAbstractWindow window = new HeadlessWindowBehaviorTest.FakeAbstractWindow(HeadlessWindowBehaviorTest.newHeadlessWindow());
    LayeredPane layeredPane = window.getRootPane().getLayeredPane();
    layeredPane.getAwtComponent().setSize(120, 60);
    Layer layer = new Layer(layeredPane, javax.swing.JLayeredPane.PALETTE_LAYER + 5);
    TestSwingView first = new TestSwingView();
    TestSwingView second = new TestSwingView();

    layer.setComponent(first);
    assertSame(first, layer.getComponent());
    assertEquals(new Dimension(120, 60), first.getAwtComponent().getSize());
    assertSame(layeredPane.getAwtComponent(), first.getAwtComponent().getParent());

    layer.setComponent(second);
    assertSame(second, layer.getComponent());
    assertNull(first.getAwtComponent().getParent());
    assertSame(layeredPane.getAwtComponent(), second.getAwtComponent().getParent());

    layer.setComponent(null);
    assertNull(layer.getComponent());
  }

  @Test
  public void layerComparisonsReflectStandardSwingLayers() throws Exception {
    HeadlessWindowBehaviorTest.FakeAbstractWindow belowWindow = new HeadlessWindowBehaviorTest.FakeAbstractWindow(HeadlessWindowBehaviorTest.newHeadlessWindow());
    HeadlessWindowBehaviorTest.FakeAbstractWindow aboveWindow = new HeadlessWindowBehaviorTest.FakeAbstractWindow(HeadlessWindowBehaviorTest.newHeadlessWindow());
    Layer below = new Layer(belowWindow.getRootPane().getLayeredPane(), javax.swing.JLayeredPane.DEFAULT_LAYER - 1);
    Layer above = new Layer(aboveWindow.getRootPane().getLayeredPane(), javax.swing.JLayeredPane.DRAG_LAYER + 1);

    assertTrue(below.isBelowDefaultLayer());
    assertFalse(below.isAboveDefaultLayer());
    assertTrue(above.isAbovePaletteLayer());
    assertTrue(above.isAboveModalLayer());
    assertTrue(above.isAbovePopupLayer());
    assertTrue(above.isAboveDragLayer());
  }

  private static final class TestSwingView extends SwingComponentView<JPanel> {
    @Override
    protected JPanel createAwtComponent() {
      return new JPanel();
    }
  }
}
