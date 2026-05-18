package org.alice.ide.member;

import org.junit.Test;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;

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
    JavaType objectType = JavaType.getInstance(Object.class);
    for (var m : objectType.getDeclaredMethods()) {
      if ("toString".equals(m.getName())) {
        assertTrue(MemberTabComposite.isInclusionDesired(m));
        return;
      }
    }
  }

  @Test
  public void isInclusionDesired_staticMethod_false() {
    JavaType mathType = JavaType.getInstance(Math.class);
    for (var m : mathType.getDeclaredMethods()) {
      if ("abs".equals(m.getName())) {
        assertFalse(MemberTabComposite.isInclusionDesired(m));
        return;
      }
    }
  }
}
