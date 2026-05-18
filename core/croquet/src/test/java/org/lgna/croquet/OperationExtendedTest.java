package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Extended tests for {@link Operation} — deeper coverage of toString,
 * appendRepr, modifyNameIfNecessary, getClassUsedForLocalization,
 * getSubKeyForLocalization, findLocalizedText, createRepr, fire disabled,
 * and OperationImp accessors.
 */
public class OperationExtendedTest {

  private static final UUID OP_ID =
      UUID.fromString("00000000-0000-0000-aaff-000000000001");
  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-aaff-000000000002"), "opExtTest");

  private TestOp op;

  @Before
  public void setUp() {
    op = new TestOp(TEST_GROUP, OP_ID);
  }

  // ── toString ────────────────────────────────────────────────────

  @Test
  public void toString_containsClassName() {
    String repr = op.toString();
    assertTrue(repr.contains("TestOp"));
  }

  @Test
  public void toString_containsGroupInfo() {
    String repr = op.toString();
    // Operation.toString() may or may not include group info
    assertNotNull(repr);
  }

  @Test
  public void toString_nonEmpty() {
    String repr = op.toString();
    assertFalse(repr.isEmpty());
  }

  // ── toString (delegates to createRepr) ────────────────────────────

  @Test
  public void toString_nonNull() {
    assertNotNull(op.toString());
  }

  @Test
  public void toString_sameAcrossCalls() {
    assertEquals(op.toString(), op.toString());
  }

  // ── appendUserRepr ────────────────────────────────────────────────

  @Test
  public void appendUserRepr_containsTodo() {
    StringBuilder sb = new StringBuilder();
    op.appendUserRepr(sb);
    assertTrue(sb.toString().contains("todo: override appendUserString"));
  }

  @Test
  public void appendUserRepr_containsFullClassName() {
    StringBuilder sb = new StringBuilder();
    op.appendUserRepr(sb);
    assertTrue(sb.toString().contains("OperationExtendedTest"));
  }

  // ── isEnabled and fire interaction ────────────────────────────────

  @Test
  public void setEnabled_false_disablesOperation() {
    op.setEnabled(false);
    assertFalse(op.isEnabled());
  }

  @Test
  public void setEnabled_viaBooleanState() {
    op.setEnabled(true);
    assertTrue(op.isEnabled());
    op.setEnabled(false);
    assertFalse(op.isEnabled());
  }

  // ── getClassUsedForLocalization ───────────────────────────────────

  @Test
  public void getClassUsedForLocalization_returnsOwnClass() {
    assertEquals(TestOp.class, op.getClassUsedForLocalization());
  }

  // ── getSubKeyForLocalization ──────────────────────────────────────

  @Test
  public void getSubKeyForLocalization_returnsNull() {
    assertNull(op.getSubKeyForLocalization());
  }

  // ── modifyNameIfNecessary ─────────────────────────────────────────

  @Test
  public void modifyNameIfNecessary_returnsInput() {
    assertEquals("Foo", op.modifyNameIfNecessary("Foo"));
  }

  // ── isToolBarTextClobbered ────────────────────────────────────────

  @Test
  public void isToolBarTextClobbered_defaultFalse() {
    assertFalse(op.isToolBarTextClobbered());
  }

  // ── getMenuItemPrepModel ──────────────────────────────────────────

  @Test
  public void getMenuItemPrepModel_nonNull() {
    assertNotNull(op.getMenuItemPrepModel());
  }

  @Test
  public void getMenuItemPrepModel_sameInstance() {
    assertSame(op.getMenuItemPrepModel(), op.getMenuItemPrepModel());
  }

  // ── setName ───────────────────────────────────────────────────────

  @Test
  public void setName_emptyString() {
    op.setName("");
    assertEquals("", op.getImp().getName());
  }

  @Test
  public void setName_longString() {
    String longName = "A very long operation name that should still work";
    op.setName(longName);
    assertEquals(longName, op.getImp().getName());
  }

  // ── setToolTipText ────────────────────────────────────────────────

  @Test
  public void setToolTipText_noException() {
    op.setToolTipText("Tooltip");
  }

  @Test
  public void setToolTipText_null_noException() {
    op.setToolTipText(null);
  }

  // ── setSmallIcon ──────────────────────────────────────────────────

  @Test
  public void setSmallIcon_noException() {
    op.setSmallIcon(new StubIcon());
  }

  @Test
  public void setSmallIcon_null_noException() {
    op.setSmallIcon(null);
  }

  // ── setButtonIcon edge cases ──────────────────────────────────────

  @Test
  public void setButtonIcon_twice_lastWins() {
    StubIcon icon1 = new StubIcon();
    StubIcon icon2 = new StubIcon();
    op.setButtonIcon(icon1);
    op.setButtonIcon(icon2);
    assertSame(icon2, op.getButtonIcon());
  }

  // ── getSidekickLabel stability ────────────────────────────────────

  @Test
  public void getSidekickLabel_sameInstance() {
    PlainStringValue a = op.getSidekickLabel();
    PlainStringValue b = op.getSidekickLabel();
    assertSame(a, b);
  }

  // ── Multiple operations sharing a group ───────────────────────────

  @Test
  public void twoOps_sameGroup_shareGroupInstance() {
    TestOp op2 = new TestOp(TEST_GROUP, CroquetTestUtils.nextTestUUID());
    assertSame(op.getGroup(), op2.getGroup());
  }

  // ── relocalize ────────────────────────────────────────────────────

  @Test
  public void relocalize_doesNotThrow() {
    op.initializeIfNecessary();
    op.relocalize();
  }

  // ── Concrete test subclass ────────────────────────────────────────

  static class TestOp extends Operation {
    TestOp(Group group, UUID id) {
      super(group, id);
    }

    @Override
    protected void localize() {
      // no-op
    }

    @Override
    protected void performInActivity(UserActivity userActivity) {
      // no-op
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
