package org.alice.stageide.perspectives;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class PerspectiveSelectionLogicTest {
  @Test
  public void buildMapAssociatesCardsWithPerspectives() {
    Map<String, Integer> map = PerspectiveSelectionLogic.buildMap("code", 1, "setup", 2);

    assertEquals(2, map.size());
    assertEquals(Integer.valueOf(1), map.get("code"));
    assertEquals(Integer.valueOf(2), map.get("setup"));
  }

  @Test
  public void getCardReturnsSelectedPerspectiveCard() {
    Map<String, String> map = PerspectiveSelectionLogic.buildMap("code", "codeCard", "setup", "setupCard");

    assertEquals("setupCard", PerspectiveSelectionLogic.getCard(map, "setup"));
    assertNull(PerspectiveSelectionLogic.getCard(map, "missing"));
  }
}
