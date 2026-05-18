package org.alice.ide.typemanager;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Deep tests for {@link TypeManager} covering createClassNameFromSuperType,
 * getEnumConstantFieldIfOneAndOnly, getNamedUserTypeFromSuperType, and
 * inner criterion classes.
 */
public class TypeManagerDeepTest {

  // --- createClassNameFromSuperType ---

  @Test
  public void createClassNameFromSuperType_SBiped_returnsBiped() {
    JavaType bipedType = JavaType.getInstance(org.lgna.story.SBiped.class);
    String name = TypeManager.createClassNameFromSuperType(bipedType);
    assertEquals("Biped", name);
  }

  @Test
  public void createClassNameFromSuperType_SCamera_returnsCamera() {
    JavaType cameraType = JavaType.getInstance(org.lgna.story.SCamera.class);
    String name = TypeManager.createClassNameFromSuperType(cameraType);
    assertEquals("Camera", name);
  }

  @Test
  public void createClassNameFromSuperType_SScene_returnsScene() {
    JavaType sceneType = JavaType.getInstance(org.lgna.story.SScene.class);
    String name = TypeManager.createClassNameFromSuperType(sceneType);
    assertEquals("Scene", name);
  }

  @Test
  public void createClassNameFromSuperType_SModel_returnsModel() {
    JavaType modelType = JavaType.getInstance(org.lgna.story.SModel.class);
    String name = TypeManager.createClassNameFromSuperType(modelType);
    assertEquals("Model", name);
  }

  @Test
  public void createClassNameFromSuperType_SFlyer_returnsFlyer() {
    JavaType flyerType = JavaType.getInstance(org.lgna.story.SFlyer.class);
    String name = TypeManager.createClassNameFromSuperType(flyerType);
    assertEquals("Flyer", name);
  }

  @Test
  public void createClassNameFromSuperType_SQuadruped_returnsQuadruped() {
    JavaType quadType = JavaType.getInstance(org.lgna.story.SQuadruped.class);
    String name = TypeManager.createClassNameFromSuperType(quadType);
    assertEquals("Quadruped", name);
  }

  @Test
  public void createClassNameFromSuperType_nonSPrefix_returnsSameName() {
    // String has no "S" prefix convention
    JavaType stringType = JavaType.getInstance(String.class);
    String name = TypeManager.createClassNameFromSuperType(stringType);
    assertEquals("String", name);
  }

  @Test
  public void createClassNameFromSuperType_singleCharName_returnsSame() {
    // Create a NamedUserType with single char name
    NamedUserType singleChar = new NamedUserType();
    singleChar.name.setValue("X");
    singleChar.superType.setValue(JavaType.getInstance(Object.class));
    String name = TypeManager.createClassNameFromSuperType(singleChar);
    assertEquals("X", name);
  }

  @Test
  public void createClassNameFromSuperType_sLowerCase_returnsSame() {
    // "string" starts with lowercase 's' so doesn't match the S-prefix pattern
    NamedUserType type = new NamedUserType();
    type.name.setValue("sLowerCase");
    type.superType.setValue(JavaType.getInstance(Object.class));
    String name = TypeManager.createClassNameFromSuperType(type);
    assertEquals("sLowerCase", name);
  }

  @Test
  public void createClassNameFromSuperType_SFollowedByLowerCase_returnsSame() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("Slower");
    type.superType.setValue(JavaType.getInstance(Object.class));
    String name = TypeManager.createClassNameFromSuperType(type);
    assertEquals("Slower", name);
  }

  // --- getEnumConstantFieldIfOneAndOnly ---

  @Test
  public void getEnumConstantField_nonEnum_returnsNull() {
    JavaType stringType = JavaType.getInstance(String.class);
    JavaField result = TypeManager.getEnumConstantFieldIfOneAndOnly(stringType);
    assertNull(result);
  }

  @Test
  public void getEnumConstantField_multiValueEnum_returnsNull() {
    // Thread.State has multiple enum constants
    JavaType threadStateType = JavaType.getInstance(Thread.State.class);
    JavaField result = TypeManager.getEnumConstantFieldIfOneAndOnly(threadStateType);
    assertNull(result);
  }

  @Test
  public void getEnumConstantField_userType_returnsNull() {
    NamedUserType userType = new NamedUserType();
    userType.name.setValue("CustomType");
    userType.superType.setValue(JavaType.getInstance(Object.class));
    JavaField result = TypeManager.getEnumConstantFieldIfOneAndOnly(userType);
    assertNull(result);
  }

  // --- getNamedUserTypeFromSuperType ---

  @Test
  public void getNamedUserTypeFromSuperType_SBiped_createsType() {
    JavaType bipedType = JavaType.getInstance(org.lgna.story.SBiped.class);
    NamedUserType result = TypeManager.getNamedUserTypeFromSuperType(bipedType);
    assertNotNull(result);
    assertEquals("Biped", result.getName());
  }

  @Test
  public void getNamedUserTypeFromSuperType_hasSuperTypeSet() {
    JavaType bipedType = JavaType.getInstance(org.lgna.story.SBiped.class);
    NamedUserType result = TypeManager.getNamedUserTypeFromSuperType(bipedType);
    assertNotNull(result.superType.getValue());
    assertEquals(bipedType, result.superType.getValue());
  }

  @Test
  public void getNamedUserTypeFromSuperType_hasConstructors() {
    JavaType bipedType = JavaType.getInstance(org.lgna.story.SBiped.class);
    NamedUserType result = TypeManager.getNamedUserTypeFromSuperType(bipedType);
    assertFalse(result.constructors.isEmpty());
  }

  @Test
  public void getNamedUserTypeFromSuperType_SScene_createsSceneType() {
    JavaType sceneType = JavaType.getInstance(org.lgna.story.SScene.class);
    NamedUserType result = TypeManager.getNamedUserTypeFromSuperType(sceneType);
    assertNotNull(result);
    assertEquals("Scene", result.getName());
    assertNotNull(result.superType.getValue());
  }

  @Test
  public void getNamedUserTypeFromSuperType_SCamera_createsCameraType() {
    JavaType cameraType = JavaType.getInstance(org.lgna.story.SCamera.class);
    NamedUserType result = TypeManager.getNamedUserTypeFromSuperType(cameraType);
    assertNotNull(result);
    assertEquals("Camera", result.getName());
  }

  @Test
  public void getNamedUserTypeFromSuperType_constructorHasBody() {
    JavaType bipedType = JavaType.getInstance(org.lgna.story.SBiped.class);
    NamedUserType result = TypeManager.getNamedUserTypeFromSuperType(bipedType);
    for (NamedUserConstructor constructor : result.constructors) {
      assertNotNull(constructor.body.getValue());
      assertNotNull(constructor.body.getValue().constructorInvocationStatement.getValue());
    }
  }

  // --- createClassNameFromArgumentField ---

  @Test
  public void createClassNameFromSuperType_SMarker_returnsMarker() {
    JavaType markerType = JavaType.getInstance(org.lgna.story.SMarker.class);
    String name = TypeManager.createClassNameFromSuperType(markerType);
    assertEquals("Marker", name);
  }

  @Test
  public void createClassNameFromSuperType_SThing_returnsThing() {
    JavaType thingType = JavaType.getInstance(org.lgna.story.SThing.class);
    String name = TypeManager.createClassNameFromSuperType(thingType);
    assertEquals("Thing", name);
  }

  @Test
  public void getNamedUserTypeFromSuperType_SFlyer_createsFlyerType() {
    JavaType flyerType = JavaType.getInstance(org.lgna.story.SFlyer.class);
    NamedUserType result = TypeManager.getNamedUserTypeFromSuperType(flyerType);
    assertNotNull(result);
    assertEquals("Flyer", result.getName());
    assertEquals(flyerType, result.superType.getValue());
  }

  @Test
  public void getNamedUserTypeFromSuperType_SQuadruped_createsQuadrupedType() {
    JavaType quadType = JavaType.getInstance(org.lgna.story.SQuadruped.class);
    NamedUserType result = TypeManager.getNamedUserTypeFromSuperType(quadType);
    assertNotNull(result);
    assertEquals("Quadruped", result.getName());
  }
}
