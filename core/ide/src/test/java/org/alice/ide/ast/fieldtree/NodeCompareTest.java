package org.alice.ide.ast.fieldtree;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.UserField;

import static org.junit.Assert.*;

public class NodeCompareTest {

  @Test
  public void compareToReturnsNegativeForAlphabeticallyEarlierName() {
    TypeNode parent = createParent();
    FieldNode apple = FieldNode.createAndAddToParent(parent, createField("Apple"));
    FieldNode banana = FieldNode.createAndAddToParent(parent, createField("banana"));

    assertTrue(apple.compareTo(banana) < 0);
  }

  @Test
  public void compareToReturnsPositiveForAlphabeticallyLaterName() {
    TypeNode parent = createParent();
    FieldNode banana = FieldNode.createAndAddToParent(parent, createField("banana"));
    FieldNode apple = FieldNode.createAndAddToParent(parent, createField("Apple"));

    assertTrue(banana.compareTo(apple) > 0);
  }

  @Test
  public void compareToReturnsZeroForSameNameIgnoringCase() {
    TypeNode parent = createParent();
    FieldNode first = FieldNode.createAndAddToParent(parent, createField("sameName"));
    FieldNode second = FieldNode.createAndAddToParent(parent, createField("SAMENAME"));

    assertEquals(0, first.compareTo(second));
  }

  @Test
  public void compareToIsCaseInsensitiveForAppleVsBanana() {
    TypeNode parent = createParent();
    FieldNode apple = FieldNode.createAndAddToParent(parent, createField("Apple"));
    FieldNode banana = FieldNode.createAndAddToParent(parent, createField("banana"));

    assertTrue(apple.compareTo(banana) < 0);
  }

  @Test
  public void toStringContainsFieldNodeAndFieldName() {
    TypeNode parent = createParent();

    FieldNode node = FieldNode.createAndAddToParent(parent, createField("sampleField"));
    String text = node.toString();

    assertTrue(text.contains("FieldNode"));
    assertTrue(text.contains("sampleField"));
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
