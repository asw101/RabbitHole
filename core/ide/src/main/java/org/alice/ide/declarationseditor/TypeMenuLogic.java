package org.alice.ide.declarationseditor;

import java.util.ArrayList;
import java.util.List;

final class TypeMenuLogic {
  private TypeMenuLogic() {
    throw new AssertionError();
  }

  static <T> List<T> buildMenuModels(T typeModel,
                                     boolean includeConstructors,
                                     List<T> constructorModels,
                                     List<T> procedureModels,
                                     T proceduresSeparator,
                                     T addProcedureModel,
                                     List<T> functionModels,
                                     T functionsSeparator,
                                     T addFunctionModel,
                                     boolean declaringTypeForManagedFields,
                                     List<T> managedFieldModels,
                                     T managedFieldsSeparator,
                                     T addManagedFieldModel,
                                     List<T> unmanagedFieldModels,
                                     T unmanagedFieldsSeparator,
                                     T fieldsSeparator,
                                     T addUnmanagedFieldModel,
                                     T separator) {
    List<T> models = new ArrayList<>();
    models.add(typeModel);

    if (includeConstructors) {
      models.add(separator);
      models.addAll(constructorModels);
    }

    models.add(separator);
    appendDeclarationSection(models, procedureModels, proceduresSeparator, addProcedureModel);

    models.add(separator);
    appendDeclarationSection(models, functionModels, functionsSeparator, addFunctionModel);

    if (declaringTypeForManagedFields) {
      models.add(separator);
      if (!managedFieldModels.isEmpty()) {
        models.add(managedFieldsSeparator);
        models.addAll(managedFieldModels);
      }
      models.add(addManagedFieldModel);
    }

    models.add(separator);
    if (!managedFieldModels.isEmpty()) {
      models.add(unmanagedFieldsSeparator);
      models.addAll(unmanagedFieldModels);
    } else if (!unmanagedFieldModels.isEmpty()) {
      models.add(fieldsSeparator);
      models.addAll(unmanagedFieldModels);
    }
    models.add(addUnmanagedFieldModel);
    return models;
  }

  private static <T> void appendDeclarationSection(List<T> models, List<T> declarationModels, T separator, T addModel) {
    if (!declarationModels.isEmpty()) {
      models.add(separator);
      models.addAll(declarationModels);
    }
    models.add(addModel);
  }
}
