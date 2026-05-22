package org.alice.stageide.sceneeditor;

import org.junit.Test;
import org.lgna.project.ast.*;
import org.lgna.story.MutableRider;
import org.lgna.story.SBiped;
import org.lgna.story.SThing;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class SceneFieldCodeGeneratorExtendedTest {
  @Test
  public void replaceReferencesInExpressionRewritesEveryMatchingFieldAccess() throws Exception {
    SceneFieldCodeGenerator generator = new SceneFieldCodeGenerator(null);
    UserField source = createField("source", SBiped.class);
    UserField replacement = createField("replacement", SBiped.class);

    ExpressionStatement statement = createSetVehicleStatement(source, source);
    Statement updated = invokeReplaceReferences(generator, source, replacement, statement);
    MethodInvocation invocation = (MethodInvocation) ((ExpressionStatement) updated).expression.getValue();

    assertSame(statement, updated);
    assertSame(replacement, ((FieldAccess) invocation.expression.getValue()).field.getValue());
    assertSame(replacement, ((FieldAccess) invocation.requiredArguments.get(0).expression.getValue()).field.getValue());
  }

  @Test
  public void replaceReferencesInExpressionLeavesUnrelatedFieldAccessAlone() throws Exception {
    SceneFieldCodeGenerator generator = new SceneFieldCodeGenerator(null);
    UserField source = createField("source", SBiped.class);
    UserField replacement = createField("replacement", SBiped.class);
    UserField other = createField("other", SBiped.class);

    ExpressionStatement statement = createSetVehicleStatement(source, other);
    MethodInvocation invocation = (MethodInvocation) ((ExpressionStatement) invokeReplaceReferences(generator, source, replacement, statement)).expression.getValue();

    assertSame(replacement, ((FieldAccess) invocation.expression.getValue()).field.getValue());
    assertSame(other, ((FieldAccess) invocation.requiredArguments.get(0).expression.getValue()).field.getValue());
  }

  @Test
  public void asSetVehicleCallReturnsInvocationForVehicleSetter() {
    UserField rider = createField("rider", SBiped.class);

    MethodInvocation invocation = SceneFieldCodeGenerator.asSetVehicleCall(createSetVehicleStatement(rider, rider));

    assertNotNull(invocation);
    assertEquals("setVehicle", invocation.method.getValue().getName());
  }

  @Test
  public void asSetVehicleCallReturnsNullForOtherExpressions() {
    UserField rider = createField("rider", SBiped.class);
    JavaMethod toStringMethod = JavaMethod.getInstance(Object.class, "toString");
    ExpressionStatement statement = new ExpressionStatement(new MethodInvocation(new FieldAccess(rider), toStringMethod));

    assertNull(SceneFieldCodeGenerator.asSetVehicleCall(statement));
    assertFalse(SceneFieldCodeGenerator.isSetVehicleInvocation(statement));
  }

  @Test
  public void asSetVehicleCallReturnsNullForNonExpressionStatements() {
    assertNull(SceneFieldCodeGenerator.asSetVehicleCall(new BlockStatement()));
    assertFalse(SceneFieldCodeGenerator.isSetVehicleInvocation(new BlockStatement()));
  }

  @Test
  public void getUndoStatementsForAddFieldCreatesSingleNullVehicleReset() {
    SceneFieldCodeGenerator generator = new SceneFieldCodeGenerator(null);
    UserField rider = createField("rider", SBiped.class);

    Statement[] undoStatements = generator.getUndoStatementsForAddField(rider);

    assertEquals(1, undoStatements.length);
    MethodInvocation invocation = SceneFieldCodeGenerator.asSetVehicleCall(undoStatements[0]);
    assertNotNull(invocation);
    assertTrue(invocation.requiredArguments.get(0).expression.getValue() instanceof NullLiteral);
  }

  @Test
  public void getDoStatementsForRemoveFieldMovesRidersToSceneThenClearsFieldVehicle() {
    SceneFieldCodeGenerator generator = new SceneFieldCodeGenerator(null);
    UserField removed = createField("removed", SBiped.class);
    UserField riderOne = createField("riderOne", SBiped.class);
    UserField riderTwo = createField("riderTwo", SBiped.class);
    Map<AbstractField, Statement> riders = new LinkedHashMap<>();
    riders.put(riderOne, createSetVehicleStatement(riderOne, removed));
    riders.put(riderTwo, createSetVehicleStatement(riderTwo, removed));

    Statement[] statements = generator.getDoStatementsForRemoveField(removed, riders);

    assertEquals(3, statements.length);
    assertVehicleInvocation(statements[0], riderOne, ThisExpression.class);
    assertVehicleInvocation(statements[1], riderTwo, ThisExpression.class);
    assertVehicleInvocation(statements[2], removed, NullLiteral.class);
  }

  @Test
  public void getDoStatementsForRemoveFieldWithoutRidersOnlyClearsRemovedField() {
    SceneFieldCodeGenerator generator = new SceneFieldCodeGenerator(null);
    UserField removed = createField("removed", SBiped.class);

    Statement[] statements = generator.getDoStatementsForRemoveField(removed, new LinkedHashMap<>());

    assertEquals(1, statements.length);
    assertVehicleInvocation(statements[0], removed, NullLiteral.class);
  }

  private static Statement invokeReplaceReferences(SceneFieldCodeGenerator generator, UserField fieldToReplace,
      UserField replacement, Statement statement) throws Exception {
    Method method = SceneFieldCodeGenerator.class.getDeclaredMethod(
        "replaceReferencesInExpression", UserField.class, UserField.class, Statement.class);
    method.setAccessible(true);
    return (Statement) method.invoke(generator, fieldToReplace, replacement, statement);
  }

  private static ExpressionStatement createSetVehicleStatement(UserField rider, AbstractField vehicle) {
    AbstractMethod setVehicle = AstMethodLookupHelpers.lookupMethod(MutableRider.class, "setVehicle", (Class<?>) SThing.class);
    return AstUtilities.createMethodInvocationStatement(new FieldAccess(rider), setVehicle, new FieldAccess(vehicle));
  }

  private static void assertVehicleInvocation(Statement statement, AbstractField expectedTargetField,
      Class<? extends Expression> expectedArgumentType) {
    MethodInvocation invocation = SceneFieldCodeGenerator.asSetVehicleCall(statement);
    assertNotNull(invocation);
    assertSame(expectedTargetField, ((FieldAccess) invocation.expression.getValue()).field.getValue());
    assertTrue(expectedArgumentType.isInstance(invocation.requiredArguments.get(0).expression.getValue()));
  }

  private static UserField createField(String name, Class<?> type) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.getInstance(type));
    return field;
  }
}
