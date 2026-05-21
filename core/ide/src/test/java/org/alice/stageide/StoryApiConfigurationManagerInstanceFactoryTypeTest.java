package org.alice.stageide;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.story.SMarker;
import org.lgna.story.SProgram;
import org.lgna.story.SThing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StoryApiConfigurationManagerInstanceFactoryTypeTest {
  @Test
  public void isInstanceFactoryDesiredForType_acceptsThingsAndProgramsButNotMarkers() {
    StoryApiConfigurationManager manager = StoryApiConfigurationManager.getInstance();
    assertTrue(manager.isInstanceFactoryDesiredForType(JavaType.getInstance(SThing.class)));
    assertTrue(manager.isInstanceFactoryDesiredForType(JavaType.getInstance(SProgram.class)));
    assertFalse(manager.isInstanceFactoryDesiredForType(JavaType.getInstance(SMarker.class)));
    assertFalse(manager.isInstanceFactoryDesiredForType(JavaType.getInstance(String.class)));
  }
}
