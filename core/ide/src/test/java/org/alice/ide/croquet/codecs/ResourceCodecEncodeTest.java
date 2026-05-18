package org.alice.ide.croquet.codecs;

import edu.cmu.cs.dennisc.codec.InputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.junit.Test;
import org.lgna.common.Resource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

/**
 * Tests the external serialization boundary of {@link ResourceCodec}.
 * Covers encodeValue/decodeValue null handling via binary streams.
 */
public class ResourceCodecEncodeTest {

  // ---- encodeValue null ----

  @Test
  public void encodeValue_null_writesFalsePrefix() {
    ResourceCodec<Resource> codec = ResourceCodec.getInstance(Resource.class);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    codec.encodeValue(encoder, null);
    encoder.flush();

    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
        new ByteArrayInputStream(baos.toByteArray()));
    assertFalse("Null resource should encode false prefix", decoder.decodeBoolean());
  }

  @Test
  public void decodeValue_null_roundTrip() {
    ResourceCodec<Resource> codec = ResourceCodec.getInstance(Resource.class);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    codec.encodeValue(encoder, null);
    encoder.flush();

    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
        new ByteArrayInputStream(baos.toByteArray()));
    Resource result = codec.decodeValue(decoder);
    assertNull(result);
  }

  // ---- appendRepresentation ----

  @Test
  public void appendRepresentation_null_containsNullText() {
    ResourceCodec<Resource> codec = ResourceCodec.getInstance(Resource.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, null);
    assertTrue(sb.toString().contains("null"));
  }

  // ---- getValueClass ----

  @Test
  public void getValueClass_returnsPassedClass() {
    ResourceCodec<Resource> codec = ResourceCodec.getInstance(Resource.class);
    assertEquals(Resource.class, codec.getValueClass());
  }

  // ---- multiple null encodes ----

  @Test
  public void encodeValue_multipleNulls_allDecodeToNull() {
    ResourceCodec<Resource> codec = ResourceCodec.getInstance(Resource.class);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    codec.encodeValue(encoder, null);
    codec.encodeValue(encoder, null);
    codec.encodeValue(encoder, null);
    encoder.flush();

    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
        new ByteArrayInputStream(baos.toByteArray()));
    assertNull(codec.decodeValue(decoder));
    assertNull(codec.decodeValue(decoder));
    assertNull(codec.decodeValue(decoder));
  }

  // ---- encoded size sanity ----

  @Test
  public void encodeValue_null_producesCompactBytes() {
    ResourceCodec<Resource> codec = ResourceCodec.getInstance(Resource.class);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    codec.encodeValue(encoder, null);
    encoder.flush();

    assertTrue("Null encode should produce compact bytes", baos.size() < 20);
  }
}
