package org.alice.stageide.oneshot;

import org.alice.stageide.ast.sort.OneShotSorter;
import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

public class MethodInvocationBlankLogicEdgeTest {
  @Test
  public void getPoseInvocationRejectsNullNonGeneratedAndEmptyMethods() {
    assertThrows(NullPointerException.class, () -> MethodInvocationBlankLogic.getPoseInvocation(null));
    assertNull(MethodInvocationBlankLogic.getPoseInvocation(new UserMethod("pose", void.class, new UserParameter[0], new BlockStatement())));

    UserMethod generatedVoidMethod = newGeneratedVoidMethod();
    assertNull(MethodInvocationBlankLogic.getPoseInvocation(generatedVoidMethod));
  }

  @Test
  public void getPoseInvocationRejectsWrongMethodKinds() {
    UserMethod nonVoidMethod = new UserMethod("pose", String.class, new UserParameter[0], new BlockStatement());
    nonVoidMethod.managementLevel.setValue(ManagementLevel.GENERATED);
    assertNull(MethodInvocationBlankLogic.getPoseInvocation(nonVoidMethod));

    UserMethod wrongNameInvocation = newGeneratedVoidMethod();
    wrongNameInvocation.body.getValue().statements.add(new ExpressionStatement(
        new MethodInvocation(new ThisExpression(), JavaType.getInstance(Object.class).getDeclaredMethod("toString"))));
    assertNull(MethodInvocationBlankLogic.getPoseInvocation(wrongNameInvocation));

    UserMethod userDefinedStrikePose = new UserMethod("strikePose", void.class, new UserParameter[0], new BlockStatement());
    UserMethod userDefinedInvocation = newGeneratedVoidMethod();
    userDefinedInvocation.body.getValue().statements.add(new ExpressionStatement(
        new MethodInvocation(new ThisExpression(), userDefinedStrikePose)));
    assertNull(MethodInvocationBlankLogic.getPoseInvocation(userDefinedInvocation));
  }

  @Test
  public void resolveFillInKindCoversRemainingBranchesAndPrecedenceRules() {
    assertEquals(MethodInvocationBlankLogic.FillInKind.STRAIGHTEN_OUT_JOINTS,
        MethodInvocationBlankLogic.resolveFillInKind(OneShotSorter.STRAIGHTEN_OUT_JOINTS_METHOD, false));
    assertEquals(MethodInvocationBlankLogic.FillInKind.SET_PAINT,
        MethodInvocationBlankLogic.resolveFillInKind(OneShotSorter.MODEL_SET_PAINT_METHOD, true));
    assertEquals(MethodInvocationBlankLogic.FillInKind.ROOM,
        MethodInvocationBlankLogic.resolveFillInKind(OneShotSorter.MODEL_SET_OPACITY_METHOD, true));
    assertEquals(MethodInvocationBlankLogic.FillInKind.SET_OPACITY,
        MethodInvocationBlankLogic.resolveFillInKind(OneShotSorter.MODEL_SET_OPACITY_METHOD, false));
    assertEquals(MethodInvocationBlankLogic.FillInKind.JAVA_DEFINED_STRIKE_POSE,
        MethodInvocationBlankLogic.resolveFillInKind(OneShotSorter.SPREAD_WINGS_METHOD, false));
    assertEquals(MethodInvocationBlankLogic.FillInKind.LOCAL_TRANSFORMATION,
        MethodInvocationBlankLogic.resolveFillInKind(JavaType.getInstance(Object.class).getDeclaredMethod("hashCode"), false));
  }

  private static UserMethod newGeneratedVoidMethod() {
    UserMethod method = new UserMethod("pose", void.class, new UserParameter[0], new BlockStatement());
    method.managementLevel.setValue(ManagementLevel.GENERATED);
    return method;
  }
}
