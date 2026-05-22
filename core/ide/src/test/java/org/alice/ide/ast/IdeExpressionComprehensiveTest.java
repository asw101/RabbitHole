package org.alice.ide.ast;

import org.junit.Test;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.FauxExpression;
import org.lgna.project.ast.JavaType;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class IdeExpressionComprehensiveTest {
  @Test
  public void ideExpressionIsAbstractAndPublic() {
    assertTrue(Modifier.isAbstract(IdeExpression.class.getModifiers()));
    assertTrue(Modifier.isPublic(IdeExpression.class.getModifiers()));
  }

  @Test
  public void ideExpressionDirectSuperclassIsFauxExpression() {
    assertSame(FauxExpression.class, IdeExpression.class.getSuperclass());
  }

  @Test
  public void ideExpressionDeclaresNoFields() {
    assertEquals(0, IdeExpression.class.getDeclaredFields().length);
  }

  @Test
  public void emptyExpressionIsIdeExpression() {
    assertTrue(new EmptyExpression(String.class) instanceof IdeExpression);
  }

  @Test
  public void selectedInstanceFactoryExpressionIsIdeExpression() {
    assertTrue(new SelectedInstanceFactoryExpression(JavaType.STRING_TYPE) instanceof IdeExpression);
  }

  @Test
  public void previousValueExpressionIsIdeExpression() {
    assertTrue(new PreviousValueExpression(String.class) instanceof IdeExpression);
  }

  @Test
  public void currentThisExpressionIsIdeExpression() {
    assertTrue(new CurrentThisExpression() instanceof IdeExpression);
  }

  @Test
  public void allConcreteIdeExpressionsAreAlsoFauxExpressions() {
    assertTrue(new EmptyExpression(String.class) instanceof FauxExpression);
    assertTrue(new SelectedInstanceFactoryExpression(JavaType.STRING_TYPE) instanceof FauxExpression);
    assertTrue(new PreviousValueExpression(String.class) instanceof FauxExpression);
    assertTrue(new CurrentThisExpression() instanceof FauxExpression);
  }

  @Test
  public void allConcreteIdeExpressionsAreAlsoExpressions() {
    assertTrue(new EmptyExpression(String.class) instanceof Expression);
    assertTrue(new SelectedInstanceFactoryExpression(JavaType.STRING_TYPE) instanceof Expression);
    assertTrue(new PreviousValueExpression(String.class) instanceof Expression);
    assertTrue(new CurrentThisExpression() instanceof Expression);
  }

  @Test
  public void processThrowsForEmptyExpression() {
    assertProcessThrows(new EmptyExpression(String.class));
  }

  @Test
  public void processThrowsForSelectedInstanceFactoryExpression() {
    assertProcessThrows(new SelectedInstanceFactoryExpression(JavaType.STRING_TYPE));
  }

  @Test
  public void processThrowsForPreviousValueExpression() {
    assertProcessThrows(new PreviousValueExpression(String.class));
  }

  @Test
  public void processThrowsForCurrentThisExpression() {
    assertProcessThrows(new CurrentThisExpression());
  }

  @Test
  public void selectedInstanceFactoryExpressionReturnsRequiredType() {
    SelectedInstanceFactoryExpression expression = new SelectedInstanceFactoryExpression(JavaType.STRING_TYPE);

    assertSame(JavaType.STRING_TYPE, expression.getRequiredType());
    assertSame(JavaType.STRING_TYPE, expression.getType());
  }

  @Test
  public void previousValueExpressionClassConstructorUsesJavaType() {
    assertSame(JavaType.STRING_TYPE, new PreviousValueExpression(String.class).getType());
  }

  @Test
  public void selectedInstanceFactoryExpressionAllowsNullRequiredType() {
    assertNull(new SelectedInstanceFactoryExpression(null).getType());
  }

  @Test
  public void ideExpressionPackageMatchesAstPackage() {
    assertEquals("org.alice.ide.ast", IdeExpression.class.getPackage().getName());
  }

  @Test
  public void ideExpressionImplementsNoInterfacesDirectly() {
    assertEquals(0, IdeExpression.class.getInterfaces().length);
  }

  @Test
  public void ideExpressionDeclaresNoMethodsDirectly() {
    assertEquals(0, IdeExpression.class.getDeclaredMethods().length);
  }

  @Test
  public void ideExpressionHasSingleNoArgConstructor() {
    assertEquals(1, IdeExpression.class.getDeclaredConstructors().length);
    assertEquals(0, IdeExpression.class.getDeclaredConstructors()[0].getParameterTypes().length);
  }

  @Test
  public void concreteIdeExpressionsRemainValid() {
    assertTrue(new EmptyExpression(String.class).isValid());
    assertTrue(new SelectedInstanceFactoryExpression(JavaType.STRING_TYPE).isValid());
    assertTrue(new PreviousValueExpression(String.class).isValid());
    assertTrue(new CurrentThisExpression().isValid());
  }

  @Test
  public void ideExpressionBinaryNameMatchesExpectedValue() {
    assertEquals("org.alice.ide.ast.IdeExpression", IdeExpression.class.getName());
  }

  @Test
  public void ideExpressionSimpleNameMatchesExpectedValue() {
    assertEquals("IdeExpression", IdeExpression.class.getSimpleName());
  }

  @Test
  public void ideExpressionIsNotFinal() {
    assertFalse(Modifier.isFinal(IdeExpression.class.getModifiers()));
  }

  private static void assertProcessThrows(FauxExpression expression) {
    try {
      expression.process(null);
      fail();
    } catch (RuntimeException expected) {
    }
  }
}
