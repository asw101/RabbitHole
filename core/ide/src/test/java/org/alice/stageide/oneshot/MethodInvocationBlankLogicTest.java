package org.alice.stageide.oneshot;

import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.UserMethod;
import org.junit.Test;

import static org.junit.Assert.*;

public class MethodInvocationBlankLogicTest {

  @SuppressWarnings("unused")
  static class MethodNameStubs {
    public void setPaint(Object p) {}
    public void setOpacity(Number n) {}
    public void foldWings() {}
    public void spreadWings() {}
    public void move(Object dir, Number amount) {}
    public void strikePose(Object pose) {}
  }

  private JavaMethod stubMethod(String name, Class<?>... params) {
    try {
      return JavaMethod.getInstance(MethodNameStubs.class.getDeclaredMethod(name, params));
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void resolveFillInKindReturnSetPaint() {
    JavaMethod method = stubMethod("setPaint", Object.class);
    assertEquals(MethodInvocationBlankLogic.FillInKind.SET_PAINT,
        MethodInvocationBlankLogic.resolveFillInKind(method, false));
  }

  @Test
  public void resolveFillInKindReturnSetOpacity() {
    JavaMethod method = stubMethod("setOpacity", Number.class);
    assertEquals(MethodInvocationBlankLogic.FillInKind.SET_OPACITY,
        MethodInvocationBlankLogic.resolveFillInKind(method, false));
  }

  @Test
  public void resolveFillInKindReturnRoomWhenHasRoomFillIn() {
    JavaMethod method = stubMethod("setOpacity", Number.class);
    assertEquals(MethodInvocationBlankLogic.FillInKind.ROOM,
        MethodInvocationBlankLogic.resolveFillInKind(method, true));
  }

  @Test
  public void resolveFillInKindReturnLocalTransformationAsDefault() {
    JavaMethod method = stubMethod("move", Object.class, Number.class);
    assertEquals(MethodInvocationBlankLogic.FillInKind.LOCAL_TRANSFORMATION,
        MethodInvocationBlankLogic.resolveFillInKind(method, false));
  }

  @Test
  public void resolveFillInKindReturnFoldWings() {
    JavaMethod method = stubMethod("foldWings");
    assertEquals(MethodInvocationBlankLogic.FillInKind.JAVA_DEFINED_STRIKE_POSE,
        MethodInvocationBlankLogic.resolveFillInKind(method, false));
  }

  @Test
  public void resolveFillInKindReturnSpreadWings() {
    JavaMethod method = stubMethod("spreadWings");
    assertEquals(MethodInvocationBlankLogic.FillInKind.JAVA_DEFINED_STRIKE_POSE,
        MethodInvocationBlankLogic.resolveFillInKind(method, false));
  }

  @Test
  public void getPoseInvocationReturnsNullForNonGenerated() {
    UserMethod method = new UserMethod();
    method.managementLevel.setValue(ManagementLevel.NONE);
    assertNull(MethodInvocationBlankLogic.getPoseInvocation(method));
  }

  @Test
  public void getPoseInvocationReturnsNullForEmptyBody() {
    UserMethod method = new UserMethod();
    method.managementLevel.setValue(ManagementLevel.GENERATED);
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.body.setValue(new BlockStatement());
    assertNull(MethodInvocationBlankLogic.getPoseInvocation(method));
  }

  @Test
  public void getPoseInvocationReturnsNullForNonStrikePose() {
    UserMethod method = new UserMethod();
    method.managementLevel.setValue(ManagementLevel.GENERATED);
    method.returnType.setValue(JavaType.VOID_TYPE);
    BlockStatement body = new BlockStatement();
    MethodInvocation mi = new MethodInvocation();
    mi.method.setValue(stubMethod("move", Object.class, Number.class));
    body.statements.add(new ExpressionStatement(mi));
    method.body.setValue(body);
    assertNull(MethodInvocationBlankLogic.getPoseInvocation(method));
  }

  @Test
  public void getPoseInvocationReturnsInvocationForStrikePose() {
    UserMethod method = new UserMethod();
    method.managementLevel.setValue(ManagementLevel.GENERATED);
    method.returnType.setValue(JavaType.VOID_TYPE);
    BlockStatement body = new BlockStatement();
    MethodInvocation mi = new MethodInvocation();
    mi.method.setValue(stubMethod("strikePose", Object.class));
    body.statements.add(new ExpressionStatement(mi));
    method.body.setValue(body);
    MethodInvocation result = MethodInvocationBlankLogic.getPoseInvocation(method);
    assertNotNull(result);
    assertEquals("strikePose", result.method.getValue().getName());
  }
}
