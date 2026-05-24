package org.alice.ide.sceneeditor;

import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;

import java.util.List;

final class AbstractSceneEditorLogic {
  private AbstractSceneEditorLogic() {
    throw new AssertionError();
  }

  static int indexOfField(List<UserField> fields, UserField field) {
    for (int i = 0; i < fields.size(); i++) {
      if (fields.get(i) == field) {
        return i;
      }
    }
    return -1;
  }

  static NamedUserType getActiveSceneType(UserField field) {
    if (field != null) {
      AbstractType<?, ?, ?> type = field.getValueType();
      if (type instanceof NamedUserType userType) {
        return userType;
      }
    }
    return null;
  }
}
