package org.alice.ide.stencil;

import org.junit.Test;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CustomViewBehaviorTest {
  @Test
  public void awtPanelDelegatesPaintAndContainsDecisionsToCustomView() {
    RecordingCustomView view = new RecordingCustomView();
    JPanel panel = view.getAwtComponent();
    panel.setSize(40, 20);

    BufferedImage image = new BufferedImage(40, 20, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = image.createGraphics();
    try {
      panel.paint(graphics);
    } finally {
      graphics.dispose();
    }

    assertEquals(List.of("prologue", "component-epilogue", "paint-epilogue"), view.calls);
    assertFalse(panel.isOpaque());

    view.containsResult = false;
    assertFalse(panel.contains(5, 5));
    assertTrue(view.lastSuperContains);

    view.containsResult = true;
    assertTrue(panel.contains(5, 5));
  }

  private static class RecordingCustomView extends CustomView {
    private final List<String> calls = new ArrayList<String>();
    private boolean containsResult = true;
    private boolean lastSuperContains;

    @Override
    protected void paintComponentPrologue(Graphics2D g2) {
      calls.add("prologue");
      g2.setColor(Color.BLUE);
    }

    @Override
    protected void paintComponentEpilogue(Graphics2D g2) {
      calls.add("component-epilogue");
    }

    @Override
    protected void paintEpilogue(Graphics2D g2) {
      calls.add("paint-epilogue");
    }

    @Override
    protected boolean contains(int x, int y, boolean superContains) {
      lastSuperContains = superContains;
      return containsResult;
    }
  }
}
