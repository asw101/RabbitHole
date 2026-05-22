package org.alice.ide.croquet.models.menubar;

import org.junit.Test;
import org.lgna.croquet.PredeterminedMenuModel;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class AllowNullMenuModelComprehensiveTest {

  @Test
  public void allowNullMenuModel_isPublicConcreteSubclass() {
    assertTrue(Modifier.isPublic(AllowNullMenuModel.class.getModifiers()));
    assertFalse(Modifier.isAbstract(AllowNullMenuModel.class.getModifiers()));
    assertTrue(PredeterminedMenuModel.class.isAssignableFrom(AllowNullMenuModel.class));
  }

  @Test
  public void allowNullMenuModel_declaresSinglePrivateConstructor() {
    Constructor<?>[] constructors = AllowNullMenuModel.class.getDeclaredConstructors();
    assertEquals(1, constructors.length);
    assertEquals(0, constructors[0].getParameterTypes().length);
    assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
  }

  @Test
  public void allowNullMenuModel_declaresPublicStaticGetInstance() throws Exception {
    Method method = AllowNullMenuModel.class.getMethod("getInstance");
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isStatic(method.getModifiers()));
    assertEquals(AllowNullMenuModel.class, method.getReturnType());
  }

  @Test
  public void getInstance_returnsNonNullSingleton() {
    assertNotNull(AllowNullMenuModel.getInstance());
  }

  @Test
  public void getInstance_returnsSameSingletonEachTime() {
    assertSame(AllowNullMenuModel.getInstance(), AllowNullMenuModel.getInstance());
  }

  @Test
  public void singleton_isExactAllowNullMenuModelType() {
    assertEquals(AllowNullMenuModel.class, AllowNullMenuModel.getInstance().getClass());
  }

  @Test
  public void singleton_isAlsoPredeterminedMenuModel() {
    assertTrue(AllowNullMenuModel.getInstance() instanceof PredeterminedMenuModel);
  }

  @Test
  public void singletonHolder_nestedClassExists() throws Exception {
    Class<?> holder = Class.forName("org.alice.ide.croquet.models.menubar.AllowNullMenuModel$SingletonHolder");
    assertNotNull(holder);
  }

  @Test
  public void singletonHolder_isPrivateStaticClass() throws Exception {
    Class<?> holder = Class.forName("org.alice.ide.croquet.models.menubar.AllowNullMenuModel$SingletonHolder");
    assertTrue(Modifier.isPrivate(holder.getModifiers()));
    assertTrue(Modifier.isStatic(holder.getModifiers()));
  }

  @Test
  public void singletonHolder_declaresStaticInstanceField() throws Exception {
    Class<?> holder = Class.forName("org.alice.ide.croquet.models.menubar.AllowNullMenuModel$SingletonHolder");
    Field field = holder.getDeclaredField("instance");
    assertTrue(Modifier.isStatic(field.getModifiers()));
    assertEquals(AllowNullMenuModel.class, field.getType());
  }

  @Test
  public void singletonHolder_hasSingleConstructor() throws Exception {
    Class<?> holder = Class.forName("org.alice.ide.croquet.models.menubar.AllowNullMenuModel$SingletonHolder");
    assertEquals(1, holder.getDeclaredConstructors().length);
  }

  @Test
  public void allowNullMenuModel_declaresNoAdditionalFields() {
    assertEquals(0, AllowNullMenuModel.class.getDeclaredFields().length);
  }

  @Test
  public void allowNullMenuModel_declaresOnlyGetInstanceMethod() {
    assertEquals(1, AllowNullMenuModel.class.getDeclaredMethods().length);
    assertEquals("getInstance", AllowNullMenuModel.class.getDeclaredMethods()[0].getName());
  }

  @Test
  public void allowNullMenuModel_isTopLevelClass() {
    assertNull(AllowNullMenuModel.class.getEnclosingClass());
  }

  @Test
  public void allowNullMenuModel_isNotFinalOrEnum() {
    assertFalse(Modifier.isFinal(AllowNullMenuModel.class.getModifiers()));
    assertFalse(AllowNullMenuModel.class.isEnum());
  }

  @Test
  public void allowNullMenuModel_isNotInterfaceOrAnnotation() {
    assertFalse(AllowNullMenuModel.class.isInterface());
    assertFalse(AllowNullMenuModel.class.isAnnotation());
  }

  @Test
  public void allowNullMenuModel_packageNameMatchesSource() {
    assertEquals("org.alice.ide.croquet.models.menubar", AllowNullMenuModel.class.getPackage().getName());
  }

  @Test
  public void allowNullMenuModel_simpleNameMatchesSource() {
    assertEquals("AllowNullMenuModel", AllowNullMenuModel.class.getSimpleName());
  }

  @Test
  public void getInstance_methodIsNotSynchronized() throws Exception {
    Method method = AllowNullMenuModel.class.getMethod("getInstance");
    assertFalse(Modifier.isSynchronized(method.getModifiers()));
  }

  @Test
  public void getInstance_methodAcceptsNoParameters() throws Exception {
    Method method = AllowNullMenuModel.class.getMethod("getInstance");
    assertEquals(0, method.getParameterTypes().length);
  }

  @Test
  public void singletonReference_isStableAcrossVariables() {
    AllowNullMenuModel first = AllowNullMenuModel.getInstance();
    AllowNullMenuModel second = AllowNullMenuModel.getInstance();
    assertSame(first, second);
  }

  @Test
  public void singletonHolder_declaresPrivateConstructor() throws Exception {
    Class<?> holder = Class.forName("org.alice.ide.croquet.models.menubar.AllowNullMenuModel$SingletonHolder");
    assertTrue(Modifier.isPrivate(holder.getDeclaredConstructors()[0].getModifiers()));
  }

  @Test
  public void singletonHolder_instanceFieldMatchesSingletonValue() throws Exception {
    Class<?> holder = Class.forName("org.alice.ide.croquet.models.menubar.AllowNullMenuModel$SingletonHolder");
    Field field = holder.getDeclaredField("instance");
    field.setAccessible(true);
    assertSame(AllowNullMenuModel.getInstance(), field.get(null));
  }

  @Test
  public void allowNullMenuModel_declaresSingleNestedClass() {
    assertEquals(1, AllowNullMenuModel.class.getDeclaredClasses().length);
  }

  @Test
  public void allowNullMenuModel_implementsNoInterfacesDirectly() {
    assertEquals(0, AllowNullMenuModel.class.getInterfaces().length);
  }

  @Test
  public void allowNullMenuModel_hasNoDeclaredPublicFields() {
    long count = java.util.Arrays.stream(AllowNullMenuModel.class.getDeclaredFields())
        .filter(field -> Modifier.isPublic(field.getModifiers()))
        .count();
    assertEquals(0L, count);
  }

  @Test
  public void getInstance_canBeFoundByName() {
    boolean found = java.util.Arrays.stream(AllowNullMenuModel.class.getDeclaredMethods())
        .anyMatch(method -> method.getName().equals("getInstance"));
    assertTrue(found);
  }

  @Test
  public void constructorParameterCount_isZero() {
    assertEquals(0, AllowNullMenuModel.class.getDeclaredConstructors()[0].getParameterCount());
  }

  @Test
  public void allowNullMenuModel_classLoaderIsAvailable() {
    assertNotNull(AllowNullMenuModel.class.getClassLoader());
  }


  @Test
  public void allowNullMenuModel_declaresNoTypeParameters() {
    assertEquals(0, AllowNullMenuModel.class.getTypeParameters().length);
  }

  @Test
  public void singletonHolder_simpleNameMatchesExpectation() throws Exception {
    assertEquals("SingletonHolder", Class.forName("org.alice.ide.croquet.models.menubar.AllowNullMenuModel$SingletonHolder").getSimpleName());
  }


  @Test
  public void allowNullMenuModel_declaresOneNestedType() {
    assertEquals(1, AllowNullMenuModel.class.getDeclaredClasses().length);
  }

}
