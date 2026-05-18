package org.alice.imageeditor.croquet;

import org.junit.Test;
import org.lgna.croquet.CancelException;
import org.lgna.croquet.Triggerable;
import org.lgna.croquet.history.UserActivity;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SaveOperationTest {
  private static final class StubImageEditorFrame extends TestSupport.HeadlessImageEditorFrame {
    private File file;
    private boolean goodToGo = true;

    @Override
    public File getFile() {
      return this.file;
    }

    @Override
    boolean isGoodToGoCroppingIfNecessary() {
      return this.goodToGo;
    }
  }

  @Test
  public void hasNextReflectsCurrentFileState() throws Exception {
    TestSupport.onEdt(() -> {
      File dir = TestSupport.createEmptyDirectory("save-operation-has-next");
      File existing = new File(dir, "existing.png");
      java.nio.file.Files.write(existing.toPath(), new byte[] {1});

      TestSupport.HeadlessImageEditorFrame frame = new TestSupport.HeadlessImageEditorFrame();
      frame.getPathHolder().setValue(existing.getAbsolutePath());
      SaveOperation existingOperation = new SaveOperation(frame);
      assertTrue(invokeHasNext(existingOperation, Collections.<UserActivity>emptyList()));
      assertFalse(invokeHasNext(existingOperation, Collections.singletonList(new UserActivity())));

      frame.getPathHolder().setValue(new File(dir, "missing.png").getAbsolutePath());
      SaveOperation missingOperation = new SaveOperation(frame);
      assertFalse(invokeHasNext(missingOperation, Collections.<UserActivity>emptyList()));
    });
  }

  @Test
  public void getNextReturnsTriggerableNullOrCancelDependingOnState() throws Exception {
    TestSupport.onEdt(() -> {
      File dir = TestSupport.createEmptyDirectory("save-operation-get-next");
      File existing = new File(dir, "existing.png");
      java.nio.file.Files.write(existing.toPath(), new byte[] {1});

      StubImageEditorFrame frame = new StubImageEditorFrame();
      frame.file = existing;
      frame.goodToGo = true;
      SaveOperation operation = new SaveOperation(frame);
      assertTrue(invokeHasNext(operation, Collections.<UserActivity>emptyList()));
      Triggerable firstTriggerable = invokeGetNext(operation, Collections.<UserActivity>emptyList());
      assertNotNull(firstTriggerable);
      assertNotNull(invokeGetNext(operation, Collections.singletonList(new UserActivity())));

      frame.file = new File(dir, "missing.png");
      SaveOperation missingOperation = new SaveOperation(frame);
      assertFalse(invokeHasNext(missingOperation, Collections.<UserActivity>emptyList()));
      assertTrue(invokeGetNext(missingOperation, Collections.<UserActivity>emptyList()) == null);

      frame.file = existing;
      frame.goodToGo = false;
      SaveOperation cancelOperation = new SaveOperation(frame);
      assertTrue(invokeHasNext(cancelOperation, Collections.<UserActivity>emptyList()));
      try {
        invokeGetNext(cancelOperation, Collections.<UserActivity>emptyList());
        fail("Expected cancel when cropping precondition fails");
      } catch (InvocationTargetException ite) {
        assertTrue(ite.getCause() instanceof CancelException);
      }
    });
  }

  @Test
  public void successfulCompletionWritesImageAndHidesFrame() throws Exception {
    TestSupport.onEdt(() -> {
      TestSupport.HeadlessImageEditorFrame frame = new TestSupport.HeadlessImageEditorFrame();
      frame.getImageHolder().setValue(new BufferedImage(14, 11, BufferedImage.TYPE_INT_RGB));
      frame.getView();
      frame.getIsFrameShowingState().setValueTransactionlessly(true);

      File dir = TestSupport.createEmptyDirectory("save-operation-complete");
      File output = new File(dir, "written.png");
      frame.getPathHolder().setValue(output.getAbsolutePath());

      SaveOperation operation = new SaveOperation(frame);
      invokeHasNext(operation, Collections.<UserActivity>emptyList());
      invokeHandleSuccessfulCompletion(operation, new UserActivity());

      assertTrue(output.isFile());
      assertTrue(output.length() > 0);
      assertFalse(frame.getIsFrameShowingState().getValue());
    });
  }

  private static boolean invokeHasNext(SaveOperation operation, java.util.List<UserActivity> steps) throws Exception {
    return (Boolean) TestSupport.invokeDeclared(SaveOperation.class, operation, "hasNext", new Class<?>[] {java.util.List.class}, steps);
  }

  private static Triggerable invokeGetNext(SaveOperation operation, java.util.List<UserActivity> steps) throws Exception {
    return (Triggerable) TestSupport.invokeDeclared(SaveOperation.class, operation, "getNext", new Class<?>[] {java.util.List.class}, steps);
  }

  private static void invokeHandleSuccessfulCompletion(SaveOperation operation, UserActivity activity) throws Exception {
    TestSupport.invokeDeclared(SaveOperation.class, operation, "handleSuccessfulCompletionOfSubModels", new Class<?>[] {UserActivity.class}, activity);
  }
}
