package org.alice.ide.croquet.models.ui.locale;

import org.junit.Test;
import org.lgna.croquet.preferences.PreferenceMutableDataSingleSelectListState;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Locale;

import static org.junit.Assert.*;

public class LocaleStateStructureTest {

  @Test
  public void localeState_whenLoaded_isAccessible() {
    assertNotNull(LocaleState.class);
  }

  @Test
  public void localeState_whenReflected_isPublicConcrete() {
    assertTrue(Modifier.isPublic(LocaleState.class.getModifiers()));
    assertFalse(Modifier.isAbstract(LocaleState.class.getModifiers()));
  }

  @Test
  public void localeState_whenReflected_extendsPreferenceMutableDataSingleSelectListState() {
    assertEquals(PreferenceMutableDataSingleSelectListState.class, LocaleState.class.getSuperclass());
  }

  @Test
  public void localeState_genericSuperclass_whenReflected_usesLocaleTypeArgument() {
    Type genericSuperclass = LocaleState.class.getGenericSuperclass();
    assertTrue(genericSuperclass instanceof ParameterizedType);
    ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
    assertEquals(Locale.class, parameterizedType.getActualTypeArguments()[0]);
  }

  @Test
  public void localeState_getInstance_whenReflected_isPublicStaticAndReturnsSelf() throws Exception {
    Method method = LocaleState.class.getMethod("getInstance");
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isStatic(method.getModifiers()));
    assertEquals(LocaleState.class, method.getReturnType());
    assertEquals(0, method.getParameterCount());
  }

  @Test
  public void localeState_constructor_whenReflected_isPrivateNoArg() throws Exception {
    Constructor<LocaleState> constructor = LocaleState.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    assertEquals(0, constructor.getParameterCount());
  }

  @Test
  public void localeState_constructors_whenReflected_areNotPublic() {
    for (Constructor<?> constructor : LocaleState.class.getDeclaredConstructors()) {
      assertFalse(Modifier.isPublic(constructor.getModifiers()));
    }
  }

  @Test
  public void localeState_singletonHolder_whenReflected_exists() throws Exception {
    Class<?> holder = Class.forName(LocaleState.class.getName() + "$SingletonHolder", false,
        LocaleState.class.getClassLoader());
    assertTrue(Modifier.isPrivate(holder.getModifiers()));
  }

  @Test
  public void localeState_declaredMethods_whenReflected_includeGetInstance() {
    Method[] methods = LocaleState.class.getDeclaredMethods();
    assertTrue(methods.length >= 1);

    boolean found = false;
    for (Method method : methods) {
      if ("getInstance".equals(method.getName())) {
        found = true;
        assertTrue(Modifier.isPublic(method.getModifiers()));
      }
    }
    assertTrue(found);
  }

  @Test
  public void localeState_declaredConstructors_whenReflected_onlyContainSingletonConstructor() {
    Constructor<?>[] constructors = LocaleState.class.getDeclaredConstructors();
    assertEquals(1, constructors.length);
    assertEquals(0, constructors[0].getParameterCount());
  }

  @Test
  public void localeState_declaredFields_whenReflected_areAbsent() {
    assertEquals(0, LocaleState.class.getDeclaredFields().length);
  }

  @Test
  public void localeState_package_whenReflected_matchesLocaleModelPackage() {
    assertEquals("org.alice.ide.croquet.models.ui.locale", LocaleState.class.getPackage().getName());
  }

  @Test
  public void localeState_inheritedValueMethod_whenReflected_isAvailable() throws Exception {
    Method method = LocaleState.class.getMethod("getValue");
    assertNotNull(method);
    assertFalse(void.class.equals(method.getReturnType()));
  }

  @Test
  public void localeState_declaredInnerClasses_whenReflected_onlyContainSingletonHolder() {
    Class<?>[] classes = LocaleState.class.getDeclaredClasses();
    assertEquals(1, classes.length);
    assertEquals("SingletonHolder", classes[0].getSimpleName());
  }

  @Test
  public void localeState_getInstance_whenReflected_declaresNoCheckedExceptions() throws Exception {
    Method method = LocaleState.class.getMethod("getInstance");
    assertEquals(0, method.getExceptionTypes().length);
  }

  @Test
  public void localeState_superclassName_whenReflected_matchesExpectedTypeName() {
    assertEquals("org.lgna.croquet.preferences.PreferenceMutableDataSingleSelectListState",
        LocaleState.class.getSuperclass().getName());
  }
}
