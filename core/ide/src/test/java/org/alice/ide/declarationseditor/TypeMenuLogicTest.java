package org.alice.ide.declarationseditor;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TypeMenuLogicTest {
  private static final String TYPE = "TYPE";
  private static final String SEPARATOR = "|";
  private static final String PROCEDURES_SEPARATOR = "PROCEDURES";
  private static final String FUNCTIONS_SEPARATOR = "FUNCTIONS";
  private static final String MANAGED_FIELDS_SEPARATOR = "MANAGED_FIELDS";
  private static final String UNMANAGED_FIELDS_SEPARATOR = "UNMANAGED_FIELDS";
  private static final String FIELDS_SEPARATOR = "FIELDS";
  private static final String ADD_PROCEDURE = "ADD_PROCEDURE";
  private static final String ADD_FUNCTION = "ADD_FUNCTION";
  private static final String ADD_MANAGED_FIELD = "ADD_MANAGED_FIELD";
  private static final String ADD_UNMANAGED_FIELD = "ADD_UNMANAGED_FIELD";

  @Test
  public void buildsConstructorProcedureFunctionAndFieldSectionsInOriginalOrder() {
    List<String> models = TypeMenuLogic.buildMenuModels(
        TYPE,
        true,
        Arrays.asList("CTOR_A", "CTOR_B"),
        Collections.singletonList("procedure"),
        PROCEDURES_SEPARATOR,
        ADD_PROCEDURE,
        Collections.singletonList("function"),
        FUNCTIONS_SEPARATOR,
        ADD_FUNCTION,
        false,
        Collections.emptyList(),
        MANAGED_FIELDS_SEPARATOR,
        ADD_MANAGED_FIELD,
        Collections.singletonList("field"),
        UNMANAGED_FIELDS_SEPARATOR,
        FIELDS_SEPARATOR,
        ADD_UNMANAGED_FIELD,
        SEPARATOR);

    assertEquals(Arrays.asList(
        TYPE,
        SEPARATOR, "CTOR_A", "CTOR_B",
        SEPARATOR, PROCEDURES_SEPARATOR, "procedure", ADD_PROCEDURE,
        SEPARATOR, FUNCTIONS_SEPARATOR, "function", ADD_FUNCTION,
        SEPARATOR, FIELDS_SEPARATOR, "field", ADD_UNMANAGED_FIELD), models);
  }

  @Test
  public void managedTypesIncludeManagedSectionBeforeUnmanagedSection() {
    List<String> models = TypeMenuLogic.buildMenuModels(
        TYPE,
        false,
        Collections.emptyList(),
        Collections.emptyList(),
        PROCEDURES_SEPARATOR,
        ADD_PROCEDURE,
        Collections.emptyList(),
        FUNCTIONS_SEPARATOR,
        ADD_FUNCTION,
        true,
        Collections.singletonList("managed"),
        MANAGED_FIELDS_SEPARATOR,
        ADD_MANAGED_FIELD,
        Collections.singletonList("unmanaged"),
        UNMANAGED_FIELDS_SEPARATOR,
        FIELDS_SEPARATOR,
        ADD_UNMANAGED_FIELD,
        SEPARATOR);

    assertEquals(Arrays.asList(
        TYPE,
        SEPARATOR, ADD_PROCEDURE,
        SEPARATOR, ADD_FUNCTION,
        SEPARATOR, MANAGED_FIELDS_SEPARATOR, "managed", ADD_MANAGED_FIELD,
        SEPARATOR, UNMANAGED_FIELDS_SEPARATOR, "unmanaged", ADD_UNMANAGED_FIELD), models);
  }

  @Test
  public void managedFieldPresenceStillTriggersUnmanagedSeparatorEvenWithoutUnmanagedFields() {
    List<String> models = TypeMenuLogic.buildMenuModels(
        TYPE,
        false,
        Collections.emptyList(),
        Collections.emptyList(),
        PROCEDURES_SEPARATOR,
        ADD_PROCEDURE,
        Collections.emptyList(),
        FUNCTIONS_SEPARATOR,
        ADD_FUNCTION,
        true,
        Collections.singletonList("managed"),
        MANAGED_FIELDS_SEPARATOR,
        ADD_MANAGED_FIELD,
        Collections.emptyList(),
        UNMANAGED_FIELDS_SEPARATOR,
        FIELDS_SEPARATOR,
        ADD_UNMANAGED_FIELD,
        SEPARATOR);

    assertEquals(Arrays.asList(
        TYPE,
        SEPARATOR, ADD_PROCEDURE,
        SEPARATOR, ADD_FUNCTION,
        SEPARATOR, MANAGED_FIELDS_SEPARATOR, "managed", ADD_MANAGED_FIELD,
        SEPARATOR, UNMANAGED_FIELDS_SEPARATOR, ADD_UNMANAGED_FIELD), models);
  }
}
