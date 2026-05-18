package org.lgna.croquet;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for {@link Group} — getInstance singleton behavior, getId,
 * toString with/without description, and identity semantics.
 */
public class GroupExtendedTest {

  // ── getInstance: singleton ────────────────────────────────────────

  @Test
  public void getInstance_sameId_returnsSameInstance() {
    UUID id = UUID.fromString("00000000-0000-0000-eeee-111111111111");
    Group a = Group.getInstance(id);
    Group b = Group.getInstance(id);
    assertSame(a, b);
  }

  @Test
  public void getInstance_differentId_returnsDifferentInstance() {
    UUID id1 = UUID.fromString("00000000-0000-0000-eeee-222222222221");
    UUID id2 = UUID.fromString("00000000-0000-0000-eeee-222222222222");
    Group a = Group.getInstance(id1);
    Group b = Group.getInstance(id2);
    assertNotSame(a, b);
  }

  // ── getId ─────────────────────────────────────────────────────────

  @Test
  public void getId_matchesConstructor() {
    UUID id = UUID.fromString("00000000-0000-0000-eeee-333333333333");
    Group g = Group.getInstance(id);
    assertEquals(id, g.getId());
  }

  // ── getInstance with description ──────────────────────────────────

  @Test
  public void getInstanceWithDescription_setsDescription() {
    UUID id = UUID.fromString("00000000-0000-0000-eeee-444444444444");
    Group g = Group.getInstance(id, "MyGroup");
    assertEquals("MyGroup", g.toString());
  }

  // ── toString ──────────────────────────────────────────────────────

  @Test
  public void toString_withoutDescription_returnsUnknown() {
    UUID id = UUID.fromString("00000000-0000-0000-eeee-555555555555");
    Group g = Group.getInstance(id);
    assertEquals("Unknown Group", g.toString());
  }

  @Test
  public void toString_withDescription_returnsDescription() {
    UUID id = UUID.fromString("00000000-0000-0000-eeee-666666666666");
    Group g = Group.getInstance(id, "TestGroup");
    assertEquals("TestGroup", g.toString());
  }

  // ── Identity ──────────────────────────────────────────────────────

  @Test
  public void group_equalsItself() {
    UUID id = UUID.fromString("00000000-0000-0000-eeee-777777777777");
    Group g = Group.getInstance(id);
    assertEquals(g, g);
  }

  @Test
  public void sameUUID_sameGroup() {
    UUID id = UUID.fromString("00000000-0000-0000-eeee-888888888888");
    Group g1 = Group.getInstance(id);
    Group g2 = Group.getInstance(id);
    assertSame(g1, g2);
  }

  @Test
  public void differentUUID_notSameGroup() {
    UUID id1 = UUID.fromString("00000000-0000-0000-eeee-999999999991");
    UUID id2 = UUID.fromString("00000000-0000-0000-eeee-999999999992");
    Group g1 = Group.getInstance(id1);
    Group g2 = Group.getInstance(id2);
    assertNotSame(g1, g2);
  }

  // ── Multiple groups ───────────────────────────────────────────────

  @Test
  public void manyGroups_allUnique() {
    Group[] groups = new Group[10];
    for (int i = 0; i < 10; i++) {
      UUID id = new UUID(0xEEEEL, 0xA000000000L + i);
      groups[i] = Group.getInstance(id);
    }
    for (int i = 0; i < 10; i++) {
      for (int j = i + 1; j < 10; j++) {
        assertNotSame(groups[i], groups[j]);
      }
    }
  }

  @Test
  public void manyGroups_getInstanceReturnsCorrect() {
    for (int i = 0; i < 10; i++) {
      UUID id = new UUID(0xEEEEL, 0xB000000000L + i);
      Group g = Group.getInstance(id);
      assertSame(g, Group.getInstance(id));
    }
  }
}
