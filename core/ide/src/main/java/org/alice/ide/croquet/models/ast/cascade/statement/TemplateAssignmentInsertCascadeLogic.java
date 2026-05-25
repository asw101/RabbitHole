package org.alice.ide.croquet.models.ast.cascade.statement;

import org.alice.ide.statementfactory.LocalArrayAtIndexAssignmentFillIn;
import org.alice.ide.statementfactory.LocalAssignmentFillIn;
import org.lgna.croquet.CascadeBlankChild;
import org.lgna.project.ast.AbstractField;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserLocal;

import java.util.ArrayList;
import java.util.List;

final class TemplateAssignmentInsertCascadeLogic {
  private TemplateAssignmentInsertCascadeLogic() {
    throw new AssertionError();
  }

  static List<UserField> getAssignableFields(AbstractType<?, ?, ?> selectedType) {
    List<UserField> assignableFields = new ArrayList<>();
    if (selectedType != null) {
      for (AbstractField field : selectedType.getDeclaredFields()) {
        if ((field instanceof UserField userField) && !userField.isFinal()) {
          assignableFields.add(userField);
        }
      }
    }
    return assignableFields;
  }

  static List<UserLocal> getAssignableLocals(Iterable<UserLocal> accessibleLocals) {
    List<UserLocal> assignableLocals = new ArrayList<>();
    for (UserLocal local : accessibleLocals) {
      if (!local.isFinal.getValue()) {
        assignableLocals.add(local);
      }
    }
    return assignableLocals;
  }

  static boolean hasAssignableTargets(List<UserField> assignableFields, List<UserLocal> assignableLocals) {
    return !(assignableFields.isEmpty() && assignableLocals.isEmpty());
  }

  static List<CascadeBlankChild> buildTargetChildren(List<UserField> assignableFields, List<UserLocal> assignableLocals) {
    List<CascadeBlankChild> children = new ArrayList<>();
    if (!assignableFields.isEmpty()) {
      children.add(FieldsSeparatorModel.getInstance());
      for (UserField field : assignableFields) {
        children.add(FieldAssignmentFillIn.getInstance(field));
        if (field.getValueType().isArray()) {
          children.add(FieldArrayAtIndexAssignmentFillIn.getInstance(field));
        }
      }
    }

    if (!assignableLocals.isEmpty()) {
      children.add(VariablesSeparatorModel.getInstance());
      for (UserLocal local : assignableLocals) {
        children.add(LocalAssignmentFillIn.getInstance(local));
        if (local.getValueType().isArray()) {
          children.add(LocalArrayAtIndexAssignmentFillIn.getInstance(local));
        }
      }
    }

    if (!hasAssignableTargets(assignableFields, assignableLocals)) {
      children.add(NoVariablesOrFieldsAccessibleCancelFillIn.getInstance());
    }
    return children;
  }
}
