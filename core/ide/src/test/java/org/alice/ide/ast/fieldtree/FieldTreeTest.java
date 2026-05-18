package org.alice.ide.ast.fieldtree;

import org.junit.Test;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.UserField;
import org.lgna.story.SBiped;

import java.util.Collections;

import static org.junit.Assert.*;

public class FieldTreeTest {

  @Test
  public void createFirstClassThresholdReturnsTenForBothThresholds() {
    FieldTree.TypeCollapseThresholdData data = FieldTree.createFirstClassThreshold(SBiped.class);

    assertEquals(JavaType.getInstance(SBiped.class), data.getType());
    assertEquals(10, data.getCollapseThreshold());
    assertEquals(10, data.getCollapseThresholdForDescendants());
  }

  @Test
  public void createSecondClassThresholdReturnsFiveForBothThresholds() {
    FieldTree.TypeCollapseThresholdData data = FieldTree.createSecondClassThreshold(String.class);

    assertEquals(JavaType.getInstance(String.class), data.getType());
    assertEquals(5, data.getCollapseThreshold());
    assertEquals(5, data.getCollapseThresholdForDescendants());
  }

  @Test
  public void createTreeForWithEmptyFieldsReturnsEmptyRootNode() {
    RootNode root = FieldTree.createTreeFor(Collections.<UserField>emptyList());

    assertNotNull(root);
    assertTrue(root.getTypeNodes().isEmpty());
    assertTrue(root.getFieldNodes().isEmpty());
  }

  @Test
  public void typeCollapseThresholdDataStoresProvidedValues() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(String.class);
    FieldTree.TypeCollapseThresholdData data = new FieldTree.TypeCollapseThresholdData(type, 7, 3);

    assertSame(type, data.getType());
    assertEquals(7, data.getCollapseThreshold());
    assertEquals(3, data.getCollapseThresholdForDescendants());
  }
}
