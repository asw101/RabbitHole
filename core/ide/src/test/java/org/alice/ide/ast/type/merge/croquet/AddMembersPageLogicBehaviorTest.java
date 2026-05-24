package org.alice.ide.ast.type.merge.croquet;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class AddMembersPageLogicBehaviorTest {
  @Test
  public void buildPageStatusTextConcatenatesOnlyNonEmptyFragments() {
    assertEquals("Loading 2 members",
        AddMembersPageLogic.buildPageStatusText(Arrays.asList("Load", "ing ", "", null, "2 members")));
  }

  @Test
  public void buildPageStatusTextReturnsEmptyStringWhenNothingContributesText() {
    assertEquals("", AddMembersPageLogic.buildPageStatusText(Arrays.asList("", null, "")));
  }
}
