package org.alice.ide.ast.fieldtree;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;

import static org.junit.Assert.*;

public class FieldtreeNodeTest {

  private UserField createField(String name) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.DOUBLE_OBJECT_TYPE);
    field.managementLevel.setValue(ManagementLevel.NONE);
    return field;
  }

  private TypeNode createTypeNode(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(Object.class));
    return TypeNode.createAndAddToParent(null, type, 0, 0);
  }

  // Unique tests below — declaration/parent/alphabetical tests already in FieldNodeTest

  @Test
  public void fieldNode_compareTo_sameNameReturnsZero() {
    TypeNode parent = createTypeNode("Parent");
    FieldNode node1 = FieldNode.createAndAddToParent(parent, createField("same"));
    FieldNode node2 = FieldNode.createAndAddToParent(parent, createField("same"));
    assertEquals(0, node1.compareTo(node2));
  }

  @Test
  public void fieldNode_compareTo_caseInsensitive() {
    TypeNode parent = createTypeNode("Parent");
    FieldNode nodeUpper = FieldNode.createAndAddToParent(parent, createField("ALPHA"));
    FieldNode nodeLower = FieldNode.createAndAddToParent(parent, createField("alpha"));
    assertEquals(0, nodeUpper.compareTo(nodeLower));
  }
}
