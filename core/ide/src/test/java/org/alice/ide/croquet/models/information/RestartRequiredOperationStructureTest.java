package org.alice.ide.croquet.models.information;

import org.alice.ide.operations.InconsequentialActionOperation;
import org.junit.Test;

import javax.swing.Icon;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class RestartRequiredOperationStructureTest {

  @Test
  public void restartRequiredOperation_whenLoaded_isAccessible() {
    assertNotNull(RestartRequiredOperation.class);
  }

  @Test
  public void restartRequiredOperation_whenReflected_isPublicConcrete() {
    assertTrue(Modifier.isPublic(RestartRequiredOperation.class.getModifiers()));
    assertFalse(Modifier.isAbstract(RestartRequiredOperation.class.getModifiers()));
  }

  @Test
  public void restartRequiredOperation_whenReflected_extendsInconsequentialActionOperation() {
    assertEquals(InconsequentialActionOperation.class, RestartRequiredOperation.class.getSuperclass());
  }

  @Test
  public void restartRequiredOperation_iconField_whenReflected_isPublicStaticFinal() throws Exception {
    Field field = RestartRequiredOperation.class.getDeclaredField("TWEEDLEDUM_AND_TWEEDLEDEE_ICON");
    assertTrue(Modifier.isPublic(field.getModifiers()));
    assertTrue(Modifier.isStatic(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
    assertEquals(Icon.class, field.getType());
  }

  @Test
  public void restartRequiredOperation_singletonHolder_whenReflected_exists() throws Exception {
    Class<?> holder = Class.forName(RestartRequiredOperation.class.getName() + "$SingletonHolder", false,
        RestartRequiredOperation.class.getClassLoader());
    assertTrue(Modifier.isPrivate(holder.getModifiers()));
  }

  @Test
  public void restartRequiredOperation_getInstance_whenReflected_isPublicStaticAndReturnsSelf() throws Exception {
    Method method = RestartRequiredOperation.class.getMethod("getInstance");
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isStatic(method.getModifiers()));
    assertEquals(RestartRequiredOperation.class, method.getReturnType());
    assertEquals(0, method.getParameterCount());
  }

  @Test
  public void restartRequiredOperation_constructor_whenReflected_isPrivateNoArg() throws Exception {
    Constructor<RestartRequiredOperation> constructor = RestartRequiredOperation.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    assertEquals(0, constructor.getParameterCount());
  }

  @Test
  public void restartRequiredOperation_constructors_whenReflected_areNotPublic() {
    for (Constructor<?> constructor : RestartRequiredOperation.class.getDeclaredConstructors()) {
      assertFalse(Modifier.isPublic(constructor.getModifiers()));
    }
  }

  @Test
  public void restartRequiredOperation_performInternal_whenReflected_isProtectedVoid() throws Exception {
    Method method = RestartRequiredOperation.class.getDeclaredMethod("performInternal");
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertEquals(void.class, method.getReturnType());
    assertEquals(0, method.getParameterCount());
  }

  @Test
  public void restartRequiredOperation_declaredPublicMethods_whenReflected_onlyExposeGetInstance() {
    int publicCount = 0;
    for (Method method : RestartRequiredOperation.class.getDeclaredMethods()) {
      if (Modifier.isPublic(method.getModifiers())) {
        publicCount++;
        assertEquals("getInstance", method.getName());
      }
    }
    assertEquals(1, publicCount);
  }

  @Test
  public void restartRequiredOperation_declaredFieldCount_whenReflected_includesIconConstant() {
    assertTrue(RestartRequiredOperation.class.getDeclaredFields().length >= 1);
  }

  @Test
  public void restartRequiredOperation_package_whenReflected_matchesInformationModels() {
    assertEquals("org.alice.ide.croquet.models.information",
        RestartRequiredOperation.class.getPackage().getName());
  }

  @Test
  public void restartRequiredOperation_declaredInnerClasses_whenReflected_onlyContainSingletonHolder() {
    Class<?>[] classes = RestartRequiredOperation.class.getDeclaredClasses();
    assertEquals(1, classes.length);
    assertEquals("SingletonHolder", classes[0].getSimpleName());
  }

  @Test
  public void restartRequiredOperation_getInstance_whenReflected_declaresNoCheckedExceptions() throws Exception {
    Method method = RestartRequiredOperation.class.getMethod("getInstance");
    assertEquals(0, method.getExceptionTypes().length);
  }

  @Test
  public void restartRequiredOperation_declaredConstructorCount_whenReflected_isOne() {
    assertEquals(1, RestartRequiredOperation.class.getDeclaredConstructors().length);
  }

  @Test
  public void restartRequiredOperation_declaredMethodCount_whenReflected_isStable() {
    assertTrue(RestartRequiredOperation.class.getDeclaredMethods().length >= 2);
  }
}
