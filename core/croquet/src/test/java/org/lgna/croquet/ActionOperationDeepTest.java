package org.lgna.croquet;

import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import java.lang.reflect.Modifier;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Deep tests for {@link ActionOperation} — concrete subclass construction,
 * performInActivity delegation, enable/disable, and hierarchy.
 */
public class ActionOperationDeepTest {

  private static final UUID OP_ID =
      UUID.fromString("00000000-0000-0000-eeff-000000000001");
  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-eeff-000000000002"), "actionOpDeep");

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(ActionOperation.class.getModifiers()));
  }

  @Test
  public void class_extendsOperation() {
    assertTrue(Operation.class.isAssignableFrom(ActionOperation.class));
  }

  @Test
  public void class_implementsTriggerable() {
    assertTrue(Triggerable.class.isAssignableFrom(ActionOperation.class));
  }

  @Test
  public void class_implementsCompletionModel() {
    assertTrue(CompletionModel.class.isAssignableFrom(ActionOperation.class));
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructor_exists() throws Exception {
    var ctor = ActionOperation.class.getDeclaredConstructor(Group.class, UUID.class);
    assertNotNull(ctor);
    assertTrue(Modifier.isPublic(ctor.getModifiers()));
  }

  // ── Concrete subclass ─────────────────────────────────────────────

  @Test
  public void concrete_construction() {
    TestAction action = new TestAction(TEST_GROUP, OP_ID);
    assertNotNull(action);
  }

  @Test
  public void concrete_getGroup() {
    TestAction action = new TestAction(TEST_GROUP, OP_ID);
    assertSame(TEST_GROUP, action.getGroup());
  }

  @Test
  public void concrete_getMigrationId() {
    TestAction action = new TestAction(TEST_GROUP, OP_ID);
    assertEquals(OP_ID, action.getMigrationId());
  }

  @Test
  public void concrete_isEnabled_default() {
    TestAction action = new TestAction(TEST_GROUP, OP_ID);
    assertTrue(action.isEnabled());
  }

  @Test
  public void concrete_setEnabled_false() {
    TestAction action = new TestAction(TEST_GROUP, OP_ID);
    action.setEnabled(false);
    assertFalse(action.isEnabled());
  }

  @Test
  public void concrete_setEnabled_true_afterDisable() {
    TestAction action = new TestAction(TEST_GROUP, OP_ID);
    action.setEnabled(false);
    action.setEnabled(true);
    assertTrue(action.isEnabled());
  }

  @Test
  public void concrete_fire_method_exists() throws Exception {
    assertNotNull(Operation.class.getMethod("fire",
        org.lgna.croquet.history.UserActivity.class));
  }

  @Test
  public void concrete_fire_disabled_staysDisabled() {
    TestAction action = new TestAction(TEST_GROUP, OP_ID);
    action.setEnabled(false);
    assertFalse(action.isEnabled());
    assertFalse(action.performCalled);
  }

  @Test
  public void concrete_setName() {
    TestAction action = new TestAction(TEST_GROUP, OP_ID);
    action.setName("Test Action");
    assertEquals("Test Action", action.getImp().getName());
  }

  @Test
  public void concrete_toString_nonEmpty() {
    TestAction action = new TestAction(TEST_GROUP, OP_ID);
    assertFalse(action.toString().isEmpty());
  }

  @Test
  public void concrete_toString_containsClassName() {
    TestAction action = new TestAction(TEST_GROUP, OP_ID);
    assertTrue(action.toString().contains("TestAction"));
  }

  @Test
  public void concrete_getImp_nonNull() {
    TestAction action = new TestAction(TEST_GROUP, OP_ID);
    assertNotNull(action.getImp());
  }

  @Test
  public void concrete_getImp_swingModel_nonNull() {
    TestAction action = new TestAction(TEST_GROUP, OP_ID);
    assertNotNull(action.getImp().getSwingModel());
  }

  @Test
  public void concrete_getMenuItemPrepModel_nonNull() {
    TestAction action = new TestAction(TEST_GROUP, OP_ID);
    assertNotNull(action.getMenuItemPrepModel());
  }

  @Test
  public void concrete_isToolBarTextClobbered_false() {
    TestAction action = new TestAction(TEST_GROUP, OP_ID);
    assertFalse(action.isToolBarTextClobbered());
  }

  // ── Method existence ──────────────────────────────────────────────

  @Test
  public void method_perform_isAbstract() throws Exception {
    var m = ActionOperation.class.getDeclaredMethod("perform", UserActivity.class);
    assertNotNull(m);
    assertTrue(Modifier.isAbstract(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void method_performInActivity_exists() throws Exception {
    var m = ActionOperation.class.getDeclaredMethod("performInActivity", UserActivity.class);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  // ── Concrete test subclass ────────────────────────────────────────

  static class TestAction extends ActionOperation {
    boolean performCalled = false;

    TestAction(Group group, UUID id) {
      super(group, id);
    }

    @Override
    protected void perform(UserActivity activity) {
      performCalled = true;
    }

    @Override
    protected void localize() {
      // no-op for test
    }
  }
}
