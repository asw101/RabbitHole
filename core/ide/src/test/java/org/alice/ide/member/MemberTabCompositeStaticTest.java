package org.alice.ide.member;

import org.junit.Test;

import static org.junit.Assert.*;

public class MemberTabCompositeStaticTest {

  @Test
  public void areToolPalettesInert_defaultTrue() {
    assertTrue(MemberTabComposite.ARE_TOOL_PALETTES_INERT);
  }

  @Test
  public void getExpandedAccountingForInert_whenInert_alwaysTrue() {
    boolean orig = MemberTabComposite.ARE_TOOL_PALETTES_INERT;
    try {
      MemberTabComposite.ARE_TOOL_PALETTES_INERT = true;
      assertTrue(MemberTabComposite.getExpandedAccountingForInert(false));
      assertTrue(MemberTabComposite.getExpandedAccountingForInert(true));
    } finally {
      MemberTabComposite.ARE_TOOL_PALETTES_INERT = orig;
    }
  }

  @Test
  public void getExpandedAccountingForInert_whenNotInert_passesThrough() {
    boolean orig = MemberTabComposite.ARE_TOOL_PALETTES_INERT;
    try {
      MemberTabComposite.ARE_TOOL_PALETTES_INERT = false;
      assertFalse(MemberTabComposite.getExpandedAccountingForInert(false));
      assertTrue(MemberTabComposite.getExpandedAccountingForInert(true));
    } finally {
      MemberTabComposite.ARE_TOOL_PALETTES_INERT = orig;
    }
  }

  @Test
  public void separator_initiallyNull() {
    assertNull(MemberTabComposite.SEPARATOR);
  }

  @Test
  public void isInclusionDesired_publicNonStaticMethod_true() {
    var method = MemberTestHelper.methodNamed(Object.class, "toString");
    if (method != null) {
      assertTrue(MemberTabComposite.isInclusionDesired(method));
    }
  }

  @Test
  public void isInclusionDesired_staticMethod_false() {
    var method = MemberTestHelper.methodNamed(Math.class, "abs");
    if (method != null) {
      assertFalse(MemberTabComposite.isInclusionDesired(method));
    }
  }
}
