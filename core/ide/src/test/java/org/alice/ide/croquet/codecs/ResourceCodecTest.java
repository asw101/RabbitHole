package org.alice.ide.croquet.codecs;

import org.junit.Test;
import org.lgna.common.resources.AudioResource;
import org.lgna.common.resources.ImageResource;

import static org.junit.Assert.*;

/**
 * Tests for {@link ResourceCodec} — cached codec for Resource instances.
 */
public class ResourceCodecTest {

  // ---- getInstance factory ----

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
  public void getInstance_differentClasses_returnDifferentCodecs() {
    ResourceCodec<AudioResource> c1 = ResourceCodec.getInstance(AudioResource.class);
    ResourceCodec<ImageResource> c2 = ResourceCodec.getInstance(ImageResource.class);
    assertNotSame(c1, c2);
  }

  // ---- getValueClass ----

  @Test
  public void getValueClass_audioResource() {
    ResourceCodec<AudioResource> codec = ResourceCodec.getInstance(AudioResource.class);
    assertEquals(AudioResource.class, codec.getValueClass());
  }

  @Test
  public void getValueClass_imageResource() {
    ResourceCodec<ImageResource> codec = ResourceCodec.getInstance(ImageResource.class);
    assertEquals(ImageResource.class, codec.getValueClass());
  }

  // ---- ItemCodec interface compliance ----

  @Test
  public void implementsItemCodec() {
    ResourceCodec<AudioResource> codec = ResourceCodec.getInstance(AudioResource.class);
    assertTrue(codec instanceof org.lgna.croquet.ItemCodec);
  }

  // ---- multiple calls ----

  @Test
  public void getInstance_calledMultipleTimes_returnsNonNull() {
    for (int i = 0; i < 5; i++) {
      ResourceCodec<AudioResource> codec = ResourceCodec.getInstance(AudioResource.class);
      assertNotNull("Call " + i, codec);
    }
  }

  @Test
  public void getValueClass_consistent() {
    ResourceCodec<AudioResource> c1 = ResourceCodec.getInstance(AudioResource.class);
    ResourceCodec<AudioResource> c2 = ResourceCodec.getInstance(AudioResource.class);
    assertEquals(c1.getValueClass(), c2.getValueClass());
  }
}
