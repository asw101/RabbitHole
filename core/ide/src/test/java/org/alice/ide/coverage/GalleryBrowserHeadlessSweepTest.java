package org.alice.ide.coverage;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class GalleryBrowserHeadlessSweepTest {

  @Test
  public void exerciseGalleryBrowserClasses() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.stageide.gallerybrowser.GalleryTab",
        "org.alice.stageide.gallerybrowser.ImportGalleryResourceCompositeHelper",
        "org.alice.stageide.gallerybrowser.ImportGalleryResourceLogic",
        "org.alice.stageide.gallerybrowser.ImportTab",
        "org.alice.stageide.gallerybrowser.ShapesTab",
        "org.alice.stageide.gallerybrowser.TreeOwningGalleryTab",
        "org.alice.stageide.gallerybrowser.enumconstant.codecs.EnumConstantResourceKeyCodec",
        "org.alice.stageide.gallerybrowser.enumconstant.data.EnumConstantResourceKeyListData",
        "org.alice.stageide.gallerybrowser.search.core.SearchGalleryWorker",
        "org.alice.stageide.gallerybrowser.search.core.SearchGalleryWorkerLogic",
        "org.alice.stageide.gallerybrowser.search.croquet.SearchTab",
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
        "org.alice.stageide.gallerybrowser.shapes.TorusDragModel",
        "org.alice.stageide.gallerybrowser.uri.UriBasedResourceNode",
        "org.alice.stageide.gallerybrowser.uri.UriGalleryDragModel",
        "org.alice.stageide.gallerybrowser.uri.UriGalleryDragModelLogic"
    );
    assertTrue("Should load at least 5 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 5);
  }
}
