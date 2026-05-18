package org.alice.ide.croquet.codecs;

import edu.cmu.cs.dennisc.codec.InputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.junit.Test;
import org.lgna.project.ast.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

/**
 * Tests the external serialization boundary of {@link PropertyOfNodeCodec}.
 * Exercises encodeValue/decodeValue for ExpressionProperty references.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class PropertyOfNodeCodecEncodeTest {

  // ---- getValueClass ----

  @Test
  public void getValueClass_returnsPassedClass() {
    PropertyOfNodeCodec codec = PropertyOfNodeCodec.getInstance(ExpressionProperty.class);
    assertEquals(ExpressionProperty.class, codec.getValueClass());
  }

  // ---- encodeValue null ----

  @Test
  public void encodeValue_null_writesFalsePrefix() {
    PropertyOfNodeCodec codec = PropertyOfNodeCodec.getInstance(ExpressionProperty.class);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    codec.encodeValue(encoder, null);
    encoder.flush();

    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
        new ByteArrayInputStream(baos.toByteArray()));
    assertFalse("Null value should encode false prefix", decoder.decodeBoolean());
  }

  @Test
  public void decodeValue_null_roundTrip() {
    PropertyOfNodeCodec codec = PropertyOfNodeCodec.getInstance(ExpressionProperty.class);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    codec.encodeValue(encoder, null);
    encoder.flush();

    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
        new ByteArrayInputStream(baos.toByteArray()));
    Object result = codec.decodeValue(decoder);
    assertNull(result);
  }

  // ---- encodeValue non-null ----

  @Test
  public void encodeValue_nonNull_writesTruePrefix() {
    PropertyOfNodeCodec codec = PropertyOfNodeCodec.getInstance(ExpressionProperty.class);

    ExpressionStatement stmt = new ExpressionStatement(new NullLiteral());
    ExpressionProperty prop = stmt.expression;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    codec.encodeValue(encoder, prop);
    encoder.flush();

    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
        new ByteArrayInputStream(baos.toByteArray()));
    assertTrue("Non-null property should encode true prefix", decoder.decodeBoolean());
  }

  // ---- non-null round-trip via global map ----

  @Test
  public void decodeValue_nonNull_viaGlobalMap_roundTrip() {
    PropertyOfNodeCodec codec = PropertyOfNodeCodec.getInstance(ExpressionProperty.class);

    ExpressionStatement stmt = new ExpressionStatement(new NullLiteral());
    ExpressionProperty prop = stmt.expression;

    NodeCodec.addNodeToGlobalMap(stmt);
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
      codec.encodeValue(encoder, prop);
      encoder.flush();

      InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
          new ByteArrayInputStream(baos.toByteArray()));
      Object result = codec.decodeValue(decoder);
      assertNotNull("Should resolve property via global node map", result);
      assertSame(prop, result);
    } finally {
      NodeCodec.removeNodeFromGlobalMap(stmt);
    }
  }

  // ---- appendRepresentation ----

  @Test
  public void appendRepresentation_null_doesNotThrow() {
    PropertyOfNodeCodec codec = PropertyOfNodeCodec.getInstance(ExpressionProperty.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, null);
    assertTrue(sb.toString().contains("null"));
  }

  @Test
  public void appendRepresentation_nonNull_producesOutput() {
    PropertyOfNodeCodec codec = PropertyOfNodeCodec.getInstance(ExpressionProperty.class);

    ExpressionStatement stmt = new ExpressionStatement(new NullLiteral());
    ExpressionProperty prop = stmt.expression;

    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, prop);
    assertTrue("Should produce non-empty representation", sb.length() > 0);
  }

  // ---- multiple null encodes ----

  @Test
  public void encodeValue_multipleNulls_allDecodeToNull() {
    PropertyOfNodeCodec codec = PropertyOfNodeCodec.getInstance(ExpressionProperty.class);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    codec.encodeValue(encoder, null);
    codec.encodeValue(encoder, null);
    encoder.flush();

    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(
        new ByteArrayInputStream(baos.toByteArray()));
    assertNull(codec.decodeValue(decoder));
    assertNull(codec.decodeValue(decoder));
  }
}
