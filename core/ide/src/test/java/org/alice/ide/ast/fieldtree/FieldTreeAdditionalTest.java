package org.alice.ide.ast.fieldtree;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Additional characterization tests for {@link FieldTree}.
 */
public class FieldTreeAdditionalTest {

  @Test
  public void typeCollapseThresholdData_largeTresholdValues() {
    FieldTree.TypeCollapseThresholdData data =
        new FieldTree.TypeCollapseThresholdData(Object.class, Integer.MAX_VALUE, Integer.MAX_VALUE);
    assertEquals(Integer.MAX_VALUE, data.getCollapseThreshold());
    assertEquals(Integer.MAX_VALUE, data.getCollapseThresholdForDescendants());
  }

  @Test
  public void typeCollapseThresholdData_zeroThresholds() {
    FieldTree.TypeCollapseThresholdData data =
        new FieldTree.TypeCollapseThresholdData(String.class, 0, 0);
    assertEquals(0, data.getCollapseThreshold());
    assertEquals(0, data.getCollapseThresholdForDescendants());
  }

  @Test
  public void firstClassThreshold_largerThanSecondClass() {
    FieldTree.TypeCollapseThresholdData first =
        FieldTree.createFirstClassThreshold(Object.class);
    FieldTree.TypeCollapseThresholdData second =
        FieldTree.createSecondClassThreshold(Object.class);
    assertTrue(first.getCollapseThreshold() >= second.getCollapseThreshold());
  }

  @Test
  public void typeCollapseThresholdData_negativeThreshold_accepted() {
    FieldTree.TypeCollapseThresholdData data =
        new FieldTree.TypeCollapseThresholdData(Object.class, -1, -1);
    assertEquals(-1, data.getCollapseThreshold());
    assertEquals(-1, data.getCollapseThresholdForDescendants());
  }
}
