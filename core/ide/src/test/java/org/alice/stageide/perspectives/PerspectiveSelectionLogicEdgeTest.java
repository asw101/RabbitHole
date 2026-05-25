package org.alice.stageide.perspectives;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class PerspectiveSelectionLogicEdgeTest {
  @Test
  public void buildMapAllowsNullKeysAndOverwritesDuplicatePerspectiveEntries() {
    Map<String, Integer> map = PerspectiveSelectionLogic.buildMap(null, 1, null, 2);

    assertEquals(1, map.size());
    assertEquals(Integer.valueOf(2), map.get(null));
  }

  @Test
  public void getCardRequiresANonNullMap() {
    assertThrows(NullPointerException.class, () -> PerspectiveSelectionLogic.getCard(null, "code"));
  }
}
