package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for {@link SingleThreadIteratingOperation} — construction, group,
 * enable/disable, and structural accessors.
 */
public class SingleThreadIteratingOperationTest {

  private static final UUID OP_ID =
      UUID.fromString("00000000-0000-0000-ffff-000000000001");
  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-ffff-000000000002"), "stioTest");

  private TestSTIO op;

  @Before
  public void setUp() {
    op = new TestSTIO(TEST_GROUP, OP_ID);
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

  // ── Sidekick Label ────────────────────────────────────────────────

  @Test
  public void hasSidekickLabel_initiallyFalse() {
    assertFalse(op.hasSidekickLabel());
  }

  @Test
  public void getSidekickLabel_returnsNonNull() {
    assertNotNull(op.getSidekickLabel());
  }

  // ── getImp ────────────────────────────────────────────────────────

  @Test
  public void getImp_returnsNonNull() {
    assertNotNull(op.getImp());
  }

  // ── setName ───────────────────────────────────────────────────────

  @Test
  public void setName_updatesImpName() {
    op.setName("STIO Test");
    assertEquals("STIO Test", op.getImp().getName());
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
    assertTrue(sb.toString().contains("TestSTIO"));
  }

  // ── getMenuItemPrepModel ──────────────────────────────────────────

  @Test
  public void getMenuItemPrepModel_returnsNonNull() {
    assertNotNull(op.getMenuItemPrepModel());
  }

  // ── Concrete test subclass ────────────────────────────────────────

  static class TestSTIO extends SingleThreadIteratingOperation {
    boolean localizeCalled = false;

    TestSTIO(Group group, UUID id) {
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
  }
}
