package org.alice.ide.typemanager;

import org.junit.Test;
import org.lgna.project.ast.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Extended coverage tests for {@link TypeManager} — covers getNamedUserTypesFromSuperTypes,
 * getTypeCache, constructor guard, createClassNameFromArgumentField, inner criterion classes,
 * and the S-prefix stripping edge cases not covered by existing tests.
 */
public class TypeManagerExtendedTest {

  // ── constructor guard ──────────────────────────────────────────────

  @Test(expected = InvocationTargetException.class)
  public void constructor_throwsAssertionError() throws Exception {
    Constructor<TypeManager> ctor = TypeManager.class.getDeclaredConstructor();
    ctor.setAccessible(true);
    ctor.newInstance();
  }

  @Test
  public void constructor_isPublic() {
    Constructor<?>[] ctors = TypeManager.class.getDeclaredConstructors();
    assertEquals("Should have exactly one declared constructor", 1, ctors.length);
    assertTrue("Constructor should be public",
        Modifier.isPublic(ctors[0].getModifiers()));
  }

  // ── getTypeCache without project ───────────────────────────────────

  @Test
  public void getTypeCache_withoutProject_returnsEmptySet() {
    Set<NamedUserType> cache = TypeManager.getTypeCache();
    assertNotNull("Cache should never be null", cache);
    assertTrue("Cache should be empty when no project is active", cache.isEmpty());
  }

  @Test
  public void getTypeCache_returnsModifiableSet() {
    Set<NamedUserType> cache = TypeManager.getTypeCache();
    NamedUserType dummy = new NamedUserType();
    dummy.name.setValue("Dummy");
    dummy.superType.setValue(JavaType.getInstance(Object.class));
    cache.add(dummy);
    assertTrue("Should be able to add to cache set", cache.contains(dummy));
  }

  // ── getNamedUserTypesFromSuperTypes ────────────────────────────────

  @Test
  public void getNamedUserTypesFromSuperTypes_emptyCollection_returnsEmptyList() {
    List<NamedUserType> result = TypeManager.getNamedUserTypesFromSuperTypes(Collections.emptyList());
    assertNotNull(result);
    assertTrue("Empty input → empty output", result.isEmpty());
  }

  @Test
  public void getNamedUserTypesFromSuperTypes_singleType_returnsOneElement() {
    JavaType bipedType = JavaType.getInstance(org.lgna.story.SBiped.class);
    List<NamedUserType> result = TypeManager.getNamedUserTypesFromSuperTypes(
        Collections.singletonList(bipedType));
    assertEquals(1, result.size());
    assertEquals("Biped", result.get(0).getName());
  }

  @Test
  public void getNamedUserTypesFromSuperTypes_multipleTypes_returnsCorrectCount() {
    List<JavaType> superTypes = Arrays.asList(
        JavaType.getInstance(org.lgna.story.SBiped.class),
        JavaType.getInstance(org.lgna.story.SScene.class),
        JavaType.getInstance(org.lgna.story.SCamera.class)
    );
    List<NamedUserType> result = TypeManager.getNamedUserTypesFromSuperTypes(superTypes);
    assertEquals(3, result.size());
  }

  @Test
  public void getNamedUserTypesFromSuperTypes_namesMatchSPrefix() {
    List<JavaType> superTypes = Arrays.asList(
        JavaType.getInstance(org.lgna.story.SFlyer.class),
        JavaType.getInstance(org.lgna.story.SQuadruped.class)
    );
    List<NamedUserType> result = TypeManager.getNamedUserTypesFromSuperTypes(superTypes);
    assertEquals("Flyer", result.get(0).getName());
    assertEquals("Quadruped", result.get(1).getName());
  }

  @Test
  public void getNamedUserTypesFromSuperTypes_eachHasSuperTypeSet() {
    JavaType sceneType = JavaType.getInstance(org.lgna.story.SScene.class);
    List<NamedUserType> result = TypeManager.getNamedUserTypesFromSuperTypes(
        Collections.singletonList(sceneType));
    assertEquals(sceneType, result.get(0).superType.getValue());
  }

  @Test
  public void getNamedUserTypesFromSuperTypes_eachHasConstructors() {
    JavaType bipedType = JavaType.getInstance(org.lgna.story.SBiped.class);
    List<NamedUserType> result = TypeManager.getNamedUserTypesFromSuperTypes(
        Collections.singletonList(bipedType));
    assertFalse("Created type should have constructors",
        result.get(0).constructors.isEmpty());
  }

  // ── createClassNameFromSuperType S-prefix edge cases ───────────────

  @Test
  public void createClassNameFromSuperType_emptyName_returnsEmpty() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("");
    type.superType.setValue(JavaType.getInstance(Object.class));
    assertEquals("", TypeManager.createClassNameFromSuperType(type));
  }

  @Test
  public void createClassNameFromSuperType_singleS_returnsS() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("S");
    type.superType.setValue(JavaType.getInstance(Object.class));
    assertEquals("S", TypeManager.createClassNameFromSuperType(type));
  }

  @Test
  public void createClassNameFromSuperType_SFollowedByDigit_returnsSame() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("S3D");
    type.superType.setValue(JavaType.getInstance(Object.class));
    assertEquals("S3D", TypeManager.createClassNameFromSuperType(type));
  }

  @Test
  public void createClassNameFromSuperType_SsLowerCase_returnsSame() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("Ss");
    type.superType.setValue(JavaType.getInstance(Object.class));
    assertEquals("Ss", TypeManager.createClassNameFromSuperType(type));
  }

  @Test
  public void createClassNameFromSuperType_SUpperCase_stripsS() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("SA");
    type.superType.setValue(JavaType.getInstance(Object.class));
    assertEquals("A", TypeManager.createClassNameFromSuperType(type));
  }

  // ── getEnumConstantFieldIfOneAndOnly ───────────────────────────────

  @Test
  public void getEnumConstantField_null_returnsNull() {
    assertNull(TypeManager.getEnumConstantFieldIfOneAndOnly(null));
  }

  @Test
  public void getEnumConstantField_namedUserType_returnsNull() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("CustomType");
    type.superType.setValue(JavaType.getInstance(Object.class));
    assertNull(TypeManager.getEnumConstantFieldIfOneAndOnly(type));
  }

  @Test
  public void getEnumConstantField_nonEnumJavaType_returnsNull() {
    assertNull(TypeManager.getEnumConstantFieldIfOneAndOnly(
        JavaType.getInstance(String.class)));
  }

  @Test
  public void getEnumConstantField_multiConstantEnum_returnsNull() {
    assertNull(TypeManager.getEnumConstantFieldIfOneAndOnly(
        JavaType.getInstance(Thread.State.class)));
  }

  // ── getNamedUserTypeFromSuperType ──────────────────────────────────

  @Test
  public void getNamedUserTypeFromSuperType_constructorBodyIsNotNull() {
    JavaType cameraType = JavaType.getInstance(org.lgna.story.SCamera.class);
    NamedUserType result = TypeManager.getNamedUserTypeFromSuperType(cameraType);
    for (NamedUserConstructor ctor : result.constructors) {
      assertNotNull("Constructor should have a body", ctor.body.getValue());
    }
  }

  @Test
  public void getNamedUserTypeFromSuperType_constructorHasSuperInvocation() {
    JavaType markerType = JavaType.getInstance(org.lgna.story.SMarker.class);
    NamedUserType result = TypeManager.getNamedUserTypeFromSuperType(markerType);
    for (NamedUserConstructor ctor : result.constructors) {
      ConstructorInvocationStatement invocation =
          ctor.body.getValue().constructorInvocationStatement.getValue();
      assertNotNull("Constructor should have super invocation", invocation);
      assertTrue("Should be SuperConstructorInvocationStatement",
          invocation instanceof SuperConstructorInvocationStatement);
    }
  }

  // ── inner criterion classes (reflection) ───────────────────────────

  @Test
  public void matchesNameTypeCriterion_existsAsPrivateStaticClass() throws Exception {
    Class<?>[] declaredClasses = TypeManager.class.getDeclaredClasses();
    boolean found = false;
    for (Class<?> cls : declaredClasses) {
      if (cls.getSimpleName().equals("MatchesNameTypeCriterion")) {
        assertTrue("Should be private", Modifier.isPrivate(cls.getModifiers()));
        assertTrue("Should be static", Modifier.isStatic(cls.getModifiers()));
        found = true;
        break;
      }
    }
    assertTrue("MatchesNameTypeCriterion inner class should exist", found);
  }

  @Test
  public void extendsTypeCriterion_existsAsPrivateAbstractStaticClass() throws Exception {
    Class<?>[] declaredClasses = TypeManager.class.getDeclaredClasses();
    boolean found = false;
    for (Class<?> cls : declaredClasses) {
      if (cls.getSimpleName().equals("ExtendsTypeCriterion")) {
        assertTrue("Should be private", Modifier.isPrivate(cls.getModifiers()));
        assertTrue("Should be abstract", Modifier.isAbstract(cls.getModifiers()));
        assertTrue("Should be static", Modifier.isStatic(cls.getModifiers()));
        found = true;
        break;
      }
    }
    assertTrue("ExtendsTypeCriterion inner class should exist", found);
  }

  @Test
  public void defaultConstructorExtendsTypeCriterion_existsAsFinalStaticClass() {
    Class<?>[] declaredClasses = TypeManager.class.getDeclaredClasses();
    boolean found = false;
    for (Class<?> cls : declaredClasses) {
      if (cls.getSimpleName().equals("DefaultConstructorExtendsTypeCriterion")) {
        assertTrue("Should be final", Modifier.isFinal(cls.getModifiers()));
        assertTrue("Should be static", Modifier.isStatic(cls.getModifiers()));
        found = true;
        break;
      }
    }
    assertTrue("DefaultConstructorExtendsTypeCriterion should exist", found);
  }

  @Test
  public void extendsTypeWithConstructorParameterTypeCriterion_exists() {
    Class<?>[] declaredClasses = TypeManager.class.getDeclaredClasses();
    boolean found = false;
    for (Class<?> cls : declaredClasses) {
      if (cls.getSimpleName().equals("ExtendsTypeWithConstructorParameterTypeCriterion")) {
        found = true;
        break;
      }
    }
    assertTrue("ExtendsTypeWithConstructorParameterTypeCriterion should exist", found);
  }

  @Test
  public void extendsTypeWithSuperArgumentFieldCriterion_exists() {
    Class<?>[] declaredClasses = TypeManager.class.getDeclaredClasses();
    boolean found = false;
    for (Class<?> cls : declaredClasses) {
      if (cls.getSimpleName().equals("ExtendsTypeWithSuperArgumentFieldCriterion")) {
        found = true;
        break;
      }
    }
    assertTrue("ExtendsTypeWithSuperArgumentFieldCriterion should exist", found);
  }

  @Test
  public void extendsTypeWithSuperArgumentExpressionsCriterion_exists() {
    Class<?>[] declaredClasses = TypeManager.class.getDeclaredClasses();
    boolean found = false;
    for (Class<?> cls : declaredClasses) {
      if (cls.getSimpleName().equals("ExtendsTypeWithSuperArgumentExpressionsCriterion")) {
        found = true;
        break;
      }
    }
    assertTrue("ExtendsTypeWithSuperArgumentExpressionsCriterion should exist", found);
  }

  @Test
  public void innerCriterionClasses_totalCount() {
    Class<?>[] declaredClasses = TypeManager.class.getDeclaredClasses();
    assertTrue("Should have at least 5 inner classes", declaredClasses.length >= 5);
  }
}
