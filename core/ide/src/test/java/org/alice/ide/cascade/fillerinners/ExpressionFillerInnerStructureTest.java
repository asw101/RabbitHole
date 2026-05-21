package org.alice.ide.cascade.fillerinners;

import org.junit.Test;
import org.lgna.croquet.CascadeBlankChild;
import org.lgna.project.annotations.ValueDetails;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.JavaType;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.junit.Assert.*;

public class ExpressionFillerInnerStructureTest {
  private static final class TestFillerInner extends ExpressionFillerInner {
    private TestFillerInner(Class<?> cls) {
      super(cls);
    }

    @Override
    public void appendItems(List<CascadeBlankChild> items, ValueDetails<?> details, boolean isTop, Expression prevExpression) {
    }
  }

  @Test
  public void classIsPublicAbstract() {
    assertTrue(Modifier.isPublic(ExpressionFillerInner.class.getModifiers()));
    assertTrue(Modifier.isAbstract(ExpressionFillerInner.class.getModifiers()));
  }

  @Test
  public void constructorUsingClassCreatesExpectedTypeRelationship() {
    TestFillerInner fillerInner = new TestFillerInner(String.class);

    assertTrue(fillerInner.isAssignableTo(JavaType.getInstance(Object.class)));
    assertFalse(fillerInner.isAssignableTo(JavaType.getInstance(Number.class)));
  }

  @Test
  public void appendItemsContractUsesExpectedParameters() throws Exception {
    Method method = ExpressionFillerInner.class.getMethod(
        "appendItems", List.class, ValueDetails.class, boolean.class, Expression.class);

    assertEquals(void.class, method.getReturnType());
    assertTrue(Modifier.isAbstract(method.getModifiers()));
  }
}
