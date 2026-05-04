package org.lgna.project.io;

import edu.cmu.cs.dennisc.java.util.zip.ByteArrayDataSource;
import edu.cmu.cs.dennisc.java.util.zip.DataSource;
import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import org.alice.tweedle.file.ModelManifest;
import org.junit.Test;
import org.lgna.story.JointedModelPose;
import org.lgna.story.implementation.JointedModelImp;
import org.lgna.story.implementation.alice.AliceResourceUtilities;
import org.lgna.story.resources.JointArrayId;
import org.lgna.story.resources.JointId;
import org.lgna.story.resources.JointedModelResource;
import org.lgna.story.resourceutilities.JointedModelAliceExporter;
import org.lgna.story.resourceutilities.JointedModelColladaExporter;
import org.lgna.story.resourceutilities.JointedModelGltfExporter;
import org.lgna.story.resourceutilities.ModelResourceInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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
  public void writeModelFromResourceListWritesArchiveEntries() throws Exception {
    ExportableResource resource = ExportableResource.DEFAULT;
    registerModelResourceMetadata(resource, "SyntheticModel", "DEFAULT");
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    new SyntheticJsonModelIo(JsonModelIo.ExportFormat.ALICE).writeModel(os, Collections.singletonList(resource));

    assertNotEquals(0, os.size());
    assertEquals(Collections.singletonList("models/SyntheticModel/SyntheticModel.json"), zipEntryNames(os.toByteArray()));
  }

  @Test
  public void writeModelFromResourceListPreservesExporterArchiveEntryNames() throws Exception {
    ExportableResource resource = ExportableResource.DEFAULT;
    registerModelResourceMetadata(resource, "SyntheticModel", "DEFAULT");
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    new MultiEntryJsonModelIo(JsonModelIo.ExportFormat.ALICE).writeModel(os, Collections.singletonList(resource));

    assertEquals(Arrays.asList(
        "models/SyntheticModel/SyntheticModel.a3r",
        "models/SyntheticModel/SyntheticModel.png",
        "models/SyntheticModel/SyntheticModel.json"), zipEntryNames(os.toByteArray()));
  }

  @Test
  public void writeModelFromEmptyResourceListIsRejected() {
    IllegalArgumentException thrown = assertThrows(
        IllegalArgumentException.class,
        () -> new JsonModelIo(JsonModelIo.ExportFormat.ALICE).writeModel(new ByteArrayOutputStream(), Collections.emptyList()));

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

  private List<String> zipEntryNames(byte[] archive) throws Exception {
    List<String> names = new ArrayList<>();
    try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(archive))) {
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        names.add(entry.getName());
      }
    }
    return names;
  }

  @SuppressWarnings("unchecked")
  private static void registerModelResourceMetadata(JointedModelResource resource, String modelName, String textureName) throws Exception {
    ModelResourceInfo parentInfo = modelResourceInfo(null, null, modelName, null);
    ModelResourceInfo resourceInfo = modelResourceInfo(parentInfo, resource.toString(), modelName, textureName);
    parentInfo.addSubResource(resourceInfo);

    Field classToInfoMapField = AliceResourceUtilities.class.getDeclaredField("classToInfoMap");
    classToInfoMapField.setAccessible(true);
    Map<String, ModelResourceInfo> classToInfoMap = (Map<String, ModelResourceInfo>) classToInfoMapField.get(null);
    classToInfoMap.put(resource.getClass().getName(), parentInfo);
    classToInfoMap.put(resource.getClass().getName() + resource, resourceInfo);

    Class<?> resourceNamesClass = Class.forName(AliceResourceUtilities.class.getName() + "$ResourceNames");
    Constructor<?> resourceNamesConstructor = resourceNamesClass.getDeclaredConstructor(String.class, String.class);
    resourceNamesConstructor.setAccessible(true);
    Object resourceNames = resourceNamesConstructor.newInstance(modelName, textureName);

    Field resourceNamesMapField = AliceResourceUtilities.class.getDeclaredField("resourceIdentifierToResourceNamesMap");
    resourceNamesMapField.setAccessible(true);
    Map<String, Object> resourceNamesMap = (Map<String, Object>) resourceNamesMapField.get(null);
    resourceNamesMap.put(resource.identifierFor(resource.toString()), resourceNames);
  }

  private static ModelResourceInfo modelResourceInfo(ModelResourceInfo parentInfo, String resourceName, String modelName, String textureName) {
    String[] noTags = new String[0];
    return new ModelResourceInfo(parentInfo, resourceName, "Test", 2024, null, noTags, noTags, noTags, modelName, textureName, false, true);
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

  private static class ExportableResource implements JointedModelResource {
    private static final ExportableResource DEFAULT = new ExportableResource();

    @Override
    public JointedModelImp.JointImplementationAndVisualDataFactory<JointedModelResource> getImplementationAndVisualFactory() {
      return null;
    }

    @Override
    public String toString() {
      return "DEFAULT";
    }
  }

  private static class SyntheticJsonModelIo extends JsonModelIo {
    SyntheticJsonModelIo(ExportFormat exportFormat) {
      super(exportFormat);
    }

    @Override
    public List<DataSource> createDataSources(String baseModelPath) {
      String modelPath = baseModelPath + "/" + modelManifest.getName() + "/" + modelManifest.getName() + ".json";
      return Collections.singletonList(new ByteArrayDataSource(modelPath, "{}"));
    }
  }

  private static class MultiEntryJsonModelIo extends JsonModelIo {
    MultiEntryJsonModelIo(ExportFormat exportFormat) {
      super(exportFormat);
    }

    @Override
    public List<DataSource> createDataSources(String baseModelPath) {
      String modelPath = baseModelPath + "/" + modelManifest.getName();
      return Arrays.asList(
          new ByteArrayDataSource(modelPath + "/" + modelManifest.getName() + ".a3r", "structure"),
          new ByteArrayDataSource(modelPath + "/" + modelManifest.getName() + ".png", "thumbnail"),
          new ByteArrayDataSource(modelPath + "/" + modelManifest.getName() + ".json", "{}"));
    }
  }
}
