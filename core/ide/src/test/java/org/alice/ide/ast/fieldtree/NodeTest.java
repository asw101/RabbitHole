package org.alice.ide.ast.fieldtree;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.UserField;

import static org.junit.Assert.*;

public class NodeTest {
  private TypeNode createTypeNode() {
    RootNode root = new RootNode();
    return TypeNode.createAndAddToParent(root, JavaType.getInstance(Object.class), 10, 5);
  }

  private FieldNode createFieldNode(TypeNode parent, String name) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.getInstance(Object.class));
    return FieldNode.createAndAddToParent(parent, field);
  }

  @Test
  public void getDeclaration_returnsFieldFromConstructor() {
    TypeNode parent = createTypeNode();
    UserField field = new UserField();
    field.name.setValue("alpha");
    field.valueType.setValue(JavaType.getInstance(Object.class));
    FieldNode node = FieldNode.createAndAddToParent(parent, field);
    assertSame(field, node.getDeclaration());
  }

  @Test
  public void getParent_returnsTypeNode() {
    TypeNode parent = createTypeNode();
    FieldNode node = createFieldNode(parent, "orphan");
    assertSame(parent, node.getParent());
  }

  @Test
  public void compareTo_alphabeticalOrdering() {
    TypeNode parent = createTypeNode();
    FieldNode nodeA = createFieldNode(parent, "alpha");
    FieldNode nodeB = createFieldNode(parent, "beta");

    assertTrue(nodeA.compareTo(nodeB) < 0);
    assertTrue(nodeB.compareTo(nodeA) > 0);
  }

  @Test
  public void compareTo_sameNameReturnsZero() {
    TypeNode parent = createTypeNode();
    FieldNode node1 = createFieldNode(parent, "same");
    FieldNode node2 = createFieldNode(parent, "same");

    assertEquals(0, node1.compareTo(node2));
  }

  @Test
  public void compareTo_caseInsensitive() {
    TypeNode parent = createTypeNode();
    FieldNode nodeUpper = createFieldNode(parent, "ALPHA");
    FieldNode nodeLower = createFieldNode(parent, "alpha");

    assertEquals(0, nodeUpper.compareTo(nodeLower));
  }

  @Test
  public void toString_includesClassName() {
    TypeNode parent = createTypeNode();
    FieldNode node = createFieldNode(parent, "myField");
    String result = node.toString();
    assertTrue(result.contains("FieldNode"));
  }

  @Test
  public void toString_includesFieldName() {
    TypeNode parent = createTypeNode();
    FieldNode node = createFieldNode(parent, "myField");
    String result = node.toString();
    assertTrue(result.contains("myField"));
  }

  @Test
  public void compareTo_multipleFields_sortCorrectly() {
    TypeNode parent = createTypeNode();
    FieldNode nodeC = createFieldNode(parent, "cherry");
    FieldNode nodeA = createFieldNode(parent, "apple");
    FieldNode nodeB = createFieldNode(parent, "banana");

    assertTrue(nodeA.compareTo(nodeB) < 0);
    assertTrue(nodeB.compareTo(nodeC) < 0);
    assertTrue(nodeA.compareTo(nodeC) < 0);
  }
}
