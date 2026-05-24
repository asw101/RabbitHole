package org.alice.ide.ast.type.merge.croquet;

import java.util.List;

final class AddMembersPageLogic {
  private AddMembersPageLogic() {
    throw new AssertionError();
  }

  static String buildPageStatusText(List<String> fragments) {
    StringBuilder sb = new StringBuilder();
    for (String fragment : fragments) {
      if (fragment != null && !fragment.isEmpty()) {
        sb.append(fragment);
      }
    }
    return sb.toString();
  }
}
