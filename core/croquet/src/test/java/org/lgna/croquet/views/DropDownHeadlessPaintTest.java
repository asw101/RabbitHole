package org.lgna.croquet.views;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.PopupPrepModel;
import org.lgna.croquet.XvfbCroquetTestSupport;
import org.lgna.croquet.history.UserActivity;

import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DropDownHeadlessPaintTest {
  @BeforeClass
  public static void ensureApplication() {
    CroquetTestUtils.ensureTestApplication();
  }

  @Test
  public void dropdownPaintsArrowAndEmbeddedComponentsHeadlessly() {
    XvfbCroquetTestSupport.onEdt(() -> {
      DropDown<TestPopupPrepModel> dropDown = new DropDown<>(new TestPopupPrepModel(), new Label("prefix"), new Label("main"), new Label("postfix"));
      javax.swing.AbstractButton button = dropDown.getAwtComponent();
      button.setSize(button.getPreferredSize());
      button.getModel().setRollover(true);

      BufferedImage image = new BufferedImage(button.getWidth(), button.getHeight(), BufferedImage.TYPE_INT_ARGB);
      button.paint(image.getGraphics());

      assertEquals(3, button.getComponentCount());
      assertTrue(button.getPreferredSize().width > 0);
      assertEquals(button.getPreferredSize(), button.getMaximumSize());
      assertTrue(hasInk(image));
      return null;
    });
  }

  private static boolean hasInk(BufferedImage image) {
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        if ((image.getRGB(x, y) >>> 24) != 0) {
          return true;
        }
      }
    }
    return false;
  }

  private static final class TestPopupPrepModel extends PopupPrepModel {
    private TestPopupPrepModel() {
      super(CroquetTestUtils.nextTestUUID());
      this.setName("Popup");
    }

    @Override
    protected void perform(UserActivity activity) {
      activity.finish();
    }
  }
}
