package org.alice.ide.croquet.models.ui.preferences;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class IsEmphasizingClassesStateTest {

  private static Class<?> getSingletonHolderClass() {
    Class<?>[] declaredClasses = IsEmphasizingClassesState.class.getDeclaredClasses();
    assertEquals(1, declaredClasses.length);
    return declaredClasses[0];
  }

  private static Field getValueField() throws Exception {
    Field field = IsEmphasizingClassesState.class.getDeclaredField("value");
    field.setAccessible(true);
    return field;
  }

  private static Field getSingletonField() throws Exception {
    Field field = getSingletonHolderClass().getDeclaredField("instance");
    field.setAccessible(true);
    return field;
  }

  private static Method getInstanceMethod() throws Exception {
    return IsEmphasizingClassesState.class.getMethod("getInstance");
  }

  private static Method getValueMethod() throws Exception {
    return IsEmphasizingClassesState.class.getMethod("getValue");
  }

  @Test
  public void classIsPublic() {
    assertTrue(Modifier.isPublic(IsEmphasizingClassesState.class.getModifiers()));
  }

  @Test
  public void classIsNotFinal() {
    assertFalse(Modifier.isFinal(IsEmphasizingClassesState.class.getModifiers()));
  }

  @Test
  public void singletonHolderExists() {
    assertEquals("SingletonHolder", getSingletonHolderClass().getSimpleName());
  }

  @Test
  public void singletonHolderIsPrivateStatic() {
    int modifiers = getSingletonHolderClass().getModifiers();
    assertTrue(Modifier.isPrivate(modifiers));
    assertTrue(Modifier.isStatic(modifiers));
  }

  @Test
  public void singletonHolderDeclaresOnlyOneField() {
    assertEquals(1, getSingletonHolderClass().getDeclaredFields().length);
  }

  @Test
  public void singletonFieldHasMatchingType() throws Exception {
    assertEquals(IsEmphasizingClassesState.class, getSingletonField().getType());
  }

  @Test
  public void singletonFieldIsPrivate() throws Exception {
    assertTrue(Modifier.isPrivate(getSingletonField().getModifiers()));
  }

  @Test
  public void singletonFieldIsStatic() throws Exception {
    assertTrue(Modifier.isStatic(getSingletonField().getModifiers()));
  }

  @Test
  public void getInstanceMethodExists() throws Exception {
    assertNotNull(getInstanceMethod());
  }

  @Test
  public void getInstanceMethodIsPublicStatic() throws Exception {
    int modifiers = getInstanceMethod().getModifiers();
    assertTrue(Modifier.isPublic(modifiers));
    assertTrue(Modifier.isStatic(modifiers));
  }

  @Test
  public void getInstanceMethodReturnsSelfType() throws Exception {
    assertEquals(IsEmphasizingClassesState.class, getInstanceMethod().getReturnType());
  }

  @Test
  public void getInstanceReturnsSingleton() {
    assertSame(IsEmphasizingClassesState.getInstance(), IsEmphasizingClassesState.getInstance());
  }

  @Test
  public void getInstanceMatchesSingletonHolderField() throws Exception {
    assertSame(IsEmphasizingClassesState.getInstance(), getSingletonField().get(null));
  }

  @Test
  public void onlyConstructorIsPrivate() {
    Constructor<?>[] constructors = IsEmphasizingClassesState.class.getDeclaredConstructors();
    assertEquals(1, constructors.length);
    assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
  }

  @Test
  public void getValueMethodExists() throws Exception {
    assertNotNull(getValueMethod());
  }

  @Test
  public void getValueMethodReturnsPrimitiveBoolean() throws Exception {
    assertEquals(boolean.class, getValueMethod().getReturnType());
  }

  @Test
  public void getValueMatchesReflectiveInvocation() throws Exception {
    Object value = getValueMethod().invoke(IsEmphasizingClassesState.getInstance());
    assertEquals(IsEmphasizingClassesState.getInstance().getValue(), value);
  }

  @Test
  public void getValueIsStableAcrossCalls() {
    boolean first = IsEmphasizingClassesState.getInstance().getValue();
    boolean second = IsEmphasizingClassesState.getInstance().getValue();
    assertEquals(first, second);
  }

  @Test
  public void classDeclaresOnlyStaticBooleanValueField() throws Exception {
    Field[] fields = IsEmphasizingClassesState.class.getDeclaredFields();
    assertEquals(1, fields.length);
    assertEquals("value", fields[0].getName());
    assertEquals(boolean.class, fields[0].getType());
  }

  @Test
  public void valueFieldIsStatic() throws Exception {
    assertTrue(Modifier.isStatic(getValueField().getModifiers()));
  }

  @Test
  public void valueFieldIsNotFinal() throws Exception {
    assertFalse(Modifier.isFinal(getValueField().getModifiers()));
  }
}
