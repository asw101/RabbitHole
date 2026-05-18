package org.lgna.story.implementation.alice;

import org.alice.math.immutable.AxisAlignedBox;
import org.alice.tweedle.file.AliceTextureReference;
import org.alice.tweedle.file.ModelManifest;
import org.alice.tweedle.file.StructureReference;
import org.junit.Test;
import org.lgna.story.resources.DynamicPropResource;
import org.lgna.story.resources.DynamicResource;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

/** Headless-safe tests for DynamicResource using an in-memory manifest. */
public class DynamicResourceTest {

  @Test
  public void createDynamicResourceReturnsDynamicPropResource() {
    DynamicResource<?, ?> resource = createResource();
    assertTrue(resource instanceof DynamicPropResource);
  }

  @Test
  public void identifierForReturnsProvidedResourceName() {
    DynamicResource<?, ?> resource = createResource();
    assertEquals("VariantA", resource.identifierFor("VariantA"));
  }

  @Test
  public void modelAndVariantNamesComeFromManifest() {
    DynamicResource<?, ?> resource = createResource();
    assertEquals("TestProp", resource.getModelClassName());
    assertEquals("VariantA", resource.getModelVariantName());
  }

  @Test
  public void tagsGroupTagsAndThemeTagsAreCopiedFromManifest() {
    DynamicResource<?, ?> resource = createResource();
    assertArrayEquals(new String[]{"tag-one", "tag-two"}, resource.getTags());
    assertArrayEquals(new String[]{"group-one"}, resource.getGroupTags());
    assertArrayEquals(new String[]{"theme-one"}, resource.getThemeTags());
  }

  @Test
  public void boundingBoxUsesStructureSpecificBoundingBox() {
    DynamicResource<?, ?> resource = createResource();
    AxisAlignedBox box = resource.getBoundingBox();
    assertEquals(-1.0, box.getXMinimum(), 1e-6);
    assertEquals(0.0, box.getYMinimum(), 1e-6);
    assertEquals(2.0, box.getZMaximum(), 1e-6);
  }

  @Test
  public void placeOnGroundReflectsManifestFlag() {
    DynamicResource<?, ?> resource = createResource();
    assertFalse(resource.getPlaceOnGround());
  }

  @Test
  public void thumbnailVisualAndTextureUrisComeFromManifestResources() {
    DynamicResource<?, ?> resource = createResource();
    assertTrue(resource.getThumbnailUrl("ignored").toString().endsWith("icon.png"));
    assertTrue(resource.getVisualURI().toString().endsWith("mesh.a3r"));
    assertTrue(resource.getTextureURI().toString().endsWith("texture.a3t"));
  }

  @Test
  public void modelSpecificJointsAreEmptyWhenManifestHasNoAdditionalJoints() {
    DynamicResource<?, ?> resource = createResource();
    assertEquals(0, resource.getModelSpecificJoints().length);
  }

  @Test
  public void getModelManifestReturnsOriginalManifestReference() {
    ModelManifest manifest = createManifest();
    ModelManifest.ModelVariant variant = manifest.models.get(0);
    DynamicResource<?, ?> resource = DynamicResource.createDynamicResource(manifest, variant);
    assertSame(manifest, resource.getModelManifest());
  }

  private static DynamicResource<?, ?> createResource() {
    ModelManifest manifest = createManifest();
    return DynamicResource.createDynamicResource(manifest, manifest.models.get(0));
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
}
