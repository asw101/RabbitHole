package org.alice.stageide.modelresource;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TreeUtilitiesDelegationBehaviorTest {
  private static ResourceNode leaf(String internalName) {
    return ModelResourceTestSupport.node(
        internalName,
        internalName,
        new String[0],
        new String[0],
        new ModelResourceTestSupport.FakeSingleSourceFactory());
  }

  @Test
  public void addNodeChildSetsParentAndKeepsChildrenSorted() {
    ResourceNode parent = new ResourceNode(new RootResourceKey("parent", "parent"), new ArrayList<>());
    ResourceNode zebra = leaf("zebra");
    ResourceNode ant = leaf("ant");

    parent.addNodeChild(zebra);
    parent.addNodeChild(ant);

    assertEquals(List.of("ant", "zebra"), parent.getNodeChildren().stream().map(ResourceNode::getText).toList());
    assertSame(parent, ant.getParent());
    assertSame(parent, zebra.getParent());
  }

  @Test
  public void addNodeChildAtIndexHonorsExplicitInsertionPoint() {
    ResourceNode parent = new ResourceNode(new RootResourceKey("parent", "parent"), new ArrayList<>());
    ResourceNode zebra = leaf("zebra");
    ResourceNode ant = leaf("ant");

    parent.addNodeChild(zebra);
    parent.addNodeChild(0, ant);

    assertEquals(List.of("ant", "zebra"), parent.getNodeChildren().stream().map(ResourceNode::getText).toList());
    assertSame(parent, ant.getParent());
    assertSame(parent, zebra.getParent());
  }

  @Test
  public void nestedTagKeysExposeLeafSegmentsInNamesAndSearchText() {
    GroupTagKey groupKey = new GroupTagKey("animals:mammals", List.of(new ModelResourceTestSupport.FakeSingleSourceFactory()));
    ThemeTagKey themeKey = new ThemeTagKey("space:planets");

    assertEquals("mammals", groupKey.getInternalName());
    assertTrue(groupKey.getSearchText().contains("mammals"));
    assertEquals("planets", themeKey.getInternalName());
    assertTrue(themeKey.getSearchText().contains("planets"));
  }
}
