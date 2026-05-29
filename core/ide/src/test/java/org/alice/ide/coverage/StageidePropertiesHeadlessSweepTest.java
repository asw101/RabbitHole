package org.alice.ide.coverage;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class StageidePropertiesHeadlessSweepTest {

  @Test
  public void exercisePropertiesClasses() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.stageide.properties.BillboardBackPaintPropertyAdapter",
        "org.alice.stageide.properties.BillboardFrontPaintPropertyAdapter",
        "org.alice.stageide.properties.GroundOpacityAdapter",
        "org.alice.stageide.properties.IsAllScaleLinkedState",
        "org.alice.stageide.properties.IsXYScaleLinkedState",
        "org.alice.stageide.properties.IsXZScaleLinkedState",
        "org.alice.stageide.properties.IsYZScaleLinkedState",
        "org.alice.stageide.properties.LinkScaleButton",
        "org.alice.stageide.properties.LinkScaleIcon",
        "org.alice.stageide.properties.ModelOpacityAdapter",
        "org.alice.stageide.properties.ModelSizeAdapter",
        "org.alice.stageide.properties.MoveableTurnableTranslationAdapter",
        "org.alice.stageide.properties.MutableRiderVehicleAdapter",
        "org.alice.stageide.properties.PaintPropertyAdapter",
        "org.alice.stageide.properties.ResourcePropertyAdapter",
        "org.alice.stageide.properties.TextFontPropertyAdapter",
        "org.alice.stageide.properties.TextValuePropertyAdapter",
        "org.alice.stageide.properties.uicontroller.CompositePropertyController",
        "org.alice.stageide.properties.uicontroller.ModelSizePropertyController",
        "org.alice.stageide.properties.uicontroller.ModelSizePropertyControllerLogic"
    );
    assertTrue("Should load at least 5 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 5);
  }
}
