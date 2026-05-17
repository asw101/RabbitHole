package org.alice.ide.ast.fieldtree;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link FieldTree} — tree construction and threshold data.
 */
public class FieldTreeTest {

  // ---- TypeCollapseThresholdData ----

  @Test
  public void typeCollapseThresholdData_fromClass() {
    FieldTree.TypeCollapseThresholdData data =
        new FieldTree.TypeCollapseThresholdData(String.class, 5, 3);
    assertEquals(JavaType.getInstance(String.class), data.getType());
    assertEquals(5, data.getCollapseThreshold());
    assertEquals(3, data.getCollapseThresholdForDescendants());
  }

  @Test
  public void typeCollapseThresholdData_fromType() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(Integer.class);
    FieldTree.TypeCollapseThresholdData data =
        new FieldTree.TypeCollapseThresholdData(type, 10, 7);
    assertSame(type, data.getType());
    assertEquals(10, data.getCollapseThreshold());
    assertEquals(7, data.getCollapseThresholdForDescendants());
  }

  // ---- factory helpers ----

  @Test
  public void createFirstClassThreshold_returnsNonNull() {
    FieldTree.TypeCollapseThresholdData data =
        FieldTree.createFirstClassThreshold(Object.class);
    assertNotNull(data);
  }

  @Test
  public void createSecondClassThreshold_returnsNonNull() {
    FieldTree.TypeCollapseThresholdData data =
        FieldTree.createSecondClassThreshold(Object.class);
    assertNotNull(data);
  }

  // ---- createTreeFor with empty fields ----

  @Test
  public void createTreeFor_emptyFields_returnsNonNullRoot() {
    java.util.List<UserField> emptyFields = java.util.Collections.emptyList();
    Object root = FieldTree.createTreeFor(emptyFields);
    assertNotNull(root);
  }

  // ---- createTreeFor with single field ----

  @Test
  public void createTreeFor_singleField_withThreshold_returnsNonNullRoot() {
    UserField field = new UserField();
    field.name.setValue("testField");
    field.valueType.setValue(JavaType.getInstance(String.class));
    field.initializer.setValue(new NullLiteral());

    // Must provide a threshold for the root of the type hierarchy
    FieldTree.TypeCollapseThresholdData threshold =
        new FieldTree.TypeCollapseThresholdData(Object.class, 10, 10);
    java.util.List<UserField> fields = java.util.Collections.singletonList(field);
    Object root = FieldTree.createTreeFor(fields, threshold);
    assertNotNull(root);
  }

  // ---- createTreeFor with multiple fields ----

  @Test
  public void createTreeFor_multipleFields_withThreshold_returnsNonNullRoot() {
    UserField f1 = createField("alpha", String.class);
    UserField f2 = createField("beta", Integer.class);
    UserField f3 = createField("gamma", Double.class);

    FieldTree.TypeCollapseThresholdData threshold =
        new FieldTree.TypeCollapseThresholdData(Object.class, 10, 10);
    java.util.List<UserField> fields = java.util.List.of(f1, f2, f3);
    Object root = FieldTree.createTreeFor(fields, threshold);
    assertNotNull(root);
  }

  // ---- createTreeFor with threshold data ----

  @Test
  public void createTreeFor_withThresholds_returnsNonNullRoot() {
    UserField field = createField("testField", String.class);
    java.util.List<UserField> fields = java.util.Collections.singletonList(field);
    FieldTree.TypeCollapseThresholdData threshold =
        FieldTree.createFirstClassThreshold(String.class);
    Object root = FieldTree.createTreeFor(fields, threshold);
    assertNotNull(root);
  }

  private static UserField createField(String name, Class<?> cls) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.getInstance(cls));
    field.initializer.setValue(new NullLiteral());
    return field;
  }
}
