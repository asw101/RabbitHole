package org.alice.ide.member;

import org.junit.Test;
import org.lgna.project.ast.JavaMethod;

import static org.junit.Assert.*;

public class FilteredMethodsSubCompositeTest {

  @Test
  public void compareMethodNames_bothNull_returnsZero() {
    assertEquals(0, FilteredMethodsSubComposite.compareMethodNames(null, null));
  }

  @Test
  public void compareMethodNames_firstNull_returnsNegative() {
    JavaMethod method = MemberTestHelper.methodNamed(Object.class, "toString");
    if (method != null) {
      assertTrue(FilteredMethodsSubComposite.compareMethodNames(null, method) < 0);
    }
  }

  @Test
  public void compareMethodNames_secondNull_returnsPositive() {
    JavaMethod method = MemberTestHelper.methodNamed(Object.class, "toString");
    if (method != null) {
      assertTrue(FilteredMethodsSubComposite.compareMethodNames(method, null) > 0);
    }
  }

  @Test
  public void compareMethodNames_sameMethod_returnsZero() {
    JavaMethod method = MemberTestHelper.methodNamed(Object.class, "toString");
    if (method != null) {
      assertEquals(0, FilteredMethodsSubComposite.compareMethodNames(method, method));
    }
  }

  @Test
  public void compareMethodNames_alphabeticalOrder() {
    JavaMethod toString = MemberTestHelper.methodNamed(Object.class, "toString");
    JavaMethod hashCode = MemberTestHelper.methodNamed(Object.class, "hashCode");
    if (toString != null && hashCode != null) {
      assertTrue(FilteredMethodsSubComposite.compareMethodNames(hashCode, toString) < 0);
      assertTrue(FilteredMethodsSubComposite.compareMethodNames(toString, hashCode) > 0);
    }
  }
}
