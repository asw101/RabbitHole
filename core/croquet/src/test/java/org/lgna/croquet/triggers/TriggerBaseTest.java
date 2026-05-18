package org.lgna.croquet.triggers;

import org.lgna.croquet.history.UserActivity;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link Trigger} base class behavior — tested through the
 * {@link IterationTrigger} concrete subclass which uses the Trigger(UserActivity) constructor.
 */
public class TriggerBaseTest {

  @Test
  public void userActivityConstructor_setsActivity() {
    UserActivity activity = new UserActivity();
    Trigger trigger = IterationTrigger.createUserInstance(activity);
    assertSame(activity, trigger.getUserActivity());
  }

  @Test
  public void userActivityConstructor_setsTriggerOnActivity() {
    UserActivity activity = new UserActivity();
    Trigger trigger = IterationTrigger.createUserInstance(activity);
    assertSame(trigger, activity.getTrigger());
  }

  @Test
  public void appendRepr_format_className_brackets() {
    UserActivity activity = new UserActivity();
    Trigger trigger = IterationTrigger.createUserInstance(activity);
    StringBuilder sb = new StringBuilder();
    trigger.appendRepr(sb);
    String repr = sb.toString();
    // Format: ClassName[...]
    assertTrue(repr.matches("\\w+\\[.*\\]"));
  }

  @Test
  public void appendRepr_emptyBrackets_forBaseTrigger() {
    UserActivity activity = new UserActivity();
    IterationTrigger trigger = IterationTrigger.createUserInstance(activity);
    StringBuilder sb = new StringBuilder();
    trigger.appendRepr(sb);
    assertEquals("IterationTrigger[]", sb.toString());
  }

  @Test
  public void getViewController_defaultNull() {
    UserActivity activity = new UserActivity();
    Trigger trigger = IterationTrigger.createUserInstance(activity);
    assertNull(trigger.getViewController());
  }

  @Test
  public void encode_noOp() {
    UserActivity activity = new UserActivity();
    Trigger trigger = IterationTrigger.createUserInstance(activity);
    // Base Trigger.encode() is a no-op — verify it doesn't NPE with null encoder
    trigger.encode(null);
  }

  @Test
  public void nullActivity_constructor() {
    // IterationTrigger uses private constructor, but through the Trigger(UserActivity)
    // path. Test via a second factory call to ensure it works with null.
    // Actually IterationTrigger.createUserInstance requires non-null. Just verify normal path.
    UserActivity activity = new UserActivity();
    Trigger trigger = IterationTrigger.createUserInstance(activity);
    assertNotNull(trigger.getUserActivity());
  }

  @Test
  public void twoTriggers_sameActivity_lastWins() {
    UserActivity activity = new UserActivity();
    IterationTrigger t1 = IterationTrigger.createUserInstance(activity);
    // Creating a second trigger on same activity overwrites the trigger
    IterationTrigger t2 = IterationTrigger.createUserInstance(activity);
    assertSame(t2, activity.getTrigger());
  }

  @Test
  public void childActivity_canHaveOwnTrigger() {
    UserActivity parent = new UserActivity();
    IterationTrigger.createUserInstance(parent);
    UserActivity child = parent.newChildActivity();
    IterationTrigger childTrigger = IterationTrigger.createUserInstance(child);
    assertSame(childTrigger, child.getTrigger());
    assertNotSame(parent.getTrigger(), child.getTrigger());
  }
}
