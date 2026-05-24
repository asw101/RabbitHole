/*******************************************************************************
 * Copyright (c) 2006, 2015, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice", nor may
 *    "Alice" appear in their name, without prior written permission of
 *    Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgement: "This product includes software
 *    developed by Carnegie Mellon University"
 *
 * 5. The gallery of art assets and animations provided with this software is
 *    contributed by Electronic Arts Inc. and may be used for personal,
 *    non-commercial, and academic use only. Redistributions of any program
 *    source code that utilizes The Sims 2 Assets must also retain the copyright
 *    notice, list of conditions and the disclaimer contained in
 *    The Alice 3.0 Art Gallery License.
 *
 * DISCLAIMER:
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 * ANY AND ALL EXPRESS, STATUTORY OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, AND NON-INFRINGEMENT ARE DISCLAIMED. IN NO EVENT
 * SHALL THE AUTHORS, COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING FROM OR OTHERWISE RELATING TO
 * THE USE OF OR OTHER DEALINGS WITH THE SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package org.alice.stageide.modelresource;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import edu.cmu.cs.dennisc.java.util.InitializingIfAbsentListHashMap;
import edu.cmu.cs.dennisc.java.util.Lists;
import edu.cmu.cs.dennisc.java.util.Maps;
import edu.cmu.cs.dennisc.java.util.logging.Logger;
import org.alice.nonfree.NebulousIde;
import org.alice.stageide.perspectives.scenesetup.SetupScenePerspectiveComposite;
import org.lgna.croquet.Application;
import org.lgna.croquet.ItemCodec;
import org.lgna.croquet.SingleSelectListState;
import org.lgna.croquet.data.MutableListData;
import org.lgna.croquet.icon.AbstractSingleSourceImageIconFactory;
import org.lgna.story.resources.BipedResource;
import org.lgna.story.resourceutilities.GalleryResourceTreeNode;
import org.lgna.story.resourceutilities.ManifestDefinedGalleryTreeNode;
import org.lgna.story.resourceutilities.StorytellingResourcesTreeUtils;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author Dennis Cosgrove
 */
public class TreeUtilities {
  private TreeUtilities() {
    throw new AssertionError();
  }

  private static ResourceNodeTreeState classTreeState = new ResourceNodeTreeState(getTreeBasedOnClassHierarchy());
  private static ResourceNodeTreeState themeTreeState = new ResourceNodeTreeState(getTreeBasedOnTheme());
  private static ResourceNodeTreeState groupTreeState = new ResourceNodeTreeState(getTreeBasedOnGroup());
  private static ResourceNodeTreeState userTreeState = new ResourceNodeTreeState(getTreeBasedOnUserResources());
  private static SingleSelectListState<ResourceNode, MutableListData<ResourceNode>> sClassListState = new SingleSelectListState<>(Application.APPLICATION_UI_GROUP, UUID.fromString("920d77ef-8da3-474a-8a2b-8ee36817b29f"), -1, getListBasedOnTopClasses());

  public static ResourceNodeTreeState getClassTreeState() {
    return classTreeState;
  }

  public static ResourceNodeTreeState getThemeTreeState() {
    return themeTreeState;
  }

  public static ResourceNodeTreeState getGroupTreeState() {
    return groupTreeState;
  }

  public static ResourceNodeTreeState getUserTreeState() {
    return userTreeState;
  }

  public static SingleSelectListState<ResourceNode, MutableListData<ResourceNode>> getSClassListState() {
    return sClassListState;
  }

  private static ResourceNode treeBasedOnClassHierarchy;
  private static ResourceNode treeBasedOnTheme;
  private static ResourceNode treeBasedOnGroup;

  static class SClassCodec implements ItemCodec<ResourceNode> {
    @Override
    public Class<ResourceNode> getValueClass() {
      return ResourceNode.class;
    }

    @Override
    public void encodeValue(BinaryEncoder binaryEncoder, ResourceNode value) {
      throw new AssertionError();
    }

    @Override
    public ResourceNode decodeValue(BinaryDecoder binaryDecoder) {
      throw new AssertionError();
    }

    @Override
    public void appendRepresentation(StringBuilder sb, ResourceNode value) {
      sb.append(value != null ? value.getSimpleClassName() : null);
    }
  }

  private static ResourceNode createNode(GalleryResourceTreeNode source, ResourceKey key) {
    List<ResourceNode> childNodes = Lists.newLinkedList();
    if (key instanceof ClassResourceKey classResourceKey) {
      if (classResourceKey.getModelResourceCls().equals(BipedResource.class)) {
        List<ResourceNode> emptyList = Collections.emptyList();
        NebulousIde.nonfree.addBipedResourceResourceNodes(childNodes, emptyList);
      }
    }
    for (GalleryResourceTreeNode childSource : source.childrenList()) {
      ResourceKey childKey = childSource.getResourceKey();
      childNodes.add(createNode(childSource, childKey));
    }
    return new ResourceNode(key, childNodes, true);
  }

  private static ResourceNode createTreeBasedOnClassHierarchy() {
    GalleryResourceTreeNode root = StorytellingResourcesTreeUtils.INSTANCE.getGalleryTree();
    return createNode(root, new RootResourceKey("AllClasses", "all classes"));
  }

  public static ResourceNode getTreeBasedOnClassHierarchy() {
    if (treeBasedOnClassHierarchy == null) {
      treeBasedOnClassHierarchy = createTreeBasedOnClassHierarchy();
    }
    return treeBasedOnClassHierarchy;
  }

  public static void updateTreeBasedOnClassHierarchy() {
    if (treeBasedOnClassHierarchy == null) {
      treeBasedOnClassHierarchy = createTreeBasedOnClassHierarchy();
    } else {
      List<ManifestDefinedGalleryTreeNode> newNodes = StorytellingResourcesTreeUtils.INSTANCE.updateGalleryTree();
      for (GalleryResourceTreeNode node : newNodes) {
        ResourceKey childKey = node.getResourceKey();
        final ResourceNode newResourceNode = createNode(node, childKey);
        final ResourceNode parent = findByKey(treeBasedOnClassHierarchy, node.getParent().getResourceKey());
        if (parent != null) {
          parent.addNodeChild(newResourceNode);
        }
        userTreeState.getRoot().addNodeChild(newResourceNode);
        // TODO add to tags and groups
      }
      if (!newNodes.isEmpty()) {
        SetupScenePerspectiveComposite.getInstance().getGalleryComposite().modelUpdated();
      }
    }
  }

  private static ResourceNode findByKey(ResourceNode root, ResourceKey resourceKey) {
    return TreeUtilitiesLogic.findByKey(root, resourceKey);
  }

  private static MutableListData<ResourceNode> getListBasedOnTopClasses() {
    List<ResourceNode> classLeaves = Lists.newLinkedList();
    // TODO Extend import to cover poses and change this to use ResourceNode::isSubclassable
    selectResourceNodes(getTreeBasedOnClassHierarchy(), classLeaves, ResourceNode::isSubclassableAndDoesNotRequirePoses);
    return new MutableListData<>(new SClassCodec(), classLeaves);
  }

  private static ResourceNode getTreeBasedOnUserResources() {
    List<ResourceNode> userResourceNodes = new ArrayList<>();
    selectResourceNodes(getTreeBasedOnClassHierarchy(), userResourceNodes, ResourceNode::isUserDefinedModel);
    return new ResourceNode(new RootResourceKey("MyGallery", "My Gallery"), copy(userResourceNodes));
  }

  private static void selectResourceNodes(ResourceNode node, List<ResourceNode> selectedNodes, Predicate<ResourceNode> qualifier) {
    TreeUtilitiesLogic.selectResourceNodes(node, selectedNodes, qualifier);
  }

  private static void addTags(InitializingIfAbsentListHashMap<String, ResourceNode> map, String[] tags, ResourceNode node) {
    TreeUtilitiesLogic.addTags(map, tags, node);
  }

  private static void buildMap(InitializingIfAbsentListHashMap<String, ResourceNode> mapGroup, InitializingIfAbsentListHashMap<String, ResourceNode> mapTheme, ResourceNode node) {
    TreeUtilitiesLogic.buildMap(mapGroup, mapTheme, node);
  }

  private static List<ResourceNode> copy(List<ResourceNode> srcNodes) {
    return TreeUtilitiesLogic.copy(srcNodes);
  }

  private static List<AbstractSingleSourceImageIconFactory> createIconFactories(List<ResourceNode> dstChildNodes) {
    return TreeUtilitiesLogic.createIconFactories(dstChildNodes);
  }

  private static void addTagNode(String tag, List<ResourceNode> dstChildNodes, List<ResourceNode> tagNodes, Map<String, ResourceNode> mapTagToNode, boolean isTheme) {
    TreeUtilitiesLogic.addTagNode(tag, dstChildNodes, tagNodes, mapTagToNode, isTheme);
  }

  private static List<ResourceNode> createTagNodeList(InitializingIfAbsentListHashMap<String, ResourceNode> map, boolean isTheme, String... emptyTagNames) {
    return TreeUtilitiesLogic.createTagNodeList(map, isTheme, emptyTagNames);
  }

  private static List<ResourceNode> createGroupTagNodeList(InitializingIfAbsentListHashMap<String, ResourceNode> mapGroup) {
    return TreeUtilitiesLogic.createGroupTagNodeList(mapGroup);
  }

  private static List<ResourceNode> createThemeTagNodeList(InitializingIfAbsentListHashMap<String, ResourceNode> mapTheme) {
    return TreeUtilitiesLogic.createThemeTagNodeList(mapTheme);
  }

  private static void createTreesBasedOnThemeAndGroup() {
    ResourceNode rootClassHierarchy = getTreeBasedOnClassHierarchy();
    InitializingIfAbsentListHashMap<String, ResourceNode> mapGroup = Maps.newInitializingIfAbsentListHashMap();
    InitializingIfAbsentListHashMap<String, ResourceNode> mapTheme = Maps.newInitializingIfAbsentListHashMap();
    buildMap(mapGroup, mapTheme, rootClassHierarchy);

    List<ResourceNode> themeNodes = createThemeTagNodeList(mapTheme);
    List<ResourceNode> groupNodes = createGroupTagNodeList(mapGroup);

    Logger.outln(themeNodes);
    Logger.outln(groupNodes);

    treeBasedOnGroup = new ResourceNode(new RootResourceKey("AllGroups", "all groups"), groupNodes);
    treeBasedOnTheme = new ResourceNode(new RootResourceKey("AllThemes", "all themes"), themeNodes);
  }

  private static ResourceNode getTreeBasedOnTheme() {
    if (treeBasedOnTheme == null) {
      createTreesBasedOnThemeAndGroup();
    }
    return treeBasedOnTheme;
  }

  private static ResourceNode getTreeBasedOnGroup() {
    if (treeBasedOnGroup == null) {
      createTreesBasedOnThemeAndGroup();
    }
    return treeBasedOnGroup;
  }
}
