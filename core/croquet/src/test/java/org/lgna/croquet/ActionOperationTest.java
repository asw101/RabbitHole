package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import javax.swing.Icon;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for {@link ActionOperation} — construction, group, enable/disable,
 * button icon, sidekick label, and initialization lifecycle.
 */
public class ActionOperationTest {

  private static final UUID OP_ID =
      UUID.fromString("00000000-0000-0000-aaaa-000000000001");
  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-aaaa-000000000002"), "actionOpTest");

  private TestActionOperation op;

  @Before
  public void setUp() {
    op = new TestActionOperation(TEST_GROUP, OP_ID);
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

  @Test
  public void setEnabled_sameValue_noException() {
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

  @Test
  public void setButtonIcon_null_clears() {
    op.setButtonIcon(new StubIcon());
    op.setButtonIcon(null);
    assertNull(op.getButtonIcon());
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

  // ── isToolBarTextClobbered ────────────────────────────────────────

  @Test
  public void isToolBarTextClobbered_defaultFalse() {
    assertFalse(op.isToolBarTextClobbered());
  }

  // ── setName ───────────────────────────────────────────────────────

  @Test
  public void setName_updatesImpName() {
    op.setName("Test Action");
    assertEquals("Test Action", op.getImp().getName());
  }

  // ── getImp ────────────────────────────────────────────────────────

  @Test
  public void getImp_returnsNonNull() {
    assertNotNull(op.getImp());
  }

  @Test
  public void getImp_swingModel_returnsNonNull() {
    assertNotNull(op.getImp().getSwingModel());
  }

  // ── initializeIfNecessary ─────────────────────────────────────────

  @Test
  public void initializeIfNecessary_callsLocalize() {
    assertFalse(op.localizeCalled);
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
    assertTrue(sb.toString().contains("TestActionOperation"));
  }

  // ── getMenuItemPrepModel ──────────────────────────────────────────

  @Test
  public void getMenuItemPrepModel_returnsNonNull() {
    assertNotNull(op.getMenuItemPrepModel());
  }

  // ── Multiple instances ────────────────────────────────────────────

  @Test
  public void twoInstances_differentIds() {
    TestActionOperation op2 = new TestActionOperation(TEST_GROUP, CroquetTestUtils.nextTestUUID());
    assertNotEquals(op.getMigrationId(), op2.getMigrationId());
  }

  @Test
  public void twoInstances_sameGroup() {
    TestActionOperation op2 = new TestActionOperation(TEST_GROUP, CroquetTestUtils.nextTestUUID());
    assertSame(op.getGroup(), op2.getGroup());
  }

  // ── Concrete test subclass ────────────────────────────────────────

  static class TestActionOperation extends ActionOperation {
    boolean localizeCalled = false;

    TestActionOperation(Group group, UUID id) {
      super(group, id);
    }

    @Override
    protected void localize() {
      localizeCalled = true;
    }

    @Override
    protected void perform(UserActivity activity) {
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
