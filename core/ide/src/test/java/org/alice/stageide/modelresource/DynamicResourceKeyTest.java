package org.alice.stageide.modelresource;

import org.alice.math.immutable.AxisAlignedBox;
import org.alice.tweedle.file.ModelManifest;
import org.junit.Test;
import org.lgna.story.resources.DynamicPropResource;
import org.lgna.story.resources.ModelResource;

import java.util.Arrays;

import static org.junit.Assert.*;

public class DynamicResourceKeyTest {

  private DynamicResourceKey createKey(String modelName, boolean placeOnGround) {
    return new DynamicResourceKey(createResource(modelName, placeOnGround));
  }

  private DynamicPropResource createResource(String modelName, boolean placeOnGround) {
    ModelManifest manifest = new ModelManifest();
    manifest.parentClass = "Prop";
    manifest.description.name = modelName;
    manifest.description.tags = Arrays.asList("tag.one", "tag.two");
    manifest.description.groupTags = Arrays.asList("group.alpha", "group.beta");
    manifest.description.themeTags = Arrays.asList("theme.alpha", "theme.beta");
    manifest.placeOnGround = placeOnGround;
    manifest.boundingBox = new ModelManifest.BoundingBox();
    manifest.boundingBox.min = Arrays.asList(1.0f, 2.0f, 3.0f);
    manifest.boundingBox.max = Arrays.asList(4.0f, 5.0f, 6.0f);

    ModelManifest.ModelVariant variant = new ModelManifest.ModelVariant();
    variant.name = "DEFAULT";
    manifest.models.add(variant);
    return new DynamicPropResource(manifest, variant);
  }

  @Test
  public void class_extendsInstanceCreatorKey() {
    assertTrue(InstanceCreatorKey.class.isAssignableFrom(DynamicResourceKey.class));
  }

  @Test
  public void constructor_validResource_createsKey() {
    DynamicResourceKey key = createKey("HeroProp", true);
    assertNotNull(key);
  }

  @Test
  public void getModelResourceCls_dynamicPropResource_returnsConcreteClass() {
    DynamicResourceKey key = createKey("HeroProp", true);
    assertEquals(DynamicPropResource.class, key.getModelResourceCls());
  }

  @Test
  public void getModelResourceCls_dynamicResourceType_extendsModelResource() {
    DynamicResourceKey key = createKey("HeroProp", true);
    Class<? extends ModelResource> valueClass = key.getModelResourceCls();
    assertTrue(ModelResource.class.isAssignableFrom(valueClass));
  }

  @Test
  public void getInternalName_manifestName_returnsOriginalName() {
    DynamicResourceKey key = createKey("HeroProp", true);
    assertEquals("HeroProp", key.getInternalName());
  }

  @Test
  public void getLocalizedName_manifestName_returnsDisplayName() {
    DynamicResourceKey key = createKey("HeroProp", true);
    assertEquals("HeroProp", key.getLocalizedName());
  }

  @Test
  public void getSearchText_simpleResource_combinesInternalAndLocalizedNames() {
    DynamicResourceKey key = createKey("HeroProp", true);
    assertEquals("HeroProp HeroProp", key.getSearchText());
  }

  @Test
  public void getTags_resourceTags_returnsManifestTags() {
    DynamicResourceKey key = createKey("HeroProp", true);
    assertArrayEquals(new String[] {"tag.one", "tag.two"}, key.getTags());
  }

  @Test
  public void getGroupTags_resourceTags_returnsManifestGroupTags() {
    DynamicResourceKey key = createKey("HeroProp", true);
    assertArrayEquals(new String[] {"group.alpha", "group.beta"}, key.getGroupTags());
  }

  @Test
  public void getThemeTags_resourceTags_returnsManifestThemeTags() {
    DynamicResourceKey key = createKey("HeroProp", true);
    assertArrayEquals(new String[] {"theme.alpha", "theme.beta"}, key.getThemeTags());
  }

  @Test
  public void isLeaf_anyDynamicResource_returnsTrue() {
    DynamicResourceKey key = createKey("HeroProp", true);
    assertTrue(key.isLeaf());
  }

  @Test
  public void getBoundingBox_manifestBoundingBox_returnsConfiguredBox() {
    DynamicResourceKey key = createKey("HeroProp", true);
    AxisAlignedBox box = key.getBoundingBox();
    assertNotNull(box);
    assertEquals(AxisAlignedBox.createAxisAlignedBox(1, 2, 3, 4, 5, 6), box);
  }

  @Test
  public void getPlaceOnGround_manifestFlag_returnsConfiguredValue() {
    assertTrue(createKey("GroundedProp", true).getPlaceOnGround());
    assertFalse(createKey("FloatingProp", false).getPlaceOnGround());
  }

  @Test
  public void equals_sameWrappedResource_returnsTrue() {
    DynamicPropResource resource = createResource("HeroProp", true);
    DynamicResourceKey first = new DynamicResourceKey(resource);
    DynamicResourceKey second = new DynamicResourceKey(resource);
    assertEquals(first, second);
  }

  @Test
  public void equals_differentWrappedResources_returnsFalse() {
    DynamicResourceKey first = createKey("HeroProp", true);
    DynamicResourceKey second = createKey("HeroProp", true);
    assertNotEquals(first, second);
  }

  @Test
  public void hashCode_sameWrappedResource_matchesResourceHashCode() {
    DynamicPropResource resource = createResource("HeroProp", true);
    DynamicResourceKey key = new DynamicResourceKey(resource);
    assertEquals(resource.hashCode(), key.hashCode());
  }

  @Test
  public void toString_keyCreated_containsKeyAndResourceClassNames() {
    DynamicResourceKey key = createKey("HeroProp", true);
    String text = key.toString();
    assertTrue(text.contains("DynamicResourceKey"));
    assertTrue(text.contains("DynamicPropResource"));
  }
}
