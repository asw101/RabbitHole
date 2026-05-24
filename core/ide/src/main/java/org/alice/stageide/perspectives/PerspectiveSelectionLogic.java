package org.alice.stageide.perspectives;

import java.util.HashMap;
import java.util.Map;

final class PerspectiveSelectionLogic {
  private PerspectiveSelectionLogic() {
    throw new AssertionError();
  }

  static <T, U> Map<T, U> buildMap(T codePerspective, U codeCard, T setupScenePerspective, U setupScenePerspectiveCard) {
    Map<T, U> rv = new HashMap<>();
    rv.put(codePerspective, codeCard);
    rv.put(setupScenePerspective, setupScenePerspectiveCard);
    return rv;
  }

  static <T, U> U getCard(Map<T, U> map, T perspective) {
    return map.get(perspective);
  }
}
