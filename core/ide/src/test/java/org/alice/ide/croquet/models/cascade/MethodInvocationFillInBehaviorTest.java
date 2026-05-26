package org.alice.ide.croquet.models.cascade;

import org.alice.ide.ast.EmptyExpression;
import org.junit.Test;
import org.lgna.croquet.CascadeBlank;
import org.lgna.project.ast.AbstractMethod;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.TypeExpression;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class MethodInvocationFillInBehaviorTest {
  @Test
  public void staticMethodInvocationFillInCachesByMethodAndBuildsStaticInvocations() {
    JavaMethod method = JavaMethod.getInstance(Math.class, "max", Integer.TYPE, Integer.TYPE);
    StaticMethodInvocationFillIn fillIn = StaticMethodInvocationFillIn.getInstance(method);

    assertSame(fillIn, StaticMethodInvocationFillIn.getInstance(Math.class, "max", Integer.TYPE, Integer.TYPE));

    List<? extends CascadeBlank<Expression>> blanks = fillIn.getBlanks();
    assertEquals(2, blanks.size());
    assertSame(ParameterBlank.getInstance(method.getRequiredParameters().get(0)), blanks.get(0));
    assertSame(ParameterBlank.getInstance(method.getRequiredParameters().get(1)), blanks.get(1));

    MethodInvocation transientInvocation = fillIn.getTransientValue(null);
    assertTrue(transientInvocation.expression.getValue() instanceof TypeExpression);

    MethodInvocation created = invokeCreateValue(fillIn, new IntegerLiteral(4), new IntegerLiteral(9));
    assertTrue(created.expression.getValue() instanceof TypeExpression);
    assertSame(method, created.method.getValue());
    assertEquals(2, created.requiredArguments.size());
    assertEquals(4, ((IntegerLiteral) created.requiredArguments.get(0).expression.getValue()).value.getValue().intValue());
    assertEquals(9, ((IntegerLiteral) created.requiredArguments.get(1).expression.getValue()).value.getValue().intValue());
  }

  @Test
  public void instanceMethodInvocationFillInAddsReceiverBlankAndUsesProvidedReceiver() {
    JavaMethod method = JavaMethod.getInstance(String.class, "substring", Integer.TYPE, Integer.TYPE);
    MethodInvocationFillInWithInstance fillIn = MethodInvocationFillInWithInstance.getInstance(method);

    assertSame(fillIn, MethodInvocationFillInWithInstance.getInstance(method));

    List<? extends CascadeBlank<Expression>> blanks = fillIn.getBlanks();
    assertEquals(3, blanks.size());
    assertTrue(blanks.get(0) instanceof ExpressionBlank);
    assertSame(JavaType.getInstance(String.class), ((ExpressionBlank) blanks.get(0)).getValueType());
    assertSame(ParameterBlank.getInstance(method.getRequiredParameters().get(0)), blanks.get(1));
    assertSame(ParameterBlank.getInstance(method.getRequiredParameters().get(1)), blanks.get(2));

    MethodInvocation transientInvocation = fillIn.getTransientValue(null);
    assertTrue(transientInvocation.expression.getValue() instanceof EmptyExpression);
    assertSame(JavaType.getInstance(String.class), transientInvocation.expression.getValue().getType());

    StringLiteral receiver = new StringLiteral("alphabet");
    MethodInvocation created = invokeCreateValue(fillIn, receiver, new IntegerLiteral(1), new IntegerLiteral(5));
    assertSame(receiver, created.expression.getValue());
    assertSame(method, created.method.getValue());
    assertEquals(2, created.requiredArguments.size());
    assertEquals(1, ((IntegerLiteral) created.requiredArguments.get(0).expression.getValue()).value.getValue().intValue());
    assertEquals(5, ((IntegerLiteral) created.requiredArguments.get(1).expression.getValue()).value.getValue().intValue());
  }

  @Test
  public void methodInvocationFillInTracksUnlockedSignaturesButCachesLockedOnes() {
    UserMethod unlockedMethod = new UserMethod();
    UserParameter unlockedFirst = new UserParameter("count", Integer.class);
    unlockedMethod.requiredParameters.add(unlockedFirst);
    TestMethodInvocationFillIn unlockedFillIn = new TestMethodInvocationFillIn(new ThisExpression(), unlockedMethod);

    assertEquals(1, unlockedFillIn.getBlanks().size());
    UserParameter unlockedSecond = new UserParameter("label", String.class);
    unlockedMethod.requiredParameters.add(unlockedSecond);
    assertEquals(2, unlockedFillIn.getBlanks().size());
    assertSame(ParameterBlank.getInstance(unlockedSecond), unlockedFillIn.getBlanks().get(1));

    UserMethod lockedMethod = new UserMethod();
    UserParameter lockedFirst = new UserParameter("value", Integer.class);
    lockedMethod.requiredParameters.add(lockedFirst);
    lockedMethod.isSignatureLocked.setValue(true);
    TestMethodInvocationFillIn lockedFillIn = new TestMethodInvocationFillIn(new ThisExpression(), lockedMethod);

    assertEquals(1, lockedFillIn.getBlanks().size());
    lockedMethod.requiredParameters.add(new UserParameter("ignored", Double.class));
    assertEquals(1, lockedFillIn.getBlanks().size());
    assertSame(ParameterBlank.getInstance(lockedFirst), lockedFillIn.getBlanks().get(0));
  }

  @Test
  public void methodInvocationFillInReusesItsConfiguredReceiverExpression() {
    UserMethod method = new UserMethod();
    method.requiredParameters.add(new UserParameter("label", String.class));
    ThisExpression receiver = new ThisExpression();
    TestMethodInvocationFillIn fillIn = new TestMethodInvocationFillIn(receiver, method);

    MethodInvocation transientInvocation = fillIn.getTransientValue(null);
    assertSame(receiver, transientInvocation.expression.getValue());

    StringLiteral argument = new StringLiteral("hello");
    MethodInvocation created = fillIn.compute(argument);

    assertSame(receiver, created.expression.getValue());
    assertSame(method, created.method.getValue());
    assertEquals(1, created.requiredArguments.size());
    assertSame(argument, created.requiredArguments.get(0).expression.getValue());
  }

  private static MethodInvocation invokeCreateValue(Object fillIn, Expression... expressions) {
    try {
      java.lang.reflect.Method method = fillIn.getClass().getDeclaredMethod("createValue", Expression[].class);
      method.setAccessible(true);
      return (MethodInvocation) method.invoke(fillIn, new Object[]{expressions});
    } catch (ReflectiveOperationException e) {
      throw new AssertionError(e);
    }
  }

  private static final class TestMethodInvocationFillIn extends MethodInvocationFillIn {
    private TestMethodInvocationFillIn(Expression transientValueExpression, AbstractMethod method) {
      super(UUID.fromString("2f43a546-83b3-4c5d-a45d-0d5b3a091c72"), transientValueExpression, method);
    }

    @Override
    protected Expression createExpression(Expression transientValueExpression) {
      return transientValueExpression;
    }

    private MethodInvocation compute(Expression... arguments) {
      return this.createValue(arguments);
    }
  }
}
