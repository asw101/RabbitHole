package org.alice.ide.member;

import org.junit.Test;
import org.lgna.project.ast.AbstractMethod;
import org.lgna.project.ast.UserMethod;

import java.util.UUID;

import static org.junit.Assert.*;

public class NameFilteredJavaMethodsSubCompositeTest {

  private static final class TestComposite extends NameFilteredJavaMethodsSubComposite {
    private TestComposite(String... methodNames) {
      super(UUID.fromString("00000000-0000-0000-0000-000000000222"), methodNames);
    }

    private boolean accepts(AbstractMethod method) {
      return this.isAcceptingOf(method);
    }
  }

  private static UserMethod methodNamed(String name) {
    UserMethod method = new UserMethod();
    method.name.setValue(name);
    return method;
  }

  @Test
  public void composite_acceptsConfiguredMethodName() {
    TestComposite composite = new TestComposite("beta", "alpha");
    assertTrue(composite.accepts(methodNamed("alpha")));
  }

  @Test
  public void composite_rejectsUnconfiguredMethodName() {
    TestComposite composite = new TestComposite("beta", "alpha");
    assertFalse(composite.accepts(methodNamed("gamma")));
  }

  @Test
  public void comparator_prefersConfiguredNameOrder() {
    TestComposite composite = new TestComposite("beta", "alpha");
    assertTrue(composite.getComparator().compare(methodNamed("beta"), methodNamed("alpha")) < 0);
  }

  @Test
  public void comparator_fallsBackToAlphabeticalOrder() {
    TestComposite composite = new TestComposite("beta", "alpha");
    assertTrue(composite.getComparator().compare(methodNamed("delta"), methodNamed("gamma")) < 0);
  }
}
