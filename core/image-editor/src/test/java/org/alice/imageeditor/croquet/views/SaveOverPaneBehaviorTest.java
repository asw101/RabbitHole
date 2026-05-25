package org.alice.imageeditor.croquet.views;

import org.alice.imageeditor.croquet.SaveOperation;
import org.alice.imageeditor.croquet.SaveOverComposite;
import org.alice.imageeditor.croquet.TestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.lgna.croquet.views.Label;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

public class SaveOverPaneBehaviorTest {
  @Test
  public void preActivationLoadsExistingPngAndRenderedReplacementDetails() throws Exception {
    File root = TestSupport.createEmptyDirectory("save-over-pane");
    File existingFile = new File(root, "existing.png");
    BufferedImage previousImage = new BufferedImage(3, 2, BufferedImage.TYPE_INT_RGB);
    previousImage.setRGB(0, 0, Color.RED.getRGB());
    ImageIO.write(previousImage, "png", existingFile);

    TestSupport.onEdt(() -> {
      TestSupport.HeadlessImageEditorFrame frame = new TestSupport.HeadlessImageEditorFrame();
      BufferedImage nextImage = new BufferedImage(4, 3, BufferedImage.TYPE_INT_RGB);
      nextImage.setRGB(1, 1, Color.GREEN.getRGB());
      frame.getShowInScreenResolutionState().setValueTransactionlessly(false);
      frame.getImageHolder().setValue(nextImage);
      frame.getPathHolder().setValue(existingFile.getAbsolutePath());

      SaveOperation operation = new SaveOperation(frame);
      SaveOverComposite composite = new SaveOverComposite(operation);
      composite.getPrevHeader().setText("Before");
      composite.getNextHeader().setText("After");
      SaveOverPane pane = new SaveOverPane(composite);

      pane.handleCompositePreActivation();

      ImageView previousView = (ImageView) TestSupport.getField(SaveOverPane.class, pane, "toBeReplacedImageView");
      ImageView nextView = (ImageView) TestSupport.getField(SaveOverPane.class, pane, "nextImageView");
      Label previousHeader = (Label) TestSupport.getField(SaveOverPane.class, pane, "toBeReplacedHeaderLabel");
      Label previousDetails = (Label) TestSupport.getField(SaveOverPane.class, pane, "toBeReplacedDetailsLabel");
      Label nextDetails = (Label) TestSupport.getField(SaveOverPane.class, pane, "nextDetailsLabel");

      Assert.assertNotNull(previousView.getImage());
      Assert.assertNotNull(nextView.getImage());
      Assert.assertTrue(previousHeader.getText().contains("last modified:"));
      Assert.assertEquals("resolution: 3 x 2", previousDetails.getText());
      Assert.assertEquals("resolution: 4 x 3", nextDetails.getText());
    });
  }
}
