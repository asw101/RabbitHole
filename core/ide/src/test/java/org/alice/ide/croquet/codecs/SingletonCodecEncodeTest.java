package org.alice.ide.croquet.codecs;

import edu.cmu.cs.dennisc.codec.InputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.alice.ide.croquet.models.project.find.core.criteria.AcceptIfNotGenerated;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

/**
 * Tests the external serialization boundary of {@link SingletonCodec}.
 * Uses {@link AcceptIfNotGenerated} as a concrete singleton with {@code getInstance()}.
 */
public class SingletonCodecEncodeTest {

  // ---- encodeValue null ----

  @Test
  public void encodeValue_null_writesFalsePrefix() {
    SingletonCodec<AcceptIfNotGenerated> codec =
        SingletonCodec.getInstance(AcceptIfNotGenerated.class);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    codec.encodeValue(encoder, null);
    encoder.flush();

    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
        new ByteArrayInputStream(baos.toByteArray()));
    assertFalse("Null should encode false prefix", decoder.decodeBoolean());
  }

  @Test
  public void decodeValue_null_roundTrip() {
    SingletonCodec<AcceptIfNotGenerated> codec =
        SingletonCodec.getInstance(AcceptIfNotGenerated.class);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    codec.encodeValue(encoder, null);
    encoder.flush();

    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
        new ByteArrayInputStream(baos.toByteArray()));
    AcceptIfNotGenerated result = codec.decodeValue(decoder);
    assertNull(result);
  }

  // ---- non-null round-trip via reflection ----

  @Test
  public void encodeValue_nonNull_writesTruePrefix() {
    SingletonCodec<AcceptIfNotGenerated> codec =
        SingletonCodec.getInstance(AcceptIfNotGenerated.class);

    AcceptIfNotGenerated instance = AcceptIfNotGenerated.getInstance();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    codec.encodeValue(encoder, instance);
    encoder.flush();

    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
        new ByteArrayInputStream(baos.toByteArray()));
    assertTrue("Non-null should encode true prefix", decoder.decodeBoolean());
  }

  @Test
  public void decodeValue_nonNull_roundTrip_returnsSingleton() {
    SingletonCodec<AcceptIfNotGenerated> codec =
        SingletonCodec.getInstance(AcceptIfNotGenerated.class);

    AcceptIfNotGenerated original = AcceptIfNotGenerated.getInstance();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    codec.encodeValue(encoder, original);
    encoder.flush();

    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
        new ByteArrayInputStream(baos.toByteArray()));
    AcceptIfNotGenerated decoded = codec.decodeValue(decoder);
    assertNotNull(decoded);
    assertSame("Singleton decode should return same instance", original, decoded);
  }

  // ---- multiple round-trips ----

  @Test
  public void multipleRoundTrips_mixedNullAndNonNull() {
    SingletonCodec<AcceptIfNotGenerated> codec =
        SingletonCodec.getInstance(AcceptIfNotGenerated.class);

    AcceptIfNotGenerated instance = AcceptIfNotGenerated.getInstance();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    codec.encodeValue(encoder, null);
    codec.encodeValue(encoder, instance);
    codec.encodeValue(encoder, null);
    codec.encodeValue(encoder, instance);
    encoder.flush();

    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
        new ByteArrayInputStream(baos.toByteArray()));
    assertNull(codec.decodeValue(decoder));
    assertSame(instance, codec.decodeValue(decoder));
    assertNull(codec.decodeValue(decoder));
    assertSame(instance, codec.decodeValue(decoder));
  }

  // ---- encoded size comparison ----

  @Test
  public void encodeValue_nonNull_producesMoreBytesThanNull() {
    SingletonCodec<AcceptIfNotGenerated> codec =
        SingletonCodec.getInstance(AcceptIfNotGenerated.class);

    ByteArrayOutputStream nullBaos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder nullEncoder = new OutputStreamBinaryEncoder(nullBaos);
    codec.encodeValue(nullEncoder, null);
    nullEncoder.flush();

    ByteArrayOutputStream nonNullBaos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder nonNullEncoder = new OutputStreamBinaryEncoder(nonNullBaos);
    codec.encodeValue(nonNullEncoder, AcceptIfNotGenerated.getInstance());
    nonNullEncoder.flush();

    assertTrue("Non-null should produce more bytes (includes class name)",
        nonNullBaos.size() > nullBaos.size());
  }
}
