package org.alice.stageide.modelresource;

import edu.cmu.cs.dennisc.java.util.InitializingIfAbsentListHashMap;
import org.junit.Test;
import org.lgna.croquet.icon.AbstractSingleSourceImageIconFactory;
import org.lgna.croquet.icon.IconFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TreeUtilitiesLogicBehaviorTest {
  private static ResourceNode node(String name, String[] groupTags, String[] themeTags, IconFactory iconFactory, ResourceNode... children) {
    return ModelResourceTestSupport.node(name, name, groupTags, themeTags, iconFactory, children);
  }

  private static void assertConstructorThrowsAssertionError(Class<?> type) throws Exception {
    Constructor<?> constructor = type.getDeclaredConstructor();
    constructor.setAccessible(true);
    try {
      constructor.newInstance();
      fail("Expected constructor to reject instantiation");
    } catch (InvocationTargetException ite) {
      assertTrue(ite.getCause() instanceof AssertionError);
    }
  }

  @Test
  public void utilityConstructorRejectsInstantiation() throws Exception {
    assertConstructorThrowsAssertionError(TreeUtilitiesLogic.class);
  }

  @Test
  public void selectResourceNodesCollectsMatchesRecursively() {
    ResourceNode sedan = node("sedan", new String[0], new String[0], null);
    ResourceNode sports = node("sports", new String[0], new String[0], null);
    ResourceNode vehicles = node("vehicles", new String[0], new String[0], null, sedan, sports);

    List<ResourceNode> selected = new ArrayList<>();
    TreeUtilitiesLogic.selectResourceNodes(vehicles, selected,
        resourceNode -> resourceNode.getResourceKey().getInternalName().contains("s"));

    assertEquals(List.of(vehicles, sedan, sports), selected);
  }

  @Test
  public void buildMapIndexesTaggedChildrenButSkipsRootNodes() {
    ResourceNode taggedChild = node("car", new String[]{"*vehicles"}, new String[]{"transport"}, null);
    ResourceNode root = new ResourceNode(new RootResourceKey("all", "all"), new ArrayList<>(List.of(taggedChild)));
    InitializingIfAbsentListHashMap<String, ResourceNode> groups = new InitializingIfAbsentListHashMap<>();
    InitializingIfAbsentListHashMap<String, ResourceNode> themes = new InitializingIfAbsentListHashMap<>();

    TreeUtilitiesLogic.buildMap(groups, themes, root);

    assertEquals(List.of(taggedChild), groups.get("vehicles"));
    assertEquals(List.of(taggedChild), themes.get("transport"));
  }

  @Test
  public void copyReturnsEmptyListForNullInput() {
    assertTrue(TreeUtilitiesLogic.copy(null).isEmpty());
  }

  @Test
  public void createIconFactoriesFiltersToSingleSourceFactoriesAndStopsAtFive() {
    ModelResourceTestSupport.FakeSingleSourceFactory first = new ModelResourceTestSupport.FakeSingleSourceFactory();
    ModelResourceTestSupport.FakeSingleSourceFactory second = new ModelResourceTestSupport.FakeSingleSourceFactory();
    ModelResourceTestSupport.FakeSingleSourceFactory third = new ModelResourceTestSupport.FakeSingleSourceFactory();
    ModelResourceTestSupport.FakeSingleSourceFactory fourth = new ModelResourceTestSupport.FakeSingleSourceFactory();
    ModelResourceTestSupport.FakeSingleSourceFactory fifth = new ModelResourceTestSupport.FakeSingleSourceFactory();
    ModelResourceTestSupport.FakeSingleSourceFactory sixth = new ModelResourceTestSupport.FakeSingleSourceFactory();
    IconFactory nonSingleSource = new GroupTagKey("group:skip", List.of(new ModelResourceTestSupport.FakeSingleSourceFactory())).getIconFactory();

    List<AbstractSingleSourceImageIconFactory> factories = TreeUtilitiesLogic.createIconFactories(List.of(
        node("skip", new String[0], new String[0], nonSingleSource),
        node("first", new String[0], new String[0], first),
        node("second", new String[0], new String[0], second),
        node("third", new String[0], new String[0], third),
        node("fourth", new String[0], new String[0], fourth),
        node("fifth", new String[0], new String[0], fifth),
        node("sixth", new String[0], new String[0], sixth)));

    assertEquals(5, factories.size());
    assertSame(first, factories.get(0));
    assertSame(fifth, factories.get(4));
  }

  @Test
  public void createGroupAndThemeTagNodeListsUseAppropriateTagKeyTypes() {
    InitializingIfAbsentListHashMap<String, ResourceNode> groups = new InitializingIfAbsentListHashMap<>();
    InitializingIfAbsentListHashMap<String, ResourceNode> themes = new InitializingIfAbsentListHashMap<>();
    ResourceNode vehicle = node("vehicle", new String[0], new String[0], new ModelResourceTestSupport.FakeSingleSourceFactory());
    ResourceNode forest = node("forest", new String[0], new String[0], new ModelResourceTestSupport.FakeSingleSourceFactory());
    groups.getInitializingIfAbsentToLinkedList("vehicles").add(vehicle);
    themes.getInitializingIfAbsentToLinkedList("outdoor").add(forest);

    List<ResourceNode> groupNodes = TreeUtilitiesLogic.createGroupTagNodeList(groups);
    List<ResourceNode> themeNodes = TreeUtilitiesLogic.createThemeTagNodeList(themes);

    assertTrue(groupNodes.get(0).getResourceKey() instanceof GroupTagKey);
    assertTrue(themeNodes.get(0).getResourceKey() instanceof ThemeTagKey);
  }
}
