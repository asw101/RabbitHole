package org.alice.ide.coverage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep12Test {
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.ide.croquet.models.history.HistoryPane",
        "org.alice.ide.croquet.models.html.HtmlProjectWriter",
        "org.alice.ide.croquet.models.print.PrintAllOperation",
        "org.alice.ide.croquet.models.print.PrintCurrentCodeOperation",
        "org.alice.ide.croquet.models.print.PrintSceneEditorOperation",
        "org.alice.ide.croquet.models.project.find.core.SearchResult",
        "org.alice.ide.croquet.models.project.find.croquet.AbstractFindComposite"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
