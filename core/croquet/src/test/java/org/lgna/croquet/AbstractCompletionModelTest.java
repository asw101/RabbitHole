package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.edits.Edit;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for {@link AbstractCompletionModel} — group, sidekickLabel lifecycle,
 * hasSidekickLabel, appendRepr, and getMigrationId.
 * Uses a concrete test subclass to exercise the abstract base.
 */
public class AbstractCompletionModelTest {

  private static final UUID CM_ID =
      UUID.fromString("00000000-0000-0000-cccc-000000000001");
  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-cccc-000000000002"), "cmTest");

  private TestCompletionModel model;

  @Before
  public void setUp() {
    model = new TestCompletionModel(TEST_GROUP, CM_ID);
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_setsGroup() {
    assertSame(TEST_GROUP, model.getGroup());
  }

  @Test
  public void constructor_setsMigrationId() {
    assertEquals(CM_ID, model.getMigrationId());
  }

  @Test
  public void constructor_differentGroup() {
    Group other = Group.getInstance(UUID.fromString("00000000-0000-0000-cccc-000000000003"), "other");
    TestCompletionModel m2 = new TestCompletionModel(other, CroquetTestUtils.nextTestUUID());
    assertSame(other, m2.getGroup());
  }

  // ── SidekickLabel ─────────────────────────────────────────────────

  @Test
  public void hasSidekickLabel_initiallyFalse() {
    assertFalse(model.hasSidekickLabel());
  }

  @Test
  public void getSidekickLabel_returnsNonNull() {
    PlainStringValue label = model.getSidekickLabel();
    assertNotNull(label);
  }

  @Test
  public void hasSidekickLabel_trueAfterGet() {
    model.getSidekickLabel();
    assertTrue(model.hasSidekickLabel());
  }

  @Test
  public void getSidekickLabel_idempotent() {
    PlainStringValue first = model.getSidekickLabel();
    PlainStringValue second = model.getSidekickLabel();
    assertSame(first, second);
  }

  // ── appendRepr ────────────────────────────────────────────────────

  @Test
  public void appendRepr_containsGroupInfo() {
    StringBuilder sb = new StringBuilder();
    model.appendRepr(sb);
    String repr = sb.toString();
    assertTrue("Should contain 'group='", repr.contains("group="));
  }

  @Test
  public void appendRepr_containsClassName() {
    String repr = model.createRepr();
    assertTrue(repr.contains("TestCompletionModel"));
  }

  // ── isEnabled / setEnabled ────────────────────────────────────────

  @Test
  public void isEnabled_defaultTrue() {
    assertTrue(model.isEnabled());
  }

  @Test
  public void setEnabled_false() {
    model.setEnabled(false);
    assertFalse(model.isEnabled());
  }

  @Test
  public void setEnabled_toggleBackToTrue() {
    model.setEnabled(false);
    model.setEnabled(true);
    assertTrue(model.isEnabled());
  }

  // ── initializeIfNecessary ─────────────────────────────────────────

  @Test
  public void initializeIfNecessary_callsLocalize() {
    assertFalse(model.localizeCalled);
    model.initializeIfNecessary();
    assertTrue(model.localizeCalled);
  }

  @Test
  public void initializeIfNecessary_idempotent() {
    model.initializeIfNecessary();
    model.localizeCalled = false;
    model.initializeIfNecessary();
    assertFalse(model.localizeCalled);
  }

  // ── relocalize ────────────────────────────────────────────────────

  @Test
  public void relocalize_callsLocalize() {
    model.initializeIfNecessary();
    model.localizeCalled = false;
    model.relocalize();
    assertTrue(model.localizeCalled);
  }

  // ── Concrete test subclass ────────────────────────────────────────

  static class TestCompletionModel extends AbstractCompletionModel {
    boolean localizeCalled = false;

    TestCompletionModel(Group group, UUID id) {
      super(group, id);
    }

    @Override
    protected void localize() {
      localizeCalled = true;
    }

    @Override
    protected Class<? extends Element> getClassUsedForLocalization() {
      return TestCompletionModel.class;
    }

    @Override
    protected String getSubKeyForLocalization() {
      return "test";
    }

    @Override
    public List<List<PrepModel>> getPotentialPrepModelPaths(Edit edit) {
      return Collections.emptyList();
    }
  }
}
