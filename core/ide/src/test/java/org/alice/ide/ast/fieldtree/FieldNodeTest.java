package org.alice.ide.ast.fieldtree;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.UserField;

import static org.junit.Assert.*;

public class FieldNodeTest {

  @Test
  public void createAndAddToParentCreatesFieldNodeAndAddsItToParent() {
    TypeNode parent = createParent();
    UserField field = createField("myField");

    FieldNode node = FieldNode.createAndAddToParent(parent, field);

    assertSame(parent, node.getParent());
    assertTrue(parent.getFieldNodes().contains(node));
  }

  @Test
  public void getDeclarationReturnsTheUserField() {
    TypeNode parent = createParent();
    UserField field = createField("myField");

    FieldNode node = FieldNode.createAndAddToParent(parent, field);

    assertSame(field, node.getDeclaration());
  }

  @Test
  public void getParentReturnsTheParentTypeNode() {
    TypeNode parent = createParent();

    FieldNode node = FieldNode.createAndAddToParent(parent, createField("myField"));

    assertSame(parent, node.getParent());
  }

  @Test
  public void toStringContainsClassNameAndFieldName() {
    TypeNode parent = createParent();

    FieldNode node = FieldNode.createAndAddToParent(parent, createField("myField"));
    String text = node.toString();

    assertTrue(text.contains("FieldNode"));
    assertTrue(text.contains("myField"));
  }

  @Test
  public void compareToComparesFieldNamesCaseInsensitively() {
    TypeNode parent = createParent();
    FieldNode alpha = FieldNode.createAndAddToParent(parent, createField("alpha"));
    FieldNode beta = FieldNode.createAndAddToParent(parent, createField("Beta"));

    assertTrue(alpha.compareTo(beta) < 0);
    assertTrue(beta.compareTo(alpha) > 0);
  }

  private static TypeNode createParent() {
    RootNode root = new RootNode();
    return TypeNode.createAndAddToParent(root, JavaType.getInstance(Object.class), 10, 10);
  }

  private static UserField createField(String name) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.getInstance(String.class));
    return field;
  }
}
