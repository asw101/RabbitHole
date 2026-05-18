package org.alice.ide.ast.fieldtree;

import org.junit.Test;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;

import static org.junit.Assert.*;

public class TypeNodeTest {

  @Test
  public void createAndAddToParentCreatesTypeNodeAndAddsItToParent() {
    RootNode root = new RootNode();

    TypeNode child = TypeNode.createAndAddToParent(root, createUserType("Child"), 8, 4);

    assertSame(root, child.getParent());
    assertTrue(root.getTypeNodes().contains(child));
  }

  @Test
  public void getTypeNodesIsInitiallyEmpty() {
    TypeNode node = new TypeNode(null, createUserType("Solo"), 6, 3);

    assertTrue(node.getTypeNodes().isEmpty());
  }

  @Test
  public void getFieldNodesIsInitiallyEmpty() {
    TypeNode node = new TypeNode(null, createUserType("Solo"), 6, 3);

    assertTrue(node.getFieldNodes().isEmpty());
  }

  @Test
  public void getCollapseThresholdReturnsConfiguredValue() {
    TypeNode node = new TypeNode(null, createUserType("ThresholdType"), 6, 3);

    assertEquals(6, node.getCollapseThreshold());
  }

  @Test
  public void getCollapseThresholdForDescendantsReturnsConfiguredValue() {
    TypeNode node = new TypeNode(null, createUserType("ThresholdType"), 6, 3);

    assertEquals(3, node.getCollapseThresholdForDescendants());
  }

  @Test
  public void collapseIfAppropriateMovesFieldsToParentWhenCountIsBelowThreshold() {
    RootNode root = new RootNode();
    TypeNode child = TypeNode.createAndAddToParent(root, createUserType("Child"), 3, 3);
    FieldNode.createAndAddToParent(child, createField("alpha"));
    FieldNode.createAndAddToParent(child, createField("beta"));

    root.collapseIfAppropriate();

    assertTrue(child.getFieldNodes().isEmpty());
    assertEquals(2, root.getFieldNodes().size());
  }

  @Test
  public void removeEmptyTypeNodesRemovesNodesWithoutFields() {
    RootNode root = new RootNode();
    TypeNode child = TypeNode.createAndAddToParent(root, createUserType("EmptyChild"), 3, 3);

    root.removeEmptyTypeNodes();

    assertFalse(root.getTypeNodes().contains(child));
    assertTrue(root.getTypeNodes().isEmpty());
  }

  @Test
  public void sortOrdersTypeNodesAndFieldNodesAlphabetically() {
    RootNode root = new RootNode();
    TypeNode.createAndAddToParent(root, createUserType("Beta"), 5, 5);
    TypeNode.createAndAddToParent(root, createUserType("Alpha"), 5, 5);
    FieldNode.createAndAddToParent(root, createField("gamma"));
    FieldNode.createAndAddToParent(root, createField("beta"));

    root.sort();

    assertEquals("Alpha", root.getTypeNodes().get(0).getDeclaration().getName());
    assertEquals("Beta", root.getTypeNodes().get(1).getDeclaration().getName());
    assertEquals("beta", root.getFieldNodes().get(0).getDeclaration().getName());
    assertEquals("gamma", root.getFieldNodes().get(1).getDeclaration().getName());
  }

  private static AbstractType<?, ?, ?> createUserType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(Object.class));
    return type;
  }

  private static UserField createField(String name) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.getInstance(String.class));
    return field;
  }
}
