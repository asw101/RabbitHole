package org.alice.ide.coverage;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static org.junit.Assert.assertTrue;

public class GalleryBrowserHeadlessSweepTest {

  @Rule
  public Timeout globalTimeout = Timeout.seconds(120);

  @Test
  public void exerciseGalleryBrowserClasses() {
    // Many gallery browser classes trigger resource loading → modal dialog.
    // Only exercise the safe ones (shapes, codecs, logic classes).
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.stageide.gallerybrowser.GalleryTab",
        "org.alice.stageide.gallerybrowser.ImportTab",
        "org.alice.stageide.gallerybrowser.ShapesTab",
        "org.alice.stageide.gallerybrowser.shapes.AxesDragModel",
        "org.alice.stageide.gallerybrowser.shapes.BillboardDragModel",
        "org.alice.stageide.gallerybrowser.shapes.BoxDragModel",
        "org.alice.stageide.gallerybrowser.shapes.ConeDragModel",
        "org.alice.stageide.gallerybrowser.shapes.CylinderDragModel",
        "org.alice.stageide.gallerybrowser.shapes.DiscDragModel",
        "org.alice.stageide.gallerybrowser.shapes.GroundDragModel",
        "org.alice.stageide.gallerybrowser.shapes.ShapeDragModel",
        "org.alice.stageide.gallerybrowser.shapes.SphereDragModel",
        "org.alice.stageide.gallerybrowser.shapes.TextModelDragModel",
        "org.alice.stageide.gallerybrowser.shapes.TorusDragModel"
    );
    assertTrue("Should load at least 3 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 3);
  }
}
