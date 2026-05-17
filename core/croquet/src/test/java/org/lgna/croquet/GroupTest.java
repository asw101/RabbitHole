package org.lgna.croquet;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for {@link Group} — singleton-per-UUID factory with description.
 */
public class GroupTest {

  // ── getInstance(UUID) ─────────────────────────────────────────────

  @Test
  public void getInstance_returnsNonNull() {
    UUID id = UUID.fromString("10000000-0000-0000-0000-000000000001");
    Group group = Group.getInstance(id);
    assertNotNull(group);
  }

  @Test
  public void getInstance_sameUUID_returnsSameInstance() {
    UUID id = UUID.fromString("10000000-0000-0000-0000-000000000002");
    Group a = Group.getInstance(id);
    Group b = Group.getInstance(id);
    assertSame(a, b);
  }

  @Test
  public void getInstance_differentUUID_returnsDifferentInstance() {
    UUID id1 = UUID.fromString("10000000-0000-0000-0000-000000000003");
    UUID id2 = UUID.fromString("10000000-0000-0000-0000-000000000004");
    Group a = Group.getInstance(id1);
    Group b = Group.getInstance(id2);
    assertNotSame(a, b);
  }

  // ── getId ─────────────────────────────────────────────────────────

  @Test
  public void getId_matchesConstructorArg() {
    UUID id = UUID.fromString("10000000-0000-0000-0000-000000000005");
    Group group = Group.getInstance(id);
    assertEquals(id, group.getId());
  }

  // ── toString ──────────────────────────────────────────────────────

  @Test
  public void toString_noDescription_returnsUnknownGroup() {
    UUID id = UUID.fromString("10000000-0000-0000-0000-000000000006");
    Group group = Group.getInstance(id);
    assertEquals("Unknown Group", group.toString());
  }

  @Test
  public void toString_withDescription_returnsDescription() {
    UUID id = UUID.fromString("10000000-0000-0000-0000-000000000007");
    Group group = Group.getInstance(id, "My Group");
    assertEquals("My Group", group.toString());
  }

  // ── getInstance(UUID, String) ─────────────────────────────────────

  @Test
  public void getInstance_withDescription_returnsGroup() {
    UUID id = UUID.fromString("10000000-0000-0000-0000-000000000008");
    Group group = Group.getInstance(id, "Test Description");
    assertNotNull(group);
    assertEquals("Test Description", group.toString());
  }

  @Test
  public void getInstance_withDescription_sameUUID_returnsSameInstance() {
    UUID id = UUID.fromString("10000000-0000-0000-0000-000000000009");
    Group a = Group.getInstance(id, "First");
    Group b = Group.getInstance(id);
    assertSame(a, b);
  }
}
