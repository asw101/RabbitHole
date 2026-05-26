package org.alice.ide.ast;

import static org.alice.ide.testing.JacocoReflectionSupport.declaredMethods;
import static org.alice.ide.testing.JacocoReflectionSupport.declaredFields;

import org.junit.Test;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.FauxExpression;
import org.lgna.project.ast.JavaType;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link IdeExpression}.
 * Deeper abstract contract testing via anonymous subclass.
 */
public class IdeExpressionCoverageTest {

  @Test
  public void isAbstractClass() {
    assertTrue(Modifier.isAbstract(IdeExpression.class.getModifiers()));
  }

  @Test
  public void extendsFauxExpression() {
    assertEquals(FauxExpression.class, IdeExpression.class.getSuperclass());
  }

  @Test
  public void isPublicClass() {
    assertTrue(Modifier.isPublic(IdeExpression.class.getModifiers()));
  }

  @Test
  public void anonymousSubclass_canBeInstantiated() {
    IdeExpression expr = new IdeExpression() {
      @Override
      public AbstractType<?, ?, ?> getType() {
        return JavaType.getInstance(String.class);
      }
    };
    assertNotNull(expr);
  }

  @Test
  public void anonymousSubclass_getTypeReturnsExpected() {
    IdeExpression expr = new IdeExpression() {
      @Override
      public AbstractType<?, ?, ?> getType() {
        return JavaType.getInstance(Integer.class);
      }
    };
    assertEquals(JavaType.getInstance(Integer.class), expr.getType());
  }

  @Test
  public void anonymousSubclass_isFauxExpression() {
    IdeExpression expr = new IdeExpression() {
      @Override
      public AbstractType<?, ?, ?> getType() {
        return null;
      }
    };
    assertTrue(expr instanceof FauxExpression);
  }

  @Test
  public void anonymousSubclass_getTypeCanReturnNull() {
    IdeExpression expr = new IdeExpression() {
      @Override
      public AbstractType<?, ?, ?> getType() {
        return null;
      }
    };
    assertNull(expr.getType());
  }

  @Test
  public void concreteSubclasses_areIdeExpressions() {
    assertTrue(IdeExpression.class.isAssignableFrom(EmptyExpression.class));
    assertTrue(IdeExpression.class.isAssignableFrom(PreviousValueExpression.class));
    assertTrue(IdeExpression.class.isAssignableFrom(SelectedInstanceFactoryExpression.class));
    assertTrue(IdeExpression.class.isAssignableFrom(CurrentThisExpression.class));
  }

  @Test
  public void declaresNoMethods() {
    assertEquals(0, declaredMethods(IdeExpression.class).length);
  }

  @Test
  public void declaresNoFields() {
    assertEquals(0, declaredFields(IdeExpression.class).length);
  }
}
