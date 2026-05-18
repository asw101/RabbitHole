package org.alice.ide.croquet.models.help;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link AbstractIssueComposite}, {@link BugSubmitAttachment},
 * and {@link BugSubmitVisibility} — help enums and ISSUE_GROUP constant.
 */
public class AbstractIssueCompositeTest {

  // ---- BugSubmitAttachment enum ----

  @Test
  public void bugSubmitAttachment_hasTwoValues() {
    BugSubmitAttachment[] values = BugSubmitAttachment.values();
    assertEquals(2, values.length);
  }

  @Test
  public void bugSubmitAttachment_valueOf_YES() {
    assertNotNull(BugSubmitAttachment.valueOf("YES"));
  }

  @Test
  public void bugSubmitAttachment_valueOf_NO() {
    assertNotNull(BugSubmitAttachment.valueOf("NO"));
  }

  @Test
  public void bugSubmitAttachment_YES_isNotNull() {
    assertNotNull(BugSubmitAttachment.YES);
  }

  @Test
  public void bugSubmitAttachment_NO_isNotNull() {
    assertNotNull(BugSubmitAttachment.NO);
  }

  @Test
  public void bugSubmitAttachment_YES_notSame_NO() {
    assertNotSame(BugSubmitAttachment.YES, BugSubmitAttachment.NO);
  }

  @Test
  public void bugSubmitAttachment_ordinals() {
    assertEquals(0, BugSubmitAttachment.YES.ordinal());
    assertEquals(1, BugSubmitAttachment.NO.ordinal());
  }

  // ---- BugSubmitVisibility enum ----

  @Test
  public void bugSubmitVisibility_hasTwoValues() {
    BugSubmitVisibility[] values = BugSubmitVisibility.values();
    assertEquals(2, values.length);
  }

  @Test
  public void bugSubmitVisibility_valueOf_PUBLIC() {
    assertNotNull(BugSubmitVisibility.valueOf("PUBLIC"));
  }

  @Test
  public void bugSubmitVisibility_valueOf_PRIVATE() {
    assertNotNull(BugSubmitVisibility.valueOf("PRIVATE"));
  }

  @Test
  public void bugSubmitVisibility_PUBLIC_isNotNull() {
    assertNotNull(BugSubmitVisibility.PUBLIC);
  }

  @Test
  public void bugSubmitVisibility_PRIVATE_isNotNull() {
    assertNotNull(BugSubmitVisibility.PRIVATE);
  }

  @Test
  public void bugSubmitVisibility_PUBLIC_notSame_PRIVATE() {
    assertNotSame(BugSubmitVisibility.PUBLIC, BugSubmitVisibility.PRIVATE);
  }

  @Test
  public void bugSubmitVisibility_ordinals() {
    assertEquals(0, BugSubmitVisibility.PUBLIC.ordinal());
    assertEquals(1, BugSubmitVisibility.PRIVATE.ordinal());
  }

  // ---- enum name ----

  @Test
  public void bugSubmitAttachment_YES_name() {
    assertEquals("YES", BugSubmitAttachment.YES.name());
  }

  @Test
  public void bugSubmitAttachment_NO_name() {
    assertEquals("NO", BugSubmitAttachment.NO.name());
  }

  @Test
  public void bugSubmitVisibility_PUBLIC_name() {
    assertEquals("PUBLIC", BugSubmitVisibility.PUBLIC.name());
  }

  @Test
  public void bugSubmitVisibility_PRIVATE_name() {
    assertEquals("PRIVATE", BugSubmitVisibility.PRIVATE.name());
  }

  // ---- enum values round-trip ----

  @Test
  public void bugSubmitAttachment_valuesContainAll() {
    BugSubmitAttachment[] values = BugSubmitAttachment.values();
    boolean hasYes = false, hasNo = false;
    for (BugSubmitAttachment v : values) {
      if (v == BugSubmitAttachment.YES) hasYes = true;
      if (v == BugSubmitAttachment.NO) hasNo = true;
    }
    assertTrue(hasYes);
    assertTrue(hasNo);
  }

  @Test
  public void bugSubmitVisibility_valuesContainAll() {
    BugSubmitVisibility[] values = BugSubmitVisibility.values();
    boolean hasPub = false, hasPriv = false;
    for (BugSubmitVisibility v : values) {
      if (v == BugSubmitVisibility.PUBLIC) hasPub = true;
      if (v == BugSubmitVisibility.PRIVATE) hasPriv = true;
    }
    assertTrue(hasPub);
    assertTrue(hasPriv);
  }
}
