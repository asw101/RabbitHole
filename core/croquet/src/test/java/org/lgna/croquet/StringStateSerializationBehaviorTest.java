package org.lgna.croquet;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class StringStateSerializationBehaviorTest {
  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-000f-000000000071"), "strSer");

  private TestStringState state;

  @Before
  public void setUp() {
    state = new TestStringState("initial");
    CroquetTestUtils.removeDocumentListeners(state);
  }

  @Test
  public void encodeAndDecodeRoundTripsUnicodeValue() {
    String original = "tabs\tnewlines\nemoji-🎉";

    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    state.encodeValue(encoder, original);
    BinaryDecoder decoder = encoder.createDecoder();

    assertEquals(original, state.decodeValue(decoder));
  }

  @Test
  public void encodeAndDecodeRoundTripsNullValue() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    state.encodeValue(encoder, null);

    assertNull(state.decodeValue(encoder.createDecoder()));
  }

  @Test
  public void appendRepresentationPreservesOriginalFormatting() {
    StringBuilder builder = new StringBuilder();
    String value = "left\nright\ttrim? no";

    state.appendRepresentation(builder, value);

    assertEquals(value, builder.toString());
  }

  private static final class TestStringState extends StringState {
    private TestStringState(String initialValue) {
      super(TEST_GROUP, CroquetTestUtils.nextTestUUID(), initialValue);
    }

    @Override
    protected Class<? extends Element> getClassUsedForLocalization() {
      return TestStringState.class;
    }

    @Override
    protected String getSubKeyForLocalization() {
      return "serialization";
    }
  }
}
