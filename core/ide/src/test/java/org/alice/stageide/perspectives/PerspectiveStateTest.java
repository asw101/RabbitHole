package org.alice.stageide.perspectives;

import static org.alice.ide.testing.JacocoReflectionSupport.declaredMethods;
import static org.alice.ide.testing.JacocoReflectionSupport.declaredFields;

import org.alice.ide.perspectives.ProjectPerspective;
import org.junit.Test;
import org.lgna.croquet.ImmutableDataSingleSelectListState;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.junit.Assert.*;

public class PerspectiveStateTest {

  // Reflection-only coverage keeps this test safe from Croquet application initialization.
  private Constructor<PerspectiveState> getConstructor() throws Exception {
    return PerspectiveState.class.getConstructor(ProjectPerspective[].class);
  }

  @Test
  public void class_isLoadable() {
    assertNotNull(PerspectiveState.class);
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(PerspectiveState.class.getModifiers()));
  }

  @Test
  public void class_isConcrete() {
    assertFalse(Modifier.isAbstract(PerspectiveState.class.getModifiers()));
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(PerspectiveState.class.getModifiers()));
  }

  @Test
  public void class_extendsImmutableDataSingleSelectListState() {
    assertEquals(ImmutableDataSingleSelectListState.class, PerspectiveState.class.getSuperclass());
  }

  @Test
  public void class_isAssignableToImmutableDataSingleSelectListState() {
    assertTrue(ImmutableDataSingleSelectListState.class.isAssignableFrom(PerspectiveState.class));
  }

  @Test
  public void genericSuperclass_projectPerspectiveTypeArgument_isPreserved() {
    Type superType = PerspectiveState.class.getGenericSuperclass();
    assertTrue(superType instanceof ParameterizedType);
    ParameterizedType parameterizedType = (ParameterizedType) superType;
    assertEquals(ProjectPerspective.class, parameterizedType.getActualTypeArguments()[0]);
  }

  @Test
  public void constructor_projectPerspectiveVarArgs_exists() throws Exception {
    assertNotNull(getConstructor());
  }

  @Test
  public void constructor_projectPerspectiveVarArgs_isPublic() throws Exception {
    assertTrue(Modifier.isPublic(getConstructor().getModifiers()));
  }

  @Test
  public void constructor_projectPerspectiveVarArgs_isVarArgs() throws Exception {
    assertTrue(getConstructor().isVarArgs());
  }

  @Test
  public void constructor_projectPerspectiveVarArgs_acceptsSingleArrayParameter() throws Exception {
    Class<?>[] parameterTypes = getConstructor().getParameterTypes();
    assertEquals(1, parameterTypes.length);
    assertEquals(ProjectPerspective[].class, parameterTypes[0]);
  }

  @Test
  public void constructor_projectPerspectiveVarArgs_componentType_isProjectPerspective() throws Exception {
    assertEquals(ProjectPerspective.class, getConstructor().getParameterTypes()[0].getComponentType());
  }

  @Test
  public void constructor_projectPerspectiveVarArgs_declaringClass_isPerspectiveState() throws Exception {
    assertEquals(PerspectiveState.class, getConstructor().getDeclaringClass());
  }

  @Test
  public void constructor_projectPerspectiveVarArgs_declaresNoCheckedExceptions() throws Exception {
    assertEquals(0, getConstructor().getExceptionTypes().length);
  }

  @Test
  public void constructors_onlyOneConstructor_exists() {
    assertEquals(1, PerspectiveState.class.getDeclaredConstructors().length);
  }

  @Test
  public void declaredFields_classDefinesNoStateOfItsOwn() {
    assertEquals(0, declaredFields(PerspectiveState.class).length);
  }

  @Test
  public void declaredMethods_classDefinesNoAdditionalMethods() {
    assertEquals(0, declaredMethods(PerspectiveState.class).length);
  }

  @Test
  public void declaredClasses_classDefinesNoNestedTypes() {
    assertEquals(0, PerspectiveState.class.getDeclaredClasses().length);
  }

  @Test
  public void className_structureTest_matchesExpectedName() {
    assertEquals("PerspectiveState", PerspectiveState.class.getSimpleName());
  }
}
