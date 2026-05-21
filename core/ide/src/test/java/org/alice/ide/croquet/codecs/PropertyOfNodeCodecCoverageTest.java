package org.alice.ide.croquet.codecs;

import edu.cmu.cs.dennisc.codec.InputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.junit.Test;
import org.lgna.project.ast.ExpressionProperty;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.NullLiteral;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

public class PropertyOfNodeCodecCoverageTest {
  @Test
  public void getInstance_sameClass_preservesValueClassEvenWhenDistinct() {
    assertNotSame(PropertyOfNodeCodec.getInstance(ExpressionProperty.class), PropertyOfNodeCodec.getInstance(ExpressionProperty.class));
    assertEquals(ExpressionProperty.class, PropertyOfNodeCodec.getInstance(ExpressionProperty.class).getValueClass());
  }

  @Test
  public void roundTrip_withOwnerInGlobalMap_returnsSamePropertyInstance() {
    ExpressionStatement statement = new ExpressionStatement(new NullLiteral());
    ExpressionProperty property = statement.expression;
    PropertyOfNodeCodec<ExpressionProperty> codec = PropertyOfNodeCodec.getInstance(ExpressionProperty.class);
    NodeCodec.addNodeToGlobalMap(statement);
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
      codec.encodeValue(encoder, property);
      encoder.flush();
      InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray()));
      assertSame(property, codec.decodeValue(decoder));
    } finally {
      NodeCodec.removeNodeFromGlobalMap(statement);
    }
  }

  @Test
  public void appendRepresentation_usesPropertyToString() {
    ExpressionProperty property = new ExpressionStatement(new NullLiteral()).expression;
    StringBuilder sb = new StringBuilder();
    PropertyOfNodeCodec.getInstance(ExpressionProperty.class).appendRepresentation(sb, property);
    assertEquals(property.toString(), sb.toString());
  }
}
