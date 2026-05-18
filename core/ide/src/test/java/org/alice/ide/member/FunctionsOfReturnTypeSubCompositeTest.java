package org.alice.ide.member;

import org.junit.Test;
import org.lgna.project.ast.JavaType;

import static org.junit.Assert.*;

public class FunctionsOfReturnTypeSubCompositeTest {

  @Test
  public void getInstance_returnsNullForNullType() {
    assertNull(FunctionsOfReturnTypeSubComposite.getInstance(null));
  }

  @Test
  public void getInstance_cachesByReturnType() {
    assertSame(
        FunctionsOfReturnTypeSubComposite.getInstance(JavaType.getInstance(String.class)),
        FunctionsOfReturnTypeSubComposite.getInstance(JavaType.getInstance(String.class)));
  }

  @Test
  public void composite_reportsRequestedReturnType() {
    FunctionsOfReturnTypeSubComposite composite = FunctionsOfReturnTypeSubComposite.getInstance(JavaType.getInstance(String.class));
    assertEquals(JavaType.getInstance(String.class), composite.getReturnType());
  }

  @Test
  public void composite_startsWithEmptyMethods() {
    FunctionsOfReturnTypeSubComposite composite = FunctionsOfReturnTypeSubComposite.getInstance(JavaType.getInstance(Integer.class));
    assertTrue(composite.getMethods().isEmpty());
  }
}
