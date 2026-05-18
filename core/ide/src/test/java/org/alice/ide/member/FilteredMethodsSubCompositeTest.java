package org.alice.ide.member;

import org.junit.Test;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class FilteredMethodsSubCompositeTest {

  @Test
  public void compareMethodNames_bothNull_returnsZero() {
    assertEquals(0, FilteredMethodsSubComposite.compareMethodNames(null, null));
  }

  @Test
  public void compareMethodNames_firstNull_returnsNegative() {
    JavaType objectType = JavaType.getInstance(Object.class);
    JavaMethod method = null;
    for (var m : objectType.getDeclaredMethods()) {
      if ("toString".equals(m.getName())) {
        method = (JavaMethod) m;
        break;
      }
    }
    if (method != null) {
      assertTrue(FilteredMethodsSubComposite.compareMethodNames(null, method) < 0);
    }
  }

  @Test
  public void compareMethodNames_secondNull_returnsPositive() {
    JavaType objectType = JavaType.getInstance(Object.class);
    JavaMethod method = null;
    for (var m : objectType.getDeclaredMethods()) {
      if ("toString".equals(m.getName())) {
        method = (JavaMethod) m;
        break;
      }
    }
    if (method != null) {
      assertTrue(FilteredMethodsSubComposite.compareMethodNames(method, null) > 0);
    }
  }

  @Test
  public void compareMethodNames_sameMethod_returnsZero() {
    JavaType objectType = JavaType.getInstance(Object.class);
    JavaMethod method = null;
    for (var m : objectType.getDeclaredMethods()) {
      if ("toString".equals(m.getName())) {
        method = (JavaMethod) m;
        break;
      }
    }
    if (method != null) {
      assertEquals(0, FilteredMethodsSubComposite.compareMethodNames(method, method));
    }
  }

  @Test
  public void compareMethodNames_alphabeticalOrder() {
    JavaType objectType = JavaType.getInstance(Object.class);
    JavaMethod toString = null;
    JavaMethod hashCode = null;
    for (var m : objectType.getDeclaredMethods()) {
      if ("toString".equals(m.getName())) {
        toString = (JavaMethod) m;
      } else if ("hashCode".equals(m.getName())) {
        hashCode = (JavaMethod) m;
      }
    }
    if (toString != null && hashCode != null) {
      assertTrue(FilteredMethodsSubComposite.compareMethodNames(hashCode, toString) < 0);
      assertTrue(FilteredMethodsSubComposite.compareMethodNames(toString, hashCode) > 0);
    }
  }
}
