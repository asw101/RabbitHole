package org.alice.ide.ast.fieldtree;

import org.junit.Test;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.UserField;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class FieldTreeTest {
  @Test
  public void createTreeFor_emptyFields_returnsEmptyRoot() {
    RootNode root = FieldTree.createTreeFor(
        Collections.<UserField>emptyList(),
        new FieldTree.TypeCollapseThresholdData(Object.class, 10, 10));
    assertNotNull(root);
    assertTrue(root.getFieldNodes().isEmpty());
  }

  @Test
  public void createFirstClassThreshold_returnsNonNull() {
    FieldTree.TypeCollapseThresholdData data = FieldTree.createFirstClassThreshold(Object.class);
    assertNotNull(data);
    assertNotNull(data.getType());
  }

  @Test
  public void createSecondClassThreshold_returnsNonNull() {
    FieldTree.TypeCollapseThresholdData data = FieldTree.createSecondClassThreshold(String.class);
    assertNotNull(data);
    assertNotNull(data.getType());
  }

  @Test
  public void createFirstClassThreshold_hasHigherThresholdThanSecondClass() {
    FieldTree.TypeCollapseThresholdData first = FieldTree.createFirstClassThreshold(Object.class);
    FieldTree.TypeCollapseThresholdData second = FieldTree.createSecondClassThreshold(Object.class);
    assertTrue(first.getCollapseThreshold() >= second.getCollapseThreshold());
  }

  @Test
  public void typeCollapseThresholdData_classConstructor() {
    FieldTree.TypeCollapseThresholdData data = new FieldTree.TypeCollapseThresholdData(String.class, 5, 3);
    assertEquals(5, data.getCollapseThreshold());
    assertEquals(3, data.getCollapseThresholdForDescendants());
    assertEquals(JavaType.getInstance(String.class), data.getType());
  }

  @Test
  public void typeCollapseThresholdData_typeConstructor() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(Integer.class);
    FieldTree.TypeCollapseThresholdData data = new FieldTree.TypeCollapseThresholdData(type, 8, 6);
    assertSame(type, data.getType());
    assertEquals(8, data.getCollapseThreshold());
    assertEquals(6, data.getCollapseThresholdForDescendants());
  }

  @Test
  public void createTreeFor_withFields_createsNodes() {
    UserField field1 = new UserField();
    field1.name.setValue("f1");
    field1.valueType.setValue(JavaType.getInstance(Object.class));

    RootNode root = FieldTree.createTreeFor(
        Arrays.asList(field1),
        new FieldTree.TypeCollapseThresholdData(Object.class, 10, 10));
    assertNotNull(root);
    int totalFields = countFields(root);
    assertEquals(1, totalFields);
  }

  private int countFields(TypeNode node) {
    int count = node.getFieldNodes().size();
    for (TypeNode child : node.getTypeNodes()) {
      count += countFields(child);
    }
    return count;
  }
}
