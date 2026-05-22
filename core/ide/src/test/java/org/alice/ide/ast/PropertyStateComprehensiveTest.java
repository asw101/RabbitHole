package org.alice.ide.ast;

import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.project.annotations.ValueDetails;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaMethodParameter;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NullLiteral;

import java.util.UUID;

import static org.junit.Assert.*;

public class PropertyStateComprehensiveTest {
  private static final class InspectablePropertyState extends PropertyState {
    private InspectablePropertyState(JavaMethod setter) {
      super(Application.PROJECT_GROUP, UUID.fromString("2b6a84f0-53b0-4d69-96a8-1b7a11f2a5a6"), setter);
    }

    private AbstractType<?, ?, ?> exposedType() {
      return this.getType();
    }

    private ValueDetails<?> exposedDetails() {
      return this.getValueDetails();
    }
  }

  @Test
  public void getSetterReturnsOriginalMethod() {
    JavaMethod setter = JavaMethod.getInstance(StringBuilder.class, "setLength", int.class);
    InspectablePropertyState state = new InspectablePropertyState(setter);

    assertSame(setter, state.getSetter());
  }

  @Test
  public void exposedTypeUsesFirstRequiredParameterType() {
    JavaMethod setter = JavaMethod.getInstance(StringBuilder.class, "setLength", int.class);

    assertSame(JavaType.getInstance(int.class), new InspectablePropertyState(setter).exposedType());
  }

  @Test
  public void exposedDetailsUsesFirstRequiredParameterDetails() {
    JavaMethod setter = JavaMethod.getInstance(StringBuilder.class, "setLength", int.class);
    InspectablePropertyState state = new InspectablePropertyState(setter);

    assertSame(((JavaMethodParameter) setter.getRequiredParameters().get(0)).getDetails(), state.exposedDetails());
  }

  @Test
  public void getValueOrNullLiteralReturnsNullLiteralInitially() {
    InspectablePropertyState state = new InspectablePropertyState(JavaMethod.getInstance(StringBuilder.class, "setLength", int.class));

    assertTrue(state.getValueOrNullLiteral() instanceof NullLiteral);
  }

  @Test
  public void getValueOrNullLiteralReturnsStoredExpressionWhenSet() {
    InspectablePropertyState state = new InspectablePropertyState(JavaMethod.getInstance(StringBuilder.class, "setLength", int.class));
    EmptyExpression expression = new EmptyExpression(int.class);

    state.setValueTransactionlessly(expression);

    assertSame(expression, state.getValueOrNullLiteral());
  }

  @Test
  public void getValueOrNullLiteralDoesNotWrapNonNullValues() {
    InspectablePropertyState state = new InspectablePropertyState(JavaMethod.getInstance(StringBuilder.class, "setLength", int.class));
    Expression expression = new EmptyExpression(JavaType.getInstance(int.class));
    state.setValueTransactionlessly(expression);

    assertFalse(state.getValueOrNullLiteral() instanceof NullLiteral);
    assertSame(expression, state.getValueOrNullLiteral());
  }

  @Test
  public void getValueOrNullLiteralCreatesFreshNullLiteralEachTimeWhenUnset() {
    InspectablePropertyState state = new InspectablePropertyState(JavaMethod.getInstance(StringBuilder.class, "setLength", int.class));

    assertNotSame(state.getValueOrNullLiteral(), state.getValueOrNullLiteral());
  }

  @Test
  public void exposedTypeUsesFirstParameterWhenMethodHasMultipleParameters() {
    JavaMethod setter = JavaMethod.getInstance(String.class, "substring", int.class, int.class);

    assertSame(JavaType.getInstance(int.class), new InspectablePropertyState(setter).exposedType());
  }

  @Test
  public void exposedDetailsUsesFirstParameterWhenMethodHasMultipleParameters() {
    JavaMethod setter = JavaMethod.getInstance(String.class, "substring", int.class, int.class);
    InspectablePropertyState state = new InspectablePropertyState(setter);

    assertSame(((JavaMethodParameter) setter.getRequiredParameters().get(0)).getDetails(), state.exposedDetails());
  }

  @Test
  public void primitiveParameterMethodsAreSupported() {
    JavaMethod setter = JavaMethod.getInstance(StringBuilder.class, "setLength", int.class);

    assertSame(JavaType.getInstance(int.class), new InspectablePropertyState(setter).exposedType());
  }

  @Test
  public void arrayParameterMethodsAreSupported() {
    JavaMethod setter = JavaMethod.getInstance(StringBuilder.class, "append", char[].class, int.class, int.class);

    assertSame(JavaType.getInstance(char[].class), new InspectablePropertyState(setter).exposedType());
  }

  @Test
  public void nullSetterIsStored() {
    InspectablePropertyState state = new InspectablePropertyState(null);

    assertNull(state.getSetter());
  }

  @Test
  public void nullSetterCausesTypeResolutionToThrow() {
    InspectablePropertyState state = new InspectablePropertyState(null);

    try {
      state.exposedType();
      fail();
    } catch (NullPointerException expected) {
    }
  }

  @Test
  public void nullSetterCausesDetailsResolutionToThrow() {
    InspectablePropertyState state = new InspectablePropertyState(null);

    try {
      state.exposedDetails();
      fail();
    } catch (NullPointerException expected) {
    }
  }

  @Test
  public void getSetterRemainsStableAfterValueChanges() {
    JavaMethod setter = JavaMethod.getInstance(StringBuilder.class, "setLength", int.class);
    InspectablePropertyState state = new InspectablePropertyState(setter);
    state.setValueTransactionlessly(new EmptyExpression(int.class));
    state.setValueTransactionlessly(new EmptyExpression(int.class));

    assertSame(setter, state.getSetter());
  }
}
