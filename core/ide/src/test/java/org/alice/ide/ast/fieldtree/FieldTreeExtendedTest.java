package org.alice.ide.ast.fieldtree;

import org.junit.Test;
import org.lgna.project.ast.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class FieldTreeExtendedTest {

  private NamedUserType createType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(Object.class));
    return type;
  }

  private UserField createField(String name) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.DOUBLE_OBJECT_TYPE);
    field.managementLevel.setValue(ManagementLevel.NONE);
    return field;
  }

  // Unique tests below — basic TypeNode/FieldNode creation, parent, children tests
  // already in TypeNodeTest and FieldNodeTest

  @Test
  public void typeNode_toString_containsTypeName() {
    NamedUserType type = createType("Actor");
    TypeNode node = TypeNode.createAndAddToParent(null, type, 0, 0);
    assertTrue(node.toString().contains("Actor"));
  }

  @Test
  public void typeNode_compareTo_byNameCaseInsensitive() {
    TypeNode nodeA = TypeNode.createAndAddToParent(null, createType("Alpha"), 0, 0);
    TypeNode nodeB = TypeNode.createAndAddToParent(null, createType("Bravo"), 0, 0);
    assertTrue(nodeA.compareTo(nodeB) < 0);
  }

  @Test
  public void typeNode_withParent_parentNotNull() {
    TypeNode parent = TypeNode.createAndAddToParent(null, createType("Parent"), 0, 0);
    TypeNode child = TypeNode.createAndAddToParent(parent, createType("Child"), 0, 0);
    assertSame(parent, child.getParent());
  }

  @Test
  public void multipleFieldNodes_sortCorrectly() {
    TypeNode parent = TypeNode.createAndAddToParent(null, createType("P"), 0, 0);
    FieldNode nZ = FieldNode.createAndAddToParent(parent, createField("zebra"));
    FieldNode nA = FieldNode.createAndAddToParent(parent, createField("apple"));
    FieldNode nM = FieldNode.createAndAddToParent(parent, createField("mango"));

    List<FieldNode> list = new ArrayList<>(List.of(nZ, nA, nM));
    Collections.sort(list);
    assertEquals("apple", list.get(0).getDeclaration().getName());
    assertEquals("mango", list.get(1).getDeclaration().getName());
    assertEquals("zebra", list.get(2).getDeclaration().getName());
  }
}
