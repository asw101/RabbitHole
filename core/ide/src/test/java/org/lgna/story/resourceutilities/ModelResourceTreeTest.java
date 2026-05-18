package org.lgna.story.resourceutilities;

import org.junit.Test;
import org.lgna.story.resources.ModelResource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class ModelResourceTreeTest {

  @Test
  public void construct_emptyList_createsEmptyTree() {
    List<Class<? extends ModelResource>> classes = Collections.emptyList();
    ModelResourceTree tree = new ModelResourceTree(classes);
    assertNotNull(tree.getTree());
  }

  @Test
  public void getTree_returnsNonNull() {
    List<Class<? extends ModelResource>> classes = new ArrayList<>();
    ModelResourceTree tree = new ModelResourceTree(classes);
    GalleryResourceTreeNode root = tree.getTree();
    assertNotNull(root);
  }

  @Test
  public void getDynamicNodes_emptyList_returnsEmpty() {
    List<Class<? extends ModelResource>> classes = Collections.emptyList();
    ModelResourceTree tree = new ModelResourceTree(classes);
    assertTrue(tree.getDynamicNodes().isEmpty());
  }

  @Test
  public void getNodeForResource_unknownResource_returnsNull() {
    List<Class<? extends ModelResource>> classes = Collections.emptyList();
    ModelResourceTree tree = new ModelResourceTree(classes);
    assertNull(tree.getNodeForResource("unknownKey"));
  }

  @Test
  public void addUserModels_emptyList_returnsEmptyList() {
    List<Class<? extends ModelResource>> classes = Collections.emptyList();
    ModelResourceTree tree = new ModelResourceTree(classes);
    List<ManifestDefinedGalleryTreeNode> result = tree.addUserModels(Collections.emptyList());
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void getGalleryResourceTreeNodeForJavaType_unknownType_returnsNull() {
    List<Class<? extends ModelResource>> classes = Collections.emptyList();
    ModelResourceTree tree = new ModelResourceTree(classes);
    org.lgna.project.ast.JavaType javaType = org.lgna.project.ast.JavaType.getInstance(Object.class);
    assertNull(tree.getGalleryResourceTreeNodeForJavaType(javaType));
  }
}
