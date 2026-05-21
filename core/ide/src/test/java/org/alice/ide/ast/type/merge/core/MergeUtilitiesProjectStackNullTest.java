package org.alice.ide.ast.type.merge.core;

import org.junit.Test;

import static org.junit.Assert.assertNull;

public class MergeUtilitiesProjectStackNullTest {
  @Test
  public void findMatchingTypeInExistingTypes_withoutProjectReturnsNull() {
    assertNull(MergeUtilities.findMatchingTypeInExistingTypes(MergeUtilitiesTestSupport.namedType("Detached")));
  }
}
