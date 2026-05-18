package org.alice.imageeditor.croquet;

import org.junit.Test;

import javax.swing.JTextField;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ImageEditorFrameBehaviorTest {
  @Test
  public void constructorDefaultsAndShapeMutatorsWork() throws Exception {
    TestSupport.onEdt(() -> {
      TestSupport.HeadlessImageEditorFrame frame = new TestSupport.HeadlessImageEditorFrame();
      assertEquals(Tool.ADD_RECTANGLE, frame.getToolState().getValue());
      assertTrue(frame.getJComboBox().isEditable());
      assertFalse(frame.getSaveOperation().isEnabled());
      assertFalse(frame.getCropOperation().isEnabled());
      assertFalse(frame.getUncropOperation().isEnabled());

      Rectangle2D shape = new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0);
      frame.addShape(shape);
      assertEquals(1, frame.getShapes().size());
      assertSame(shape, frame.getShapes().get(0));
      frame.removeShape(shape);
      assertTrue(frame.getShapes().isEmpty());
      frame.addShape(shape);
      frame.clearShapes();
      assertTrue(frame.getShapes().isEmpty());
      try {
        frame.getShapes().add(shape);
        fail("Expected unmodifiable view");
      } catch (UnsupportedOperationException expected) {
      }
    });
  }

  @Test
  public void updatePathHandlesEmptyRelativeExistingAndInvalidInputs() throws Exception {
    TestSupport.onEdt(() -> {
      TestSupport.HeadlessImageEditorFrame frame = new TestSupport.HeadlessImageEditorFrame();
      File root = TestSupport.createEmptyDirectory("image-editor-frame-update-path");
      frame.getRootDirectoryState().setValueTransactionlessly(root.getAbsolutePath());
      JTextField editor = TestSupport.getEditorTextField(frame.getJComboBox());

      editor.setText("");
      invokeUpdatePath(frame);
      assertEquals(ImageEditorFrame.INVALID_PATH_EMPTY_SUB_PATH, frame.getPathHolder().getValue());
      assertFalse(frame.getSaveOperation().isEnabled());
      assertEquals("save", getOperationName(frame.getSaveOperation()));

      editor.setText("draft");
      invokeUpdatePath(frame);
      assertEquals(new File(root, "draft.png").getAbsolutePath(), frame.getPathHolder().getValue());
      assertTrue(frame.getSaveOperation().isEnabled());
      assertEquals("save", getOperationName(frame.getSaveOperation()));
      assertEquals(new File(root, "draft.png").getAbsolutePath(), frame.getFile().getAbsolutePath());

      editor.setText("partial.pn");
      invokeUpdatePath(frame);
      assertEquals(new File(root, "partial.png").getAbsolutePath(), frame.getPathHolder().getValue());

      File existing = new File(root, "existing.png");
      java.nio.file.Files.write(existing.toPath(), new byte[] {7});
      editor.setText(existing.getName());
      invokeUpdatePath(frame);
      assertEquals(existing.getAbsolutePath(), frame.getPathHolder().getValue());
      assertEquals("save over...", getOperationName(frame.getSaveOperation()));

      frame.getRootDirectoryState().setValueTransactionlessly(existing.getAbsolutePath());
      editor.setText("ignored-name");
      invokeUpdatePath(frame);
      assertEquals(ImageEditorFrame.INVALID_PATH_NOT_A_DIRECTORY, frame.getPathHolder().getValue());
      assertFalse(frame.getSaveOperation().isEnabled());
    });
  }

  @Test
  public void preActivationEditorChangesAndCropLifecycleUpdateOperations() throws Exception {
    TestSupport.onEdt(() -> {
      TestSupport.HeadlessImageEditorFrame frame = new TestSupport.HeadlessImageEditorFrame();
      File nonDirectory = new File(TestSupport.createEmptyDirectory("image-editor-frame-lifecycle"), "not-a-directory");
      frame.getRootDirectoryState().setValueTransactionlessly(nonDirectory.getAbsolutePath());
      frame.getView();

      frame.handlePreActivation();
      JTextField editor = TestSupport.getEditorTextField(frame.getJComboBox());
      editor.setText("notes");
      assertEquals(ImageEditorFrame.INVALID_PATH_NOT_A_DIRECTORY, frame.getPathHolder().getValue());

      Rectangle selection = new Rectangle(2, 3, 4, 5);
      frame.getCropSelectHolder().setValue(selection);
      assertTrue(frame.getCropOperation().isEnabled());

      frame.crop();
      assertNull(frame.getCropSelectHolder().getValue());
      assertEquals(selection, frame.getCropCommitHolder().getValue());
      assertTrue(frame.getUncropOperation().isEnabled());

      invokeUncrop(frame);
      assertEquals(selection, frame.getCropSelectHolder().getValue());
      assertNull(frame.getCropCommitHolder().getValue());
      assertFalse(frame.getUncropOperation().isEnabled());

      frame.handlePostDeactivation();
    });
  }

  @Test
  public void setImageClearShapesAndShowFrameClearsState() throws Exception {
    TestSupport.onEdt(() -> {
      TestSupport.HeadlessImageEditorFrame frame = new TestSupport.HeadlessImageEditorFrame();
      frame.getView();
      frame.addShape(new Rectangle2D.Double(0.0, 0.0, 5.0, 5.0));
      BufferedImage image = new BufferedImage(16, 9, BufferedImage.TYPE_INT_RGB);

      frame.setImageClearShapesAndShowFrame(image);

      assertSame(image, frame.getImageHolder().getValue());
      assertTrue(frame.getShapes().isEmpty());
      assertTrue(frame.getIsFrameShowingState().getValue());
    });
  }

  private static String getOperationName(org.lgna.croquet.Operation operation) throws Exception {
    Object imp = TestSupport.getField(org.lgna.croquet.Operation.class, operation, "imp");
    return (String) TestSupport.invokeDeclared(imp.getClass(), imp, "getName", new Class<?>[0]);
  }

  private static void invokeUpdatePath(ImageEditorFrame frame) throws Exception {
    TestSupport.invokeDeclared(ImageEditorFrame.class, frame, "updatePath", new Class<?>[0]);
  }

  private static void invokeUncrop(ImageEditorFrame frame) throws Exception {
    TestSupport.invokeDeclared(ImageEditorFrame.class, frame, "uncrop", new Class<?>[0]);
  }
}
