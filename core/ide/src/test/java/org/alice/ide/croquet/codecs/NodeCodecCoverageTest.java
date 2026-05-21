package org.alice.ide.croquet.codecs;

import edu.cmu.cs.dennisc.codec.InputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.junit.Test;
import org.lgna.project.ast.Node;
import org.lgna.project.ast.NullLiteral;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

public class NodeCodecCoverageTest {
  @Test
  public void getInstance_sameClass_preservesValueClassEvenWhenDistinct() {
    assertNotSame(NodeCodec.getInstance(Node.class), NodeCodec.getInstance(Node.class));
    assertEquals(Node.class, NodeCodec.getInstance(Node.class).getValueClass());
  }

  @Test
  public void roundTrip_withGlobalMap_returnsSameNodeInstance() {
    NullLiteral node = new NullLiteral();
    NodeCodec<NullLiteral> codec = NodeCodec.getInstance(NullLiteral.class);
    NodeCodec.addNodeToGlobalMap(node);
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
      codec.encodeValue(encoder, node);
      encoder.flush();
      InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray()));
      assertSame(node, codec.decodeValue(decoder));
    } finally {
      NodeCodec.removeNodeFromGlobalMap(node);
    }
  }

  @Test
  public void appendRepresentation_withNull_containsNull() {
    StringBuilder sb = new StringBuilder();
    NodeCodec.getInstance(Node.class).appendRepresentation(sb, null);
    assertTrue(sb.toString().contains("null"));
  }
}
