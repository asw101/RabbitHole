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
import org.lgna.project.code.ProcessableNode;
import org.lgna.story.event.SceneActivationListener;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class HtmlEncoderListenerLogicTest {
  @Test
  public void constructorThrowsAssertionError() throws Exception {
    java.lang.reflect.Constructor<HtmlEncoderLogic> constructor = HtmlEncoderLogic.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    try {
      constructor.newInstance();
      fail("Expected AssertionError");
    } catch (java.lang.reflect.InvocationTargetException exception) {
      assertTrue(exception.getCause() instanceof AssertionError);
    }
  }

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
  public void shouldSkipMethodReturnsFalseForInstanceMainMethod() {
    assertFalse(HtmlEncoderLogic.shouldSkipMethod(method("main", ManagementLevel.NONE, false)));
  }

  @Test
  public void isClassEmptyTreatsNonMethodItemsAsContent() {
    Map<String, List<ProcessableNode>> sections = Map.of("procedures", List.of(new NullLiteral()));

    assertFalse(HtmlEncoderLogic.isClassEmpty(sections, Set.of()));
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
    assertNull(HtmlEncoderLogic.getRequiredListenerArgument(null));

    MethodInvocation emptyInvocation = new MethodInvocation();
    assertNull(HtmlEncoderLogic.getRequiredListenerArgument(emptyInvocation));

    MethodInvocation invocation = new MethodInvocation();
    invocation.requiredArguments.add(new SimpleArgument(new UserParameter("speed", Number.class), new NullLiteral()));
    invocation.requiredArguments.add(new SimpleArgument(
        new UserParameter("listener", SceneActivationListener.class),
        new LambdaExpression(new UserLambda(void.class, new UserParameter[0], new BlockStatement()))));

    assertNull(HtmlEncoderLogic.getRequiredListenerArgument(invocation));
    assertFalse(HtmlEncoderLogic.isListenerArgument(new SimpleArgument(null, new NullLiteral())));
    assertFalse(HtmlEncoderLogic.isListenerArgument(new SimpleArgument(new UserParameter("callback", SceneActivationListener.class), new NullLiteral())));
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
