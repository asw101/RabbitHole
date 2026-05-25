package org.alice.imageeditor.croquet;

import org.junit.Assert;
import org.junit.Test;
import org.lgna.croquet.event.ValueEvent;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;

public class ImageEditorFrameBehaviorTest {
  @Test
  public void updatePathHandlesExistingAndNewPngTargetsAndCropState() throws Exception {
    File root = TestSupport.createEmptyDirectory("image-editor-frame-paths");
    Files.write(new File(root, "existing.png").toPath(), new byte[] {1, 2, 3});

    TestSupport.onEdt(() -> {
      TestSupport.HeadlessImageEditorFrame frame = new TestSupport.HeadlessImageEditorFrame();
      javax.swing.JTextField editor = TestSupport.getEditorTextField(frame.getJComboBox());
      frame.getRootDirectoryState().setValueTransactionlessly(root.getAbsolutePath());

      editor.setText("existing");
      invokeUpdatePath(frame);
      Assert.assertEquals(new File(root, "existing.png").getAbsolutePath(), frame.getPathHolder().getValue());
      Assert.assertEquals("save over...", getOperationName(frame.getSaveOperation()));
      Assert.assertTrue(frame.getSaveOperation().isEnabled());

      editor.setText("nested/fresh");
      invokeUpdatePath(frame);
      Assert.assertEquals(new File(root, "nested/fresh.png").getAbsolutePath(), frame.getPathHolder().getValue());
      Assert.assertEquals("save", getOperationName(frame.getSaveOperation()));

      Rectangle crop = new Rectangle(1, 2, 3, 4);
      invokeCropSelectChanged(frame, crop);
      Assert.assertTrue(frame.getCropOperation().isEnabled());

      frame.getCropSelectHolder().setValue(crop);
      frame.crop();
      Assert.assertNull(frame.getCropSelectHolder().getValue());
      Assert.assertEquals(crop, frame.getCropCommitHolder().getValue());

      invokeCropCommitChanged(frame, crop);
      Assert.assertTrue(frame.getUncropOperation().isEnabled());

      TestSupport.invokeDeclared(ImageEditorFrame.class, frame, "uncrop", new Class<?>[0]);
      Assert.assertEquals(crop, frame.getCropSelectHolder().getValue());
      Assert.assertNull(frame.getCropCommitHolder().getValue());
    });
  }

  @Test
  public void invalidRootClearsSaveStateAndShowingFrameResetsShapes() throws Exception {
    File root = TestSupport.createEmptyDirectory("image-editor-frame-invalid-root");
    File notDirectory = new File(root, "not-a-directory.txt");
    Files.write(notDirectory.toPath(), new byte[] {7});

    TestSupport.onEdt(() -> {
      RecordingFrame frame = new RecordingFrame();
      javax.swing.JTextField editor = TestSupport.getEditorTextField(frame.getJComboBox());
      frame.getRootDirectoryState().setValueTransactionlessly(notDirectory.getAbsolutePath());
      editor.setText("capture");

      invokeUpdatePath(frame);
      Assert.assertEquals(ImageEditorFrame.INVALID_PATH_NOT_A_DIRECTORY, frame.getPathHolder().getValue());
      Assert.assertEquals("save", getOperationName(frame.getSaveOperation()));
      Assert.assertFalse(frame.getSaveOperation().isEnabled());

      Rectangle2D.Double shape = new Rectangle2D.Double(0.0, 0.0, 4.0, 4.0);
      frame.addShape(shape);
      Assert.assertEquals(1, frame.getShapes().size());
      frame.removeShape(shape);
      Assert.assertTrue(frame.getShapes().isEmpty());

      frame.addShape(shape);
      BufferedImage image = new BufferedImage(5, 4, BufferedImage.TYPE_INT_RGB);
      frame.setImageClearShapesAndShowFrame(image);
      Assert.assertSame(image, frame.getImageHolder().getValue());
      Assert.assertTrue(frame.getShapes().isEmpty());
      Assert.assertTrue(frame.getIsFrameShowingState().getValue());

      frame.addShape(shape);
      frame.clearShapes();
      Assert.assertTrue(frame.getShapes().isEmpty());
    });
  }

  private static void invokeUpdatePath(ImageEditorFrame frame) {
    try {
      TestSupport.invokeDeclared(ImageEditorFrame.class, frame, "updatePath", new Class<?>[0]);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static void invokeCropSelectChanged(ImageEditorFrame frame, Rectangle crop) {
    try {
      TestSupport.invokeDeclared(
          ImageEditorFrame.class,
          frame,
          "handleCropSelectChanged",
          new Class<?>[] {ValueEvent.class},
          ValueEvent.createInstance(crop));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static void invokeCropCommitChanged(ImageEditorFrame frame, Rectangle crop) {
    try {
      TestSupport.invokeDeclared(
          ImageEditorFrame.class,
          frame,
          "handleCropCommitChanged",
          new Class<?>[] {ValueEvent.class},
          ValueEvent.createInstance(crop));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static final class RecordingFrame extends TestSupport.HeadlessImageEditorFrame {
    private final org.lgna.croquet.BooleanState isFrameShowingState = new org.lgna.croquet.BooleanState(
        org.lgna.croquet.Application.INHERIT_GROUP,
        java.util.UUID.randomUUID(),
        false) {
    };

    @Override
    public org.lgna.croquet.BooleanState getIsFrameShowingState() {
      return this.isFrameShowingState;
    }
  }

  private static String getOperationName(org.lgna.croquet.Operation operation) {
    try {
      Object imp = TestSupport.getField(org.lgna.croquet.Operation.class, operation, "imp");
      return (String) imp.getClass().getMethod("getName").invoke(imp);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
