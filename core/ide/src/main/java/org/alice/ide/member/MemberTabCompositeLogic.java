package org.alice.ide.member;

import org.lgna.project.annotations.Visibility;
import org.lgna.project.ast.AbstractField;
import org.lgna.project.ast.AbstractMember;
import org.lgna.project.ast.AbstractMethod;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

final class MemberTabCompositeLogic {
  private MemberTabCompositeLogic() {
    throw new AssertionError();
  }

  static boolean getExpandedAccountingForInert(boolean areToolPalettesInert, boolean isExpanded) {
    return areToolPalettesInert || isExpanded;
  }

  static boolean isInclusionDesired(AbstractMember member) {
    if (member instanceof AbstractMethod method) {
      if (method.isStatic()) {
        return false;
      }
    } else if (member instanceof AbstractField field) {
      if (field.isStatic()) {
        return false;
      }
    }
    if (member.isPublicAccess() || member.isUserAuthored()) {
      Visibility visibility = member.getVisibility();
      return (visibility == null) || visibility.equals(Visibility.PRIME_TIME);
    } else {
      return false;
    }
  }

  static <T extends AbstractMethod> List<T> withoutOverrides(List<T> methods) {
    List<T> filteredMethods = new LinkedList<>(methods);
    ListIterator<T> iterator = filteredMethods.listIterator();
    while (iterator.hasNext()) {
      AbstractMethod method = iterator.next();
      AbstractMethod overridden = method.getOverriddenMethod();
      if ((overridden != null) && filteredMethods.contains(overridden)) {
        iterator.remove();
      }
    }
    return filteredMethods;
  }
}
