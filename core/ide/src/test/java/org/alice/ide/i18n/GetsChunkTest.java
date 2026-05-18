package org.alice.ide.i18n;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link GetsChunk} — boolean isTowardLeading flag.
 */
public class GetsChunkTest {

  @Test
  public void towardLeadingTrue() {
    GetsChunk gc = new GetsChunk(true);
    assertTrue(gc.isTowardLeading());
  }

  @Test
  public void towardLeadingFalse() {
    GetsChunk gc = new GetsChunk(false);
    assertFalse(gc.isTowardLeading());
  }

  @Test
  public void updateRepr_containsFlag() {
    GetsChunk gc = new GetsChunk(true);
    StringBuilder sb = new StringBuilder();
    gc.updateRepr(sb);
    String repr = sb.toString();
    assertTrue(repr.contains("isTowardLeading=true"));
  }

}
