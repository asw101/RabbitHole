package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep23Test {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.ide.x.components.FieldAccessView",
        "org.alice.ide.x.components.InstanceCreationView",
        "org.alice.ide.x.croquet.edits.SceneEditorUpdatingExpressionPropertyEdit",
        "org.alice.stageide.StageIDE",
        "org.alice.stageide.StoryApiConfigurationManager",
        "org.alice.stageide.ast.declaration.AddCopiedManagedFieldComposite",
        "org.alice.stageide.ast.declaration.AddResourceKeyManagedFieldComposite"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
