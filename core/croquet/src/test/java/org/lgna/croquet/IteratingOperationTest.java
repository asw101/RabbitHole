package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import javax.swing.Icon;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for {@link IteratingOperation} — construction, group, enable/disable,
 * button icon, and structural accessors. Does NOT test iterateOverSubModels
 * since that requires Application context.
 */
public class IteratingOperationTest {

  private static final UUID OP_ID =
      UUID.fromString("00000000-0000-0000-dddd-000000000001");
  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-dddd-000000000002"), "iterOpTest");

  private TestIteratingOperation op;

  @Before
  public void setUp() {
    op = new TestIteratingOperation(TEST_GROUP, OP_ID);
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_setsGroup() {
    assertSame(TEST_GROUP, op.getGroup());
  }

  @Test
  public void constructor_setsMigrationId() {
    assertEquals(OP_ID, op.getMigrationId());
  }

  @Test
  public void constructor_enabledByDefault() {
    assertTrue(op.isEnabled());
  }

  // ── Enable/Disable ────────────────────────────────────────────────

  @Test
  public void setEnabled_false() {
    op.setEnabled(false);
    assertFalse(op.isEnabled());
  }

  @Test
  public void setEnabled_true_afterDisable() {
    op.setEnabled(false);
    op.setEnabled(true);
    assertTrue(op.isEnabled());
  }

  // ── Button Icon ───────────────────────────────────────────────────

  @Test
  public void getButtonIcon_initiallyNull() {
    assertNull(op.getButtonIcon());
  }

  @Test
  public void setButtonIcon_storesIcon() {
    Icon icon = new StubIcon();
    op.setButtonIcon(icon);
    assertSame(icon, op.getButtonIcon());
  }

  // ── Sidekick Label ────────────────────────────────────────────────

  @Test
  public void hasSidekickLabel_initiallyFalse() {
    assertFalse(op.hasSidekickLabel());
  }

  @Test
  public void getSidekickLabel_returnsNonNull() {
    assertNotNull(op.getSidekickLabel());
  }

  @Test
  public void hasSidekickLabel_trueAfterGet() {
    op.getSidekickLabel();
    assertTrue(op.hasSidekickLabel());
  }

  // ── getImp ────────────────────────────────────────────────────────

  @Test
  public void getImp_returnsNonNull() {
    assertNotNull(op.getImp());
  }

  // ── setName ───────────────────────────────────────────────────────

  @Test
  public void setName_updatesImpName() {
    op.setName("Iterate Test");
    assertEquals("Iterate Test", op.getImp().getName());
  }

  // ── initializeIfNecessary ─────────────────────────────────────────

  @Test
  public void initializeIfNecessary_callsLocalize() {
    op.initializeIfNecessary();
    assertTrue(op.localizeCalled);
  }

  @Test
  public void initializeIfNecessary_idempotent() {
    op.initializeIfNecessary();
    op.localizeCalled = false;
    op.initializeIfNecessary();
    assertFalse(op.localizeCalled);
  }

  // ── appendUserRepr ────────────────────────────────────────────────

  @Test
  public void appendUserRepr_containsClassName() {
    StringBuilder sb = new StringBuilder();
    op.appendUserRepr(sb);
    assertTrue(sb.toString().contains("TestIteratingOperation"));
  }

  // ── getMenuItemPrepModel ──────────────────────────────────────────

  @Test
  public void getMenuItemPrepModel_returnsNonNull() {
    assertNotNull(op.getMenuItemPrepModel());
  }

  // ── Multiple instances with different groups ──────────────────────

  @Test
  public void differentGroup_returnsNewGroup() {
    Group other = Group.getInstance(UUID.fromString("00000000-0000-0000-dddd-000000000003"), "otherGroup");
    TestIteratingOperation op2 = new TestIteratingOperation(other, CroquetTestUtils.nextTestUUID());
    assertSame(other, op2.getGroup());
    assertNotSame(op.getGroup(), op2.getGroup());
  }

  // ── Concrete test subclass ────────────────────────────────────────

  static class TestIteratingOperation extends IteratingOperation {
    boolean localizeCalled = false;

    TestIteratingOperation(Group group, UUID id) {
      super(group, id);
    }

    @Override
    protected void localize() {
      localizeCalled = true;
    }

    @Override
    protected boolean hasNext(List<UserActivity> finishedSteps) {
      return false;
    }

    @Override
    protected Triggerable getNext(List<UserActivity> finishedSteps) {
      return null;
    }

    @Override
    protected void performInActivity(UserActivity userActivity) {
      // no-op for test
    }
  }

  static class StubIcon implements Icon {
    @Override
    public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {}

    @Override
    public int getIconWidth() { return 16; }

    @Override
    public int getIconHeight() { return 16; }
  }
}
