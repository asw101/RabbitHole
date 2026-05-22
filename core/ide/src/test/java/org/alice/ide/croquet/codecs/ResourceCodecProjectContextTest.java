package org.alice.ide.croquet.codecs;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.BinaryEncodableAndDecodable;
import edu.cmu.cs.dennisc.codec.ReferenceableBinaryEncodableAndDecodable;
import edu.cmu.cs.dennisc.property.InstancePropertyOwner;
import org.alice.ide.testing.ProjectContextTestCase;
import org.junit.Test;
import org.lgna.common.resources.AudioResource;

import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertSame;

public class ResourceCodecProjectContextTest extends ProjectContextTestCase {
  @Test
  public void decodeUsesLoadedProjectResources() {
    ResourceCodec<AudioResource> codec = ResourceCodec.getInstance(AudioResource.class);
    AudioResource decoded = codec.decodeValue(new FixedBinaryDecoder(fixture.audioResource.getId()));
    assertSame(fixture.audioResource, decoded);
  }

  private static final class FixedBinaryDecoder implements BinaryDecoder {
    private final UUID id;

    private FixedBinaryDecoder(UUID id) {
      this.id = id;
    }

    @Override public byte[] readFully(byte[] rv) { return rv; }
    @Override public boolean decodeBoolean() { return true; }
    @Override public byte decodeByte() { return 0; }
    @Override public char decodeChar() { return 0; }
    @Override public double decodeDouble() { return 0; }
    @Override public float decodeFloat() { return 0; }
    @Override public int decodeInt() { return 0; }
    @Override public long decodeLong() { return 0; }
    @Override public short decodeShort() { return 0; }
    @Override public String decodeString() { return null; }
    @Override public <E extends Enum<E>> E decodeEnum() { return null; }
    @Override public UUID decodeId() { return this.id; }
    @Override public <E extends BinaryEncodableAndDecodable> E decodeBinaryEncodableAndDecodable() { return null; }
    @Override public <E extends BinaryEncodableAndDecodable> E decodeBinaryEncodableAndDecodable(Object context) { return null; }
    @Override public <E extends ReferenceableBinaryEncodableAndDecodable> E decodeReferenceableBinaryEncodableAndDecodable(Map<Integer, ReferenceableBinaryEncodableAndDecodable> map) { return null; }
    @Override public boolean[] decodeBooleanArray() { return null; }
    @Override public byte[] decodeByteArray() { return null; }
    @Override public char[] decodeCharArray() { return null; }
    @Override public double[] decodeDoubleArray() { return null; }
    @Override public float[] decodeFloatArray() { return null; }
    @Override public int[] decodeIntArray() { return null; }
    @Override public long[] decodeLongArray() { return null; }
    @Override public short[] decodeShortArray() { return null; }
    @Override public String[] decodeStringArray() { return null; }
    @Override public <E extends Enum<E>> E[] decodeEnumArray(Class<E> cls) { return null; }
    @Override public UUID[] decodeIdArray() { return null; }
    @Override public <E extends BinaryEncodableAndDecodable> E[] decodeBinaryEncodableAndDecodableArray(Class<E> componentCls) { return null; }
    @Override public <E extends ReferenceableBinaryEncodableAndDecodable> E[] decodeReferenceableBinaryEncodableAndDecodableArray(Class<E> componentCls, Map<Integer, ReferenceableBinaryEncodableAndDecodable> map) { return null; }
    @Override public void decodeProperties(InstancePropertyOwner owner, Map<Integer, ReferenceableBinaryEncodableAndDecodable> map) { }
    @Override public <C> C decodeRecord() { return null; }
  }
}
