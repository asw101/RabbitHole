package org.alice.ide.croquet.codecs;

import edu.cmu.cs.dennisc.codec.InputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.junit.Test;
import org.lgna.project.ast.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

/**
 * Tests the external serialization boundary of {@link NodeCodec}:
 * encodeValue writes a boolean prefix + UUID, decodeValue reads them back.
 * Non-null decode requires the node in the global map or a project on the stack.
 */
public class NodeCodecEncodeTest {

  // ---- encodeValue null prefix ----

  @Test
  public void encodeValue_null_writesFalsePrefix() {
    NodeCodec<NullLiteral> codec = NodeCodec.getInstance(NullLiteral.class);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);

    codec.encodeValue(encoder, null);
    encoder.flush();

    assertTrue("Encoded bytes should be non-empty", baos.size() > 0);

    // Decode the boolean prefix — should be false for null
    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
        new ByteArrayInputStream(baos.toByteArray()));
    assertFalse("Null value should encode false prefix", decoder.decodeBoolean());
  }

  @Test
  public void encodeValue_nonNull_writesTruePrefix() {
    NodeCodec<NullLiteral> codec = NodeCodec.getInstance(NullLiteral.class);
    NullLiteral node = new NullLiteral();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);

    codec.encodeValue(encoder, node);
    encoder.flush();

    // Decode the boolean prefix — should be true for non-null
    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
        new ByteArrayInputStream(baos.toByteArray()));
    assertTrue("Non-null value should encode true prefix", decoder.decodeBoolean());
  }

  // ---- null round-trip ----

  @Test
  public void decodeValue_null_roundTrip() {
    NodeCodec<NullLiteral> codec = NodeCodec.getInstance(NullLiteral.class);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    codec.encodeValue(encoder, null);
    encoder.flush();

    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
        new ByteArrayInputStream(baos.toByteArray()));
    NullLiteral result = codec.decodeValue(decoder);
    assertNull("Null encode/decode should round-trip to null", result);
  }

  // ---- non-null round-trip via global map ----

  @Test
  public void decodeValue_viaGlobalMap_roundTrip() {
    NodeCodec<NullLiteral> codec = NodeCodec.getInstance(NullLiteral.class);
    NullLiteral node = new NullLiteral();

    NodeCodec.addNodeToGlobalMap(node);
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
      codec.encodeValue(encoder, node);
      encoder.flush();

      InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
          new ByteArrayInputStream(baos.toByteArray()));
      NullLiteral result = codec.decodeValue(decoder);
      assertSame("Round-trip via global map should return same node", node, result);
    } finally {
      NodeCodec.removeNodeFromGlobalMap(node);
    }
  }

  @Test
  public void decodeValue_integerLiteral_viaGlobalMap() {
    NodeCodec<IntegerLiteral> codec = NodeCodec.getInstance(IntegerLiteral.class);
    IntegerLiteral node = new IntegerLiteral(42);

    NodeCodec.addNodeToGlobalMap(node);
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
      codec.encodeValue(encoder, node);
      encoder.flush();

      InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
          new ByteArrayInputStream(baos.toByteArray()));
      IntegerLiteral result = codec.decodeValue(decoder);
      assertSame(node, result);
    } finally {
      NodeCodec.removeNodeFromGlobalMap(node);
    }
  }

  @Test
  public void decodeValue_blockStatement_viaGlobalMap() {
    NodeCodec<BlockStatement> codec = NodeCodec.getInstance(BlockStatement.class);
    BlockStatement block = new BlockStatement();

    NodeCodec.addNodeToGlobalMap(block);
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
      codec.encodeValue(encoder, block);
      encoder.flush();

      InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
          new ByteArrayInputStream(baos.toByteArray()));
      BlockStatement result = codec.decodeValue(decoder);
      assertSame(block, result);
    } finally {
      NodeCodec.removeNodeFromGlobalMap(block);
    }
  }

  @Test
  public void decodeValue_userMethod_viaGlobalMap() {
    NodeCodec<UserMethod> codec = NodeCodec.getInstance(UserMethod.class);
    UserMethod method = new UserMethod();

    NodeCodec.addNodeToGlobalMap(method);
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
      codec.encodeValue(encoder, method);
      encoder.flush();

      InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
          new ByteArrayInputStream(baos.toByteArray()));
      UserMethod result = codec.decodeValue(decoder);
      assertSame(method, result);
    } finally {
      NodeCodec.removeNodeFromGlobalMap(method);
    }
  }

  // ---- multiple null encodes ----

  @Test
  public void encodeValue_multipleNulls_allDecodeToNull() {
    NodeCodec<Expression> codec = NodeCodec.getInstance(Expression.class);
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

  // ---- mixed null and non-null sequence ----

  @Test
  public void encodeValue_mixedNullAndNonNull_sequence() {
    NodeCodec<NullLiteral> codec = NodeCodec.getInstance(NullLiteral.class);
    NullLiteral node = new NullLiteral();

    NodeCodec.addNodeToGlobalMap(node);
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);

      codec.encodeValue(encoder, null);
      codec.encodeValue(encoder, node);
      codec.encodeValue(encoder, null);
      encoder.flush();

      InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
          new ByteArrayInputStream(baos.toByteArray()));

      assertNull(codec.decodeValue(decoder));
      assertSame(node, codec.decodeValue(decoder));
      assertNull(codec.decodeValue(decoder));
    } finally {
      NodeCodec.removeNodeFromGlobalMap(node);
    }
  }

  // ---- encoded bytes size sanity ----

  @Test
  public void encodeValue_null_producesMinimalBytes() {
    NodeCodec<NullLiteral> codec = NodeCodec.getInstance(NullLiteral.class);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    codec.encodeValue(encoder, null);
    encoder.flush();

    // Null should produce very few bytes (just the ObjectOutputStream header + boolean false)
    assertTrue("Null encode should produce compact bytes", baos.size() < 20);
  }

  @Test
  public void encodeValue_nonNull_producesMoreBytes() {
    NodeCodec<NullLiteral> codec = NodeCodec.getInstance(NullLiteral.class);
    NullLiteral node = new NullLiteral();

    ByteArrayOutputStream nullBaos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder nullEncoder = new OutputStreamBinaryEncoder(nullBaos);
    codec.encodeValue(nullEncoder, null);
    nullEncoder.flush();

    ByteArrayOutputStream nodeBaos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder nodeEncoder = new OutputStreamBinaryEncoder(nodeBaos);
    codec.encodeValue(nodeEncoder, node);
    nodeEncoder.flush();

    assertTrue("Non-null encode should produce more bytes than null",
        nodeBaos.size() > nullBaos.size());
  }
}
