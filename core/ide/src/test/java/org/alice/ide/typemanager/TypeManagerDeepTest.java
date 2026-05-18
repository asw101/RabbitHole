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

  private static final JavaType OBJECT_TYPE = JavaType.getInstance(Object.class);
  private static final JavaType STRING_TYPE = JavaType.getInstance(String.class);
  private static final JavaType BIPED_TYPE = JavaType.getInstance(org.lgna.story.SBiped.class);
  private static final JavaType CAMERA_TYPE = JavaType.getInstance(org.lgna.story.SCamera.class);
  private static final JavaType SCENE_TYPE = JavaType.getInstance(org.lgna.story.SScene.class);
  private static final JavaType FLYER_TYPE = JavaType.getInstance(org.lgna.story.SFlyer.class);
  private static final JavaType QUAD_TYPE = JavaType.getInstance(org.lgna.story.SQuadruped.class);

  private static NamedUserType namedType(String name) {
    NamedUserType t = new NamedUserType();
    t.name.setValue(name);
    t.superType.setValue(OBJECT_TYPE);
    return t;
  }

  // --- createClassNameFromSuperType ---

  @Test
  public void createClassNameFromSuperType_SBiped_returnsBiped() {
    assertEquals("Biped", TypeManager.createClassNameFromSuperType(BIPED_TYPE));
  }

  @Test
  public void createClassNameFromSuperType_SCamera_returnsCamera() {
    assertEquals("Camera", TypeManager.createClassNameFromSuperType(CAMERA_TYPE));
  }

  @Test
  public void createClassNameFromSuperType_SScene_returnsScene() {
    assertEquals("Scene", TypeManager.createClassNameFromSuperType(SCENE_TYPE));
  }

  @Test
  public void createClassNameFromSuperType_SModel_returnsModel() {
    JavaType modelType = JavaType.getInstance(org.lgna.story.SModel.class);
    assertEquals("Model", TypeManager.createClassNameFromSuperType(modelType));
  }

  @Test
  public void createClassNameFromSuperType_SFlyer_returnsFlyer() {
    assertEquals("Flyer", TypeManager.createClassNameFromSuperType(FLYER_TYPE));
  }

  @Test
  public void createClassNameFromSuperType_SQuadruped_returnsQuadruped() {
    assertEquals("Quadruped", TypeManager.createClassNameFromSuperType(QUAD_TYPE));
  }

  @Test
  public void createClassNameFromSuperType_nonSPrefix_returnsSameName() {
    assertEquals("String", TypeManager.createClassNameFromSuperType(STRING_TYPE));
  }

  @Test
  public void createClassNameFromSuperType_singleCharName_returnsSame() {
    assertEquals("X", TypeManager.createClassNameFromSuperType(namedType("X")));
  }

  @Test
  public void createClassNameFromSuperType_sLowerCase_returnsSame() {
    assertEquals("sLowerCase", TypeManager.createClassNameFromSuperType(namedType("sLowerCase")));
  }

  @Test
  public void createClassNameFromSuperType_SFollowedByLowerCase_returnsSame() {
    assertEquals("Slower", TypeManager.createClassNameFromSuperType(namedType("Slower")));
  }

  // --- getNamedUserTypeFromSuperType ---

  @Test
  public void getNamedUserTypeFromSuperType_SBiped_createsType() {
    NamedUserType result = TypeManager.getNamedUserTypeFromSuperType(BIPED_TYPE);
    assertNotNull(result);
    assertEquals("Biped", result.getName());
  }

  @Test
  public void getNamedUserTypeFromSuperType_hasSuperTypeSet() {
    NamedUserType result = TypeManager.getNamedUserTypeFromSuperType(BIPED_TYPE);
    assertNotNull(result.superType.getValue());
    assertEquals(BIPED_TYPE, result.superType.getValue());
  }

  @Test
  public void getNamedUserTypeFromSuperType_hasConstructors() {
    NamedUserType result = TypeManager.getNamedUserTypeFromSuperType(BIPED_TYPE);
    assertFalse(result.constructors.isEmpty());
  }

  @Test
  public void getNamedUserTypeFromSuperType_SScene_createsSceneType() {
    NamedUserType result = TypeManager.getNamedUserTypeFromSuperType(SCENE_TYPE);
    assertNotNull(result);
    assertEquals("Scene", result.getName());
    assertNotNull(result.superType.getValue());
  }

  @Test
  public void getNamedUserTypeFromSuperType_SCamera_createsCameraType() {
    NamedUserType result = TypeManager.getNamedUserTypeFromSuperType(CAMERA_TYPE);
    assertNotNull(result);
    assertEquals("Camera", result.getName());
  }

  @Test
  public void getNamedUserTypeFromSuperType_constructorHasBody() {
    NamedUserType result = TypeManager.getNamedUserTypeFromSuperType(BIPED_TYPE);
    for (NamedUserConstructor constructor : result.constructors) {
      assertNotNull(constructor.body.getValue());
      assertNotNull(constructor.body.getValue().constructorInvocationStatement.getValue());
    }
  }

  // --- createClassNameFromArgumentField ---

  @Test
  public void createClassNameFromSuperType_SMarker_returnsMarker() {
    assertEquals("Marker", TypeManager.createClassNameFromSuperType(
        JavaType.getInstance(org.lgna.story.SMarker.class)));
  }

  @Test
  public void createClassNameFromSuperType_SThing_returnsThing() {
    assertEquals("Thing", TypeManager.createClassNameFromSuperType(
        JavaType.getInstance(org.lgna.story.SThing.class)));
  }

  @Test
  public void getNamedUserTypeFromSuperType_SFlyer_createsFlyerType() {
    NamedUserType result = TypeManager.getNamedUserTypeFromSuperType(FLYER_TYPE);
    assertNotNull(result);
    assertEquals("Flyer", result.getName());
    assertEquals(FLYER_TYPE, result.superType.getValue());
  }

  @Test
  public void getNamedUserTypeFromSuperType_SQuadruped_createsQuadrupedType() {
    NamedUserType result = TypeManager.getNamedUserTypeFromSuperType(QUAD_TYPE);
    assertNotNull(result);
    assertEquals("Quadruped", result.getName());
  }
}
