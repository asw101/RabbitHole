package org.alice.ide.croquet.codecs;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Test;
import org.lgna.common.Resource;

import java.util.UUID;

import static org.junit.Assert.*;

public class ResourceCodecTest {

  private static class SampleResource extends Resource {
    private SampleResource(String name) {
      super(UUID.randomUUID());
      this.setName(name);
      this.setOriginalFileName(name + ".dat");
      this.setContent("application/test", new byte[] {1, 2, 3});
    }
  }

  private static final class OtherResource extends Resource {
    private OtherResource(String name) {
      super(UUID.randomUUID());
      this.setName(name);
      this.setOriginalFileName(name + ".dat");
      this.setContent("application/test", new byte[] {4, 5, 6});
    }
  }

  @Test
  public void getValueClass_returnsRequestedClass() {
    ResourceCodec<SampleResource> codec = ResourceCodec.getInstance(SampleResource.class);

    assertEquals(SampleResource.class, codec.getValueClass());
  }

  @Test
  public void getInstance_returnsSameCodecForSameClass() {
    assertSame(ResourceCodec.getInstance(SampleResource.class), ResourceCodec.getInstance(SampleResource.class));
  }

  @Test
  public void getInstance_returnsDifferentCodecsForDifferentClasses() {
    assertNotSame(ResourceCodec.getInstance(SampleResource.class), ResourceCodec.getInstance(OtherResource.class));
  }

  @Test
  public void encodeValue_nullCanBeDecodedAsNull() {
    ResourceCodec<SampleResource> codec = ResourceCodec.getInstance(SampleResource.class);
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();

    codec.encodeValue(encoder, null);

    assertNull(codec.decodeValue(encoder.createDecoder()));
  }

  @Test
  public void encodeValue_nonNullStoresResourceId() {
    ResourceCodec<SampleResource> codec = ResourceCodec.getInstance(SampleResource.class);
    SampleResource resource = new SampleResource("sample");
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();

    codec.encodeValue(encoder, resource);

    BinaryDecoder decoder = encoder.createDecoder();
    assertTrue(decoder.decodeBoolean());
    assertEquals(resource.getId(), decoder.decodeId());
  }

  @Test
  public void appendRepresentation_usesResourceToString() {
    SampleResource resource = new SampleResource("sample");
    StringBuilder sb = new StringBuilder();

    ResourceCodec.getInstance(SampleResource.class).appendRepresentation(sb, resource);

    assertEquals(resource.toString(), sb.toString());
  }
}
