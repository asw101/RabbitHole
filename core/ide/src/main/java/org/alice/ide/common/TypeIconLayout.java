package org.alice.ide.common;

import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserMethod;

final class TypeIconLayout {
  static final int INDENT_PER_DEPTH = 12;
  static final int BONUS_GAP = 4;

  private TypeIconLayout() {
    throw new AssertionError();
  }

  static int countDisplayableMembers(NamedUserType userType) {
    int count = userType.fields.size();
    for (UserMethod method : userType.methods) {
      if (method.getManagementLevel() == ManagementLevel.NONE) {
        count += 1;
      }
    }
    return count;
  }

  static String getBonusText(AbstractType<?, ?, ?> type, boolean isIndentForDepthAndMemberCountTextDesired) {
    if (!isIndentForDepthAndMemberCountTextDesired || !(type instanceof NamedUserType userType)) {
      return null;
    }
    int count = countDisplayableMembers(userType);
    return count > 0 ? "(%d)".formatted(count) : null;
  }

  static int calculateExtraWidth(boolean isIndentForDepthAndMemberCountTextDesired, int bonusTextWidth, int depth) {
    if (!isIndentForDepthAndMemberCountTextDesired) {
      return 0;
    }
    int extra = BONUS_GAP + bonusTextWidth;
    if (depth > 0) {
      extra += depth * INDENT_PER_DEPTH;
    }
    return extra;
  }
}
