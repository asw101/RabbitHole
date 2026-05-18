package org.alice.ide.ast.fieldtree;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.UserField;

import static org.junit.Assert.*;

public class FieldNodeTest {

  @Test
  public void createAndAddToParent_addsToParentFieldNodes() {
    RootNode root = new RootNode();
    UserField field = createField("alpha");

    FieldNode fieldNode = FieldNode.createAndAddToParent(root, field);

    assertTrue(root.getFieldNodes().contains(fieldNode));
  }

  @Test
  public void createAndAddToParent_parentIsSet() {
    RootNode root = new RootNode();
    UserField field = createField("alpha");

    FieldNode fieldNode = FieldNode.createAndAddToParent(root, field);

    assertSame(root, fieldNode.getParent());
  }

  @Test
  public void createAndAddToParent_declarationIsSet() {
    RootNode root = new RootNode();
    UserField field = createField("alpha");

    FieldNode fieldNode = FieldNode.createAndAddToParent(root, field);

    assertSame(field, fieldNode.getDeclaration());
  }

  @Test
  public void compareTo_alphabeticalOrder() {
    RootNode root = new RootNode();
    FieldNode alpha = FieldNode.createAndAddToParent(root, createField("alpha"));
    FieldNode beta = FieldNode.createAndAddToParent(root, createField("beta"));

    assertTrue(alpha.compareTo(beta) < 0);
    assertTrue(beta.compareTo(alpha) > 0);
  }

  @Test
  public void compareTo_sameDeclaration_returnsZero() {
    RootNode root = new RootNode();
    FieldNode left = FieldNode.createAndAddToParent(root, createField("alpha"));
    FieldNode right = FieldNode.createAndAddToParent(root, createField("alpha"));

    assertEquals(0, left.compareTo(right));
  }

  @Test
  public void compareTo_caseInsensitive() {
    RootNode root = new RootNode();
    FieldNode upper = FieldNode.createAndAddToParent(root, createField("Alpha"));
    FieldNode lower = FieldNode.createAndAddToParent(root, createField("alpha"));

    assertEquals(0, upper.compareTo(lower));
  }

  @Test
  public void toString_containsFieldName() {
    RootNode root = new RootNode();
    FieldNode fieldNode = FieldNode.createAndAddToParent(root, createField("alpha"));

    assertTrue(fieldNode.toString().contains("alpha"));
  }

  @Test
  public void toString_rootNode_containsDash() {
    RootNode root = new RootNode();

    assertTrue(root.toString().contains(" - "));
  }

  private static UserField createField(String name) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.getInstance(String.class));
    return field;
  }
}
