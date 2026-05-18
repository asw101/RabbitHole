package org.lgna.croquet;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for the {@link Group} class — UUID-keyed singleton registry for edit groups.
 * Covers getInstance, getId, toString, and singleton semantics.
 */
public class GroupExtendedContractTest {

  @Test
  public void getInstance_uuid_returnsNonNull() {
    UUID uuid = CroquetTestUtils.nextTestUUID();
    Group group = Group.getInstance(uuid);
    assertNotNull(group);
  }

  @Test
  public void getInstance_uuidAndName_returnsNonNull() {
    UUID uuid = CroquetTestUtils.nextTestUUID();
    Group group = Group.getInstance(uuid, "testGroup");
    assertNotNull(group);
  }

  @Test
  public void getInstance_sameUuid_returnsSameInstance() {
    UUID uuid = CroquetTestUtils.nextTestUUID();
    Group g1 = Group.getInstance(uuid, "sameTest");
    Group g2 = Group.getInstance(uuid);
    assertSame(g1, g2);
  }

  @Test
  public void getInstance_differentUuid_returnsDifferentInstance() {
    Group g1 = Group.getInstance(CroquetTestUtils.nextTestUUID(), "a");
    Group g2 = Group.getInstance(CroquetTestUtils.nextTestUUID(), "b");
    assertNotSame(g1, g2);
  }

  @Test
  public void getId_returnsConstructorUuid() {
    UUID uuid = CroquetTestUtils.nextTestUUID();
    Group group = Group.getInstance(uuid, "idTest");
    assertEquals(uuid, group.getId());
  }

  @Test
  public void toString_returnsNonNull() {
    Group group = Group.getInstance(CroquetTestUtils.nextTestUUID(), "strTest");
    assertNotNull(group.toString());
  }

  @Test
  public void toString_containsName() {
    Group group = Group.getInstance(CroquetTestUtils.nextTestUUID(), "myGroupName");
    assertTrue(group.toString().contains("myGroupName"));
  }

  @Test
  public void multipleGroups_independentIds() {
    UUID u1 = CroquetTestUtils.nextTestUUID();
    UUID u2 = CroquetTestUtils.nextTestUUID();
    Group g1 = Group.getInstance(u1, "first");
    Group g2 = Group.getInstance(u2, "second");
    assertNotEquals(g1.getId(), g2.getId());
  }
}
