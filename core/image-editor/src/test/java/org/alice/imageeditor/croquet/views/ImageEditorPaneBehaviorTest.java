package org.alice.imageeditor.croquet.views;

import edu.cmu.cs.dennisc.javax.swing.JShowLabel;
import org.alice.imageeditor.croquet.ImageEditorFrame;
import org.alice.imageeditor.croquet.TestSupport;
import org.junit.Assert;
import org.junit.Test;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageEditorPaneBehaviorTest {
  @Test
  public void focusGainSelectsRelativeFileNameAndRenderUsesCommittedCrop() throws Exception {
    File root = TestSupport.createEmptyDirectory("image-editor-pane-focus");
    Object[] state = TestSupport.callOnEdt(() -> {
      TestSupport.HeadlessImageEditorFrame frame = new TestSupport.HeadlessImageEditorFrame();
      TestSupport.HeadlessImageEditorPane pane = new TestSupport.HeadlessImageEditorPane(frame);
      JTextField editor = TestSupport.getEditorTextField(frame.getJComboBox());
      String absolutePath = new File(root, "nested/example.png").getAbsolutePath();

      frame.getRootDirectoryState().setValueTransactionlessly(root.getAbsolutePath());
      editor.setText(absolutePath);
      FocusListener listener = (FocusListener) TestSupport.getField(ImageEditorPane.class, pane, "comboBoxEditorFocusListener");
      listener.focusGained(new FocusEvent(editor, FocusEvent.FOCUS_GAINED));
      return new Object[] {frame, pane, editor, absolutePath};
    });

    TestSupport.onEdt(() -> {
    });
    TestSupport.onEdt(() -> {
      TestSupport.HeadlessImageEditorFrame frame = (TestSupport.HeadlessImageEditorFrame) state[0];
      TestSupport.HeadlessImageEditorPane pane = (TestSupport.HeadlessImageEditorPane) state[1];
      JTextField editor = (JTextField) state[2];
      String absolutePath = (String) state[3];

      Assert.assertEquals(root.getAbsolutePath().length() + 1, editor.getSelectionStart());
      Assert.assertEquals(absolutePath.length() - 4, editor.getSelectionEnd());

      BufferedImage source = new BufferedImage(4, 3, BufferedImage.TYPE_INT_RGB);
      source.setRGB(1, 1, Color.GREEN.getRGB());
      source.setRGB(2, 1, Color.BLUE.getRGB());
      frame.getShowInScreenResolutionState().setValueTransactionlessly(false);
      frame.getImageHolder().setValue(source);
      frame.getCropCommitHolder().setValue(new Rectangle(1, 1, 2, 1));

      Image renderedImage = pane.render();
      BufferedImage rendered = (BufferedImage) renderedImage;
      Assert.assertEquals(2, rendered.getWidth());
      Assert.assertEquals(1, rendered.getHeight());
      Assert.assertEquals(source.getRGB(1, 1), rendered.getRGB(0, 0));
      Assert.assertEquals(source.getRGB(2, 1), rendered.getRGB(1, 0));
    });
  }

  @Test
  public void updatePathLabelOnlyShowsWhenDisplayedPathDiffersFromEditorText() throws Exception {
    TestSupport.onEdt(() -> {
      TestSupport.HeadlessImageEditorFrame frame = new TestSupport.HeadlessImageEditorFrame();
      TestSupport.HeadlessImageEditorPane pane = new TestSupport.HeadlessImageEditorPane(frame);
      JTextField editor = TestSupport.getEditorTextField(frame.getJComboBox());
      JShowLabel label = (JShowLabel) TestSupport.getField(ImageEditorPane.class, pane, "jPathLabel");

      editor.setText("/images/match.png");
      invokeUpdatePathLabel(pane, "/images/match.png");
      Assert.assertEquals("/images/match.png", label.getText());
      Assert.assertFalse((Boolean) TestSupport.getField(JShowLabel.class, label, "isShowing"));

      invokeUpdatePathLabel(pane, ImageEditorFrame.INVALID_PATH_EMPTY_SUB_PATH);
      Assert.assertFalse((Boolean) TestSupport.getField(JShowLabel.class, label, "isShowing"));

      invokeUpdatePathLabel(pane, "/images/other.png");
      Assert.assertEquals("/images/other.png", label.getText());
      Assert.assertTrue((Boolean) TestSupport.getField(JShowLabel.class, label, "isShowing"));
    });
  }

  private static void invokeUpdatePathLabel(ImageEditorPane pane, String nextPath) {
    try {
      TestSupport.invokeDeclared(ImageEditorPane.class, pane, "updatePathLabel", new Class<?>[] {String.class}, nextPath);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
