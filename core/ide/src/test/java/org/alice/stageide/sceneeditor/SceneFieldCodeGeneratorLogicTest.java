package org.alice.stageide.sceneeditor;

import org.junit.Test;
import org.lgna.project.ast.AstMethodLookupHelpers;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.DoInOrder;
import org.lgna.project.ast.DoTogether;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.Statement;
import org.lgna.project.ast.UserField;
import org.lgna.story.MutableRider;
import org.lgna.story.SThing;
import org.lgna.story.STurnable;
import org.lgna.story.SetOrientationRelativeToVehicle;

import static org.junit.Assert.*;

public class SceneFieldCodeGeneratorLogicTest {
  @Test
  public void createCurrentStateStatementMovesVehicleAssignmentAheadOfDoTogether() {
    UserField rider = new UserField("rider", SThing.class);
    Statement setVehicle = AstUtilities.createMethodInvocationStatement(new FieldAccess(rider), AstMethodLookupHelpers.lookupMethod(MutableRider.class, "setVehicle", (Class<?>) SThing.class), new org.lgna.project.ast.NullLiteral());
    Statement keep = new ExpressionStatement(new MethodInvocation(new FieldAccess(rider), JavaMethod.getInstance(Object.class, "toString")));
    BlockStatement block = new BlockStatement();
    block.statements.add(setVehicle);
    block.statements.add(keep);

    Statement currentState = SceneFieldCodeGeneratorLogic.createCurrentStateStatement(block);

    assertTrue(currentState instanceof DoInOrder);
    BlockStatement outerBody = ((DoInOrder) currentState).body.getValue();
    assertSame(setVehicle, outerBody.statements.get(0));
    assertTrue(outerBody.statements.get(1) instanceof DoTogether);
    BlockStatement innerBody = ((DoTogether) outerBody.statements.get(1)).body.getValue();
    assertEquals(1, innerBody.statements.size());
    assertSame(keep, innerBody.statements.get(0));
  }

  @Test
  public void createCurrentStateStatementUsesDoTogetherWhenNoVehicleAssignmentExists() {
    UserField rider = new UserField("rider", Object.class);
    Statement keep = new ExpressionStatement(new MethodInvocation(new FieldAccess(rider), JavaMethod.getInstance(Object.class, "toString")));
    BlockStatement block = new BlockStatement();
    block.statements.add(keep);

    Statement currentState = SceneFieldCodeGeneratorLogic.createCurrentStateStatement(block);

    assertTrue(currentState instanceof DoTogether);
    assertSame(keep, ((DoTogether) currentState).body.getValue().statements.get(0));
  }

  @Test
  public void stripCopyStateStatementsRemovesVehicleAndTransformStatements() {
    UserField rider = new UserField("rider", SThing.class);
    Statement setVehicle = AstUtilities.createMethodInvocationStatement(new FieldAccess(rider), AstMethodLookupHelpers.lookupMethod(MutableRider.class, "setVehicle", (Class<?>) SThing.class), new org.lgna.project.ast.NullLiteral());
    Statement setOrientation = AstUtilities.createMethodInvocationStatement(new FieldAccess(rider), AstMethodLookupHelpers.lookupMethod(STurnable.class, "setOrientationRelativeToVehicle", org.lgna.story.Orientation.class, SetOrientationRelativeToVehicle.Detail[].class), new org.lgna.project.ast.NullLiteral());
    Statement keep = new ExpressionStatement(new MethodInvocation(new FieldAccess(rider), JavaMethod.getInstance(Object.class, "toString")));
    BlockStatement block = new BlockStatement();
    block.statements.add(setVehicle);
    block.statements.add(setOrientation);
    block.statements.add(keep);

    SceneFieldCodeGeneratorLogic.stripCopyStateStatements(block);

    assertEquals(1, block.statements.size());
    assertSame(keep, block.statements.get(0));
  }

  @Test
  public void stripCopyStateStatementsRemovesCopyStateCallsFromNestedBodies() {
    UserField rider = new UserField("rider", Object.class);
    Statement nestedSetVehicle = AstUtilities.createMethodInvocationStatement(
        new FieldAccess(rider), JavaMethod.getInstance(CopyStateStub.class, "setVehicle", Object.class), new NullLiteral());
    Statement nestedSetPosition = AstUtilities.createMethodInvocationStatement(
        new FieldAccess(rider), JavaMethod.getInstance(CopyStateStub.class, "setPositionRelativeToVehicle", Object.class), new NullLiteral());
    Statement keep = new ExpressionStatement(new MethodInvocation(new FieldAccess(rider), JavaMethod.getInstance(Object.class, "toString")));
    DoTogether nested = new DoTogether(new BlockStatement(nestedSetVehicle, nestedSetPosition, keep));
    BlockStatement block = new BlockStatement(nested);

    SceneFieldCodeGeneratorLogic.stripCopyStateStatements(block);

    BlockStatement nestedBody = nested.body.getValue();
    assertEquals(1, nestedBody.statements.size());
    assertSame(keep, nestedBody.statements.get(0));
  }

  @Test
  public void doesSetVehicleImplyVehicleRecognizesDirectAndJointRiders() {
    UserField vehicle = new UserField("vehicle", Object.class);
    UserField rider = new UserField("rider", Object.class);
    MethodInvocation directCall = AstUtilities.createMethodInvocation(new FieldAccess(rider), AstMethodLookupHelpers.lookupMethod(MutableRider.class, "setVehicle", (Class<?>) SThing.class), new FieldAccess(vehicle));
    MethodInvocation jointCall = AstUtilities.createMethodInvocation(new FieldAccess(rider), AstMethodLookupHelpers.lookupMethod(MutableRider.class, "setVehicle", (Class<?>) SThing.class), new MethodInvocation(new FieldAccess(vehicle), JavaMethod.getInstance(Object.class, "toString")));

    assertTrue(SceneFieldCodeGeneratorLogic.doesSetVehicleImplyVehicle(directCall, vehicle));
    assertTrue(SceneFieldCodeGeneratorLogic.doesSetVehicleImplyVehicle(jointCall, vehicle));
  }

  @Test
  public void doesSetVehicleImplyVehicleRejectsInvocationsWithoutMatchingVehicleReference() {
    UserField vehicle = new UserField("vehicle", Object.class);
    UserField otherVehicle = new UserField("otherVehicle", Object.class);
    UserField rider = new UserField("rider", Object.class);
    MethodInvocation otherVehicleCall = AstUtilities.createMethodInvocation(new FieldAccess(rider), AstMethodLookupHelpers.lookupMethod(MutableRider.class, "setVehicle", (Class<?>) SThing.class), new FieldAccess(otherVehicle));
    MethodInvocation missingArgumentCall = new MethodInvocation(new FieldAccess(rider), JavaMethod.getInstance(CopyStateStub.class, "setVehicle", Object.class));

    assertFalse(SceneFieldCodeGeneratorLogic.doesSetVehicleImplyVehicle(otherVehicleCall, vehicle));
    assertFalse(SceneFieldCodeGeneratorLogic.doesSetVehicleImplyVehicle(missingArgumentCall, vehicle));
  }

  @Test
  public void asSetVehicleCallRejectsOtherInvocations() {
    UserField rider = new UserField("rider", Object.class);
    Statement statement = new ExpressionStatement(new MethodInvocation(new FieldAccess(rider), JavaMethod.getInstance(Object.class, "toString")));

    assertNull(SceneFieldCodeGeneratorLogic.asSetVehicleCall(statement));
  }

  private static final class CopyStateStub {
    public void setVehicle(Object vehicle) {
    }

    public void setPositionRelativeToVehicle(Object position) {
    }
  }
}
