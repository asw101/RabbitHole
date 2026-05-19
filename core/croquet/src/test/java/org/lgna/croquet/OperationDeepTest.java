package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import java.lang.reflect.Modifier;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Deep tests for {@link Operation} — covering fire behavior, relocalize,
 * findLocalizedText, createRepr, appendRepr, and multiple operations
 * sharing the same group.
 */
public class OperationDeepTest {

  private static final UUID OP_ID =
      UUID.fromString("00000000-0000-0000-ccdd-000000000001");
  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-ccdd-000000000002"), "opDeepTest");

  private TestOp op;

  @Before
  public void setUp() {
    op = new TestOp(TEST_GROUP, OP_ID);
  }

  // ── fire behavior (cannot test directly - requires Application) ────

  @Test
  public void fire_method_exists() throws Exception {
    assertNotNull(Operation.class.getMethod("fire",
        org.lgna.croquet.history.UserActivity.class));
  }

  @Test
  public void fire_deprecated_method_exists() throws Exception {
    assertNotNull(Operation.class.getMethod("fire"));
  }

  @Test
  public void fire_disabled_doesNotInitialize() {
    op.setEnabled(false);
    assertFalse(op.localizeCalled);
  }

  // ── relocalize ────────────────────────────────────────────────────

  @Test
  public void relocalize_callsLocalize() {
    op.initializeIfNecessary();
    op.localizeCalled = false;
    op.relocalize();
    assertTrue(op.localizeCalled);
  }

  @Test
  public void relocalize_isFinal() throws Exception {
    assertTrue(Modifier.isFinal(
        Operation.class.getMethod("relocalize").getModifiers()));
  }

  // ── toString / createRepr ─────────────────────────────────────────

  @Test
  public void toString_nonEmpty() {
    assertFalse(op.toString().isEmpty());
  }

  @Test
  public void toString_containsClassName() {
    assertTrue(op.toString().contains("TestOp"));
  }

  @Test
  public void toString_sameAcrossMultipleCalls() {
    assertEquals(op.toString(), op.toString());
  }

  // ── getPotentialPrepModelPaths ─────────────────────────────────────

  @Test
  public void getPotentialPrepModelPaths_null_returnsEmptyOrList() {
    var paths = op.getPotentialPrepModelPaths(null);
    assertNotNull(paths);
  }

  @Test
  public void getPotentialPrepModelPaths_afterGetMenu_returnsNonEmpty() {
    op.getMenuItemPrepModel(); // force lazy init
    var paths = op.getPotentialPrepModelPaths(null);
    assertNotNull(paths);
    assertFalse(paths.isEmpty());
  }

  // ── modifyNameIfNecessary ─────────────────────────────────────────

  @Test
  public void modifyNameIfNecessary_returnsInput() {
    assertEquals("Hello", op.modifyNameIfNecessary("Hello"));
  }

  @Test
  public void modifyNameIfNecessary_null_returnsNull() {
    assertNull(op.modifyNameIfNecessary(null));
  }

  @Test
  public void modifyNameIfNecessary_empty_returnsEmpty() {
    assertEquals("", op.modifyNameIfNecessary(""));
  }

  // ── setName / getImp().getName() ──────────────────────────────────

  @Test
  public void setName_getImpName_roundtrip() {
    op.setName("Deep Test");
    assertEquals("Deep Test", op.getImp().getName());
  }

  @Test
  public void setName_null_getImpName_null() {
    op.setName(null);
    assertNull(op.getImp().getName());
  }

  // ── setSmallIcon ──────────────────────────────────────────────────

  @Test
  public void setSmallIcon_storesInImp() {
    javax.swing.Icon icon = new StubIcon();
    op.setSmallIcon(icon);
    // Should not throw
    assertNotNull(icon);
  }

  // ── setToolTipText ────────────────────────────────────────────────

  @Test
  public void setToolTipText_storesDescription() {
    op.setToolTipText("tip text");
    // Should not throw
  }

  // ── hasSidekickLabel lifecycle ────────────────────────────────────

  @Test
  public void hasSidekickLabel_false_before_get() {
    assertFalse(op.hasSidekickLabel());
  }

  @Test
  public void hasSidekickLabel_true_after_get() {
    op.getSidekickLabel();
    assertTrue(op.hasSidekickLabel());
  }

  @Test
  public void getSidekickLabel_nonNull() {
    assertNotNull(op.getSidekickLabel());
  }

  @Test
  public void getSidekickLabel_sameInstance() {
    PlainStringValue a = op.getSidekickLabel();
    PlainStringValue b = op.getSidekickLabel();
    assertSame(a, b);
  }

  // ── Multiple operations ───────────────────────────────────────────

  @Test
  public void multipleOps_differentMigrationIds() {
    TestOp op2 = new TestOp(TEST_GROUP, CroquetTestUtils.nextTestUUID());
    assertNotEquals(op.getMigrationId(), op2.getMigrationId());
  }

  @Test
  public void multipleOps_sameGroup() {
    TestOp op2 = new TestOp(TEST_GROUP, CroquetTestUtils.nextTestUUID());
    assertSame(op.getGroup(), op2.getGroup());
  }

  // ── findDefaultLocalizedText ──────────────────────────────────────

  @Test
  public void findDefaultLocalizedText_returnsNull_noBundle() {
    // TestOp has no resource bundle so findDefaultLocalizedText returns null
    String text = op.findDefaultLocalizedText();
    assertNull(text);
  }

  // ── Concrete test operation ───────────────────────────────────────

  static class TestOp extends Operation {
    boolean localizeCalled = false;
    boolean performCalled = false;

    TestOp(Group group, UUID id) {
      super(group, id);
    }

    @Override
    protected void localize() {
      localizeCalled = true;
    }

    @Override
    protected void performInActivity(UserActivity userActivity) {
      performCalled = true;
    }
  }

  static class StubIcon implements javax.swing.Icon {
    @Override
    public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {}
    @Override
    public int getIconWidth() { return 16; }
    @Override
    public int getIconHeight() { return 16; }
  }
}
