package org.alice.stageide;

import org.junit.Test;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;
import org.lgna.story.SProgram;
import org.lgna.story.SScene;
import org.lgna.story.resources.BipedResource;

import static org.junit.Assert.*;

public class StoryApiConfigurationManagerTest {
  @Test
  public void setActiveSceneMethodMatchesCachedLookup() {
    JavaMethod expected = JavaMethod.getInstance(SProgram.class, "setActiveScene", SScene.class);

    assertSame(expected, StoryApiConfigurationManager.SET_ACTIVE_SCENE_METHOD);
  }

  @Test
  public void setActiveSceneMethodHasExpectedName() {
    assertEquals("setActiveScene", StoryApiConfigurationManager.SET_ACTIVE_SCENE_METHOD.getName());
  }

  @Test
  public void setActiveSceneMethodIsDeclaredOnProgram() {
    assertEquals(JavaType.getInstance(SProgram.class), StoryApiConfigurationManager.SET_ACTIVE_SCENE_METHOD.getDeclaringType());
  }

  @Test
  public void setActiveSceneMethodRequiresSingleSceneParameter() {
    assertEquals(1, StoryApiConfigurationManager.SET_ACTIVE_SCENE_METHOD.getRequiredParameters().size());
    assertEquals(
        JavaType.getInstance(SScene.class),
        StoryApiConfigurationManager.SET_ACTIVE_SCENE_METHOD.getRequiredParameters().get(0).getValueType());
  }

  @Test
  public void bipedResourceTypeUsesBipedResourceClass() {
    assertEquals(JavaType.getInstance(BipedResource.class), StoryApiConfigurationManager.BIPED_RESOURCE_TYPE);
  }
}
