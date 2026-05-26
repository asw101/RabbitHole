package org.alice.ide.croquet.models.declaration;

import static org.alice.ide.testing.JacocoReflectionSupport.declaredMethods;
import static org.alice.ide.testing.JacocoReflectionSupport.declaredFields;

import org.alice.ide.croquet.models.ExpressionState;
import org.alice.ide.croquet.models.StandardExpressionState;
import org.junit.Test;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.StringLiteral;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class InitializerStateComprehensiveTest {

  @Test
  public void initializerState_isPublicConcreteClass() {
    assertTrue(Modifier.isPublic(InitializerState.class.getModifiers()));
    assertFalse(Modifier.isAbstract(InitializerState.class.getModifiers()));
  }

  @Test
  public void initializerState_extendsStandardExpressionState() {
    assertTrue(StandardExpressionState.class.isAssignableFrom(InitializerState.class));
  }

  @Test
  public void initializerState_isAlsoAnExpressionState() {
    assertTrue(ExpressionState.class.isAssignableFrom(InitializerState.class));
  }

  @Test
  public void initializerState_hasSinglePublicConstructor() {
    Constructor<?>[] constructors = InitializerState.class.getConstructors();
    assertEquals(1, constructors.length);
    assertTrue(Modifier.isPublic(constructors[0].getModifiers()));
  }

  @Test
  public void initializerState_constructorParametersMatchSource() throws Exception {
    Constructor<InitializerState> constructor = InitializerState.class.getConstructor(InitializerStateOwner.class, org.lgna.project.ast.Expression.class);
    assertArrayEquals(new Class<?>[] {InitializerStateOwner.class, org.lgna.project.ast.Expression.class}, constructor.getParameterTypes());
  }

  @Test
  public void initializerState_declaresPrivateFinalOwnerField() throws Exception {
    Field field = InitializerState.class.getDeclaredField("owner");
    assertEquals(InitializerStateOwner.class, field.getType());
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void initializerState_declaresOnlyExpectedField() {
    assertEquals(1, declaredFields(InitializerState.class).length);
  }

  @Test
  public void initializerState_declaresExpectedProtectedMethods() throws Exception {
    Method getValueDetails = InitializerState.class.getDeclaredMethod("getValueDetails");
    Method getType = InitializerState.class.getDeclaredMethod("getType");
    assertTrue(Modifier.isProtected(getValueDetails.getModifiers()));
    assertTrue(Modifier.isProtected(getType.getModifiers()));
  }

  @Test
  public void getValueDetails_returnsNullByContract() throws Exception {
    InitializerState state = new InitializerState(new FixedOwner(JavaType.STRING_TYPE), new NullLiteral());
    Method method = InitializerState.class.getDeclaredMethod("getValueDetails");
    method.setAccessible(true);
    assertNull(method.invoke(state));
  }

  @Test
  public void getType_delegatesToOwnerForStringType() throws Exception {
    InitializerState state = new InitializerState(new FixedOwner(JavaType.STRING_TYPE), new NullLiteral());
    assertSame(JavaType.STRING_TYPE, invokeGetType(state));
  }

  @Test
  public void getType_delegatesToOwnerForIntegerType() throws Exception {
    InitializerState state = new InitializerState(new FixedOwner(JavaType.INTEGER_OBJECT_TYPE), new NullLiteral());
    assertSame(JavaType.INTEGER_OBJECT_TYPE, invokeGetType(state));
  }

  @Test
  public void getType_readsMutableOwnerEachTime() throws Exception {
    MutableOwner owner = new MutableOwner(JavaType.STRING_TYPE);
    InitializerState state = new InitializerState(owner, new NullLiteral());
    assertSame(JavaType.STRING_TYPE, invokeGetType(state));
    owner.valueType = JavaType.BOOLEAN_OBJECT_TYPE;
    assertSame(JavaType.BOOLEAN_OBJECT_TYPE, invokeGetType(state));
  }

  @Test
  public void constructor_preservesInitialExpressionValue() {
    StringLiteral literal = new StringLiteral("hello");
    InitializerState state = new InitializerState(new FixedOwner(JavaType.STRING_TYPE), literal);
    assertSame(literal, state.getValue());
  }

  @Test
  public void constructor_acceptsNullInitialValue() {
    InitializerState state = new InitializerState(new FixedOwner(JavaType.STRING_TYPE), null);
    assertNull(state.getValue());
  }

  @Test
  public void differentOwners_canProduceDifferentTypes() throws Exception {
    InitializerState stringState = new InitializerState(new FixedOwner(JavaType.STRING_TYPE), new NullLiteral());
    InitializerState booleanState = new InitializerState(new FixedOwner(JavaType.BOOLEAN_OBJECT_TYPE), new NullLiteral());
    assertNotSame(invokeGetType(stringState), invokeGetType(booleanState));
  }

  @Test
  public void getValueDetails_returnTypeMatchesDeclaration() throws Exception {
    assertEquals(org.lgna.project.annotations.ValueDetails.class,
        InitializerState.class.getDeclaredMethod("getValueDetails").getReturnType());
  }

  @Test
  public void getType_returnTypeMatchesDeclaration() throws Exception {
    assertEquals(AbstractType.class, InitializerState.class.getDeclaredMethod("getType").getReturnType());
  }

  @Test
  public void ownerField_storesSuppliedOwnerInstance() throws Exception {
    FixedOwner owner = new FixedOwner(JavaType.STRING_TYPE);
    InitializerState state = new InitializerState(owner, new NullLiteral());
    Field field = InitializerState.class.getDeclaredField("owner");
    field.setAccessible(true);
    assertSame(owner, field.get(state));
  }

  @Test
  public void noAdditionalPublicMethodsAreDeclared() {
    assertEquals(2, declaredMethods(InitializerState.class).length);
  }

  @Test
  public void initializerStateOwner_isAnInterface() {
    assertTrue(Modifier.isInterface(InitializerStateOwner.class.getModifiers()));
  }

  @Test
  public void initializerStateOwner_declaresGetValueType() throws Exception {
    Method method = InitializerStateOwner.class.getMethod("getValueType");
    assertEquals(AbstractType.class, method.getReturnType());
  }

  @Test
  public void initializerState_instancesRemainDistinct() {
    InitializerState first = new InitializerState(new FixedOwner(JavaType.STRING_TYPE), new NullLiteral());
    InitializerState second = new InitializerState(new FixedOwner(JavaType.STRING_TYPE), new NullLiteral());
    assertNotSame(first, second);
  }

  private static AbstractType<?, ?, ?> invokeGetType(InitializerState state) throws Exception {
    Method method = InitializerState.class.getDeclaredMethod("getType");
    method.setAccessible(true);
    return (AbstractType<?, ?, ?>) method.invoke(state);
  }

  private static final class FixedOwner implements InitializerStateOwner {
    private final AbstractType<?, ?, ?> valueType;

    private FixedOwner(AbstractType<?, ?, ?> valueType) {
      this.valueType = valueType;
    }

    @Override
    public AbstractType<?, ?, ?> getValueType() {
      return valueType;
    }
  }

  private static final class MutableOwner implements InitializerStateOwner {
    private AbstractType<?, ?, ?> valueType;

    private MutableOwner(AbstractType<?, ?, ?> valueType) {
      this.valueType = valueType;
    }

    @Override
    public AbstractType<?, ?, ?> getValueType() {
      return valueType;
    }
  }

  @Test
  public void initializerStateOwner_declaresSingleMethod() {
    assertEquals(1, declaredMethods(InitializerStateOwner.class).length);
  }

  @Test
  public void statesWithSameOwner_canHoldDifferentValues() {
    FixedOwner owner = new FixedOwner(JavaType.STRING_TYPE);
    InitializerState first = new InitializerState(owner, new StringLiteral("a"));
    InitializerState second = new InitializerState(owner, new StringLiteral("b"));
    assertNotSame(first.getValue(), second.getValue());
  }

  @Test
  public void ownerFieldName_matchesSource() throws Exception {
    assertEquals("owner", declaredFields(InitializerState.class)[0].getName());
  }

}
