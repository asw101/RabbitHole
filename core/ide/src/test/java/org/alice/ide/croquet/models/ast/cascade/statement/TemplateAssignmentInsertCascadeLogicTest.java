package org.alice.ide.croquet.models.ast.cascade.statement;

import org.junit.Test;
import org.lgna.project.ast.FieldModifierFinalVolatileOrNeither;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserLocal;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TemplateAssignmentInsertCascadeLogicTest {
  @Test
  public void getAssignableFields_filtersOutFinalFields() {
    NamedUserType type = new NamedUserType();
    UserField mutableField = new UserField("count", Integer.TYPE, new IntegerLiteral(1));
    UserField finalField = new UserField("name", String.class);
    UserField arrayField = new UserField("values", JavaType.getInstance(String[].class), new NullLiteral());
    mutableField.finalVolatileOrNeither.setValue(FieldModifierFinalVolatileOrNeither.NEITHER);
    arrayField.finalVolatileOrNeither.setValue(FieldModifierFinalVolatileOrNeither.NEITHER);
    type.fields.add(mutableField);
    type.fields.add(finalField);
    type.fields.add(arrayField);

    List<UserField> assignable = TemplateAssignmentInsertCascadeLogic.getAssignableFields(type);

    assertEquals(2, assignable.size());
    assertSame(mutableField, assignable.get(0));
    assertSame(arrayField, assignable.get(1));
  }

  @Test
  public void getAssignableLocals_filtersOutFinalLocals() {
    UserLocal mutableLocal = new UserLocal("count", Integer.TYPE, false);
    UserLocal finalLocal = new UserLocal("name", String.class, true);
    UserLocal arrayLocal = new UserLocal("values", JavaType.getInstance(String[].class), false);

    List<UserLocal> assignable = TemplateAssignmentInsertCascadeLogic.getAssignableLocals(List.of(mutableLocal, finalLocal, arrayLocal));

    assertEquals(2, assignable.size());
    assertSame(mutableLocal, assignable.get(0));
    assertSame(arrayLocal, assignable.get(1));
  }

  @Test
  public void hasAssignableTargets_requiresAtLeastOneFieldOrLocal() {
    assertFalse(TemplateAssignmentInsertCascadeLogic.hasAssignableTargets(List.of(), List.of()));
    assertTrue(TemplateAssignmentInsertCascadeLogic.hasAssignableTargets(List.of(new UserField()), List.of()));
    assertTrue(TemplateAssignmentInsertCascadeLogic.hasAssignableTargets(List.of(), List.of(new UserLocal())));
  }
}
