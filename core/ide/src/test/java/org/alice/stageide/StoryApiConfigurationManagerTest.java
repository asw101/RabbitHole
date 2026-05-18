package org.alice.stageide;

import org.junit.Test;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;
import org.lgna.story.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link StoryApiConfigurationManager} — static constants,
 * type checking, and method configuration logic.
 */
public class StoryApiConfigurationManagerTest {

  // ---- Static constants ----

  @Test
  public void setActiveSceneMethod_isNotNull() {
    assertNotNull(StoryApiConfigurationManager.SET_ACTIVE_SCENE_METHOD);
  }

  @Test
  public void setActiveSceneMethod_hasCorrectName() {
    assertEquals("setActiveScene", StoryApiConfigurationManager.SET_ACTIVE_SCENE_METHOD.getName());
  }

  @Test
  public void bipedResourceType_isNotNull() {
    assertNotNull(StoryApiConfigurationManager.BIPED_RESOURCE_TYPE);
  }

  @Test
  public void bipedResourceType_nameContainsBiped() {
    String name = StoryApiConfigurationManager.BIPED_RESOURCE_TYPE.getName();
    assertTrue(name.contains("BipedResource"));
  }

  // ---- Type system queries ----

  @Test
  public void sThingType_isAssignableFromSSphere() {
    JavaType thingType = JavaType.getInstance(SThing.class);
    JavaType sphereType = JavaType.getInstance(SSphere.class);
    assertTrue(thingType.isAssignableFrom(sphereType));
  }

  @Test
  public void sSceneType_isNotAssignableFromSThing() {
    JavaType sceneType = JavaType.getInstance(SScene.class);
    JavaType thingType = JavaType.getInstance(SThing.class);
    assertFalse(sceneType.isAssignableFrom(thingType));
  }

  @Test
  public void sMarkerType_isAssignableToSThing() {
    JavaType markerType = JavaType.getInstance(SMarker.class);
    assertTrue(markerType.isAssignableTo(SThing.class));
  }

  @Test
  public void sProgramType_isNotAssignableToSThing() {
    JavaType programType = JavaType.getInstance(SProgram.class);
    assertFalse(programType.isAssignableTo(SThing.class));
  }

  // ---- JavaType constants used internally ----

  @Test
  public void cameraType_resolves() {
    JavaType cameraType = JavaType.getInstance(SCamera.class);
    assertNotNull(cameraType);
    assertTrue(cameraType.getName().contains("Camera"));
  }

  @Test
  public void vrUserType_resolves() {
    JavaType vrUserType = JavaType.getInstance(SVRUser.class);
    assertNotNull(vrUserType);
  }

  @Test
  public void jointType_resolves() {
    JavaType jointType = JavaType.getInstance(SJoint.class);
    assertNotNull(jointType);
  }

  // ---- Shape classes used in icon registration ----

  @Test
  public void shapeClasses_resolve() {
    assertNotNull(JavaType.getInstance(SSphere.class));
    assertNotNull(JavaType.getInstance(SCylinder.class));
    assertNotNull(JavaType.getInstance(SCone.class));
    assertNotNull(JavaType.getInstance(SDisc.class));
    assertNotNull(JavaType.getInstance(STorus.class));
    assertNotNull(JavaType.getInstance(SBox.class));
    assertNotNull(JavaType.getInstance(SGround.class));
    assertNotNull(JavaType.getInstance(STextModel.class));
    assertNotNull(JavaType.getInstance(SBillboard.class));
  }

  // ---- Story direction enums ----

  @Test
  public void moveDirectionType_resolves() {
    JavaType type = JavaType.getInstance(MoveDirection.class);
    assertNotNull(type);
    assertTrue(type.isAssignableTo(Enum.class));
  }

  @Test
  public void turnDirectionType_resolves() {
    JavaType type = JavaType.getInstance(TurnDirection.class);
    assertNotNull(type);
    assertTrue(type.isAssignableTo(Enum.class));
  }

  @Test
  public void rollDirectionType_resolves() {
    JavaType type = JavaType.getInstance(RollDirection.class);
    assertNotNull(type);
    assertTrue(type.isAssignableTo(Enum.class));
  }

  @Test
  public void keyType_resolves() {
    JavaType type = JavaType.getInstance(Key.class);
    assertNotNull(type);
    assertTrue(type.isAssignableTo(Enum.class));
  }

  // ---- Color and Paint types ----

  @Test
  public void paintType_resolves() {
    JavaType type = JavaType.getInstance(Paint.class);
    assertNotNull(type);
  }

  @Test
  public void colorType_resolves() {
    JavaType type = JavaType.getInstance(Color.class);
    assertNotNull(type);
  }

  // ---- AudioSource type ----

  @Test
  public void audioSourceType_resolves() {
    JavaType type = JavaType.getInstance(AudioSource.class);
    assertNotNull(type);
  }
}
