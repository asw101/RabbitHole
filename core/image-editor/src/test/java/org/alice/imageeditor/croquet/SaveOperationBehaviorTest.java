package org.alice.imageeditor.croquet;

import org.junit.Assert;
import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.croquet.BooleanState;
import org.lgna.croquet.CancelException;
import org.lgna.croquet.Triggerable;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.UUID;

public class SaveOperationBehaviorTest {
  @Test
  public void hasNextDependsOnFrameFileState() throws Exception {
    File missing = new File(TestSupport.createEmptyDirectory("save-operation-state"), "missing.png");
    RecordingFrame frame = TestSupport.callOnEdt(() -> new RecordingFrame(missing));
    SaveOperation operation = TestSupport.callOnEdt(() -> new SaveOperation(frame));

    Assert.assertFalse(TestSupport.callOnEdt(() -> operation.hasNext(Collections.emptyList())));

    Files.write(missing.toPath(), new byte[] {1, 2, 3});
    Assert.assertTrue(TestSupport.callOnEdt(() -> operation.hasNext(Collections.emptyList())));

    frame.file = null;
    Assert.assertTrue(TestSupport.callOnEdt(() -> operation.hasNext(Collections.emptyList())));
  }

  @Test
  public void getNextRequiresCropApprovalAndAFileSelection() throws Exception {
    File existing = new File(TestSupport.createEmptyDirectory("save-operation-next"), "existing.png");
    Files.write(existing.toPath(), new byte[] {1});
    RecordingFrame frame = TestSupport.callOnEdt(() -> new RecordingFrame(existing));
    SaveOperation operation = TestSupport.callOnEdt(() -> new SaveOperation(frame));

    Assert.assertTrue(TestSupport.callOnEdt(() -> operation.hasNext(Collections.emptyList())));
    Triggerable triggerable = TestSupport.callOnEdt(() -> operation.getNext(Collections.emptyList()));
    Assert.assertNotNull(triggerable);

    frame.goodToGo = false;
    TestSupport.onEdt(() -> {
      try {
        operation.getNext(Collections.emptyList());
        Assert.fail("Expected cancel when cropping is not approved");
      } catch (CancelException expected) {
        Assert.assertNotNull(expected);
      }
    });

    frame.goodToGo = true;
    frame.file = null;
    Assert.assertTrue(TestSupport.callOnEdt(() -> operation.hasNext(Collections.emptyList())));
  }

  @Test
  public void successfulCompletionWritesRenderedImageAndHidesFrame() throws Exception {
    File outputFile = new File(TestSupport.createEmptyDirectory("save-operation-write"), "written.png");
    RecordingFrame frame = TestSupport.callOnEdt(() -> new RecordingFrame(outputFile));
    SaveOperation operation = TestSupport.callOnEdt(() -> new SaveOperation(frame));

    TestSupport.onEdt(() -> {
      frame.getImageHolder().setValue(new BufferedImage(3, 2, BufferedImage.TYPE_INT_RGB));
      frame.getIsFrameShowingState().setValueTransactionlessly(true);
      TestSupport.setField(SaveOperation.class, operation, "file", outputFile);
      operation.handleSuccessfulCompletionOfSubModels(null);
    });

    Assert.assertTrue(outputFile.isFile());
    BufferedImage image = javax.imageio.ImageIO.read(outputFile);
    Assert.assertNotNull(image);
    Assert.assertEquals(3, image.getWidth());
    Assert.assertEquals(2, image.getHeight());
    Assert.assertFalse(TestSupport.callOnEdt(() -> frame.getIsFrameShowingState().getValue()));
  }

  private static final class RecordingFrame extends TestSupport.HeadlessImageEditorFrame {
    private final BooleanState isFrameShowingState = new BooleanState(Application.INHERIT_GROUP, UUID.randomUUID(), false) {
    };
    private File file;
    private boolean goodToGo = true;

    private RecordingFrame(File file) {
      this.file = file;
    }

    @Override
    public BooleanState getIsFrameShowingState() {
      return this.isFrameShowingState;
    }

    @Override
    public File getFile() {
      return this.file;
    }

    @Override
    boolean isGoodToGoCroppingIfNecessary() {
      return this.goodToGo;
    }
  }
}
