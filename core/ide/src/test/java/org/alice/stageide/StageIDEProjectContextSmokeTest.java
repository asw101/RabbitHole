package org.alice.stageide;

import org.alice.ide.testing.ProjectContextTestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class StageIDEProjectContextSmokeTest extends ProjectContextTestCase {
  @Test
  public void loadedProjectProvidesSceneTypeAndField() {
    StageIDE ide = StageIDE.getActiveInstance();

    assertNotNull(ide.getDocumentFrame().getDocument());
    assertSame(fixture.project, ide.getProject());
    assertSame(fixture.sceneField, ide.getSceneField());
    assertSame(fixture.sceneType, ide.getSceneType());
    assertEquals("storyAction", fixture.sceneProcedure.getName());
  }
}
