package org.lgna.croquet.codecs;

import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Test;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.SimpleTabComposite;
import org.lgna.croquet.views.CompositeView;

import javax.swing.JPanel;

import static org.junit.Assert.*;

public class SimpleTabCompositeCodecTest {

  @Test
  public void getInstance_sameClass_returnsDistinctCodecsForNow() {
    SimpleTabCompositeCodec<TestTabComposite> first = SimpleTabCompositeCodec.getInstance(TestTabComposite.class);
    SimpleTabCompositeCodec<TestTabComposite> second = SimpleTabCompositeCodec.getInstance(TestTabComposite.class);

    assertNotSame(first, second);
    assertEquals(TestTabComposite.class, first.getValueClass());
    assertEquals(TestTabComposite.class, second.getValueClass());
  }

  @Test
  public void getInstance_differentClasses_returnsDifferentCodecs() {
    SimpleTabCompositeCodec<TestTabComposite> first = SimpleTabCompositeCodec.getInstance(TestTabComposite.class);
    SimpleTabCompositeCodec<OtherTabComposite> second = SimpleTabCompositeCodec.getInstance(OtherTabComposite.class);

    assertNotSame(first, second);
  }

  @Test
  public void getValueClass_matchesRequestedTabCompositeClass() {
    SimpleTabCompositeCodec<TestTabComposite> codec = SimpleTabCompositeCodec.getInstance(TestTabComposite.class);

    assertEquals(TestTabComposite.class, codec.getValueClass());
  }

  @Test
  public void appendRepresentation_null_appendsNull() {
    SimpleTabCompositeCodec<TestTabComposite> codec = SimpleTabCompositeCodec.getInstance(TestTabComposite.class);
    StringBuilder sb = new StringBuilder();

    codec.appendRepresentation(sb, null);

    assertEquals("null", sb.toString());
  }

  @Test
  public void appendRepresentation_null_doesNotInitializeComposite() {
    SimpleTabCompositeCodec<TestTabComposite> codec = SimpleTabCompositeCodec.getInstance(TestTabComposite.class);
    TestTabComposite composite = new TestTabComposite("alpha");

    codec.appendRepresentation(new StringBuilder(), null);

    assertEquals(0, composite.getInitializeCount());
  }

  @Test
  public void appendRepresentation_nonNull_initializesCompositeOnce() {
    SimpleTabCompositeCodec<TestTabComposite> codec = SimpleTabCompositeCodec.getInstance(TestTabComposite.class);
    TestTabComposite composite = new TestTabComposite("alpha");

    codec.appendRepresentation(new StringBuilder(), composite);
    codec.appendRepresentation(new StringBuilder(), composite);

    assertEquals(1, composite.getInitializeCount());
  }

  @Test
  public void appendRepresentation_nonNull_usesCompositeUserRepresentation() {
    SimpleTabCompositeCodec<TestTabComposite> codec = SimpleTabCompositeCodec.getInstance(TestTabComposite.class);
    TestTabComposite composite = new TestTabComposite("visible tab");
    StringBuilder sb = new StringBuilder();

    codec.appendRepresentation(sb, composite);

    assertEquals("visible tab", sb.toString());
  }

  @Test
  public void encodeValue_null_roundTripsToNull() {
    SimpleTabCompositeCodec<TestTabComposite> codec = SimpleTabCompositeCodec.getInstance(TestTabComposite.class);
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();

    codec.encodeValue(encoder, null);

    assertNull(codec.decodeValue(encoder.createDecoder()));
  }

  @Test
  public void encodeValue_nonNull_throwsTodoRuntimeException() {
    SimpleTabCompositeCodec<TestTabComposite> codec = SimpleTabCompositeCodec.getInstance(TestTabComposite.class);

    RuntimeException exception = assertThrows(RuntimeException.class, () ->
        codec.encodeValue(new ByteArrayBinaryEncoder(), new TestTabComposite("alpha")));

    assertEquals("todo", exception.getMessage());
  }

  @Test
  public void decodeValue_trueMarker_throwsTodoRuntimeException() {
    SimpleTabCompositeCodec<TestTabComposite> codec = SimpleTabCompositeCodec.getInstance(TestTabComposite.class);
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    encoder.encode(true);

    RuntimeException exception = assertThrows(RuntimeException.class, () -> codec.decodeValue(encoder.createDecoder()));

    assertEquals("todo", exception.getMessage());
  }

  private static class TestTabComposite extends SimpleTabComposite<StubView> {
    private final String userRepr;
    private int initializeCount;

    TestTabComposite(String userRepr) {
      super(CroquetTestUtils.nextTestUUID(), IsCloseable.FALSE);
      this.userRepr = userRepr;
    }

    int getInitializeCount() {
      return this.initializeCount;
    }

    @Override
    protected void initialize() {
      this.initializeCount++;
      super.initialize();
    }

    @Override
    protected StubView createView() {
      return new StubView();
    }

    @Override
    public void appendUserRepr(StringBuilder userRepr) {
      userRepr.append(this.userRepr);
    }
  }

  private static final class OtherTabComposite extends SimpleTabComposite<StubView> {
    OtherTabComposite() {
      super(CroquetTestUtils.nextTestUUID(), IsCloseable.FALSE);
    }

    @Override
    protected StubView createView() {
      return new StubView();
    }

    @Override
    public void appendUserRepr(StringBuilder userRepr) {
      userRepr.append("other");
    }
  }

  private static final class StubView extends CompositeView<JPanel, org.lgna.croquet.Composite<?>> {
    StubView() {
      super(null);
    }

    @Override
    protected JPanel createAwtComponent() {
      return new JPanel();
    }
  }
}
