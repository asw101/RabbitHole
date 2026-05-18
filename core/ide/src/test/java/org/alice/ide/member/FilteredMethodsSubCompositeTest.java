package org.alice.ide.member;

import org.junit.Test;
import org.lgna.project.ast.UserMethod;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class FilteredMethodsSubCompositeTest {

  private static UserMethod methodNamed(String name) {
    UserMethod method = new UserMethod();
    method.name.setValue(name);
    return method;
  }

  @Test
  public void filteredMethodsSubComposite_isAbstract() {
    assertTrue(Modifier.isAbstract(FilteredMethodsSubComposite.class.getModifiers()));
  }

  @Test
  public void compareMethodNames_sortsAlphabetically() {
    assertTrue(FilteredMethodsSubComposite.compareMethodNames(methodNamed("alpha"), methodNamed("beta")) < 0);
  }

  @Test
  public void compareMethodNames_handlesNullLeftOperand() {
    assertTrue(FilteredMethodsSubComposite.compareMethodNames(null, methodNamed("beta")) < 0);
  }

  @Test
  public void compareMethodNames_handlesBothNull() {
    assertEquals(0, FilteredMethodsSubComposite.compareMethodNames(null, null));
  }
}
