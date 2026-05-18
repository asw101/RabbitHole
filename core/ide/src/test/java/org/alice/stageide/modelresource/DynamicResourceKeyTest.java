package org.alice.stageide.modelresource;

import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import org.alice.tweedle.file.ModelManifest;
import org.junit.Test;
import org.lgna.story.resources.DynamicPropResource;

import static org.junit.Assert.*;

public class DynamicResourceKeyTest {
  private DynamicPropResource createDynamicResource(String modelName, String tag) {
    ModelManifest manifest = new ModelManifest();
    manifest.parentClass = "Prop";
    manifest.description.name = modelName;
    manifest.description.tags.add(tag);
    manifest.description.groupTags.add(tag + "Group");
    manifest.description.themeTags.add(tag + "Theme");
    manifest.placeOnGround = true;
    ModelManifest.BoundingBox boundingBox = new ModelManifest.BoundingBox();
    boundingBox.min = java.util.List.of(0.0f, 1.0f, 2.0f);
    boundingBox.max = java.util.List.of(3.0f, 4.0f, 5.0f);
    manifest.boundingBox = boundingBox;
    ModelManifest.ModelVariant variant = new ModelManifest.ModelVariant();
    variant.name = "DEFAULT";
    return new DynamicPropResource(manifest, variant);
  }

  @Test
  public void getModelResourceCls_returnsDynamicResourceClass() {
    DynamicResourceKey key = new DynamicResourceKey(createDynamicResource("Custom Prop", "tag"));
    assertEquals(DynamicPropResource.class, key.getModelResourceCls());
  }

  @Test
  public void getInternalName_and_getLocalizedName_returnModelClassName() {
    DynamicResourceKey key = new DynamicResourceKey(createDynamicResource("Custom Prop", "tag"));
    assertEquals("Custom Prop", key.getInternalName());
    assertEquals("Custom Prop", key.getLocalizedName());
  }

  @Test
  public void getSearchText_containsModelName() {
    DynamicResourceKey key = new DynamicResourceKey(createDynamicResource("Custom Prop", "tag"));
    assertTrue(key.getSearchText().contains("Custom Prop"));
  }

  @Test
  public void tagAccessors_delegateToDynamicResource() {
    DynamicResourceKey key = new DynamicResourceKey(createDynamicResource("Custom Prop", "tag"));
    assertArrayEquals(new String[] {"tag"}, key.getTags());
    assertArrayEquals(new String[] {"tagGroup"}, key.getGroupTags());
    assertArrayEquals(new String[] {"tagTheme"}, key.getThemeTags());
  }

  @Test
  public void boundingBox_and_placeOnGround_delegateToDynamicResource() {
    DynamicResourceKey key = new DynamicResourceKey(createDynamicResource("Custom Prop", "tag"));
    assertEquals(new AxisAlignedBox(new Point3(0, 1, 2), new Point3(3, 4, 5)), key.getBoundingBox());
    assertTrue(key.getPlaceOnGround());
  }

  @Test
  public void isLeaf_returnsTrue() {
    DynamicResourceKey key = new DynamicResourceKey(createDynamicResource("Custom Prop", "tag"));
    assertTrue(key.isLeaf());
  }

  @Test
  public void equals_and_hashCode_forSameResource() {
    DynamicPropResource resource = createDynamicResource("Custom Prop", "tag");
    DynamicResourceKey key1 = new DynamicResourceKey(resource);
    DynamicResourceKey key2 = new DynamicResourceKey(resource);
    assertEquals(key1, key2);
    assertEquals(key1.hashCode(), key2.hashCode());
  }

  @Test
  public void equals_differentResourceInstance_returnsFalse() {
    DynamicResourceKey key1 = new DynamicResourceKey(createDynamicResource("Custom Prop", "tag"));
    DynamicResourceKey key2 = new DynamicResourceKey(createDynamicResource("Another Prop", "other"));
    assertNotEquals(key1, key2);
  }

  @Test
  public void toString_containsClassName() {
    DynamicResourceKey key = new DynamicResourceKey(createDynamicResource("Custom Prop", "tag"));
    assertTrue(key.toString().contains("DynamicResourceKey"));
  }
}
