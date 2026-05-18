package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import javax.swing.Icon;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for {@link Operation} — construction, accessors, repr, enable/disable,
 * button icon, sidekick label, and initializeIfNecessary lifecycle.
 * Uses a concrete test subclass to avoid full Application dependencies.
 */
public class OperationTest {

  private static final UUID OP_ID =
      UUID.fromString("00000000-0000-0000-bbbb-000000000001");
  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-bbbb-000000000002"), "opTest");

  private TestOperation operation;

  @Before
  public void setUp() {
    operation = new TestOperation(TEST_GROUP, OP_ID);
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_setsMigrationId() {
    assertEquals(OP_ID, operation.getMigrationId());
  }

  @Test
  public void constructor_setsGroup() {
    assertSame(TEST_GROUP, operation.getGroup());
  }

  @Test
  public void constructor_enabledByDefault() {
    assertTrue(operation.isEnabled());
  }

  // ── Enable/Disable ────────────────────────────────────────────────

  @Test
  public void setEnabled_false() {
    operation.setEnabled(false);
    assertFalse(operation.isEnabled());
  }

  @Test
  public void setEnabled_true_afterDisable() {
    operation.setEnabled(false);
    operation.setEnabled(true);
    assertTrue(operation.isEnabled());
  }

  // ── Button Icon ───────────────────────────────────────────────────

  @Test
  public void getButtonIcon_initiallyNull() {
    assertNull(operation.getButtonIcon());
  }

  @Test
  public void setButtonIcon_storesIcon() {
    Icon icon = new StubIcon();
    operation.setButtonIcon(icon);
    assertSame(icon, operation.getButtonIcon());
  }

  @Test
  public void setButtonIcon_null_clearsIcon() {
    operation.setButtonIcon(new StubIcon());
    operation.setButtonIcon(null);
    assertNull(operation.getButtonIcon());
  }

  // ── isToolBarTextClobbered ────────────────────────────────────────

  @Test
  public void isToolBarTextClobbered_defaultFalse() {
    assertFalse(operation.isToolBarTextClobbered());
  }

  // ── Sidekick Label ────────────────────────────────────────────────

  @Test
  public void hasSidekickLabel_initiallyFalse() {
    assertFalse(operation.hasSidekickLabel());
  }

  @Test
  public void getSidekickLabel_returnsNonNull() {
    PlainStringValue label = operation.getSidekickLabel();
    assertNotNull(label);
  }

  @Test
  public void hasSidekickLabel_trueAfterGet() {
    operation.getSidekickLabel();
    assertTrue(operation.hasSidekickLabel());
  }

  // ── setName / setToolTipText / setSmallIcon ───────────────────────

  @Test
  public void setName_updatesImpName() {
    operation.setName("Test Op");
    assertEquals("Test Op", operation.getImp().getName());
  }

  @Test
  public void setToolTipText_noException() {
    operation.setToolTipText("A tooltip");
  }

  @Test
  public void setSmallIcon_noException() {
    operation.setSmallIcon(new StubIcon());
  }

  // ── getImp ────────────────────────────────────────────────────────

  @Test
  public void getImp_returnsNonNull() {
    assertNotNull(operation.getImp());
  }

  @Test
  public void getImp_swingModel_returnsNonNull() {
    assertNotNull(operation.getImp().getSwingModel());
  }

  // ── initializeIfNecessary ─────────────────────────────────────────

  @Test
  public void initializeIfNecessary_callsLocalize() {
    assertFalse(operation.localizeCalled);
    operation.initializeIfNecessary();
    assertTrue(operation.localizeCalled);
  }

  @Test
  public void initializeIfNecessary_idempotent() {
    operation.initializeIfNecessary();
    operation.localizeCalled = false;
    operation.initializeIfNecessary();
    assertFalse(operation.localizeCalled);
  }

  // ── appendRepr ────────────────────────────────────────────────────

  @Test
  public void appendUserRepr_containsTodo() {
    StringBuilder sb = new StringBuilder();
    operation.appendUserRepr(sb);
    assertTrue(sb.toString().contains("todo: override appendUserString"));
  }

  @Test
  public void appendUserRepr_containsClassName() {
    StringBuilder sb = new StringBuilder();
    operation.appendUserRepr(sb);
    assertTrue(sb.toString().contains("TestOperation"));
  }

  // ── getMenuItemPrepModel ──────────────────────────────────────────

  @Test
  public void getMenuItemPrepModel_returnsNonNull() {
    assertNotNull(operation.getMenuItemPrepModel());
  }

  // ── Concrete test operation ───────────────────────────────────────

  static class TestOperation extends Operation {
    boolean localizeCalled = false;

    TestOperation(Group group, UUID id) {
      super(group, id);
    }

    @Override
    protected void localize() {
      localizeCalled = true;
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
