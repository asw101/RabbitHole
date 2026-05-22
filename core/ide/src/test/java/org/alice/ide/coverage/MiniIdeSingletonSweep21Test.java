package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep21Test {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.ide.properties.adapter.AbstractPropertyAdapter",
        "org.alice.ide.recentprojects.RecentProjectsMenuModel",
        "org.alice.ide.resource.manager.ImportGalleryResourceOperation",
        "org.alice.ide.resource.manager.ReloadContentResourceOperation",
        "org.alice.ide.resource.manager.ResourceManagerComposite",
        "org.alice.ide.resource.manager.edits.AddOrRemoveResourceEdit",
        "org.alice.ide.sceneeditor.AbstractSceneEditor"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
