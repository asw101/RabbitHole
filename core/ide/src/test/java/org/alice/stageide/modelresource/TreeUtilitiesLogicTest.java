package org.alice.stageide.modelresource;

import edu.cmu.cs.dennisc.java.util.InitializingIfAbsentListHashMap;
import edu.cmu.cs.dennisc.java.util.Maps;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TreeUtilitiesLogicTest {
  private static ResourceNode node(String keyText, String displayText, ResourceNode... children) {
    return new ResourceNode(new RootResourceKey(keyText, displayText), new ArrayList<>(List.of(children)));
  }

  @Test
  public void findByKeyReturnsNestedNode() {
    ResourceNode leaf = node("leaf", "leaf");
    ResourceNode root = node("root", "root", node("branch", "branch", leaf));

    ResourceNode found = TreeUtilitiesLogic.findByKey(root, leaf.getResourceKey());

    assertSame(leaf, found);
  }

  @Test
  public void addTagsStripsLeadingAsterisk() {
    InitializingIfAbsentListHashMap<String, ResourceNode> map = Maps.newInitializingIfAbsentListHashMap();
    ResourceNode node = node("leaf", "leaf");

    TreeUtilitiesLogic.addTags(map, new String[] {"*vehicles", "animals"}, node);

    assertEquals(List.of(node), map.get("vehicles"));
    assertEquals(List.of(node), map.get("animals"));
  }

  @Test
  public void copyCreatesIndependentSortedNodes() {
    ResourceNode zebra = node("zebra", "zebra");
    ResourceNode ant = node("ant", "ant", node("child", "child"));

    List<ResourceNode> copy = TreeUtilitiesLogic.copy(List.of(zebra, ant));

    assertEquals("ant", copy.get(0).getResourceKey().getInternalName());
    assertEquals("zebra", copy.get(1).getResourceKey().getInternalName());
    assertNotSame(ant, copy.get(0));
    assertNotSame(ant.getNodeChildren().get(0), copy.get(0).getNodeChildren().get(0));
  }

  @Test
  public void createTagNodeListMovesNestedTagBeneathParent() {
    InitializingIfAbsentListHashMap<String, ResourceNode> map = Maps.newInitializingIfAbsentListHashMap();
    ResourceNode sedan = node("sedan", "sedan");
    ResourceNode sportsCar = node("sports", "sports");
    map.getInitializingIfAbsentToLinkedList("vehicles").add(sedan);
    map.getInitializingIfAbsentToLinkedList("vehicles:cars").add(sportsCar);

    List<ResourceNode> tags = TreeUtilitiesLogic.createTagNodeList(map, false);

    ResourceNode vehicles = tags.stream()
        .filter(node -> "vehicles".equals(((TagKey) node.getResourceKey()).getTag()))
        .findFirst()
        .orElseThrow();
    assertEquals("vehicles", ((TagKey) vehicles.getResourceKey()).getTag());
    assertFalse(vehicles.getNodeChildren().isEmpty());
    ResourceNode cars = vehicles.getNodeChildren().get(0);
    assertEquals("vehicles:cars", ((TagKey) cars.getResourceKey()).getTag());
    assertEquals(1, cars.getNodeChildren().size());
    assertEquals("sports", cars.getNodeChildren().get(0).getResourceKey().getInternalName());
  }
}
