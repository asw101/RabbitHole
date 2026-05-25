package org.alice.stageide.modelresource;

import edu.cmu.cs.dennisc.java.util.InitializingIfAbsentListHashMap;
import edu.cmu.cs.dennisc.java.util.Maps;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TreeUtilitiesDeepBehaviorTest {
  private static ResourceNode node(String keyText, String displayText, ResourceNode... children) {
    return new ResourceNode(new RootResourceKey(keyText, displayText), new ArrayList<>(List.of(children)));
  }

  @Test
  public void selectResourceNodesTraversesDepthFirst() {
    ResourceNode beta = node("beta", "beta");
    ResourceNode root = node("root", "root", node("alpha", "alpha"), node("branch", "branch", beta));
    List<ResourceNode> selected = new ArrayList<>();

    TreeUtilitiesLogic.selectResourceNodes(root, selected, candidate -> candidate.getResourceKey().getInternalName().startsWith("b"));

    assertEquals(List.of("branch", "beta"), selected.stream().map(node -> node.getResourceKey().getInternalName()).toList());
  }

  @Test
  public void createThemeTagNodeListUsesThemeTagKeysForSimpleTags() {
    InitializingIfAbsentListHashMap<String, ResourceNode> map = Maps.newInitializingIfAbsentListHashMap();
    map.getInitializingIfAbsentToLinkedList("forest").add(node("tree", "tree"));

    List<ResourceNode> tags = TreeUtilitiesLogic.createThemeTagNodeList(map);

    assertEquals(2, tags.size());
    assertTrue(tags.stream().allMatch(tag -> tag.getResourceKey() instanceof ThemeTagKey));
    assertTrue(tags.stream().anyMatch(tag -> "forest".equals(((TagKey) tag.getResourceKey()).getTag())));
  }
}
