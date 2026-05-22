package org.lgna.croquet.views;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lgna.croquet.PopupPrepModel;
import org.lgna.croquet.XvfbCroquetTestSupport;
import org.lgna.croquet.history.UserActivity;

import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

public class DropDownXvfbPaintTest {
  @BeforeClass
  public static void installApplication() {
    XvfbCroquetTestSupport.installApplication();
  }

  @Test
  public void dropdownPaintsArrowAndEmbeddedComponents() {
    XvfbCroquetTestSupport.tryOnEdt(() -> {
      DropDown<TestPopupPrepModel> dropDown = new DropDown<>(new TestPopupPrepModel(), new Label("prefix"), new Label("main"), new Label("postfix"));
      javax.swing.AbstractButton button = dropDown.getAwtComponent();
      button.setSize(button.getPreferredSize());

      BufferedImage image = new BufferedImage(button.getWidth(), button.getHeight(), BufferedImage.TYPE_INT_ARGB);
      button.paint(image.getGraphics());

      assertEquals(3, button.getComponentCount());
      assertTrue(button.getPreferredSize().width > 0);
      assertEquals(button.getPreferredSize(), button.getMaximumSize());
    });
  }

  private static final class TestPopupPrepModel extends PopupPrepModel {
    private TestPopupPrepModel() {
      super(java.util.UUID.randomUUID());
      this.setName("Popup");
    }

    @Override
    protected void perform(UserActivity activity) {
      activity.finish();
    }
  }
}
