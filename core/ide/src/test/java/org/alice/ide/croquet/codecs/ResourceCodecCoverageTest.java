package org.alice.ide.croquet.codecs;

import edu.cmu.cs.dennisc.codec.InputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.junit.Test;
import org.lgna.common.resources.AudioResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static org.junit.Assert.*;

public class ResourceCodecCoverageTest {
  @Test
  public void getInstance_sameClass_preservesValueClassEvenWhenDistinct() {
    assertNotSame(ResourceCodec.getInstance(AudioResource.class), ResourceCodec.getInstance(AudioResource.class));
    assertEquals(AudioResource.class, ResourceCodec.getInstance(AudioResource.class).getValueClass());
  }

  @Test
  public void roundTrip_nullResource_returnsNullWithoutIdeLookup() {
    ResourceCodec<AudioResource> codec = ResourceCodec.getInstance(AudioResource.class);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    codec.encodeValue(encoder, null);
    encoder.flush();
    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray()));
    assertNull(codec.decodeValue(decoder));
  }

  @Test
  public void appendRepresentation_usesResourceToString() {
    AudioResource resource = new AudioResource(UUID.randomUUID());
    StringBuilder sb = new StringBuilder();
    ResourceCodec.getInstance(AudioResource.class).appendRepresentation(sb, resource);
    assertEquals(resource.toString(), sb.toString());
  }
}
