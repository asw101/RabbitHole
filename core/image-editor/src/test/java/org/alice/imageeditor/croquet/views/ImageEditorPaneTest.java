package org.alice.imageeditor.croquet.views;

import edu.cmu.cs.dennisc.javax.swing.JShowLabel;
import org.alice.imageeditor.croquet.ImageEditorFrame;
import org.alice.imageeditor.croquet.TestSupport;
import org.junit.Test;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ImageEditorPaneTest {
  @Test
  public void renderReturnsNullOrBufferedImageMatchingCrop() throws Exception {
    TestSupport.onEdt(() -> {
      TestSupport.HeadlessImageEditorFrame frame = new TestSupport.HeadlessImageEditorFrame();
      ImageEditorPane pane = frame.getView();

      assertNull(pane.render());

      BufferedImage image = new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB);
      frame.getImageHolder().setValue(image);
      assertEquals(20, pane.render().getWidth(null));
      assertEquals(10, pane.render().getHeight(null));

      frame.getCropCommitHolder().setValue(new Rectangle(2, 3, 7, 4));
      assertEquals(7, pane.render().getWidth(null));
      assertEquals(4, pane.render().getHeight(null));
    });
  }

  @Test
  public void updatePathLabelAndActivationLifecycleManageListeners() throws Exception {
    TestSupport.onEdt(() -> {
      TestSupport.HeadlessImageEditorFrame frame = new TestSupport.HeadlessImageEditorFrame();
      ImageEditorPane pane = frame.getView();
      assertSame(frame, pane.getComposite());

      JShowLabel label = (JShowLabel) TestSupport.getField(ImageEditorPane.class, pane, "jPathLabel");
      TestSupport.getEditorTextField(frame.getJComboBox()).setText("/same/path.png");
      invokeUpdatePathLabel(pane, "/same/path.png");
      assertEquals("/same/path.png", label.getText());
      assertFalse(getInternalShowingFlag(label));

      invokeUpdatePathLabel(pane, "/different/path.png");
      assertEquals("/different/path.png", label.getText());
      assertTrue(getInternalShowingFlag(label));

      pane.handleCompositePreActivation();
      frame.getPathHolder().setValue("listener-update");
      assertEquals("listener-update", label.getText());
      pane.handleCompositePostDeactivation();
      frame.getPathHolder().setValue("after-post");
      assertEquals("listener-update", label.getText());
      assertNotNull(pane.render());
    });
  }

  private static void invokeUpdatePathLabel(ImageEditorPane pane, String path) throws Exception {
    TestSupport.invokeDeclared(ImageEditorPane.class, pane, "updatePathLabel", new Class<?>[] {String.class}, path);
  }

  private static boolean getInternalShowingFlag(JShowLabel label) throws Exception {
    return (Boolean) TestSupport.getField(JShowLabel.class, label, "isShowing");
  }
}
