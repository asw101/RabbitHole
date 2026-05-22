package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep29Test {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.stageide.sceneeditor.viewmanager.MoveMarkerToActiveCameraActionOperation",
        "org.alice.stageide.sceneeditor.views.InstanceFactorySelectionPanel",
        "org.alice.stageide.sceneeditor.views.SceneObjectPropertyManagerPanel",
        "org.alice.stageide.typecontext.NonSceneTypeComposite",
        "org.alice.stageide.typecontext.components.NonSceneTypeView",
        "org.alice.tools.EatmeEditProcedure",
        "org.lgna.debug.pick.croquet.PickDebugFrame"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
