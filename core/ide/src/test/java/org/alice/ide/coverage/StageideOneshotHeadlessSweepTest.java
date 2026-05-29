package org.alice.ide.coverage;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class StageideOneshotHeadlessSweepTest {

  @Test
  public void exerciseOneshotClasses() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.stageide.oneshot.AllJointLocalTransformationsMethodInvocationEditFactory",
        "org.alice.stageide.oneshot.DynamicOneShotMenuModel",
        "org.alice.stageide.oneshot.InstanceFactoryLabelSeparatorModel",
        "org.alice.stageide.oneshot.LocalTransformationMethodInvocationEditFactory",
        "org.alice.stageide.oneshot.MethodInvocationBlank",
        "org.alice.stageide.oneshot.MethodInvocationBlankLogic",
        "org.alice.stageide.oneshot.MethodInvocationEditFactory",
        "org.alice.stageide.oneshot.OneShotMenuModel",
        "org.alice.stageide.oneshot.OneShotUtilities",
        "org.alice.stageide.oneshot.SetOpacityMethodInvocationEditFactory",
        "org.alice.stageide.oneshot.SetPaintMethodInvocationEditFactory",
        "org.alice.stageide.oneshot.StrikePoseMethodInvocationEditFactory",
        "org.alice.stageide.oneshot.edits.AbstractSetPaintEdit",
        "org.alice.stageide.oneshot.edits.AllJointLocalTransformationsEdit",
        "org.alice.stageide.oneshot.edits.LocalTransformationEdit",
        "org.alice.stageide.oneshot.edits.MethodInvocationEdit",
        "org.alice.stageide.oneshot.edits.SetOpacityEdit",
        "org.alice.stageide.oneshot.edits.SetPaintEdit",
        "org.alice.stageide.oneshot.edits.StrikePoseEdit"
    );
    assertTrue("Should load at least 5 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 5);
  }
}
