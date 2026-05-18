package org.alice.stageide.modelresource;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.croquet.SingleSelectListState;
import org.lgna.croquet.data.MutableListData;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class TreeUtilitiesTest {
  private void assumeNotHeadless() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
  }

  @Test
  public void getClassTreeState_notNull() {
    assumeNotHeadless();
    assertNotNull(TreeUtilities.getClassTreeState());
  }

  @Test
  public void getTreeBasedOnClassHierarchy_returnsCachedRoot() {
    assumeNotHeadless();
    ResourceNode tree = TreeUtilities.getTreeBasedOnClassHierarchy();
    assertNotNull(tree);
    assertSame(tree, TreeUtilities.getTreeBasedOnClassHierarchy());
    assertSame(tree, TreeUtilities.getClassTreeState().getRoot());
    assertEquals("all classes", tree.getResourceKey().getInternalName());
  }

  @Test
  public void getThemeTreeState_rootUsesAllThemesKey() {
    assumeNotHeadless();
    ResourceNode root = TreeUtilities.getThemeTreeState().getRoot();
    assertNotNull(root);
    assertEquals("all themes", root.getResourceKey().getInternalName());
  }

  @Test
  public void getGroupTreeState_rootUsesAllGroupsKey() {
    assumeNotHeadless();
    ResourceNode root = TreeUtilities.getGroupTreeState().getRoot();
    assertNotNull(root);
    assertEquals("all groups", root.getResourceKey().getInternalName());
  }

  @Test
  public void getUserTreeState_rootUsesMyGalleryKey() {
    assumeNotHeadless();
    ResourceNode root = TreeUtilities.getUserTreeState().getRoot();
    assertNotNull(root);
    assertEquals("My Gallery", root.getResourceKey().getInternalName());
  }

  @Test
  public void getSClassListState_exposesData() {
    assumeNotHeadless();
    SingleSelectListState<ResourceNode, MutableListData<ResourceNode>> state = TreeUtilities.getSClassListState();
    assertNotNull(state);
    assertNotNull(state.getData());
    assertTrue(state.getItemCount() >= 0);
  }
}
