package org.alice.ide.ast;

import org.alice.ide.croquet.models.StandardExpressionState;
import org.junit.Before;
import org.junit.Test;
import org.lgna.project.ast.AbstractConstructor;
import org.lgna.project.ast.AbstractParameter;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaConstructor;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.UserField;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import static org.junit.Assert.*;

public class FieldInitializerInstanceCreationArgument0StateComprehensiveTest {
  @Before
  public void clearCache() throws Exception {
    ((Map<?, ?>) readMapField().get(null)).clear();
  }

  @Test
  public void classExtendsStandardExpressionState() {
    assertTrue(StandardExpressionState.class.isAssignableFrom(FieldInitializerInstanceCreationArgument0State.class));
  }

  @Test
  public void getInstanceReturnsNullForNullField() {
    assertNull(FieldInitializerInstanceCreationArgument0State.getInstance(null));
  }

  @Test
  public void getInstanceReturnsNullWhenInitializerIsNotInstanceCreation() {
    UserField field = new UserField("value", JavaType.STRING_TYPE, new StringLiteral("plain"));

    assertNull(FieldInitializerInstanceCreationArgument0State.getInstance(field));
  }

  @Test
  public void getInstanceReturnsNullWhenInitializerIsNullLiteral() {
    UserField field = new UserField("value", JavaType.STRING_TYPE, new NullLiteral());

    assertNull(FieldInitializerInstanceCreationArgument0State.getInstance(field));
  }

  @Test
  public void getInstanceReturnsNullWhenConstructorHasNoRequiredParameters() {
    UserField field = createField(JavaConstructor.getInstance(StringBuilder.class));

    assertNull(FieldInitializerInstanceCreationArgument0State.getInstance(field));
  }

  @Test
  public void getInstanceReturnsStateForSingleArgumentConstructor() {
    UserField field = createField(JavaConstructor.getInstance(java.io.File.class, String.class));

    assertNotNull(FieldInitializerInstanceCreationArgument0State.getInstance(field));
  }

  @Test
  public void getInstanceCachesStatePerField() {
    UserField field = createField(JavaConstructor.getInstance(java.io.File.class, String.class));

    assertSame(FieldInitializerInstanceCreationArgument0State.getInstance(field), FieldInitializerInstanceCreationArgument0State.getInstance(field));
  }

  @Test
  public void differentFieldsReceiveDifferentCachedStates() {
    FieldInitializerInstanceCreationArgument0State first = FieldInitializerInstanceCreationArgument0State.getInstance(createField(JavaConstructor.getInstance(java.io.File.class, String.class)));
    FieldInitializerInstanceCreationArgument0State second = FieldInitializerInstanceCreationArgument0State.getInstance(createField(JavaConstructor.getInstance(java.io.File.class, String.class)));

    assertNotSame(first, second);
  }

  @Test
  public void privateFieldReferenceMatchesSuppliedField() throws Exception {
    UserField field = createField(JavaConstructor.getInstance(java.io.File.class, String.class));
    FieldInitializerInstanceCreationArgument0State state = FieldInitializerInstanceCreationArgument0State.getInstance(field);
    Field reflectedField = FieldInitializerInstanceCreationArgument0State.class.getDeclaredField("field");
    reflectedField.setAccessible(true);

    assertSame(field, reflectedField.get(state));
  }

  @Test
  public void privateInstanceCreationReferenceMatchesInitializer() throws Exception {
    UserField field = createField(JavaConstructor.getInstance(java.io.File.class, String.class));
    FieldInitializerInstanceCreationArgument0State state = FieldInitializerInstanceCreationArgument0State.getInstance(field);
    Field reflectedField = FieldInitializerInstanceCreationArgument0State.class.getDeclaredField("instanceCreation");
    reflectedField.setAccessible(true);

    assertSame(field.initializer.getValue(), reflectedField.get(state));
  }

  @Test
  public void getRequiredParameter0ReturnsFirstParameter() throws Exception {
    UserField field = createField(JavaConstructor.getInstance(java.awt.Point.class, int.class, int.class));
    FieldInitializerInstanceCreationArgument0State state = FieldInitializerInstanceCreationArgument0State.getInstance(field);
    Method method = FieldInitializerInstanceCreationArgument0State.class.getDeclaredMethod("getRequiredParameter0");
    method.setAccessible(true);

    assertSame(field.initializer.getValue() instanceof InstanceCreation creation ? creation.constructor.getValue().getRequiredParameters().get(0) : null, method.invoke(state));
  }

  @Test
  public void getTypeUsesFirstRequiredParameterType() throws Exception {
    UserField field = createField(JavaConstructor.getInstance(java.awt.Point.class, int.class, int.class));
    FieldInitializerInstanceCreationArgument0State state = FieldInitializerInstanceCreationArgument0State.getInstance(field);
    Method method = FieldInitializerInstanceCreationArgument0State.class.getDeclaredMethod("getType");
    method.setAccessible(true);

    assertSame(JavaType.getInstance(int.class), method.invoke(state));
  }

  @Test
  public void getValueDetailsUsesFirstRequiredParameterDetails() throws Exception {
    UserField field = createField(JavaConstructor.getInstance(java.awt.Point.class, int.class, int.class));
    InstanceCreation creation = (InstanceCreation) field.initializer.getValue();
    FieldInitializerInstanceCreationArgument0State state = FieldInitializerInstanceCreationArgument0State.getInstance(field);
    Method method = FieldInitializerInstanceCreationArgument0State.class.getDeclaredMethod("getValueDetails");
    method.setAccessible(true);

    assertSame(creation.constructor.getValue().getRequiredParameters().get(0).getDetails(), method.invoke(state));
  }

  @Test
  public void constructorIsPrivate() {
    assertTrue(Modifier.isPrivate(FieldInitializerInstanceCreationArgument0State.class.getDeclaredConstructors()[0].getModifiers()));
  }

  @Test
  public void mapFieldIsStatic() throws Exception {
    assertTrue(Modifier.isStatic(readMapField().getModifiers()));
  }

  @Test
  public void getInstanceLeavesArgumentExpressionAvailable() {
    UserField field = createField(JavaConstructor.getInstance(java.io.File.class, String.class));
    FieldInitializerInstanceCreationArgument0State.getInstance(field);
    InstanceCreation creation = (InstanceCreation) field.initializer.getValue();

    assertTrue(creation.requiredArguments.get(0).expression.getValue() instanceof StringLiteral);
  }

  private static UserField createField(AbstractConstructor constructor) {
    InstanceCreation creation = IncompleteAstUtilities.createIncompleteInstanceCreation(constructor);
    if (!creation.requiredArguments.isEmpty()) {
      AbstractParameter parameter = constructor.getRequiredParameters().get(0);
      if (JavaType.STRING_TYPE.equals(parameter.getValueType())) {
        creation.requiredArguments.get(0).expression.setValue(new StringLiteral("resource"));
      } else {
        creation.requiredArguments.get(0).expression.setValue(new IntegerLiteral(1));
      }
    }
    return new UserField("value", constructor.getDeclaringType(), creation);
  }

  private static Field readMapField() throws Exception {
    Field field = FieldInitializerInstanceCreationArgument0State.class.getDeclaredField("map");
    field.setAccessible(true);
    return field;
  }
}
