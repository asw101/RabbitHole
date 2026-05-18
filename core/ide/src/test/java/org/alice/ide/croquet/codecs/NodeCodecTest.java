package org.alice.ide.croquet.codecs;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Test;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.Node;
import org.lgna.project.ast.UserMethod;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

public class NodeCodecTest {

  @Test
  public void getValueClass_returnsRequestedClass() {
    NodeCodec<NamedUserType> codec = NodeCodec.getInstance(NamedUserType.class);

    assertEquals(NamedUserType.class, codec.getValueClass());
  }

  @Test
  public void getInstance_returnsSameCodecForSameClass() {
    assertSame(NodeCodec.getInstance(NamedUserType.class), NodeCodec.getInstance(NamedUserType.class));
  }

  @Test
  public void getInstance_returnsDifferentCodecsForDifferentClasses() {
    assertNotSame(NodeCodec.getInstance(NamedUserType.class), NodeCodec.getInstance(UserMethod.class));
  }

  @Test
  public void encodeValue_nullCanBeDecodedAsNull() {
    NodeCodec<NamedUserType> codec = NodeCodec.getInstance(NamedUserType.class);
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();

    codec.encodeValue(encoder, null);

    assertNull(codec.decodeValue(encoder.createDecoder()));
  }

  @Test
  public void encodeValue_nonNullStoresNodeId() {
    NodeCodec<NamedUserType> codec = NodeCodec.getInstance(NamedUserType.class);
    NamedUserType node = new NamedUserType();
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();

    codec.encodeValue(encoder, node);

    BinaryDecoder decoder = encoder.createDecoder();
    assertTrue(decoder.decodeBoolean());
    assertEquals(node.getId(), decoder.decodeId());
  }

  @Test
  public void decodeValue_returnsNodeFromGlobalMap() {
    NodeCodec<NamedUserType> codec = NodeCodec.getInstance(NamedUserType.class);
    NamedUserType node = new NamedUserType();
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    codec.encodeValue(encoder, node);

    NodeCodec.addNodeToGlobalMap(node);
    try {
      assertSame(node, codec.decodeValue(encoder.createDecoder()));
    } finally {
      NodeCodec.removeNodeFromGlobalMap(node);
    }
  }

  @Test
  public void removeNodeFromGlobalMap_removesMappedNode() throws Exception {
    NamedUserType node = new NamedUserType();

    NodeCodec.addNodeToGlobalMap(node);
    assertSame(node, getGlobalNodeMap().get(node.getId()));

    NodeCodec.removeNodeFromGlobalMap(node);

    assertFalse(getGlobalNodeMap().containsKey(node.getId()));
  }

  @SuppressWarnings("unchecked")
  private static Map<UUID, Node> getGlobalNodeMap() throws Exception {
    Field field = NodeCodec.class.getDeclaredField("mapIdToNode");
    field.setAccessible(true);
    return (Map<UUID, Node>) field.get(null);
  }
}
