package org.alice.imageeditor.croquet.views;

import org.alice.imageeditor.croquet.SaveOperation;
import org.alice.imageeditor.croquet.SaveOverComposite;
import org.alice.imageeditor.croquet.TestSupport;
import org.junit.Test;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class SaveOverPaneTest {
  @Test
  public void constructorAndCompositeAccessorsWork() throws Exception {
    TestSupport.onEdt(() -> {
      SaveOverPane pane = new SaveOverPane(new SaveOverComposite(new SaveOperation(new TestSupport.HeadlessImageEditorFrame())));
      assertSame(pane.getComposite(), pane.getComposite());
    });
  }

  @Test
  public void handleCompositePreActivationLoadsPreviewImagesAndDetails() throws Exception {
    TestSupport.onEdt(() -> {
      File dir = TestSupport.createEmptyDirectory("save-over-pane");
      File existing = new File(dir, "existing.png");
      BufferedImage previous = new BufferedImage(6, 4, BufferedImage.TYPE_INT_RGB);
      BufferedImage next = new BufferedImage(8, 5, BufferedImage.TYPE_INT_RGB);
      ImageIO.write(previous, "png", existing);

      TestSupport.HeadlessImageEditorFrame frame = new TestSupport.HeadlessImageEditorFrame();
      frame.getImageHolder().setValue(next);
      frame.getPathHolder().setValue(existing.getAbsolutePath());

      SaveOverComposite composite = new SaveOverComposite(new SaveOperation(frame));
      composite.getPrevHeader().setText("Previous");
      composite.getNextHeader().setText("Next");
      SaveOverPane pane = new SaveOverPane(composite);
      assertSame(composite, pane.getComposite());

      pane.handleCompositePreActivation();

      ImageView previousView = (ImageView) TestSupport.getField(SaveOverPane.class, pane, "toBeReplacedImageView");
      ImageView nextView = (ImageView) TestSupport.getField(SaveOverPane.class, pane, "nextImageView");
      JLabel previousHeader = (JLabel) TestSupport.getField(SaveOverPane.class, pane, "toBeReplacedHeaderLabel");
      JLabel previousDetails = (JLabel) TestSupport.getField(SaveOverPane.class, pane, "toBeReplacedDetailsLabel");
      JLabel nextDetails = (JLabel) TestSupport.getField(SaveOverPane.class, pane, "nextDetailsLabel");

      assertNotNull(previousView.getImage());
      assertNotNull(nextView.getImage());
      assertEquals("resolution: 6 x 4", previousDetails.getText());
      assertEquals("resolution: 8 x 5", nextDetails.getText());
      assertTrue(previousHeader.getText().startsWith("Previous (last modified: "));
      assertEquals("resolution: 7 x 9", invokeGetResolutionText(new BufferedImage(7, 9, BufferedImage.TYPE_INT_RGB)));
    });
  }

  private static String invokeGetResolutionText(BufferedImage image) throws Exception {
    return (String) TestSupport.invokeDeclared(SaveOverPane.class, null, "getResolutionText", new Class<?>[] {java.awt.Image.class}, image);
  }
}
