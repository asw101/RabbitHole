package org.lgna.story.implementation.alice;

import org.alice.math.immutable.AxisAlignedBox;
import org.alice.tweedle.file.AliceTextureReference;
import org.alice.tweedle.file.ModelManifest;
import org.alice.tweedle.file.StructureReference;
import org.junit.Test;
import org.lgna.project.ast.AstProcessor;
import org.lgna.story.resources.DynamicResource;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class DynamicResourceAdditionalTest {
  @Test
  public void createDynamicResourceReturnsNullForUnknownParentClass() {
    ModelManifest manifest = createManifest();
    manifest.parentClass = "UnknownType";
    assertNull(DynamicResource.createDynamicResource(manifest, manifest.models.get(0)));
  }

  @Test
  public void additionalJointsAreCreatedAndProcessed() {
    ModelManifest manifest = createManifest();

    ModelManifest.Joint root = new ModelManifest.Joint();
    root.name = "RootDynamic";
    root.parent = null;
    manifest.additionalJoints.add(root);

    ModelManifest.Joint child = new ModelManifest.Joint();
    child.name = "ChildDynamic";
    child.parent = "RootDynamic";
    manifest.additionalJoints.add(child);

    DynamicResource<?, ?> resource = DynamicResource.createDynamicResource(manifest, manifest.models.get(0));
    assertEquals(2, resource.getModelSpecificJoints().length);
    assertEquals("RootDynamic", resource.getModelSpecificJoints()[0].toString());
    assertEquals("ChildDynamic", resource.getModelSpecificJoints()[1].toString());

    RecordingProcessor processor = new RecordingProcessor();
    resource.process(processor);
    assertTrue(processor.resourceType.endsWith("DynamicPropResource"));
    assertEquals("VariantA", processor.resourceName);
    assertEquals(2, processor.addedJoints.length);
  }

  @Test
  public void fallbackBoundingBoxAndUrisComeFromManifest() {
    ModelManifest manifest = createManifest();
    manifest.getStructure("structureA").boundingBox = null;
    manifest.boundingBox = new ModelManifest.BoundingBox();
    manifest.boundingBox.min = Arrays.asList(-2.0f, -3.0f, -4.0f);
    manifest.boundingBox.max = Arrays.asList(5.0f, 6.0f, 7.0f);

    DynamicResource<?, ?> resource = DynamicResource.createDynamicResource(manifest, manifest.models.get(0));
    AxisAlignedBox box = resource.getBoundingBox();
    assertEquals(-2.0, box.getXMinimum(), 1e-6);
    assertEquals(7.0, box.getZMaximum(), 1e-6);
    assertTrue(resource.getIconURI().toString().endsWith("icon.png"));
    assertTrue(resource.getThumbnailUrl("ignored").toString().endsWith("icon.png"));
    assertTrue(resource.getVisualURI().toString().endsWith("mesh.a3r"));
    assertTrue(resource.getTextureURI().toString().endsWith("texture.a3t"));
  }

  @Test
  public void gettersExposeManifestBackedValues() {
    ModelManifest manifest = createManifest();
    DynamicResource<?, ?> resource = DynamicResource.createDynamicResource(manifest, manifest.models.get(0));
    assertEquals("TestProp", resource.getInternalModelClassName());
    assertEquals("TestProp", resource.getModelClassName());
    assertEquals("VariantA", resource.getModelVariantName());
    assertEquals(0, resource.getRootJointIds().length);
    assertNotNull(resource.getImplementationAndVisualFactory());
  }

  private static ModelManifest createManifest() {
    ModelManifest manifest = new ModelManifest();
    manifest.setRootFile(new File(System.getProperty("user.dir")));
    manifest.parentClass = "Prop";
    manifest.description = new ModelManifest.Description();
    manifest.description.name = "TestProp";
    manifest.description.tags = new ArrayList<>(Arrays.asList("tag-one", "tag-two"));
    manifest.description.groupTags = new ArrayList<>(Arrays.asList("group-one"));
    manifest.description.themeTags = new ArrayList<>(Arrays.asList("theme-one"));
    manifest.additionalJoints = new ArrayList<>();
    manifest.placeOnGround = false;

    StructureReference structureReference = new StructureReference();
    structureReference.name = "structureA";
    structureReference.file = "mesh.a3r";
    structureReference.boundingBox = new ModelManifest.BoundingBox();
    structureReference.boundingBox.min = Arrays.asList(-1.0f, 0.0f, -1.0f);
    structureReference.boundingBox.max = Arrays.asList(1.0f, 2.0f, 2.0f);
    manifest.resources.add(structureReference);

    AliceTextureReference textureReference = new AliceTextureReference();
    textureReference.name = "textureA";
    textureReference.file = "texture.a3t";
    manifest.resources.add(textureReference);

    ModelManifest.TextureSet textureSet = new ModelManifest.TextureSet();
    textureSet.name = "textureSetA";
    textureSet.idToResourceMap.put(0, "textureA");
    manifest.textureSets.add(textureSet);

    ModelManifest.ModelVariant variant = new ModelManifest.ModelVariant();
    variant.name = "VariantA";
    variant.structure = "structureA";
    variant.textureSet = "textureSetA";
    variant.icon = "icon.png";
    manifest.models.add(variant);
    return manifest;
  }

  private static final class RecordingProcessor implements AstProcessor {
    private String resourceType;
    private String resourceName;
    private org.lgna.project.code.InstantiableTweedleNode[] addedJoints;

    @Override
    public org.lgna.project.code.CodeOrganizer getNewCodeOrganizerForTypeName(String typeName) {
      return null;
    }

    @Override
    public void processDynamicResource(String jointedModelResource, String resourceName, org.lgna.project.code.InstantiableTweedleNode[] addedJoints) {
      this.resourceType = jointedModelResource;
      this.resourceName = resourceName;
      this.addedJoints = addedJoints;
    }
  }
}
