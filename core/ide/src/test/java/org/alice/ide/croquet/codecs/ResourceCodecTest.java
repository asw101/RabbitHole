package org.alice.ide.croquet.codecs;

import org.junit.Test;
import org.lgna.common.Resource;
import org.lgna.common.resources.AudioResource;
import org.lgna.common.resources.ImageResource;

import static org.junit.Assert.*;

/**
 * Tests for {@link ResourceCodec} — codec for Resource instances.
 * Covers getInstance factory and getValueClass.
 */
public class ResourceCodecTest {

  // ---- getInstance factory ----

  @Test
  public void getInstance_returnsNonNull() {
    ResourceCodec<Resource> codec = ResourceCodec.getInstance(Resource.class);
    assertNotNull(codec);
  }

  @Test
  public void getInstance_audioResource_returnsNonNull() {
    ResourceCodec<AudioResource> codec = ResourceCodec.getInstance(AudioResource.class);
    assertNotNull(codec);
  }

  @Test
  public void getInstance_imageResource_returnsNonNull() {
    ResourceCodec<ImageResource> codec = ResourceCodec.getInstance(ImageResource.class);
    assertNotNull(codec);
  }

  @Test
  public void getInstance_differentClasses_differentCodecs() {
    ResourceCodec<AudioResource> c1 = ResourceCodec.getInstance(AudioResource.class);
    ResourceCodec<ImageResource> c2 = ResourceCodec.getInstance(ImageResource.class);
    assertNotSame(c1, c2);
  }

  // ---- getValueClass ----

  @Test
  public void getValueClass_returnsResource() {
    ResourceCodec<Resource> codec = ResourceCodec.getInstance(Resource.class);
    assertEquals(Resource.class, codec.getValueClass());
  }

  @Test
  public void getValueClass_returnsAudioResource() {
    ResourceCodec<AudioResource> codec = ResourceCodec.getInstance(AudioResource.class);
    assertEquals(AudioResource.class, codec.getValueClass());
  }

  @Test
  public void getValueClass_returnsImageResource() {
    ResourceCodec<ImageResource> codec = ResourceCodec.getInstance(ImageResource.class);
    assertEquals(ImageResource.class, codec.getValueClass());
  }

  // ---- appendRepresentation ----

  @Test
  public void appendRepresentation_withNull_appendsNullString() {
    ResourceCodec<Resource> codec = ResourceCodec.getInstance(Resource.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  // ---- ItemCodec interface compliance ----

  @Test
  public void implementsItemCodec() {
    ResourceCodec<Resource> codec = ResourceCodec.getInstance(Resource.class);
    assertTrue(codec instanceof org.lgna.croquet.ItemCodec);
  }
}
