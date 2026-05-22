package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep16Test {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.ide.declarationseditor.events.AddEventListenerCascade",
        "org.alice.ide.declarationseditor.type.ConstructorMenuModel",
        "org.alice.ide.declarationseditor.type.ManagedFieldsComposite",
        "org.alice.ide.declarationseditor.type.MethodMenuModel",
        "org.alice.ide.declarationseditor.type.components.TypeDeclarationView",
        "org.alice.ide.declarationseditor.type.views.ConstructorView",
        "org.alice.ide.declarationseditor.type.views.MethodView"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
