package org.lgna.story.resources;

import org.alice.tweedle.file.ModelManifest;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for DynamicResource static factory method and structural contracts.
 * DynamicResource's constructors need real resources, so we test what's
 * available without them: the static factory dispatch and class hierarchy.
 */
public class DynamicResourceTest {

  // ── createDynamicResource — switch dispatch ───────────

  @Test
  public void createDynamicResource_unknownParentClass_returnsNull() {
    ModelManifest manifest = new ModelManifest();
    manifest.parentClass = "UnknownType";
    ModelManifest.ModelVariant variant = new ModelManifest.ModelVariant();
    variant.name = "test";

    DynamicResource result = DynamicResource.createDynamicResource(manifest, variant);
    assertNull("Unknown parentClass should return null", result);
  }

  @Test
  public void createDynamicResource_bipedParentClass_returnsDynamicBipedResource() {
    ModelManifest manifest = createMinimalManifest("Biped");
    ModelManifest.ModelVariant variant = createMinimalVariant();

    DynamicResource result = DynamicResource.createDynamicResource(manifest, variant);
    assertNotNull(result);
    assertTrue(result instanceof DynamicBipedResource);
  }

  @Test
  public void createDynamicResource_quadrupedParentClass_returnsDynamicQuadrupedResource() {
    ModelManifest manifest = createMinimalManifest("Quadruped");
    ModelManifest.ModelVariant variant = createMinimalVariant();

    DynamicResource result = DynamicResource.createDynamicResource(manifest, variant);
    assertNotNull(result);
    assertTrue(result instanceof DynamicQuadrupedResource);
  }

  @Test
  public void createDynamicResource_flyerParentClass_returnsDynamicFlyerResource() {
    ModelManifest manifest = createMinimalManifest("Flyer");
    ModelManifest.ModelVariant variant = createMinimalVariant();

    DynamicResource result = DynamicResource.createDynamicResource(manifest, variant);
    assertNotNull(result);
    assertTrue(result instanceof DynamicFlyerResource);
  }

  @Test
  public void createDynamicResource_propParentClass_returnsDynamicPropResource() {
    ModelManifest manifest = createMinimalManifest("Prop");
    ModelManifest.ModelVariant variant = createMinimalVariant();

    DynamicResource result = DynamicResource.createDynamicResource(manifest, variant);
    assertNotNull(result);
    assertTrue(result instanceof DynamicPropResource);
  }

  @Test
  public void createDynamicResource_slithererParentClass() {
    ModelManifest manifest = createMinimalManifest("Slitherer");
    ModelManifest.ModelVariant variant = createMinimalVariant();

    DynamicResource result = DynamicResource.createDynamicResource(manifest, variant);
    assertNotNull(result);
  }

  @Test
  public void createDynamicResource_fishParentClass() {
    ModelManifest manifest = createMinimalManifest("Fish");
    ModelManifest.ModelVariant variant = createMinimalVariant();

    DynamicResource result = DynamicResource.createDynamicResource(manifest, variant);
    assertNotNull(result);
  }

  @Test
  public void createDynamicResource_marineMammalParentClass() {
    ModelManifest manifest = createMinimalManifest("MarineMammal");
    ModelManifest.ModelVariant variant = createMinimalVariant();

    DynamicResource result = DynamicResource.createDynamicResource(manifest, variant);
    assertNotNull(result);
  }

  @Test
  public void createDynamicResource_trainParentClass() {
    ModelManifest manifest = createMinimalManifest("Train");
    ModelManifest.ModelVariant variant = createMinimalVariant();

    DynamicResource result = DynamicResource.createDynamicResource(manifest, variant);
    assertNotNull(result);
  }

  @Test
  public void createDynamicResource_automobileParentClass() {
    ModelManifest manifest = createMinimalManifest("Automobile");
    ModelManifest.ModelVariant variant = createMinimalVariant();

    DynamicResource result = DynamicResource.createDynamicResource(manifest, variant);
    assertNotNull(result);
  }

  @Test
  public void createDynamicResource_aircraftParentClass() {
    ModelManifest manifest = createMinimalManifest("Aircraft");
    ModelManifest.ModelVariant variant = createMinimalVariant();

    DynamicResource result = DynamicResource.createDynamicResource(manifest, variant);
    assertNotNull(result);
  }

  @Test
  public void createDynamicResource_watercraftParentClass() {
    ModelManifest manifest = createMinimalManifest("Watercraft");
    ModelManifest.ModelVariant variant = createMinimalVariant();

    DynamicResource result = DynamicResource.createDynamicResource(manifest, variant);
    assertNotNull(result);
  }

  // ── DynamicResource class hierarchy ───────────────────

  @Test
  public void dynamicBipedResource_implementsBipedResource() {
    assertTrue(BipedResource.class.isAssignableFrom(DynamicBipedResource.class));
  }

  @Test
  public void dynamicQuadrupedResource_implementsQuadrupedResource() {
    assertTrue(QuadrupedResource.class.isAssignableFrom(DynamicQuadrupedResource.class));
  }

  @Test
  public void dynamicFlyerResource_implementsFlyerResource() {
    assertTrue(FlyerResource.class.isAssignableFrom(DynamicFlyerResource.class));
  }

  @Test
  public void dynamicPropResource_implementsPropResource() {
    assertTrue(PropResource.class.isAssignableFrom(DynamicPropResource.class));
  }

  // ── Helpers ───────────────────────────────────────────

  private ModelManifest createMinimalManifest(String parentClass) {
    ModelManifest manifest = new ModelManifest();
    manifest.parentClass = parentClass;
    manifest.description = new ModelManifest.Description();
    manifest.description.name = "Test" + parentClass;
    manifest.description.tags = new java.util.ArrayList<>();
    manifest.description.groupTags = new java.util.ArrayList<>();
    manifest.description.themeTags = new java.util.ArrayList<>();
    manifest.additionalJoints = new java.util.ArrayList<>();
    return manifest;
  }

  private ModelManifest.ModelVariant createMinimalVariant() {
    ModelManifest.ModelVariant variant = new ModelManifest.ModelVariant();
    variant.name = "TestVariant";
    return variant;
  }
}
