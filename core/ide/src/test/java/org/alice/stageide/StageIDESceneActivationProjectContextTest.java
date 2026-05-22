package org.alice.stageide;

import org.alice.ide.testing.ProjectContextTestCase;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class StageIDESceneActivationProjectContextTest extends ProjectContextTestCase {
  @Test
  public void sceneActivationListenerScanUsesLoadedProjectMethods() {
    assertTrue(StageIDE.getActiveInstance().getUserMethodsInvokedFromSceneActivationListeners().contains(fixture.sceneProcedure));
  }
}
