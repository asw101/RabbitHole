package org.alice.stageide.modelresource;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.UUID;

import static org.junit.Assert.*;

public class ResourceNodeTreeStateStructureTest {
  private ResourceNode createRootNode() {
    return new ResourceNode(UUID.randomUUID(), new RootResourceKey("root", "Root"));
  }

  @Test
  public void rootStateUsesProvidedRootNode() throws Exception {
    ResourceNode root = createRootNode();
    ResourceNodeTreeState state = new ResourceNodeTreeState(root);
    Method method = ResourceNodeTreeState.class.getDeclaredMethod("getRoot");
    method.setAccessible(true);

    assertSame(root, method.invoke(state));
  }

  @Test
  public void rootNodeHasNoParentAndIsNotLeaf() {
    ResourceNode root = createRootNode();
    ResourceNodeTreeState state = new ResourceNodeTreeState(root);

    assertNull(state.getParent(root));
    assertFalse(state.isLeaf(root));
  }

  @Test
  public void childCountForEmptyRootIsZero() throws Exception {
    ResourceNode root = createRootNode();
    ResourceNodeTreeState state = new ResourceNodeTreeState(root);
    Method method = ResourceNodeTreeState.class.getDeclaredMethod("getChildCount", ResourceNode.class);
    method.setAccessible(true);

    assertEquals(0, method.invoke(state, root));
  }
}
