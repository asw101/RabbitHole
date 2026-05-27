package edu.cmu.cs.dennisc.codec;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ReferenceableBinaryCodecTest {
  public static final class ReferenceNode implements ReferenceableBinaryEncodableAndDecodable {
    private String name;

    public ReferenceNode() {
    }

    private ReferenceNode(String name) {
      this.name = name;
    }

    @Override
    public void decode(BinaryDecoder binaryDecoder, Map<Integer, ReferenceableBinaryEncodableAndDecodable> map) {
      this.name = binaryDecoder.decodeString();
    }

    @Override
    public void encode(BinaryEncoder binaryEncoder, Map<ReferenceableBinaryEncodableAndDecodable, Integer> map) {
      binaryEncoder.encode(name);
    }
  }

  @Test
  public void repeatedReferenceDecodesToTheSameInstance() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    Map<ReferenceableBinaryEncodableAndDecodable, Integer> encodeMap = new HashMap<ReferenceableBinaryEncodableAndDecodable, Integer>();
    ReferenceNode shared = new ReferenceNode("shared");
    encoder.encode(shared, encodeMap);
    encoder.encode(shared, encodeMap);

    BinaryDecoder decoder = encoder.createDecoder();
    Map<Integer, ReferenceableBinaryEncodableAndDecodable> decodeMap = new HashMap<Integer, ReferenceableBinaryEncodableAndDecodable>();
    ReferenceNode first = decoder.decodeReferenceableBinaryEncodableAndDecodable(decodeMap);
    ReferenceNode second = decoder.decodeReferenceableBinaryEncodableAndDecodable(decodeMap);

    assertSame(first, second);
    assertEquals("shared", first.name);
  }

  @Test
  public void referenceableArrayRoundTripsDistinctAndSharedEntries() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    Map<ReferenceableBinaryEncodableAndDecodable, Integer> encodeMap = new HashMap<ReferenceableBinaryEncodableAndDecodable, Integer>();
    ReferenceNode shared = new ReferenceNode("shared");
    ReferenceNode other = new ReferenceNode("other");
    encoder.encode(new ReferenceableBinaryEncodableAndDecodable[] {shared, other, shared}, encodeMap);

    ReferenceNode[] decoded = encoder.createDecoder().decodeReferenceableBinaryEncodableAndDecodableArray(
        ReferenceNode.class,
        new HashMap<Integer, ReferenceableBinaryEncodableAndDecodable>()
    );

    assertEquals(3, decoded.length);
    assertEquals("shared", decoded[0].name);
    assertEquals("other", decoded[1].name);
    assertSame(decoded[0], decoded[2]);
    assertNotSame(decoded[0], decoded[1]);
  }
}
