package org.lgna.croquet.edits;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.Element;
import org.lgna.croquet.Group;
import org.lgna.croquet.StringState;
import org.lgna.croquet.history.UserActivity;

import java.util.UUID;

import static org.junit.Assert.*;

public class StateEditRuntimeTest {
  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-1600-000000000001"), "stateEditRuntime");

  private TestStringState state;
  private UserActivity activity;

  @Before
  public void setUp() {
    state = new TestStringState("before");
    CroquetTestUtils.removeDocumentListeners(state);
    activity = new UserActivity();
    activity.setCompletionModel(state);
  }

  @Test
  public void stateEdit_redoAndUndo_updateBoundState() {
    StateEdit<String> edit = new StateEdit<>(activity, "before", "after");

    assertTrue(edit.canUndo());
    assertTrue(edit.canRedo());
    assertSame(TEST_GROUP, edit.getGroup());

    edit.doOrRedo(false);
    assertEquals("after", state.getValue());

    edit.undo();
    assertEquals("before", state.getValue());
  }

  @Test
  public void stateEdit_encodeDecodeAndCreateCopy_preserveValues() {
    StateEdit<String> original = new StateEdit<>(activity, "before", "after");
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();

    original.encode(encoder);
    BinaryDecoder decoder = encoder.createDecoder();
    StateEdit<String> decoded = new StateEdit<>(decoder, activity);
    StateEdit<String> copy = AbstractEdit.createCopy(original, activity);

    assertEquals("before", decoded.getPreviousValue());
    assertEquals("after", decoded.getNextValue());
    assertNotSame(original, copy);
    assertEquals("before", copy.getPreviousValue());
    assertEquals("after", copy.getNextValue());
  }

  @Test
  public void abstractEditCreateCopy_invokesHooks() {
    CopyAwareEdit original = new CopyAwareEdit(activity, "payload");

    CopyAwareEdit copy = AbstractEdit.createCopy(original, activity);

    assertTrue(original.preCopyCalled);
    assertSame(copy, original.postCopyResult);
    assertEquals("payload", copy.payload);
  }

  private static final class TestStringState extends StringState {
    private TestStringState(String initialValue) {
      super(TEST_GROUP, CroquetTestUtils.nextTestUUID(), initialValue);
    }

    @Override
    protected Class<? extends Element> getClassUsedForLocalization() {
      return TestStringState.class;
    }

    @Override
    protected String getSubKeyForLocalization() {
      return "stateEdit";
    }
  }

  public static final class CopyAwareEdit extends AbstractEdit<StringState> {
    private String payload;
    private boolean preCopyCalled;
    private AbstractEdit<?> postCopyResult;

    public CopyAwareEdit(UserActivity userActivity, String payload) {
      super(userActivity);
      this.payload = payload;
    }

    public CopyAwareEdit(BinaryDecoder binaryDecoder, Object step) {
      super(binaryDecoder, step);
      this.payload = binaryDecoder.decodeString();
    }

    @Override
    public void encode(BinaryEncoder binaryEncoder) {
      super.encode(binaryEncoder);
      binaryEncoder.encode(this.payload);
    }

    @Override
    protected void preCopy() {
      this.preCopyCalled = true;
    }

    @Override
    protected void postCopy(AbstractEdit<?> result) {
      this.postCopyResult = result;
    }

    @Override
    protected void doOrRedoInternal(boolean isDo) {
    }

    @Override
    protected void undoInternal() {
    }

    @Override
    protected void appendDescription(StringBuilder rv, DescriptionStyle descriptionStyle) {
      rv.append(this.payload);
    }
  }
}
