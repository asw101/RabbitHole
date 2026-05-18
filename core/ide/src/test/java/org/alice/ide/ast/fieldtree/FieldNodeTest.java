package org.alice.ide.ast.fieldtree;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.UserField;

import static org.junit.Assert.*;

public class FieldNodeTest {
  private UserField createTestField(String name) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.getInstance(String.class));
    return field;
  }

  @Test
  public void createAndAddToParent_addsToParentFieldNodes() {
    RootNode root = new RootNode();
    TypeNode typeNode = TypeNode.createAndAddToParent(root, JavaType.getInstance(Object.class), 10, 5);
    UserField field = createTestField("myField");
    FieldNode fieldNode = FieldNode.createAndAddToParent(typeNode, field);
    assertEquals(1, typeNode.getFieldNodes().size());
    assertSame(fieldNode, typeNode.getFieldNodes().get(0));
  }

  @Test
  public void getDeclaration_returnsField() {
    RootNode root = new RootNode();
    TypeNode typeNode = TypeNode.createAndAddToParent(root, JavaType.getInstance(Object.class), 10, 5);
    UserField field = createTestField("testField");
    FieldNode fieldNode = FieldNode.createAndAddToParent(typeNode, field);
    assertSame(field, fieldNode.getDeclaration());
  }

  @Test
  public void getParent_returnsTypeNode() {
    RootNode root = new RootNode();
    TypeNode typeNode = TypeNode.createAndAddToParent(root, JavaType.getInstance(Object.class), 10, 5);
    UserField field = createTestField("parentField");
    FieldNode fieldNode = FieldNode.createAndAddToParent(typeNode, field);
    assertSame(typeNode, fieldNode.getParent());
  }

  @Test
  public void compareTo_alphabeticalOrder() {
    RootNode root = new RootNode();
    TypeNode typeNode = TypeNode.createAndAddToParent(root, JavaType.getInstance(Object.class), 10, 5);
    UserField fieldA = createTestField("alpha");
    UserField fieldB = createTestField("beta");
    FieldNode nodeA = FieldNode.createAndAddToParent(typeNode, fieldA);
    FieldNode nodeB = FieldNode.createAndAddToParent(typeNode, fieldB);
    assertTrue(nodeA.compareTo(nodeB) < 0);
    assertTrue(nodeB.compareTo(nodeA) > 0);
  }

  @Test
  public void compareTo_caseInsensitive() {
    RootNode root = new RootNode();
    TypeNode typeNode = TypeNode.createAndAddToParent(root, JavaType.getInstance(Object.class), 10, 5);
    UserField fieldA = createTestField("Alpha");
    UserField fielda = createTestField("alpha");
    FieldNode nodeA = FieldNode.createAndAddToParent(typeNode, fieldA);
    FieldNode nodea = FieldNode.createAndAddToParent(typeNode, fielda);
    assertEquals(0, nodeA.compareTo(nodea));
  }

  @Test
  public void toString_containsFieldName() {
    RootNode root = new RootNode();
    TypeNode typeNode = TypeNode.createAndAddToParent(root, JavaType.getInstance(Object.class), 10, 5);
    UserField field = createTestField("mySpecialField");
    FieldNode fieldNode = FieldNode.createAndAddToParent(typeNode, field);
    assertTrue(fieldNode.toString().contains("FieldNode"));
  }
}
