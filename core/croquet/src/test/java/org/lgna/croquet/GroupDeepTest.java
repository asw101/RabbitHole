package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Deep tests for {@link Group} — getId, toString, instance caching,
 * description behavior, and edge cases.
 */
public class GroupDeepTest {

  // ── getId ─────────────────────────────────────────────────────────

  @Test
  public void getId_matchesInput() {
    UUID id = UUID.fromString("20000000-0000-0000-0000-000000000001");
    Group g = Group.getInstance(id);
    assertEquals(id, g.getId());
  }

  @Test
  public void getId_nonNull() {
    UUID id = UUID.fromString("20000000-0000-0000-0000-000000000002");
    Group g = Group.getInstance(id);
    assertNotNull(g.getId());
  }

  // ── toString ──────────────────────────────────────────────────────

  @Test
  public void toString_withoutDescription_returnsUnknown() {
    UUID id = UUID.fromString("20000000-0000-0000-0000-000000000003");
    Group g = Group.getInstance(id);
    assertEquals("Unknown Group", g.toString());
  }

  @Test
  public void toString_withDescription_returnsDescription() {
    UUID id = UUID.fromString("20000000-0000-0000-0000-000000000004");
    Group g = Group.getInstance(id, "My Deep Group");
    assertEquals("My Deep Group", g.toString());
  }

  @Test
  public void toString_nonNull() {
    UUID id = UUID.fromString("20000000-0000-0000-0000-000000000005");
    Group g = Group.getInstance(id);
    assertNotNull(g.toString());
  }

  @Test
  public void toString_nonEmpty() {
    UUID id = UUID.fromString("20000000-0000-0000-0000-000000000006");
    Group g = Group.getInstance(id);
    assertFalse(g.toString().isEmpty());
  }

  // ── Instance caching ──────────────────────────────────────────────

  @Test
  public void sameUUID_returnsSameInstance() {
    UUID id = UUID.fromString("20000000-0000-0000-0000-000000000007");
    Group a = Group.getInstance(id);
    Group b = Group.getInstance(id);
    assertSame(a, b);
  }

  @Test
  public void differentUUID_returnsDifferentInstance() {
    UUID id1 = UUID.fromString("20000000-0000-0000-0000-000000000008");
    UUID id2 = UUID.fromString("20000000-0000-0000-0000-000000000009");
    Group a = Group.getInstance(id1);
    Group b = Group.getInstance(id2);
    assertNotSame(a, b);
  }

  @Test
  public void getInstance_withDescription_thenWithout_sameInstance() {
    UUID id = UUID.fromString("20000000-0000-0000-0000-00000000000a");
    Group a = Group.getInstance(id, "Described");
    Group b = Group.getInstance(id);
    assertSame(a, b);
  }

  @Test
  public void getInstance_withDescription_thenWithout_keepDescription() {
    UUID id = UUID.fromString("20000000-0000-0000-0000-00000000000b");
    Group.getInstance(id, "KeepDesc");
    Group b = Group.getInstance(id);
    assertEquals("KeepDesc", b.toString());
  }

  // ── Many groups ───────────────────────────────────────────────────

  @Test
  public void manyGroups_allUnique() {
    Group[] groups = new Group[10];
    for (int i = 0; i < 10; i++) {
      UUID id = UUID.fromString(
          String.format("20000000-0000-0000-0000-0000000100%02d", i));
      groups[i] = Group.getInstance(id, "Group-" + i);
    }
    for (int i = 0; i < 10; i++) {
      for (int j = i + 1; j < 10; j++) {
        assertNotSame(groups[i], groups[j]);
      }
    }
  }

  @Test
  public void manyGroups_eachHasCorrectDescription() {
    for (int i = 0; i < 5; i++) {
      UUID id = UUID.fromString(
          String.format("20000000-0000-0000-0000-0000000200%02d", i));
      Group g = Group.getInstance(id, "Desc-" + i);
      assertEquals("Desc-" + i, g.toString());
    }
  }

  // ── Equality by identity ──────────────────────────────────────────

  @Test
  public void equals_sameInstance_true() {
    UUID id = UUID.fromString("20000000-0000-0000-0000-00000000000c");
    Group g = Group.getInstance(id);
    assertEquals(g, g);
  }

  @Test
  public void hashCode_consistent() {
    UUID id = UUID.fromString("20000000-0000-0000-0000-00000000000d");
    Group g = Group.getInstance(id);
    assertEquals(g.hashCode(), g.hashCode());
  }
}
