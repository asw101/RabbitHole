package org.alice.ide.croquet.codecs;

import edu.cmu.cs.dennisc.codec.InputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.alice.ide.testing.ProjectContextTestCase;
import org.junit.Test;
import org.lgna.common.Resource;
import org.lgna.common.resources.AudioResource;
import org.lgna.common.resources.ImageResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class ResourceCodecRoundTripProjectContextTest extends ProjectContextTestCase {
  @Test
  public void audioResourceRoundTripsThroughBinaryStreams() {
    assertRoundTrip(AudioResource.class, fixture.audioResource);
  }

  @Test
  public void imageResourceRoundTripsThroughBinaryStreams() {
    assertRoundTrip(ImageResource.class, fixture.imageResource);
  }

  @Test
  public void mixedResourceAndNullSequenceRoundTrips() {
    ResourceCodec<AudioResource> audioCodec = ResourceCodec.getInstance(AudioResource.class);
    ResourceCodec<ImageResource> imageCodec = ResourceCodec.getInstance(ImageResource.class);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    audioCodec.encodeValue(encoder, fixture.audioResource);
    imageCodec.encodeValue(encoder, null);
    imageCodec.encodeValue(encoder, fixture.imageResource);
    encoder.flush();

    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray()));
    assertSame(fixture.audioResource, audioCodec.decodeValue(decoder));
    assertNull(imageCodec.decodeValue(decoder));
    assertSame(fixture.imageResource, imageCodec.decodeValue(decoder));
  }

  private <R extends Resource> void assertRoundTrip(Class<R> resourceClass, R resource) {
    ResourceCodec<R> codec = ResourceCodec.getInstance(resourceClass);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    codec.encodeValue(encoder, resource);
    encoder.flush();

    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray()));
    assertSame(resource, codec.decodeValue(decoder));
  }
}
