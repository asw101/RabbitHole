package org.alice.ide.croquet.models.html;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.LambdaExpression;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.SimpleArgument;
import org.lgna.project.ast.UserLambda;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.story.event.SceneActivationListener;

import static org.junit.Assert.*;

public class HtmlEncoderListenerLogicTest {
  @Test
  public void shouldSkipMethodReturnsTrueForGeneratedMethod() {
    assertTrue(HtmlEncoderLogic.shouldSkipMethod(method("generatedHelper", ManagementLevel.GENERATED, false)));
  }

  @Test
  public void shouldSkipMethodReturnsTrueForStaticMainMethod() {
    assertTrue(HtmlEncoderLogic.shouldSkipMethod(method("main", ManagementLevel.NONE, true)));
  }

  @Test
  public void shouldSkipMethodReturnsFalseForUserAuthoredStaticHelper() {
    assertFalse(HtmlEncoderLogic.shouldSkipMethod(method("helper", ManagementLevel.NONE, true)));
  }

  @Test
  public void getRequiredListenerArgumentReturnsFirstListenerArgument() {
    MethodInvocation invocation = new MethodInvocation();
    SimpleArgument listenerArgument = new SimpleArgument(
        new UserParameter("listener", SceneActivationListener.class),
        new LambdaExpression(new UserLambda(void.class, new UserParameter[0], new BlockStatement())));
    invocation.requiredArguments.add(listenerArgument);

    assertSame(listenerArgument, HtmlEncoderLogic.getRequiredListenerArgument(invocation));
    assertTrue(HtmlEncoderLogic.isListenerArgument(listenerArgument));
  }

  @Test
  public void getRequiredListenerArgumentIgnoresMissingOrMisnamedFirstArgument() {
    MethodInvocation emptyInvocation = new MethodInvocation();
    assertNull(HtmlEncoderLogic.getRequiredListenerArgument(emptyInvocation));

    MethodInvocation invocation = new MethodInvocation();
    invocation.requiredArguments.add(new SimpleArgument(new UserParameter("speed", Number.class), new NullLiteral()));
    invocation.requiredArguments.add(new SimpleArgument(
        new UserParameter("listener", SceneActivationListener.class),
        new LambdaExpression(new UserLambda(void.class, new UserParameter[0], new BlockStatement()))));

    assertNull(HtmlEncoderLogic.getRequiredListenerArgument(invocation));
    assertFalse(HtmlEncoderLogic.isListenerArgument(new SimpleArgument(null, new NullLiteral())));
  }

  @Test
  public void getUserLambdaReturnsUserLambdaOnlyForLambdaExpressionArguments() {
    UserLambda lambda = new UserLambda(void.class, new UserParameter[0], new BlockStatement());
    SimpleArgument listenerArgument = new SimpleArgument(
        new UserParameter("listener", SceneActivationListener.class),
        new LambdaExpression(lambda));

    assertSame(lambda, HtmlEncoderLogic.getUserLambda(listenerArgument));
    assertNull(HtmlEncoderLogic.getUserLambda(new SimpleArgument(listenerArgument.parameter.getValue(), new NullLiteral())));
    assertNull(HtmlEncoderLogic.getUserLambda(null));
  }

  private static UserMethod method(String name, ManagementLevel managementLevel, boolean isStatic) {
    UserMethod method = new UserMethod();
    method.name.setValue(name);
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.body.setValue(new BlockStatement());
    method.managementLevel.setValue(managementLevel);
    method.isStatic.setValue(isStatic);
    return method;
  }
}
