package org.alice.stageide.sceneeditor.interact;

import org.lgna.croquet.Application;
import org.lgna.croquet.Group;
import org.lgna.project.ast.UserField;

final class GlobalDragAdapterLogic {
  private GlobalDragAdapterLogic() {
    throw new AssertionError();
  }

  static Group resolveUndoGroup(UserField manipulatedField) {
    return manipulatedField == null ? Application.DOCUMENT_UI_GROUP : Application.PROJECT_GROUP;
  }
}
