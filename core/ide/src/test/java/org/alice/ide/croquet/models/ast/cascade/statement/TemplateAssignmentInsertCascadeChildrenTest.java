package org.alice.ide.croquet.models.ast.cascade.statement;

import org.alice.ide.statementfactory.LocalArrayAtIndexAssignmentFillIn;
import org.alice.ide.statementfactory.LocalAssignmentFillIn;
import org.junit.Test;
import org.lgna.croquet.CascadeBlankChild;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.FieldModifierFinalVolatileOrNeither;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserLocal;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class TemplateAssignmentInsertCascadeChildrenTest {
  @Test
  public void buildTargetChildren_returnsCancelFillInWhenNothingIsAssignable() {
    List<CascadeBlankChild> children = TemplateAssignmentInsertCascadeLogic.buildTargetChildren(List.of(), List.of());

    assertChildren(children, NoVariablesOrFieldsAccessibleCancelFillIn.getInstance());
  }

  @Test
  public void buildTargetChildren_ordersFieldAndLocalOptionsWithArrayVariants() {
    UserField countField = createMutableField("count", Integer.TYPE, new IntegerLiteral(1));
    UserField valuesField = createMutableField("values", JavaType.getInstance(String[].class), new NullLiteral());
    UserLocal countLocal = new UserLocal("countLocal", Integer.TYPE, false);
    UserLocal valuesLocal = new UserLocal("valuesLocal", JavaType.getInstance(String[].class), false);

    List<CascadeBlankChild> children = TemplateAssignmentInsertCascadeLogic.buildTargetChildren(
        List.of(countField, valuesField),
        List.of(countLocal, valuesLocal)
    );

    assertChildren(
        children,
        FieldsSeparatorModel.getInstance(),
        FieldAssignmentFillIn.getInstance(countField),
        FieldAssignmentFillIn.getInstance(valuesField),
        FieldArrayAtIndexAssignmentFillIn.getInstance(valuesField),
        VariablesSeparatorModel.getInstance(),
        LocalAssignmentFillIn.getInstance(countLocal),
        LocalAssignmentFillIn.getInstance(valuesLocal),
        LocalArrayAtIndexAssignmentFillIn.getInstance(valuesLocal)
    );
  }

  @Test
  public void buildTargetChildren_omitsUnusedSeparators() {
    UserLocal countLocal = new UserLocal("countLocal", Integer.TYPE, false);

    List<CascadeBlankChild> children = TemplateAssignmentInsertCascadeLogic.buildTargetChildren(List.of(), List.of(countLocal));

    assertChildren(
        children,
        VariablesSeparatorModel.getInstance(),
        LocalAssignmentFillIn.getInstance(countLocal)
    );
  }

  private static UserField createMutableField(String name, Class<?> valueType, org.lgna.project.ast.Expression initializer) {
    UserField field = new UserField(name, valueType, initializer);
    field.finalVolatileOrNeither.setValue(FieldModifierFinalVolatileOrNeither.NEITHER);
    return field;
  }

  private static UserField createMutableField(String name, AbstractType<?, ?, ?> valueType, org.lgna.project.ast.Expression initializer) {
    UserField field = new UserField(name, valueType, initializer);
    field.finalVolatileOrNeither.setValue(FieldModifierFinalVolatileOrNeither.NEITHER);
    return field;
  }

  private static void assertChildren(List<CascadeBlankChild> actual, CascadeBlankChild... expected) {
    assertEquals(expected.length, actual.size());
    for (int i = 0; i < expected.length; i++) {
      assertSame("child index " + i, expected[i], actual.get(i));
    }
  }
}
