package org.alice.ide.croquet.models.ast;

import org.lgna.project.ast.ManagementLevel;

final class DeleteMemberOperationLogic {
  private DeleteMemberOperationLogic() {
    throw new AssertionError();
  }

  static boolean hasReferences(int referenceCount) {
    return referenceCount > 0;
  }

  static boolean shouldUseManagedFieldPath(ManagementLevel managementLevel) {
    return managementLevel == ManagementLevel.MANAGED;
  }

  static String createDeleteMethodBlockedMessage(String methodName, boolean isProcedure, int referenceCount) {
    StringBuilder sb = new StringBuilder();
    sb.append("Unable to delete ");
    sb.append(isProcedure ? "procedure" : "function");
    sb.append(" named \"");
    sb.append(methodName);
    sb.append("\" because it has ");
    if (referenceCount == 1) {
      sb.append("an invocation reference");
    } else {
      sb.append(referenceCount);
      sb.append(" invocation references");
    }
    sb.append(" to it.\nYou must remove ");
    if (referenceCount == 1) {
      sb.append("this reference");
    } else {
      sb.append("these references");
    }
    sb.append(" if you want to delete \"");
    sb.append(methodName);
    sb.append("\" .");
    return sb.toString();
  }
}
