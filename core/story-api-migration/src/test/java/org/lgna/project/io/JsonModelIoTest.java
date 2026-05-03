package org.lgna.project.io;

import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import org.alice.tweedle.file.ModelManifest;
import org.junit.Test;
import org.lgna.story.resourceutilities.JointedModelAliceExporter;
import org.lgna.story.resourceutilities.JointedModelColladaExporter;
import org.lgna.story.resourceutilities.JointedModelGltfExporter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JsonModelIoTest {
  private static final String RESOURCE_PATH = "models/SyntheticProp";

  @Test
  public void exportFormatsCreateExpectedExportersForSyntheticVisual() {
    ModelManifest manifest = syntheticManifest();
    ModelManifest.ModelVariant variant = syntheticVariant();

    assertExporter(
        JsonModelIo.ExportFormat.COLLADA,
        JointedModelColladaExporter.class,
        "models/SyntheticProp/SyntheticTexture.dae",
        manifest,
        variant);
    assertExporter(
        JsonModelIo.ExportFormat.GLTF,
        JointedModelGltfExporter.class,
        "models/SyntheticProp/SyntheticTexture.glb",
        manifest,
        variant);
    assertExporter(
        JsonModelIo.ExportFormat.ALICE,
        JointedModelAliceExporter.class,
        "models/SyntheticProp/syntheticstructure.a3r",
        manifest,
        variant);
  }

  private void assertExporter(
      JsonModelIo.ExportFormat format,
      Class<? extends JointedModelExporter> expectedType,
      String expectedStructureName,
      ModelManifest manifest,
      ModelManifest.ModelVariant variant) {
    JsonModelIo modelIo = new JsonModelIo(manifest, new SkeletonVisual(), null, format);

    JointedModelExporter exporter = format.exporter(modelIo, new SkeletonVisual(), variant, RESOURCE_PATH);

    assertTrue(expectedType.isInstance(exporter));
    assertEquals(expectedStructureName, exporter.createStructureDataSource().getName());
  }

  private ModelManifest syntheticManifest() {
    ModelManifest manifest = new ModelManifest();
    manifest.description.name = "SyntheticProp";
    return manifest;
  }

  private ModelManifest.ModelVariant syntheticVariant() {
    ModelManifest.ModelVariant variant = new ModelManifest.ModelVariant();
    variant.structure = "SyntheticStructure";
    variant.textureSet = "SyntheticTexture";
    return variant;
  }
}
