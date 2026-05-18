package org.alice.imageeditor.croquet.views;

import org.alice.imageeditor.croquet.TestSupport;
import org.junit.Test;

import java.awt.image.BufferedImage;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class ImageViewTest {
  @Test
  public void getAndSetImageDelegateToSwingComponent() throws Exception {
    TestSupport.onEdt(() -> {
      ImageView view = new ImageView();
      BufferedImage image = new BufferedImage(7, 9, BufferedImage.TYPE_INT_RGB);

      assertNotNull(view.getAwtComponent());
      assertNull(view.getImage());

      view.setImage(image);
      assertSame(image, view.getImage());
    });
  }
}
