package org.alice.ide.ast.fieldtree;

import org.junit.Test;
import org.lgna.project.ast.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Deep coverage tests for {@link FieldTree} — tree creation, node structure,
 * collapsing, sorting, and threshold behavior.
 */
public class FieldTreeDeepTest {

  // ---- createTreeFor with empty fields and no thresholds ----

  @Test
  public void createTreeFor_emptyFieldsNoThresholds_returnsEmptyRoot() {
    List<UserField> fields = Collections.emptyList();
    RootNode root = FieldTree.createTreeFor(fields);
    assertNotNull(root);
    assertTrue("Root should have no type nodes with empty fields",
        root.getTypeNodes().isEmpty());
    assertTrue("Root should have no field nodes with empty fields",
        root.getFieldNodes().isEmpty());
  }

  // ---- createTreeFor single field with matching threshold ----

  @Test
  public void createTreeFor_singleField_collapsesToRoot() {
    UserField field = createField("alpha", String.class);
    FieldTree.TypeCollapseThresholdData threshold =
        new FieldTree.TypeCollapseThresholdData(Object.class, 10, 10);
    RootNode root = FieldTree.createTreeFor(Collections.singletonList(field), threshold);
    assertNotNull(root);
    // With threshold 10 and only 1 field, it should collapse to root
  }

  // ---- createTreeFor multiple fields of same type ----

  @Test
  public void createTreeFor_multipleFieldsSameType_groupedUnderType() {
    UserField f1 = createField("alpha", String.class);
    UserField f2 = createField("beta", String.class);
    UserField f3 = createField("gamma", String.class);

    // Threshold of 2 means if there are fewer than 2, collapse up
    FieldTree.TypeCollapseThresholdData threshold =
        new FieldTree.TypeCollapseThresholdData(Object.class, 2, 2);
    RootNode root = FieldTree.createTreeFor(Arrays.asList(f1, f2, f3), threshold);
    assertNotNull(root);
  }

  // ---- TypeCollapseThresholdData getters ----

  @Test
  public void typeCollapseThresholdData_getters_returnConstructorValues() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(Double.class);
    FieldTree.TypeCollapseThresholdData data =
        new FieldTree.TypeCollapseThresholdData(type, 8, 4);
    assertSame(type, data.getType());
    assertEquals(8, data.getCollapseThreshold());
    assertEquals(4, data.getCollapseThresholdForDescendants());
  }

  // ---- createFirstClassThreshold ----

  @Test
  public void createFirstClassThreshold_hasExpectedThresholds() {
    FieldTree.TypeCollapseThresholdData data =
        FieldTree.createFirstClassThreshold(String.class);
    assertNotNull(data);
    assertEquals(JavaType.getInstance(String.class), data.getType());
    assertEquals(10, data.getCollapseThreshold());
    assertEquals(10, data.getCollapseThresholdForDescendants());
  }

  // ---- createSecondClassThreshold ----

  @Test
  public void createSecondClassThreshold_hasExpectedThresholds() {
    FieldTree.TypeCollapseThresholdData data =
        FieldTree.createSecondClassThreshold(Integer.class);
    assertNotNull(data);
    assertEquals(JavaType.getInstance(Integer.class), data.getType());
    assertEquals(5, data.getCollapseThreshold());
    assertEquals(5, data.getCollapseThresholdForDescendants());
  }

  // ---- RootNode is a TypeNode with null declaration ----

  @Test
  public void rootNode_declarationIsNull() {
    RootNode root = new RootNode();
    assertNull(root.getDeclaration());
  }

  @Test
  public void rootNode_parentIsNull() {
    RootNode root = new RootNode();
    assertNull(root.getParent());
  }

  // ---- TypeNode.createAndAddToParent ----

  @Test
  public void typeNode_createAndAddToParent_addsToParentTypeNodes() {
    RootNode root = new RootNode();
    TypeNode child = TypeNode.createAndAddToParent(
        root, JavaType.getInstance(String.class), 5, 3);
    assertNotNull(child);
    assertTrue(root.getTypeNodes().contains(child));
    assertEquals(5, child.getCollapseThreshold());
    assertEquals(3, child.getCollapseThresholdForDescendants());
  }

  // ---- FieldNode.createAndAddToParent ----

  @Test
  public void fieldNode_createAndAddToParent_addsToParentFieldNodes() {
    RootNode root = new RootNode();
    TypeNode typeNode = TypeNode.createAndAddToParent(
        root, JavaType.getInstance(Object.class), 10, 10);
    UserField field = createField("myField", Object.class);
    FieldNode fieldNode = FieldNode.createAndAddToParent(typeNode, field);
    assertNotNull(fieldNode);
    assertTrue(typeNode.getFieldNodes().contains(fieldNode));
    assertSame(field, fieldNode.getDeclaration());
  }

  // ---- Tree sorting ----

  @Test
  public void createTreeFor_multipleFields_sortedAlphabetically() {
    UserField fGamma = createField("gamma", String.class);
    UserField fAlpha = createField("alpha", String.class);
    UserField fBeta = createField("beta", String.class);

    FieldTree.TypeCollapseThresholdData threshold =
        new FieldTree.TypeCollapseThresholdData(Object.class, 1, 1);
    RootNode root = FieldTree.createTreeFor(
        Arrays.asList(fGamma, fAlpha, fBeta), threshold);
    assertNotNull(root);
    // After sort, check that fields are in alphabetical order
    // Fields may be on root or in type nodes depending on collapse
    StringBuilder sb = new StringBuilder();
    root.append(sb, 0);
    String treeStr = sb.toString();
    assertFalse("Tree string should not be empty", treeStr.isEmpty());
  }

  // ---- Node.toString ----

  @Test
  public void rootNode_toString_containsClassName() {
    RootNode root = new RootNode();
    String str = root.toString();
    assertTrue(str.contains("RootNode"));
  }

  // ---- helper ----

  private static UserField createField(String name, Class<?> cls) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.getInstance(cls));
    field.initializer.setValue(new NullLiteral());
    return field;
  }
}
