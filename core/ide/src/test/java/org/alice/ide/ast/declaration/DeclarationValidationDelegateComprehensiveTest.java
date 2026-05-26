package org.alice.ide.ast.declaration;

import static org.alice.ide.testing.JacocoReflectionSupport.declaredMethods;
import static org.alice.ide.testing.JacocoReflectionSupport.declaredFields;

import org.junit.Test;
import org.lgna.croquet.AbstractSeverityStatusComposite;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.Expression;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class DeclarationValidationDelegateComprehensiveTest {

  private DeclarationValidationDelegate newDelegate() throws Exception {
    Constructor<DeclarationValidationDelegate> constructor = DeclarationValidationDelegate.class.getDeclaredConstructor(DeclarationLikeSubstanceComposite.class);
    constructor.setAccessible(true);
    return constructor.newInstance((DeclarationLikeSubstanceComposite<?>) null);
  }

  @Test
  public void classExists() {
    assertNotNull(DeclarationValidationDelegate.class);
  }

  @Test
  public void classIsPackagePrivate() {
    int modifiers = DeclarationValidationDelegate.class.getModifiers();
    assertFalse(Modifier.isPublic(modifiers));
    assertFalse(Modifier.isProtected(modifiers));
    assertFalse(Modifier.isPrivate(modifiers));
  }

  @Test
  public void classIsConcrete() {
    assertFalse(Modifier.isAbstract(DeclarationValidationDelegate.class.getModifiers()));
  }

  @Test
  public void classIsFinal() {
    assertTrue(Modifier.isFinal(DeclarationValidationDelegate.class.getModifiers()));
  }

  @Test
  public void classIsNotInterfaceEnumOrAnnotation() {
    assertFalse(DeclarationValidationDelegate.class.isInterface());
    assertFalse(DeclarationValidationDelegate.class.isEnum());
    assertFalse(DeclarationValidationDelegate.class.isAnnotation());
  }

  @Test
  public void simpleNameMatchesSource() {
    assertEquals("DeclarationValidationDelegate", DeclarationValidationDelegate.class.getSimpleName());
  }

  @Test
  public void packageNameMatchesSource() {
    assertEquals("org.alice.ide.ast.declaration", DeclarationValidationDelegate.class.getPackage().getName());
  }

  @Test
  public void hasSingleConstructor() {
    assertEquals(1, DeclarationValidationDelegate.class.getDeclaredConstructors().length);
  }

  @Test
  public void constructorAcceptsComposite() throws Exception {
    assertNotNull(DeclarationValidationDelegate.class.getDeclaredConstructor(DeclarationLikeSubstanceComposite.class));
  }

  @Test
  public void constructorIsPackagePrivate() throws Exception {
    Constructor<?> constructor = DeclarationValidationDelegate.class.getDeclaredConstructor(DeclarationLikeSubstanceComposite.class);
    int modifiers = constructor.getModifiers();
    assertFalse(Modifier.isPublic(modifiers));
    assertFalse(Modifier.isProtected(modifiers));
    assertFalse(Modifier.isPrivate(modifiers));
  }

  @Test
  public void constructorHasSingleParameter() throws Exception {
    assertEquals(1, DeclarationValidationDelegate.class.getDeclaredConstructor(DeclarationLikeSubstanceComposite.class).getParameterCount());
  }

  @Test
  public void reflectiveConstructionWithNullCompositeWorks() throws Exception {
    assertNotNull(newDelegate());
  }

  @Test
  public void compositeFieldExists() throws Exception {
    assertNotNull(DeclarationValidationDelegate.class.getDeclaredField("composite"));
  }

  @Test
  public void compositeFieldIsPrivateFinal() throws Exception {
    Field field = DeclarationValidationDelegate.class.getDeclaredField("composite");
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void compositeFieldUsesCompositeType() throws Exception {
    Field field = DeclarationValidationDelegate.class.getDeclaredField("composite");
    assertEquals(DeclarationLikeSubstanceComposite.class, field.getType());
  }

  @Test
  public void constructorStoresCompositeReference() throws Exception {
    Field field = DeclarationValidationDelegate.class.getDeclaredField("composite");
    field.setAccessible(true);
    assertNull(field.get(newDelegate()));
  }

  @Test
  public void getValueTypeExplanationMethodExists() throws Exception {
    assertNotNull(DeclarationValidationDelegate.class.getDeclaredMethod("getValueTypeExplanation", AbstractType.class));
  }

  @Test
  public void getNameExplanationMethodExists() throws Exception {
    assertNotNull(DeclarationValidationDelegate.class.getDeclaredMethod("getNameExplanation", String.class));
  }

  @Test
  public void nullAllowanceMethodExists() throws Exception {
    assertNotNull(DeclarationValidationDelegate.class.getDeclaredMethod("isNullAllowedForInitializerUnderAnyCircumstances"));
  }

  @Test
  public void getInitializerExplanationMethodExists() throws Exception {
    assertNotNull(DeclarationValidationDelegate.class.getDeclaredMethod("getInitializerExplanation", Expression.class));
  }

  @Test
  public void computeStatusMethodExists() throws Exception {
    assertNotNull(DeclarationValidationDelegate.class.getDeclaredMethod("computeStatus", AbstractSeverityStatusComposite.ErrorStatus.class));
  }

  @Test
  public void explanationMethodsReturnString() throws Exception {
    assertEquals(String.class, DeclarationValidationDelegate.class.getDeclaredMethod("getValueTypeExplanation", AbstractType.class).getReturnType());
    assertEquals(String.class, DeclarationValidationDelegate.class.getDeclaredMethod("getNameExplanation", String.class).getReturnType());
    assertEquals(String.class, DeclarationValidationDelegate.class.getDeclaredMethod("getInitializerExplanation", Expression.class).getReturnType());
  }

  @Test
  public void booleanReturningMethodsUsePrimitiveBoolean() throws Exception {
    Method nullAllowed = DeclarationValidationDelegate.class.getDeclaredMethod("isNullAllowedForInitializerUnderAnyCircumstances");
    Method compute = DeclarationValidationDelegate.class.getDeclaredMethod("computeStatus", AbstractSeverityStatusComposite.ErrorStatus.class);
    assertEquals(boolean.class, nullAllowed.getReturnType());
    assertEquals(boolean.class, compute.getReturnType());
  }

  @Test
  public void helperMethodsArePackagePrivate() throws Exception {
    Method valueType = DeclarationValidationDelegate.class.getDeclaredMethod("getValueTypeExplanation", AbstractType.class);
    Method name = DeclarationValidationDelegate.class.getDeclaredMethod("getNameExplanation", String.class);
    Method initializer = DeclarationValidationDelegate.class.getDeclaredMethod("getInitializerExplanation", Expression.class);
    Method compute = DeclarationValidationDelegate.class.getDeclaredMethod("computeStatus", AbstractSeverityStatusComposite.ErrorStatus.class);
    assertFalse(Modifier.isPublic(valueType.getModifiers()));
    assertFalse(Modifier.isPublic(name.getModifiers()));
    assertFalse(Modifier.isPublic(initializer.getModifiers()));
    assertFalse(Modifier.isPublic(compute.getModifiers()));
  }

  @Test
  public void methodParameterCountsMatchSource() throws Exception {
    assertEquals(1, DeclarationValidationDelegate.class.getDeclaredMethod("getValueTypeExplanation", AbstractType.class).getParameterCount());
    assertEquals(1, DeclarationValidationDelegate.class.getDeclaredMethod("getNameExplanation", String.class).getParameterCount());
    assertEquals(0, DeclarationValidationDelegate.class.getDeclaredMethod("isNullAllowedForInitializerUnderAnyCircumstances").getParameterCount());
    assertEquals(1, DeclarationValidationDelegate.class.getDeclaredMethod("getInitializerExplanation", Expression.class).getParameterCount());
    assertEquals(1, DeclarationValidationDelegate.class.getDeclaredMethod("computeStatus", AbstractSeverityStatusComposite.ErrorStatus.class).getParameterCount());
  }

  @Test
  public void declaredFieldCountIsOne() {
    assertEquals(1, declaredFields(DeclarationValidationDelegate.class).length);
  }

  @Test
  public void declaredMethodCountMatchesDelegateSurface() {
    assertEquals(5, declaredMethods(DeclarationValidationDelegate.class).length);
  }


  @Test
  public void onlyDeclaredFieldIsNamedComposite() {
    assertEquals("composite", declaredFields(DeclarationValidationDelegate.class)[0].getName());
  }

  @Test
  public void noPublicConstructorsExist() {
    for (Constructor<?> constructor : DeclarationValidationDelegate.class.getDeclaredConstructors()) {
      assertFalse(Modifier.isPublic(constructor.getModifiers()));
    }
  }

  @Test
  public void declaredMethodNamesContainComputeStatus() {
    boolean found = false;
    for (Method method : declaredMethods(DeclarationValidationDelegate.class)) {
      if ("computeStatus".equals(method.getName())) {
        found = true;
      }
    }
    assertTrue(found);
  }

}
