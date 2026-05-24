package org.alice.ide.ast.type.merge.croquet;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class AddMembersPageLogicTest {
  @Test
  public void buildPageStatusTextConcatenatesNonEmptyFragments() {
    assertEquals("firstsecond", AddMembersPageLogic.buildPageStatusText(Arrays.asList("first", "", null, "second")));
  }
}
