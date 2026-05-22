package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep13Test {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.ide.croquet.models.project.stats.croquet.StatisticsFlowControlFrequencyComposite",
        "org.alice.ide.croquet.models.project.stats.croquet.StatisticsMethodFrequencyTabComposite",
        "org.alice.ide.croquet.models.projecturi.AbstractSaveOperation",
        "org.alice.ide.croquet.models.projecturi.EvidenceJsonWriter",
        "org.alice.ide.croquet.models.projecturi.ExitOperation",
        "org.alice.ide.croquet.models.projecturi.PotentialClearanceIteratingOperation",
        "org.alice.ide.croquet.models.projecturi.RevertProjectOperation"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
