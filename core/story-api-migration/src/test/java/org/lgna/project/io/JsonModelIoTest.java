package org.lgna.project.io;

import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import org.alice.tweedle.file.ModelManifest;
import org.junit.Test;
import org.lgna.story.JointedModelPose;
import org.lgna.story.implementation.JointedModelImp;
import org.lgna.story.resources.JointArrayId;
import org.lgna.story.resources.JointId;
import org.lgna.story.resources.JointedModelResource;
import org.lgna.story.resourceutilities.JointedModelAliceExporter;
import org.lgna.story.resourceutilities.JointedModelColladaExporter;
import org.lgna.story.resourceutilities.JointedModelGltfExporter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
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

  @Test
  public void emptyModelResourceSetIsRejected() {
    IllegalArgumentException thrown = assertThrows(
        IllegalArgumentException.class,
        () -> new JsonModelIo(Collections.emptySet(), JsonModelIo.ExportFormat.GLTF));

    assertTrue(thrown.getMessage().contains("model resource"));
  }

  @Test
  public void modelDataReflectionFailuresThrowInsteadOfWritingNullManifestEntries() throws Exception {
    InaccessibleResource resource = new InaccessibleResource();

    assertFieldReaderThrows("createPose", "hiddenPose", resource);
    assertFieldReaderThrows("createJoint", "hiddenJoint", resource);
    assertFieldReaderThrows("createJointArray", "hiddenJointArray", resource);
    assertFieldReaderThrows("createJointArrayId", "hiddenJointArrayId", resource);
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

  private void assertFieldReaderThrows(String methodName, String fieldName, JointedModelResource resource) throws Exception {
    Method method = JsonModelIo.class.getDeclaredMethod(methodName, Field.class, JointedModelResource.class);
    method.setAccessible(true);
    Field field = resource.getClass().getDeclaredField(fieldName);

    InvocationTargetException thrown = assertThrows(
        InvocationTargetException.class,
        () -> method.invoke(null, field, resource));

    assertTrue(thrown.getCause() instanceof IllegalStateException);
    assertTrue(thrown.getCause().getMessage().contains(fieldName));
  }

  private static class InaccessibleResource implements JointedModelResource {
    private final JointedModelPose hiddenPose = new JointedModelPose();
    private final JointId hiddenJoint = new JointId(null, InaccessibleResource.class);
    private final JointId[] hiddenJointArray = {hiddenJoint};
    private final JointArrayId hiddenJointArrayId = new JointArrayId("hiddenJoint[%d]", hiddenJoint, InaccessibleResource.class);

    @Override
    public JointedModelImp.JointImplementationAndVisualDataFactory<JointedModelResource> getImplementationAndVisualFactory() {
      return null;
    }
  }
}
