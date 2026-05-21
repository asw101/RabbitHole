package org.lgna.croquet.views;

import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.croquet.BooleanState;
import org.lgna.croquet.CroquetTestUtils;

import javax.swing.BorderFactory;
import javax.swing.plaf.metal.MetalToggleButtonUI;
import java.awt.Color;

import static org.junit.Assert.*;

public class PushButtonBehaviorTest {
  @Test
  public void createAwtComponent_installsToggleUiAndBorder() {
    PushButton button = new PushButton(new TestBooleanState());

    assertTrue(button.getAwtComponent().getUI() instanceof MetalToggleButtonUI);
    assertEquals(BorderFactory.createEmptyBorder(4, 4, 4, 4).getBorderInsets(button.getAwtComponent()), button.getAwtComponent().getBorder().getBorderInsets(button.getAwtComponent()));
  }

  @Test
  public void selectedColor_canBeUpdated() {
    PushButton button = new PushButton(new TestBooleanState());
    Color color = new Color(12, 34, 56);

    button.setSelectedColor(color);

    assertEquals(color, button.getSelectedColor());
  }

  private static final class TestBooleanState extends BooleanState {
    private TestBooleanState() {
      super(Application.DOCUMENT_UI_GROUP, CroquetTestUtils.nextTestUUID(), false);
    }
  }
}
