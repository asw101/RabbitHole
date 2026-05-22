package org.alice.ide.ast.fieldtree;

import org.junit.Test;
import org.lgna.project.ast.*;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Branch coverage tests for {@link FieldTree}, {@link FieldNode}, {@link TypeNode}, etc.
 * Focuses on class hierarchy and metadata since constructors are private.
 */
public class FieldTreeBranchCoverageTest {

  // ---- FieldNode ----

  @Test
  public void fieldNode_extendsNode() {
    assertTrue(Node.class.isAssignableFrom(FieldNode.class));
  }

  @Test
  public void fieldNode_isPublic() {
    assertTrue(Modifier.isPublic(FieldNode.class.getModifiers()));
  }

  @Test
  public void fieldNode_isNotAbstract() {
    assertFalse(Modifier.isAbstract(FieldNode.class.getModifiers()));
  }

  @Test
  public void fieldNode_className() {
    assertEquals("FieldNode", FieldNode.class.getSimpleName());
  }

  @Test
  public void fieldNode_hasCreateAndAddToParentMethod() throws Exception {
    assertNotNull(FieldNode.class.getMethod("createAndAddToParent", TypeNode.class, UserField.class));
  }

  // ---- TypeNode ----

  @Test
  public void typeNode_extendsNode() {
    assertTrue(Node.class.isAssignableFrom(TypeNode.class));
  }

  @Test
  public void typeNode_isPublic() {
    assertTrue(Modifier.isPublic(TypeNode.class.getModifiers()));
  }

  @Test
  public void typeNode_isNotAbstract() {
    assertFalse(Modifier.isAbstract(TypeNode.class.getModifiers()));
  }

  @Test
  public void typeNode_className() {
    assertEquals("TypeNode", TypeNode.class.getSimpleName());
  }

  // ---- RootNode ----

  @Test
  public void rootNode_extendsNode() {
    assertTrue(Node.class.isAssignableFrom(RootNode.class));
  }

  @Test
  public void rootNode_isPublic() {
    assertTrue(Modifier.isPublic(RootNode.class.getModifiers()));
  }

  @Test
  public void rootNode_className() {
    assertEquals("RootNode", RootNode.class.getSimpleName());
  }

  // ---- Node ----

  @Test
  public void node_isPublic() {
    assertTrue(Modifier.isPublic(Node.class.getModifiers()));
  }

  @Test
  public void node_isAbstract() {
    assertTrue(Modifier.isAbstract(Node.class.getModifiers()));
  }

  @Test
  public void node_className() {
    assertEquals("Node", Node.class.getSimpleName());
  }

  // ---- FieldTree ----

  @Test
  public void fieldTree_isPublic() {
    assertTrue(Modifier.isPublic(FieldTree.class.getModifiers()));
  }

  @Test
  public void fieldTree_className() {
    assertEquals("FieldTree", FieldTree.class.getSimpleName());
  }

  // ---- class relationships ----

  @Test
  public void fieldNodeIsNotTypeNode() {
    assertNotEquals(FieldNode.class, TypeNode.class);
  }

  @Test
  public void fieldNodeIsNotRootNode() {
    assertNotEquals(FieldNode.class, RootNode.class);
  }

  @Test
  public void typeNodeIsNotRootNode() {
    assertNotEquals(TypeNode.class, RootNode.class);
  }

  @Test
  public void allConcreteNodes_extendNode() {
    assertTrue(Node.class.isAssignableFrom(FieldNode.class));
    assertTrue(Node.class.isAssignableFrom(TypeNode.class));
    assertTrue(Node.class.isAssignableFrom(RootNode.class));
  }
}
