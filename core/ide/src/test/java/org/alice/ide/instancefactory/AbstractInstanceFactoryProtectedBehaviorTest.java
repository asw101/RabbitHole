package org.alice.ide.instancefactory;

import org.alice.ide.ast.CurrentThisExpression;
import org.junit.Test;
import org.lgna.project.ast.AbstractCode;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserField;

import static org.junit.Assert.*;

public class AbstractInstanceFactoryProtectedBehaviorTest {
  private static final class DummyFactory extends AbstractInstanceFactory {
    private DummyFactory(UserField field) {
      super(field.name);
    }

    private Expression exposedThis() {
      return createThisExpression();
    }

    private Expression exposedTransientThis() {
      return createTransientThisExpression();
    }

    @Override
    protected boolean isValid(AbstractType<?, ?, ?> type, AbstractCode code) {
      return false;
    }

    @Override
    public Expression createTransientExpression() {
      return exposedTransientThis();
    }

    @Override
    public Expression createExpression() {
      return exposedThis();
    }

    @Override
    public AbstractType<?, ?, ?> getValueType() {
      return JavaType.STRING_TYPE;
    }

    @Override
    public String getRepr() {
      return "dummy";
    }
  }

  @Test
  public void protectedHelpersCreateExpectedThisExpressions() {
    DummyFactory factory = new DummyFactory(InstanceFactoryTestSupport.createStringField("text"));
    assertTrue(factory.exposedThis() instanceof ThisExpression);
    assertTrue(factory.exposedTransientThis() instanceof CurrentThisExpression);
  }

  @Test
  public void mutablePropertiesAndToStringDelegateToRepresentation() {
    UserField field = InstanceFactoryTestSupport.createStringField("text");
    DummyFactory factory = new DummyFactory(field);
    assertEquals(1, factory.getMutablePropertiesOfInterest().length);
    assertSame(field.name, factory.getMutablePropertiesOfInterest()[0]);
    assertEquals("dummy", factory.toString());
  }
}
