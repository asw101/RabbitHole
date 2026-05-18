package org.alice.ide.i18n;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link MethodInvocationChunk} — strips trailing "()" from method name.
 */
public class MethodInvocationChunkTest {

  @Test
  public void stripsTrailingParens() {
    MethodInvocationChunk mic = new MethodInvocationChunk("doWork()");
    assertEquals("doWork", mic.getMethodName());
  }

  @Test
  public void updateRepr_containsMethodName() {
    MethodInvocationChunk mic = new MethodInvocationChunk("test()");
    StringBuilder sb = new StringBuilder();
    mic.updateRepr(sb);
    assertTrue(sb.toString().contains("methodName=test"));
  }

}
