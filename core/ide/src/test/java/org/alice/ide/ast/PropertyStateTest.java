package org.alice.ide.ast;

import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.project.annotations.ValueDetails;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaMethodParameter;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.StringLiteral;

import java.util.UUID;

import static org.junit.Assert.*;

public class PropertyStateTest {

  private static final class TestPropertyState extends PropertyState {
    private TestPropertyState(JavaMethod setter) {
      super(Application.PROJECT_GROUP, UUID.fromString("2b6d1e38-f96f-4ee4-a3b1-cddb11d7cbaf"), setter);
    }

    private AbstractType<?, ?, ?> exposeType() {
      return super.getType();
    }

    private ValueDetails<?> exposeValueDetails() {
      return super.getValueDetails();
    }
  }

  private static JavaMethod createSetter() {
    JavaMethod setter = JavaMethod.getInstance(StringBuilder.class, "append", String.class);
    assertNotNull(setter);
    return setter;
  }

  @Test
  public void constructor_storesSetter() {
    JavaMethod setter = createSetter();
    TestPropertyState state = new TestPropertyState(setter);

    assertSame(setter, state.getSetter());
  }

  @Test
  public void getType_usesFirstRequiredParameterType() {
    JavaMethod setter = createSetter();
    JavaMethodParameter parameter = (JavaMethodParameter) setter.getRequiredParameters().getFirst();
    TestPropertyState state = new TestPropertyState(setter);

    assertSame(parameter.getValueType(), state.exposeType());
  }

  @Test
  public void getValueDetails_usesFirstRequiredParameterDetails() {
    JavaMethod setter = createSetter();
    JavaMethodParameter parameter = (JavaMethodParameter) setter.getRequiredParameters().getFirst();
    TestPropertyState state = new TestPropertyState(setter);

    assertSame(parameter.getDetails(), state.exposeValueDetails());
  }

  @Test
  public void getValueOrNullLiteral_returnsNullLiteralWhenValueIsNull() {
    TestPropertyState state = new TestPropertyState(createSetter());

    assertTrue(state.getValueOrNullLiteral() instanceof NullLiteral);
  }

  @Test
  public void getValueOrNullLiteral_returnsCurrentExpressionWhenValueIsPresent() {
    TestPropertyState state = new TestPropertyState(createSetter());
    Expression expression = new StringLiteral("hello");
    state.setValueTransactionlessly(expression);

    assertSame(expression, state.getValueOrNullLiteral());
  }
}
