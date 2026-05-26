package org.alice.ide.ast.fieldtree;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.UserField;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class FieldTreeUtilityBehaviorTest {
  private static UserField createField(String name, Class<?> valueType) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.getInstance(valueType));
    field.initializer.setValue(new NullLiteral());
    return field;
  }

  private static List<String> getFieldNames(RootNode root) {
    List<String> names = new ArrayList<>();
    for (FieldNode fieldNode : root.getFieldNodes()) {
      names.add(fieldNode.getDeclaration().getName());
    }
    return names;
  }

  private static List<String> getFieldNames(TypeNode typeNode) {
    List<String> names = new ArrayList<>();
    for (FieldNode fieldNode : typeNode.getFieldNodes()) {
      names.add(fieldNode.getDeclaration().getName());
    }
    return names;
  }

  private static TypeNode findTypeNode(RootNode root, Class<?> valueType) {
    for (TypeNode typeNode : root.getTypeNodes()) {
      if (JavaType.getInstance(valueType).equals(typeNode.getDeclaration())) {
        return typeNode;
      }
    }
    fail("Unable to find type node for " + valueType.getName());
    return null;
  }

  @Test
  public void createTreeFor_collapsesSmallGroupsToRootAndSortsFieldNames() {
    RootNode root = FieldTree.createTreeFor(
        List.of(
            createField("gamma", String.class),
            createField("alpha", String.class),
            createField("beta", Integer.class)
        ),
        new FieldTree.TypeCollapseThresholdData(Object.class, 10, 10)
    );

    assertTrue(root.getTypeNodes().isEmpty());
    assertEquals(List.of("alpha", "beta", "gamma"), getFieldNames(root));
  }

  @Test
  public void createTreeFor_preservesTypeNodeWhenFieldCountMeetsThreshold() {
    RootNode root = FieldTree.createTreeFor(
        List.of(
            createField("beta", String.class),
            createField("alpha", String.class)
        ),
        new FieldTree.TypeCollapseThresholdData(Object.class, 1, 1)
    );

    assertTrue(root.getFieldNodes().isEmpty());
    assertEquals(1, root.getTypeNodes().size());

    TypeNode stringNode = findTypeNode(root, String.class);
    assertEquals(List.of("alpha", "beta"), getFieldNames(stringNode));
  }

  @Test
  public void createTreeFor_routesFieldsThroughMultipleTopLevelThresholds() {
    RootNode root = FieldTree.createTreeFor(
        List.of(
            createField("wholeNumber", Integer.class),
            createField("text", String.class)
        ),
        new FieldTree.TypeCollapseThresholdData(Number.class, 1, 1),
        new FieldTree.TypeCollapseThresholdData(Object.class, 1, 1)
    );

    assertTrue(root.getFieldNodes().isEmpty());
    assertEquals(2, root.getTypeNodes().size());

    TypeNode integerNode = findTypeNode(root, Integer.class);
    TypeNode stringNode = findTypeNode(root, String.class);

    assertEquals(List.of("wholeNumber"), getFieldNames(integerNode));
    assertEquals(List.of("text"), getFieldNames(stringNode));
  }

  @Test
  public void append_rendersIndentedHierarchyForPreservedTypeNode() {
    RootNode root = FieldTree.createTreeFor(
        List.of(
            createField("beta", String.class),
            createField("alpha", String.class)
        ),
        new FieldTree.TypeCollapseThresholdData(Object.class, 1, 1)
    );

    StringBuilder sb = new StringBuilder();
    root.append(sb, 0);
    String tree = sb.toString();

    assertTrue(tree.contains(JavaType.getInstance(String.class).getName()));
    assertTrue(tree.contains("\t\t"));
    assertTrue(tree.contains("alpha"));
    assertTrue(tree.indexOf("alpha") < tree.indexOf("beta"));
  }
}
