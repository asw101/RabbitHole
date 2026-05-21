package org.alice.ide.cascade.fillerinners;

import org.junit.Test;
import org.lgna.project.ast.JavaType;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.junit.Assert.*;

public class PoseFillerInnerStructureTest {
  @Test
  public void classExtendsExpressionFillerInner() {
    assertEquals(ExpressionFillerInner.class, PoseFillerInner.class.getSuperclass());
    assertTrue(Modifier.isPublic(PoseFillerInner.class.getModifiers()));
  }

  @Test
  public void constructorConfiguresPoseAsObjectAssignableType() {
    PoseFillerInner fillerInner = new PoseFillerInner();
    assertTrue(fillerInner.isAssignableTo(JavaType.getInstance(Object.class)));
  }

  @Test
  public void appendItemsMethodMatchesExpectedSignature() throws Exception {
    Method method = PoseFillerInner.class.getMethod(
        "appendItems", List.class, org.lgna.project.annotations.ValueDetails.class, boolean.class, org.lgna.project.ast.Expression.class);
    assertEquals(void.class, method.getReturnType());
  }
}
