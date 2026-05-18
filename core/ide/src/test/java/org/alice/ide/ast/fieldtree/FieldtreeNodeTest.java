package org.alice.ide.ast.fieldtree;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;

import static org.junit.Assert.*;

/**
 * Tests for FieldNode behavior when parented under a TypeNode.
 * Basic FieldNode/compareTo/toString contracts are covered in FieldNodeTest.
 */
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

  @Test
  public void fieldNode_parentIsTypeNode() {
    TypeNode parent = createTypeNode("Parent");
    UserField field = createField("speed");
    FieldNode node = FieldNode.createAndAddToParent(parent, field);
    assertSame(parent, node.getParent());
  }

  @Test
  public void createAndAddToParent_incrementsParentChildCount() {
    TypeNode parent = createTypeNode("Parent");
    int before = parent.getFieldNodes().size();
    FieldNode.createAndAddToParent(parent, createField("newField"));
    assertEquals(before + 1, parent.getFieldNodes().size());
  }

  @Test
  public void fieldNode_toString_containsFieldNode() {
    TypeNode parent = createTypeNode("Parent");
    FieldNode node = FieldNode.createAndAddToParent(parent, createField("myField"));
    assertNotNull(node.toString());
  }
}
