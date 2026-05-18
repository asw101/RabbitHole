package org.alice.ide.member;

import org.alice.ide.member.views.MethodsSubView;
import org.junit.Test;
import org.lgna.croquet.views.ScrollPane;
import org.lgna.project.ast.AbstractMethod;
import org.lgna.project.ast.UserMethod;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class MethodsSubCompositeTest {

  private static final class TestMethodsSubComposite extends MethodsSubComposite {
    private final List<? extends AbstractMethod> methods;

    private TestMethodsSubComposite(List<? extends AbstractMethod> methods) {
      super(UUID.fromString("00000000-0000-0000-0000-000000000111"), true);
      this.methods = methods;
    }

    @Override
    public List<? extends AbstractMethod> getMethods() {
      return this.methods;
    }

    @Override
    protected MethodsSubView<?> createView() {
      return new MethodsSubView<>(this);
    }

    private boolean exposeIsMethodCountDesired(boolean isExpanded, int methodCount) {
      return this.isMethodCountDesired(isExpanded, methodCount);
    }

    private String exposeModifyText(String text, boolean isExpanded) {
      return this.modifyTextIfNecessary(text, isExpanded);
    }

    private ScrollPane exposeCreateScrollPaneIfDesired() {
      return this.createScrollPaneIfDesired();
    }
  }

  private static UserMethod methodNamed(String name) {
    UserMethod method = new UserMethod();
    method.name.setValue(name);
    return method;
  }

  @Test
  public void methodsSubComposite_returnsConfiguredMethods() {
    TestMethodsSubComposite composite = new TestMethodsSubComposite(List.of(methodNamed("alpha")));
    assertEquals(1, composite.getMethods().size());
  }

  @Test
  public void methodsSubComposite_countsMethodsWhenCollapsed() {
    TestMethodsSubComposite composite = new TestMethodsSubComposite(List.of(methodNamed("alpha"), methodNamed("beta")));
    assertTrue(composite.exposeModifyText("Methods", false).contains("(2)"));
  }

  @Test
  public void methodsSubComposite_hidesMethodCountWhenExpanded() {
    TestMethodsSubComposite composite = new TestMethodsSubComposite(List.of(methodNamed("alpha"), methodNamed("beta")));
    assertFalse(composite.exposeIsMethodCountDesired(true, 2));
  }

  @Test
  public void methodsSubComposite_neverCreatesScrollPane() {
    TestMethodsSubComposite composite = new TestMethodsSubComposite(List.of());
    assertNull(composite.exposeCreateScrollPaneIfDesired());
  }
}
