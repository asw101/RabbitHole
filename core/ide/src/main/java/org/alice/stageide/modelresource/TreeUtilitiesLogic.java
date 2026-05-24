package org.alice.stageide.modelresource;

import edu.cmu.cs.dennisc.java.util.InitializingIfAbsentListHashMap;
import edu.cmu.cs.dennisc.java.util.Lists;
import edu.cmu.cs.dennisc.java.util.Maps;
import edu.cmu.cs.dennisc.java.util.logging.Logger;
import org.lgna.croquet.icon.AbstractSingleSourceImageIconFactory;
import org.lgna.croquet.icon.IconFactory;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Predicate;

final class TreeUtilitiesLogic {
  private TreeUtilitiesLogic() {
    throw new AssertionError();
  }

  static ResourceNode findByKey(ResourceNode root, ResourceKey resourceKey) {
    if (resourceKey.equals(root.getResourceKey())) {
      return root;
    }
    for (ResourceNode child : root.getNodeChildren()) {
      ResourceNode checkChild = findByKey(child, resourceKey);
      if (checkChild != null) {
        return checkChild;
      }
    }
    return null;
  }

  static void selectResourceNodes(ResourceNode node, List<ResourceNode> selectedNodes, Predicate<ResourceNode> qualifier) {
    if (qualifier.test(node)) {
      selectedNodes.add(node);
    }
    for (ResourceNode child : node.getNodeChildren()) {
      selectResourceNodes(child, selectedNodes, qualifier);
    }
  }

  static void addTags(InitializingIfAbsentListHashMap<String, ResourceNode> map, String[] tags, ResourceNode node) {
    if (tags != null) {
      for (String tag : tags) {
        if (tag.startsWith("*")) {
          tag = tag.substring(1);
        }
        List<ResourceNode> list = map.getInitializingIfAbsentToLinkedList(tag);
        list.add(node);
      }
    }
  }

  static void buildMap(InitializingIfAbsentListHashMap<String, ResourceNode> mapGroup, InitializingIfAbsentListHashMap<String, ResourceNode> mapTheme, ResourceNode node) {
    ResourceKey resourceKey = node.getResourceKey();
    if (!(resourceKey instanceof EnumConstantResourceKey)) {
      if (!(resourceKey instanceof RootResourceKey)) {
        addTags(mapGroup, node.getResourceKey().getGroupTags(), node);
        addTags(mapTheme, node.getResourceKey().getThemeTags(), node);
      }
      for (ResourceNode child : node.getNodeChildren()) {
        buildMap(mapGroup, mapTheme, child);
      }
    }
  }

  static List<ResourceNode> copy(List<ResourceNode> srcNodes) {
    if (srcNodes != null) {
      List<ResourceNode> dstNodes = Lists.newLinkedList();
      for (ResourceNode srcNode : srcNodes) {
        List<ResourceNode> dstChildNodes = copy(srcNode.getNodeChildren());
        ResourceNode node = new ResourceNode(srcNode.getResourceKey(), dstChildNodes);
        dstNodes.add(node);
      }
      Collections.sort(dstNodes);
      return dstNodes;
    } else {
      return Collections.emptyList();
    }
  }

  static List<AbstractSingleSourceImageIconFactory> createIconFactories(List<ResourceNode> dstChildNodes) {
    List<AbstractSingleSourceImageIconFactory> iconFactories = Lists.newLinkedList();
    for (ResourceNode resourceNode : dstChildNodes) {
      ResourceKey resourceKey = resourceNode.getResourceKey();
      IconFactory iconFactory = resourceKey.getIconFactory();
      if (iconFactory instanceof AbstractSingleSourceImageIconFactory imageIconFactory) {
        iconFactories.add(imageIconFactory);
      }
      if (iconFactories.size() == 5) {
        break;
      }
    }
    return iconFactories;
  }

  static void addTagNode(String tag, List<ResourceNode> dstChildNodes, List<ResourceNode> tagNodes, Map<String, ResourceNode> mapTagToNode, boolean isTheme) {
    TagKey tagKey;
    if (isTheme && tag.indexOf(TagKey.SEPARATOR) == -1) {
      tagKey = new ThemeTagKey(tag);
    } else {
      tagKey = new GroupTagKey(tag, createIconFactories(dstChildNodes));
    }
    ResourceNode dstNode = new ResourceNode(tagKey, dstChildNodes);
    tagNodes.add(dstNode);
    mapTagToNode.put(tag, dstNode);
  }

  static List<ResourceNode> createTagNodeList(InitializingIfAbsentListHashMap<String, ResourceNode> map, boolean isTheme, String... emptyTagNames) {
    Map<String, ResourceNode> mapInternal = Maps.newHashMap();

    List<ResourceNode> rv = Lists.newLinkedList();

    List<String> emptyTags = Lists.newArrayList(emptyTagNames);
    for (String emptyGroupTag : emptyTags) {
      List<ResourceNode> childNodes = Lists.newLinkedList();
      addTagNode(emptyGroupTag, childNodes, rv, mapInternal, isTheme);
    }

    for (String tag : map.keySet()) {
      if (emptyTags.contains(tag)) {
        Logger.severe(tag);
      } else {
        List<ResourceNode> srcChildNodes = map.get(tag);
        addTagNode(tag, copy(srcChildNodes), rv, mapInternal, isTheme);
      }
    }
    ListIterator<ResourceNode> listIterator = rv.listIterator();
    while (listIterator.hasNext()) {
      ResourceNode resourceNode = listIterator.next();
      TagKey tagKey = (TagKey) resourceNode.getResourceKey();
      String tag = tagKey.getTag();
      assert !tag.startsWith("*") : tag;
      int lastIndex = tag.lastIndexOf(TagKey.SEPARATOR);
      if (lastIndex != -1) {
        String parentTag = tag.substring(0, lastIndex);
        ResourceNode parentToBeNode = mapInternal.get(parentTag);
        if (parentToBeNode != null) {
          listIterator.remove();
          parentToBeNode.addNodeChild(0, resourceNode);
        } else {
          Logger.severe(tagKey);
        }
      }
    }
    Collections.sort(rv);

    return rv;
  }

  static List<ResourceNode> createGroupTagNodeList(InitializingIfAbsentListHashMap<String, ResourceNode> mapGroup) {
    return createTagNodeList(mapGroup, false, "household");
  }

  static List<ResourceNode> createThemeTagNodeList(InitializingIfAbsentListHashMap<String, ResourceNode> mapTheme) {
    return createTagNodeList(mapTheme, true, "household");
  }
}
