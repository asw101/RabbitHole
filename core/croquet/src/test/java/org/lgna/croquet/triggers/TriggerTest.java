package org.lgna.croquet.triggers;

import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.lgna.croquet.history.UserActivity;
import org.lgna.croquet.views.PopupMenu;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

public class TriggerTest {

  private static final class TestTrigger extends Trigger {
    private final String reprFragment;
    private PopupMenu lastPopupMenu;

    private TestTrigger(UserActivity userActivity) {
      this(userActivity, "");
    }

    private TestTrigger(UserActivity userActivity, String reprFragment) {
      super(userActivity);
      this.reprFragment = reprFragment;
    }

    @Override
    public void showPopupMenu(PopupMenu popupMenu) {
      this.lastPopupMenu = popupMenu;
    }

    @Override
    protected void appendReprInternal(StringBuilder repr) {
      repr.append(this.reprFragment);
    }
  }

  @Test
  public void constructorWithNullUserActivity_preservesNull() {
    TestTrigger trigger = new TestTrigger(null);

    assertNull(trigger.getUserActivity());
  }

  @Test
  public void constructorWithUserActivity_preservesReference() {
    UserActivity activity = new UserActivity();
    TestTrigger trigger = new TestTrigger(activity);

    assertSame(activity, trigger.getUserActivity());
  }

  @Test
  public void constructorWithUserActivity_setsTriggerOnActivity() {
    UserActivity activity = new UserActivity();
    TestTrigger trigger = new TestTrigger(activity);

    assertSame(trigger, activity.getTrigger());
  }

  @Test
  public void getViewController_defaultsToNull() {
    TestTrigger trigger = new TestTrigger(null);

    assertNull(trigger.getViewController());
  }

  @Test
  public void encode_doesNotChangeEncoderOutput() {
    TestTrigger trigger = new TestTrigger(null);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    BinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    int sizeBefore = baos.size();

    trigger.encode(encoder);

    assertEquals(sizeBefore, baos.size());
  }

  @Test
  public void appendRepr_withoutInternalContent_usesSimpleFormat() {
    TestTrigger trigger = new TestTrigger(null);
    StringBuilder repr = new StringBuilder();

    trigger.appendRepr(repr);

    assertEquals("TestTrigger[]", repr.toString());
  }

  @Test
  public void appendRepr_includesInternalContent() {
    TestTrigger trigger = new TestTrigger(null, "user");
    StringBuilder repr = new StringBuilder();

    trigger.appendRepr(repr);

    assertEquals("TestTrigger[user]", repr.toString());
  }

  @Test
  public void showPopupMenu_overrideCanBeInvokedWithNull() {
    TestTrigger trigger = new TestTrigger(null);

    trigger.showPopupMenu(null);

    assertNull(trigger.lastPopupMenu);
  }
}
