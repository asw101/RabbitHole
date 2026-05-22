package org.alice.ide.ast;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for {@link CurrentThisExpression} covering construction and types.
 */
public class CurrentThisExpressionBranchCoverageTest {

  @Test
  public void construct_default_succeeds() {
    CurrentThisExpression expr = new CurrentThisExpression();
    assertNotNull(expr);
  }

  @Test
  public void isInstanceOfIdeExpression() {
    CurrentThisExpression expr = new CurrentThisExpression();
    assertTrue(expr instanceof IdeExpression);
  }

  @Test
  public void isInstanceOfExpression() {
    CurrentThisExpression expr = new CurrentThisExpression();
    assertTrue(expr instanceof Expression);
  }

  @Test
  public void twoInstances_areDifferent() {
    CurrentThisExpression e1 = new CurrentThisExpression();
    CurrentThisExpression e2 = new CurrentThisExpression();
    assertNotSame(e1, e2);
  }

  @Test
  public void className_isCurrentThisExpression() {
    assertEquals("CurrentThisExpression", CurrentThisExpression.class.getSimpleName());
  }

  @Test
  public void packageName_isCorrect() {
    assertEquals("org.alice.ide.ast", CurrentThisExpression.class.getPackage().getName());
  }

  @Test
  public void multipleConstructions_succeed() {
    for (int i = 0; i < 10; i++) {
      CurrentThisExpression expr = new CurrentThisExpression();
      assertNotNull("Construction " + i, expr);
    }
  }

  @Test
  public void isNotInstanceOfEmptyExpression() {
    assertFalse(EmptyExpression.class.isAssignableFrom(CurrentThisExpression.class));
  }

  @Test
  public void classIsNotAbstract() {
    assertFalse(java.lang.reflect.Modifier.isAbstract(CurrentThisExpression.class.getModifiers()));
  }

  @Test
  public void classIsPublic() {
    assertTrue(java.lang.reflect.Modifier.isPublic(CurrentThisExpression.class.getModifiers()));
  }

  @Test
  public void extendsIdeExpression() {
    assertTrue(IdeExpression.class.isAssignableFrom(CurrentThisExpression.class));
  }

  @Test
  public void extendsExpression_transitively() {
    assertTrue(Expression.class.isAssignableFrom(CurrentThisExpression.class));
  }
}
