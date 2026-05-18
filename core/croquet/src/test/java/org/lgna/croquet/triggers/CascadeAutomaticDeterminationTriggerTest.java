package org.lgna.croquet.triggers;

import org.lgna.croquet.history.UserActivity;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link CascadeAutomaticDeterminationTrigger} — trigger that creates
 * a child activity from a parent and delegates view/popup to the previous trigger.
 */
public class CascadeAutomaticDeterminationTriggerTest {

  @Test
  public void createChildActivity_returnsNonNull() {
    UserActivity parent = new UserActivity();
    // Must set a trigger on parent first
    IterationTrigger.createUserInstance(parent);
    UserActivity child = CascadeAutomaticDeterminationTrigger.createChildActivity(parent);
    assertNotNull(child);
  }

  @Test
  public void createChildActivity_childIsOwnedByParent() {
    UserActivity parent = new UserActivity();
    IterationTrigger.createUserInstance(parent);
    UserActivity child = CascadeAutomaticDeterminationTrigger.createChildActivity(parent);
    assertSame(parent, child.getOwner());
  }

  @Test
  public void createChildActivity_childHasTrigger() {
    UserActivity parent = new UserActivity();
    IterationTrigger.createUserInstance(parent);
    UserActivity child = CascadeAutomaticDeterminationTrigger.createChildActivity(parent);
    assertNotNull(child.getTrigger());
    assertTrue(child.getTrigger() instanceof CascadeAutomaticDeterminationTrigger);
  }

  @Test
  public void createChildActivity_childIsPending() {
    UserActivity parent = new UserActivity();
    IterationTrigger.createUserInstance(parent);
    UserActivity child = CascadeAutomaticDeterminationTrigger.createChildActivity(parent);
    assertTrue(child.isPending());
  }

  @Test
  public void createChildActivity_addsToParentChildren() {
    UserActivity parent = new UserActivity();
    IterationTrigger.createUserInstance(parent);
    int beforeCount = parent.getChildActivities().size();
    CascadeAutomaticDeterminationTrigger.createChildActivity(parent);
    assertEquals(beforeCount + 1, parent.getChildActivities().size());
  }

  @Test
  public void getViewController_delegatesToPreviousTrigger() {
    UserActivity parent = new UserActivity();
    IterationTrigger parentTrigger = IterationTrigger.createUserInstance(parent);
    UserActivity child = CascadeAutomaticDeterminationTrigger.createChildActivity(parent);
    // IterationTrigger.getViewController() returns null
    // CascadeAutomaticDeterminationTrigger delegates to previous trigger
    assertNull(child.getTrigger().getViewController());
  }

  @Test
  public void appendRepr_containsClassName() {
    UserActivity parent = new UserActivity();
    IterationTrigger.createUserInstance(parent);
    UserActivity child = CascadeAutomaticDeterminationTrigger.createChildActivity(parent);
    StringBuilder sb = new StringBuilder();
    child.getTrigger().appendRepr(sb);
    assertTrue(sb.toString().contains("CascadeAutomaticDeterminationTrigger"));
  }

  @Test
  public void multipleChildren_eachGetOwnTrigger() {
    UserActivity parent = new UserActivity();
    IterationTrigger.createUserInstance(parent);
    UserActivity child1 = CascadeAutomaticDeterminationTrigger.createChildActivity(parent);
    UserActivity child2 = CascadeAutomaticDeterminationTrigger.createChildActivity(parent);
    assertNotSame(child1.getTrigger(), child2.getTrigger());
  }

  @Test
  public void extendsTrigger() {
    UserActivity parent = new UserActivity();
    IterationTrigger.createUserInstance(parent);
    UserActivity child = CascadeAutomaticDeterminationTrigger.createChildActivity(parent);
    assertTrue(child.getTrigger() instanceof Trigger);
  }
}
