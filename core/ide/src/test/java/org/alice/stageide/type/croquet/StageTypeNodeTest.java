package org.alice.stageide.type.croquet;

import org.junit.Test;
import org.lgna.project.ast.JavaType;

import static org.junit.Assert.*;

public class StageTypeNodeTest {
  @Test
  public void constructorStoresType() {
    TypeNode node = new TypeNode(JavaType.getInstance(String.class));

    assertSame(JavaType.getInstance(String.class), node.getType());
  }

  @Test
  public void nodeCanAcceptChildren() {
    TypeNode parent = new TypeNode(JavaType.getInstance(Object.class));
    TypeNode child = new TypeNode(JavaType.getInstance(String.class));
    parent.add(child);

    assertEquals(1, parent.getChildCount());
    assertSame(child, parent.getChildAt(0));
  }

  @Test
  public void childParentRelationshipUsesTreeNodeSupport() {
    TypeNode parent = new TypeNode(JavaType.getInstance(Number.class));
    TypeNode child = new TypeNode(JavaType.getInstance(Integer.class));
    parent.add(child);

    assertSame(parent, child.getParent());
  }
}
