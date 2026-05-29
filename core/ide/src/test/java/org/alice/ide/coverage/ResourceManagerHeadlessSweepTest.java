package org.alice.ide.coverage;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static org.junit.Assert.assertTrue;

public class ResourceManagerHeadlessSweepTest {

  @Rule
  public Timeout globalTimeout = Timeout.seconds(120);

  @Test
  public void exerciseResourceManagerClasses() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.ide.resource.manager.ResourceManagerCompositeLogic",
        "org.alice.ide.resource.manager.ResourceSingleSelectTableRowState",
        "org.alice.ide.resource.manager.edits.AddOrRemoveResourceEdit",
        "org.alice.ide.resource.manager.edits.AddResourceEdit",
        "org.alice.ide.resource.manager.edits.RemoveResourceEdit",
        "org.alice.ide.resource.manager.edits.RenameResourceEdit"
    );
    assertTrue("Should load at least 2 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 2);
  }
}
